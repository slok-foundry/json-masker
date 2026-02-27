package com.open.source.mask.json;


import com.open.source.mask.json.matcher.CompositeFieldMatcher;
import com.open.source.mask.json.matcher.FieldMatcher;
import com.open.source.mask.json.matcher.RegexFieldMatcher;
import com.open.source.mask.json.strategy.FullMaskStrategy;
import com.open.source.mask.json.strategy.MiddleMaskStrategy;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * Main
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
public class Main {

  public static void main(String[] args) throws Exception {
    String json = """
        {
          "username": "JohnDoe",
          "user_password": "secret123",
          "details": {
            "name": "Jonathan"
          }
        }
        """;

    json = FileUtils.readFileToString(new File("src/main/resources/testJson.json"), StandardCharsets.UTF_8);

    FieldMatcher regexMatcher = new RegexFieldMatcher(".*password.*");
    FieldMatcher containsName = field -> field.toLowerCase(Locale.ROOT).contains("name");

    FieldMatcher composite = new CompositeFieldMatcher(
        List.of(regexMatcher, containsName),
        CompositeFieldMatcher.MatchType.OR
    );

    Map<String, FieldMaskingRule> rules = Map.of(
        "user_password", new FieldMaskingRule(new FullMaskStrategy(), "#"),
        "name", new FieldMaskingRule(new MiddleMaskStrategy(), "*")
    );

    JsonMasker masker = new JsonMasker();
//    String masked = masker.maskJson(json, composite, rules);

//    System.out.println(masked);
  }

}
