package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

public class Function extends ASTNodeAccessImpl implements Expression
{
    private String name;
    private ExpressionList parameters;
    private boolean allColumns;
    private boolean distinct;
    private boolean isEscaped;
    private String attribute;
    private KeepExpression keep;
    
    public Function() {
        this.allColumns = false;
        this.distinct = false;
        this.isEscaped = false;
        this.keep = null;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String string) {
        this.name = string;
    }
    
    public boolean isAllColumns() {
        return this.allColumns;
    }
    
    public void setAllColumns(final boolean b) {
        this.allColumns = b;
    }
    
    public boolean isDistinct() {
        return this.distinct;
    }
    
    public void setDistinct(final boolean b) {
        this.distinct = b;
    }
    
    public ExpressionList getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final ExpressionList list) {
        this.parameters = list;
    }
    
    public boolean isEscaped() {
        return this.isEscaped;
    }
    
    public void setEscaped(final boolean isEscaped) {
        this.isEscaped = isEscaped;
    }
    
    public String getAttribute() {
        return this.attribute;
    }
    
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }
    
    public KeepExpression getKeep() {
        return this.keep;
    }
    
    public void setKeep(final KeepExpression keep) {
        this.keep = keep;
    }
    
    @Override
    public String toString() {
        String params;
        if (this.parameters != null) {
            params = this.parameters.toString();
            if (this.isDistinct()) {
                params = params.replaceFirst("\\(", "(DISTINCT ");
            }
            else if (this.isAllColumns()) {
                params = params.replaceFirst("\\(", "(ALL ");
            }
        }
        else if (this.isAllColumns()) {
            params = "(*)";
        }
        else {
            params = "()";
        }
        String ans = this.name + "" + params + "";
        if (this.attribute != null) {
            ans = ans + "." + this.attribute;
        }
        if (this.keep != null) {
            ans = ans + " " + this.keep.toString();
        }
        if (this.isEscaped) {
            ans = "{fn " + ans + "}";
        }
        return ans;
    }
}
