package cn.carsh.job.task;

import cn.carsh.job.pojo.RkSubjectInfo;
import cn.carsh.job.service.RkSubjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;


@Component
public class RkSubjectDataPipeline implements Pipeline {
    @Autowired
    private RkSubjectInfoService newsUsInfoService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取信息
        List<RkSubjectInfo> rksubject = resultItems.get("rksubject");
        if (rksubject!=null){
            this.newsUsInfoService.save(rksubject);
        }
    }
}
