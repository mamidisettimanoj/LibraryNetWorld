package com.librarynet.controller;

import com.librarynet.dto.BorrowRequest;
import com.librarynet.dto.LoanResponse;
import com.librarynet.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) { this.service = service; }

    @GetMapping
    public List<LoanResponse> list() { return service.list(); }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrow(@Valid @RequestBody BorrowRequest request) { return service.borrow(request); }

    @PutMapping("/{loanId}/return")
    public LoanResponse returnBook(@PathVariable Long loanId) { return service.returnBook(loanId); }
}
