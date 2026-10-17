package locator.excercise;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class hrmAdminPage {

    private static final String ROW2 = "(//div[@class='oxd-table-body']/div[@class='oxd-table-card'])[2]";

    // ===== Sidebar =====
    static By adminMenu = By.cssSelector("a[href='/web/index.php/admin/viewAdminModule']");
    static By pimMenu = By.cssSelector("a[href='/web/index.php/pim/viewPimModule']");
    static By leaveMenu = By.cssSelector("a[href='/web/index.php/leave/viewLeaveModule']");

    // ===== Form tìm kiếm =====
    static By usernameInput = By.xpath("//label[text()='Username']/parent::div/following-sibling::div/input");
    static By userRoleDropdown = By.xpath("//label[text()='User Role']/parent::div/following-sibling::div//div[contains(@class,'oxd-select-text-input')]");
    static By adminRoleOption = By.xpath("//div[@role='listbox']//span[text()='Admin']");
    static By essRoleOption = By.xpath("//div[@role='listbox']//span[text()='ESS']");
    static By employeeNameInput = By.cssSelector("input[placeholder='Type for hints...']");
    static By resetButton = By.xpath("//button[normalize-space()='Reset']");
    static By searchButton = By.cssSelector("button[type='submit']");

    // ===== Top tab =====
    static By userManagementTab = By.xpath("//li[contains(@class,'oxd-topbar-body-nav-tab')][.//span[normalize-space()='User Management']]");

    // ===== Bảng (dòng 2) =====
    static By secondRowCheckbox = By.xpath(ROW2 + "//span[contains(@class,'oxd-checkbox-input')]");
    static By secondRowUserRole = By.xpath(ROW2 + "//div[@role='cell'][3]/div");
    static By secondRowDeleteIcon = By.xpath(ROW2 + "//button[.//i[contains(@class,'bi-trash')]]");
    static By secondRowEditIcon = By.xpath(ROW2 + "//button[.//i[contains(@class,'bi-pencil-fill')]]");

    // ===== Header bảng =====
    // Dùng class + contains(.) vì text có thể nằm trong thẻ con cùng icon sort
    static By userRoleHeader = By.xpath("//div[contains(@class,'oxd-table-header-cell')][contains(.,'User Role')]");
    static By employeeNameHeader = By.xpath("//div[contains(@class,'oxd-table-header-cell')][contains(.,'Employee Name')]");

    // ===== Khác =====
    static By breadcrumb = By.cssSelector("span.oxd-topbar-header-breadcrumb");
    static By sidebarToggle = By.xpath("//button[.//i[contains(@class,'bi-chevron-left')]]");
    static By systemUsersPanelToggle = By.xpath("//div[contains(@class,'oxd-table-filter-header')]//button");
    static By addNewUserButton = By.xpath("//button[normalize-space()='Add']");

    public static void main(String[] args) {
        WebDriver driver = new EdgeDriver();
        List<String> failed = new ArrayList<>();

        try {
            driver.get("https://opensource-demo.orangehrmlive.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("Admin");
            driver.findElement(By.name("password")).sendKeys("admin123");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            // Vào trang Admin
            wait.until(ExpectedConditions.elementToBeClickable(adminMenu)).click();
            wait.until(ExpectedConditions.urlContains("/admin/viewSystemUsers"));
            System.out.println("Admin page opened successfully!");

            // Timeout ngắn cho mỗi locator
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            Map<String, By> locators = new LinkedHashMap<>();
            locators.put("1. Admin menu", adminMenu);
            locators.put("2. PIM menu", pimMenu);
            locators.put("3. Leave menu", leaveMenu);
            locators.put("4. Username input", usernameInput);
            locators.put("5. User Role dropdown", userRoleDropdown);
            locators.put("8. Employee Name input", employeeNameInput);
            locators.put("9. Reset button", resetButton);
            locators.put("10. Search button", searchButton);
            locators.put("11. User Management tab", userManagementTab);
            locators.put("12. Row 2 checkbox", secondRowCheckbox);
            locators.put("13. Row 2 User Role", secondRowUserRole);
            locators.put("14. Row 2 Delete icon", secondRowDeleteIcon);
            locators.put("15. Row 2 Edit icon", secondRowEditIcon);
            locators.put("16. User Role header", userRoleHeader);
            locators.put("17. Employee Name header", employeeNameHeader);
            locators.put("18. Breadcrumb", breadcrumb);
            locators.put("19. Sidebar toggle", sidebarToggle);
            locators.put("20. System Users panel toggle", systemUsersPanelToggle);
            locators.put("21. Add button", addNewUserButton);

            for (Map.Entry<String, By> e : locators.entrySet()) {
                check(shortWait, e.getKey(), e.getValue(), failed);
            }

            // 6, 7: option chỉ có trong DOM sau khi mở dropdown
            try {
                driver.findElement(userRoleDropdown).click();
                check(shortWait, "6. Option Admin", adminRoleOption, failed);
                check(shortWait, "7. Option ESS", essRoleOption, failed);
            } catch (Exception ex) {
                System.err.println("FAIL: không mở được dropdown User Role -> skip #6, #7 (" + firstLine(ex) + ")");
                failed.add("6. Option Admin");
                failed.add("7. Option ESS");
            }

            System.out.println("\n===== RESULT =====");
            System.out.println(failed.isEmpty() ? "All locators are OK" : "Locator error: " + failed);

        } finally {
            driver.quit();
        }
    }

    // Lỗi thì log rồi bỏ qua, test tiếp locator sau
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