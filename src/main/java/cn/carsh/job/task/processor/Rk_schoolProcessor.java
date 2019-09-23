package cn.carsh.job.task.processor;

import cn.carsh.job.pojo.RkSchoolInfo;
import cn.carsh.job.task.RkSchoolDataPipeline;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crash
 * @version 2019/9/23
 * 軟科：1-按學校：
 */
@Component
public class Rk_schoolProcessor implements PageProcessor {
    private static final String URL = "http://www.zuihaodaxue.com/ARWU2018.html";
    private Site site = Site.me()
            .setCharset("gbk")
            .setTimeOut(10000)
            .setRetrySleepTime(3000)
            .setRetryTimes(3);
    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("div.news-text tr").nodes();
        if (list.size()!=0){
            //详情页
            this.saveInfo(page,list);
        }
    }

    //解析页面获取信息
    private void saveInfo(Page page,List<Selectable> list) {
        List<RkSchoolInfo> root = new ArrayList<>();
        for (int i=1;i<list.size();i++){
            RkSchoolInfo info = new RkSchoolInfo();
            Selectable htmlSel = list.get(i);
            String rank = Jsoup.parse(htmlSel.css("td").nodes().get(0).toString()).text();
            String school = htmlSel.css("td.align-left a","text").toString();
            info.setRanking(rank);
            info.setSchool(school);
            root.add(info);
        }
        page.putField("rkschool",root);

    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     *
     */
    @Autowired
    private RkSchoolDataPipeline springDataPipeline;

    //new一个Spider对象传入对应的url和对应的DataPipeline获取web信息并存贮
    //@Scheduled(initialDelay = 1000,fixedDelay = 10000)
    public void process(){
        Spider.create(new Rk_schoolProcessor())
                .addUrl(URL)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000)))
                .thread(5)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
