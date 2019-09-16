import entity.Comic;
import enums.Source;
import lombok.extern.slf4j.Slf4j;

/**
 * Application
 *
 * @author: zhaoyiwei
 * @date: 2019/7/24 11:40
 */
@Slf4j
public class Application {

    // 漫画目录页面
    private static String chapterIndexUrl = "http://m.dmzj.com/info/15663.html";
    //
    // 下载起始章节 包含该章节
    private static Integer startChapter = 11;
    // 下载结束章节 包含该章节
    private static Integer endChapter = 30;
    // 资源
    private static Source source = Source.DMZJ;

    public static void main(String[] args) throws Exception {
        Comic comic = ComicFactory.getComic(source);
        comic.download(chapterIndexUrl, startChapter, endChapter);
        comic.convertToPDF();
    }
}
