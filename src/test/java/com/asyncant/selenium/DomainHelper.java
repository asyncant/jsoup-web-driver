package com.asyncant.selenium;

public class DomainHelper {
  public String getHostName() {
    return "first.asyncant.localhost";
  }

  public String getAlternateHostName() {
    return "second.asyncant.localhost";
  }

  public boolean checkHasValidAlternateHostname() {
    return true;
  }

  public String getUrlForFirstValidHostname(String path) {
    return "http://first.asyncant.localhost:8080" + path;
  }

  public String getUrlForSecondValidHostname(String path) {
    return "http://second.asyncant.localhost:8080" + path;
  }
}
