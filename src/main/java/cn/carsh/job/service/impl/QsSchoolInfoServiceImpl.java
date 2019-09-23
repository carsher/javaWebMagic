package cn.carsh.job.service.impl;

import cn.carsh.job.dao.QsSchoolInfoDao;
import cn.carsh.job.pojo.QsSchoolInfo;
import cn.carsh.job.service.QsSchoolInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QsSchoolInfoServiceImpl implements QsSchoolInfoService {
    @Autowired
    private QsSchoolInfoDao jobInfoDao;

    @Override
    @Transactional
    public void save(List<QsSchoolInfo> item) {
        QsSchoolInfo param = new QsSchoolInfo();
        param.setRanking(item.get(0).getRanking());
        param.setSchool(item.get(0).getSchool());
        boolean bool = this.findAll(param);
        //保持数据到数据库表中
        if (bool){
            try {
                this.jobInfoDao.saveAll(item);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean findAll(QsSchoolInfo item) {
         //查询条件
        Example<QsSchoolInfo> example = Example.of(item);
        List list = this.jobInfoDao.findAll(example);
        if (list.size()==0){
            return true;
        }
        return false;
    }
}
