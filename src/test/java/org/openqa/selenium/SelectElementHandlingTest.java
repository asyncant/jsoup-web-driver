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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectElementHandlingTest extends JsoupDriverTestBase {

  @Test
  void testShouldBePossibleToDeselectASingleOptionFromASelectWhichAllowsMultipleChoices() {
    driver.get(pages.formPage);

    WebElement multiSelect = driver.findElement(By.id("multi"));
    List<WebElement> options = multiSelect.findElements(By.tagName("option"));

    WebElement option = options.get(0);
    assertThat(option.isSelected()).isTrue();
    option.click();
    assertThat(option.isSelected()).isFalse();
    option.click();
    assertThat(option.isSelected()).isTrue();

    option = options.get(2);
    assertThat(option.isSelected()).isTrue();
  }

  @Test
  void testShouldBeAbleToChangeTheSelectedOptionInASelect() {
    driver.get(pages.formPage);
    WebElement selectBox = driver.findElement(By.xpath("//select[@name='selectomatic']"));
    List<WebElement> options = selectBox.findElements(By.tagName("option"));
    WebElement one = options.get(0);
    WebElement two = options.get(1);
    assertThat(one.isSelected()).isTrue();
    assertThat(two.isSelected()).isFalse();

    two.click();
    assertThat(one.isSelected()).isFalse();
    assertThat(two.isSelected()).isTrue();
  }

  @Test
  void testShouldBeAbleToSelectMoreThanOneOptionFromASelectWhichAllowsMultipleChoices() {
    driver.get(pages.formPage);

    WebElement multiSelect = driver.findElement(By.id("multi"));
    List<WebElement> options = multiSelect.findElements(By.tagName("option"));
    for (WebElement option : options) {
      if (!option.isSelected()) {
        option.click();
      }
    }

    for (int i = 0; i < options.size(); i++) {
      WebElement option = options.get(i);
      assertThat(option.isSelected()).as("Option at index %s", i).isTrue();
    }
  }

  @Test
  void testShouldSelectFirstOptionByDefaultIfNoneIsSelected() {
    driver.get(pages.formPage);
    WebElement selectBox = driver.findElement(By.xpath("//select[@name='select-default']"));
    List<WebElement> options = selectBox.findElements(By.tagName("option"));
    WebElement one = options.get(0);
    WebElement two = options.get(1);
    assertThat(one.isSelected()).isTrue();
    assertThat(two.isSelected()).isFalse();

    two.click();
    assertThat(one.isSelected()).isFalse();
    assertThat(two.isSelected()).isTrue();
  }

  @Test
  void testCanSelectElementsInOptGroups() {
    driver.get(pages.selectPage);
    WebElement element = driver.findElement(By.id("two-in-group"));
    element.click();
    assertThat(element.isSelected()).isTrue();
  }

  @Test
  void testCanGetValueFromOptionViaAttributeWhenAttributeDoesntExist() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.cssSelector("select[name='select-default'] option"));
    assertThat(element.getAttribute("value")).isEqualTo("One");
    element = driver.findElement(By.id("blankOption"));
    assertThat(element.getAttribute("value")).isEmpty();
  }

  @Test
  void testCanGetValueFromOptionViaAttributeWhenAttributeIsEmptyString() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("optionEmptyValueSet"));
    assertThat(element.getAttribute("value")).isEmpty();
  }

  @Test
  void testCanSelectFromMultipleSelectWhereValueIsBelowVisibleRange() {
    driver.get(pages.selectPage);
    WebElement option =
        driver.findElements(By.cssSelector("#selectWithMultipleLongList option")).get(4);
    option.click();
    assertThat(option.isSelected()).isTrue();
  }

  @Test
  void testCannotSetDisabledOption() {
    driver.get(pages.selectPage);
    WebElement element = driver.findElement(By.cssSelector("#visibility .disabled"));
    element.click();
    assertThat(element.isSelected()).isFalse();
  }

  @Test
  public void testCanSetHiddenOption() {
    driver.get(pages.selectPage);
    WebElement element = driver.findElement(By.cssSelector("#visibility .hidden"));
    element.click();
    assertThat(element.isSelected()).isTrue();
  }

  @Test
  public void testCanSetInvisibleOption() {
    driver.get(pages.selectPage);
    WebElement element = driver.findElement(By.cssSelector("#visibility .invisible"));
    element.click();
    assertThat(element.isSelected()).isTrue();
  }

  @Test
  void testCanHandleTransparentSelect() {
    driver.get(pages.selectPage);
    WebElement element = driver.findElement(By.cssSelector("#transparent option"));
    element.click();
    assertThat(element.isSelected()).isTrue();
  }
}
