package com.bengi.amazon.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {


    private static final String AMAZON_USER_DATA_PATH = "data/amazon_user_info.txt";
    private static final String LOG_FILE_PATH = "test_log_amazon.txt";


    // Amazon için metot
    public static String[] getAmazonCredentials() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(AMAZON_USER_DATA_PATH)));
            log("Amazon kullanıcı bilgileri dosyadan okundu.");
            return content.trim().split(":");
        } catch (IOException e) {
            log("HATA: Amazon kullanıcı bilgileri dosyası okunamadı! Path: " + AMAZON_USER_DATA_PATH);
            throw new RuntimeException("Amazon kullanıcı bilgileri dosyası bulunamadı!", e);
        }
    }

    // log metodu
    public static void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logMessageWithTimestamp = timestamp + " - " + message;


        // Mesajı, dosyaya yazmadan önce konsola yaz
        System.out.println(logMessageWithTimestamp);


        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            // Dosyaya  da zaman damgalı mesajı yaz.
            bw.write(logMessageWithTimestamp);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Log dosyasına yazılamadı: " + e.getMessage());
        }
    }
}
