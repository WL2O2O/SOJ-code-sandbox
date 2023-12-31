package com.wl2o2o.smartojcodesandbox.model;

import lombok.Data;

/**
 * 执行进程信息
 *
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/25
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String messgae;

    private String errorMessage;

    private Long time;

    private Long memory;

}
