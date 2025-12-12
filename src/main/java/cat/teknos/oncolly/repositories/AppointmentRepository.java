package cat.teknos.oncolly.repositories;

import cat.teknos.oncolly.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByPatientId(UUID patientId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.isDeleted = false " +
            "AND a.status != 'CANCELLED' " +
            "AND (" +
            "   (a.startTime < :newEnd AND a.endTime > :newStart)" +
            ")")
    boolean existsByDoctorIdAndTimeOverlap(
            @Param("doctorId") UUID doctorId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );
}