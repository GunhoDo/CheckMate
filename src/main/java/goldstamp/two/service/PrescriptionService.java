package goldstamp.two.service;

import goldstamp.two.domain.*;
import goldstamp.two.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final MemberRepositoryClass memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;


    @Transactional
    public Long createEmptyPrescription(Long memberId) {
        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);

        // 2. 빈 처방전 생성 (질병 및 약 없이)
        Prescription prescription = new Prescription();
        prescription.setMember(member);

        // 3. 저장
        prescriptionRepository.save(prescription);

        return prescription.getId();
    }

    @Transactional
    public Long createPrescriptionByDiseaseName(Long memberId, String diseaseName) {

        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);

        // 2. 질병 조회
        Disease disease = diseaseRepository.findByName(diseaseName);

        // 3. 해당 질병 이름이 들어간 약 효능으로 약 조회
        List<Medicine> medicines = medicineRepository.findByEfficientContaining(disease.getName());
        // 4. 질병을 중간 데이터로 변경
        List<PrescriptionMedicine> prescriptionMedicines = medicines.stream()
                .map(medicine -> {
                    PrescriptionMedicine pm = new PrescriptionMedicine();
                    pm.setMedicine(medicine);
                    // 복용량, 복용시간 등 추가 정보가 있다면 여기서 세팅
                    return pm;
                })
                .collect(Collectors.toList());

        // 4. 처방전 생성 (정적 팩토리 메서드 사용)
        Prescription prescription = Prescription.createPrescription(member, disease, prescriptionMedicines.toArray(new PrescriptionMedicine[0]));

        // 5. 저장
        prescriptionRepository.save(prescription);

        return prescription.getId();
    }

    // 진단서 ID로 하나의 진단서 조회 메서드 추가
    public Prescription findOnePrescription(Long prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));
    }

    @Transactional
    public void addDiseaseToPrescription(Long memberId, Long prescriptionId, String diseaseName) {
        // 1. 멤버와 처방전 유효성 검사 (선택 사항, 보안 강화)
        // 실제 애플리케이션에서는 memberId를 통해 해당 멤버가 prescriptionId의 소유자인지 확인하는 로직이 필요합니다.
        // 현재는 memberId를 단순히 경로 변수로 받지만, JWT 토큰 등에서 가져온 인증된 사용자 ID와 비교하는 것이 좋습니다.

        // 2. 처방전 조회
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));

        // 3. 질병 조회
        Disease disease = diseaseRepository.findByName(diseaseName);
        if (disease == null) {
            throw new IllegalArgumentException("Disease not found with name: " + diseaseName);
        }

        // 4. 처방전에 질병 추가 (업데이트)
        prescription.setDisease(disease);

        // 저장 (Transactional 어노테이션으로 인해 자동 더티 체킹되어 별도 save 호출 필요 없음)
    }

    @Transactional
    public void addMedicineToPrescription(Long memberId, Long prescriptionId, String medicineName) {
        // 1. 멤버와 처방전 유효성 검사 (선택 사항, 보안 강화)

        // 2. 처방전 조회
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));

        // 3. 약 조회
        Medicine medicine = medicineRepository.findByMedicineNameContainingIgnoreCase(medicineName)
                .stream()
                .findFirst() // 정확히 일치하는 약을 찾거나, 여러 개 중 첫 번째를 가져옴
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found with name: " + medicineName));

        // 4. PrescriptionMedicine 객체 생성 및 처방전에 추가
        PrescriptionMedicine prescriptionMedicine = new PrescriptionMedicine();
        prescriptionMedicine.setMedicine(medicine);
        prescription.addMedicine(prescriptionMedicine);

        // 저장 (Transactional 어노테이션으로 인해 자동 더티 체킹되어 별도 save 호출 필요 없음)
    }
}
