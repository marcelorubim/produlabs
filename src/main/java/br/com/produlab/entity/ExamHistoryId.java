package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ExamHistoryId implements Serializable {
    @Column(name = "laboratory_id")
    public Long laboratoryID;
    @Column(name = "exam_id")
    public Long examID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    @JsonbDateFormat("yyyyMMdd")
    @Column(name="period")
    public LocalDate period;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamHistoryId that = (ExamHistoryId) o;
        return Objects.equals(laboratoryID, that.laboratoryID) &&
                Objects.equals(examID, that.examID) &&
                Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(laboratoryID, examID, period);
    }
}
