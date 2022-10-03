package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class MatchingIterator extends DTMAxisIteratorBase
{
    private DTMAxisIterator _source;
    private final int _match;
    
    public MatchingIterator(final int match, final DTMAxisIterator source) {
        this._source = source;
        this._match = match;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            final MatchingIterator clone = (MatchingIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._isRestartable = false;
            return clone.reset();
        }
        catch (final CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }
    
    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._source.setStartNode(node);
            this._position = 1;
            while ((node = this._source.next()) != -1 && node != this._match) {
                ++this._position;
            }
        }
        return this;
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        return this.resetPosition();
    }
    
    @Override
    public int next() {
        return this._source.next();
    }
    
    @Override
    public int getLast() {
        if (this._last == -1) {
            this._last = this._source.getLast();
        }
        return this._last;
    }
    
    @Override
    public int getPosition() {
        return this._position;
    }
    
    @Override
    public void setMark() {
        this._source.setMark();
    }
    
    @Override
    public void gotoMark() {
        this._source.gotoMark();
    }
}
