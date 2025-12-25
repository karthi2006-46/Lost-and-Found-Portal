package com.example.lostfound.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "item_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition="TEXT")
    private String description;

    private String location;

    private LocalDate dateEvent; // date lost or found

    private String category;

    private boolean lost; // true if lost, false if found

    @Lob
    private byte[] photo; // optional

    private String status; // UNVERIFIED, VERIFIED, RETURNED, REMOVED

    @ManyToOne
    private User reportedBy;

    private Instant createdAt = Instant.now();
}