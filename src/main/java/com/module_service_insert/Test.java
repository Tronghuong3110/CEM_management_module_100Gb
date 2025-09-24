package com.module_service_insert;

import java.io.IOException;

/**
 * @author Trọng Hướng
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String command = "cmd.exe /c start "+"ping -t 8.8.8.8";
        Runtime rt = Runtime.getRuntime();
        rt.exec(command);
    }
}
