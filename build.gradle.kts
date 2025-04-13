plugins {
  id("java")
}

group = "com.asyncant.selenium"
version = "0.4-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform(libs.junitBom))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation(libs.assertJ)
  implementation(libs.jsoup)
  implementation(libs.seleniumApi)


  testImplementation(libs.seleniumHtmlUnitDriver)
  testImplementation(libs.seleniumFirefoxDriver)
  testImplementation(libs.seleniumChromeDriver)
}

tasks.test {
  useJUnitPlatform()
}
