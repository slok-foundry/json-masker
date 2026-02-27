package com.open.source.mask.json.strategy;

/**
 * MaskingStrategy
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public interface MaskingStrategy {
  String mask(String input, String maskChar);
}
