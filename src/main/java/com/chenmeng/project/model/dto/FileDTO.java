package com.chenmeng.project.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件DTO
 *
 * @author 沉梦听雨
 **/
@Data
public class FileDTO {

    private String fileName;

    private MultipartFile file;
}
