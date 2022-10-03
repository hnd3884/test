package com.zoho.conf.tree;

public class ConfTreeBuilder extends ConfRadixTreeBuilderBase<ConfTreeBuilder>
{
    public static ConfTreeBuilder confTree() {
        return new ConfTreeBuilder();
    }
    
    public ConfTreeBuilder() {
        super(new ConfTree());
    }
    
    public ConfTree build() {
        return this.getInstance();
    }
}
