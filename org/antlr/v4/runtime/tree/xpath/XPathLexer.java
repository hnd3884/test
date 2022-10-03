package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.Lexer;

public class XPathLexer extends Lexer
{
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int TOKEN_REF = 1;
    public static final int RULE_REF = 2;
    public static final int ANYWHERE = 3;
    public static final int ROOT = 4;
    public static final int WILDCARD = 5;
    public static final int BANG = 6;
    public static final int ID = 7;
    public static final int STRING = 8;
    public static String[] modeNames;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0002\n4\b\u0001\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0007\u0006\u001f\n\u0006\f\u0006\u000e\u0006\"\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0005\u0007(\n\u0007\u0003\b\u0003\b\u0003\t\u0003\t\u0007\t.\n\t\f\t\u000e\t1\u000b\t\u0003\t\u0003\t\u0003/\u0002\n\u0003\u0005\u0005\u0006\u0007\u0007\t\b\u000b\t\r\u0002\u000f\u0002\u0011\n\u0003\u0002\u0004\u0007\u00022;aa¹¹\u0302\u0371\u2041\u2042\u000f\u0002C\\c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff4\u0002\u0003\u0003\u0002\u0002\u0002\u0002\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002\u0002\u0002\t\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002\u0002\u0002\u0002\u0011\u0003\u0002\u0002\u0002\u0003\u0013\u0003\u0002\u0002\u0002\u0005\u0016\u0003\u0002\u0002\u0002\u0007\u0018\u0003\u0002\u0002\u0002\t\u001a\u0003\u0002\u0002\u0002\u000b\u001c\u0003\u0002\u0002\u0002\r'\u0003\u0002\u0002\u0002\u000f)\u0003\u0002\u0002\u0002\u0011+\u0003\u0002\u0002\u0002\u0013\u0014\u00071\u0002\u0002\u0014\u0015\u00071\u0002\u0002\u0015\u0004\u0003\u0002\u0002\u0002\u0016\u0017\u00071\u0002\u0002\u0017\u0006\u0003\u0002\u0002\u0002\u0018\u0019\u0007,\u0002\u0002\u0019\b\u0003\u0002\u0002\u0002\u001a\u001b\u0007#\u0002\u0002\u001b\n\u0003\u0002\u0002\u0002\u001c \u0005\u000f\b\u0002\u001d\u001f\u0005\r\u0007\u0002\u001e\u001d\u0003\u0002\u0002\u0002\u001f\"\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002 !\u0003\u0002\u0002\u0002!#\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002#$\b\u0006\u0002\u0002$\f\u0003\u0002\u0002\u0002%(\u0005\u000f\b\u0002&(\t\u0002\u0002\u0002'%\u0003\u0002\u0002\u0002'&\u0003\u0002\u0002\u0002(\u000e\u0003\u0002\u0002\u0002)*\t\u0003\u0002\u0002*\u0010\u0003\u0002\u0002\u0002+/\u0007)\u0002\u0002,.\u000b\u0002\u0002\u0002-,\u0003\u0002\u0002\u0002.1\u0003\u0002\u0002\u0002/0\u0003\u0002\u0002\u0002/-\u0003\u0002\u0002\u000202\u0003\u0002\u0002\u00021/\u0003\u0002\u0002\u000223\u0007)\u0002\u00023\u0012\u0003\u0002\u0002\u0002\u0006\u0002 '/\u0003\u0003\u0006\u0002";
    public static final ATN _ATN;
    
    @Deprecated
    @Override
    public String[] getTokenNames() {
        return XPathLexer.tokenNames;
    }
    
    @Override
    public Vocabulary getVocabulary() {
        return XPathLexer.VOCABULARY;
    }
    
    public XPathLexer(final CharStream input) {
        super(input);
        this._interp = (ATNInterpreter)new LexerATNSimulator(this, XPathLexer._ATN, XPathLexer._decisionToDFA, XPathLexer._sharedContextCache);
    }
    
    @Override
    public String getGrammarFileName() {
        return "XPathLexer.g4";
    }
    
    @Override
    public String[] getRuleNames() {
        return XPathLexer.ruleNames;
    }
    
