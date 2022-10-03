package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.RuntimeMetaData;
import com.github.odiszapc.nginxparser.NgxIfBlock;
import java.util.ArrayList;
import com.github.odiszapc.nginxparser.NgxToken;
import java.util.Collection;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxParam;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import com.github.odiszapc.nginxparser.NgxComment;
import com.github.odiszapc.nginxparser.NgxEntry;
import com.github.odiszapc.nginxparser.NgxConfig;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.Parser;

public class NginxParser extends Parser
{
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int T__1 = 2;
    public static final int T__2 = 3;
    public static final int T__3 = 4;
    public static final int T__4 = 5;
    public static final int T__5 = 6;
    public static final int T__6 = 7;
    public static final int T__7 = 8;
    public static final int T__8 = 9;
    public static final int T__9 = 10;
    public static final int T__10 = 11;
    public static final int T__11 = 12;
    public static final int T__12 = 13;
    public static final int T__13 = 14;
    public static final int Value = 15;
    public static final int STR_EXT = 16;
    public static final int Comment = 17;
    public static final int REGEXP_PREFIXED = 18;
    public static final int QUOTED_STRING = 19;
    public static final int SINGLE_QUOTED = 20;
    public static final int WS = 21;
    public static final int RULE_config = 0;
    public static final int RULE_statement = 1;
    public static final int RULE_genericStatement = 2;
    public static final int RULE_regexHeaderStatement = 3;
    public static final int RULE_block = 4;
    public static final int RULE_genericBlockHeader = 5;
    public static final int RULE_if_statement = 6;
    public static final int RULE_if_body = 7;
    public static final int RULE_regexp = 8;
    public static final int RULE_locationBlockHeader = 9;
    public static final int RULE_rewriteStatement = 10;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0003\u0017º\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0006\u0002!\n\u0002\r\u0002\u000e\u0002\"\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003.\n\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u00049\n\u0004\f\u0004\u000e\u0004<\u000b\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006I\n\u0006\u0003\u0006\u0005\u0006L\n\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Z\n\u0006\f\u0006\u000e\u0006]\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0007\u0007h\n\u0007\f\u0007\u000e\u0007k\u000b\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0005\br\n\b\u0003\b\u0003\b\u0003\b\u0003\b\u0007\bx\n\b\f\b\u000e\b{\u000b\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u0084\n\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u008b\n\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0006\n\u009a\n\n\r\n\u000e\n\u009b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b¢\n\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b©\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f²\n\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f¸\n\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0003\u0003\u0002\r\u0010\u00ca\u0002 \u0003\u0002\u0002\u0002\u0004-\u0003\u0002\u0002\u0002\u00061\u0003\u0002\u0002\u0002\b=\u0003\u0002\u0002\u0002\nH\u0003\u0002\u0002\u0002\f`\u0003\u0002\u0002\u0002\u000el\u0003\u0002\u0002\u0002\u0010~\u0003\u0002\u0002\u0002\u0012\u0099\u0003\u0002\u0002\u0002\u0014\u009d\u0003\u0002\u0002\u0002\u0016ª\u0003\u0002\u0002\u0002\u0018\u0019\u0005\u0004\u0003\u0002\u0019\u001a\b\u0002\u0001\u0002\u001a!\u0003\u0002\u0002\u0002\u001b\u001c\u0005\n\u0006\u0002\u001c\u001d\b\u0002\u0001\u0002\u001d!\u0003\u0002\u0002\u0002\u001e\u001f\u0007\u0013\u0002\u0002\u001f!\b\u0002\u0001\u0002 \u0018\u0003\u0002\u0002\u0002 \u001b\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002!\"\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#\u0003\u0003\u0002\u0002\u0002$%\u0005\u0016\f\u0002%&\b\u0003\u0001\u0002&.\u0003\u0002\u0002\u0002'(\u0005\u0006\u0004\u0002()\b\u0003\u0001\u0002).\u0003\u0002\u0002\u0002*+\u0005\b\u0005\u0002+,\b\u0003\u0001\u0002,.\u0003\u0002\u0002\u0002-$\u0003\u0002\u0002\u0002-'\u0003\u0002\u0002\u0002-*\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/0\u0007\u0003\u0002\u00020\u0005\u0003\u0002\u0002\u000212\u0007\u0011\u0002\u00022:\b\u0004\u0001\u000234\u0007\u0011\u0002\u000249\b\u0004\u0001\u000256\u0005\u0012\n\u000267\b\u0004\u0001\u000279\u0003\u0002\u0002\u000283\u0003\u0002\u0002\u000285\u0003\u0002\u0002\u00029<\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002:;\u0003\u0002\u0002\u0002;\u0007\u0003\u0002\u0002\u0002<:\u0003\u0002\u0002\u0002=>\u0007\u0014\u0002\u0002>?\b\u0005\u0001\u0002?@\u0007\u0011\u0002\u0002@A\b\u0005\u0001\u0002A\t\u0003\u0002\u0002\u0002BC\u0005\u0014\u000b\u0002CD\b\u0006\u0001\u0002DI\u0003\u0002\u0002\u0002EF\u0005\f\u0007\u0002FG\b\u0006\u0001\u0002GI\u0003\u0002\u0002\u0002HB\u0003\u0002\u0002\u0002HE\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0007\u0013\u0002\u0002KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LM\u0003\u0002\u0002\u0002M[\u0007\u0004\u0002\u0002NO\u0005\u0004\u0003\u0002OP\b\u0006\u0001\u0002PZ\u0003\u0002\u0002\u0002QR\u0005\n\u0006\u0002RS\b\u0006\u0001\u0002SZ\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0006\u0001\u0002VZ\u0003\u0002\u0002\u0002WX\u0007\u0013\u0002\u0002XZ\b\u0006\u0001\u0002YN\u0003\u0002\u0002\u0002YQ\u0003\u0002\u0002\u0002YT\u0003\u0002\u0002\u0002YW\u0003\u0002\u0002\u0002Z]\u0003\u0002\u0002\u0002[Y\u0003\u0002\u0002\u0002[\\\u0003\u0002\u0002\u0002\\^\u0003\u0002\u0002\u0002][\u0003\u0002\u0002\u0002^_\u0007\u0005\u0002\u0002_\u000b\u0003\u0002\u0002\u0002`a\u0007\u0011\u0002\u0002ai\b\u0007\u0001\u0002bc\u0007\u0011\u0002\u0002ch\b\u0007\u0001\u0002de\u0005\u0012\n\u0002ef\b\u0007\u0001\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gd\u0003\u0002\u0002\u0002hk\u0003\u0002\u0002\u0002ig\u0003\u0002\u0002\u0002ij\u0003\u0002\u0002\u0002j\r\u0003\u0002\u0002\u0002ki\u0003\u0002\u0002\u0002lm\u0007\u0006\u0002\u0002mn\b\b\u0001\u0002no\u0005\u0010\t\u0002oq\b\b\u0001\u0002pr\u0007\u0013\u0002\u0002qp\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002rs\u0003\u0002\u0002\u0002sy\u0007\u0004\u0002\u0002tu\u0005\u0004\u0003\u0002uv\b\b\u0001\u0002vx\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z|\u0003\u0002\u0002\u0002{y\u0003\u0002\u0002\u0002|}\u0007\u0005\u0002\u0002}\u000f\u0003\u0002\u0002\u0002~\u007f\u0007\u0007\u0002\u0002\u007f\u0080\u0007\u0011\u0002\u0002\u0080\u0083\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082\u0084\b\t\u0001\u0002\u0083\u0081\u0003\u0002\u0002\u0002\u0083\u0084\u0003\u0002\u0002\u0002\u0084\u008a\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0011\u0002\u0002\u0086\u008b\b\t\u0001\u0002\u0087\u0088\u0005\u0012\n\u0002\u0088\u0089\b\t\u0001\u0002\u0089\u008b\u0003\u0002\u0002\u0002\u008a\u0085\u0003\u0002\u0002\u0002\u008a\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b\u008c\u0003\u0002\u0002\u0002\u008c\u008d\u0007\b\u0002\u0002\u008d\u0011\u0003\u0002\u0002\u0002\u008e\u008f\u0007\t\u0002\u0002\u008f\u009a\b\n\u0001\u0002\u0090\u0091\u0007\n\u0002\u0002\u0091\u009a\b\n\u0001\u0002\u0092\u0093\u0007\u0011\u0002\u0002\u0093\u009a\b\n\u0001\u0002\u0094\u0095\u0007\u0007\u0002\u0002\u0095\u0096\u0005\u0012\n\u0002\u0096\u0097\b\n\u0001\u0002\u0097\u0098\u0007\b\u0002\u0002\u0098\u009a\u0003\u0002\u0002\u0002\u0099\u008e\u0003\u0002\u0002\u0002\u0099\u0090\u0003\u0002\u0002\u0002\u0099\u0092\u0003\u0002\u0002\u0002\u0099\u0094\u0003\u0002\u0002\u0002\u009a\u009b\u0003\u0002\u0002\u0002\u009b\u0099\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u0013\u0003\u0002\u0002\u0002\u009d\u009e\u0007\u000b\u0002\u0002\u009e¡\b\u000b\u0001\u0002\u009f \u0007\u0011\u0002\u0002 ¢\b\u000b\u0001\u0002¡\u009f\u0003\u0002\u0002\u0002¡¢\u0003\u0002\u0002\u0002¢¨\u0003\u0002\u0002\u0002£¤\u0007\u0011\u0002\u0002¤©\b\u000b\u0001\u0002¥¦\u0005\u0012\n\u0002¦§\b\u000b\u0001\u0002§©\u0003\u0002\u0002\u0002¨£\u0003\u0002\u0002\u0002¨¥\u0003\u0002\u0002\u0002©\u0015\u0003\u0002\u0002\u0002ª«\u0007\f\u0002\u0002«±\b\f\u0001\u0002¬\u00ad\u0007\u0011\u0002\u0002\u00ad²\b\f\u0001\u0002®¯\u0005\u0012\n\u0002¯°\b\f\u0001\u0002°²\u0003\u0002\u0002\u0002±¬\u0003\u0002\u0002\u0002±®\u0003\u0002\u0002\u0002²³\u0003\u0002\u0002\u0002³´\u0007\u0011\u0002\u0002´·\b\f\u0001\u0002µ¶\t\u0002\u0002\u0002¶¸\b\f\u0001\u0002·µ\u0003\u0002\u0002\u0002·¸\u0003\u0002\u0002\u0002¸\u0017\u0003\u0002\u0002\u0002\u0017 \"-8:HKY[giqy\u0083\u008a\u0099\u009b¡¨±·";
    public static final ATN _ATN;
    
