package com.sun.java_cup.internal.runtime;

public class Symbol
{
    public int sym;
    public int parse_state;
    boolean used_by_parser;
    public int left;
    public int right;
    public Object value;
    
    public Symbol(final int id, final int l, final int r, final Object o) {
        this(id);
        this.left = l;
        this.right = r;
        this.value = o;
    }
    
    public Symbol(final int id, final Object o) {
        this(id);
        this.left = -1;
        this.right = -1;
        this.value = o;
    }
    
    public Symbol(final int sym_num, final int l, final int r) {
        this.used_by_parser = false;
        this.sym = sym_num;
        this.left = l;
        this.right = r;
        this.value = null;
    }
    
    public Symbol(final int sym_num) {
        this(sym_num, -1);
        this.left = -1;
        this.right = -1;
        this.value = null;
    }
    
    public Symbol(final int sym_num, final int state) {
        this.used_by_parser = false;
        this.sym = sym_num;
        this.parse_state = state;
    }
    
    @Override
    public String toString() {
        return "#" + this.sym;
    }
}
