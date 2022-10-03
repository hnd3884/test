package net.sf.jsqlparser.expression;

public class WindowOffset
{
    private Expression expression;
    private Type type;
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public void setType(final Type type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        if (this.expression != null) {
            buffer.append(' ').append(this.expression);
            if (this.type != null) {
                buffer.append(' ');
                buffer.append(this.type);
            }
        }
        else {
            switch (this.type) {
                case PRECEDING: {
                    buffer.append(" UNBOUNDED PRECEDING");
                    break;
                }
                case FOLLOWING: {
                    buffer.append(" UNBOUNDED FOLLOWING");
                    break;
                }
                case CURRENT: {
                    buffer.append(" CURRENT ROW");
                    break;
                }
            }
        }
        return buffer.toString();
    }
    
    public enum Type
    {
        PRECEDING, 
        FOLLOWING, 
        CURRENT, 
        EXPR;
    }
}
