package com.module_service_insert.utils.functionUtils;

import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author Trọng Hướng
 */
public class ChooseLocationFile {
    public static File choosesFolderFile(String baseFileName) {
        // 1) Mở hộp thoại chọn nơi lưu + tên file
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn nơi lưu file xuất");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json")
        );
        fc.setInitialFileName(baseFileName + "_" + java.time.LocalDate.now() + ".json");

        // Lấy cửa sổ hiện hành làm owner an toàn
        javafx.stage.Window owner = null;
        for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
            if (w.isShowing()) { owner = w; break; }
        }

        File file = fc.showSaveDialog(owner);
        if (file == null) {
            return null;
        }

        // Đảm bảo có phần mở rộng .xlsx nếu người dùng không gõ
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".json")) {
            file = new File(path + ".json");
        }
        return file;
    }
}
