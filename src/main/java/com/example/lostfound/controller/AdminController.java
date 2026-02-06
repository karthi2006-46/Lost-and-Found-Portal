package com.example.lostfound.controller;

import com.example.lostfound.model.Claim;
import com.example.lostfound.model.ItemPost;
import com.example.lostfound.model.User;
import com.example.lostfound.repository.ClaimRepository;
import com.example.lostfound.repository.ItemPostRepository;
import com.example.lostfound.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")

public class AdminController {
    private final ItemPostRepository itemRepo;
    private final ClaimRepository claimRepo;
    private final UserRepository userRepo;
    public AdminController(ItemPostRepository itemRepo, ClaimRepository claimRepo, UserRepository userRepo) {
        this.itemRepo = itemRepo; this.claimRepo = claimRepo; this.userRepo = userRepo;
    }

    @GetMapping("/items")
    public List<ItemPost> listUnverified() {
        return itemRepo.findAll().stream().filter(i -> !"VERIFIED".equals(i.getStatus()) && !"REMOVED".equals(i.getStatus())).toList();
    }

    @PutMapping("/items/{id}/verify")
    public ResponseEntity<?> verifyItem(@PathVariable Long id, @RequestParam String action, @RequestParam(required=false) String note) {
        ItemPost it = itemRepo.findById(id).orElseThrow();
        if ("verify".equalsIgnoreCase(action)) it.setStatus("VERIFIED");
        else if ("remove".equalsIgnoreCase(action)) it.setStatus("REMOVED");
        else if ("returned".equalsIgnoreCase(action)) it.setStatus("RETURNED");
        itemRepo.save(it);
        return ResponseEntity.ok(it);
    }

    @GetMapping("/claims")
    public List<Claim> listClaims() { return claimRepo.findAll(); }

    @PutMapping("/claims/{id}")
    public ResponseEntity<?> resolveClaim(@PathVariable Long id, @RequestParam String action, @RequestParam String adminUsername) {
        Claim c = claimRepo.findById(id).orElseThrow();
        User admin = userRepo.findByUsername(adminUsername).orElseThrow();
        if ("approve".equalsIgnoreCase(action)) c.setStatus("APPROVED");
        else c.setStatus("REJECTED");
        c.setResolvedBy(admin);
        c.setResolvedAt(Instant.now());
        claimRepo.save(c);
        return ResponseEntity.ok(c);
    }
}