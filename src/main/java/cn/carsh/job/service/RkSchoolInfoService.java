package cn.carsh.job.service;

import cn.carsh.job.pojo.RkSchoolInfo;

import java.util.List;

public interface RkSchoolInfoService {
    /**
     *
     * @param item
     */
    public void save(List<RkSchoolInfo> item);
    //
    public boolean findAll(RkSchoolInfo item);
}
