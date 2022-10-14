package onde.there.domain;

import javax.persistence.*;

@Entity
public class PlaceImage {
    @Id
    @Column(name = "place_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    private String url;
}
