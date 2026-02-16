package com.example.lostfound.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATIONS =================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnoreProperties({"photo", "reportedBy"})
    private ItemPost itemPost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private User claimant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    @JsonIgnore
    private User resolvedBy;

    // ================= CLAIM DETAILS =================

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String proofType;

    @Column(nullable = false, length = 1000)
    private String reason;

    // ================= PROOF IMAGES =================

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] govProofImage;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] productProofImage;

    // ================= STATUS =================

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant resolvedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.status = "PENDING";
    }
}