package entity;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MhtComic
 *
 * @author: zhaoyiwei
 * @date: 2019/7/26 10:03
 */
@Slf4j
public class MhtComic extends Comic {

    private static final String chapterIndexReg = "<li>\\s*<a href=\"(.*?)\">\\s*<span class=\"list_con_tb\"></span><span class=\"list_con_zj\">(.*?)\\s*</span><span class=\"list_con_tips\">";
    private static final String chapterReg = "var chapterImages = (.*?);";
    private static final String commicNameReg = "<h1><a href=\"(.*?)\">(.*?)</a></h1>";
    private static final String mht = "https://www.manhuatao.com";

    @Override
    public List<ChapterIndex> getChapterIndexUrls(String content) {
        List<ChapterIndex> result = Lists.newArrayList();
        String splitReg = "<ul id=\"chapter-list-1\" data-sort=\"asc\" class=\"list_con_li autoHeight\">(.*?)</ul>";
        Pattern split = Pattern.compile(splitReg, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher splitMatcher = split.matcher(content);
        if (splitMatcher.find()) {
            content = splitMatcher.group(0);
        }
        Pattern r = Pattern.compile(chapterIndexReg, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = r.matcher(content);
        while (m.find()) {
//            log.info("name:{} url:{}", m.group(2), m.group(1));
            ChapterIndex index = new ChapterIndex();
            index.setUrl(mht + m.group(1));
            index.setName(m.group(2));
            result.add(index);
        }
        return result;
    }

    @Override
    public Chapter getChapter(String content, String chapterName) {
        List<String> result = Lists.newArrayList();
        Pattern r = Pattern.compile(chapterReg);
        Matcher m = r.matcher(content);
        if (m.find()) {
            List<String> list = (List<String>) JSONArray.parse(m.group(1));
            for (String temp : list) {
                result.add("https://restp.dongqiniqin.com/" + temp);
            }
        }
        return Chapter.builder().commicName(getComicName(content)).chapterName(chapterName).picUrls(result).build();
    }

    private String getComicName(String content) {
        Pattern r = Pattern.compile(commicNameReg);
        Matcher m = r.matcher(content);
        if (m.find()) {
            return m.group(2);
        }
        return "xxx";
    }

}
