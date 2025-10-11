# AWS Lambda + SQS Setup for TravelApp

This folder contains a **completely separate** serverless setup that doesn't affect your Fly.io deployment.

## What's Included
- **SQS Queue:** Receives booking messages
- **Lambda Function:** Processes messages automatically with built-in features:
  - ✅ CloudWatch logging (all bookings)
  - ✅ High-value booking alerts ($5,000+)
  - 📧 Email confirmations (optional, requires AWS SES setup)
  - 📊 Analytics tracking (optional, CloudWatch Metrics)
  - 📄 Booking reports (optional, S3 storage)
- **Terraform Configuration:** Infrastructure as Code
- **IAM Roles & Policies:** Secure permissions

## Prerequisites
1. **AWS Account:** Free tier is fine
2. **AWS CLI:** Install from https://aws.amazon.com/cli/
3. **Terraform:** Install from https://www.terraform.io/downloads
4. **Configure AWS credentials:**
   ```bash
   aws configure
   ```
   Enter your:
   - AWS Access Key ID
   - AWS Secret Access Key
   - Default region (e.g., `us-east-1`)
   - Output format: `json`

## Deployment Steps

### 1. Package Lambda Function
From the `aws-lambda` folder:
```bash
cd aws-lambda
zip lambda_function.zip lambda_function.py requirements.txt
```

### 2. Initialize Terraform
```bash
terraform init
```

### 3. Preview Changes
```bash
terraform plan
```

### 4. Deploy to AWS
```bash
terraform apply
```
Type `yes` when prompted.

### 5. Get Outputs
After deployment, Terraform will show:
- **SQS Queue URL:** Copy this for your Spring Boot app
- **Lambda Function Name:** For monitoring in AWS Console
- **AWS Region:** Where everything is deployed

## Testing the Setup

### Send a Test Message to SQS
```bash
aws sqs send-message \
  --queue-url YOUR_QUEUE_URL_FROM_OUTPUT \
  --message-body '{"bookingId": "test-123", "customerName": "John Doe", "vacationTitle": "Italy"}'
```

### Check Lambda Logs
```bash
aws logs tail /aws/lambda/travelapp-booking-processor --follow
```

## Integration with Your Spring Boot App (Optional)

Add to `demo/pom.xml`:
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sqs</artifactId>
    <version>2.20.0</version>
</dependency>
```

Example service to send messages:
```java
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import org.springframework.stereotype.Service;

@Service
public class SqsService {
    private final SqsClient sqsClient = SqsClient.create();
    
    public void sendBookingMessage(String bookingData) {
        SendMessageRequest request = SendMessageRequest.builder()
            .queueUrl("YOUR_SQS_QUEUE_URL") // From terraform output
            .messageBody(bookingData)
            .build();
        sqsClient.sendMessage(request);
    }
}
```

Set environment variables in Fly.io:
```bash
fly secrets set AWS_ACCESS_KEY_ID=your_key
fly secrets set AWS_SECRET_ACCESS_KEY=your_secret
fly secrets set AWS_REGION=us-east-1
```

## Cost
- **SQS:** First 1 million requests/month are free
- **Lambda:** First 1 million requests/month are free
- **CloudWatch Logs:** Very minimal cost for logs
- **Total:** Should be $0 for learning/small usage

## Cleanup
When you're done experimenting:
```bash
terraform destroy
```
Type `yes` to remove all AWS resources.

## Troubleshooting

### Lambda not triggering?
- Check IAM permissions in AWS Console
- Verify SQS queue has messages: `aws sqs get-queue-attributes --queue-url YOUR_URL --attribute-names All`

### Deployment fails?
- Run `aws configure` to verify credentials
- Check you have permissions to create Lambda/SQS/IAM resources

### Need help?
- Check AWS CloudWatch Logs for Lambda errors
- Verify Terraform state: `terraform show`

## Files Overview
- `main.tf`: Main infrastructure definition
- `variables.tf`: Configurable variables (region, etc.)
- `outputs.tf`: Displays important values after deployment
- `lambda_function.py`: Lambda function code (Python) with booking processing features
- `requirements.txt`: Python dependencies (boto3 optional for advanced features)
- `lambda_function.zip`: Packaged code (generated)
- `ENABLE_FEATURES.md`: Guide to enable email, analytics, reports, etc.

## Notes
- **Does NOT affect Fly.io:** Completely separate AWS account/resources
- **Can be deleted anytime:** Run `terraform destroy`
- **Learning resource:** Great for understanding serverless architecture
- **Scalable:** Auto-scales with demand (no server management)
