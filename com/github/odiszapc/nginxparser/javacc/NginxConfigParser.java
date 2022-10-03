package com.github.odiszapc.nginxparser.javacc;

import java.util.Iterator;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.io.InputStream;
import com.github.odiszapc.nginxparser.NgxComment;
import com.github.odiszapc.nginxparser.NgxParam;
import com.github.odiszapc.nginxparser.NgxIfBlock;
import com.github.odiszapc.nginxparser.NgxToken;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxAbstractEntry;
import com.github.odiszapc.nginxparser.NgxEntry;
import com.github.odiszapc.nginxparser.NgxConfig;
import java.util.List;

public class NginxConfigParser implements NginxConfigParserConstants
{
    private boolean isDebug;
    public NginxConfigParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    
    public void setDebug(final boolean isDebug) {
        this.isDebug = isDebug;
    }
    
    private void debug(final String s) {
        this.debug(s, false);
    }
    
    private void debugLn(final String s) {
        this.debug(s, true);
    }
    
    private void debug(final String s, final boolean b) {
        if (this.isDebug) {
            System.out.print(s);
            if (b) {
                System.out.println();
            }
        }
    }
    
    public final NgxConfig parse() throws ParseException {
        final NgxConfig statements = this.statements();
        if ("" != null) {
            return statements;
        }
        throw new Error("Missing return statement in function");
    }
    
