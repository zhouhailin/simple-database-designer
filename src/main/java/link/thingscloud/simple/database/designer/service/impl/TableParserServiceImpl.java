package link.thingscloud.simple.database.designer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import link.thingscloud.simple.database.designer.domain.*;
import link.thingscloud.simple.database.designer.service.GeneratorCode;
import link.thingscloud.simple.database.designer.service.GeneratorScript;
import link.thingscloud.simple.database.designer.service.TableParserService;
import link.thingscloud.simple.database.designer.util.CellUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhouhailin
 */
@Slf4j
@Service
public class TableParserServiceImpl implements InitializingBean, TableParserService {

    @Value("${simpleGenerateDir}")
    private String simpleGenerateDir;
    @Value("${code.info.namespace}")
    private String namespace;

    @Autowired
    private List<GeneratorScript> generateScripts;
    @Autowired
    private List<GeneratorCode> generatorCodes;

    private final List<Table> tables = new ArrayList<>(254);

    private void handleCodeInfo(Table table, XSSFRow row) {
        XSSFCell namespaceCell = row.getCell(3);
        XSSFCell authorCell = row.getCell(5);
        XSSFCell reqUrlCell = row.getCell(7);
        table.setCodeInfo(new CodeInfo()
                .setNamespace(StrUtil.trimToEmpty(namespaceCell.toString()))
                .setAuthor(StrUtil.trimToEmpty(authorCell.toString()))
                .setRequestUrl(StrUtil.trimToEmpty(reqUrlCell.toString())));
    }

    private void handleTableInfo(Table table, XSSFRow row) {
        XSSFCell charsetCell = row.getCell(3);
        XSSFCell databaseCell = row.getCell(5);
        XSSFCell tableNameCell = row.getCell(7);
        XSSFCell tableCommentCell = row.getCell(9);
        if (StrUtil.isNotBlank(charsetCell.toString())) {
            table.setCharset(charsetCell.toString());
        }
        table.setDbName(databaseCell.getStringCellValue())
                .setName(tableNameCell.getStringCellValue())
                .setComment(tableCommentCell.getStringCellValue());
        log.info("add row index : {}, table header charset[{}] : {}, database name[{}] : {}, table name[{}] : {}, comment[{}] : {}",
                CellUtil.getRowIndex(charsetCell),
                CellUtil.getIndex(charsetCell), table.getCharset(),
                CellUtil.getIndex(databaseCell), table.getDbName(),
                CellUtil.getIndex(tableNameCell), table.getName(),
                CellUtil.getIndex(tableCommentCell), table.getComment());
    }

    private void handleColumnInfo(Table table, XSSFRow row) {
        XSSFCell idCell = row.getCell(1);
        XSSFCell nameCell = row.getCell(2);
        XSSFCell typeCell = row.getCell(3);
        XSSFCell lengthCell = row.getCell(4);
        XSSFCell decimalCell = row.getCell(5);
        XSSFCell nullableCell = row.getCell(6);
        XSSFCell defaultValueCell = row.getCell(7);
        XSSFCell primaryKeyCell = row.getCell(8);
        XSSFCell commentCell = row.getCell(9);
        if (StrUtil.isBlank(nameCell.toString())) {
            log.info("ignore row index : {}, cause column name is blank.", CellUtil.getRowIndex(nameCell));
            return;
        }
        FieldTypeEnum fieldType = getFieldType(typeCell.toString());
        if (fieldType == null) {
            log.error("parse column row index : {}, cause value is not number.", CellUtil.getRowIndex(idCell));
            log.error("parse field type failed, index {} : {}", CellUtil.getIndex(typeCell), typeCell.toString());
            throw new IllegalArgumentException("fieldType is invalid : " + CellUtil.getIndex(typeCell));
        }
        Column column = new Column()
                .setId(idCell.toString())
                .setName(nameCell.toString().toLowerCase())
                .setType(fieldType)
                .setLength(NumberUtil.parseInt(lengthCell.toString()))
                .setScale(NumberUtil.parseInt(decimalCell.toString()))
                .setNullable(StrUtil.equals(nullableCell.toString(), "否"))
                .setDefaultValue(defaultValueCell.toString())
                .setComment(commentCell.toString());
        if (StrUtil.containsAny(primaryKeyCell.toString(), "是")) {
            column.setPrimaryKey(true);
            if (StrUtil.containsAny(primaryKeyCell.toString(), "自动递增")) {
                column.setAutoIncrement(true);
            }
        }
        log.info("add row index : {}, column : {}", CellUtil.getRowIndex(idCell), column);
        table.getColumns().add(column);
    }

