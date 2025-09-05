package com.module_service_insert.utils.functionUtils;

import java.text.Normalizer;

/**
 * @author Trọng Hướng
 */
public class NormalizeString {

    public static String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }
}
