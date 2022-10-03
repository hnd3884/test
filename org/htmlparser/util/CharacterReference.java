package org.htmlparser.util;

import org.htmlparser.util.sort.Ordered;
import java.io.Serializable;

public class CharacterReference implements Serializable, Cloneable, Ordered
{
    protected int mCharacter;
    protected String mKernel;
    
    public CharacterReference(final String kernel, final int character) {
        this.mKernel = kernel;
        this.mCharacter = character;
        if (null == this.mKernel) {
            this.mKernel = "";
        }
    }
    
    public String getKernel() {
        return this.mKernel;
    }
    
    void setKernel(final String kernel) {
        this.mKernel = kernel;
    }
    
    public int getCharacter() {
        return this.mCharacter;
    }
    
    void setCharacter(final int character) {
        this.mCharacter = character;
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer(16);
        final String hex = Integer.toHexString(this.getCharacter());
        ret.append("\\u");
        for (int i = hex.length(); i < 4; ++i) {
            ret.append("0");
        }
        ret.append(hex);
        ret.append("[");
        ret.append(this.getKernel());
        ret.append("]");
        return ret.toString();
    }
    
    public int compare(final Object that) {
        final CharacterReference r = (CharacterReference)that;
        return this.getKernel().compareTo(r.getKernel());
    }
}
