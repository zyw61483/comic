import entity.ChapterIndex;
import entity.DmzjCommic;
import entity.HtmlPage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Application
 *
 * @author: zhaoyiwei
 * @date: 2019/7/24 11:40
 */
@Slf4j
public class Application {

    private static String chapterIndexUrl = "https://m.dmzj.com/info/33365.html";

    public static void main(String[] args) throws Exception {
        HtmlPage chaptherIndex = new HtmlPage(chapterIndexUrl, true);
        chaptherIndex.init();
        DmzjCommic commic = new DmzjCommic();
        List<ChapterIndex> chapterIndex = commic.getChapterIndexUrls(chaptherIndex.getContent());
        commic.downloadChapter(chapterIndex,0,6);
    }
}
