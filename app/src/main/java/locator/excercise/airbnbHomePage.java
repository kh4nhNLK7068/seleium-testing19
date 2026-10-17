package locator.excercise;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class airbnbHomePage {

    // ===== Header =====
    // 1. Logo CyberSoftbnb (Airbnb), top-left: text "airbnb" (any tag) or a logo image; first match in the DOM
    static By logo = By.xpath("//*[contains(translate(normalize-space(text()),'AIRBN','airbn'),'airbnb')]"
            + " | //img[contains(@src,'logo') or contains(@alt,'logo')]");
    // 2. "Home" link
    static By homeLink = By.xpath("//a[normalize-space()='Home']");
    // 3. "About" link
    static By aboutLink = By.xpath("//a[normalize-space()='About']");
    // 4. User profile icon, top-right (class contains user / avatar / profile, or an svg user icon)
    static By userProfileIcon = By.xpath("//nav/div/div[1]/button[1]/img");

    // ===== Search bar =====
    // 5. Location box "Địa điểm / Bạn sắp đi đâu?"
    static By locationBox = By.xpath("(//*[normalize-space(text())='Địa điểm'])[1]/parent::*");
    // 6. Date range box. The date may be dynamic, so match any leaf element/input containing a "/20xx" date
    static By dateRangeBox = By.xpath("//*[not(*)][contains(.,'/20')] | //input[contains(@value,'/20') or contains(@placeholder,'/20')]");
    // 7. Search button (pink magnifier icon)
    static By searchButton = By.xpath("//*[contains(@class,'search')][self::button or self::span or self::i or self::svg][1]");

    // ===== Guest popup (appears after clicking "Thêm khách") =====
    static By addGuestTrigger = By.xpath("//*[normalize-space(text())='Thêm khách']");
    // 8. Increase guests (+)
    static By guestPlusButton = By.xpath("(//*[normalize-space(text())='Khách']/following::button)[2]");
    // 9. Decrease guests (-)
    static By guestMinusButton = By.xpath("(//*[normalize-space(text())='Khách']/following::button)[1]");

    // ===== Destination cards =====
    // 10. "Hồ Chí Minh" card
    static By hcmCard = By.xpath("//*[normalize-space(text())='Hồ Chí Minh']/ancestor::div[.//img][1]");
    // 11. "Cần Thơ" title
    static By canThoTitle = By.xpath("//*[normalize-space(text())='Cần Thơ']");

    // ===== Filters =====
    // 12. "Loại nơi ở" button
    static By roomTypeFilter = By.xpath("//button[normalize-space()='Loại nơi ở']");
    // 13. "Giá" button
    static By priceFilter = By.xpath("//button[normalize-space()='Giá']");

    // 14. "6.5 giờ lái xe" in the Nha Trang card.
    // React renders "6.5" and " giờ lái xe" as separate text nodes, so use contains(.) on a leaf element, not text()
    static By nhaTrangDriveTime = By.xpath("//*[normalize-space(text())='Nha Trang']/ancestor::div[.//img][1]//*[not(*)][contains(.,'giờ lái xe')]");

    public static void main(String[] args) {
        // Print Vietnamese characters correctly in the console
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        WebDriver driver = new EdgeDriver();
        List<String> failed = new ArrayList<>();

        try {
            driver.get("https://demo5.cybersoft.edu.vn/");

            // Wait for the React SPA to render
            try {
                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.visibilityOfElementLocated(homeLink));
                System.out.println("Home page loaded!");
            } catch (Exception ex) {
                System.err.println("WARN: page did not finish loading | " + firstLine(ex));
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            Map<String, By> locators = new LinkedHashMap<>();
            locators.put("1. Logo", logo);
            locators.put("2. Home link", homeLink);
            locators.put("3. About link", aboutLink);
            locators.put("4. User profile icon", userProfileIcon);
            locators.put("5. Location box", locationBox);
            locators.put("6. Date range box", dateRangeBox);
            locators.put("7. Search button", searchButton);
            locators.put("10. Ho Chi Minh card", hcmCard);
            locators.put("11. Can Tho title", canThoTitle);
            locators.put("12. Room type filter", roomTypeFilter);
            locators.put("13. Price filter", priceFilter);
            locators.put("14. Nha Trang drive time", nhaTrangDriveTime);

            for (Map.Entry<String, By> e : locators.entrySet()) {
                check(wait, e.getKey(), e.getValue(), failed);
            }

            // 8, 9: only present after opening the "Thêm khách" popup
            try {
                wait.until(ExpectedConditions.elementToBeClickable(addGuestTrigger)).click();
                check(wait, "8. Guest plus (+)", guestPlusButton, failed);
                check(wait, "9. Guest minus (-)", guestMinusButton, failed);
            } catch (Exception ex) {
                System.err.println("FAIL: could not open the guest popup -> skipping #8, #9 | " + firstLine(ex));
                failed.add("8. Guest plus (+)");
                failed.add("9. Guest minus (-)");
            }

            System.out.println("\n===== RESULT =====");
            System.out.println(failed.isEmpty() ? "All locators OK" : "Failed locators: " + failed);

        } finally {
            driver.quit();
        }
    }

    // On failure: log and continue with the next locator
    private static void check(WebDriverWait wait, String name, By locator, List<String> failed) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            System.out.println("OK:   " + name + " -> displayed=" + el.isDisplayed());
        } catch (Exception ex) {
            failed.add(name);
            System.err.println("FAIL: " + name + " | " + locator + " | " + firstLine(ex));
        }
    }

    private static String firstLine(Exception ex) {
        String msg = ex.getMessage();
        return ex.getClass().getSimpleName() + ": " + (msg == null ? "" : msg.split("\n")[0]);
    }
}