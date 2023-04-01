package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
    //Attempt a payment of amountSent for reservationId using the given mode ("cASh", "card", or "upi")
    //If the amountSent is less than bill, throw "Insufficient Amount" exception, otherwise update payment attributes
    //If the mode contains a string other than "cash", "card", or "upi" (any character in uppercase or lowercase), throw
    //"Payment mode not detected" exception.
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Spot spot = reservation.getSpot();
        //create bill
        int bill = reservation.getNumberOfHours() * spot.getPricePerHour();

        Payment payment = new Payment();
        payment.setReservation(reservation);

        String updatedMode = mode.toUpperCase();
        payment.setPaymentCompleted(false);

        if(updatedMode == "CASH") {
            payment.setPaymentMode(PaymentMode.CASH);
        } else if (updatedMode == "CARD") {
            payment.setPaymentMode(PaymentMode.CARD);
        } else if (updatedMode == "UPI") {
            payment.setPaymentMode(PaymentMode.UPI);
        } else {
            throw new Exception("Payment mode not detected");
        }

        if(amountSent < bill) {
            throw new Exception("Insufficient Amount");
        }

        spot.setOccupied(false);
        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);
        reservationRepository2.save(reservation);
        return payment;
    }
}
