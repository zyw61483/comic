import com.google.common.collect.Maps;
import entity.Comic;
import entity.DmzjComic;
import entity.MhdComic;
import entity.MhtComic;
import enums.Source;

import java.util.Map;

import static enums.Source.*;

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
        sourceMap.put(MHD,new MhdComic());
    }

    public static Comic getComic(Source source) {
        switch (source) {
            case MHT:
                return sourceMap.get(MHT);
            case MHD:
                return sourceMap.get(MHD);
            default:
                return sourceMap.get(DMZJ);
        }
    }
}
