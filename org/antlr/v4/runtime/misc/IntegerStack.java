package org.antlr.v4.runtime.misc;

public class IntegerStack extends IntegerList
{
    public IntegerStack() {
    }
    
    public IntegerStack(final int capacity) {
        super(capacity);
    }
    
    public IntegerStack(final IntegerStack list) {
        super(list);
    }
    
    public final void push(final int value) {
        this.add(value);
    }
    
    public final int pop() {
        return this.removeAt(this.size() - 1);
    }
    
    public final int peek() {
        return this.get(this.size() - 1);
    }
}
