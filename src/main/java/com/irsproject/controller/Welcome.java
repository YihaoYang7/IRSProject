package com.irsproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Welcome
{
    @RequestMapping("/hello")
    @ResponseBody
    public String welcomePage()
    {
        return "Hello";
    }
}
