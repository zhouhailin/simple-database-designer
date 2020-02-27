package link.thingscloud.simple.database.designer.util;

import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * @author : zhouhailin
 */
public class CellUtil {

    public static int getColumnIndex(XSSFCell cell) {
        return cell.getColumnIndex();
    }

    public static int getRowIndex(XSSFCell cell) {
        return cell.getRowIndex();
    }

    public static String getIndex(XSSFCell cell) {
        return (cell.getRowIndex() + 1) + "" + ColEnum.values()[getColumnIndex(cell)];
    }

    static enum ColEnum {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Z, R, S, T
    }
}
