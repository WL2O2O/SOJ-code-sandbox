import java.util.ArrayList;
import java.util.List;
/**
 * 无限占用内存空间（浪费系统内存）
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/26
 */

public class Main {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while (true) {
            bytes.add(new byte[1000]);
        }
    }
}