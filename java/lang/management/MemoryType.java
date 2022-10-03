package java.lang.management;

public enum MemoryType
{
    HEAP("Heap memory"), 
    NON_HEAP("Non-heap memory");
    
    private final String description;
    private static final long serialVersionUID = 6992337162326171013L;
    
    private MemoryType(final String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
