
package com.ascude.multitenancy.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class BootstrapApplication extends SpringBootServletInitializer {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BootstrapApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        Environment env = configurableApplicationContext.getEnvironment();
        log.info("""
                        
                        ----------------------------------------------------------
                        Application is running! Access URLs:
                        Local:    http://localhost:{}
                        Doc:      http://localhost:{}/doc.html
                        ----------------------------------------------------------""",
                env.getProperty("server.port"),
                env.getProperty("server.port"));
    }
}
