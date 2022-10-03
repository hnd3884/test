package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public class ReverseAxesWalker extends AxesWalker
{
    static final long serialVersionUID = 2847007647832768941L;
    protected DTMAxisIterator m_iterator;
    
    ReverseAxesWalker(final LocPathIterator locPathIterator, final int axis) {
        super(locPathIterator, axis);
    }
    
    @Override
    public void setRoot(final int root) {
        super.setRoot(root);
        (this.m_iterator = this.getDTM(root).getAxisIterator(this.m_axis)).setStartNode(root);
    }
    
    @Override
    public void detach() {
        this.m_iterator = null;
        super.detach();
    }
    
    @Override
    protected int getNextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        final int next = this.m_iterator.next();
        if (this.m_isFresh) {
            this.m_isFresh = false;
        }
        if (-1 == next) {
            this.m_foundLast = true;
        }
        return next;
    }
    
    @Override
    public boolean isReverseAxes() {
        return true;
    }
    
    @Override
    protected int getProximityPosition(final int predicateIndex) {
        if (predicateIndex < 0) {
            return -1;
        }
        int count = this.m_proximityPositions[predicateIndex];
        if (count <= 0) {
            final AxesWalker savedWalker = this.wi().getLastUsedWalker();
            try {
                final ReverseAxesWalker clone = (ReverseAxesWalker)this.clone();
                clone.setRoot(this.getRoot());
                clone.setPredicateCount(predicateIndex);
                clone.setPrevWalker(null);
                clone.setNextWalker(null);
                this.wi().setLastUsedWalker(clone);
                ++count;
                int next;
                while (-1 != (next = clone.nextNode())) {
                    ++count;
                }
                this.m_proximityPositions[predicateIndex] = count;
            }
            catch (final CloneNotSupportedException ex) {}
            finally {
                this.wi().setLastUsedWalker(savedWalker);
            }
        }
        return count;
    }
    
    @Override
    protected void countProximityPosition(final int i) {
        if (i < this.m_proximityPositions.length) {
            final int[] proximityPositions = this.m_proximityPositions;
            --proximityPositions[i];
        }
    }
    
    @Override
    public int getLastPos(final XPathContext xctxt) {
        int count = 0;
        final AxesWalker savedWalker = this.wi().getLastUsedWalker();
        try {
            final ReverseAxesWalker clone = (ReverseAxesWalker)this.clone();
            clone.setRoot(this.getRoot());
            clone.setPredicateCount(this.getPredicateCount() - 1);
            clone.setPrevWalker(null);
            clone.setNextWalker(null);
            this.wi().setLastUsedWalker(clone);
            int next;
            while (-1 != (next = clone.nextNode())) {
                ++count;
            }
        }
        catch (final CloneNotSupportedException ex) {}
        finally {
            this.wi().setLastUsedWalker(savedWalker);
        }
        return count;
    }
    
    @Override
    public boolean isDocOrdered() {
        return false;
    }
}
