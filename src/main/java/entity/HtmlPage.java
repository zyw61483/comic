package entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Data
@Slf4j
public class HtmlPage {
    private String url;
    private String reg;
    private StringBuffer content;

    public HtmlPage(){}

    public HtmlPage(String url,boolean isInit) throws IOException {
        this.url = url;
        if(isInit)
            this.init();
    }

    /**
     * 初始化html页面content
     * @throws IOException
     */
    public void init() throws IOException {
        HttpGet httpGet = new HttpGet(this.url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        this.content = new StringBuffer(EntityUtils.toString(response.getEntity()));
//        log.debug("content:{}", this.content.substring(0,100));
    }

    public HttpEntity getEntity() throws IOException {
        HttpGet httpGet = new HttpGet(this.url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return response.getEntity();
    }

    public String getContent(){
        return this.content.toString();
    }

}