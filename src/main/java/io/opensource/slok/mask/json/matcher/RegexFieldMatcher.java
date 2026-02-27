package io.opensource.slok.mask.json.matcher;

import java.util.regex.Pattern;

/**
 * RegexFieldMatcher
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */

public class RegexFieldMatcher implements FieldMatcher {

  private final Pattern pattern;

  public RegexFieldMatcher(String regex) {
    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  @Override
  public boolean matches(String fieldName) {
    return fieldName != null && pattern.matcher(fieldName).matches();
  }
}
