package goldstamp.two.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class NextMed {

    private String nextDisease;

    private int duringDay;
}
