package com.chenmeng.project.common;

import lombok.Data;

import java.io.Serializable;


/**
 * 删除请求体
 *
 * @author chenmeng
 * @date 2023/06/19
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}