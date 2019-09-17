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
    private static String chapterIndexUrl = "https://m.manhuadui.com/manhua/heisewuyecao/";
    // 下载起始章节 包含该章节
    private static Integer startChapter = 151;
    // 下载结束章节 包含该章节
    private static Integer endChapter = 200;
    // 资源
    private static Source source = Source.MHD;

    public static void main(String[] args) throws Exception {
        Comic comic = ComicFactory.getComic(source);
        comic.download(chapterIndexUrl, startChapter, endChapter);
        comic.convertToPDF();
    }
}
