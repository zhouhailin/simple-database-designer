package link.thingscloud.simple.database.designer.service.impl.code;

import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Test;

/**
 * @author : zhouhailin
 */
class MybatisPlusGeneratorCodeTest {

    @Test
    void doGenerateCode() {
//        FileUtil.del("d:/tmp/a");
//        FileUtil.writeUtf8String("todo", new File("d:/tmp/a/a/a/a"));
        System.out.println(StrUtil.toCamelCase("a_bc_d5_se"));
        System.out.println(StrUtil.upperFirst(StrUtil.toCamelCase("a_bc_d5_se")));

    }
}