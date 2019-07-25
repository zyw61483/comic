package entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Chapter{
    private String name;
    private Long num;
    private List<String> page_urls;
}