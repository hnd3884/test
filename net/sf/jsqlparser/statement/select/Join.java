package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Column;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;

public class Join
{
    private boolean outer;
    private boolean right;
    private boolean left;
    private boolean natural;
    private boolean full;
    private boolean inner;
    private boolean simple;
    private boolean cross;
    private boolean semi;
    private FromItem rightItem;
    private Expression onExpression;
    private List<Column> usingColumns;
    
    public Join() {
        this.outer = false;
        this.right = false;
        this.left = false;
        this.natural = false;
        this.full = false;
        this.inner = false;
        this.simple = false;
        this.cross = false;
        this.semi = false;
    }
    
    public boolean isSimple() {
        return this.simple;
    }
    
    public void setSimple(final boolean b) {
        this.simple = b;
    }
    
    public boolean isInner() {
        return this.inner;
    }
    
    public void setInner(final boolean b) {
        this.inner = b;
    }
    
    public boolean isOuter() {
        return this.outer;
    }
    
    public void setOuter(final boolean b) {
        this.outer = b;
    }
    
    public boolean isSemi() {
        return this.semi;
    }
    
    public void setSemi(final boolean b) {
        this.semi = b;
    }
    
    public boolean isLeft() {
        return this.left;
    }
    
    public void setLeft(final boolean b) {
        this.left = b;
    }
    
    public boolean isRight() {
        return this.right;
    }
    
    public void setRight(final boolean b) {
        this.right = b;
    }
    
    public boolean isNatural() {
        return this.natural;
    }
    
    public void setNatural(final boolean b) {
        this.natural = b;
    }
    
    public boolean isFull() {
        return this.full;
    }
    
    public void setFull(final boolean b) {
        this.full = b;
    }
    
    public boolean isCross() {
        return this.cross;
    }
    
    public void setCross(final boolean cross) {
        this.cross = cross;
    }
    
    public FromItem getRightItem() {
        return this.rightItem;
    }
    
    public void setRightItem(final FromItem item) {
        this.rightItem = item;
    }
    
    public Expression getOnExpression() {
        return this.onExpression;
    }
    
    public void setOnExpression(final Expression expression) {
        this.onExpression = expression;
    }
    
    public List<Column> getUsingColumns() {
        return this.usingColumns;
    }
    
    public void setUsingColumns(final List<Column> list) {
        this.usingColumns = list;
    }
    
    @Override
    public String toString() {
        if (this.isSimple()) {
            return "" + this.rightItem;
        }
        String type = "";
        if (this.isRight()) {
            type += "RIGHT ";
        }
        else if (this.isNatural()) {
            type += "NATURAL ";
        }
        else if (this.isFull()) {
            type += "FULL ";
        }
        else if (this.isLeft()) {
            type += "LEFT ";
        }
        else if (this.isCross()) {
            type += "CROSS ";
        }
        if (this.isOuter()) {
            type += "OUTER ";
        }
        else if (this.isInner()) {
            type += "INNER ";
        }
        else if (this.isSemi()) {
            type += "SEMI ";
        }
        return type + "JOIN " + this.rightItem + ((this.onExpression != null) ? (" ON " + this.onExpression + "") : "") + PlainSelect.getFormatedList(this.usingColumns, "USING", true, true);
    }
}
