package cn.carsh.job.service;


import cn.carsh.job.pojo.UsNewsInfo;

import java.util.List;

public interface UsNewsInfoService {
    /**
     *
     * @param item
     */
    public void save(List<UsNewsInfo> item);
    //
    public boolean findAll(UsNewsInfo item);
}
