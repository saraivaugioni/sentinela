import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import br.com.saraivaugioni.sentinela.main.Sentinela;

public class Teste {

	public static void main(String[] args) {
		// Webdriver driver
		System.setProperty("webdriver.gecko.driver", "driver\\geckodriver.exe");

		WebDriver driver = new FirefoxDriver();

		// Map webdriver elements
		// Google
		//Single elements
		driver.get("https://www.google.com");
		WebElement logoGoogle = driver.findElement(By.id("hplogo"));
		WebElement buttonSearchGoogle = driver.findElement(By.name("btnK"));
		WebElement fieldSearch = driver.findElement(By.id("lst-ib"));
		
		//List of welbelements
		List<WebElement> googleElements = driver.findElements(By.className("list"));
		googleElements.add(logoGoogle);
		googleElements.add(buttonSearchGoogle);
		googleElements.add(fieldSearch);

		// -------------------API USE EXAMPLE--------------------//

		// Make API instance, set image path and gen report path
		// and last a resolution to work.
		Sentinela sentinela = new Sentinela(driver, "C:\\testRegression\\testImages", "C:\\testRegression\\testReport\\", 1920, 1080);
		
		// Validate a webelements list
		sentinela.validate(googleElements, "elementsGoogle");

		// Validate a full page
		sentinela.validate("screen_google");
		
		// Validate a webelement
		sentinela.validate(logoGoogle, "logo_google");
		
		// Gen final report
		sentinela.generateReport();
		
		System.out.println(sentinela.isDiff());
		
		driver.quit();
		
		Assert.assertFalse(sentinela.isDiff());
		


	}

}
