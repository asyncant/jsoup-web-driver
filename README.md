# jsoup-web-driver

A [Selenium](https://www.selenium.dev) webdriver that uses [jsoup](https://jsoup.org) to parse HTML pages and simulates
a webdriver on top of it to enable a short feedback loop for rapid iteration when implementing functionality in apps
using server rendered webpages.

This driver's main focus is to speed up the development process by (continuously) running the functional tests quickly.
It is intended to run functional/sequence tests (e.g. login, change X, change Y, is Z correct?) and is not suited for
display tests (e.g. what color is X? is Y left of Z?). After functionality is implemented, the same functional tests can
be used and run with the drivers for (actual) browsers to ensure the webpages work as expected in all target browsers.

In short, it sacrifices features, most prominently CSS and Javascript, for speed.

# Feature comparison

| Feature                              | jsoup driver                         | Chrome driver      | Firefox driver     | HTMLUnit driver                    |
|--------------------------------------|--------------------------------------|--------------------|--------------------|------------------------------------|
| CSS                                  | :x:                                  | :white_check_mark: | :white_check_mark: | :negative_squared_cross_mark: Most |
| Javascript                           | :x:                                  | :white_check_mark: | :white_check_mark: | :negative_squared_cross_mark: Most |
| HTML                                 | :negative_squared_cross_mark: Common | :white_check_mark: | :white_check_mark: | :negative_squared_cross_mark: Most |
| 357 HTML-only tests, driver per test | 575ms                                | 43s 440 ms         | 3m 54s 12ms        | 2s 95ms (with errors)              |
| 357 HTML-only tests, re-used driver  | 564ms                                | 14s 150ms          | 19s 813ms          | 2s 452ms (with errors)             |

