package cn.carsh.job.task;
import cn.carsh.job.pojo.RkSchoolInfo;
import cn.carsh.job.service.RkSchoolInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
@Component
public class RkSchoolDataPipeline implements Pipeline {
    @Autowired
    private RkSchoolInfoService newsUsInfoService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取信息
        List<RkSchoolInfo> rkschool = resultItems.get("rkschool");
        if (rkschool!=null){
            this.newsUsInfoService.save(rkschool);
        }
    }
}
