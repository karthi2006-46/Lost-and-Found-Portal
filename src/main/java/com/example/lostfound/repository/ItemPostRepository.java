package com.example.lostfound.repository;

import com.example.lostfound.model.ItemPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemPostRepository extends JpaRepository<ItemPost,Long> {
    @Query("SELECT i FROM ItemPost i WHERE " +
           "(:q IS NULL OR lower(i.title) LIKE lower(concat('%',:q,'%')) OR lower(i.description) LIKE lower(concat('%',:q,'%'))) " +
           "AND (:loc IS NULL OR lower(i.location) LIKE lower(concat('%',:loc,'%'))) " +
           "AND (:cat IS NULL OR i.category = :cat) " +
           "AND (:isLost IS NULL OR i.lost = :isLost)")
    List<ItemPost> search(String q, String loc, String cat, Boolean isLost, Pageable pageable);
}