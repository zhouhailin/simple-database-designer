package link.thingscloud.simple.database.designer.service.impl.code;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import link.thingscloud.simple.database.designer.domain.FieldTypeEnum;
import link.thingscloud.simple.database.designer.domain.Table;
import link.thingscloud.simple.database.designer.service.impl.AbstractGeneratorCode;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

import static cn.hutool.core.util.StrUtil.LF;

/**
 * @author : zhouhailin
 */
@Slf4j
@Service
public class MybatisPlusGeneratorCode extends AbstractGeneratorCode {

    @Value("${appVersion}")
    private String appVersion;
    @Value("${appName}")
    private String appName;
    @Value("${appUrl}")
    private String appUrl;
    @Value("${code.info.author:}")
    private String author;

    private void appendCodeInfo(StringBuilder sb, Table table) {
        if (table.getCodeInfo() == null) {
            if (StrUtil.isBlank(author)) {
                sb.append(" * @author : ").append(appName).append("  ").append(appVersion).append(LF);
            } else {
                sb.append(" * @author : ").append(author).append(LF);
            }
        } else {
            sb.append(" * @author : ").append(table.getCodeInfo().getAuthor()).append(LF);
        }
    }

    private void doGenerateController(TableInfo info, String moduleName, Table table) {
        log.debug("generate controller {} ...", info.getServiceImplFullClassName());
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(info.getControllerPkgName()).append(";").append(LF)
                .append(LF)
                .append("import ").append(info.getServiceFullClassName()).append(";").append(LF)
                .append("import org.springframework.beans.factory.annotation.Autowired;").append(LF)
                .append("import org.springframework.web.bind.annotation.RequestMapping;").append(LF)
                .append("import org.springframework.web.bind.annotation.RestController;").append(LF)
                .append("import ").append(info.getEntityFullClassName()).append(";").append(LF)
                .append(LF)
                .append("/**").append(LF)
                .append(" * <p>").append(LF)
                .append(" * ").append(table.getComment()).append(" 前端控制器").append(LF)
                .append(" * </p>").append(LF)
                .append(LF);

        appendCodeInfo(sb, table);
        sb
                .append(" */").append(LF)
                .append("@RestController").append(LF)
                .append("@RequestMapping(\"/").append(moduleName).append(info.getCamelCaseName()).append("\")").append(LF)
                .append("public class ").append(info.getControllerSimpleClassName()).append(" {").append(LF)
                .append(LF)
                .append("}")
                .append(LF);
        System.out.println(sb.toString());
        FileUtil.writeUtf8String(sb.toString(), info.getControllerFile());
    }

    private void doGenerateServiceImpl(TableInfo info, Table table) {
        log.debug("generate service impl {} ...", info.getServiceImplFullClassName());
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(info.getServiceImplPkgName()).append(";").append(LF)
                .append(LF)
                .append("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;").append(LF)
                .append("import ").append(info.getEntityFullClassName()).append(";").append(LF)
                .append("import ").append(info.getMapperFullClassName()).append(";").append(LF)
                .append("import ").append(info.getServiceFullClassName()).append(";").append(LF)
                .append("import lombok.extern.slf4j.Slf4j").append(";").append(LF)
                .append("import org.springframework.stereotype.Service").append(";").append(LF)
                .append(LF)
                .append("/**").append(LF)
                .append(" * <p>").append(LF)
                .append(" * ").append(table.getComment()).append(" 服务类").append(LF)
                .append(" * </p>").append(LF)
                .append(LF);
        appendCodeInfo(sb, table);
        sb
                .append(" */").append(LF)
                .append("@Slf4j").append(LF)
                .append("@Service").append(LF)
                .append("public class ").append(info.getServiceImplSimpleClassName()).append(" extends ServiceImpl<").append(info.getMapperSimpleClassName()).append(",").append(info.getEntitySimpleClassName()).append(">").append(" implements ").append(info.getServiceSimpleClassName()).append(" {").append(LF)
                .append(LF)
                .append("}")
                .append(LF);
        System.out.println(sb.toString());
        FileUtil.writeUtf8String(sb.toString(), info.getServiceImplFile());
    }

    private void doGenerateService(TableInfo tableInfo, Table table) {
        log.debug("generate service {}.java ...", tableInfo.getServiceFullClassName());
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(tableInfo.getMapperFullClassName()).append(";").append(LF)
                .append(LF)
                .append("import com.baomidou.mybatisplus.extension.service.IService;").append(LF)
                .append("import ").append(tableInfo.getEntityFullClassName()).append(";").append(LF)
                .append(LF)
                .append("/**").append(LF)
                .append(" * <p>").append(LF)
                .append(" * ").append(table.getComment()).append(" 服务类").append(LF)
                .append(" * </p>").append(LF)
                .append(LF);
        appendCodeInfo(sb, table);
        sb
                .append(" */").append(LF)
                .append("public interface ").append(tableInfo.getServiceSimpleClassName()).append(" extends IService<").append(tableInfo.getEntitySimpleClassName()).append(">").append(" {").append(LF)
                .append(LF)
                .append("}")
                .append(LF);
        System.out.println(sb.toString());
        FileUtil.writeUtf8String(sb.toString(), tableInfo.getServiceFile());
    }

