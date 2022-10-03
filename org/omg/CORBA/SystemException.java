package org.omg.CORBA;

public abstract class SystemException extends RuntimeException
{
    public int minor;
    public CompletionStatus completed;
    
    protected SystemException(final String s, final int minor, final CompletionStatus completed) {
        super(s);
        this.minor = minor;
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        final String string = super.toString();
        final int n = this.minor & 0xFFFFF000;
        String s = null;
        switch (n) {
            case 1330446336: {
                s = string + "  vmcid: OMG";
                break;
            }
            case 1398079488: {
                s = string + "  vmcid: SUN";
                break;
            }
            default: {
                s = string + "  vmcid: 0x" + Integer.toHexString(n);
                break;
            }
        }
        final String string2 = s + "  minor code: " + (this.minor & 0xFFF);
        String s2 = null;
        switch (this.completed.value()) {
            case 0: {
                s2 = string2 + "  completed: Yes";
                break;
            }
            case 1: {
                s2 = string2 + "  completed: No";
                break;
            }
            default: {
                s2 = string2 + " completed: Maybe";
                break;
            }
        }
        return s2;
    }
}
