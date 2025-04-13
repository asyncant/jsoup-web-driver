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

public class Pages {
  private final String baseUrl;

  public String ajaxyPage;
  public String alertsPage;
  public String blankPage;
  public String bodyTypingPage;
  public String booleanAttributes;
  public String childPage;
  public String chinesePage;
  public String clickEventPage;
  public String clickJacker;
  public String clicksPage;
  public String colorPage;
  public String documentWrite;
  public String dragDropOverflow;
  public String draggableLists;
  public String dragAndDropPage;
  public String droppableItems;
  public String dynamicallyModifiedPage;
  public String dynamicPage;
  public String echoPage;
  public String errorsPage;
  public String formPage;
  public String formSelectionPage;
  public String framesetPage;
  public String grandchildPage;
  public String html5Page;
  public String iframePage;
  public String javascriptEnhancedForm;
  public String javascriptPage;
  public String linkedImage;
  public String longContentPage;
  public String macbethPage;
  public String mapVisibilityPage;
  public String metaRedirectPage;
  public String missedJsReferencePage;
  public String modernModalPage;
  public String mouseInteractionPage;
  public String mouseOverPage;
  public String mouseTrackerPage;
  public String nestedPage;
  public String pointerActionsPage;
  public String printPage;
  public String readOnlyPage;
  public String rectanglesPage;
  public String redirectPage;
  public String richTextPage;
  public String selectableItemsPage;
  public String selectPage;
  public String shadowRootPage;
  public String simpleTestPage;
  public String simpleXmlDocument;
  public String sleepingPage;
  public String slowIframes;
  public String slowLoadingAlertPage;
  public String svgPage;
  public String svgTestPage;
  public String tables;
  public String underscorePage;
  public String unicodeLtrPage;
  public String uploadPage;
  public String userDefinedProperty;
  public String veryLargeCanvas;
  public String xhtmlFormPage;
  public String xhtmlTestPage;
  public String inputsPage;
  public String styledPage;
  public String clickOutOfBoundsPage;
  public String clickTestTestClicksASurroundingStrongTagPage;
  public final String clickTestsGoogleMapPage;
  public final String clickTooBigPage;
  public String tinymcePage;
  public String contentEditablePage;

  public Pages(String baseUrl) {
    this.baseUrl = baseUrl;

    ajaxyPage = baseUrl + "/ajaxy_page.html";
    alertsPage = baseUrl + "/alerts.html";
    blankPage = baseUrl + "/blank.html";
    bodyTypingPage = baseUrl + "/bodyTypingTest.html";
    booleanAttributes = baseUrl + "/booleanAttributes.html";
    childPage = baseUrl + "/child/childPage.html";
    chinesePage = baseUrl + "/cn-test.html";
    clickJacker = baseUrl + "/click_jacker.html";
    clickEventPage = baseUrl + "/clickEventPage.html";
    clickOutOfBoundsPage = baseUrl + "/click_out_of_bounds.html";
    clicksPage = baseUrl + "/clicks.html";
    clickTestTestClicksASurroundingStrongTagPage = baseUrl + "/ClickTest_testClicksASurroundingStrongTag.html";
    clickTestsGoogleMapPage = baseUrl + "/click_tests/google_map.html";
    clickTooBigPage = baseUrl + "/click_too_big.html";
    colorPage = baseUrl + "/colorPage.html";
    contentEditablePage = baseUrl + "/content-editable.html";
    dragDropOverflow = baseUrl + "/dragDropOverflow.html";
    draggableLists = baseUrl + "/draggableLists.html";
    dragAndDropPage = baseUrl + "/dragAndDropTest.html";
    droppableItems = baseUrl + "/droppableItems.html";
    documentWrite = baseUrl + "/document_write_in_onload.html";
    dynamicallyModifiedPage = baseUrl + "/dynamicallyModifiedPage.html";
    dynamicPage = baseUrl + "/dynamic.html";
    echoPage = baseUrl + "/echo";
    errorsPage = baseUrl + "/errors.html";
    xhtmlFormPage = baseUrl + "/xhtmlFormPage.xhtml";
    formPage = baseUrl + "/formPage.html";
    formSelectionPage = baseUrl + "/formSelectionPage.html";
    framesetPage = baseUrl + "/frameset.html";
    grandchildPage = baseUrl + "/child/grandchild/grandchildPage.html";
    html5Page = baseUrl + "/html5Page.html";
    iframePage = baseUrl + "/iframes.html";
    inputsPage = baseUrl + "/inputs.html";
    javascriptEnhancedForm = baseUrl + "/javascriptEnhancedForm.html";
    javascriptPage = baseUrl + "/javascriptPage.html";
    linkedImage = baseUrl + "/linked_image.html";
    longContentPage = baseUrl + "/longContentPage.html";
    macbethPage = baseUrl + "/macbeth.html";
    mapVisibilityPage = baseUrl + "/map_visibility.html";
    metaRedirectPage = baseUrl + "/meta-redirect.html";
    missedJsReferencePage = baseUrl + "/missedJsReference.html";
    modernModalPage = baseUrl + "/modal_dialogs/modern_modal.html";
    mouseInteractionPage = baseUrl + "/mouse_interaction.html";
    mouseOverPage = baseUrl + "/mouseOver.html";
    mouseTrackerPage = baseUrl + "/mousePositionTracker.html";
    nestedPage = baseUrl + "/nestedElements.html";
    pointerActionsPage = baseUrl + "/pointerActionsPage.html";
    printPage = baseUrl + "/printPage.html";
    readOnlyPage = baseUrl + "/readOnlyPage.html";
    rectanglesPage = baseUrl + "/rectangles.html";
    redirectPage = baseUrl + "/redirect";
    richTextPage = baseUrl + "/rich_text.html";
    selectableItemsPage = baseUrl + "/selectableItems.html";
    selectPage = baseUrl + "/selectPage.html";
    simpleTestPage = baseUrl + "/simpleTest.html";
    simpleXmlDocument = baseUrl + "/simple.xml";
    shadowRootPage = baseUrl + "/shadowRootPage.html";
    sleepingPage = baseUrl + "/sleep";
    slowIframes = baseUrl + "/slow_loading_iframes.html";
    slowLoadingAlertPage = baseUrl + "/slowLoadingAlert.html";
    styledPage = baseUrl + "/styledPage.html";
    svgPage = baseUrl + "/svgPiechart.xhtml";
    svgTestPage = baseUrl + "/svgTest.svg";
    tables = baseUrl + "/tables.html";
    tinymcePage = baseUrl + "/tinymce.html";
    underscorePage = baseUrl + "/underscore.html";
    unicodeLtrPage = baseUrl + "/unicode_ltr.html";
    uploadPage = baseUrl + "/upload.html";
    userDefinedProperty = baseUrl + "/userDefinedProperty.html";
    veryLargeCanvas = baseUrl + "/veryLargeCanvas.html";
    xhtmlTestPage = baseUrl + "/xhtmlTest.html";
  }

  public String whereIs(String relativePath) {
    return baseUrl + "/" + relativePath;
  }
}
