package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class DanishStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final char[] g_v;
    private static final char[] g_s_ending;
    private int I_x;
    private int I_p1;
    private StringBuilder S_ch;
    
    public DanishStemmer() {
        this.S_ch = new StringBuilder();
    }
    
    private void copy_from(final DanishStemmer other) {
        this.I_x = other.I_x;
        this.I_p1 = other.I_p1;
        this.S_ch = other.S_ch;
        super.copy_from(other);
    }
    
    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        final int v_1 = this.cursor;
        final int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.I_x = this.cursor;
        this.cursor = v_1;
        while (true) {
            final int v_2 = this.cursor;
            if (this.in_grouping(DanishStemmer.g_v, 97, 248)) {
                this.cursor = v_2;
                while (!this.out_grouping(DanishStemmer.g_v, 97, 248)) {
                    if (this.cursor >= this.limit) {
                        return false;
                    }
                    ++this.cursor;
                }
                this.I_p1 = this.cursor;
                if (this.I_p1 < this.I_x) {
                    this.I_p1 = this.I_x;
                }
                return true;
            }
            this.cursor = v_2;
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
    }
    
    private boolean r_main_suffix() {
        final int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        final int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        final int among_var = this.find_among_b(DanishStemmer.a_0, 32);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.in_grouping_b(DanishStemmer.g_s_ending, 97, 229)) {
                    return false;
                }
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_consonant_pair() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        final int v_3 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_2;
        this.ket = this.cursor;
        if (this.find_among_b(DanishStemmer.a_1, 4) == 0) {
            this.limit_backward = v_3;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_3;
        this.cursor = this.limit - v_1;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }
    
    private boolean r_other_suffix() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (this.eq_s_b(2, "st")) {
            this.bra = this.cursor;
            if (this.eq_s_b(2, "ig")) {
                this.slice_del();
            }
        }
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        final int v_3 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_2;
        this.ket = this.cursor;
        final int among_var = this.find_among_b(DanishStemmer.a_2, 5);
        if (among_var == 0) {
            this.limit_backward = v_3;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_3;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                final int v_4 = this.limit - this.cursor;
                if (!this.r_consonant_pair()) {}
                this.cursor = this.limit - v_4;
                break;
            }
            case 2: {
                this.slice_from("l\u00f8s");
                break;
            }
        }
        return true;
    }
    
    private boolean r_undouble() {
        final int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        final int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        if (!this.out_grouping_b(DanishStemmer.g_v, 97, 248)) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.S_ch = this.slice_to(this.S_ch);
        this.limit_backward = v_2;
        if (!this.eq_v_b(this.S_ch)) {
            return false;
        }
        this.slice_del();
        return true;
    }
    
    @Override
    public boolean stem() {
        final int v_1 = this.cursor;
        if (!this.r_mark_regions()) {}
        this.cursor = v_1;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_2 = this.limit - this.cursor;
        if (!this.r_main_suffix()) {}
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.r_consonant_pair()) {}
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        if (!this.r_other_suffix()) {}
        this.cursor = this.limit - v_4;
        final int v_5 = this.limit - this.cursor;
        if (!this.r_undouble()) {}
        this.cursor = this.limit - v_5;
        this.cursor = this.limit_backward;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DanishStemmer;
    }
    
    @Override
    public int hashCode() {
        return DanishStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("hed", -1, 1, "", DanishStemmer.methodObject), new Among("ethed", 0, 1, "", DanishStemmer.methodObject), new Among("ered", -1, 1, "", DanishStemmer.methodObject), new Among("e", -1, 1, "", DanishStemmer.methodObject), new Among("erede", 3, 1, "", DanishStemmer.methodObject), new Among("ende", 3, 1, "", DanishStemmer.methodObject), new Among("erende", 5, 1, "", DanishStemmer.methodObject), new Among("ene", 3, 1, "", DanishStemmer.methodObject), new Among("erne", 3, 1, "", DanishStemmer.methodObject), new Among("ere", 3, 1, "", DanishStemmer.methodObject), new Among("en", -1, 1, "", DanishStemmer.methodObject), new Among("heden", 10, 1, "", DanishStemmer.methodObject), new Among("eren", 10, 1, "", DanishStemmer.methodObject), new Among("er", -1, 1, "", DanishStemmer.methodObject), new Among("heder", 13, 1, "", DanishStemmer.methodObject), new Among("erer", 13, 1, "", DanishStemmer.methodObject), new Among("s", -1, 2, "", DanishStemmer.methodObject), new Among("heds", 16, 1, "", DanishStemmer.methodObject), new Among("es", 16, 1, "", DanishStemmer.methodObject), new Among("endes", 18, 1, "", DanishStemmer.methodObject), new Among("erendes", 19, 1, "", DanishStemmer.methodObject), new Among("enes", 18, 1, "", DanishStemmer.methodObject), new Among("ernes", 18, 1, "", DanishStemmer.methodObject), new Among("eres", 18, 1, "", DanishStemmer.methodObject), new Among("ens", 16, 1, "", DanishStemmer.methodObject), new Among("hedens", 24, 1, "", DanishStemmer.methodObject), new Among("erens", 24, 1, "", DanishStemmer.methodObject), new Among("ers", 16, 1, "", DanishStemmer.methodObject), new Among("ets", 16, 1, "", DanishStemmer.methodObject), new Among("erets", 28, 1, "", DanishStemmer.methodObject), new Among("et", -1, 1, "", DanishStemmer.methodObject), new Among("eret", 30, 1, "", DanishStemmer.methodObject) };
        a_1 = new Among[] { new Among("gd", -1, -1, "", DanishStemmer.methodObject), new Among("dt", -1, -1, "", DanishStemmer.methodObject), new Among("gt", -1, -1, "", DanishStemmer.methodObject), new Among("kt", -1, -1, "", DanishStemmer.methodObject) };
        a_2 = new Among[] { new Among("ig", -1, 1, "", DanishStemmer.methodObject), new Among("lig", 0, 1, "", DanishStemmer.methodObject), new Among("elig", 1, 1, "", DanishStemmer.methodObject), new Among("els", -1, 1, "", DanishStemmer.methodObject), new Among("l\u00f8st", -1, 2, "", DanishStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '0', '\0', '\u0080' };
        g_s_ending = new char[] { '\u00ef', '\u00fe', '*', '\u0003', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0010' };
    }
}
