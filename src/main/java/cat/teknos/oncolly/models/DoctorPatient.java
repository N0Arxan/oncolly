package cat.teknos.oncolly.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import cat.teknos.oncolly.models.keys.DoctorPatientKey;

@Entity
@Table(name = "doctor_patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPatient {

    @EmbeddedId
    private DoctorPatientKey id;

    @ManyToOne
    @MapsId("doctorId") // Maps the "doctorId" inside the Key to this Entity
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @MapsId("patientId") // Maps the "patientId" inside the Key to this Entity
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;
}
