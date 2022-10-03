package javax.lang.model.element;

public enum NestingKind
{
    TOP_LEVEL, 
    MEMBER, 
    LOCAL, 
    ANONYMOUS;
    
    public boolean isNested() {
        return this != NestingKind.TOP_LEVEL;
    }
}
