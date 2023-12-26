package com.wl2o2o.smartojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author WL2O2O
 * @create 2023/12/16 16:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    private List<String> outputList;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    private JudgeInfo judgeInfo;
}
