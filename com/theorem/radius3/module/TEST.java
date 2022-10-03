package com.theorem.radius3.module;

public abstract class TEST
{
    protected Integer a;
    
    public TEST(final Integer a) {
        this.a = a;
    }
    
    public TEST() {
        this.a = new Integer(4000000);
    }
    
    public abstract void test();
}
