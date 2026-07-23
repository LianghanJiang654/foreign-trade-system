package com.foreigntrade.foreign_trade_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String country;

    @Column(name = "contact_name")
    private String contactName;

    private String email;

    private String currency;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
