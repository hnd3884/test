package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class FrenchStemmer extends SnowballProgram
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
    private static final char[] g_v;
    private static final char[] g_keep_with_s;
    private int I_p2;
    private int I_p1;
    private int I_pV;
    
    private void copy_from(final FrenchStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }
    
    private boolean r_prelude() {
        int v_1 = 0;
    Block_10:
        while (true) {
            v_1 = this.cursor;
            int v_2;
            while (true) {
                v_2 = this.cursor;
                final int v_3 = this.cursor;
                if (this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                    this.bra = this.cursor;
                    final int v_4 = this.cursor;
                    if (this.eq_s(1, "u")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                            this.slice_from("U");
                            break;
                        }
                    }
                    this.cursor = v_4;
                    if (this.eq_s(1, "i")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                            this.slice_from("I");
                            break;
                        }
                    }
                    this.cursor = v_4;
                    if (this.eq_s(1, "y")) {
                        this.ket = this.cursor;
                        this.slice_from("Y");
                        break;
                    }
                }
                this.cursor = v_3;
                this.bra = this.cursor;
                if (this.eq_s(1, "y")) {
                    this.ket = this.cursor;
                    if (this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                        this.slice_from("Y");
                        break;
                    }
                }
                this.cursor = v_3;
                if (this.eq_s(1, "q")) {
                    this.bra = this.cursor;
                    if (this.eq_s(1, "u")) {
                        this.ket = this.cursor;
                        this.slice_from("U");
                        break;
                    }
                }
                this.cursor = v_2;
                if (this.cursor >= this.limit) {
                    break Block_10;
                }
                ++this.cursor;
            }
            this.cursor = v_2;
        }
        this.cursor = v_1;
        return true;
    }
    
    private boolean r_mark_regions() {
        this.I_pV = this.limit;
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        final int v_1 = this.cursor;
        final int v_2 = this.cursor;
        Label_0192: {
            Label_0184: {
                if (this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                    if (this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                        if (this.cursor < this.limit) {
                            ++this.cursor;
                            break Label_0184;
                        }
                    }
                }
                this.cursor = v_2;
                if (this.find_among(FrenchStemmer.a_0, 3) == 0) {
                    this.cursor = v_2;
                    if (this.cursor >= this.limit) {
                        break Label_0192;
                    }
                    ++this.cursor;
                    while (!this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                        if (this.cursor >= this.limit) {
                            break Label_0192;
                        }
                        ++this.cursor;
                    }
                }
            }
            this.I_pV = this.cursor;
        }
        this.cursor = v_1;
        final int v_3 = this.cursor;
    Label_0386:
        while (true) {
            while (!this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_3;
                    return true;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor >= this.limit) {
                    continue Label_0386;
                }
                ++this.cursor;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor >= this.limit) {
                    continue Label_0386;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor >= this.limit) {
                    continue Label_0386;
                }
                ++this.cursor;
            }
            this.I_p2 = this.cursor;
            continue Label_0386;
        }
    }
    
    private boolean r_postlude() {
        int v_1 = 0;
    Label_0129:
        while (true) {
            v_1 = this.cursor;
            this.bra = this.cursor;
            final int among_var = this.find_among(FrenchStemmer.a_1, 4);
            if (among_var == 0) {
                break;
            }
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break Label_0129;
                }
                case 1: {
                    this.slice_from("i");
                    continue;
                }
                case 2: {
                    this.slice_from("u");
                    continue;
                }
                case 3: {
                    this.slice_from("y");
                    continue;
                }
                case 4: {
                    if (this.cursor >= this.limit) {
                        break Label_0129;
                    }
                    ++this.cursor;
                    continue;
                }
            }
        }
        this.cursor = v_1;
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
    
    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(FrenchStemmer.a_4, 43);
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
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                final int v_1 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "ic")) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.bra = this.cursor;
                final int v_2 = this.limit - this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_2;
                    this.slice_from("iqU");
                    break;
                }
                this.slice_del();
                break;
            }
            case 3: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("log");
                break;
            }
            case 4: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("u");
                break;
            }
            case 5: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("ent");
                break;
            }
            case 6: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                final int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(FrenchStemmer.a_2, 6);
                if (among_var == 0) {
                    this.cursor = this.limit - v_3;
                    break;
                }
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_3;
                        break;
                    }
                    case 1: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_del();
                        this.ket = this.cursor;
                        if (!this.eq_s_b(2, "at")) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.bra = this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        final int v_4 = this.limit - this.cursor;
                        if (this.r_R2()) {
                            this.slice_del();
                            break;
                        }
                        this.cursor = this.limit - v_4;
                        if (!this.r_R1()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_from("eux");
                        break;
                    }
                    case 3: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 4: {
                        if (!this.r_RV()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_from("i");
                        break;
                    }
                }
                break;
            }
            case 7: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                final int v_5 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(FrenchStemmer.a_3, 3);
                if (among_var == 0) {
                    this.cursor = this.limit - v_5;
                    break;
                }
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_5;
                        break;
                    }
                    case 1: {
                        final int v_6 = this.limit - this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_6;
                            this.slice_from("abl");
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        final int v_7 = this.limit - this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_7;
                            this.slice_from("iqU");
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 3: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_5;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                }
                break;
            }
            case 8: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                final int v_8 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "at")) {
                    this.cursor = this.limit - v_8;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_8;
                    break;
                }
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "ic")) {
                    this.cursor = this.limit - v_8;
                    break;
                }
                this.bra = this.cursor;
                final int v_9 = this.limit - this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_9;
                    this.slice_from("iqU");
                    break;
                }
                this.slice_del();
                break;
            }
            case 9: {
                this.slice_from("eau");
                break;
            }
            case 10: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("al");
                break;
            }
            case 11: {
                final int v_10 = this.limit - this.cursor;
                if (this.r_R2()) {
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_10;
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("eux");
                break;
            }
            case 12: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.out_grouping_b(FrenchStemmer.g_v, 97, 251)) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 13: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_from("ant");
                return false;
            }
            case 14: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_from("ent");
                return false;
            }
            case 15: {
                final int v_11 = this.limit - this.cursor;
                if (!this.in_grouping_b(FrenchStemmer.g_v, 97, 251)) {
                    return false;
                }
                if (!this.r_RV()) {
                    return false;
                }
                this.cursor = this.limit - v_11;
                this.slice_del();
                return false;
            }
        }
        return true;
    }
    
    private boolean r_i_verb_suffix() {
        final int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        final int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        final int among_var = this.find_among_b(FrenchStemmer.a_5, 35);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                this.limit_backward = v_2;
                return false;
            }
            case 1: {
                if (!this.out_grouping_b(FrenchStemmer.g_v, 97, 251)) {
                    this.limit_backward = v_2;
                    return false;
                }
                this.slice_del();
                break;
            }
        }
        this.limit_backward = v_2;
        return true;
    }
    
    private boolean r_verb_suffix() {
        final int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        final int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        final int among_var = this.find_among_b(FrenchStemmer.a_6, 38);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                this.limit_backward = v_2;
                return false;
            }
            case 1: {
                if (!this.r_R2()) {
                    this.limit_backward = v_2;
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_del();
                final int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "e")) {
                    this.cursor = this.limit - v_3;
                    break;
                }
                this.bra = this.cursor;
                this.slice_del();
                break;
            }
        }
        this.limit_backward = v_2;
        return true;
    }
    
    private boolean r_residual_suffix() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_1;
        }
        else {
            this.bra = this.cursor;
            final int v_2 = this.limit - this.cursor;
            if (!this.out_grouping_b(FrenchStemmer.g_keep_with_s, 97, 232)) {
                this.cursor = this.limit - v_1;
            }
            else {
                this.cursor = this.limit - v_2;
                this.slice_del();
            }
        }
        final int v_3 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        final int v_4 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_3;
        this.ket = this.cursor;
        final int among_var = this.find_among_b(FrenchStemmer.a_7, 7);
        if (among_var == 0) {
            this.limit_backward = v_4;
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                this.limit_backward = v_4;
                return false;
            }
            case 1: {
                if (!this.r_R2()) {
                    this.limit_backward = v_4;
                    return false;
                }
                final int v_5 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "s")) {
                    this.cursor = this.limit - v_5;
                    if (!this.eq_s_b(1, "t")) {
                        this.limit_backward = v_4;
                        return false;
                    }
                }
                this.slice_del();
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
            case 4: {
                if (!this.eq_s_b(2, "gu")) {
                    this.limit_backward = v_4;
                    return false;
                }
                this.slice_del();
                break;
            }
        }
        this.limit_backward = v_4;
        return true;
    }
    
    private boolean r_un_double() {
        final int v_1 = this.limit - this.cursor;
        if (this.find_among_b(FrenchStemmer.a_8, 5) == 0) {
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
    
    private boolean r_un_accent() {
        int v_1 = 1;
        while (this.out_grouping_b(FrenchStemmer.g_v, 97, 251)) {
            --v_1;
        }
        if (v_1 > 0) {
            return false;
        }
        this.ket = this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "\u00e9")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "\u00e8")) {
                return false;
            }
        }
        this.bra = this.cursor;
        this.slice_from("e");
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
        final int v_4 = this.limit - this.cursor;
        final int v_5 = this.limit - this.cursor;
        final int v_6 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            this.cursor = this.limit - v_6;
            if (!this.r_i_verb_suffix()) {
                this.cursor = this.limit - v_6;
                if (!this.r_verb_suffix()) {
                    this.cursor = this.limit - v_4;
                    if (!this.r_residual_suffix()) {}
                }
            }
        }
        this.cursor = this.limit - v_5;
        final int v_7 = this.limit - this.cursor;
        this.ket = this.cursor;
        final int v_8 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "Y")) {
            this.cursor = this.limit - v_8;
            if (!this.eq_s_b(1, "\u00e7")) {
                this.cursor = this.limit - v_7;
            }
            else {
                this.bra = this.cursor;
                this.slice_from("c");
            }
        }
        else {
            this.bra = this.cursor;
            this.slice_from("i");
        }
        this.cursor = this.limit - v_3;
        final int v_9 = this.limit - this.cursor;
        if (!this.r_un_double()) {}
        this.cursor = this.limit - v_9;
        final int v_10 = this.limit - this.cursor;
        if (!this.r_un_accent()) {}
        this.cursor = this.limit - v_10;
        this.cursor = this.limit_backward;
        final int v_11 = this.cursor;
        if (!this.r_postlude()) {}
        this.cursor = v_11;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof FrenchStemmer;
    }
    
    @Override
    public int hashCode() {
        return FrenchStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("col", -1, -1, "", FrenchStemmer.methodObject), new Among("par", -1, -1, "", FrenchStemmer.methodObject), new Among("tap", -1, -1, "", FrenchStemmer.methodObject) };
        a_1 = new Among[] { new Among("", -1, 4, "", FrenchStemmer.methodObject), new Among("I", 0, 1, "", FrenchStemmer.methodObject), new Among("U", 0, 2, "", FrenchStemmer.methodObject), new Among("Y", 0, 3, "", FrenchStemmer.methodObject) };
        a_2 = new Among[] { new Among("iqU", -1, 3, "", FrenchStemmer.methodObject), new Among("abl", -1, 3, "", FrenchStemmer.methodObject), new Among("I\u00e8r", -1, 4, "", FrenchStemmer.methodObject), new Among("i\u00e8r", -1, 4, "", FrenchStemmer.methodObject), new Among("eus", -1, 2, "", FrenchStemmer.methodObject), new Among("iv", -1, 1, "", FrenchStemmer.methodObject) };
        a_3 = new Among[] { new Among("ic", -1, 2, "", FrenchStemmer.methodObject), new Among("abil", -1, 1, "", FrenchStemmer.methodObject), new Among("iv", -1, 3, "", FrenchStemmer.methodObject) };
        a_4 = new Among[] { new Among("iqUe", -1, 1, "", FrenchStemmer.methodObject), new Among("atrice", -1, 2, "", FrenchStemmer.methodObject), new Among("ance", -1, 1, "", FrenchStemmer.methodObject), new Among("ence", -1, 5, "", FrenchStemmer.methodObject), new Among("logie", -1, 3, "", FrenchStemmer.methodObject), new Among("able", -1, 1, "", FrenchStemmer.methodObject), new Among("isme", -1, 1, "", FrenchStemmer.methodObject), new Among("euse", -1, 11, "", FrenchStemmer.methodObject), new Among("iste", -1, 1, "", FrenchStemmer.methodObject), new Among("ive", -1, 8, "", FrenchStemmer.methodObject), new Among("if", -1, 8, "", FrenchStemmer.methodObject), new Among("usion", -1, 4, "", FrenchStemmer.methodObject), new Among("ation", -1, 2, "", FrenchStemmer.methodObject), new Among("ution", -1, 4, "", FrenchStemmer.methodObject), new Among("ateur", -1, 2, "", FrenchStemmer.methodObject), new Among("iqUes", -1, 1, "", FrenchStemmer.methodObject), new Among("atrices", -1, 2, "", FrenchStemmer.methodObject), new Among("ances", -1, 1, "", FrenchStemmer.methodObject), new Among("ences", -1, 5, "", FrenchStemmer.methodObject), new Among("logies", -1, 3, "", FrenchStemmer.methodObject), new Among("ables", -1, 1, "", FrenchStemmer.methodObject), new Among("ismes", -1, 1, "", FrenchStemmer.methodObject), new Among("euses", -1, 11, "", FrenchStemmer.methodObject), new Among("istes", -1, 1, "", FrenchStemmer.methodObject), new Among("ives", -1, 8, "", FrenchStemmer.methodObject), new Among("ifs", -1, 8, "", FrenchStemmer.methodObject), new Among("usions", -1, 4, "", FrenchStemmer.methodObject), new Among("ations", -1, 2, "", FrenchStemmer.methodObject), new Among("utions", -1, 4, "", FrenchStemmer.methodObject), new Among("ateurs", -1, 2, "", FrenchStemmer.methodObject), new Among("ments", -1, 15, "", FrenchStemmer.methodObject), new Among("ements", 30, 6, "", FrenchStemmer.methodObject), new Among("issements", 31, 12, "", FrenchStemmer.methodObject), new Among("it\u00e9s", -1, 7, "", FrenchStemmer.methodObject), new Among("ment", -1, 15, "", FrenchStemmer.methodObject), new Among("ement", 34, 6, "", FrenchStemmer.methodObject), new Among("issement", 35, 12, "", FrenchStemmer.methodObject), new Among("amment", 34, 13, "", FrenchStemmer.methodObject), new Among("emment", 34, 14, "", FrenchStemmer.methodObject), new Among("aux", -1, 10, "", FrenchStemmer.methodObject), new Among("eaux", 39, 9, "", FrenchStemmer.methodObject), new Among("eux", -1, 1, "", FrenchStemmer.methodObject), new Among("it\u00e9", -1, 7, "", FrenchStemmer.methodObject) };
        a_5 = new Among[] { new Among("ira", -1, 1, "", FrenchStemmer.methodObject), new Among("ie", -1, 1, "", FrenchStemmer.methodObject), new Among("isse", -1, 1, "", FrenchStemmer.methodObject), new Among("issante", -1, 1, "", FrenchStemmer.methodObject), new Among("i", -1, 1, "", FrenchStemmer.methodObject), new Among("irai", 4, 1, "", FrenchStemmer.methodObject), new Among("ir", -1, 1, "", FrenchStemmer.methodObject), new Among("iras", -1, 1, "", FrenchStemmer.methodObject), new Among("ies", -1, 1, "", FrenchStemmer.methodObject), new Among("\u00eemes", -1, 1, "", FrenchStemmer.methodObject), new Among("isses", -1, 1, "", FrenchStemmer.methodObject), new Among("issantes", -1, 1, "", FrenchStemmer.methodObject), new Among("\u00eetes", -1, 1, "", FrenchStemmer.methodObject), new Among("is", -1, 1, "", FrenchStemmer.methodObject), new Among("irais", 13, 1, "", FrenchStemmer.methodObject), new Among("issais", 13, 1, "", FrenchStemmer.methodObject), new Among("irions", -1, 1, "", FrenchStemmer.methodObject), new Among("issions", -1, 1, "", FrenchStemmer.methodObject), new Among("irons", -1, 1, "", FrenchStemmer.methodObject), new Among("issons", -1, 1, "", FrenchStemmer.methodObject), new Among("issants", -1, 1, "", FrenchStemmer.methodObject), new Among("it", -1, 1, "", FrenchStemmer.methodObject), new Among("irait", 21, 1, "", FrenchStemmer.methodObject), new Among("issait", 21, 1, "", FrenchStemmer.methodObject), new Among("issant", -1, 1, "", FrenchStemmer.methodObject), new Among("iraIent", -1, 1, "", FrenchStemmer.methodObject), new Among("issaIent", -1, 1, "", FrenchStemmer.methodObject), new Among("irent", -1, 1, "", FrenchStemmer.methodObject), new Among("issent", -1, 1, "", FrenchStemmer.methodObject), new Among("iront", -1, 1, "", FrenchStemmer.methodObject), new Among("\u00eet", -1, 1, "", FrenchStemmer.methodObject), new Among("iriez", -1, 1, "", FrenchStemmer.methodObject), new Among("issiez", -1, 1, "", FrenchStemmer.methodObject), new Among("irez", -1, 1, "", FrenchStemmer.methodObject), new Among("issez", -1, 1, "", FrenchStemmer.methodObject) };
        a_6 = new Among[] { new Among("a", -1, 3, "", FrenchStemmer.methodObject), new Among("era", 0, 2, "", FrenchStemmer.methodObject), new Among("asse", -1, 3, "", FrenchStemmer.methodObject), new Among("ante", -1, 3, "", FrenchStemmer.methodObject), new Among("\u00e9e", -1, 2, "", FrenchStemmer.methodObject), new Among("ai", -1, 3, "", FrenchStemmer.methodObject), new Among("erai", 5, 2, "", FrenchStemmer.methodObject), new Among("er", -1, 2, "", FrenchStemmer.methodObject), new Among("as", -1, 3, "", FrenchStemmer.methodObject), new Among("eras", 8, 2, "", FrenchStemmer.methodObject), new Among("\u00e2mes", -1, 3, "", FrenchStemmer.methodObject), new Among("asses", -1, 3, "", FrenchStemmer.methodObject), new Among("antes", -1, 3, "", FrenchStemmer.methodObject), new Among("\u00e2tes", -1, 3, "", FrenchStemmer.methodObject), new Among("\u00e9es", -1, 2, "", FrenchStemmer.methodObject), new Among("ais", -1, 3, "", FrenchStemmer.methodObject), new Among("erais", 15, 2, "", FrenchStemmer.methodObject), new Among("ions", -1, 1, "", FrenchStemmer.methodObject), new Among("erions", 17, 2, "", FrenchStemmer.methodObject), new Among("assions", 17, 3, "", FrenchStemmer.methodObject), new Among("erons", -1, 2, "", FrenchStemmer.methodObject), new Among("ants", -1, 3, "", FrenchStemmer.methodObject), new Among("\u00e9s", -1, 2, "", FrenchStemmer.methodObject), new Among("ait", -1, 3, "", FrenchStemmer.methodObject), new Among("erait", 23, 2, "", FrenchStemmer.methodObject), new Among("ant", -1, 3, "", FrenchStemmer.methodObject), new Among("aIent", -1, 3, "", FrenchStemmer.methodObject), new Among("eraIent", 26, 2, "", FrenchStemmer.methodObject), new Among("\u00e8rent", -1, 2, "", FrenchStemmer.methodObject), new Among("assent", -1, 3, "", FrenchStemmer.methodObject), new Among("eront", -1, 2, "", FrenchStemmer.methodObject), new Among("\u00e2t", -1, 3, "", FrenchStemmer.methodObject), new Among("ez", -1, 2, "", FrenchStemmer.methodObject), new Among("iez", 32, 2, "", FrenchStemmer.methodObject), new Among("eriez", 33, 2, "", FrenchStemmer.methodObject), new Among("assiez", 33, 3, "", FrenchStemmer.methodObject), new Among("erez", 32, 2, "", FrenchStemmer.methodObject), new Among("\u00e9", -1, 2, "", FrenchStemmer.methodObject) };
        a_7 = new Among[] { new Among("e", -1, 3, "", FrenchStemmer.methodObject), new Among("I\u00e8re", 0, 2, "", FrenchStemmer.methodObject), new Among("i\u00e8re", 0, 2, "", FrenchStemmer.methodObject), new Among("ion", -1, 1, "", FrenchStemmer.methodObject), new Among("Ier", -1, 2, "", FrenchStemmer.methodObject), new Among("ier", -1, 2, "", FrenchStemmer.methodObject), new Among("\u00eb", -1, 4, "", FrenchStemmer.methodObject) };
        a_8 = new Among[] { new Among("ell", -1, -1, "", FrenchStemmer.methodObject), new Among("eill", -1, -1, "", FrenchStemmer.methodObject), new Among("enn", -1, -1, "", FrenchStemmer.methodObject), new Among("onn", -1, -1, "", FrenchStemmer.methodObject), new Among("ett", -1, -1, "", FrenchStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080', '\u0082', 'g', '\b', '\u0005' };
        g_keep_with_s = new char[] { '\u0001', 'A', '\u0014', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080' };
    }
}
