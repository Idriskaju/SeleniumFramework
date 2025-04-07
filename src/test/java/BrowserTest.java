import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

public class BrowserTest {
    WebDriver driver;

    @BeforeTest
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode (for GitHub Actions)
        options.addArguments("--no-sandbox"); // Bypass OS security model
        options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems

        driver = new ChromeDriver(options); // Use local ChromeDriver
    }

    @Test
    public void myTest() {
        driver.get("https://www.lambdatest.com/selenium-playground/simple-form-demo");
        driver.manage().window().maximize();

        driver.findElement(By.id("sum1")).sendKeys("10");
        driver.findElement(By.id("sum2")).sendKeys("15");
        driver.findElement(By.xpath("//button[text()='Get Sum']")).click();
        String text = driver.findElement(By.id("addmessage")).getText();

        System.out.println("Result value is: " + text);
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
