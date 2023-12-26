package com.chenmeng.project.controller;

import com.chenmeng.project.model.dto.FileDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 测试控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public String test(){
        return "Hello World!";
    }

    @PostMapping("/file")
    public String muTest(MultipartFile file){
        return "Hello World!";
    }

    @PostMapping("/file-dto")
    public String muDtoTest(FileDTO fileDTO){
        return "Hello World!";
    }
}
