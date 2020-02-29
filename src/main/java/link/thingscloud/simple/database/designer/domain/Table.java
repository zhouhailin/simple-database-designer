package link.thingscloud.simple.database.designer.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhouhailin
 */
@Data
@Accessors(chain = true)
public class Table {
    private String charset;
    private String dbName;
    private String name;
    private String comment;
    private List<Column> columns = new ArrayList<>(256);
    private List<Index> indexs = new ArrayList<>(64);
    private CodeInfo codeInfo;
}
