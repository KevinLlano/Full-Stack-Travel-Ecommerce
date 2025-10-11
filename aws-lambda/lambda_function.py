"""
AWS Lambda function to process travel booking messages from SQS

This function is triggered automatically when messages arrive in the SQS queue.
It processes booking confirmations (e.g., sends emails, updates databases, etc.)
"""

import json
import logging
import os
from datetime import datetime

# Set up logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)

def track_booking_metrics(booking_data):
    """
    Track booking metrics for analytics
    TODO: Integrate with CloudWatch Metrics, DataDog, or custom analytics
    """
    logger.info(f'📊 Tracking metrics for booking: {booking_data.get("orderNumber")}')
    # Example: Send to CloudWatch custom metrics
    # cloudwatch = boto3.client('cloudwatch')
    # cloudwatch.put_metric_data(
    #     Namespace='TravelApp/Bookings',
    #     MetricData=[
    #         {
    #             'MetricName': 'TotalBookingValue',
    #             'Value': booking_data.get('totalPrice', 0),
    #             'Unit': 'None'
    #         }
    #     ]
    # )
    pass

def generate_booking_report(booking_data):
    """
    Generate and store booking report
    TODO: Save to S3, DynamoDB, or external database
    """
    logger.info(f'📄 Generating report for booking: {booking_data.get("orderTrackingNumber")}')
    # Example: Save to S3
    # s3 = boto3.client('s3')
    # s3.put_object(
    #     Bucket='travelapp-bookings',
    #     Key=f'reports/{booking_data.get("orderTrackingNumber")}.json',
    #     Body=json.dumps(booking_data)
    # )
    pass

def send_confirmation_email(customer_email, order_number, vacation_title, total_price):
    """
    Send booking confirmation email via AWS SES
    TODO: Configure AWS SES and uncomment
    """
    logger.info(f'📧 Would send confirmation email to: {customer_email}')
    # Example: Send via AWS SES
    # ses = boto3.client('ses', region_name='us-east-1')
    # ses.send_email(
    #     Source='noreply@yourdomain.com',
    #     Destination={'ToAddresses': [customer_email]},
    #     Message={
    #         'Subject': {'Data': f'Booking Confirmation - Order #{order_number}'},
    #         'Body': {
    #             'Text': {
    #                 'Data': f'''
    #                     Thank you for your booking!
    #                     
    #                     Order Number: {order_number}
    #                     Vacation: {vacation_title}
    #                     Total: ${total_price:.2f}
    #                     
    #                     We look forward to serving you!
    #                 '''
    #             }
    #         }
    #     }
    # )
    pass

def lambda_handler(event, context):
    """
    Main Lambda handler function
    Processes SQS messages containing booking data
    """
    logger.info('Lambda function invoked')
    logger.info('Received event: %s', json.dumps(event, indent=2))

    # Get environment variables
    queue_url = os.environ.get('QUEUE_URL', 'unknown')

    # Process each SQS message
    processed_count = 0

    for record in event.get('Records', []):
        try:
            # Parse the message body
            message_body = json.loads(record['body'])
            message_id = record['messageId']

            logger.info('Processing booking message: %s', json.dumps(message_body, indent=2))

            # Extract booking details
            order_number = message_body.get('orderTrackingNumber', 'unknown')
            customer_name = message_body.get('customerName', 'unknown')
            vacation_title = message_body.get('vacationTitle', 'unknown')
            total_price = message_body.get('totalPrice', 0.0)
            timestamp = message_body.get('timestamp', datetime.utcnow().isoformat())

            # Process booking (add your custom logic here)
            
            # 1. Log booking details (FREE - goes to CloudWatch Logs)
            logger.info(f'📋 New Booking: Order #{order_number}')
            logger.info(f'👤 Customer: {customer_name}')
            logger.info(f'🏖️  Vacation: {vacation_title}')
            logger.info(f'💰 Total: ${total_price:.2f}')
            logger.info(f'🕒 Time: {timestamp}')
            
            # 2. Send confirmation email (TODO: Add email service)
            # Uncomment and configure when you set up AWS SES
            # send_confirmation_email(
            #     customer_email='customer@example.com',
            #     order_number=order_number,
            #     vacation_title=vacation_title,
            #     total_price=total_price
            # )
            
            # 3. High-value booking alert
            if total_price > 5000:
                logger.warning(f'🚨 HIGH-VALUE BOOKING: ${total_price:.2f} - Order #{order_number}')
                # TODO: Send admin notification via SNS or email
            
            # 4. Track booking metrics (placeholder for analytics)
            # TODO: Send to analytics service (e.g., CloudWatch Metrics, DataDog)
            track_booking_metrics({
                'orderNumber': order_number,
                'totalPrice': total_price,
                'vacationTitle': vacation_title,
                'timestamp': timestamp
            })
            
            # 5. Generate booking report (placeholder)
            # TODO: Store in S3, DynamoDB, or external database
            generate_booking_report({
                'orderTrackingNumber': order_number,
                'customerName': customer_name,
                'vacationTitle': vacation_title,
                'totalPrice': total_price,
                'processedAt': datetime.utcnow().isoformat()
            })

            # Simulate processing
            booking_result = {
                'messageId': message_id,
                'bookingData': message_body,
                'processedAt': datetime.utcnow().isoformat(),
                'status': 'processed'
            }

            logger.info('Booking processed successfully: %s', json.dumps(booking_result, indent=2))
            processed_count += 1

            # If processing succeeds, the message is automatically deleted from SQS
            # If an error is thrown, the message will retry based on SQS settings

        except json.JSONDecodeError as e:
            logger.error('Failed to parse message body: %s', str(e))
            raise e
        except Exception as e:
            logger.error('Error processing message: %s', str(e))
            # Throwing an error will cause the message to retry
            raise e

    # Return success response
    return {
        'statusCode': 200,
        'body': json.dumps({
            'message': 'Messages processed successfully',
            'processedCount': processed_count,
            'totalMessages': len(event.get('Records', []))
        })
    }
