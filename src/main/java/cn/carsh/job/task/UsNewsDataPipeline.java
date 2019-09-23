package cn.carsh.job.task;

import cn.carsh.job.pojo.UsNewsInfo;
import cn.carsh.job.service.UsNewsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;


@Component
public class UsNewsDataPipeline implements Pipeline {
    @Autowired
    private UsNewsInfoService newsUsInfoService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取信息
        List<UsNewsInfo> usnews = resultItems.get("usnews");
        if (usnews!=null){
            this.newsUsInfoService.save(usnews);
        }
    }
}
