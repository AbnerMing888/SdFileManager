package com.abner.manager.controller;

import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping(path = "/")
    public String index() {
        return "forward:/index.html";
    }

}