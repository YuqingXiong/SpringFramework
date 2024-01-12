package com.rainsun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BookController {
    @RequestMapping("book/save")
    @ResponseBody
    public String save(){
        System.out.println("book save ...");
        return "{'module':'book save'}";
    }

    @RequestMapping("book/delete")
    @ResponseBody
    public String delete(){
        System.out.println("book delete ...");
        return "{'module':'book delete'}";
    }
}
