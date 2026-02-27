package com.open.source.mask.json.matcher;

import java.util.List;

/**
 * CompositeFieldMatcher
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public class CompositeFieldMatcher implements FieldMatcher {

  public enum MatchType {
    AND,
    OR
  }

  private final List<FieldMatcher> matchers;
  private final MatchType matchType;

  public CompositeFieldMatcher(List<FieldMatcher> matchers, MatchType matchType) {
    this.matchers = matchers;
    this.matchType = matchType;
  }

  @Override
  public boolean matches(String fieldName) {
    if (matchType == MatchType.AND) {
      return matchers.stream().allMatch(m -> m.matches(fieldName));
    } else {
      return matchers.stream().anyMatch(m -> m.matches(fieldName));
    }
  }
}
