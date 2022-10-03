package com.azul.crs.com.fasterxml.jackson.databind;

import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public abstract class PropertyNamingStrategies implements Serializable
{
    private static final long serialVersionUID = 2L;
    public static final PropertyNamingStrategy LOWER_CAMEL_CASE;
    public static final PropertyNamingStrategy UPPER_CAMEL_CASE;
    public static final PropertyNamingStrategy SNAKE_CASE;
    public static final PropertyNamingStrategy LOWER_CASE;
    public static final PropertyNamingStrategy KEBAB_CASE;
    public static final PropertyNamingStrategy LOWER_DOT_CASE;
    
    static {
        LOWER_CAMEL_CASE = new LowerCamelCaseStrategy();
        UPPER_CAMEL_CASE = new UpperCamelCaseStrategy();
        SNAKE_CASE = new SnakeCaseStrategy();
        LOWER_CASE = new LowerCaseStrategy();
        KEBAB_CASE = new KebabCaseStrategy();
        LOWER_DOT_CASE = new LowerDotCaseStrategy();
    }
    
    public abstract static class NamingBase extends PropertyNamingStrategy
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String nameForField(final MapperConfig<?> config, final AnnotatedField field, final String defaultName) {
            return this.translate(defaultName);
        }
        
        @Override
        public String nameForGetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
            return this.translate(defaultName);
        }
        
        @Override
        public String nameForSetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
            return this.translate(defaultName);
        }
        
        @Override
        public String nameForConstructorParameter(final MapperConfig<?> config, final AnnotatedParameter ctorParam, final String defaultName) {
            return this.translate(defaultName);
        }
        
        public abstract String translate(final String p0);
        
        protected String translateLowerCaseWithSeparator(final String input, final char separator) {
            if (input == null) {
                return input;
            }
            final int length = input.length();
            if (length == 0) {
                return input;
            }
            final StringBuilder result = new StringBuilder(length + (length >> 1));
            int upperCount = 0;
            for (int i = 0; i < length; ++i) {
                final char ch = input.charAt(i);
                final char lc = Character.toLowerCase(ch);
                if (lc == ch) {
                    if (upperCount > 1) {
                        result.insert(result.length() - 1, separator);
                    }
                    upperCount = 0;
                }
                else {
                    if (upperCount == 0 && i > 0) {
                        result.append(separator);
                    }
                    ++upperCount;
                }
                result.append(lc);
            }
            return result.toString();
        }
    }
    
    public static class SnakeCaseStrategy extends NamingBase
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String translate(final String input) {
            if (input == null) {
                return input;
            }
            final int length = input.length();
            final StringBuilder result = new StringBuilder(length * 2);
            int resultLength = 0;
            boolean wasPrevTranslated = false;
            for (int i = 0; i < length; ++i) {
                char c = input.charAt(i);
                if (i > 0 || c != '_') {
                    if (Character.isUpperCase(c)) {
                        if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                            result.append('_');
                            ++resultLength;
                        }
                        c = Character.toLowerCase(c);
                        wasPrevTranslated = true;
                    }
                    else {
                        wasPrevTranslated = false;
                    }
                    result.append(c);
                    ++resultLength;
                }
            }
            return (resultLength > 0) ? result.toString() : input;
        }
    }
    
    public static class LowerCamelCaseStrategy extends NamingBase
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String translate(final String input) {
            return input;
        }
    }
    
    public static class UpperCamelCaseStrategy extends NamingBase
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String translate(final String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }
            final char c = input.charAt(0);
            final char uc = Character.toUpperCase(c);
            if (c == uc) {
                return input;
            }
            final StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(0, uc);
            return sb.toString();
        }
    }
    
    public static class LowerCaseStrategy extends NamingBase
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String translate(final String input) {
            return input.toLowerCase();
        }
    }
    
    public static class KebabCaseStrategy extends NamingBase
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String translate(final String input) {
            return this.translateLowerCaseWithSeparator(input, '-');
        }
    }
    
    public static class LowerDotCaseStrategy extends NamingBase
    {
        private static final long serialVersionUID = 2L;
        
        @Override
        public String translate(final String input) {
            return this.translateLowerCaseWithSeparator(input, '.');
        }
    }
}
