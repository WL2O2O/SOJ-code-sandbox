package com.wl2o2o.smartojcodesandbox.security;

import java.security.Permission;

/**
 * 我的安全管理器
 *
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/28
 */

public class MySecurityManager extends SecurityManager {

    // 检查所有的权限
    @Override
    public void checkPermission(Permission perm) {
        // super.checkPermission(perm);
        System.out.println(perm);
    }

    // 检查执行权限
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("exec 权限被禁用！" + cmd);
    }

    @Override
    public void checkRead(String file) {
        // System.out.println(file);
        // if (file.contains("E:\\Exercise\\project\\smartoj-code-sandbox")) {
        //     return;
        // }
        // throw new SecurityException("read 权限被禁用！" + file);
    }

    @Override
    public void checkWrite(String file) {
        // throw new SecurityException("write 权限被禁用！" + file);
    }

    // 检查删除操作权限
    @Override
    public void checkDelete(String file) {
        // throw new SecurityException("delete 权限被禁用！" + file);
    }

    // 检查程序可链接网络权限
    @Override
    public void checkConnect(String host, int port) {
        // throw new SecurityException("connect 权限被禁用！" + host + ":" + port);
    }
}
