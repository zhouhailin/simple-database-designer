package link.thingscloud.simple.database.designer.service.impl;

import cn.hutool.core.io.FileUtil;
import link.thingscloud.simple.database.designer.domain.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

/**
 * @author : zhouhailin
 */
@Slf4j
@Service
public class OracleGeneratorScriptImpl extends AbstractGeneratorScript {
    @Override
    public void doGenerateScript(File scriptDir, Collection<Table> tables) {
        log.info("do generate oracle script ...");
        StringBuilder sb = new StringBuilder();

        appendInfo(sb, "ORACLE");

        tables.forEach(table -> {
        });
        FileUtil.writeUtf8String(sb.toString(), new File(scriptDir, scriptDir.getName() + "-oracle.sql"));
    }
}
