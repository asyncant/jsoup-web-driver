rootProject.name = "jsoup-web-driver"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }

  versionCatalogs {
    create("libs") {
      library("junitBom", "org.junit:junit-bom:5.10.0")
      library("assertJ", "org.assertj:assertj-core:3.27.2")

      library("seleniumApi", "org.seleniumhq.selenium:selenium-api:4.31.0")
      library("seleniumHtmlUnitDriver", "org.seleniumhq.selenium:htmlunit3-driver:4.30.0")
      library("seleniumFirefoxDriver", "org.seleniumhq.selenium:selenium-firefox-driver:4.31.0")
      library("seleniumChromeDriver", "org.seleniumhq.selenium:selenium-chrome-driver:4.31.0")

      library("jsoup", "org.jsoup:jsoup:1.18.3")
    }
  }
}
