package net.sf.jsqlparser.expression;

public class StringValue implements Expression
{
    private String value;
    
    public StringValue(final String escapedValue) {
        this.value = "";
        if (escapedValue.startsWith("'") && escapedValue.endsWith("'")) {
            this.value = escapedValue.substring(1, escapedValue.length() - 1);
        }
        else {
            this.value = escapedValue;
        }
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getNotExcapedValue() {
        final StringBuilder buffer = new StringBuilder(this.value);
        for (int index = 0, deletesNum = 0; (index = this.value.indexOf("''", index)) != -1; index += 2, ++deletesNum) {
            buffer.deleteCharAt(index - deletesNum);
        }
        return buffer.toString();
    }
    
    public void setValue(final String string) {
        this.value = string;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "'" + this.value + "'";
    }
}
