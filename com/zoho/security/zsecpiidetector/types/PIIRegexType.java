package com.zoho.security.zsecpiidetector.types;

import java.util.regex.Pattern;

public class PIIRegexType
{
    private String name;
    private Pattern value;
    private Float score;
    private Integer min_len;
    private Integer max_len;
    
    public PIIRegexType(final String name, final Pattern value, final Float score, final Integer min_len, final Integer max_len) {
        this.name = name;
        this.value = value;
        this.score = score;
        this.min_len = min_len;
        this.max_len = max_len;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Pattern getValue() {
        return this.value;
    }
    
    public Float getScore() {
        return this.score;
    }
    
    public Integer getMinimumLength() {
        return this.min_len;
    }
    
    public Integer getMaximumLength() {
        return this.max_len;
    }
    
    @Override
    public String toString() {
        return "Name: " + this.name + " Value: " + this.value.pattern() + " Score: " + this.score + " Minimum Length: " + this.min_len + " Maximum Length: " + this.max_len;
    }
}
