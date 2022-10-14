package onde.there.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "journey_theme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JourneyTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journey_theme_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "journey_id")
    private Journey journey;

    @Column(name = "journey_theme_name")
    private String journeyThemeName;

}
