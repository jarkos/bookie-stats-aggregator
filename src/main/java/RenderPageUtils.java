import model.ClickAction;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

class RenderPageUtils {

    static String renderFullPage(String url, ClickAction action) {
        System.setProperty("webdriver.chrome.driver", "/Repos/BookieStatsAgregator/src/main/resources/libs/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        waitJsRendering(driver);
        if (action != ClickAction.NONE) {
            driver.findElement(By.className(action.value)).click();
            waitJsRendering(driver);
            try {
                Thread.sleep(8000); //lame TODO
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String content = driver.getPageSource();
        driver.close();
        return content;
    }

    private static void waitJsRendering(WebDriver driver) {
        new FluentWait<>(driver).pollingEvery(Duration.ofMillis(200))
                .withTimeout(Duration.ofSeconds(15))
                .ignoring(NoSuchElementException.class);
    }

}