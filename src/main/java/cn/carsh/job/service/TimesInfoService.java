package cn.carsh.job.service;

import cn.carsh.job.pojo.TimesInfo;

import java.util.List;

public interface TimesInfoService {
    /**
     *
     * @param item
     */
    public void save(List<TimesInfo> item);
    //
    public boolean findAll(TimesInfo item);
}
