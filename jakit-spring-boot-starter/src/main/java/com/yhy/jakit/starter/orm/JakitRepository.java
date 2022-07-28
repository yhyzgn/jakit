package com.yhy.jakit.starter.orm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 公用 JpaRepository 基类
 * <p>
 * Created on 2022-07-28 11:40
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@NoRepositoryBean
public interface JakitRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
