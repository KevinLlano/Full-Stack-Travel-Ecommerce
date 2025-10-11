# SQS Integration Setup Guide

This guide shows how to connect your Spring Boot app to AWS Lambda + SQS.

## Prerequisites
1. Deploy the Lambda + SQS infrastructure (see `aws-lambda/README.md`)
2. AWS credentials configured locally or in Fly.io

## Step 1: Get SQS Queue URL
After running `terraform apply` in the `aws-lambda` folder, copy the `sqs_queue_url` from the output.

Example output:
```
sqs_queue_url = "https://sqs.us-east-1.amazonaws.com/123456789/travelapp-bookings-queue"
```

## Step 2: Enable SQS in application.properties

Update `demo/src/main/resources/application.properties`:
```properties
# Enable SQS integration
aws.sqs.enabled=true
aws.sqs.queue.url=https://sqs.us-east-1.amazonaws.com/YOUR_ACCOUNT/travelapp-bookings-queue
```

## Step 3: Configure AWS Credentials

### For Local Development:
Run `aws configure` and enter your credentials, or set environment variables:
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_REGION=us-east-1
```

### For Fly.io Deployment:
Set secrets in Fly.io:
```bash
fly secrets set AWS_ACCESS_KEY_ID=your_access_key
fly secrets set AWS_SECRET_ACCESS_KEY=your_secret_key
fly secrets set AWS_REGION=us-east-1
```

## Step 4: Test the Integration

1. **Start your Spring Boot app** (locally or on Fly.io)

2. **Make a test booking** through your Angular frontend or via curl:
```bash
curl -X POST http://localhost:8080/api/checkout/purchase \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {"firstName": "John", "lastName": "Doe"},
    "cart": {"packagePrice": 1500.00}
  }'
```

3. **Check Lambda logs** to see if the message was processed:
```bash
aws logs tail /aws/lambda/travelapp-booking-processor --follow
```

## What Happens

### The Flow:
1. User completes a booking on your travel app
2. Spring Boot `CheckoutController` processes the order
3. `SqsService` sends a message to SQS queue (async, non-blocking)
4. Lambda function automatically triggers and processes the message
5. Lambda can send emails, update analytics, notify admins, etc.

### Message Format:
```json
{
  "orderTrackingNumber": "abc-123-def",
  "customerName": "John Doe",
  "vacationTitle": "Vacation Package",
  "totalPrice": 1500.00,
  "timestamp": "2025-10-11T10:30:00Z"
}
```

## Safety Features

### Fail-Safe Design:
- ✅ **Won't break your app** if AWS is not configured
- ✅ **Graceful degradation** - SQS disabled by default
- ✅ **Try-catch protection** - errors logged but don't fail orders
- ✅ **Optional dependency** - works with or without Lambda

### If SQS Fails:
- Order still completes successfully
- Error logged to console
- No impact on user experience

## Disabling SQS

To turn off SQS integration:
```properties
aws.sqs.enabled=false
```

Or simply don't configure AWS credentials - the service will detect this and skip SQS operations.

## Troubleshooting

### "Failed to initialize SQS client"
- Run `aws configure` to set up credentials
- Verify IAM user has `sqs:SendMessage` permission

### "Queue URL not configured"
- Check `application.properties` has correct queue URL
- Verify `aws.sqs.enabled=true`

### Messages not reaching Lambda
- Check SQS queue in AWS Console for messages
- Verify Lambda event source mapping is active
- Check Lambda CloudWatch logs for errors

## Cost Monitoring

### Estimated Costs (After Free Tier):
- SQS: ~$0.40 per million messages
- Lambda: ~$0.20 per million invocations
- **Total for typical usage:** <$1/month

### Monitor Usage:
```bash
# Check SQS metrics
aws sqs get-queue-attributes \
  --queue-url YOUR_QUEUE_URL \
  --attribute-names ApproximateNumberOfMessages

# Check Lambda invocations
aws cloudwatch get-metric-statistics \
  --namespace AWS/Lambda \
  --metric-name Invocations \
  --dimensions Name=FunctionName,Value=travelapp-booking-processor \
  --start-time 2025-10-01T00:00:00Z \
  --end-time 2025-10-31T23:59:59Z \
  --period 86400 \
  --statistics Sum
```

## Next Steps

Once integrated, you can enhance the Lambda function to:
- Send booking confirmation emails (via AWS SES)
- Update a dashboard with booking metrics
- Notify admins of high-value bookings
- Integrate with third-party APIs
- Store data in DynamoDB for analytics

See `aws-lambda/lambda_function.py` for the Lambda code to customize.
