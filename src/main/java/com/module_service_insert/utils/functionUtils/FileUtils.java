package com.module_service_insert.utils.functionUtils;

import com.module_service_insert.constant.VariableCommon;
import org.json.JSONObject;
import ucar.nc2.Variable;

import javax.swing.*;
import java.io.*;

/**
 * @author Trọng Hướng
 */
public class FileUtils {
    private static JSONObject arguments; // key là tên hiển thị
    private static JSONObject argumentsWithKeyIsFlags;

    public static JSONObject readFileArgumentsConfig() {
        // lưu dạng json:
        /*
        * {
        *   display_name: "",
        *   command_name: "",
        *   data_type: ""
        * }
        * */
        File argumentsFile = new File(VariableCommon.ARGUMENTS_FILE_PATH);
        if (!argumentsFile.exists()) {
            AlertUtils.showAlert("Lỗi", "File cấu hình arguments không tồn tại.", "ERROR");
            return new JSONObject();
        }
        long lastModifiedTime = argumentsFile.lastModified();
        // nếu file bị chỉnh sửa thì đọc lại file
        if(VariableCommon.LAST_MODIFIED_TIME_ARGUMENT_CONFIG == null || VariableCommon.LAST_MODIFIED_TIME_ARGUMENT_CONFIG != lastModifiedTime) {
            try {
                arguments = new JSONObject();
                BufferedReader reader = new BufferedReader(new FileReader(argumentsFile));
                String line;
                while((line = reader.readLine()) != null) {
                    String[] part = line.split(":");
                    if(line.startsWith("#") || part.length < 3) continue;
                    JSONObject argument = new JSONObject();
                    argument.put("display_name", part[0]);
                    argument.put("command_name", part[1]);
                    argument.put("data_type", part[2]);
                    arguments.put(part[0], argument);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            VariableCommon.LAST_MODIFIED_TIME_ARGUMENT_CONFIG = lastModifiedTime;
        }
        return arguments;
    }

    public static JSONObject readFileArgumentsConfigWithKeyIsFlags() {
        // lưu dạng json:
        /*
         * {
         *   display_name: "",
         *   command_name: "",
         *   data_type: ""
         * }
         * */
        File argumentsFile = new File(VariableCommon.ARGUMENTS_FILE_PATH);
        if (!argumentsFile.exists()) {
            AlertUtils.showAlert("Lỗi", "File cấu hình arguments không tồn tại.", "ERROR");
            return new JSONObject();
        }
        // nếu file bị chỉnh sửa thì đọc lại file
//        if(VariableCommon.LAST_MODIFIED_TIME_ARGUMENT_CONFIG == null || VariableCommon.LAST_MODIFIED_TIME_ARGUMENT_CONFIG != lastModifiedTime) {
        try {
            argumentsWithKeyIsFlags = new JSONObject();
            BufferedReader reader = new BufferedReader(new FileReader(argumentsFile));
            String line;
            while((line = reader.readLine()) != null) {
                String[] part = line.split(":");
                if(line.startsWith("#") || part.length < 3) continue;
                JSONObject argument = new JSONObject();
                argument.put("display_name", part[0]);
                argument.put("command_name", part[1]);
                argument.put("data_type", part[2]);
                argumentsWithKeyIsFlags.put(part[1], argument);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        }
        return argumentsWithKeyIsFlags;
    }

}
