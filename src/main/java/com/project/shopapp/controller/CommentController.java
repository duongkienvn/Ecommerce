package com.project.shopapp.controller;

import com.project.shopapp.model.dto.CommentDto;
import com.project.shopapp.model.request.CommentUpdateRequest;
import com.project.shopapp.model.response.CommentResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.service.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/comments")
public class CommentController {
    private final ICommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.status(CREATED).body(commentService.addComment(commentDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") Long id, @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(commentService.updateComment(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Delete Comment successfully!");
    }

    @DeleteMapping("/bulk-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteComments(@RequestBody List<Long> ids) {
        commentService.deleteComments(ids);
        return ResponseEntity.ok("Delete Comments successfully!");
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommentsByUserId(@PathVariable("id") Long userId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "5") int limit) {

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentsByUserId(userId, pageRequest);
        List<CommentResponse> commentResponses = commentResponsePage.getContent();
        int totalPages = commentResponsePage.getTotalPages();

        return ResponseEntity.ok(PageResponse.builder().data(commentResponses).totalPages(totalPages).build());
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getCommentsByProductId(@PathVariable("id") Long productId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "5") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentsByProductId(productId, pageRequest);
        List<CommentResponse> commentResponses = commentResponsePage.getContent();
        int totalPages = commentResponsePage.getTotalPages();

        return ResponseEntity.ok(PageResponse.builder().data(commentResponses).totalPages(totalPages).build());
    }
}

