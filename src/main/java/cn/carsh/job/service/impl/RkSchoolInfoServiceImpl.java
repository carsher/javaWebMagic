package cn.carsh.job.service.impl;

import cn.carsh.job.dao.RkSchoolInfoDao;
import cn.carsh.job.pojo.RkSchoolInfo;
import cn.carsh.job.service.RkSchoolInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RkSchoolInfoServiceImpl implements RkSchoolInfoService {
    @Autowired
    private RkSchoolInfoDao jobInfoDao;

    @Override
    @Transactional
    public void save( List<RkSchoolInfo> jobInfo) {
        RkSchoolInfo param = new RkSchoolInfo();
        param.setSchool(jobInfo.get(0).getSchool());
        param.setRanking(jobInfo.get(0).getRanking());
        boolean bool = this.findAll(param);
        //保持数据到数据库表中
        if (!bool){
            try {
                this.jobInfoDao.saveAll(jobInfo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean findAll(RkSchoolInfo item) {
        //查询条件
        Example<RkSchoolInfo> example = Example.of(item);
        List list = this.jobInfoDao.findAll(example);
        if (list.size()>0){
            return true;
        }
        return false;
    }
}
