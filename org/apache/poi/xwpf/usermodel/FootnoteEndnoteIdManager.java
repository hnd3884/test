package org.apache.poi.xwpf.usermodel;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;

public class FootnoteEndnoteIdManager
{
    private XWPFDocument document;
    
    public FootnoteEndnoteIdManager(final XWPFDocument document) {
        this.document = document;
    }
    
    public BigInteger nextId() {
        final List<BigInteger> ids = new ArrayList<BigInteger>();
        for (final XWPFAbstractFootnoteEndnote note : this.document.getFootnotes()) {
            ids.add(note.getId());
        }
        for (final XWPFAbstractFootnoteEndnote note : this.document.getEndnotes()) {
            ids.add(note.getId());
        }
        int cand;
        BigInteger newId;
        for (cand = ids.size(), newId = BigInteger.valueOf(cand); ids.contains(newId); newId = BigInteger.valueOf(++cand)) {}
        return newId;
    }
}
