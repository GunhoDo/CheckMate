package goldstamp.two.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {
    private String medicineName;

    private String efficient;

    private String useMethod;

    private String acquire;

    private String warning;
}
