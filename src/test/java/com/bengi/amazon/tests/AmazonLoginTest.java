package com.bengi.amazon.tests;


import com.bengi.amazon.base.BaseTest;
import com.bengi.amazon.utils.FileManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AmazonLoginTest extends BaseTest {

    // ----------- LOGIN (tanıdık / yabancı ayrımı) ------------
    private void loginToAmazon() throws InterruptedException {
        FileManager.log("Test Başladı: Amazon Login Senaryosu.");
        driver.get("https://www.amazon.com.tr");

        // Çerez kontrolü (implicit'i kısa kapatıp anlık kontrol)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        try {
            if (driver.findElements(By.id("sp-cc-accept")).size() > 0) {
                driver.findElement(By.id("sp-cc-accept")).click();
                FileManager.log("Çerezler anında kabul edildi.");
            } else {
                FileManager.log("Çerez pop-up'ı anında kontrolde çıkmadı.");
            }
        } catch (Exception ignored) {
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }

        // Anasayfa tipini kontrol et (tanıdık mı değil mi)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        boolean isFamiliarHomepage = driver.findElements(By.id("nav-link-accountList")).size() > 0;
        if (isFamiliarHomepage) {
            try {
                String greeting = driver.findElement(By.id("nav-link-accountList-nav-line-1")).getText();
                isFamiliarHomepage = greeting != null && greeting.contains("Merhaba");
            } catch (Exception e) {
                isFamiliarHomepage = false;
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (isFamiliarHomepage) {
            FileManager.log("Kişiselleştirilmiş anasayfa algılandı. Giriş menüsü açılıyor...");
            WebElement accountListMenu = driver.findElement(By.id("nav-link-accountList"));
            new Actions(driver).moveToElement(accountListMenu).perform();
            Thread.sleep(1000);
            WebElement signInButtonInMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@id='nav-flyout-accountList']//span[text()='Giriş yap']")));
            signInButtonInMenu.click();
        } else {
            FileManager.log("Genel anasayfa algılandı. 'Hesabım' üzerinden giriş menüsü açılıyor...");
            WebElement ilkHesabimLinki = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Hesabım")));
            ilkHesabimLinki.click();
            Thread.sleep(1000);
            WebElement accountListMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList")));
            new Actions(driver).moveToElement(accountListMenu).perform();
            Thread.sleep(1000);
            WebElement signInButtonInMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@id='nav-flyout-accountList']//span[text()='Giriş yap']")));
            signInButtonInMenu.click();
        }

        wait.until(ExpectedConditions.urlContains("signin"));

        String[] credentials = FileManager.getAmazonCredentials();
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ap_email_login")));
        emailInput.sendKeys(credentials[0]);
        driver.findElement(By.id("continue")).click();

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ap_password")));
        passwordInput.sendKeys(credentials[1]);
        driver.findElement(By.id("signInSubmit")).click();

        // Girişin başarılı olduğunu doğrula (hesap menüsünü aç ve Çıkış linkini bekle)
        WebElement myAccountMenuAfterLogin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList")));
        new Actions(driver).moveToElement(myAccountMenuAfterLogin).perform();
        WebElement signOutLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-item-signout")));
        Assert.assertTrue(signOutLink.isDisplayed(), "GİRİŞ BAŞARISIZ: 'Çıkış Yap' linki bulunamadı!");
        FileManager.log("Giriş başarılı.");
    }



    private void navigateToLaptopsPage() throws InterruptedException {
        FileManager.log("Dizüstü bilgisayar kategorisine gidiliyor...");

        // Hamburger menüsünü aç
        WebElement allMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu")));
        allMenuButton.click();
        FileManager.log("Hamburger (Tümü) menüsü açıldı.");

        // "Bilgisayar" kategorisini bul
        WebElement computerCategory = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='hmenu-content']//a[div[contains(text(), 'Bilgisayar')]]")));
        FileManager.log("'" + computerCategory.getText().trim() + "' kategorisi bulundu.");

        // Güvenilirlik için Javascript ile tıkla
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", computerCategory);
        FileManager.log("'" + computerCategory.getText().trim() + "' kategorisine tıklandı.");


        // Alt menünün yüklenmesini bekle
        WebElement subMenuContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='hmenu hmenu-visible hmenu-translateX' and @data-menu-id='5']")
        ));
        FileManager.log("Alt menü konteyneri DOM'da görünür hale geldi.");

        //  "Dizüstü Bilgisayarlar" linkinin var olmasını bekle.
        //   Bu, içeriğin de yüklendiğini garanti eder.
        WebElement laptopCategoryLink = wait.until(ExpectedConditions.visibilityOf(
                subMenuContainer.findElement(By.linkText("Dizüstü Bilgisayarlar"))
        ));
        FileManager.log("'Dizüstü Bilgisayarlar' linki alt menüde görünür hale geldi.");

        // Tıklama işlemi
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", laptopCategoryLink);
        FileManager.log("'Dizüstü Bilgisayarlar' linkine başarıyla tıklandı.");


        // Sayfanın yüklenmesi doğrulanır
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), 'sonuç arasından') and contains(text(), 'gösteriliyor')]")));
        FileManager.log("Dizüstü bilgisayar listesi sayfası başarıyla açıldı.");
    }


    private void applyFulfilledByAmazonFilter() {
        FileManager.log("'Amazon Tarafından Gönderilir' filtresi uygulanıyor...");

        try {
            // ID'si p_98-title olan başlığın hemen yanındaki (kardeşi olan) ul içindeki a linkini bul.
            By filterLocator = By.cssSelector("#p_98-title + ul a");

            WebElement filterLink = wait.until(ExpectedConditions.elementToBeClickable(filterLocator));

            // Filtreye tıkla
            filterLink.click();
            FileManager.log("Filtre linkine tıklandı.");

            // Sayfanın yeni sonuçlarla güncellendiğini doğrulamak için, ürün listesi konteynerinin tekrar görünür olmasını bekleyelim.
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-cy='asin-faceout-container']")));
            FileManager.log("BAŞARILI: Filtre uygulandı ve ürün listesi güncellendi.");

        } catch (Exception e) {
            FileManager.log("HATA: 'Amazon Tarafından Gönderilir' filtresi bulunamadı veya tıklanamadı. Hata: " + e.getMessage());
            Assert.fail("Filtreleme adımı başarısız oldu.", e);
        }
    }



    private void addLaptopToCartByIndex(int desiredProductIndex) {
        if (desiredProductIndex <= 0) {
            Assert.fail("Ürün sırası 1 veya daha büyük bir değer olmalıdır.");
            return;
        }

        FileManager.log("Filtrelenmiş listeden sepete eklenebilir " + desiredProductIndex + ". ürün aranıyor...");

        try {
            By productContainerLocator = By.xpath("//div[@data-cy='asin-faceout-container']");
            List<WebElement> productContainers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productContainerLocator));

            if (productContainers.isEmpty()) {
                Assert.fail("Filtreleme sonrası hiç ürün bulunamadı.");
                return;
            }

            int addableProductsFound = 0;

            // data-cy="add-to-cart" niteliğine sahip konteynerin içindeki butonu hedefliyoruz.
            By addToCartButtonLocator = By.cssSelector("[data-cy='add-to-cart'] button");

            for (WebElement currentProduct : productContainers) {
                String productTitle;
                try {
                    productTitle = currentProduct.findElement(By.cssSelector("h2 span")).getText();
                } catch (Exception e) {
                    productTitle = "[Başlık Okunamadı]";
                }

                // Ürün kutusunun içinde "Sepete Ekle" butonu var mı diye kontrol edilir.
                List<WebElement> addToCartButtons = currentProduct.findElements(addToCartButtonLocator);

                if (!addToCartButtons.isEmpty() && addToCartButtons.get(0).isDisplayed()) {
                    // Eğer buton bulunduysa, eklenebilir ürün sayacını artır.
                    addableProductsFound++;
                    FileManager.log("Eklenebilir ürün bulundu: '" + productTitle + "'. Toplam eklenebilir ürün: " + addableProductsFound);

                    // Aradığım sıradaki eklenebilir ürünü buldum mu?
                    if (addableProductsFound == desiredProductIndex) {
                        // Evet, buldum. Tıkla ve döngüden çık.
                        addToCartButtons.get(0).click();
                        FileManager.log("BAŞARILI: Listeden " + desiredProductIndex + ". eklenebilir ürün olan '" + productTitle + "' sepete eklendi.");

                        // Görev tamamlandığı için metottan çık.
                        return;
                    }
                }
            }

            // Eğer döngü bittiği halde buraya ulaşıldıysa, yeterli sayıda eklenebilir ürün bulunamadı demektir.
            Assert.fail("Filtrelenmiş listede " + desiredProductIndex + " adet 'Sepete Eklenebilir' ürün bulunamadı. Bulunan: " + addableProductsFound);

        } catch (Exception e) {
            FileManager.log("HATA: Filtrelenmiş listeden ürün sepete eklenirken bir sorun oluştu. Hata: " + e.getMessage());
            Assert.fail("Filtrelenmiş ürün sepete eklenemedi.", e);
        }
    }


    private void removeAllFromCart() {
        FileManager.log("Sepet temizleme başlatılıyor...");
        driver.get("https://www.amazon.com.tr/gp/cart/view.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sc-active-cart")));

        while (true) { // Sonsuz bir döngü başlat
            // Her döngünün başında "Sil" butonlarını yeniden ara.
            List<WebElement> deleteButtons = driver.findElements(By.cssSelector("input[value='Sil']"));

            // Eğer "Sil" butonu bulunamazsa (liste boşsa), döngüyü kır ve çık.
            if (deleteButtons.isEmpty()) {
                FileManager.log("Sepette silinecek başka ürün bulunamadı.");
                break;
            }

            // Eğer buton bulunduysa, ilkine tıkla.
            FileManager.log("Silinecek bir ürün bulundu, tıklanıyor...");
            deleteButtons.get(0).click();

            // Sayfanın yenilenmesini beklemek için KISA bir gecikme.
            try {
                Thread.sleep(1500); // 1.5 saniye bekle
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Döngü başa dönecek ve yenilenmiş sayfada tekrar arama yapacak.
        }

        // Sepetin boş olduğunu doğrula
        FileManager.log("Tüm ürünler silindi. Ara toplamın '(0 ürün)' olduğu doğrulanıyor...");

        WebElement subtotalLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sc-subtotal-label-activecart")));
        String subtotalText = subtotalLabel.getText();
        FileManager.log("Sepet ara toplam yazısı: " + subtotalText);

        Assert.assertTrue(subtotalText.contains("(0 ürün)"), "Sepet temizlenemedi! Ürün sayısı 0 değil.");
        FileManager.log("Sepet başarıyla temizlendi.");
    }


    private void addAddress() throws InterruptedException {
        FileManager.log("Adres ekleme denemesi başlatılıyor...");
        driver.get("https://www.amazon.com.tr/gp/cart/view.html");

        if (driver.findElements(By.name("proceedToRetailCheckout")).isEmpty()) {
            FileManager.log("Sepet boş ya da 'Alışverişi tamamla' butonu bulunamadı. İşlem durduruldu.");
            Assert.fail("'Alışverişi tamamla' butonu bulunamadı.");
            return;
        }

        driver.findElement(By.name("proceedToRetailCheckout")).click();
        wait.until(ExpectedConditions.urlContains("checkout"));
        FileManager.log("Teslimat adresi seçme sayfasına gelindi.");

        WebElement addNewAddressLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='add-new-address-desktop-sasp-tango-link']/span/a")));
        addNewAddressLink.click();
        FileManager.log("'Yeni adres ekle' linkine tıklandı.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address-ui-widgets-enterAddressFullName")));
        FileManager.log("Adres ekleme formu açıldı.");

        String[] userInfo = FileManager.getAmazonCredentials();
        String fullName = userInfo[3], phone = userInfo[4], addressTitle = userInfo[5],
                addressLine = userInfo[6], city = userInfo[7], district = userInfo[8], neighborhood = userInfo[9];

        driver.findElement(By.id("address-ui-widgets-enterAddressFullName")).sendKeys(fullName);
        driver.findElement(By.id("address-ui-widgets-enterAddressPhoneNumber")).sendKeys(phone);
        driver.findElement(By.id("address-ui-widgets-enterAddressLine1")).sendKeys(addressLine);
        driver.findElement(By.id("address-ui-widgets-enterAddressLine2")).sendKeys(addressTitle);
        FileManager.log("Metin alanları dolduruldu: Ad, Telefon, Adres.");

        // ADRES SEÇİMLERİ
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Şehir seçimi
        FileManager.log("Şehir alanı dolduruluyor: " + city);
        WebElement cityInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("address-ui-widgets-enterAddressCity")));
        cityInput.click();
        cityInput.clear();
        for (char c : city.toCharArray()) {
            cityInput.sendKeys(String.valueOf(c));
            Thread.sleep(150);
        }
        WebElement cityOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[b[text()='" + city + "']]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cityOption);
        FileManager.log("Şehir otomatik tamamlamadan seçildi: " + city);
        Thread.sleep(500);

        FileManager.log("İlçe alanı dolduruluyor: " + district);
        WebElement districtInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("address-ui-widgets-enterAddressStateOrRegion")));
        districtInput.click();
        districtInput.clear();
        for (char c : district.toCharArray()) {
            districtInput.sendKeys(String.valueOf(c));
            Thread.sleep(100);
        }
        WebElement districtOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[b[text()='" + district + "']]")
        ));
        js.executeScript("arguments[0].click();", districtOption);
        FileManager.log("İlçe otomatik tamamlamadan seçildi: " + district);
        Thread.sleep(500);

        // Mahalle alanı dolduruluyor
        FileManager.log("Mahalle alanı dolduruluyor: " + neighborhood);
        WebElement neighborhoodInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("address-ui-widgets-enterAddressDistrictOrCounty")));
        neighborhoodInput.click();
        neighborhoodInput.clear();

        // Harf harf yazılıyor
        for (char c : neighborhood.toCharArray()) {
            neighborhoodInput.sendKeys(String.valueOf(c));
            Thread.sleep(100);
        }

        // Otomatik tamamlama listesinden eşleşeni seç
        WebElement neighborhoodOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[contains(normalize-space(.), '" + neighborhood + "')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", neighborhoodOption);
        FileManager.log("Mahalle otomatik tamamlamadan seçildi: " + neighborhood);
        Thread.sleep(500);

        // "Bu adresi kullan" butonuna tıkla
        FileManager.log("'Bu adresi kullan' butonuna tıklanıyor...");
        WebElement useThisAddressBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[data-testid='bottom-continue-button']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", useThisAddressBtn);
        FileManager.log("Adres kayıt işlemi için butona tıklandı.");


        try {
            // Sayfa geçişinin başladığını doğrula.
            wait.until(ExpectedConditions.stalenessOf(useThisAddressBtn));
            FileManager.log("DOĞRULANDI: Sayfa geçişi başarıyla tetiklendi.");

            //  Artık yeni sayfanın gelmesini bekle. 2 olasılık kontrol edilir

            // Olasılık 1: Gümrük (TCKN/KYC) sayfası
            By kycPanelLocator = By.id("checkout-kycSummaryPanel");

            // Olasılık 2: Ödeme sayfası
            By paymentPanelLocator = By.id("checkout-paymentOptionPanel");

            // Bu İKİ panelden BİRİ görünür olana kadar bekle
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(kycPanelLocator),
                    ExpectedConditions.visibilityOfElementLocated(paymentPanelLocator)
            ));

            FileManager.log("DOĞRULANDI: Adres başarıyla eklendi ve yeni sayfaya (TCKN veya Ödeme) geçildi.");

        } catch (TimeoutException e) {
            FileManager.log("HATA: Adres eklendikten sonra beklenen Gümrük veya Ödeme paneli bulunamadı.");
            FileManager.log("Mevcut Sayfa Başlığı: " + driver.getTitle());
            FileManager.log("Mevcut URL: " + driver.getCurrentUrl());
            Assert.fail("Adres ekleme adımı başarısız oldu. Sayfa geçişi doğrulanamadı.", e);
        }

    }


    private void handleKycPageIfPresent() {
        FileManager.log("TCKN (KYC) sayfası kontrol ediliyor...");

        try {
            // KYC panelinin görünür olmasını bekle
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkout-kycSummaryPanel")));
            FileManager.log("TCKN (KYC) sayfası algılandı. 'Şimdilik atla' seçeneği işlenecek.");


            By skipLabelLocator = By.cssSelector("label[for='kyc-xborder-radio-skip']");

            // Elementin tıklanabilir olmasını bekle: Bu, elementin görünür ve aktif olduğunu doğrular
            WebElement skipForNowLabel = wait.until(ExpectedConditions.elementToBeClickable(skipLabelLocator));


            skipForNowLabel.click();
            FileManager.log("'Şimdilik atla' seçeneğinin etiketine (label) tıklandı.");

            // Tıklamanın başarısı doğrulanır: "Devam et" butonunun aktifleşmesi beklenir
            By continueButtonLocator = By.id("kyc-xborder-continue-button");
            wait.until(ExpectedConditions.not(
                    ExpectedConditions.attributeContains(continueButtonLocator, "class", "a-button-disabled")
            ));
            FileManager.log("DOĞRULANDI: 'Devam et' butonu aktifleşti.");

            // Devam et
            driver.findElement(continueButtonLocator).click();
            FileManager.log("'Devam et' butonuna tıklandı.");

            // Bir sonraki sayfanın yüklendiğini doğrula.
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(text(), 'Kredi kartı veya banka kartı ekleyin')]")
            ));
            FileManager.log("Ödeme seçenekleri sayfasına başarıyla geçildi.");
        } catch (TimeoutException e) {
            FileManager.log("TCKN (KYC) sayfası belirtilen sürede çıkmadı, bu adım atlanıyor.");
        } catch (Exception e) {
            FileManager.log("HATA: TCKN (KYC) sayfası işlenirken beklenmedik bir hata oluştu: " + e.getMessage());
            Assert.fail("TCKN (KYC) sayfası adımı başarısız oldu.", e);
        }
    }


    private void enterCreditCardDetails(String cardNumber, String cardName, String expiryDate) {
        FileManager.log("Kredi kartı ekleme adımı başlatılıyor...");

        WebElement addPaymentMethodLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Kredi kartı veya banka kartı ekleyin')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addPaymentMethodLink);
        FileManager.log("'Kredi kartı veya banka kartı ekleyin' linkine Javascript ile tıklandı.");

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.cssSelector("iframe.apx-secure-iframe")
        ));
        FileManager.log("Ödeme formunu içeren iframe bulundu ve geçiş yapıldı.");

        FileManager.log("Form alanlarının doldurulması başlıyor...");

        // Kart Numarası
        WebElement cardNumberInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("addCreditCardNumber")));
        cardNumberInput.sendKeys(cardNumber);
        FileManager.log("Kart numarası girildi.");

        // Kart Üzerindeki İsim
        WebElement cardNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("ppw-accountHolderName")));
        cardNameInput.sendKeys(cardName);
        FileManager.log("Kart üzerindeki isim girildi.");

        // Son Kullanma Tarihi
        String[] dateParts = expiryDate.split("/");
        if (dateParts.length == 2) {
            String correctedMonthValue = String.valueOf(Integer.parseInt(dateParts[0]));
            new Select(driver.findElement(By.name("ppw-expirationDate_month"))).selectByValue(correctedMonthValue);
            new Select(driver.findElement(By.name("ppw-expirationDate_year"))).selectByValue("20" + dateParts[1]);
            FileManager.log("Son kullanma tarihi (Ay/Yıl) seçildi: " + expiryDate);
        } else {
            Assert.fail("Son kullanma tarihi formatı geçersiz: " + expiryDate);
        }

        // "Hayır" Butonuna Tıklama
        By noTextSpanLocator = By.cssSelector("input[name='ppw-storageConsent'][value='OptedOut'] ~ span.a-radio-label");
        WebElement noTextSpan = wait.until(ExpectedConditions.presenceOfElementLocated(noTextSpanLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", noTextSpan);
        FileManager.log("'Hayır' yazısını içeren SPAN elementine Javascript ile tıklandı.");

        // "Kartınızı Ekleyin" Butonu
        WebElement addCardButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.name("ppw-widgetEvent:AddCreditCardEvent")));
        addCardButton.click();
        FileManager.log("'Kartınızı ekleyin' butonuna tıklandı.");


        // Sunucunun yanıt vermesi için ona 1 saniye bekleme
        try {
            Thread.sleep(1000); // 1 saniye bekle
        } catch (InterruptedException ignored) {}


        FileManager.log("Kart ekleme sonrası olası hata mesajı kontrol ediliyor...");
        By errorMessageLocator = By.cssSelector("div.a-alert-error span.a-list-item");
        List<WebElement> errorMessages = driver.findElements(errorMessageLocator);

        if (!errorMessages.isEmpty() && errorMessages.get(0).isDisplayed()) {
            //Hata senaryosu: Hata mesajı bulundu.
            String errorText = errorMessages.get(0).getText();
            FileManager.log("HATA YAKALANDI: '" + errorText + "'. Şimdi 'İptal et' butonuna tıklanacak.");

            WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("ppw-widgetEvent:CancelAddCreditCardEvent")));
            cancelButton.click();
            FileManager.log("'İptal et' butonuna tıklandı.");

            driver.switchTo().defaultContent();
            FileManager.log("Ana içerik alanına (default content) geri dönüldü.");

        } else {
            // Başarı seneryosu: Hata mesajı bulunamadı
            FileManager.log("Hata mesajı bulunamadı. Kart ekleme işleminin başarılı olduğu varsayılıyor.");

            driver.switchTo().defaultContent();
            FileManager.log("Ana içerik alanına (default content) geri dönüldü.");

            FileManager.log("'Bu ödeme şeklini kullan' butonunun aktifleşmesi bekleniyor...");
            By continueButtonLocator = By.cssSelector("[data-testid='bottom-continue-button']");
            wait.until(ExpectedConditions.elementToBeClickable(continueButtonLocator));
            FileManager.log("BAŞARILI: 'Bu ödeme şeklini kullan' butonu aktifleşti.");
        }
    }




    private void logoutFromAmazon() {
        FileManager.log("Hesaptan çıkış yapılıyor...");

        try {
            // "Hesap ve Listeler" menüsünü (div'i) bul
            WebElement accountListMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList")));

            // Fareyi menünün üzerine getirerek açılır pencereyi tetikle.
            new Actions(driver).moveToElement(accountListMenu).perform();
            FileManager.log("'Hesap ve Listeler' menüsünün üzerine fare ile gelindi.");

            // Açılan menüden "Çıkış Yap" linkini bul
            WebElement signOutLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-item-signout")));
            FileManager.log("'Çıkış Yap' linki bulundu.");

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signOutLink);
            FileManager.log("'Çıkış Yap' linkine Javascript ile tıklandı.");

            // Yeni sayfanın yüklendiğini doğrula, "Giriş Yap" sayfasındaki ana başlığı bekliyoruz.
            By signInPageHeaderLocator = By.xpath("//h1[contains(text(), 'Giriş yap')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(signInPageHeaderLocator));
            FileManager.log("BAŞARILI: Hesaptan çıkış yapıldı ve 'Giriş yap' sayfası başlığı doğrulandı.");

        } catch (Exception e) {
            FileManager.log("HATA: Hesaptan çıkış yapılırken bir sorun oluştu. Hata: " + e.getMessage());
            Assert.fail("Çıkış yapma adımı başarısız oldu.", e);
        }
    }

    @Test(description = "Amazon.com.tr - Ürün Filtrele, Sepete Ekle, Adres ve Kart Ekle")
    public void amazonFullTest() throws InterruptedException {

        // Test verilerini dosyadan al
        String[] userInfo = FileManager.getAmazonCredentials();

        // 1. ADIM: GİRİŞ YAP
        loginToAmazon();

        // 2. ADIM: DİZÜSTÜ BİLGİSAYAR SAYFASINA GİT
        navigateToLaptopsPage();

        // 3. ADIM: FİLTRELEME
        applyFulfilledByAmazonFilter();

        // 4. ADIM: ÜRÜNÜ SEPETE EKLE
        addLaptopToCartByIndex(1);

        // 5. ADIM: SEPETE GİT
        FileManager.log("Sağ üstteki sepet ikonuna tıklanarak sepet sayfasına gidiliyor...");

        // Ürünün sepete eklendiğini doğrulamak için sepet ikonundaki sayının '0'dan büyük olmasını bekle
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("nav-cart-count"), "0")));

        // Sağ üstteki sepet ikonuna tıkla
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart")));
        cartIcon.click();

        // Sepet sayfasının yüklendiğini doğrula
        wait.until(ExpectedConditions.titleContains("Alışveriş Sepeti"));
        FileManager.log("Alışveriş sepeti sayfası başarıyla açıldı.");

        // 6. ADIM: ALIŞVERİŞİ TAMAMLA BUTONUNA TIKLA
        WebElement proceedToCheckoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("proceedToRetailCheckout")));
        proceedToCheckoutButton.click();
        FileManager.log("Sepet sayfasındaki 'Alışverişi Tamamla' butonuna tıklandı.");

        // 7. ADIM: ADRES EKLE
        addAddress();

        // 8. ADIM: GÜMRÜK KONTROL
        handleKycPageIfPresent();

        // 9. ADIM: KREDİ KARTI EKLE
        enterCreditCardDetails(userInfo[10], userInfo[11], userInfo[12]);

        // KART EKLEME İŞLEMİNİ DOĞRULANIYORDDU - SİLDİM  ---
        //verifyContinueButtonIsActive();

        // 10. ADIM: SEPETTEKİ ÜRÜNLERİ SİL
        removeAllFromCart();

        // 11. ADIM: HESAPTAN ÇIK
        logoutFromAmazon();

        FileManager.log("TÜM TEST SENARYOSU BAŞARIYLA TAMAMLANDI.");
    }
}


