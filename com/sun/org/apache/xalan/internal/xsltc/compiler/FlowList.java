package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Iterator;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import java.util.Vector;

public final class FlowList
{
    private Vector _elements;
    
    public FlowList() {
        this._elements = null;
    }
    
    public FlowList(final InstructionHandle bh) {
        (this._elements = new Vector()).addElement(bh);
    }
    
    public FlowList(final FlowList list) {
        this._elements = list._elements;
    }
    
    public FlowList add(final InstructionHandle bh) {
        if (this._elements == null) {
            this._elements = new Vector();
        }
        this._elements.addElement(bh);
        return this;
    }
    
    public FlowList append(final FlowList right) {
        if (this._elements == null) {
            this._elements = right._elements;
        }
        else {
            final Vector temp = right._elements;
            if (temp != null) {
                for (int n = temp.size(), i = 0; i < n; ++i) {
                    this._elements.addElement(temp.elementAt(i));
                }
            }
        }
        return this;
    }
    
    public void backPatch(final InstructionHandle target) {
        if (this._elements != null) {
            for (int n = this._elements.size(), i = 0; i < n; ++i) {
                final BranchHandle bh = this._elements.elementAt(i);
                bh.setTarget(target);
            }
            this._elements.clear();
        }
    }
    
    public FlowList copyAndRedirect(final InstructionList oldList, final InstructionList newList) {
        final FlowList result = new FlowList();
        if (this._elements == null) {
            return result;
        }
        final int n = this._elements.size();
        final Iterator oldIter = oldList.iterator();
        final Iterator newIter = newList.iterator();
        while (oldIter.hasNext()) {
            final InstructionHandle oldIh = oldIter.next();
            final InstructionHandle newIh = newIter.next();
            for (int i = 0; i < n; ++i) {
                if (this._elements.elementAt(i) == oldIh) {
                    result.add(newIh);
                }
            }
        }
        return result;
    }
}
