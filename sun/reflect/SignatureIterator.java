package sun.reflect;

public class SignatureIterator
{
    private final String sig;
    private int idx;
    
    public SignatureIterator(final String sig) {
        this.sig = sig;
        this.reset();
    }
    
    public void reset() {
        this.idx = 1;
    }
    
    public boolean atEnd() {
        return this.sig.charAt(this.idx) == ')';
    }
    
    public String next() {
        if (this.atEnd()) {
            return null;
        }
        char c = this.sig.charAt(this.idx);
        if (c != '[' && c != 'L') {
            ++this.idx;
            return new String(new char[] { c });
        }
        int idx = this.idx;
        if (c == '[') {
            while ((c = this.sig.charAt(idx)) == '[') {
                ++idx;
            }
        }
        if (c == 'L') {
            while (this.sig.charAt(idx) != ';') {
                ++idx;
            }
        }
        final int idx2 = this.idx;
        this.idx = idx + 1;
        return this.sig.substring(idx2, this.idx);
    }
    
    public String returnType() {
        if (!this.atEnd()) {
            throw new InternalError("Illegal use of SignatureIterator");
        }
        return this.sig.substring(this.idx + 1, this.sig.length());
    }
}
