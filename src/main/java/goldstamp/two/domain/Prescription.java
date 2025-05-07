package goldstamp.two.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "prescriptions")
public class Prescription {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "prescription_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "disease_id")
    private Disease disease;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<Medicine> medicines = new ArrayList<>();

    private String name;

    private String description;
}
