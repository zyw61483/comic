package entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Chapter{
    private String commicName;
    private String chapterName;
    private List<String> picUrls;
}