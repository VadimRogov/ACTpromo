package backend.service;

import backend.model.Comment;
import backend.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentService {

    private final CommentRepository commentRepository;

    public ComentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).get();
    }

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }
}