    /**
     * @param table
     * @param row
     */
    private void handleIndexInfo(Table table, XSSFRow row) {
        XSSFCell idCell = row.getCell(1);
        XSSFCell nameCell = row.getCell(2);
        XSSFCell typeCell = row.getCell(3);
        XSSFCell methodCell = row.getCell(4);
        XSSFCell fieldsCell = row.getCell(5);
        XSSFCell commentCell = row.getCell(9);
        if (StrUtil.isBlank(nameCell.toString())) {
            log.info("ignore row index : {}, cause index name is blank.", CellUtil.getRowIndex(nameCell));
            return;
        }

        if (StrUtil.isBlank(fieldsCell.toString())) {
            log.error("parse row row index : {}, cause field must not be blank.", CellUtil.getRowIndex(idCell));
            throw new IllegalArgumentException("fieldType is invalid : " + CellUtil.getIndex(typeCell));
        }

        List<String> fieldNameList = new ArrayList<>();

        String[] fileldNameArr = StrUtil.split(fieldsCell.toString(), ",");
        for (String fieldName : fileldNameArr) {
            boolean found = false;
            for (Column column : table.getColumns()) {
                if (StrUtil.equalsIgnoreCase(StrUtil.trimToEmpty(fieldName), column.getName())) {
                    fieldNameList.add(column.getName());
                    found = true;
                }
            }
            if (found) {
                continue;
            }
            log.error("not found index field {} : {}", CellUtil.getIndex(typeCell), typeCell.toString());
            throw new IllegalArgumentException("index field is invalid : " + CellUtil.getIndex(typeCell));
        }
        Index index0 = new Index()
                .setId(idCell.toString())
                .setName(StrUtil.trimToEmpty(nameCell.toString()))
                .setUniqueIndex(StrUtil.containsAny(typeCell.toString(), "Unique"))
                .setFieldNames(fieldNameList)
                .setMethod(methodCell.toString())
                .setComment(StrUtil.trimToEmpty(commentCell.toString()));
        log.info("add row index : {}, index : {}", CellUtil.getRowIndex(idCell), index0);
        table.getIndexs().add(index0);
    }

    private boolean isIndexInfo = false;

    private void handleRow(Table table, XSSFRow row) {
        XSSFCell cell = row.getCell(1);
        if (cell == null) {
            return;
        }
        String cellValue = cell.toString();
        if (StrUtil.isBlank(cellValue)) {
            log.info("ignore row index : {}, cause value is blank.", CellUtil.getRowIndex(cell));
            return;
        }
        if (StrUtil.equals(cellValue, "#")) {
            isIndexInfo = false;
            handleTableInfo(table, row);
            return;
        } else if (StrUtil.contains(cellValue, "索引")) {
            isIndexInfo = true;
            return;
        } else if (StrUtil.equals(cellValue, "#2")) {
            // java code info
            handleCodeInfo(table, row);
        }
        if (!NumberUtil.isNumber(cellValue)) {
            log.info("ignore row index : {}, cause value is not number.", CellUtil.getRowIndex(cell));
            return;
        }
        if (isIndexInfo) {
            handleIndexInfo(table, row);
        } else {
            handleColumnInfo(table, row);
        }
    }

    /**
     * 字符串(String),整型(Integer),长整型(Long),日期时间(DateTime),浮点型(BigDecimal)
     *
     * @param fieldType
     * @return
     */
    private FieldTypeEnum getFieldType(String fieldType) {
        if (StrUtil.containsAny(fieldType, "String")) {
            return FieldTypeEnum.STRING;
        }
        if (StrUtil.containsAny(fieldType, "Integer")) {
            return FieldTypeEnum.INTEGER;
        }
        if (StrUtil.containsAny(fieldType, "Long")) {
            return FieldTypeEnum.LONG;
        }
        if (StrUtil.containsAny(fieldType, "DateTime")) {
            return FieldTypeEnum.DATE_TIME;
        }
        if (StrUtil.containsAny(fieldType, "BigDecimal")) {
            return FieldTypeEnum.BIG_DECIMAL;
        }
        if (StrUtil.containsAny(fieldType, "Boolean")) {
            return FieldTypeEnum.BOOLEAN;
        }
        if (StrUtil.containsAny(fieldType, "TinyInt")) {
            return FieldTypeEnum.TINY_INT;
        }
        return null;
    }

    private void handleSheet(File file, XSSFSheet sheet) {
        log.info("handle Workbook name : {}, Sheet name : {}", file.getName(), sheet.getSheetName());
        Table table = new Table();
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            handleRow(table, sheet.getRow(i));
        }
        for (Table table0 : tables) {
            if (StrUtil.equalsIgnoreCase(table0.getDbName() + "." + table0.getName(), table.getDbName() + "." + table.getName())) {
                log.error("ignore file : {}, sheet name : {}, table : {}, table is existed", file.getName(), sheet.getSheetName(), table.getDbName() + "." + table.getName());
                return;
            }
        }
        tables.add(table);
    }

    private void handleWorkbook(File file) {
        try {
            if (!file.isFile()) {
                log.warn("file is not exist, absolute path : {}", file.getAbsolutePath());
                return;
            }
            log.info("handle Workbook name : {}", file.getName());
            try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    handleSheet(file, workbook.getSheetAt(i));
                }
            }
        } catch (IOException | InvalidFormatException e) {
            log.info("handleWorkbook {}, cause : ", file.getAbsolutePath(), e);
        }
    }

    private void startup() {
        log.info("startup simpleGenerateDir : {} at {}", simpleGenerateDir, DateUtil.now());
        File simpleGenerateFile = new File(simpleGenerateDir);
        if (simpleGenerateFile.isDirectory()) {
            File[] files = simpleGenerateFile.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.getName().startsWith("~$")) {
                    // 打开文件后临时文件
                    continue;
                }
                if (file.isFile() && file.getName().endsWith(".xlsx")) {
                    handleWorkbook(file);
                }
            }
        }

        if (tables.isEmpty()) {
            return;
        }

        // 生成脚本
        generateScripts.forEach(
                generatorScript -> generatorScript.doGenerateScript(simpleGenerateFile, tables)
        );
        // 生成代码
        generatorCodes.forEach(
                generatorCode -> generatorCode.doGenerateCode(namespace, simpleGenerateFile, tables)
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startup();
    }

}
