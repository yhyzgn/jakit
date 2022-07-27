package com.yhy.jakit.simple.crypto.component;

import com.yhy.jakit.simple.support.model.Res;
import com.yhy.jakit.starter.crypto.exception.CryptoException;
import com.yhy.jakit.starter.crypto.exception.UnSupportedCryptAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 2021-06-05 10:18
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class CryptoExceptionHandler {

    @ExceptionHandler(CryptoException.class)
    public Res exception(HttpServletRequest req, HttpServletResponse res, CryptoException e) {
        log.error("", e);
        return Res.failure(-1, e.getMessage());
    }

    @ExceptionHandler(UnSupportedCryptAlgorithmException.class)
    public Res exception(HttpServletRequest req, HttpServletResponse res, UnSupportedCryptAlgorithmException e) {
        log.error("", e);
        return Res.failure(-1, e.getMessage());
    }
}
