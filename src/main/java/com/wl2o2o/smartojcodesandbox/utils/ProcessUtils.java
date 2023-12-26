package com.wl2o2o.smartojcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.wl2o2o.smartojcodesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.*;

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
                StringBuilder compileOutputStringBuilder = new StringBuilder();
                String output;
                // 逐行读取输出
                while ((output = bufferedReader.readLine()) != null ) {
                    compileOutputStringBuilder.append(output);
                }
                executeMessage.setMessgae(compileOutputStringBuilder.toString());
            } else {
                System.out.println(opName + "失败：" + exitValue);
                // 成块获取控制台输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringBuilder errorOutputStringBuilder = new StringBuilder();
                String output;
                // 逐行读取输出
                while ((output = bufferedReader.readLine()) != null ) {
                    errorOutputStringBuilder.append(output);
                }
                executeMessage.setErrorMessage(errorOutputStringBuilder.toString());
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
