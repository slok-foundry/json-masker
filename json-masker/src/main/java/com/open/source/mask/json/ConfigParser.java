package com.open.source.mask.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.source.mask.json.matcher.ContainsFieldMatcher;
import com.open.source.mask.json.matcher.FieldMatcher;
import com.open.source.mask.json.matcher.RegexFieldMatcher;
import com.open.source.mask.json.strategy.FullMaskStrategy;
import com.open.source.mask.json.strategy.LengthBasedMaskStrategy;
import com.open.source.mask.json.strategy.MaskingStrategy;
import com.open.source.mask.json.strategy.MiddleMaskStrategy;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ConfigParser
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */

public class ConfigParser {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static Map<FieldMatcher, FieldMaskingRule> parseConfig(String configJson) throws Exception {
    JsonNode root = mapper.readTree(configJson);
    Map<FieldMatcher, FieldMaskingRule> map = new LinkedHashMap<>();

    for (JsonNode rule : root.withArray("rules")) {
      // Matcher
      JsonNode match = rule.get("match");
      String matchType = match.get("type").asText();
      String matchValue = match.get("value").asText();
      FieldMatcher matcher;
      if ("contains".equalsIgnoreCase(matchType)) {
        matcher = new ContainsFieldMatcher(matchValue);
      } else if ("regex".equalsIgnoreCase(matchType)) {
        matcher = new RegexFieldMatcher(matchValue);
      } else {
        throw new IllegalArgumentException("Unsupported matcher type: " + matchType);
      }

      // Strategy
      JsonNode strat = rule.get("strategy");
      String stratType = strat.get("type").asText();
      String maskChar = strat.get("maskChar").asText("*"); // default *
      MaskingStrategy strategy;
      if ("full".equalsIgnoreCase(stratType)) {
        strategy = new FullMaskStrategy();
      } else if ("middle".equalsIgnoreCase(stratType)) {
        int keepLeft = strat.has("keepLeft") ? strat.get("keepLeft").asInt() : 1;
        int keepRight = strat.has("keepRight") ? strat.get("keepRight").asInt() : 1;
        strategy = new MiddleMaskStrategy(keepLeft, keepRight);
      }else if ("length".equalsIgnoreCase(stratType)) {
        strategy = new LengthBasedMaskStrategy();
      } else {
        throw new IllegalArgumentException("Unsupported strategy type: " + stratType);
      }

      FieldMaskingRule maskingRule = new FieldMaskingRule(strategy, maskChar);
      map.put(matcher, maskingRule);
    }

    return map;
  }
}

