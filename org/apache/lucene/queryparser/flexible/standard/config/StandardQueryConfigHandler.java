package org.apache.lucene.queryparser.flexible.standard.config;

import org.apache.lucene.document.DateTools;
import java.util.Map;
import java.util.TimeZone;
import java.util.HashMap;
import org.apache.lucene.search.MultiTermQuery;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.config.ConfigurationKey;
import java.util.LinkedHashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;

public class StandardQueryConfigHandler extends QueryConfigHandler
{
    public StandardQueryConfigHandler() {
        this.addFieldConfigListener(new FieldBoostMapFCListener(this));
        this.addFieldConfigListener(new FieldDateResolutionFCListener(this));
        this.addFieldConfigListener(new NumericFieldConfigListener(this));
        this.set(ConfigurationKeys.ALLOW_LEADING_WILDCARD, false);
        this.set(ConfigurationKeys.ANALYZER, null);
        this.set(ConfigurationKeys.DEFAULT_OPERATOR, Operator.OR);
        this.set(ConfigurationKeys.PHRASE_SLOP, 0);
        this.set(ConfigurationKeys.LOWERCASE_EXPANDED_TERMS, true);
        this.set(ConfigurationKeys.ENABLE_POSITION_INCREMENTS, false);
        this.set((ConfigurationKey<LinkedHashMap>)ConfigurationKeys.FIELD_BOOST_MAP, new LinkedHashMap());
        this.set(ConfigurationKeys.FUZZY_CONFIG, new FuzzyConfig());
        this.set(ConfigurationKeys.LOCALE, Locale.getDefault());
        this.set(ConfigurationKeys.MULTI_TERM_REWRITE_METHOD, MultiTermQuery.CONSTANT_SCORE_REWRITE);
        this.set((ConfigurationKey<HashMap>)ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP, new HashMap());
    }
    
    public static final class ConfigurationKeys
    {
        public static final ConfigurationKey<Boolean> ENABLE_POSITION_INCREMENTS;
        public static final ConfigurationKey<Boolean> LOWERCASE_EXPANDED_TERMS;
        public static final ConfigurationKey<Boolean> ALLOW_LEADING_WILDCARD;
        public static final ConfigurationKey<Analyzer> ANALYZER;
        public static final ConfigurationKey<Operator> DEFAULT_OPERATOR;
        public static final ConfigurationKey<Integer> PHRASE_SLOP;
        public static final ConfigurationKey<Locale> LOCALE;
        public static final ConfigurationKey<TimeZone> TIMEZONE;
        public static final ConfigurationKey<MultiTermQuery.RewriteMethod> MULTI_TERM_REWRITE_METHOD;
        public static final ConfigurationKey<CharSequence[]> MULTI_FIELDS;
        public static final ConfigurationKey<Map<String, Float>> FIELD_BOOST_MAP;
        public static final ConfigurationKey<Map<CharSequence, DateTools.Resolution>> FIELD_DATE_RESOLUTION_MAP;
        public static final ConfigurationKey<FuzzyConfig> FUZZY_CONFIG;
        public static final ConfigurationKey<DateTools.Resolution> DATE_RESOLUTION;
        public static final ConfigurationKey<Float> BOOST;
        public static final ConfigurationKey<NumericConfig> NUMERIC_CONFIG;
        public static final ConfigurationKey<Map<String, NumericConfig>> NUMERIC_CONFIG_MAP;
        
        static {
            ENABLE_POSITION_INCREMENTS = ConfigurationKey.newInstance();
            LOWERCASE_EXPANDED_TERMS = ConfigurationKey.newInstance();
            ALLOW_LEADING_WILDCARD = ConfigurationKey.newInstance();
            ANALYZER = ConfigurationKey.newInstance();
            DEFAULT_OPERATOR = ConfigurationKey.newInstance();
            PHRASE_SLOP = ConfigurationKey.newInstance();
            LOCALE = ConfigurationKey.newInstance();
            TIMEZONE = ConfigurationKey.newInstance();
            MULTI_TERM_REWRITE_METHOD = ConfigurationKey.newInstance();
            MULTI_FIELDS = ConfigurationKey.newInstance();
            FIELD_BOOST_MAP = ConfigurationKey.newInstance();
            FIELD_DATE_RESOLUTION_MAP = ConfigurationKey.newInstance();
            FUZZY_CONFIG = ConfigurationKey.newInstance();
            DATE_RESOLUTION = ConfigurationKey.newInstance();
            BOOST = ConfigurationKey.newInstance();
            NUMERIC_CONFIG = ConfigurationKey.newInstance();
            NUMERIC_CONFIG_MAP = ConfigurationKey.newInstance();
        }
    }
    
    public enum Operator
    {
        AND, 
        OR;
    }
}
