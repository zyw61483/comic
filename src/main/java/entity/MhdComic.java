package entity;

import com.google.common.collect.Lists;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MhdComic
 *
 * @author: zhaoyiwei
 * @date: 2019/9/16 13:58
 */
@Slf4j
@Data
public class MhdComic extends Comic {

    private static final String commicNameReg = ";decrypt20180904((.*?));";
    private static final String cryptContentReg = "var chapterImages = \"(.*?)\"";
    private static final String chapterPathReg = "var chapterPath = \"(.*?)\"";
    private static final String chapterIndexReg = "<li>\\s*<a href=\"(.*?)\"\\s*class=\"\">\\s*<span>(.*?)</span>\\s*</a>\\s*</li>";
    private static final String mhd = "https://m.manhuadui.com";
    private static final String mhdcdn = "https://mhcdn.manhuazj.com";
    private ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

    public List<ChapterIndex> getChapterIndexUrls(String content) {
        List<ChapterIndex> result = Lists.newArrayList();
        String splitReg = "<ul id=\"chapter-list-1\" data-sort=\"asc\" class=\"Drama \">(.*?)</ul>";
        Pattern split = Pattern.compile(splitReg, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher splitMatcher = split.matcher(content);
        if (splitMatcher.find()) {
            content = splitMatcher.group(0);
        }
        Pattern r = Pattern.compile(chapterIndexReg, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = r.matcher(content);
        while (m.find()) {
            ChapterIndex index = new ChapterIndex();
            index.setUrl(mhd + m.group(1));
            String name = m.group(2);
            if (!name.contains("话")) {
                name = name + "话";
            }
            if (name.substring(0, name.lastIndexOf("话")).length() == 1) {
                name = "0" + name;
            }
            index.setName(name.contains("第") ? name : "第" + name);
            result.add(index);
        }
        return result;
    }

    public Chapter getChapter(String content, String chapterName) throws IOException {
        List<String> result = Lists.newArrayList();
        Pattern r = Pattern.compile(commicNameReg);
        Matcher m = r.matcher(content);
        String name = null;
        while (m.find()) {
            String[] split = m.group(1).split(",");
            name = split[split.length - 1];
            name = split[split.length - 1].substring(1, name.lastIndexOf("\""));
        }

        Pattern rcryptContentP = Pattern.compile(cryptContentReg);
        Matcher mcryptContent = rcryptContentP.matcher(content);
        String cryptContent = null;
        while (mcryptContent.find()) {
            cryptContent = mcryptContent.group(1);
        }

        Pattern chapterPathp = Pattern.compile(chapterPathReg);
        Matcher chapterPathpm = chapterPathp.matcher(content);
        String chapterPath = "";
        while (chapterPathpm.find()) {
            chapterPath = chapterPathpm.group(1);
        }

        try {
            ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
            File file = new File("");
            nashorn.eval(new FileReader(new File(file.getCanonicalPath() + "/src/main/resources/mhd/crypto-js.js")));
            nashorn.eval(new FileReader(new File(file.getCanonicalPath() + "/src/main/resources/mhd/decrypt20180904.js")));

            Invocable invocable = (Invocable) nashorn;
            ScriptObjectMirror mirror = (ScriptObjectMirror) invocable.invokeFunction("decrypt20180904", cryptContent);
            String finalChapterPath = chapterPath;
            mirror.values().forEach(a -> {
                String path = (String) a;
                if (!path.contains("http")) {
                    result.add(mhdcdn + "/" + finalChapterPath + a);
                } else {
                    result.add(path);
                }
            });
        } catch (Exception e) {
        }

        return Chapter.builder().comicName(name).chapterName(chapterName).picUrls(result).build();
    }

}
