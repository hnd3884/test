package org.apache.xerces.impl.xpath.regex;

import java.util.Vector;

class Op
{
    static final int DOT = 0;
    static final int CHAR = 1;
    static final int RANGE = 3;
    static final int NRANGE = 4;
    static final int ANCHOR = 5;
    static final int STRING = 6;
    static final int CLOSURE = 7;
    static final int NONGREEDYCLOSURE = 8;
    static final int QUESTION = 9;
    static final int NONGREEDYQUESTION = 10;
    static final int UNION = 11;
    static final int CAPTURE = 15;
    static final int BACKREFERENCE = 16;
    static final int LOOKAHEAD = 20;
    static final int NEGATIVELOOKAHEAD = 21;
    static final int LOOKBEHIND = 22;
    static final int NEGATIVELOOKBEHIND = 23;
    static final int INDEPENDENT = 24;
    static final int MODIFIER = 25;
    static final int CONDITION = 26;
    static int nofinstances;
    static final boolean COUNT = false;
    final int type;
    Op next;
    
    static Op createDot() {
        return new Op(0);
    }
    
    static CharOp createChar(final int n) {
        return new CharOp(1, n);
    }
    
    static CharOp createAnchor(final int n) {
        return new CharOp(5, n);
    }
    
    static CharOp createCapture(final int n, final Op next) {
        final CharOp charOp = new CharOp(15, n);
        charOp.next = next;
        return charOp;
    }
    
    static UnionOp createUnion(final int n) {
        return new UnionOp(11, n);
    }
    
    static ChildOp createClosure(final int n) {
        return new ModifierOp(7, n, -1);
    }
    
    static ChildOp createNonGreedyClosure() {
        return new ChildOp(8);
    }
    
    static ChildOp createQuestion(final boolean b) {
        return new ChildOp(b ? 10 : 9);
    }
    
    static RangeOp createRange(final Token token) {
        return new RangeOp(3, token);
    }
    
    static ChildOp createLook(final int n, final Op next, final Op child) {
        final ChildOp childOp = new ChildOp(n);
        childOp.setChild(child);
        childOp.next = next;
        return childOp;
    }
    
    static CharOp createBackReference(final int n) {
        return new CharOp(16, n);
    }
    
    static StringOp createString(final String s) {
        return new StringOp(6, s);
    }
    
    static ChildOp createIndependent(final Op next, final Op child) {
        final ChildOp childOp = new ChildOp(24);
        childOp.setChild(child);
        childOp.next = next;
        return childOp;
    }
    
    static ModifierOp createModifier(final Op next, final Op child, final int n, final int n2) {
        final ModifierOp modifierOp = new ModifierOp(25, n, n2);
        modifierOp.setChild(child);
        modifierOp.next = next;
        return modifierOp;
    }
    
    static ConditionOp createCondition(final Op next, final int n, final Op op, final Op op2, final Op op3) {
        final ConditionOp conditionOp = new ConditionOp(26, n, op, op2, op3);
        conditionOp.next = next;
        return conditionOp;
    }
    
    protected Op(final int type) {
        this.next = null;
        this.type = type;
    }
    
    int size() {
        return 0;
    }
    
    Op elementAt(final int n) {
        throw new RuntimeException("Internal Error: type=" + this.type);
    }
    
    Op getChild() {
        throw new RuntimeException("Internal Error: type=" + this.type);
    }
    
    int getData() {
        throw new RuntimeException("Internal Error: type=" + this.type);
    }
    
    int getData2() {
        throw new RuntimeException("Internal Error: type=" + this.type);
    }
    
    RangeToken getToken() {
        throw new RuntimeException("Internal Error: type=" + this.type);
    }
    
    String getString() {
        throw new RuntimeException("Internal Error: type=" + this.type);
    }
    
    static {
        Op.nofinstances = 0;
    }
    
    static class CharOp extends Op
    {
        final int charData;
        
        CharOp(final int n, final int charData) {
            super(n);
            this.charData = charData;
        }
        
        int getData() {
            return this.charData;
        }
    }
    
    static class ChildOp extends Op
    {
        Op child;
        
        ChildOp(final int n) {
            super(n);
        }
        
        void setChild(final Op child) {
            this.child = child;
        }
        
        Op getChild() {
            return this.child;
        }
    }
    
    static class ConditionOp extends Op
    {
        final int refNumber;
        final Op condition;
        final Op yes;
        final Op no;
        
        ConditionOp(final int n, final int refNumber, final Op condition, final Op yes, final Op no) {
            super(n);
            this.refNumber = refNumber;
            this.condition = condition;
            this.yes = yes;
            this.no = no;
        }
    }
    
    static class ModifierOp extends ChildOp
    {
        final int v1;
        final int v2;
        
        ModifierOp(final int n, final int v1, final int v2) {
            super(n);
            this.v1 = v1;
            this.v2 = v2;
        }
        
        int getData() {
            return this.v1;
        }
        
        int getData2() {
            return this.v2;
        }
    }
    
    static class RangeOp extends Op
    {
        final Token tok;
        
        RangeOp(final int n, final Token tok) {
            super(n);
            this.tok = tok;
        }
        
        RangeToken getToken() {
            return (RangeToken)this.tok;
        }
    }
    
    static class StringOp extends Op
    {
        final String string;
        
        StringOp(final int n, final String string) {
            super(n);
            this.string = string;
        }
        
        String getString() {
            return this.string;
        }
    }
    
    static class UnionOp extends Op
    {
        final Vector branches;
        
        UnionOp(final int n, final int n2) {
            super(n);
            this.branches = new Vector(n2);
        }
        
        void addElement(final Op op) {
            this.branches.addElement(op);
        }
        
        int size() {
            return this.branches.size();
        }
        
        Op elementAt(final int n) {
            return this.branches.elementAt(n);
        }
    }
}
