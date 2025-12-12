package cat.teknos.oncolly.repositories;

import cat.teknos.oncolly.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}