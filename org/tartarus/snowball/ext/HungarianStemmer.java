package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class HungarianStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final Among[] a_3;
    private static final Among[] a_4;
    private static final Among[] a_5;
    private static final Among[] a_6;
    private static final Among[] a_7;
    private static final Among[] a_8;
    private static final Among[] a_9;
    private static final Among[] a_10;
    private static final Among[] a_11;
    private static final char[] g_v;
    private int I_p1;
    
    private void copy_from(final HungarianStemmer other) {
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }
    
    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        final int v_1 = this.cursor;
        if (this.in_grouping(HungarianStemmer.g_v, 97, 252)) {
            while (true) {
                final int v_2 = this.cursor;
                if (this.out_grouping(HungarianStemmer.g_v, 97, 252)) {
                    this.cursor = v_2;
                    final int v_3 = this.cursor;
                    if (this.find_among(HungarianStemmer.a_0, 8) == 0) {
                        this.cursor = v_3;
                        if (this.cursor >= this.limit) {
                            break;
                        }
                        ++this.cursor;
                    }
                    this.I_p1 = this.cursor;
                    return true;
                }
                this.cursor = v_2;
                if (this.cursor >= this.limit) {
                    break;
                }
                ++this.cursor;
            }
        }
        this.cursor = v_1;
        if (!this.out_grouping(HungarianStemmer.g_v, 97, 252)) {
            return false;
        }
        while (!this.in_grouping(HungarianStemmer.g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        return true;
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_v_ending() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_1, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("a");
                break;
            }
            case 2: {
                this.slice_from("e");
                break;
            }
        }
        return true;
    }
    
    private boolean r_double() {
        final int v_1 = this.limit - this.cursor;
        if (this.find_among_b(HungarianStemmer.a_2, 23) == 0) {
            return false;
        }
        this.cursor = this.limit - v_1;
        return true;
    }
    
    private boolean r_undouble() {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.ket = this.cursor;
        final int c = this.cursor - 1;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }
    
    private boolean r_instrum() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_3, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_double()) {
                    return false;
                }
                break;
            }
            case 2: {
                if (!this.r_double()) {
                    return false;
                }
                break;
            }
        }
        this.slice_del();
        return this.r_undouble();
    }
    
    private boolean r_case() {
        this.ket = this.cursor;
        if (this.find_among_b(HungarianStemmer.a_4, 44) == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        this.slice_del();
        return this.r_v_ending();
    }
    
    private boolean r_case_special() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_5, 3);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("e");
                break;
            }
            case 2: {
                this.slice_from("a");
                break;
            }
            case 3: {
                this.slice_from("a");
                break;
            }
        }
        return true;
    }
    
    private boolean r_case_other() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_6, 6);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("a");
                break;
            }
            case 4: {
                this.slice_from("e");
                break;
            }
        }
        return true;
    }
    
    private boolean r_factive() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_7, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_double()) {
                    return false;
                }
                break;
            }
            case 2: {
                if (!this.r_double()) {
                    return false;
                }
                break;
            }
        }
        this.slice_del();
        return this.r_undouble();
    }
    
    private boolean r_plural() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_8, 7);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("a");
                break;
            }
            case 2: {
                this.slice_from("e");
                break;
            }
            case 3: {
                this.slice_del();
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_del();
                break;
            }
            case 6: {
                this.slice_del();
                break;
            }
            case 7: {
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_owned() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_9, 12);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("e");
                break;
            }
            case 3: {
                this.slice_from("a");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("e");
                break;
            }
            case 6: {
                this.slice_from("a");
                break;
            }
            case 7: {
                this.slice_del();
                break;
            }
            case 8: {
                this.slice_from("e");
                break;
            }
            case 9: {
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_sing_owner() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_10, 31);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("a");
                break;
            }
            case 3: {
                this.slice_from("e");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("a");
                break;
            }
            case 6: {
                this.slice_from("e");
                break;
            }
            case 7: {
                this.slice_del();
                break;
            }
            case 8: {
                this.slice_del();
                break;
            }
            case 9: {
                this.slice_del();
                break;
            }
            case 10: {
                this.slice_from("a");
                break;
            }
            case 11: {
                this.slice_from("e");
                break;
            }
            case 12: {
                this.slice_del();
                break;
            }
            case 13: {
                this.slice_del();
                break;
            }
            case 14: {
                this.slice_from("a");
                break;
            }
            case 15: {
                this.slice_from("e");
                break;
            }
            case 16: {
                this.slice_del();
                break;
            }
            case 17: {
                this.slice_del();
                break;
            }
            case 18: {
                this.slice_del();
                break;
            }
            case 19: {
                this.slice_from("a");
                break;
            }
            case 20: {
                this.slice_from("e");
                break;
            }
        }
        return true;
    }
    
    private boolean r_plur_owner() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(HungarianStemmer.a_11, 42);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("a");
                break;
            }
            case 3: {
                this.slice_from("e");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_del();
                break;
            }
            case 6: {
                this.slice_del();
                break;
            }
            case 7: {
                this.slice_from("a");
                break;
            }
            case 8: {
                this.slice_from("e");
                break;
            }
            case 9: {
                this.slice_del();
                break;
            }
            case 10: {
                this.slice_del();
                break;
            }
            case 11: {
                this.slice_del();
                break;
            }
            case 12: {
                this.slice_from("a");
                break;
            }
            case 13: {
                this.slice_from("e");
                break;
            }
            case 14: {
                this.slice_del();
                break;
            }
            case 15: {
                this.slice_del();
                break;
            }
            case 16: {
                this.slice_del();
                break;
            }
            case 17: {
                this.slice_del();
                break;
            }
            case 18: {
                this.slice_from("a");
                break;
            }
            case 19: {
                this.slice_from("e");
                break;
            }
            case 20: {
                this.slice_del();
                break;
            }
            case 21: {
                this.slice_del();
                break;
            }
            case 22: {
                this.slice_from("a");
                break;
            }
            case 23: {
                this.slice_from("e");
                break;
            }
            case 24: {
                this.slice_del();
                break;
            }
            case 25: {
                this.slice_del();
                break;
            }
            case 26: {
                this.slice_del();
                break;
            }
            case 27: {
                this.slice_from("a");
                break;
            }
            case 28: {
                this.slice_from("e");
                break;
            }
            case 29: {
                this.slice_del();
                break;
            }
        }
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
        if (!this.r_instrum()) {}
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.r_case()) {}
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        if (!this.r_case_special()) {}
        this.cursor = this.limit - v_4;
        final int v_5 = this.limit - this.cursor;
        if (!this.r_case_other()) {}
        this.cursor = this.limit - v_5;
        final int v_6 = this.limit - this.cursor;
        if (!this.r_factive()) {}
        this.cursor = this.limit - v_6;
        final int v_7 = this.limit - this.cursor;
        if (!this.r_owned()) {}
        this.cursor = this.limit - v_7;
        final int v_8 = this.limit - this.cursor;
        if (!this.r_sing_owner()) {}
        this.cursor = this.limit - v_8;
        final int v_9 = this.limit - this.cursor;
        if (!this.r_plur_owner()) {}
        this.cursor = this.limit - v_9;
        final int v_10 = this.limit - this.cursor;
        if (!this.r_plural()) {}
        this.cursor = this.limit - v_10;
        this.cursor = this.limit_backward;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof HungarianStemmer;
    }
    
    @Override
    public int hashCode() {
        return HungarianStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("cs", -1, -1, "", HungarianStemmer.methodObject), new Among("dzs", -1, -1, "", HungarianStemmer.methodObject), new Among("gy", -1, -1, "", HungarianStemmer.methodObject), new Among("ly", -1, -1, "", HungarianStemmer.methodObject), new Among("ny", -1, -1, "", HungarianStemmer.methodObject), new Among("sz", -1, -1, "", HungarianStemmer.methodObject), new Among("ty", -1, -1, "", HungarianStemmer.methodObject), new Among("zs", -1, -1, "", HungarianStemmer.methodObject) };
        a_1 = new Among[] { new Among("\u00e1", -1, 1, "", HungarianStemmer.methodObject), new Among("\u00e9", -1, 2, "", HungarianStemmer.methodObject) };
        a_2 = new Among[] { new Among("bb", -1, -1, "", HungarianStemmer.methodObject), new Among("cc", -1, -1, "", HungarianStemmer.methodObject), new Among("dd", -1, -1, "", HungarianStemmer.methodObject), new Among("ff", -1, -1, "", HungarianStemmer.methodObject), new Among("gg", -1, -1, "", HungarianStemmer.methodObject), new Among("jj", -1, -1, "", HungarianStemmer.methodObject), new Among("kk", -1, -1, "", HungarianStemmer.methodObject), new Among("ll", -1, -1, "", HungarianStemmer.methodObject), new Among("mm", -1, -1, "", HungarianStemmer.methodObject), new Among("nn", -1, -1, "", HungarianStemmer.methodObject), new Among("pp", -1, -1, "", HungarianStemmer.methodObject), new Among("rr", -1, -1, "", HungarianStemmer.methodObject), new Among("ccs", -1, -1, "", HungarianStemmer.methodObject), new Among("ss", -1, -1, "", HungarianStemmer.methodObject), new Among("zzs", -1, -1, "", HungarianStemmer.methodObject), new Among("tt", -1, -1, "", HungarianStemmer.methodObject), new Among("vv", -1, -1, "", HungarianStemmer.methodObject), new Among("ggy", -1, -1, "", HungarianStemmer.methodObject), new Among("lly", -1, -1, "", HungarianStemmer.methodObject), new Among("nny", -1, -1, "", HungarianStemmer.methodObject), new Among("tty", -1, -1, "", HungarianStemmer.methodObject), new Among("ssz", -1, -1, "", HungarianStemmer.methodObject), new Among("zz", -1, -1, "", HungarianStemmer.methodObject) };
        a_3 = new Among[] { new Among("al", -1, 1, "", HungarianStemmer.methodObject), new Among("el", -1, 2, "", HungarianStemmer.methodObject) };
        a_4 = new Among[] { new Among("ba", -1, -1, "", HungarianStemmer.methodObject), new Among("ra", -1, -1, "", HungarianStemmer.methodObject), new Among("be", -1, -1, "", HungarianStemmer.methodObject), new Among("re", -1, -1, "", HungarianStemmer.methodObject), new Among("ig", -1, -1, "", HungarianStemmer.methodObject), new Among("nak", -1, -1, "", HungarianStemmer.methodObject), new Among("nek", -1, -1, "", HungarianStemmer.methodObject), new Among("val", -1, -1, "", HungarianStemmer.methodObject), new Among("vel", -1, -1, "", HungarianStemmer.methodObject), new Among("ul", -1, -1, "", HungarianStemmer.methodObject), new Among("n\u00e1l", -1, -1, "", HungarianStemmer.methodObject), new Among("n\u00e9l", -1, -1, "", HungarianStemmer.methodObject), new Among("b\u00f3l", -1, -1, "", HungarianStemmer.methodObject), new Among("r\u00f3l", -1, -1, "", HungarianStemmer.methodObject), new Among("t\u00f3l", -1, -1, "", HungarianStemmer.methodObject), new Among("b\u00f5l", -1, -1, "", HungarianStemmer.methodObject), new Among("r\u00f5l", -1, -1, "", HungarianStemmer.methodObject), new Among("t\u00f5l", -1, -1, "", HungarianStemmer.methodObject), new Among("\u00fcl", -1, -1, "", HungarianStemmer.methodObject), new Among("n", -1, -1, "", HungarianStemmer.methodObject), new Among("an", 19, -1, "", HungarianStemmer.methodObject), new Among("ban", 20, -1, "", HungarianStemmer.methodObject), new Among("en", 19, -1, "", HungarianStemmer.methodObject), new Among("ben", 22, -1, "", HungarianStemmer.methodObject), new Among("k\u00e9ppen", 22, -1, "", HungarianStemmer.methodObject), new Among("on", 19, -1, "", HungarianStemmer.methodObject), new Among("\u00f6n", 19, -1, "", HungarianStemmer.methodObject), new Among("k\u00e9pp", -1, -1, "", HungarianStemmer.methodObject), new Among("kor", -1, -1, "", HungarianStemmer.methodObject), new Among("t", -1, -1, "", HungarianStemmer.methodObject), new Among("at", 29, -1, "", HungarianStemmer.methodObject), new Among("et", 29, -1, "", HungarianStemmer.methodObject), new Among("k\u00e9nt", 29, -1, "", HungarianStemmer.methodObject), new Among("ank\u00e9nt", 32, -1, "", HungarianStemmer.methodObject), new Among("enk\u00e9nt", 32, -1, "", HungarianStemmer.methodObject), new Among("onk\u00e9nt", 32, -1, "", HungarianStemmer.methodObject), new Among("ot", 29, -1, "", HungarianStemmer.methodObject), new Among("\u00e9rt", 29, -1, "", HungarianStemmer.methodObject), new Among("\u00f6t", 29, -1, "", HungarianStemmer.methodObject), new Among("hez", -1, -1, "", HungarianStemmer.methodObject), new Among("hoz", -1, -1, "", HungarianStemmer.methodObject), new Among("h\u00f6z", -1, -1, "", HungarianStemmer.methodObject), new Among("v\u00e1", -1, -1, "", HungarianStemmer.methodObject), new Among("v\u00e9", -1, -1, "", HungarianStemmer.methodObject) };
        a_5 = new Among[] { new Among("\u00e1n", -1, 2, "", HungarianStemmer.methodObject), new Among("\u00e9n", -1, 1, "", HungarianStemmer.methodObject), new Among("\u00e1nk\u00e9nt", -1, 3, "", HungarianStemmer.methodObject) };
        a_6 = new Among[] { new Among("stul", -1, 2, "", HungarianStemmer.methodObject), new Among("astul", 0, 1, "", HungarianStemmer.methodObject), new Among("\u00e1stul", 0, 3, "", HungarianStemmer.methodObject), new Among("st\u00fcl", -1, 2, "", HungarianStemmer.methodObject), new Among("est\u00fcl", 3, 1, "", HungarianStemmer.methodObject), new Among("\u00e9st\u00fcl", 3, 4, "", HungarianStemmer.methodObject) };
        a_7 = new Among[] { new Among("\u00e1", -1, 1, "", HungarianStemmer.methodObject), new Among("\u00e9", -1, 2, "", HungarianStemmer.methodObject) };
        a_8 = new Among[] { new Among("k", -1, 7, "", HungarianStemmer.methodObject), new Among("ak", 0, 4, "", HungarianStemmer.methodObject), new Among("ek", 0, 6, "", HungarianStemmer.methodObject), new Among("ok", 0, 5, "", HungarianStemmer.methodObject), new Among("\u00e1k", 0, 1, "", HungarianStemmer.methodObject), new Among("\u00e9k", 0, 2, "", HungarianStemmer.methodObject), new Among("\u00f6k", 0, 3, "", HungarianStemmer.methodObject) };
        a_9 = new Among[] { new Among("\u00e9i", -1, 7, "", HungarianStemmer.methodObject), new Among("\u00e1\u00e9i", 0, 6, "", HungarianStemmer.methodObject), new Among("\u00e9\u00e9i", 0, 5, "", HungarianStemmer.methodObject), new Among("\u00e9", -1, 9, "", HungarianStemmer.methodObject), new Among("k\u00e9", 3, 4, "", HungarianStemmer.methodObject), new Among("ak\u00e9", 4, 1, "", HungarianStemmer.methodObject), new Among("ek\u00e9", 4, 1, "", HungarianStemmer.methodObject), new Among("ok\u00e9", 4, 1, "", HungarianStemmer.methodObject), new Among("\u00e1k\u00e9", 4, 3, "", HungarianStemmer.methodObject), new Among("\u00e9k\u00e9", 4, 2, "", HungarianStemmer.methodObject), new Among("\u00f6k\u00e9", 4, 1, "", HungarianStemmer.methodObject), new Among("\u00e9\u00e9", 3, 8, "", HungarianStemmer.methodObject) };
        a_10 = new Among[] { new Among("a", -1, 18, "", HungarianStemmer.methodObject), new Among("ja", 0, 17, "", HungarianStemmer.methodObject), new Among("d", -1, 16, "", HungarianStemmer.methodObject), new Among("ad", 2, 13, "", HungarianStemmer.methodObject), new Among("ed", 2, 13, "", HungarianStemmer.methodObject), new Among("od", 2, 13, "", HungarianStemmer.methodObject), new Among("\u00e1d", 2, 14, "", HungarianStemmer.methodObject), new Among("\u00e9d", 2, 15, "", HungarianStemmer.methodObject), new Among("\u00f6d", 2, 13, "", HungarianStemmer.methodObject), new Among("e", -1, 18, "", HungarianStemmer.methodObject), new Among("je", 9, 17, "", HungarianStemmer.methodObject), new Among("nk", -1, 4, "", HungarianStemmer.methodObject), new Among("unk", 11, 1, "", HungarianStemmer.methodObject), new Among("\u00e1nk", 11, 2, "", HungarianStemmer.methodObject), new Among("\u00e9nk", 11, 3, "", HungarianStemmer.methodObject), new Among("\u00fcnk", 11, 1, "", HungarianStemmer.methodObject), new Among("uk", -1, 8, "", HungarianStemmer.methodObject), new Among("juk", 16, 7, "", HungarianStemmer.methodObject), new Among("\u00e1juk", 17, 5, "", HungarianStemmer.methodObject), new Among("\u00fck", -1, 8, "", HungarianStemmer.methodObject), new Among("j\u00fck", 19, 7, "", HungarianStemmer.methodObject), new Among("\u00e9j\u00fck", 20, 6, "", HungarianStemmer.methodObject), new Among("m", -1, 12, "", HungarianStemmer.methodObject), new Among("am", 22, 9, "", HungarianStemmer.methodObject), new Among("em", 22, 9, "", HungarianStemmer.methodObject), new Among("om", 22, 9, "", HungarianStemmer.methodObject), new Among("\u00e1m", 22, 10, "", HungarianStemmer.methodObject), new Among("\u00e9m", 22, 11, "", HungarianStemmer.methodObject), new Among("o", -1, 18, "", HungarianStemmer.methodObject), new Among("\u00e1", -1, 19, "", HungarianStemmer.methodObject), new Among("\u00e9", -1, 20, "", HungarianStemmer.methodObject) };
        a_11 = new Among[] { new Among("id", -1, 10, "", HungarianStemmer.methodObject), new Among("aid", 0, 9, "", HungarianStemmer.methodObject), new Among("jaid", 1, 6, "", HungarianStemmer.methodObject), new Among("eid", 0, 9, "", HungarianStemmer.methodObject), new Among("jeid", 3, 6, "", HungarianStemmer.methodObject), new Among("\u00e1id", 0, 7, "", HungarianStemmer.methodObject), new Among("\u00e9id", 0, 8, "", HungarianStemmer.methodObject), new Among("i", -1, 15, "", HungarianStemmer.methodObject), new Among("ai", 7, 14, "", HungarianStemmer.methodObject), new Among("jai", 8, 11, "", HungarianStemmer.methodObject), new Among("ei", 7, 14, "", HungarianStemmer.methodObject), new Among("jei", 10, 11, "", HungarianStemmer.methodObject), new Among("\u00e1i", 7, 12, "", HungarianStemmer.methodObject), new Among("\u00e9i", 7, 13, "", HungarianStemmer.methodObject), new Among("itek", -1, 24, "", HungarianStemmer.methodObject), new Among("eitek", 14, 21, "", HungarianStemmer.methodObject), new Among("jeitek", 15, 20, "", HungarianStemmer.methodObject), new Among("\u00e9itek", 14, 23, "", HungarianStemmer.methodObject), new Among("ik", -1, 29, "", HungarianStemmer.methodObject), new Among("aik", 18, 26, "", HungarianStemmer.methodObject), new Among("jaik", 19, 25, "", HungarianStemmer.methodObject), new Among("eik", 18, 26, "", HungarianStemmer.methodObject), new Among("jeik", 21, 25, "", HungarianStemmer.methodObject), new Among("\u00e1ik", 18, 27, "", HungarianStemmer.methodObject), new Among("\u00e9ik", 18, 28, "", HungarianStemmer.methodObject), new Among("ink", -1, 20, "", HungarianStemmer.methodObject), new Among("aink", 25, 17, "", HungarianStemmer.methodObject), new Among("jaink", 26, 16, "", HungarianStemmer.methodObject), new Among("eink", 25, 17, "", HungarianStemmer.methodObject), new Among("jeink", 28, 16, "", HungarianStemmer.methodObject), new Among("\u00e1ink", 25, 18, "", HungarianStemmer.methodObject), new Among("\u00e9ink", 25, 19, "", HungarianStemmer.methodObject), new Among("aitok", -1, 21, "", HungarianStemmer.methodObject), new Among("jaitok", 32, 20, "", HungarianStemmer.methodObject), new Among("\u00e1itok", -1, 22, "", HungarianStemmer.methodObject), new Among("im", -1, 5, "", HungarianStemmer.methodObject), new Among("aim", 35, 4, "", HungarianStemmer.methodObject), new Among("jaim", 36, 1, "", HungarianStemmer.methodObject), new Among("eim", 35, 4, "", HungarianStemmer.methodObject), new Among("jeim", 38, 1, "", HungarianStemmer.methodObject), new Among("\u00e1im", 35, 2, "", HungarianStemmer.methodObject), new Among("\u00e9im", 35, 3, "", HungarianStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0001', '\u0011', '4', '\u000e' };
    }
}
