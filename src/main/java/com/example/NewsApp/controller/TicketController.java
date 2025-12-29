package com.example.NewsApp.controller;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.NewsApp.dto.TicketResponse;
import com.example.NewsApp.entity.Ticket;
import com.example.NewsApp.enums.TicketStatus;
import com.example.NewsApp.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<TicketResponse> createTicket(
            @PathVariable Long userId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String email) {

        return ResponseEntity.ok(ticketService.createTicket(userId, title, description, email));
    }


   
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<TicketResponse>> getTickets(@PathVariable Long userId) {
        List<TicketResponse> tickets = ticketService.getTickets(userId);
        return ResponseEntity.ok(tickets);
    }
    

    @PutMapping("/update/{userId}/{ticketId}")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long userId,
            @PathVariable Long ticketId,
            @RequestParam TicketStatus status) {

        Ticket updatedTicket = ticketService.updateTicketStatus(userId, ticketId, status);
        return ResponseEntity.ok(updatedTicket);
    }
}

