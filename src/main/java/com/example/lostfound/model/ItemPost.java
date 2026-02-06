package com.example.lostfound.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "item_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition="TEXT")
    private String description;

    private String location;

    private String mobile;  

    private LocalDate dateEvent;

    private String category;

    private boolean lost;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Builder.Default
    private String status = "UNVERIFIED";     // default status

    @ManyToOne
    private User reportedBy;

    @Builder.Default
    private Instant createdAt = Instant.now();  // default timestamp
}