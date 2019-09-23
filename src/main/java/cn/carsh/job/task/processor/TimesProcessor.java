package cn.carsh.job.task.processor;

import cn.carsh.job.pojo.TimesInfo;
import cn.carsh.job.task.TimesDataPipeline;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

/**
 * @author crash
 * @version 2019/9/23
 * Times（按學校和專業）
 */
@Component
public class TimesProcessor implements PageProcessor {
    private static final String url = "https://www.timeshighereducation.com/world-university-rankings/by-subject";
    private Site site = Site.me()
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Encoding","gzip, deflate, sdch")
            .addHeader("Accept-Language","zh-CN,zh;q=0.8")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36")
            .setCharset("utf8")
            .setTimeOut(50000)
            .setRetrySleepTime(5000)
            .setRetryTimes(3)
            ;
    private List<HashMap<String,String>> subject=new ArrayList<>();
    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("div.pane-content div.item-list a").nodes();
        String isadd = page.getHtml().xpath("meta[@name='msvalidate.01']").toString();
        //判断是否为详情是则save数据
        if (isadd==null||isadd.isEmpty()){
            this.saveInfo(page);
        }
        if (list.size()==0){
            //通过分析网页，拼凑链接
            HashMap<String,String> map =new HashMap();
            String jsonRoot=null;
            List<Selectable> scriptList = page.getHtml().css("script").nodes();
            if (scriptList.size()!=0){
                String str = scriptList.get(9).toString().substring(38);
                 jsonRoot = str.substring(0,(str.length()-11));
            }
            if (jsonRoot!=null) {
                JSONObject jsonObject = JSON.parseObject(jsonRoot);
                JSONObject the_data_rankings = JSONObject.parseObject(jsonObject.get("the_data_rankings").toString());
                JSONObject datatable = JSONObject.parseObject(the_data_rankings.get("#datatable-1").toString());
                JSONObject ajax = JSONObject.parseObject(datatable.get("ajax").toString());
                String urler = ajax.get("url").toString();
                String[] title = page.getHtml().xpath("meta[@name='twitter:title']/@content").toString().split("\\:");
                String numValue = title[title.length - 1];
                String[] arrNum = urler.split("\\/");
                String num = arrNum[arrNum.length - 1].split("\\.")[0];
                map.put(String.valueOf(num), numValue);
                subject.add(map);
                page.addTargetRequest(urler);
            }
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
        String urler = page.getUrl().toString();
        String [] arrNum =  urler.split("\\/");
        String num =arrNum[arrNum.length-1].split("\\.")[0];
        String sub = null;
        for (int i=0;i<this.subject.size();i++) {
            if (this.subject.get(i).get(String.valueOf(num))!=null){
                sub =this.subject.get(i).get(String.valueOf(num));
            }
        }
        List<String> jsonText = new JsonPathSelector("$.data[*]").selectList(page.getRawText());
        List<TimesInfo> list = new ArrayList<>();
        for (String endUrl : jsonText) {
            TimesInfo timesInfo = new TimesInfo();
            JSONObject jsonObject = JSON.parseObject(endUrl);
            String title = jsonObject.get("name").toString();
            String rank_display = jsonObject.get("rank").toString();
            timesInfo.setSchool(title);
            timesInfo.setRanking(rank_display);
            timesInfo.setSubject(sub);
            list.add(timesInfo);
        }
        page.putField("times",list);

    }

    @Override
    public Site getSite() {
        return site;
    }


    @Autowired
    private TimesDataPipeline springDataPipeline;

    //new一个Spider对象传入对应的url和对应的DataPipeline获取web信息并存贮
    //@Scheduled(initialDelay = 1000,fixedDelay = 10000)
    public void process(){
        Spider.create(new TimesProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000)))
                .thread(10)
                .addPipeline(springDataPipeline)
                .run();
    }
}
