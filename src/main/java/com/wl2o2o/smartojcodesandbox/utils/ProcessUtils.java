package com.wl2o2o.smartojcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.wl2o2o.smartojcodesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/25
 */

public class ProcessUtils {

    /**
     * 执行进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch  = new StopWatch();
            stopWatch.start();
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);

            if (exitValue == 0) {
                System.out.println(opName + "成功！");
                // 成块获取控制台输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                String compileOutputLine;
                // 逐行读取输出
                while ((compileOutputLine = bufferedReader.readLine()) != null ) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessgae(StringUtils.join(outputStrList, "/n"));
            } else {
                System.out.println(opName + "失败，错误码：" + exitValue);
                // 分批成块获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                String compileOutputLine;
                // 逐行读取输出
                while ((compileOutputLine = bufferedReader.readLine()) != null ) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessgae(StringUtils.join(outputStrList, "/n"));

                // 分批成块获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> errorOutputStrList = new ArrayList<>();
                String errorCompileOutputLine;
                // 逐行读取输出
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null ) {
                    errorOutputStrList.add(errorCompileOutputLine);
                }
                executeMessage.setMessgae(StringUtils.join(errorOutputStrList, "/n"));
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runInterProcessAndGetMessage(Process runProcess, String opName, String args) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            // 向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            String[] split = args.split(" ");
            outputStreamWriter.write(StrUtil.join("\n", (Object) split) + "\n");
            // 相当于回车
            outputStreamWriter.flush();

            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);

            // 成块获取控制台输出
            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            String output;
            // 逐行读取输出
            while ((output = bufferedReader.readLine()) != null ) {
                compileOutputStringBuilder.append(output);
            }
            executeMessage.setMessgae(compileOutputStringBuilder.toString());


            // 资源回收
            outputStream.close();
            outputStreamWriter.close();
            inputStream.close();
            runProcess.destroy();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}
