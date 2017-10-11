package org.togethwy.sample.common;

import org.togethwy.cheetah.Cheetah;
import org.togethwy.cheetah.SiteConfig;
import org.togethwy.cheetah.downloader.JsonDataResult;
import org.togethwy.cheetah.downloader.Page;
import org.togethwy.cheetah.handler.ConsoleHandler;
import org.togethwy.cheetah.handler.RedisHandler;
import org.togethwy.cheetah.processor.PageProcessor;
import org.togethwy.cheetah.selector.Html;
import org.togethwy.cheetah.handler.ElasticHandler;

import java.util.*;

/**
 * 豆瓣电影爬虫demo
 *
 * @author wangtonghe
 * @date 2017/7/8 16:44
 */
public class DoubanMovieDemo implements PageProcessor {

    private SiteConfig siteConfig = SiteConfig.create();

    @Override
    public void process(Page page) {

        String name = page.getHtml().$("#content h1 span[property=v:itemreviewed]").getValue();

        Html info = page.getHtml().$("#info");

        String author = info.get(0).$("span .attrs a").getValue();
        List<String> actor_list = info.$("span.actor .attrs a").getAll();
        actor_list = actor_list.size() == 0 ? null : actor_list;

        List<String> category_list = info.$("span[property=v:genre]").getAll();
        category_list = category_list.size() == 0 ? null : category_list;

        String country = info.$("span.pl:contains(制片国家/地区)").get(0).nextNodeText();
        String lang = info.$("span.pl:contains(语言)").get(0).nextNodeText();
        String date = info.$("span[property=v:initialReleaseDate]").getValue();

        String mark = page.getHtml().$("#interest_sectl .rating_num").getValue();

        Map<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("author", author);
        result.put("actor", actor_list);
        result.put("lang", lang);
        result.put("country", country);
        result.put("category", category_list);
        if (date != null && date.indexOf("(") > 0) {
            date = date.substring(0, date.indexOf("("));
        }
        result.put("date", date);
        result.put("mark", mark);

        page.addResult(result);


    }

    @Override
    public SiteConfig setAndGetSiteConfig() {
        this.siteConfig.setDomain("https://movie.douban.com")
                .setStartUrl("https://movie.douban.com")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36")
                .addCookie("bid", "PI0P2w4aMDI")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
                .addHeader("Accept-Language", "zh-CN, zh; q=0.8, en; q=0.6")
                .setThreadSleep(2000)
                .setThreadNum(3)
                .setJsonAPIUrl("https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags=&start=0")
                .setStartJSONAPI(true)
                .setOnlyAPI(true);
        return siteConfig;
    }


    @Override
    public void processJSON(JsonDataResult jsonData) {

        List<Map<String, Object>> listData = jsonData.parseListFromMap();

        listData.forEach(eachMap -> {
            String url = (String) eachMap.get("url");
            jsonData.addWaitRequest(url);

        });
        String url = jsonData.getUrl();
        String numStr = url.substring(url.lastIndexOf("start="));
        int num = Integer.parseInt(numStr.substring(6));
        String newUrl = url.replace(numStr, "start=" + (num + 10));
        System.out.println(newUrl);
        jsonData.setJsonUrl(newUrl);

    }


    public static void main(String[] args) {
            Cheetah.create(new DoubanMovieDemo())
//                    .setHandler(new ElasticHandler("localhost",9300,"elasticsearch","cheetah","movie"))
                    .setHandler(new RedisHandler("localhost","movie"))
                    .setHandler(new ConsoleHandler())
                    .run();
    }
}
