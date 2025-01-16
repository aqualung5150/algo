package com.seungjoon.algo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class HelloController {

    @GetMapping
    public String hello() {
        return "hello";
    }

//    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    @PreAuthorize("hasAuthority('ROLE_USERNAME_UNSET')")
    @ResponseBody
    @GetMapping("/test-auth")
    public String testAuth() {
        log.info("testAuth");
        return "success";
    }
}
