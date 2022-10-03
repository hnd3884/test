package org.owasp.esapi.codecs;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HTMLEntityCodec extends ZCodecForHTMLUnicode
{
    private static final Map<Character, String> characterToEntityMap;
    private static final Trie<Character> entityToCharacterTrie;
    
    @Override
    public String encodeCharacter(final char[] immune, final int codePoint) {
        final char c = (char)codePoint;
        if (Codec.containsCharacter(c, immune)) {
            return "" + c;
        }
        final String hex = ZCodecForHTMLUnicode.getHexForNonAlphanumeric(c, codePoint);
        if (hex == null) {
            return "" + c;
        }
        if (c == '\t' || c == '\n' || c == '\r') {
            return "" + c;
        }
        if (c <= '\u001f' || (c >= '\u007f' && c <= '\u009f')) {
            return " ";
        }
        final String entityName = HTMLEntityCodec.characterToEntityMap.get(c);
        if (entityName != null) {
            return "&" + entityName + ";";
        }
        return "&#x" + hex + ";";
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '&') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        if (second == null) {
            input.reset();
            return null;
        }
        if (second == '#') {
            final Character c = this.getNumericEntity(input);
            if (c != null) {
                return c;
            }
        }
        else if (Character.isLetter(second)) {
            input.pushback(second);
            final Character c = this.getNamedEntity(input);
            if (c != null) {
                return c;
            }
        }
        input.reset();
        return null;
    }
    
    private Character getNumericEntity(final PushbackString input) {
        final Character first = input.peek();
        if (first == null) {
            return null;
        }
        if (first == 'x' || first == 'X') {
            input.next();
            return this.parseHex(input);
        }
        return this.parseNumber(input);
    }
    
    private Character parseNumber(final PushbackString input) {
        final StringBuilder sb = new StringBuilder();
        while (input.hasNext()) {
            final Character c = input.peek();
            if (Character.isDigit(c)) {
                sb.append(c);
                input.next();
            }
            else {
                if (c == ';') {
                    input.next();
                    break;
                }
                break;
            }
        }
        try {
            final int i = Integer.parseInt(sb.toString());
            if (Character.isValidCodePoint(i)) {
                return (char)i;
            }
        }
        catch (final NumberFormatException ex) {}
        return null;
    }
    
    private Character parseHex(final PushbackString input) {
        final StringBuilder sb = new StringBuilder();
        while (input.hasNext()) {
            final Character c = input.peek();
            if ("0123456789ABCDEFabcdef".indexOf(c) != -1) {
                sb.append(c);
                input.next();
            }
            else {
                if (c == ';') {
                    input.next();
                    break;
                }
                break;
            }
        }
        try {
            final int i = Integer.parseInt(sb.toString(), 16);
            if (Character.isValidCodePoint(i)) {
                return (char)i;
            }
        }
        catch (final NumberFormatException ex) {}
        return null;
    }
    
    private Character getNamedEntity(final PushbackString input) {
        final StringBuilder possible = new StringBuilder();
        for (int len = Math.min(input.remainder().length(), HTMLEntityCodec.entityToCharacterTrie.getMaxKeyLength()), i = 0; i < len; ++i) {
            possible.append(Character.toLowerCase(input.next()));
        }
        final Map.Entry<CharSequence, Character> entry = HTMLEntityCodec.entityToCharacterTrie.getLongestMatch(possible);
        if (entry == null) {
            return null;
        }
        input.reset();
        input.next();
        for (int len = entry.getKey().length(), i = 0; i < len; ++i) {
            input.next();
        }
        if (input.peek(';')) {
            input.next();
        }
        return entry.getValue();
    }
    
    private static synchronized Map<Character, String> mkCharacterToEntityMap() {
        final Map<Character, String> map = new HashMap<Character, String>(252);
        map.put('\"', "quot");
        map.put('&', "amp");
        map.put('<', "lt");
        map.put('>', "gt");
        map.put(' ', "nbsp");
        map.put('¡', "iexcl");
        map.put('¢', "cent");
        map.put('£', "pound");
        map.put('¤', "curren");
        map.put('¥', "yen");
        map.put('¦', "brvbar");
        map.put('§', "sect");
        map.put('¨', "uml");
        map.put('©', "copy");
        map.put('ª', "ordf");
        map.put('«', "laquo");
        map.put('¬', "not");
        map.put('\u00ad', "shy");
        map.put('®', "reg");
        map.put('¯', "macr");
        map.put('°', "deg");
        map.put('±', "plusmn");
        map.put('²', "sup2");
        map.put('³', "sup3");
        map.put('´', "acute");
        map.put('µ', "micro");
        map.put('¶', "para");
        map.put('·', "middot");
        map.put('¸', "cedil");
        map.put('¹', "sup1");
        map.put('º', "ordm");
        map.put('»', "raquo");
        map.put('¼', "frac14");
        map.put('½', "frac12");
        map.put('¾', "frac34");
        map.put('¿', "iquest");
        map.put('\u00c0', "Agrave");
        map.put('\u00c1', "Aacute");
        map.put('\u00c2', "Acirc");
        map.put('\u00c3', "Atilde");
        map.put('\u00c4', "Auml");
        map.put('\u00c5', "Aring");
        map.put('\u00c6', "AElig");
        map.put('\u00c7', "Ccedil");
        map.put('\u00c8', "Egrave");
        map.put('\u00c9', "Eacute");
        map.put('\u00ca', "Ecirc");
        map.put('\u00cb', "Euml");
        map.put('\u00cc', "Igrave");
        map.put('\u00cd', "Iacute");
        map.put('\u00ce', "Icirc");
        map.put('\u00cf', "Iuml");
        map.put('\u00d0', "ETH");
        map.put('\u00d1', "Ntilde");
        map.put('\u00d2', "Ograve");
        map.put('\u00d3', "Oacute");
        map.put('\u00d4', "Ocirc");
        map.put('\u00d5', "Otilde");
        map.put('\u00d6', "Ouml");
        map.put('\u00d7', "times");
        map.put('\u00d8', "Oslash");
        map.put('\u00d9', "Ugrave");
        map.put('\u00da', "Uacute");
        map.put('\u00db', "Ucirc");
        map.put('\u00dc', "Uuml");
        map.put('\u00dd', "Yacute");
        map.put('\u00de', "THORN");
        map.put('\u00df', "szlig");
        map.put('\u00e0', "agrave");
        map.put('\u00e1', "aacute");
        map.put('\u00e2', "acirc");
        map.put('\u00e3', "atilde");
        map.put('\u00e4', "auml");
        map.put('\u00e5', "aring");
        map.put('\u00e6', "aelig");
        map.put('\u00e7', "ccedil");
        map.put('\u00e8', "egrave");
        map.put('\u00e9', "eacute");
        map.put('\u00ea', "ecirc");
        map.put('\u00eb', "euml");
        map.put('\u00ec', "igrave");
        map.put('\u00ed', "iacute");
        map.put('\u00ee', "icirc");
        map.put('\u00ef', "iuml");
        map.put('\u00f0', "eth");
        map.put('\u00f1', "ntilde");
        map.put('\u00f2', "ograve");
        map.put('\u00f3', "oacute");
        map.put('\u00f4', "ocirc");
        map.put('\u00f5', "otilde");
        map.put('\u00f6', "ouml");
        map.put('\u00f7', "divide");
        map.put('\u00f8', "oslash");
        map.put('\u00f9', "ugrave");
        map.put('\u00fa', "uacute");
        map.put('\u00fb', "ucirc");
        map.put('\u00fc', "uuml");
        map.put('\u00fd', "yacute");
        map.put('\u00fe', "thorn");
        map.put('\u00ff', "yuml");
        map.put('\u0152', "OElig");
        map.put('\u0153', "oelig");
        map.put('\u0160', "Scaron");
        map.put('\u0161', "scaron");
        map.put('\u0178', "Yuml");
        map.put('\u0192', "fnof");
        map.put('\u02c6', "circ");
        map.put('\u02dc', "tilde");
        map.put('\u0391', "Alpha");
        map.put('\u0392', "Beta");
        map.put('\u0393', "Gamma");
        map.put('\u0394', "Delta");
        map.put('\u0395', "Epsilon");
        map.put('\u0396', "Zeta");
        map.put('\u0397', "Eta");
        map.put('\u0398', "Theta");
        map.put('\u0399', "Iota");
        map.put('\u039a', "Kappa");
        map.put('\u039b', "Lambda");
        map.put('\u039c', "Mu");
        map.put('\u039d', "Nu");
        map.put('\u039e', "Xi");
        map.put('\u039f', "Omicron");
        map.put('\u03a0', "Pi");
        map.put('\u03a1', "Rho");
        map.put('\u03a3', "Sigma");
        map.put('\u03a4', "Tau");
        map.put('\u03a5', "Upsilon");
        map.put('\u03a6', "Phi");
        map.put('\u03a7', "Chi");
        map.put('\u03a8', "Psi");
        map.put('\u03a9', "Omega");
        map.put('\u03b1', "alpha");
        map.put('\u03b2', "beta");
        map.put('\u03b3', "gamma");
        map.put('\u03b4', "delta");
        map.put('\u03b5', "epsilon");
        map.put('\u03b6', "zeta");
        map.put('\u03b7', "eta");
        map.put('\u03b8', "theta");
        map.put('\u03b9', "iota");
        map.put('\u03ba', "kappa");
        map.put('\u03bb', "lambda");
        map.put('\u03bc', "mu");
        map.put('\u03bd', "nu");
        map.put('\u03be', "xi");
        map.put('\u03bf', "omicron");
        map.put('\u03c0', "pi");
        map.put('\u03c1', "rho");
        map.put('\u03c2', "sigmaf");
        map.put('\u03c3', "sigma");
        map.put('\u03c4', "tau");
        map.put('\u03c5', "upsilon");
        map.put('\u03c6', "phi");
        map.put('\u03c7', "chi");
        map.put('\u03c8', "psi");
        map.put('\u03c9', "omega");
        map.put('\u03d1', "thetasym");
        map.put('\u03d2', "upsih");
        map.put('\u03d6', "piv");
        map.put('\u2002', "ensp");
        map.put('\u2003', "emsp");
        map.put('\u2009', "thinsp");
        map.put('\u200c', "zwnj");
        map.put('\u200d', "zwj");
        map.put('\u200e', "lrm");
        map.put('\u200f', "rlm");
        map.put('\u2013', "ndash");
        map.put('\u2014', "mdash");
        map.put('\u2018', "lsquo");
        map.put('\u2019', "rsquo");
        map.put('\u201a', "sbquo");
        map.put('\u201c', "ldquo");
        map.put('\u201d', "rdquo");
        map.put('\u201e', "bdquo");
        map.put('\u2020', "dagger");
        map.put('\u2021', "Dagger");
        map.put('\u2022', "bull");
        map.put('\u2026', "hellip");
        map.put('\u2030', "permil");
        map.put('\u2032', "prime");
        map.put('\u2033', "Prime");
        map.put('\u2039', "lsaquo");
        map.put('\u203a', "rsaquo");
        map.put('\u203e', "oline");
        map.put('\u2044', "frasl");
        map.put('\u20ac', "euro");
        map.put('\u2111', "image");
        map.put('\u2118', "weierp");
        map.put('\u211c', "real");
        map.put('\u2122', "trade");
        map.put('\u2135', "alefsym");
        map.put('\u2190', "larr");
        map.put('\u2191', "uarr");
        map.put('\u2192', "rarr");
        map.put('\u2193', "darr");
        map.put('\u2194', "harr");
        map.put('\u21b5', "crarr");
        map.put('\u21d0', "lArr");
        map.put('\u21d1', "uArr");
        map.put('\u21d2', "rArr");
        map.put('\u21d3', "dArr");
        map.put('\u21d4', "hArr");
        map.put('\u2200', "forall");
        map.put('\u2202', "part");
        map.put('\u2203', "exist");
        map.put('\u2205', "empty");
        map.put('\u2207', "nabla");
        map.put('\u2208', "isin");
        map.put('\u2209', "notin");
        map.put('\u220b', "ni");
        map.put('\u220f', "prod");
        map.put('\u2211', "sum");
        map.put('\u2212', "minus");
        map.put('\u2217', "lowast");
        map.put('\u221a', "radic");
        map.put('\u221d', "prop");
        map.put('\u221e', "infin");
        map.put('\u2220', "ang");
        map.put('\u2227', "and");
        map.put('\u2228', "or");
        map.put('\u2229', "cap");
        map.put('\u222a', "cup");
        map.put('\u222b', "int");
        map.put('\u2234', "there4");
        map.put('\u223c', "sim");
        map.put('\u2245', "cong");
        map.put('\u2248', "asymp");
        map.put('\u2260', "ne");
        map.put('\u2261', "equiv");
        map.put('\u2264', "le");
        map.put('\u2265', "ge");
        map.put('\u2282', "sub");
        map.put('\u2283', "sup");
        map.put('\u2284', "nsub");
        map.put('\u2286', "sube");
        map.put('\u2287', "supe");
        map.put('\u2295', "oplus");
        map.put('\u2297', "otimes");
        map.put('\u22a5', "perp");
        map.put('\u22c5', "sdot");
        map.put('\u2308', "lceil");
        map.put('\u2309', "rceil");
        map.put('\u230a', "lfloor");
        map.put('\u230b', "rfloor");
        map.put('\u2329', "lang");
        map.put('\u232a', "rang");
        map.put('\u25ca', "loz");
        map.put('\u2660', "spades");
        map.put('\u2663', "clubs");
        map.put('\u2665', "hearts");
        map.put('\u2666', "diams");
        return Collections.unmodifiableMap((Map<? extends Character, ? extends String>)map);
    }
    
    private static synchronized Trie<Character> mkEntityToCharacterTrie() {
        final Trie<Character> trie = new HashTrie<Character>();
        for (final Map.Entry<Character, String> entry : HTMLEntityCodec.characterToEntityMap.entrySet()) {
            trie.put(entry.getValue(), entry.getKey());
        }
        return Trie.Util.unmodifiable(trie);
    }
    
    static {
        characterToEntityMap = mkCharacterToEntityMap();
        entityToCharacterTrie = mkEntityToCharacterTrie();
    }
}
