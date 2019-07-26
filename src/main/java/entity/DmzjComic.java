package entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public DmzjComic(String chapterIndexUrl, Integer startChapter, Integer endChapter) throws IOException {
        super(chapterIndexUrl, startChapter, endChapter);
    }

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

    public Chapter getChapter(String content) {
        List<String> result = Lists.newArrayList();
        Pattern r = Pattern.compile(chapterReg);
        Matcher m = r.matcher(content);
        String name = null;
        Long chapter_num = null;
        while (m.find()) {
            String url = m.group(1);
            name = url.substring(url.lastIndexOf("},") + 4, url.lastIndexOf("\","));
            String json = url.substring(0, url.lastIndexOf("}") + 1);
            log.debug("chapter json:{}", json);
            JSONObject jsonObject = JSONObject.parseObject(json);
            chapter_num = jsonObject.getLong("chapter_num");
            List<String> page_urls = (List<String>) jsonObject.get("page_url");
            for (String page_url : page_urls) {
                result.add(page_url);
            }
        }
        return Chapter.builder().name(name).num(chapter_num).page_urls(result).build();
    }

    @Override
    public void downloadChapter(List<ChapterIndex> list, Integer start, Integer end) throws Exception {
        int picCounts = 0;
        for (ChapterIndex index : list) {
            Pattern r = Pattern.compile("第(.*?)话");
            Matcher m = r.matcher(index.getName());
            if (m.find()) {
                int huaNum = 0;
                boolean notNum = false;
                try {
                    huaNum = Integer.parseInt(m.group(1));
                } catch (Exception e) {
                    notNum = true;
                }
                if (notNum || (huaNum > start && huaNum <= end)) {
                    System.out.println(index);
                    HtmlPage chapterPage = new HtmlPage(index.getUrl(), true);
                    Chapter chapterInfo = this.getChapter(chapterPage.getContent());
                    List<String> result = chapterInfo.getPage_urls();
                    picCounts += result.size();
                    for (int i = 0; i < result.size(); i++) {
                        String chapterPicUrl = result.get(i);
                        int name = i;
//                        log.debug("chapter pic:{}", chapterPicUrl);
                        super.getThreadPool().submit(() -> {
                            try {
                                HtmlPage picPage = new HtmlPage(chapterPicUrl, false);
                                HttpEntity entity = picPage.getEntity();
                                String pathName = "/pic1/" + chapterInfo.getName() + "/第" + chapterInfo.getNum() + "话/";
                                File dic = new File(pathName);
                                if (!dic.exists()) {
                                    dic.mkdirs();
                                }
                                File file = new File(pathName + getBtName(name) + ".jpg");
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                FileOutputStream out = new FileOutputStream(file);
                                InputStream in = entity.getContent();
                                byte[] buffer = new byte[4096];
                                int readLength = 0;
                                while ((readLength = in.read(buffer)) > 0) {
                                    byte[] bytes = new byte[readLength];
                                    System.arraycopy(buffer, 0, bytes, 0, readLength);
                                    out.write(bytes);
                                }
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                log.error("download error", e);
                                return false;
                            }
                            return true;
                        });
                    }
                }
            }
        }

        for (int i = 1; i < picCounts; i++) {
            if (super.getThreadPool().take().get()) {
                System.out.println(picCounts + ":" + i);
            }
        }
        super.shutdown();
    }

    private String getBtName(int i) {
        int a = i / 26;
        int y = i % 26;
        StringBuffer name = new StringBuffer();
        for (int j = 0; j < a; j++) {
            name.append("z");
        }
        name.append((char) (y + 97));
        return name.toString();
    }
}
