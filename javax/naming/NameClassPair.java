package javax.naming;

import java.io.Serializable;

public class NameClassPair implements Serializable
{
    private String name;
    private String className;
    private String fullName;
    private boolean isRel;
    private static final long serialVersionUID = 5620776610160863339L;
    
    public NameClassPair(final String name, final String className) {
        this.fullName = null;
        this.isRel = true;
        this.name = name;
        this.className = className;
    }
    
    public NameClassPair(final String name, final String className, final boolean isRel) {
        this.fullName = null;
        this.isRel = true;
        this.name = name;
        this.className = className;
        this.isRel = isRel;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public boolean isRelative() {
        return this.isRel;
    }
    
    public void setRelative(final boolean isRel) {
        this.isRel = isRel;
    }
    
    public String getNameInNamespace() {
        if (this.fullName == null) {
            throw new UnsupportedOperationException();
        }
        return this.fullName;
    }
    
    public void setNameInNamespace(final String fullName) {
        this.fullName = fullName;
    }
    
    @Override
    public String toString() {
        return (this.isRelative() ? "" : "(not relative)") + this.getName() + ": " + this.getClassName();
    }
}
