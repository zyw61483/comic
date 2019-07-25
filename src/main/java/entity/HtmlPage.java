package entity;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
public class HtmlPage {
    private String url;
    private String reg;
    private StringBuffer content;

    public HtmlPage(String url,boolean flag) throws IOException {
        this.url = url;
        if(flag)
            this.init();
    }


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

    public String getTargetData() {
        String result = "not found";
        Pattern r = Pattern.compile(this.reg);
        Matcher m = r.matcher(this.content);
        if (m.find()) {
            result = m.group(1);
        }
        return result;
    }

    public String getContent(){
        return this.content.toString();
    }

//    public List<String> getTargetDatas() {
//        List<String> result = Lists.newArrayList();
//        Pattern r = Pattern.compile(this.reg);
//        Matcher m = r.matcher(this.content);
//        while (m.find()) {
//            String url = m.group(1);
//            String json = url.substring(0, url.lastIndexOf("}")+1);
//            JSONObject jsonObject = JSONObject.parseObject(json);
//            List<String> page_urls = (List<String>) jsonObject.get("page_url");
//            for(String page_url:page_urls){
//                result.add(page_url);
//            }
//        }
//        return Chapter.builder().name(name).num(chapter_num).page_urls(result).build();
//    }
}