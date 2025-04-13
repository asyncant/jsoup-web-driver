package com.asyncant.selenium;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class CookieHttpHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    Map<String, String> parsedQuery = parseQuery(query);
    switch (parsedQuery.get("action")) {
      case "deleteAll" -> {
        var cookieHeader = exchange.getRequestHeaders().get("Cookie");
        if (cookieHeader != null) addDeleteCookieHeaders(exchange, cookieHeader);
      }
      case "add" -> {
        String cookie = parsedQuery.get("name") + "=" + parsedQuery.get("value");
        if (parsedQuery.containsKey("path")) cookie += ";path=" + parsedQuery.get("path");
        if ("true".equals(parsedQuery.get("httpOnly"))) cookie += ";HttpOnly";
        exchange.getResponseHeaders().put("Set-Cookie", List.of(cookie));
      }
    }

    exchange.getResponseHeaders().put("Content-Type", List.of("text/html"));
    exchange.sendResponseHeaders(200, 0);
    var os = exchange.getResponseBody();
    os.close();
  }

  private void addDeleteCookieHeaders(HttpExchange exchange, List<String> cookieHeader) {
    for (String cookieName : getCookieNames(cookieHeader.getFirst())) {
      exchange.getResponseHeaders().put("Set-Cookie", List.of(cookieName + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT"));
    }
  }

  private Map<String, String> parseQuery(String query) {
    return Arrays.stream(query.split("&")).map(it -> it.split("=")).collect(Collectors.toMap(it -> it[0], it -> it[1]));
  }

  private List<String> getCookieNames(String cookieHeader) {
    return Arrays.stream(cookieHeader.split(" ")).map(it -> it.split(";")[0].split("=")[0]).collect(toList());
  }
}
