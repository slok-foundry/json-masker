package io.opensource.slok.mask.json.strategy;

import org.apache.commons.lang3.StringUtils;

/**
 * MiddleMaskStrategy
 * Masks the middle part of a string while keeping
 * a configurable number of characters visible at the start and end.
 * Example:
 *   keepLeft = 2, keepRight = 2, input = "john.doe@example.com"
 *   => "jo***************om"
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public class MiddleMaskStrategy implements MaskingStrategy {

  private final int keepLeft;
  private final int keepRight;

  /**
   * Default constructor:
   * Keeps first 2 and last 2 characters.
   */
  public MiddleMaskStrategy() {
    this.keepLeft = 2;
    this.keepRight = 2;
  }

  /**
   * Custom constructor:
   * Allows caller to configure how many characters
   * to keep visible on left and right.
   */
  public MiddleMaskStrategy(int keepLeft, int keepRight) {
    this.keepLeft = Math.max(0, keepLeft);
    this.keepRight = Math.max(0, keepRight);
  }

  @Override
  public String mask(String input, String maskChar) {
    if (StringUtils.isBlank(input)) {
      return input;
    }

    int length = input.length();

    // If too short to apply middle mask, mask all
    if (length <= keepLeft + keepRight) {
      return maskChar.repeat(length);
    }

    // Split into visible + masked parts
    String left = input.substring(0, keepLeft);
    String right = input.substring(length - keepRight);
    String middle = maskChar.repeat(length - keepLeft - keepRight);

    return left + middle + right;
  }
}
