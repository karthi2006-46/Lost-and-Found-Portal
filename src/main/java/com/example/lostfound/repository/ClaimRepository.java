package com.example.lostfound.repository;

import com.example.lostfound.model.Claim;
import com.example.lostfound.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimant(User user);
}
