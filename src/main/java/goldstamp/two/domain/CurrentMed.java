package goldstamp.two.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class CurrentMed {

    private LocalDate startDate;

    private LocalDate endDate;
}
