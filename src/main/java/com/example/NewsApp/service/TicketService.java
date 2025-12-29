package com.example.NewsApp.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.NewsApp.dto.TicketResponse;
import com.example.NewsApp.entity.Role;
import com.example.NewsApp.entity.Ticket;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.enums.TicketStatus;
import com.example.NewsApp.repository.TicketRepository;
import com.example.NewsApp.repository.UserRepository;
import com.example.NewsApp.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;

    @Transactional
    public TicketResponse createTicket(
            Long userId,
            String title,
            String description,
            String email
    ) {
        // 1. Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Create ticket
        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .email(email)
                .ticketStatus(TicketStatus.OPEN)   // default status
                .user(user)
                .build();

        // 3. Save ticket
        Ticket savedTicket = ticketRepository.save(ticket);

        // 4. Send email AFTER save
        emailService.sendTicketCreatedEmail(savedTicket);

        // 5. Return DTO (NO ENTITY)
        return TicketResponse.builder()
                .id(savedTicket.getId())
                .title(savedTicket.getTitle())
                .description(savedTicket.getDescription())
                .email(savedTicket.getEmail())
                .status(savedTicket.getTicketStatus().name())
                .createdAt(savedTicket.getCreatedAt())
                .build();
    }


    @Transactional(readOnly = true)
    public List<TicketResponse> getTickets(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = userRoleRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getRole();

        List<Ticket> tickets;

        if (role.getId().equals(1L)) {
            // ADMIN
            tickets = ticketRepository.findAll();
        } else {
            // NORMAL USER
            tickets = ticketRepository.findByUser(user);
        }

        return tickets.stream()
                .map(ticket -> TicketResponse.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .description(ticket.getDescription())
                        .email(ticket.getEmail())
                        .status(ticket.getTicketStatus().name())
                        .createdAt(ticket.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public Ticket updateTicketStatus(Long userId, Long ticketId, TicketStatus status) {
        Long roleId = userRoleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getRole().getId();

        if (roleId != 1L) {
            throw new RuntimeException("Only admin can update ticket status");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setTicketStatus(status);
        Ticket updatedTicket = ticketRepository.save(ticket);

        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            emailService.sendTicketResolvedEmail(updatedTicket);
        }

        return updatedTicket;
    }
}
