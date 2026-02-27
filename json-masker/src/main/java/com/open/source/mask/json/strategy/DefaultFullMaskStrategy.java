package com.open.source.mask.json.strategy;

/**
 * DefaultFullMaskStrategy
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public class DefaultFullMaskStrategy implements MaskingStrategy {

  @Override
  public String mask(String originalValue, String maskChar) {
    if (originalValue == null) return null;
    return maskChar.repeat(originalValue.length());
  }
}
