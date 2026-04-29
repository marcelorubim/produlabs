package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "patients_history")
public class PatientsHistory extends PanacheEntityBase {
    @EmbeddedId
    public PatientHistoryId id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="laboratory_id",insertable = false, updatable = false)
    public Laboratory laboratory;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    @JsonbDateFormat("yyyyMMdd")
    @Column(name="period",insertable = false, updatable = false)
    public LocalDate period;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at",nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_by",nullable = false)
    public User createdBy;
    @Column(name = "patients_value",nullable = false)
    public Integer patientsValue;
    @Enumerated(EnumType.STRING)
    @Column(name = "state",nullable = false)
    public ExamHistoryState state;
    @OneToMany
    @JoinColumns({
            @JoinColumn(name = "laboratory_id",referencedColumnName = "laboratory_id"),
            @JoinColumn(name = "period",referencedColumnName = "period")
    })
    public List<HistoryState> historyState;

    public static List<PatientsHistory> findByPeriod(LocalDate period,ExamHistoryState state){
        return find("period = ?1 AND state = ?2",period,state).list();
    }

    public static PatientsHistory findLaboratoryHistoryByPeriod(Long laboratoryID, LocalDate period) {
        return find("laboratory.id = ?1 AND period = ?2",laboratoryID,period).firstResult();
    }

    public static List<PatientsHistory> findLaboratoryHistoryByPeriod(Long laboratoryID, Integer year,ExamHistoryState state) {

        LocalDate initialDate = LocalDate.of(year,1,1);
        LocalDate finalDate = LocalDate.of(year,12,31);
        return find("laboratory.id = ?1 AND period BETWEEN ?2 AND ?3 AND state = ?4",laboratoryID,initialDate,finalDate,state).list();
    }
    @Transactional
    public static void updateState(Long laboratoryID, LocalDate period,ExamHistoryState state){
        PatientsHistory.update("state = ?1 where id.laboratoryID = ?2 and period = ?3",state,laboratoryID,period);
    }

    public static List<PatientsHistory> findByYear(Integer year, ExamHistoryState state) {
        LocalDate initialDate = LocalDate.of(year,1,1);
        LocalDate finalDate = LocalDate.of(year,12,31);
        return find("period BETWEEN ?1 AND ?2 AND state = ?3",initialDate,finalDate,state).list();
    }
}
