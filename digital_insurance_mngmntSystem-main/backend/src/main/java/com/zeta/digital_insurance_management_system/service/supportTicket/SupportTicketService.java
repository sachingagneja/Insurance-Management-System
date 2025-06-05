package com.zeta.digital_insurance_management_system.service.supportTicket;

import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions.TicketNotFoundException;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions.TicketAlreadyClosedException;
import com.zeta.digital_insurance_management_system.model.SupportTicket;
import com.zeta.digital_insurance_management_system.repository.SupportTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportTicketService implements ISupportTicketService {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketService.class);

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Override
    public SupportTicket createTicket(SupportTicket ticket) {
        logger.info("Creating new support ticket for user ID: {} with subject: {}", ticket.getUser().getId(), ticket.getSubject());
        ticket.setStatus(SupportTicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        logger.info("Successfully created support ticket with ID: {}", savedTicket.getId());
        return savedTicket;
    }

    @Override
    public List<SupportTicket> getTicketsByUserId(Long userId) {
        logger.info("Fetching support tickets for user ID: {}", userId);
        List<SupportTicket> tickets = supportTicketRepository.findByUserId(userId);
        logger.info("Found {} support tickets for user ID: {}", tickets.size(), userId);
        return tickets;
    }

    @Override
    public SupportTicket updateTicket(Long ticketId, String response, SupportTicketStatus status) {
        logger.info("Updating support ticket ID: {} with status: {} and response: {}", ticketId, status, response);
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.warn("Support ticket with ID: {} not found for update.", ticketId);
                    return new TicketNotFoundException(ticketId);
                });

            if (ticket.getStatus() == SupportTicketStatus.CLOSED) {
                logger.warn("Attempted to update already closed support ticket ID: {}", ticketId);
                throw new TicketAlreadyClosedException(ticketId);
            }

            if (ticket.getStatus() != SupportTicketStatus.OPEN && status == SupportTicketStatus.RESOLVED) {
                logger.warn("Invalid status transition for support ticket ID: {} from {} to RESOLVED", ticketId, ticket.getStatus());
                throw new SupportTicketExceptions.InvalidTicketTransitionException(ticketId);
            }

        ticket.setResponse(response);
        ticket.setStatus(status);
        if (status == SupportTicketStatus.RESOLVED || status == SupportTicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
            logger.info("Support ticket ID: {} marked as {} at {}", ticketId, status, ticket.getResolvedAt());
        }
        SupportTicket updatedTicket = supportTicketRepository.save(ticket);
        logger.info("Successfully updated support ticket ID: {}", ticketId);
        return updatedTicket;
    }

    @Override
    public SupportTicket getTicketById(Long ticketId) {
        logger.info("Fetching support ticket with ID: {}", ticketId);
        return supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.warn("Support ticket with ID: {} not found.", ticketId);
                    return new TicketNotFoundException(ticketId);
                });
    }

    @Override
    public List<SupportTicket> getAllTickets() {
        logger.info("Fetching all support tickets.");
        List<SupportTicket> tickets = supportTicketRepository.findAll();
        logger.info("Found {} total support tickets.", tickets.size());
        return tickets;
    }

    @Override
    public void deleteTicket(Long ticketId) {
        logger.info("Deleting support ticket with ID: {}", ticketId);
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.warn("Support ticket with ID: {} not found for deletion.", ticketId);
                    return new TicketNotFoundException(ticketId);
                });
        supportTicketRepository.delete(ticket);
        logger.info("Successfully deleted support ticket with ID: {}", ticketId);
    }
}
