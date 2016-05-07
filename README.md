Testes de Regressão Visual com Sentinela API + WebDriver

Aplicação de uma API simples que combinada com WebDriver consegue realizar testes de regressão visual de forma rápida e eficiente.

Automação com WebDriver - Qualquer projeto, em qualquer modelo de desenvolvimento feito em Selenium WebDriver;
Sentinela API - Foco do nosso trabalho, API que gera prints de tela, gerencia baseline de prints e faz comparação de resultados, Sentinela API é escrita em Java.

Considerando um projeto de automação que já esteja com seus testes funcionais rodando, mas percebe-se a necessidade de realizar testes de regressão visuais devido a frequentes quebras de layout no sistema em produção.
Para que esses testes sejam executados de forma isolada, a equipe dependeria de uma ferramenta específica e pessoal capacitado envolvido, o que poderia gerar mais custos ao projeto.
A solução proposta possibilita apenas acoplar essa API visando criar um mecanismo onde as comparações visuais possam ser realizadas com facilidade e simplicidade atendendo a necessidade.

Mas como a API funciona?

1 - O automatizador deve primeiramente, importar Sentinela API.<br>
2 - O automatizador, irá marcar em seus testes alguns pontos onde a Sentinela API irá realizar seu teste;<br>
3 - O automatizador irá executar o seu teste funcional normalmente;<br>
4 - Cada vez que chegar no ponto marcado o Sentinela API entra em ação;<br>
5 - Tira um print de tela e salva em sua base line;<br>
6 - Compara o resultado com outra baseline (se existir);<br>
7 - Gera o resultado dos testes.

Download:
https://sourceforge.net/projects/sentinelaapi/


Exemplo de código em java.

Preparação do ambiente: ter o webdriver e a sentinela API adicionada no build path.

package teste;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import sentinela.Sentinela;

public class UseExample {

	public static void main(String[] args) {
		
		//Webdriver driver
		WebDriver driver = new FirefoxDriver();
		
		
		//Mapeando os componentes pelo webdriver
		// Google Componentes
		driver.get("http://www.google.com.br");
		WebElement logoGoogle = driver.findElement(By.id("hplogo"));
		WebElement botaoPesquisaGoogle = driver.findElement(By.name("btnK"));
		WebElement botaoEstouComSorte = driver.findElement(By.name("btnI"));
		WebElement campoBuscar = driver.findElement(By.id("lst-ib"));
		List<WebElement> elementosGoogle = driver.findElements(By.className("naoExiste"));
		elementosGoogle.add(logoGoogle);
		elementosGoogle.add(botaoPesquisaGoogle);
		elementosGoogle.add(botaoEstouComSorte);
		
		
		
		//-------------------USO DA API--------------------//
		
		// Cria uma instância da API, passando o path das imagens, o path onde o
		// relatorio será gerado e por última a resolução que irá trabalhar.
		Sentinela sentinela = new Sentinela(driver, "C:\\testeRegressao\\", "C:\\testeRegressao\\relatorio\\", 1920, 1080);

		// Valida uma lista de elementos
		sentinela.validar(elementosGoogle, "elementos_google");
		
		sentinela.validar("tela_google");
		sentinela.validar("tela2_google");
		sentinela.validar("tela3_google");
		
		// Valida uma página inteira
		campoBuscar.sendKeys("teste de software");
		sentinela.validar("tela4_google");
		sentinela.validar("tela5_google");
		sentinela.validar("tela6_google");
		sentinela.validar("tela7_google");
		
		// Gerar o relatorio final
		sentinela.gerarRelatorio();
		driver.quit();
	}
}