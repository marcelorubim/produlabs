package br.com.produlab.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "exams_history_summary")
public class ExamsHistorySummary extends PanacheEntityBase implements Serializable {
    @EmbeddedId
    public ExamHistorySummaryId id;
    @Column(name = "hospitalization_value",nullable = false)
    public Integer hospitalizationValue;
    @Column(name = "ambulatory_value",nullable = false)
    public Integer ambulatoryValue;
    @Column(name = "emergency_value",nullable = false)
    public Integer emergencyValue;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="exam_id",insertable = false, updatable = false)
    public Exam exam;

    public static List<ExamsHistorySummary> findByPeriod(LocalDate period){
        return find("period = ?1",period).list();
    }
}
