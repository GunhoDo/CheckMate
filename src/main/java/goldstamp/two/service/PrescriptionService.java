package goldstamp.two.service;

import goldstamp.two.domain.Disease;
import goldstamp.two.domain.Medicine;
import goldstamp.two.domain.Member;
import goldstamp.two.domain.Prescription;
import goldstamp.two.repository.DiseaseRepository;
import goldstamp.two.repository.MedicineRepository;
import goldstamp.two.repository.MemberRepository;
import goldstamp.two.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public Long createPrescriptionByDiseaseName(Long memberId, String diseaseName) {

        // 1. 멤버 조회
        Member member = memberRepository.findOne(memberId);

        // 2. 질병 조회
        Disease disease = diseaseRepository.findByName(diseaseName);

        // 3. 해당 질병 이름이 들어간 약 효능으로 약 조회
        List<Medicine> medicines = medicineRepository.findByEfficientContaining(disease.getName());

        // 4. 처방전 생성 (정적 팩토리 메서드 사용)
        Prescription prescription = Prescription.createPrescription(member, disease, medicines.toArray(new Medicine[0]));

        // 5. 저장
        prescriptionRepository.save(prescription);

        return prescription.getId();
    }
}
