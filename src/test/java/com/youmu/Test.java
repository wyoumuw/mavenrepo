package com.youmu;

import com.google.common.collect.Lists;
import com.youmu.win.m2repo.ProcessServiceImpl;
import com.youmu.win.m2repo.model.IndexItemModel;
import com.youmu.win.m2repo.model.VersionItemModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class Test {

    @org.junit.Test
    public void indexTest() throws URISyntaxException, IOException {
        // HttpClientUtils.NetResponse netResponse=
        // HttpClientUtils.get("http://mvnrepository.com/search?q=jedis%2Cpool",null);
        // String content=HttpClientUtils.netResponse2String(netResponse);
        InputStream inputStream = null;
        try {
            inputStream = Test.class.getClassLoader().getResourceAsStream("t.html");
            String content = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            // System.out.println(content);
            Document document = Jsoup.parse(content);
            List<IndexItemModel> list = Lists.newArrayList();
            Element mainContent = document.getElementById("maincontent");
            // 解析总条数
            Elements h2s = mainContent.getElementsByTag("h2");
            String total = "0";
            for (Element h2 : h2s) {
                Elements bs = h2.getElementsByTag("b");
                if (StringUtils.isNotBlank(h2.text()) && h2.text().contains("result"))
                    ;
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
            list.forEach(System.out::println);
            System.out.println(total);
        } finally {
            if (null != inputStream)
                inputStream.close();
        }
    }

    @org.junit.Test
    public void version() {
        new ProcessServiceImpl().getVersion("/artifact/commons-io/commons-io")
                .forEach(System.out::println);
    }

    @org.junit.Test
    public void dependency() {
        VersionItemModel versionItemModel = new VersionItemModel();
        versionItemModel.setSubUrl("/artifact/org.testifyproject.tools/service-generator/0.9.6");
        System.out
                .println(new ProcessServiceImpl().fillDependency(versionItemModel).getDependency());
    }

    @org.junit.Test
    public void cache() throws IOException {
        File cache = new File(
                "E:\\m2rep" + "/artifact/org.testifyproject.tools/service-generator/0.9.7");
        if (!cache.exists()) {

            // cache.createNewFile();
        }
    }

}
