package com.bengi.amazon.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    // TestNG, bir test metodunu çalıştırmaya başladığında bu metodu çağırır.
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("===== TEST BAŞLADI: " + result.getMethod().getMethodName() + " =====");
    }

    // Test metodu başarıyla tamamlandığında bu metodu çağırır.
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("===== TEST BAŞARILI: " + result.getMethod().getMethodName() + " =====");
    }

    // Test metodu başarısız olduğunda bu metodu çağırır.
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("!!!!! TEST BAŞARISIZ: " + result.getMethod().getMethodName() + " !!!!!");
        // Hatanın ne olduğunu da yazdıralım.
        System.out.println("Hata Sebebi: " + result.getThrowable());
    }

    // Test metodu atlandığında bu metodu çağırır.
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("===== TEST ATLANDI: " + result.getMethod().getMethodName() + " =====");
    }

    // Aşağıdaki metodları şimdilik boş bırakabiliriz ama arayüz gereği bulunmaları gerekir.
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("### TEST SÜİTİ BAŞLATILIYOR ###");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("### TEST SÜİTİ TAMAMLANDI ###");
    }
}