package link.thingscloud.simple.database.designer.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author : zhouhailin
 */
@Data
@Accessors(chain = true)
public class Index {
    private String id;
    private String name;
    private boolean uniqueIndex;
    private String method;
    private List<String> fieldNames;
    private String comment;
}
