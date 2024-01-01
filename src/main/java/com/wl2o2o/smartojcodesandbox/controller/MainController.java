package com.wl2o2o.smartojcodesandbox.controller;

import com.wl2o2o.smartojcodesandbox.JavaNativeCodeSandBox;
import com.wl2o2o.smartojcodesandbox.JavaNativeCodeSandBoxOld;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeRequest;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/23
 */
@RestController("/")
public class MainController {

    @Resource
    JavaNativeCodeSandBox javaNativeCodeSandBox;

    @GetMapping("/health")
    public String healthCheck() {
        return "ok";
    }

    /**
     * 暴露执行代码的接口
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        return javaNativeCodeSandBox.executeCode(executeCodeRequest);
    }

}