    @Deprecated
    public String[] getTokenNames() {
        return NginxParser.tokenNames;
    }
    
    public Vocabulary getVocabulary() {
        return NginxParser.VOCABULARY;
    }
    
    public String getGrammarFileName() {
        return "Nginx.g4";
    }
    
    public String[] getRuleNames() {
        return NginxParser.ruleNames;
    }
    
    public String getSerializedATN() {
        return "\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0003\u0017º\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0006\u0002!\n\u0002\r\u0002\u000e\u0002\"\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003.\n\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u00049\n\u0004\f\u0004\u000e\u0004<\u000b\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006I\n\u0006\u0003\u0006\u0005\u0006L\n\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Z\n\u0006\f\u0006\u000e\u0006]\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0007\u0007h\n\u0007\f\u0007\u000e\u0007k\u000b\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0005\br\n\b\u0003\b\u0003\b\u0003\b\u0003\b\u0007\bx\n\b\f\b\u000e\b{\u000b\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u0084\n\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u008b\n\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0006\n\u009a\n\n\r\n\u000e\n\u009b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b¢\n\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b©\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f²\n\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f¸\n\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0003\u0003\u0002\r\u0010\u00ca\u0002 \u0003\u0002\u0002\u0002\u0004-\u0003\u0002\u0002\u0002\u00061\u0003\u0002\u0002\u0002\b=\u0003\u0002\u0002\u0002\nH\u0003\u0002\u0002\u0002\f`\u0003\u0002\u0002\u0002\u000el\u0003\u0002\u0002\u0002\u0010~\u0003\u0002\u0002\u0002\u0012\u0099\u0003\u0002\u0002\u0002\u0014\u009d\u0003\u0002\u0002\u0002\u0016ª\u0003\u0002\u0002\u0002\u0018\u0019\u0005\u0004\u0003\u0002\u0019\u001a\b\u0002\u0001\u0002\u001a!\u0003\u0002\u0002\u0002\u001b\u001c\u0005\n\u0006\u0002\u001c\u001d\b\u0002\u0001\u0002\u001d!\u0003\u0002\u0002\u0002\u001e\u001f\u0007\u0013\u0002\u0002\u001f!\b\u0002\u0001\u0002 \u0018\u0003\u0002\u0002\u0002 \u001b\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002!\"\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#\u0003\u0003\u0002\u0002\u0002$%\u0005\u0016\f\u0002%&\b\u0003\u0001\u0002&.\u0003\u0002\u0002\u0002'(\u0005\u0006\u0004\u0002()\b\u0003\u0001\u0002).\u0003\u0002\u0002\u0002*+\u0005\b\u0005\u0002+,\b\u0003\u0001\u0002,.\u0003\u0002\u0002\u0002-$\u0003\u0002\u0002\u0002-'\u0003\u0002\u0002\u0002-*\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/0\u0007\u0003\u0002\u00020\u0005\u0003\u0002\u0002\u000212\u0007\u0011\u0002\u00022:\b\u0004\u0001\u000234\u0007\u0011\u0002\u000249\b\u0004\u0001\u000256\u0005\u0012\n\u000267\b\u0004\u0001\u000279\u0003\u0002\u0002\u000283\u0003\u0002\u0002\u000285\u0003\u0002\u0002\u00029<\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002:;\u0003\u0002\u0002\u0002;\u0007\u0003\u0002\u0002\u0002<:\u0003\u0002\u0002\u0002=>\u0007\u0014\u0002\u0002>?\b\u0005\u0001\u0002?@\u0007\u0011\u0002\u0002@A\b\u0005\u0001\u0002A\t\u0003\u0002\u0002\u0002BC\u0005\u0014\u000b\u0002CD\b\u0006\u0001\u0002DI\u0003\u0002\u0002\u0002EF\u0005\f\u0007\u0002FG\b\u0006\u0001\u0002GI\u0003\u0002\u0002\u0002HB\u0003\u0002\u0002\u0002HE\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0007\u0013\u0002\u0002KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LM\u0003\u0002\u0002\u0002M[\u0007\u0004\u0002\u0002NO\u0005\u0004\u0003\u0002OP\b\u0006\u0001\u0002PZ\u0003\u0002\u0002\u0002QR\u0005\n\u0006\u0002RS\b\u0006\u0001\u0002SZ\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0006\u0001\u0002VZ\u0003\u0002\u0002\u0002WX\u0007\u0013\u0002\u0002XZ\b\u0006\u0001\u0002YN\u0003\u0002\u0002\u0002YQ\u0003\u0002\u0002\u0002YT\u0003\u0002\u0002\u0002YW\u0003\u0002\u0002\u0002Z]\u0003\u0002\u0002\u0002[Y\u0003\u0002\u0002\u0002[\\\u0003\u0002\u0002\u0002\\^\u0003\u0002\u0002\u0002][\u0003\u0002\u0002\u0002^_\u0007\u0005\u0002\u0002_\u000b\u0003\u0002\u0002\u0002`a\u0007\u0011\u0002\u0002ai\b\u0007\u0001\u0002bc\u0007\u0011\u0002\u0002ch\b\u0007\u0001\u0002de\u0005\u0012\n\u0002ef\b\u0007\u0001\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gd\u0003\u0002\u0002\u0002hk\u0003\u0002\u0002\u0002ig\u0003\u0002\u0002\u0002ij\u0003\u0002\u0002\u0002j\r\u0003\u0002\u0002\u0002ki\u0003\u0002\u0002\u0002lm\u0007\u0006\u0002\u0002mn\b\b\u0001\u0002no\u0005\u0010\t\u0002oq\b\b\u0001\u0002pr\u0007\u0013\u0002\u0002qp\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002rs\u0003\u0002\u0002\u0002sy\u0007\u0004\u0002\u0002tu\u0005\u0004\u0003\u0002uv\b\b\u0001\u0002vx\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z|\u0003\u0002\u0002\u0002{y\u0003\u0002\u0002\u0002|}\u0007\u0005\u0002\u0002}\u000f\u0003\u0002\u0002\u0002~\u007f\u0007\u0007\u0002\u0002\u007f\u0080\u0007\u0011\u0002\u0002\u0080\u0083\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082\u0084\b\t\u0001\u0002\u0083\u0081\u0003\u0002\u0002\u0002\u0083\u0084\u0003\u0002\u0002\u0002\u0084\u008a\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0011\u0002\u0002\u0086\u008b\b\t\u0001\u0002\u0087\u0088\u0005\u0012\n\u0002\u0088\u0089\b\t\u0001\u0002\u0089\u008b\u0003\u0002\u0002\u0002\u008a\u0085\u0003\u0002\u0002\u0002\u008a\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b\u008c\u0003\u0002\u0002\u0002\u008c\u008d\u0007\b\u0002\u0002\u008d\u0011\u0003\u0002\u0002\u0002\u008e\u008f\u0007\t\u0002\u0002\u008f\u009a\b\n\u0001\u0002\u0090\u0091\u0007\n\u0002\u0002\u0091\u009a\b\n\u0001\u0002\u0092\u0093\u0007\u0011\u0002\u0002\u0093\u009a\b\n\u0001\u0002\u0094\u0095\u0007\u0007\u0002\u0002\u0095\u0096\u0005\u0012\n\u0002\u0096\u0097\b\n\u0001\u0002\u0097\u0098\u0007\b\u0002\u0002\u0098\u009a\u0003\u0002\u0002\u0002\u0099\u008e\u0003\u0002\u0002\u0002\u0099\u0090\u0003\u0002\u0002\u0002\u0099\u0092\u0003\u0002\u0002\u0002\u0099\u0094\u0003\u0002\u0002\u0002\u009a\u009b\u0003\u0002\u0002\u0002\u009b\u0099\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u0013\u0003\u0002\u0002\u0002\u009d\u009e\u0007\u000b\u0002\u0002\u009e¡\b\u000b\u0001\u0002\u009f \u0007\u0011\u0002\u0002 ¢\b\u000b\u0001\u0002¡\u009f\u0003\u0002\u0002\u0002¡¢\u0003\u0002\u0002\u0002¢¨\u0003\u0002\u0002\u0002£¤\u0007\u0011\u0002\u0002¤©\b\u000b\u0001\u0002¥¦\u0005\u0012\n\u0002¦§\b\u000b\u0001\u0002§©\u0003\u0002\u0002\u0002¨£\u0003\u0002\u0002\u0002¨¥\u0003\u0002\u0002\u0002©\u0015\u0003\u0002\u0002\u0002ª«\u0007\f\u0002\u0002«±\b\f\u0001\u0002¬\u00ad\u0007\u0011\u0002\u0002\u00ad²\b\f\u0001\u0002®¯\u0005\u0012\n\u0002¯°\b\f\u0001\u0002°²\u0003\u0002\u0002\u0002±¬\u0003\u0002\u0002\u0002±®\u0003\u0002\u0002\u0002²³\u0003\u0002\u0002\u0002³´\u0007\u0011\u0002\u0002´·\b\f\u0001\u0002µ¶\t\u0002\u0002\u0002¶¸\b\f\u0001\u0002·µ\u0003\u0002\u0002\u0002·¸\u0003\u0002\u0002\u0002¸\u0017\u0003\u0002\u0002\u0002\u0017 \"-8:HKY[giqy\u0083\u008a\u0099\u009b¡¨±·";
    }
    
