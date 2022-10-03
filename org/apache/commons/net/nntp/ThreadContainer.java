package org.apache.commons.net.nntp;

class ThreadContainer
{
    Threadable threadable;
    ThreadContainer parent;
    ThreadContainer next;
    ThreadContainer child;
    
    boolean findChild(final ThreadContainer target) {
        return this.child != null && (this.child == target || this.child.findChild(target));
    }
    
    void flush() {
        if (this.parent != null && this.threadable == null) {
            throw new RuntimeException("no threadable in " + this.toString());
        }
        this.parent = null;
        if (this.threadable != null) {
            this.threadable.setChild((this.child == null) ? null : this.child.threadable);
        }
        if (this.child != null) {
            this.child.flush();
            this.child = null;
        }
        if (this.threadable != null) {
            this.threadable.setNext((this.next == null) ? null : this.next.threadable);
        }
        if (this.next != null) {
            this.next.flush();
            this.next = null;
        }
        this.threadable = null;
    }
    
    void reverseChildren() {
        if (this.child != null) {
            ThreadContainer prev = null;
            for (ThreadContainer kid = this.child, rest = kid.next; kid != null; kid = rest, rest = ((rest == null) ? null : rest.next)) {
                kid.next = prev;
                prev = kid;
            }
            this.child = prev;
            for (ThreadContainer kid = this.child; kid != null; kid = kid.next) {
                kid.reverseChildren();
            }
        }
    }
}
