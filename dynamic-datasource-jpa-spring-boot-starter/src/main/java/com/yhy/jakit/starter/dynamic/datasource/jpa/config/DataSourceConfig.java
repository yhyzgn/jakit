package com.yhy.jakit.starter.dynamic.datasource.jpa.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created on 2021-05-26 11:05
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceConfig implements Serializable {
    private String driver;
    private String url;
    private String username;
    private String password;
    private String dialect;
    private String physicalStrategy;
}
