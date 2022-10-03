package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class DutchStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final Among[] a_3;
    private static final Among[] a_4;
    private static final Among[] a_5;
    private static final char[] g_v;
    private static final char[] g_v_I;
    private static final char[] g_v_j;
    private int I_p2;
    private int I_p1;
    private boolean B_e_found;
    
    private void copy_from(final DutchStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.B_e_found = other.B_e_found;
        super.copy_from(other);
    }
    
    private boolean r_prelude() {
        final int v_1 = this.cursor;
        int v_2 = 0;
    Label_0163:
        while (true) {
            v_2 = this.cursor;
            this.bra = this.cursor;
            final int among_var = this.find_among(DutchStemmer.a_0, 11);
            if (among_var == 0) {
                break;
            }
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break Label_0163;
                }
                case 1: {
                    this.slice_from("a");
                    continue;
                }
                case 2: {
                    this.slice_from("e");
                    continue;
                }
                case 3: {
                    this.slice_from("i");
                    continue;
                }
                case 4: {
                    this.slice_from("o");
                    continue;
                }
                case 5: {
                    this.slice_from("u");
                    continue;
                }
                case 6: {
                    if (this.cursor >= this.limit) {
                        break Label_0163;
                    }
                    ++this.cursor;
                    continue;
                }
            }
        }
        this.cursor = v_2;
        this.cursor = v_1;
        final int v_3 = this.cursor;
        this.bra = this.cursor;
        if (!this.eq_s(1, "y")) {
            this.cursor = v_3;
        }
        else {
            this.ket = this.cursor;
            this.slice_from("Y");
        }
        int v_4 = 0;
    Block_8:
        while (true) {
            v_4 = this.cursor;
            int v_5;
            while (true) {
                v_5 = this.cursor;
                if (this.in_grouping(DutchStemmer.g_v, 97, 232)) {
                    this.bra = this.cursor;
                    final int v_6 = this.cursor;
                    if (this.eq_s(1, "i")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(DutchStemmer.g_v, 97, 232)) {
                            this.slice_from("I");
                            break;
                        }
                    }
                    this.cursor = v_6;
                    if (this.eq_s(1, "y")) {
                        this.ket = this.cursor;
                        this.slice_from("Y");
                        break;
                    }
                }
                this.cursor = v_5;
                if (this.cursor >= this.limit) {
                    break Block_8;
                }
                ++this.cursor;
            }
            this.cursor = v_5;
        }
        this.cursor = v_4;
        return true;
    }
    
    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        while (!this.in_grouping(DutchStemmer.g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(DutchStemmer.g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        if (this.I_p1 < 3) {
            this.I_p1 = 3;
        }
        while (!this.in_grouping(DutchStemmer.g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(DutchStemmer.g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p2 = this.cursor;
        return true;
    }
    
    private boolean r_postlude() {
        int v_1 = 0;
    Label_0116:
        while (true) {
            v_1 = this.cursor;
            this.bra = this.cursor;
            final int among_var = this.find_among(DutchStemmer.a_1, 3);
            if (among_var == 0) {
                break;
            }
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break Label_0116;
                }
                case 1: {
                    this.slice_from("y");
                    continue;
                }
                case 2: {
                    this.slice_from("i");
                    continue;
                }
                case 3: {
                    if (this.cursor >= this.limit) {
                        break Label_0116;
                    }
                    ++this.cursor;
                    continue;
                }
            }
        }
        this.cursor = v_1;
        return true;
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }
    
    private boolean r_undouble() {
        final int v_1 = this.limit - this.cursor;
        if (this.find_among_b(DutchStemmer.a_2, 3) == 0) {
            return false;
        }
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }
    
    private boolean r_e_ending() {
        this.B_e_found = false;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "e")) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        final int v_1 = this.limit - this.cursor;
        if (!this.out_grouping_b(DutchStemmer.g_v, 97, 232)) {
            return false;
        }
        this.cursor = this.limit - v_1;
        this.slice_del();
        this.B_e_found = true;
        return this.r_undouble();
    }
    
    private boolean r_en_ending() {
        if (!this.r_R1()) {
            return false;
        }
        final int v_1 = this.limit - this.cursor;
        if (!this.out_grouping_b(DutchStemmer.g_v, 97, 232)) {
            return false;
        }
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(3, "gem")) {
            this.cursor = this.limit - v_2;
            this.slice_del();
            return this.r_undouble();
        }
        return false;
    }
    
    private boolean r_standard_suffix() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(DutchStemmer.a_3, 5);
        if (among_var != 0) {
            this.bra = this.cursor;
            switch (among_var) {
                case 1: {
                    if (!this.r_R1()) {
                        break;
                    }
                    this.slice_from("heid");
                    break;
                }
                case 2: {
                    if (!this.r_en_ending()) {
                        break;
                    }
                    break;
                }
                case 3: {
                    if (!this.r_R1()) {
                        break;
                    }
                    if (!this.out_grouping_b(DutchStemmer.g_v_j, 97, 232)) {
                        break;
                    }
                    this.slice_del();
                    break;
                }
            }
        }
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.r_e_ending()) {}
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (this.eq_s_b(4, "heid")) {
            this.bra = this.cursor;
            if (this.r_R2()) {
                final int v_4 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "c")) {
                    this.cursor = this.limit - v_4;
                    this.slice_del();
                    this.ket = this.cursor;
                    if (this.eq_s_b(2, "en")) {
                        this.bra = this.cursor;
                        if (!this.r_en_ending()) {}
                    }
                }
            }
        }
        this.cursor = this.limit - v_3;
        final int v_5 = this.limit - this.cursor;
        this.ket = this.cursor;
        among_var = this.find_among_b(DutchStemmer.a_4, 6);
        if (among_var != 0) {
            this.bra = this.cursor;
            switch (among_var) {
                case 1: {
                    if (!this.r_R2()) {
                        break;
                    }
                    this.slice_del();
                    final int v_6 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (this.eq_s_b(2, "ig")) {
                        this.bra = this.cursor;
                        if (this.r_R2()) {
                            final int v_7 = this.limit - this.cursor;
                            if (!this.eq_s_b(1, "e")) {
                                this.cursor = this.limit - v_7;
                                this.slice_del();
                                break;
                            }
                        }
                    }
                    this.cursor = this.limit - v_6;
                    if (!this.r_undouble()) {
                        break;
                    }
                    break;
                }
                case 2: {
                    if (!this.r_R2()) {
                        break;
                    }
                    final int v_8 = this.limit - this.cursor;
                    if (!this.eq_s_b(1, "e")) {
                        this.cursor = this.limit - v_8;
                        this.slice_del();
                        break;
                    }
                    break;
                }
                case 3: {
                    if (!this.r_R2()) {
                        break;
                    }
                    this.slice_del();
                    if (!this.r_e_ending()) {
                        break;
                    }
                    break;
                }
                case 4: {
                    if (!this.r_R2()) {
                        break;
                    }
                    this.slice_del();
                    break;
                }
                case 5: {
                    if (!this.r_R2()) {
                        break;
                    }
                    if (!this.B_e_found) {
                        break;
                    }
                    this.slice_del();
                    break;
                }
            }
        }
        this.cursor = this.limit - v_5;
        final int v_9 = this.limit - this.cursor;
        if (this.out_grouping_b(DutchStemmer.g_v_I, 73, 232)) {
            final int v_10 = this.limit - this.cursor;
            if (this.find_among_b(DutchStemmer.a_5, 4) != 0) {
                if (this.out_grouping_b(DutchStemmer.g_v, 97, 232)) {
                    this.cursor = this.limit - v_10;
                    this.ket = this.cursor;
                    if (this.cursor > this.limit_backward) {
                        --this.cursor;
                        this.bra = this.cursor;
                        this.slice_del();
                    }
                }
            }
        }
        this.cursor = this.limit - v_9;
        return true;
    }
    
    @Override
    public boolean stem() {
        final int v_1 = this.cursor;
        if (!this.r_prelude()) {}
        this.cursor = v_1;
        final int v_2 = this.cursor;
        if (!this.r_mark_regions()) {}
        this.cursor = v_2;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_3 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {}
        this.cursor = this.limit - v_3;
        this.cursor = this.limit_backward;
        final int v_4 = this.cursor;
        if (!this.r_postlude()) {}
        this.cursor = v_4;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DutchStemmer;
    }
    
    @Override
    public int hashCode() {
        return DutchStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("", -1, 6, "", DutchStemmer.methodObject), new Among("\u00e1", 0, 1, "", DutchStemmer.methodObject), new Among("\u00e4", 0, 1, "", DutchStemmer.methodObject), new Among("\u00e9", 0, 2, "", DutchStemmer.methodObject), new Among("\u00eb", 0, 2, "", DutchStemmer.methodObject), new Among("\u00ed", 0, 3, "", DutchStemmer.methodObject), new Among("\u00ef", 0, 3, "", DutchStemmer.methodObject), new Among("\u00f3", 0, 4, "", DutchStemmer.methodObject), new Among("\u00f6", 0, 4, "", DutchStemmer.methodObject), new Among("\u00fa", 0, 5, "", DutchStemmer.methodObject), new Among("\u00fc", 0, 5, "", DutchStemmer.methodObject) };
        a_1 = new Among[] { new Among("", -1, 3, "", DutchStemmer.methodObject), new Among("I", 0, 2, "", DutchStemmer.methodObject), new Among("Y", 0, 1, "", DutchStemmer.methodObject) };
        a_2 = new Among[] { new Among("dd", -1, -1, "", DutchStemmer.methodObject), new Among("kk", -1, -1, "", DutchStemmer.methodObject), new Among("tt", -1, -1, "", DutchStemmer.methodObject) };
        a_3 = new Among[] { new Among("ene", -1, 2, "", DutchStemmer.methodObject), new Among("se", -1, 3, "", DutchStemmer.methodObject), new Among("en", -1, 2, "", DutchStemmer.methodObject), new Among("heden", 2, 1, "", DutchStemmer.methodObject), new Among("s", -1, 3, "", DutchStemmer.methodObject) };
        a_4 = new Among[] { new Among("end", -1, 1, "", DutchStemmer.methodObject), new Among("ig", -1, 2, "", DutchStemmer.methodObject), new Among("ing", -1, 1, "", DutchStemmer.methodObject), new Among("lijk", -1, 3, "", DutchStemmer.methodObject), new Among("baar", -1, 4, "", DutchStemmer.methodObject), new Among("bar", -1, 5, "", DutchStemmer.methodObject) };
        a_5 = new Among[] { new Among("aa", -1, -1, "", DutchStemmer.methodObject), new Among("ee", -1, -1, "", DutchStemmer.methodObject), new Among("oo", -1, -1, "", DutchStemmer.methodObject), new Among("uu", -1, -1, "", DutchStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080' };
        g_v_I = new char[] { '\u0001', '\0', '\0', '\u0011', 'A', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080' };
        g_v_j = new char[] { '\u0011', 'C', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080' };
    }
}
