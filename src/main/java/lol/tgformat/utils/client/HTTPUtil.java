package lol.tgformat.utils.client;

import lombok.SneakyThrows;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @Author LangYa
 * @Since 2024/06/28 上午10:34
 */
@StringEncryption
public class HTTPUtil {
    private static final int MAX_RETRY_COUNT = 3;

    @SneakyThrows
    public static void download(String fileUrl, File outputFile) {
        int retryCount = 0;
        boolean success = false;

        while (retryCount < MAX_RETRY_COUNT && !success) {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setConnectTimeout(5000);  // 设置连接超时
                httpConn.setReadTimeout(5000);     // 设置读取超时

                // 模拟谷歌浏览器的User-Agent
                httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

                int responseCode = httpConn.getResponseCode();

                // 检查HTTP响应码是否正常
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 创建文件夹和文件
                    File folder = new File(outputFile.getParent());
                    folder.mkdirs();
                    File newfile = new File(outputFile.getPath());
                    newfile.createNewFile();

                    // 打开一个输入流用于从服务器获取数据
                    InputStream inputStream = httpConn.getInputStream();

                    // 打开一个输出流用于将数据写入本地文件
                    FileOutputStream outputStream = new FileOutputStream(outputFile);

                    int bytesRead;
                    byte[] buffer = new byte[4096];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();
                    success = true;
                } else {
                    System.out.println("Download Error：" + responseCode);
                }
                httpConn.disconnect();
            } catch (SocketException | UnknownHostException e) {
                retryCount++;
                System.out.println("Connection reset, retrying... (" + retryCount + "/" + MAX_RETRY_COUNT + ")");
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw e;
                }
                Thread.sleep(2000); // 等待2秒后重试
            } catch (IOException e) {
                retryCount++;
                System.out.println("IOException occurred, retrying... (" + retryCount + "/" + MAX_RETRY_COUNT + ")");
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw e;
                }
                Thread.sleep(2000); // 等待2秒后重试
            }
        }
    }
}
