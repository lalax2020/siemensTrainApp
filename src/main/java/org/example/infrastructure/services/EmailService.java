package org.example.infrastructure.services;

import lombok.RequiredArgsConstructor;
import org.example.booking.entity.Booking;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(Booking booking){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getCustomerEmail());
        msg.setSubject("Booking Confirmation");
        msg.setText("Dear " + booking.getCustomerName() + ",\n\n" +
            "Your booking for " + booking.getDepartureStation() + " to " +
            booking.getArrivalStation() + " on " + booking.getTravelDate() +
            " for " + booking.getNoOfSeats() + " seats " +
            " has been confirmed.\n\n" +
            "Thank you for choosing our service!");
        mailSender.send(msg);
    }

    public void sendDelayNotification(Booking booking , String stationName, int delay){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getCustomerEmail());
        msg.setSubject("Train Delay Notification");
        msg.setText("Dear " + booking.getCustomerName()
        + "\n\n Your train is currently delayed by " + delay
        + "minutes at station " + stationName + ".");
        mailSender.send(msg);
    }
}
