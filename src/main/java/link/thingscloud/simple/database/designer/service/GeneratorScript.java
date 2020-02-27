package link.thingscloud.simple.database.designer.service;

import link.thingscloud.simple.database.designer.domain.Table;

import java.io.File;
import java.util.Collection;

/**
 * @author : zhouhailin
 */
public interface GeneratorScript {

    void doGenerateScript(File scriptDir, Collection<Table> tables);

}
