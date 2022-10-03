package org.htmlparser.util;

class CharacterReferenceEx extends CharacterReference
{
    protected int mStart;
    protected int mEnd;
    
    public CharacterReferenceEx() {
        super("", 0);
    }
    
    public void setStart(final int start) {
        this.mStart = start;
    }
    
    public void setEnd(final int end) {
        this.mEnd = end;
    }
    
    public String getKernel() {
        return this.mKernel.substring(this.mStart, this.mEnd);
    }
    
    public int compare(final Object that) {
        int ret = 0;
        final CharacterReference r = (CharacterReference)that;
        final String kernel = r.getKernel();
        final int length = kernel.length();
        for (int i = this.mStart, j = 0; i < this.mEnd; ++i, ++j) {
            if (j >= length) {
                ret = 1;
                break;
            }
            ret = this.mKernel.charAt(i) - kernel.charAt(j);
            if (0 != ret) {
                break;
            }
        }
        return ret;
    }
}
