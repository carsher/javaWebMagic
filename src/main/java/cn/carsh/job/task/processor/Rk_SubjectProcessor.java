package cn.carsh.job.task.processor;

import cn.carsh.job.pojo.RkSubjectInfo;
import cn.carsh.job.task.RkSubjectDataPipeline;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crash
 * @version 2019/9/23
 * 軟科：2-按學科：
 */
@Component
public class Rk_SubjectProcessor implements PageProcessor {
    private static final String URL = "http://www.zuihaodaxue.com/arwu_subject_rankings.html";
    private Site site = Site.me()
            .setCharset("utf8")
            .setTimeOut(10000)
            .setRetrySleepTime(3000)
            .setRetryTimes(3);
    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("div.row div.subject-ranking-content a").nodes();
        if (list.size()==0){
            //详情页
            this.saveInfo(page);
        }else{
            //列表页，解析出详情页的URL地址
            for (Selectable selectable : list) {
                String JobInfoUrl = selectable.links().toString();
                page.addTargetRequest(JobInfoUrl);
            }

        }
    }

    //解析页面获取的详情信息
    private void saveInfo(Page page) {
        Html html = page.getHtml();
        String subject = html.css("div.ranking_year_select span","text").toString();
        String subjectName = subject.split("\\-")[1];
        List<RkSubjectInfo> root = new ArrayList<>();
        List<Selectable> list = page.getHtml().css("div.news-blk tr").nodes();
        for (int i=1;i<list.size();i++){
            RkSubjectInfo info = new RkSubjectInfo();
            Selectable htmlSel = list.get(i);
            String rank = Jsoup.parse(htmlSel.css("td").nodes().get(0).toString()).text();
            String school = htmlSel.css("td.align-left","text").toString();
            info.setSubject(subjectName);
            info.setRanking(rank);
            info.setSchool(school);
            root.add(info);
        }
        page.putField("rksubject",root);
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     *
     */
    @Autowired
    private RkSubjectDataPipeline springDataPipeline;

    //new一个Spider对象传入对应的url和对应的DataPipeline获取web信息并存贮
    //@Scheduled(initialDelay = 1000,fixedDelay = 10000)
    public void process(){
        Spider.create(new Rk_SubjectProcessor())
                .addUrl(URL)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(40000)))
                .thread(10)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
