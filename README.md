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
