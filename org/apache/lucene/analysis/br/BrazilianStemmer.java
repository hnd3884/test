package org.apache.lucene.analysis.br;

import java.util.Locale;

public class BrazilianStemmer
{
    private static final Locale locale;
    private String TERM;
    private String CT;
    private String R1;
    private String R2;
    private String RV;
    
    protected String stem(final String term) {
        boolean altered = false;
        this.createCT(term);
        if (!this.isIndexable(this.CT)) {
            return null;
        }
        if (!this.isStemmable(this.CT)) {
            return this.CT;
        }
        this.R1 = this.getR1(this.CT);
        this.R2 = this.getR1(this.R1);
        this.RV = this.getRV(this.CT);
        this.TERM = term + ";" + this.CT;
        altered = this.step1();
        if (!altered) {
            altered = this.step2();
        }
        if (altered) {
            this.step3();
        }
        else {
            this.step4();
        }
        this.step5();
        return this.CT;
    }
    
    private boolean isStemmable(final String term) {
        for (int c = 0; c < term.length(); ++c) {
            if (!Character.isLetter(term.charAt(c))) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isIndexable(final String term) {
        return term.length() < 30 && term.length() > 2;
    }
    
    private boolean isVowel(final char value) {
        return value == 'a' || value == 'e' || value == 'i' || value == 'o' || value == 'u';
    }
    
    private String getR1(final String value) {
        if (value == null) {
            return null;
        }
        int i;
        int j;
        for (i = value.length() - 1, j = 0; j < i && !this.isVowel(value.charAt(j)); ++j) {}
        if (j >= i) {
            return null;
        }
        while (j < i && this.isVowel(value.charAt(j))) {
            ++j;
        }
        if (j >= i) {
            return null;
        }
        return value.substring(j + 1);
    }
    
    private String getRV(final String value) {
        if (value == null) {
            return null;
        }
        final int i = value.length() - 1;
        if (i > 0 && !this.isVowel(value.charAt(1))) {
            int j;
            for (j = 2; j < i && !this.isVowel(value.charAt(j)); ++j) {}
            if (j < i) {
                return value.substring(j + 1);
            }
        }
        if (i > 1 && this.isVowel(value.charAt(0)) && this.isVowel(value.charAt(1))) {
            int j;
            for (j = 2; j < i && this.isVowel(value.charAt(j)); ++j) {}
            if (j < i) {
                return value.substring(j + 1);
            }
        }
        if (i > 2) {
            return value.substring(3);
        }
        return null;
    }
    
    private String changeTerm(String value) {
        String r = "";
        if (value == null) {
            return null;
        }
        value = value.toLowerCase(BrazilianStemmer.locale);
        for (int j = 0; j < value.length(); ++j) {
            if (value.charAt(j) == '\u00e1' || value.charAt(j) == '\u00e2' || value.charAt(j) == '\u00e3') {
                r += "a";
            }
            else if (value.charAt(j) == '\u00e9' || value.charAt(j) == '\u00ea') {
                r += "e";
            }
            else if (value.charAt(j) == '\u00ed') {
                r += "i";
            }
            else if (value.charAt(j) == '\u00f3' || value.charAt(j) == '\u00f4' || value.charAt(j) == '\u00f5') {
                r += "o";
            }
            else if (value.charAt(j) == '\u00fa' || value.charAt(j) == '\u00fc') {
                r += "u";
            }
            else if (value.charAt(j) == '\u00e7') {
                r += "c";
            }
            else if (value.charAt(j) == '\u00f1') {
                r += "n";
            }
            else {
                r += value.charAt(j);
            }
        }
        return r;
    }
    
    private boolean suffix(final String value, final String suffix) {
        return value != null && suffix != null && suffix.length() <= value.length() && value.substring(value.length() - suffix.length()).equals(suffix);
    }
    
    private String replaceSuffix(final String value, final String toReplace, final String changeTo) {
        if (value == null || toReplace == null || changeTo == null) {
            return value;
        }
        final String vvalue = this.removeSuffix(value, toReplace);
        if (value.equals(vvalue)) {
            return value;
        }
        return vvalue + changeTo;
    }
    
    private String removeSuffix(final String value, final String toRemove) {
        if (value == null || toRemove == null || !this.suffix(value, toRemove)) {
            return value;
        }
        return value.substring(0, value.length() - toRemove.length());
    }
    
    private boolean suffixPreceded(final String value, final String suffix, final String preceded) {
        return value != null && suffix != null && preceded != null && this.suffix(value, suffix) && this.suffix(this.removeSuffix(value, suffix), preceded);
    }
    
    private void createCT(final String term) {
        this.CT = this.changeTerm(term);
        if (this.CT.length() < 2) {
            return;
        }
        if (this.CT.charAt(0) == '\"' || this.CT.charAt(0) == '\'' || this.CT.charAt(0) == '-' || this.CT.charAt(0) == ',' || this.CT.charAt(0) == ';' || this.CT.charAt(0) == '.' || this.CT.charAt(0) == '?' || this.CT.charAt(0) == '!') {
            this.CT = this.CT.substring(1);
        }
        if (this.CT.length() < 2) {
            return;
        }
        if (this.CT.charAt(this.CT.length() - 1) == '-' || this.CT.charAt(this.CT.length() - 1) == ',' || this.CT.charAt(this.CT.length() - 1) == ';' || this.CT.charAt(this.CT.length() - 1) == '.' || this.CT.charAt(this.CT.length() - 1) == '?' || this.CT.charAt(this.CT.length() - 1) == '!' || this.CT.charAt(this.CT.length() - 1) == '\'' || this.CT.charAt(this.CT.length() - 1) == '\"') {
            this.CT = this.CT.substring(0, this.CT.length() - 1);
        }
    }
    
    private boolean step1() {
        if (this.CT == null) {
            return false;
        }
        if (this.suffix(this.CT, "uciones") && this.suffix(this.R2, "uciones")) {
            this.CT = this.replaceSuffix(this.CT, "uciones", "u");
            return true;
        }
        if (this.CT.length() >= 6) {
            if (this.suffix(this.CT, "imentos") && this.suffix(this.R2, "imentos")) {
                this.CT = this.removeSuffix(this.CT, "imentos");
                return true;
            }
            if (this.suffix(this.CT, "amentos") && this.suffix(this.R2, "amentos")) {
                this.CT = this.removeSuffix(this.CT, "amentos");
                return true;
            }
            if (this.suffix(this.CT, "adores") && this.suffix(this.R2, "adores")) {
                this.CT = this.removeSuffix(this.CT, "adores");
                return true;
            }
            if (this.suffix(this.CT, "adoras") && this.suffix(this.R2, "adoras")) {
                this.CT = this.removeSuffix(this.CT, "adoras");
                return true;
            }
            if (this.suffix(this.CT, "logias") && this.suffix(this.R2, "logias")) {
                this.replaceSuffix(this.CT, "logias", "log");
                return true;
            }
            if (this.suffix(this.CT, "encias") && this.suffix(this.R2, "encias")) {
                this.CT = this.replaceSuffix(this.CT, "encias", "ente");
                return true;
            }
            if (this.suffix(this.CT, "amente") && this.suffix(this.R1, "amente")) {
                this.CT = this.removeSuffix(this.CT, "amente");
                return true;
            }
            if (this.suffix(this.CT, "idades") && this.suffix(this.R2, "idades")) {
                this.CT = this.removeSuffix(this.CT, "idades");
                return true;
            }
        }
        if (this.CT.length() >= 5) {
            if (this.suffix(this.CT, "acoes") && this.suffix(this.R2, "acoes")) {
                this.CT = this.removeSuffix(this.CT, "acoes");
                return true;
            }
            if (this.suffix(this.CT, "imento") && this.suffix(this.R2, "imento")) {
                this.CT = this.removeSuffix(this.CT, "imento");
                return true;
            }
            if (this.suffix(this.CT, "amento") && this.suffix(this.R2, "amento")) {
                this.CT = this.removeSuffix(this.CT, "amento");
                return true;
            }
            if (this.suffix(this.CT, "adora") && this.suffix(this.R2, "adora")) {
                this.CT = this.removeSuffix(this.CT, "adora");
                return true;
            }
            if (this.suffix(this.CT, "ismos") && this.suffix(this.R2, "ismos")) {
                this.CT = this.removeSuffix(this.CT, "ismos");
                return true;
            }
            if (this.suffix(this.CT, "istas") && this.suffix(this.R2, "istas")) {
                this.CT = this.removeSuffix(this.CT, "istas");
                return true;
            }
            if (this.suffix(this.CT, "logia") && this.suffix(this.R2, "logia")) {
                this.CT = this.replaceSuffix(this.CT, "logia", "log");
                return true;
            }
            if (this.suffix(this.CT, "ucion") && this.suffix(this.R2, "ucion")) {
                this.CT = this.replaceSuffix(this.CT, "ucion", "u");
                return true;
            }
            if (this.suffix(this.CT, "encia") && this.suffix(this.R2, "encia")) {
                this.CT = this.replaceSuffix(this.CT, "encia", "ente");
                return true;
            }
            if (this.suffix(this.CT, "mente") && this.suffix(this.R2, "mente")) {
                this.CT = this.removeSuffix(this.CT, "mente");
                return true;
            }
            if (this.suffix(this.CT, "idade") && this.suffix(this.R2, "idade")) {
                this.CT = this.removeSuffix(this.CT, "idade");
                return true;
            }
        }
        if (this.CT.length() >= 4) {
            if (this.suffix(this.CT, "acao") && this.suffix(this.R2, "acao")) {
                this.CT = this.removeSuffix(this.CT, "acao");
                return true;
            }
            if (this.suffix(this.CT, "ezas") && this.suffix(this.R2, "ezas")) {
                this.CT = this.removeSuffix(this.CT, "ezas");
                return true;
            }
            if (this.suffix(this.CT, "icos") && this.suffix(this.R2, "icos")) {
                this.CT = this.removeSuffix(this.CT, "icos");
                return true;
            }
            if (this.suffix(this.CT, "icas") && this.suffix(this.R2, "icas")) {
                this.CT = this.removeSuffix(this.CT, "icas");
                return true;
            }
            if (this.suffix(this.CT, "ismo") && this.suffix(this.R2, "ismo")) {
                this.CT = this.removeSuffix(this.CT, "ismo");
                return true;
            }
            if (this.suffix(this.CT, "avel") && this.suffix(this.R2, "avel")) {
                this.CT = this.removeSuffix(this.CT, "avel");
                return true;
            }
            if (this.suffix(this.CT, "ivel") && this.suffix(this.R2, "ivel")) {
                this.CT = this.removeSuffix(this.CT, "ivel");
                return true;
            }
            if (this.suffix(this.CT, "ista") && this.suffix(this.R2, "ista")) {
                this.CT = this.removeSuffix(this.CT, "ista");
                return true;
            }
            if (this.suffix(this.CT, "osos") && this.suffix(this.R2, "osos")) {
                this.CT = this.removeSuffix(this.CT, "osos");
                return true;
            }
            if (this.suffix(this.CT, "osas") && this.suffix(this.R2, "osas")) {
                this.CT = this.removeSuffix(this.CT, "osas");
                return true;
            }
            if (this.suffix(this.CT, "ador") && this.suffix(this.R2, "ador")) {
                this.CT = this.removeSuffix(this.CT, "ador");
                return true;
            }
            if (this.suffix(this.CT, "ivas") && this.suffix(this.R2, "ivas")) {
                this.CT = this.removeSuffix(this.CT, "ivas");
                return true;
            }
            if (this.suffix(this.CT, "ivos") && this.suffix(this.R2, "ivos")) {
                this.CT = this.removeSuffix(this.CT, "ivos");
                return true;
            }
            if (this.suffix(this.CT, "iras") && this.suffix(this.RV, "iras") && this.suffixPreceded(this.CT, "iras", "e")) {
                this.CT = this.replaceSuffix(this.CT, "iras", "ir");
                return true;
            }
        }
        if (this.CT.length() >= 3) {
            if (this.suffix(this.CT, "eza") && this.suffix(this.R2, "eza")) {
                this.CT = this.removeSuffix(this.CT, "eza");
                return true;
            }
            if (this.suffix(this.CT, "ico") && this.suffix(this.R2, "ico")) {
                this.CT = this.removeSuffix(this.CT, "ico");
                return true;
            }
            if (this.suffix(this.CT, "ica") && this.suffix(this.R2, "ica")) {
                this.CT = this.removeSuffix(this.CT, "ica");
                return true;
            }
            if (this.suffix(this.CT, "oso") && this.suffix(this.R2, "oso")) {
                this.CT = this.removeSuffix(this.CT, "oso");
                return true;
            }
            if (this.suffix(this.CT, "osa") && this.suffix(this.R2, "osa")) {
                this.CT = this.removeSuffix(this.CT, "osa");
                return true;
            }
            if (this.suffix(this.CT, "iva") && this.suffix(this.R2, "iva")) {
                this.CT = this.removeSuffix(this.CT, "iva");
                return true;
            }
            if (this.suffix(this.CT, "ivo") && this.suffix(this.R2, "ivo")) {
                this.CT = this.removeSuffix(this.CT, "ivo");
                return true;
            }
            if (this.suffix(this.CT, "ira") && this.suffix(this.RV, "ira") && this.suffixPreceded(this.CT, "ira", "e")) {
                this.CT = this.replaceSuffix(this.CT, "ira", "ir");
                return true;
            }
        }
        return false;
    }
    
    private boolean step2() {
        if (this.RV == null) {
            return false;
        }
        if (this.RV.length() >= 7) {
            if (this.suffix(this.RV, "issemos")) {
                this.CT = this.removeSuffix(this.CT, "issemos");
                return true;
            }
            if (this.suffix(this.RV, "essemos")) {
                this.CT = this.removeSuffix(this.CT, "essemos");
                return true;
            }
            if (this.suffix(this.RV, "assemos")) {
                this.CT = this.removeSuffix(this.CT, "assemos");
                return true;
            }
            if (this.suffix(this.RV, "ariamos")) {
                this.CT = this.removeSuffix(this.CT, "ariamos");
                return true;
            }
            if (this.suffix(this.RV, "eriamos")) {
                this.CT = this.removeSuffix(this.CT, "eriamos");
                return true;
            }
            if (this.suffix(this.RV, "iriamos")) {
                this.CT = this.removeSuffix(this.CT, "iriamos");
                return true;
            }
        }
        if (this.RV.length() >= 6) {
            if (this.suffix(this.RV, "iremos")) {
                this.CT = this.removeSuffix(this.CT, "iremos");
                return true;
            }
            if (this.suffix(this.RV, "eremos")) {
                this.CT = this.removeSuffix(this.CT, "eremos");
                return true;
            }
            if (this.suffix(this.RV, "aremos")) {
                this.CT = this.removeSuffix(this.CT, "aremos");
                return true;
            }
            if (this.suffix(this.RV, "avamos")) {
                this.CT = this.removeSuffix(this.CT, "avamos");
                return true;
            }
            if (this.suffix(this.RV, "iramos")) {
                this.CT = this.removeSuffix(this.CT, "iramos");
                return true;
            }
            if (this.suffix(this.RV, "eramos")) {
                this.CT = this.removeSuffix(this.CT, "eramos");
                return true;
            }
            if (this.suffix(this.RV, "aramos")) {
                this.CT = this.removeSuffix(this.CT, "aramos");
                return true;
            }
            if (this.suffix(this.RV, "asseis")) {
                this.CT = this.removeSuffix(this.CT, "asseis");
                return true;
            }
            if (this.suffix(this.RV, "esseis")) {
                this.CT = this.removeSuffix(this.CT, "esseis");
                return true;
            }
            if (this.suffix(this.RV, "isseis")) {
                this.CT = this.removeSuffix(this.CT, "isseis");
                return true;
            }
            if (this.suffix(this.RV, "arieis")) {
                this.CT = this.removeSuffix(this.CT, "arieis");
                return true;
            }
            if (this.suffix(this.RV, "erieis")) {
                this.CT = this.removeSuffix(this.CT, "erieis");
                return true;
            }
            if (this.suffix(this.RV, "irieis")) {
                this.CT = this.removeSuffix(this.CT, "irieis");
                return true;
            }
        }
        if (this.RV.length() >= 5) {
            if (this.suffix(this.RV, "irmos")) {
                this.CT = this.removeSuffix(this.CT, "irmos");
                return true;
            }
            if (this.suffix(this.RV, "iamos")) {
                this.CT = this.removeSuffix(this.CT, "iamos");
                return true;
            }
            if (this.suffix(this.RV, "armos")) {
                this.CT = this.removeSuffix(this.CT, "armos");
                return true;
            }
            if (this.suffix(this.RV, "ermos")) {
                this.CT = this.removeSuffix(this.CT, "ermos");
                return true;
            }
            if (this.suffix(this.RV, "areis")) {
                this.CT = this.removeSuffix(this.CT, "areis");
                return true;
            }
            if (this.suffix(this.RV, "ereis")) {
                this.CT = this.removeSuffix(this.CT, "ereis");
                return true;
            }
            if (this.suffix(this.RV, "ireis")) {
                this.CT = this.removeSuffix(this.CT, "ireis");
                return true;
            }
            if (this.suffix(this.RV, "asses")) {
                this.CT = this.removeSuffix(this.CT, "asses");
                return true;
            }
            if (this.suffix(this.RV, "esses")) {
                this.CT = this.removeSuffix(this.CT, "esses");
                return true;
            }
            if (this.suffix(this.RV, "isses")) {
                this.CT = this.removeSuffix(this.CT, "isses");
                return true;
            }
            if (this.suffix(this.RV, "astes")) {
                this.CT = this.removeSuffix(this.CT, "astes");
                return true;
            }
            if (this.suffix(this.RV, "assem")) {
                this.CT = this.removeSuffix(this.CT, "assem");
                return true;
            }
            if (this.suffix(this.RV, "essem")) {
                this.CT = this.removeSuffix(this.CT, "essem");
                return true;
            }
            if (this.suffix(this.RV, "issem")) {
                this.CT = this.removeSuffix(this.CT, "issem");
                return true;
            }
            if (this.suffix(this.RV, "ardes")) {
                this.CT = this.removeSuffix(this.CT, "ardes");
                return true;
            }
            if (this.suffix(this.RV, "erdes")) {
                this.CT = this.removeSuffix(this.CT, "erdes");
                return true;
            }
            if (this.suffix(this.RV, "irdes")) {
                this.CT = this.removeSuffix(this.CT, "irdes");
                return true;
            }
            if (this.suffix(this.RV, "ariam")) {
                this.CT = this.removeSuffix(this.CT, "ariam");
                return true;
            }
            if (this.suffix(this.RV, "eriam")) {
                this.CT = this.removeSuffix(this.CT, "eriam");
                return true;
            }
            if (this.suffix(this.RV, "iriam")) {
                this.CT = this.removeSuffix(this.CT, "iriam");
                return true;
            }
            if (this.suffix(this.RV, "arias")) {
                this.CT = this.removeSuffix(this.CT, "arias");
                return true;
            }
            if (this.suffix(this.RV, "erias")) {
                this.CT = this.removeSuffix(this.CT, "erias");
                return true;
            }
            if (this.suffix(this.RV, "irias")) {
                this.CT = this.removeSuffix(this.CT, "irias");
                return true;
            }
            if (this.suffix(this.RV, "estes")) {
                this.CT = this.removeSuffix(this.CT, "estes");
                return true;
            }
            if (this.suffix(this.RV, "istes")) {
                this.CT = this.removeSuffix(this.CT, "istes");
                return true;
            }
            if (this.suffix(this.RV, "areis")) {
                this.CT = this.removeSuffix(this.CT, "areis");
                return true;
            }
            if (this.suffix(this.RV, "aveis")) {
                this.CT = this.removeSuffix(this.CT, "aveis");
                return true;
            }
        }
        if (this.RV.length() >= 4) {
            if (this.suffix(this.RV, "aria")) {
                this.CT = this.removeSuffix(this.CT, "aria");
                return true;
            }
            if (this.suffix(this.RV, "eria")) {
                this.CT = this.removeSuffix(this.CT, "eria");
                return true;
            }
            if (this.suffix(this.RV, "iria")) {
                this.CT = this.removeSuffix(this.CT, "iria");
                return true;
            }
            if (this.suffix(this.RV, "asse")) {
                this.CT = this.removeSuffix(this.CT, "asse");
                return true;
            }
            if (this.suffix(this.RV, "esse")) {
                this.CT = this.removeSuffix(this.CT, "esse");
                return true;
            }
            if (this.suffix(this.RV, "isse")) {
                this.CT = this.removeSuffix(this.CT, "isse");
                return true;
            }
            if (this.suffix(this.RV, "aste")) {
                this.CT = this.removeSuffix(this.CT, "aste");
                return true;
            }
            if (this.suffix(this.RV, "este")) {
                this.CT = this.removeSuffix(this.CT, "este");
                return true;
            }
            if (this.suffix(this.RV, "iste")) {
                this.CT = this.removeSuffix(this.CT, "iste");
                return true;
            }
            if (this.suffix(this.RV, "arei")) {
                this.CT = this.removeSuffix(this.CT, "arei");
                return true;
            }
            if (this.suffix(this.RV, "erei")) {
                this.CT = this.removeSuffix(this.CT, "erei");
                return true;
            }
            if (this.suffix(this.RV, "irei")) {
                this.CT = this.removeSuffix(this.CT, "irei");
                return true;
            }
            if (this.suffix(this.RV, "aram")) {
                this.CT = this.removeSuffix(this.CT, "aram");
                return true;
            }
            if (this.suffix(this.RV, "eram")) {
                this.CT = this.removeSuffix(this.CT, "eram");
                return true;
            }
            if (this.suffix(this.RV, "iram")) {
                this.CT = this.removeSuffix(this.CT, "iram");
                return true;
            }
            if (this.suffix(this.RV, "avam")) {
                this.CT = this.removeSuffix(this.CT, "avam");
                return true;
            }
            if (this.suffix(this.RV, "arem")) {
                this.CT = this.removeSuffix(this.CT, "arem");
                return true;
            }
            if (this.suffix(this.RV, "erem")) {
                this.CT = this.removeSuffix(this.CT, "erem");
                return true;
            }
            if (this.suffix(this.RV, "irem")) {
                this.CT = this.removeSuffix(this.CT, "irem");
                return true;
            }
            if (this.suffix(this.RV, "ando")) {
                this.CT = this.removeSuffix(this.CT, "ando");
                return true;
            }
            if (this.suffix(this.RV, "endo")) {
                this.CT = this.removeSuffix(this.CT, "endo");
                return true;
            }
            if (this.suffix(this.RV, "indo")) {
                this.CT = this.removeSuffix(this.CT, "indo");
                return true;
            }
            if (this.suffix(this.RV, "arao")) {
                this.CT = this.removeSuffix(this.CT, "arao");
                return true;
            }
            if (this.suffix(this.RV, "erao")) {
                this.CT = this.removeSuffix(this.CT, "erao");
                return true;
            }
            if (this.suffix(this.RV, "irao")) {
                this.CT = this.removeSuffix(this.CT, "irao");
                return true;
            }
            if (this.suffix(this.RV, "adas")) {
                this.CT = this.removeSuffix(this.CT, "adas");
                return true;
            }
            if (this.suffix(this.RV, "idas")) {
                this.CT = this.removeSuffix(this.CT, "idas");
                return true;
            }
            if (this.suffix(this.RV, "aras")) {
                this.CT = this.removeSuffix(this.CT, "aras");
                return true;
            }
            if (this.suffix(this.RV, "eras")) {
                this.CT = this.removeSuffix(this.CT, "eras");
                return true;
            }
            if (this.suffix(this.RV, "iras")) {
                this.CT = this.removeSuffix(this.CT, "iras");
                return true;
            }
            if (this.suffix(this.RV, "avas")) {
                this.CT = this.removeSuffix(this.CT, "avas");
                return true;
            }
            if (this.suffix(this.RV, "ares")) {
                this.CT = this.removeSuffix(this.CT, "ares");
                return true;
            }
            if (this.suffix(this.RV, "eres")) {
                this.CT = this.removeSuffix(this.CT, "eres");
                return true;
            }
            if (this.suffix(this.RV, "ires")) {
                this.CT = this.removeSuffix(this.CT, "ires");
                return true;
            }
            if (this.suffix(this.RV, "ados")) {
                this.CT = this.removeSuffix(this.CT, "ados");
                return true;
            }
            if (this.suffix(this.RV, "idos")) {
                this.CT = this.removeSuffix(this.CT, "idos");
                return true;
            }
            if (this.suffix(this.RV, "amos")) {
                this.CT = this.removeSuffix(this.CT, "amos");
                return true;
            }
            if (this.suffix(this.RV, "emos")) {
                this.CT = this.removeSuffix(this.CT, "emos");
                return true;
            }
            if (this.suffix(this.RV, "imos")) {
                this.CT = this.removeSuffix(this.CT, "imos");
                return true;
            }
            if (this.suffix(this.RV, "iras")) {
                this.CT = this.removeSuffix(this.CT, "iras");
                return true;
            }
            if (this.suffix(this.RV, "ieis")) {
                this.CT = this.removeSuffix(this.CT, "ieis");
                return true;
            }
        }
        if (this.RV.length() >= 3) {
            if (this.suffix(this.RV, "ada")) {
                this.CT = this.removeSuffix(this.CT, "ada");
                return true;
            }
            if (this.suffix(this.RV, "ida")) {
                this.CT = this.removeSuffix(this.CT, "ida");
                return true;
            }
            if (this.suffix(this.RV, "ara")) {
                this.CT = this.removeSuffix(this.CT, "ara");
                return true;
            }
            if (this.suffix(this.RV, "era")) {
                this.CT = this.removeSuffix(this.CT, "era");
                return true;
            }
            if (this.suffix(this.RV, "ira")) {
                this.CT = this.removeSuffix(this.CT, "ava");
                return true;
            }
            if (this.suffix(this.RV, "iam")) {
                this.CT = this.removeSuffix(this.CT, "iam");
                return true;
            }
            if (this.suffix(this.RV, "ado")) {
                this.CT = this.removeSuffix(this.CT, "ado");
                return true;
            }
            if (this.suffix(this.RV, "ido")) {
                this.CT = this.removeSuffix(this.CT, "ido");
                return true;
            }
            if (this.suffix(this.RV, "ias")) {
                this.CT = this.removeSuffix(this.CT, "ias");
                return true;
            }
            if (this.suffix(this.RV, "ais")) {
                this.CT = this.removeSuffix(this.CT, "ais");
                return true;
            }
            if (this.suffix(this.RV, "eis")) {
                this.CT = this.removeSuffix(this.CT, "eis");
                return true;
            }
            if (this.suffix(this.RV, "ira")) {
                this.CT = this.removeSuffix(this.CT, "ira");
                return true;
            }
            if (this.suffix(this.RV, "ear")) {
                this.CT = this.removeSuffix(this.CT, "ear");
                return true;
            }
        }
        if (this.RV.length() >= 2) {
            if (this.suffix(this.RV, "ia")) {
                this.CT = this.removeSuffix(this.CT, "ia");
                return true;
            }
            if (this.suffix(this.RV, "ei")) {
                this.CT = this.removeSuffix(this.CT, "ei");
                return true;
            }
            if (this.suffix(this.RV, "am")) {
                this.CT = this.removeSuffix(this.CT, "am");
                return true;
            }
            if (this.suffix(this.RV, "em")) {
                this.CT = this.removeSuffix(this.CT, "em");
                return true;
            }
            if (this.suffix(this.RV, "ar")) {
                this.CT = this.removeSuffix(this.CT, "ar");
                return true;
            }
            if (this.suffix(this.RV, "er")) {
                this.CT = this.removeSuffix(this.CT, "er");
                return true;
            }
            if (this.suffix(this.RV, "ir")) {
                this.CT = this.removeSuffix(this.CT, "ir");
                return true;
            }
            if (this.suffix(this.RV, "as")) {
                this.CT = this.removeSuffix(this.CT, "as");
                return true;
            }
            if (this.suffix(this.RV, "es")) {
                this.CT = this.removeSuffix(this.CT, "es");
                return true;
            }
            if (this.suffix(this.RV, "is")) {
                this.CT = this.removeSuffix(this.CT, "is");
                return true;
            }
            if (this.suffix(this.RV, "eu")) {
                this.CT = this.removeSuffix(this.CT, "eu");
                return true;
            }
            if (this.suffix(this.RV, "iu")) {
                this.CT = this.removeSuffix(this.CT, "iu");
                return true;
            }
            if (this.suffix(this.RV, "iu")) {
                this.CT = this.removeSuffix(this.CT, "iu");
                return true;
            }
            if (this.suffix(this.RV, "ou")) {
                this.CT = this.removeSuffix(this.CT, "ou");
                return true;
            }
        }
        return false;
    }
    
    private void step3() {
        if (this.RV == null) {
            return;
        }
        if (this.suffix(this.RV, "i") && this.suffixPreceded(this.RV, "i", "c")) {
            this.CT = this.removeSuffix(this.CT, "i");
        }
    }
    
    private void step4() {
        if (this.RV == null) {
            return;
        }
        if (this.suffix(this.RV, "os")) {
            this.CT = this.removeSuffix(this.CT, "os");
            return;
        }
        if (this.suffix(this.RV, "a")) {
            this.CT = this.removeSuffix(this.CT, "a");
            return;
        }
        if (this.suffix(this.RV, "i")) {
            this.CT = this.removeSuffix(this.CT, "i");
            return;
        }
        if (this.suffix(this.RV, "o")) {
            this.CT = this.removeSuffix(this.CT, "o");
        }
    }
    
    private void step5() {
        if (this.RV == null) {
            return;
        }
        if (!this.suffix(this.RV, "e")) {
            return;
        }
        if (this.suffixPreceded(this.RV, "e", "gu")) {
            this.CT = this.removeSuffix(this.CT, "e");
            this.CT = this.removeSuffix(this.CT, "u");
            return;
        }
        if (this.suffixPreceded(this.RV, "e", "ci")) {
            this.CT = this.removeSuffix(this.CT, "e");
            this.CT = this.removeSuffix(this.CT, "i");
            return;
        }
        this.CT = this.removeSuffix(this.CT, "e");
    }
    
    public String log() {
        return " (TERM = " + this.TERM + ")" + " (CT = " + this.CT + ")" + " (RV = " + this.RV + ")" + " (R1 = " + this.R1 + ")" + " (R2 = " + this.R2 + ")";
    }
    
    static {
        locale = new Locale("pt", "BR");
    }
}
