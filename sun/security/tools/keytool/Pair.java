package sun.security.tools.keytool;

import java.util.Objects;

class Pair<A, B>
{
    public final A fst;
    public final B snd;
    
    public Pair(final A fst, final B snd) {
        this.fst = fst;
        this.snd = snd;
    }
    
    @Override
    public String toString() {
        return "Pair[" + this.fst + "," + this.snd + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Pair && Objects.equals(this.fst, ((Pair)o).fst) && Objects.equals(this.snd, ((Pair)o).snd);
    }
    
    @Override
    public int hashCode() {
        if (this.fst == null) {
            return (this.snd == null) ? 0 : (this.snd.hashCode() + 1);
        }
        if (this.snd == null) {
            return this.fst.hashCode() + 2;
        }
        return this.fst.hashCode() * 17 + this.snd.hashCode();
    }
    
    public static <A, B> Pair<A, B> of(final A a, final B b) {
        return new Pair<A, B>(a, b);
    }
}
