package com.springboot.anecdote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置文件属性配置类
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/12/29 20:32
 */
@ConfigurationProperties(prefix = "my.properties")
@Component
public class MyProperties {

    /**
     * 上传文件（图片）保存位置
     */
    private String fileUploadLocation;

    public String getFileUploadLocation() {
        return fileUploadLocation;
    }

    public void setFileUploadLocation(String fileUploadLocation) {
        this.fileUploadLocation = fileUploadLocation;
    }
}
