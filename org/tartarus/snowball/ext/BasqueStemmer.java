package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import java.lang.invoke.MethodHandles;
import org.tartarus.snowball.SnowballProgram;

public class BasqueStemmer extends SnowballProgram
{
    private static final long serialVersionUID = 1L;
    private static final MethodHandles.Lookup methodObject;
    private static final Among[] a_0;
    private static final Among[] a_1;
    private static final Among[] a_2;
    private static final char[] g_v;
    private int I_p2;
    private int I_p1;
    private int I_pV;
    
    private void copy_from(final BasqueStemmer other) {
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
        final int v_2 = this.cursor;
        Label_0318: {
            Label_0310: {
                Label_0177: {
                    if (this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                        final int v_3 = this.cursor;
                        Label_0114: {
                            if (this.out_grouping(BasqueStemmer.g_v, 97, 117)) {
                                while (!this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                                    if (this.cursor >= this.limit) {
                                        break Label_0114;
                                    }
                                    ++this.cursor;
                                }
                                break Label_0310;
                            }
                        }
                        this.cursor = v_3;
                        if (this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                            while (!this.out_grouping(BasqueStemmer.g_v, 97, 117)) {
                                if (this.cursor >= this.limit) {
                                    break Label_0177;
                                }
                                ++this.cursor;
                            }
                            break Label_0310;
                        }
                    }
                }
                this.cursor = v_2;
                if (!this.out_grouping(BasqueStemmer.g_v, 97, 117)) {
                    break Label_0318;
                }
                final int v_4 = this.cursor;
                Label_0263: {
                    if (this.out_grouping(BasqueStemmer.g_v, 97, 117)) {
                        while (!this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                            if (this.cursor >= this.limit) {
                                break Label_0263;
                            }
                            ++this.cursor;
                        }
                        break Label_0310;
                    }
                }
                this.cursor = v_4;
                if (!this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                    break Label_0318;
                }
                if (this.cursor >= this.limit) {
                    break Label_0318;
                }
                ++this.cursor;
            }
            this.I_pV = this.cursor;
        }
        this.cursor = v_1;
        final int v_5 = this.cursor;
    Label_0509:
        while (true) {
            while (!this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                if (this.cursor >= this.limit) {
                    this.cursor = v_5;
                    return true;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(BasqueStemmer.g_v, 97, 117)) {
                if (this.cursor >= this.limit) {
                    continue Label_0509;
                }
                ++this.cursor;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(BasqueStemmer.g_v, 97, 117)) {
                if (this.cursor >= this.limit) {
                    continue Label_0509;
                }
                ++this.cursor;
            }
            while (!this.out_grouping(BasqueStemmer.g_v, 97, 117)) {
                if (this.cursor >= this.limit) {
                    continue Label_0509;
                }
                ++this.cursor;
            }
            this.I_p2 = this.cursor;
            continue Label_0509;
        }
    }
    
    private boolean r_RV() {
        return this.I_pV <= this.cursor;
    }
    
    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }
    
    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }
    
    private boolean r_aditzak() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(BasqueStemmer.a_0, 109);
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
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("atseden");
                break;
            }
            case 4: {
                this.slice_from("arabera");
                break;
            }
            case 5: {
                this.slice_from("baditu");
                break;
            }
        }
        return true;
    }
    
    private boolean r_izenak() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(BasqueStemmer.a_1, 295);
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
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("jok");
                break;
            }
            case 4: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("tra");
                break;
            }
            case 6: {
                this.slice_from("minutu");
                break;
            }
            case 7: {
                this.slice_from("zehar");
                break;
            }
            case 8: {
                this.slice_from("geldi");
                break;
            }
            case 9: {
                this.slice_from("igaro");
                break;
            }
            case 10: {
                this.slice_from("aurka");
                break;
            }
        }
        return true;
    }
    
    private boolean r_adjetiboak() {
        this.ket = this.cursor;
        final int among_var = this.find_among_b(BasqueStemmer.a_2, 19);
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
                this.slice_from("z");
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
        int v_2;
        do {
            v_2 = this.limit - this.cursor;
        } while (this.r_aditzak());
        this.cursor = this.limit - v_2;
        int v_3;
        do {
            v_3 = this.limit - this.cursor;
        } while (this.r_izenak());
        this.cursor = this.limit - v_3;
        final int v_4 = this.limit - this.cursor;
        if (!this.r_adjetiboak()) {}
        this.cursor = this.limit - v_4;
        this.cursor = this.limit_backward;
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof BasqueStemmer;
    }
    
    @Override
    public int hashCode() {
        return BasqueStemmer.class.getName().hashCode();
    }
    
    static {
        methodObject = MethodHandles.lookup();
        a_0 = new Among[] { new Among("idea", -1, 1, "", BasqueStemmer.methodObject), new Among("bidea", 0, 1, "", BasqueStemmer.methodObject), new Among("kidea", 0, 1, "", BasqueStemmer.methodObject), new Among("pidea", 0, 1, "", BasqueStemmer.methodObject), new Among("kundea", -1, 1, "", BasqueStemmer.methodObject), new Among("galea", -1, 1, "", BasqueStemmer.methodObject), new Among("tailea", -1, 1, "", BasqueStemmer.methodObject), new Among("tzailea", -1, 1, "", BasqueStemmer.methodObject), new Among("gunea", -1, 1, "", BasqueStemmer.methodObject), new Among("kunea", -1, 1, "", BasqueStemmer.methodObject), new Among("tzaga", -1, 1, "", BasqueStemmer.methodObject), new Among("gaia", -1, 1, "", BasqueStemmer.methodObject), new Among("aldia", -1, 1, "", BasqueStemmer.methodObject), new Among("taldia", 12, 1, "", BasqueStemmer.methodObject), new Among("karia", -1, 1, "", BasqueStemmer.methodObject), new Among("garria", -1, 2, "", BasqueStemmer.methodObject), new Among("karria", -1, 1, "", BasqueStemmer.methodObject), new Among("ka", -1, 1, "", BasqueStemmer.methodObject), new Among("tzaka", 17, 1, "", BasqueStemmer.methodObject), new Among("la", -1, 1, "", BasqueStemmer.methodObject), new Among("mena", -1, 1, "", BasqueStemmer.methodObject), new Among("pena", -1, 1, "", BasqueStemmer.methodObject), new Among("kina", -1, 1, "", BasqueStemmer.methodObject), new Among("ezina", -1, 1, "", BasqueStemmer.methodObject), new Among("tezina", 23, 1, "", BasqueStemmer.methodObject), new Among("kuna", -1, 1, "", BasqueStemmer.methodObject), new Among("tuna", -1, 1, "", BasqueStemmer.methodObject), new Among("kizuna", -1, 1, "", BasqueStemmer.methodObject), new Among("era", -1, 1, "", BasqueStemmer.methodObject), new Among("bera", 28, 1, "", BasqueStemmer.methodObject), new Among("arabera", 29, 4, "", BasqueStemmer.methodObject), new Among("kera", 28, 1, "", BasqueStemmer.methodObject), new Among("pera", 28, 1, "", BasqueStemmer.methodObject), new Among("orra", -1, 1, "", BasqueStemmer.methodObject), new Among("korra", 33, 1, "", BasqueStemmer.methodObject), new Among("dura", -1, 1, "", BasqueStemmer.methodObject), new Among("gura", -1, 1, "", BasqueStemmer.methodObject), new Among("kura", -1, 1, "", BasqueStemmer.methodObject), new Among("tura", -1, 1, "", BasqueStemmer.methodObject), new Among("eta", -1, 1, "", BasqueStemmer.methodObject), new Among("keta", 39, 1, "", BasqueStemmer.methodObject), new Among("gailua", -1, 1, "", BasqueStemmer.methodObject), new Among("eza", -1, 1, "", BasqueStemmer.methodObject), new Among("erreza", 42, 1, "", BasqueStemmer.methodObject), new Among("tza", -1, 2, "", BasqueStemmer.methodObject), new Among("gaitza", 44, 1, "", BasqueStemmer.methodObject), new Among("kaitza", 44, 1, "", BasqueStemmer.methodObject), new Among("kuntza", 44, 1, "", BasqueStemmer.methodObject), new Among("ide", -1, 1, "", BasqueStemmer.methodObject), new Among("bide", 48, 1, "", BasqueStemmer.methodObject), new Among("kide", 48, 1, "", BasqueStemmer.methodObject), new Among("pide", 48, 1, "", BasqueStemmer.methodObject), new Among("kunde", -1, 1, "", BasqueStemmer.methodObject), new Among("tzake", -1, 1, "", BasqueStemmer.methodObject), new Among("tzeke", -1, 1, "", BasqueStemmer.methodObject), new Among("le", -1, 1, "", BasqueStemmer.methodObject), new Among("gale", 55, 1, "", BasqueStemmer.methodObject), new Among("taile", 55, 1, "", BasqueStemmer.methodObject), new Among("tzaile", 55, 1, "", BasqueStemmer.methodObject), new Among("gune", -1, 1, "", BasqueStemmer.methodObject), new Among("kune", -1, 1, "", BasqueStemmer.methodObject), new Among("tze", -1, 1, "", BasqueStemmer.methodObject), new Among("atze", 61, 1, "", BasqueStemmer.methodObject), new Among("gai", -1, 1, "", BasqueStemmer.methodObject), new Among("aldi", -1, 1, "", BasqueStemmer.methodObject), new Among("taldi", 64, 1, "", BasqueStemmer.methodObject), new Among("ki", -1, 1, "", BasqueStemmer.methodObject), new Among("ari", -1, 1, "", BasqueStemmer.methodObject), new Among("kari", 67, 1, "", BasqueStemmer.methodObject), new Among("lari", 67, 1, "", BasqueStemmer.methodObject), new Among("tari", 67, 1, "", BasqueStemmer.methodObject), new Among("etari", 70, 1, "", BasqueStemmer.methodObject), new Among("garri", -1, 2, "", BasqueStemmer.methodObject), new Among("karri", -1, 1, "", BasqueStemmer.methodObject), new Among("arazi", -1, 1, "", BasqueStemmer.methodObject), new Among("tarazi", 74, 1, "", BasqueStemmer.methodObject), new Among("an", -1, 1, "", BasqueStemmer.methodObject), new Among("ean", 76, 1, "", BasqueStemmer.methodObject), new Among("rean", 77, 1, "", BasqueStemmer.methodObject), new Among("kan", 76, 1, "", BasqueStemmer.methodObject), new Among("etan", 76, 1, "", BasqueStemmer.methodObject), new Among("atseden", -1, 3, "", BasqueStemmer.methodObject), new Among("men", -1, 1, "", BasqueStemmer.methodObject), new Among("pen", -1, 1, "", BasqueStemmer.methodObject), new Among("kin", -1, 1, "", BasqueStemmer.methodObject), new Among("rekin", 84, 1, "", BasqueStemmer.methodObject), new Among("ezin", -1, 1, "", BasqueStemmer.methodObject), new Among("tezin", 86, 1, "", BasqueStemmer.methodObject), new Among("tun", -1, 1, "", BasqueStemmer.methodObject), new Among("kizun", -1, 1, "", BasqueStemmer.methodObject), new Among("go", -1, 1, "", BasqueStemmer.methodObject), new Among("ago", 90, 1, "", BasqueStemmer.methodObject), new Among("tio", -1, 1, "", BasqueStemmer.methodObject), new Among("dako", -1, 1, "", BasqueStemmer.methodObject), new Among("or", -1, 1, "", BasqueStemmer.methodObject), new Among("kor", 94, 1, "", BasqueStemmer.methodObject), new Among("tzat", -1, 1, "", BasqueStemmer.methodObject), new Among("du", -1, 1, "", BasqueStemmer.methodObject), new Among("gailu", -1, 1, "", BasqueStemmer.methodObject), new Among("tu", -1, 1, "", BasqueStemmer.methodObject), new Among("atu", 99, 1, "", BasqueStemmer.methodObject), new Among("aldatu", 100, 1, "", BasqueStemmer.methodObject), new Among("tatu", 100, 1, "", BasqueStemmer.methodObject), new Among("baditu", 99, 5, "", BasqueStemmer.methodObject), new Among("ez", -1, 1, "", BasqueStemmer.methodObject), new Among("errez", 104, 1, "", BasqueStemmer.methodObject), new Among("tzez", 104, 1, "", BasqueStemmer.methodObject), new Among("gaitz", -1, 1, "", BasqueStemmer.methodObject), new Among("kaitz", -1, 1, "", BasqueStemmer.methodObject) };
        a_1 = new Among[] { new Among("ada", -1, 1, "", BasqueStemmer.methodObject), new Among("kada", 0, 1, "", BasqueStemmer.methodObject), new Among("anda", -1, 1, "", BasqueStemmer.methodObject), new Among("denda", -1, 1, "", BasqueStemmer.methodObject), new Among("gabea", -1, 1, "", BasqueStemmer.methodObject), new Among("kabea", -1, 1, "", BasqueStemmer.methodObject), new Among("aldea", -1, 1, "", BasqueStemmer.methodObject), new Among("kaldea", 6, 1, "", BasqueStemmer.methodObject), new Among("taldea", 6, 1, "", BasqueStemmer.methodObject), new Among("ordea", -1, 1, "", BasqueStemmer.methodObject), new Among("zalea", -1, 1, "", BasqueStemmer.methodObject), new Among("tzalea", 10, 1, "", BasqueStemmer.methodObject), new Among("gilea", -1, 1, "", BasqueStemmer.methodObject), new Among("emea", -1, 1, "", BasqueStemmer.methodObject), new Among("kumea", -1, 1, "", BasqueStemmer.methodObject), new Among("nea", -1, 1, "", BasqueStemmer.methodObject), new Among("enea", 15, 1, "", BasqueStemmer.methodObject), new Among("zionea", 15, 1, "", BasqueStemmer.methodObject), new Among("unea", 15, 1, "", BasqueStemmer.methodObject), new Among("gunea", 18, 1, "", BasqueStemmer.methodObject), new Among("pea", -1, 1, "", BasqueStemmer.methodObject), new Among("aurrea", -1, 1, "", BasqueStemmer.methodObject), new Among("tea", -1, 1, "", BasqueStemmer.methodObject), new Among("kotea", 22, 1, "", BasqueStemmer.methodObject), new Among("artea", 22, 1, "", BasqueStemmer.methodObject), new Among("ostea", 22, 1, "", BasqueStemmer.methodObject), new Among("etxea", -1, 1, "", BasqueStemmer.methodObject), new Among("ga", -1, 1, "", BasqueStemmer.methodObject), new Among("anga", 27, 1, "", BasqueStemmer.methodObject), new Among("gaia", -1, 1, "", BasqueStemmer.methodObject), new Among("aldia", -1, 1, "", BasqueStemmer.methodObject), new Among("taldia", 30, 1, "", BasqueStemmer.methodObject), new Among("handia", -1, 1, "", BasqueStemmer.methodObject), new Among("mendia", -1, 1, "", BasqueStemmer.methodObject), new Among("geia", -1, 1, "", BasqueStemmer.methodObject), new Among("egia", -1, 1, "", BasqueStemmer.methodObject), new Among("degia", 35, 1, "", BasqueStemmer.methodObject), new Among("tegia", 35, 1, "", BasqueStemmer.methodObject), new Among("nahia", -1, 1, "", BasqueStemmer.methodObject), new Among("ohia", -1, 1, "", BasqueStemmer.methodObject), new Among("kia", -1, 1, "", BasqueStemmer.methodObject), new Among("tokia", 40, 1, "", BasqueStemmer.methodObject), new Among("oia", -1, 1, "", BasqueStemmer.methodObject), new Among("koia", 42, 1, "", BasqueStemmer.methodObject), new Among("aria", -1, 1, "", BasqueStemmer.methodObject), new Among("karia", 44, 1, "", BasqueStemmer.methodObject), new Among("laria", 44, 1, "", BasqueStemmer.methodObject), new Among("taria", 44, 1, "", BasqueStemmer.methodObject), new Among("eria", -1, 1, "", BasqueStemmer.methodObject), new Among("keria", 48, 1, "", BasqueStemmer.methodObject), new Among("teria", 48, 1, "", BasqueStemmer.methodObject), new Among("garria", -1, 2, "", BasqueStemmer.methodObject), new Among("larria", -1, 1, "", BasqueStemmer.methodObject), new Among("kirria", -1, 1, "", BasqueStemmer.methodObject), new Among("duria", -1, 1, "", BasqueStemmer.methodObject), new Among("asia", -1, 1, "", BasqueStemmer.methodObject), new Among("tia", -1, 1, "", BasqueStemmer.methodObject), new Among("ezia", -1, 1, "", BasqueStemmer.methodObject), new Among("bizia", -1, 1, "", BasqueStemmer.methodObject), new Among("ontzia", -1, 1, "", BasqueStemmer.methodObject), new Among("ka", -1, 1, "", BasqueStemmer.methodObject), new Among("joka", 60, 3, "", BasqueStemmer.methodObject), new Among("aurka", 60, 10, "", BasqueStemmer.methodObject), new Among("ska", 60, 1, "", BasqueStemmer.methodObject), new Among("xka", 60, 1, "", BasqueStemmer.methodObject), new Among("zka", 60, 1, "", BasqueStemmer.methodObject), new Among("gibela", -1, 1, "", BasqueStemmer.methodObject), new Among("gela", -1, 1, "", BasqueStemmer.methodObject), new Among("kaila", -1, 1, "", BasqueStemmer.methodObject), new Among("skila", -1, 1, "", BasqueStemmer.methodObject), new Among("tila", -1, 1, "", BasqueStemmer.methodObject), new Among("ola", -1, 1, "", BasqueStemmer.methodObject), new Among("na", -1, 1, "", BasqueStemmer.methodObject), new Among("kana", 72, 1, "", BasqueStemmer.methodObject), new Among("ena", 72, 1, "", BasqueStemmer.methodObject), new Among("garrena", 74, 1, "", BasqueStemmer.methodObject), new Among("gerrena", 74, 1, "", BasqueStemmer.methodObject), new Among("urrena", 74, 1, "", BasqueStemmer.methodObject), new Among("zaina", 72, 1, "", BasqueStemmer.methodObject), new Among("tzaina", 78, 1, "", BasqueStemmer.methodObject), new Among("kina", 72, 1, "", BasqueStemmer.methodObject), new Among("mina", 72, 1, "", BasqueStemmer.methodObject), new Among("garna", 72, 1, "", BasqueStemmer.methodObject), new Among("una", 72, 1, "", BasqueStemmer.methodObject), new Among("duna", 83, 1, "", BasqueStemmer.methodObject), new Among("asuna", 83, 1, "", BasqueStemmer.methodObject), new Among("tasuna", 85, 1, "", BasqueStemmer.methodObject), new Among("ondoa", -1, 1, "", BasqueStemmer.methodObject), new Among("kondoa", 87, 1, "", BasqueStemmer.methodObject), new Among("ngoa", -1, 1, "", BasqueStemmer.methodObject), new Among("zioa", -1, 1, "", BasqueStemmer.methodObject), new Among("koa", -1, 1, "", BasqueStemmer.methodObject), new Among("takoa", 91, 1, "", BasqueStemmer.methodObject), new Among("zkoa", 91, 1, "", BasqueStemmer.methodObject), new Among("noa", -1, 1, "", BasqueStemmer.methodObject), new Among("zinoa", 94, 1, "", BasqueStemmer.methodObject), new Among("aroa", -1, 1, "", BasqueStemmer.methodObject), new Among("taroa", 96, 1, "", BasqueStemmer.methodObject), new Among("zaroa", 96, 1, "", BasqueStemmer.methodObject), new Among("eroa", -1, 1, "", BasqueStemmer.methodObject), new Among("oroa", -1, 1, "", BasqueStemmer.methodObject), new Among("osoa", -1, 1, "", BasqueStemmer.methodObject), new Among("toa", -1, 1, "", BasqueStemmer.methodObject), new Among("ttoa", 102, 1, "", BasqueStemmer.methodObject), new Among("ztoa", 102, 1, "", BasqueStemmer.methodObject), new Among("txoa", -1, 1, "", BasqueStemmer.methodObject), new Among("tzoa", -1, 1, "", BasqueStemmer.methodObject), new Among("\u00f1oa", -1, 1, "", BasqueStemmer.methodObject), new Among("ra", -1, 1, "", BasqueStemmer.methodObject), new Among("ara", 108, 1, "", BasqueStemmer.methodObject), new Among("dara", 109, 1, "", BasqueStemmer.methodObject), new Among("liara", 109, 1, "", BasqueStemmer.methodObject), new Among("tiara", 109, 1, "", BasqueStemmer.methodObject), new Among("tara", 109, 1, "", BasqueStemmer.methodObject), new Among("etara", 113, 1, "", BasqueStemmer.methodObject), new Among("tzara", 109, 1, "", BasqueStemmer.methodObject), new Among("bera", 108, 1, "", BasqueStemmer.methodObject), new Among("kera", 108, 1, "", BasqueStemmer.methodObject), new Among("pera", 108, 1, "", BasqueStemmer.methodObject), new Among("ora", 108, 2, "", BasqueStemmer.methodObject), new Among("tzarra", 108, 1, "", BasqueStemmer.methodObject), new Among("korra", 108, 1, "", BasqueStemmer.methodObject), new Among("tra", 108, 1, "", BasqueStemmer.methodObject), new Among("sa", -1, 1, "", BasqueStemmer.methodObject), new Among("osa", 123, 1, "", BasqueStemmer.methodObject), new Among("ta", -1, 1, "", BasqueStemmer.methodObject), new Among("eta", 125, 1, "", BasqueStemmer.methodObject), new Among("keta", 126, 1, "", BasqueStemmer.methodObject), new Among("sta", 125, 1, "", BasqueStemmer.methodObject), new Among("dua", -1, 1, "", BasqueStemmer.methodObject), new Among("mendua", 129, 1, "", BasqueStemmer.methodObject), new Among("ordua", 129, 1, "", BasqueStemmer.methodObject), new Among("lekua", -1, 1, "", BasqueStemmer.methodObject), new Among("burua", -1, 1, "", BasqueStemmer.methodObject), new Among("durua", -1, 1, "", BasqueStemmer.methodObject), new Among("tsua", -1, 1, "", BasqueStemmer.methodObject), new Among("tua", -1, 1, "", BasqueStemmer.methodObject), new Among("mentua", 136, 1, "", BasqueStemmer.methodObject), new Among("estua", 136, 1, "", BasqueStemmer.methodObject), new Among("txua", -1, 1, "", BasqueStemmer.methodObject), new Among("zua", -1, 1, "", BasqueStemmer.methodObject), new Among("tzua", 140, 1, "", BasqueStemmer.methodObject), new Among("za", -1, 1, "", BasqueStemmer.methodObject), new Among("eza", 142, 1, "", BasqueStemmer.methodObject), new Among("eroza", 142, 1, "", BasqueStemmer.methodObject), new Among("tza", 142, 2, "", BasqueStemmer.methodObject), new Among("koitza", 145, 1, "", BasqueStemmer.methodObject), new Among("antza", 145, 1, "", BasqueStemmer.methodObject), new Among("gintza", 145, 1, "", BasqueStemmer.methodObject), new Among("kintza", 145, 1, "", BasqueStemmer.methodObject), new Among("kuntza", 145, 1, "", BasqueStemmer.methodObject), new Among("gabe", -1, 1, "", BasqueStemmer.methodObject), new Among("kabe", -1, 1, "", BasqueStemmer.methodObject), new Among("kide", -1, 1, "", BasqueStemmer.methodObject), new Among("alde", -1, 1, "", BasqueStemmer.methodObject), new Among("kalde", 154, 1, "", BasqueStemmer.methodObject), new Among("talde", 154, 1, "", BasqueStemmer.methodObject), new Among("orde", -1, 1, "", BasqueStemmer.methodObject), new Among("ge", -1, 1, "", BasqueStemmer.methodObject), new Among("zale", -1, 1, "", BasqueStemmer.methodObject), new Among("tzale", 159, 1, "", BasqueStemmer.methodObject), new Among("gile", -1, 1, "", BasqueStemmer.methodObject), new Among("eme", -1, 1, "", BasqueStemmer.methodObject), new Among("kume", -1, 1, "", BasqueStemmer.methodObject), new Among("ne", -1, 1, "", BasqueStemmer.methodObject), new Among("zione", 164, 1, "", BasqueStemmer.methodObject), new Among("une", 164, 1, "", BasqueStemmer.methodObject), new Among("gune", 166, 1, "", BasqueStemmer.methodObject), new Among("pe", -1, 1, "", BasqueStemmer.methodObject), new Among("aurre", -1, 1, "", BasqueStemmer.methodObject), new Among("te", -1, 1, "", BasqueStemmer.methodObject), new Among("kote", 170, 1, "", BasqueStemmer.methodObject), new Among("arte", 170, 1, "", BasqueStemmer.methodObject), new Among("oste", 170, 1, "", BasqueStemmer.methodObject), new Among("etxe", -1, 1, "", BasqueStemmer.methodObject), new Among("gai", -1, 1, "", BasqueStemmer.methodObject), new Among("di", -1, 1, "", BasqueStemmer.methodObject), new Among("aldi", 176, 1, "", BasqueStemmer.methodObject), new Among("taldi", 177, 1, "", BasqueStemmer.methodObject), new Among("geldi", 176, 8, "", BasqueStemmer.methodObject), new Among("handi", 176, 1, "", BasqueStemmer.methodObject), new Among("mendi", 176, 1, "", BasqueStemmer.methodObject), new Among("gei", -1, 1, "", BasqueStemmer.methodObject), new Among("egi", -1, 1, "", BasqueStemmer.methodObject), new Among("degi", 183, 1, "", BasqueStemmer.methodObject), new Among("tegi", 183, 1, "", BasqueStemmer.methodObject), new Among("nahi", -1, 1, "", BasqueStemmer.methodObject), new Among("ohi", -1, 1, "", BasqueStemmer.methodObject), new Among("ki", -1, 1, "", BasqueStemmer.methodObject), new Among("toki", 188, 1, "", BasqueStemmer.methodObject), new Among("oi", -1, 1, "", BasqueStemmer.methodObject), new Among("goi", 190, 1, "", BasqueStemmer.methodObject), new Among("koi", 190, 1, "", BasqueStemmer.methodObject), new Among("ari", -1, 1, "", BasqueStemmer.methodObject), new Among("kari", 193, 1, "", BasqueStemmer.methodObject), new Among("lari", 193, 1, "", BasqueStemmer.methodObject), new Among("tari", 193, 1, "", BasqueStemmer.methodObject), new Among("garri", -1, 2, "", BasqueStemmer.methodObject), new Among("larri", -1, 1, "", BasqueStemmer.methodObject), new Among("kirri", -1, 1, "", BasqueStemmer.methodObject), new Among("duri", -1, 1, "", BasqueStemmer.methodObject), new Among("asi", -1, 1, "", BasqueStemmer.methodObject), new Among("ti", -1, 1, "", BasqueStemmer.methodObject), new Among("ontzi", -1, 1, "", BasqueStemmer.methodObject), new Among("\u00f1i", -1, 1, "", BasqueStemmer.methodObject), new Among("ak", -1, 1, "", BasqueStemmer.methodObject), new Among("ek", -1, 1, "", BasqueStemmer.methodObject), new Among("tarik", -1, 1, "", BasqueStemmer.methodObject), new Among("gibel", -1, 1, "", BasqueStemmer.methodObject), new Among("ail", -1, 1, "", BasqueStemmer.methodObject), new Among("kail", 209, 1, "", BasqueStemmer.methodObject), new Among("kan", -1, 1, "", BasqueStemmer.methodObject), new Among("tan", -1, 1, "", BasqueStemmer.methodObject), new Among("etan", 212, 1, "", BasqueStemmer.methodObject), new Among("en", -1, 4, "", BasqueStemmer.methodObject), new Among("ren", 214, 2, "", BasqueStemmer.methodObject), new Among("garren", 215, 1, "", BasqueStemmer.methodObject), new Among("gerren", 215, 1, "", BasqueStemmer.methodObject), new Among("urren", 215, 1, "", BasqueStemmer.methodObject), new Among("ten", 214, 4, "", BasqueStemmer.methodObject), new Among("tzen", 214, 4, "", BasqueStemmer.methodObject), new Among("zain", -1, 1, "", BasqueStemmer.methodObject), new Among("tzain", 221, 1, "", BasqueStemmer.methodObject), new Among("kin", -1, 1, "", BasqueStemmer.methodObject), new Among("min", -1, 1, "", BasqueStemmer.methodObject), new Among("dun", -1, 1, "", BasqueStemmer.methodObject), new Among("asun", -1, 1, "", BasqueStemmer.methodObject), new Among("tasun", 226, 1, "", BasqueStemmer.methodObject), new Among("aizun", -1, 1, "", BasqueStemmer.methodObject), new Among("ondo", -1, 1, "", BasqueStemmer.methodObject), new Among("kondo", 229, 1, "", BasqueStemmer.methodObject), new Among("go", -1, 1, "", BasqueStemmer.methodObject), new Among("ngo", 231, 1, "", BasqueStemmer.methodObject), new Among("zio", -1, 1, "", BasqueStemmer.methodObject), new Among("ko", -1, 1, "", BasqueStemmer.methodObject), new Among("trako", 234, 5, "", BasqueStemmer.methodObject), new Among("tako", 234, 1, "", BasqueStemmer.methodObject), new Among("etako", 236, 1, "", BasqueStemmer.methodObject), new Among("eko", 234, 1, "", BasqueStemmer.methodObject), new Among("tariko", 234, 1, "", BasqueStemmer.methodObject), new Among("sko", 234, 1, "", BasqueStemmer.methodObject), new Among("tuko", 234, 1, "", BasqueStemmer.methodObject), new Among("minutuko", 241, 6, "", BasqueStemmer.methodObject), new Among("zko", 234, 1, "", BasqueStemmer.methodObject), new Among("no", -1, 1, "", BasqueStemmer.methodObject), new Among("zino", 244, 1, "", BasqueStemmer.methodObject), new Among("ro", -1, 1, "", BasqueStemmer.methodObject), new Among("aro", 246, 1, "", BasqueStemmer.methodObject), new Among("igaro", 247, 9, "", BasqueStemmer.methodObject), new Among("taro", 247, 1, "", BasqueStemmer.methodObject), new Among("zaro", 247, 1, "", BasqueStemmer.methodObject), new Among("ero", 246, 1, "", BasqueStemmer.methodObject), new Among("giro", 246, 1, "", BasqueStemmer.methodObject), new Among("oro", 246, 1, "", BasqueStemmer.methodObject), new Among("oso", -1, 1, "", BasqueStemmer.methodObject), new Among("to", -1, 1, "", BasqueStemmer.methodObject), new Among("tto", 255, 1, "", BasqueStemmer.methodObject), new Among("zto", 255, 1, "", BasqueStemmer.methodObject), new Among("txo", -1, 1, "", BasqueStemmer.methodObject), new Among("tzo", -1, 1, "", BasqueStemmer.methodObject), new Among("gintzo", 259, 1, "", BasqueStemmer.methodObject), new Among("\u00f1o", -1, 1, "", BasqueStemmer.methodObject), new Among("zp", -1, 1, "", BasqueStemmer.methodObject), new Among("ar", -1, 1, "", BasqueStemmer.methodObject), new Among("dar", 263, 1, "", BasqueStemmer.methodObject), new Among("behar", 263, 1, "", BasqueStemmer.methodObject), new Among("zehar", 263, 7, "", BasqueStemmer.methodObject), new Among("liar", 263, 1, "", BasqueStemmer.methodObject), new Among("tiar", 263, 1, "", BasqueStemmer.methodObject), new Among("tar", 263, 1, "", BasqueStemmer.methodObject), new Among("tzar", 263, 1, "", BasqueStemmer.methodObject), new Among("or", -1, 2, "", BasqueStemmer.methodObject), new Among("kor", 271, 1, "", BasqueStemmer.methodObject), new Among("os", -1, 1, "", BasqueStemmer.methodObject), new Among("ket", -1, 1, "", BasqueStemmer.methodObject), new Among("du", -1, 1, "", BasqueStemmer.methodObject), new Among("mendu", 275, 1, "", BasqueStemmer.methodObject), new Among("ordu", 275, 1, "", BasqueStemmer.methodObject), new Among("leku", -1, 1, "", BasqueStemmer.methodObject), new Among("buru", -1, 2, "", BasqueStemmer.methodObject), new Among("duru", -1, 1, "", BasqueStemmer.methodObject), new Among("tsu", -1, 1, "", BasqueStemmer.methodObject), new Among("tu", -1, 1, "", BasqueStemmer.methodObject), new Among("tatu", 282, 4, "", BasqueStemmer.methodObject), new Among("mentu", 282, 1, "", BasqueStemmer.methodObject), new Among("estu", 282, 1, "", BasqueStemmer.methodObject), new Among("txu", -1, 1, "", BasqueStemmer.methodObject), new Among("zu", -1, 1, "", BasqueStemmer.methodObject), new Among("tzu", 287, 1, "", BasqueStemmer.methodObject), new Among("gintzu", 288, 1, "", BasqueStemmer.methodObject), new Among("z", -1, 1, "", BasqueStemmer.methodObject), new Among("ez", 290, 1, "", BasqueStemmer.methodObject), new Among("eroz", 290, 1, "", BasqueStemmer.methodObject), new Among("tz", 290, 1, "", BasqueStemmer.methodObject), new Among("koitz", 293, 1, "", BasqueStemmer.methodObject) };
        a_2 = new Among[] { new Among("zlea", -1, 2, "", BasqueStemmer.methodObject), new Among("keria", -1, 1, "", BasqueStemmer.methodObject), new Among("la", -1, 1, "", BasqueStemmer.methodObject), new Among("era", -1, 1, "", BasqueStemmer.methodObject), new Among("dade", -1, 1, "", BasqueStemmer.methodObject), new Among("tade", -1, 1, "", BasqueStemmer.methodObject), new Among("date", -1, 1, "", BasqueStemmer.methodObject), new Among("tate", -1, 1, "", BasqueStemmer.methodObject), new Among("gi", -1, 1, "", BasqueStemmer.methodObject), new Among("ki", -1, 1, "", BasqueStemmer.methodObject), new Among("ik", -1, 1, "", BasqueStemmer.methodObject), new Among("lanik", 10, 1, "", BasqueStemmer.methodObject), new Among("rik", 10, 1, "", BasqueStemmer.methodObject), new Among("larik", 12, 1, "", BasqueStemmer.methodObject), new Among("ztik", 10, 1, "", BasqueStemmer.methodObject), new Among("go", -1, 1, "", BasqueStemmer.methodObject), new Among("ro", -1, 1, "", BasqueStemmer.methodObject), new Among("ero", 16, 1, "", BasqueStemmer.methodObject), new Among("to", -1, 1, "", BasqueStemmer.methodObject) };
        g_v = new char[] { '\u0011', 'A', '\u0010' };
    }
}
