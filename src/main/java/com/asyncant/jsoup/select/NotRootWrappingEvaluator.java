package com.asyncant.jsoup.select;

import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.jspecify.annotations.NullMarked;

/**
 * Wraps an evaluator so that it doesn't match the root element.
 */
@NullMarked
public class NotRootWrappingEvaluator extends Evaluator {
  private final Evaluator wrapped;

  public NotRootWrappingEvaluator(Evaluator wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public boolean matches(Element root, Element element) {
    if (root == element) return false;
    return wrapped.matches(root, element);
  }
}
