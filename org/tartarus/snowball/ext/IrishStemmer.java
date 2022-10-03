package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class IrishStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final Among[] a_3;
    private static final char[] g_v;
    private int I_p2;
    private int I_p1;
    private int I_pV;
    
    private void copy_from(final IrishStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }
    
    private boolean r_mark_regions() {
        this.I_pV = this.limit;
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        final int v_1 = this.cursor;
        while (true) {
            while (!this.in_grouping(IrishStemmer.g_v, 97, 250)) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_1;
                    final int v_2 = this.cursor;
                Label_0273:
                    while (true) {
                        while (!this.in_grouping(IrishStemmer.g_v, 97, 250)) {
                            if (this.cursor >= this.limit) {
                                this.cursor = v_2;
                                return true;
                            }
                            ++this.cursor;
                        }
                        while (!this.out_grouping(IrishStemmer.g_v, 97, 250)) {
                            if (this.cursor >= this.limit) {
                                continue Label_0273;
                            }
                            ++this.cursor;
                        }
                        this.I_p1 = this.cursor;
                        while (!this.in_grouping(IrishStemmer.g_v, 97, 250)) {
                            if (this.cursor >= this.limit) {
                                continue Label_0273;
                            }
                            ++this.cursor;
                        }
                        while (!this.out_grouping(IrishStemmer.g_v, 97, 250)) {
                            if (this.cursor >= this.limit) {
                                continue Label_0273;
                            }
                            ++this.cursor;
                        }
                        this.I_p2 = this.cursor;
                        continue Label_0273;
                    }
                }
                ++this.cursor;
            }
            this.I_pV = this.cursor;
            continue;
        }
    }
    
    private boolean r_initial_morph() {
        this.bra = this.cursor;
        final int among_var = this.find_among(IrishStemmer.a_0, 24);
        if (among_var == 0) {
            return false;
        }
        this.ket = this.cursor;
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
                this.slice_from("f");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("s");
                break;
            }
            case 6: {
                this.slice_from("b");
                break;
            }
            case 7: {
                this.slice_from("c");
                break;
            }
            case 8: {
                this.slice_from("d");
                break;
            }
            case 9: {
                this.slice_from("f");
                break;
            }
            case 10: {
                this.slice_from("g");
                break;
            }
            case 11: {
                this.slice_from("p");
                break;
            }
            case 12: {
                this.slice_from("s");
                break;
            }
            case 13: {
                this.slice_from("t");
                break;
            }
            case 14: {
                this.slice_from("b");
                break;
            }
            case 15: {
                this.slice_from("c");
                break;
            }
            case 16: {
                this.slice_from("d");
                break;
            }
            case 17: {
                this.slice_from("f");
                break;
            }
            case 18: {
                this.slice_from("g");
                break;
            }
            case 19: {
                this.slice_from("m");
                break;
            }
            case 20: {
                this.slice_from("p");
                break;
            }
            case 21: {
                this.slice_from("t");
                break;
            }
        }
        return true;
    }
    
    private boolean r_RV() {
        return this.I_pV <= this.cursor;
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }
    
    private boolean r_noun_sfx() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(IrishStemmer.a_1, 16);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_deriv() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(IrishStemmer.a_2, 25);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("arc");
                break;
            }
            case 3: {
                this.slice_from("gin");
                break;
            }
            case 4: {
                this.slice_from("graf");
                break;
            }
            case 5: {
                this.slice_from("paite");
                break;
            }
            case 6: {
                this.slice_from("\u00f3id");
                break;
            }
        }
        return true;
    }
    
    private boolean r_verb_sfx() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(IrishStemmer.a_3, 12);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    @Override
    public boolean stem() {
        final int v_1 = this.cursor;
        if (!this.r_initial_morph()) {}
        this.cursor = v_1;
        final int v_2 = this.cursor;
        if (!this.r_mark_regions()) {}
        this.cursor = v_2;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_3 = this.limit - this.cursor;
        if (!this.r_noun_sfx()) {}
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        if (!this.r_deriv()) {}
        this.cursor = this.limit - v_4;
        final int v_5 = this.limit - this.cursor;
        if (!this.r_verb_sfx()) {}
        this.cursor = this.limit - v_5;
        this.cursor = this.limit_backward;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof IrishStemmer;
    }
    
    @Override
    public int hashCode() {
        return IrishStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("b'", -1, 4, "", IrishStemmer.methodObject), new Among("bh", -1, 14, "", IrishStemmer.methodObject), new Among("bhf", 1, 9, "", IrishStemmer.methodObject), new Among("bp", -1, 11, "", IrishStemmer.methodObject), new Among("ch", -1, 15, "", IrishStemmer.methodObject), new Among("d'", -1, 2, "", IrishStemmer.methodObject), new Among("d'fh", 5, 3, "", IrishStemmer.methodObject), new Among("dh", -1, 16, "", IrishStemmer.methodObject), new Among("dt", -1, 13, "", IrishStemmer.methodObject), new Among("fh", -1, 17, "", IrishStemmer.methodObject), new Among("gc", -1, 7, "", IrishStemmer.methodObject), new Among("gh", -1, 18, "", IrishStemmer.methodObject), new Among("h-", -1, 1, "", IrishStemmer.methodObject), new Among("m'", -1, 4, "", IrishStemmer.methodObject), new Among("mb", -1, 6, "", IrishStemmer.methodObject), new Among("mh", -1, 19, "", IrishStemmer.methodObject), new Among("n-", -1, 1, "", IrishStemmer.methodObject), new Among("nd", -1, 8, "", IrishStemmer.methodObject), new Among("ng", -1, 10, "", IrishStemmer.methodObject), new Among("ph", -1, 20, "", IrishStemmer.methodObject), new Among("sh", -1, 5, "", IrishStemmer.methodObject), new Among("t-", -1, 1, "", IrishStemmer.methodObject), new Among("th", -1, 21, "", IrishStemmer.methodObject), new Among("ts", -1, 12, "", IrishStemmer.methodObject) };
        a_1 = new Among[] { new Among("\u00edochta", -1, 1, "", IrishStemmer.methodObject), new Among("a\u00edochta", 0, 1, "", IrishStemmer.methodObject), new Among("ire", -1, 2, "", IrishStemmer.methodObject), new Among("aire", 2, 2, "", IrishStemmer.methodObject), new Among("abh", -1, 1, "", IrishStemmer.methodObject), new Among("eabh", 4, 1, "", IrishStemmer.methodObject), new Among("ibh", -1, 1, "", IrishStemmer.methodObject), new Among("aibh", 6, 1, "", IrishStemmer.methodObject), new Among("amh", -1, 1, "", IrishStemmer.methodObject), new Among("eamh", 8, 1, "", IrishStemmer.methodObject), new Among("imh", -1, 1, "", IrishStemmer.methodObject), new Among("aimh", 10, 1, "", IrishStemmer.methodObject), new Among("\u00edocht", -1, 1, "", IrishStemmer.methodObject), new Among("a\u00edocht", 12, 1, "", IrishStemmer.methodObject), new Among("ir\u00ed", -1, 2, "", IrishStemmer.methodObject), new Among("air\u00ed", 14, 2, "", IrishStemmer.methodObject) };
        a_2 = new Among[] { new Among("\u00f3ideacha", -1, 6, "", IrishStemmer.methodObject), new Among("patacha", -1, 5, "", IrishStemmer.methodObject), new Among("achta", -1, 1, "", IrishStemmer.methodObject), new Among("arcachta", 2, 2, "", IrishStemmer.methodObject), new Among("eachta", 2, 1, "", IrishStemmer.methodObject), new Among("grafa\u00edochta", -1, 4, "", IrishStemmer.methodObject), new Among("paite", -1, 5, "", IrishStemmer.methodObject), new Among("ach", -1, 1, "", IrishStemmer.methodObject), new Among("each", 7, 1, "", IrishStemmer.methodObject), new Among("\u00f3ideach", 8, 6, "", IrishStemmer.methodObject), new Among("gineach", 8, 3, "", IrishStemmer.methodObject), new Among("patach", 7, 5, "", IrishStemmer.methodObject), new Among("grafa\u00edoch", -1, 4, "", IrishStemmer.methodObject), new Among("pataigh", -1, 5, "", IrishStemmer.methodObject), new Among("\u00f3idigh", -1, 6, "", IrishStemmer.methodObject), new Among("acht\u00fail", -1, 1, "", IrishStemmer.methodObject), new Among("eacht\u00fail", 15, 1, "", IrishStemmer.methodObject), new Among("gineas", -1, 3, "", IrishStemmer.methodObject), new Among("ginis", -1, 3, "", IrishStemmer.methodObject), new Among("acht", -1, 1, "", IrishStemmer.methodObject), new Among("arcacht", 19, 2, "", IrishStemmer.methodObject), new Among("eacht", 19, 1, "", IrishStemmer.methodObject), new Among("grafa\u00edocht", -1, 4, "", IrishStemmer.methodObject), new Among("arcachta\u00ed", -1, 2, "", IrishStemmer.methodObject), new Among("grafa\u00edochta\u00ed", -1, 4, "", IrishStemmer.methodObject) };
        a_3 = new Among[] { new Among("imid", -1, 1, "", IrishStemmer.methodObject), new Among("aimid", 0, 1, "", IrishStemmer.methodObject), new Among("\u00edmid", -1, 1, "", IrishStemmer.methodObject), new Among("a\u00edmid", 2, 1, "", IrishStemmer.methodObject), new Among("adh", -1, 2, "", IrishStemmer.methodObject), new Among("eadh", 4, 2, "", IrishStemmer.methodObject), new Among("faidh", -1, 1, "", IrishStemmer.methodObject), new Among("fidh", -1, 1, "", IrishStemmer.methodObject), new Among("\u00e1il", -1, 2, "", IrishStemmer.methodObject), new Among("ain", -1, 2, "", IrishStemmer.methodObject), new Among("tear", -1, 2, "", IrishStemmer.methodObject), new Among("tar", -1, 2, "", IrishStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0001', '\u0011', '\u0004', '\u0002' };
    }
}
