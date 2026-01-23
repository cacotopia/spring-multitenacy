package com.ascude.multitenancy.mybatis;

import com.baomidou.mybatisplus.annotation.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * 实体类扫描器
 * 负责扫描项目中所有的实体类并注册到EntityCache
 */
public class EntityScanner {

    private static final Logger logger = LoggerFactory.getLogger(EntityScanner.class);

    private final EntityCache entityCache;

    public EntityScanner(EntityCache entityCache) {
        this.entityCache = entityCache;
    }

    /**
     * 扫描指定包路径下的所有实体类
     *
     * @param basePackages 基础包路径数组
     */
    public void scanAndRegister(String... basePackages) {
        if (basePackages == null || basePackages.length == 0) {
            logger.warn("No base packages specified for entity scanning");
            return;
        }

        ClassPathScanningCandidateComponentProvider scanner = createScanner();

        for (String basePackage : basePackages) {
            logger.info("Scanning entities in package: {}", basePackage);
            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);

            for (BeanDefinition candidate : candidates) {
                try {
                    Class<?> entityClass = ClassUtils.forName(
                            candidate.getBeanClassName(),
                            ClassUtils.getDefaultClassLoader());

                    // 注册实体到缓存
                    entityCache.registerEntity(entityClass);

                } catch (ClassNotFoundException e) {
                    logger.error("Failed to load entity class: {}", candidate.getBeanClassName(), e);
                }
            }
        }

        logger.info("Entity scanning completed. Registered {} entities",
                entityCache.getAllRegisteredEntities().size());
    }

    /**
     * 创建扫描器，配置需要扫描的注解类型
     */
    private ClassPathScanningCandidateComponentProvider createScanner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        // 扫描带有 @TableName 注解的类（MyBatis-Plus实体）
        scanner.addIncludeFilter(new AnnotationTypeFilter(TableName.class));

        // 也可以扫描其他ORM的实体注解
        // scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class)); // JPA

        return scanner;
    }
}
