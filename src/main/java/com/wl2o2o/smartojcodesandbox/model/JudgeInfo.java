package com.wl2o2o.smartojcodesandbox.model;

import lombok.Data;

/**
 * 判题信息
 * @author WL2O2O
 * @create 2023/11/30 15:48
 */
@Data
public class JudgeInfo {

    /**
     * 题目信息
     */
    private String message;

    /**
     * 消耗时间（ms）
     */
    private Long time;

    /**
     * 消耗内存（kb）
     */
    private Long memory;

}
