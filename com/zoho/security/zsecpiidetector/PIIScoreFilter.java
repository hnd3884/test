package com.zoho.security.zsecpiidetector;

import org.json.JSONObject;

public interface PIIScoreFilter
{
    public static final int PARSE_AS_SCORE = 0;
    public static final int PARSE_AS_JSON = 1;
    public static final float DEFAULT_REGEX_SCORE = 0.8f;
    public static final float DEFAULT_DICTIONARY_SCORE = 0.8f;
    public static final float DEFAULT_REGEX_AND_DICTIONARY_SCORE = 0.8f;
    public static final float DEFAULT_OVERALL_SCORE = 0.8f;
    
    int getParserType();
    
    boolean isValidScore(final float p0);
    
    boolean isValidDetectedPIIData(final JSONObject p0);
}
