package sun.nio.ch;

class OptionKey
{
    private int level;
    private int name;
    
    OptionKey(final int level, final int name) {
        this.level = level;
        this.name = name;
    }
    
    int level() {
        return this.level;
    }
    
    int name() {
        return this.name;
    }
}
