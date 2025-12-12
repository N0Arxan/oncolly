package cat.teknos.oncolly.utils;

import cat.teknos.oncolly.dtos.activity.ActivityResponse;
import cat.teknos.oncolly.dtos.appointment.AppointmentResponse;
import cat.teknos.oncolly.dtos.patient.PatientResponse;
import cat.teknos.oncolly.models.Activity;
import cat.teknos.oncolly.models.Appointment;
import cat.teknos.oncolly.models.Patient;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    // Convert Patient Entity -> PatientResponse DTO
    public PatientResponse toPatientResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                patient.getDateOfBirth()
        );
    }

    // Convert Activity Entity -> ActivityResponse DTO
    public ActivityResponse toActivityResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getPatient().getId(),
                activity.getActivityType(),
                activity.getValue(),
                activity.getOccurredAt(),
                activity.getCreatedAt()
        );
    }

    // Convert Appointment Entity -> AppointmentResponse DTO
    public AppointmentResponse toAppointmentResponse(Appointment appt) {
        return new AppointmentResponse(
                appt.getId(),
                appt.getDoctor().getId(),
                appt.getDoctor().getEmail(), // Or add a Name field to Doctor later
                appt.getPatient().getId(),
                appt.getPatient().getEmail(), // Or add Name field
                appt.getStartTime(),
                appt.getEndTime(),
                appt.getStatus(),
                appt.getTitle() // Ensure 'title' exists in your Appointment Entity
        );
    }
}