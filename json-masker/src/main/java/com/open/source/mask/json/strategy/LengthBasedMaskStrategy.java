package com.open.source.mask.json.strategy;

import org.apache.commons.lang3.StringUtils;

/**
 * DynamicMaskStrategy
 * Masks the middle part of a string while keeping rest dynamically based on length
 * a configurable number of characters visible at the start and end.
 * Example:
 *   keepLeft = 2, keepRight = 2, input = "john.doe@example.com"
 *   => "jo***************om"
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public class LengthBasedMaskStrategy implements MaskingStrategy {

  @Override
  public String mask(String plain, String maskChar) {
    if (StringUtils.isBlank(plain)) {
      return plain;
    }

    if (StringUtils.isBlank(plain)) {
      return maskChar;
    }

    int length = plain.length();

    if (length > 15) {
      // Keep 5 at start and 5 at end
      return maskWithKeep(plain, 5, 5, maskChar);
    } else if (length >= 9) {
      // Keep 3 at start and 3 at end
      return maskWithKeep(plain, 3, 3, maskChar);
    } else if (length > 4) {
      // Keep 3 at start, mask rest
      return maskWithKeep(plain, 3, 0, maskChar);
    } else {
      // Too short → fully mask
      return maskChar;
    }
  }

  /**
   * Utility method to mask with keep rules
   * @param input        original string
   * @param keepStart    number of chars to keep at start
   * @param keepEnd      number of chars to keep at end
   * @param maskChars    mask string
   * @return masked string
   */
  private static String maskWithKeep(String input, int keepStart, int keepEnd, String maskChars) {
    int length = input.length();

    if (keepStart + keepEnd >= length) {
      // if rules try to keep more than available, just return mask
      return maskChars;
    }

    String prefix = input.substring(0, keepStart);
    String suffix = keepEnd > 0 ? input.substring(length - keepEnd) : "";
    return prefix + maskChars + suffix;
  }

}
