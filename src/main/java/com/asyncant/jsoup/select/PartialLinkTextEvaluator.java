package com.asyncant.jsoup.select;

import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.jspecify.annotations.NullMarked;

/**
 * Matches a link containing the given text.
 */
@NullMarked
public class PartialLinkTextEvaluator extends Evaluator {
  private final String text;

  public PartialLinkTextEvaluator(String text) {
    this.text = text;
  }

  @Override
  public boolean matches(Element root, Element element) {
    if (!element.tagName().equals("a")) return false;
    return element.text().contains(text);
  }

  @Override
  public String toString() {
    return String.format(":link-text(%s)", text);
  }
}
