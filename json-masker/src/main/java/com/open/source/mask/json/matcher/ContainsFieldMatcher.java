package com.open.source.mask.json.matcher;


import java.util.Locale;
/**
 * ContainsFieldMatcher
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */


public class ContainsFieldMatcher implements FieldMatcher {
  private final String keyword;

  public ContainsFieldMatcher(String keyword) {
    this.keyword = keyword.toLowerCase(Locale.ROOT);
  }

  @Override
  public boolean matches(String fieldName) {
    return fieldName != null && fieldName.toLowerCase(Locale.ROOT).contains(keyword);
  }
}

