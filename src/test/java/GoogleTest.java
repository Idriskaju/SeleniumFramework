import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;


public class GoogleTest {
	public static void main(String[]args) {
		
		WebDriver driver = new ChromeDriver();
		
		String BaseUrl = "https://www.google.com";
		
		driver.get(BaseUrl);
		
		WebElement searchBox = driver.findElement(By.name("q"));
		searchBox.sendKeys("CP-SAT course");
		searchBox.submit();
		
		System.out.println("Title: " + driver.getTitle());
		
		driver.quit();
		
		
		
		
	}
}
