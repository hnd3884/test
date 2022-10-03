package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class TurkishStemmer extends SnowballProgram
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
    private static final Among[] a_12;
    private static final Among[] a_13;
    private static final Among[] a_14;
    private static final Among[] a_15;
    private static final Among[] a_16;
    private static final Among[] a_17;
    private static final Among[] a_18;
    private static final Among[] a_19;
    private static final Among[] a_20;
    private static final Among[] a_21;
    private static final Among[] a_22;
    private static final Among[] a_23;
    private static final char[] g_vowel;
    private static final char[] g_U;
    private static final char[] g_vowel1;
    private static final char[] g_vowel2;
    private static final char[] g_vowel3;
    private static final char[] g_vowel4;
    private static final char[] g_vowel5;
    private static final char[] g_vowel6;
    private boolean B_continue_stemming_noun_suffixes;
    private int I_strlen;
    
    private void copy_from(final TurkishStemmer other) {
        this.B_continue_stemming_noun_suffixes = other.B_continue_stemming_noun_suffixes;
        this.I_strlen = other.I_strlen;
        super.copy_from(other);
    }
    
    private boolean r_check_vowel_harmony() {
        final int v_1 = this.limit - this.cursor;
        while (true) {
            final int v_2 = this.limit - this.cursor;
            if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                this.cursor = this.limit - v_2;
                final int v_3 = this.limit - this.cursor;
                Label_0916: {
                    if (this.eq_s_b(1, "a")) {
                        while (true) {
                            final int v_4 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel1, 97, 305)) {
                                this.cursor = this.limit - v_4;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_4;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (this.eq_s_b(1, "e")) {
                        while (true) {
                            final int v_5 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel2, 101, 252)) {
                                this.cursor = this.limit - v_5;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_5;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (this.eq_s_b(1, "\u0131")) {
                        while (true) {
                            final int v_6 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel3, 97, 305)) {
                                this.cursor = this.limit - v_6;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_6;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (this.eq_s_b(1, "i")) {
                        while (true) {
                            final int v_7 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel4, 101, 105)) {
                                this.cursor = this.limit - v_7;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_7;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (this.eq_s_b(1, "o")) {
                        while (true) {
                            final int v_8 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel5, 111, 117)) {
                                this.cursor = this.limit - v_8;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_8;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (this.eq_s_b(1, "\u00f6")) {
                        while (true) {
                            final int v_9 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel6, 246, 252)) {
                                this.cursor = this.limit - v_9;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_9;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (this.eq_s_b(1, "u")) {
                        while (true) {
                            final int v_10 = this.limit - this.cursor;
                            if (this.in_grouping_b(TurkishStemmer.g_vowel5, 111, 117)) {
                                this.cursor = this.limit - v_10;
                                break Label_0916;
                            }
                            this.cursor = this.limit - v_10;
                            if (this.cursor <= this.limit_backward) {
                                break;
                            }
                            --this.cursor;
                        }
                    }
                    this.cursor = this.limit - v_3;
                    if (!this.eq_s_b(1, "\u00fc")) {
                        return false;
                    }
                    while (true) {
                        final int v_11 = this.limit - this.cursor;
                        if (this.in_grouping_b(TurkishStemmer.g_vowel6, 246, 252)) {
                            this.cursor = this.limit - v_11;
                            break;
                        }
                        this.cursor = this.limit - v_11;
                        if (this.cursor <= this.limit_backward) {
                            return false;
                        }
                        --this.cursor;
                    }
                }
                this.cursor = this.limit - v_1;
                return true;
            }
            this.cursor = this.limit - v_2;
            if (this.cursor <= this.limit_backward) {
                return false;
            }
            --this.cursor;
        }
    }
    
    private boolean r_mark_suffix_with_optional_n_consonant() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                final int v_3 = this.limit - this.cursor;
                if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        final int v_4 = this.limit - this.cursor;
        final int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        final int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        final int v_7 = this.limit - this.cursor;
        if (!this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }
    
    private boolean r_mark_suffix_with_optional_s_consonant() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                final int v_3 = this.limit - this.cursor;
                if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        final int v_4 = this.limit - this.cursor;
        final int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        final int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        final int v_7 = this.limit - this.cursor;
        if (!this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }
    
    private boolean r_mark_suffix_with_optional_y_consonant() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "y")) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                final int v_3 = this.limit - this.cursor;
                if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        final int v_4 = this.limit - this.cursor;
        final int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "y")) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        final int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        final int v_7 = this.limit - this.cursor;
        if (!this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }
    
    private boolean r_mark_suffix_with_optional_U_vowel() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (this.in_grouping_b(TurkishStemmer.g_U, 105, 305)) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                final int v_3 = this.limit - this.cursor;
                if (this.out_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        final int v_4 = this.limit - this.cursor;
        final int v_5 = this.limit - this.cursor;
        if (this.in_grouping_b(TurkishStemmer.g_U, 105, 305)) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        final int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        final int v_7 = this.limit - this.cursor;
        if (!this.out_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }
    
    private boolean r_mark_possessives() {
        return this.find_among_b(TurkishStemmer.a_0, 10) != 0 && this.r_mark_suffix_with_optional_U_vowel();
    }
    
    private boolean r_mark_sU() {
        return this.r_check_vowel_harmony() && this.in_grouping_b(TurkishStemmer.g_U, 105, 305) && this.r_mark_suffix_with_optional_s_consonant();
    }
    
    private boolean r_mark_lArI() {
        return this.find_among_b(TurkishStemmer.a_1, 2) != 0;
    }
    
    private boolean r_mark_yU() {
        return this.r_check_vowel_harmony() && this.in_grouping_b(TurkishStemmer.g_U, 105, 305) && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_nU() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_2, 4) != 0;
    }
    
    private boolean r_mark_nUn() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_3, 4) != 0 && this.r_mark_suffix_with_optional_n_consonant();
    }
    
    private boolean r_mark_yA() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_4, 2) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_nA() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_5, 2) != 0;
    }
    
    private boolean r_mark_DA() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_6, 4) != 0;
    }
    
    private boolean r_mark_ndA() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_7, 2) != 0;
    }
    
    private boolean r_mark_DAn() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_8, 4) != 0;
    }
    
    private boolean r_mark_ndAn() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_9, 2) != 0;
    }
    
    private boolean r_mark_ylA() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_10, 2) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_ki() {
        return this.eq_s_b(2, "ki");
    }
    
    private boolean r_mark_ncA() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_11, 2) != 0 && this.r_mark_suffix_with_optional_n_consonant();
    }
    
    private boolean r_mark_yUm() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_12, 4) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_sUn() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_13, 4) != 0;
    }
    
    private boolean r_mark_yUz() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_14, 4) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_sUnUz() {
        return this.find_among_b(TurkishStemmer.a_15, 4) != 0;
    }
    
    private boolean r_mark_lAr() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_16, 2) != 0;
    }
    
    private boolean r_mark_nUz() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_17, 4) != 0;
    }
    
    private boolean r_mark_DUr() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_18, 8) != 0;
    }
    
    private boolean r_mark_cAsInA() {
        return this.find_among_b(TurkishStemmer.a_19, 2) != 0;
    }
    
    private boolean r_mark_yDU() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_20, 32) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_ysA() {
        return this.find_among_b(TurkishStemmer.a_21, 8) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_ymUs_() {
        return this.r_check_vowel_harmony() && this.find_among_b(TurkishStemmer.a_22, 4) != 0 && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_mark_yken() {
        return this.eq_s_b(3, "ken") && this.r_mark_suffix_with_optional_y_consonant();
    }
    
    private boolean r_stem_nominal_verb_suffixes() {
        this.ket = this.cursor;
        this.B_continue_stemming_noun_suffixes = true;
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        Label_0717: {
            if (!this.r_mark_ymUs_()) {
                this.cursor = this.limit - v_2;
                if (!this.r_mark_yDU()) {
                    this.cursor = this.limit - v_2;
                    if (!this.r_mark_ysA()) {
                        this.cursor = this.limit - v_2;
                        if (!this.r_mark_yken()) {
                            this.cursor = this.limit - v_1;
                            if (this.r_mark_cAsInA()) {
                                final int v_3 = this.limit - this.cursor;
                                if (!this.r_mark_sUnUz()) {
                                    this.cursor = this.limit - v_3;
                                    if (!this.r_mark_lAr()) {
                                        this.cursor = this.limit - v_3;
                                        if (!this.r_mark_yUm()) {
                                            this.cursor = this.limit - v_3;
                                            if (!this.r_mark_sUn()) {
                                                this.cursor = this.limit - v_3;
                                                if (!this.r_mark_yUz()) {
                                                    this.cursor = this.limit - v_3;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (this.r_mark_ymUs_()) {
                                    break Label_0717;
                                }
                            }
                            this.cursor = this.limit - v_1;
                            if (!this.r_mark_lAr()) {
                                this.cursor = this.limit - v_1;
                                if (this.r_mark_nUz()) {
                                    final int v_4 = this.limit - this.cursor;
                                    if (this.r_mark_yDU()) {
                                        break Label_0717;
                                    }
                                    this.cursor = this.limit - v_4;
                                    if (this.r_mark_ysA()) {
                                        break Label_0717;
                                    }
                                }
                                this.cursor = this.limit - v_1;
                                final int v_5 = this.limit - this.cursor;
                                if (!this.r_mark_sUnUz()) {
                                    this.cursor = this.limit - v_5;
                                    if (!this.r_mark_yUz()) {
                                        this.cursor = this.limit - v_5;
                                        if (!this.r_mark_sUn()) {
                                            this.cursor = this.limit - v_5;
                                            if (!this.r_mark_yUm()) {
                                                this.cursor = this.limit - v_1;
                                                if (!this.r_mark_DUr()) {
                                                    return false;
                                                }
                                                this.bra = this.cursor;
                                                this.slice_del();
                                                final int v_6 = this.limit - this.cursor;
                                                this.ket = this.cursor;
                                                final int v_7 = this.limit - this.cursor;
                                                if (!this.r_mark_sUnUz()) {
                                                    this.cursor = this.limit - v_7;
                                                    if (!this.r_mark_lAr()) {
                                                        this.cursor = this.limit - v_7;
                                                        if (!this.r_mark_yUm()) {
                                                            this.cursor = this.limit - v_7;
                                                            if (!this.r_mark_sUn()) {
                                                                this.cursor = this.limit - v_7;
                                                                if (!this.r_mark_yUz()) {
                                                                    this.cursor = this.limit - v_7;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (!this.r_mark_ymUs_()) {
                                                    this.cursor = this.limit - v_6;
                                                }
                                                break Label_0717;
                                            }
                                        }
                                    }
                                }
                                this.bra = this.cursor;
                                this.slice_del();
                                final int v_8 = this.limit - this.cursor;
                                this.ket = this.cursor;
                                if (!this.r_mark_ymUs_()) {
                                    this.cursor = this.limit - v_8;
                                }
                            }
                            else {
                                this.bra = this.cursor;
                                this.slice_del();
                                final int v_9 = this.limit - this.cursor;
                                this.ket = this.cursor;
                                final int v_10 = this.limit - this.cursor;
                                if (!this.r_mark_DUr()) {
                                    this.cursor = this.limit - v_10;
                                    if (!this.r_mark_yDU()) {
                                        this.cursor = this.limit - v_10;
                                        if (!this.r_mark_ysA()) {
                                            this.cursor = this.limit - v_10;
                                            if (!this.r_mark_ymUs_()) {
                                                this.cursor = this.limit - v_9;
                                            }
                                        }
                                    }
                                }
                                this.B_continue_stemming_noun_suffixes = false;
                            }
                        }
                    }
                }
            }
        }
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }
    
    private boolean r_stem_suffix_chain_before_ki() {
        this.ket = this.cursor;
        if (!this.r_mark_ki()) {
            return false;
        }
        final int v_1 = this.limit - this.cursor;
        if (!this.r_mark_DA()) {
            this.cursor = this.limit - v_1;
            if (!this.r_mark_nUn()) {
                this.cursor = this.limit - v_1;
                if (!this.r_mark_ndA()) {
                    return false;
                }
                final int v_2 = this.limit - this.cursor;
                if (!this.r_mark_lArI()) {
                    this.cursor = this.limit - v_2;
                    if (!this.r_mark_sU()) {
                        this.cursor = this.limit - v_2;
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            return false;
                        }
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        final int v_3 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        if (!this.r_mark_lAr()) {
                            this.cursor = this.limit - v_3;
                        }
                        else {
                            this.bra = this.cursor;
                            this.slice_del();
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_3;
                            }
                        }
                    }
                }
                else {
                    this.bra = this.cursor;
                    this.slice_del();
                }
            }
            else {
                this.bra = this.cursor;
                this.slice_del();
                final int v_4 = this.limit - this.cursor;
                this.ket = this.cursor;
                final int v_5 = this.limit - this.cursor;
                if (!this.r_mark_lArI()) {
                    this.cursor = this.limit - v_5;
                    this.ket = this.cursor;
                    final int v_6 = this.limit - this.cursor;
                    if (!this.r_mark_possessives()) {
                        this.cursor = this.limit - v_6;
                        if (!this.r_mark_sU()) {
                            this.cursor = this.limit - v_5;
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_4;
                                return true;
                            }
                            return true;
                        }
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    final int v_7 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (!this.r_mark_lAr()) {
                        this.cursor = this.limit - v_7;
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            this.cursor = this.limit - v_7;
                        }
                    }
                }
                else {
                    this.bra = this.cursor;
                    this.slice_del();
                }
            }
        }
        else {
            this.bra = this.cursor;
            this.slice_del();
            final int v_8 = this.limit - this.cursor;
            this.ket = this.cursor;
            final int v_9 = this.limit - this.cursor;
            if (!this.r_mark_lAr()) {
                this.cursor = this.limit - v_9;
                if (!this.r_mark_possessives()) {
                    this.cursor = this.limit - v_8;
                }
                else {
                    this.bra = this.cursor;
                    this.slice_del();
                    final int v_10 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (!this.r_mark_lAr()) {
                        this.cursor = this.limit - v_10;
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            this.cursor = this.limit - v_10;
                        }
                    }
                }
            }
            else {
                this.bra = this.cursor;
                this.slice_del();
                final int v_11 = this.limit - this.cursor;
                if (!this.r_stem_suffix_chain_before_ki()) {
                    this.cursor = this.limit - v_11;
                }
            }
        }
        return true;
    }
    
    private boolean r_stem_noun_suffixes() {
        final int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.r_mark_lAr()) {
            this.cursor = this.limit - v_1;
            this.ket = this.cursor;
            if (!this.r_mark_ncA()) {
                this.cursor = this.limit - v_1;
                this.ket = this.cursor;
                final int v_2 = this.limit - this.cursor;
                Label_0595: {
                    if (!this.r_mark_ndA()) {
                        this.cursor = this.limit - v_2;
                        if (!this.r_mark_nA()) {
                            break Label_0595;
                        }
                    }
                    final int v_3 = this.limit - this.cursor;
                    if (this.r_mark_lArI()) {
                        this.bra = this.cursor;
                        this.slice_del();
                        return true;
                    }
                    this.cursor = this.limit - v_3;
                    if (!this.r_mark_sU()) {
                        this.cursor = this.limit - v_3;
                        if (this.r_stem_suffix_chain_before_ki()) {
                            return true;
                        }
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        final int v_4 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        if (!this.r_mark_lAr()) {
                            this.cursor = this.limit - v_4;
                            return true;
                        }
                        this.bra = this.cursor;
                        this.slice_del();
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            this.cursor = this.limit - v_4;
                            return true;
                        }
                        return true;
                    }
                }
                this.cursor = this.limit - v_1;
                this.ket = this.cursor;
                final int v_5 = this.limit - this.cursor;
                Label_0776: {
                    if (!this.r_mark_ndAn()) {
                        this.cursor = this.limit - v_5;
                        if (!this.r_mark_nU()) {
                            break Label_0776;
                        }
                    }
                    final int v_6 = this.limit - this.cursor;
                    if (!this.r_mark_sU()) {
                        this.cursor = this.limit - v_6;
                        if (this.r_mark_lArI()) {
                            return true;
                        }
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        final int v_7 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        if (!this.r_mark_lAr()) {
                            this.cursor = this.limit - v_7;
                            return true;
                        }
                        this.bra = this.cursor;
                        this.slice_del();
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            this.cursor = this.limit - v_7;
                            return true;
                        }
                        return true;
                    }
                }
                this.cursor = this.limit - v_1;
                this.ket = this.cursor;
                if (!this.r_mark_DAn()) {
                    this.cursor = this.limit - v_1;
                    this.ket = this.cursor;
                    final int v_8 = this.limit - this.cursor;
                    if (!this.r_mark_nUn()) {
                        this.cursor = this.limit - v_8;
                        if (!this.r_mark_ylA()) {
                            this.cursor = this.limit - v_1;
                            this.ket = this.cursor;
                            if (this.r_mark_lArI()) {
                                this.bra = this.cursor;
                                this.slice_del();
                                return true;
                            }
                            this.cursor = this.limit - v_1;
                            if (this.r_stem_suffix_chain_before_ki()) {
                                return true;
                            }
                            this.cursor = this.limit - v_1;
                            this.ket = this.cursor;
                            final int v_9 = this.limit - this.cursor;
                            if (!this.r_mark_DA()) {
                                this.cursor = this.limit - v_9;
                                if (!this.r_mark_yU()) {
                                    this.cursor = this.limit - v_9;
                                    if (!this.r_mark_yA()) {
                                        this.cursor = this.limit - v_1;
                                        this.ket = this.cursor;
                                        final int v_10 = this.limit - this.cursor;
                                        if (!this.r_mark_possessives()) {
                                            this.cursor = this.limit - v_10;
                                            if (!this.r_mark_sU()) {
                                                return false;
                                            }
                                        }
                                        this.bra = this.cursor;
                                        this.slice_del();
                                        final int v_11 = this.limit - this.cursor;
                                        this.ket = this.cursor;
                                        if (!this.r_mark_lAr()) {
                                            this.cursor = this.limit - v_11;
                                            return true;
                                        }
                                        this.bra = this.cursor;
                                        this.slice_del();
                                        if (!this.r_stem_suffix_chain_before_ki()) {
                                            this.cursor = this.limit - v_11;
                                            return true;
                                        }
                                        return true;
                                    }
                                }
                            }
                            this.bra = this.cursor;
                            this.slice_del();
                            final int v_12 = this.limit - this.cursor;
                            this.ket = this.cursor;
                            final int v_13 = this.limit - this.cursor;
                            if (!this.r_mark_possessives()) {
                                this.cursor = this.limit - v_13;
                                if (!this.r_mark_lAr()) {
                                    this.cursor = this.limit - v_12;
                                    return true;
                                }
                            }
                            else {
                                this.bra = this.cursor;
                                this.slice_del();
                                final int v_14 = this.limit - this.cursor;
                                this.ket = this.cursor;
                                if (!this.r_mark_lAr()) {
                                    this.cursor = this.limit - v_14;
                                }
                            }
                            this.bra = this.cursor;
                            this.slice_del();
                            this.ket = this.cursor;
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_12;
                                return true;
                            }
                            return true;
                        }
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    final int v_15 = this.limit - this.cursor;
                    final int v_16 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (this.r_mark_lAr()) {
                        this.bra = this.cursor;
                        this.slice_del();
                        if (this.r_stem_suffix_chain_before_ki()) {
                            return true;
                        }
                    }
                    this.cursor = this.limit - v_16;
                    this.ket = this.cursor;
                    final int v_17 = this.limit - this.cursor;
                    if (!this.r_mark_possessives()) {
                        this.cursor = this.limit - v_17;
                        if (!this.r_mark_sU()) {
                            this.cursor = this.limit - v_16;
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_15;
                                return true;
                            }
                            return true;
                        }
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    final int v_18 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (!this.r_mark_lAr()) {
                        this.cursor = this.limit - v_18;
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            this.cursor = this.limit - v_18;
                        }
                    }
                }
                else {
                    this.bra = this.cursor;
                    this.slice_del();
                    final int v_19 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    final int v_20 = this.limit - this.cursor;
                    if (!this.r_mark_possessives()) {
                        this.cursor = this.limit - v_20;
                        if (!this.r_mark_lAr()) {
                            this.cursor = this.limit - v_20;
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_19;
                            }
                        }
                        else {
                            this.bra = this.cursor;
                            this.slice_del();
                            final int v_21 = this.limit - this.cursor;
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_21;
                            }
                        }
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        final int v_22 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        if (!this.r_mark_lAr()) {
                            this.cursor = this.limit - v_22;
                        }
                        else {
                            this.bra = this.cursor;
                            this.slice_del();
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_22;
                            }
                        }
                    }
                }
            }
            else {
                this.bra = this.cursor;
                this.slice_del();
                final int v_23 = this.limit - this.cursor;
                final int v_24 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.r_mark_lArI()) {
                    this.cursor = this.limit - v_24;
                    this.ket = this.cursor;
                    final int v_25 = this.limit - this.cursor;
                    if (!this.r_mark_possessives()) {
                        this.cursor = this.limit - v_25;
                        if (!this.r_mark_sU()) {
                            this.cursor = this.limit - v_24;
                            this.ket = this.cursor;
                            if (!this.r_mark_lAr()) {
                                this.cursor = this.limit - v_23;
                                return true;
                            }
                            this.bra = this.cursor;
                            this.slice_del();
                            if (!this.r_stem_suffix_chain_before_ki()) {
                                this.cursor = this.limit - v_23;
                                return true;
                            }
                            return true;
                        }
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    final int v_26 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (!this.r_mark_lAr()) {
                        this.cursor = this.limit - v_26;
                    }
                    else {
                        this.bra = this.cursor;
                        this.slice_del();
                        if (!this.r_stem_suffix_chain_before_ki()) {
                            this.cursor = this.limit - v_26;
                        }
                    }
                }
                else {
                    this.bra = this.cursor;
                    this.slice_del();
                }
            }
        }
        else {
            this.bra = this.cursor;
            this.slice_del();
            final int v_27 = this.limit - this.cursor;
            if (!this.r_stem_suffix_chain_before_ki()) {
                this.cursor = this.limit - v_27;
            }
        }
        return true;
    }
    
    private boolean r_post_process_last_consonants() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(TurkishStemmer.a_23, 4);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("p");
                break;
            }
            case 2: {
                this.slice_from("\u00e7");
                break;
            }
            case 3: {
                this.slice_from("t");
                break;
            }
            case 4: {
                this.slice_from("k");
                break;
            }
        }
        return true;
    }
    
    private boolean r_append_U_to_stems_ending_with_d_or_g() {
        final int v_1 = this.limit - this.cursor;
        final int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "d")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "g")) {
                return false;
            }
        }
        this.cursor = this.limit - v_1;
        final int v_3 = this.limit - this.cursor;
        final int v_4 = this.limit - this.cursor;
        while (true) {
            final int v_5 = this.limit - this.cursor;
            if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                this.cursor = this.limit - v_5;
                final int v_6 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "a")) {
                    this.cursor = this.limit - v_6;
                    if (!this.eq_s_b(1, "\u0131")) {
                        break;
                    }
                }
                this.cursor = this.limit - v_4;
                final int c = this.cursor;
                this.insert(this.cursor, this.cursor, "\u0131");
                this.cursor = c;
                return true;
            }
            this.cursor = this.limit - v_5;
            if (this.cursor <= this.limit_backward) {
                break;
            }
            --this.cursor;
        }
        this.cursor = this.limit - v_3;
        final int v_7 = this.limit - this.cursor;
        while (true) {
            final int v_8 = this.limit - this.cursor;
            if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                this.cursor = this.limit - v_8;
                final int v_9 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "e")) {
                    this.cursor = this.limit - v_9;
                    if (!this.eq_s_b(1, "i")) {
                        break;
                    }
                }
                this.cursor = this.limit - v_7;
                final int c = this.cursor;
                this.insert(this.cursor, this.cursor, "i");
                this.cursor = c;
                return true;
            }
            this.cursor = this.limit - v_8;
            if (this.cursor <= this.limit_backward) {
                break;
            }
            --this.cursor;
        }
        this.cursor = this.limit - v_3;
        final int v_10 = this.limit - this.cursor;
        while (true) {
            final int v_11 = this.limit - this.cursor;
            if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                this.cursor = this.limit - v_11;
                final int v_12 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "o")) {
                    this.cursor = this.limit - v_12;
                    if (!this.eq_s_b(1, "u")) {
                        break;
                    }
                }
                this.cursor = this.limit - v_10;
                final int c = this.cursor;
                this.insert(this.cursor, this.cursor, "u");
                this.cursor = c;
                return true;
            }
            this.cursor = this.limit - v_11;
            if (this.cursor <= this.limit_backward) {
                break;
            }
            --this.cursor;
        }
        this.cursor = this.limit - v_3;
        final int v_13 = this.limit - this.cursor;
        while (true) {
            final int v_14 = this.limit - this.cursor;
            if (this.in_grouping_b(TurkishStemmer.g_vowel, 97, 305)) {
                this.cursor = this.limit - v_14;
                final int v_15 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "\u00f6")) {
                    this.cursor = this.limit - v_15;
                    if (!this.eq_s_b(1, "\u00fc")) {
                        return false;
                    }
                }
                this.cursor = this.limit - v_13;
                final int c = this.cursor;
                this.insert(this.cursor, this.cursor, "\u00fc");
                this.cursor = c;
                break;
            }
            this.cursor = this.limit - v_14;
            if (this.cursor <= this.limit_backward) {
                return false;
            }
            --this.cursor;
        }
        return true;
    }
    
    private boolean r_more_than_one_syllable_word() {
        final int v_1 = this.cursor;
        int v_2 = 2;
        int v_3 = 0;
    Block_2:
        while (true) {
            v_3 = this.cursor;
            while (!this.in_grouping(TurkishStemmer.g_vowel, 97, 305)) {
                if (this.cursor >= this.limit) {
                    break Block_2;
                }
                ++this.cursor;
            }
            --v_2;
        }
        this.cursor = v_3;
        if (v_2 > 0) {
            return false;
        }
        this.cursor = v_1;
        return true;
    }
    
    private boolean r_is_reserved_word() {
        final int v_1 = this.cursor;
        final int v_2 = this.cursor;
        while (true) {
            while (!this.eq_s(2, "ad")) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_1;
                    final int v_3 = this.cursor;
                    while (!this.eq_s(5, "soyad")) {
                        if (this.cursor >= this.limit) {
                            return false;
                        }
                        ++this.cursor;
                    }
                    this.I_strlen = 5;
                    if (this.I_strlen != this.limit) {
                        return false;
                    }
                    this.cursor = v_3;
                    return true;
                }
                else {
                    ++this.cursor;
                }
            }
            this.I_strlen = 2;
            if (this.I_strlen != this.limit) {
                continue;
            }
            break;
        }
        this.cursor = v_2;
        return true;
    }
    
    private boolean r_postlude() {
        final int v_1 = this.cursor;
        if (!this.r_is_reserved_word()) {
            this.cursor = v_1;
            this.limit_backward = this.cursor;
            this.cursor = this.limit;
            final int v_2 = this.limit - this.cursor;
            if (!this.r_append_U_to_stems_ending_with_d_or_g()) {}
            this.cursor = this.limit - v_2;
            final int v_3 = this.limit - this.cursor;
            if (!this.r_post_process_last_consonants()) {}
            this.cursor = this.limit - v_3;
            this.cursor = this.limit_backward;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean stem() {
        if (!this.r_more_than_one_syllable_word()) {
            return false;
        }
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        final int v_1 = this.limit - this.cursor;
        if (!this.r_stem_nominal_verb_suffixes()) {}
        this.cursor = this.limit - v_1;
        if (!this.B_continue_stemming_noun_suffixes) {
            return false;
        }
        final int v_2 = this.limit - this.cursor;
        if (!this.r_stem_noun_suffixes()) {}
        this.cursor = this.limit - v_2;
        this.cursor = this.limit_backward;
        return this.r_postlude();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof TurkishStemmer;
    }
    
    @Override
    public int hashCode() {
        return TurkishStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("m", -1, -1, "", TurkishStemmer.methodObject), new Among("n", -1, -1, "", TurkishStemmer.methodObject), new Among("miz", -1, -1, "", TurkishStemmer.methodObject), new Among("niz", -1, -1, "", TurkishStemmer.methodObject), new Among("muz", -1, -1, "", TurkishStemmer.methodObject), new Among("nuz", -1, -1, "", TurkishStemmer.methodObject), new Among("m\u00fcz", -1, -1, "", TurkishStemmer.methodObject), new Among("n\u00fcz", -1, -1, "", TurkishStemmer.methodObject), new Among("m\u0131z", -1, -1, "", TurkishStemmer.methodObject), new Among("n\u0131z", -1, -1, "", TurkishStemmer.methodObject) };
        a_1 = new Among[] { new Among("leri", -1, -1, "", TurkishStemmer.methodObject), new Among("lar\u0131", -1, -1, "", TurkishStemmer.methodObject) };
        a_2 = new Among[] { new Among("ni", -1, -1, "", TurkishStemmer.methodObject), new Among("nu", -1, -1, "", TurkishStemmer.methodObject), new Among("n\u00fc", -1, -1, "", TurkishStemmer.methodObject), new Among("n\u0131", -1, -1, "", TurkishStemmer.methodObject) };
        a_3 = new Among[] { new Among("in", -1, -1, "", TurkishStemmer.methodObject), new Among("un", -1, -1, "", TurkishStemmer.methodObject), new Among("\u00fcn", -1, -1, "", TurkishStemmer.methodObject), new Among("\u0131n", -1, -1, "", TurkishStemmer.methodObject) };
        a_4 = new Among[] { new Among("a", -1, -1, "", TurkishStemmer.methodObject), new Among("e", -1, -1, "", TurkishStemmer.methodObject) };
        a_5 = new Among[] { new Among("na", -1, -1, "", TurkishStemmer.methodObject), new Among("ne", -1, -1, "", TurkishStemmer.methodObject) };
        a_6 = new Among[] { new Among("da", -1, -1, "", TurkishStemmer.methodObject), new Among("ta", -1, -1, "", TurkishStemmer.methodObject), new Among("de", -1, -1, "", TurkishStemmer.methodObject), new Among("te", -1, -1, "", TurkishStemmer.methodObject) };
        a_7 = new Among[] { new Among("nda", -1, -1, "", TurkishStemmer.methodObject), new Among("nde", -1, -1, "", TurkishStemmer.methodObject) };
        a_8 = new Among[] { new Among("dan", -1, -1, "", TurkishStemmer.methodObject), new Among("tan", -1, -1, "", TurkishStemmer.methodObject), new Among("den", -1, -1, "", TurkishStemmer.methodObject), new Among("ten", -1, -1, "", TurkishStemmer.methodObject) };
        a_9 = new Among[] { new Among("ndan", -1, -1, "", TurkishStemmer.methodObject), new Among("nden", -1, -1, "", TurkishStemmer.methodObject) };
        a_10 = new Among[] { new Among("la", -1, -1, "", TurkishStemmer.methodObject), new Among("le", -1, -1, "", TurkishStemmer.methodObject) };
        a_11 = new Among[] { new Among("ca", -1, -1, "", TurkishStemmer.methodObject), new Among("ce", -1, -1, "", TurkishStemmer.methodObject) };
        a_12 = new Among[] { new Among("im", -1, -1, "", TurkishStemmer.methodObject), new Among("um", -1, -1, "", TurkishStemmer.methodObject), new Among("\u00fcm", -1, -1, "", TurkishStemmer.methodObject), new Among("\u0131m", -1, -1, "", TurkishStemmer.methodObject) };
        a_13 = new Among[] { new Among("sin", -1, -1, "", TurkishStemmer.methodObject), new Among("sun", -1, -1, "", TurkishStemmer.methodObject), new Among("s\u00fcn", -1, -1, "", TurkishStemmer.methodObject), new Among("s\u0131n", -1, -1, "", TurkishStemmer.methodObject) };
        a_14 = new Among[] { new Among("iz", -1, -1, "", TurkishStemmer.methodObject), new Among("uz", -1, -1, "", TurkishStemmer.methodObject), new Among("\u00fcz", -1, -1, "", TurkishStemmer.methodObject), new Among("\u0131z", -1, -1, "", TurkishStemmer.methodObject) };
        a_15 = new Among[] { new Among("siniz", -1, -1, "", TurkishStemmer.methodObject), new Among("sunuz", -1, -1, "", TurkishStemmer.methodObject), new Among("s\u00fcn\u00fcz", -1, -1, "", TurkishStemmer.methodObject), new Among("s\u0131n\u0131z", -1, -1, "", TurkishStemmer.methodObject) };
        a_16 = new Among[] { new Among("lar", -1, -1, "", TurkishStemmer.methodObject), new Among("ler", -1, -1, "", TurkishStemmer.methodObject) };
        a_17 = new Among[] { new Among("niz", -1, -1, "", TurkishStemmer.methodObject), new Among("nuz", -1, -1, "", TurkishStemmer.methodObject), new Among("n\u00fcz", -1, -1, "", TurkishStemmer.methodObject), new Among("n\u0131z", -1, -1, "", TurkishStemmer.methodObject) };
        a_18 = new Among[] { new Among("dir", -1, -1, "", TurkishStemmer.methodObject), new Among("tir", -1, -1, "", TurkishStemmer.methodObject), new Among("dur", -1, -1, "", TurkishStemmer.methodObject), new Among("tur", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u00fcr", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u00fcr", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u0131r", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u0131r", -1, -1, "", TurkishStemmer.methodObject) };
        a_19 = new Among[] { new Among("cas\u0131na", -1, -1, "", TurkishStemmer.methodObject), new Among("cesine", -1, -1, "", TurkishStemmer.methodObject) };
        a_20 = new Among[] { new Among("di", -1, -1, "", TurkishStemmer.methodObject), new Among("ti", -1, -1, "", TurkishStemmer.methodObject), new Among("dik", -1, -1, "", TurkishStemmer.methodObject), new Among("tik", -1, -1, "", TurkishStemmer.methodObject), new Among("duk", -1, -1, "", TurkishStemmer.methodObject), new Among("tuk", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u00fck", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u00fck", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u0131k", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u0131k", -1, -1, "", TurkishStemmer.methodObject), new Among("dim", -1, -1, "", TurkishStemmer.methodObject), new Among("tim", -1, -1, "", TurkishStemmer.methodObject), new Among("dum", -1, -1, "", TurkishStemmer.methodObject), new Among("tum", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u00fcm", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u00fcm", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u0131m", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u0131m", -1, -1, "", TurkishStemmer.methodObject), new Among("din", -1, -1, "", TurkishStemmer.methodObject), new Among("tin", -1, -1, "", TurkishStemmer.methodObject), new Among("dun", -1, -1, "", TurkishStemmer.methodObject), new Among("tun", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u00fcn", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u00fcn", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u0131n", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u0131n", -1, -1, "", TurkishStemmer.methodObject), new Among("du", -1, -1, "", TurkishStemmer.methodObject), new Among("tu", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u00fc", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u00fc", -1, -1, "", TurkishStemmer.methodObject), new Among("d\u0131", -1, -1, "", TurkishStemmer.methodObject), new Among("t\u0131", -1, -1, "", TurkishStemmer.methodObject) };
        a_21 = new Among[] { new Among("sa", -1, -1, "", TurkishStemmer.methodObject), new Among("se", -1, -1, "", TurkishStemmer.methodObject), new Among("sak", -1, -1, "", TurkishStemmer.methodObject), new Among("sek", -1, -1, "", TurkishStemmer.methodObject), new Among("sam", -1, -1, "", TurkishStemmer.methodObject), new Among("sem", -1, -1, "", TurkishStemmer.methodObject), new Among("san", -1, -1, "", TurkishStemmer.methodObject), new Among("sen", -1, -1, "", TurkishStemmer.methodObject) };
        a_22 = new Among[] { new Among("mi\u015f", -1, -1, "", TurkishStemmer.methodObject), new Among("mu\u015f", -1, -1, "", TurkishStemmer.methodObject), new Among("m\u00fc\u015f", -1, -1, "", TurkishStemmer.methodObject), new Among("m\u0131\u015f", -1, -1, "", TurkishStemmer.methodObject) };
        a_23 = new Among[] { new Among("b", -1, 1, "", TurkishStemmer.methodObject), new Among("c", -1, 2, "", TurkishStemmer.methodObject), new Among("d", -1, 3, "", TurkishStemmer.methodObject), new Among("\u011f", -1, 4, "", TurkishStemmer.methodObject) };
        g_vowel = new char[] { '\u0011', 'A', '\u0010', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', ' ', '\b', '\0', '\0', '\0', '\0', '\0', '\0', '\u0001' };
        g_U = new char[] { '\u0001', '\u0010', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\b', '\0', '\0', '\0', '\0', '\0', '\0', '\u0001' };
        g_vowel1 = new char[] { '\u0001', '@', '\u0010', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0001' };
        g_vowel2 = new char[] { '\u0011', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0082' };
        g_vowel3 = new char[] { '\u0001', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u0001' };
        g_vowel4 = new char[] { '\u0011' };
        g_vowel5 = new char[] { 'A' };
        g_vowel6 = new char[] { 'A' };
    }
}
