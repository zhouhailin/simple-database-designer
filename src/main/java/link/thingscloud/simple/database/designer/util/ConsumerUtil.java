package link.thingscloud.simple.database.designer.util;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author : zhouhailin
 */
public class ConsumerUtil {

    public static <O> void doAccept(List<O> list, TripleConsumer<O, O, O> consumer) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        O prev;
        O next;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                prev = list.get(i - 1);
            } else {
                prev = null;
            }
            if (i < (size - 1)) {
                next = list.get(i + 1);
            } else {
                next = null;
            }
            consumer.accept(prev, list.get(i), next);
        }
    }

}
