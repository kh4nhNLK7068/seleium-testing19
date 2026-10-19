package locator.excercise;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

public class amazonSearchResultPage {

    // The n-th product card in the result list: RESULT + "[n]"
    private static final String RESULT = "(//div[@data-component-type='s-search-result'])";

    // 1. Image of product 3 (Mens Runfalcon 6)
    static By product3Image = By.xpath(RESULT + "[3]//img[contains(@class,'s-image')]");
    // 2. Price of product 4 ($53.00 - Men's Tricot Track Suit)
    static By product4Price = By.xpath(RESULT + "[4]//span[@class='a-price'][1]");
    // 3. Price of product 2 ($79.95 - Women's VL Court)
    static By product2Price = By.xpath(RESULT + "[2]//span[@class='a-price'][1]");
    // 4. Main search box (contains "adidas")
    static By searchBox = By.id("twotabsearchtextbox");
    // 5. Cart icon in the header
    static By cartIcon = By.id("nav-cart");
    // 6. "Today's Deals" menu
    static By todaysDeals = By.xpath("//div[@id='nav-xshop']//a[contains(normalize-space(.),'Deals')]");
    // 7. "Add to cart" button of the first product
    static By product1AddToCart = By.xpath(RESULT + "[1]//button[normalize-space()='Add to cart']");
    // 8. Name of product 2 ("Women's VL Court 3.0 Sneakers"): last h2 in the card (the first one is the brand)
    static By product2Title = By.xpath("(" + RESULT + "[2]//h2)[last()]");
    // 9. "1K+ bought in past month" line of product 1
    static By product1BoughtLine = By.xpath(RESULT + "[1]//span[contains(normalize-space(.),'bought in past month')][not(*)]");
    // 10. Brand "adidas" of the first product: first h2 in the card
    static By product1Brand = By.xpath("(" + RESULT + "[1]//h2)[1]");
    // 11. "Results" header section
    static By resultsHeader = By.xpath("//h2[normalize-space()='Results'] | //span[normalize-space()='Results']");
    // 12. "Men" checkbox in the Gender group (the input itself is hidden; only presence is checked)
    static By menCheckbox = By.xpath("//li[@aria-labelledby='-title']//span//a[@aria-label='Apply Men filter to narrow results']");
    // 13. Result bar "1-48 of over 10,000 results for 'adidas'"
    static By resultInfoBar = By.xpath("//div[@data-component-type='s-result-info-bar'] | //span[contains(text(),'results for')]");
    // 14. First "See more" link in the left sidebar (under Popular Shopping Ideas)
    static By seeMoreLink = By.xpath("(//div[@id='s-refinements']//*[normalize-space(text())='See more'])[1]");

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        WebDriver driver = new EdgeDriver();
        List<String> failed = new ArrayList<>();

        try {
            // language=en_US forces the English UI, so the text-based locators match
            driver.get("https://www.amazon.com/?language=en_US");
            WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Search "adidas" and press Enter
            try {
                WebElement box = pageWait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
                box.sendKeys("adidas", Keys.ENTER);
                pageWait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@data-component-type='s-search-result']")));
                System.out.println("Search result page loaded!");
            } catch (Exception ex) {
                // Amazon may show a CAPTCHA / bot-check page instead; solve it manually, then re-run
                System.err.println("WARN: search result page not loaded (CAPTCHA?) | " + firstLine(ex));
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            Map<String, By> locators = new LinkedHashMap<>();
            locators.put("1. Product 3 image", product3Image);
            locators.put("2. Product 4 price", product4Price);
            locators.put("3. Product 2 price", product2Price);
            locators.put("4. Search box", searchBox);
            locators.put("5. Cart icon", cartIcon);
            locators.put("6. Today's Deals", todaysDeals);
            locators.put("7. Product 1 Add to cart", product1AddToCart);
            locators.put("8. Product 2 title", product2Title);
            locators.put("9. Product 1 bought line", product1BoughtLine);
            locators.put("10. Product 1 brand", product1Brand);
            locators.put("11. Results header", resultsHeader);
            locators.put("12. Men checkbox", menCheckbox);
            locators.put("13. Result info bar", resultInfoBar);
            locators.put("14. See more link", seeMoreLink);

            for (Map.Entry<String, By> e : locators.entrySet()) {
                check(wait, e.getKey(), e.getValue(), failed);
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
            String text = el.getText().replace("\n", " ").trim();
            if (text.length() > 40) text = text.substring(0, 40) + "...";
            System.out.println("OK:   " + name + " -> displayed=" + el.isDisplayed() + ", text='" + text + "'");
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