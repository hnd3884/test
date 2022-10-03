package org.eclipse.jdt.internal.compiler.ast;

public abstract class NumberLiteral extends Literal
{
    char[] source;
    
    public NumberLiteral(final char[] token, final int s, final int e) {
        this(s, e);
        this.source = token;
    }
    
    public NumberLiteral(final int s, final int e) {
        super(s, e);
    }
    
    @Override
    public boolean isValidJavaStatement() {
        return false;
    }
    
    @Override
    public char[] source() {
        return this.source;
    }
    
    protected static char[] removePrefixZerosAndUnderscores(final char[] token, final boolean isLong) {
        final int max = token.length;
        int start = 0;
        int end = max - 1;
        if (isLong) {
            --end;
        }
        if (max > 1 && token[0] == '0') {
            if (max > 2 && (token[1] == 'x' || token[1] == 'X')) {
                start = 2;
            }
            else if (max > 2 && (token[1] == 'b' || token[1] == 'B')) {
                start = 2;
            }
            else {
                start = 1;
            }
        }
        boolean modified = false;
        boolean ignore = true;
    Label_0173:
        for (int i = start; i < max; ++i) {
            final char currentChar = token[i];
            switch (currentChar) {
                case '0': {
                    if (ignore && !modified && i < end) {
                        modified = true;
                        break;
                    }
                    break;
                }
                case '_': {
                    modified = true;
                    break Label_0173;
                }
                default: {
                    ignore = false;
                    break;
                }
            }
        }
        if (!modified) {
            return token;
        }
        ignore = true;
        final StringBuffer buffer = new StringBuffer();
        buffer.append(token, 0, start);
        for (int j = start; j < max; ++j) {
            final char currentChar2 = token[j];
            switch (currentChar2) {
                case '0': {
                    if (ignore && j < end) {
                        continue;
                    }
                    break;
                }
                case '_': {
                    continue;
                }
                default: {
                    ignore = false;
                    break;
                }
            }
            buffer.append(currentChar2);
        }
        return buffer.toString().toCharArray();
    }
}
