package top.easyboot.springboot.operate.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.easyboot.springboot.operate.property.RestfulApiOperateProperties;


@Configuration
@EnableConfigurationProperties(RestfulApiOperateProperties.class)
public class OperateConfigurationProperties {
}
