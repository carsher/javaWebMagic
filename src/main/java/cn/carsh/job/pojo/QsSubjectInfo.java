package cn.carsh.job.pojo;

import javax.persistence.*;

/**
 * @author crash
 * @version 2019/9/23
 * 1.QS：-按學校
 * 2.表qs_subject
 */
@Entity
@Table(name = "qs_subject")
public class QsSubjectInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String ranking;
    private String school;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
