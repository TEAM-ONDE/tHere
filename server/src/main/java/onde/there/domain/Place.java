package onde.there.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Entity
public class Place {
    @Id
    @Column(name = "place_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    private String title;

    private String text;

    private String addressName;

    private String region1;

    private String region2;

    private String region3;

    private String region4;

    private LocalDateTime placeTime;

    @ManyToOne
    @JoinColumn(name = "journey_id")
    private Journey journey;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "place")
//    @ToString.Exclude
//    private List<PlaceCategory> placeCategories = new ArrayList<>();

// @OneToMany(fetch = FetchType.EAGER, mappedBy = "place")
// @ToString.Exclude
// private List<PlaceImg> placeImges = new ArrayList<>();

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "place_Id")
//    @ToString.Exclude
//    private List<PlaceHeart> placeHearts;


    private long placeHeartSum;

}
