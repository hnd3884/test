package net.sf.jsqlparser.statement.alter;

public class EnableConstraint implements ConstraintState
{
    private boolean disable;
    
    public EnableConstraint(final boolean disable) {
        this.disable = disable;
    }
    
    public boolean isDisable() {
        return this.disable;
    }
    
    public void setDisable(final boolean disable) {
        this.disable = disable;
    }
    
    @Override
    public String toString() {
        return this.disable ? "DISABLE" : "ENABLE";
    }
}
