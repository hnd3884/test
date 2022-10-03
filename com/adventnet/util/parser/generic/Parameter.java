package com.adventnet.util.parser.generic;

class Parameter
{
    String name;
    String value;
    
    Parameter(final String name, final String value) {
        this.name = null;
        this.value = null;
        this.name = name;
        this.value = value;
    }
    
    String getParamName() {
        return this.name;
    }
    
    String getParamValue() {
        return this.value;
    }
    
    void printName() {
        System.out.println("Name: " + this.name);
    }
    
    void printValue() {
        System.out.println("Value: " + this.value);
    }
}
