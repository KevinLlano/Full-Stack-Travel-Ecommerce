output "sqs_queue_url" {
  description = "URL of the SQS queue (use this in your Spring Boot app)"
  value       = aws_sqs_queue.booking_queue.url
}

output "sqs_queue_arn" {
  description = "ARN of the SQS queue"
  value       = aws_sqs_queue.booking_queue.arn
}

output "lambda_function_name" {
  description = "Name of the Lambda function"
  value       = aws_lambda_function.booking_processor.function_name
}

output "lambda_function_arn" {
  description = "ARN of the Lambda function"
  value       = aws_lambda_function.booking_processor.arn
}

output "aws_region" {
  description = "AWS region where resources are deployed"
  value       = var.aws_region
}
