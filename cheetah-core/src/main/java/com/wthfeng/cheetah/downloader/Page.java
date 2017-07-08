package com.wthfeng.cheetah.downloader;

import com.wthfeng.cheetah.Result;
import com.wthfeng.cheetah.selector.Html;
import com.wthfeng.cheetah.util.StringUtil;
import com.wthfeng.cheetah.util.UrlUtils;
import org.jsoup.Jsoup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 下载的页面表示类
 * @author wangtonghe
 * @date 2017/7/8 16:14
 */
public class Page {

    private String rawHtml;

    private Html html;

    private int statusCode;

    private Result result;

    private Set<Request> waitRequests = new HashSet<>();

    public Html getHtml() {
        if(this.rawHtml==null){
            return null;
        }
        return new Html(rawHtml);
    }

    public void setHtml(Html html) {
        this.html = html;
    }

    /**
     * 当前页面url
     */
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Set<Request> getWaitRequests() {
        return waitRequests;
    }


    public void addWaitRequest(List<String> requests){
        //TODO: synchronized  同步锁
        for (String s : requests) {
            if (StringUtil.isEmpty(s) || s.equals("#") || s.startsWith("javascript:")) {
                break;
            }
            s = UrlUtils.canonicalizeUrl(s, getUrl());
            waitRequests.add(new Request(s));
        }
    }



    public Result getResult() {
        return result;

    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getRawHtml() {
        return rawHtml;
    }

    public void setRawHtml(String rawHtml) {
        this.rawHtml = rawHtml;
    }
}