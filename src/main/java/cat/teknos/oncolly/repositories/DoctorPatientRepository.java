package cat.teknos.oncolly.repositories;

import cat.teknos.oncolly.models.DoctorPatient;
import cat.teknos.oncolly.models.keys.DoctorPatientKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DoctorPatientRepository extends JpaRepository<DoctorPatient, DoctorPatientKey> {

    List<DoctorPatient> findByDoctorIdAndIsDeletedFalse(UUID doctorId);

    List<DoctorPatient> findByPatientIdAndIsDeletedFalse(UUID patientId);

    boolean existsByDoctorIdAndPatientIdAndIsDeletedFalse(UUID doctorId, UUID patientId);
}