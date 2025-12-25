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

    // 🔍 List items (Home page)
    @GetMapping
    public List<ItemPost> search(@RequestParam(required=false) String q,
                                 @RequestParam(required=false) String location,
                                 @RequestParam(required=false) String category,
                                 @RequestParam(required=false) Boolean isLost,
                                 @RequestParam(defaultValue="0") int page,
                                 @RequestParam(defaultValue="20") int size) {

        return itemRepo.search(q, location, category, isLost, PageRequest.of(page,size));
    }

    // 🖼️ View Photo
    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> photo(@PathVariable Long id) {
        return itemRepo.findById(id)
            .filter(i -> i.getPhoto() != null)
            .map(i -> ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(i.getPhoto()))
            .orElse(ResponseEntity.notFound().build());
    }

    // 👁️ View Single Item (FIXED)
    @GetMapping("/{id}")
    public ResponseEntity<ItemPost> getById(@PathVariable Long id) {
        return itemRepo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ➕ Report Lost / Found Item
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestParam String title,
                                    @RequestParam(required=false) String description,
                                    @RequestParam(required=false) String location,
                                    @RequestParam(required=false) String dateEvent,
                                    @RequestParam(required=false) String category,
                                    @RequestParam boolean lost,
                                    @RequestParam(required=false) MultipartFile photo,
                                    Authentication authentication) throws Exception {

        String username = (String) authentication.getPrincipal();
        User user = userRepo.findByUsername(username).orElseThrow();

        ItemPost it = new ItemPost();
        it.setTitle(title);
        it.setDescription(description);
        it.setLocation(location);
        if (dateEvent != null && !dateEvent.isBlank())
            it.setDateEvent(LocalDate.parse(dateEvent));
        it.setCategory(category);
        it.setLost(lost);
        if (photo != null && !photo.isEmpty())
            it.setPhoto(photo.getBytes());
        it.setStatus("UNVERIFIED");
        it.setReportedBy(user);

        itemRepo.save(it);
        return ResponseEntity.ok(it);
    }
}
