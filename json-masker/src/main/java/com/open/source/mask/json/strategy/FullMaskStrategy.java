package com.open.source.mask.json.strategy;

/**
 * FullMaskStrategy
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public class FullMaskStrategy implements MaskingStrategy {

  @Override
  public String mask(String input, String maskChar) {
    if (input == null || maskChar == null) return input;
    return maskChar.repeat(input.length());
  }
}
