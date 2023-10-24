package com.fzoo.zoomanagementsystem.controller;

import com.fzoo.zoomanagementsystem.model.Ticket;
import com.fzoo.zoomanagementsystem.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @Operation(
            summary = "List tickets",
            description = "Get list of sold tickets from database"
    )
    @GetMapping("v1/ticket")
    public List<Ticket> getAllTicket(){
        return ticketService.getAllTicket();
    }

    @Operation(
            summary = "Create tickets",
            description = "Create ticket transaction and save to database"
    )
    @PostMapping("v1/ticket")
    public void checkoutTicket(@RequestBody Ticket request){
        ticketService.ticketCheckout(request);
    }
}
