package org.apache.poi.ss.format;

import java.util.Locale;

public enum CellFormatType
{
    GENERAL {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellGeneralFormatter();
        }
        
        @Override
        CellFormatter formatter(final Locale locale, final String pattern) {
            return new CellGeneralFormatter(locale);
        }
    }, 
    NUMBER {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellNumberFormatter(pattern);
        }
        
        @Override
        CellFormatter formatter(final Locale locale, final String pattern) {
            return new CellNumberFormatter(locale, pattern);
        }
    }, 
    DATE {
        @Override
        boolean isSpecial(final char ch) {
            return ch == '\'' || (ch <= '\u007f' && Character.isLetter(ch));
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellDateFormatter(pattern);
        }
        
        @Override
        CellFormatter formatter(final Locale locale, final String pattern) {
            return new CellDateFormatter(locale, pattern);
        }
    }, 
    ELAPSED {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellElapsedFormatter(pattern);
        }
        
        @Override
        CellFormatter formatter(final Locale locale, final String pattern) {
            return new CellElapsedFormatter(pattern);
        }
    }, 
    TEXT {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellTextFormatter(pattern);
        }
        
        @Override
        CellFormatter formatter(final Locale locale, final String pattern) {
            return new CellTextFormatter(pattern);
        }
    };
    
    abstract boolean isSpecial(final char p0);
    
    abstract CellFormatter formatter(final String p0);
    
    abstract CellFormatter formatter(final Locale p0, final String p1);
}
