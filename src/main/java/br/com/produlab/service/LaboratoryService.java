package br.com.produlab.service;

import br.com.produlab.entity.*;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.time.LocalDate;

@RequestScoped
public class LaboratoryService {

    @Transactional
    public void addLaboratory(Laboratory laboratory, User currentUser){
        validateLaboratory(laboratory);
        laboratory.createdBy = currentUser;
        laboratory.persist();
    }

    private void validateLaboratory(Laboratory laboratory){

    }
    @Transactional
    public void addExamHistory(Laboratory laboratory, Exam exam, LocalDate periodDate, User currentUser, ExamHistory examHistory) {
        examHistory.id = new ExamHistoryId();
        examHistory.id.examID = exam.id;
        examHistory.id.laboratoryID = laboratory.id;
        examHistory.id.period = periodDate;
        examHistory.createdBy = currentUser;
        examHistory.persist();
    }
}
