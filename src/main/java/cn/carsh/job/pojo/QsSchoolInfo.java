package cn.carsh.job.pojo;

import javax.persistence.*;
/**
 * @author crash
 * @version 2019/9/23
 * 1.QS:-按專業
 * 2.表qs_school
 */
@Entity
@Table(name = "qs_school")
public class QsSchoolInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ranking;
    private String school;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
