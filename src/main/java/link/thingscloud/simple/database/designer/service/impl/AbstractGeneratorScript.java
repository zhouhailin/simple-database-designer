package link.thingscloud.simple.database.designer.service.impl;

import cn.hutool.core.date.DateUtil;
import link.thingscloud.simple.database.designer.service.GeneratorScript;
import org.springframework.beans.factory.annotation.Value;

import static cn.hutool.core.util.CharUtil.LF;

/**
 * @author : zhouhailin
 */
public abstract class AbstractGeneratorScript implements GeneratorScript {

    @Value("${appVersion}")
    private String appVersion;
    @Value("${appName}")
    private String appName;
    @Value("${appUrl}")
    private String appUrl;

    /**
     * @param sb     sb
     * @param dbName 数据库名称
     */
    protected void appendInfo(StringBuilder sb, String dbName) {
        sb.append("/*").append(LF);
        sb.append("  ").append(appName).append(" (").append(appVersion).append(")").append(LF);
        sb.append(LF);
        sb.append("  Project URL  : ").append(appUrl).append(LF);
        sb.append(LF);
        sb.append("  Database Type : ").append(dbName).append(LF);
        sb.append(LF);
        sb.append("  Date : " + DateUtil.now()).append(LF);
        sb.append("*/").append(LF).append(LF);
    }

}
