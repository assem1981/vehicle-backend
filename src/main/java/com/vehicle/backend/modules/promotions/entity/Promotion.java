package com.vehicle.backend.modules.promotions.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // E.g., SUMMER20, WELCOME10

    @Column(nullable = false)
    private BigDecimal discountPercentage; // E.g., 20.0 for 20% off

    private LocalDate expirationDate;

    private Integer usageLimit;

    @Builder.Default
    private Integer timesUsed = 0;

    @Builder.Default
    private boolean active = true;
}
