package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class GermanStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final Among[] a_3;
    private static final Among[] a_4;
    private static final char[] g_v;
    private static final char[] g_s_ending;
    private static final char[] g_st_ending;
    private int I_x;
    private int I_p2;
    private int I_p1;
    
    private void copy_from(final GermanStemmer other) {
        this.I_x = other.I_x;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }
    
    private boolean r_prelude() {
        final int v_1 = this.cursor;
        int v_2;
        while (true) {
            v_2 = this.cursor;
            final int v_3 = this.cursor;
            this.bra = this.cursor;
            if (!this.eq_s(1, "\u00df")) {
                this.cursor = v_3;
                if (this.cursor >= this.limit) {
                    break;
                }
                ++this.cursor;
            }
            else {
                this.ket = this.cursor;
                this.slice_from("ss");
            }
        }
        this.cursor = v_2;
        this.cursor = v_1;
        int v_4 = 0;
    Block_8:
        while (true) {
            v_4 = this.cursor;
            int v_5;
            while (true) {
                v_5 = this.cursor;
                if (this.in_grouping(GermanStemmer.g_v, 97, 252)) {
                    this.bra = this.cursor;
                    final int v_6 = this.cursor;
                    if (this.eq_s(1, "u")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(GermanStemmer.g_v, 97, 252)) {
                            this.slice_from("U");
                            break;
                        }
                    }
                    this.cursor = v_6;
                    if (this.eq_s(1, "y")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(GermanStemmer.g_v, 97, 252)) {
                            this.slice_from("Y");
                            break;
                        }
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
        final int v_1 = this.cursor;
        final int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.I_x = this.cursor;
        this.cursor = v_1;
        while (!this.in_grouping(GermanStemmer.g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(GermanStemmer.g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        if (this.I_p1 < this.I_x) {
            this.I_p1 = this.I_x;
        }
        while (!this.in_grouping(GermanStemmer.g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(GermanStemmer.g_v, 97, 252)) {
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
    Label_0155:
        while (true) {
            v_1 = this.cursor;
            this.bra = this.cursor;
            final int among_var = this.find_among(GermanStemmer.a_0, 6);
            if (among_var == 0) {
                break;
            }
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break Label_0155;
                }
                case 1: {
                    this.slice_from("y");
                    continue;
                }
                case 2: {
                    this.slice_from("u");
                    continue;
                }
                case 3: {
                    this.slice_from("a");
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
                        break Label_0155;
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
    
    private boolean r_standard_suffix() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(GermanStemmer.a_1, 7);
        if (among_var != 0) {
            this.bra = this.cursor;
            if (this.r_R1()) {
                switch (among_var) {
                    case 1: {
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        if (!this.in_grouping_b(GermanStemmer.g_s_ending, 98, 116)) {
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                }
            }
        }
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        this.ket = this.cursor;
        among_var = this.find_among_b(GermanStemmer.a_2, 4);
        if (among_var != 0) {
            this.bra = this.cursor;
            if (this.r_R1()) {
                switch (among_var) {
                    case 1: {
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        if (!this.in_grouping_b(GermanStemmer.g_st_ending, 98, 116)) {
                            break;
                        }
                        final int c = this.cursor - 3;
                        if (this.limit_backward > c) {
                            break;
                        }
                        if (c > this.limit) {
                            break;
                        }
                        this.cursor = c;
                        this.slice_del();
                        break;
                    }
                }
            }
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        this.ket = this.cursor;
        among_var = this.find_among_b(GermanStemmer.a_4, 8);
        Label_0778: {
            if (among_var != 0) {
                this.bra = this.cursor;
                if (this.r_R2()) {
                    switch (among_var) {
                        case 1: {
                            this.slice_del();
                            final int v_4 = this.limit - this.cursor;
                            this.ket = this.cursor;
                            if (!this.eq_s_b(2, "ig")) {
                                this.cursor = this.limit - v_4;
                                break;
                            }
                            this.bra = this.cursor;
                            final int v_5 = this.limit - this.cursor;
                            if (this.eq_s_b(1, "e")) {
                                this.cursor = this.limit - v_4;
                                break;
                            }
                            this.cursor = this.limit - v_5;
                            if (!this.r_R2()) {
                                this.cursor = this.limit - v_4;
                                break;
                            }
                            this.slice_del();
                            break;
                        }
                        case 2: {
                            final int v_6 = this.limit - this.cursor;
                            if (!this.eq_s_b(1, "e")) {
                                this.cursor = this.limit - v_6;
                                this.slice_del();
                                break;
                            }
                            break;
                        }
                        case 3: {
                            this.slice_del();
                            final int v_7 = this.limit - this.cursor;
                            this.ket = this.cursor;
                            final int v_8 = this.limit - this.cursor;
                            if (!this.eq_s_b(2, "er")) {
                                this.cursor = this.limit - v_8;
                                if (!this.eq_s_b(2, "en")) {
                                    this.cursor = this.limit - v_7;
                                    break;
                                }
                            }
                            this.bra = this.cursor;
                            if (!this.r_R1()) {
                                this.cursor = this.limit - v_7;
                                break;
                            }
                            this.slice_del();
                            break;
                        }
                        case 4: {
                            this.slice_del();
                            final int v_9 = this.limit - this.cursor;
                            this.ket = this.cursor;
                            among_var = this.find_among_b(GermanStemmer.a_3, 2);
                            if (among_var == 0) {
                                this.cursor = this.limit - v_9;
                                break;
                            }
                            this.bra = this.cursor;
                            if (!this.r_R2()) {
                                this.cursor = this.limit - v_9;
                                break;
                            }
                            switch (among_var) {
                                case 0: {
                                    this.cursor = this.limit - v_9;
                                    break Label_0778;
                                }
                                case 1: {
                                    this.slice_del();
                                    break Label_0778;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        this.cursor = this.limit - v_3;
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
        return o instanceof GermanStemmer;
    }
    
    @Override
    public int hashCode() {
        return GermanStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("", -1, 6, "", GermanStemmer.methodObject), new Among("U", 0, 2, "", GermanStemmer.methodObject), new Among("Y", 0, 1, "", GermanStemmer.methodObject), new Among("\u00e4", 0, 3, "", GermanStemmer.methodObject), new Among("\u00f6", 0, 4, "", GermanStemmer.methodObject), new Among("\u00fc", 0, 5, "", GermanStemmer.methodObject) };
        a_1 = new Among[] { new Among("e", -1, 1, "", GermanStemmer.methodObject), new Among("em", -1, 1, "", GermanStemmer.methodObject), new Among("en", -1, 1, "", GermanStemmer.methodObject), new Among("ern", -1, 1, "", GermanStemmer.methodObject), new Among("er", -1, 1, "", GermanStemmer.methodObject), new Among("s", -1, 2, "", GermanStemmer.methodObject), new Among("es", 5, 1, "", GermanStemmer.methodObject) };
        a_2 = new Among[] { new Among("en", -1, 1, "", GermanStemmer.methodObject), new Among("er", -1, 1, "", GermanStemmer.methodObject), new Among("st", -1, 2, "", GermanStemmer.methodObject), new Among("est", 2, 1, "", GermanStemmer.methodObject) };
        a_3 = new Among[] { new Among("ig", -1, 1, "", GermanStemmer.methodObject), new Among("lich", -1, 1, "", GermanStemmer.methodObject) };
        a_4 = new Among[] { new Among("end", -1, 1, "", GermanStemmer.methodObject), new Among("ig", -1, 2, "", GermanStemmer.methodObject), new Among("ung", -1, 1, "", GermanStemmer.methodObject), new Among("lich", -1, 3, "", GermanStemmer.methodObject), new Among("isch", -1, 2, "", GermanStemmer.methodObject), new Among("ik", -1, 2, "", GermanStemmer.methodObject), new Among("heit", -1, 3, "", GermanStemmer.methodObject), new Among("keit", -1, 4, "", GermanStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\b', '\0', ' ', '\b' };
        g_s_ending = new char[] { 'u', '\u001e', '\u0005' };
        g_st_ending = new char[] { 'u', '\u001e', '\u0004' };
    }
}
