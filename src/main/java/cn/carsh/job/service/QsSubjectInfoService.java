package cn.carsh.job.service;

import cn.carsh.job.pojo.QsSubjectInfo;

import java.util.List;

public interface QsSubjectInfoService {
    /**
     *
     * @param item
     */
    public void save(List<QsSubjectInfo> item);
    //
    public boolean findAll(QsSubjectInfo item);
}
