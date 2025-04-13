package com.asyncant.selenium;

import com.asyncant.jsoup.select.LinkTextEvaluator;
import com.asyncant.jsoup.select.NotRootWrappingEvaluator;
import com.asyncant.jsoup.select.PartialLinkTextEvaluator;
import org.jsoup.Connection;
import org.jsoup.helper.ValidationException;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.Logs;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A jsoup-based implementation of WebDriver, intended to be used for functional testing. It does not implement all
 * features of the {@link WebDriver} interface, and instead sacrifices features, most prominently CSS and Javascript,
 * for speed.
 */
@NullMarked
public class JsoupWebDriver implements WebDriver {
  private Page lastPage = new Page(Document.createShell("http://invalid.localhost"));
  private final JsoupWebDriverOptions manageOptions = new JsoupWebDriverOptions();
  private final JsoupWebDriverTimeoutConfig timeoutConfig = new JsoupWebDriverTimeoutConfig();
  private final JsoupWebDriverLogs logs = new JsoupWebDriverLogs();
  private final JsoupWebDriverWindowConfig windowConfig = new JsoupWebDriverWindowConfig();
  private final JsoupWebDriverNavigation navigation = new JsoupWebDriverNavigation();

  @Override
  public void get(String url) {
    if ("about:blank".equals(url)) {
      lastPage = new Page(Document.createShell("http://invalid.localhost"));
      return;
    }

    Connection connection = lastPage.response.connection();
    try {
      lastPage = new Page(connection.newRequest(url).ignoreHttpErrors(true).get());
    } catch (IllegalArgumentException e) {
      throw new WebDriverException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nullable String getCurrentUrl() {
    return getCurrentUri().toString();
  }

  @Override
  public @Nullable String getTitle() {
    return lastPage.response.title();
  }

  @Override
  public List<WebElement> findElements(By by) {
    return lastPage.rootElement.findElements(by);
  }

  @Override
  public WebElement findElement(By by) {
    return lastPage.rootElement.findElement(by);
  }

  private static Evaluator parseCss(String selector) {
    try {
      return QueryParser.parse(selector);
    } catch (ValidationException | Selector.SelectorParseException e) {
      throw new InvalidSelectorException("Invalid css selector: " + selector, e);
    }
  }

  @Override
  public @Nullable String getPageSource() {
    return lastPage.response.outerHtml();
  }

  @Override
  public void close() {

  }

  @Override
  public void quit() {

  }

  @Override
  public Set<String> getWindowHandles() {
    return Set.of();
  }

  @Override
  public String getWindowHandle() {
    return "";
  }

  @Override
  public TargetLocator switchTo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Navigation navigate() {
    return navigation;
  }

  @Override
  public Options manage() {
    return manageOptions;
  }

  @NullMarked
  class JsoupWebElement implements WebElement, WrapsDriver {
    private final Element rawElement;

    JsoupWebElement(Element rawElement) {
      this.rawElement = rawElement;
    }

    @Override
    public void click() {
      if (!isEnabled()) return;

      switch (getTagName()) {
        case "label" -> {
          var forAttribute = rawElement.attribute("for");
          if (forAttribute != null) {
            var forElement = JsoupWebDriver.this.findElement(new By.ById(forAttribute.getValue()));
            forElement.click();
            return;
          }
        }
        case "input" -> {
          switch (rawElement.attr("type")) {
            case "radio" -> {
              Element form = rawElement.closest("form");
              if (form != null) {
                for (Element siblingInputs : form.getElementsByTag("input")) {
                  if (siblingInputs.attr("type").equals("radio")) {
                    siblingInputs.attr("checked", false);
                  }
                }
              }

              rawElement.attr("checked", true);
            }
            case "checkbox" -> rawElement.attr("checked", !rawElement.hasAttr("checked"));
            case "image", "submit" -> submit();
          }

          return;
        }
        case "button" -> {
          var formId = rawElement.attr("form");
          if (!formId.isEmpty()) {
            var form = JsoupWebDriver.this.lastPage.response.getElementById(formId);
            if (form == null) return;
            submitForm((FormElement) form);
          } else {
            submit();
          }
          return;
        }
        case "option" -> {
          var select = rawElement.closest("select");
          if (select == null) return;
          if (select.hasAttr("multiple")) {
            rawElement.attr("selected", !rawElement.hasAttr("selected"));
          } else {
            var options = select.getElementsByTag("option");
            for (Element option : options) {
              option.attr("selected", false);
            }
            rawElement.attr("selected", true);
          }
          return;
        }
      }

      var button = rawElement.closest("button");
      if (button != null) {
        JsoupWebDriver.this.lastPage.getOrWrapElement(button).click();
        return;
      }

      var href = rawElement.absUrl("href");
      if (!href.isEmpty()) {
        get(href);
        return;
      }

      var anchor = rawElement.closest("a");
      if (anchor == null) return;
      get(anchor.absUrl("href"));
    }

    @Override
    public void submit() {
      var form = rawElement.closest("form");
      if (form == null) throw new UnsupportedOperationException("Can only submit forms.");

      submitForm((FormElement) form);
    }

    private void submitForm(FormElement form) {
      try {
        lastPage = new Page(form.submit().execute().parse());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
      if (keysToSend.length == 0) return;
      if (!isEnabled()) throw new InvalidElementStateException("Element is disabled.");

      if (keysToSend.length == 1 && (keysToSend[0].equals("\n") || keysToSend[0].equals(Keys.ENTER))) {
        if (rawElement.closest("form") != null) {
          submit();
          return;
        }
      }

      if (getTagName().equals("textarea") || getTagName().equals("input")) {
        if (!rawElement.hasAttr(ORIGINAL_VALUE_ATTR)) {
          var attribute = rawElement.attribute("value");
          rawElement.attr(ORIGINAL_VALUE_ATTR, attribute == null ? ORIGINAL_VALUE_ATTR : attribute.getValue());
        }
        rawElement.attr("value", rawElement.val() + String.join("", keysToSend));
      } else if (isContentEditable()) rawElement.appendText(String.join("", keysToSend));
    }

    @Override
    public void clear() {
      if (!isEnabled()) throw new InvalidElementStateException("Element is disabled.");
      if (isReadOnly()) throw new InvalidElementStateException("Element is read only.");

      String tagName = getTagName();
      if (tagName.equals("textarea")) rawElement.text("");
      else if (tagName.equals("input")) clearInput();
      else if (isContentEditable()) rawElement.text("");
      else rawElement.val("");
    }

    private void clearInput() {
      switch (rawElement.attr("type")) {
        case "color" -> rawElement.attr("value", "#000000");
        case "range" -> {
          var max = getInputMax();
          var min = getInputMin();
          var value = (max + min) / 2 + min;
          String newValue = String.valueOf(value);
          if (value == Math.floor(value)) newValue = Integer.toString((int) value);
          rawElement.attr("value", newValue);
        }
        default -> rawElement.val("");
      }
    }

    @Override
    public String getTagName() {
      return rawElement.tagName();
    }

    @Override
    public @Nullable String getDomProperty(String name) {
      return switch (name) {
        case "class" -> null;
        case "className" -> rawElement.attr("class");
        case "classList" -> Arrays.stream(rawElement.attr("class").split(" "))
          .filter(it -> !it.isEmpty())
          .collect(Collectors.joining(", ", "[", "]"));
        case "colspan" -> null;
        case "colSpan" -> rawElement.attr("colspan");
        case "index" -> {
          if (!getTagName().equals("option")) yield rawElement.attr(name);

          Element select = rawElement.closest("select");
          if (select == null) yield null;
          yield Integer.toString(select.getElementsByTag("option").indexOf(rawElement));
        }
        case "innerHTML" -> rawElement.html();
        case "innerText", "textContent" -> rawElement.text();
        case "href" -> rawElement.absUrl("href");
        case "selectedIndex" -> {
          if (!getTagName().equals("select")) yield rawElement.attr(name);

          var options = rawElement.getElementsByTag("option");
          for (int i = 0; i < options.size(); i++) {
            Element option = options.get(i);
            if (option.hasAttr("selected")) yield Integer.toString(i);
          }
          yield "-1";
        }
        case "src" -> {
          if (!rawElement.hasAttr("src")) yield null;
          yield rawElement.absUrl("src");
        }
        case "value" -> {
          switch (getTagName()) {
            case "textarea", "option" -> {
              var value = rawElement.attribute("value");
              if (value != null) yield value.getValue();
              else yield getText();
            }
            case "input" -> {
              yield rawElement.val();
            }
            default -> {
              yield rawElement.attr(name);
            }
          }
        }
        case String attr when (BOOLEAN_ATTRIBUTES.contains(attr)) -> rawElement.hasAttr(name) ? "true" : "false";
        default -> {
          var attribute = rawElement.attribute(name);
          if (attribute != null) yield attribute.getValue();
          yield null;
        }
      };
    }

    @Override
    public @Nullable String getDomAttribute(String name) {
      String tagName = getTagName();
      if (name.equals("value") && tagName.equals("input")) {
        if (rawElement.hasAttr(ORIGINAL_VALUE_ATTR)) {
          var originalValue = rawElement.attr(ORIGINAL_VALUE_ATTR);
          return originalValue.equals(ORIGINAL_VALUE_ATTR) ? null : originalValue;
        }
        var value = rawElement.val();
        if (value.isEmpty()) return null;
        return value;
      }
      if ("selected".equals(name) && tagName.equals("input")) return rawElement.hasAttr("checked") ? "true" : null;
      if (BOOLEAN_ATTRIBUTES.contains(name.toLowerCase())) return rawElement.hasAttr(name) ? "true" : null;

      Attribute attribute = rawElement.attribute(name);
      if (attribute == null) return null;
      return attribute.getValue();
    }

    @Override
    public @Nullable String getAttribute(String name) {
      if ("href".equals(name) || "src".equals(name)) return getDomProperty(name);
      if ("value".equals(name)) return getDomProperty(name);
      if (BOOLEAN_ATTRIBUTES.contains(name.toLowerCase())) return getDomAttribute(name);

      String domAttribute = getDomAttribute(name);
      if (domAttribute != null) return domAttribute;
      return getDomProperty(name);
    }

    @Override
    public @Nullable String getAriaRole() {
      return WebElement.super.getAriaRole();
    }

    @Override
    public @Nullable String getAccessibleName() {
      return WebElement.super.getAccessibleName();
    }

    @Override
    public boolean isSelected() {
      String tagName = getTagName();
      if (tagName.equals("input")) return rawElement.hasAttr("checked");
      if (tagName.equals("option")) {
        var selected = rawElement.hasAttr("selected");
        if (selected) return true;
        var select = rawElement.closest("select");
        if (select == null) return false;
        var siblings = select.getElementsByTag("option");
        if (siblings.indexOf(rawElement) != 0) return false;
        // If no other option is selected and this is the first option then it's selected by default.
        return siblings.stream().noneMatch(it -> it.hasAttr("selected"));
      }

      throw new UnsupportedOperationException("Unsupported element type: " + tagName);
    }

    @Override
    public boolean isEnabled() {
      return !rawElement.hasAttr("disabled");
    }

    public boolean isReadOnly() {
      return rawElement.hasAttr("readonly");
    }

    public boolean isContentEditable() {
      Attribute attribute = rawElement.attribute("contenteditable");
      return attribute != null && attribute.getValue().equals("true");
    }

    @Override
    public String getText() {
      if (getTagName().equals("pre")) return rawElement.wholeText();

      return rawElement.wholeText().trim().replace(' ', ' ').replace("‎", "");
    }

    @Override
    public List<WebElement> findElements(By by) {
      if (by instanceof By.ByXPath byXPath) return findElementsByXPath(byXPath, rawElement);
      if (by instanceof By.ByName byName && getSelector(byName).isEmpty()) return Collections.emptyList();

      return findElements(toEvaluator(by));
    }

    @Override
    public WebElement findElement(By by) {
      if (by instanceof By.ByXPath byXPath) {
        var elements = findElementsByXPath(byXPath, rawElement);
        if (elements.isEmpty()) throw new NoSuchElementException("No element with selector: " + by);
        return elements.getFirst();
      }

      if (by instanceof By.ByName byName && getSelector(byName).isEmpty()) {
        throw new NoSuchElementException("No element with selector: " + by);
      }

      return findElement(toEvaluator(by), by);
    }

    @Override
    public SearchContext getShadowRoot() {
      return WebElement.super.getShadowRoot();
    }

    @Override
    public boolean isDisplayed() {
      return false;
    }

    @Override
    public Point getLocation() {
      return new Point(0, 0);
    }

    @Override
    public Dimension getSize() {
      return new Dimension(0, 0);
    }

    @Override
    public Rectangle getRect() {
      return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public String getCssValue(String propertyName) {
      return "";
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
      throw new UnsupportedOperationException();
    }

    @Override
    public WebDriver getWrappedDriver() {
      return JsoupWebDriver.this;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      JsoupWebElement that = (JsoupWebElement) o;
      return Objects.equals(rawElement, that.rawElement);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(rawElement);
    }

    private List<WebElement> findElements(Evaluator evaluator) {
      var elements = rawElement.select(new NotRootWrappingEvaluator(evaluator));
      return lastPage.getOrWrapElements(elements);
    }

    private WebElement findElement(Evaluator evaluator, By selector) {
      var element = rawElement.selectFirst(new NotRootWrappingEvaluator(evaluator));
      if (element == null) throw new NoSuchElementException("No element with selector: " + selector);
      return lastPage.getOrWrapElement(element);
    }

    private List<WebElement> findElementsByXPath(By.ByXPath byXPath, Element document) {
      try {
        return lastPage.getOrWrapElements(document.selectXpath(getSelector(byXPath)));
      } catch (Selector.SelectorParseException e) {
        throw new InvalidSelectorException("Invalid selector: " + byXPath, e);
      }
    }

    private double getInputMin() {
      String min = getDomAttribute("min");
      if (min == null) return 0;

      try {
        return Double.parseDouble(min);
      } catch (NumberFormatException e) {
        return 0;
      }
    }

    private double getInputMax() {
      String max = getDomAttribute("max");
      if (max == null) return 100;

      try {
        return Double.parseDouble(max);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
  }

  @NullMarked
  private class JsoupWebDriverOptions implements Options {

    @Override
    public void addCookie(Cookie cookie) {
      String domain = cookie.getDomain();
      if (domain == null) {
        domain = lastPage.response.connection().request().url().getHost();
      }
      HttpCookie javaCookie = seleniumCookieToHttpCookie(cookie, domain);

      lastPage.response.connection().cookieStore().add(URI.create(domain), javaCookie);
    }

    private static HttpCookie seleniumCookieToHttpCookie(Cookie cookie, String domain) {
      HttpCookie javaCookie = new HttpCookie(cookie.getName(), cookie.getValue());
      javaCookie.setDomain(domain);
      javaCookie.setPath(cookie.getPath());
      javaCookie.setSecure(cookie.isSecure());
      javaCookie.setHttpOnly(cookie.isHttpOnly());
      if (cookie.getExpiry() != null) javaCookie.setMaxAge(cookie.getExpiry().getTime());
      return javaCookie;
    }

    private static Cookie httpCookieToSeleniumCookie(HttpCookie cookie) {
      Cookie.Builder javaCookie = new Cookie.Builder(cookie.getName(), cookie.getValue())
        .domain(cookie.getDomain())
        .path(cookie.getPath())
        .isSecure(cookie.getSecure())
        .isHttpOnly(cookie.isHttpOnly());
      if (cookie.getMaxAge() > 0) javaCookie.expiresOn(new Date(cookie.getMaxAge()));
      return javaCookie.build();
    }


    @Override
    public void deleteCookieNamed(String name) {
      if (name.isBlank()) throw new IllegalArgumentException("Cookie name cannot be blank.");

      CookieStore cookieStore = lastPage.response.connection().cookieStore();
      URI uri = getCurrentUri();

      Optional<HttpCookie> cookie = cookieStore.get(uri)
        .stream()
        .filter(it -> it.getName().equals(name)).findFirst();

      if (cookie.isEmpty()) return;
      cookieStore.remove(uri, cookie.get());
    }

    @Override
    public void deleteCookie(Cookie cookie) {
      deleteCookieNamed(cookie.getName());
    }

    @Override
    public void deleteAllCookies() {
      URI uri = getCurrentUri();
      CookieStore cookieStore = lastPage.response.connection().cookieStore();
      for (HttpCookie cookie : cookieStore.get(uri)) {
        cookieStore.remove(uri, cookie);
      }
    }

    @Override
    public Set<Cookie> getCookies() {
      return lastPage.response.connection().cookieStore().getCookies().stream()
        .map(JsoupWebDriverOptions::httpCookieToSeleniumCookie).collect(Collectors.toSet());
    }

    @Override
    public @Nullable Cookie getCookieNamed(String name) {
      if (name.isBlank()) throw new IllegalArgumentException("Cookie name cannot be blank.");

      URI uri = getCurrentUri();
      CookieStore cookieStore = lastPage.response.connection().cookieStore();
      Optional<HttpCookie> cookie = cookieStore.get(uri).stream()
        .filter(it -> uri.getPath().startsWith(it.getPath()))
        .filter(it -> it.getName().equals(name)).findFirst();
      return cookie.map(JsoupWebDriverOptions::httpCookieToSeleniumCookie).orElse(null);
    }

    @Override
    public Timeouts timeouts() {
      return timeoutConfig;
    }

    @Override
    public Window window() {
      return windowConfig;
    }

    @Override
    public Logs logs() {
      return logs;
    }
  }

  @NullMarked
  private static class JsoupWebDriverTimeoutConfig implements Timeouts {
    @Override
    public Timeouts implicitlyWait(long time, TimeUnit unit) {
      return this;
    }

    @Override
    public Timeouts implicitlyWait(Duration duration) {
      return this;
    }

    @Override
    public Duration getImplicitWaitTimeout() {
      return Duration.ZERO;
    }

    @Override
    public Timeouts setScriptTimeout(long time, TimeUnit unit) {
      return this;
    }

    @Override
    public Timeouts scriptTimeout(Duration duration) {
      return this;
    }

    @Override
    public Duration getScriptTimeout() {
      return Duration.ZERO;
    }

    @Override
    public Timeouts pageLoadTimeout(long time, TimeUnit unit) {
      return this;
    }

    @Override
    public Timeouts pageLoadTimeout(Duration duration) {
      return this;
    }

    @Override
    public Duration getPageLoadTimeout() {
      return Duration.ZERO;
    }
  }

  private static class JsoupWebDriverLogs implements Logs {
    @Override
    public LogEntries get(String logType) {
      return new LogEntries(List.of());
    }

    @Override
    public Set<String> getAvailableLogTypes() {
      return Set.of();
    }
  }

  @NullMarked
  private static class JsoupWebDriverWindowConfig implements Window {

    @Override
    public Dimension getSize() {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void setSize(Dimension targetSize) {

    }

    @Override
    public Point getPosition() {
      return new Point(0, 0);
    }

    @Override
    public void setPosition(Point targetPosition) {

    }

    @Override
    public void maximize() {

    }

    @Override
    public void minimize() {

    }

    @Override
    public void fullscreen() {

    }
  }

  @NullMarked
  private class JsoupWebDriverNavigation implements Navigation {
    @Override
    public void back() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forward() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void to(String url) {
      get(url);
    }

    @Override
    public void to(URL url) {
      get(url.toString());
    }

    @Override
    public void refresh() {
      try {
        lastPage = new Page(lastPage.response.connection().newRequest().execute().parse());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private URI getCurrentUri() {
    try {
      return lastPage.response.connection().request().url().toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private Evaluator toEvaluator(By by) {
    if (!(by instanceof By.Remotable byRemotable)) throw new InvalidSelectorException("Invalid selector: " + by);

    var selector = getSelector(byRemotable);
    if (selector.isEmpty()) throw new InvalidSelectorException("Invalid selector: " + by);

    return switch (by) {
      case By.ById ignored -> new Evaluator.Id(selector);
      case By.ByTagName ignored -> new Evaluator.Tag(selector);
      case By.ByLinkText ignored -> new LinkTextEvaluator(selector);
      case By.ByClassName ignored -> new Evaluator.Class(selector);
      case By.ByName ignored -> new Evaluator.AttributeWithValue("name", selector);
      case By.ByCssSelector ignored -> parseCss(selector);
      case By.ByPartialLinkText ignored -> new PartialLinkTextEvaluator(selector);
      default -> throw new UnsupportedOperationException("Not implemented: " + by);
    };
  }

  private static String getSelector(By.Remotable byRemotable) {
    return byRemotable.getRemoteParameters().value().toString();
  }

  private class Page {
    public final Document response;
    public final JsoupWebElement rootElement;
    private final Map<Element, JsoupWebElement> elements = new HashMap<>();

    private Page(Document response) {
      this.response = response;
      this.rootElement = new JsoupWebElement(response);
    }

    public JsoupWebElement getOrWrapElement(Element element) {
      return elements.computeIfAbsent(element, JsoupWebElement::new);
    }

    public List<WebElement> getOrWrapElements(Elements elements) {
      return elements.stream()
        .map(element -> (WebElement) this.elements.computeIfAbsent(element, JsoupWebElement::new))
        .collect(Collectors.toList());
    }
  }

  private static final String ORIGINAL_VALUE_ATTR = "__jsoupwebdriver_original_value";
  private static final List<String> BOOLEAN_ATTRIBUTES = Arrays.asList("async", "autofocus", "autoplay", "checked",
    "compact", "complete", "controls", "declare", "defaultchecked", "defaultselected", "defer", "disabled",
    "draggable", "ended", "formnovalidate", "hidden", "indeterminate", "iscontenteditable", "ismap", "itemscope",
    "loop", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "paused", "pubdate",
    "readonly", "required", "reversed", "scoped", "seamless", "seeking", "selected", "truespeed", "willvalidate");

}
