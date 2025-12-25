package com.example.lostfound.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "claims")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private ItemPost itemPost;

    @ManyToOne(optional=false)
    private User claimant;

    @Column(columnDefinition="TEXT")
    private String reason;

    private String status; // PENDING, APPROVED, REJECTED

    private Instant createdAt = Instant.now();

    @ManyToOne
    private User resolvedBy;

    private Instant resolvedAt;
}