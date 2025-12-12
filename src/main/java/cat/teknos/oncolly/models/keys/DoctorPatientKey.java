package cat.teknos.oncolly.models.keys;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPatientKey implements Serializable {
    private UUID doctorId;
    private UUID patientId;
}
