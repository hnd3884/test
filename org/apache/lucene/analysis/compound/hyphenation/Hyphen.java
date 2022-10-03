package org.apache.lucene.analysis.compound.hyphenation;

public class Hyphen
{
    public String preBreak;
    public String noBreak;
    public String postBreak;
    
    Hyphen(final String pre, final String no, final String post) {
        this.preBreak = pre;
        this.noBreak = no;
        this.postBreak = post;
    }
    
    Hyphen(final String pre) {
        this.preBreak = pre;
        this.noBreak = null;
        this.postBreak = null;
    }
    
    @Override
    public String toString() {
        if (this.noBreak == null && this.postBreak == null && this.preBreak != null && this.preBreak.equals("-")) {
            return "-";
        }
        final StringBuilder res = new StringBuilder("{");
        res.append(this.preBreak);
        res.append("}{");
        res.append(this.postBreak);
        res.append("}{");
        res.append(this.noBreak);
        res.append('}');
        return res.toString();
    }
}
