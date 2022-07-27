package com.yhy.jakit.simple.crypto.controller;

import com.yhy.jakit.simple.crypto.model.Student;
import com.yhy.jakit.simple.crypto.model.Subject;
import com.yhy.jakit.simple.support.model.Res;
import com.yhy.jakit.starter.crypto.annotation.Decrypt;
import com.yhy.jakit.starter.crypto.exec.CryptoExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created on 2021-03-30 15:24
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private CryptoExecutor executor;

    @GetMapping("/no")
    public Res no() {
        String encrypted = executor.encrypt("Test 1234");
        log.info(encrypted);
        String decrypted = executor.decrypt(encrypted);
        log.info(decrypted);

        return Res.success("就这样");
    }

    @GetMapping("/normal")
    public String normal(HttpServletRequest request, @Decrypt String text) {
        return "接收到参数 " + text;
    }

    @GetMapping("/getVO")
    public Student getVO(Student vo) {
        return vo;
    }

    @PostMapping("/postVO")
    public Student postVO(@RequestBody Student student) {
        log.info("{}", student);
        return student;
    }

    @PostMapping("/postForm")
    public Student postForm(Student student) {
        return student;
    }

    @GetMapping("/res")
    public Res res() {
        return Res.success(student());
    }

    private Student student() {
        Student student = new Student();
        student.setId(1L);
        student.setName("李万姬");
        student.setMobile("18818818818");
        student.setIdCard("200202202002202202");

        Subject subject = new Subject("sb-2020020021234", "语文");
        student.setSubject(subject);

        return student;
    }
}
