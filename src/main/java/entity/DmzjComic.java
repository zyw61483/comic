package entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DmzjComic
 *
 * @author: zhaoyiwei
 * @date: 2019/7/24 13:58
 */
@Slf4j
@Data
public class DmzjComic extends Comic {

    private static final String reg = "initIntroData\\((.*?)\\);";
    private static final String chapterReg = "mReader.initData\\((.*?)\\);";

    public List<ChapterIndex> getChapterIndexUrls(String content) {
        Pattern r = Pattern.compile(reg);
        Matcher m = r.matcher(content);
        ArrayList<ChapterIndex> result = Lists.newArrayList();
        if (m.find()) {
            String json = m.group(1);
            log.debug("init chapter index json:{}", json);
            JSONArray array = JSONObject.parseArray(json);
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = (JSONObject) array.get(i);
                if (StringUtils.equals(item.getString("title"), "连载")) {
                    JSONArray datas = item.getJSONArray("data");
                    for (int j = 0; j < datas.size(); j++) {
                        JSONObject chapterItem = (JSONObject) datas.get(j);
//                        log.debug("chapter url:{}", String.format("https://m.dmzj.com/view/%s/%s.html", chapterItem.getString("comic_id"), chapterItem.getString("id")));
                        ChapterIndex index = new ChapterIndex();
                        index.setName(chapterItem.getString("chapter_name"));
                        index.setUrl(String.format("https://m.dmzj.com/view/%s/%s.html", chapterItem.getString("comic_id"), chapterItem.getString("id")));
                        result.add(index);
                    }
                }
            }
        }
        return result;
    }

    public Chapter getChapter(String content,String chapterName) {
        List<String> result = Lists.newArrayList();
        Pattern r = Pattern.compile(chapterReg);
        Matcher m = r.matcher(content);
        String name = null;
        while (m.find()) {
            String url = m.group(1);
            name = url.substring(url.lastIndexOf("},") + 4, url.lastIndexOf("\","));
            String json = url.substring(0, url.lastIndexOf("}") + 1);
            log.debug("chapter json:{}", json);
            JSONObject jsonObject = JSONObject.parseObject(json);
            result = (List<String>) jsonObject.get("page_url");
        }
        return Chapter.builder().commicName(name).chapterName(chapterName).picUrls(result).build();
    }

}
