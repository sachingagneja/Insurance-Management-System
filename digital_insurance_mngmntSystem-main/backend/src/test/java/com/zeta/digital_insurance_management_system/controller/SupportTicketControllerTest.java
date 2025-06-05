package com.zeta.digital_insurance_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.digital_insurance_management_system.dto.support.CreateSupportTicketRequest;
import com.zeta.digital_insurance_management_system.dto.support.UpdateSupportTicketRequest;
import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.SupportTicket;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.repository.ClaimManagementRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserRepository;
import com.zeta.digital_insurance_management_system.security.jwt.JwtService;
import com.zeta.digital_insurance_management_system.security.service.MyUserDetailsService;
import com.zeta.digital_insurance_management_system.service.ClaimManagement.ClaimManagementService;
import com.zeta.digital_insurance_management_system.service.policy.PolicyService;
import com.zeta.digital_insurance_management_system.service.supportTicket.ISupportTicketService;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SupportTicketController.class)
public class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISupportTicketService supportTicketService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private PolicyService policyService;

    @MockBean
    private ClaimManagementService claimManagementService;

    @MockBean
    private UserPolicyRepository userPolicyRepository;

    @MockBean
    private ClaimManagementRepository claimManagementRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private SupportTicket supportTicket1;
    private SupportTicket supportTicket2;
    private User user;
    private Policy policy;
    private Claim claim;
    private UserDetails mockUserDetails;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        policy = new Policy();
        policy.setId(101L);
        policy.setName("Test Policy");

        claim = new Claim();
        claim.setId(201L);
        claim.setReason("Test Claim");

        mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getEmail());


        when(jwtService.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtService.extractUserName(anyString())).thenReturn(user.getEmail());

        when(userServiceImpl.getCurrentUserId()).thenReturn(user.getId());
        when(userServiceImpl.getUserById(user.getId())).thenReturn(user);
        
        when(myUserDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);


        supportTicket1 = new SupportTicket(1L, user, null, null, "Subject 1", "Description 1", SupportTicketStatus.OPEN, null, LocalDateTime.now(), null);
        supportTicket2 = new SupportTicket(2L, user, null, null, "Subject 2", "Description 2", SupportTicketStatus.OPEN, null, LocalDateTime.now(), null);
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void createTicket_shouldReturnCreatedTicket_withNoPolicyOrClaim() throws Exception {
        CreateSupportTicketRequest requestDto = new CreateSupportTicketRequest();
        requestDto.setSubject("New Ticket Subject");
        requestDto.setDescription("New Ticket Description");

        when(supportTicketService.createTicket(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket ticketArg = invocation.getArgument(0);
            ticketArg.setId(1L);
            ticketArg.setUser(user);
            ticketArg.setSubject(requestDto.getSubject());
            ticketArg.setDescription(requestDto.getDescription());
            ticketArg.setStatus(SupportTicketStatus.OPEN);
            ticketArg.setCreatedAt(LocalDateTime.now());
            return ticketArg;
        });

        mockMvc.perform(post("/support")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.subject").value(requestDto.getSubject()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.status").value(SupportTicketStatus.OPEN.toString()));

        verify(supportTicketService, times(1)).createTicket(argThat(ticket ->
                ticket.getSubject().equals(requestDto.getSubject()) &&
                ticket.getDescription().equals(requestDto.getDescription()) &&
                ticket.getUser() != null && ticket.getUser().getId().equals(user.getId()) &&
                ticket.getPolicy() == null &&
                ticket.getClaim() == null
        ));
    }
    
    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void createTicket_shouldReturnCreatedTicket_withPolicy() throws Exception {
        CreateSupportTicketRequest requestDto = new CreateSupportTicketRequest();
        requestDto.setSubject("Ticket with Policy");
        requestDto.setDescription("Policy related issue");
        requestDto.setPolicyId(policy.getId());

        when(userPolicyRepository.existsByUserIdAndPolicyId(user.getId(), policy.getId())).thenReturn(true);
        when(policyService.getPolicyById(policy.getId())).thenReturn(policy);

        when(supportTicketService.createTicket(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket ticketArg = invocation.getArgument(0);
            ticketArg.setId(2L);
            ticketArg.setUser(user);
            ticketArg.setSubject(requestDto.getSubject());
            ticketArg.setDescription(requestDto.getDescription());
            ticketArg.setPolicy(policy); // Policy should be set
            ticketArg.setStatus(SupportTicketStatus.OPEN);
            ticketArg.setCreatedAt(LocalDateTime.now());
            return ticketArg;
        });

        mockMvc.perform(post("/support")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.subject").value(requestDto.getSubject()))
                .andExpect(jsonPath("$.policy.id").value(policy.getId())); // This line uses $.policy.id

        verify(supportTicketService, times(1)).createTicket(argThat(ticket ->
                ticket.getPolicy() != null && ticket.getPolicy().getId().equals(policy.getId())
        ));
    }


    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void getTicketsByUser_shouldReturnListOfTickets() throws Exception {
        List<SupportTicket> tickets = Arrays.asList(supportTicket1, supportTicket2);
        when(userServiceImpl.getCurrentUserId()).thenReturn(user.getId());
        when(supportTicketService.getTicketsByUserId(user.getId())).thenReturn(tickets);

        mockMvc.perform(get("/support/user")
                        .header("Authorization", "Bearer mock-token")) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tickets.size()))
                .andExpect(jsonPath("$[0].id").value(supportTicket1.getId()))
                .andExpect(jsonPath("$[1].id").value(supportTicket2.getId()));

        verify(supportTicketService, times(1)).getTicketsByUserId(user.getId());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void getTicketsByUser_shouldReturnEmptyListWhenNoTicketsFound() throws Exception {
        when(userServiceImpl.getCurrentUserId()).thenReturn(user.getId());
        when(supportTicketService.getTicketsByUserId(user.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/support/user")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(supportTicketService, times(1)).getTicketsByUserId(user.getId());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateTicket_shouldReturnUpdatedTicket() throws Exception {
        UpdateSupportTicketRequest updateRequest = new UpdateSupportTicketRequest("Resolved issue", SupportTicketStatus.RESOLVED);
        SupportTicket updatedTicket = new SupportTicket(1L, user, null, null, "Subject 1", "Description 1", SupportTicketStatus.RESOLVED, "Resolved issue", LocalDateTime.now(), LocalDateTime.now());

        when(supportTicketService.updateTicket(eq(1L), eq(updateRequest.getResponse()), eq(updateRequest.getStatus()))).thenReturn(updatedTicket);

        mockMvc.perform(put("/support/{ticketId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedTicket.getId()))
                .andExpect(jsonPath("$.status").value(SupportTicketStatus.RESOLVED.toString()))
                .andExpect(jsonPath("$.response").value(updateRequest.getResponse()));

        verify(supportTicketService, times(1)).updateTicket(1L, updateRequest.getResponse(), updateRequest.getStatus());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateTicket_whenTicketNotFound_shouldReturnNotFound() throws Exception {
        UpdateSupportTicketRequest updateRequest = new UpdateSupportTicketRequest("Resolved issue", SupportTicketStatus.RESOLVED);
        when(supportTicketService.updateTicket(eq(1L), anyString(), any(SupportTicketStatus.class)))
                .thenThrow(new SupportTicketExceptions.TicketNotFoundException(1L));

        mockMvc.perform(put("/support/{ticketId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(supportTicketService, times(1)).updateTicket(1L, updateRequest.getResponse(), updateRequest.getStatus());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateTicket_whenTicketAlreadyClosed_shouldReturnBadRequest() throws Exception {
        UpdateSupportTicketRequest updateRequest = new UpdateSupportTicketRequest("Further details", SupportTicketStatus.CLOSED);
        when(supportTicketService.updateTicket(eq(1L), anyString(), any(SupportTicketStatus.class)))
                .thenThrow(new SupportTicketExceptions.TicketAlreadyClosedException(1L));

        mockMvc.perform(put("/support/{ticketId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        verify(supportTicketService, times(1)).updateTicket(1L, updateRequest.getResponse(), updateRequest.getStatus());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getAllTickets_shouldReturnListOfAllTickets() throws Exception {
        List<SupportTicket> tickets = Arrays.asList(supportTicket1, supportTicket2);
        when(supportTicketService.getAllTickets()).thenReturn(tickets);

        mockMvc.perform(get("/support")
                        .header("Authorization", "Bearer mock-token")) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tickets.size()))
                .andExpect(jsonPath("$[0].id").value(supportTicket1.getId()))
                .andExpect(jsonPath("$[1].id").value(supportTicket2.getId()));

        verify(supportTicketService, times(1)).getAllTickets();
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteTicket_shouldReturnNoContent() throws Exception {
        doNothing().when(supportTicketService).deleteTicket(anyLong());

        mockMvc.perform(delete("/support/{ticketId}", 1L)
                        .with(csrf())) 
                .andExpect(status().isNoContent());

        verify(supportTicketService, times(1)).deleteTicket(1L);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteTicket_whenTicketNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new SupportTicketExceptions.TicketNotFoundException(1L)).when(supportTicketService).deleteTicket(1L);

        mockMvc.perform(delete("/support/{ticketId}", 1L)
                        .with(csrf())) 
                .andExpect(status().isNotFound());

        verify(supportTicketService, times(1)).deleteTicket(1L);
    }
}