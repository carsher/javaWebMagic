package cn.carsh.job;

import cn.carsh.job.task.RkSchoolDataPipeline;
import cn.carsh.job.task.processor.Rk_SubjectProcessor;
import cn.carsh.job.task.processor.Rk_schoolProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

@Component
@Order(3)
public class ApplicationStartup implements ApplicationRunner {
    @Autowired
    private RkSchoolDataPipeline springDataPipeline;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("项目又启动了，这次使用的是：继承 ApplicationRunner");
//       Spider.create(new Rk_SubjectProcessor())
//                .addUrl("http://www.zuihaodaxue.com/arwu_subject_rankings.html")
//                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(40000)))
//                .thread(15)
//                .addPipeline(this.springDataPipeline)
//                .run();
    }
}
