package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class LovinsStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final Among[] a_3;
    
    private void copy_from(final LovinsStemmer other) {
        super.copy_from(other);
    }
    
    private boolean r_A() {
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }
    
    private boolean r_B() {
        final int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }
    
    private boolean r_C() {
        final int c = this.cursor - 4;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }
    
    private boolean r_D() {
        final int c = this.cursor - 5;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }
    
    private boolean r_E() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "e")) {
            this.cursor = this.limit - v_2;
            return true;
        }
        return false;
    }
    
    private boolean r_F() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "e")) {
            this.cursor = this.limit - v_2;
            return true;
        }
        return false;
    }
    
    private boolean r_G() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(1, "f");
    }
    
    private boolean r_H() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "t")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(2, "ll")) {
                return false;
            }
        }
        return true;
    }
    
    private boolean r_I() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "o")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "e")) {
            this.cursor = this.limit - v_3;
            return true;
        }
        return false;
    }
    
    private boolean r_J() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "a")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "e")) {
            this.cursor = this.limit - v_3;
            return true;
        }
        return false;
    }
    
    private boolean r_K() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "i")) {
                this.cursor = this.limit - v_2;
                if (!this.eq_s_b(1, "e")) {
                    return false;
                }
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
                if (!this.eq_s_b(1, "u")) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean r_L() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "u")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "x")) {
            this.cursor = this.limit - v_3;
            final int v_4 = this.limit - this.cursor;
            if (this.eq_s_b(1, "s")) {
                final int v_5 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "o")) {
                    this.cursor = this.limit - v_5;
                    return false;
                }
            }
            this.cursor = this.limit - v_4;
            return true;
        }
        return false;
    }
    
    private boolean r_M() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "a")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "c")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        if (this.eq_s_b(1, "e")) {
            return false;
        }
        this.cursor = this.limit - v_4;
        final int v_5 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "m")) {
            this.cursor = this.limit - v_5;
            return true;
        }
        return false;
    }
    
    private boolean r_N() {
        final int v_1 = this.limit - this.cursor;
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        final int v_2 = this.limit - this.cursor;
        final int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_3;
        }
        else {
            this.cursor = this.limit - v_2;
            c = this.cursor - 2;
            if (this.limit_backward > c || c > this.limit) {
                return false;
            }
            this.cursor = c;
        }
        return true;
    }
    
    private boolean r_O() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "i")) {
                return false;
            }
        }
        return true;
    }
    
    private boolean r_P() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "c")) {
            this.cursor = this.limit - v_2;
            return true;
        }
        return false;
    }
    
    private boolean r_Q() {
        final int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "l")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_4;
            return true;
        }
        return false;
    }
    
    private boolean r_R() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "r")) {
                return false;
            }
        }
        return true;
    }
    
    private boolean r_S() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(2, "dr")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "t")) {
                return false;
            }
            final int v_3 = this.limit - this.cursor;
            if (this.eq_s_b(1, "t")) {
                return false;
            }
            this.cursor = this.limit - v_3;
        }
        return true;
    }
    
    private boolean r_T() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "t")) {
                return false;
            }
            final int v_3 = this.limit - this.cursor;
            if (this.eq_s_b(1, "o")) {
                return false;
            }
            this.cursor = this.limit - v_3;
        }
        return true;
    }
    
    private boolean r_U() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "m")) {
                this.cursor = this.limit - v_2;
                if (!this.eq_s_b(1, "n")) {
                    this.cursor = this.limit - v_2;
                    if (!this.eq_s_b(1, "r")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean r_V() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(1, "c");
    }
    
    private boolean r_W() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "u")) {
            this.cursor = this.limit - v_3;
            return true;
        }
        return false;
    }
    
    private boolean r_X() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "i")) {
                this.cursor = this.limit - v_2;
                if (!this.eq_s_b(1, "e")) {
                    return false;
                }
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
                if (!this.eq_s_b(1, "u")) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean r_Y() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(2, "in");
    }
    
    private boolean r_Z() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "f")) {
            this.cursor = this.limit - v_2;
            return true;
        }
        return false;
    }
    
    private boolean r_AA() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.find_among_b(LovinsStemmer.a_0, 9) != 0;
    }
    
    private boolean r_BB() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(3, "met")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(4, "ryst")) {
            this.cursor = this.limit - v_3;
            return true;
        }
        return false;
    }
    
    private boolean r_CC() {
        final int v_1 = this.limit - this.cursor;
        final int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(1, "l");
    }
    
    private boolean r_endings() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(LovinsStemmer.a_1, 294);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_undouble() {
        final int v_1 = this.limit - this.cursor;
        if (this.find_among_b(LovinsStemmer.a_2, 10) == 0) {
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
    
    private boolean r_respell() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(LovinsStemmer.a_3, 34);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("ief");
                break;
            }
            case 2: {
                this.slice_from("uc");
                break;
            }
            case 3: {
                this.slice_from("um");
                break;
            }
            case 4: {
                this.slice_from("rb");
                break;
            }
            case 5: {
                this.slice_from("ur");
                break;
            }
            case 6: {
                this.slice_from("ister");
                break;
            }
            case 7: {
                this.slice_from("meter");
                break;
            }
            case 8: {
                this.slice_from("olut");
                break;
            }
            case 9: {
                final int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(1, "a")) {
                    return false;
                }
                this.cursor = this.limit - v_1;
                final int v_2 = this.limit - this.cursor;
                if (this.eq_s_b(1, "i")) {
                    return false;
                }
                this.cursor = this.limit - v_2;
                final int v_3 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "o")) {
                    this.cursor = this.limit - v_3;
                    this.slice_from("l");
                    break;
                }
                return false;
            }
            case 10: {
                this.slice_from("bic");
                break;
            }
            case 11: {
                this.slice_from("dic");
                break;
            }
            case 12: {
                this.slice_from("pic");
                break;
            }
            case 13: {
                this.slice_from("tic");
                break;
            }
            case 14: {
                this.slice_from("ac");
                break;
            }
            case 15: {
                this.slice_from("ec");
                break;
            }
            case 16: {
                this.slice_from("ic");
                break;
            }
            case 17: {
                this.slice_from("luc");
                break;
            }
            case 18: {
                this.slice_from("uas");
                break;
            }
            case 19: {
                this.slice_from("vas");
                break;
            }
            case 20: {
                this.slice_from("cis");
                break;
            }
            case 21: {
                this.slice_from("lis");
                break;
            }
            case 22: {
                this.slice_from("eris");
                break;
            }
            case 23: {
                this.slice_from("pans");
                break;
            }
            case 24: {
                final int v_4 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "s")) {
                    this.cursor = this.limit - v_4;
                    this.slice_from("ens");
                    break;
                }
                return false;
            }
            case 25: {
                this.slice_from("ons");
                break;
            }
            case 26: {
                this.slice_from("lus");
                break;
            }
            case 27: {
                this.slice_from("rus");
                break;
            }
            case 28: {
                final int v_5 = this.limit - this.cursor;
                if (this.eq_s_b(1, "p")) {
                    return false;
                }
                this.cursor = this.limit - v_5;
                final int v_6 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "t")) {
                    this.cursor = this.limit - v_6;
                    this.slice_from("hes");
                    break;
                }
                return false;
            }
            case 29: {
                this.slice_from("mis");
                break;
            }
            case 30: {
                final int v_7 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "m")) {
                    this.cursor = this.limit - v_7;
                    this.slice_from("ens");
                    break;
                }
                return false;
            }
            case 31: {
                this.slice_from("ers");
                break;
            }
            case 32: {
                final int v_8 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "n")) {
                    this.cursor = this.limit - v_8;
                    this.slice_from("es");
                    break;
                }
                return false;
            }
            case 33: {
                this.slice_from("ys");
                break;
            }
            case 34: {
                this.slice_from("ys");
                break;
            }
        }
        return true;
    }
    
    @Override
    public boolean stem() {
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_1 = this.limit - this.cursor;
        if (!this.r_endings()) {}
        this.cursor = this.limit - v_1;
        final int v_2 = this.limit - this.cursor;
        if (!this.r_undouble()) {}
        this.cursor = this.limit - v_2;
        final int v_3 = this.limit - this.cursor;
        if (!this.r_respell()) {}
        this.cursor = this.limit - v_3;
        this.cursor = this.limit_backward;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof LovinsStemmer;
    }
    
    @Override
    public int hashCode() {
        return LovinsStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("d", -1, -1, "", LovinsStemmer.methodObject), new Among("f", -1, -1, "", LovinsStemmer.methodObject), new Among("ph", -1, -1, "", LovinsStemmer.methodObject), new Among("th", -1, -1, "", LovinsStemmer.methodObject), new Among("l", -1, -1, "", LovinsStemmer.methodObject), new Among("er", -1, -1, "", LovinsStemmer.methodObject), new Among("or", -1, -1, "", LovinsStemmer.methodObject), new Among("es", -1, -1, "", LovinsStemmer.methodObject), new Among("t", -1, -1, "", LovinsStemmer.methodObject) };
        a_1 = new Among[] { new Among("s'", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("a", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("ia", 1, 1, "r_A", LovinsStemmer.methodObject), new Among("ata", 1, 1, "r_A", LovinsStemmer.methodObject), new Among("ic", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("aic", 4, 1, "r_A", LovinsStemmer.methodObject), new Among("allic", 4, 1, "r_BB", LovinsStemmer.methodObject), new Among("aric", 4, 1, "r_A", LovinsStemmer.methodObject), new Among("atic", 4, 1, "r_B", LovinsStemmer.methodObject), new Among("itic", 4, 1, "r_H", LovinsStemmer.methodObject), new Among("antic", 4, 1, "r_C", LovinsStemmer.methodObject), new Among("istic", 4, 1, "r_A", LovinsStemmer.methodObject), new Among("alistic", 11, 1, "r_B", LovinsStemmer.methodObject), new Among("aristic", 11, 1, "r_A", LovinsStemmer.methodObject), new Among("ivistic", 11, 1, "r_A", LovinsStemmer.methodObject), new Among("ed", -1, 1, "r_E", LovinsStemmer.methodObject), new Among("anced", 15, 1, "r_B", LovinsStemmer.methodObject), new Among("enced", 15, 1, "r_A", LovinsStemmer.methodObject), new Among("ished", 15, 1, "r_A", LovinsStemmer.methodObject), new Among("ied", 15, 1, "r_A", LovinsStemmer.methodObject), new Among("ened", 15, 1, "r_E", LovinsStemmer.methodObject), new Among("ioned", 15, 1, "r_A", LovinsStemmer.methodObject), new Among("ated", 15, 1, "r_I", LovinsStemmer.methodObject), new Among("ented", 15, 1, "r_C", LovinsStemmer.methodObject), new Among("ized", 15, 1, "r_F", LovinsStemmer.methodObject), new Among("arized", 24, 1, "r_A", LovinsStemmer.methodObject), new Among("oid", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("aroid", 26, 1, "r_A", LovinsStemmer.methodObject), new Among("hood", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("ehood", 28, 1, "r_A", LovinsStemmer.methodObject), new Among("ihood", 28, 1, "r_A", LovinsStemmer.methodObject), new Among("elihood", 30, 1, "r_E", LovinsStemmer.methodObject), new Among("ward", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("e", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("ae", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("ance", 33, 1, "r_B", LovinsStemmer.methodObject), new Among("icance", 35, 1, "r_A", LovinsStemmer.methodObject), new Among("ence", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("ide", 33, 1, "r_L", LovinsStemmer.methodObject), new Among("icide", 38, 1, "r_A", LovinsStemmer.methodObject), new Among("otide", 38, 1, "r_A", LovinsStemmer.methodObject), new Among("age", 33, 1, "r_B", LovinsStemmer.methodObject), new Among("able", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("atable", 42, 1, "r_A", LovinsStemmer.methodObject), new Among("izable", 42, 1, "r_E", LovinsStemmer.methodObject), new Among("arizable", 44, 1, "r_A", LovinsStemmer.methodObject), new Among("ible", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("encible", 46, 1, "r_A", LovinsStemmer.methodObject), new Among("ene", 33, 1, "r_E", LovinsStemmer.methodObject), new Among("ine", 33, 1, "r_M", LovinsStemmer.methodObject), new Among("idine", 49, 1, "r_I", LovinsStemmer.methodObject), new Among("one", 33, 1, "r_R", LovinsStemmer.methodObject), new Among("ature", 33, 1, "r_E", LovinsStemmer.methodObject), new Among("eature", 52, 1, "r_Z", LovinsStemmer.methodObject), new Among("ese", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("wise", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("ate", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("entiate", 56, 1, "r_A", LovinsStemmer.methodObject), new Among("inate", 56, 1, "r_A", LovinsStemmer.methodObject), new Among("ionate", 56, 1, "r_D", LovinsStemmer.methodObject), new Among("ite", 33, 1, "r_AA", LovinsStemmer.methodObject), new Among("ive", 33, 1, "r_A", LovinsStemmer.methodObject), new Among("ative", 61, 1, "r_A", LovinsStemmer.methodObject), new Among("ize", 33, 1, "r_F", LovinsStemmer.methodObject), new Among("alize", 63, 1, "r_A", LovinsStemmer.methodObject), new Among("icalize", 64, 1, "r_A", LovinsStemmer.methodObject), new Among("ialize", 64, 1, "r_A", LovinsStemmer.methodObject), new Among("entialize", 66, 1, "r_A", LovinsStemmer.methodObject), new Among("ionalize", 64, 1, "r_A", LovinsStemmer.methodObject), new Among("arize", 63, 1, "r_A", LovinsStemmer.methodObject), new Among("ing", -1, 1, "r_N", LovinsStemmer.methodObject), new Among("ancing", 70, 1, "r_B", LovinsStemmer.methodObject), new Among("encing", 70, 1, "r_A", LovinsStemmer.methodObject), new Among("aging", 70, 1, "r_B", LovinsStemmer.methodObject), new Among("ening", 70, 1, "r_E", LovinsStemmer.methodObject), new Among("ioning", 70, 1, "r_A", LovinsStemmer.methodObject), new Among("ating", 70, 1, "r_I", LovinsStemmer.methodObject), new Among("enting", 70, 1, "r_C", LovinsStemmer.methodObject), new Among("ying", 70, 1, "r_B", LovinsStemmer.methodObject), new Among("izing", 70, 1, "r_F", LovinsStemmer.methodObject), new Among("arizing", 79, 1, "r_A", LovinsStemmer.methodObject), new Among("ish", -1, 1, "r_C", LovinsStemmer.methodObject), new Among("yish", 81, 1, "r_A", LovinsStemmer.methodObject), new Among("i", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("al", -1, 1, "r_BB", LovinsStemmer.methodObject), new Among("ical", 84, 1, "r_A", LovinsStemmer.methodObject), new Among("aical", 85, 1, "r_A", LovinsStemmer.methodObject), new Among("istical", 85, 1, "r_A", LovinsStemmer.methodObject), new Among("oidal", 84, 1, "r_A", LovinsStemmer.methodObject), new Among("eal", 84, 1, "r_Y", LovinsStemmer.methodObject), new Among("ial", 84, 1, "r_A", LovinsStemmer.methodObject), new Among("ancial", 90, 1, "r_A", LovinsStemmer.methodObject), new Among("arial", 90, 1, "r_A", LovinsStemmer.methodObject), new Among("ential", 90, 1, "r_A", LovinsStemmer.methodObject), new Among("ional", 84, 1, "r_A", LovinsStemmer.methodObject), new Among("ational", 94, 1, "r_B", LovinsStemmer.methodObject), new Among("izational", 95, 1, "r_A", LovinsStemmer.methodObject), new Among("ental", 84, 1, "r_A", LovinsStemmer.methodObject), new Among("ful", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("eful", 98, 1, "r_A", LovinsStemmer.methodObject), new Among("iful", 98, 1, "r_A", LovinsStemmer.methodObject), new Among("yl", -1, 1, "r_R", LovinsStemmer.methodObject), new Among("ism", -1, 1, "r_B", LovinsStemmer.methodObject), new Among("icism", 102, 1, "r_A", LovinsStemmer.methodObject), new Among("oidism", 102, 1, "r_A", LovinsStemmer.methodObject), new Among("alism", 102, 1, "r_B", LovinsStemmer.methodObject), new Among("icalism", 105, 1, "r_A", LovinsStemmer.methodObject), new Among("ionalism", 105, 1, "r_A", LovinsStemmer.methodObject), new Among("inism", 102, 1, "r_J", LovinsStemmer.methodObject), new Among("ativism", 102, 1, "r_A", LovinsStemmer.methodObject), new Among("um", -1, 1, "r_U", LovinsStemmer.methodObject), new Among("ium", 110, 1, "r_A", LovinsStemmer.methodObject), new Among("ian", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("ician", 112, 1, "r_A", LovinsStemmer.methodObject), new Among("en", -1, 1, "r_F", LovinsStemmer.methodObject), new Among("ogen", 114, 1, "r_A", LovinsStemmer.methodObject), new Among("on", -1, 1, "r_S", LovinsStemmer.methodObject), new Among("ion", 116, 1, "r_Q", LovinsStemmer.methodObject), new Among("ation", 117, 1, "r_B", LovinsStemmer.methodObject), new Among("ication", 118, 1, "r_G", LovinsStemmer.methodObject), new Among("entiation", 118, 1, "r_A", LovinsStemmer.methodObject), new Among("ination", 118, 1, "r_A", LovinsStemmer.methodObject), new Among("isation", 118, 1, "r_A", LovinsStemmer.methodObject), new Among("arisation", 122, 1, "r_A", LovinsStemmer.methodObject), new Among("entation", 118, 1, "r_A", LovinsStemmer.methodObject), new Among("ization", 118, 1, "r_F", LovinsStemmer.methodObject), new Among("arization", 125, 1, "r_A", LovinsStemmer.methodObject), new Among("action", 117, 1, "r_G", LovinsStemmer.methodObject), new Among("o", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("ar", -1, 1, "r_X", LovinsStemmer.methodObject), new Among("ear", 129, 1, "r_Y", LovinsStemmer.methodObject), new Among("ier", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("ariser", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("izer", -1, 1, "r_F", LovinsStemmer.methodObject), new Among("arizer", 133, 1, "r_A", LovinsStemmer.methodObject), new Among("or", -1, 1, "r_T", LovinsStemmer.methodObject), new Among("ator", 135, 1, "r_A", LovinsStemmer.methodObject), new Among("s", -1, 1, "r_W", LovinsStemmer.methodObject), new Among("'s", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("as", 137, 1, "r_B", LovinsStemmer.methodObject), new Among("ics", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("istics", 140, 1, "r_A", LovinsStemmer.methodObject), new Among("es", 137, 1, "r_E", LovinsStemmer.methodObject), new Among("ances", 142, 1, "r_B", LovinsStemmer.methodObject), new Among("ences", 142, 1, "r_A", LovinsStemmer.methodObject), new Among("ides", 142, 1, "r_L", LovinsStemmer.methodObject), new Among("oides", 145, 1, "r_A", LovinsStemmer.methodObject), new Among("ages", 142, 1, "r_B", LovinsStemmer.methodObject), new Among("ies", 142, 1, "r_P", LovinsStemmer.methodObject), new Among("acies", 148, 1, "r_A", LovinsStemmer.methodObject), new Among("ancies", 148, 1, "r_A", LovinsStemmer.methodObject), new Among("encies", 148, 1, "r_A", LovinsStemmer.methodObject), new Among("aries", 148, 1, "r_A", LovinsStemmer.methodObject), new Among("ities", 148, 1, "r_A", LovinsStemmer.methodObject), new Among("alities", 153, 1, "r_A", LovinsStemmer.methodObject), new Among("ivities", 153, 1, "r_A", LovinsStemmer.methodObject), new Among("ines", 142, 1, "r_M", LovinsStemmer.methodObject), new Among("nesses", 142, 1, "r_A", LovinsStemmer.methodObject), new Among("ates", 142, 1, "r_A", LovinsStemmer.methodObject), new Among("atives", 142, 1, "r_A", LovinsStemmer.methodObject), new Among("ings", 137, 1, "r_N", LovinsStemmer.methodObject), new Among("is", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("als", 137, 1, "r_BB", LovinsStemmer.methodObject), new Among("ials", 162, 1, "r_A", LovinsStemmer.methodObject), new Among("entials", 163, 1, "r_A", LovinsStemmer.methodObject), new Among("ionals", 162, 1, "r_A", LovinsStemmer.methodObject), new Among("isms", 137, 1, "r_B", LovinsStemmer.methodObject), new Among("ians", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("icians", 167, 1, "r_A", LovinsStemmer.methodObject), new Among("ions", 137, 1, "r_B", LovinsStemmer.methodObject), new Among("ations", 169, 1, "r_B", LovinsStemmer.methodObject), new Among("arisations", 170, 1, "r_A", LovinsStemmer.methodObject), new Among("entations", 170, 1, "r_A", LovinsStemmer.methodObject), new Among("izations", 170, 1, "r_A", LovinsStemmer.methodObject), new Among("arizations", 173, 1, "r_A", LovinsStemmer.methodObject), new Among("ars", 137, 1, "r_O", LovinsStemmer.methodObject), new Among("iers", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("izers", 137, 1, "r_F", LovinsStemmer.methodObject), new Among("ators", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("less", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("eless", 179, 1, "r_A", LovinsStemmer.methodObject), new Among("ness", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("eness", 181, 1, "r_E", LovinsStemmer.methodObject), new Among("ableness", 182, 1, "r_A", LovinsStemmer.methodObject), new Among("eableness", 183, 1, "r_E", LovinsStemmer.methodObject), new Among("ibleness", 182, 1, "r_A", LovinsStemmer.methodObject), new Among("ateness", 182, 1, "r_A", LovinsStemmer.methodObject), new Among("iteness", 182, 1, "r_A", LovinsStemmer.methodObject), new Among("iveness", 182, 1, "r_A", LovinsStemmer.methodObject), new Among("ativeness", 188, 1, "r_A", LovinsStemmer.methodObject), new Among("ingness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("ishness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("iness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("ariness", 192, 1, "r_E", LovinsStemmer.methodObject), new Among("alness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("icalness", 194, 1, "r_A", LovinsStemmer.methodObject), new Among("antialness", 194, 1, "r_A", LovinsStemmer.methodObject), new Among("entialness", 194, 1, "r_A", LovinsStemmer.methodObject), new Among("ionalness", 194, 1, "r_A", LovinsStemmer.methodObject), new Among("fulness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("lessness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("ousness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("eousness", 201, 1, "r_A", LovinsStemmer.methodObject), new Among("iousness", 201, 1, "r_A", LovinsStemmer.methodObject), new Among("itousness", 201, 1, "r_A", LovinsStemmer.methodObject), new Among("entness", 181, 1, "r_A", LovinsStemmer.methodObject), new Among("ants", 137, 1, "r_B", LovinsStemmer.methodObject), new Among("ists", 137, 1, "r_A", LovinsStemmer.methodObject), new Among("icists", 207, 1, "r_A", LovinsStemmer.methodObject), new Among("us", 137, 1, "r_V", LovinsStemmer.methodObject), new Among("ous", 209, 1, "r_A", LovinsStemmer.methodObject), new Among("eous", 210, 1, "r_A", LovinsStemmer.methodObject), new Among("aceous", 211, 1, "r_A", LovinsStemmer.methodObject), new Among("antaneous", 211, 1, "r_A", LovinsStemmer.methodObject), new Among("ious", 210, 1, "r_A", LovinsStemmer.methodObject), new Among("acious", 214, 1, "r_B", LovinsStemmer.methodObject), new Among("itous", 210, 1, "r_A", LovinsStemmer.methodObject), new Among("ant", -1, 1, "r_B", LovinsStemmer.methodObject), new Among("icant", 217, 1, "r_A", LovinsStemmer.methodObject), new Among("ent", -1, 1, "r_C", LovinsStemmer.methodObject), new Among("ement", 219, 1, "r_A", LovinsStemmer.methodObject), new Among("izement", 220, 1, "r_A", LovinsStemmer.methodObject), new Among("ist", -1, 1, "r_A", LovinsStemmer.methodObject), new Among("icist", 222, 1, "r_A", LovinsStemmer.methodObject), new Among("alist", 222, 1, "r_A", LovinsStemmer.methodObject), new Among("icalist", 224, 1, "r_A", LovinsStemmer.methodObject), new Among("ialist", 224, 1, "r_A", LovinsStemmer.methodObject), new Among("ionist", 222, 1, "r_A", LovinsStemmer.methodObject), new Among("entist", 222, 1, "r_A", LovinsStemmer.methodObject), new Among("y", -1, 1, "r_B", LovinsStemmer.methodObject), new Among("acy", 229, 1, "r_A", LovinsStemmer.methodObject), new Among("ancy", 229, 1, "r_B", LovinsStemmer.methodObject), new Among("ency", 229, 1, "r_A", LovinsStemmer.methodObject), new Among("ly", 229, 1, "r_B", LovinsStemmer.methodObject), new Among("ealy", 233, 1, "r_Y", LovinsStemmer.methodObject), new Among("ably", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("ibly", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("edly", 233, 1, "r_E", LovinsStemmer.methodObject), new Among("iedly", 237, 1, "r_A", LovinsStemmer.methodObject), new Among("ely", 233, 1, "r_E", LovinsStemmer.methodObject), new Among("ately", 239, 1, "r_A", LovinsStemmer.methodObject), new Among("ively", 239, 1, "r_A", LovinsStemmer.methodObject), new Among("atively", 241, 1, "r_A", LovinsStemmer.methodObject), new Among("ingly", 233, 1, "r_B", LovinsStemmer.methodObject), new Among("atingly", 243, 1, "r_A", LovinsStemmer.methodObject), new Among("ily", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("lily", 245, 1, "r_A", LovinsStemmer.methodObject), new Among("arily", 245, 1, "r_A", LovinsStemmer.methodObject), new Among("ally", 233, 1, "r_B", LovinsStemmer.methodObject), new Among("ically", 248, 1, "r_A", LovinsStemmer.methodObject), new Among("aically", 249, 1, "r_A", LovinsStemmer.methodObject), new Among("allically", 249, 1, "r_C", LovinsStemmer.methodObject), new Among("istically", 249, 1, "r_A", LovinsStemmer.methodObject), new Among("alistically", 252, 1, "r_B", LovinsStemmer.methodObject), new Among("oidally", 248, 1, "r_A", LovinsStemmer.methodObject), new Among("ially", 248, 1, "r_A", LovinsStemmer.methodObject), new Among("entially", 255, 1, "r_A", LovinsStemmer.methodObject), new Among("ionally", 248, 1, "r_A", LovinsStemmer.methodObject), new Among("ationally", 257, 1, "r_B", LovinsStemmer.methodObject), new Among("izationally", 258, 1, "r_B", LovinsStemmer.methodObject), new Among("entally", 248, 1, "r_A", LovinsStemmer.methodObject), new Among("fully", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("efully", 261, 1, "r_A", LovinsStemmer.methodObject), new Among("ifully", 261, 1, "r_A", LovinsStemmer.methodObject), new Among("enly", 233, 1, "r_E", LovinsStemmer.methodObject), new Among("arly", 233, 1, "r_K", LovinsStemmer.methodObject), new Among("early", 265, 1, "r_Y", LovinsStemmer.methodObject), new Among("lessly", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("ously", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("eously", 268, 1, "r_A", LovinsStemmer.methodObject), new Among("iously", 268, 1, "r_A", LovinsStemmer.methodObject), new Among("ently", 233, 1, "r_A", LovinsStemmer.methodObject), new Among("ary", 229, 1, "r_F", LovinsStemmer.methodObject), new Among("ery", 229, 1, "r_E", LovinsStemmer.methodObject), new Among("icianry", 229, 1, "r_A", LovinsStemmer.methodObject), new Among("atory", 229, 1, "r_A", LovinsStemmer.methodObject), new Among("ity", 229, 1, "r_A", LovinsStemmer.methodObject), new Among("acity", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("icity", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("eity", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("ality", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("icality", 280, 1, "r_A", LovinsStemmer.methodObject), new Among("iality", 280, 1, "r_A", LovinsStemmer.methodObject), new Among("antiality", 282, 1, "r_A", LovinsStemmer.methodObject), new Among("entiality", 282, 1, "r_A", LovinsStemmer.methodObject), new Among("ionality", 280, 1, "r_A", LovinsStemmer.methodObject), new Among("elity", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("ability", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("izability", 287, 1, "r_A", LovinsStemmer.methodObject), new Among("arizability", 288, 1, "r_A", LovinsStemmer.methodObject), new Among("ibility", 276, 1, "r_A", LovinsStemmer.methodObject), new Among("inity", 276, 1, "r_CC", LovinsStemmer.methodObject), new Among("arity", 276, 1, "r_B", LovinsStemmer.methodObject), new Among("ivity", 276, 1, "r_A", LovinsStemmer.methodObject) };
        a_2 = new Among[] { new Among("bb", -1, -1, "", LovinsStemmer.methodObject), new Among("dd", -1, -1, "", LovinsStemmer.methodObject), new Among("gg", -1, -1, "", LovinsStemmer.methodObject), new Among("ll", -1, -1, "", LovinsStemmer.methodObject), new Among("mm", -1, -1, "", LovinsStemmer.methodObject), new Among("nn", -1, -1, "", LovinsStemmer.methodObject), new Among("pp", -1, -1, "", LovinsStemmer.methodObject), new Among("rr", -1, -1, "", LovinsStemmer.methodObject), new Among("ss", -1, -1, "", LovinsStemmer.methodObject), new Among("tt", -1, -1, "", LovinsStemmer.methodObject) };
        a_3 = new Among[] { new Among("uad", -1, 18, "", LovinsStemmer.methodObject), new Among("vad", -1, 19, "", LovinsStemmer.methodObject), new Among("cid", -1, 20, "", LovinsStemmer.methodObject), new Among("lid", -1, 21, "", LovinsStemmer.methodObject), new Among("erid", -1, 22, "", LovinsStemmer.methodObject), new Among("pand", -1, 23, "", LovinsStemmer.methodObject), new Among("end", -1, 24, "", LovinsStemmer.methodObject), new Among("ond", -1, 25, "", LovinsStemmer.methodObject), new Among("lud", -1, 26, "", LovinsStemmer.methodObject), new Among("rud", -1, 27, "", LovinsStemmer.methodObject), new Among("ul", -1, 9, "", LovinsStemmer.methodObject), new Among("her", -1, 28, "", LovinsStemmer.methodObject), new Among("metr", -1, 7, "", LovinsStemmer.methodObject), new Among("istr", -1, 6, "", LovinsStemmer.methodObject), new Among("urs", -1, 5, "", LovinsStemmer.methodObject), new Among("uct", -1, 2, "", LovinsStemmer.methodObject), new Among("et", -1, 32, "", LovinsStemmer.methodObject), new Among("mit", -1, 29, "", LovinsStemmer.methodObject), new Among("ent", -1, 30, "", LovinsStemmer.methodObject), new Among("umpt", -1, 3, "", LovinsStemmer.methodObject), new Among("rpt", -1, 4, "", LovinsStemmer.methodObject), new Among("ert", -1, 31, "", LovinsStemmer.methodObject), new Among("yt", -1, 33, "", LovinsStemmer.methodObject), new Among("iev", -1, 1, "", LovinsStemmer.methodObject), new Among("olv", -1, 8, "", LovinsStemmer.methodObject), new Among("ax", -1, 14, "", LovinsStemmer.methodObject), new Among("ex", -1, 15, "", LovinsStemmer.methodObject), new Among("bex", 26, 10, "", LovinsStemmer.methodObject), new Among("dex", 26, 11, "", LovinsStemmer.methodObject), new Among("pex", 26, 12, "", LovinsStemmer.methodObject), new Among("tex", 26, 13, "", LovinsStemmer.methodObject), new Among("ix", -1, 16, "", LovinsStemmer.methodObject), new Among("lux", -1, 17, "", LovinsStemmer.methodObject), new Among("yz", -1, 34, "", LovinsStemmer.methodObject) };
    }
}
