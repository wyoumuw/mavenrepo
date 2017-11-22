package com.youmu.win.m2repo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.youmu.win.m2repo.model.IndexItemModel;
import com.youmu.win.m2repo.model.IndexPageModel;
import com.youmu.win.m2repo.model.IndexQueryModel;
import com.youmu.win.m2repo.model.VersionItemModel;
import com.youmu.win.m2repo.utils.HttpClientUtils;
import com.youmu.win.m2repo.utils.NetUtils;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class ProcessServiceImpl implements ProcessService {

    public IndexPageModel getIndex(String query, int page) {
        HttpClientUtils.NetResponse netResponse = HttpClientUtils.get(NetUtils.queryUrl(),
                new IndexQueryModel(query, page).getMap());
        String content = HttpClientUtils.netResponse2String(netResponse);
         System.out.println(content);
        Document document = Jsoup.parse(content);
        List<IndexItemModel> list = Lists.newArrayList();
        Element mainContent = document.getElementById("maincontent");
        // 解析总条数
        Elements h2s = mainContent.getElementsByTag("h2");
        String total = "0";
        for (Element h2 : h2s) {
            Elements bs = h2.getElementsByTag("b");
            if (StringUtils.isNotBlank(h2.text()) && h2.text().contains("result"));
            for (Element b : bs) {
                total = b.text();
            }
            break;
        }
        Elements ims = mainContent.getElementsByClass("im");
        for (Element im : ims) {
            Elements imHeaders = im.getElementsByClass("im-header");
            for (Element imHeader : imHeaders) {
                Elements as = imHeader.getElementsByTag("a");
                IndexItemModel indexItemModel = new IndexItemModel();
                list.add(indexItemModel);
                for (Element a : as) {
                    if (a.hasAttr("href") && a.hasClass("im-usage")) {
                        indexItemModel.setUsage(a.child(0).text());
                    } else {
                        indexItemModel.setName(a.text());
                        indexItemModel.setSubUrl(a.attr("href"));
                    }

                }
            }
        }
        return new IndexPageModel(list, Integer.valueOf(total));
    }

    public List<VersionItemModel> getVersion(String subUrl) {
        HttpClientUtils.NetResponse netResponse = HttpClientUtils.get(NetUtils.querySubUrl(subUrl),
                null);
        String content = HttpClientUtils.netResponse2String(netResponse);
        // System.out.println(content);
        Document document = Jsoup.parse(content);
        List<VersionItemModel> list = Lists.newArrayList();
        Elements versionss = document.getElementsByClass("grid versions");
        Element versions = versionss.size() == 0 ? null : versionss.iterator().next();
        if (null == versions) {
            return Collections.emptyList();
        }
        Elements trs = versions.getElementsByTag("tr");
        Iterator<Element> trsi = trs.iterator();
        if (!trsi.hasNext()) {
            return Collections.emptyList();
        }
        trsi.next();
        while (trsi.hasNext()) {
            Element tr = trsi.next();
            Elements tds = tr.getElementsByTag("td");
            VersionItemModel versionItemModel = new VersionItemModel();
            for (Element td : tds) {
                Elements as = td.getElementsByTag("a");
                if (0 == as.size()) {
                    versionItemModel.setDate(td.text());
                }
                for (Element a : as) {
                    String href = a.attr("href");

                    if (href.endsWith("usages")) {
                        versionItemModel.setUsage(a.text());
                    } else if (a.hasClass("b lic")) {
                    } else {
                        versionItemModel.setName(a.text());
                        versionItemModel.setSubUrl(subUrl + href.substring(href.indexOf("/")));
                    }
                }
            }
            list.add(versionItemModel);
        }
        return list;
    }

    public VersionItemModel fillDependency(VersionItemModel versionItemModel) {
        HttpClientUtils.NetResponse netResponse = HttpClientUtils
                .get(NetUtils.querySubUrl(versionItemModel.getSubUrl()), null);
        String content = HttpClientUtils.netResponse2String(netResponse);
        Document document = Jsoup.parse(content);
        Element element = document.getElementById("maven-a");
        versionItemModel.setDependency(element.text());
        return versionItemModel;
    }

    public static void main(String[] args) {
        System.out.println("common-io/3.3.2".substring("common-io/3.3.2".indexOf("/")));
    }
}
