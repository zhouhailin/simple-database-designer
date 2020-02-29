package link.thingscloud.simple.database.designer.service.impl.script;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import link.thingscloud.simple.database.designer.domain.FieldTypeEnum;
import link.thingscloud.simple.database.designer.domain.Table;
import link.thingscloud.simple.database.designer.service.impl.AbstractGeneratorScript;
import link.thingscloud.simple.database.designer.util.ConsumerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

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

        AtomicBoolean autoIncremnet = new AtomicBoolean(false);

        StringBuilder sbPk = new StringBuilder();

        // `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
        // `id` bigint(0) NOT NULL COMMENT '自增主键',
        // `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
        //  `xx` bigint(255) NOT NULL AUTO_INCREMENT,
        ConsumerUtil.doAccept(table.getColumns(), (prev, current, next) -> {
            // 当前是否是第一个字段
            if (prev != null) {
                sb.append(",").append(LF);
            }
            // 字段名
            sb.append("  `").append(current.getName()).append("`");
            if (current.isPrimaryKey()) {
                if (StrUtil.isNotEmpty(sbPk.toString())) {
                    sbPk.append(", ");
                }
                sbPk.append("`").append(current.getName()).append("`");

                sb.append(" bigint(20) UNSIGNED NOT NULL");
                if (current.isAutoIncrement()) {
                    sb.append(" AUTO_INCREMENT");
                    autoIncremnet.set(true);
                }
            } else {
                // 数据类型
                appendDbType(sb, current.getType(), current.getLength(), current.getScale());

                // 是否可以为空
                if (current.isNullable()) {
                    sb.append(" NOT NULL");
                } else {
                    sb.append(" NULL");
                }

                // 默认值设置
                if (StrUtil.isNotBlank(current.getDefaultValue())) {
                    if (StrUtil.containsAny(current.getDefaultValue(), "空字符串", "EMPTY_STRING")) {
                        sb.append(" DEFAULT ''");
                    } else {
                        sb.append(" DEFAULT '").append(current.getDefaultValue()).append("'");
                    }
                }
            }
            // 添加备注信息
            if (StrUtil.isNotBlank(current.getComment())) {
                sb.append(" COMMENT '").append(current.getComment()).append("'");
            }
        });

        // 当前是最后一个字段，判断是否存在主键信息
        if (StrUtil.isNotEmpty(sbPk.toString())) {
            sb.append(",").append(LF).append("  PRIMARY KEY (").append(sbPk.toString()).append(")");
        }

        //  PRIMARY KEY (`id`),
        //  UNIQUE INDEX `idx_xx`(`name`, `age`)
        // 处理索引
        ConsumerUtil.doAccept(table.getIndexs(), (prev, current, next) -> {
            if (StrUtil.isBlank(current.getName())) {
                return;
            }
            sb.append(",").append(LF).append(" ");
            if (current.isUniqueIndex()) {
                sb.append(" UNIQUE");
            }
            sb.append(" INDEX `").append(StrUtil.trim(current.getName())).append("`(");
            ConsumerUtil.doAccept(current.getFieldNames(), (prev1, current1, next1) -> {
                if (prev1 != null) {
                    sb.append(", ");
                }
                sb.append("`").append(current1).append("`");
            });
            sb.append(")");
            if (StrUtil.isNotBlank(current.getComment())) {
                sb.append(" COMMENT '").append(current.getComment()).append("'");
            }
        });

        sb.append(LF);

        if (StrUtil.equalsIgnoreCase(table.getCharset(), "utf8mb4")) {
            sb.append(") ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci");
        } else {
            sb.append(") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci");
        }
        if (autoIncremnet.get()) {
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

        appendInfo(sb, "MySQL");

        tables.forEach(table -> {
            doGenerateTableScript(sb, table);
        });

        log.debug("do generate mysql script : \n{}", sb.toString());
        FileUtil.writeUtf8String(sb.toString(), new File(scriptDir, scriptDir.getName() + "-mysql.sql"));
    }
}
