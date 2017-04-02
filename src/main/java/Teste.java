import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import br.com.saraivaugioni.sentinela.main.Sentinela;
import br.com.saraivaugioni.sentinela.util.language.LanguageCodes;

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
		campoBuscar.sendKeys("11111teste de ");
		sentinela.validate("screen_google", "validar a tela inicial do google, com objetivo de verificar a integridade do layout e demais componentes do super buscador");

		// Gen final report
		sentinela.generateReport();
		driver.quit();

	}

}
