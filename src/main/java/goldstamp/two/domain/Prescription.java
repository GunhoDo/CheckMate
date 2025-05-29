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
    private List<PrescriptionMedicine> prescriptionMedicines = new ArrayList<>();

    @Column(length = 1000) // Name 필드 길이도 Disease의 name 필드와 일치시키는 것이 좋습니다.
    private String name;

    @Column(length = 3000) // DESCRIPTION 컬럼 길이 3000으로 수정
    private String description;

    public void setMember(Member member) {
        this.member = member;
        member.getPrescriptions().add(this);
    }
    public void setDisease(Disease disease) {
        this.disease = disease;
        disease.setPrescription(this);
    }
    public void addMedicine(PrescriptionMedicine prescriptionMedicine) {
        prescriptionMedicines.add((prescriptionMedicine));
        prescriptionMedicine.setPrescription(this);
    }

    public static Prescription createPrescription(Member member, Disease disease, PrescriptionMedicine... prescriptionMedicines) {
        Prescription prescription = new Prescription();
        prescription.setMember(member);
        prescription.setDisease(disease);
        for (PrescriptionMedicine prescriptionMedicine : prescriptionMedicines ) {
            prescription.addMedicine(prescriptionMedicine);
        }
        return prescription;
    }
}