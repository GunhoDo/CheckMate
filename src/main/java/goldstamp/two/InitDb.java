package goldstamp.two;


import goldstamp.two.domain.*;
import goldstamp.two.repository.DiseaseRepository;
import goldstamp.two.repository.MedicineRepository;
import goldstamp.two.service.PrescriptionService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        private final PrescriptionService prescriptionService;

        private final DiseaseRepository diseaseRepository;

        private final MedicineRepository medicineRepository;


        public void dbInit1() {
            Member member = createMember("Hwang", "Hwangw123", "goodboy", Gender.MAN, LocalDate.parse("2001-03-01"), 180, 70, new CurrentMed(LocalDate.parse("2025-03-01"), LocalDate.parse("2025-03-08")), new NextMed("감기", 7));
            em.persist(member);
            Prescription prescription =  Prescription.createPrescription(member, diseaseRepository.findByName("감기"), medicineRepository.findByEfficientContaining("감기").toArray(new Medicine[0]));
            em.persist(prescription);
        }
        public void dbInit2() {
            Member member = createMember("Lee","leeTiger88","fastpass", Gender.WOMAN, LocalDate.parse("1995-11-12"),165, 58, new CurrentMed(LocalDate.parse("2025-04-10"), LocalDate.parse("2025-04-17")), new NextMed("비염", 5));
            em.persist(member);
            Prescription prescription = Prescription.createPrescription(member, diseaseRepository.findByName("열상"), medicineRepository.findByEfficientContaining("열상").toArray(new Medicine[0]));
            em.persist(prescription);
        }
        private static Member createMember(String name, String loginId, String password, Gender gender, LocalDate birthDay, int height, int weight, CurrentMed currentMed, NextMed nextMed) {
            Member member = new Member();
            member.setName(name);
            member.setLoginId(loginId);
            member.setPassword(password);
            member.setGender(gender);
            member.setBirthDay(birthDay);
            member.setHeight(height);
            member.setWeight(weight);
            member.setCurrentMed(currentMed);
            member.setNextMed(nextMed);
            return member;
        }

    }
}


