import com.google.common.collect.Maps;
import entity.Comic;
import entity.DmzjComic;
import entity.MhtComic;
import enums.Source;

import java.util.Map;

import static enums.Source.DMZJ;
import static enums.Source.MHT;

/**
 * ComicFactory
 *
 * @author: zhaoyiwei
 * @date: 2019/7/26 16:44
 */
public class ComicFactory {
    private static Map<Source, Comic> sourceMap = Maps.newHashMap();

    static {
        sourceMap.put(DMZJ,new DmzjComic());
        sourceMap.put(MHT,new MhtComic());
    }

    public static Comic getComic(Source source) {
        switch (source) {
            case MHT:
                return sourceMap.get(MHT);
            default:
                return sourceMap.get(DMZJ);
        }
    }
}
