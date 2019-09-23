package cn.carsh.job.pojo;

import javax.persistence.*;

/**
 * @author crash
 * @version 2019/9/23
 * 1.软科-按学校
 * 2.表rk_school
 */
@Entity
@Table(name = "rk_school")
public class RkSchoolInfo {
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
