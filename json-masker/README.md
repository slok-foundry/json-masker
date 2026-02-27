# JSON Masker

A lightweight Java library for masking sensitive data in JSON payloads. Protect PII, financial data, and credentials in logs, APIs, and data pipelines.

## Features

- 🔒 **Multiple Masking Strategies**: Full, middle, and length-based masking
- 🎯 **Flexible Field Matching**: Regex and contains-based field matchers
- ⚙️ **Configuration-Driven**: JSON-based masking rules
- 🚀 **High Performance**: Efficient recursive JSON traversal
- 📦 **Zero Dependencies**: Only Jackson for JSON processing

## Installation

### Maven

```xml
<dependency>
    <groupId>com.open.source</groupId>
    <artifactId>json-masker</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.open.source:json-masker:0.1.0-SNAPSHOT'
```

## Quick Start

### Basic Usage

```java
import com.open.source.mask.json.JsonMasker;

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
**Input**: `"address": "123 Main Street"`  
**Output**: `"address": "123XX"`

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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and questions, please open an issue on GitHub.


