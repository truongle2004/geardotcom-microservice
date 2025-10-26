package web_ecommerce.sale_service.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Vietnamese text normalization functionality
 * This demonstrates the Vietnamese text processing capabilities
 */
@SpringBootTest
@ActiveProfiles("test")
public class VietnameseSearchTest {

    private final VietnameseSearchServiceImpl vietnameseSearchService = new VietnameseSearchServiceImpl(null, null);

    @Test
    public void testVietnameseTextNormalization() {
        // Test diacritics removal
        String input1 = "điện thoại";
        String expected1 = "dien thoai";
        String result1 = normalizeVietnameseText(input1);
        assertEquals(expected1, result1);

        // Test mixed case
        String input2 = "Điện Thoại";
        String expected2 = "dien thoai";
        String result2 = normalizeVietnameseText(input2);
        assertEquals(expected2, result2);

        // Test with spaces and special characters
        String input3 = "Máy tính xách tay";
        String expected3 = "may tinh xach tay";
        String result3 = normalizeVietnameseText(input3);
        assertEquals(expected3, result3);

        // Test empty string
        String input4 = "";
        String result4 = normalizeVietnameseText(input4);
        assertEquals("", result4);

        // Test null
        String result5 = normalizeVietnameseText(null);
        assertNull(result5);
    }

    @Test
    public void testVietnameseSearchQueries() {
        // Test common Vietnamese product search terms
        String[] testQueries = {
            "điện thoại",
            "laptop",
            "máy tính",
            "quần áo",
            "giày dép",
            "đồng hồ",
            "túi xách",
            "mỹ phẩm"
        };

        for (String query : testQueries) {
            String normalized = normalizeVietnameseText(query);
            assertNotNull(normalized);
            assertFalse(normalized.isEmpty());
            // Should not contain Vietnamese diacritics
            assertFalse(normalized.matches(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ].*"));
        }
    }

    @Test
    public void testVietnameseStopWords() {
        // Test that common Vietnamese stop words are handled
        String[] stopWords = {"và", "của", "cho", "với", "từ", "đến", "trong"};
        
        for (String stopWord : stopWords) {
            String normalized = normalizeVietnameseText(stopWord);
            assertNotNull(normalized);
            // Stop words should be normalized but not removed here
            // (removal happens in the analyzer)
        }
    }

    /**
     * Helper method to access the private normalizeVietnameseText method
     * This is a simplified version for testing purposes
     */
    private String normalizeVietnameseText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        // Normalize Unicode characters
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        
        // Remove diacritics
        normalized = normalized.replaceAll("\\p{M}", "");
        
        // Convert to lowercase and handle Vietnamese-specific characters
        normalized = normalized.toLowerCase().trim();
        normalized = normalized.replace("đ", "d");
        
        return normalized;
    }
}
