package com.example.lostfound.controller;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import com.example.lostfound.model.*;
import com.example.lostfound.repository.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // =====================================================
    // USER SUBMIT CLAIM
    // =====================================================
    @PostMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitClaim(
            @PathVariable Long itemId,
            @RequestParam String fullName,
            @RequestParam Integer age,
            @RequestParam String mobile,
            @RequestParam String govProofName,
            @RequestParam String reason,
            @RequestParam MultipartFile govProofImage,
            @RequestParam MultipartFile productProofImage,
            Authentication auth) {

        try {

            if (auth == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Login required");
            }

            if (govProofImage == null || govProofImage.isEmpty()
                    || productProofImage == null || productProofImage.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Both proof images are required");
            }

            User user = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ItemPost item = itemRepo.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            // ❌ Only FOUND items can be claimed
            if (item.isLost()) {
                return ResponseEntity.badRequest()
                        .body("Lost items cannot be claimed");
            }

            // ❌ Cannot claim own item
            if (item.getReportedBy() != null &&
                    item.getReportedBy().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body("You cannot claim your own item");
            }

            // ❌ Already returned
            if ("RETURNED".equalsIgnoreCase(item.getStatus())) {
                return ResponseEntity.badRequest()
                        .body("Item already returned");
            }

            // ❌ Duplicate claim
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
                    .fullName(fullName)
                    .age(age)
                    .mobile(mobile)
                    .proofType(govProofName)
                    .reason(reason)
                    .govProofImage(govProofImage.getBytes())
                    .productProofImage(productProofImage.getBytes())
                    .status("PENDING")
                    .createdAt(Instant.now())
                    .build();

            claimRepo.save(claim);

            return ResponseEntity.ok("Claim submitted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while submitting claim");
        }
    }

    // =====================================================
    // ADMIN VIEW PENDING CLAIMS
    // =====================================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Claim>> getPendingClaims() {

        List<Claim> pending = claimRepo.findAll()
                .stream()
                .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus()))
                .toList();

        return ResponseEntity.ok(pending);
    }

    // =====================================================
    // ADMIN VIEW GOV PROOF IMAGE
    // =====================================================
 @PreAuthorize("hasRole('ADMIN')")
@GetMapping("/proof/gov/{id}")
public ResponseEntity<byte[]> viewGovProof(@PathVariable Long id) throws Exception {

    Claim claim = claimRepo.findById(id).orElseThrow();
    byte[] image = claim.getGovProofImage();

    // 🔥 Automatically detect image type (jpg/png)
    String mimeType = URLConnection.guessContentTypeFromStream(
            new ByteArrayInputStream(image));

    if (mimeType == null) {
        mimeType = "image/jpeg"; // fallback
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .body(image);
}

//     =====================================================
//     ADMIN VIEW PRODUCT PROOF IMAGE
//     =====================================================
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/proof/product/{id}")
public ResponseEntity<byte[]> viewProductProof(@PathVariable Long id) throws Exception {

    Claim claim = claimRepo.findById(id).orElseThrow();
    byte[] image = claim.getProductProofImage();

    String mimeType = URLConnection.guessContentTypeFromStream(
            new ByteArrayInputStream(image));

    if (mimeType == null) {
        mimeType = "image/jpeg";
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .body(image);
}

    // =====================================================
    // ADMIN APPROVE / REJECT
    // =====================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveClaim(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication auth) {

        if (!status.equalsIgnoreCase("APPROVED")
                && !status.equalsIgnoreCase("REJECTED")) {
            return ResponseEntity.badRequest()
                    .body("Invalid status");
        }

        Claim claim = claimRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        User admin = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        claim.setStatus(status.toUpperCase());
        claim.setResolvedBy(admin);
        claim.setResolvedAt(Instant.now());

        // If approved → mark item returned
        if ("APPROVED".equalsIgnoreCase(status)) {
            ItemPost item = claim.getItemPost();
            item.setStatus("RETURNED");
            itemRepo.save(item);
        }

        claimRepo.save(claim);

        return ResponseEntity.ok("Claim " + status);
    }

    // =====================================================
    // USER VIEW OWN CLAIMS
    // =====================================================
    @GetMapping("/my")
    public ResponseEntity<?> myClaims(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login required");
        }

        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(claimRepo.findByClaimant(user));
    }
}