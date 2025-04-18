// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium;

import com.asyncant.selenium.JsoupDriverTestBase;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ElementEqualityTest extends JsoupDriverTestBase {

  @Test
  void testSameElementLookedUpDifferentWaysShouldBeEqual() {
    driver.get(pages.simpleTestPage);

    WebElement body = driver.findElement(By.tagName("body"));
    WebElement xbody = driver.findElements(By.xpath("//body")).get(0);

    assertThat(xbody).isEqualTo(body);
  }

  @Test
  void testDifferentElementsShouldNotBeEqual() {
    driver.get(pages.simpleTestPage);

    List<WebElement> ps = driver.findElements(By.tagName("p"));

    assertThat(ps.get(0).equals(ps.get(1))).isFalse();
  }

  @Test
  void testSameElementLookedUpDifferentWaysUsingFindElementShouldHaveSameHashCode() {
    driver.get(pages.simpleTestPage);
    WebElement body = driver.findElement(By.tagName("body"));
    WebElement xbody = driver.findElement(By.xpath("//body"));

    assertThat(xbody.hashCode()).isEqualTo(body.hashCode());
  }

  @Test
  void testSameElementLookedUpDifferentWaysUsingFindElementsShouldHaveSameHashCode() {
    driver.get(pages.simpleTestPage);
    List<WebElement> body = driver.findElements(By.tagName("body"));
    List<WebElement> xbody = driver.findElements(By.xpath("//body"));

    assertThat(xbody.get(0).hashCode()).isEqualTo(body.get(0).hashCode());
  }

//  @SwitchToTopAfterTest
//  @Test
//  @NotYetImplemented(SAFARI)
//  public void testAnElementFoundInViaJsShouldHaveSameId() {
//    driver.get(pages.missedJsReferencePage);
//
//    driver.switchTo().frame("inner");
//    WebElement first = driver.findElement(By.id("oneline"));
//
//    WebElement element =
//        (WebElement)
//            ((JavascriptExecutor) driver)
//                .executeScript("return document.getElementById('oneline');");
//
//    checkIdEqualityIfRemote(first, element);
//  }

  private void checkIdEqualityIfRemote(WebElement first, WebElement second) {
    String firstId = getId(unwrapIfNecessary(first));
    String secondId = getId(unwrapIfNecessary(second));

    assertThat(secondId).isEqualTo(firstId);
  }

  private String getId(WebElement element) {
    if (!(element instanceof RemoteWebElement)) {
      System.err.println("Skipping remote element equality test - not a remote web driver");
      return null;
    }

    return ((RemoteWebElement) element).getId();
  }

  private WebElement unwrapIfNecessary(WebElement element) {
    if (element instanceof WrapsElement) {
      return ((WrapsElement) element).getWrappedElement();
    }
    return element;
  }
}
