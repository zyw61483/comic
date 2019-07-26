package entity;

import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Comic
 *
 * @author: zhaoyiwei
 * @date: 2019/7/24 11:29
 */
@Data
public abstract class Comic {
    private Integer startChapter = 0;
    private Integer endChapter = 1;
    private String chapterContent;
    private ExecutorService threadPool = Executors.newFixedThreadPool(15);
    private CompletionService<Boolean> completionService = new ExecutorCompletionService<>(threadPool);

    public Comic(String chapterIndexUrl, Integer startChapter, Integer endChapter) throws IOException {
        HtmlPage chapterIndexPage = new HtmlPage(chapterIndexUrl, true);
        this.chapterContent = chapterIndexPage.getContent();
        this.startChapter = startChapter;
        this.endChapter = endChapter;
    }


    public CompletionService<Boolean> getThreadPool() {
        return completionService;
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public abstract List<ChapterIndex> getChapterIndexUrls(String content);

    public abstract Chapter getChapter(String content);

    public abstract void downloadChapter(List<ChapterIndex> list, Integer start, Integer end) throws Exception;

    public void download() throws Exception {
        List<ChapterIndex> chapterIndex = getChapterIndexUrls(this.chapterContent);
        downloadChapter(chapterIndex, startChapter, endChapter);
    }
}
