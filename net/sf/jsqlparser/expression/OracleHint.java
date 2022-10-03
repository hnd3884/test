package net.sf.jsqlparser.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OracleHint implements Expression
{
    private static final Pattern SINGLE_LINE;
    private static final Pattern MULTI_LINE;
    private String value;
    private boolean singleLine;
    
    public OracleHint() {
        this.singleLine = false;
    }
    
    public static boolean isHintMatch(final String comment) {
        return OracleHint.SINGLE_LINE.matcher(comment).find() || OracleHint.MULTI_LINE.matcher(comment).find();
    }
    
    public final void setComment(final String comment) {
        Matcher m = OracleHint.SINGLE_LINE.matcher(comment);
        if (m.find()) {
            this.value = m.group(1);
            this.singleLine = true;
            return;
        }
        m = OracleHint.MULTI_LINE.matcher(comment);
        if (m.find()) {
            this.value = m.group(1);
            this.singleLine = false;
        }
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public boolean isSingleLine() {
        return this.singleLine;
    }
    
    public void setSingleLine(final boolean singleLine) {
        this.singleLine = singleLine;
    }
    
    @Override
    public void accept(final ExpressionVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        if (this.singleLine) {
            return "--+ " + this.value + "\n";
        }
        return "/*+ " + this.value + " */";
    }
    
    static {
        SINGLE_LINE = Pattern.compile("--\\+ *([^ ].*[^ ])");
        MULTI_LINE = Pattern.compile("\\/\\*\\+ *([^ ].*[^ ]) *\\*+\\/", 40);
    }
}
