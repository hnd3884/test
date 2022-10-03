package org.apache.axiom.locator;

final class Feature
{
    private final String name;
    private final int priority;
    
    Feature(final String name, final int priority) {
        this.name = name;
        this.priority = priority;
    }
    
    String getName() {
        return this.name;
    }
    
    int getPriority() {
        return this.priority;
    }
    
    @Override
    public String toString() {
        return this.name + "(priority=" + this.priority + ")";
    }
}
