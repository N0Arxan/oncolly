package cat.teknos.oncolly.repositories;


import cat.teknos.oncolly.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByPatientId(UUID patientId);

    List<Activity> findAllByPatientIdAndUpdatedAtAfter(UUID patientId, LocalDateTime lastSync);

    @Query("SELECT a FROM Activity a " +
            "JOIN a.patient p " +
            "JOIN DoctorPatient dp ON p.id = dp.patient.id " +
            "WHERE dp.doctor.id = :doctorId AND dp.isDeleted = false")
    List<Activity> findAllByDoctorId(@Param("doctorId") UUID doctorId);

    List<Activity> findByPatientIdAndIsDeletedFalse(UUID id);

}
