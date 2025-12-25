package com.example.lostfound.controller;

import com.example.lostfound.model.Claim;
import com.example.lostfound.model.ItemPost;
import com.example.lostfound.model.User;
import com.example.lostfound.repository.ClaimRepository;
import com.example.lostfound.repository.ItemPostRepository;
import com.example.lostfound.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class ClaimController {
    private final ClaimRepository claimRepo;
    private final ItemPostRepository itemRepo;
    private final UserRepository userRepo;
    public ClaimController(ClaimRepository claimRepo, ItemPostRepository itemRepo, UserRepository userRepo) {
        this.claimRepo = claimRepo; this.itemRepo = itemRepo; this.userRepo = userRepo;
    }

    @PostMapping("/items/{id}/claims")
    public ResponseEntity<?> makeClaim(@PathVariable Long id, @RequestBody Claim request, Authentication auth) {
        String username = (String) auth.getPrincipal();
        User claimant = userRepo.findByUsername(username).orElseThrow();
        ItemPost item = itemRepo.findById(id).orElseThrow();
        Claim c = Claim.builder()
                .itemPost(item)
                .claimant(claimant)
                .reason(request.getReason())
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
        claimRepo.save(c);
        return ResponseEntity.ok(c);
    }
}