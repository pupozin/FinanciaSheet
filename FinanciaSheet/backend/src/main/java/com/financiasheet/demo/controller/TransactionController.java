package com.financiasheet.demo.controller;

import com.financiasheet.demo.dto.TransactionResponse;
import com.financiasheet.demo.entity.User;
import com.financiasheet.demo.repository.TransactionRepository;
import com.financiasheet.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionRepository txRepo;
    private final UserRepository userRepository;

    public TransactionController(TransactionRepository txRepo, UserRepository userRepository) {
        this.txRepo = txRepo;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<TransactionResponse> listTransactions(
            @AuthenticationPrincipal UserDetails principal,
            @PageableDefault(size = 50, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        if (principal == null) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado"));

        return txRepo.findByUser(user, pageable)
                .map(TransactionResponse::fromEntity);
    }
}