    public ATN getATN() {
        return NginxParser._ATN;
    }
    
    public NginxParser(final TokenStream tokenStream) {
        super(tokenStream);
        this._interp = (ATNSimulator)new ParserATNSimulator((Parser)this, NginxParser._ATN, NginxParser._decisionToDFA, NginxParser._sharedContextCache);
    }
    
    public final ConfigContext config() throws RecognitionException {
        final ConfigContext configContext = new ConfigContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)configContext, 0, 0);
        configContext.ret = new NgxConfig();
        try {
            this.enterOuterAlt((ParserRuleContext)configContext, 1);
            this.setState(30);
            this._errHandler.sync((Parser)this);
            this._input.LA(1);
            int la;
            do {
                this.setState(30);
                this._errHandler.sync((Parser)this);
                switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 0, this._ctx)) {
                    case 1: {
                        this.setState(22);
                        configContext.statement = this.statement();
                        configContext.ret.addEntry(configContext.statement.ret);
                        break;
                    }
                    case 2: {
                        this.setState(25);
                        configContext.block = this.block();
                        configContext.ret.addEntry(configContext.block.ret);
                        break;
                    }
                    case 3: {
                        this.setState(28);
                        configContext.Comment = this.match(17);
                        configContext.ret.addEntry(new NgxComment((configContext.Comment != null) ? configContext.Comment.getText() : null));
                        break;
                    }
                }
                this.setState(32);
                this._errHandler.sync((Parser)this);
                la = this._input.LA(1);
            } while ((la & 0xFFFFFFC0) == 0x0 && (1L << la & 0x68600L) != 0x0L);
        }
        catch (final RecognitionException exception) {
            configContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return configContext;
    }
    
    public final StatementContext statement() throws RecognitionException {
        final StatementContext statementContext = new StatementContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)statementContext, 2, 1);
        try {
            this.enterOuterAlt((ParserRuleContext)statementContext, 1);
            this.setState(43);
            switch (this._input.LA(1)) {
                case 10: {
                    this.setState(34);
                    statementContext.rewriteStatement = this.rewriteStatement();
                    statementContext.ret = statementContext.rewriteStatement.ret;
                    break;
                }
                case 15: {
                    this.setState(37);
                    statementContext.genericStatement = this.genericStatement();
                    statementContext.ret = statementContext.genericStatement.ret;
                    break;
                }
                case 18: {
                    this.setState(40);
                    statementContext.regexHeaderStatement = this.regexHeaderStatement();
                    statementContext.ret = statementContext.regexHeaderStatement.ret;
                    break;
                }
                default: {
                    throw new NoViableAltException((Parser)this);
                }
            }
            this.setState(45);
            this.match(1);
        }
        catch (final RecognitionException exception) {
            statementContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return statementContext;
    }
    
    public final GenericStatementContext genericStatement() throws RecognitionException {
        final GenericStatementContext genericStatementContext = new GenericStatementContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)genericStatementContext, 4, 2);
        genericStatementContext.ret = new NgxParam();
        try {
            this.enterOuterAlt((ParserRuleContext)genericStatementContext, 1);
            this.setState(47);
            genericStatementContext.Value = this.match(15);
            genericStatementContext.ret.addValue((genericStatementContext.Value != null) ? genericStatementContext.Value.getText() : null);
            this.setState(56);
            this._errHandler.sync((Parser)this);
            for (int n = this._input.LA(1); (n & 0xFFFFFFC0) == 0x0 && (1L << n & 0x81A0L) != 0x0L; n = this._input.LA(1)) {
                this.setState(54);
                this._errHandler.sync((Parser)this);
                switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 3, this._ctx)) {
                    case 1: {
                        this.setState(49);
                        genericStatementContext.Value = this.match(15);
                        genericStatementContext.ret.addValue((genericStatementContext.Value != null) ? genericStatementContext.Value.getText() : null);
                        break;
                    }
                    case 2: {
                        this.setState(51);
                        genericStatementContext.r = this.regexp();
                        genericStatementContext.ret.addValue(genericStatementContext.r.ret);
                        break;
                    }
                }
                this.setState(58);
                this._errHandler.sync((Parser)this);
            }
        }
        catch (final RecognitionException exception) {
            genericStatementContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return genericStatementContext;
    }
    
    public final RegexHeaderStatementContext regexHeaderStatement() throws RecognitionException {
        final RegexHeaderStatementContext regexHeaderStatementContext = new RegexHeaderStatementContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)regexHeaderStatementContext, 6, 3);
        regexHeaderStatementContext.ret = new NgxParam();
        try {
            this.enterOuterAlt((ParserRuleContext)regexHeaderStatementContext, 1);
            this.setState(59);
            regexHeaderStatementContext.REGEXP_PREFIXED = this.match(18);
            regexHeaderStatementContext.ret.addValue((regexHeaderStatementContext.REGEXP_PREFIXED != null) ? regexHeaderStatementContext.REGEXP_PREFIXED.getText() : null);
            this.setState(61);
            regexHeaderStatementContext.Value = this.match(15);
            regexHeaderStatementContext.ret.addValue((regexHeaderStatementContext.Value != null) ? regexHeaderStatementContext.Value.getText() : null);
        }
        catch (final RecognitionException exception) {
            regexHeaderStatementContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return regexHeaderStatementContext;
    }
    
    public final BlockContext block() throws RecognitionException {
        final BlockContext blockContext = new BlockContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)blockContext, 8, 4);
        blockContext.ret = new NgxBlock();
        try {
            this.enterOuterAlt((ParserRuleContext)blockContext, 1);
            this.setState(70);
            switch (this._input.LA(1)) {
                case 9: {
                    this.setState(64);
                    blockContext.locationBlockHeader = this.locationBlockHeader();
                    blockContext.ret.getTokens().addAll(blockContext.locationBlockHeader.ret);
                    break;
                }
                case 15: {
                    this.setState(67);
                    blockContext.genericBlockHeader = this.genericBlockHeader();
                    blockContext.ret.getTokens().addAll(blockContext.genericBlockHeader.ret);
                    break;
                }
                default: {
                    throw new NoViableAltException((Parser)this);
                }
            }
            this.setState(73);
            if (this._input.LA(1) == 17) {
                this.setState(72);
                blockContext.Comment = this.match(17);
            }
            this.setState(75);
            this.match(2);
            this.setState(89);
            this._errHandler.sync((Parser)this);
            for (int n = this._input.LA(1); (n & 0xFFFFFFC0) == 0x0 && (1L << n & 0x68610L) != 0x0L; n = this._input.LA(1)) {
                this.setState(87);
                this._errHandler.sync((Parser)this);
                switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 7, this._ctx)) {
                    case 1: {
                        this.setState(76);
                        blockContext.statement = this.statement();
                        blockContext.ret.addEntry(blockContext.statement.ret);
                        break;
                    }
                    case 2: {
                        this.setState(79);
                        blockContext.b = this.block();
                        blockContext.ret.addEntry(blockContext.b.ret);
                        break;
                    }
                    case 3: {
                        this.setState(82);
                        blockContext.if_statement = this.if_statement();
                        blockContext.ret.addEntry(blockContext.if_statement.ret);
                        break;
                    }
                    case 4: {
                        this.setState(85);
                        blockContext.Comment = this.match(17);
                        blockContext.ret.addEntry(new NgxComment((blockContext.Comment != null) ? blockContext.Comment.getText() : null));
                        break;
                    }
                }
                this.setState(91);
                this._errHandler.sync((Parser)this);
            }
            this.setState(92);
            this.match(3);
        }
        catch (final RecognitionException exception) {
            blockContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return blockContext;
    }
    
    public final GenericBlockHeaderContext genericBlockHeader() throws RecognitionException {
        final GenericBlockHeaderContext genericBlockHeaderContext = new GenericBlockHeaderContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)genericBlockHeaderContext, 10, 5);
        genericBlockHeaderContext.ret = new ArrayList<NgxToken>();
        try {
            this.enterOuterAlt((ParserRuleContext)genericBlockHeaderContext, 1);
            this.setState(94);
            genericBlockHeaderContext.Value = this.match(15);
            genericBlockHeaderContext.ret.add(new NgxToken((genericBlockHeaderContext.Value != null) ? genericBlockHeaderContext.Value.getText() : null));
            this.setState(103);
            this._errHandler.sync((Parser)this);
            for (int n = this._input.LA(1); (n & 0xFFFFFFC0) == 0x0 && (1L << n & 0x81A0L) != 0x0L; n = this._input.LA(1)) {
                this.setState(101);
                this._errHandler.sync((Parser)this);
                switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 9, this._ctx)) {
                    case 1: {
                        this.setState(96);
                        genericBlockHeaderContext.Value = this.match(15);
                        genericBlockHeaderContext.ret.add(new NgxToken((genericBlockHeaderContext.Value != null) ? genericBlockHeaderContext.Value.getText() : null));
                        break;
                    }
                    case 2: {
                        this.setState(98);
                        genericBlockHeaderContext.regexp = this.regexp();
                        genericBlockHeaderContext.ret.add(new NgxToken(genericBlockHeaderContext.regexp.ret));
                        break;
                    }
                }
                this.setState(105);
                this._errHandler.sync((Parser)this);
            }
        }
        catch (final RecognitionException exception) {
            genericBlockHeaderContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return genericBlockHeaderContext;
    }
    
    public final If_statementContext if_statement() throws RecognitionException {
        final If_statementContext if_statementContext = new If_statementContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)if_statementContext, 12, 6);
        if_statementContext.ret = new NgxIfBlock();
        try {
            this.enterOuterAlt((ParserRuleContext)if_statementContext, 1);
            this.setState(106);
            if_statementContext.id = this.match(4);
            if_statementContext.ret.addValue(new NgxToken((if_statementContext.id != null) ? if_statementContext.id.getText() : null));
            this.setState(108);
            if_statementContext.if_body = this.if_body();
            if_statementContext.ret.getTokens().addAll(if_statementContext.if_body.ret);
            this.setState(111);
            if (this._input.LA(1) == 17) {
                this.setState(110);
                this.match(17);
            }
            this.setState(113);
            this.match(2);
            this.setState(119);
            this._errHandler.sync((Parser)this);
            for (int n = this._input.LA(1); (n & 0xFFFFFFC0) == 0x0 && (1L << n & 0x48400L) != 0x0L; n = this._input.LA(1)) {
                this.setState(114);
                if_statementContext.statement = this.statement();
                if_statementContext.ret.addEntry(if_statementContext.statement.ret);
                this.setState(121);
                this._errHandler.sync((Parser)this);
            }
            this.setState(122);
            this.match(3);
        }
        catch (final RecognitionException exception) {
            if_statementContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return if_statementContext;
    }
    
    public final If_bodyContext if_body() throws RecognitionException {
        final If_bodyContext if_bodyContext = new If_bodyContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)if_bodyContext, 14, 7);
        if_bodyContext.ret = new ArrayList<NgxToken>();
        try {
            this.enterOuterAlt((ParserRuleContext)if_bodyContext, 1);
            this.setState(124);
            this.match(5);
            this.setState(125);
            if_bodyContext.Value = this.match(15);
            if_bodyContext.ret.add(new NgxToken((if_bodyContext.Value != null) ? if_bodyContext.Value.getText() : null));
            this.setState(129);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 13, this._ctx)) {
                case 1: {
                    this.setState(127);
                    if_bodyContext.Value = this.match(15);
                    if_bodyContext.ret.add(new NgxToken((if_bodyContext.Value != null) ? if_bodyContext.Value.getText() : null));
                    break;
                }
            }
            this.setState(136);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 14, this._ctx)) {
                case 1: {
                    this.setState(131);
                    if_bodyContext.Value = this.match(15);
                    if_bodyContext.ret.add(new NgxToken((if_bodyContext.Value != null) ? if_bodyContext.Value.getText() : null));
                    break;
                }
                case 2: {
                    this.setState(133);
                    if_bodyContext.regexp = this.regexp();
                    if_bodyContext.ret.add(new NgxToken(if_bodyContext.regexp.ret));
                    break;
                }
            }
            this.setState(138);
            this.match(6);
        }
        catch (final RecognitionException exception) {
            if_bodyContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return if_bodyContext;
    }
    
    public final RegexpContext regexp() throws RecognitionException {
        final RegexpContext regexpContext = new RegexpContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)regexpContext, 16, 8);
        regexpContext.ret = "";
        try {
            this.enterOuterAlt((ParserRuleContext)regexpContext, 1);
            this.setState(151);
            this._errHandler.sync((Parser)this);
            int adaptivePredict = 1;
            do {
                switch (adaptivePredict) {
                    case 1: {
                        this.setState(151);
                        switch (this._input.LA(1)) {
                            case 7: {
                                this.setState(140);
                                regexpContext.id = this.match(7);
                                final StringBuilder sb = new StringBuilder();
                                final RegexpContext regexpContext2 = regexpContext;
                                regexpContext2.ret = sb.append(regexpContext2.ret).append((regexpContext.id != null) ? regexpContext.id.getText() : null).toString();
                                break;
                            }
                            case 8: {
                                this.setState(142);
                                regexpContext.id = this.match(8);
                                final StringBuilder sb2 = new StringBuilder();
                                final RegexpContext regexpContext3 = regexpContext;
                                regexpContext3.ret = sb2.append(regexpContext3.ret).append((regexpContext.id != null) ? regexpContext.id.getText() : null).toString();
                                break;
                            }
                            case 15: {
                                this.setState(144);
                                regexpContext.Value = this.match(15);
                                final StringBuilder sb3 = new StringBuilder();
                                final RegexpContext regexpContext4 = regexpContext;
                                regexpContext4.ret = sb3.append(regexpContext4.ret).append((regexpContext.Value != null) ? regexpContext.Value.getText() : null).toString();
                                break;
                            }
                            case 5: {
                                this.setState(146);
                                this.match(5);
                                this.setState(147);
                                regexpContext.r = this.regexp();
                                final StringBuilder sb4 = new StringBuilder();
                                final RegexpContext regexpContext5 = regexpContext;
                                regexpContext5.ret = sb4.append(regexpContext5.ret).append("(".concat(regexpContext.r.ret).concat(")")).toString();
                                this.setState(149);
                                this.match(6);
                                break;
                            }
                            default: {
                                throw new NoViableAltException((Parser)this);
                            }
                        }
                        this.setState(153);
                        this._errHandler.sync((Parser)this);
                        adaptivePredict = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 16, this._ctx);
                        continue;
                    }
                    default: {
                        throw new NoViableAltException((Parser)this);
                    }
                }
            } while (adaptivePredict != 2 && adaptivePredict != 0);
        }
        catch (final RecognitionException exception) {
            regexpContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return regexpContext;
    }
    
    public final LocationBlockHeaderContext locationBlockHeader() throws RecognitionException {
        final LocationBlockHeaderContext locationBlockHeaderContext = new LocationBlockHeaderContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)locationBlockHeaderContext, 18, 9);
        locationBlockHeaderContext.ret = new ArrayList<NgxToken>();
        try {
            this.enterOuterAlt((ParserRuleContext)locationBlockHeaderContext, 1);
            this.setState(155);
            locationBlockHeaderContext.id = this.match(9);
            locationBlockHeaderContext.ret.add(new NgxToken((locationBlockHeaderContext.id != null) ? locationBlockHeaderContext.id.getText() : null));
            this.setState(159);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 17, this._ctx)) {
                case 1: {
                    this.setState(157);
                    locationBlockHeaderContext.Value = this.match(15);
                    locationBlockHeaderContext.ret.add(new NgxToken((locationBlockHeaderContext.Value != null) ? locationBlockHeaderContext.Value.getText() : null));
                    break;
                }
            }
            this.setState(166);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 18, this._ctx)) {
                case 1: {
                    this.setState(161);
                    locationBlockHeaderContext.Value = this.match(15);
                    locationBlockHeaderContext.ret.add(new NgxToken((locationBlockHeaderContext.Value != null) ? locationBlockHeaderContext.Value.getText() : null));
                    break;
                }
                case 2: {
                    this.setState(163);
                    locationBlockHeaderContext.regexp = this.regexp();
                    locationBlockHeaderContext.ret.add(new NgxToken(locationBlockHeaderContext.regexp.ret));
                    break;
                }
            }
        }
        catch (final RecognitionException exception) {
            locationBlockHeaderContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return locationBlockHeaderContext;
    }
    
    public final RewriteStatementContext rewriteStatement() throws RecognitionException {
        final RewriteStatementContext rewriteStatementContext = new RewriteStatementContext(this._ctx, this.getState());
        this.enterRule((ParserRuleContext)rewriteStatementContext, 20, 10);
        rewriteStatementContext.ret = new NgxParam();
        try {
            this.enterOuterAlt((ParserRuleContext)rewriteStatementContext, 1);
            this.setState(168);
            rewriteStatementContext.id = this.match(10);
            rewriteStatementContext.ret.addValue((rewriteStatementContext.id != null) ? rewriteStatementContext.id.getText() : null);
            this.setState(175);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 19, this._ctx)) {
                case 1: {
                    this.setState(170);
                    rewriteStatementContext.Value = this.match(15);
                    rewriteStatementContext.ret.addValue((rewriteStatementContext.Value != null) ? rewriteStatementContext.Value.getText() : null);
                    break;
                }
                case 2: {
                    this.setState(172);
                    rewriteStatementContext.regexp = this.regexp();
                    rewriteStatementContext.ret.addValue(rewriteStatementContext.regexp.ret);
                    break;
                }
            }
            this.setState(177);
            rewriteStatementContext.Value = this.match(15);
            rewriteStatementContext.ret.addValue((rewriteStatementContext.Value != null) ? rewriteStatementContext.Value.getText() : null);
            this.setState(181);
            final int la = this._input.LA(1);
            if ((la & 0xFFFFFFC0) == 0x0 && (1L << la & 0x7800L) != 0x0L) {
                this.setState(179);
                rewriteStatementContext.opt = this._input.LT(1);
                final int la2 = this._input.LA(1);
                if ((la2 & 0xFFFFFFC0) != 0x0 || (1L << la2 & 0x7800L) == 0x0L) {
                    rewriteStatementContext.opt = this._errHandler.recoverInline((Parser)this);
                }
                else {
                    this.consume();
                }
                rewriteStatementContext.ret.addValue((rewriteStatementContext.opt != null) ? rewriteStatementContext.opt.getText() : null);
            }
        }
        catch (final RecognitionException exception) {
            rewriteStatementContext.exception = exception;
            this._errHandler.reportError((Parser)this, exception);
            this._errHandler.recover((Parser)this, exception);
        }
        finally {
            this.exitRule();
        }
        return rewriteStatementContext;
    }
    
    static {
        RuntimeMetaData.checkVersion("4.5.3", "4.5.3");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = new String[] { "config", "statement", "genericStatement", "regexHeaderStatement", "block", "genericBlockHeader", "if_statement", "if_body", "regexp", "locationBlockHeader", "rewriteStatement" };
        _LITERAL_NAMES = new String[] { null, "';'", "'{'", "'}'", "'if'", "'('", "')'", "'\\.'", "'^'", "'location'", "'rewrite'", "'last'", "'break'", "'redirect'", "'permanent'" };
        _SYMBOLIC_NAMES = new String[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Value", "STR_EXT", "Comment", "REGEXP_PREFIXED", "QUOTED_STRING", "SINGLE_QUOTED", "WS" };
        VOCABULARY = (Vocabulary)new VocabularyImpl(NginxParser._LITERAL_NAMES, NginxParser._SYMBOLIC_NAMES);
        tokenNames = new String[NginxParser._SYMBOLIC_NAMES.length];
        for (int i = 0; i < NginxParser.tokenNames.length; ++i) {
            NginxParser.tokenNames[i] = NginxParser.VOCABULARY.getLiteralName(i);
            if (NginxParser.tokenNames[i] == null) {
                NginxParser.tokenNames[i] = NginxParser.VOCABULARY.getSymbolicName(i);
            }
            if (NginxParser.tokenNames[i] == null) {
                NginxParser.tokenNames[i] = "<INVALID>";
            }
        }
        _ATN = new ATNDeserializer().deserialize("\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0003\u0017º\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0006\u0002!\n\u0002\r\u0002\u000e\u0002\"\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003.\n\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u00049\n\u0004\f\u0004\u000e\u0004<\u000b\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006I\n\u0006\u0003\u0006\u0005\u0006L\n\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Z\n\u0006\f\u0006\u000e\u0006]\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0007\u0007h\n\u0007\f\u0007\u000e\u0007k\u000b\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0005\br\n\b\u0003\b\u0003\b\u0003\b\u0003\b\u0007\bx\n\b\f\b\u000e\b{\u000b\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u0084\n\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u008b\n\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0006\n\u009a\n\n\r\n\u000e\n\u009b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b¢\n\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b©\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f²\n\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f¸\n\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0003\u0003\u0002\r\u0010\u00ca\u0002 \u0003\u0002\u0002\u0002\u0004-\u0003\u0002\u0002\u0002\u00061\u0003\u0002\u0002\u0002\b=\u0003\u0002\u0002\u0002\nH\u0003\u0002\u0002\u0002\f`\u0003\u0002\u0002\u0002\u000el\u0003\u0002\u0002\u0002\u0010~\u0003\u0002\u0002\u0002\u0012\u0099\u0003\u0002\u0002\u0002\u0014\u009d\u0003\u0002\u0002\u0002\u0016ª\u0003\u0002\u0002\u0002\u0018\u0019\u0005\u0004\u0003\u0002\u0019\u001a\b\u0002\u0001\u0002\u001a!\u0003\u0002\u0002\u0002\u001b\u001c\u0005\n\u0006\u0002\u001c\u001d\b\u0002\u0001\u0002\u001d!\u0003\u0002\u0002\u0002\u001e\u001f\u0007\u0013\u0002\u0002\u001f!\b\u0002\u0001\u0002 \u0018\u0003\u0002\u0002\u0002 \u001b\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002!\"\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#\u0003\u0003\u0002\u0002\u0002$%\u0005\u0016\f\u0002%&\b\u0003\u0001\u0002&.\u0003\u0002\u0002\u0002'(\u0005\u0006\u0004\u0002()\b\u0003\u0001\u0002).\u0003\u0002\u0002\u0002*+\u0005\b\u0005\u0002+,\b\u0003\u0001\u0002,.\u0003\u0002\u0002\u0002-$\u0003\u0002\u0002\u0002-'\u0003\u0002\u0002\u0002-*\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/0\u0007\u0003\u0002\u00020\u0005\u0003\u0002\u0002\u000212\u0007\u0011\u0002\u00022:\b\u0004\u0001\u000234\u0007\u0011\u0002\u000249\b\u0004\u0001\u000256\u0005\u0012\n\u000267\b\u0004\u0001\u000279\u0003\u0002\u0002\u000283\u0003\u0002\u0002\u000285\u0003\u0002\u0002\u00029<\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002:;\u0003\u0002\u0002\u0002;\u0007\u0003\u0002\u0002\u0002<:\u0003\u0002\u0002\u0002=>\u0007\u0014\u0002\u0002>?\b\u0005\u0001\u0002?@\u0007\u0011\u0002\u0002@A\b\u0005\u0001\u0002A\t\u0003\u0002\u0002\u0002BC\u0005\u0014\u000b\u0002CD\b\u0006\u0001\u0002DI\u0003\u0002\u0002\u0002EF\u0005\f\u0007\u0002FG\b\u0006\u0001\u0002GI\u0003\u0002\u0002\u0002HB\u0003\u0002\u0002\u0002HE\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0007\u0013\u0002\u0002KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LM\u0003\u0002\u0002\u0002M[\u0007\u0004\u0002\u0002NO\u0005\u0004\u0003\u0002OP\b\u0006\u0001\u0002PZ\u0003\u0002\u0002\u0002QR\u0005\n\u0006\u0002RS\b\u0006\u0001\u0002SZ\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0006\u0001\u0002VZ\u0003\u0002\u0002\u0002WX\u0007\u0013\u0002\u0002XZ\b\u0006\u0001\u0002YN\u0003\u0002\u0002\u0002YQ\u0003\u0002\u0002\u0002YT\u0003\u0002\u0002\u0002YW\u0003\u0002\u0002\u0002Z]\u0003\u0002\u0002\u0002[Y\u0003\u0002\u0002\u0002[\\\u0003\u0002\u0002\u0002\\^\u0003\u0002\u0002\u0002][\u0003\u0002\u0002\u0002^_\u0007\u0005\u0002\u0002_\u000b\u0003\u0002\u0002\u0002`a\u0007\u0011\u0002\u0002ai\b\u0007\u0001\u0002bc\u0007\u0011\u0002\u0002ch\b\u0007\u0001\u0002de\u0005\u0012\n\u0002ef\b\u0007\u0001\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gd\u0003\u0002\u0002\u0002hk\u0003\u0002\u0002\u0002ig\u0003\u0002\u0002\u0002ij\u0003\u0002\u0002\u0002j\r\u0003\u0002\u0002\u0002ki\u0003\u0002\u0002\u0002lm\u0007\u0006\u0002\u0002mn\b\b\u0001\u0002no\u0005\u0010\t\u0002oq\b\b\u0001\u0002pr\u0007\u0013\u0002\u0002qp\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002rs\u0003\u0002\u0002\u0002sy\u0007\u0004\u0002\u0002tu\u0005\u0004\u0003\u0002uv\b\b\u0001\u0002vx\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z|\u0003\u0002\u0002\u0002{y\u0003\u0002\u0002\u0002|}\u0007\u0005\u0002\u0002}\u000f\u0003\u0002\u0002\u0002~\u007f\u0007\u0007\u0002\u0002\u007f\u0080\u0007\u0011\u0002\u0002\u0080\u0083\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082\u0084\b\t\u0001\u0002\u0083\u0081\u0003\u0002\u0002\u0002\u0083\u0084\u0003\u0002\u0002\u0002\u0084\u008a\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0011\u0002\u0002\u0086\u008b\b\t\u0001\u0002\u0087\u0088\u0005\u0012\n\u0002\u0088\u0089\b\t\u0001\u0002\u0089\u008b\u0003\u0002\u0002\u0002\u008a\u0085\u0003\u0002\u0002\u0002\u008a\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b\u008c\u0003\u0002\u0002\u0002\u008c\u008d\u0007\b\u0002\u0002\u008d\u0011\u0003\u0002\u0002\u0002\u008e\u008f\u0007\t\u0002\u0002\u008f\u009a\b\n\u0001\u0002\u0090\u0091\u0007\n\u0002\u0002\u0091\u009a\b\n\u0001\u0002\u0092\u0093\u0007\u0011\u0002\u0002\u0093\u009a\b\n\u0001\u0002\u0094\u0095\u0007\u0007\u0002\u0002\u0095\u0096\u0005\u0012\n\u0002\u0096\u0097\b\n\u0001\u0002\u0097\u0098\u0007\b\u0002\u0002\u0098\u009a\u0003\u0002\u0002\u0002\u0099\u008e\u0003\u0002\u0002\u0002\u0099\u0090\u0003\u0002\u0002\u0002\u0099\u0092\u0003\u0002\u0002\u0002\u0099\u0094\u0003\u0002\u0002\u0002\u009a\u009b\u0003\u0002\u0002\u0002\u009b\u0099\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u0013\u0003\u0002\u0002\u0002\u009d\u009e\u0007\u000b\u0002\u0002\u009e¡\b\u000b\u0001\u0002\u009f \u0007\u0011\u0002\u0002 ¢\b\u000b\u0001\u0002¡\u009f\u0003\u0002\u0002\u0002¡¢\u0003\u0002\u0002\u0002¢¨\u0003\u0002\u0002\u0002£¤\u0007\u0011\u0002\u0002¤©\b\u000b\u0001\u0002¥¦\u0005\u0012\n\u0002¦§\b\u000b\u0001\u0002§©\u0003\u0002\u0002\u0002¨£\u0003\u0002\u0002\u0002¨¥\u0003\u0002\u0002\u0002©\u0015\u0003\u0002\u0002\u0002ª«\u0007\f\u0002\u0002«±\b\f\u0001\u0002¬\u00ad\u0007\u0011\u0002\u0002\u00ad²\b\f\u0001\u0002®¯\u0005\u0012\n\u0002¯°\b\f\u0001\u0002°²\u0003\u0002\u0002\u0002±¬\u0003\u0002\u0002\u0002±®\u0003\u0002\u0002\u0002²³\u0003\u0002\u0002\u0002³´\u0007\u0011\u0002\u0002´·\b\f\u0001\u0002µ¶\t\u0002\u0002\u0002¶¸\b\f\u0001\u0002·µ\u0003\u0002\u0002\u0002·¸\u0003\u0002\u0002\u0002¸\u0017\u0003\u0002\u0002\u0002\u0017 \"-8:HKY[giqy\u0083\u008a\u0099\u009b¡¨±·".toCharArray());
        _decisionToDFA = new DFA[NginxParser._ATN.getNumberOfDecisions()];
        for (int j = 0; j < NginxParser._ATN.getNumberOfDecisions(); ++j) {
            NginxParser._decisionToDFA[j] = new DFA(NginxParser._ATN.getDecisionState(j), j);
        }
    }
    
    public static class RewriteStatementContext extends ParserRuleContext
    {
        public NgxParam ret;
        public Token id;
        public Token Value;
        public RegexpContext regexp;
        public Token opt;
        
        public List<TerminalNode> Value() {
            return this.getTokens(15);
        }
        
        public TerminalNode Value(final int n) {
            return this.getToken(15, n);
        }
        
        public RegexpContext regexp() {
            return (RegexpContext)this.getRuleContext((Class)RegexpContext.class, 0);
        }
        
        public RewriteStatementContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 10;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterRewriteStatement(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitRewriteStatement(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitRewriteStatement(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class RegexpContext extends ParserRuleContext
    {
        public String ret;
        public Token id;
        public Token Value;
        public RegexpContext r;
        
        public List<TerminalNode> Value() {
            return this.getTokens(15);
        }
        
        public TerminalNode Value(final int n) {
            return this.getToken(15, n);
        }
        
        public List<RegexpContext> regexp() {
            return this.getRuleContexts((Class)RegexpContext.class);
        }
        
        public RegexpContext regexp(final int n) {
            return (RegexpContext)this.getRuleContext((Class)RegexpContext.class, n);
        }
        
        public RegexpContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 8;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterRegexp(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitRegexp(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitRegexp(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class LocationBlockHeaderContext extends ParserRuleContext
    {
        public List<NgxToken> ret;
        public Token id;
        public Token Value;
        public RegexpContext regexp;
        
        public List<TerminalNode> Value() {
            return this.getTokens(15);
        }
        
        public TerminalNode Value(final int n) {
            return this.getToken(15, n);
        }
        
        public RegexpContext regexp() {
            return (RegexpContext)this.getRuleContext((Class)RegexpContext.class, 0);
        }
        
        public LocationBlockHeaderContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 9;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterLocationBlockHeader(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitLocationBlockHeader(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitLocationBlockHeader(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class If_bodyContext extends ParserRuleContext
    {
        public List<NgxToken> ret;
        public Token Value;
        public RegexpContext regexp;
        
        public List<TerminalNode> Value() {
            return this.getTokens(15);
        }
        
        public TerminalNode Value(final int n) {
            return this.getToken(15, n);
        }
        
        public RegexpContext regexp() {
            return (RegexpContext)this.getRuleContext((Class)RegexpContext.class, 0);
        }
        
        public If_bodyContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 7;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterIf_body(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitIf_body(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitIf_body(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class If_statementContext extends ParserRuleContext
    {
        public NgxIfBlock ret;
        public Token id;
        public If_bodyContext if_body;
        public StatementContext statement;
        
        public If_bodyContext if_body() {
            return (If_bodyContext)this.getRuleContext((Class)If_bodyContext.class, 0);
        }
        
        public TerminalNode Comment() {
            return this.getToken(17, 0);
        }
        
        public List<StatementContext> statement() {
            return this.getRuleContexts((Class)StatementContext.class);
        }
        
        public StatementContext statement(final int n) {
            return (StatementContext)this.getRuleContext((Class)StatementContext.class, n);
        }
        
        public If_statementContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 6;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterIf_statement(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitIf_statement(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitIf_statement(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class StatementContext extends ParserRuleContext
    {
        public NgxParam ret;
        public RewriteStatementContext rewriteStatement;
        public GenericStatementContext genericStatement;
        public RegexHeaderStatementContext regexHeaderStatement;
        
        public RewriteStatementContext rewriteStatement() {
            return (RewriteStatementContext)this.getRuleContext((Class)RewriteStatementContext.class, 0);
        }
        
        public GenericStatementContext genericStatement() {
            return (GenericStatementContext)this.getRuleContext((Class)GenericStatementContext.class, 0);
        }
        
        public RegexHeaderStatementContext regexHeaderStatement() {
            return (RegexHeaderStatementContext)this.getRuleContext((Class)RegexHeaderStatementContext.class, 0);
        }
        
        public StatementContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 1;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterStatement(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitStatement(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitStatement(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class GenericStatementContext extends ParserRuleContext
    {
        public NgxParam ret;
        public Token Value;
        public RegexpContext r;
        
        public List<TerminalNode> Value() {
            return this.getTokens(15);
        }
        
        public TerminalNode Value(final int n) {
            return this.getToken(15, n);
        }
        
        public List<RegexpContext> regexp() {
            return this.getRuleContexts((Class)RegexpContext.class);
        }
        
        public RegexpContext regexp(final int n) {
            return (RegexpContext)this.getRuleContext((Class)RegexpContext.class, n);
        }
        
        public GenericStatementContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 2;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterGenericStatement(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitGenericStatement(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitGenericStatement(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class RegexHeaderStatementContext extends ParserRuleContext
    {
        public NgxParam ret;
        public Token REGEXP_PREFIXED;
        public Token Value;
        
        public TerminalNode REGEXP_PREFIXED() {
            return this.getToken(18, 0);
        }
        
        public TerminalNode Value() {
            return this.getToken(15, 0);
        }
        
        public RegexHeaderStatementContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 3;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterRegexHeaderStatement(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitRegexHeaderStatement(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitRegexHeaderStatement(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class GenericBlockHeaderContext extends ParserRuleContext
    {
        public List<NgxToken> ret;
        public Token Value;
        public RegexpContext regexp;
        
        public List<TerminalNode> Value() {
            return this.getTokens(15);
        }
        
        public TerminalNode Value(final int n) {
            return this.getToken(15, n);
        }
        
        public List<RegexpContext> regexp() {
            return this.getRuleContexts((Class)RegexpContext.class);
        }
        
        public RegexpContext regexp(final int n) {
            return (RegexpContext)this.getRuleContext((Class)RegexpContext.class, n);
        }
        
        public GenericBlockHeaderContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 5;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterGenericBlockHeader(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitGenericBlockHeader(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitGenericBlockHeader(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class BlockContext extends ParserRuleContext
    {
        public NgxBlock ret;
        public LocationBlockHeaderContext locationBlockHeader;
        public GenericBlockHeaderContext genericBlockHeader;
        public Token Comment;
        public StatementContext statement;
        public BlockContext b;
        public If_statementContext if_statement;
        
        public LocationBlockHeaderContext locationBlockHeader() {
            return (LocationBlockHeaderContext)this.getRuleContext((Class)LocationBlockHeaderContext.class, 0);
        }
        
        public GenericBlockHeaderContext genericBlockHeader() {
            return (GenericBlockHeaderContext)this.getRuleContext((Class)GenericBlockHeaderContext.class, 0);
        }
        
        public List<TerminalNode> Comment() {
            return this.getTokens(17);
        }
        
        public TerminalNode Comment(final int n) {
            return this.getToken(17, n);
        }
        
        public List<StatementContext> statement() {
            return this.getRuleContexts((Class)StatementContext.class);
        }
        
        public StatementContext statement(final int n) {
            return (StatementContext)this.getRuleContext((Class)StatementContext.class, n);
        }
        
        public List<If_statementContext> if_statement() {
            return this.getRuleContexts((Class)If_statementContext.class);
        }
        
        public If_statementContext if_statement(final int n) {
            return (If_statementContext)this.getRuleContext((Class)If_statementContext.class, n);
        }
        
        public List<BlockContext> block() {
            return this.getRuleContexts((Class)BlockContext.class);
        }
        
        public BlockContext block(final int n) {
            return (BlockContext)this.getRuleContext((Class)BlockContext.class, n);
        }
        
        public BlockContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 4;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterBlock(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitBlock(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitBlock(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
    
    public static class ConfigContext extends ParserRuleContext
    {
        public NgxConfig ret;
        public StatementContext statement;
        public BlockContext block;
        public Token Comment;
        
        public List<StatementContext> statement() {
            return this.getRuleContexts((Class)StatementContext.class);
        }
        
        public StatementContext statement(final int n) {
            return (StatementContext)this.getRuleContext((Class)StatementContext.class, n);
        }
        
        public List<BlockContext> block() {
            return this.getRuleContexts((Class)BlockContext.class);
        }
        
        public BlockContext block(final int n) {
            return (BlockContext)this.getRuleContext((Class)BlockContext.class, n);
        }
        
        public List<TerminalNode> Comment() {
            return this.getTokens(17);
        }
        
        public TerminalNode Comment(final int n) {
            return this.getToken(17, n);
        }
        
        public ConfigContext(final ParserRuleContext parserRuleContext, final int n) {
            super(parserRuleContext, n);
        }
        
        public int getRuleIndex() {
            return 0;
        }
        
        public void enterRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).enterConfig(this);
            }
        }
        
        public void exitRule(final ParseTreeListener parseTreeListener) {
            if (parseTreeListener instanceof NginxListener) {
                ((NginxListener)parseTreeListener).exitConfig(this);
            }
        }
        
        public <T> T accept(final ParseTreeVisitor<? extends T> parseTreeVisitor) {
            if (parseTreeVisitor instanceof NginxVisitor) {
                return ((NginxVisitor<T>)parseTreeVisitor).visitConfig(this);
            }
            return (T)parseTreeVisitor.visitChildren((RuleNode)this);
        }
    }
}
