package javax.print.attribute;

import java.util.Vector;
import java.io.Serializable;

public abstract class SetOfIntegerSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = 3666874174847632203L;
    private int[][] members;
    
    protected SetOfIntegerSyntax(final String s) {
        this.members = parse(s);
    }
    
    private static int[][] parse(final String s) {
        final Vector vector = new Vector();
        final int n = (s == null) ? 0 : s.length();
        int i = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (i < n) {
            final char char1 = s.charAt(i++);
            switch (n2) {
                case 0: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 0;
                        continue;
                    }
                    final int digit;
                    if ((digit = Character.digit(char1, 10)) != -1) {
                        n3 = digit;
                        n2 = 1;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
                case 1: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 2;
                        continue;
                    }
                    final int digit2;
                    if ((digit2 = Character.digit(char1, 10)) != -1) {
                        n3 = 10 * n3 + digit2;
                        n2 = 1;
                        continue;
                    }
                    if (char1 == '-' || char1 == ':') {
                        n2 = 3;
                        continue;
                    }
                    if (char1 == ',') {
                        accumulate(vector, n3, n3);
                        n2 = 6;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
                case 2: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 2;
                        continue;
                    }
                    if (char1 == '-' || char1 == ':') {
                        n2 = 3;
                        continue;
                    }
                    if (char1 == ',') {
                        accumulate(vector, n3, n3);
                        n2 = 6;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
                case 3: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 3;
                        continue;
                    }
                    final int digit3;
                    if ((digit3 = Character.digit(char1, 10)) != -1) {
                        n4 = digit3;
                        n2 = 4;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
                case 4: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 5;
                        continue;
                    }
                    final int digit4;
                    if ((digit4 = Character.digit(char1, 10)) != -1) {
                        n4 = 10 * n4 + digit4;
                        n2 = 4;
                        continue;
                    }
                    if (char1 == ',') {
                        accumulate(vector, n3, n4);
                        n2 = 6;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
                case 5: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 5;
                        continue;
                    }
                    if (char1 == ',') {
                        accumulate(vector, n3, n4);
                        n2 = 6;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
                case 6: {
                    if (Character.isWhitespace(char1)) {
                        n2 = 6;
                        continue;
                    }
                    final int digit5;
                    if ((digit5 = Character.digit(char1, 10)) != -1) {
                        n3 = digit5;
                        n2 = 1;
                        continue;
                    }
                    throw new IllegalArgumentException();
                }
            }
        }
        switch (n2) {
            case 1:
            case 2: {
                accumulate(vector, n3, n3);
                break;
            }
            case 4:
            case 5: {
                accumulate(vector, n3, n4);
                break;
            }
            case 3:
            case 6: {
                throw new IllegalArgumentException();
            }
        }
        return canonicalArrayForm(vector);
    }
    
    private static void accumulate(final Vector vector, final int n, final int n2) {
        if (n <= n2) {
            vector.add(new int[] { n, n2 });
            for (int i = vector.size() - 2; i >= 0; --i) {
                final int[] array = vector.elementAt(i);
                final int n3 = array[0];
                final int n4 = array[1];
                final int[] array2 = vector.elementAt(i + 1);
                final int n5 = array2[0];
                final int n6 = array2[1];
                if (Math.max(n3, n5) - Math.min(n4, n6) <= 1) {
                    vector.setElementAt(new int[] { Math.min(n3, n5), Math.max(n4, n6) }, i);
                    vector.remove(i + 1);
                }
                else {
                    if (n3 <= n5) {
                        break;
                    }
                    vector.setElementAt(array2, i);
                    vector.setElementAt(array, i + 1);
                }
            }
        }
    }
    
    private static int[][] canonicalArrayForm(final Vector vector) {
        return vector.toArray(new int[vector.size()][]);
    }
    
    protected SetOfIntegerSyntax(final int[][] array) {
        this.members = parse(array);
    }
    
    private static int[][] parse(final int[][] array) {
        final Vector vector = new Vector();
        for (int n = (array == null) ? 0 : array.length, i = 0; i < n; ++i) {
            int n3;
            int n2;
            if (array[i].length == 1) {
                n2 = (n3 = array[i][0]);
            }
            else {
                if (array[i].length != 2) {
                    throw new IllegalArgumentException();
                }
                n3 = array[i][0];
                n2 = array[i][1];
            }
            if (n3 <= n2 && n3 < 0) {
                throw new IllegalArgumentException();
            }
            accumulate(vector, n3, n2);
        }
        return canonicalArrayForm(vector);
    }
    
    protected SetOfIntegerSyntax(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        this.members = new int[][] { { n, n } };
    }
    
    protected SetOfIntegerSyntax(final int n, final int n2) {
        if (n <= n2 && n < 0) {
            throw new IllegalArgumentException();
        }
        this.members = ((n <= n2) ? new int[][] { { n, n2 } } : new int[0][]);
    }
    
    public int[][] getMembers() {
        final int length = this.members.length;
        final int[][] array = new int[length][];
        for (int i = 0; i < length; ++i) {
            array[i] = new int[] { this.members[i][0], this.members[i][1] };
        }
        return array;
    }
    
    public boolean contains(final int n) {
        for (int length = this.members.length, i = 0; i < length; ++i) {
            if (n < this.members[i][0]) {
                return false;
            }
            if (n <= this.members[i][1]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final IntegerSyntax integerSyntax) {
        return this.contains(integerSyntax.getValue());
    }
    
    public int next(final int n) {
        for (int length = this.members.length, i = 0; i < length; ++i) {
            if (n < this.members[i][0]) {
                return this.members[i][0];
            }
            if (n < this.members[i][1]) {
                return n + 1;
            }
        }
        return -1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof SetOfIntegerSyntax)) {
            return false;
        }
        final int[][] members = this.members;
        final int[][] members2 = ((SetOfIntegerSyntax)o).members;
        final int length = members.length;
        if (length == members2.length) {
            for (int i = 0; i < length; ++i) {
                if (members[i][0] != members2[i][0] || members[i][1] != members2[i][1]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int length = this.members.length, i = 0; i < length; ++i) {
            n += this.members[i][0] + this.members[i][1];
        }
        return n;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int length = this.members.length, i = 0; i < length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this.members[i][0]);
            if (this.members[i][0] != this.members[i][1]) {
                sb.append('-');
                sb.append(this.members[i][1]);
            }
        }
        return sb.toString();
    }
}
