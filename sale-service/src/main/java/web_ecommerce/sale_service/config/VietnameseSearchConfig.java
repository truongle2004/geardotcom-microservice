package web_ecommerce.sale_service.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.CharArraySet;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration for Vietnamese text analysis in Lucene/Hibernate Search
 * This analyzer handles Vietnamese diacritics, normalization, and common Vietnamese stop words
 */
@Configuration
public class VietnameseSearchConfig implements LuceneAnalysisConfigurer {

    // Vietnamese stop words - common words that should be filtered out during search
    private static final CharArraySet VIETNAMESE_STOP_WORDS = new CharArraySet(
        Arrays.asList(
            "và", "của", "cho", "với", "từ", "đến", "trong", "ngoài", "trên", "dưới",
            "trước", "sau", "giữa", "bên", "cạnh", "gần", "xa", "này", "đó", "kia",
            "đây", "đó", "kia", "nào", "gì", "ai", "đâu", "bao", "giờ", "khi", "nào",
            "tại", "sao", "vì", "do", "bởi", "như", "thế", "nào", "là", "có", "không",
            "được", "phải", "cần", "nên", "muốn", "thích", "yêu", "ghét", "tốt", "xấu",
            "lớn", "nhỏ", "cao", "thấp", "dài", "ngắn", "rộng", "hẹp", "nhiều", "ít",
            "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín", "mười",
            "cái", "con", "người", "việc", "điều", "chuyện", "vấn", "đề", "cách", "phương",
            "pháp", "kết", "quả", "thành", "công", "thất", "bại", "đúng", "sai", "đúng",
            "sai", "đúng", "sai", "đúng", "sai", "đúng", "sai", "đúng", "sai", "đúng"
        ), true
    );

    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        // Vietnamese analyzer for full-text search
        context.analyzer("vietnamese")
            .custom()
            .tokenizer(StandardTokenizer.class)
            .tokenFilter(LowerCaseFilter.class)
            .tokenFilter(ASCIIFoldingFilter.class) // Normalize Vietnamese diacritics
            .tokenFilter(StopFilter.class)
                .param("words", VIETNAMESE_STOP_WORDS)
                .param("ignoreCase", true);

        // Vietnamese analyzer for autocomplete/suggestions
        context.analyzer("vietnamese_autocomplete")
            .custom()
            .tokenizer(WhitespaceTokenizer.class)
            .tokenFilter(LowerCaseFilter.class)
            .tokenFilter(ASCIIFoldingFilter.class)
            .tokenFilter(StopFilter.class)
                .param("words", VIETNAMESE_STOP_WORDS)
                .param("ignoreCase", true);

        // Vietnamese analyzer for exact matching (minimal processing)
        context.analyzer("vietnamese_exact")
            .custom()
            .tokenizer(StandardTokenizer.class)
            .tokenFilter(LowerCaseFilter.class)
            .tokenFilter(ASCIIFoldingFilter.class);

        // Vietnamese analyzer for keyword search (no tokenization)
        context.analyzer("vietnamese_keyword")
            .custom()
            .tokenizer(WhitespaceTokenizer.class)
            .tokenFilter(LowerCaseFilter.class)
            .tokenFilter(ASCIIFoldingFilter.class);
    }
}
