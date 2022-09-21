package com.github.npcdw.storeapi.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static String readResourceAsString(String filename) throws Exception {
        try (InputStream input = FileUtil.class.getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (input == null) {
                throw new Exception("read " + filename + " fail");
            }
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }

}
