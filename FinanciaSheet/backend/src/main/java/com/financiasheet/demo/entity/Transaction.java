package com.financiasheet.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tx_fingerprint", columnNames = {"fingerprint"})
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;                 // data lancamento

    @Column(nullable = false)
    private BigDecimal amount;              // negativo = gasto; positivo = recebido

    @Column(nullable = false, length = 500)
    private String description;             // descrição original

    @Column(length = 100)
    private String category;                // opcional (ainda vazio — virá de regras futuras)

    @Column(length = 100)
    private String account;                 // ex.: "nubank", "xp", "itau", etc.

    @Column(length = 40)
    private String kind;                    // "EXTRATO" | "FATURA"

    @Column(length = 100)
    private String source;                  // ex.: "nubank_csv", "xp_csv"

    @Column(nullable = false, length = 128)
    private String fingerprint;             // hash p/ evitar duplicatas

    @Column(length = 100)
    private String externalId;              // Identificador da linha (se existir no arquivo)

    public Transaction() {}

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
}
