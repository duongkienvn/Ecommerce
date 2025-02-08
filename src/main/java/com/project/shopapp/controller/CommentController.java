package com.project.shopapp.controller;

import com.project.shopapp.model.dto.CommentDto;
import com.project.shopapp.model.request.CommentUpdateRequest;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.model.response.CommentResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.service.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/comments")
public class CommentController {
    private final ICommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse> addComment(@Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value(),
                "Add comment successfully!",
                commentService.addComment(commentDto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updateComment(@PathVariable("id") Long id, @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(
                new ApiResponse(OK.value(), "Update comment successfully!",
                        commentService.updateComment(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(new ApiResponse(OK.value(), "Delete Comment successfully!"));
    }

    @DeleteMapping("/bulk-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComments(@RequestBody List<Long> ids) {
        commentService.deleteComments(ids);
        return ResponseEntity.ok(new ApiResponse(OK.value(), "Delete Comments successfully!"));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommentsByUserId(@PathVariable("id") Long userId, Pageable pageable) {
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentsByUserId(userId, pageable);
        List<CommentResponse> commentResponses = commentResponsePage.getContent();
        int totalPages = commentResponsePage.getTotalPages();

        return ResponseEntity.ok(new ApiResponse(OK.value(), "Get all comments by user's id successfully!",
                PageResponse.builder()
                        .data(commentResponses)
                        .totalPages(totalPages).build()));
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getCommentsByProductId(@PathVariable("id") Long productId, Pageable pageable) {
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentsByProductId(productId, pageable);
        List<CommentResponse> commentResponses = commentResponsePage.getContent();
        int totalPages = commentResponsePage.getTotalPages();

        return ResponseEntity.ok(new ApiResponse(OK.value(), "Get all comments by product's id successfully!",
                PageResponse.builder()
                        .data(commentResponses)
                        .totalPages(totalPages)
                        .build()));
    }
}

