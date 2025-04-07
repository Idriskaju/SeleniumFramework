import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

public class BrowserTest {
    RemoteWebDriver driver;

    @BeforeTest
    public void setUp() throws Exception {
        ChromeOptions browserOptions = new ChromeOptions();
        browserOptions.setPlatformName("Windows 10");
        browserOptions.setBrowserVersion("latest"); // Use "latest" instead of "dev"

        HashMap<String, Object> ltOptions = new HashMap<String, Object>();
        ltOptions.put("username", "idriskaju");
        ltOptions.put("accessKey", "rVaSUBQVkxPA7FGJtqPJFonsCJezDMUG9CFWel0GTMTpZdv5rp");
        ltOptions.put("project", "Untitled");
        ltOptions.put("w3c", true);
        ltOptions.put("plugin", "java-java");

        browserOptions.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://hub.lambdatest.com/wd/hub"), browserOptions);
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
        
            driver.quit();
        
    }
}
