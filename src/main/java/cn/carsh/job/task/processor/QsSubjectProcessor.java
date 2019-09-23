package cn.carsh.job.task.processor;

import cn.carsh.job.pojo.QsSubjectInfo;
import cn.carsh.job.task.QsSubjectDataPipeline;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

/**
 * @author crash
 * @version 2019/9/23
 * QS：1-按學校：
 */
@Component
public class QsSubjectProcessor implements PageProcessor {
    private static final String URL = "https://www.topuniversities.com/subject-rankings/2019";
    private Site site = Site.me()
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Encoding","gzip, deflate, sdch")
            .addHeader("Accept-Language","zh-CN,zh;q=0.8")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36")
            .setCharset("utf8")
            .setTimeOut(50000)
            .setRetrySleepTime(5000)
            .setRetryTimes(3);
    private List<HashMap<String,String>> subject=new ArrayList<>();
    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("div.sub-list a").nodes();
        String isadd = page.getHtml().xpath("meta[@name='referrer']").toString();
        if (isadd==null||isadd.length()==0){
            this.saveInfo(page);
        }
        if (list.size()==0){
            //通过分析网页，拼凑链接
            HashMap<String,String> map =new HashMap();
            Html html = page.getHtml();
            String numer = html.xpath("link[@rel='shortlink']/@href ").toString();
            if (numer!=null&&numer.length()>0){
            String[] arrNum = numer.split("\\/");
            String num = arrNum[arrNum.length-1];
            String numValue = page.getHtml().xpath("meta[@name='twitter:title']/@content").toString();
            StringBuffer QsSubjectUrl = new StringBuffer();
            QsSubjectUrl.append("https://www.topuniversities.com/sites/default/files/qs-rankings-data/")
                    .append(num)
                    .append(".txt?");
            map.put(String.valueOf(num),numValue);
            subject.add(map);
            page.addTargetRequest(QsSubjectUrl.toString());
            }
        }else{
            for (Selectable selectable : list) {
                String JobInfoUrl = selectable.links().toString();
                page.addTargetRequest(JobInfoUrl);
            }

        }
    }

    //解析页面获取的信息
    private void saveInfo(Page page) {
        String numer = page.getUrl().toString();
        String num = numer.split("\\/")[7].split("\\.")[0];
        String sub = null;
        for (int i=0;i<this.subject.size();i++) {
            if (this.subject.get(i).get(String.valueOf(num))!=null){
                sub =this.subject.get(i).get(String.valueOf(num));
            }
        }
        List<String> jsonText = new JsonPathSelector("$.data[*]").selectList(page.getRawText());
        List<QsSubjectInfo> list = new ArrayList<>();
                for (String endUrl : jsonText) {
                    QsSubjectInfo qsSubjectInfo = new QsSubjectInfo();
                    JSONObject jsonObject = JSON.parseObject(endUrl);
                    String title = jsonObject.get("title").toString();
                    String rank_display = jsonObject.get("rank_display").toString();
                    qsSubjectInfo.setSchool(title);
                    qsSubjectInfo.setRanking(rank_display);
                    qsSubjectInfo.setSubject(sub);
                    list.add(qsSubjectInfo);
                }
                page.putField("qsSubject",list);
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     *
     */
    @Autowired
    private QsSubjectDataPipeline springDataPipeline;

    //new一个Spider对象传入对应的url和对应的DataPipeline获取web信息并存贮
    @Scheduled(initialDelay = 1000,fixedDelay = 10000)
    public void process(){
        Spider.create(new QsSubjectProcessor())
                .addUrl(URL)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(5000)))
                .thread(10)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
