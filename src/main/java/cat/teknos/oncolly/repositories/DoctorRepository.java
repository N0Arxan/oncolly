package cat.teknos.oncolly.repositories;

import cat.teknos.oncolly.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
}