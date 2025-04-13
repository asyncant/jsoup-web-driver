package com.asyncant.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class JsoupDriverTestBase {
  private static final Duration DEFAULT_SCRIPT_TIMEOUT = Duration.ofSeconds(1);
  protected Pages pages = new Pages("http://localhost:8080");
  protected static WebDriver driver;
  private static JsoupTestServer server;
  protected Wait<WebDriver> shortWait;
  protected Wait<WebDriver> wait;

  @BeforeEach
  public void beforeEach() {
//    driver = getDriverFromEnv();
    shortWait = new WebDriverWait(driver, Duration.ofMillis(200), Duration.ofMillis(10));
    wait = new WebDriverWait(driver, Duration.ofMillis(200), Duration.ofMillis(10));
  }

  @AfterEach
  public void afterEach() {
//    driver.quit();
  }

  @BeforeAll
  public static void beforeAll() {
    server = new JsoupTestServer();
    server.start();
    driver = getDriverFromEnv();
  }

  @AfterAll
  public static void afterAll() {
    driver.quit();
    server.stop();
  }

  protected static WebDriver getDriverFromEnv() {
    String driver = System.getenv("WEBAPP_WEB_DRIVER");
    if (driver == null) {
      driver = "default";
    }
    return switch (driver) {
      case "chrome" -> getChromeDriver();
      case "firefox" -> getFirefoxDriver();
      case "htmlunit" -> getHtmlUnitDriver();
      case "jsoup", "default" -> new JsoupWebDriver();
      default -> throw new IllegalArgumentException("Unknown web driver: " + driver);
    };
  }

  private static ChromeDriver getChromeDriver() {
    ChromeOptions options = new ChromeOptions();
    options.setScriptTimeout(Duration.ofSeconds(DEFAULT_SCRIPT_TIMEOUT.toSeconds()));
    options.addArguments("--headless");
    // Workaround until new version is released for https://github.com/SeleniumHQ/selenium/issues/11750
    options.addArguments("--remote-allow-origins=*");
    return new ChromeDriver(options);
  }

  private static FirefoxDriver getFirefoxDriver() {
    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("-headless");
    options.addArguments("--log-level", "fatal");
    options.setLogLevel(FirefoxDriverLogLevel.WARN);
    // Without these fission preferences, a warning is logged to set them to these values.
    options.addPreference("fission.webContentIsolationStrategy", 0);
    options.addPreference("fission.bfcacheInParent", false);
    options.setCapability("webSocketUrl", true);

    return new FirefoxDriver(options);
  }

  private static HtmlUnitDriver getHtmlUnitDriver() {
    // Quiet htmlunit's verbose warnings, e.g. about "text/javascript" being obsolete while it isn't (anymore).
    java.util.logging.Logger.getLogger("org.htmlunit.IncorrectnessListenerImpl").setLevel(java.util.logging.Level.SEVERE);
    java.util.logging.Logger.getLogger("org.htmlunit.DefaultCssErrorHandler").setLevel(java.util.logging.Level.SEVERE);
    HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(true);
    htmlUnitDriver.manage().timeouts().scriptTimeout(DEFAULT_SCRIPT_TIMEOUT);
    return htmlUnitDriver;
  }
}
