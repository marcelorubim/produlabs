package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_state")
public class HistoryState extends PanacheEntityBase {
    @EmbeddedId
    public HistoryStateId id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="laboratory_id",updatable = false,insertable = false)
    public Laboratory laboratory;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    @JsonbDateFormat("yyyyMMdd")
    @Column(name="period",updatable = false,insertable = false)
    public LocalDate period;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at",nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_by",nullable = false)
    public User createdBy;
    @Enumerated(EnumType.STRING)
    @Column(name = "state",nullable = false)
    public ExamHistoryState state;
}
