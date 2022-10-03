package com.adventnet.mfw.bean;

public class BeanProps
{
    private Object beanObj;
    private int txType;
    
    public BeanProps(final Object beanObj, final int txType) {
        this.beanObj = null;
        this.txType = -1;
        this.beanObj = beanObj;
        this.txType = txType;
    }
    
    public Object getObject() {
        return this.beanObj;
    }
    
    public int getTx() {
        return this.txType;
    }
    
    public static void main(final String[] args) {
        System.out.println("::");
    }
}
