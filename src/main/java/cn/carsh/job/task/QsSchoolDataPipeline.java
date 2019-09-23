package cn.carsh.job.task;

import cn.carsh.job.pojo.QsSchoolInfo;
import cn.carsh.job.service.QsSchoolInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
public class QsSchoolDataPipeline implements Pipeline {
    @Autowired
    private QsSchoolInfoService jObInfoService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取信息
        List<QsSchoolInfo> qsSchool = resultItems.get("qsSchool");
        if (qsSchool!=null){
            this.jObInfoService.save(qsSchool);
        }
    }
}
