package com.assessment.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

/**
 * Service for sending messages to AWS SQS queue
 * This is OPTIONAL - only works if AWS credentials are configured
 */
@Service
public class SqsService {

    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);

    @Value("${aws.sqs.queue.url:}")
    private String queueUrl;

    @Value("${aws.sqs.enabled:false}")
    private boolean sqsEnabled;

    private SqsClient sqsClient;

    /**
     * Initialize SQS client only if enabled and credentials exist
     */
    private SqsClient getSqsClient() {
        if (sqsClient == null && sqsEnabled && queueUrl != null && !queueUrl.isEmpty()) {
            try {
                sqsClient = SqsClient.builder()
                        .region(Region.US_EAST_1) // Change if your queue is in different region
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
                logger.info("SQS client initialized successfully for queue: {}", queueUrl);
            } catch (Exception e) {
                logger.warn("Failed to initialize SQS client (AWS credentials may not be configured): {}", e.getMessage());
                sqsEnabled = false; // Disable SQS if initialization fails
            }
        }
        return sqsClient;
    }

    /**
     * Send a booking message to SQS queue
     * Fails gracefully if SQS is not configured
     *
     * @param messageBody JSON string with booking data
     */
    public void sendBookingMessage(String messageBody) {
        if (!sqsEnabled) {
            logger.debug("SQS is disabled, skipping message send");
            return;
        }

        if (queueUrl == null || queueUrl.isEmpty()) {
            logger.debug("SQS queue URL not configured, skipping message send");
            return;
        }

        try {
            SqsClient client = getSqsClient();
            if (client == null) {
                logger.debug("SQS client not available, skipping message send");
                return;
            }

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            SendMessageResponse response = client.sendMessage(request);
            logger.info("Successfully sent message to SQS. MessageId: {}", response.messageId());

        } catch (Exception e) {
            // Log error but don't break the application
            logger.error("Failed to send message to SQS (non-critical error): {}", e.getMessage());
        }
    }

    /**
     * Format booking data as JSON string for SQS
     *
     * @param orderTrackingNumber Unique order ID
     * @param customerName Customer full name
     * @param vacationTitle Vacation destination
     * @param totalPrice Total booking price
     * @return JSON formatted string
     */
    public String formatBookingMessage(String orderTrackingNumber, String customerName, 
                                      String vacationTitle, Double totalPrice) {
        return String.format("""
                {
                    "orderTrackingNumber": "%s",
                    "customerName": "%s",
                    "vacationTitle": "%s",
                    "totalPrice": %.2f,
                    "timestamp": "%s"
                }
                """,
                orderTrackingNumber,
                customerName,
                vacationTitle,
                totalPrice,
                java.time.Instant.now().toString()
        );
    }
}