    private void doGenerateMapper(TableInfo tableInfo, Table table) {
        log.debug("generate mapper {} ...", tableInfo.getMapperFullClassName());
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(tableInfo.getMapperPkgName()).append(";").append(LF)
                .append(LF)
                .append("import com.baomidou.mybatisplus.core.mapper.BaseMapper;").append(LF)
                .append("import ").append(tableInfo.getEntityFullClassName()).append(";").append(LF)
                .append(LF)
                .append("/**").append(LF)
                .append(" * <p>").append(LF)
                .append(" * ").append(table.getComment()).append(" 接口").append(LF)
                .append(" * </p>").append(LF)
                .append(LF);
        appendCodeInfo(sb, table);
        sb
                .append(" */").append(LF)
                .append("public interface ").append(tableInfo.getMapperSimpleClassName()).append(" extends BaseMapper<").append(tableInfo.getEntitySimpleClassName()).append(">").append(" {").append(LF)
                .append(LF)
                .append("}")
                .append(LF);
        System.out.println(sb.toString());
        FileUtil.writeUtf8String(sb.toString(), tableInfo.getMapperFile());
    }

    private void doGenerateEntity(TableInfo tableInfo, Table table) {
        log.debug("generate entity {} ...", tableInfo.getEntityFullClassName());
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(tableInfo.getEntityPkgName()).append(";").append(LF)
                .append(LF)
                .append("import com.baomidou.mybatisplus.annotation.*;").append(LF)
                .append("import lombok.Data;").append(LF)
                .append("import lombok.EqualsAndHashCode;").append(LF)
                .append("import lombok.experimental.Accessors;").append(LF)
                .append(LF)
                .append("import java.io.Serializable;").append(LF)
                .append("import java.time.LocalDateTime;").append(LF)
                .append(LF)
                .append("/**").append(LF)
                .append(" * <p>").append(LF)
                .append(" * ").append(table.getComment()).append(" 数据库实体").append(LF)
                .append(" * </p>").append(LF)
                .append(LF);
        appendCodeInfo(sb, table);
        sb
                .append(" */").append(LF)
                .append(LF)
                .append("@Data").append(LF)
                .append("@EqualsAndHashCode").append(LF)
                .append("@Accessors(chain = true)").append(LF)
                .append("@TableName(\"").append(table.getName()).append("\")").append(LF)
                .append("public class ").append(tableInfo.getEntitySimpleClassName()).append(" implements Serializable {").append(LF)
                .append(LF)
                .append("    private static final long serialVersionUID = 1L;\n").append(LF);
        table.getColumns().forEach(column -> {
            String name = column.getName();
            String fieldName = StrUtil.toCamelCase(column.getName());
            // 注释
            sb.append("    /**").append(LF);
            sb.append("     * ").append(column.getComment()).append(LF);
            sb.append("     */").append(LF);
            if (column.isPrimaryKey()) {
                // 主键
                if (column.isAutoIncrement()) {
                    sb.append("    @TableId(value = \"").append(name).append("\", type = IdType.AUTO)").append(LF);
                } else {
                    sb.append("    @TableId(value = \"").append(name).append("\", type = IdType.NONE)").append(LF);
                }
                sb.append("    private Long ").append(fieldName).append(";").append(LF);
            } else {
                // 非主键字段
                FieldTypeEnum type = column.getType();
                switch (type) {
                    case BOOLEAN:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        sb.append("    private Boolean ").append(fieldName).append(";").append(LF);
                        break;
                    case BIG_DECIMAL:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        sb.append("    private BigDecimal ").append(fieldName).append(";").append(LF);
                        break;
                    case INTEGER:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        sb.append("    private Integer ").append(fieldName).append(";").append(LF);
                        break;
                    case LONG:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        sb.append("    private Long ").append(fieldName).append(";").append(LF);
                        break;
                    case DATE_TIME:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        if (StrUtil.equalsIgnoreCase(name, "create_time")) {
                            sb.append("    @TableField(fill = FieldFill.INSERT)").append(LF);
                        }
                        if (StrUtil.equalsIgnoreCase(name, "update_time")) {
                            sb.append("    @TableField(fill = FieldFill.INSERT_UPDATE)").append(LF);
                        }
                        sb.append("    private LocalDateTime ").append(fieldName).append(";").append(LF);
                        break;
                    case STRING:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        sb.append("    private String ").append(fieldName).append(";").append(LF);
                        break;
                    case TINY_INT:
                        sb.append("    @TableId(\"").append(name).append("\")").append(LF);
                        sb.append("    private Short ").append(fieldName).append(";").append(LF);
                        break;
                    default:
                        log.error("unknow column type : {}", type);
                }
            }
        });
        sb.append("}").append(LF);
        System.out.println(sb.toString());
        FileUtil.writeUtf8String(sb.toString(), tableInfo.getEntityFile());
    }

