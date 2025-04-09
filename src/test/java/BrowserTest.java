import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.MediaEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BrowserTest {
    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeTest
    public void setUpReport() {
    	ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
    	extent = new ExtentReports();
    	extent.attachReporter(sparkReporter);

    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @Test
    public void myTest() throws IOException {
        test = extent.createTest("My Selenium Test");
        try {
            driver.get("https://www.lambdatest.com/selenium-playground/simple-form-demo");
            driver.findElement(By.id("sum1")).sendKeys("10");
            driver.findElement(By.id("sum2")).sendKeys("15");
            driver.findElement(By.xpath("//button[text()='Get Sum']")).click();
            String result = driver.findElement(By.id("addmessage")).getText();

            test.pass("Result is: " + result);
        } catch (Exception e) {
            String screenshotPath = takeScreenshot("myTest");
            test.fail("Test failed: " + e.getMessage(),
			          MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
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

    // 🔧 Screenshot method embedded in this class
    private String takeScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = "test-output/screenshots/" + testName + "_" + timestamp + ".png";

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(screenshotPath);

        try {
            destFile.getParentFile().mkdirs(); // Create folder if not exists
            Files.copy(srcFile.toPath(), destFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destFile.getAbsolutePath();
    }
}
