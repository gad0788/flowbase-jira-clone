package com.flowbase.jira.service.impl;

import com.flowbase.jira.dto.request.CommentRequest;
import com.flowbase.jira.dto.response.CommentResponse;
import com.flowbase.jira.dto.response.UserResponse;
import com.flowbase.jira.model.Comment;
import com.flowbase.jira.repository.CommentRepository;
import com.flowbase.jira.repository.IssueRepository;
import com.flowbase.jira.repository.UserRepository;
import com.flowbase.jira.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              IssueRepository issueRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> findByIssueId(Long issueId) {
        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId)
                .stream().map(this::enrichCommentResponse).toList();
    }

    @Override
    public CommentResponse create(Long issueId, CommentRequest request) {
        if (!issueRepository.existsById(issueId)) {
            throw new EntityNotFoundException("Issue not found: " + issueId);
        }
        if (!userRepository.existsById(request.getAuthorId())) {
            throw new EntityNotFoundException("User not found: " + request.getAuthorId());
        }
        Comment comment = new Comment(request.getBody(), request.getAuthorId(), issueId);
        return enrichCommentResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse update(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + commentId));
        comment.setBody(request.getBody());
        return enrichCommentResponse(commentRepository.save(comment));
    }

    @Override
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment not found: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    private CommentResponse enrichCommentResponse(Comment comment) {
        CommentResponse r = CommentResponse.from(comment);
        userRepository.findById(comment.getAuthorId()).ifPresent(u ->
                r.setAuthor(UserResponse.from(u)));
        return r;
    }
}
