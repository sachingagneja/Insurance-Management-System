package com.zeta.digital_insurance_management_system.service.supportTicket;

import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.model.SupportTicket;

import java.util.List;

public interface ISupportTicketService {
    SupportTicket createTicket(SupportTicket ticket);
    List<SupportTicket> getTicketsByUserId(Long userId);
    SupportTicket updateTicket(Long ticketId, String response, SupportTicketStatus status);
    SupportTicket getTicketById(Long ticketId);
    List<SupportTicket> getAllTickets();
    void deleteTicket(Long ticketId);
}
