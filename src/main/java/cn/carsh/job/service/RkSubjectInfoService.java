package cn.carsh.job.service;


import cn.carsh.job.pojo.RkSubjectInfo;

import java.util.List;

public interface RkSubjectInfoService {
    /**
     *
     * @param item
     */
    public void save(List<RkSubjectInfo> item);
    //
    public boolean findAll(RkSubjectInfo item);
}
