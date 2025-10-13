package com.module_service_insert;

import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Trọng Hướng
 */
public class Test {
    public static void main(String[] args) throws IOException {
        while(true) {
            try {
                Thread.sleep(5000);
                System.out.println("Hello World");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
