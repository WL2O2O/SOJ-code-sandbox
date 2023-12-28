package com.wl2o2o.smartojcodesandbox.security;

import java.security.Permission;

/**
 * 默认禁用所有权限安全管理器
 *
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/28
 */

public class DefaultSecurityManager extends SecurityManager {

    // 检查所有权限
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException(perm.getActions() + "权限被禁用！");
    }
}
