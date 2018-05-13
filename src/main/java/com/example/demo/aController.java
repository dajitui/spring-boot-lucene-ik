package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class aController {
    @RequestMapping("/a")
    public String a() {
        return "search";
    }
}