    public final NgxConfig statements() throws ParseException {
        final NgxConfig ngxConfig = new NgxConfig();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 13:
                case 14:
                case 15:
                case 16: {
                    NgxAbstractEntry ngxAbstractEntry = null;
                    if (this.jj_2_1(4)) {
                        ngxAbstractEntry = this.param();
                    }
                    else {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 13:
                            case 14:
                            case 15: {
                                ngxAbstractEntry = this.block();
                                break;
                            }
                            case 16: {
                                ngxAbstractEntry = this.comment();
                                break;
                            }
                            default: {
                                this.jj_la1[1] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                    }
                    ngxConfig.addEntry(ngxAbstractEntry);
                    continue;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    if ("" != null) {
                        return ngxConfig;
                    }
                    throw new Error("Missing return statement in function");
                }
            }
        }
    }
    
    public final NgxBlock block() throws ParseException {
        final String s = "";
        final NgxBlock ngxBlock = new NgxBlock();
        final NgxToken id = this.id();
        String s2 = s + this.token.image + " ";
        ngxBlock.getTokens().add(id);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 13:
                case 14:
                case 15: {
                    final NgxToken id2 = this.id();
                    s2 = s2 + this.token.image + " ";
                    ngxBlock.getTokens().add(id2);
                    continue;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    this.debugLn("BLOCK=" + s2 + "{");
                    this.jj_consume_token(7);
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 10:
                            case 13:
                            case 14:
                            case 15:
                            case 16: {
                                NgxAbstractEntry ngxAbstractEntry = null;
                                if (this.jj_2_2(4)) {
                                    ngxAbstractEntry = this.param();
                                }
                                else {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 13:
                                        case 14:
                                        case 15: {
                                            ngxAbstractEntry = this.block();
                                            break;
                                        }
                                        case 16: {
                                            ngxAbstractEntry = this.comment();
                                            break;
                                        }
                                        case 10: {
                                            ngxAbstractEntry = this.if_block();
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[4] = this.jj_gen;
                                            this.jj_consume_token(-1);
                                            throw new ParseException();
                                        }
                                    }
                                }
                                ngxBlock.addEntry(ngxAbstractEntry);
                                continue;
                            }
                            default: {
                                this.jj_la1[3] = this.jj_gen;
                                this.jj_consume_token(8);
                                this.debugLn("}");
                                if ("" != null) {
                                    return ngxBlock;
                                }
                                throw new Error("Missing return statement in function");
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public final NgxBlock if_block() throws ParseException {
        final NgxIfBlock ngxIfBlock = new NgxIfBlock();
        this.jj_consume_token(10);
        this.debug(this.token.image + " ");
        ngxIfBlock.addValue(new NgxToken(this.token.image));
        this.jj_consume_token(11);
        this.debug(this.token.image + " ");
        ngxIfBlock.addValue(new NgxToken(this.token.image));
        this.jj_consume_token(7);
        this.debugLn(this.token.image);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 13:
                case 14:
                case 15:
                case 16: {
                    NgxAbstractEntry ngxAbstractEntry = null;
                    if (this.jj_2_3(4)) {
                        ngxAbstractEntry = this.param();
                    }
                    else {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 13:
                            case 14:
                            case 15: {
                                ngxAbstractEntry = this.block();
                                break;
                            }
                            case 16: {
                                ngxAbstractEntry = this.comment();
                                break;
                            }
                            default: {
                                this.jj_la1[6] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                    }
                    ngxIfBlock.addEntry(ngxAbstractEntry);
                    continue;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    this.jj_consume_token(8);
                    this.debugLn(this.token.image);
                    if ("" != null) {
                        return ngxIfBlock;
                    }
                    throw new Error("Missing return statement in function");
                }
            }
        }
    }
    
    public final NgxParam param() throws ParseException {
        final NgxParam ngxParam = new NgxParam();
        final NgxToken id = this.id();
        this.debug("KEY=" + this.token.image + ", VALUE=");
        ngxParam.getTokens().add(id);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 12:
                case 13:
                case 14:
                case 15: {
                    final NgxToken value = this.value();
                    this.debug(this.token.image + " | ");
                    ngxParam.getTokens().add(value);
                    continue;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    this.debugLn("");
                    this.jj_consume_token(9);
                    if ("" != null) {
                        return ngxParam;
                    }
                    throw new Error("Missing return statement in function");
                }
            }
        }
    }
    
    public final NgxComment comment() throws ParseException {
        this.jj_consume_token(16);
        this.debugLn("COMMENT=" + this.token.image);
        if ("" != null) {
            return new NgxComment(this.token.image);
        }
        throw new Error("Missing return statement in function");
    }
    
    public final NgxToken id() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 13: {
                this.jj_consume_token(13);
                break;
            }
            case 14: {
                this.jj_consume_token(14);
                break;
            }
            case 15: {
                this.jj_consume_token(15);
                break;
            }
            default: {
                this.jj_la1[8] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return new NgxToken(this.token.image);
        }
        throw new Error("Missing return statement in function");
    }
    
    public final NgxToken value() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 12: {
                this.jj_consume_token(12);
                break;
            }
            case 13: {
                this.jj_consume_token(13);
                break;
            }
            case 14: {
                this.jj_consume_token(14);
                break;
            }
            case 15: {
                this.jj_consume_token(15);
                break;
            }
            default: {
                this.jj_la1[9] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return new NgxToken(this.token.image);
        }
        throw new Error("Missing return statement in function");
    }
    
    private boolean jj_2_1(final int jj_la) {
        this.jj_la = jj_la;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_1();
        }
        catch (final LookaheadSuccess lookaheadSuccess) {
            return true;
        }
        finally {
            this.jj_save(0, jj_la);
        }
    }
    
    private boolean jj_2_2(final int jj_la) {
        this.jj_la = jj_la;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_2();
        }
        catch (final LookaheadSuccess lookaheadSuccess) {
            return true;
        }
        finally {
            this.jj_save(1, jj_la);
        }
    }
    
    private boolean jj_2_3(final int jj_la) {
        this.jj_la = jj_la;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_3();
        }
        catch (final LookaheadSuccess lookaheadSuccess) {
            return true;
        }
        finally {
            this.jj_save(2, jj_la);
        }
    }
    
    private boolean jj_3R_8() {
        return this.jj_3R_9();
    }
    
    private boolean jj_3R_7() {
        final Token jj_scanpos = this.jj_scanpos;
        if (this.jj_scan_token(13)) {
            this.jj_scanpos = jj_scanpos;
            if (this.jj_scan_token(14)) {
                this.jj_scanpos = jj_scanpos;
                if (this.jj_scan_token(15)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3_2() {
        return this.jj_3R_6();
    }
    
    private boolean jj_3R_9() {
        final Token jj_scanpos = this.jj_scanpos;
        if (this.jj_scan_token(12)) {
            this.jj_scanpos = jj_scanpos;
            if (this.jj_scan_token(13)) {
                this.jj_scanpos = jj_scanpos;
                if (this.jj_scan_token(14)) {
                    this.jj_scanpos = jj_scanpos;
                    if (this.jj_scan_token(15)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3_3() {
        return this.jj_3R_6();
    }
    
    private boolean jj_3_1() {
        return this.jj_3R_6();
    }
    
    private boolean jj_3R_6() {
        if (this.jj_3R_7()) {
            return true;
        }
        Token jj_scanpos;
        do {
            jj_scanpos = this.jj_scanpos;
        } while (!this.jj_3R_8());
        this.jj_scanpos = jj_scanpos;
        return this.jj_scan_token(9);
    }
    
    private static void jj_la1_init_0() {
        NginxConfigParser.jj_la1_0 = new int[] { 122880, 122880, 57344, 123904, 123904, 122880, 122880, 61440, 57344, 61440 };
    }
    
    public NginxConfigParser(final InputStream inputStream) {
        this(inputStream, null);
    }
    
    public NginxConfigParser(final InputStream inputStream, final String s) {
        this.isDebug = false;
        this.jj_la1 = new int[10];
        this.jj_2_rtns = new JJCalls[3];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        try {
            this.jj_input_stream = new SimpleCharStream(inputStream, s, 1, 1);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        this.token_source = new NginxConfigParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int j = 0; j < this.jj_2_rtns.length; ++j) {
            this.jj_2_rtns[j] = new JJCalls();
        }
    }
    
    public void ReInit(final InputStream inputStream) {
        this.ReInit(inputStream, null);
    }
    
    public void ReInit(final InputStream inputStream, final String s) {
        try {
            this.jj_input_stream.ReInit(inputStream, s, 1, 1);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int j = 0; j < this.jj_2_rtns.length; ++j) {
            this.jj_2_rtns[j] = new JJCalls();
        }
    }
    
    public NginxConfigParser(final Reader reader) {
        this.isDebug = false;
        this.jj_la1 = new int[10];
        this.jj_2_rtns = new JJCalls[3];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(reader, 1, 1);
        this.token_source = new NginxConfigParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int j = 0; j < this.jj_2_rtns.length; ++j) {
            this.jj_2_rtns[j] = new JJCalls();
        }
    }
    
    public void ReInit(final Reader reader) {
        this.jj_input_stream.ReInit(reader, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int j = 0; j < this.jj_2_rtns.length; ++j) {
            this.jj_2_rtns[j] = new JJCalls();
        }
    }
    
    public NginxConfigParser(final NginxConfigParserTokenManager token_source) {
        this.isDebug = false;
        this.jj_la1 = new int[10];
        this.jj_2_rtns = new JJCalls[3];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = token_source;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int j = 0; j < this.jj_2_rtns.length; ++j) {
            this.jj_2_rtns[j] = new JJCalls();
        }
    }
    
    public void ReInit(final NginxConfigParserTokenManager token_source) {
        this.token_source = token_source;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int j = 0; j < this.jj_2_rtns.length; ++j) {
            this.jj_2_rtns[j] = new JJCalls();
        }
    }
    
    private Token jj_consume_token(final int jj_kind) throws ParseException {
        final Token token;
        if ((token = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token2 = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == jj_kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for (JJCalls next = this.jj_2_rtns[i]; next != null; next = next.next) {
                        if (next.gen < this.jj_gen) {
                            next.first = null;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = token;
        this.jj_kind = jj_kind;
        throw this.generateParseException();
    }
    
    private boolean jj_scan_token(final int n) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int n2 = 0;
            Token token;
            for (token = this.token; token != null && token != this.jj_scanpos; token = token.next) {
                ++n2;
            }
            if (token != null) {
                this.jj_add_error_token(n, n2);
            }
        }
        if (this.jj_scanpos.kind != n) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }
    
    public final Token getToken(final int n) {
        Token token = this.token;
        for (int i = 0; i < n; ++i) {
            if (token.next != null) {
                token = token.next;
            }
            else {
                final Token token2 = token;
                final Token nextToken = this.token_source.getNextToken();
                token2.next = nextToken;
                token = nextToken;
            }
        }
        return token;
    }
    
    private int jj_ntk_f() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    private void jj_add_error_token(final int n, final int jj_endpos) {
        if (jj_endpos >= 100) {
            return;
        }
        if (jj_endpos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = n;
        }
        else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
        Label_0092:
            for (final int[] array : this.jj_expentries) {
                if (array.length == this.jj_expentry.length) {
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (array[j] != this.jj_expentry[j]) {
                            continue Label_0092;
                        }
                    }
                    this.jj_expentries.add(this.jj_expentry);
                    break;
                }
            }
            if (jj_endpos != 0) {
                this.jj_lasttokens[(this.jj_endpos = jj_endpos) - 1] = n;
            }
        }
    }
    
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        final boolean[] array = new boolean[19];
        if (this.jj_kind >= 0) {
            array[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 10; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((NginxConfigParser.jj_la1_0[i] & 1 << j) != 0x0) {
                        array[j] = true;
                    }
                }
            }
        }
        for (int k = 0; k < 19; ++k) {
            if (array[k]) {
                (this.jj_expentry = new int[1])[0] = k;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        final int[][] array2 = new int[this.jj_expentries.size()][];
        for (int l = 0; l < this.jj_expentries.size(); ++l) {
            array2[l] = this.jj_expentries.get(l);
        }
        return new ParseException(this.token, array2, NginxConfigParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 3; ++i) {
            try {
                JJCalls next = this.jj_2_rtns[i];
                do {
                    if (next.gen > this.jj_gen) {
                        this.jj_la = next.arg;
                        final Token first = next.first;
                        this.jj_scanpos = first;
                        this.jj_lastpos = first;
                        switch (i) {
                            case 0: {
                                this.jj_3_1();
                                break;
                            }
                            case 1: {
                                this.jj_3_2();
                                break;
                            }
                            case 2: {
                                this.jj_3_3();
                                break;
                            }
                        }
                    }
                    next = next.next;
                } while (next != null);
            }
            catch (final LookaheadSuccess lookaheadSuccess) {}
        }
        this.jj_rescan = false;
    }
    
    private void jj_save(final int n, final int arg) {
        JJCalls next;
        for (next = this.jj_2_rtns[n]; next.gen > this.jj_gen; next = next.next) {
            if (next.next == null) {
                final JJCalls jjCalls = next;
                final JJCalls next2 = new JJCalls();
                jjCalls.next = next2;
                next = next2;
                break;
            }
        }
        next.gen = this.jj_gen + arg - this.jj_la;
        next.first = this.token;
        next.arg = arg;
    }
    
    static {
        jj_la1_init_0();
    }
    
    static final class JJCalls
    {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
    
    private static final class LookaheadSuccess extends Error
    {
    }
}
