import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

class RenderPageUtils {

    private static final String ODDS = "Kursy";
    private static final int DAYS_BACK = Integer.parseInt(Main.rb.getString("aggregation.days.back"));

    static String renderFullPage(String url, ClickAction action) {
        System.setProperty("webdriver.chrome.driver", "/Repos/BookieStatsAgregator/src/main/resources/libs/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        waitJsRendering(driver);
        if (action != ClickAction.NONE) {
            for (int i = 0; i < DAYS_BACK; i++) {
                driver.findElement(By.className(action.value)).click();
                try {
                    Thread.sleep(2500);
                    if (i == 0) {
                        driver.findElement(By.linkText(ODDS)).click();
                        Thread.sleep(2000);
                    }
                    waitJsRendering(driver);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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