package javax.swing.text;

import java.util.Enumeration;
import java.util.Stack;

public class ElementIterator implements Cloneable
{
    private Element root;
    private Stack<StackItem> elementStack;
    
    public ElementIterator(final Document document) {
        this.elementStack = null;
        this.root = document.getDefaultRootElement();
    }
    
    public ElementIterator(final Element root) {
        this.elementStack = null;
        this.root = root;
    }
    
    public synchronized Object clone() {
        try {
            final ElementIterator elementIterator = new ElementIterator(this.root);
            if (this.elementStack != null) {
                elementIterator.elementStack = new Stack<StackItem>();
                for (int i = 0; i < this.elementStack.size(); ++i) {
                    elementIterator.elementStack.push((StackItem)((StackItem)this.elementStack.elementAt(i)).clone());
                }
            }
            return elementIterator;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    public Element first() {
        if (this.root == null) {
            return null;
        }
        this.elementStack = new Stack<StackItem>();
        if (this.root.getElementCount() != 0) {
            this.elementStack.push(new StackItem(this.root));
        }
        return this.root;
    }
    
    public int depth() {
        if (this.elementStack == null) {
            return 0;
        }
        return this.elementStack.size();
    }
    
    public Element current() {
        if (this.elementStack == null) {
            return this.first();
        }
        if (this.elementStack.empty()) {
            return null;
        }
        final StackItem stackItem = this.elementStack.peek();
        final Element access$100 = stackItem.getElement();
        final int access$101 = stackItem.getIndex();
        if (access$101 == -1) {
            return access$100;
        }
        return access$100.getElement(access$101);
    }
    
    public Element next() {
        if (this.elementStack == null) {
            return this.first();
        }
        if (this.elementStack.isEmpty()) {
            return null;
        }
        final StackItem stackItem = this.elementStack.peek();
        final Element access$100 = stackItem.getElement();
        final int access$101 = stackItem.getIndex();
        if (access$101 + 1 < access$100.getElementCount()) {
            final Element element = access$100.getElement(access$101 + 1);
            if (element.isLeaf()) {
                stackItem.incrementIndex();
            }
            else {
                this.elementStack.push(new StackItem(element));
            }
            return element;
        }
        this.elementStack.pop();
        if (!this.elementStack.isEmpty()) {
            this.elementStack.peek().incrementIndex();
            return this.next();
        }
        return null;
    }
    
    public Element previous() {
        final int size;
        if (this.elementStack == null || (size = this.elementStack.size()) == 0) {
            return null;
        }
        final StackItem stackItem = this.elementStack.peek();
        final Element access$100 = stackItem.getElement();
        int access$101 = stackItem.getIndex();
        if (access$101 > 0) {
            return this.getDeepestLeaf(access$100.getElement(--access$101));
        }
        if (access$101 == 0) {
            return access$100;
        }
        if (access$101 != -1) {
            return null;
        }
        if (size == 1) {
            return null;
        }
        final StackItem stackItem2 = this.elementStack.pop();
        final StackItem stackItem3 = this.elementStack.peek();
        this.elementStack.push(stackItem2);
        final Element access$102 = stackItem3.getElement();
        final int access$103 = stackItem3.getIndex();
        return (access$103 == -1) ? access$102 : this.getDeepestLeaf(access$102.getElement(access$103));
    }
    
    private Element getDeepestLeaf(final Element element) {
        if (element.isLeaf()) {
            return element;
        }
        final int elementCount = element.getElementCount();
        if (elementCount == 0) {
            return element;
        }
        return this.getDeepestLeaf(element.getElement(elementCount - 1));
    }
    
    private void dumpTree() {
        Element next;
        while ((next = this.next()) != null) {
            System.out.println("elem: " + next.getName());
            final AttributeSet attributes = next.getAttributes();
            String s = "";
            final Enumeration<?> attributeNames = attributes.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                final Object nextElement = attributeNames.nextElement();
                final Object attribute = attributes.getAttribute(nextElement);
                if (attribute instanceof AttributeSet) {
                    s = s + nextElement + "=**AttributeSet** ";
                }
                else {
                    s = s + nextElement + "=" + attribute + " ";
                }
            }
            System.out.println("attributes: " + s);
        }
    }
    
    private class StackItem implements Cloneable
    {
        Element item;
        int childIndex;
        
        private StackItem(final Element item) {
            this.item = item;
            this.childIndex = -1;
        }
        
        private void incrementIndex() {
            ++this.childIndex;
        }
        
        private Element getElement() {
            return this.item;
        }
        
        private int getIndex() {
            return this.childIndex;
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
