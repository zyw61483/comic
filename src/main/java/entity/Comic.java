package entity;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Comic
 *
 * @author: zhaoyiwei
 * @date: 2019/7/24 11:29
 */
@Slf4j
@Data
public abstract class Comic {
    private Boolean isConvertPDF = true;
    private Integer startChapter = 0;
    private Integer endChapter = 1;
    private String chapterContent;
    private final String PDF_PATH = "/pdf/";
    private final String PIC_PATH = "/pic/";
    private String comicName;
    private ExecutorService threadPool = Executors.newFixedThreadPool(15);
    private CompletionService<Boolean> completionService = new ExecutorCompletionService<>(threadPool);

    public abstract List<ChapterIndex> getChapterIndexUrls(String content);

    public abstract Chapter getChapter(String content, String chapterName);

    public void download(String chapterIndexUrl, Integer startChapter, Integer endChapter) throws Exception {
        // 漫画目录页初始化
        this.init(chapterIndexUrl, startChapter, endChapter);
        // 获取章节信息
        List<ChapterIndex> chapterIndex = this.getChapterIndexUrls(this.chapterContent);
        int picCounts = 0;
        for (ChapterIndex index : chapterIndex) {
            if (isHandleThisChapter(index.getName())) {
                log.info("ChapterIndex:{}", index);
                HtmlPage chapterPage = new HtmlPage(index.getUrl(), true);
                Chapter chapterInfo = this.getChapter(chapterPage.getContent(), index.getName());
                picCounts += downloadChapter(chapterInfo);
            }
        }
        this.showProgress(picCounts);
        this.shutdown();
    }

    private void init(String chapterIndexUrl, Integer startChapter, Integer endChapter) throws IOException {
        HtmlPage chapterIndexPage = new HtmlPage(chapterIndexUrl, true);
        this.chapterContent = chapterIndexPage.getContent();
        this.startChapter = startChapter;
        this.endChapter = endChapter;
    }

    private int downloadChapter(Chapter chapterInfo) {
        List<String> picUrls = chapterInfo.getPicUrls();
        for (int i = 0; i < picUrls.size(); i++) {
            String tempUrl = picUrls.get(i);
            String picUrl = tempUrl.trim().replaceAll(" ", "%20");
            int name = i;
            this.getThreadPool().submit(() -> {
                try {
                    HtmlPage picPage = new HtmlPage(picUrl, false);
                    HttpEntity entity = picPage.getEntity();
                    String pathName = PIC_PATH + chapterInfo.getComicName() + "/" + chapterInfo.getChapterName();
                    if (Objects.isNull(this.getComicName())) {
                        this.setComicName(chapterInfo.getComicName());
                    }
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
        return picUrls.size();
    }

    private CompletionService<Boolean> getThreadPool() {
        return completionService;
    }

    private void showProgress(int picCounts) throws Exception {
        for (int i = 1; i < picCounts; i++) {
            if (getThreadPool().take().get()) {
                DecimalFormat df = new DecimalFormat("0.00%");
                log.info("sum:{}，num:{} {}", picCounts - 1, i, df.format((float) i / (picCounts - 1)));
            }
        }
        log.info("download success");
    }

    private void shutdown() {
        threadPool.shutdown();
    }

    private String getBtName(int i) {
        int a = i / 26;
        int y = i % 26;
        StringBuffer name = new StringBuffer("/");
        for (int j = 0; j < a; j++) {
            name.append("z");
        }
        name.append((char) (y + 97));
        return name.toString();
    }

    public void convertToPDF() throws Exception {
        Rectangle rect = new Rectangle(PageSize.A4);
        Document document = new Document(rect);
        String comicPath = PIC_PATH + "/" + this.getComicName();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(comicPath + this.getStartChapter() + "-" + this.getEndChapter() + ".pdf"));
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfOutline root = cb.getRootOutline();
        File comic = new File(comicPath);
        File[] chapters = comic.listFiles();
        for (int i = 0; i < chapters.length; i++) {

            if(!isHandleThisChapter(chapters[i].getName())){
                continue;
            }

            String chapterPath = comicPath + "/" + chapters[i].getName();
            File chapter = new File(chapterPath);
            File[] pics = chapter.listFiles();
            document.add(new Chunk(chapters[i].getName()).setLocalDestination(Integer.toString(i)));
            new PdfOutline(root, PdfAction.gotoLocalPage(Integer.toString(i), false), chapters[i].getName());
            for (int j = 0; j < pics.length; j++) {
                String picPath = chapterPath + "/" + pics[j].getName();
                Image image = Image.getInstance(picPath);
                image.scaleToFit(rect);
                document.add(image);
                document.newPage();
            }
        }
        document.close();
    }

    private boolean isHandleThisChapter(String name) {
        Pattern r = Pattern.compile("第(.*?)话");
        Matcher m = r.matcher(name);
        boolean isDownload = false;
        if (m.find()) {
            int huaNum = 0;
            try {
                huaNum = Integer.parseInt(m.group(1));
                isDownload = huaNum >= getStartChapter() && huaNum <= getEndChapter();
            } catch (Exception e) {
            }
        }
        return isDownload;
    }
}
