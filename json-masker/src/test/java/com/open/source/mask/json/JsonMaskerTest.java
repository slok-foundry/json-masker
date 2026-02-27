package com.open.source.mask.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.source.mask.json.matcher.FieldMatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonMaskerTest
 *
 * @author slok
 * date: 09-Jan-2025
 * @since 1.0.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonMaskerTest {

  private final JsonMasker masker = new JsonMasker();
  private final ObjectMapper mapper = new ObjectMapper();
  private static final String TEST_DATA_DIR = "src/test/resources/jsonmask";
  private static final String CONFIG_FILE = TEST_DATA_DIR + "/masking-config.json";
  private Map<FieldMatcher, FieldMaskingRule> rules;

  @BeforeAll
  public void setup() throws Exception {
    String configJson = Files.readString(Paths.get(CONFIG_FILE), StandardCharsets.UTF_8);
    rules = ConfigParser.parseConfig(configJson);
  }

  @Test
  public void testMaskAllJsonFiles() throws Exception {
    List<Path> jsonFiles = new ArrayList<>();
    
    try (Stream<Path> paths = Files.walk(Paths.get(TEST_DATA_DIR))) {
      paths.filter(Files::isRegularFile)
           .filter(p -> p.toString().endsWith(".json"))
           .filter(p -> !p.toString().contains("-config"))
           .forEach(jsonFiles::add);
    }

    assertFalse(jsonFiles.isEmpty(), "No test JSON files found");

    for (Path path : jsonFiles) {
      String input = Files.readString(path, StandardCharsets.UTF_8);
      String masked = masker.maskJson(input, rules);
      
      assertNotNull(masked, "Masked output should not be null for " + path.getFileName());
      assertFalse(masked.isEmpty(), "Masked output should not be empty for " + path.getFileName());
      
      JsonNode maskedNode = mapper.readTree(masked);
      assertNotNull(maskedNode, "Masked JSON should be valid for " + path.getFileName());
      
      verifySensitiveDataMasked(path.getFileName().toString(), input, masked);
      
      System.out.println("✓ Successfully masked: " + path.getFileName());
    }
  }

  @Test
  public void testCustomerProfileMasking() throws Exception {
    String input = Files.readString(Paths.get(TEST_DATA_DIR + "/customer-profile.json"), StandardCharsets.UTF_8);
    String configJson = Files.readString(Paths.get(TEST_DATA_DIR + "/customer-profile-config.json"), StandardCharsets.UTF_8);
    String masked = masker.maskJson(input, configJson);
    
    assertNotNull(masked);
    
    // SSN should be fully masked
    assertFalse(masked.contains("123-45-6789"), "SSN should be masked");
    assertFalse(masked.contains("98-7654321"), "Tax ID should be masked");
    
    // Credit card numbers should be masked
    assertFalse(masked.contains("4532015112830366"), "Visa card should be masked");
    assertFalse(masked.contains("5425233430109903"), "Mastercard should be masked");
    
    // CVV should be fully masked
    assertFalse(masked.contains("\"cvv\" : \"123\""), "CVV should be masked");
    assertFalse(masked.contains("\"cvv\" : \"456\""), "CVV should be masked");
    
    // Passwords should be fully masked
    assertFalse(masked.contains("SecureP@ssw0rd!2025"), "Password should be masked");
    
    // API keys should be fully masked
    assertFalse(masked.contains("sk_live_51HqJ8KLkjhg87HGjhg87HGjhg87HGjhg"), "API key should be masked");
    assertFalse(masked.contains("whsec_1234567890abcdefghijklmnopqrstuvwxyz"), "API secret should be masked");
    
    // Bank account should be masked
    assertFalse(masked.contains("9876543210"), "Bank account should be masked");
    assertFalse(masked.contains("021000021"), "Routing number should be masked");
    
    // Email should be partially masked
    assertFalse(masked.contains("jennifer.anderson@email.com"), "Email should be masked");
    
    // Phone should be partially masked
    assertFalse(masked.contains("+1-555-234-5678"), "Phone should be masked");
    
    // Insurance number should be masked
    assertFalse(masked.contains("INS-987654321"), "Insurance number should be masked");
    
    System.out.println("Customer Profile Masking:\n" + masked);
  }

  @Test
  public void testHealthcareRecordMasking() throws Exception {
    String input = Files.readString(Paths.get(TEST_DATA_DIR + "/healthcare-record.json"), StandardCharsets.UTF_8);
    String configJson = Files.readString(Paths.get(TEST_DATA_DIR + "/healthcare-record-config.json"), StandardCharsets.UTF_8);
    String masked = masker.maskJson(input, configJson);
    
    assertNotNull(masked);
    
    // SSN should be fully masked
    assertFalse(masked.contains("456-78-9012"), "Patient SSN should be masked");
    
    // Insurance policy numbers should be masked
    assertFalse(masked.contains("BCBS-123456789"), "Insurance policy should be masked");
    assertFalse(masked.contains("AET-987654321"), "Secondary insurance should be masked");
    
    // Credit card should be masked
    assertFalse(masked.contains("5555555555554444"), "Card number should be masked");
    assertFalse(masked.contains("\"cvv\" : \"789\""), "CVV should be masked");
    
    // Bank account should be masked
    assertFalse(masked.contains("9876543210123456"), "Bank account should be masked");
    assertFalse(masked.contains("011000015"), "Routing number should be masked");
    
    // Email should be masked
    assertFalse(masked.contains("sarah.johnson@email.com"), "Email should be masked");
    
    // Phone should be masked
    assertFalse(masked.contains("+1-555-123-4567"), "Phone should be masked");
    
    // Date of birth should be masked
    assertFalse(masked.contains("1978-11-25"), "Date of birth should be masked");
    
    System.out.println("Healthcare Record Masking:\n" + masked);
  }

  @Test
  public void testPaymentTransactionMasking() throws Exception {
    String input = Files.readString(Paths.get(TEST_DATA_DIR + "/payment-transaction.json"), StandardCharsets.UTF_8);
    String configJson = Files.readString(Paths.get(TEST_DATA_DIR + "/payment-transaction-config.json"), StandardCharsets.UTF_8);
    String masked = masker.maskJson(input, configJson);
    
    assertNotNull(masked);
    
    // Card number should be masked
    assertFalse(masked.contains("4111111111111111"), "Card number should be masked");
    
    // CVV/CVC should be fully masked
    assertFalse(masked.contains("\"cvc\" : \"737\""), "CVC should be masked");
    
    // SSN should be fully masked
    assertFalse(masked.contains("987-65-4321"), "SSN should be masked");
    
    // Email should be masked
    assertFalse(masked.contains("john.smith@example.com"), "Email should be masked");
    
    // Phone should be masked
    assertFalse(masked.contains("+1-212-555-0123"), "Phone should be masked");
    
    // API credentials should be fully masked
    assertFalse(masked.contains("AQEyhmfxKYvLbRZNw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZjjU5QwzwVRbHyQH8h8+jWN8="), "API key should be masked");
    assertFalse(masked.contains("DFB1EB5485895CFA42E9F3D3E4F5A6B7C8D9E0F1A2B3C4D5E6F7A8B9C0D1E2F3"), "API secret should be masked");
    
    // Webhook credentials should be masked
    assertFalse(masked.contains("wh_p@ssw0rd_secure_2025!"), "Webhook password should be masked");
    assertFalse(masked.contains("wh_m3rch@nt_p@ss!"), "Merchant password should be masked");
    
    // HMAC key should be masked
    assertFalse(masked.contains("hmac_key_1234567890abcdefghijklmnopqrstuvwxyz"), "HMAC key should be masked");
    
    // Bank details should be masked
    assertFalse(masked.contains("1234567890123456"), "Bank account should be masked");
    assertFalse(masked.contains("US12BANK0000001234567890"), "IBAN should be masked");
    
    // Date of birth should be masked
    assertFalse(masked.contains("1990-07-22"), "Date of birth should be masked");
    
    System.out.println("Payment Transaction Masking:\n" + masked);
  }

  @Test
  public void testLargeJunkMasking() throws Exception {
    String input = Files.readString(Paths.get(TEST_DATA_DIR + "/largejunk.json"), StandardCharsets.UTF_8);
    String configJson = Files.readString(Paths.get(TEST_DATA_DIR + "/largejunk-config.json"), StandardCharsets.UTF_8);
    String masked = masker.maskJson(input, configJson);
    
    assertNotNull(masked);
    
    // Password should be masked
    assertFalse(masked.contains("supersecret"), "Password should be masked");
    
    // Card number should be masked
    assertFalse(masked.contains("4111111111111111"), "Card number should be masked");
    
    // CVV should be masked
    assertFalse(masked.contains("\"cvv\" : \"123\""), "CVV should be masked");
    
    // Email should be masked
    assertFalse(masked.contains("john.doe@example.com"), "Email should be masked");
    
    System.out.println("Large Junk Masking:\n" + masked);
  }

  @Test
  public void testTestExchangeMasking() throws Exception {
    File file = new File(TEST_DATA_DIR + "/testExchange.json");
    if (!file.exists()) {
      System.out.println("testExchange.json not found, skipping");
      return;
    }
    
    String input = Files.readString(Paths.get(TEST_DATA_DIR + "/testExchange.json"), StandardCharsets.UTF_8);
    String masked = masker.maskJson(input, rules);
    
    assertNotNull(masked);
    
    // API credentials should be masked
    assertFalse(masked.contains("0hmItsSTroingPassword"), "API password should be masked");
    
    // Secrets should be masked
    assertFalse(masked.contains("MIIEvQcGJGKJGIUKLGDGHGKHHJHGKHLKKUA="), "Secret data should be masked");
    
    // Email should be masked
    assertFalse(masked.contains("hans.wen@shijigroup.com"), "Email should be masked");
    assertFalse(masked.contains("kumarecom@shijigroup.com"), "Email should be masked");
    
    System.out.println("Test Exchange Masking:\n" + masked);
  }

  private void verifySensitiveDataMasked(String fileName, String original, String masked) {
    // Skip validation for config files
    if (fileName.contains("-config")) {
      return;
    }
    
    // Verify specific sensitive values are not in masked output
    if (original.contains("123-45-6789")) {
      assertFalse(masked.contains("123-45-6789"), "SSN should be masked in " + fileName);
    }
    if (original.contains("4532015112830366")) {
      assertFalse(masked.contains("4532015112830366"), "Card number should be masked in " + fileName);
    }
    if (original.contains("SecureP@ssw0rd")) {
      assertFalse(masked.contains("SecureP@ssw0rd"), "Password should be masked in " + fileName);
    }
  }
}
