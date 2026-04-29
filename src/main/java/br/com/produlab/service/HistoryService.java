package br.com.produlab.service;

import br.com.produlab.entity.*;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.Optional;

@RequestScoped
public class HistoryService {
    @Transactional
    public ExamHistory saveExamHistory(ExamHistory examHistory, User currentUser) {
        ExamHistory examHistoryDB = ExamHistory.findById(examHistory.id);
        if (examHistoryDB != null) {
            merge(examHistoryDB, examHistory);
            return examHistoryDB;
        } else {
            examHistory.createdBy = currentUser;
            examHistory.state = ExamHistoryState.DRAFT;
            examHistory.persist();
            return examHistory;
        }

    }

    private void merge(ExamHistory examHistoryDB, ExamHistory examHistory) {
        if (ExamHistoryState.SENT.equals(examHistoryDB.state)) {
            throw new NotAuthorizedException("ExamHistory is at sent state");
        }
        examHistoryDB.ambulatoryValue = examHistory.ambulatoryValue;
        examHistoryDB.hospitalizationValue = examHistory.hospitalizationValue;
        examHistoryDB.emergencyValue = examHistory.emergencyValue;
        examHistoryDB.state = ExamHistoryState.DRAFT;
        examHistoryDB.persist();
    }

    @Transactional
    public void savePatientHistory(PatientsHistory patientsHistory, User currentUser) {
        if (patientsHistory.period.isAfter(LocalDate.now())) {
            throw new InvalidParameterException();
        }
        if (patientsHistory.id != null && PatientsHistory.findByIdOptional(patientsHistory.id).isPresent()) {
            PatientsHistory patientsHistoryDB = PatientsHistory.findById(patientsHistory.id);
            patientsHistoryDB.patientsValue = patientsHistory.patientsValue;
            patientsHistoryDB.persist();
        } else {
            patientsHistory.id = new PatientHistoryId();
            patientsHistory.id.period = patientsHistory.period;
            patientsHistory.id.laboratoryID = patientsHistory.laboratory.id;
            patientsHistory.createdBy = currentUser;
            patientsHistory.state = ExamHistoryState.DRAFT;
            patientsHistory.persist();
        }
    }

    @Transactional
    public void updateState(Long laboratoryID, LocalDate period, ExamHistoryState state) {
        PatientsHistory.findByIdOptional(new PatientHistoryId(laboratoryID,period)).ifPresentOrElse(p -> {
            if(((PatientsHistory) p).patientsValue == 0) throw new BadRequestException();
        },() -> {
            throw new BadRequestException();
        });

        ExamHistory.updateState(laboratoryID, period, state);
        PatientsHistory.updateState(laboratoryID, period, state);
    }
}
