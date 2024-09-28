package com.jpmc.midascore;

import org.springframework.stereotype.Component;
import org.apache.commons.io.IOUtils;


import java.io.InputStream;

@Component
public class FileLoader {
    public String[] loadStrings(String path) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(path);
            String fileText = IOUtils.toString(inputStream, "UTF-8");
            System.out.println("Loaded File Text: " + fileText);
            return fileText.split(System.lineSeparator());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
