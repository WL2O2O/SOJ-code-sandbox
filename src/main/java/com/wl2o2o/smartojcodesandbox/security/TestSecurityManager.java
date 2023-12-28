package com.wl2o2o.smartojcodesandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/28
 */

public class TestSecurityManager {
    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());

        // List<String> strings = FileUtil.readLines("E:\\Exercise\\project\\smartoj-code-sandbox\\src\\main\\resources\\application.yml", Charset.defaultCharset());
        FileUtil.writeString("aaa", "1212231", Charset.defaultCharset());

    }
}
