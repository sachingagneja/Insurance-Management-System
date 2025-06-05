package com.zeta.digital_insurance_management_system.exception;

public class SupportTicketExceptions {

    public static class SupportTicketException extends RuntimeException {
        public SupportTicketException(String message) {
            super(message);
        }
    }

    public static class TicketNotFoundException extends SupportTicketException {
        public TicketNotFoundException(Long ticketId) {
            super("SupportTicket not found with id: " + ticketId);
        }
    }

    public static class TicketAlreadyClosedException extends SupportTicketException {
        public TicketAlreadyClosedException(Long ticketId) {
            super("SupportTicket with id " + ticketId + " is already closed and cannot be updated.");
        }
    }

    public static class InvalidTicketTransitionException extends SupportTicketException {
        public InvalidTicketTransitionException(Long ticketId) {
            super("SupportTicket with id " + ticketId + " is already closed or resolved and cannot be updated.");
        }
    }

}
