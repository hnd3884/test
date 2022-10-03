package org.apache.commons.beanutils.expression;

public class DefaultResolver implements Resolver
{
    private static final char NESTED = '.';
    private static final char MAPPED_START = '(';
    private static final char MAPPED_END = ')';
    private static final char INDEXED_START = '[';
    private static final char INDEXED_END = ']';
    
    @Override
    public int getIndex(final String expression) {
        if (expression == null || expression.length() == 0) {
            return -1;
        }
        int i = 0;
        while (i < expression.length()) {
            final char c = expression.charAt(i);
            if (c == '.' || c == '(') {
                return -1;
            }
            if (c == '[') {
                final int end = expression.indexOf(93, i);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing End Delimiter");
                }
                final String value = expression.substring(i + 1, end);
                if (value.length() == 0) {
                    throw new IllegalArgumentException("No Index Value");
                }
                int index = 0;
                try {
                    index = Integer.parseInt(value, 10);
                }
                catch (final Exception e) {
                    throw new IllegalArgumentException("Invalid index value '" + value + "'");
                }
                return index;
            }
            else {
                ++i;
            }
        }
        return -1;
    }
    
    @Override
    public String getKey(final String expression) {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        int i = 0;
        while (i < expression.length()) {
            final char c = expression.charAt(i);
            if (c == '.' || c == '[') {
                return null;
            }
            if (c == '(') {
                final int end = expression.indexOf(41, i);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing End Delimiter");
                }
                return expression.substring(i + 1, end);
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    @Override
    public String getProperty(final String expression) {
        if (expression == null || expression.length() == 0) {
            return expression;
        }
        for (int i = 0; i < expression.length(); ++i) {
            final char c = expression.charAt(i);
            if (c == '.') {
                return expression.substring(0, i);
            }
            if (c == '(' || c == '[') {
                return expression.substring(0, i);
            }
        }
        return expression;
    }
    
    @Override
    public boolean hasNested(final String expression) {
        return expression != null && expression.length() != 0 && this.remove(expression) != null;
    }
    
    @Override
    public boolean isIndexed(final String expression) {
        if (expression == null || expression.length() == 0) {
            return false;
        }
        for (int i = 0; i < expression.length(); ++i) {
            final char c = expression.charAt(i);
            if (c == '.' || c == '(') {
                return false;
            }
            if (c == '[') {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isMapped(final String expression) {
        if (expression == null || expression.length() == 0) {
            return false;
        }
        for (int i = 0; i < expression.length(); ++i) {
            final char c = expression.charAt(i);
            if (c == '.' || c == '[') {
                return false;
            }
            if (c == '(') {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String next(final String expression) {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        boolean indexed = false;
        boolean mapped = false;
        for (int i = 0; i < expression.length(); ++i) {
            final char c = expression.charAt(i);
            if (indexed) {
                if (c == ']') {
                    return expression.substring(0, i + 1);
                }
            }
            else if (mapped) {
                if (c == ')') {
                    return expression.substring(0, i + 1);
                }
            }
            else {
                if (c == '.') {
                    return expression.substring(0, i);
                }
                if (c == '(') {
                    mapped = true;
                }
                else if (c == '[') {
                    indexed = true;
                }
            }
        }
        return expression;
    }
    
    @Override
    public String remove(final String expression) {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        final String property = this.next(expression);
        if (expression.length() == property.length()) {
            return null;
        }
        int start = property.length();
        if (expression.charAt(start) == '.') {
            ++start;
        }
        return expression.substring(start);
    }
}
