package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class KpStemmer extends SnowballProgram
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
    private static final char[] g_v_WX;
    private static final char[] g_AOU;
    private static final char[] g_AIOU;
    private boolean B_GE_removed;
    private boolean B_stemmed;
    private boolean B_Y_found;
    private int I_p2;
    private int I_p1;
    private int I_x;
    private StringBuilder S_ch;
    
    public KpStemmer() {
        this.S_ch = new StringBuilder();
    }
    
    private void copy_from(final KpStemmer other) {
        this.B_GE_removed = other.B_GE_removed;
        this.B_stemmed = other.B_stemmed;
        this.B_Y_found = other.B_Y_found;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_x = other.I_x;
        this.S_ch = other.S_ch;
        super.copy_from(other);
    }
    
    private boolean r_R1() {
        this.I_x = this.cursor;
        return this.I_x >= this.I_p1;
    }
    
    private boolean r_R2() {
        this.I_x = this.cursor;
        return this.I_x >= this.I_p2;
    }
    
    private boolean r_V() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (!this.in_grouping_b(KpStemmer.g_v, 97, 121)) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(2, "ij")) {
                return false;
            }
        }
        this.cursor = this.limit - v_1;
        return true;
    }
    
    private boolean r_VX() {
        final int v_1 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (!this.in_grouping_b(KpStemmer.g_v, 97, 121)) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(2, "ij")) {
                return false;
            }
        }
        this.cursor = this.limit - v_1;
        return true;
    }
    
    private boolean r_C() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(2, "ij")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        if (!this.out_grouping_b(KpStemmer.g_v, 97, 121)) {
            return false;
        }
        this.cursor = this.limit - v_1;
        return true;
    }
    
    private boolean r_lengthen_V() {
        final int v_1 = this.limit - this.cursor;
        Label_0399: {
            if (this.out_grouping_b(KpStemmer.g_v_WX, 97, 121)) {
                this.ket = this.cursor;
                final int v_2 = this.limit - this.cursor;
                Label_0359: {
                    Label_0143: {
                        if (this.in_grouping_b(KpStemmer.g_AOU, 97, 117)) {
                            this.bra = this.cursor;
                            final int v_3 = this.limit - this.cursor;
                            final int v_4 = this.limit - this.cursor;
                            if (!this.out_grouping_b(KpStemmer.g_v, 97, 121)) {
                                this.cursor = this.limit - v_4;
                                if (this.cursor > this.limit_backward) {
                                    break Label_0143;
                                }
                            }
                            this.cursor = this.limit - v_3;
                            break Label_0359;
                        }
                    }
                    this.cursor = this.limit - v_2;
                    if (!this.eq_s_b(1, "e")) {
                        break Label_0399;
                    }
                    this.bra = this.cursor;
                    final int v_5 = this.limit - this.cursor;
                    final int v_6 = this.limit - this.cursor;
                    if (!this.out_grouping_b(KpStemmer.g_v, 97, 121)) {
                        this.cursor = this.limit - v_6;
                        if (this.cursor > this.limit_backward) {
                            break Label_0399;
                        }
                    }
                    final int v_7 = this.limit - this.cursor;
                    if (this.in_grouping_b(KpStemmer.g_AIOU, 97, 117)) {
                        break Label_0399;
                    }
                    this.cursor = this.limit - v_7;
                    final int v_8 = this.limit - this.cursor;
                    if (this.cursor > this.limit_backward) {
                        --this.cursor;
                        if (this.in_grouping_b(KpStemmer.g_AIOU, 97, 117)) {
                            if (this.out_grouping_b(KpStemmer.g_v, 97, 121)) {
                                break Label_0399;
                            }
                        }
                    }
                    this.cursor = this.limit - v_8;
                    this.cursor = this.limit - v_5;
                }
                this.S_ch = this.slice_to(this.S_ch);
                final int c = this.cursor;
                this.insert(this.cursor, this.cursor, this.S_ch);
                this.cursor = c;
            }
        }
        this.cursor = this.limit - v_1;
        return true;
    }
    
    private boolean r_Step_1() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(KpStemmer.a_0, 7);
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
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                final int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(1, "t")) {
                    if (this.r_R1()) {
                        return false;
                    }
                }
                this.cursor = this.limit - v_1;
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("ie");
                break;
            }
            case 4: {
                final int v_2 = this.limit - this.cursor;
                if (this.eq_s_b(2, "ar")) {
                    if (this.r_R1()) {
                        if (this.r_C()) {
                            this.bra = this.cursor;
                            this.slice_del();
                            if (this.r_lengthen_V()) {
                                break;
                            }
                        }
                    }
                }
                this.cursor = this.limit - v_2;
                if (this.eq_s_b(2, "er")) {
                    if (this.r_R1()) {
                        if (this.r_C()) {
                            this.bra = this.cursor;
                            this.slice_del();
                            break;
                        }
                    }
                }
                this.cursor = this.limit - v_2;
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("e");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_V()) {
                    return false;
                }
                this.slice_from("au");
                break;
            }
            case 6: {
                final int v_3 = this.limit - this.cursor;
                if (this.eq_s_b(3, "hed")) {
                    if (this.r_R1()) {
                        this.bra = this.cursor;
                        this.slice_from("heid");
                        break;
                    }
                }
                this.cursor = this.limit - v_3;
                if (this.eq_s_b(2, "nd")) {
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_3;
                if (this.eq_s_b(1, "d")) {
                    if (this.r_R1()) {
                        if (this.r_C()) {
                            this.bra = this.cursor;
                            this.slice_del();
                            break;
                        }
                    }
                }
                this.cursor = this.limit - v_3;
                final int v_4 = this.limit - this.cursor;
                Label_0575: {
                    if (!this.eq_s_b(1, "i")) {
                        this.cursor = this.limit - v_4;
                        if (!this.eq_s_b(1, "j")) {
                            break Label_0575;
                        }
                    }
                    if (this.r_V()) {
                        this.slice_del();
                        break;
                    }
                }
                this.cursor = this.limit - v_3;
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 7: {
                this.slice_from("nd");
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_2() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(KpStemmer.a_1, 11);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                final int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(2, "'t")) {
                    this.bra = this.cursor;
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(2, "et")) {
                    this.bra = this.cursor;
                    if (this.r_R1()) {
                        if (this.r_C()) {
                            this.slice_del();
                            break;
                        }
                    }
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(3, "rnt")) {
                    this.bra = this.cursor;
                    this.slice_from("rn");
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(1, "t")) {
                    this.bra = this.cursor;
                    if (this.r_R1()) {
                        if (this.r_VX()) {
                            this.slice_del();
                            break;
                        }
                    }
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(3, "ink")) {
                    this.bra = this.cursor;
                    this.slice_from("ing");
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(2, "mp")) {
                    this.bra = this.cursor;
                    this.slice_from("m");
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(1, "'")) {
                    this.bra = this.cursor;
                    if (this.r_R1()) {
                        this.slice_del();
                        break;
                    }
                }
                this.cursor = this.limit - v_1;
                this.bra = this.cursor;
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("g");
                break;
            }
            case 3: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("lijk");
                break;
            }
            case 4: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("isch");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 6: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("t");
                break;
            }
            case 7: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("s");
                break;
            }
            case 8: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("r");
                break;
            }
            case 9: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "l");
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 10: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "en");
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 11: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("ief");
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_3() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(KpStemmer.a_2, 14);
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
                this.slice_from("eer");
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 3: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 4: {
                this.slice_from("r");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 6: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("aar");
                break;
            }
            case 7: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "f");
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 8: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "g");
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
            case 9: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("t");
                break;
            }
            case 10: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("d");
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_4() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(KpStemmer.a_3, 16);
        Label_0341: {
            if (among_var != 0) {
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        break Label_0341;
                    }
                    case 1: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        this.slice_from("ie");
                        break;
                    }
                    case 2: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        this.slice_from("eer");
                        break;
                    }
                    case 3: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        this.slice_del();
                        break;
                    }
                    case 4: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        if (!this.r_V()) {
                            break Label_0341;
                        }
                        this.slice_from("n");
                        break;
                    }
                    case 5: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        if (!this.r_V()) {
                            break Label_0341;
                        }
                        this.slice_from("l");
                        break;
                    }
                    case 6: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        if (!this.r_V()) {
                            break Label_0341;
                        }
                        this.slice_from("r");
                        break;
                    }
                    case 7: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        this.slice_from("teer");
                        break;
                    }
                    case 8: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        this.slice_from("lijk");
                        break;
                    }
                    case 9: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        this.slice_del();
                        break;
                    }
                    case 10: {
                        if (!this.r_R1()) {
                            break Label_0341;
                        }
                        if (!this.r_C()) {
                            break Label_0341;
                        }
                        this.slice_del();
                        if (!this.r_lengthen_V()) {
                            break Label_0341;
                        }
                        break;
                    }
                }
                return true;
            }
        }
        this.cursor = this.limit - v_1;
        this.ket = this.cursor;
        among_var = this.find_among_b(KpStemmer.a_4, 3);
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
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                if (!this.r_lengthen_V()) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_7() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(KpStemmer.a_5, 3);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("k");
                break;
            }
            case 2: {
                this.slice_from("f");
                break;
            }
            case 3: {
                this.slice_from("p");
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_6() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(KpStemmer.a_6, 22);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("b");
                break;
            }
            case 2: {
                this.slice_from("c");
                break;
            }
            case 3: {
                this.slice_from("d");
                break;
            }
            case 4: {
                this.slice_from("f");
                break;
            }
            case 5: {
                this.slice_from("g");
                break;
            }
            case 6: {
                this.slice_from("h");
                break;
            }
            case 7: {
                this.slice_from("j");
                break;
            }
            case 8: {
                this.slice_from("k");
                break;
            }
            case 9: {
                this.slice_from("l");
                break;
            }
            case 10: {
                this.slice_from("m");
                break;
            }
            case 11: {
                this.slice_from("n");
                break;
            }
            case 12: {
                this.slice_from("p");
                break;
            }
            case 13: {
                this.slice_from("q");
                break;
            }
            case 14: {
                this.slice_from("r");
                break;
            }
            case 15: {
                this.slice_from("s");
                break;
            }
            case 16: {
                this.slice_from("t");
                break;
            }
            case 17: {
                this.slice_from("v");
                break;
            }
            case 18: {
                this.slice_from("w");
                break;
            }
            case 19: {
                this.slice_from("x");
                break;
            }
            case 20: {
                this.slice_from("z");
                break;
            }
            case 21: {
                this.slice_from("f");
                break;
            }
            case 22: {
                this.slice_from("s");
                break;
            }
        }
        return true;
    }
    
    private boolean r_Step_1c() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(KpStemmer.a_7, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        if (!this.r_C()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                final int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(1, "n")) {
                    if (this.r_R1()) {
                        return false;
                    }
                }
                this.cursor = this.limit - v_1;
                this.slice_del();
                break;
            }
            case 2: {
                final int v_2 = this.limit - this.cursor;
                if (this.eq_s_b(1, "h")) {
                    if (this.r_R1()) {
                        return false;
                    }
                }
                this.cursor = this.limit - v_2;
                this.slice_del();
                break;
            }
        }
        return true;
    }
    
    private boolean r_Lose_prefix() {
        this.bra = this.cursor;
        if (!this.eq_s(2, "ge")) {
            return false;
        }
        this.ket = this.cursor;
        final int v_1 = this.cursor;
        final int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = v_1;
        while (true) {
            final int v_2 = this.cursor;
            if (!this.in_grouping(KpStemmer.g_v, 97, 121)) {
                this.cursor = v_2;
                if (this.cursor >= this.limit) {
                    return false;
                }
                ++this.cursor;
            }
            else {
                this.cursor = v_2;
                while (true) {
                    final int v_3 = this.cursor;
                    if (this.out_grouping(KpStemmer.g_v, 97, 121)) {
                        this.cursor = v_3;
                        this.B_GE_removed = true;
                        this.slice_del();
                        return true;
                    }
                    this.cursor = v_3;
                    if (this.cursor >= this.limit) {
                        return false;
                    }
                    ++this.cursor;
                }
            }
        }
    }
    
    private boolean r_Lose_infix() {
        if (this.cursor >= this.limit) {
            return false;
        }
        ++this.cursor;
        while (true) {
            this.bra = this.cursor;
            if (!this.eq_s(2, "ge")) {
                if (this.cursor >= this.limit) {
                    return false;
                }
                ++this.cursor;
            }
            else {
                this.ket = this.cursor;
                final int v_2 = this.cursor;
                final int c = this.cursor + 3;
                if (0 > c || c > this.limit) {
                    return false;
                }
                this.cursor = c;
                this.cursor = v_2;
                while (true) {
                    final int v_3 = this.cursor;
                    if (!this.in_grouping(KpStemmer.g_v, 97, 121)) {
                        this.cursor = v_3;
                        if (this.cursor >= this.limit) {
                            return false;
                        }
                        ++this.cursor;
                    }
                    else {
                        this.cursor = v_3;
                        while (true) {
                            final int v_4 = this.cursor;
                            if (this.out_grouping(KpStemmer.g_v, 97, 121)) {
                                this.cursor = v_4;
                                this.B_GE_removed = true;
                                this.slice_del();
                                return true;
                            }
                            this.cursor = v_4;
                            if (this.cursor >= this.limit) {
                                return false;
                            }
                            ++this.cursor;
                        }
                    }
                }
            }
        }
    }
    
    private boolean r_measure() {
        final int v_1 = this.cursor;
        this.cursor = this.limit;
        this.I_p1 = this.cursor;
        this.I_p2 = this.cursor;
        this.cursor = v_1;
        final int v_2 = this.cursor;
        while (this.out_grouping(KpStemmer.g_v, 97, 121)) {}
        int v_3 = 1;
        int v_4;
        while (true) {
            v_4 = this.cursor;
            final int v_5 = this.cursor;
            if (!this.eq_s(2, "ij")) {
                this.cursor = v_5;
                if (!this.in_grouping(KpStemmer.g_v, 97, 121)) {
                    break;
                }
            }
            --v_3;
        }
        this.cursor = v_4;
        if (v_3 <= 0) {
            if (this.out_grouping(KpStemmer.g_v, 97, 121)) {
                this.I_p1 = this.cursor;
                while (this.out_grouping(KpStemmer.g_v, 97, 121)) {}
                int v_6 = 1;
                int v_7;
                while (true) {
                    v_7 = this.cursor;
                    final int v_8 = this.cursor;
                    if (!this.eq_s(2, "ij")) {
                        this.cursor = v_8;
                        if (!this.in_grouping(KpStemmer.g_v, 97, 121)) {
                            break;
                        }
                    }
                    --v_6;
                }
                this.cursor = v_7;
                if (v_6 <= 0) {
                    if (this.out_grouping(KpStemmer.g_v, 97, 121)) {
                        this.I_p2 = this.cursor;
                    }
                }
            }
        }
        this.cursor = v_2;
        return true;
    }
    
    @Override
    public boolean stem() {
        this.B_Y_found = false;
        this.B_stemmed = false;
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
                if (this.in_grouping(KpStemmer.g_v, 97, 121)) {
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
        if (!this.r_measure()) {
            return false;
        }
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_5 = this.limit - this.cursor;
        if (this.r_Step_1()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_5;
        final int v_6 = this.limit - this.cursor;
        if (this.r_Step_2()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_6;
        final int v_7 = this.limit - this.cursor;
        if (this.r_Step_3()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_7;
        final int v_8 = this.limit - this.cursor;
        if (this.r_Step_4()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_8;
        this.cursor = this.limit_backward;
        this.B_GE_removed = false;
        final int v_9 = this.cursor;
        final int v_10 = this.cursor;
        if (this.r_Lose_prefix()) {
            this.cursor = v_10;
            if (!this.r_measure()) {}
        }
        this.cursor = v_9;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_11 = this.limit - this.cursor;
        if (this.B_GE_removed) {
            if (!this.r_Step_1c()) {}
        }
        this.cursor = this.limit - v_11;
        this.cursor = this.limit_backward;
        this.B_GE_removed = false;
        final int v_12 = this.cursor;
        final int v_13 = this.cursor;
        if (this.r_Lose_infix()) {
            this.cursor = v_13;
            if (!this.r_measure()) {}
        }
        this.cursor = v_12;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_14 = this.limit - this.cursor;
        if (this.B_GE_removed) {
            if (!this.r_Step_1c()) {}
        }
        this.cursor = this.limit - v_14;
        this.cursor = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_15 = this.limit - this.cursor;
        if (this.r_Step_7()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_15;
        final int v_16 = this.limit - this.cursor;
        if (this.B_stemmed || this.B_GE_removed) {
            if (!this.r_Step_6()) {}
        }
        this.cursor = this.limit - v_16;
        this.cursor = this.limit_backward;
        final int v_17 = this.cursor;
        if (this.B_Y_found) {
            int v_18 = 0;
        Block_19:
            while (true) {
                v_18 = this.cursor;
                while (true) {
                    final int v_19 = this.cursor;
                    this.bra = this.cursor;
                    if (this.eq_s(1, "Y")) {
                        this.ket = this.cursor;
                        this.cursor = v_19;
                        this.slice_from("y");
                        break;
                    }
                    this.cursor = v_19;
                    if (this.cursor >= this.limit) {
                        break Block_19;
                    }
                    ++this.cursor;
                }
            }
            this.cursor = v_18;
        }
        this.cursor = v_17;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof KpStemmer;
    }
    
    @Override
    public int hashCode() {
        return KpStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("nde", -1, 7, "", KpStemmer.methodObject), new Among("en", -1, 6, "", KpStemmer.methodObject), new Among("s", -1, 2, "", KpStemmer.methodObject), new Among("'s", 2, 1, "", KpStemmer.methodObject), new Among("es", 2, 4, "", KpStemmer.methodObject), new Among("ies", 4, 3, "", KpStemmer.methodObject), new Among("aus", 2, 5, "", KpStemmer.methodObject) };
        a_1 = new Among[] { new Among("de", -1, 5, "", KpStemmer.methodObject), new Among("ge", -1, 2, "", KpStemmer.methodObject), new Among("ische", -1, 4, "", KpStemmer.methodObject), new Among("je", -1, 1, "", KpStemmer.methodObject), new Among("lijke", -1, 3, "", KpStemmer.methodObject), new Among("le", -1, 9, "", KpStemmer.methodObject), new Among("ene", -1, 10, "", KpStemmer.methodObject), new Among("re", -1, 8, "", KpStemmer.methodObject), new Among("se", -1, 7, "", KpStemmer.methodObject), new Among("te", -1, 6, "", KpStemmer.methodObject), new Among("ieve", -1, 11, "", KpStemmer.methodObject) };
        a_2 = new Among[] { new Among("heid", -1, 3, "", KpStemmer.methodObject), new Among("fie", -1, 7, "", KpStemmer.methodObject), new Among("gie", -1, 8, "", KpStemmer.methodObject), new Among("atie", -1, 1, "", KpStemmer.methodObject), new Among("isme", -1, 5, "", KpStemmer.methodObject), new Among("ing", -1, 5, "", KpStemmer.methodObject), new Among("arij", -1, 6, "", KpStemmer.methodObject), new Among("erij", -1, 5, "", KpStemmer.methodObject), new Among("sel", -1, 3, "", KpStemmer.methodObject), new Among("rder", -1, 4, "", KpStemmer.methodObject), new Among("ster", -1, 3, "", KpStemmer.methodObject), new Among("iteit", -1, 2, "", KpStemmer.methodObject), new Among("dst", -1, 10, "", KpStemmer.methodObject), new Among("tst", -1, 9, "", KpStemmer.methodObject) };
        a_3 = new Among[] { new Among("end", -1, 10, "", KpStemmer.methodObject), new Among("atief", -1, 2, "", KpStemmer.methodObject), new Among("erig", -1, 10, "", KpStemmer.methodObject), new Among("achtig", -1, 9, "", KpStemmer.methodObject), new Among("ioneel", -1, 1, "", KpStemmer.methodObject), new Among("baar", -1, 3, "", KpStemmer.methodObject), new Among("laar", -1, 5, "", KpStemmer.methodObject), new Among("naar", -1, 4, "", KpStemmer.methodObject), new Among("raar", -1, 6, "", KpStemmer.methodObject), new Among("eriger", -1, 10, "", KpStemmer.methodObject), new Among("achtiger", -1, 9, "", KpStemmer.methodObject), new Among("lijker", -1, 8, "", KpStemmer.methodObject), new Among("tant", -1, 7, "", KpStemmer.methodObject), new Among("erigst", -1, 10, "", KpStemmer.methodObject), new Among("achtigst", -1, 9, "", KpStemmer.methodObject), new Among("lijkst", -1, 8, "", KpStemmer.methodObject) };
        a_4 = new Among[] { new Among("ig", -1, 1, "", KpStemmer.methodObject), new Among("iger", -1, 1, "", KpStemmer.methodObject), new Among("igst", -1, 1, "", KpStemmer.methodObject) };
        a_5 = new Among[] { new Among("ft", -1, 2, "", KpStemmer.methodObject), new Among("kt", -1, 1, "", KpStemmer.methodObject), new Among("pt", -1, 3, "", KpStemmer.methodObject) };
        a_6 = new Among[] { new Among("bb", -1, 1, "", KpStemmer.methodObject), new Among("cc", -1, 2, "", KpStemmer.methodObject), new Among("dd", -1, 3, "", KpStemmer.methodObject), new Among("ff", -1, 4, "", KpStemmer.methodObject), new Among("gg", -1, 5, "", KpStemmer.methodObject), new Among("hh", -1, 6, "", KpStemmer.methodObject), new Among("jj", -1, 7, "", KpStemmer.methodObject), new Among("kk", -1, 8, "", KpStemmer.methodObject), new Among("ll", -1, 9, "", KpStemmer.methodObject), new Among("mm", -1, 10, "", KpStemmer.methodObject), new Among("nn", -1, 11, "", KpStemmer.methodObject), new Among("pp", -1, 12, "", KpStemmer.methodObject), new Among("qq", -1, 13, "", KpStemmer.methodObject), new Among("rr", -1, 14, "", KpStemmer.methodObject), new Among("ss", -1, 15, "", KpStemmer.methodObject), new Among("tt", -1, 16, "", KpStemmer.methodObject), new Among("v", -1, 21, "", KpStemmer.methodObject), new Among("vv", 16, 17, "", KpStemmer.methodObject), new Among("ww", -1, 18, "", KpStemmer.methodObject), new Among("xx", -1, 19, "", KpStemmer.methodObject), new Among("z", -1, 22, "", KpStemmer.methodObject), new Among("zz", 20, 20, "", KpStemmer.methodObject) };
        a_7 = new Among[] { new Among("d", -1, 1, "", KpStemmer.methodObject), new Among("t", -1, 2, "", KpStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010', '\u0001' };
        g_v_WX = new char[] { '\u0011', 'A', '\u00d0', '\u0001' };
        g_AOU = new char[] { '\u0001', '@', '\u0010' };
        g_AIOU = new char[] { '\u0001', 'A', '\u0010' };
    }
}
