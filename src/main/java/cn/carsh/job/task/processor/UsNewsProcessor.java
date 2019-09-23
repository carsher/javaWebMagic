package cn.carsh.job.task.processor;

import cn.carsh.job.pojo.UsNewsInfo;
import cn.carsh.job.task.UsNewsDataPipeline;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author crash
 * @version 2019/9/23
 * USnews （按學校和專業的）
 */
@Component
public class UsNewsProcessor implements PageProcessor {
    private static final String url = "https://www.usnews.com/education/best-global-universities/rankings";
    private static final String cookie = "akacd_www=2177452799~rv=9~id=602548edb3a6328e2f7c98d68c2a0173; " +
            "s_cc=true; s_fid=3E9DDC257AC029E6-1997335B27CD7EF3; _ga=GA1.2.2130113860.1568012216; " +
            "_ntv_uid=607dbd9c-d4b1-4e3f-9c88-aed3d09847bc; " +
            "OX_plg=pm; __gads=ID=9a216aac2640199e:T=1568084140:S=ALNI_Ma9XdOHV5C5GLrbl5qG5c1jl-athg; " +
            "s_sq=%5B%5BB%5D%5D; utag_main=v_id:016d14cf8ef00018a2779915f8d103073004206b00bd0$_sn:5$_ss:1$_st:1568712909873$_pn:1%3Bexp-session$ses_id:1568710807520%3Bexp-session$_prevpage:www.usnews.com%2Feducation%2Fbest-global-universities%2Fsearch%3Bexp-1568714709858;" +
            " _sp_id.26f9=09f8c8e2-0036-45cf-9727-18abda692582.1568012218.6.1568802730.1568711319.c7906f50-0e57-4528-98a7-ac4020cd3b11; _sp_ses.26f9=*; RT=\"sl=0&ss=1568710805781&tt=0&obo=0&sh=&dm=usnews.com&si=29c226d6-6744-401a-af76-62a66bc1c19c&bcn=%2F%2F60062f0d.akstat.io%2F&r=https%3A%2F%2Fwww.usnews.com%2Feducation%2Fbest-global-universities%2Fsearch%3F4fd83a6d8728f1fea3dcd67ee3d43b52&ul=1568802766118\"; " +
            "ak_bmsc=F09590D4FD1F5850F2BF50C3E4468DFEA5FE60134E250000EF07825DC249366C~pl9BW9c8syX1Rayg1P34p9mNUqDaiVeNSsWO/ONIhqJg9RweBB7SfdMepgnoVKrqBy4YOrwjRltJJ0lnhivcz9NJEONg9swtsSeUz6cjGNqPvYVfbnJUTFwmMVcFAlspIyrXMk5cZCd3l2z3cdZ+jaXAmYmwcA+9pN4avmBX92dZ9wu5Mpri6rtCVjRifzMxRxWtv/1nYSJ6Ah0z8z/LrR658l16Av29SzXobPjn07MOk=";
    private Site site = Site.me()
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Encoding","gzip, deflate, sdch")
            .addHeader("cookie",cookie)
            .addHeader("Accept-Language","zh-CN,zh;q=0.8")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36")
            .setCharset("utf8")
            .setTimeOut(50000)
            .setRetrySleepTime(5000)
            .setRetryTimes(3)
            ;
    private Set set = new HashSet();
    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("select#subject option").nodes();
        if (set.add(page.getUrl())){
            this.saveInfo(page);
        }
        if (list.size()==0){
            //详情页
        }else{
            //列表页，解析出详情页的URL地址
            for (Selectable selectable : list) {
                String value = selectable.xpath("/option[@value]/@value").toString();
                if (!value.isEmpty()){
                    String JobInfoUrl = "https://www.usnews.com/education/best-global-universities/search?subject="+value+"&name=";
                    page.addTargetRequest(JobInfoUrl);
                }
                //获取下一页的Url
                List<Selectable> nodes = page.getHtml().css("div.pagination a.pager_link").nodes();
                String bkUrl = nodes.get(nodes.size()-1).links().toString();
                page.addTargetRequest(bkUrl);
            }

        }
    }

    //解析页面获取的详情信息
    private void saveInfo(Page page) {
        Html html = page.getHtml();
        String subject = html.css("div#blurbMain h1.h-biggest","text").toString();
        String subjectName = subject.split("for").length==1?"Befault Rankings":subject.split("for ")[1];
        List<UsNewsInfo> root = new ArrayList<>();
        List<Selectable> list = page.getHtml().css("div#resultsMain div.sep").nodes();
        for (int i=1;i<list.size();i++){
            UsNewsInfo info = new UsNewsInfo();
            Selectable htmlSel = list.get(i);
            String rank = Jsoup.parse(htmlSel.css("span").nodes().get(0).toString()).text();
            String school = htmlSel.css("h2.h-taut a","text").toString();
            info.setSubject(subjectName);
            info.setRanking(rank);
            info.setSchool(school);
            root.add(info);
        }
        page.putField("usnews",root);
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     *
     */
    @Autowired
    private UsNewsDataPipeline springDataPipeline;

    //new一个Spider对象传入对应的url和对应的DataPipeline获取web信息并存贮
    //@Scheduled(initialDelay = 1000,fixedDelay = 10000)
    public void process(){
        Spider.create(new UsNewsProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000)))
                .thread(10)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
