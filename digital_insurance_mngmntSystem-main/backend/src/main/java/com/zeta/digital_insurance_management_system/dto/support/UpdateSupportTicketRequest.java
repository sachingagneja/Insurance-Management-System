package com.zeta.digital_insurance_management_system.dto.support;

import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;

public class UpdateSupportTicketRequest {
    private String response;
    private SupportTicketStatus status;

    public UpdateSupportTicketRequest(String response, SupportTicketStatus status) {
        this.response = response;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public SupportTicketStatus getStatus() {
        return status;
    }

    public void setStatus(SupportTicketStatus status) {
        this.status = status;
    }
}
