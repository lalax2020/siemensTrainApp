package org.example.infrastructure.exceptions;

public class OverBookingException extends RuntimeException{
    public OverBookingException(String message){
        super(message);
    }
}
