package br.com.produlab.service;

import br.com.produlab.entity.Exam;
import br.com.produlab.entity.Sector;
import br.com.produlab.entity.User;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RequestScoped
public class ExamService {
    @Transactional
    public Exam addExam(Exam exam) {
        if(exam.id !=null){
            Exam examDB = Exam.findById(exam.id);
            examDB.name = exam.name;
            examDB.sector = Sector.findById(exam.sector.id);
            examDB.codeSUS = exam.codeSUS;
            examDB.persist();
            return examDB;
        }else {
            exam.persist();
            return exam;
        }


    }

    @Transactional
    public void deleteExam(Exam exam, User currentUser) {
        Exam examDB = Exam.findById(exam.id);
        examDB.deletedAt = LocalDateTime.now();
        examDB.deletedBy = currentUser;
        examDB.persist();
    }
}
