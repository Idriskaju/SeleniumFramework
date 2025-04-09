import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.MediaEntityBuilder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Duration;

public class BrowserTest {
    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeTest
    public void setupReport() throws IOException {
        FileUtils.deleteDirectory(new File("test-output/screenshots"));
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeMethod
    public void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        String isCI = System.getenv("CI");
        if ("true".equalsIgnoreCase(isCI)) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test(dataProvider = "loginData")
    public void fullFlowTest(String username, String password) throws IOException {
        test = extent.createTest("Full Flow - User: " + username);

        try {
            driver.get("https://demoqa.com/login");
            driver.findElement(By.id("userName")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.id("login")).click();
            test.pass("Logged in").addScreenCaptureFromPath(takeScreenshot("login"));
        } catch (Exception e) {
            captureFail("Login", e);
        }

        try {
            driver.get("https://demoqa.com/select-menu");
            WebElement dropdown = driver.findElement(By.id("oldSelectMenu"));
            new Select(dropdown).selectByVisibleText("Blue");
            test.pass("Dropdown selected").addScreenCaptureFromPath(takeScreenshot("dropdown"));
        } catch (Exception e) {
            captureFail("Dropdown", e);
        }

        try {
            driver.get("https://demoqa.com/checkbox");
            driver.findElement(By.cssSelector(".rct-icon-expand-close")).click();
            driver.findElement(By.xpath("//span[text()='Notes']/preceding-sibling::span[@class='rct-checkbox']")).click();
            test.pass("Checkbox selected").addScreenCaptureFromPath(takeScreenshot("checkbox"));
        } catch (Exception e) {
            captureFail("Checkbox", e);
        }

        try {
            driver.get("https://demoqa.com/radio-button");
            driver.findElement(By.xpath("//label[@for='yesRadio']")).click();
            test.pass("Radio button selected").addScreenCaptureFromPath(takeScreenshot("radiobutton"));
        } catch (Exception e) {
            captureFail("Radio Button", e);
        }

        try {
            driver.get("https://demoqa.com/frames");
            driver.switchTo().frame("frame1");
            String textInFrame = driver.findElement(By.id("sampleHeading")).getText();
            test.pass("iFrame text: " + textInFrame).addScreenCaptureFromPath(takeScreenshot("iframe"));
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            captureFail("iFrame", e);
        }

        try {
            driver.get("https://demoqa.com/upload-download");
            WebElement upload = driver.findElement(By.id("uploadFile"));
            upload.sendKeys(new File("sample.txt").getAbsolutePath());
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

    private String takeScreenshot(String name) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = name + "_" + timestamp + ".png";
        String screenshotDir = "test-output/screenshots/";
        String relativePath = "screenshots/" + fileName;
        String fullPath = screenshotDir + fileName;

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dest = new File(fullPath);
        try {
            dest.getParentFile().mkdirs();
            Files.copy(src.toPath(), dest.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return relativePath;
    }

    private void captureFail(String step, Exception e) throws IOException {
        test.fail(step + " failed: " + e.getMessage(),
                MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(step)).build());
    }

    // ✅ Excel Reader Utility
    public static class ExcelUtil {
        public static String[][] readLoginData(String excelPath, String sheetName) throws IOException {
            FileInputStream fis = new FileInputStream(excelPath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(0).getLastCellNum();
            String[][] data = new String[rowCount - 1][colCount];

            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    data[i - 1][j] = row.getCell(j).toString();
                }
            }

            workbook.close();
            fis.close();
            return data;
        }
    }

    // Test Data
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        String path = "src/test/resources/TestData.xlsx";
        return ExcelUtil.readLoginData(path, "LoginData");
    }
}
