package entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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

    public abstract List<ChapterIndex> getChapterIndexUrls(String content);

    public abstract Chapter getChapter(String content);

    public abstract Integer downloadChapter(List<ChapterIndex> list, Integer start, Integer end) throws Exception;

    public void download() throws Exception {
        List<ChapterIndex> chapterIndex = getChapterIndexUrls(this.chapterContent);
        Integer picCounts = downloadChapter(chapterIndex, startChapter, endChapter);
        this.showProgress(picCounts);
        this.shutdown();
    }

    private void showProgress(int picCounts) throws Exception {
        for (int i = 1; i < picCounts; i++) {
            if (getThreadPool().take().get()) {
                log.info("共{}张，目前:{}", picCounts - 1, i);
            }
        }
    }

    private void shutdown() {
        threadPool.shutdown();
    }
}
