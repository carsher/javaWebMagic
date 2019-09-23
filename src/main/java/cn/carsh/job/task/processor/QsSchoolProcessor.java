package cn.carsh.job.task.processor;

import cn.carsh.job.pojo.QsSchoolInfo;
import cn.carsh.job.task.QsSchoolDataPipeline;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crash
 * @version 2019/9/23
 * QS：2-按學科：
 */
@Component
public class QsSchoolProcessor implements PageProcessor {
    //URL匹配规则
    private static final String URLRULE = "https://www.topuniversities\\.com/sites/default/files/qs-rankings-data/.*";
    private static final String URL = "https://www.topuniversities.com/sites/default/files/qs-rankings-data/397863.txt?_=1568899999849";
    private Site site = Site.me()
            .setCharset("utf8")
            .setTimeOut(10000)
            .setRetrySleepTime(3000)
            .setRetryTimes(3);
    @Override
    public void process(Page page) {
        List<QsSchoolInfo> list = new ArrayList<>();
        if (page.getUrl().regex(URLRULE).match()) {
            //通过jsonpath得到json数据中的id内容，之后再拼凑待爬取链接
            List<String> endUrls = new JsonPathSelector("$.data[*]").selectList(page.getRawText());
            System.out.println(endUrls);
            if (CollectionUtils.isNotEmpty(endUrls)) {
                for (String endUrl : endUrls) {
                    QsSchoolInfo qsSchoolInfo = new QsSchoolInfo();
                    JSONObject jsonObject = JSON.parseObject(endUrl);
                    String title = jsonObject.get("title").toString();
                    String rank_display = jsonObject.get("rank_display").toString();
                    qsSchoolInfo.setSchool(title);
                    qsSchoolInfo.setRanking(rank_display);
                    list.add(qsSchoolInfo);
                }
                page.putField("qsSchool",list);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
    /**
     *
     */
    @Autowired
    private QsSchoolDataPipeline springDataPipeline;

    //new一个Spider对象传入对应的url和对应的DataPipeline获取web信息并存贮
 //   @Scheduled(initialDelay = 1000,fixedDelay = 10000)
    public void process(){
        Spider.create(new QsSchoolProcessor())
                .addUrl(URL)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(2000)))
                .thread(5)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
