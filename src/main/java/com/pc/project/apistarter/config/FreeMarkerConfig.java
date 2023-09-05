package com.pc.project.apistarter.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/08/06 16:36
 **/
@Slf4j
@org.springframework.context.annotation.Configuration
public class FreeMarkerConfig {

    private static freemarker.template.Configuration cfg;

    @Resource
    private ResourceLoader resourceLoader;

    @Bean
    public Configuration configuration() {
        try {
            SpringTemplateLoader templateLoader = new SpringTemplateLoader(resourceLoader, "classpath:templates");
            cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setTemplateLoader(templateLoader);
            // /www/wwwroot/object
//        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
//        cfg.setDirectoryForTemplateLoading(new File("/www/wwwroot/object/templates"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
        }
        catch (Exception e) {
            log.error("模板引擎加载失败", e);
            e.printStackTrace();
        }
        return cfg;
    }
}
