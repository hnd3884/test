package javax.accessibility;

public class AccessibleTextSequence
{
    public int startIndex;
    public int endIndex;
    public String text;
    
    public AccessibleTextSequence(final int startIndex, final int endIndex, final String text) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.text = text;
    }
}
