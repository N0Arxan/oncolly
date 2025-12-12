package cat.teknos.oncolly.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends AppUser {

    private String specialization;

    //TODO: can add clinic_name, license_number here later if needed
}
