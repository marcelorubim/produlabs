package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, unique = true)
    public String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_by")
    public User createdBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="sector_id")
    public Sector sector;
    @Column(name="deleted_at")
    public LocalDateTime deletedAt;
    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="deleted_by")
    public User deletedBy;
    @Column(name="code_sus")
    public String codeSUS;

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", sector=" + sector +
                ", deletedAt=" + deletedAt +
                ", deletedBy=" + deletedBy +
                '}';
    }
}
