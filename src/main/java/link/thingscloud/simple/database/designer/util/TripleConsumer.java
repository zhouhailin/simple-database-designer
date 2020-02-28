package link.thingscloud.simple.database.designer.util;

/**
 * @author : zhouhailin
 */
public interface TripleConsumer<L, M, N> {

    void accept(L prev, M current, N next);

}
