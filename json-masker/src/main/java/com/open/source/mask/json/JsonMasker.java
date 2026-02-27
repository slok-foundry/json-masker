package com.open.source.mask.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.source.mask.json.matcher.FieldMatcher;
import java.util.Iterator;
import java.util.Map;

public class JsonMasker {

  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Recursively masks fields in a JsonNode structure.
   *
   * @param root              JSON tree (can be object or array)
   * @param matcherStrategyMap Map of field matchers → masking rules
   * @return                  The modified JSON node with fields masked
   */
  private JsonNode maskFields(JsonNode root,
      Map<FieldMatcher, FieldMaskingRule> matcherStrategyMap) {

    if (root == null || root.isNull()) return root;

    if (root.isObject()) {
      ObjectNode objectNode = (ObjectNode) root;
      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();

      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> entry = fields.next();
        String fieldName = entry.getKey();
        JsonNode value = entry.getValue();

        if (value.isTextual()) {
          for (Map.Entry<FieldMatcher, FieldMaskingRule> ruleEntry : matcherStrategyMap.entrySet()) {
            FieldMatcher matcher = ruleEntry.getKey();
            if (matcher.matches(fieldName)) {
              FieldMaskingRule rule = ruleEntry.getValue();
              objectNode.put(fieldName, rule.apply(value.asText()));
              break; // stop at first match
            }
          }
        } else {
          maskFields(value, matcherStrategyMap);
        }
      }

    } else if (root.isArray()) {
      for (JsonNode item : root) {
        maskFields(item, matcherStrategyMap);
      }
    }

    return root;
  }

  /**
   * Masks a raw JSON payload using a field strategy map.
   */
  public String maskJson(String jsonInput,
      Map<FieldMatcher, FieldMaskingRule> fieldStrategyMap) throws Exception {
    JsonNode root = mapper.readTree(jsonInput);
    JsonNode masked = maskFields(root, fieldStrategyMap);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(masked);
  }

  /**
   * Masks a raw JSON payload using config JSON string.
   *
   * @param jsonInput   The raw payload JSON string
   * @param configJson  Config JSON defining matchers and strategies
   * @return            Masked JSON as formatted string
   */
  public String maskJson(String jsonInput, String configJson) throws Exception {
    Map<FieldMatcher, FieldMaskingRule> fieldStrategyMap = ConfigParser.parseConfig(configJson);
    return maskJson(jsonInput, fieldStrategyMap);
  }
}
