terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 1.0"
}

provider "aws" {
  region = var.aws_region
}

# SQS Queue for booking messages
resource "aws_sqs_queue" "booking_queue" {
  name                       = "travelapp-bookings-queue"
  delay_seconds              = 0
  max_message_size           = 262144
  message_retention_seconds  = 345600  # 4 days
  receive_wait_time_seconds  = 10
  visibility_timeout_seconds = 300

  tags = {
    Name        = "TravelApp Bookings Queue"
    Environment = "production"
    Project     = "Angular-Travel-System"
  }
}

# Dead Letter Queue for failed messages
resource "aws_sqs_queue" "booking_dlq" {
  name = "travelapp-bookings-dlq"

  tags = {
    Name        = "TravelApp Bookings DLQ"
    Environment = "production"
    Project     = "Angular-Travel-System"
  }
}

# IAM role for Lambda execution
resource "aws_iam_role" "lambda_execution_role" {
  name = "travelapp-lambda-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name    = "TravelApp Lambda Execution Role"
    Project = "Angular-Travel-System"
  }
}

# IAM policy for Lambda to access SQS and CloudWatch Logs
resource "aws_iam_role_policy" "lambda_policy" {
  name = "travelapp-lambda-policy"
  role = aws_iam_role.lambda_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Resource = aws_sqs_queue.booking_queue.arn
      },
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:*:*:*"
      }
    ]
  })
}

# Lambda function
resource "aws_lambda_function" "booking_processor" {
  filename         = "lambda_function.zip"
  function_name    = "travelapp-booking-processor"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "lambda_function.lambda_handler"
  source_code_hash = filebase64sha256("lambda_function.zip")
  runtime         = "python3.9"
  timeout         = 60
  memory_size     = 256

  environment {
    variables = {
      QUEUE_URL = aws_sqs_queue.booking_queue.url
    }
  }

  tags = {
    Name        = "TravelApp Booking Processor"
    Environment = "production"
    Project     = "Angular-Travel-System"
  }

  depends_on = [aws_iam_role_policy.lambda_policy]
}

# Connect SQS to Lambda (Event Source Mapping)
resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.booking_queue.arn
  function_name    = aws_lambda_function.booking_processor.arn
  batch_size       = 10
  enabled          = true

  depends_on = [aws_iam_role_policy.lambda_policy]
}

# CloudWatch Log Group for Lambda
resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.booking_processor.function_name}"
  retention_in_days = 7

  tags = {
    Name    = "TravelApp Lambda Logs"
    Project = "Angular-Travel-System"
  }
}
