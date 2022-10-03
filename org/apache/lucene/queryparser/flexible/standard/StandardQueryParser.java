package org.apache.lucene.queryparser.flexible.standard;

import org.apache.lucene.document.DateTools;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import java.util.Map;
import org.apache.lucene.queryparser.flexible.standard.config.FuzzyConfig;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.parser.SyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.processors.StandardQueryNodeProcessorPipeline;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.QueryParserHelper;

public class StandardQueryParser extends QueryParserHelper implements CommonQueryParserConfiguration
{
    public StandardQueryParser() {
        super(new StandardQueryConfigHandler(), new StandardSyntaxParser(), new StandardQueryNodeProcessorPipeline(null), new StandardQueryTreeBuilder());
        this.setEnablePositionIncrements(true);
    }
    
    public StandardQueryParser(final Analyzer analyzer) {
        this();
        this.setAnalyzer(analyzer);
    }
    
    @Override
    public String toString() {
        return "<StandardQueryParser config=\"" + this.getQueryConfigHandler() + "\"/>";
    }
    
    @Override
    public Query parse(final String query, final String defaultField) throws QueryNodeException {
        return (Query)super.parse(query, defaultField);
    }
    
    public StandardQueryConfigHandler.Operator getDefaultOperator() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
    }
    
    public void setDefaultOperator(final StandardQueryConfigHandler.Operator operator) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR, operator);
    }
    
    @Override
    public void setLowercaseExpandedTerms(final boolean lowercaseExpandedTerms) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.LOWERCASE_EXPANDED_TERMS, lowercaseExpandedTerms);
    }
    
    @Override
    public boolean getLowercaseExpandedTerms() {
        final Boolean lowercaseExpandedTerms = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOWERCASE_EXPANDED_TERMS);
        return lowercaseExpandedTerms == null || lowercaseExpandedTerms;
    }
    
    @Override
    public void setAllowLeadingWildcard(final boolean allowLeadingWildcard) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.ALLOW_LEADING_WILDCARD, allowLeadingWildcard);
    }
    
    @Override
    public void setEnablePositionIncrements(final boolean enabled) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.ENABLE_POSITION_INCREMENTS, enabled);
    }
    
    @Override
    public boolean getEnablePositionIncrements() {
        final Boolean enablePositionsIncrements = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ENABLE_POSITION_INCREMENTS);
        return enablePositionsIncrements != null && enablePositionsIncrements;
    }
    
    @Override
    public void setMultiTermRewriteMethod(final MultiTermQuery.RewriteMethod method) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.MULTI_TERM_REWRITE_METHOD, method);
    }
    
    @Override
    public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_TERM_REWRITE_METHOD);
    }
    
    public void setMultiFields(CharSequence[] fields) {
        if (fields == null) {
            fields = new CharSequence[0];
        }
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS, fields);
    }
    
    @Deprecated
    public void getMultiFields(final CharSequence[] fields) {
        this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS);
    }
    
    public CharSequence[] getMultiFields() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS);
    }
    
    @Override
    public void setFuzzyPrefixLength(final int fuzzyPrefixLength) {
        final QueryConfigHandler config = this.getQueryConfigHandler();
        FuzzyConfig fuzzyConfig = config.get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            fuzzyConfig = new FuzzyConfig();
            config.set(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG, fuzzyConfig);
        }
        fuzzyConfig.setPrefixLength(fuzzyPrefixLength);
    }
    
    public void setNumericConfigMap(final Map<String, NumericConfig> numericConfigMap) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG_MAP, numericConfigMap);
    }
    
    public Map<String, NumericConfig> getNumericConfigMap() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG_MAP);
    }
    
    @Override
    public void setLocale(final Locale locale) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.LOCALE, locale);
    }
    
    @Override
    public Locale getLocale() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOCALE);
    }
    
    @Override
    public void setTimeZone(final TimeZone timeZone) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.TIMEZONE, timeZone);
    }
    
    @Override
    public TimeZone getTimeZone() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.TIMEZONE);
    }
    
    @Deprecated
    public void setDefaultPhraseSlop(final int defaultPhraseSlop) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP, defaultPhraseSlop);
    }
    
    @Override
    public void setPhraseSlop(final int defaultPhraseSlop) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP, defaultPhraseSlop);
    }
    
    public void setAnalyzer(final Analyzer analyzer) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.ANALYZER, analyzer);
    }
    
    @Override
    public Analyzer getAnalyzer() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ANALYZER);
    }
    
    @Override
    public boolean getAllowLeadingWildcard() {
        final Boolean allowLeadingWildcard = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ALLOW_LEADING_WILDCARD);
        return allowLeadingWildcard != null && allowLeadingWildcard;
    }
    
    @Override
    public float getFuzzyMinSim() {
        final FuzzyConfig fuzzyConfig = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            return 2.0f;
        }
        return fuzzyConfig.getMinSimilarity();
    }
    
    @Override
    public int getFuzzyPrefixLength() {
        final FuzzyConfig fuzzyConfig = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            return 0;
        }
        return fuzzyConfig.getPrefixLength();
    }
    
    @Override
    public int getPhraseSlop() {
        final Integer phraseSlop = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP);
        if (phraseSlop == null) {
            return 0;
        }
        return phraseSlop;
    }
    
    @Override
    public void setFuzzyMinSim(final float fuzzyMinSim) {
        final QueryConfigHandler config = this.getQueryConfigHandler();
        FuzzyConfig fuzzyConfig = config.get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            fuzzyConfig = new FuzzyConfig();
            config.set(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG, fuzzyConfig);
        }
        fuzzyConfig.setMinSimilarity(fuzzyMinSim);
    }
    
    public void setFieldsBoost(final Map<String, Float> boosts) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.FIELD_BOOST_MAP, boosts);
    }
    
    public Map<String, Float> getFieldsBoost() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_BOOST_MAP);
    }
    
    @Override
    public void setDateResolution(final DateTools.Resolution dateResolution) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION, dateResolution);
    }
    
    public DateTools.Resolution getDateResolution() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION);
    }
    
    @Deprecated
    public void setDateResolution(final Map<CharSequence, DateTools.Resolution> dateRes) {
        this.setDateResolutionMap(dateRes);
    }
    
    public Map<CharSequence, DateTools.Resolution> getDateResolutionMap() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP);
    }
    
    public void setDateResolutionMap(final Map<CharSequence, DateTools.Resolution> dateRes) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP, dateRes);
    }
}
