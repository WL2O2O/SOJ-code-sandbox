import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 向服务器写文件（植入木马程序）
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/26
 */

public class RunFileError {
    public static void main(String[] args) throws IOException, InterruptedException {
        String property = System.getProperty("user.dir");
        String filePath = property + File.separator + "src/main/resources/木马程序.bat";
        Process process = Runtime.getRuntime().exec(filePath);
        process.waitFor();
        // 分批成块获取进程的正常输出
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // 逐行读取
        String runCompileOutputLine;
        while ((runCompileOutputLine = bufferedReader.readLine()) != null){
            System.out.println(runCompileOutputLine);
        }

        System.out.println("执行异常程序成功！");
    }
}
