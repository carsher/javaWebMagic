package cn.carsh.job.service.impl;

import cn.carsh.job.dao.UsNewsInfoDao;
import cn.carsh.job.pojo.UsNewsInfo;
import cn.carsh.job.service.UsNewsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UsNewsInfoServiceImpl implements UsNewsInfoService {
    @Autowired
    private UsNewsInfoDao subjectInfoDao;


    @Override
    @Transactional
    public void save(List<UsNewsInfo> item) {
        UsNewsInfo param = new UsNewsInfo();
        param.setSubject(item.get(0).getSubject());
        param.setSchool(item.get(0).getSchool());
        boolean bool = this.findAll(param);
        //保持数据到数据库表中
        if (!bool){
            try {
                this.subjectInfoDao.saveAll(item);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean findAll(UsNewsInfo item) {
        //查询条件
        Example<UsNewsInfo> example = Example.of(item);
        List list = this.subjectInfoDao.findAll(example);
        if (list.size()>0){
            return true;
        }
        return false;
    }
}
