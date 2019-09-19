import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Getter
@Setter
class ApplicationTest {
    // 文件生成路径
    private final String PATH = "/book/";
    // 目标url
    private final String TARGET = "http://www.wellsofgrace.com/biography/intro/brief-bio/luther/index.htm";

    @Test
    public void test() throws Exception {
        Book book = new Book();
        getChapters(getIndexUrls(book), book);
        createdPdf(book);
    }

    private void createdPdf(Book book) throws Exception {
        log.info("book name:{}", book.getBookName());
        // 初始化文档对象
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(new Rectangle(PageSize.A4));
        // 生成PdfWriter实例
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PATH + book.getBookName() + ".pdf"));
        // 开启文档
        document.open();

        PdfContentByte cb = writer.getDirectContent();
        PdfOutline root = cb.getRootOutline();

        // 封面页
        document.add(book.getBookNamePdf());
        document.newPage();

        // 添加正文
        List<Chapter> chapters = book.getChapters();
        for (int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            log.info("title:{}\n{}", chapter.getTitle(), chapter.content);
            document.add(new Chunk(chapter.getTitle(), book.getContentFont()).setLocalDestination(String.valueOf(i)));
            document.add(new Paragraph(chapter.content, book.getContentFont()));
            new PdfOutline(root, PdfAction.gotoLocalPage(String.valueOf(i), false), chapter.getTitle());
            document.newPage();
        }
        // 关闭文档
        document.close();
    }

    /**
     * 获取所有章节
     *
     * @param indexUrls
     * @param book
     */
    private void getChapters(Elements indexUrls, Book book) {
        List<Chapter> chapters = Lists.newArrayList();
        HashSet<String> chapter = Sets.newHashSet();
        indexUrls.forEach(indexUrl -> {
            String chapterName = indexUrl.text(), chapterHref = indexUrl.attr("abs:href");
            // 截取锚点 去重
            String[] split = chapterHref.split("#");
            if (chapter.add(split[0])) {
                chapters.add(initEachChapter(split[0], chapterName));
            }
        });
        book.setChapters(chapters);
    }

    /**
     * 获取菜单elements,bookName
     *
     * @param book
     * @return
     * @throws IOException
     */
    private Elements getIndexUrls(Book book) throws IOException {
        // 获取菜单url
        Document targetPageDoc = Jsoup.connect(TARGET).get();
        Element leftFrame = targetPageDoc.getElementById("leftFrame");
        if(null == leftFrame){
            leftFrame = targetPageDoc.selectFirst("[name=contents]");
        }

        String src = leftFrame.attr("abs:src");
        // 获取菜单数据 url,text
        Document menuDoc = Jsoup.connect(src).get();
        Elements links = menuDoc.getElementsByTag("a");
        //
        book.setBookName(menuDoc.selectFirst("[color=#008000]").text());
        return links;
    }

    /**
     * 组织每一章的数据
     *
     * @param chapterUrl
     * @param chapterName
     * @return
     */
    private Chapter initEachChapter(String chapterUrl, String chapterName) {
        // 组织名称
        Chapter chapter = new Chapter();
        chapter.setTitle(chapterName);
        // 组织内容
        try {
            StringBuffer sb = new StringBuffer();
            Document chapterDoc = Jsoup.connect(chapterUrl).get();
            Elements elements = chapterDoc.getElementsByTag("p");
            elements.forEach(pContent -> {
                if (StringUtils.isBlank(pContent.text()))
                    return;

                if (pContent.text().length() > 20) {
                    sb.append("        ");
                } else {
                    sb.append("\n");
                }

                sb.append(pContent.text());
                sb.append("\n");
            });
            chapter.setContent(sb.toString());
        } catch (Exception e) {
            log.error("getEachChapterContent error", e);
        }
        return chapter;
    }

    @Data
    class Chapter {
        private String title;
        private String content;
    }

    @Data
    @ToString
    class Book {
        private String bookName;
        private List<Chapter> chapters;
        // 中文字体
        private Font contentFont;
        private Font bookNameFont;

        public Book() throws IOException, DocumentException {
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            bookNameFont = new Font(bfChinese, 24, Font.BOLD);
            contentFont = new Font(bfChinese, 12, Font.NORMAL);
        }

        public Paragraph getBookNamePdf() {
            Paragraph bookName = new Paragraph(this.getBookName(), bookNameFont);
            bookName.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            return bookName;
        }
    }
}
