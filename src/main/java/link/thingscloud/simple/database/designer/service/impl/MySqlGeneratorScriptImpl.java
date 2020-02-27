package link.thingscloud.simple.database.designer.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import link.thingscloud.simple.database.designer.domain.Column;
import link.thingscloud.simple.database.designer.domain.FieldTypeEnum;
import link.thingscloud.simple.database.designer.domain.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

import static cn.hutool.core.util.StrUtil.LF;

/**
 * @author : zhouhailin
 */
@Slf4j
@Service
public class MySqlGeneratorScriptImpl extends AbstractGeneratorScript {

    private void appendDbType(final StringBuilder sb, FieldTypeEnum fieldType, int length, int scale) {
        /**
         * CREATE TABLE `obs`.`Untitled`  (
         *   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
         *   `name` varchar(50) NOT NULL COMMENT '名称',
         *   `create_time` datetime(0) NOT NULL COMMENT '时间',
         *   `update_time` datetime(0) NULL,
         *   `xx` tinyint(0) NOT NULL AUTO_INCREMENT,
         *   `aa` bigint(255) NULL,
         *   PRIMARY KEY (`xx`, `id`)
         * ) COMMENT = '星河盛世';
         */
        switch (fieldType) {
            case STRING:
                sb.append(" varchar(").append(length).append(")");
                break;
            case DATE_TIME:
                sb.append(" datetime(0)");
                break;
            case LONG:
                sb.append(" bigint(20)");
                break;
            case TINY_INT:
                sb.append(" tinyint(4)");
                break;
            case INTEGER:
                sb.append(" int(").append(length).append(")");
                break;
            case BOOLEAN:
                sb.append(" bit(1)");
                break;
            case BIG_DECIMAL:
                sb.append(" decimal(").append(length).append(", ").append(scale).append(")");
                break;
            default:
                break;
        }
    }

    private void appendColumn(final StringBuilder sb, Column column) {

    }

    /**
     * CREATE TABLE `obs`.`Untitled`  (
     * `id` bigint(0) NOT NULL COMMENT '自增主键',
     * `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
     * `name` varchar(50) NOT NULL COMMENT '名称',
     * `create_time` datetime(0) NOT NULL COMMENT '时间',
     * PRIMARY KEY (`id`)
     * ) COMMENT = '星河盛世';
     *
     * @param sb
     * @param table
     */
    private void doGenerateTableScript(final StringBuilder sb, Table table) {
        log.info("generate table name : {}", table.getName());

        appendInfo(sb, "MySQL");

        sb.append("-- ----------------------------").append(LF);
        sb.append("-- Table structure for ").append(table.getName()).append(LF);
        sb.append("-- ----------------------------").append(LF);
        if (StrUtil.isBlank(table.getDbName())) {
            sb.append("DROP TABLE IF EXISTS `").append(table.getName()).append("`;").append(LF);
            sb.append("CREATE TABLE `").append(table.getName()).append("`  (").append(LF);
        } else {
            sb.append("DROP TABLE IF EXISTS `").append(table.getDbName()).append("`.`").append(table.getName()).append("`;").append(LF);
            sb.append("CREATE TABLE `").append(table.getDbName()).append("`.`").append(table.getName()).append("`  (").append(LF);
        }
        boolean autoIncrement = false;

        String primaryKey = "";
        for (Column column : table.getColumns()) {
            sb.append("`").append(column.getName()).append("`");
            // 是否为主键
            if (StrUtil.contains(column.getPrimaryKey(), "是")) {
                // PRIMARY KEY (`xx`, `id`)
                if (StrUtil.isEmpty(primaryKey)) {
                    primaryKey += "`" + column.getName() + "`";
                } else {
                    primaryKey += ", `" + column.getName() + "`";
                }
                // `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                // `id` bigint(0) NOT NULL COMMENT '自增主键',
                // `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                //  `xx` bigint(255) NOT NULL AUTO_INCREMENT,
                sb.append(" bigint(20) NOT NULL");
                if (StrUtil.contains(column.getPrimaryKey(), "自动递增")) {
                    sb.append(" AUTO_INCREMENT");
                    autoIncrement = true;
                }
                if (StrUtil.isNotBlank(column.getComment())) {
                    sb.append(" COMMENT '").append(column.getComment()).append("'");
                }
                sb.append(",").append(LF);
                continue;
            }

            // 数据类型
            appendDbType(sb, column.getType(), column.getLength(), column.getScale());

            // 是否可以为空
            if (column.isNullable()) {
                sb.append(" NOT NULL");
            } else {
                sb.append(" NULL");
            }

            // 默认值设置
            if (StrUtil.isNotBlank(column.getDefaultValue())) {
                if (StrUtil.containsAny(column.getDefaultValue(), "空字符串", "EMPTY_STRING")) {
                    sb.append(" DEFAULT ''");
                } else {
                    sb.append(" DEFAULT '").append(column.getDefaultValue()).append("'");
                }
            }

            // 字段说明
            if (StrUtil.isNotBlank(column.getComment())) {
                sb.append(" COMMENT '").append(column.getComment()).append("'");
            }
            sb.append(",").append(LF);
        }

        if (StrUtil.isNotEmpty(primaryKey)) {
            sb.append("PRIMARY KEY (").append(primaryKey).append(")").append(LF);
        }
        if (StrUtil.equalsIgnoreCase(table.getCharset(), "utf8mb4")) {
            sb.append(") ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci");
        } else {
            sb.append(") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci");
        }
        if (autoIncrement) {
            sb.append(" AUTO_INCREMENT = 1");
        }
        if (StrUtil.isNotBlank(table.getComment())) {
            sb.append(" COMMENT = '").append(table.getComment()).append("'");
        }
        sb.append(";").append(LF);
        // ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '星河盛世' ROW_FORMAT = Dynamic
        sb.append(LF);
    }

    @Override
    public void doGenerateScript(File scriptDir, Collection<Table> tables) {
        log.info("do generate mysql script ...");
        StringBuilder sb = new StringBuilder();
        tables.forEach(table -> {
            doGenerateTableScript(sb, table);
        });
        FileUtil.writeUtf8String(sb.toString(), new File(scriptDir, scriptDir.getName() + "-mysql.sql"));
    }
}
