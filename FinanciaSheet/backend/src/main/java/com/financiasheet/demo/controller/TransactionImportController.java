package com.financiasheet.demo.controller;

import com.financiasheet.demo.dto.ImportResponse;
import com.financiasheet.demo.entity.User;
import com.financiasheet.demo.repository.UserRepository;
import com.financiasheet.demo.service.ImportService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionImportController {

    private final ImportService importService;
    private final UserRepository userRepository;

    public TransactionImportController(ImportService importService, UserRepository userRepository) {
        this.importService = importService;
        this.userRepository = userRepository;
    }

    /**
     * Importa CSV de extrato ou fatura.
     * Exemplo:
     *  POST /api/v1/transactions/import?kind=EXTRATO&source=nubank_csv&account=nubank
     *  Content-Type: multipart/form-data (file=arquivo.csv)
     */
    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResponse importCsv(@AuthenticationPrincipal UserDetails principal,
                                    @RequestParam(defaultValue = "EXTRATO") String kind,
                                    @RequestParam(defaultValue = "unknown_csv") String source,
                                    @RequestParam(required = false) String account,
                                    @RequestPart("file") MultipartFile file) {

        if (principal == null) {
            throw new org.springframework.security.access.AccessDeniedException("Usu??rio n??o autenticado");
        }

        User u = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));


        return importService.importCsv(u, kind.toUpperCase(), source, account, file);
    }
}
