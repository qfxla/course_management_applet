import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

/**
 * @author
 * @since 2018/12/13
 */
@SpringBootTest
public class CodeGenerator {

    @Test
    public void generator(){
        String projectPath = System.getProperty("user.dir");
        FastAutoGenerator.create("jdbc:mysql://sh-cynosdbmysql-grp-owlz7pwe.sql.tencentcdb.com:20162/zkcourse?serverTimezone=GMT%2B8", "root", "zkcourse8346@")
                .globalConfig(builder -> {
                    builder.author("DJT") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(projectPath+"/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.haotongxue"); // 设置父包名
                            //.moduleName("system") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, projectPath+"src/main/java/com/haotongxue/course_service/com.haotongxue.mapper/xml")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_course_plus") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_","p_") // 设置过滤表前缀
                    .entityBuilder()
                            .enableLombok()
                            .versionColumnName("version")
                            .logicDeleteColumnName("is_deleted")
                            .logicDeletePropertyName("isDeleted")
                            .idType(IdType.ASSIGN_ID)
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
                    .controllerBuilder()
                            .enableRestStyle();
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
