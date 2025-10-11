# Lambda Functions - How to Enable Features

Your Lambda now has placeholder functions for common booking processing tasks. Here's how to enable each one:

## 🆓 Already Working (No Setup Needed)

### 1. CloudWatch Logging
**What it does:** Logs all booking details to CloudWatch Logs
**Status:** ✅ Already enabled
**View logs:**
```bash
aws logs tail /aws/lambda/travelapp-booking-processor --follow
```

### 2. High-Value Booking Alerts
**What it does:** Logs a warning for bookings over $5,000
**Status:** ✅ Already enabled
**Customize threshold:** Edit line 69 in `lambda_function.py`:
```python
if total_price > 5000:  # Change this number
```

## 📧 Email Confirmations (AWS SES)

### Prerequisites:
- AWS SES account (simple to set up)
- Verified sender email address

### Setup Steps:

1. **Verify your email in AWS SES:**
```bash
aws ses verify-email-identity --email-address noreply@yourdomain.com
```
Check your email and click the verification link.

2. **Uncomment boto3 in requirements.txt:**
```txt
boto3>=1.28.0
```

3. **Uncomment the email function call in lambda_function.py (line 60-65):**
```python
send_confirmation_email(
    customer_email='customer@example.com',  # Get from booking data
    order_number=order_number,
    vacation_title=vacation_title,
    total_price=total_price
)
```

4. **Update the email function (line 48-75) with your verified email:**
```python
Source='noreply@yourdomain.com',  # Your verified email
```

5. **Add SES permission to Lambda IAM role in main.tf:**
```hcl
# In the lambda_policy resource, add:
{
  Effect = "Allow"
  Action = [
    "ses:SendEmail",
    "ses:SendRawEmail"
  ]
  Resource = "*"
}
```

6. **Redeploy:**
```bash
zip lambda_function.zip lambda_function.py requirements.txt
terraform apply
```

**Cost:** First 62,000 emails/month free, then $0.10 per 1,000 emails

## 📊 CloudWatch Metrics (Analytics)

### What it does:
Tracks booking metrics (total value, count, etc.) in CloudWatch

### Setup Steps:

1. **Uncomment boto3 in requirements.txt**

2. **Uncomment the metrics code in `track_booking_metrics()` function (lines 21-34)**

3. **Add CloudWatch permission to Lambda IAM role in main.tf:**
```hcl
{
  Effect = "Allow"
  Action = [
    "cloudwatch:PutMetricData"
  ]
  Resource = "*"
}
```

4. **Redeploy Lambda**

5. **View metrics in AWS Console:**
   - CloudWatch → Metrics → Custom Namespaces → TravelApp/Bookings

**Cost:** First 1 million API requests free, then $0.01 per 1,000 requests

## 📄 Booking Reports (S3 Storage)

### What it does:
Saves each booking as a JSON file in S3

### Setup Steps:

1. **Create S3 bucket:**
```bash
aws s3 mb s3://travelapp-bookings-reports
```

2. **Uncomment boto3 in requirements.txt**

3. **Uncomment the S3 code in `generate_booking_report()` function (lines 41-48)**

4. **Update bucket name in the function:**
```python
Bucket='travelapp-bookings-reports',  # Your bucket name
```

5. **Add S3 permission to Lambda IAM role in main.tf:**
```hcl
{
  Effect = "Allow"
  Action = [
    "s3:PutObject",
    "s3:GetObject"
  ]
  Resource = "arn:aws:s3:::travelapp-bookings-reports/*"
}
```

6. **Redeploy Lambda**

**Cost:** First 5GB storage free, then $0.023 per GB/month

## 🔔 Admin Notifications (SNS)

### What it does:
Sends SMS/email to admins for high-value bookings

### Setup Steps:

1. **Create SNS topic:**
```bash
aws sns create-topic --name travelapp-high-value-bookings
aws sns subscribe --topic-arn YOUR_TOPIC_ARN --protocol email --notification-endpoint admin@yourdomain.com
```

2. **Add to lambda_function.py (after line 70):**
```python
import boto3
sns = boto3.client('sns')
sns.publish(
    TopicArn='arn:aws:sns:us-east-1:YOUR_ACCOUNT:travelapp-high-value-bookings',
    Subject=f'High-Value Booking Alert',
    Message=f'New ${total_price:.2f} booking from {customer_name}'
)
```

3. **Add SNS permission to Lambda IAM role**

4. **Redeploy Lambda**

**Cost:** First 1,000 emails free/month, then $2 per 100,000 emails

## 🎯 Recommended Setup Order

### Phase 1 (Free):
1. ✅ CloudWatch Logging (already working)
2. ✅ High-value alerts (already working)

### Phase 2 (Minimal cost):
3. CloudWatch Metrics (analytics)
4. S3 Reports (storage)

### Phase 3 (Email requires setup):
5. AWS SES (confirmation emails)
6. SNS (admin notifications)

## 🧪 Testing Each Feature

### Test Email:
```bash
aws sqs send-message \
  --queue-url YOUR_QUEUE_URL \
  --message-body '{
    "orderTrackingNumber": "TEST-123",
    "customerName": "John Doe",
    "vacationTitle": "Italy",
    "totalPrice": 1500.00
  }'
```

### Test High-Value Alert:
```bash
aws sqs send-message \
  --queue-url YOUR_QUEUE_URL \
  --message-body '{
    "orderTrackingNumber": "TEST-456",
    "customerName": "Jane Smith",
    "vacationTitle": "Luxury Cruise",
    "totalPrice": 9999.00
  }'
```

### View Logs:
```bash
aws logs tail /aws/lambda/travelapp-booking-processor --follow
```

## 💡 Quick Tips

### Start Simple:
- Keep logging enabled (free and useful)
- Add features one at a time
- Test each feature before adding the next

### Cost Control:
- Set billing alerts in AWS Console
- Use CloudWatch for free monitoring first
- Email/SNS only for important notifications

### Debugging:
- Check CloudWatch Logs for errors
- Test with small messages first
- Verify IAM permissions if features fail

## 📚 Next Steps

Once comfortable, you can:
- Add customer email to booking data (from Spring Boot)
- Integrate with third-party APIs (Mailchimp, SendGrid, etc.)
- Store reports in DynamoDB instead of S3
- Add webhook notifications to Slack/Discord
- Create custom dashboards with booking analytics

Need help enabling any of these? Just ask!
