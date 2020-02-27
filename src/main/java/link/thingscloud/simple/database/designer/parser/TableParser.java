package link.thingscloud.simple.database.designer.parser;

import link.thingscloud.simple.database.designer.domain.Table;

/**
 * @author : zhouhailin
 */
public class TableParser {

    private TableParser() {
    }

    public static Table parse() {
        return new Table();
    }

}
