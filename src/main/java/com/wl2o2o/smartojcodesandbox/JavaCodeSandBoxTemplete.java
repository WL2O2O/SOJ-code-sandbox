package com.wl2o2o.smartojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeRequest;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeResponse;
import com.wl2o2o.smartojcodesandbox.model.ExecuteMessage;
import com.wl2o2o.smartojcodesandbox.model.JudgeInfo;
import com.wl2o2o.smartojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 代码沙箱模板方法
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/31
 */
@Slf4j
public abstract class JavaCodeSandBoxTemplete implements CodeSandBox{

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final Long TIME_OUT = 5000L;

//    private static final String SECURITY_MANAGER_PATH = "E:\\Exercise\\project\\smartoj-code-sandbox\\src\\main\\resources\\security";
//
//    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";


    /**
     * 1. 把用户文件保存为文件
     * @param code
     * @return
     */
    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalPathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断目录是否存在
        if (!FileUtil.exist(globalPathName)) FileUtil.mkdir(globalPathName);
        // 把用户的代码隔离存放
        String userCodeParentPath = globalPathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 2. 编译代码
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeCompileMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            if (executeCompileMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeCompileMessage;
        } catch (IOException e) {
            // return getErrorResponce(e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 3. 执行文件，获得执行结果列表
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        // TODO: Bug 记录，此处如果再次使用UUID.randomUUID();的话，会再次生成一个随机码，
        //  与第一步保存文件的随机码不同，导致找不到文件
        // String userDir = System.getProperty("user.dir");
        // String globalPathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // String userCodeParentPath = globalPathName + File.separator + UUID.randomUUID();
        // 使用 userCodeFile 向上进行寻找 UUID 路径
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        System.out.println(userCodeParentPath);
        ArrayList<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String input : inputList) {
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=utf8 -cp %s;%s -Djava.security.manager=%s Main %s", userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, input);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=utf8 -cp %s Main %s", userCodeParentPath, input);

            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                // TODO 直接设置了固定的时间，可能不太完美，可以先进行判断这个进程是否正常完成，然后在进行销毁
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断！");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (IOException e) {
                // return getErrorResponce(e);
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }

    /**
     * 4. 从控制台，收集整理输出结果
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outpuList = new ArrayList<>();
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            // 如果有错误输出
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 定义一个枚举值   执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(time, maxTime);
            }
            outpuList.add(executeMessage.getMessgae());
        }
        if (outpuList.size() == executeMessageList.size()) {
            // 程序运行正常
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outpuList);
        JudgeInfo judgeInfo = new JudgeInfo();
        // 判题模块负责  judgeInfo.setMessage();
        judgeInfo.setTime(maxTime);
        // 要借助第三方库才可以实现
        // judgeInfo.setMemory();
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /**
     * 5. 删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del? "成功" : "失败"));
            return del;
        }
        return true;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        // 1. 把用户文件保存为文件
        File userCodeFile = saveCodeToFile(code);

        // 2. 编译代码，得到 class 文件
        ExecuteMessage executeCompileMessage = compileFile(userCodeFile);
        System.out.println(executeCompileMessage);

        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessages = runFile(userCodeFile, inputList);


        // 4. 从控制台，收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessages);


        // 5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }

        return outputResponse;
    }

    /**
     * 6. 错误处理，提升程序健壮性（获取错误响应）
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponce(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // TODO 设置ENUM ,表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }


}
