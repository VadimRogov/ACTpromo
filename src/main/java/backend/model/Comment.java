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
    @Lob
    @Column(name = "content", nullable = false)
    private String content; // Текст комментария
    @Lob
    @Column(name = "author", nullable = false)
    private String author; // Автор комментария
}
