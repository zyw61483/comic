package entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Chapter{
    private String comicName;
    private String chapterName;
    private List<String> picUrls;
}