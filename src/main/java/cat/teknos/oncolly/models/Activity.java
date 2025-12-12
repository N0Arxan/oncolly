package cat.teknos.oncolly.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "activity_type")
    private String activityType;

    private String value;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}
