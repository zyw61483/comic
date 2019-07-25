import entity.ChapterIndex;
import entity.DmzjComic;
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

    private static String chapterIndexUrl = "https://m.dmzj.com/info/40341.html";
    private static Integer startChapter = 0;
    private static Integer endChapter = 7;

    public static void main(String[] args) throws Exception {
        HtmlPage chapterIndexPage = new HtmlPage(chapterIndexUrl, true);
        chapterIndexPage.init();
        DmzjComic comic = new DmzjComic();
        List<ChapterIndex> chapterIndex = comic.getChapterIndexUrls(chapterIndexPage.getContent());
        comic.downloadChapter(chapterIndex, startChapter, endChapter);
    }
}
