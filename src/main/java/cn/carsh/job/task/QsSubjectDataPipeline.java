package cn.carsh.job.task;

import cn.carsh.job.pojo.QsSchoolInfo;
import cn.carsh.job.pojo.QsSubjectInfo;
import cn.carsh.job.service.QsSchoolInfoService;
import cn.carsh.job.service.QsSubjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
public class QsSubjectDataPipeline implements Pipeline {
    @Autowired
    private QsSubjectInfoService jObInfoService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取信息
        List<QsSubjectInfo> qsSubject = resultItems.get("qsSubject");
        if (qsSubject!=null){
            this.jObInfoService.save(qsSubject);
        }
    }
}
