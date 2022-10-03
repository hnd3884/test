package javax.management;

class MatchQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = -7156603696948215014L;
    private AttributeValueExp exp;
    private String pattern;
    
    public MatchQueryExp() {
    }
    
    public MatchQueryExp(final AttributeValueExp exp, final StringValueExp stringValueExp) {
        this.exp = exp;
        this.pattern = stringValueExp.getValue();
    }
    
    public AttributeValueExp getAttribute() {
        return this.exp;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        final ValueExp apply = this.exp.apply(objectName);
        return apply instanceof StringValueExp && wildmatch(((StringValueExp)apply).getValue(), this.pattern);
    }
    
    @Override
    public String toString() {
        return this.exp + " like " + new StringValueExp(this.pattern);
    }
    
    private static boolean wildmatch(final String s, final String s2) {
        int n = 0;
        int i = 0;
        final int length = s.length();
        final int length2 = s2.length();
        while (i < length2) {
            final char char1 = s2.charAt(i++);
            if (char1 == '?') {
                if (++n > length) {
                    return false;
                }
                continue;
            }
            else if (char1 == '[') {
                if (n >= length) {
                    return false;
                }
                boolean b = true;
                boolean b2 = false;
                if (s2.charAt(i) == '!') {
                    b = false;
                    ++i;
                }
                char char2;
                while ((char2 = s2.charAt(i)) != ']' && ++i < length2) {
                    if (s2.charAt(i) == '-' && i + 1 < length2 && s2.charAt(i + 1) != ']') {
                        if (s.charAt(n) >= s2.charAt(i - 1) && s.charAt(n) <= s2.charAt(i + 1)) {
                            b2 = true;
                        }
                        ++i;
                    }
                    else {
                        if (char2 != s.charAt(n)) {
                            continue;
                        }
                        b2 = true;
                    }
                }
                if (i >= length2 || b != b2) {
                    return false;
                }
                ++i;
                ++n;
            }
            else if (char1 == '*') {
                if (i >= length2) {
                    return true;
                }
                while (!wildmatch(s.substring(n), s2.substring(i))) {
                    if (++n >= length) {
                        return false;
                    }
                }
                return true;
            }
            else if (char1 == '\\') {
                if (i >= length2 || n >= length || s2.charAt(i++) != s.charAt(n++)) {
                    return false;
                }
                continue;
            }
            else {
                if (n >= length || char1 != s.charAt(n++)) {
                    return false;
                }
                continue;
            }
        }
        return n == length;
    }
}