    @Override
    public void doGenerateCode(String packageName, File rootDir, Collection<Table> tables) {
        log.info("do generate MyBatis Plus code packege : {} ...", packageName);

        File mybatisPlusDir = new File(rootDir, "code/java/mybatis-plus/");
        if (mybatisPlusDir.isDirectory()) {
            FileUtil.del(mybatisPlusDir);
        }
        File codeDir = new File(mybatisPlusDir, packageName.replace(".", "/"));

        tables.forEach(table -> {
            TableInfo info = new TableInfo()
                    .setPackageName(packageName)
                    .setName(table.getName())
                    .setCamelCaseName(StrUtil.toCamelCase(table.getName()))
                    .setUpperCamelCaseName(StrUtil.upperFirst(StrUtil.toCamelCase(table.getName())));

            if (table.getCodeInfo() != null && StrUtil.isNotBlank(table.getCodeInfo().getAuthor())) {
                info.setPackageName(table.getCodeInfo().getNamespace());
            }

            info.setEntitySimpleClassName(info.getUpperCamelCaseName() + "DO")
                    .setEntityPkgName(info.getPackageName() + ".entity")
                    .setEntityCodeDir(new File(codeDir, "entity"));
            info
                    .setEntityFile(new File(info.getEntityCodeDir(), info.getEntitySimpleClassName() + ".java"))
                    .setEntityFullClassName(info.getEntityPkgName() + "." + info.getEntitySimpleClassName());

            info
                    .setMapperSimpleClassName(info.getUpperCamelCaseName() + "Mapper")
                    .setMapperPkgName(info.getPackageName() + ".mapper")
                    .setMapperCodeDir(new File(codeDir, "mapper"));
            info
                    .setMapperFile(new File(info.getMapperCodeDir(), info.getMapperSimpleClassName() + ".java"))
                    .setMapperFullClassName(info.getMapperPkgName() + "." + info.getMapperSimpleClassName());

            info
                    .setServiceSimpleClassName(info.getUpperCamelCaseName() + "Service")
                    .setServicePkgName(info.getPackageName() + ".service")
                    .setServiceCodeDir(new File(codeDir, "service"));
            info
                    .setServiceFile(new File(info.getServiceCodeDir(), info.getServiceSimpleClassName() + ".java"))
                    .setServiceFullClassName(info.getServicePkgName() + "." + info.getServiceSimpleClassName());

            info
                    .setServiceImplSimpleClassName(info.getUpperCamelCaseName() + "ServiceImpl")
                    .setServiceImplPkgName(info.getPackageName() + ".service.impl")
                    .setServiceImplCodeDir(new File(codeDir, "service/impl"));
            info
                    .setServiceImplFile(new File(info.getServiceImplCodeDir(), info.getServiceImplSimpleClassName() + ".java"))
                    .setServiceImplFullClassName(info.getServiceImplPkgName() + "." + info.getServiceImplSimpleClassName());

            info
                    .setControllerSimpleClassName(info.getUpperCamelCaseName() + "Controller")
                    .setControllerPkgName(info.getPackageName() + ".controller")
                    .setControllerCodeDir(new File(codeDir, "contorller"));
            info
                    .setControllerFile(new File(info.getControllerCodeDir(), info.getControllerSimpleClassName() + ".java"))
                    .setControllerFullClassName(info.getControllerPkgName() + "." + info.getControllerSimpleClassName());

            doGenerateEntity(info, table);
            doGenerateMapper(info, table);
            doGenerateService(info, table);
            doGenerateServiceImpl(info, table);
            if (table.getCodeInfo() != null && StrUtil.isNotBlank(table.getCodeInfo().getModuleName())) {
                doGenerateController(info, table.getCodeInfo().getModuleName() + "/", table);
            } else {
                doGenerateController(info, "", table);
            }
        });
    }


    @Data
    @Accessors(chain = true)
    class TableInfo {
        private String packageName;
        private String name;
        private String camelCaseName;
        private String upperCamelCaseName;

        private File entityFile;
        private File entityCodeDir;
        private String entitySimpleClassName;
        private String entityFullClassName;
        private String entityPkgName;

        private File mapperFile;
        private File mapperCodeDir;
        private String mapperSimpleClassName;
        private String mapperFullClassName;
        private String mapperPkgName;

        private File serviceFile;
        private File serviceCodeDir;
        private String serviceSimpleClassName;
        private String serviceFullClassName;
        private String servicePkgName;

        private File serviceImplFile;
        private File serviceImplCodeDir;
        private String serviceImplSimpleClassName;
        private String serviceImplFullClassName;
        private String serviceImplPkgName;

        private File controllerFile;
        private File controllerCodeDir;
        private String controllerSimpleClassName;
        private String controllerFullClassName;
        private String controllerPkgName;
    }
}
