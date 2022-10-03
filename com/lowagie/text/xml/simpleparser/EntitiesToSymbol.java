package com.lowagie.text.xml.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import java.util.HashMap;

public class EntitiesToSymbol
{
    public static final HashMap map;
    
    public static Chunk get(final String e, final Font font) {
        final char s = getCorrespondingSymbol(e);
        if (s == '\0') {
            try {
                return new Chunk(String.valueOf((char)Integer.parseInt(e)), font);
            }
            catch (final Exception exception) {
                return new Chunk(e, font);
            }
        }
        final Font symbol = new Font(3, font.getSize(), font.getStyle(), font.getColor());
        return new Chunk(String.valueOf(s), symbol);
    }
    
    public static char getCorrespondingSymbol(final String name) {
        final Character symbol = EntitiesToSymbol.map.get(name);
        if (symbol == null) {
            return '\0';
        }
        return symbol;
    }
    
    static {
        (map = new HashMap()).put("169", new Character('\u00e3'));
        EntitiesToSymbol.map.put("172", new Character('\u00d8'));
        EntitiesToSymbol.map.put("174", new Character('\u00d2'));
        EntitiesToSymbol.map.put("177", new Character('�'));
        EntitiesToSymbol.map.put("215", new Character('�'));
        EntitiesToSymbol.map.put("247", new Character('�'));
        EntitiesToSymbol.map.put("8230", new Character('�'));
        EntitiesToSymbol.map.put("8242", new Character('�'));
        EntitiesToSymbol.map.put("8243", new Character('�'));
        EntitiesToSymbol.map.put("8260", new Character('�'));
        EntitiesToSymbol.map.put("8364", new Character('\u00f0'));
        EntitiesToSymbol.map.put("8465", new Character('\u00c1'));
        EntitiesToSymbol.map.put("8472", new Character('\u00c3'));
        EntitiesToSymbol.map.put("8476", new Character('\u00c2'));
        EntitiesToSymbol.map.put("8482", new Character('\u00d4'));
        EntitiesToSymbol.map.put("8501", new Character('\u00c0'));
        EntitiesToSymbol.map.put("8592", new Character('�'));
        EntitiesToSymbol.map.put("8593", new Character('\u00ad'));
        EntitiesToSymbol.map.put("8594", new Character('�'));
        EntitiesToSymbol.map.put("8595", new Character('�'));
        EntitiesToSymbol.map.put("8596", new Character('�'));
        EntitiesToSymbol.map.put("8629", new Character('�'));
        EntitiesToSymbol.map.put("8656", new Character('\u00dc'));
        EntitiesToSymbol.map.put("8657", new Character('\u00dd'));
        EntitiesToSymbol.map.put("8658", new Character('\u00de'));
        EntitiesToSymbol.map.put("8659", new Character('\u00df'));
        EntitiesToSymbol.map.put("8660", new Character('\u00db'));
        EntitiesToSymbol.map.put("8704", new Character('\"'));
        EntitiesToSymbol.map.put("8706", new Character('�'));
        EntitiesToSymbol.map.put("8707", new Character('$'));
        EntitiesToSymbol.map.put("8709", new Character('\u00c6'));
        EntitiesToSymbol.map.put("8711", new Character('\u00d1'));
        EntitiesToSymbol.map.put("8712", new Character('\u00ce'));
        EntitiesToSymbol.map.put("8713", new Character('\u00cf'));
        EntitiesToSymbol.map.put("8717", new Character('\''));
        EntitiesToSymbol.map.put("8719", new Character('\u00d5'));
        EntitiesToSymbol.map.put("8721", new Character('\u00e5'));
        EntitiesToSymbol.map.put("8722", new Character('-'));
        EntitiesToSymbol.map.put("8727", new Character('*'));
        EntitiesToSymbol.map.put("8729", new Character('�'));
        EntitiesToSymbol.map.put("8730", new Character('\u00d6'));
        EntitiesToSymbol.map.put("8733", new Character('�'));
        EntitiesToSymbol.map.put("8734", new Character('�'));
        EntitiesToSymbol.map.put("8736", new Character('\u00d0'));
        EntitiesToSymbol.map.put("8743", new Character('\u00d9'));
        EntitiesToSymbol.map.put("8744", new Character('\u00da'));
        EntitiesToSymbol.map.put("8745", new Character('\u00c7'));
        EntitiesToSymbol.map.put("8746", new Character('\u00c8'));
        EntitiesToSymbol.map.put("8747", new Character('\u00f2'));
        EntitiesToSymbol.map.put("8756", new Character('\\'));
        EntitiesToSymbol.map.put("8764", new Character('~'));
        EntitiesToSymbol.map.put("8773", new Character('@'));
        EntitiesToSymbol.map.put("8776", new Character('�'));
        EntitiesToSymbol.map.put("8800", new Character('�'));
        EntitiesToSymbol.map.put("8801", new Character('�'));
        EntitiesToSymbol.map.put("8804", new Character('�'));
        EntitiesToSymbol.map.put("8805", new Character('�'));
        EntitiesToSymbol.map.put("8834", new Character('\u00cc'));
        EntitiesToSymbol.map.put("8835", new Character('\u00c9'));
        EntitiesToSymbol.map.put("8836", new Character('\u00cb'));
        EntitiesToSymbol.map.put("8838", new Character('\u00cd'));
        EntitiesToSymbol.map.put("8839", new Character('\u00ca'));
        EntitiesToSymbol.map.put("8853", new Character('\u00c5'));
        EntitiesToSymbol.map.put("8855", new Character('\u00c4'));
        EntitiesToSymbol.map.put("8869", new Character('^'));
        EntitiesToSymbol.map.put("8901", new Character('\u00d7'));
        EntitiesToSymbol.map.put("8992", new Character('\u00f3'));
        EntitiesToSymbol.map.put("8993", new Character('\u00f5'));
        EntitiesToSymbol.map.put("9001", new Character('\u00e1'));
        EntitiesToSymbol.map.put("9002", new Character('\u00f1'));
        EntitiesToSymbol.map.put("913", new Character('A'));
        EntitiesToSymbol.map.put("914", new Character('B'));
        EntitiesToSymbol.map.put("915", new Character('G'));
        EntitiesToSymbol.map.put("916", new Character('D'));
        EntitiesToSymbol.map.put("917", new Character('E'));
        EntitiesToSymbol.map.put("918", new Character('Z'));
        EntitiesToSymbol.map.put("919", new Character('H'));
        EntitiesToSymbol.map.put("920", new Character('Q'));
        EntitiesToSymbol.map.put("921", new Character('I'));
        EntitiesToSymbol.map.put("922", new Character('K'));
        EntitiesToSymbol.map.put("923", new Character('L'));
        EntitiesToSymbol.map.put("924", new Character('M'));
        EntitiesToSymbol.map.put("925", new Character('N'));
        EntitiesToSymbol.map.put("926", new Character('X'));
        EntitiesToSymbol.map.put("927", new Character('O'));
        EntitiesToSymbol.map.put("928", new Character('P'));
        EntitiesToSymbol.map.put("929", new Character('R'));
        EntitiesToSymbol.map.put("931", new Character('S'));
        EntitiesToSymbol.map.put("932", new Character('T'));
        EntitiesToSymbol.map.put("933", new Character('U'));
        EntitiesToSymbol.map.put("934", new Character('F'));
        EntitiesToSymbol.map.put("935", new Character('C'));
        EntitiesToSymbol.map.put("936", new Character('Y'));
        EntitiesToSymbol.map.put("937", new Character('W'));
        EntitiesToSymbol.map.put("945", new Character('a'));
        EntitiesToSymbol.map.put("946", new Character('b'));
        EntitiesToSymbol.map.put("947", new Character('g'));
        EntitiesToSymbol.map.put("948", new Character('d'));
        EntitiesToSymbol.map.put("949", new Character('e'));
        EntitiesToSymbol.map.put("950", new Character('z'));
        EntitiesToSymbol.map.put("951", new Character('h'));
        EntitiesToSymbol.map.put("952", new Character('q'));
        EntitiesToSymbol.map.put("953", new Character('i'));
        EntitiesToSymbol.map.put("954", new Character('k'));
        EntitiesToSymbol.map.put("955", new Character('l'));
        EntitiesToSymbol.map.put("956", new Character('m'));
        EntitiesToSymbol.map.put("957", new Character('n'));
        EntitiesToSymbol.map.put("958", new Character('x'));
        EntitiesToSymbol.map.put("959", new Character('o'));
        EntitiesToSymbol.map.put("960", new Character('p'));
        EntitiesToSymbol.map.put("961", new Character('r'));
        EntitiesToSymbol.map.put("962", new Character('V'));
        EntitiesToSymbol.map.put("963", new Character('s'));
        EntitiesToSymbol.map.put("964", new Character('t'));
        EntitiesToSymbol.map.put("965", new Character('u'));
        EntitiesToSymbol.map.put("966", new Character('f'));
        EntitiesToSymbol.map.put("967", new Character('c'));
        EntitiesToSymbol.map.put("9674", new Character('\u00e0'));
        EntitiesToSymbol.map.put("968", new Character('y'));
        EntitiesToSymbol.map.put("969", new Character('w'));
        EntitiesToSymbol.map.put("977", new Character('J'));
        EntitiesToSymbol.map.put("978", new Character('�'));
        EntitiesToSymbol.map.put("981", new Character('j'));
        EntitiesToSymbol.map.put("982", new Character('v'));
        EntitiesToSymbol.map.put("9824", new Character('�'));
        EntitiesToSymbol.map.put("9827", new Character('�'));
        EntitiesToSymbol.map.put("9829", new Character('�'));
        EntitiesToSymbol.map.put("9830", new Character('�'));
        EntitiesToSymbol.map.put("Alpha", new Character('A'));
        EntitiesToSymbol.map.put("Beta", new Character('B'));
        EntitiesToSymbol.map.put("Chi", new Character('C'));
        EntitiesToSymbol.map.put("Delta", new Character('D'));
        EntitiesToSymbol.map.put("Epsilon", new Character('E'));
        EntitiesToSymbol.map.put("Eta", new Character('H'));
        EntitiesToSymbol.map.put("Gamma", new Character('G'));
        EntitiesToSymbol.map.put("Iota", new Character('I'));
        EntitiesToSymbol.map.put("Kappa", new Character('K'));
        EntitiesToSymbol.map.put("Lambda", new Character('L'));
        EntitiesToSymbol.map.put("Mu", new Character('M'));
        EntitiesToSymbol.map.put("Nu", new Character('N'));
        EntitiesToSymbol.map.put("Omega", new Character('W'));
        EntitiesToSymbol.map.put("Omicron", new Character('O'));
        EntitiesToSymbol.map.put("Phi", new Character('F'));
        EntitiesToSymbol.map.put("Pi", new Character('P'));
        EntitiesToSymbol.map.put("Prime", new Character('�'));
        EntitiesToSymbol.map.put("Psi", new Character('Y'));
        EntitiesToSymbol.map.put("Rho", new Character('R'));
        EntitiesToSymbol.map.put("Sigma", new Character('S'));
        EntitiesToSymbol.map.put("Tau", new Character('T'));
        EntitiesToSymbol.map.put("Theta", new Character('Q'));
        EntitiesToSymbol.map.put("Upsilon", new Character('U'));
        EntitiesToSymbol.map.put("Xi", new Character('X'));
        EntitiesToSymbol.map.put("Zeta", new Character('Z'));
        EntitiesToSymbol.map.put("alefsym", new Character('\u00c0'));
        EntitiesToSymbol.map.put("alpha", new Character('a'));
        EntitiesToSymbol.map.put("and", new Character('\u00d9'));
        EntitiesToSymbol.map.put("ang", new Character('\u00d0'));
        EntitiesToSymbol.map.put("asymp", new Character('�'));
        EntitiesToSymbol.map.put("beta", new Character('b'));
        EntitiesToSymbol.map.put("cap", new Character('\u00c7'));
        EntitiesToSymbol.map.put("chi", new Character('c'));
        EntitiesToSymbol.map.put("clubs", new Character('�'));
        EntitiesToSymbol.map.put("cong", new Character('@'));
        EntitiesToSymbol.map.put("copy", new Character('\u00d3'));
        EntitiesToSymbol.map.put("crarr", new Character('�'));
        EntitiesToSymbol.map.put("cup", new Character('\u00c8'));
        EntitiesToSymbol.map.put("dArr", new Character('\u00df'));
        EntitiesToSymbol.map.put("darr", new Character('�'));
        EntitiesToSymbol.map.put("delta", new Character('d'));
        EntitiesToSymbol.map.put("diams", new Character('�'));
        EntitiesToSymbol.map.put("divide", new Character('�'));
        EntitiesToSymbol.map.put("empty", new Character('\u00c6'));
        EntitiesToSymbol.map.put("epsilon", new Character('e'));
        EntitiesToSymbol.map.put("equiv", new Character('�'));
        EntitiesToSymbol.map.put("eta", new Character('h'));
        EntitiesToSymbol.map.put("euro", new Character('\u00f0'));
        EntitiesToSymbol.map.put("exist", new Character('$'));
        EntitiesToSymbol.map.put("forall", new Character('\"'));
        EntitiesToSymbol.map.put("frasl", new Character('�'));
        EntitiesToSymbol.map.put("gamma", new Character('g'));
        EntitiesToSymbol.map.put("ge", new Character('�'));
        EntitiesToSymbol.map.put("hArr", new Character('\u00db'));
        EntitiesToSymbol.map.put("harr", new Character('�'));
        EntitiesToSymbol.map.put("hearts", new Character('�'));
        EntitiesToSymbol.map.put("hellip", new Character('�'));
        EntitiesToSymbol.map.put("horizontal arrow extender", new Character('�'));
        EntitiesToSymbol.map.put("image", new Character('\u00c1'));
        EntitiesToSymbol.map.put("infin", new Character('�'));
        EntitiesToSymbol.map.put("int", new Character('\u00f2'));
        EntitiesToSymbol.map.put("iota", new Character('i'));
        EntitiesToSymbol.map.put("isin", new Character('\u00ce'));
        EntitiesToSymbol.map.put("kappa", new Character('k'));
        EntitiesToSymbol.map.put("lArr", new Character('\u00dc'));
        EntitiesToSymbol.map.put("lambda", new Character('l'));
        EntitiesToSymbol.map.put("lang", new Character('\u00e1'));
        EntitiesToSymbol.map.put("large brace extender", new Character('\u00ef'));
        EntitiesToSymbol.map.put("large integral extender", new Character('\u00f4'));
        EntitiesToSymbol.map.put("large left brace (bottom)", new Character('\u00ee'));
        EntitiesToSymbol.map.put("large left brace (middle)", new Character('\u00ed'));
        EntitiesToSymbol.map.put("large left brace (top)", new Character('\u00ec'));
        EntitiesToSymbol.map.put("large left bracket (bottom)", new Character('\u00eb'));
        EntitiesToSymbol.map.put("large left bracket (extender)", new Character('\u00ea'));
        EntitiesToSymbol.map.put("large left bracket (top)", new Character('\u00e9'));
        EntitiesToSymbol.map.put("large left parenthesis (bottom)", new Character('\u00e8'));
        EntitiesToSymbol.map.put("large left parenthesis (extender)", new Character('\u00e7'));
        EntitiesToSymbol.map.put("large left parenthesis (top)", new Character('\u00e6'));
        EntitiesToSymbol.map.put("large right brace (bottom)", new Character('\u00fe'));
        EntitiesToSymbol.map.put("large right brace (middle)", new Character('\u00fd'));
        EntitiesToSymbol.map.put("large right brace (top)", new Character('\u00fc'));
        EntitiesToSymbol.map.put("large right bracket (bottom)", new Character('\u00fb'));
        EntitiesToSymbol.map.put("large right bracket (extender)", new Character('\u00fa'));
        EntitiesToSymbol.map.put("large right bracket (top)", new Character('\u00f9'));
        EntitiesToSymbol.map.put("large right parenthesis (bottom)", new Character('\u00f8'));
        EntitiesToSymbol.map.put("large right parenthesis (extender)", new Character('\u00f7'));
        EntitiesToSymbol.map.put("large right parenthesis (top)", new Character('\u00f6'));
        EntitiesToSymbol.map.put("larr", new Character('�'));
        EntitiesToSymbol.map.put("le", new Character('�'));
        EntitiesToSymbol.map.put("lowast", new Character('*'));
        EntitiesToSymbol.map.put("loz", new Character('\u00e0'));
        EntitiesToSymbol.map.put("minus", new Character('-'));
        EntitiesToSymbol.map.put("mu", new Character('m'));
        EntitiesToSymbol.map.put("nabla", new Character('\u00d1'));
        EntitiesToSymbol.map.put("ne", new Character('�'));
        EntitiesToSymbol.map.put("not", new Character('\u00d8'));
        EntitiesToSymbol.map.put("notin", new Character('\u00cf'));
        EntitiesToSymbol.map.put("nsub", new Character('\u00cb'));
        EntitiesToSymbol.map.put("nu", new Character('n'));
        EntitiesToSymbol.map.put("omega", new Character('w'));
        EntitiesToSymbol.map.put("omicron", new Character('o'));
        EntitiesToSymbol.map.put("oplus", new Character('\u00c5'));
        EntitiesToSymbol.map.put("or", new Character('\u00da'));
        EntitiesToSymbol.map.put("otimes", new Character('\u00c4'));
        EntitiesToSymbol.map.put("part", new Character('�'));
        EntitiesToSymbol.map.put("perp", new Character('^'));
        EntitiesToSymbol.map.put("phi", new Character('f'));
        EntitiesToSymbol.map.put("pi", new Character('p'));
        EntitiesToSymbol.map.put("piv", new Character('v'));
        EntitiesToSymbol.map.put("plusmn", new Character('�'));
        EntitiesToSymbol.map.put("prime", new Character('�'));
        EntitiesToSymbol.map.put("prod", new Character('\u00d5'));
        EntitiesToSymbol.map.put("prop", new Character('�'));
        EntitiesToSymbol.map.put("psi", new Character('y'));
        EntitiesToSymbol.map.put("rArr", new Character('\u00de'));
        EntitiesToSymbol.map.put("radic", new Character('\u00d6'));
        EntitiesToSymbol.map.put("radical extender", new Character('`'));
        EntitiesToSymbol.map.put("rang", new Character('\u00f1'));
        EntitiesToSymbol.map.put("rarr", new Character('�'));
        EntitiesToSymbol.map.put("real", new Character('\u00c2'));
        EntitiesToSymbol.map.put("reg", new Character('\u00d2'));
        EntitiesToSymbol.map.put("rho", new Character('r'));
        EntitiesToSymbol.map.put("sdot", new Character('\u00d7'));
        EntitiesToSymbol.map.put("sigma", new Character('s'));
        EntitiesToSymbol.map.put("sigmaf", new Character('V'));
        EntitiesToSymbol.map.put("sim", new Character('~'));
        EntitiesToSymbol.map.put("spades", new Character('�'));
        EntitiesToSymbol.map.put("sub", new Character('\u00cc'));
        EntitiesToSymbol.map.put("sube", new Character('\u00cd'));
        EntitiesToSymbol.map.put("sum", new Character('\u00e5'));
        EntitiesToSymbol.map.put("sup", new Character('\u00c9'));
        EntitiesToSymbol.map.put("supe", new Character('\u00ca'));
        EntitiesToSymbol.map.put("tau", new Character('t'));
        EntitiesToSymbol.map.put("there4", new Character('\\'));
        EntitiesToSymbol.map.put("theta", new Character('q'));
        EntitiesToSymbol.map.put("thetasym", new Character('J'));
        EntitiesToSymbol.map.put("times", new Character('�'));
        EntitiesToSymbol.map.put("trade", new Character('\u00d4'));
        EntitiesToSymbol.map.put("uArr", new Character('\u00dd'));
        EntitiesToSymbol.map.put("uarr", new Character('\u00ad'));
        EntitiesToSymbol.map.put("upsih", new Character('�'));
        EntitiesToSymbol.map.put("upsilon", new Character('u'));
        EntitiesToSymbol.map.put("vertical arrow extender", new Character('�'));
        EntitiesToSymbol.map.put("weierp", new Character('\u00c3'));
        EntitiesToSymbol.map.put("xi", new Character('x'));
        EntitiesToSymbol.map.put("zeta", new Character('z'));
    }
}