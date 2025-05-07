package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id")
    private long id;

    @Column(unique = true, nullable = false)
    private String loginId;

    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Prescription> prescriptions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDay;

    private double height;

    private double weight;

    @Embedded
    private CurrentMed currentMed;

    @Embedded
    private NextMed nextMed;

}
