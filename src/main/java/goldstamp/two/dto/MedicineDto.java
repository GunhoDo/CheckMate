package goldstamp.two.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDto {
    private String medicineName;

    private String efficient;

    private String useMethod;

    private String acquire;

    private String warning;
}
