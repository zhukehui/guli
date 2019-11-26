package com.atguigu.guli.service.oss.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author eternity
 * @create 2019-11-25 20:43
 */
@Data
@Component
//注意prefix要写到最后一个 "." 符号之前
@ConfigurationProperties(prefix="aliyun.oss.file")
public class OssProperties {
    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketName;
}
