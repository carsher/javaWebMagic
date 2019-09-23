package cn.carsh.job.service.impl;

import cn.carsh.job.dao.QsSubjectInfoDao;
import cn.carsh.job.pojo.QsSubjectInfo;
import cn.carsh.job.service.QsSubjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QsSubjectInfoServiceImpl implements QsSubjectInfoService {
    @Autowired
    private QsSubjectInfoDao jobInfoDao;

    @Override
    @Transactional
    public void save(List<QsSubjectInfo> item) {
        QsSubjectInfo param = new QsSubjectInfo();
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
    public boolean findAll(QsSubjectInfo item) {
         //查询条件
        Example<QsSubjectInfo> example = Example.of(item);
        List list = this.jobInfoDao.findAll(example);
        if (list.size()==0){
            return true;
        }
        return false;
    }
}
