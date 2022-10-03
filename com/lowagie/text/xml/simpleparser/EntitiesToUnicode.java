package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;

public class EntitiesToUnicode
{
    public static final HashMap map;
    
    public static char decodeEntity(final String name) {
        if (name.startsWith("#x")) {
            try {
                return (char)Integer.parseInt(name.substring(2), 16);
            }
            catch (final NumberFormatException nfe) {
                return '\0';
            }
        }
        if (name.startsWith("#")) {
            try {
                return (char)Integer.parseInt(name.substring(1));
            }
            catch (final NumberFormatException nfe) {
                return '\0';
            }
        }
        final Character c = EntitiesToUnicode.map.get(name);
        if (c == null) {
            return '\0';
        }
        return c;
    }
    
    public static String decodeString(final String s) {
        int pos_amp = s.indexOf(38);
        if (pos_amp == -1) {
            return s;
        }
        final StringBuffer buf = new StringBuffer(s.substring(0, pos_amp));
        while (true) {
            final int pos_sc = s.indexOf(59, pos_amp);
            if (pos_sc == -1) {
                buf.append(s.substring(pos_amp));
                return buf.toString();
            }
            for (int pos_a = s.indexOf(38, pos_amp + 1); pos_a != -1 && pos_a < pos_sc; pos_a = s.indexOf(38, pos_amp + 1)) {
                buf.append(s, pos_amp, pos_a);
                pos_amp = pos_a;
            }
            final char replace = decodeEntity(s.substring(pos_amp + 1, pos_sc));
            if (s.length() < pos_sc + 1) {
                return buf.toString();
            }
            if (replace == '\0') {
                buf.append(s, pos_amp, pos_sc + 1);
            }
            else {
                buf.append(replace);
            }
            pos_amp = s.indexOf(38, pos_sc);
            if (pos_amp == -1) {
                buf.append(s.substring(pos_sc + 1));
                return buf.toString();
            }
            buf.append(s, pos_sc + 1, pos_amp);
        }
    }
    
