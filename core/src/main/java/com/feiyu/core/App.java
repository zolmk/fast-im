package com.feiyu.core;

import com.feiyu.core.communicate.MsgManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author zhufeifei 2023/9/8
 **/

public class App {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        MsgManager msgManager = null;
        try (InputStream inputStream = Files.newInputStream(Paths.get("./src/main/resources/application.properties"))) {
            properties.load(inputStream);

            msgManager = new MsgManager(properties);
            msgManager.start();
            Thread.sleep(200000);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (msgManager != null) {
                msgManager.close();
            }
        }


    }
}
