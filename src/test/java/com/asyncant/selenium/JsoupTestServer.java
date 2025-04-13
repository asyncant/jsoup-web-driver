package com.asyncant.selenium;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JsoupTestServer {
  private HttpServer server;

  public JsoupTestServer() {
    // Without this, the sun httpserver can delay requests up to 40 ms on JRE <= 21 (haven't experienced it on 23).
    // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7068416
    System.setProperty("sun.net.httpserver.nodelay", "true");
  }

  void start() {
    try {
      server = HttpServer.create(new InetSocketAddress(8080), 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    server.createContext("/", new ResourceHtmlHttpHandler("/"));
    server.createContext("/click_tests", new ResourceHtmlHttpHandler("/click_tests/"));
    server.createContext("/common/cookie", new CookieHttpHandler());
    server.createContext("/common", new EmptyResourceHttpHandler());
    server.createContext("/encoding", new EncodingHttpHandler());
    server.createContext("/redirect", new RedirectHttpHandler());
    server.setExecutor(null);
    server.start();
  }

  void stop() {
    server.stop(0);
  }

  static class ResourceHtmlHttpHandler implements HttpHandler {
    private final String basePath;

    ResourceHtmlHttpHandler(String basePath) {
      this.basePath = basePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String path = exchange.getRequestURI().getPath();
      if (!path.startsWith(basePath) || path.substring(basePath.length()).contains("/")) throw new RuntimeException("Invalid path: " + path);

      byte[] byteResponse = getResourceBytes(path);
      if (byteResponse == null) {
        exchange.getResponseHeaders().put("Content-Type", List.of("text/html"));
        exchange.sendResponseHeaders(404, 0);
        byteResponse = "Not found".getBytes(UTF_8);
      }
      else {
        if (path.endsWith(".xml")) exchange.getResponseHeaders().put("Content-Type", List.of("text/xml"));
        else if (path.startsWith("/utf8")) exchange.getResponseHeaders().put("Content-Type", List.of("text/html;charset=UTF-8"));
        else exchange.getResponseHeaders().put("Content-Type", List.of("text/html"));
        exchange.sendResponseHeaders(HTTP_OK, byteResponse.length);
      }

      var os = exchange.getResponseBody();
      os.write(byteResponse);
      os.close();
    }

    private byte[] getResourceBytes(String fileName) {
      try (InputStream stream = getClass().getResourceAsStream("/org/openqa/selenium" + fileName)) {
        if (stream == null) return null;
        return stream.readAllBytes();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static class EmptyResourceHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      byte[] byteResponse = new byte[0];
      exchange.getResponseHeaders().put("Content-Type", List.of("text/html"));
      exchange.sendResponseHeaders(HTTP_OK, byteResponse.length);
      var os = exchange.getResponseBody();
      os.write(byteResponse);
      os.close();
    }
  }

  static class EncodingHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      @SuppressWarnings("UnnecessaryUnicodeEscape")
      String text =
        "<html><title>Character encoding (UTF 16)</title>"
          + "<body><p id='text'>"
          + "\u05E9\u05DC\u05D5\u05DD" // "Shalom"
          + "</p></body></html>";

      byte[] byteResponse = text.getBytes(UTF_16LE);
      exchange.getResponseHeaders().put("Content-Type", List.of("text/html;charset=UTF-16LE"));
      exchange.sendResponseHeaders(HTTP_OK, byteResponse.length);
      var os = exchange.getResponseBody();
      os.write(byteResponse);
      os.close();
    }
  }

  static class RedirectHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      exchange.getResponseHeaders().put("Content-Type", List.of("text/html"));
      exchange.getResponseHeaders().put("Location", List.of("resultPage.html"));
      exchange.sendResponseHeaders(HTTP_MOVED_TEMP, 0);
      var os = exchange.getResponseBody();
      os.close();
    }
  }
}
