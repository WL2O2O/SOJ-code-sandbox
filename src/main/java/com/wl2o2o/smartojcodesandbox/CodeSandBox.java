package com.wl2o2o.smartojcodesandbox;

import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeRequest;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 *
 * @author WL2O2O
 * @create 2023/12/16 16:20
 */
public interface CodeSandBox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) throws InterruptedException;
}
