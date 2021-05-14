package com.springboot.anecdote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class FileLocationConfig
 * Description 配置文件路径相关信息；
 * Date 2020/12/29 20:32
 *
 * @author Sampert
 * @version 1.0
 */
@ConfigurationProperties(prefix = "file.location")
@Component
public class FileLocationConfig {

    /**
     * 上传文件保存位置
     */
    private String upload;

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }
}
