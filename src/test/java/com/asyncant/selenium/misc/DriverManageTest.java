package com.asyncant.selenium.misc;

import com.asyncant.selenium.JsoupDriverTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DriverManageTest extends JsoupDriverTestBase {
  @Test
  public void manageReturnsObject() {
    assertNotNull(driver.manage());
    assertNotNull(driver.manage().timeouts());
    assertNotNull(driver.manage().logs());
    assertNotNull(driver.manage().window());
  }
}
