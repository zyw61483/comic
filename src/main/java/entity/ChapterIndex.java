package entity;

import lombok.Data;
import lombok.ToString;

/**
 * ChapterIndex
 *
 * @author: zhaoyiwei
 * @date: 2019/7/24 16:51
 */
@Data
@ToString
public class ChapterIndex {
    /**
     * 第几话
     */
    private String name;
    /**
     * 对应话的url
     */
    private String url;
}
