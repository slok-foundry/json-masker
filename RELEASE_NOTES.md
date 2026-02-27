# Release Notes - JSON Masker v1.0.0

## 🎉 Initial Release

We're excited to announce the first stable release of JSON Masker - a lightweight Java library for masking sensitive data in JSON payloads.

## ✨ Features

### Masking Strategies
- **Full Masking** - Complete replacement of sensitive values
- **Middle Masking** - Preserve start/end characters, mask the middle
- **Length-Based Masking** - Dynamic masking based on value length with intelligent rules

### Field Matching
- **Contains Matcher** - Case-insensitive substring matching
- **Regex Matcher** - Pattern-based field matching with full regex support

### Extensibility
- **Plugin Architecture** - Resource-based configuration for custom strategies and matchers
- Easy to extend with custom masking strategies via `MaskingStrategy` interface
- Easy to extend with custom field matchers via `FieldMatcher` interface

### Configuration
- JSON-based configuration for masking rules
- Flexible rule composition with multiple matchers and strategies
- Support for custom mask characters

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>io.github.slok-foundry</groupId>
    <artifactId>json-masker</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'io.github.slok-foundry:json-masker:1.0.0'
```

## 🔧 Technical Details

- **Java Version**: 17+
- **Dependencies**: Jackson 2.18.2, Apache Commons Lang3 3.17.0
- **Build Tool**: Maven 3.6+
- **License**: MIT

## 📚 Documentation

Full documentation available in the [README](https://github.com/slok-foundry/json-masker/blob/main/README.md)

## 🎯 Use Cases

- API request/response logging
- PII data protection (GDPR, CCPA compliance)
- Payment data masking (PCI-DSS compliance)
- Healthcare data protection (HIPAA compliance)
- Audit trail security
- Data anonymization for testing

## 🙏 Acknowledgments

Thank you to all contributors and early adopters who helped shape this release!

## 📝 Changelog

### Added
- Core JSON masking engine with recursive traversal
- Three built-in masking strategies (Full, Middle, Length-Based)
- Two field matchers (Contains, Regex)
- Configuration parser with JSON support
- Resource-based plugin system for extensibility
- Comprehensive test suite
- Complete documentation and examples

---

**Full Changelog**: https://github.com/slok-foundry/json-masker/commits/v1.0.0
