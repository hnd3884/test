package sun.security.tools.policytool;

class Perm
{
    public final String CLASS;
    public final String FULL_CLASS;
    public final String[] TARGETS;
    public final String[] ACTIONS;
    
    public Perm(final String class1, final String full_CLASS, final String[] targets, final String[] actions) {
        this.CLASS = class1;
        this.FULL_CLASS = full_CLASS;
        this.TARGETS = targets;
        this.ACTIONS = actions;
    }
}