    @Override
    public String getSerializedATN() {
        return "\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0002\n4\b\u0001\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0007\u0006\u001f\n\u0006\f\u0006\u000e\u0006\"\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0005\u0007(\n\u0007\u0003\b\u0003\b\u0003\t\u0003\t\u0007\t.\n\t\f\t\u000e\t1\u000b\t\u0003\t\u0003\t\u0003/\u0002\n\u0003\u0005\u0005\u0006\u0007\u0007\t\b\u000b\t\r\u0002\u000f\u0002\u0011\n\u0003\u0002\u0004\u0007\u00022;aa¹¹\u0302\u0371\u2041\u2042\u000f\u0002C\\c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff4\u0002\u0003\u0003\u0002\u0002\u0002\u0002\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002\u0002\u0002\t\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002\u0002\u0002\u0002\u0011\u0003\u0002\u0002\u0002\u0003\u0013\u0003\u0002\u0002\u0002\u0005\u0016\u0003\u0002\u0002\u0002\u0007\u0018\u0003\u0002\u0002\u0002\t\u001a\u0003\u0002\u0002\u0002\u000b\u001c\u0003\u0002\u0002\u0002\r'\u0003\u0002\u0002\u0002\u000f)\u0003\u0002\u0002\u0002\u0011+\u0003\u0002\u0002\u0002\u0013\u0014\u00071\u0002\u0002\u0014\u0015\u00071\u0002\u0002\u0015\u0004\u0003\u0002\u0002\u0002\u0016\u0017\u00071\u0002\u0002\u0017\u0006\u0003\u0002\u0002\u0002\u0018\u0019\u0007,\u0002\u0002\u0019\b\u0003\u0002\u0002\u0002\u001a\u001b\u0007#\u0002\u0002\u001b\n\u0003\u0002\u0002\u0002\u001c \u0005\u000f\b\u0002\u001d\u001f\u0005\r\u0007\u0002\u001e\u001d\u0003\u0002\u0002\u0002\u001f\"\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002 !\u0003\u0002\u0002\u0002!#\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002#$\b\u0006\u0002\u0002$\f\u0003\u0002\u0002\u0002%(\u0005\u000f\b\u0002&(\t\u0002\u0002\u0002'%\u0003\u0002\u0002\u0002'&\u0003\u0002\u0002\u0002(\u000e\u0003\u0002\u0002\u0002)*\t\u0003\u0002\u0002*\u0010\u0003\u0002\u0002\u0002+/\u0007)\u0002\u0002,.\u000b\u0002\u0002\u0002-,\u0003\u0002\u0002\u0002.1\u0003\u0002\u0002\u0002/0\u0003\u0002\u0002\u0002/-\u0003\u0002\u0002\u000202\u0003\u0002\u0002\u00021/\u0003\u0002\u0002\u000223\u0007)\u0002\u00023\u0012\u0003\u0002\u0002\u0002\u0006\u0002 '/\u0003\u0003\u0006\u0002";
    }
    
    @Override
    public String[] getModeNames() {
        return XPathLexer.modeNames;
    }
    
    @Override
    public ATN getATN() {
        return XPathLexer._ATN;
    }
    
    @Override
    public void action(final RuleContext _localctx, final int ruleIndex, final int actionIndex) {
        switch (ruleIndex) {
            case 4: {
                this.ID_action(_localctx, actionIndex);
                break;
            }
        }
    }
    
    private void ID_action(final RuleContext _localctx, final int actionIndex) {
        switch (actionIndex) {
            case 0: {
                final String text = this.getText();
                if (Character.isUpperCase(text.charAt(0))) {
                    this.setType(1);
                    break;
                }
                this.setType(2);
                break;
            }
        }
    }
    
