package sun.misc;

import java.io.PrintStream;

class RegexpNode
{
    char c;
    RegexpNode firstchild;
    RegexpNode nextsibling;
    int depth;
    boolean exact;
    Object result;
    String re;
    
    RegexpNode() {
        this.re = null;
        this.c = '#';
        this.depth = 0;
    }
    
    RegexpNode(final char c, final int depth) {
        this.re = null;
        this.c = c;
        this.depth = depth;
    }
    
    RegexpNode add(final char c) {
        RegexpNode regexpNode = this.firstchild;
        RegexpNode firstchild;
        if (regexpNode == null) {
            firstchild = new RegexpNode(c, this.depth + 1);
        }
        else {
            while (regexpNode != null) {
                if (regexpNode.c == c) {
                    return regexpNode;
                }
                regexpNode = regexpNode.nextsibling;
            }
            firstchild = new RegexpNode(c, this.depth + 1);
            firstchild.nextsibling = this.firstchild;
        }
        return this.firstchild = firstchild;
    }
    
    RegexpNode find(final char c) {
        for (RegexpNode regexpNode = this.firstchild; regexpNode != null; regexpNode = regexpNode.nextsibling) {
            if (regexpNode.c == c) {
                return regexpNode;
            }
        }
        return null;
    }
    
    void print(final PrintStream printStream) {
        if (this.nextsibling != null) {
            RegexpNode nextsibling = this;
            printStream.print("(");
            while (nextsibling != null) {
                printStream.write(nextsibling.c);
                if (nextsibling.firstchild != null) {
                    nextsibling.firstchild.print(printStream);
                }
                nextsibling = nextsibling.nextsibling;
                printStream.write((nextsibling != null) ? 124 : 41);
            }
        }
        else {
            printStream.write(this.c);
            if (this.firstchild != null) {
                this.firstchild.print(printStream);
            }
        }
    }
}
