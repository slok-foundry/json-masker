package io.opensource.slok.mask.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opensource.slok.mask.json.matcher.ContainsFieldMatcher;
import io.opensource.slok.mask.json.matcher.FieldMatcher;
import io.opensource.slok.mask.json.matcher.RegexFieldMatcher;
import io.opensource.slok.mask.json.strategy.MaskingStrategy;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ConfigParser
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */

public class ConfigParser {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final Properties strategyMappings = new Properties();
  private static final Properties matcherMappings = new Properties();

  static {
    try (InputStream is = ConfigParser.class.getClassLoader()
        .getResourceAsStream("masking-strategies.properties")) {
      if (is != null) {
        strategyMappings.load(is);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load strategy mappings", e);
    }
    
    try (InputStream is = ConfigParser.class.getClassLoader()
        .getResourceAsStream("field-matchers.properties")) {
      if (is != null) {
        matcherMappings.load(is);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load matcher mappings", e);
    }
  }

  public static Map<FieldMatcher, FieldMaskingRule> parseConfig(String configJson) throws Exception {
    JsonNode root = mapper.readTree(configJson);
    Map<FieldMatcher, FieldMaskingRule> map = new LinkedHashMap<>();

    for (JsonNode rule : root.withArray("rules")) {
      // Matcher - Dynamic loading
      JsonNode match = rule.get("match");
      String matchType = match.get("type").asText();
      String matchValue = match.get("value").asText();
      FieldMatcher matcher = loadMatcher(matchType, matchValue);

      // Strategy - Dynamic loading
      JsonNode strat = rule.get("strategy");
      String stratType = strat.get("type").asText();
      String maskChar = strat.get("maskChar").asText("*");
      MaskingStrategy strategy = loadStrategy(stratType, strat);

      FieldMaskingRule maskingRule = new FieldMaskingRule(strategy, maskChar);
      map.put(matcher, maskingRule);
    }

    return map;
  }

  private static FieldMatcher loadMatcher(String matchType, String matchValue) throws Exception {
    String className = matcherMappings.getProperty(matchType.toLowerCase());
    
    if (className == null) {
      throw new IllegalArgumentException("Matcher not found: " + matchType);
    }
    
    try {
      Class<?> clazz = Class.forName(className);
      Constructor<?> constructor = clazz.getConstructor(String.class);
      return (FieldMatcher) constructor.newInstance(matchValue);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Matcher class not found: " + className, e);
    }
  }

  private static MaskingStrategy loadStrategy(String stratType, JsonNode config) throws Exception {
    String className = strategyMappings.getProperty(stratType.toLowerCase());
    
    if (className == null) {
      throw new IllegalArgumentException("Strategy not found: " + stratType);
    }
    
    try {
      Class<?> clazz = Class.forName(className);
      
      // Try constructor with parameters for strategies like MiddleMaskStrategy
      if ("middle".equalsIgnoreCase(stratType)) {
        int keepLeft = config.has("keepLeft") ? config.get("keepLeft").asInt() : 1;
        int keepRight = config.has("keepRight") ? config.get("keepRight").asInt() : 1;
        Constructor<?> constructor = clazz.getConstructor(int.class, int.class);
        return (MaskingStrategy) constructor.newInstance(keepLeft, keepRight);
      }
      
      // Default: no-arg constructor
      return (MaskingStrategy) clazz.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Strategy class not found: " + className, e);
    }
  }
}

