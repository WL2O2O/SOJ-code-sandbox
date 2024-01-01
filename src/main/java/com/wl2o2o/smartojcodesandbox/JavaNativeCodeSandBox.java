package com.wl2o2o.smartojcodesandbox;

import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeRequest;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * Java 原生代码沙箱实现（直接复用模板方法）
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/31
 */
@Component
public class JavaNativeCodeSandBox extends JavaCodeSandBoxTemplete {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
