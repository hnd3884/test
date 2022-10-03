package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class ItalianStemmer extends SnowballProgram
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
    private static final char[] g_v;
    private static final char[] g_AEIO;
    private static final char[] g_CG;
    private int I_p2;
    private int I_p1;
    private int I_pV;
    
    private void copy_from(final ItalianStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }
    
    private boolean r_prelude() {
        final int v_1 = this.cursor;
        int v_2 = 0;
    Label_0176:
        while (true) {
            v_2 = this.cursor;
            this.bra = this.cursor;
            final int among_var = this.find_among(ItalianStemmer.a_0, 7);
            if (among_var == 0) {
                break;
            }
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break Label_0176;
                }
                case 1: {
                    this.slice_from("\u00e0");
                    continue;
                }
                case 2: {
                    this.slice_from("\u00e8");
                    continue;
                }
                case 3: {
                    this.slice_from("\u00ec");
                    continue;
                }
                case 4: {
                    this.slice_from("\u00f2");
                    continue;
                }
                case 5: {
                    this.slice_from("\u00f9");
                    continue;
                }
                case 6: {
                    this.slice_from("qU");
                    continue;
                }
                case 7: {
                    if (this.cursor >= this.limit) {
                        break Label_0176;
                    }
                    ++this.cursor;
                    continue;
                }
            }
        }
        this.cursor = v_2;
        this.cursor = v_1;
        int v_3 = 0;
    Block_8:
        while (true) {
            v_3 = this.cursor;
            int v_4;
            while (true) {
                v_4 = this.cursor;
                if (this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                    this.bra = this.cursor;
                    final int v_5 = this.cursor;
                    if (this.eq_s(1, "u")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                            this.slice_from("U");
                            break;
                        }
                    }
                    this.cursor = v_5;
                    if (this.eq_s(1, "i")) {
                        this.ket = this.cursor;
                        if (this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                            this.slice_from("I");
                            break;
                        }
                    }
                }
                this.cursor = v_4;
                if (this.cursor >= this.limit) {
                    break Block_8;
                }
                ++this.cursor;
            }
            this.cursor = v_4;
        }
        this.cursor = v_3;
        return true;
    }
    
    private boolean r_mark_regions() {
        this.I_pV = this.limit;
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        final int v_1 = this.cursor;
        final int v_2 = this.cursor;
        Label_0327: {
            Label_0319: {
                Label_0182: {
                    if (this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                        final int v_3 = this.cursor;
                        Label_0117: {
                            if (this.out_grouping(ItalianStemmer.g_v, 97, 249)) {
                                while (!this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                                    if (this.cursor >= this.limit) {
                                        break Label_0117;
                                    }
                                    ++this.cursor;
                                }
                                break Label_0319;
                            }
                        }
                        this.cursor = v_3;
                        if (this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                            while (!this.out_grouping(ItalianStemmer.g_v, 97, 249)) {
                                if (this.cursor >= this.limit) {
                                    break Label_0182;
                                }
                                ++this.cursor;
                            }
                            break Label_0319;
                        }
                    }
                }
                this.cursor = v_2;
                if (!this.out_grouping(ItalianStemmer.g_v, 97, 249)) {
                    break Label_0327;
                }
                final int v_4 = this.cursor;
                Label_0271: {
                    if (this.out_grouping(ItalianStemmer.g_v, 97, 249)) {
                        while (!this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                            if (this.cursor >= this.limit) {
                                break Label_0271;
                            }
                            ++this.cursor;
                        }
                        break Label_0319;
                    }
                }
                this.cursor = v_4;
                if (!this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                    break Label_0327;
                }
                if (this.cursor >= this.limit) {
                    break Label_0327;
                }
                ++this.cursor;
            }
            this.I_pV = this.cursor;
        }
        this.cursor = v_1;
        final int v_5 = this.cursor;
    Label_0522:
        while (true) {
            while (!this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_5;
                    return true;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(ItalianStemmer.g_v, 97, 249)) {
                if (this.cursor >= this.limit) {
                    continue Label_0522;
                }
                ++this.cursor;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(ItalianStemmer.g_v, 97, 249)) {
                if (this.cursor >= this.limit) {
                    continue Label_0522;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(ItalianStemmer.g_v, 97, 249)) {
                if (this.cursor >= this.limit) {
                    continue Label_0522;
                }
                ++this.cursor;
            }
            this.I_p2 = this.cursor;
            continue Label_0522;
        }
    }
    
    private boolean r_postlude() {
        int v_1 = 0;
    Label_0116:
        while (true) {
            v_1 = this.cursor;
            this.bra = this.cursor;
            final int among_var = this.find_among(ItalianStemmer.a_1, 3);
            if (among_var == 0) {
                break;
            }
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break Label_0116;
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
    
    private boolean r_RV() {
        return this.I_pV <= this.cursor;
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }
    
    private boolean r_attached_pronoun() {
        this.ket = this.cursor;
        if (this.find_among_b(ItalianStemmer.a_2, 37) == 0) {
            return false;
        }
        this.bra = this.cursor;
        final int among_var = this.find_among_b(ItalianStemmer.a_3, 5);
        if (among_var == 0) {
            return false;
        }
        if (!this.r_RV()) {
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
        }
        return true;
    }
    
    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(ItalianStemmer.a_6, 51);
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
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_1;
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
                this.slice_from("ente");
                break;
            }
            case 6: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 7: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                final int v_2 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(ItalianStemmer.a_4, 4);
                if (among_var == 0) {
                    this.cursor = this.limit - v_2;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_2;
                    break;
                }
                this.slice_del();
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_2;
                        break;
                    }
                    case 1: {
                        this.ket = this.cursor;
                        if (!this.eq_s_b(2, "at")) {
                            this.cursor = this.limit - v_2;
                            break;
                        }
                        this.bra = this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_2;
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
                final int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(ItalianStemmer.a_5, 3);
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
                        break;
                    }
                }
                break;
            }
            case 9: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                final int v_4 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "at")) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "ic")) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.slice_del();
                break;
            }
        }
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
        final int among_var = this.find_among_b(ItalianStemmer.a_7, 87);
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
                this.slice_del();
                break;
            }
        }
        this.limit_backward = v_2;
        return true;
    }
    
    private boolean r_vowel_suffix() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.in_grouping_b(ItalianStemmer.g_AEIO, 97, 242)) {
            this.cursor = this.limit - v_1;
        }
        else {
            this.bra = this.cursor;
            if (!this.r_RV()) {
                this.cursor = this.limit - v_1;
            }
            else {
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "i")) {
                    this.cursor = this.limit - v_1;
                }
                else {
                    this.bra = this.cursor;
                    if (!this.r_RV()) {
                        this.cursor = this.limit - v_1;
                    }
                    else {
                        this.slice_del();
                    }
                }
            }
        }
        final int v_2 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "h")) {
            this.cursor = this.limit - v_2;
        }
        else {
            this.bra = this.cursor;
            if (!this.in_grouping_b(ItalianStemmer.g_CG, 99, 103)) {
                this.cursor = this.limit - v_2;
            }
            else if (!this.r_RV()) {
                this.cursor = this.limit - v_2;
            }
            else {
                this.slice_del();
            }
        }
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
        if (!this.r_attached_pronoun()) {}
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        final int v_5 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            this.cursor = this.limit - v_5;
            if (!this.r_verb_suffix()) {}
        }
        this.cursor = this.limit - v_4;
        final int v_6 = this.limit - this.cursor;
        if (!this.r_vowel_suffix()) {}
        this.cursor = this.limit - v_6;
        this.cursor = this.limit_backward;
        final int v_7 = this.cursor;
        if (!this.r_postlude()) {}
        this.cursor = v_7;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ItalianStemmer;
    }
    
    @Override
    public int hashCode() {
        return ItalianStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("", -1, 7, "", ItalianStemmer.methodObject), new Among("qu", 0, 6, "", ItalianStemmer.methodObject), new Among("\u00e1", 0, 1, "", ItalianStemmer.methodObject), new Among("\u00e9", 0, 2, "", ItalianStemmer.methodObject), new Among("\u00ed", 0, 3, "", ItalianStemmer.methodObject), new Among("\u00f3", 0, 4, "", ItalianStemmer.methodObject), new Among("\u00fa", 0, 5, "", ItalianStemmer.methodObject) };
        a_1 = new Among[] { new Among("", -1, 3, "", ItalianStemmer.methodObject), new Among("I", 0, 1, "", ItalianStemmer.methodObject), new Among("U", 0, 2, "", ItalianStemmer.methodObject) };
        a_2 = new Among[] { new Among("la", -1, -1, "", ItalianStemmer.methodObject), new Among("cela", 0, -1, "", ItalianStemmer.methodObject), new Among("gliela", 0, -1, "", ItalianStemmer.methodObject), new Among("mela", 0, -1, "", ItalianStemmer.methodObject), new Among("tela", 0, -1, "", ItalianStemmer.methodObject), new Among("vela", 0, -1, "", ItalianStemmer.methodObject), new Among("le", -1, -1, "", ItalianStemmer.methodObject), new Among("cele", 6, -1, "", ItalianStemmer.methodObject), new Among("gliele", 6, -1, "", ItalianStemmer.methodObject), new Among("mele", 6, -1, "", ItalianStemmer.methodObject), new Among("tele", 6, -1, "", ItalianStemmer.methodObject), new Among("vele", 6, -1, "", ItalianStemmer.methodObject), new Among("ne", -1, -1, "", ItalianStemmer.methodObject), new Among("cene", 12, -1, "", ItalianStemmer.methodObject), new Among("gliene", 12, -1, "", ItalianStemmer.methodObject), new Among("mene", 12, -1, "", ItalianStemmer.methodObject), new Among("sene", 12, -1, "", ItalianStemmer.methodObject), new Among("tene", 12, -1, "", ItalianStemmer.methodObject), new Among("vene", 12, -1, "", ItalianStemmer.methodObject), new Among("ci", -1, -1, "", ItalianStemmer.methodObject), new Among("li", -1, -1, "", ItalianStemmer.methodObject), new Among("celi", 20, -1, "", ItalianStemmer.methodObject), new Among("glieli", 20, -1, "", ItalianStemmer.methodObject), new Among("meli", 20, -1, "", ItalianStemmer.methodObject), new Among("teli", 20, -1, "", ItalianStemmer.methodObject), new Among("veli", 20, -1, "", ItalianStemmer.methodObject), new Among("gli", 20, -1, "", ItalianStemmer.methodObject), new Among("mi", -1, -1, "", ItalianStemmer.methodObject), new Among("si", -1, -1, "", ItalianStemmer.methodObject), new Among("ti", -1, -1, "", ItalianStemmer.methodObject), new Among("vi", -1, -1, "", ItalianStemmer.methodObject), new Among("lo", -1, -1, "", ItalianStemmer.methodObject), new Among("celo", 31, -1, "", ItalianStemmer.methodObject), new Among("glielo", 31, -1, "", ItalianStemmer.methodObject), new Among("melo", 31, -1, "", ItalianStemmer.methodObject), new Among("telo", 31, -1, "", ItalianStemmer.methodObject), new Among("velo", 31, -1, "", ItalianStemmer.methodObject) };
        a_3 = new Among[] { new Among("ando", -1, 1, "", ItalianStemmer.methodObject), new Among("endo", -1, 1, "", ItalianStemmer.methodObject), new Among("ar", -1, 2, "", ItalianStemmer.methodObject), new Among("er", -1, 2, "", ItalianStemmer.methodObject), new Among("ir", -1, 2, "", ItalianStemmer.methodObject) };
        a_4 = new Among[] { new Among("ic", -1, -1, "", ItalianStemmer.methodObject), new Among("abil", -1, -1, "", ItalianStemmer.methodObject), new Among("os", -1, -1, "", ItalianStemmer.methodObject), new Among("iv", -1, 1, "", ItalianStemmer.methodObject) };
        a_5 = new Among[] { new Among("ic", -1, 1, "", ItalianStemmer.methodObject), new Among("abil", -1, 1, "", ItalianStemmer.methodObject), new Among("iv", -1, 1, "", ItalianStemmer.methodObject) };
        a_6 = new Among[] { new Among("ica", -1, 1, "", ItalianStemmer.methodObject), new Among("logia", -1, 3, "", ItalianStemmer.methodObject), new Among("osa", -1, 1, "", ItalianStemmer.methodObject), new Among("ista", -1, 1, "", ItalianStemmer.methodObject), new Among("iva", -1, 9, "", ItalianStemmer.methodObject), new Among("anza", -1, 1, "", ItalianStemmer.methodObject), new Among("enza", -1, 5, "", ItalianStemmer.methodObject), new Among("ice", -1, 1, "", ItalianStemmer.methodObject), new Among("atrice", 7, 1, "", ItalianStemmer.methodObject), new Among("iche", -1, 1, "", ItalianStemmer.methodObject), new Among("logie", -1, 3, "", ItalianStemmer.methodObject), new Among("abile", -1, 1, "", ItalianStemmer.methodObject), new Among("ibile", -1, 1, "", ItalianStemmer.methodObject), new Among("usione", -1, 4, "", ItalianStemmer.methodObject), new Among("azione", -1, 2, "", ItalianStemmer.methodObject), new Among("uzione", -1, 4, "", ItalianStemmer.methodObject), new Among("atore", -1, 2, "", ItalianStemmer.methodObject), new Among("ose", -1, 1, "", ItalianStemmer.methodObject), new Among("ante", -1, 1, "", ItalianStemmer.methodObject), new Among("mente", -1, 1, "", ItalianStemmer.methodObject), new Among("amente", 19, 7, "", ItalianStemmer.methodObject), new Among("iste", -1, 1, "", ItalianStemmer.methodObject), new Among("ive", -1, 9, "", ItalianStemmer.methodObject), new Among("anze", -1, 1, "", ItalianStemmer.methodObject), new Among("enze", -1, 5, "", ItalianStemmer.methodObject), new Among("ici", -1, 1, "", ItalianStemmer.methodObject), new Among("atrici", 25, 1, "", ItalianStemmer.methodObject), new Among("ichi", -1, 1, "", ItalianStemmer.methodObject), new Among("abili", -1, 1, "", ItalianStemmer.methodObject), new Among("ibili", -1, 1, "", ItalianStemmer.methodObject), new Among("ismi", -1, 1, "", ItalianStemmer.methodObject), new Among("usioni", -1, 4, "", ItalianStemmer.methodObject), new Among("azioni", -1, 2, "", ItalianStemmer.methodObject), new Among("uzioni", -1, 4, "", ItalianStemmer.methodObject), new Among("atori", -1, 2, "", ItalianStemmer.methodObject), new Among("osi", -1, 1, "", ItalianStemmer.methodObject), new Among("anti", -1, 1, "", ItalianStemmer.methodObject), new Among("amenti", -1, 6, "", ItalianStemmer.methodObject), new Among("imenti", -1, 6, "", ItalianStemmer.methodObject), new Among("isti", -1, 1, "", ItalianStemmer.methodObject), new Among("ivi", -1, 9, "", ItalianStemmer.methodObject), new Among("ico", -1, 1, "", ItalianStemmer.methodObject), new Among("ismo", -1, 1, "", ItalianStemmer.methodObject), new Among("oso", -1, 1, "", ItalianStemmer.methodObject), new Among("amento", -1, 6, "", ItalianStemmer.methodObject), new Among("imento", -1, 6, "", ItalianStemmer.methodObject), new Among("ivo", -1, 9, "", ItalianStemmer.methodObject), new Among("it\u00e0", -1, 8, "", ItalianStemmer.methodObject), new Among("ist\u00e0", -1, 1, "", ItalianStemmer.methodObject), new Among("ist\u00e8", -1, 1, "", ItalianStemmer.methodObject), new Among("ist\u00ec", -1, 1, "", ItalianStemmer.methodObject) };
        a_7 = new Among[] { new Among("isca", -1, 1, "", ItalianStemmer.methodObject), new Among("enda", -1, 1, "", ItalianStemmer.methodObject), new Among("ata", -1, 1, "", ItalianStemmer.methodObject), new Among("ita", -1, 1, "", ItalianStemmer.methodObject), new Among("uta", -1, 1, "", ItalianStemmer.methodObject), new Among("ava", -1, 1, "", ItalianStemmer.methodObject), new Among("eva", -1, 1, "", ItalianStemmer.methodObject), new Among("iva", -1, 1, "", ItalianStemmer.methodObject), new Among("erebbe", -1, 1, "", ItalianStemmer.methodObject), new Among("irebbe", -1, 1, "", ItalianStemmer.methodObject), new Among("isce", -1, 1, "", ItalianStemmer.methodObject), new Among("ende", -1, 1, "", ItalianStemmer.methodObject), new Among("are", -1, 1, "", ItalianStemmer.methodObject), new Among("ere", -1, 1, "", ItalianStemmer.methodObject), new Among("ire", -1, 1, "", ItalianStemmer.methodObject), new Among("asse", -1, 1, "", ItalianStemmer.methodObject), new Among("ate", -1, 1, "", ItalianStemmer.methodObject), new Among("avate", 16, 1, "", ItalianStemmer.methodObject), new Among("evate", 16, 1, "", ItalianStemmer.methodObject), new Among("ivate", 16, 1, "", ItalianStemmer.methodObject), new Among("ete", -1, 1, "", ItalianStemmer.methodObject), new Among("erete", 20, 1, "", ItalianStemmer.methodObject), new Among("irete", 20, 1, "", ItalianStemmer.methodObject), new Among("ite", -1, 1, "", ItalianStemmer.methodObject), new Among("ereste", -1, 1, "", ItalianStemmer.methodObject), new Among("ireste", -1, 1, "", ItalianStemmer.methodObject), new Among("ute", -1, 1, "", ItalianStemmer.methodObject), new Among("erai", -1, 1, "", ItalianStemmer.methodObject), new Among("irai", -1, 1, "", ItalianStemmer.methodObject), new Among("isci", -1, 1, "", ItalianStemmer.methodObject), new Among("endi", -1, 1, "", ItalianStemmer.methodObject), new Among("erei", -1, 1, "", ItalianStemmer.methodObject), new Among("irei", -1, 1, "", ItalianStemmer.methodObject), new Among("assi", -1, 1, "", ItalianStemmer.methodObject), new Among("ati", -1, 1, "", ItalianStemmer.methodObject), new Among("iti", -1, 1, "", ItalianStemmer.methodObject), new Among("eresti", -1, 1, "", ItalianStemmer.methodObject), new Among("iresti", -1, 1, "", ItalianStemmer.methodObject), new Among("uti", -1, 1, "", ItalianStemmer.methodObject), new Among("avi", -1, 1, "", ItalianStemmer.methodObject), new Among("evi", -1, 1, "", ItalianStemmer.methodObject), new Among("ivi", -1, 1, "", ItalianStemmer.methodObject), new Among("isco", -1, 1, "", ItalianStemmer.methodObject), new Among("ando", -1, 1, "", ItalianStemmer.methodObject), new Among("endo", -1, 1, "", ItalianStemmer.methodObject), new Among("Yamo", -1, 1, "", ItalianStemmer.methodObject), new Among("iamo", -1, 1, "", ItalianStemmer.methodObject), new Among("avamo", -1, 1, "", ItalianStemmer.methodObject), new Among("evamo", -1, 1, "", ItalianStemmer.methodObject), new Among("ivamo", -1, 1, "", ItalianStemmer.methodObject), new Among("eremo", -1, 1, "", ItalianStemmer.methodObject), new Among("iremo", -1, 1, "", ItalianStemmer.methodObject), new Among("assimo", -1, 1, "", ItalianStemmer.methodObject), new Among("ammo", -1, 1, "", ItalianStemmer.methodObject), new Among("emmo", -1, 1, "", ItalianStemmer.methodObject), new Among("eremmo", 54, 1, "", ItalianStemmer.methodObject), new Among("iremmo", 54, 1, "", ItalianStemmer.methodObject), new Among("immo", -1, 1, "", ItalianStemmer.methodObject), new Among("ano", -1, 1, "", ItalianStemmer.methodObject), new Among("iscano", 58, 1, "", ItalianStemmer.methodObject), new Among("avano", 58, 1, "", ItalianStemmer.methodObject), new Among("evano", 58, 1, "", ItalianStemmer.methodObject), new Among("ivano", 58, 1, "", ItalianStemmer.methodObject), new Among("eranno", -1, 1, "", ItalianStemmer.methodObject), new Among("iranno", -1, 1, "", ItalianStemmer.methodObject), new Among("ono", -1, 1, "", ItalianStemmer.methodObject), new Among("iscono", 65, 1, "", ItalianStemmer.methodObject), new Among("arono", 65, 1, "", ItalianStemmer.methodObject), new Among("erono", 65, 1, "", ItalianStemmer.methodObject), new Among("irono", 65, 1, "", ItalianStemmer.methodObject), new Among("erebbero", -1, 1, "", ItalianStemmer.methodObject), new Among("irebbero", -1, 1, "", ItalianStemmer.methodObject), new Among("assero", -1, 1, "", ItalianStemmer.methodObject), new Among("essero", -1, 1, "", ItalianStemmer.methodObject), new Among("issero", -1, 1, "", ItalianStemmer.methodObject), new Among("ato", -1, 1, "", ItalianStemmer.methodObject), new Among("ito", -1, 1, "", ItalianStemmer.methodObject), new Among("uto", -1, 1, "", ItalianStemmer.methodObject), new Among("avo", -1, 1, "", ItalianStemmer.methodObject), new Among("evo", -1, 1, "", ItalianStemmer.methodObject), new Among("ivo", -1, 1, "", ItalianStemmer.methodObject), new Among("ar", -1, 1, "", ItalianStemmer.methodObject), new Among("ir", -1, 1, "", ItalianStemmer.methodObject), new Among("er\u00e0", -1, 1, "", ItalianStemmer.methodObject), new Among("ir\u00e0", -1, 1, "", ItalianStemmer.methodObject), new Among("er\u00f2", -1, 1, "", ItalianStemmer.methodObject), new Among("ir\u00f2", -1, 1, "", ItalianStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080', '\u0080', '\b', '\u0002', '\u0001' };
        g_AEIO = new char[] { '\u0011', 'A', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0080', '\u0080', '\b', '\u0002' };
        g_CG = new char[] { '\u0011' };
    }
}
