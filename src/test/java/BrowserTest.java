import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.MediaEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Duration;

public class BrowserTest {
    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeTest
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeMethod
    public void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        String isCI = System.getenv("CI"); // GitHub Actions sets CI=true
        if ("true".equalsIgnoreCase(isCI)) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void fullFlowTest() throws IOException {
        test = extent.createTest("Full Feature Flow");

        try {
            // 1. Login
            driver.get("https://demoqa.com/login");
            driver.findElement(By.id("userName")).sendKeys("testuser");
            driver.findElement(By.id("password")).sendKeys("Test@123");
            driver.findElement(By.id("login")).click();
            test.pass("Logged in").addScreenCaptureFromPath(takeScreenshot("login"));

        } catch (Exception e) {
            captureFail("Login", e);
        }

        try {
            // 2. Dropdown
            driver.get("https://demoqa.com/select-menu");
            WebElement dropdown = driver.findElement(By.id("oldSelectMenu"));
            new Select(dropdown).selectByVisibleText("Blue");
            test.pass("Dropdown selected").addScreenCaptureFromPath(takeScreenshot("dropdown"));

        } catch (Exception e) {
            captureFail("Dropdown", e);
        }

        try {
            // 3. Checkbox
            driver.get("https://demoqa.com/checkbox");
            driver.findElement(By.cssSelector(".rct-icon-expand-close")).click(); // expand tree
            driver.findElement(By.xpath("//span[text()='Notes']/preceding-sibling::span[@class='rct-checkbox']")).click();
            test.pass("Checkbox selected").addScreenCaptureFromPath(takeScreenshot("checkbox"));

        } catch (Exception e) {
            captureFail("Checkbox", e);
        }

        try {
            // 4. Radio Button
            driver.get("https://demoqa.com/radio-button");
            driver.findElement(By.xpath("//label[@for='yesRadio']")).click();
            test.pass("Radio button selected").addScreenCaptureFromPath(takeScreenshot("radiobutton"));

        } catch (Exception e) {
            captureFail("Radio Button", e);
        }

        try {
            // 5. iFrame Interaction
            driver.get("https://demoqa.com/frames");
            driver.switchTo().frame("frame1");
            String textInFrame = driver.findElement(By.id("sampleHeading")).getText();
            test.pass("iFrame text: " + textInFrame).addScreenCaptureFromPath(takeScreenshot("iframe"));
            driver.switchTo().defaultContent();

        } catch (Exception e) {
            captureFail("iFrame", e);
        }

        try {
            // 6. File Upload
            driver.get("https://demoqa.com/upload-download");
            WebElement upload = driver.findElement(By.id("uploadFile"));
            upload.sendKeys(new File("sample.txt").getAbsolutePath()); // Use a small local file
            test.pass("File uploaded").addScreenCaptureFromPath(takeScreenshot("upload"));

        } catch (Exception e) {
            captureFail("File Upload", e);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterTest
    public void flushReport() {
        extent.flush();
    }

    // Utility to take screenshots
    private String takeScreenshot(String name) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = "test-output/screenshots/" + name + "_" + timestamp + ".png";

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dest = new File(path);
        try {
            dest.getParentFile().mkdirs();
            Files.copy(src.toPath(), dest.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    // Utility to log failure and attach screenshot
    private void captureFail(String step, Exception e) throws IOException {
        test.fail(step + " failed: " + e.getMessage(),
		          MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(step)).build());
    }
}
