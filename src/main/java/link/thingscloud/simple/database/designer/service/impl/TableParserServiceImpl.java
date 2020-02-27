package link.thingscloud.simple.database.designer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import link.thingscloud.simple.database.designer.domain.Column;
import link.thingscloud.simple.database.designer.domain.FieldTypeEnum;
import link.thingscloud.simple.database.designer.domain.Table;
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
    @Autowired
    private List<GeneratorScript> generateScripts;

    private final List<Table> tables = new ArrayList<>(254);

    private void handleRow(Table table, XSSFRow row) {
        XSSFCell cell = row.getCell(1);
        if (cell == null) {
            return;
        }
        String cellValue = cell.toString();
        if (StrUtil.isBlank(cellValue)) {
            return;
        }
        if (StrUtil.equals(cellValue, "#")) {
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
            log.info("charset[{}] : {}, database name[{}] : {}, table name[{}] : {}, comment[{}] : {}",
                    CellUtil.getIndex(charsetCell), table.getCharset(),
                    CellUtil.getIndex(databaseCell), table.getDbName(),
                    CellUtil.getIndex(tableNameCell), table.getName(),
                    CellUtil.getIndex(tableCommentCell), table.getComment());
            return;
        }
        if (!NumberUtil.isNumber(cellValue)) {
            return;
        }
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
            return;
        }
        FieldTypeEnum fieldType = getFieldType(typeCell.toString());
        if (fieldType == null) {
            log.error("parse field type failed, index {} : {}", CellUtil.getIndex(typeCell), typeCell.toString());
            throw new RuntimeException("fieldType is not valid : " + CellUtil.getIndex(typeCell));
        }
        table.getColumns().add(new Column()
                .setId(idCell.toString())
                .setName(nameCell.toString())
                .setType(fieldType)
                .setLength(NumberUtil.parseInt(lengthCell.toString()))
                .setScale(NumberUtil.parseInt(decimalCell.toString()))
                .setNullable(StrUtil.equals(nullableCell.toString(), "否"))
                .setDefaultValue(defaultValueCell.toString())
                .setPrimaryKey(primaryKeyCell.toString())
                .setComment(commentCell.toString())
        );
        log.info("  序号[{}] : {}, 字段名[{}] : {}, 类型[{}] : {}, 长度[{}] : {}, 小数点[{}] : {}, 允许NULL[{}] : {}, 默认值（空字符串）[{}] : {}, 主键[{}] : {}, 注释[{}] : {}",
                CellUtil.getIndex(idCell), idCell,
                CellUtil.getIndex(nameCell), nameCell,
                CellUtil.getIndex(typeCell), typeCell,
                CellUtil.getIndex(lengthCell), lengthCell,
                CellUtil.getIndex(decimalCell), decimalCell,
                CellUtil.getIndex(nullableCell), nullableCell,
                CellUtil.getIndex(defaultValueCell), defaultValueCell,
                CellUtil.getIndex(primaryKeyCell), primaryKeyCell,
                CellUtil.getIndex(commentCell), commentCell
        );
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

    private void handleSheet(XSSFSheet sheet) {
        log.info("handleSheet name : {}", sheet.getSheetName());
        Table table = new Table();
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            handleRow(table, sheet.getRow(i));
        }
        tables.add(table);
    }

    private void handleWorkbook(File file) {
        try {
            if (!file.isFile()) {
                log.warn("file is not exist, absolute path : {}", file.getAbsolutePath());
                return;
            }
            log.info("handleWorkbook name : {}", file.getName());
            try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    handleSheet(workbook.getSheetAt(i));
                }
            }
        } catch (IOException | InvalidFormatException e) {
            log.info("handleWorkbook {}, cause : ", file.getAbsolutePath(), e);
        }
    }

    @Override
    public void startup() {
        log.info("startup simpleGenerateDir : {} at {}", simpleGenerateDir, DateUtil.now());
        File simpleGenerateFile = new File(simpleGenerateDir);
        if (simpleGenerateFile.isDirectory()) {
            File[] files = simpleGenerateFile.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file != null && file.isFile() && file.getName().endsWith(".xlsx")) {
                    handleWorkbook(file);
                }
            }
        }

        if (tables.isEmpty()) {
            return;
        }
        generateScripts.forEach(
                generatorScript -> generatorScript.doGenerateScript(simpleGenerateFile, tables)
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startup();
    }

}
