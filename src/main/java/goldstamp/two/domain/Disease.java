package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Disease {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "disease_id")
    private long id;

    @JsonIgnore
    @OneToOne(mappedBy = "disease", fetch = FetchType.LAZY)
    private Prescription prescription;

    @Column(length = 1000)
    private String name;

    @Column(length = 3000)
    private String explain;
}
