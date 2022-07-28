package com.yhy.jakit.starter.logging.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 具体操作
 * <p>
 * Created on 2020-11-02 17:51
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class Operation implements Serializable {
    private static final long serialVersionUID = -8170448359517116443L;

    private Type type;
    private Object before;
    private Object after;
    private Boolean transactional;
    private Long begin;
    private Long finish;
}
