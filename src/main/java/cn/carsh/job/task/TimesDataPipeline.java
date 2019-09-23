package cn.carsh.job.task;

import cn.carsh.job.pojo.TimesInfo;
import cn.carsh.job.service.TimesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
public class TimesDataPipeline implements Pipeline {
    @Autowired
    private TimesInfoService newsUsInfoService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取信息
        List<TimesInfo> times = resultItems.get("times");
        if (times!=null){
            this.newsUsInfoService.save(times);
        }
    }
}
