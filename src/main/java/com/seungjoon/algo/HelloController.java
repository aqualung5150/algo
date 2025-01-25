package com.seungjoon.algo;

import com.seungjoon.algo.auth.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class HelloController {

    @ResponseBody
    @GetMapping("/")
    public String root() {
        return "root";
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

//    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
//    @PreAuthorize("hasAuthority('ROLE_USERNAME_UNSET')")
    @ResponseBody
    @GetMapping("/unset")
    public String unset() {
        log.info("unset");
        return "success";
    }

    @ResponseBody
    @GetMapping("/test-mem")
    public String member(@AuthenticationPrincipal PrincipalDetails member) {
        log.info("member");
        return String.valueOf(member.getId());
    }
}
