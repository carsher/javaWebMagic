package cn.carsh.job.service;

import cn.carsh.job.pojo.QsSchoolInfo;

import java.util.List;

public interface QsSchoolInfoService {
    /**
     *
     * @param item
     */
    public void save(List<QsSchoolInfo> item);
    //
    public boolean findAll(QsSchoolInfo item);
}
