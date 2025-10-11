package com.assessment.demo.controllers;

import com.assessment.demo.dto.Purchase;
import com.assessment.demo.dto.PurchaseResponse;
import com.assessment.demo.services.CheckoutService;
import com.assessment.demo.services.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final SqsService sqsService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, SqsService sqsService) {
        this.checkoutService = checkoutService;
        this.sqsService = sqsService;
    }

    //takes a Purchase and places an order in the checkout, giving a PurchaseResponse
    @PostMapping("/purchase")
    public PurchaseResponse placeOrder(@RequestBody Purchase purchase) {
        PurchaseResponse response = checkoutService.placeOrder(purchase);
        
        // Send booking notification to SQS (optional - won't break if SQS is not configured)
        try {
            String customerName = purchase.getCustomer().getFirstName() + " " + 
                                 purchase.getCustomer().getLastName();
            String vacationTitle = purchase.getCart().getCartItems().isEmpty() 
                ? "N/A" 
                : "Vacation Package"; // Simplified - adjust based on your cart structure
            
            String message = sqsService.formatBookingMessage(
                response.getOrderTrackingNumber(),
                customerName,
                vacationTitle,
                purchase.getCart().getPackagePrice()
            );
            
            sqsService.sendBookingMessage(message);
        } catch (Exception e) {
            // Log but don't fail the order if SQS fails
            System.err.println("Failed to send SQS notification (non-critical): " + e.getMessage());
        }
        
        return response;
    }
}