    static {
        RuntimeMetaData.checkVersion("4.5", "4.5.3");
        _sharedContextCache = new PredictionContextCache();
        XPathLexer.modeNames = new String[] { "DEFAULT_MODE" };
        ruleNames = new String[] { "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar", "STRING" };
        _LITERAL_NAMES = new String[] { null, null, null, "'//'", "'/'", "'*'", "'!'" };
        _SYMBOLIC_NAMES = new String[] { null, "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "STRING" };
        VOCABULARY = new VocabularyImpl(XPathLexer._LITERAL_NAMES, XPathLexer._SYMBOLIC_NAMES);
        tokenNames = new String[XPathLexer._SYMBOLIC_NAMES.length];
        for (int i = 0; i < XPathLexer.tokenNames.length; ++i) {
            XPathLexer.tokenNames[i] = XPathLexer.VOCABULARY.getLiteralName(i);
            if (XPathLexer.tokenNames[i] == null) {
                XPathLexer.tokenNames[i] = XPathLexer.VOCABULARY.getSymbolicName(i);
            }
            if (XPathLexer.tokenNames[i] == null) {
                XPathLexer.tokenNames[i] = "<INVALID>";
            }
        }
        _ATN = new ATNDeserializer().deserialize("\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0002\n4\b\u0001\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0007\u0006\u001f\n\u0006\f\u0006\u000e\u0006\"\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0005\u0007(\n\u0007\u0003\b\u0003\b\u0003\t\u0003\t\u0007\t.\n\t\f\t\u000e\t1\u000b\t\u0003\t\u0003\t\u0003/\u0002\n\u0003\u0005\u0005\u0006\u0007\u0007\t\b\u000b\t\r\u0002\u000f\u0002\u0011\n\u0003\u0002\u0004\u0007\u00022;aa¹¹\u0302\u0371\u2041\u2042\u000f\u0002C\\c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff4\u0002\u0003\u0003\u0002\u0002\u0002\u0002\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002\u0002\u0002\t\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002\u0002\u0002\u0002\u0011\u0003\u0002\u0002\u0002\u0003\u0013\u0003\u0002\u0002\u0002\u0005\u0016\u0003\u0002\u0002\u0002\u0007\u0018\u0003\u0002\u0002\u0002\t\u001a\u0003\u0002\u0002\u0002\u000b\u001c\u0003\u0002\u0002\u0002\r'\u0003\u0002\u0002\u0002\u000f)\u0003\u0002\u0002\u0002\u0011+\u0003\u0002\u0002\u0002\u0013\u0014\u00071\u0002\u0002\u0014\u0015\u00071\u0002\u0002\u0015\u0004\u0003\u0002\u0002\u0002\u0016\u0017\u00071\u0002\u0002\u0017\u0006\u0003\u0002\u0002\u0002\u0018\u0019\u0007,\u0002\u0002\u0019\b\u0003\u0002\u0002\u0002\u001a\u001b\u0007#\u0002\u0002\u001b\n\u0003\u0002\u0002\u0002\u001c \u0005\u000f\b\u0002\u001d\u001f\u0005\r\u0007\u0002\u001e\u001d\u0003\u0002\u0002\u0002\u001f\"\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002 !\u0003\u0002\u0002\u0002!#\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002#$\b\u0006\u0002\u0002$\f\u0003\u0002\u0002\u0002%(\u0005\u000f\b\u0002&(\t\u0002\u0002\u0002'%\u0003\u0002\u0002\u0002'&\u0003\u0002\u0002\u0002(\u000e\u0003\u0002\u0002\u0002)*\t\u0003\u0002\u0002*\u0010\u0003\u0002\u0002\u0002+/\u0007)\u0002\u0002,.\u000b\u0002\u0002\u0002-,\u0003\u0002\u0002\u0002.1\u0003\u0002\u0002\u0002/0\u0003\u0002\u0002\u0002/-\u0003\u0002\u0002\u000202\u0003\u0002\u0002\u00021/\u0003\u0002\u0002\u000223\u0007)\u0002\u00023\u0012\u0003\u0002\u0002\u0002\u0006\u0002 '/\u0003\u0003\u0006\u0002".toCharArray());
        _decisionToDFA = new DFA[XPathLexer._ATN.getNumberOfDecisions()];
        for (int i = 0; i < XPathLexer._ATN.getNumberOfDecisions(); ++i) {
            XPathLexer._decisionToDFA[i] = new DFA(XPathLexer._ATN.getDecisionState(i), i);
        }
    }
}
