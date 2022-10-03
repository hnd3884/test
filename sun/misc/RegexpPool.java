package sun.misc;

import java.io.PrintStream;

public class RegexpPool
{
    private RegexpNode prefixMachine;
    private RegexpNode suffixMachine;
    private static final int BIG = Integer.MAX_VALUE;
    private int lastDepth;
    
    public RegexpPool() {
        this.prefixMachine = new RegexpNode();
        this.suffixMachine = new RegexpNode();
        this.lastDepth = Integer.MAX_VALUE;
    }
    
    public void add(final String s, final Object o) throws REException {
        this.add(s, o, false);
    }
    
    public void replace(final String s, final Object o) {
        try {
            this.add(s, o, true);
        }
        catch (final Exception ex) {}
    }
    
    public Object delete(final String s) {
        Object o = null;
        RegexpNode prefixMachine;
        RegexpNode find = prefixMachine = this.prefixMachine;
        int n = s.length() - 1;
        boolean b = true;
        if (!s.startsWith("*") || !s.endsWith("*")) {
            ++n;
        }
        if (n <= 0) {
            return null;
        }
        for (int n2 = 0; find != null; find = find.find(s.charAt(n2)), ++n2) {
            if (find.result != null && find.depth < Integer.MAX_VALUE && (!find.exact || n2 == n)) {
                prefixMachine = find;
            }
            if (n2 >= n) {
                break;
            }
        }
        RegexpNode regexpNode = this.suffixMachine;
        for (int n3 = n; --n3 >= 0 && regexpNode != null; regexpNode = regexpNode.find(s.charAt(n3))) {
            if (regexpNode.result != null && regexpNode.depth < Integer.MAX_VALUE) {
                b = false;
                prefixMachine = regexpNode;
            }
        }
        if (b) {
            if (s.equals(prefixMachine.re)) {
                o = prefixMachine.result;
                prefixMachine.result = null;
            }
        }
        else if (s.equals(prefixMachine.re)) {
            o = prefixMachine.result;
            prefixMachine.result = null;
        }
        return o;
    }
    
    public Object match(final String s) {
        return this.matchAfter(s, Integer.MAX_VALUE);
    }
    
    public Object matchNext(final String s) {
        return this.matchAfter(s, this.lastDepth);
    }
    
    private void add(final String re, final Object result, final boolean b) throws REException {
        int i = re.length();
        RegexpNode regexpNode;
        if (re.charAt(0) == '*') {
            for (regexpNode = this.suffixMachine; i > 1; regexpNode = regexpNode.add(re.charAt(--i))) {}
        }
        else {
            boolean exact = false;
            if (re.charAt(i - 1) == '*') {
                --i;
            }
            else {
                exact = true;
            }
            regexpNode = this.prefixMachine;
            for (int j = 0; j < i; ++j) {
                regexpNode = regexpNode.add(re.charAt(j));
            }
            regexpNode.exact = exact;
        }
        if (regexpNode.result != null && !b) {
            throw new REException(re + " is a duplicate");
        }
        regexpNode.re = re;
        regexpNode.result = result;
    }
    
    private Object matchAfter(final String s, final int n) {
        RegexpNode prefixMachine;
        RegexpNode find = prefixMachine = this.prefixMachine;
        int n2 = 0;
        int n3 = 0;
        final int length = s.length();
        if (length <= 0) {
            return null;
        }
        for (int n4 = 0; find != null; find = find.find(s.charAt(n4)), ++n4) {
            if (find.result != null && find.depth < n && (!find.exact || n4 == length)) {
                this.lastDepth = find.depth;
                prefixMachine = find;
                n2 = n4;
                n3 = length;
            }
            if (n4 >= length) {
                break;
            }
        }
        RegexpNode regexpNode = this.suffixMachine;
        for (int n5 = length; --n5 >= 0 && regexpNode != null; regexpNode = regexpNode.find(s.charAt(n5))) {
            if (regexpNode.result != null && regexpNode.depth < n) {
                this.lastDepth = regexpNode.depth;
                prefixMachine = regexpNode;
                n2 = 0;
                n3 = n5 + 1;
            }
        }
        Object o = prefixMachine.result;
        if (o != null && o instanceof RegexpTarget) {
            o = ((RegexpTarget)o).found(s.substring(n2, n3));
        }
        return o;
    }
    
    public void reset() {
        this.lastDepth = Integer.MAX_VALUE;
    }
    
    public void print(final PrintStream printStream) {
        printStream.print("Regexp pool:\n");
        if (this.suffixMachine.firstchild != null) {
            printStream.print(" Suffix machine: ");
            this.suffixMachine.firstchild.print(printStream);
            printStream.print("\n");
        }
        if (this.prefixMachine.firstchild != null) {
            printStream.print(" Prefix machine: ");
            this.prefixMachine.firstchild.print(printStream);
            printStream.print("\n");
        }
    }
}
