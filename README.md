# JSON Masker

A lightweight Java library for masking sensitive data in JSON payloads. Protect PII, financial data, and credentials in logs, APIs, and data pipelines.

## Features

- 🔒 **Multiple Masking Strategies**: Full, middle, and length-based masking
- 🎯 **Flexible Field Matching**: Regex and contains-based field matchers
- ⚙️ **Configuration-Driven**: JSON-based masking rules
- 🚀 **High Performance**: Efficient recursive JSON traversal
- 📦 **Minimal Dependencies**: Only Jackson for JSON processing

## Installation

### Maven

```xml
<dependency>
    <groupId>io.opensource.slok</groupId>
    <artifactId>json-masker</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.opensource.slok:json-masker:0.1.0-SNAPSHOT'
```

## Quick Start

### Basic Usage

```java
import json.mask.io.opensource.slok.JsonMasker;

JsonMasker masker = new JsonMasker();
String jsonInput = "{\"email\":\"user@example.com\",\"ssn\":\"123-45-6789\"}";
String configJson = """
    {
      "rules": [
        {
          "match": {"type": "contains", "value": "email"},
          "strategy": {"type": "middle", "keepLeft": 2, "keepRight": 2, "maskChar": "*"}
        },
        {
          "match": {"type": "contains", "value": "ssn"},
          "strategy": {"type": "full", "maskChar": "*"}
        }
      ]
    }
    """;

String masked = masker.maskJson(jsonInput, configJson);
// Output: {"email":"us***********om","ssn":"***********"}
```

## Masking Strategies

### Full Masking
Replaces entire value with mask characters.

```json
{
  "match": {"type": "contains", "value": "password"},
  "strategy": {"type": "full", "maskChar": "*"}
}
```
**Input**: `"password": "Secret123"`  
**Output**: `"password": "*********"`

### Middle Masking
Keeps specified characters at start and end, masks the middle.

```json
{
  "match": {"type": "contains", "value": "cardNumber"},
  "strategy": {"type": "middle", "keepLeft": 4, "keepRight": 4, "maskChar": "*"}
}
```
**Input**: `"cardNumber": "4532015112830366"`  
**Output**: `"cardNumber": "4532********0366"`

### Length-Based Masking
Dynamically adjusts masking based on value length.

```json
{
  "match": {"type": "contains", "value": "address"},
  "strategy": {"type": "length", "maskChar": "X"}
}
```

**Masking Rules by Length:**

| Length Range | Behavior | Example Input | Example Output |
|--------------|----------|---------------|----------------|
| > 15 chars   | Keep 5 at start, 5 at end | `"1234567890123456"` | `"12345X6789"` |
| 9-15 chars   | Keep 3 at start, 3 at end | `"123456789"` | `"123X789"` |
| 5-8 chars    | Keep 3 at start only | `"12345"` | `"123X"` |
| ≤ 4 chars    | Fully mask | `"1234"` | `"X"` |

**Input**: `"address": "123 Main Street"`  
**Output**: `"address": "123XX"`

### Custom Masking Strategy
Implement MaskingStrategy interface for custom masking logic.

```java
public class CustomMaskStrategy implements MaskingStrategy {
  @Override
  public String mask(String plain, String maskChar) {
    // Your custom masking logic
    return plain.replaceAll("[a-zA-Z]", maskChar);
  }
}
```

## Field Matchers

### Contains Matcher
Matches fields containing the specified substring (case-insensitive).

```json
{
  "match": {"type": "contains", "value": "email"}
}
```
Matches: `email`, `userEmail`, `workEmail`, `EMAIL_ADDRESS`

### Regex Matcher
Matches fields using regular expressions.

```json
{
  "match": {"type": "regex", "value": ".*(password|pwd|secret).*"}
}
```
Matches: `password`, `userPassword`, `apiSecret`, `pwd`

## Configuration Examples

### Comprehensive PII Masking

```json
{
  "rules": [
    {
      "match": {"type": "regex", "value": ".*(ssn|social.*security|tax.*id).*"},
      "strategy": {"type": "full", "maskChar": "*"}
    },
    {
      "match": {"type": "regex", "value": ".*(card.*number|account.*number).*"},
      "strategy": {"type": "middle", "keepLeft": 4, "keepRight": 4, "maskChar": "*"}
    },
    {
      "match": {"type": "regex", "value": ".*(password|api.*key|secret).*"},
      "strategy": {"type": "full", "maskChar": "*"}
    },
    {
      "match": {"type": "contains", "value": "email"},
      "strategy": {"type": "middle", "keepLeft": 2, "keepRight": 2, "maskChar": "*"}
    },
    {
      "match": {"type": "regex", "value": ".*(phone|mobile).*"},
      "strategy": {"type": "middle", "keepLeft": 3, "keepRight": 4, "maskChar": "*"}
    }
  ]
}
```

### Payment Data Masking

```json
{
  "rules": [
    {
      "match": {"type": "contains", "value": "cvv"},
      "strategy": {"type": "full", "maskChar": "*"}
    },
    {
      "match": {"type": "regex", "value": ".*(card|pan).*"},
      "strategy": {"type": "middle", "keepLeft": 6, "keepRight": 4, "maskChar": "*"}
    },
    {
      "match": {"type": "contains", "value": "iban"},
      "strategy": {"type": "middle", "keepLeft": 4, "keepRight": 4, "maskChar": "*"}
    }
  ]
}
```

## Use Cases

- **API Logging**: Mask sensitive data in request/response logs
- **Data Export**: Anonymize data for analytics or testing
- **Compliance**: GDPR, PCI-DSS, HIPAA data protection
- **Audit Trails**: Secure logging of user activities
- **Data Sharing**: Safe data exchange between systems

## Building from Source

```bash
git clone https://github.com/yourusername/json-masker.git
cd json-masker
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Requirements

- Java 17 or higher
- Maven 3.6+

## Extensibility

### Adding Custom Strategies

The library uses resource files to map strategy names to implementations. To add a custom strategy:

1. Implement the `MaskingStrategy` interface:
```java
public class EmailMaskStrategy implements MaskingStrategy {
  @Override
  public String mask(String plain, String maskChar) {
    // Custom logic
    return plain.replaceAll("@.*", "@***");
  }
}
```

2. Add mapping to `src/main/resources/masking-strategies.properties`:
```properties
email=com.example.EmailMaskStrategy
```

3. Use in configuration:
```json
{
  "match": {"type": "contains", "value": "email"},
  "strategy": {"type": "email", "maskChar": "*"}
}
```

### Adding Custom Matchers

Similarly, custom field matchers can be added:

1. Implement the `FieldMatcher` interface:
```java
public class ExactFieldMatcher implements FieldMatcher {
  private final String fieldName;
  
  public ExactFieldMatcher(String fieldName) {
    this.fieldName = fieldName;
  }
  
  @Override
  public boolean matches(String field) {
    return fieldName.equals(field);
  }
}
```

2. Add mapping to `src/main/resources/field-matchers.properties`:
```properties
exact=com.example.ExactFieldMatcher
```

3. Use in configuration:
```json
{
  "match": {"type": "exact", "value": "password"},
  "strategy": {"type": "full", "maskChar": "*"}
}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and questions, please open an issue on GitHub.