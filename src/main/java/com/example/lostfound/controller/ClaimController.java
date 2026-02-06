package com.example.lostfound.controller;

import com.example.lostfound.model.*;
import com.example.lostfound.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository claimRepo;
    private final ItemPostRepository itemRepo;
    private final UserRepository userRepo;

    public ClaimController(ClaimRepository claimRepo,
                           ItemPostRepository itemRepo,
                           UserRepository userRepo) {
        this.claimRepo = claimRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    // =========================
    // USER SEND CLAIM (LOGIN REQUIRED)
    // =========================
    @PostMapping("/{itemId}")
    public ResponseEntity<?> claim(@PathVariable Long itemId,
                                   @RequestParam String reason,
                                   Authentication auth) {

        // 🔐 LOGIN CHECK
        if (auth == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Login required to submit claim");
        }

        User user = userRepo.findByUsername(auth.getName()).orElseThrow();
        ItemPost item = itemRepo.findById(itemId).orElseThrow();

        // ❌ Rule 1 – Only FOUND items can be claimed
        if (item.isLost()) {
            return ResponseEntity.badRequest()
                    .body("Lost items cannot be claimed");
        }

        // ❌ Rule 2 – Finder cannot claim their own item
        if (item.getReportedBy() != null &&
            item.getReportedBy().getId().equals(user.getId())) {

            return ResponseEntity.badRequest()
                    .body("You cannot claim your own found item");
        }

        // ❌ Rule 3 – Already returned item cannot be claimed
        if ("RETURNED".equalsIgnoreCase(item.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("This item is already returned");
        }

        // ❌ Rule 4 – Prevent duplicate claim
        boolean alreadyClaimed = claimRepo.findByClaimant(user)
                .stream()
                .anyMatch(c -> c.getItemPost().getId().equals(itemId));

        if (alreadyClaimed) {
            return ResponseEntity.badRequest()
                    .body("You already claimed this item");
        }

        Claim claim = Claim.builder()
                .itemPost(item)
                .claimant(user)
                .reason(reason)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        claimRepo.save(claim);

        return ResponseEntity.ok("Claim submitted successfully");
    }

    // =========================
    // ADMIN VIEW PENDING CLAIMS
    // =========================
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/pending")
    public List<Claim> pending() {
        return claimRepo.findAll()
                .stream()
                .filter(c -> "PENDING".equals(c.getStatus()))
                .toList();
    }

    // =========================
    // ADMIN APPROVE / REJECT
    // =========================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id,
                                     @RequestParam String status,
                                     Authentication auth) {

        if (!status.equalsIgnoreCase("APPROVED") &&
            !status.equalsIgnoreCase("REJECTED")) {

            return ResponseEntity.badRequest()
                    .body("Invalid status");
        }

        Claim claim = claimRepo.findById(id).orElseThrow();
        User admin = userRepo.findByUsername(auth.getName()).orElseThrow();

        claim.setStatus(status.toUpperCase());
        claim.setResolvedBy(admin);
        claim.setResolvedAt(Instant.now());

        // OPTIONAL: mark item as returned
        if ("APPROVED".equalsIgnoreCase(status)) {
            ItemPost item = claim.getItemPost();
            item.setStatus("RETURNED");
            itemRepo.save(item);
        }

        claimRepo.save(claim);

        return ResponseEntity.ok("Claim " + status);
    }

    // =========================
    // USER VIEW OWN CLAIMS
    // =========================
    @GetMapping("/my")
    public ResponseEntity<?> myClaims(Authentication auth) {

        if (auth == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Login required");
        }

        User user = userRepo.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(claimRepo.findByClaimant(user));
    }
}
