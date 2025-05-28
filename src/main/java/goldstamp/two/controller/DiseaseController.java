package goldstamp.two.controller;

import goldstamp.two.domain.Disease;
import goldstamp.two.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @GetMapping
    public ResponseEntity<List<String>> autocompleteDiseases(@RequestParam("query") String query) {
        List<Disease> diseases = diseaseService.searchDiseases(query);
        // 질병 객체 리스트에서 이름(name)만 추출하여 반환
        List<String> diseaseNames = diseases.stream()
                .map(Disease::getName)
                .toList();
        return ResponseEntity.ok(diseaseNames);
    }
}
