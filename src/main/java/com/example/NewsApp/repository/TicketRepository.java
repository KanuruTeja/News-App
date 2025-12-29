package com.example.NewsApp.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NewsApp.entity.Ticket;
import com.example.NewsApp.entity.User;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser(User user);
}
