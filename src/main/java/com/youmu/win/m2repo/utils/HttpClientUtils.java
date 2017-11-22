package com.youmu.win.m2repo.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by youmu on 2017/6/2. 推荐使用resttemplate
 */
public class HttpClientUtils {
    private static final HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    public static CloseableHttpClient getClient() {
        return HttpClients.custom().setConnectionManager(connectionManager)
                .setConnectionManagerShared(true).build();
    }

    public static NetResponse postForm(String url, Map<String, String> strParams,
            Map<String, File> fileParams) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = getClient();
            HttpPost httpMethod = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (!MapUtils.isEmpty(strParams)) {
                for (Map.Entry<String, String> entry : strParams.entrySet()) {
                    builder.addPart(entry.getKey(),
                            new StringBody(entry.getValue(), ContentType.TEXT_PLAIN));
                }
            }
            if (!MapUtils.isEmpty(fileParams)) {
                for (Map.Entry<String, File> entry : fileParams.entrySet()) {
                    builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
                }
            }
            httpMethod.setEntity(builder.build());
            long start = System.nanoTime();
            response = client.execute(httpMethod);
            log.info("get {} spend {} nano", url, System.nanoTime() - start);
            return new NetResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(response);
            close(client);
        }
    }

    public static NetResponse post(String url, Map<String, String> params, byte[] out) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = getClient();
            URI uri = buildUri(url, params);
            HttpPost httpMethod = new HttpPost(uri);
            if (null != out && out.length > 0) {
                HttpEntity entity = new ByteArrayEntity(out);
                httpMethod.setEntity(entity);
            }
            long start = System.nanoTime();
            response = client.execute(httpMethod);
            log.info("get {} spend {} nano", url, System.nanoTime() - start);
            return new NetResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(response);
            close(client);
        }
    }

    public static NetResponse get(String url, Map<String, String> params) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            URI uri = buildUri(url, params);
            HttpGet httpMethod = new HttpGet(uri);
            client = getClient();
            long start = System.nanoTime();
            response = client.execute(httpMethod);
            log.info("get {} spend {} nano", url, System.nanoTime() - start);
            return new NetResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(response);
            close(client);
        }
    }

    public static URI buildUri(String url, Map<String, String> params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        if (!MapUtils.isEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * @param netResponse
     * @return {@code true} if status is 20x
     */
    public static boolean checkStatusCode(NetResponse netResponse) {

        return null != netResponse && netResponse.getCode() / 10 == 20;
    }

    // TODO 要加入共用内容
    public static void close(Closeable closeable) throws IllegalStateException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * NetResponseUtils
     */
    public static String netResponse2String(NetResponse response) {
        if (null == response || null == response.getContent()) {
            return null;
        }
        try {
            String rtn = EntityUtils.toString(response.getContent());
            log.info(rtn);
            return rtn;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File netResponse2File(NetResponse response, String directory, String fileName) {
        if (null == response || null == response.getContent()) {
            return null;
        }
        Header header = response.getContent().getContentType();
        String ext = "";
        try {
            ext = MimeTypes.getDefaultMimeTypes().forName(header.getValue()).getExtension();
        } catch (MimeTypeException e) {
            throw new RuntimeException(e);
        }
        File file = null;
        if (StringUtils.isEmpty(directory)) {
            try {
                file = File.createTempFile(fileName, ext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file = new File(directory + File.separator + fileName + ext);
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            response.getContent().writeTo(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public final static class NetResponse {

        private int code;
        private String msg;
        private HttpEntity content;
        private Header[] headers;

        public NetResponse() {
        }

        public NetResponse(HttpResponse response) throws IOException {
            if (null != response && null != response.getEntity()) {
                this.code = response.getStatusLine().getStatusCode();
                this.msg = response.getStatusLine().getReasonPhrase();
                this.content = new BufferedHttpEntity(response.getEntity());
                this.headers = response.getAllHeaders();
            }
        }

        public NetResponse(int code, String msg, HttpEntity content, Header[] headers) {
            this.code = code;
            this.msg = msg;
            this.content = content;
            this.headers = headers;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public HttpEntity getContent() {
            return content;
        }

        public void setContent(HttpEntity content) {
            this.content = content;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        public Header[] getHeaders(String name) {
            List<Header> headersFound = null;
            // HTTPCORE-361 : we don't use the for-each syntax, i.e.
            // for (Header header : headers)
            // as that creates an Iterator that needs to be garbage-collected
            if (headers == null) {
                return new Header[] {};
            }
            for (int i = 0; i < this.headers.length; i++) {
                final Header header = this.headers[i];
                if (header.getName().equalsIgnoreCase(name)) {
                    if (headersFound == null) {
                        headersFound = new ArrayList<Header>();
                    }
                    headersFound.add(header);
                }
            }
            return headersFound != null ? headersFound.toArray(new Header[headersFound.size()])
                    : new Header[] {};
        }
    }
}