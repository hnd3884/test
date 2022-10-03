package sun.management;

import java.io.Serializable;

public class MethodInfo implements Serializable
{
    private String name;
    private long type;
    private int compileSize;
    private static final long serialVersionUID = 6992337162326171013L;
    
    MethodInfo(final String name, final long type, final int compileSize) {
        this.name = name;
        this.type = type;
        this.compileSize = compileSize;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getType() {
        return this.type;
    }
    
    public int getCompileSize() {
        return this.compileSize;
    }
    
    @Override
    public String toString() {
        return this.getName() + " type = " + this.getType() + " compileSize = " + this.getCompileSize();
    }
}