    static {
        (map = new HashMap()).put("nbsp", new Character(' '));
        EntitiesToUnicode.map.put("iexcl", new Character('¡'));
        EntitiesToUnicode.map.put("cent", new Character('¢'));
        EntitiesToUnicode.map.put("pound", new Character('£'));
        EntitiesToUnicode.map.put("curren", new Character('¤'));
        EntitiesToUnicode.map.put("yen", new Character('¥'));
        EntitiesToUnicode.map.put("brvbar", new Character('¦'));
        EntitiesToUnicode.map.put("sect", new Character('§'));
        EntitiesToUnicode.map.put("uml", new Character('¨'));
        EntitiesToUnicode.map.put("copy", new Character('©'));
        EntitiesToUnicode.map.put("ordf", new Character('ª'));
        EntitiesToUnicode.map.put("laquo", new Character('«'));
        EntitiesToUnicode.map.put("not", new Character('¬'));
        EntitiesToUnicode.map.put("shy", new Character('\u00ad'));
        EntitiesToUnicode.map.put("reg", new Character('®'));
        EntitiesToUnicode.map.put("macr", new Character('¯'));
        EntitiesToUnicode.map.put("deg", new Character('°'));
        EntitiesToUnicode.map.put("plusmn", new Character('±'));
        EntitiesToUnicode.map.put("sup2", new Character('²'));
        EntitiesToUnicode.map.put("sup3", new Character('³'));
        EntitiesToUnicode.map.put("acute", new Character('´'));
        EntitiesToUnicode.map.put("micro", new Character('µ'));
        EntitiesToUnicode.map.put("para", new Character('¶'));
        EntitiesToUnicode.map.put("middot", new Character('·'));
        EntitiesToUnicode.map.put("cedil", new Character('¸'));
        EntitiesToUnicode.map.put("sup1", new Character('¹'));
        EntitiesToUnicode.map.put("ordm", new Character('º'));
        EntitiesToUnicode.map.put("raquo", new Character('»'));
        EntitiesToUnicode.map.put("frac14", new Character('¼'));
        EntitiesToUnicode.map.put("frac12", new Character('½'));
        EntitiesToUnicode.map.put("frac34", new Character('¾'));
        EntitiesToUnicode.map.put("iquest", new Character('¿'));
        EntitiesToUnicode.map.put("Agrave", new Character('\u00c0'));
        EntitiesToUnicode.map.put("Aacute", new Character('\u00c1'));
        EntitiesToUnicode.map.put("Acirc", new Character('\u00c2'));
        EntitiesToUnicode.map.put("Atilde", new Character('\u00c3'));
        EntitiesToUnicode.map.put("Auml", new Character('\u00c4'));
        EntitiesToUnicode.map.put("Aring", new Character('\u00c5'));
        EntitiesToUnicode.map.put("AElig", new Character('\u00c6'));
        EntitiesToUnicode.map.put("Ccedil", new Character('\u00c7'));
        EntitiesToUnicode.map.put("Egrave", new Character('\u00c8'));
        EntitiesToUnicode.map.put("Eacute", new Character('\u00c9'));
        EntitiesToUnicode.map.put("Ecirc", new Character('\u00ca'));
        EntitiesToUnicode.map.put("Euml", new Character('\u00cb'));
        EntitiesToUnicode.map.put("Igrave", new Character('\u00cc'));
        EntitiesToUnicode.map.put("Iacute", new Character('\u00cd'));
        EntitiesToUnicode.map.put("Icirc", new Character('\u00ce'));
        EntitiesToUnicode.map.put("Iuml", new Character('\u00cf'));
        EntitiesToUnicode.map.put("ETH", new Character('\u00d0'));
        EntitiesToUnicode.map.put("Ntilde", new Character('\u00d1'));
        EntitiesToUnicode.map.put("Ograve", new Character('\u00d2'));
        EntitiesToUnicode.map.put("Oacute", new Character('\u00d3'));
        EntitiesToUnicode.map.put("Ocirc", new Character('\u00d4'));
        EntitiesToUnicode.map.put("Otilde", new Character('\u00d5'));
        EntitiesToUnicode.map.put("Ouml", new Character('\u00d6'));
        EntitiesToUnicode.map.put("times", new Character('\u00d7'));
        EntitiesToUnicode.map.put("Oslash", new Character('\u00d8'));
        EntitiesToUnicode.map.put("Ugrave", new Character('\u00d9'));
        EntitiesToUnicode.map.put("Uacute", new Character('\u00da'));
        EntitiesToUnicode.map.put("Ucirc", new Character('\u00db'));
        EntitiesToUnicode.map.put("Uuml", new Character('\u00dc'));
        EntitiesToUnicode.map.put("Yacute", new Character('\u00dd'));
        EntitiesToUnicode.map.put("THORN", new Character('\u00de'));
        EntitiesToUnicode.map.put("szlig", new Character('\u00df'));
        EntitiesToUnicode.map.put("agrave", new Character('\u00e0'));
        EntitiesToUnicode.map.put("aacute", new Character('\u00e1'));
        EntitiesToUnicode.map.put("acirc", new Character('\u00e2'));
        EntitiesToUnicode.map.put("atilde", new Character('\u00e3'));
        EntitiesToUnicode.map.put("auml", new Character('\u00e4'));
        EntitiesToUnicode.map.put("aring", new Character('\u00e5'));
        EntitiesToUnicode.map.put("aelig", new Character('\u00e6'));
        EntitiesToUnicode.map.put("ccedil", new Character('\u00e7'));
        EntitiesToUnicode.map.put("egrave", new Character('\u00e8'));
        EntitiesToUnicode.map.put("eacute", new Character('\u00e9'));
        EntitiesToUnicode.map.put("ecirc", new Character('\u00ea'));
        EntitiesToUnicode.map.put("euml", new Character('\u00eb'));
        EntitiesToUnicode.map.put("igrave", new Character('\u00ec'));
        EntitiesToUnicode.map.put("iacute", new Character('\u00ed'));
        EntitiesToUnicode.map.put("icirc", new Character('\u00ee'));
        EntitiesToUnicode.map.put("iuml", new Character('\u00ef'));
        EntitiesToUnicode.map.put("eth", new Character('\u00f0'));
        EntitiesToUnicode.map.put("ntilde", new Character('\u00f1'));
        EntitiesToUnicode.map.put("ograve", new Character('\u00f2'));
        EntitiesToUnicode.map.put("oacute", new Character('\u00f3'));
        EntitiesToUnicode.map.put("ocirc", new Character('\u00f4'));
        EntitiesToUnicode.map.put("otilde", new Character('\u00f5'));
        EntitiesToUnicode.map.put("ouml", new Character('\u00f6'));
        EntitiesToUnicode.map.put("divide", new Character('\u00f7'));
        EntitiesToUnicode.map.put("oslash", new Character('\u00f8'));
        EntitiesToUnicode.map.put("ugrave", new Character('\u00f9'));
        EntitiesToUnicode.map.put("uacute", new Character('\u00fa'));
        EntitiesToUnicode.map.put("ucirc", new Character('\u00fb'));
        EntitiesToUnicode.map.put("uuml", new Character('\u00fc'));
        EntitiesToUnicode.map.put("yacute", new Character('\u00fd'));
        EntitiesToUnicode.map.put("thorn", new Character('\u00fe'));
        EntitiesToUnicode.map.put("yuml", new Character('\u00ff'));
        EntitiesToUnicode.map.put("fnof", new Character('\u0192'));
        EntitiesToUnicode.map.put("Alpha", new Character('\u0391'));
        EntitiesToUnicode.map.put("Beta", new Character('\u0392'));
        EntitiesToUnicode.map.put("Gamma", new Character('\u0393'));
        EntitiesToUnicode.map.put("Delta", new Character('\u0394'));
        EntitiesToUnicode.map.put("Epsilon", new Character('\u0395'));
        EntitiesToUnicode.map.put("Zeta", new Character('\u0396'));
        EntitiesToUnicode.map.put("Eta", new Character('\u0397'));
        EntitiesToUnicode.map.put("Theta", new Character('\u0398'));
        EntitiesToUnicode.map.put("Iota", new Character('\u0399'));
        EntitiesToUnicode.map.put("Kappa", new Character('\u039a'));
        EntitiesToUnicode.map.put("Lambda", new Character('\u039b'));
        EntitiesToUnicode.map.put("Mu", new Character('\u039c'));
        EntitiesToUnicode.map.put("Nu", new Character('\u039d'));
        EntitiesToUnicode.map.put("Xi", new Character('\u039e'));
        EntitiesToUnicode.map.put("Omicron", new Character('\u039f'));
        EntitiesToUnicode.map.put("Pi", new Character('\u03a0'));
        EntitiesToUnicode.map.put("Rho", new Character('\u03a1'));
        EntitiesToUnicode.map.put("Sigma", new Character('\u03a3'));
        EntitiesToUnicode.map.put("Tau", new Character('\u03a4'));
        EntitiesToUnicode.map.put("Upsilon", new Character('\u03a5'));
        EntitiesToUnicode.map.put("Phi", new Character('\u03a6'));
        EntitiesToUnicode.map.put("Chi", new Character('\u03a7'));
        EntitiesToUnicode.map.put("Psi", new Character('\u03a8'));
        EntitiesToUnicode.map.put("Omega", new Character('\u03a9'));
        EntitiesToUnicode.map.put("alpha", new Character('\u03b1'));
        EntitiesToUnicode.map.put("beta", new Character('\u03b2'));
        EntitiesToUnicode.map.put("gamma", new Character('\u03b3'));
        EntitiesToUnicode.map.put("delta", new Character('\u03b4'));
        EntitiesToUnicode.map.put("epsilon", new Character('\u03b5'));
        EntitiesToUnicode.map.put("zeta", new Character('\u03b6'));
        EntitiesToUnicode.map.put("eta", new Character('\u03b7'));
        EntitiesToUnicode.map.put("theta", new Character('\u03b8'));
        EntitiesToUnicode.map.put("iota", new Character('\u03b9'));
        EntitiesToUnicode.map.put("kappa", new Character('\u03ba'));
        EntitiesToUnicode.map.put("lambda", new Character('\u03bb'));
        EntitiesToUnicode.map.put("mu", new Character('\u03bc'));
        EntitiesToUnicode.map.put("nu", new Character('\u03bd'));
        EntitiesToUnicode.map.put("xi", new Character('\u03be'));
        EntitiesToUnicode.map.put("omicron", new Character('\u03bf'));
        EntitiesToUnicode.map.put("pi", new Character('\u03c0'));
        EntitiesToUnicode.map.put("rho", new Character('\u03c1'));
        EntitiesToUnicode.map.put("sigmaf", new Character('\u03c2'));
        EntitiesToUnicode.map.put("sigma", new Character('\u03c3'));
        EntitiesToUnicode.map.put("tau", new Character('\u03c4'));
        EntitiesToUnicode.map.put("upsilon", new Character('\u03c5'));
        EntitiesToUnicode.map.put("phi", new Character('\u03c6'));
        EntitiesToUnicode.map.put("chi", new Character('\u03c7'));
        EntitiesToUnicode.map.put("psi", new Character('\u03c8'));
        EntitiesToUnicode.map.put("omega", new Character('\u03c9'));
        EntitiesToUnicode.map.put("thetasym", new Character('\u03d1'));
        EntitiesToUnicode.map.put("upsih", new Character('\u03d2'));
        EntitiesToUnicode.map.put("piv", new Character('\u03d6'));
        EntitiesToUnicode.map.put("bull", new Character('\u2022'));
        EntitiesToUnicode.map.put("hellip", new Character('\u2026'));
        EntitiesToUnicode.map.put("prime", new Character('\u2032'));
        EntitiesToUnicode.map.put("Prime", new Character('\u2033'));
        EntitiesToUnicode.map.put("oline", new Character('\u203e'));
        EntitiesToUnicode.map.put("frasl", new Character('\u2044'));
        EntitiesToUnicode.map.put("weierp", new Character('\u2118'));
        EntitiesToUnicode.map.put("image", new Character('\u2111'));
        EntitiesToUnicode.map.put("real", new Character('\u211c'));
        EntitiesToUnicode.map.put("trade", new Character('\u2122'));
        EntitiesToUnicode.map.put("alefsym", new Character('\u2135'));
        EntitiesToUnicode.map.put("larr", new Character('\u2190'));
        EntitiesToUnicode.map.put("uarr", new Character('\u2191'));
        EntitiesToUnicode.map.put("rarr", new Character('\u2192'));
        EntitiesToUnicode.map.put("darr", new Character('\u2193'));
        EntitiesToUnicode.map.put("harr", new Character('\u2194'));
        EntitiesToUnicode.map.put("crarr", new Character('\u21b5'));
        EntitiesToUnicode.map.put("lArr", new Character('\u21d0'));
        EntitiesToUnicode.map.put("uArr", new Character('\u21d1'));
        EntitiesToUnicode.map.put("rArr", new Character('\u21d2'));
        EntitiesToUnicode.map.put("dArr", new Character('\u21d3'));
        EntitiesToUnicode.map.put("hArr", new Character('\u21d4'));
        EntitiesToUnicode.map.put("forall", new Character('\u2200'));
        EntitiesToUnicode.map.put("part", new Character('\u2202'));
        EntitiesToUnicode.map.put("exist", new Character('\u2203'));
        EntitiesToUnicode.map.put("empty", new Character('\u2205'));
        EntitiesToUnicode.map.put("nabla", new Character('\u2207'));
        EntitiesToUnicode.map.put("isin", new Character('\u2208'));
        EntitiesToUnicode.map.put("notin", new Character('\u2209'));
        EntitiesToUnicode.map.put("ni", new Character('\u220b'));
        EntitiesToUnicode.map.put("prod", new Character('\u220f'));
        EntitiesToUnicode.map.put("sum", new Character('\u2211'));
        EntitiesToUnicode.map.put("minus", new Character('\u2212'));
        EntitiesToUnicode.map.put("lowast", new Character('\u2217'));
        EntitiesToUnicode.map.put("radic", new Character('\u221a'));
        EntitiesToUnicode.map.put("prop", new Character('\u221d'));
        EntitiesToUnicode.map.put("infin", new Character('\u221e'));
        EntitiesToUnicode.map.put("ang", new Character('\u2220'));
        EntitiesToUnicode.map.put("and", new Character('\u2227'));
        EntitiesToUnicode.map.put("or", new Character('\u2228'));
        EntitiesToUnicode.map.put("cap", new Character('\u2229'));
        EntitiesToUnicode.map.put("cup", new Character('\u222a'));
        EntitiesToUnicode.map.put("int", new Character('\u222b'));
        EntitiesToUnicode.map.put("there4", new Character('\u2234'));
        EntitiesToUnicode.map.put("sim", new Character('\u223c'));
        EntitiesToUnicode.map.put("cong", new Character('\u2245'));
        EntitiesToUnicode.map.put("asymp", new Character('\u2248'));
        EntitiesToUnicode.map.put("ne", new Character('\u2260'));
        EntitiesToUnicode.map.put("equiv", new Character('\u2261'));
        EntitiesToUnicode.map.put("le", new Character('\u2264'));
        EntitiesToUnicode.map.put("ge", new Character('\u2265'));
        EntitiesToUnicode.map.put("sub", new Character('\u2282'));
        EntitiesToUnicode.map.put("sup", new Character('\u2283'));
        EntitiesToUnicode.map.put("nsub", new Character('\u2284'));
        EntitiesToUnicode.map.put("sube", new Character('\u2286'));
        EntitiesToUnicode.map.put("supe", new Character('\u2287'));
        EntitiesToUnicode.map.put("oplus", new Character('\u2295'));
        EntitiesToUnicode.map.put("otimes", new Character('\u2297'));
        EntitiesToUnicode.map.put("perp", new Character('\u22a5'));
        EntitiesToUnicode.map.put("sdot", new Character('\u22c5'));
        EntitiesToUnicode.map.put("lceil", new Character('\u2308'));
        EntitiesToUnicode.map.put("rceil", new Character('\u2309'));
        EntitiesToUnicode.map.put("lfloor", new Character('\u230a'));
        EntitiesToUnicode.map.put("rfloor", new Character('\u230b'));
        EntitiesToUnicode.map.put("lang", new Character('\u2329'));
        EntitiesToUnicode.map.put("rang", new Character('\u232a'));
        EntitiesToUnicode.map.put("loz", new Character('\u25ca'));
        EntitiesToUnicode.map.put("spades", new Character('\u2660'));
        EntitiesToUnicode.map.put("clubs", new Character('\u2663'));
        EntitiesToUnicode.map.put("hearts", new Character('\u2665'));
        EntitiesToUnicode.map.put("diams", new Character('\u2666'));
        EntitiesToUnicode.map.put("quot", new Character('\"'));
        EntitiesToUnicode.map.put("amp", new Character('&'));
        EntitiesToUnicode.map.put("apos", new Character('\''));
        EntitiesToUnicode.map.put("lt", new Character('<'));
        EntitiesToUnicode.map.put("gt", new Character('>'));
        EntitiesToUnicode.map.put("OElig", new Character('\u0152'));
        EntitiesToUnicode.map.put("oelig", new Character('\u0153'));
        EntitiesToUnicode.map.put("Scaron", new Character('\u0160'));
        EntitiesToUnicode.map.put("scaron", new Character('\u0161'));
        EntitiesToUnicode.map.put("Yuml", new Character('\u0178'));
        EntitiesToUnicode.map.put("circ", new Character('\u02c6'));
        EntitiesToUnicode.map.put("tilde", new Character('\u02dc'));
        EntitiesToUnicode.map.put("ensp", new Character('\u2002'));
        EntitiesToUnicode.map.put("emsp", new Character('\u2003'));
        EntitiesToUnicode.map.put("thinsp", new Character('\u2009'));
        EntitiesToUnicode.map.put("zwnj", new Character('\u200c'));
        EntitiesToUnicode.map.put("zwj", new Character('\u200d'));
        EntitiesToUnicode.map.put("lrm", new Character('\u200e'));
        EntitiesToUnicode.map.put("rlm", new Character('\u200f'));
        EntitiesToUnicode.map.put("ndash", new Character('\u2013'));
        EntitiesToUnicode.map.put("mdash", new Character('\u2014'));
        EntitiesToUnicode.map.put("lsquo", new Character('\u2018'));
        EntitiesToUnicode.map.put("rsquo", new Character('\u2019'));
        EntitiesToUnicode.map.put("sbquo", new Character('\u201a'));
        EntitiesToUnicode.map.put("ldquo", new Character('\u201c'));
        EntitiesToUnicode.map.put("rdquo", new Character('\u201d'));
        EntitiesToUnicode.map.put("bdquo", new Character('\u201e'));
        EntitiesToUnicode.map.put("dagger", new Character('\u2020'));
        EntitiesToUnicode.map.put("Dagger", new Character('\u2021'));
        EntitiesToUnicode.map.put("permil", new Character('\u2030'));
        EntitiesToUnicode.map.put("lsaquo", new Character('\u2039'));
        EntitiesToUnicode.map.put("rsaquo", new Character('\u203a'));
        EntitiesToUnicode.map.put("euro", new Character('\u20ac'));
    }
}
