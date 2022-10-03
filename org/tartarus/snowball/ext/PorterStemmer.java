package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class PorterStemmer extends SnowballProgram
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
    private static final char[] g_v_WXY;
    private boolean B_Y_found;
    private int I_p2;
    private int I_p1;
    
    private void copy_from(final PorterStemmer other) {
        this.B_Y_found = other.B_Y_found;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }
    
    private boolean r_shortv() {
        return this.out_grouping_b(PorterStemmer.g_v_WXY, 89, 121) && this.in_grouping_b(PorterStemmer.g_v, 97, 121) && this.out_grouping_b(PorterStemmer.g_v, 97, 121);
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }
    
    private boolean r_Step_1a() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(PorterStemmer.a_0, 4);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("ss");
                break;
            }
            case 2: {
                this.slice_from("i");
                break;
            }
            case 3: {
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_1b() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(PorterStemmer.a_2, 3);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        Label_0359: {
            switch (among_var) {
                case 0: {
                    return false;
                }
                case 1: {
                    if (!this.r_R1()) {
                        return false;
                    }
                    this.slice_from("ee");
                    break;
                }
                case 2: {
                    final int v_1 = this.limit - this.cursor;
                    while (!this.in_grouping_b(PorterStemmer.g_v, 97, 121)) {
                        if (this.cursor <= this.limit_backward) {
                            return false;
                        }
                        --this.cursor;
                    }
                    this.cursor = this.limit - v_1;
                    this.slice_del();
                    final int v_2 = this.limit - this.cursor;
                    among_var = this.find_among_b(PorterStemmer.a_1, 13);
                    if (among_var == 0) {
                        return false;
                    }
                    this.cursor = this.limit - v_2;
                    switch (among_var) {
                        case 0: {
                            return false;
                        }
                        case 1: {
                            final int c = this.cursor;
                            this.insert(this.cursor, this.cursor, "e");
                            this.cursor = c;
                            break Label_0359;
                        }
                        case 2: {
                            this.ket = this.cursor;
                            if (this.cursor <= this.limit_backward) {
                                return false;
                            }
                            --this.cursor;
                            this.bra = this.cursor;
                            this.slice_del();
                            break Label_0359;
                        }
                        case 3: {
                            if (this.cursor != this.I_p1) {
                                return false;
                            }
                            final int v_3 = this.limit - this.cursor;
                            if (!this.r_shortv()) {
                                return false;
                            }
                            this.cursor = this.limit - v_3;
                            final int c = this.cursor;
                            this.insert(this.cursor, this.cursor, "e");
                            this.cursor = c;
                            break Label_0359;
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }
    
    private boolean r_Step_1c() {
        this.ket = this.cursor;
        final int v_1 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "y")) {
            this.cursor = this.limit - v_1;
            if (!this.eq_s_b(1, "Y")) {
                return false;
            }
        }
        this.bra = this.cursor;
        while (!this.in_grouping_b(PorterStemmer.g_v, 97, 121)) {
            if (this.cursor <= this.limit_backward) {
                return false;
            }
            --this.cursor;
        }
        this.slice_from("i");
        return true;
    }
    
    private boolean r_Step_2() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(PorterStemmer.a_3, 20);
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
                this.slice_from("tion");
                break;
            }
            case 2: {
                this.slice_from("ence");
                break;
            }
            case 3: {
                this.slice_from("ance");
                break;
            }
            case 4: {
                this.slice_from("able");
                break;
            }
            case 5: {
                this.slice_from("ent");
                break;
            }
            case 6: {
                this.slice_from("e");
                break;
            }
            case 7: {
                this.slice_from("ize");
                break;
            }
            case 8: {
                this.slice_from("ate");
                break;
            }
            case 9: {
                this.slice_from("al");
                break;
            }
            case 10: {
                this.slice_from("al");
                break;
            }
            case 11: {
                this.slice_from("ful");
                break;
            }
            case 12: {
                this.slice_from("ous");
                break;
            }
            case 13: {
                this.slice_from("ive");
                break;
            }
            case 14: {
                this.slice_from("ble");
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_3() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(PorterStemmer.a_4, 7);
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
                this.slice_from("al");
                break;
            }
            case 2: {
                this.slice_from("ic");
                break;
            }
            case 3: {
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_4() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(PorterStemmer.a_5, 19);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R2()) {
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
                final int v_1 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "s")) {
                    this.cursor = this.limit - v_1;
                    if (!this.eq_s_b(1, "t")) {
                        return false;
                    }
                }
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_5a() {
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "e")) {
            return false;
        }
        this.bra = this.cursor;
        final int v_1 = this.limit - this.cursor;
        if (!this.r_R2()) {
            this.cursor = this.limit - v_1;
            if (!this.r_R1()) {
                return false;
            }
            final int v_2 = this.limit - this.cursor;
            if (this.r_shortv()) {
                return false;
            }
            this.cursor = this.limit - v_2;
        }
        this.slice_del();
        return true;
    }
    
    private boolean r_Step_5b() {
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "l")) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R2()) {
            return false;
        }
        if (!this.eq_s_b(1, "l")) {
            return false;
        }
        this.slice_del();
        return true;
    }
    
    @Override
    public boolean stem() {
        this.B_Y_found = false;
        final int v_1 = this.cursor;
        this.bra = this.cursor;
        if (this.eq_s(1, "y")) {
            this.ket = this.cursor;
            this.slice_from("Y");
            this.B_Y_found = true;
        }
        this.cursor = v_1;
        final int v_2 = this.cursor;
        int v_3 = 0;
    Block_4:
        while (true) {
            v_3 = this.cursor;
            while (true) {
                final int v_4 = this.cursor;
                if (this.in_grouping(PorterStemmer.g_v, 97, 121)) {
                    this.bra = this.cursor;
                    if (this.eq_s(1, "y")) {
                        this.ket = this.cursor;
                        this.cursor = v_4;
                        this.slice_from("Y");
                        this.B_Y_found = true;
                        break;
                    }
                }
                this.cursor = v_4;
                if (this.cursor >= this.limit) {
                    break Block_4;
                }
                ++this.cursor;
            }
        }
        this.cursor = v_3;
        this.cursor = v_2;
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        final int v_5 = this.cursor;
    Label_0388:
        while (true) {
            while (!this.in_grouping(PorterStemmer.g_v, 97, 121)) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_5;
                    this.limit_backward = this.cursor;
                    this.cursor = this.limit;
                    final int v_6 = this.limit - this.cursor;
                    if (!this.r_Step_1a()) {}
                    this.cursor = this.limit - v_6;
                    final int v_7 = this.limit - this.cursor;
                    if (!this.r_Step_1b()) {}
                    this.cursor = this.limit - v_7;
                    final int v_8 = this.limit - this.cursor;
                    if (!this.r_Step_1c()) {}
                    this.cursor = this.limit - v_8;
                    final int v_9 = this.limit - this.cursor;
                    if (!this.r_Step_2()) {}
                    this.cursor = this.limit - v_9;
                    final int v_10 = this.limit - this.cursor;
                    if (!this.r_Step_3()) {}
                    this.cursor = this.limit - v_10;
                    final int v_11 = this.limit - this.cursor;
                    if (!this.r_Step_4()) {}
                    this.cursor = this.limit - v_11;
                    final int v_12 = this.limit - this.cursor;
                    if (!this.r_Step_5a()) {}
                    this.cursor = this.limit - v_12;
                    final int v_13 = this.limit - this.cursor;
                    if (!this.r_Step_5b()) {}
                    this.cursor = this.limit - v_13;
                    this.cursor = this.limit_backward;
                    final int v_14 = this.cursor;
                    if (this.B_Y_found) {
                        int v_15 = 0;
                    Block_15:
                        while (true) {
                            v_15 = this.cursor;
                            while (true) {
                                final int v_16 = this.cursor;
                                this.bra = this.cursor;
                                if (this.eq_s(1, "Y")) {
                                    this.ket = this.cursor;
                                    this.cursor = v_16;
                                    this.slice_from("y");
                                    break;
                                }
                                this.cursor = v_16;
                                if (this.cursor >= this.limit) {
                                    break Block_15;
                                }
                                ++this.cursor;
                            }
                        }
                        this.cursor = v_15;
                    }
                    this.cursor = v_14;
                    return true;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(PorterStemmer.g_v, 97, 121)) {
                if (this.cursor >= this.limit) {
                    continue Label_0388;
                }
                ++this.cursor;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(PorterStemmer.g_v, 97, 121)) {
                if (this.cursor >= this.limit) {
                    continue Label_0388;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(PorterStemmer.g_v, 97, 121)) {
                if (this.cursor >= this.limit) {
                    continue Label_0388;
                }
                ++this.cursor;
            }
            this.I_p2 = this.cursor;
            continue Label_0388;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof PorterStemmer;
    }
    
    @Override
    public int hashCode() {
        return PorterStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("s", -1, 3, "", PorterStemmer.methodObject), new Among("ies", 0, 2, "", PorterStemmer.methodObject), new Among("sses", 0, 1, "", PorterStemmer.methodObject), new Among("ss", 0, -1, "", PorterStemmer.methodObject) };
        a_1 = new Among[] { new Among("", -1, 3, "", PorterStemmer.methodObject), new Among("bb", 0, 2, "", PorterStemmer.methodObject), new Among("dd", 0, 2, "", PorterStemmer.methodObject), new Among("ff", 0, 2, "", PorterStemmer.methodObject), new Among("gg", 0, 2, "", PorterStemmer.methodObject), new Among("bl", 0, 1, "", PorterStemmer.methodObject), new Among("mm", 0, 2, "", PorterStemmer.methodObject), new Among("nn", 0, 2, "", PorterStemmer.methodObject), new Among("pp", 0, 2, "", PorterStemmer.methodObject), new Among("rr", 0, 2, "", PorterStemmer.methodObject), new Among("at", 0, 1, "", PorterStemmer.methodObject), new Among("tt", 0, 2, "", PorterStemmer.methodObject), new Among("iz", 0, 1, "", PorterStemmer.methodObject) };
        a_2 = new Among[] { new Among("ed", -1, 2, "", PorterStemmer.methodObject), new Among("eed", 0, 1, "", PorterStemmer.methodObject), new Among("ing", -1, 2, "", PorterStemmer.methodObject) };
        a_3 = new Among[] { new Among("anci", -1, 3, "", PorterStemmer.methodObject), new Among("enci", -1, 2, "", PorterStemmer.methodObject), new Among("abli", -1, 4, "", PorterStemmer.methodObject), new Among("eli", -1, 6, "", PorterStemmer.methodObject), new Among("alli", -1, 9, "", PorterStemmer.methodObject), new Among("ousli", -1, 12, "", PorterStemmer.methodObject), new Among("entli", -1, 5, "", PorterStemmer.methodObject), new Among("aliti", -1, 10, "", PorterStemmer.methodObject), new Among("biliti", -1, 14, "", PorterStemmer.methodObject), new Among("iviti", -1, 13, "", PorterStemmer.methodObject), new Among("tional", -1, 1, "", PorterStemmer.methodObject), new Among("ational", 10, 8, "", PorterStemmer.methodObject), new Among("alism", -1, 10, "", PorterStemmer.methodObject), new Among("ation", -1, 8, "", PorterStemmer.methodObject), new Among("ization", 13, 7, "", PorterStemmer.methodObject), new Among("izer", -1, 7, "", PorterStemmer.methodObject), new Among("ator", -1, 8, "", PorterStemmer.methodObject), new Among("iveness", -1, 13, "", PorterStemmer.methodObject), new Among("fulness", -1, 11, "", PorterStemmer.methodObject), new Among("ousness", -1, 12, "", PorterStemmer.methodObject) };
        a_4 = new Among[] { new Among("icate", -1, 2, "", PorterStemmer.methodObject), new Among("ative", -1, 3, "", PorterStemmer.methodObject), new Among("alize", -1, 1, "", PorterStemmer.methodObject), new Among("iciti", -1, 2, "", PorterStemmer.methodObject), new Among("ical", -1, 2, "", PorterStemmer.methodObject), new Among("ful", -1, 3, "", PorterStemmer.methodObject), new Among("ness", -1, 3, "", PorterStemmer.methodObject) };
        a_5 = new Among[] { new Among("ic", -1, 1, "", PorterStemmer.methodObject), new Among("ance", -1, 1, "", PorterStemmer.methodObject), new Among("ence", -1, 1, "", PorterStemmer.methodObject), new Among("able", -1, 1, "", PorterStemmer.methodObject), new Among("ible", -1, 1, "", PorterStemmer.methodObject), new Among("ate", -1, 1, "", PorterStemmer.methodObject), new Among("ive", -1, 1, "", PorterStemmer.methodObject), new Among("ize", -1, 1, "", PorterStemmer.methodObject), new Among("iti", -1, 1, "", PorterStemmer.methodObject), new Among("al", -1, 1, "", PorterStemmer.methodObject), new Among("ism", -1, 1, "", PorterStemmer.methodObject), new Among("ion", -1, 2, "", PorterStemmer.methodObject), new Among("er", -1, 1, "", PorterStemmer.methodObject), new Among("ous", -1, 1, "", PorterStemmer.methodObject), new Among("ant", -1, 1, "", PorterStemmer.methodObject), new Among("ent", -1, 1, "", PorterStemmer.methodObject), new Among("ment", 15, 1, "", PorterStemmer.methodObject), new Among("ement", 16, 1, "", PorterStemmer.methodObject), new Among("ou", -1, 1, "", PorterStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001' };
        g_v_WXY = new char[] { '\u0001', '\u0011', 'A', '\u00d0', '\u0001' };
    }
}
