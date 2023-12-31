package com.wl2o2o.smartojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeRequest;
import com.wl2o2o.smartojcodesandbox.model.ExecuteCodeResponse;
import com.wl2o2o.smartojcodesandbox.model.ExecuteMessage;
import com.wl2o2o.smartojcodesandbox.model.JudgeInfo;
import com.wl2o2o.smartojcodesandbox.utils.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/25
 */

public class JavaNativeCodeSandBox implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final Long TIME_OUT = 5000L;

//    private static final String SECURITY_MANAGER_PATH = "E:\\Exercise\\project\\smartoj-code-sandbox\\src\\main\\resources\\security";
//
//    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";

    private static final List<String> blacklist = Arrays.asList("File", "exec");

    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blacklist);
    }

    public static void main(String[] args) {
        JavaNativeCodeSandBox javaNativeCodeSandBox = new JavaNativeCodeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
         String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//         String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        // String code = ResourceUtil.readStr("testCode/unsafeCode/SleepError.java", StandardCharsets.UTF_8);
        // String code = ResourceUtil.readStr("testCode/unsafeCode/MemoryError.java", StandardCharsets.UTF_8);
        // String code = ResourceUtil.readStr("testCode/unsafeCode/ReadFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/WriteFileError.java", StandardCharsets.UTF_8);
        // String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);

        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);

    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        // 实际上，我们只需要对如下子程序进行安全管理，所以采用下行命令进行管理
        // String runCmd = String.format("java -Xmx256m -Dfile.encoding=utf8 -cp %s;%s -Djava.security.manager=%s Main %s", userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, input);
//         System.setSecurityManager(new MySecurityManager());

        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词汇：" + foundWord.getFoundWord());
            return null;
        }

        // 1. 把用户文件保存为文件
        String userDir = System.getProperty("user.dir");
        String globalPathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        // 判断目录是否存在
        if (!FileUtil.exist(globalPathName)) {
            FileUtil.mkdir(globalPathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalPathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);


        // 2. 编译代码，得到 class 文件
        String compileCmd = String.format("javac -encoding utf8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeCompileMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeCompileMessage);
        } catch (IOException e) {
            return getErrorResponce(e);
        }
        // 3. 执行代码，得到输出结果
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
                return getErrorResponce(e);
            }
        }

        // 4. 从控制台，收集整理输出结果
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
        
        // 5. 文件清理
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del? "成功" : "失败"));
        }
        return executeCodeResponse;
    }

    // 6. 错误处理，提升程序健壮性（封装一个错误返回方法:）
    /**i
     * 获取错误响应
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
