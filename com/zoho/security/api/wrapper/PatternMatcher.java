package com.zoho.security.api.wrapper;

public interface PatternMatcher
{
    boolean matches(final CharSequence p0);
    
    boolean find(final CharSequence p0);
    
    boolean find(final CharSequence p0, final int p1);
    
    String replaceFirst(final CharSequence p0, final String p1);
    
    String replaceAll(final CharSequence p0, final String p1);
    
    String[] split(final String p0);
    
    String[] split(final String p0, final int p1);
    
    String getPattern();
}
