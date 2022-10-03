package net.sf.jsqlparser.expression;

public class Alias
{
    private String name;
    private boolean useAs;
    
    public Alias(final String name) {
        this.useAs = true;
        this.name = name;
    }
    
    public Alias(final String name, final boolean useAs) {
        this.useAs = true;
        this.name = name;
        this.useAs = useAs;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isUseAs() {
        return this.useAs;
    }
    
    public void setUseAs(final boolean useAs) {
        this.useAs = useAs;
    }
    
    @Override
    public String toString() {
        return (this.useAs ? " AS " : " ") + this.name;
    }
}
