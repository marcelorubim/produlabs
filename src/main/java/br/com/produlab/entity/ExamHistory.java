package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exams_history")
public class ExamHistory extends PanacheEntityBase implements Serializable {
    @EmbeddedId
    public ExamHistoryId id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="laboratory_id",insertable = false, updatable = false)
    @JsonbTransient
    public Laboratory laboratory;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="exam_id",insertable = false, updatable = false)
    @JsonbTransient
    public Exam exam;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    @JsonbDateFormat("yyyyMMdd")
    @Column(name="period",insertable = false, updatable = false)
    public LocalDate period;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at",nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_by",nullable = false)
    @JsonbTransient
    public User createdBy;
    @Column(name = "hospitalization_value",nullable = false)
    public Integer hospitalizationValue;
    @Column(name = "ambulatory_value",nullable = false)
    public Integer ambulatoryValue;
    @Column(name = "emergency_value",nullable = false)
    public Integer emergencyValue;
    @Enumerated(EnumType.STRING)
    @Column(name = "state",nullable = false)
    public ExamHistoryState state;

    public static List<ExamHistory> findByPeriod(LocalDate period){
        return find("period = ?1",period).list();
    }

    public static List<ExamHistory> findHistoryByYear(Integer year,ExamHistoryState state){
        LocalDate initialDate = LocalDate.of(year,1,1);
        LocalDate finalDate = LocalDate.of(year,12,31);
        return find("period BETWEEN ?1 AND ?2 AND state = ?3",initialDate,finalDate,state).list();
    }

    public static List<ExamHistory> findLaboratoryHistoryByPeriod(Long laboratoryID,LocalDate period){
        return find("laboratory.id = ?1 AND period = ?2",laboratoryID,period).list();
    }

    public static List<ExamHistory> findLaboratoryHistoryByPeriod(Long laboratoryID,LocalDate period,ExamHistoryState state){
        return find("laboratory.id = ?1 AND period = ?2 AND state =?3",laboratoryID,period,state).list();
    }

    public static List<ExamHistory> findLaboratoryHistoryByYear(Long laboratoryID, Integer year,ExamHistoryState state){
        LocalDate initialDate = LocalDate.of(year,1,1);
        LocalDate finalDate = LocalDate.of(year,12,31);
        return find("laboratory.id = ?1 AND period BETWEEN ?2 AND ?3 AND state = ?4",laboratoryID,initialDate,finalDate,state).list();
    }

    @Transactional
    public static void updateState(Long laboratoryID, LocalDate period,ExamHistoryState state){
        ExamHistory.update("state = ?1 where id.laboratoryID = ?2 and period = ?3",state,laboratoryID,period);
    }

    @Override
    public String toString() {
        return "ExamHistory{" +
                "id=" + id +
                ", laboratory=" + laboratory +
                ", exam=" + exam +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", hospitalizationValue=" + hospitalizationValue +
                ", ambulatoryValue=" + ambulatoryValue +
                ", emergencyValue=" + emergencyValue +
                '}';
    }
}
