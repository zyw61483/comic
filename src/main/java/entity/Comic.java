package entity;

import lombok.Data;

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
    private ExecutorService threadPool = Executors.newFixedThreadPool(15);
    private CompletionService<Boolean> completionService = new ExecutorCompletionService<>(threadPool);
    private String name;
    private String chapterIndexUrl;
    private Long chapter;
    private String chapterUrl;

    public CompletionService<Boolean> getThreadPool() {
        return completionService;
    }

    public void shutdown(){
        threadPool.shutdown();
    }

    public abstract List<ChapterIndex> getChapterIndexUrls(String content);

    public abstract Chapter getChapter(String content);

    public abstract void downloadChapter(List<ChapterIndex> list, Integer start, Integer end) throws Exception;
}
