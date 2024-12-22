package backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // Текст комментария

    @Column(name = "author", nullable = false, columnDefinition = "TEXT")
    private String author; // Автор комментария
}
