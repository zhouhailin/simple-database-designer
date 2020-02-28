package link.thingscloud.simple.database.designer.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 序号	字段名	类型	长度	小数点	允许NULL(是/否)	默认值（空字符串）	主键（是/否）	注释
 *
 * @author : zhouhailin
 */
@Data
@Accessors(chain = true)
public class Column {
    private String id;
    private String name;
    private FieldTypeEnum type;
    private Integer length;
    private Integer scale;
    private boolean nullable;
    private String defaultValue;
    private boolean primaryKey;
    private boolean autoIncrement;
    private String comment;
}
