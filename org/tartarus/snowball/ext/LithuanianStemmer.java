package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class LithuanianStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final Among[] a_3;
    private static final Among[] a_4;
    private static final char[] g_v;
    private boolean B_CHANGE;
    private int I_s;
    private int I_p2;
    private int I_p1;
    
    private void copy_from(final LithuanianStemmer other) {
        this.B_CHANGE = other.B_CHANGE;
        this.I_s = other.I_s;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_step1() {
        final int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        final int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        if (this.find_among_b(LithuanianStemmer.a_0, 206) == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        if (!this.r_R1()) {
            return false;
        }
        this.slice_del();
        return true;
    }
    
    private boolean r_step2() {
        int v_1;
        while (true) {
            v_1 = this.limit - this.cursor;
            final int v_2 = this.limit - this.cursor;
            if (this.cursor < this.I_p1) {
                break;
            }
            this.cursor = this.I_p1;
            final int v_3 = this.limit_backward;
            this.limit_backward = this.cursor;
            this.cursor = this.limit - v_2;
            this.ket = this.cursor;
            if (this.find_among_b(LithuanianStemmer.a_1, 62) == 0) {
                this.limit_backward = v_3;
                break;
            }
            this.bra = this.cursor;
            this.limit_backward = v_3;
            this.slice_del();
        }
        this.cursor = this.limit - v_1;
        return true;
    }
    
    private boolean r_fix_conflicts() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(LithuanianStemmer.a_2, 11);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("ait\u0117");
                this.B_CHANGE = true;
                break;
            }
            case 2: {
                this.slice_from("ait\u0117");
                this.B_CHANGE = true;
                break;
            }
            case 3: {
                this.slice_from("uot\u0117");
                this.B_CHANGE = true;
                break;
            }
            case 4: {
                this.slice_from("uot\u0117");
                this.B_CHANGE = true;
                break;
            }
            case 5: {
                this.slice_from("\u0117jimas");
                this.B_CHANGE = true;
                break;
            }
            case 6: {
                this.slice_from("esys");
                this.B_CHANGE = true;
                break;
            }
            case 7: {
                this.slice_from("asys");
                this.B_CHANGE = true;
                break;
            }
            case 8: {
                this.slice_from("avimas");
                this.B_CHANGE = true;
                break;
            }
            case 9: {
                this.slice_from("ojimas");
                this.B_CHANGE = true;
                break;
            }
            case 10: {
                this.slice_from("okat\u0117");
                this.B_CHANGE = true;
                break;
            }
            case 11: {
                this.slice_from("okat\u0117");
                this.B_CHANGE = true;
                break;
            }
        }
        return true;
    }
    
    private boolean r_fix_chdz() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(LithuanianStemmer.a_3, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("t");
                this.B_CHANGE = true;
                break;
            }
            case 2: {
                this.slice_from("d");
                this.B_CHANGE = true;
                break;
            }
        }
        return true;
    }
    
    private boolean r_fix_gd() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(LithuanianStemmer.a_4, 1);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("g");
                this.B_CHANGE = true;
                break;
            }
        }
        return true;
    }
    
    @Override
    public boolean stem() {
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        this.I_s = this.getCurrent().length();
        final int v_1 = this.cursor;
        final int v_2 = this.cursor;
        final int v_3 = this.cursor;
        if (!this.eq_s(1, "a")) {
            this.cursor = v_2;
        }
        else {
            this.cursor = v_3;
            if (this.I_s <= 6) {
                this.cursor = v_2;
            }
            else {
                final int c = this.cursor + 1;
                if (0 > c || c > this.limit) {
                    this.cursor = v_2;
                }
                else {
                    this.cursor = c;
                }
            }
        }
    Label_0303:
        while (true) {
            while (!this.in_grouping(LithuanianStemmer.g_v, 97, 371)) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_1;
                    this.limit_backward = this.cursor;
                    this.cursor = this.limit;
                    final int v_4 = this.limit - this.cursor;
                    if (!this.r_fix_conflicts()) {}
                    this.cursor = this.limit - v_4;
                    final int v_5 = this.limit - this.cursor;
                    if (!this.r_step1()) {}
                    this.cursor = this.limit - v_5;
                    final int v_6 = this.limit - this.cursor;
                    if (!this.r_fix_chdz()) {}
                    this.cursor = this.limit - v_6;
                    final int v_7 = this.limit - this.cursor;
                    if (!this.r_step2()) {}
                    this.cursor = this.limit - v_7;
                    final int v_8 = this.limit - this.cursor;
                    if (!this.r_fix_chdz()) {}
                    this.cursor = this.limit - v_8;
                    final int v_9 = this.limit - this.cursor;
                    if (!this.r_fix_gd()) {}
                    this.cursor = this.limit - v_9;
                    this.cursor = this.limit_backward;
                    return true;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(LithuanianStemmer.g_v, 97, 371)) {
                if (this.cursor >= this.limit) {
                    continue Label_0303;
                }
                ++this.cursor;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(LithuanianStemmer.g_v, 97, 371)) {
                if (this.cursor >= this.limit) {
                    continue Label_0303;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(LithuanianStemmer.g_v, 97, 371)) {
                if (this.cursor >= this.limit) {
                    continue Label_0303;
                }
                ++this.cursor;
            }
            this.I_p2 = this.cursor;
            continue Label_0303;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof LithuanianStemmer;
    }
    
    @Override
    public int hashCode() {
        return LithuanianStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("a", -1, -1, "", LithuanianStemmer.methodObject), new Among("ia", 0, -1, "", LithuanianStemmer.methodObject), new Among("eria", 1, -1, "", LithuanianStemmer.methodObject), new Among("osna", 0, -1, "", LithuanianStemmer.methodObject), new Among("iosna", 3, -1, "", LithuanianStemmer.methodObject), new Among("uosna", 3, -1, "", LithuanianStemmer.methodObject), new Among("iuosna", 5, -1, "", LithuanianStemmer.methodObject), new Among("ysna", 0, -1, "", LithuanianStemmer.methodObject), new Among("\u0117sna", 0, -1, "", LithuanianStemmer.methodObject), new Among("e", -1, -1, "", LithuanianStemmer.methodObject), new Among("ie", 9, -1, "", LithuanianStemmer.methodObject), new Among("enie", 10, -1, "", LithuanianStemmer.methodObject), new Among("erie", 10, -1, "", LithuanianStemmer.methodObject), new Among("oje", 9, -1, "", LithuanianStemmer.methodObject), new Among("ioje", 13, -1, "", LithuanianStemmer.methodObject), new Among("uje", 9, -1, "", LithuanianStemmer.methodObject), new Among("iuje", 15, -1, "", LithuanianStemmer.methodObject), new Among("yje", 9, -1, "", LithuanianStemmer.methodObject), new Among("enyje", 17, -1, "", LithuanianStemmer.methodObject), new Among("eryje", 17, -1, "", LithuanianStemmer.methodObject), new Among("\u0117je", 9, -1, "", LithuanianStemmer.methodObject), new Among("ame", 9, -1, "", LithuanianStemmer.methodObject), new Among("iame", 21, -1, "", LithuanianStemmer.methodObject), new Among("sime", 9, -1, "", LithuanianStemmer.methodObject), new Among("ome", 9, -1, "", LithuanianStemmer.methodObject), new Among("\u0117me", 9, -1, "", LithuanianStemmer.methodObject), new Among("tum\u0117me", 25, -1, "", LithuanianStemmer.methodObject), new Among("ose", 9, -1, "", LithuanianStemmer.methodObject), new Among("iose", 27, -1, "", LithuanianStemmer.methodObject), new Among("uose", 27, -1, "", LithuanianStemmer.methodObject), new Among("iuose", 29, -1, "", LithuanianStemmer.methodObject), new Among("yse", 9, -1, "", LithuanianStemmer.methodObject), new Among("enyse", 31, -1, "", LithuanianStemmer.methodObject), new Among("eryse", 31, -1, "", LithuanianStemmer.methodObject), new Among("\u0117se", 9, -1, "", LithuanianStemmer.methodObject), new Among("ate", 9, -1, "", LithuanianStemmer.methodObject), new Among("iate", 35, -1, "", LithuanianStemmer.methodObject), new Among("ite", 9, -1, "", LithuanianStemmer.methodObject), new Among("kite", 37, -1, "", LithuanianStemmer.methodObject), new Among("site", 37, -1, "", LithuanianStemmer.methodObject), new Among("ote", 9, -1, "", LithuanianStemmer.methodObject), new Among("tute", 9, -1, "", LithuanianStemmer.methodObject), new Among("\u0117te", 9, -1, "", LithuanianStemmer.methodObject), new Among("tum\u0117te", 42, -1, "", LithuanianStemmer.methodObject), new Among("i", -1, -1, "", LithuanianStemmer.methodObject), new Among("ai", 44, -1, "", LithuanianStemmer.methodObject), new Among("iai", 45, -1, "", LithuanianStemmer.methodObject), new Among("eriai", 46, -1, "", LithuanianStemmer.methodObject), new Among("ei", 44, -1, "", LithuanianStemmer.methodObject), new Among("tumei", 48, -1, "", LithuanianStemmer.methodObject), new Among("ki", 44, -1, "", LithuanianStemmer.methodObject), new Among("imi", 44, -1, "", LithuanianStemmer.methodObject), new Among("erimi", 51, -1, "", LithuanianStemmer.methodObject), new Among("umi", 44, -1, "", LithuanianStemmer.methodObject), new Among("iumi", 53, -1, "", LithuanianStemmer.methodObject), new Among("si", 44, -1, "", LithuanianStemmer.methodObject), new Among("asi", 55, -1, "", LithuanianStemmer.methodObject), new Among("iasi", 56, -1, "", LithuanianStemmer.methodObject), new Among("esi", 55, -1, "", LithuanianStemmer.methodObject), new Among("iesi", 58, -1, "", LithuanianStemmer.methodObject), new Among("siesi", 59, -1, "", LithuanianStemmer.methodObject), new Among("isi", 55, -1, "", LithuanianStemmer.methodObject), new Among("aisi", 61, -1, "", LithuanianStemmer.methodObject), new Among("eisi", 61, -1, "", LithuanianStemmer.methodObject), new Among("tumeisi", 63, -1, "", LithuanianStemmer.methodObject), new Among("uisi", 61, -1, "", LithuanianStemmer.methodObject), new Among("osi", 55, -1, "", LithuanianStemmer.methodObject), new Among("\u0117josi", 66, -1, "", LithuanianStemmer.methodObject), new Among("uosi", 66, -1, "", LithuanianStemmer.methodObject), new Among("iuosi", 68, -1, "", LithuanianStemmer.methodObject), new Among("siuosi", 69, -1, "", LithuanianStemmer.methodObject), new Among("usi", 55, -1, "", LithuanianStemmer.methodObject), new Among("ausi", 71, -1, "", LithuanianStemmer.methodObject), new Among("\u010diausi", 72, -1, "", LithuanianStemmer.methodObject), new Among("\u0105si", 55, -1, "", LithuanianStemmer.methodObject), new Among("\u0117si", 55, -1, "", LithuanianStemmer.methodObject), new Among("\u0173si", 55, -1, "", LithuanianStemmer.methodObject), new Among("t\u0173si", 76, -1, "", LithuanianStemmer.methodObject), new Among("ti", 44, -1, "", LithuanianStemmer.methodObject), new Among("enti", 78, -1, "", LithuanianStemmer.methodObject), new Among("inti", 78, -1, "", LithuanianStemmer.methodObject), new Among("oti", 78, -1, "", LithuanianStemmer.methodObject), new Among("ioti", 81, -1, "", LithuanianStemmer.methodObject), new Among("uoti", 81, -1, "", LithuanianStemmer.methodObject), new Among("iuoti", 83, -1, "", LithuanianStemmer.methodObject), new Among("auti", 78, -1, "", LithuanianStemmer.methodObject), new Among("iauti", 85, -1, "", LithuanianStemmer.methodObject), new Among("yti", 78, -1, "", LithuanianStemmer.methodObject), new Among("\u0117ti", 78, -1, "", LithuanianStemmer.methodObject), new Among("tel\u0117ti", 88, -1, "", LithuanianStemmer.methodObject), new Among("in\u0117ti", 88, -1, "", LithuanianStemmer.methodObject), new Among("ter\u0117ti", 88, -1, "", LithuanianStemmer.methodObject), new Among("ui", 44, -1, "", LithuanianStemmer.methodObject), new Among("iui", 92, -1, "", LithuanianStemmer.methodObject), new Among("eniui", 93, -1, "", LithuanianStemmer.methodObject), new Among("oj", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0117j", -1, -1, "", LithuanianStemmer.methodObject), new Among("k", -1, -1, "", LithuanianStemmer.methodObject), new Among("am", -1, -1, "", LithuanianStemmer.methodObject), new Among("iam", 98, -1, "", LithuanianStemmer.methodObject), new Among("iem", -1, -1, "", LithuanianStemmer.methodObject), new Among("im", -1, -1, "", LithuanianStemmer.methodObject), new Among("sim", 101, -1, "", LithuanianStemmer.methodObject), new Among("om", -1, -1, "", LithuanianStemmer.methodObject), new Among("tum", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0117m", -1, -1, "", LithuanianStemmer.methodObject), new Among("tum\u0117m", 105, -1, "", LithuanianStemmer.methodObject), new Among("an", -1, -1, "", LithuanianStemmer.methodObject), new Among("on", -1, -1, "", LithuanianStemmer.methodObject), new Among("ion", 108, -1, "", LithuanianStemmer.methodObject), new Among("un", -1, -1, "", LithuanianStemmer.methodObject), new Among("iun", 110, -1, "", LithuanianStemmer.methodObject), new Among("\u0117n", -1, -1, "", LithuanianStemmer.methodObject), new Among("o", -1, -1, "", LithuanianStemmer.methodObject), new Among("io", 113, -1, "", LithuanianStemmer.methodObject), new Among("enio", 114, -1, "", LithuanianStemmer.methodObject), new Among("\u0117jo", 113, -1, "", LithuanianStemmer.methodObject), new Among("uo", 113, -1, "", LithuanianStemmer.methodObject), new Among("s", -1, -1, "", LithuanianStemmer.methodObject), new Among("as", 118, -1, "", LithuanianStemmer.methodObject), new Among("ias", 119, -1, "", LithuanianStemmer.methodObject), new Among("es", 118, -1, "", LithuanianStemmer.methodObject), new Among("ies", 121, -1, "", LithuanianStemmer.methodObject), new Among("is", 118, -1, "", LithuanianStemmer.methodObject), new Among("ais", 123, -1, "", LithuanianStemmer.methodObject), new Among("iais", 124, -1, "", LithuanianStemmer.methodObject), new Among("tumeis", 123, -1, "", LithuanianStemmer.methodObject), new Among("imis", 123, -1, "", LithuanianStemmer.methodObject), new Among("enimis", 127, -1, "", LithuanianStemmer.methodObject), new Among("omis", 123, -1, "", LithuanianStemmer.methodObject), new Among("iomis", 129, -1, "", LithuanianStemmer.methodObject), new Among("umis", 123, -1, "", LithuanianStemmer.methodObject), new Among("\u0117mis", 123, -1, "", LithuanianStemmer.methodObject), new Among("enis", 123, -1, "", LithuanianStemmer.methodObject), new Among("asis", 123, -1, "", LithuanianStemmer.methodObject), new Among("ysis", 123, -1, "", LithuanianStemmer.methodObject), new Among("ams", 118, -1, "", LithuanianStemmer.methodObject), new Among("iams", 136, -1, "", LithuanianStemmer.methodObject), new Among("iems", 118, -1, "", LithuanianStemmer.methodObject), new Among("ims", 118, -1, "", LithuanianStemmer.methodObject), new Among("enims", 139, -1, "", LithuanianStemmer.methodObject), new Among("erims", 139, -1, "", LithuanianStemmer.methodObject), new Among("oms", 118, -1, "", LithuanianStemmer.methodObject), new Among("ioms", 142, -1, "", LithuanianStemmer.methodObject), new Among("ums", 118, -1, "", LithuanianStemmer.methodObject), new Among("\u0117ms", 118, -1, "", LithuanianStemmer.methodObject), new Among("ens", 118, -1, "", LithuanianStemmer.methodObject), new Among("os", 118, -1, "", LithuanianStemmer.methodObject), new Among("ios", 147, -1, "", LithuanianStemmer.methodObject), new Among("uos", 147, -1, "", LithuanianStemmer.methodObject), new Among("iuos", 149, -1, "", LithuanianStemmer.methodObject), new Among("ers", 118, -1, "", LithuanianStemmer.methodObject), new Among("us", 118, -1, "", LithuanianStemmer.methodObject), new Among("aus", 152, -1, "", LithuanianStemmer.methodObject), new Among("iaus", 153, -1, "", LithuanianStemmer.methodObject), new Among("ius", 152, -1, "", LithuanianStemmer.methodObject), new Among("ys", 118, -1, "", LithuanianStemmer.methodObject), new Among("enys", 156, -1, "", LithuanianStemmer.methodObject), new Among("erys", 156, -1, "", LithuanianStemmer.methodObject), new Among("om\u00c4\u0097s", 118, -1, "", LithuanianStemmer.methodObject), new Among("ot\u00c4\u0097s", 118, -1, "", LithuanianStemmer.methodObject), new Among("\u0105s", 118, -1, "", LithuanianStemmer.methodObject), new Among("i\u0105s", 161, -1, "", LithuanianStemmer.methodObject), new Among("\u0117s", 118, -1, "", LithuanianStemmer.methodObject), new Among("am\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("iam\u0117s", 164, -1, "", LithuanianStemmer.methodObject), new Among("im\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("kim\u0117s", 166, -1, "", LithuanianStemmer.methodObject), new Among("sim\u0117s", 166, -1, "", LithuanianStemmer.methodObject), new Among("om\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("\u0117m\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("tum\u0117m\u0117s", 170, -1, "", LithuanianStemmer.methodObject), new Among("at\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("iat\u0117s", 172, -1, "", LithuanianStemmer.methodObject), new Among("sit\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("ot\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("\u0117t\u0117s", 163, -1, "", LithuanianStemmer.methodObject), new Among("tum\u0117t\u0117s", 176, -1, "", LithuanianStemmer.methodObject), new Among("\u012fs", 118, -1, "", LithuanianStemmer.methodObject), new Among("\u016bs", 118, -1, "", LithuanianStemmer.methodObject), new Among("t\u0173s", 118, -1, "", LithuanianStemmer.methodObject), new Among("at", -1, -1, "", LithuanianStemmer.methodObject), new Among("iat", 181, -1, "", LithuanianStemmer.methodObject), new Among("it", -1, -1, "", LithuanianStemmer.methodObject), new Among("sit", 183, -1, "", LithuanianStemmer.methodObject), new Among("ot", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0117t", -1, -1, "", LithuanianStemmer.methodObject), new Among("tum\u0117t", 186, -1, "", LithuanianStemmer.methodObject), new Among("u", -1, -1, "", LithuanianStemmer.methodObject), new Among("au", 188, -1, "", LithuanianStemmer.methodObject), new Among("iau", 189, -1, "", LithuanianStemmer.methodObject), new Among("\u010diau", 190, -1, "", LithuanianStemmer.methodObject), new Among("iu", 188, -1, "", LithuanianStemmer.methodObject), new Among("eniu", 192, -1, "", LithuanianStemmer.methodObject), new Among("siu", 192, -1, "", LithuanianStemmer.methodObject), new Among("y", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0105", -1, -1, "", LithuanianStemmer.methodObject), new Among("i\u0105", 196, -1, "", LithuanianStemmer.methodObject), new Among("\u0117", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0119", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u012f", -1, -1, "", LithuanianStemmer.methodObject), new Among("en\u012f", 200, -1, "", LithuanianStemmer.methodObject), new Among("er\u012f", 200, -1, "", LithuanianStemmer.methodObject), new Among("\u0173", -1, -1, "", LithuanianStemmer.methodObject), new Among("i\u0173", 203, -1, "", LithuanianStemmer.methodObject), new Among("er\u0173", 203, -1, "", LithuanianStemmer.methodObject) };
        a_1 = new Among[] { new Among("ing", -1, -1, "", LithuanianStemmer.methodObject), new Among("aj", -1, -1, "", LithuanianStemmer.methodObject), new Among("iaj", 1, -1, "", LithuanianStemmer.methodObject), new Among("iej", -1, -1, "", LithuanianStemmer.methodObject), new Among("oj", -1, -1, "", LithuanianStemmer.methodObject), new Among("ioj", 4, -1, "", LithuanianStemmer.methodObject), new Among("uoj", 4, -1, "", LithuanianStemmer.methodObject), new Among("iuoj", 6, -1, "", LithuanianStemmer.methodObject), new Among("auj", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0105j", -1, -1, "", LithuanianStemmer.methodObject), new Among("i\u0105j", 9, -1, "", LithuanianStemmer.methodObject), new Among("\u0117j", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0173j", -1, -1, "", LithuanianStemmer.methodObject), new Among("i\u0173j", 12, -1, "", LithuanianStemmer.methodObject), new Among("ok", -1, -1, "", LithuanianStemmer.methodObject), new Among("iok", 14, -1, "", LithuanianStemmer.methodObject), new Among("iuk", -1, -1, "", LithuanianStemmer.methodObject), new Among("uliuk", 16, -1, "", LithuanianStemmer.methodObject), new Among("u\u010diuk", 16, -1, "", LithuanianStemmer.methodObject), new Among("i\u0161k", -1, -1, "", LithuanianStemmer.methodObject), new Among("iul", -1, -1, "", LithuanianStemmer.methodObject), new Among("yl", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0117l", -1, -1, "", LithuanianStemmer.methodObject), new Among("am", -1, -1, "", LithuanianStemmer.methodObject), new Among("dam", 23, -1, "", LithuanianStemmer.methodObject), new Among("jam", 23, -1, "", LithuanianStemmer.methodObject), new Among("zgan", -1, -1, "", LithuanianStemmer.methodObject), new Among("ain", -1, -1, "", LithuanianStemmer.methodObject), new Among("esn", -1, -1, "", LithuanianStemmer.methodObject), new Among("op", -1, -1, "", LithuanianStemmer.methodObject), new Among("iop", 29, -1, "", LithuanianStemmer.methodObject), new Among("ias", -1, -1, "", LithuanianStemmer.methodObject), new Among("ies", -1, -1, "", LithuanianStemmer.methodObject), new Among("ais", -1, -1, "", LithuanianStemmer.methodObject), new Among("iais", 33, -1, "", LithuanianStemmer.methodObject), new Among("os", -1, -1, "", LithuanianStemmer.methodObject), new Among("ios", 35, -1, "", LithuanianStemmer.methodObject), new Among("uos", 35, -1, "", LithuanianStemmer.methodObject), new Among("iuos", 37, -1, "", LithuanianStemmer.methodObject), new Among("aus", -1, -1, "", LithuanianStemmer.methodObject), new Among("iaus", 39, -1, "", LithuanianStemmer.methodObject), new Among("\u0105s", -1, -1, "", LithuanianStemmer.methodObject), new Among("i\u0105s", 41, -1, "", LithuanianStemmer.methodObject), new Among("\u0119s", -1, -1, "", LithuanianStemmer.methodObject), new Among("ut\u0117ait", -1, -1, "", LithuanianStemmer.methodObject), new Among("ant", -1, -1, "", LithuanianStemmer.methodObject), new Among("iant", 45, -1, "", LithuanianStemmer.methodObject), new Among("siant", 46, -1, "", LithuanianStemmer.methodObject), new Among("int", -1, -1, "", LithuanianStemmer.methodObject), new Among("ot", -1, -1, "", LithuanianStemmer.methodObject), new Among("uot", 49, -1, "", LithuanianStemmer.methodObject), new Among("iuot", 50, -1, "", LithuanianStemmer.methodObject), new Among("yt", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0117t", -1, -1, "", LithuanianStemmer.methodObject), new Among("yk\u0161t", -1, -1, "", LithuanianStemmer.methodObject), new Among("iau", -1, -1, "", LithuanianStemmer.methodObject), new Among("dav", -1, -1, "", LithuanianStemmer.methodObject), new Among("sv", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0161v", -1, -1, "", LithuanianStemmer.methodObject), new Among("yk\u0161\u010d", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0119", -1, -1, "", LithuanianStemmer.methodObject), new Among("\u0117j\u0119", 60, -1, "", LithuanianStemmer.methodObject) };
        a_2 = new Among[] { new Among("ojime", -1, 9, "", LithuanianStemmer.methodObject), new Among("\u0117jime", -1, 5, "", LithuanianStemmer.methodObject), new Among("avime", -1, 8, "", LithuanianStemmer.methodObject), new Among("okate", -1, 11, "", LithuanianStemmer.methodObject), new Among("aite", -1, 1, "", LithuanianStemmer.methodObject), new Among("uote", -1, 4, "", LithuanianStemmer.methodObject), new Among("asius", -1, 7, "", LithuanianStemmer.methodObject), new Among("okat\u0117s", -1, 10, "", LithuanianStemmer.methodObject), new Among("ait\u0117s", -1, 2, "", LithuanianStemmer.methodObject), new Among("uot\u0117s", -1, 3, "", LithuanianStemmer.methodObject), new Among("esiu", -1, 6, "", LithuanianStemmer.methodObject) };
        a_3 = new Among[] { new Among("\u010d", -1, 1, "", LithuanianStemmer.methodObject), new Among("d\u017e", -1, 2, "", LithuanianStemmer.methodObject) };
        a_4 = new Among[] { new Among("gd", -1, 1, "", LithuanianStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0010', '\0', '@', '\u0001', '\0', '@', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0004', '\u0004' };
    }
}
