package cn.carsh.job.service.impl;

import cn.carsh.job.dao.TimesInfoDao;
import cn.carsh.job.pojo.QsSubjectInfo;
import cn.carsh.job.pojo.TimesInfo;
import cn.carsh.job.service.TimesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TimesnInfoServiceImpl implements TimesInfoService {
    @Autowired
    private TimesInfoDao timesInfoDao;


    @Override
    @Transactional
    public void save(List<TimesInfo> item) {
        TimesInfo param = new TimesInfo();
        param.setSubject(item.get(0).getSubject());
        param.setRanking(item.get(0).getRanking());
        param.setSchool(item.get(0).getSchool());
        boolean bool = this.findAll(param);
        //保持数据到数据库表中
        if (bool){
            try {
                this.timesInfoDao.saveAll(item);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean findAll(TimesInfo item) {
        //查询条件
        Example<TimesInfo> example = Example.of(item);
        List list = this.timesInfoDao.findAll(example);
        if (list.size()==0){
            return true;
        }
        return false;
    }
}
