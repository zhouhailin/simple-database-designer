package link.thingscloud.simple.database.designer.service;

import link.thingscloud.simple.database.designer.domain.Table;

import java.io.File;
import java.util.Collection;

/**
 * @author : zhouhailin
 */
public interface GeneratorCode {

    void doGenerateCode(String packageName, File codeDir, Collection<Table> tables);

}
