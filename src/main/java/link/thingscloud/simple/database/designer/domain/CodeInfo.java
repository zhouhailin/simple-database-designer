package link.thingscloud.simple.database.designer.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : zhouhailin
 */
@Data
@Accessors(chain = true)
public class CodeInfo {
    private String namespace;
    private String author;
    private String requestUrl;
}
