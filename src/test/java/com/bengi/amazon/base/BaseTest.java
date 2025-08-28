package com.bengi.amazon.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    public WebDriver driver;
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        //Bot tespitini atlamak için özel ayarlar
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");


        // Chrome'un tercihlerini (preferences) değiştirmek için bir harita (Map) oluşturuyoruz.
        Map<String, Object> prefs = new HashMap<>();


        prefs.put("credentials_enable_service", false);// Şifre yöneticisini ve kimlik bilgisi kaydetme servisini kapatıyoruz.
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);//Kredi kartı kaydetme özelliğini ve şifre yöneticisini kapatın

        options.setExperimentalOption("prefs", prefs); // Bu tercihleri 'options' nesnemize ekliyoruz.



        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Bekleme süresi 30 saniyeye
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}