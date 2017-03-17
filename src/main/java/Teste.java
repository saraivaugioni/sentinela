import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import br.com.saraivaugioni.sentinelaAPI.main.Sentinela;

public class Teste {

	public static void main(String[] args) {
		// Webdriver driver
		System.setProperty("webdriver.gecko.driver", "driver\\geckodriver.exe");

		WebDriver driver = new FirefoxDriver();

		// Map webdriver elements
		// Google
		driver.get("http://www.google.com.br");
		WebElement logoGoogle = driver.findElement(By.id("hplogo"));
		WebElement botaoPesquisaGoogle = driver.findElement(By.name("btnK"));
		WebElement botaoEstouComSorte = driver.findElement(By.name("btnI"));
		WebElement campoBuscar = driver.findElement(By.id("lst-ib"));
		List<WebElement> elementosGoogle = driver.findElements(By.className("list"));
		elementosGoogle.add(logoGoogle);
		elementosGoogle.add(botaoPesquisaGoogle);
		elementosGoogle.add(botaoEstouComSorte);

		// -------------------API USE EXAMPLE--------------------//

		// Make API instance, set image path and gen report path
		// and last a resolution to work.
		Sentinela sentinela = new Sentinela(driver, "C:\\sentinela\\testRegression\\", "C:\\sentinela\\testReport\\", 1920,
				1080);

		// Validate a webelements list
		sentinela.validate(elementosGoogle, "elementos_google");

		// Validate a full page
		campoBuscar.sendKeys("teste de ");
		sentinela.validate("screen_google");

		// Gen final report
		sentinela.generateReport();
		driver.quit();

	}

}
