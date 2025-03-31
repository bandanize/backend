package com.bandanize.backend.controllers;

import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.CommentModel;
import com.bandanize.backend.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    // Get all comments
    @GetMapping
    public List<CommentModel> getAllComments() {
        return commentRepository.findAll();
    }

    // Get a comment by ID
    @GetMapping("/{id}")
    public CommentModel getCommentById(@PathVariable Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
    }

    // Create a new comment
    @PostMapping
    public CommentModel createComment(@RequestBody CommentModel comment) {
        return commentRepository.save(comment);
    }

    // Update an existing comment
    @PutMapping("/{id}")
    public CommentModel updateComment(@PathVariable Long id, @RequestBody CommentModel commentDetails) {
        CommentModel comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        comment.setUser(commentDetails.getUser());
        comment.setComment(commentDetails.getComment());
        comment.setDateTime(commentDetails.getDateTime());

        return commentRepository.save(comment);
    }

    // Delete a comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        CommentModel comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}