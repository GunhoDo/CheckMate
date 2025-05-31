// front + back/back/src/main/java/goldstamp/two/controller/PrescriptionController.java
package goldstamp.two.controller;

import goldstamp.two.domain.Prescription; // Prescription 도메인 추가
import goldstamp.two.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // List 임포트 추가

@RestController
@RequestMapping("/members/{memberId}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Long> createPrescription(
            @PathVariable Long memberId,
            @RequestParam String diseaseName
    ) {
        Long prescriptionId = prescriptionService.createPrescriptionByDiseaseName(memberId, diseaseName);
        return ResponseEntity.ok(prescriptionId);
    }

    @PostMapping("/empty") // 새로운 엔드포인트 추가
    public ResponseEntity<Long> createEmptyPrescription(@PathVariable Long memberId) { // 반환 타입을 Long으로 변경
        Long prescriptionId = prescriptionService.createEmptyPrescription(memberId);
        return ResponseEntity.ok(prescriptionId); // prescriptionId만 반환
    }

    // 진단서 수정 페이지를 위한 GET 엔드포인트 (진단서 상세 정보 반환)
    @GetMapping("/{prescriptionId}/edit")
    public ResponseEntity<Prescription> getPrescriptionDetails( // 반환 타입을 String에서 Prescription으로 변경
                                                                @PathVariable Long memberId,
                                                                @PathVariable Long prescriptionId
    ) {
        // memberId를 사용하여 해당 멤버가 prescriptionId의 소유자인지 확인하는 로직 (보안 강화)
        // 현재는 memberId를 단순히 경로 변수로 받지만, JWT 토큰 등에서 가져온 인증된 사용자 ID와 비교하는 것이 좋습니다.

        // PrescriptionService를 통해 진단서 상세 정보 조회
        Prescription prescription = prescriptionService.findOnePrescription(prescriptionId);

        // 조회된 진단서 정보 반환
        return ResponseEntity.ok(prescription);
    }

    // Member ID로 모든 처방전 조회 (새로 추가된 엔드포인트)
    @GetMapping
    public ResponseEntity<List<Prescription>> getPrescriptionsByMemberId(@PathVariable Long memberId) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByMemberId(memberId);
        return ResponseEntity.ok(prescriptions);
    }

    @PatchMapping("/{prescriptionId}/disease")
    public ResponseEntity<Void> addDiseaseToPrescription(
            @PathVariable Long memberId,
            @PathVariable Long prescriptionId,
            @RequestParam String diseaseName
    ) {
        prescriptionService.addDiseaseToPrescription(memberId, prescriptionId, diseaseName);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{prescriptionId}/medicine")
    public ResponseEntity<Void> addMedicineToPrescription(
            @PathVariable Long memberId,
            @PathVariable Long prescriptionId,
            @RequestParam String medicineName
    ) {
        prescriptionService.addMedicineToPrescription(memberId, prescriptionId, medicineName);
        return ResponseEntity.ok().build();
    }
}
