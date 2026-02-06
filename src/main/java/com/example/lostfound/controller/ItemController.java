package com.example.lostfound.controller;

import com.example.lostfound.model.ItemPost;
import com.example.lostfound.model.User;
import com.example.lostfound.repository.ItemPostRepository;
import com.example.lostfound.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemPostRepository itemRepo;
    private final UserRepository userRepo;

    public ItemController(ItemPostRepository itemRepo, UserRepository userRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    // 🔍 PUBLIC SEARCH
    @GetMapping
    public List<ItemPost> search(
            @RequestParam(required=false) String q,
            @RequestParam(required=false) String location,
            @RequestParam(required=false) String category,
            @RequestParam(required=false) Boolean isLost,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {

        return itemRepo.search(q, location, category, isLost, PageRequest.of(page,size));
    }

    // 👁️ PUBLIC VIEW SINGLE ITEM  ✅ FIX
    @GetMapping("/{id}")
    public ResponseEntity<ItemPost> getItemById(@PathVariable Long id) {
        return itemRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🖼️ PUBLIC PHOTO VIEW
    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> photo(@PathVariable Long id) {
        return itemRepo.findById(id)
            .filter(i -> i.getPhoto() != null)
            .map(i -> ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(i.getPhoto()))
            .orElse(ResponseEntity.notFound().build());
    }

    // ➕ POST LOST / FOUND ITEM
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam String title,
            @RequestParam(required=false) String description,
            @RequestParam(required=false) String location,
            @RequestParam String dateEvent,
            @RequestParam String category,
            @RequestParam String mobile,
            @RequestParam boolean lost,
            @RequestParam(required=false) MultipartFile photo,
            Authentication auth) throws Exception {

        // 🔐 LOST ITEM → LOGIN REQUIRED
        if (lost && auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login required to post lost item");
        }

        User reporter = null;
        if (auth != null) {
            reporter = userRepo.findByUsername(auth.getName()).orElseThrow();
        }

        ItemPost item = new ItemPost();
        item.setTitle(title);
        item.setDescription(description);
        item.setLocation(location);
        item.setMobile(mobile);
        item.setCategory(category);
        item.setLost(lost);
        item.setDateEvent(LocalDate.parse(dateEvent));
        item.setReportedBy(reporter);

        if (photo != null && !photo.isEmpty())
            item.setPhoto(photo.getBytes());

        item.setStatus(lost ? "UNVERIFIED" : "VERIFIED");

        return ResponseEntity.ok(itemRepo.save(item));
    }
}
