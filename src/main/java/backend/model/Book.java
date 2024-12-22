package backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Column(name = "title", nullable = false)
    private String title;
    @Lob
    @Column(name = "author", nullable = false)
    private String author;
    @Lob
    @Column(name = "description", nullable = false)
    private String description;
    @Lob
    @Column(name = "url", nullable = false)
    private String url;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images;
}
