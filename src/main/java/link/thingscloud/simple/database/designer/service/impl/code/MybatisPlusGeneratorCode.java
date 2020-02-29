package link.thingscloud.simple.database.designer.service.impl.code;

import link.thingscloud.simple.database.designer.domain.Table;
import link.thingscloud.simple.database.designer.service.impl.AbstractGeneratorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

/**
 * @author : zhouhailin
 */
@Slf4j
@Service
public class MybatisPlusGeneratorCode extends AbstractGeneratorCode {
    @Override
    public void doGenerateCode(String packageName, File codeDir, Collection<Table> tables) {
        log.info("do generate MyBatis Plus code ...");
    }
}
