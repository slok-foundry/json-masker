package io.opensource.slok.mask.json;


import io.opensource.slok.mask.json.strategy.MaskingStrategy;

/**
 * FieldMaskingRule
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */

public class FieldMaskingRule {
  private final MaskingStrategy strategy;
  private final String maskChar;

  public FieldMaskingRule(MaskingStrategy strategy, String maskChar) {
    this.strategy = strategy;
    this.maskChar = maskChar;
  }

  public String apply(String input) {
    return strategy.mask(input, maskChar);
  }
}
