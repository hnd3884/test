package javax.imageio.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Iterator;

class PartialOrderIterator implements Iterator
{
    LinkedList zeroList;
    Map inDegrees;
    
    public PartialOrderIterator(final Iterator iterator) {
        this.zeroList = new LinkedList();
        this.inDegrees = new HashMap();
        while (iterator.hasNext()) {
            final DigraphNode digraphNode = iterator.next();
            final int inDegree = digraphNode.getInDegree();
            this.inDegrees.put(digraphNode, new Integer(inDegree));
            if (inDegree == 0) {
                this.zeroList.add(digraphNode);
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return !this.zeroList.isEmpty();
    }
    
    @Override
    public Object next() {
        final DigraphNode digraphNode = this.zeroList.removeFirst();
        final Iterator outNodes = digraphNode.getOutNodes();
        while (outNodes.hasNext()) {
            final DigraphNode digraphNode2 = outNodes.next();
            final int n = this.inDegrees.get(digraphNode2) - 1;
            this.inDegrees.put(digraphNode2, new Integer(n));
            if (n == 0) {
                this.zeroList.add(digraphNode2);
            }
        }
        return digraphNode.getData();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
