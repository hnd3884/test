package jdk.internal.util.xml.impl;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.Reader;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.InputSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Parser
{
    public static final String FAULT = "";
    protected static final int BUFFSIZE_READER = 512;
    protected static final int BUFFSIZE_PARSER = 128;
    public static final char EOS = '\uffff';
    private Pair mNoNS;
    private Pair mXml;
    private Map<String, Input> mEnt;
    private Map<String, Input> mPEnt;
    protected boolean mIsSAlone;
    protected boolean mIsSAloneSet;
    protected boolean mIsNSAware;
    protected int mPh;
    protected static final int PH_BEFORE_DOC = -1;
    protected static final int PH_DOC_START = 0;
    protected static final int PH_MISC_DTD = 1;
    protected static final int PH_DTD = 2;
    protected static final int PH_DTD_MISC = 3;
    protected static final int PH_DOCELM = 4;
    protected static final int PH_DOCELM_MISC = 5;
    protected static final int PH_AFTER_DOC = 6;
    protected int mEvt;
    protected static final int EV_NULL = 0;
    protected static final int EV_ELM = 1;
    protected static final int EV_ELMS = 2;
    protected static final int EV_ELME = 3;
    protected static final int EV_TEXT = 4;
    protected static final int EV_WSPC = 5;
    protected static final int EV_PI = 6;
    protected static final int EV_CDAT = 7;
    protected static final int EV_COMM = 8;
    protected static final int EV_DTD = 9;
    protected static final int EV_ENT = 10;
    private char mESt;
    protected char[] mBuff;
    protected int mBuffIdx;
    protected Pair mPref;
    protected Pair mElm;
    protected Pair mAttL;
    protected Input mDoc;
    protected Input mInp;
    private char[] mChars;
    private int mChLen;
    private int mChIdx;
    protected Attrs mAttrs;
    private String[] mItems;
    private char mAttrIdx;
    private String mUnent;
    private Pair mDltd;
    private static final char[] NONS;
    private static final char[] XML;
    private static final char[] XMLNS;
    private static final byte[] asctyp;
    private static final byte[] nmttyp;
    
    protected Parser() {
        this.mPh = -1;
        this.mBuff = new char[128];
        this.mAttrs = new Attrs();
        this.mPref = this.pair(this.mPref);
        this.mPref.name = "";
        this.mPref.value = "";
        this.mPref.chars = Parser.NONS;
        this.mNoNS = this.mPref;
        this.mPref = this.pair(this.mPref);
        this.mPref.name = "xml";
        this.mPref.value = "http://www.w3.org/XML/1998/namespace";
        this.mPref.chars = Parser.XML;
        this.mXml = this.mPref;
    }
    
    protected void init() {
        this.mUnent = null;
        this.mElm = null;
        this.mPref = this.mXml;
        this.mAttL = null;
        this.mPEnt = new HashMap<String, Input>();
        this.mEnt = new HashMap<String, Input>();
        this.mDoc = this.mInp;
        this.mChars = this.mInp.chars;
        this.mPh = 0;
    }
    
    protected void cleanup() {
        while (this.mAttL != null) {
            while (this.mAttL.list != null) {
                if (this.mAttL.list.list != null) {
                    this.del(this.mAttL.list.list);
                }
                this.mAttL.list = this.del(this.mAttL.list);
            }
            this.mAttL = this.del(this.mAttL);
        }
        while (this.mElm != null) {
            this.mElm = this.del(this.mElm);
        }
        while (this.mPref != this.mXml) {
            this.mPref = this.del(this.mPref);
        }
        while (this.mInp != null) {
            this.pop();
        }
        if (this.mDoc != null && this.mDoc.src != null) {
            try {
                this.mDoc.src.close();
            }
            catch (final IOException ex) {}
        }
        this.mPEnt = null;
        this.mEnt = null;
        this.mDoc = null;
        this.mPh = 6;
    }
    
    protected int step() throws Exception {
        this.mEvt = 0;
        int n = 0;
        while (this.mEvt == 0) {
            final char c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : this.getch();
            switch (n) {
                case 0: {
                    if (c != '<') {
                        this.bkch();
                        this.mBuffIdx = -1;
                        n = 1;
                        continue;
                    }
                    switch (this.getch()) {
                        case '/': {
                            this.mEvt = 3;
                            if (this.mElm == null) {
                                this.panic("");
                            }
                            this.mBuffIdx = -1;
                            this.bname(this.mIsNSAware);
                            final char[] chars = this.mElm.chars;
                            if (chars.length == this.mBuffIdx + 1) {
                                for (int i = 1; i <= this.mBuffIdx; i = (char)(i + 1)) {
                                    if (chars[i] != this.mBuff[i]) {
                                        this.panic("");
                                    }
                                }
                            }
                            else {
                                this.panic("");
                            }
                            if (this.wsskip() != '>') {
                                this.panic("");
                            }
                            this.getch();
                            continue;
                        }
                        case '!': {
                            final char getch = this.getch();
                            this.bkch();
                            switch (getch) {
                                case 45: {
                                    this.mEvt = 8;
                                    this.comm();
                                    continue;
                                }
                                case 91: {
                                    this.mEvt = 7;
                                    this.cdat();
                                    continue;
                                }
                                default: {
                                    this.mEvt = 9;
                                    this.dtd();
                                    continue;
                                }
                            }
                            break;
                        }
                        case '?': {
                            this.mEvt = 6;
                            this.pi();
                            continue;
                        }
                        default: {
                            this.bkch();
                            this.mElm = this.pair(this.mElm);
                            this.mElm.chars = this.qname(this.mIsNSAware);
                            this.mElm.name = this.mElm.local();
                            this.mElm.id = ((this.mElm.next != null) ? this.mElm.next.id : 0);
                            this.mElm.num = 0;
                            final Pair find = this.find(this.mAttL, this.mElm.chars);
                            this.mElm.list = ((find != null) ? find.list : null);
                            this.mAttrIdx = '\0';
                            final Pair pair = this.pair(null);
                            pair.num = 0;
                            this.attr(pair);
                            this.del(pair);
                            this.mElm.value = (this.mIsNSAware ? this.rslv(this.mElm.chars) : null);
                            switch (this.wsskip()) {
                                case '>': {
                                    this.getch();
                                    this.mEvt = 2;
                                    continue;
                                }
                                case '/': {
                                    this.getch();
                                    if (this.getch() != '>') {
                                        this.panic("");
                                    }
                                    this.mEvt = 1;
                                    continue;
                                }
                                default: {
                                    this.panic("");
                                    continue;
                                }
                            }
                            break;
                        }
                    }
                    continue;
                }
                case 1: {
                    switch (c) {
                        case 9:
                        case 10:
                        case 32: {
                            this.bappend(c);
                            continue;
                        }
                        case 13: {
                            if (this.getch() != '\n') {
                                this.bkch();
                            }
                            this.bappend('\n');
                            continue;
                        }
                        case 60: {
                            this.mEvt = 5;
                            this.bkch();
                            this.bflash_ws();
                            continue;
                        }
                        default: {
                            this.bkch();
                            n = 2;
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (c) {
                        case '&': {
                            if (this.mUnent != null) {
                                this.mEvt = 10;
                                this.skippedEnt(this.mUnent);
                                this.mUnent = null;
                                continue;
                            }
                            if ((this.mUnent = this.ent('x')) != null) {
                                this.mEvt = 4;
                                this.bkch();
                                this.setch('&');
                                this.bflash();
                                continue;
                            }
                            continue;
                        }
                        case '<': {
                            this.mEvt = 4;
                            this.bkch();
                            this.bflash();
                            continue;
                        }
                        case '\r': {
                            if (this.getch() != '\n') {
                                this.bkch();
                            }
                            this.bappend('\n');
                            continue;
                        }
                        case '\uffff': {
                            this.panic("");
                            break;
                        }
                    }
                    this.bappend(c);
                    continue;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
        return this.mEvt;
    }
    
    private void dtd() throws Exception {
        String name = null;
        Pair pubsys = null;
        if (!"DOCTYPE".equals(this.name(false))) {
            this.panic("");
        }
        this.mPh = 2;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    if (this.chtyp(getch) != ' ') {
                        this.bkch();
                        name = this.name(this.mIsNSAware);
                        this.wsskip();
                        i = 1;
                        continue;
                    }
                    continue;
                }
                case 1: {
                    switch (this.chtyp(getch)) {
                        case 'A': {
                            this.bkch();
                            pubsys = this.pubsys(' ');
                            i = 2;
                            this.docType(name, pubsys.name, pubsys.value);
                            continue;
                        }
                        case '[': {
                            this.bkch();
                            i = 2;
                            this.docType(name, null, null);
                            continue;
                        }
                        case '>': {
                            this.bkch();
                            i = 3;
                            this.docType(name, null, null);
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (this.chtyp(getch)) {
                        case '[': {
                            this.dtdsub();
                            i = 3;
                            continue;
                        }
                        case '>': {
                            this.bkch();
                            i = 3;
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (this.chtyp(getch)) {
                        case '>': {
                            if (pubsys != null) {
                                final InputSource resolveEnt = this.resolveEnt(name, pubsys.name, pubsys.value);
                                if (resolveEnt != null) {
                                    if (!this.mIsSAlone) {
                                        this.bkch();
                                        this.setch(']');
                                        this.push(new Input(512));
                                        this.setinp(resolveEnt);
                                        this.mInp.pubid = pubsys.name;
                                        this.mInp.sysid = pubsys.value;
                                        this.dtdsub();
                                    }
                                    else {
                                        this.skippedEnt("[dtd]");
                                        if (resolveEnt.getCharacterStream() != null) {
                                            try {
                                                resolveEnt.getCharacterStream().close();
                                            }
                                            catch (final IOException ex) {}
                                        }
                                        if (resolveEnt.getByteStream() != null) {
                                            try {
                                                resolveEnt.getByteStream().close();
                                            }
                                            catch (final IOException ex2) {}
                                        }
                                    }
                                }
                                else {
                                    this.skippedEnt("[dtd]");
                                }
                                this.del(pubsys);
                            }
                            i = -1;
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void dtdsub() throws Exception {
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    switch (this.chtyp(getch)) {
                        case '<': {
                            switch (this.getch()) {
                                case '?': {
                                    this.pi();
                                    continue;
                                }
                                case '!': {
                                    final char getch2 = this.getch();
                                    this.bkch();
                                    if (getch2 == '-') {
                                        this.comm();
                                        continue;
                                    }
                                    this.bntok();
                                    switch (this.bkeyword()) {
                                        case 'n': {
                                            this.dtdent();
                                            break;
                                        }
                                        case 'a': {
                                            this.dtdattl();
                                            break;
                                        }
                                        case 'e': {
                                            this.dtdelm();
                                            break;
                                        }
                                        case 'o': {
                                            this.dtdnot();
                                            break;
                                        }
                                        default: {
                                            this.panic("");
                                            break;
                                        }
                                    }
                                    i = 1;
                                    continue;
                                }
                                default: {
                                    this.panic("");
                                    continue;
                                }
                            }
                            break;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        case ']': {
                            i = -1;
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        case 'Z': {
                            if (this.getch() != ']') {
                                this.panic("");
                            }
                            i = -1;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (getch) {
                        case '>': {
                            i = 0;
                            continue;
                        }
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void dtdent() throws Exception {
        String s = null;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    switch (this.chtyp(getch)) {
                        case ' ': {
                            continue;
                        }
                        case '%': {
                            final char getch2 = this.getch();
                            this.bkch();
                            if (this.chtyp(getch2) != ' ') {
                                this.pent(' ');
                                continue;
                            }
                            this.wsskip();
                            s = this.name(false);
                            switch (this.chtyp(this.wsskip())) {
                                case 'A': {
                                    final Pair pubsys = this.pubsys(' ');
                                    if (this.wsskip() == '>') {
                                        if (!this.mPEnt.containsKey(s)) {
                                            final Input input = new Input();
                                            input.pubid = pubsys.name;
                                            input.sysid = pubsys.value;
                                            this.mPEnt.put(s, input);
                                        }
                                    }
                                    else {
                                        this.panic("");
                                    }
                                    this.del(pubsys);
                                    i = -1;
                                    continue;
                                }
                                case '\"':
                                case '\'': {
                                    this.bqstr('d');
                                    final char[] array = new char[this.mBuffIdx + 1];
                                    System.arraycopy(this.mBuff, 1, array, 1, array.length - 1);
                                    array[0] = ' ';
                                    if (!this.mPEnt.containsKey(s)) {
                                        final Input input2 = new Input(array);
                                        input2.pubid = this.mInp.pubid;
                                        input2.sysid = this.mInp.sysid;
                                        input2.xmlenc = this.mInp.xmlenc;
                                        input2.xmlver = this.mInp.xmlver;
                                        this.mPEnt.put(s, input2);
                                    }
                                    i = -1;
                                    continue;
                                }
                                default: {
                                    this.panic("");
                                    continue;
                                }
                            }
                            break;
                        }
                        default: {
                            this.bkch();
                            s = this.name(false);
                            i = 1;
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (this.chtyp(getch)) {
                        case '\"':
                        case '\'': {
                            this.bkch();
                            this.bqstr('d');
                            if (this.mEnt.get(s) == null) {
                                final char[] array2 = new char[this.mBuffIdx];
                                System.arraycopy(this.mBuff, 1, array2, 0, array2.length);
                                if (!this.mEnt.containsKey(s)) {
                                    final Input input3 = new Input(array2);
                                    input3.pubid = this.mInp.pubid;
                                    input3.sysid = this.mInp.sysid;
                                    input3.xmlenc = this.mInp.xmlenc;
                                    input3.xmlver = this.mInp.xmlver;
                                    this.mEnt.put(s, input3);
                                }
                            }
                            i = -1;
                            continue;
                        }
                        case 'A': {
                            this.bkch();
                            final Pair pubsys2 = this.pubsys(' ');
                            Label_0722: {
                                switch (this.wsskip()) {
                                    case '>': {
                                        if (!this.mEnt.containsKey(s)) {
                                            final Input input4 = new Input();
                                            input4.pubid = pubsys2.name;
                                            input4.sysid = pubsys2.value;
                                            this.mEnt.put(s, input4);
                                        }
                                        break Label_0722;
                                    }
                                    case 'N': {
                                        if ("NDATA".equals(this.name(false))) {
                                            this.wsskip();
                                            this.unparsedEntDecl(s, pubsys2.name, pubsys2.value, this.name(false));
                                            break Label_0722;
                                        }
                                        break;
                                    }
                                }
                                this.panic("");
                            }
                            this.del(pubsys2);
                            i = -1;
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void dtdelm() throws Exception {
        this.wsskip();
        this.name(this.mIsNSAware);
    Label_0048:
        while (true) {
            switch (this.getch()) {
                case '>': {
                    break Label_0048;
                }
                case '\uffff': {
                    this.panic("");
                    continue;
                }
            }
        }
        this.bkch();
    }
    
    private void dtdattl() throws Exception {
        Pair mAttL = null;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    switch (this.chtyp(getch)) {
                        case ':':
                        case 'A':
                        case 'X':
                        case '_':
                        case 'a': {
                            this.bkch();
                            final char[] qname = this.qname(this.mIsNSAware);
                            mAttL = this.find(this.mAttL, qname);
                            if (mAttL == null) {
                                mAttL = this.pair(this.mAttL);
                                mAttL.chars = qname;
                                this.mAttL = mAttL;
                            }
                            i = 1;
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (this.chtyp(getch)) {
                        case ':':
                        case 'A':
                        case 'X':
                        case '_':
                        case 'a': {
                            this.bkch();
                            this.dtdatt(mAttL);
                            if (this.wsskip() == '>') {
                                return;
                            }
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void dtdatt(final Pair pair) throws Exception {
        Pair list = null;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    switch (this.chtyp(getch)) {
                        case ':':
                        case 'A':
                        case 'X':
                        case '_':
                        case 'a': {
                            this.bkch();
                            final char[] qname = this.qname(this.mIsNSAware);
                            if (this.find(pair.list, qname) == null) {
                                list = this.pair(pair.list);
                                list.chars = qname;
                                pair.list = list;
                            }
                            else {
                                list = this.pair(null);
                                list.chars = qname;
                                list.id = 99;
                            }
                            this.wsskip();
                            i = 1;
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (this.chtyp(getch)) {
                        case '(': {
                            list.id = 117;
                            i = 2;
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.bkch();
                            this.bntok();
                            switch (list.id = this.bkeyword()) {
                                case 111: {
                                    if (this.wsskip() != '(') {
                                        this.panic("");
                                    }
                                    this.getch();
                                    i = 2;
                                    continue;
                                }
                                case 78:
                                case 82:
                                case 84:
                                case 99:
                                case 105:
                                case 110:
                                case 114:
                                case 116: {
                                    this.wsskip();
                                    i = 4;
                                    continue;
                                }
                                default: {
                                    this.panic("");
                                    continue;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (this.chtyp(getch)) {
                        case '-':
                        case '.':
                        case ':':
                        case 'A':
                        case 'X':
                        case '_':
                        case 'a':
                        case 'd': {
                            this.bkch();
                            switch (list.id) {
                                case 117: {
                                    this.bntok();
                                    break;
                                }
                                case 111: {
                                    this.mBuffIdx = -1;
                                    this.bname(false);
                                    break;
                                }
                                default: {
                                    this.panic("");
                                    break;
                                }
                            }
                            this.wsskip();
                            i = 3;
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (getch) {
                        case ')': {
                            this.wsskip();
                            i = 4;
                            continue;
                        }
                        case '|': {
                            this.wsskip();
                            switch (list.id) {
                                case 117: {
                                    this.bntok();
                                    break;
                                }
                                case 111: {
                                    this.mBuffIdx = -1;
                                    this.bname(false);
                                    break;
                                }
                                default: {
                                    this.panic("");
                                    break;
                                }
                            }
                            this.wsskip();
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (getch) {
                        case '#': {
                            this.bntok();
                            switch (this.bkeyword()) {
                                case 'F': {
                                    switch (this.wsskip()) {
                                        case '\"':
                                        case '\'': {
                                            i = 5;
                                            continue;
                                        }
                                        case '\uffff': {
                                            this.panic("");
                                            break;
                                        }
                                    }
                                    i = -1;
                                    continue;
                                }
                                case 'I':
                                case 'Q': {
                                    i = -1;
                                    continue;
                                }
                                default: {
                                    this.panic("");
                                    continue;
                                }
                            }
                            break;
                        }
                        case '\"':
                        case '\'': {
                            this.bkch();
                            i = 5;
                            continue;
                        }
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            continue;
                        }
                        case '%': {
                            this.pent(' ');
                            continue;
                        }
                        default: {
                            this.bkch();
                            i = -1;
                            continue;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (getch) {
                        case '\"':
                        case '\'': {
                            this.bkch();
                            this.bqstr('d');
                            list.list = this.pair(null);
                            list.list.chars = new char[list.chars.length + this.mBuffIdx + 3];
                            System.arraycopy(list.chars, 1, list.list.chars, 0, list.chars.length - 1);
                            list.list.chars[list.chars.length - 1] = '=';
                            list.list.chars[list.chars.length] = getch;
                            System.arraycopy(this.mBuff, 1, list.list.chars, list.chars.length + 1, this.mBuffIdx);
                            list.list.chars[list.chars.length + this.mBuffIdx + 1] = getch;
                            list.list.chars[list.chars.length + this.mBuffIdx + 2] = ' ';
                            i = -1;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void dtdnot() throws Exception {
        this.wsskip();
        final String name = this.name(false);
        this.wsskip();
        final Pair pubsys = this.pubsys('N');
        this.notDecl(name, pubsys.name, pubsys.value);
        this.del(pubsys);
    }
    
    private void attr(final Pair pair) throws Exception {
        switch (this.wsskip()) {
            case '/':
            case '>': {
                if ((pair.num & 0x2) == 0x0) {
                    pair.num |= 0x2;
                    final Input mInp = this.mInp;
                    for (Pair pair2 = this.mElm.list; pair2 != null; pair2 = pair2.next) {
                        if (pair2.list != null) {
                            if (this.find(pair.next, pair2.chars) == null) {
                                this.push(new Input(pair2.list.chars));
                            }
                        }
                    }
                    if (this.mInp != mInp) {
                        this.attr(pair);
                        return;
                    }
                }
                this.mAttrs.setLength(this.mAttrIdx);
                this.mItems = this.mAttrs.mItems;
                return;
            }
            case '\uffff': {
                this.panic("");
                break;
            }
        }
        pair.chars = this.qname(this.mIsNSAware);
        pair.name = pair.local();
        final String atype = this.atype(pair);
        this.wsskip();
        if (this.getch() != '=') {
            this.panic("");
        }
        this.bqstr((char)pair.id);
        final String s = new String(this.mBuff, 1, this.mBuffIdx);
        final Pair pair3 = this.pair(pair);
        pair3.num = (pair.num & 0xFFFFFFFE);
        if (!this.mIsNSAware || !this.isdecl(pair, s)) {
            ++this.mAttrIdx;
            this.attr(pair3);
            --this.mAttrIdx;
            final char c = (char)(this.mAttrIdx << 3);
            this.mItems[c + '\u0001'] = pair.qname();
            this.mItems[c + '\u0002'] = (this.mIsNSAware ? pair.name : "");
            this.mItems[c + '\u0003'] = s;
            this.mItems[c + '\u0004'] = atype;
            switch (pair.num & 0x3) {
                case 0: {
                    this.mItems[c + '\u0005'] = null;
                    break;
                }
                case 1: {
                    this.mItems[c + '\u0005'] = "d";
                    break;
                }
                default: {
                    this.mItems[c + '\u0005'] = "D";
                    break;
                }
            }
            this.mItems[c + '\0'] = ((pair.chars[0] != '\0') ? this.rslv(pair.chars) : "");
        }
        else {
            this.newPrefix();
            this.attr(pair3);
        }
        this.del(pair3);
    }
    
    private String atype(final Pair pair) throws Exception {
        pair.id = 99;
        final Pair find;
        if (this.mElm.list == null || (find = this.find(this.mElm.list, pair.chars)) == null) {
            return "CDATA";
        }
        pair.num |= 0x1;
        pair.id = 105;
        switch (find.id) {
            case 105: {
                return "ID";
            }
            case 114: {
                return "IDREF";
            }
            case 82: {
                return "IDREFS";
            }
            case 110: {
                return "ENTITY";
            }
            case 78: {
                return "ENTITIES";
            }
            case 116: {
                return "NMTOKEN";
            }
            case 84: {
                return "NMTOKENS";
            }
            case 117: {
                return "NMTOKEN";
            }
            case 111: {
                return "NOTATION";
            }
            case 99: {
                pair.id = 99;
                return "CDATA";
            }
            default: {
                this.panic("");
                return null;
            }
        }
    }
    
    private void comm() throws Exception {
        if (this.mPh == 0) {
            this.mPh = 1;
        }
        this.mBuffIdx = -1;
        int i = 0;
        while (i >= 0) {
            final char c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : this.getch();
            if (c == '\uffff') {
                this.panic("");
            }
            switch (i) {
                case 0: {
                    if (c == '-') {
                        i = 1;
                        continue;
                    }
                    this.panic("");
                    continue;
                }
                case 1: {
                    if (c == '-') {
                        i = 2;
                        continue;
                    }
                    this.panic("");
                    continue;
                }
                case 2: {
                    switch (c) {
                        case 45: {
                            i = 3;
                            continue;
                        }
                        default: {
                            this.bappend(c);
                            continue;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (c) {
                        case '-': {
                            i = 4;
                            continue;
                        }
                        default: {
                            this.bappend('-');
                            this.bappend(c);
                            i = 2;
                            continue;
                        }
                    }
                    break;
                }
                case 4: {
                    if (c == '>') {
                        this.comm(this.mBuff, this.mBuffIdx + 1);
                        i = -1;
                        continue;
                    }
                    break;
                }
            }
            this.panic("");
        }
    }
    
    private void pi() throws Exception {
        String name = null;
        this.mBuffIdx = -1;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            if (getch == '\uffff') {
                this.panic("");
            }
            switch (i) {
                case 0: {
                    switch (this.chtyp(getch)) {
                        case ':':
                        case 'A':
                        case 'X':
                        case '_':
                        case 'a': {
                            this.bkch();
                            name = this.name(false);
                            if (name.length() == 0 || this.mXml.name.equals(name.toLowerCase())) {
                                this.panic("");
                            }
                            if (this.mPh == 0) {
                                this.mPh = 1;
                            }
                            this.wsskip();
                            i = 1;
                            this.mBuffIdx = -1;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (getch) {
                        case '?': {
                            i = 2;
                            continue;
                        }
                        default: {
                            this.bappend(getch);
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (getch) {
                        case '>': {
                            this.pi(name, new String(this.mBuff, 0, this.mBuffIdx + 1));
                            i = -1;
                            continue;
                        }
                        case '?': {
                            this.bappend('?');
                            continue;
                        }
                        default: {
                            this.bappend('?');
                            this.bappend(getch);
                            i = 1;
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void cdat() throws Exception {
        this.mBuffIdx = -1;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    if (getch == '[') {
                        i = 1;
                        continue;
                    }
                    this.panic("");
                    continue;
                }
                case 1: {
                    if (this.chtyp(getch) == 'A') {
                        this.bappend(getch);
                        continue;
                    }
                    if (!"CDATA".equals(new String(this.mBuff, 0, this.mBuffIdx + 1))) {
                        this.panic("");
                    }
                    this.bkch();
                    i = 2;
                    continue;
                }
                case 2: {
                    if (getch != '[') {
                        this.panic("");
                    }
                    this.mBuffIdx = -1;
                    i = 3;
                    continue;
                }
                case 3: {
                    if (getch != ']') {
                        this.bappend(getch);
                        continue;
                    }
                    i = 4;
                    continue;
                }
                case 4: {
                    if (getch != ']') {
                        this.bappend(']');
                        this.bappend(getch);
                        i = 3;
                        continue;
                    }
                    i = 5;
                    continue;
                }
                case 5: {
                    switch (getch) {
                        case ']': {
                            this.bappend(']');
                            continue;
                        }
                        case '>': {
                            this.bflash();
                            i = -1;
                            continue;
                        }
                        default: {
                            this.bappend(']');
                            this.bappend(']');
                            this.bappend(getch);
                            i = 3;
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    protected String name(final boolean b) throws Exception {
        this.mBuffIdx = -1;
        this.bname(b);
        return new String(this.mBuff, 1, this.mBuffIdx);
    }
    
    protected char[] qname(final boolean b) throws Exception {
        this.mBuffIdx = -1;
        this.bname(b);
        final char[] array = new char[this.mBuffIdx + 1];
        System.arraycopy(this.mBuff, 0, array, 0, this.mBuffIdx + 1);
        return array;
    }
    
    private void pubsys(final Input input) throws Exception {
        final Pair pubsys = this.pubsys(' ');
        input.pubid = pubsys.name;
        input.sysid = pubsys.value;
        this.del(pubsys);
    }
    
    private Pair pubsys(final char c) throws Exception {
        final Pair pair = this.pair(null);
        final String name = this.name(false);
        if ("PUBLIC".equals(name)) {
            this.bqstr('i');
            pair.name = new String(this.mBuff, 1, this.mBuffIdx);
            switch (this.wsskip()) {
                case '\"':
                case '\'': {
                    this.bqstr(' ');
                    pair.value = new String(this.mBuff, 1, this.mBuffIdx);
                    return pair;
                }
                case '\uffff': {
                    this.panic("");
                    break;
                }
            }
            if (c != 'N') {
                this.panic("");
            }
            pair.value = null;
            return pair;
        }
        if ("SYSTEM".equals(name)) {
            pair.name = null;
            this.bqstr(' ');
            pair.value = new String(this.mBuff, 1, this.mBuffIdx);
            return pair;
        }
        this.panic("");
        return null;
    }
    
    protected String eqstr(final char c) throws Exception {
        if (c == '=') {
            this.wsskip();
            if (this.getch() != '=') {
                this.panic("");
            }
        }
        this.bqstr((c == '=') ? '-' : c);
        return new String(this.mBuff, 1, this.mBuffIdx);
    }
    
    private String ent(final char c) throws Exception {
        final int n = this.mBuffIdx + 1;
        String s = null;
        this.mESt = '\u0100';
        this.bappend('&');
        int i = 0;
        while (i >= 0) {
            char c2 = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : this.getch();
            switch (i) {
                case 0:
                case 1: {
                    switch (this.chtyp(c2)) {
                        case '-':
                        case '.':
                        case 'd': {
                            if (i != 1) {
                                this.panic("");
                            }
                        }
                        case 'A':
                        case 'X':
                        case '_':
                        case 'a': {
                            this.bappend(c2);
                            this.eappend(c2);
                            i = 1;
                            continue;
                        }
                        case ':': {
                            if (this.mIsNSAware) {
                                this.panic("");
                            }
                            this.bappend(c2);
                            this.eappend(c2);
                            i = 1;
                            continue;
                        }
                        case ';': {
                            if (this.mESt < '\u0100') {
                                this.mBuffIdx = n - 1;
                                this.bappend(this.mESt);
                                i = -1;
                                continue;
                            }
                            if (this.mPh == 2) {
                                this.bappend(';');
                                i = -1;
                                continue;
                            }
                            s = new String(this.mBuff, n + 1, this.mBuffIdx - n);
                            final Input input = this.mEnt.get(s);
                            this.mBuffIdx = n - 1;
                            if (input != null) {
                                if (input.chars == null) {
                                    final InputSource resolveEnt = this.resolveEnt(s, input.pubid, input.sysid);
                                    if (resolveEnt != null) {
                                        this.push(new Input(512));
                                        this.setinp(resolveEnt);
                                        this.mInp.pubid = input.pubid;
                                        this.mInp.sysid = input.sysid;
                                        s = null;
                                    }
                                    else if (c != 'x') {
                                        this.panic("");
                                    }
                                }
                                else {
                                    this.push(input);
                                    s = null;
                                }
                            }
                            else if (c != 'x') {
                                this.panic("");
                            }
                            i = -1;
                            continue;
                        }
                        case '#': {
                            if (i != 0) {
                                this.panic("");
                            }
                            i = 2;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (this.chtyp(c2)) {
                        case 'd': {
                            this.bappend(c2);
                            continue;
                        }
                        case ';': {
                            try {
                                final int int1 = Integer.parseInt(new String(this.mBuff, n + 1, this.mBuffIdx - n), 10);
                                if (int1 >= 65535) {
                                    this.panic("");
                                }
                                c2 = (char)int1;
                            }
                            catch (final NumberFormatException ex) {
                                this.panic("");
                            }
                            this.mBuffIdx = n - 1;
                            if (c2 == ' ' || this.mInp.next != null) {
                                this.bappend(c2, c);
                            }
                            else {
                                this.bappend(c2);
                            }
                            i = -1;
                            continue;
                        }
                        case 'a': {
                            if (this.mBuffIdx == n && c2 == 'x') {
                                i = 3;
                                continue;
                            }
                            break;
                        }
                    }
                    this.panic("");
                    continue;
                }
                case 3: {
                    switch (this.chtyp(c2)) {
                        case 'A':
                        case 'a':
                        case 'd': {
                            this.bappend(c2);
                            continue;
                        }
                        case ';': {
                            try {
                                final int int2 = Integer.parseInt(new String(this.mBuff, n + 1, this.mBuffIdx - n), 16);
                                if (int2 >= 65535) {
                                    this.panic("");
                                }
                                c2 = (char)int2;
                            }
                            catch (final NumberFormatException ex2) {
                                this.panic("");
                            }
                            this.mBuffIdx = n - 1;
                            if (c2 == ' ' || this.mInp.next != null) {
                                this.bappend(c2, c);
                            }
                            else {
                                this.bappend(c2);
                            }
                            i = -1;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
        return s;
    }
    
    private void pent(final char c) throws Exception {
        final int n = this.mBuffIdx + 1;
        this.bappend('%');
        if (this.mPh != 2) {
            return;
        }
        this.bname(false);
        final String s = new String(this.mBuff, n + 2, this.mBuffIdx - n - 1);
        if (this.getch() != ';') {
            this.panic("");
        }
        final Input input = this.mPEnt.get(s);
        this.mBuffIdx = n - 1;
        if (input != null) {
            if (input.chars == null) {
                final InputSource resolveEnt = this.resolveEnt(s, input.pubid, input.sysid);
                if (resolveEnt != null) {
                    if (c != '-') {
                        this.bappend(' ');
                    }
                    this.push(new Input(512));
                    this.setinp(resolveEnt);
                    this.mInp.pubid = input.pubid;
                    this.mInp.sysid = input.sysid;
                }
                else {
                    this.skippedEnt("%" + s);
                }
            }
            else {
                if (c == '-') {
                    input.chIdx = 1;
                }
                else {
                    this.bappend(' ');
                    input.chIdx = 0;
                }
                this.push(input);
            }
        }
        else {
            this.skippedEnt("%" + s);
        }
    }
    
    private boolean isdecl(final Pair pair, final String s) {
        if (pair.chars[0] == '\0') {
            if ("xmlns".equals(pair.name)) {
                this.mPref = this.pair(this.mPref);
                this.mPref.list = this.mElm;
                this.mPref.value = s;
                this.mPref.name = "";
                this.mPref.chars = Parser.NONS;
                final Pair mElm = this.mElm;
                ++mElm.num;
                return true;
            }
        }
        else if (pair.eqpref(Parser.XMLNS)) {
            final int length = pair.name.length();
            this.mPref = this.pair(this.mPref);
            this.mPref.list = this.mElm;
            this.mPref.value = s;
            this.mPref.name = pair.name;
            (this.mPref.chars = new char[length + 1])[0] = (char)(length + 1);
            pair.name.getChars(0, length, this.mPref.chars, 1);
            final Pair mElm2 = this.mElm;
            ++mElm2.num;
            return true;
        }
        return false;
    }
    
    private String rslv(final char[] array) throws Exception {
        for (Pair pair = this.mPref; pair != null; pair = pair.next) {
            if (pair.eqpref(array)) {
                return pair.value;
            }
        }
        if (array[0] == '\u0001') {
            for (Pair pair2 = this.mPref; pair2 != null; pair2 = pair2.next) {
                if (pair2.chars[0] == '\0') {
                    return pair2.value;
                }
            }
        }
        this.panic("");
        return null;
    }
    
    protected char wsskip() throws IOException {
        char c;
        do {
            c = ((this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : this.getch());
        } while (c < '\u0080' && Parser.nmttyp[c] == 3);
        --this.mChIdx;
        return c;
    }
    
    protected abstract void docType(final String p0, final String p1, final String p2) throws SAXException;
    
    protected abstract void comm(final char[] p0, final int p1);
    
    protected abstract void pi(final String p0, final String p1) throws Exception;
    
    protected abstract void newPrefix() throws Exception;
    
    protected abstract void skippedEnt(final String p0) throws Exception;
    
    protected abstract InputSource resolveEnt(final String p0, final String p1, final String p2) throws Exception;
    
    protected abstract void notDecl(final String p0, final String p1, final String p2) throws Exception;
    
    protected abstract void unparsedEntDecl(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    protected abstract void panic(final String p0) throws Exception;
    
    private void bname(final boolean b) throws Exception {
        ++this.mBuffIdx;
        int mBuffIdx;
        final int n = mBuffIdx = this.mBuffIdx;
        int n3;
        int n2 = n3 = n + 1;
        int n4 = this.mChIdx;
        int n5 = (short)(b ? 0 : 2);
        while (true) {
            if (this.mChIdx >= this.mChLen) {
                this.bcopy(n4, n3);
                this.getch();
                --this.mChIdx;
                n4 = this.mChIdx;
                n3 = n2;
            }
            final char c = this.mChars[this.mChIdx++];
            char c2 = '\0';
            if (c < '\u0080') {
                c2 = (char)Parser.nmttyp[c];
            }
            else if (c == '\uffff') {
                this.panic("");
            }
            switch (n5) {
                case 0:
                case 2: {
                    switch (c2) {
                        case 0: {
                            ++n2;
                            n5 = (short)(n5 + 1);
                            continue;
                        }
                        case 1: {
                            --this.mChIdx;
                            n5 = (short)(n5 + 1);
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 1:
                case 3: {
                    switch (c2) {
                        case 0:
                        case 2: {
                            ++n2;
                            continue;
                        }
                        case 1: {
                            ++n2;
                            if (!b) {
                                continue;
                            }
                            if (mBuffIdx != n) {
                                this.panic("");
                            }
                            mBuffIdx = n2 - 1;
                            if (n5 == 1) {
                                n5 = 2;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            --this.mChIdx;
                            this.bcopy(n4, n3);
                            this.mBuff[n] = (char)(mBuffIdx - n);
                            return;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
    }
    
    private void bntok() throws Exception {
        this.mBuffIdx = -1;
        this.bappend('\0');
    Label_0118:
        while (true) {
            final char getch = this.getch();
            switch (this.chtyp(getch)) {
                case '-':
                case '.':
                case ':':
                case 'A':
                case 'X':
                case '_':
                case 'a':
                case 'd': {
                    this.bappend(getch);
                    continue;
                }
                case 'Z': {
                    this.panic("");
                }
                default: {
                    break Label_0118;
                }
            }
        }
        this.bkch();
    }
    
    private char bkeyword() throws Exception {
        final String s = new String(this.mBuff, 1, this.mBuffIdx);
        Label_0499: {
            switch (s.length()) {
                case 2: {
                    return (char)("ID".equals(s) ? 105 : 63);
                }
                case 5: {
                    switch (this.mBuff[1]) {
                        case 'I': {
                            return (char)("IDREF".equals(s) ? 114 : 63);
                        }
                        case 'C': {
                            return (char)("CDATA".equals(s) ? 99 : 63);
                        }
                        case 'F': {
                            return (char)("FIXED".equals(s) ? 70 : 63);
                        }
                        default: {
                            break Label_0499;
                        }
                    }
                    break;
                }
                case 6: {
                    switch (this.mBuff[1]) {
                        case 'I': {
                            return (char)("IDREFS".equals(s) ? 82 : 63);
                        }
                        case 'E': {
                            return (char)("ENTITY".equals(s) ? 110 : 63);
                        }
                        default: {
                            break Label_0499;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (this.mBuff[1]) {
                        case 'I': {
                            return (char)("IMPLIED".equals(s) ? 73 : 63);
                        }
                        case 'N': {
                            return (char)("NMTOKEN".equals(s) ? 116 : 63);
                        }
                        case 'A': {
                            return (char)("ATTLIST".equals(s) ? 97 : 63);
                        }
                        case 'E': {
                            return (char)("ELEMENT".equals(s) ? 101 : 63);
                        }
                        default: {
                            break Label_0499;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (this.mBuff[2]) {
                        case 'N': {
                            return (char)("ENTITIES".equals(s) ? 78 : 63);
                        }
                        case 'M': {
                            return (char)("NMTOKENS".equals(s) ? 84 : 63);
                        }
                        case 'O': {
                            return (char)("NOTATION".equals(s) ? 111 : 63);
                        }
                        case 'E': {
                            return (char)("REQUIRED".equals(s) ? 81 : 63);
                        }
                        default: {
                            break Label_0499;
                        }
                    }
                    break;
                }
            }
        }
        return '?';
    }
    
    private void bqstr(final char c) throws Exception {
        final Input mInp = this.mInp;
        this.mBuffIdx = -1;
        this.bappend('\0');
        int i = 0;
        while (i >= 0) {
            char c2 = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : this.getch();
            switch (i) {
                case 0: {
                    switch (c2) {
                        case 9:
                        case 10:
                        case 13:
                        case 32: {
                            continue;
                        }
                        case 39: {
                            i = 2;
                            continue;
                        }
                        case 34: {
                            i = 3;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 2:
                case 3: {
                    switch (c2) {
                        case 39: {
                            if (i == 2 && this.mInp == mInp) {
                                i = -1;
                                continue;
                            }
                            this.bappend(c2);
                            continue;
                        }
                        case 34: {
                            if (i == 3 && this.mInp == mInp) {
                                i = -1;
                                continue;
                            }
                            this.bappend(c2);
                            continue;
                        }
                        case 38: {
                            if (c != 'd') {
                                this.ent(c);
                                continue;
                            }
                            this.bappend(c2);
                            continue;
                        }
                        case 37: {
                            if (c == 'd') {
                                this.pent('-');
                                continue;
                            }
                            this.bappend(c2);
                            continue;
                        }
                        case 60: {
                            if (c == '-' || c == 'd') {
                                this.bappend(c2);
                                continue;
                            }
                            this.panic("");
                            continue;
                        }
                        case 65535: {
                            this.panic("");
                        }
                        case 13: {
                            if (c != ' ' && this.mInp.next == null) {
                                if (this.getch() != '\n') {
                                    this.bkch();
                                }
                                c2 = '\n';
                                break;
                            }
                            break;
                        }
                    }
                    this.bappend(c2, c);
                    continue;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
        if (c == 'i' && this.mBuff[this.mBuffIdx] == ' ') {
            --this.mBuffIdx;
        }
    }
    
    protected abstract void bflash() throws Exception;
    
    protected abstract void bflash_ws() throws Exception;
    
    private void bappend(char c, final char c2) {
        Label_0149: {
            switch (c2) {
                case 'i': {
                    switch (c) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            if (this.mBuffIdx > 0 && this.mBuff[this.mBuffIdx] != ' ') {
                                this.bappend(' ');
                            }
                            return;
                        }
                        default: {
                            break Label_0149;
                        }
                    }
                    break;
                }
                case 'c': {
                    switch (c) {
                        case '\t':
                        case '\n':
                        case '\r': {
                            c = ' ';
                            break Label_0149;
                        }
                        default: {
                            break Label_0149;
                        }
                    }
                    break;
                }
            }
        }
        ++this.mBuffIdx;
        if (this.mBuffIdx < this.mBuff.length) {
            this.mBuff[this.mBuffIdx] = c;
        }
        else {
            --this.mBuffIdx;
            this.bappend(c);
        }
    }
    
    private void bappend(final char c) {
        try {
            this.mBuff[++this.mBuffIdx] = c;
        }
        catch (final Exception ex) {
            final char[] mBuff = new char[this.mBuff.length << 1];
            System.arraycopy(this.mBuff, 0, mBuff, 0, this.mBuff.length);
            (this.mBuff = mBuff)[this.mBuffIdx] = c;
        }
    }
    
    private void bcopy(final int n, final int n2) {
        final int n3 = this.mChIdx - n;
        if (n2 + n3 + 1 >= this.mBuff.length) {
            final char[] mBuff = new char[this.mBuff.length + n3];
            System.arraycopy(this.mBuff, 0, mBuff, 0, this.mBuff.length);
            this.mBuff = mBuff;
        }
        System.arraycopy(this.mChars, n, this.mBuff, n2, n3);
        this.mBuffIdx += n3;
    }
    
    private void eappend(final char c) {
        Label_0466: {
            switch (this.mESt) {
                case '\u0100': {
                    switch (c) {
                        case 'l': {
                            this.mESt = '\u0101';
                            break Label_0466;
                        }
                        case 'g': {
                            this.mESt = '\u0102';
                            break Label_0466;
                        }
                        case 'a': {
                            this.mESt = '\u0103';
                            break Label_0466;
                        }
                        case 'q': {
                            this.mESt = '\u0107';
                            break Label_0466;
                        }
                        default: {
                            this.mESt = '\u0200';
                            break Label_0466;
                        }
                    }
                    break;
                }
                case '\u0101': {
                    this.mESt = ((c == 't') ? '<' : '\u0200');
                    break;
                }
                case '\u0102': {
                    this.mESt = ((c == 't') ? '>' : '\u0200');
                    break;
                }
                case '\u0103': {
                    switch (c) {
                        case 'm': {
                            this.mESt = '\u0104';
                            break Label_0466;
                        }
                        case 'p': {
                            this.mESt = '\u0105';
                            break Label_0466;
                        }
                        default: {
                            this.mESt = '\u0200';
                            break Label_0466;
                        }
                    }
                    break;
                }
                case '\u0104': {
                    this.mESt = ((c == 'p') ? '&' : '\u0200');
                    break;
                }
                case '\u0105': {
                    this.mESt = ((c == 'o') ? '\u0106' : '\u0200');
                    break;
                }
                case '\u0106': {
                    this.mESt = ((c == 's') ? '\'' : '\u0200');
                    break;
                }
                case '\u0107': {
                    this.mESt = ((c == 'u') ? '\u0108' : '\u0200');
                    break;
                }
                case '\u0108': {
                    this.mESt = ((c == 'o') ? '\u0109' : '\u0200');
                    break;
                }
                case '\u0109': {
                    this.mESt = ((c == 't') ? '\"' : '\u0200');
                    break;
                }
                case '\"':
                case '&':
                case '\'':
                case '<':
                case '>': {
                    this.mESt = '\u0200';
                    break;
                }
            }
        }
    }
    
    protected void setinp(final InputSource inputSource) throws Exception {
        Reader src = null;
        this.mChIdx = 0;
        this.mChLen = 0;
        this.mChars = this.mInp.chars;
        this.mInp.src = null;
        if (this.mPh < 0) {
            this.mIsSAlone = false;
        }
        this.mIsSAloneSet = false;
        if (inputSource.getCharacterStream() != null) {
            src = inputSource.getCharacterStream();
            this.xml(src);
        }
        else if (inputSource.getByteStream() != null) {
            if (inputSource.getEncoding() != null) {
                final String upperCase = inputSource.getEncoding().toUpperCase();
                if (upperCase.equals("UTF-16")) {
                    src = this.bom(inputSource.getByteStream(), 'U');
                }
                else {
                    src = this.enc(upperCase, inputSource.getByteStream());
                }
                this.xml(src);
            }
            else {
                src = this.bom(inputSource.getByteStream(), ' ');
                if (src == null) {
                    final String xml = this.xml(this.enc("UTF-8", inputSource.getByteStream()));
                    if (xml.startsWith("UTF-16")) {
                        this.panic("");
                    }
                    src = this.enc(xml, inputSource.getByteStream());
                }
                else {
                    this.xml(src);
                }
            }
        }
        else {
            this.panic("");
        }
        this.mInp.src = src;
        this.mInp.pubid = inputSource.getPublicId();
        this.mInp.sysid = inputSource.getSystemId();
    }
    
    private Reader bom(final InputStream inputStream, final char c) throws Exception {
        final int read = inputStream.read();
        switch (read) {
            case 239: {
                if (c == 'U') {
                    this.panic("");
                }
                if (inputStream.read() != 187) {
                    this.panic("");
                }
                if (inputStream.read() != 191) {
                    this.panic("");
                }
                return new ReaderUTF8(inputStream);
            }
            case 254: {
                if (inputStream.read() != 255) {
                    this.panic("");
                }
                return new ReaderUTF16(inputStream, 'b');
            }
            case 255: {
                if (inputStream.read() != 254) {
                    this.panic("");
                }
                return new ReaderUTF16(inputStream, 'l');
            }
            case -1: {
                this.mChars[this.mChIdx++] = '\uffff';
                return new ReaderUTF8(inputStream);
            }
            default: {
                if (c == 'U') {
                    this.panic("");
                }
                switch (read & 0xF0) {
                    case 192:
                    case 208: {
                        this.mChars[this.mChIdx++] = (char)((read & 0x1F) << 6 | (inputStream.read() & 0x3F));
                        break;
                    }
                    case 224: {
                        this.mChars[this.mChIdx++] = (char)((read & 0xF) << 12 | (inputStream.read() & 0x3F) << 6 | (inputStream.read() & 0x3F));
                        break;
                    }
                    case 240: {
                        throw new UnsupportedEncodingException();
                    }
                    default: {
                        this.mChars[this.mChIdx++] = (char)read;
                        break;
                    }
                }
                return null;
            }
        }
    }
    
    private String xml(final Reader reader) throws Exception {
        String xmlenc = "UTF-8";
        int n;
        if (this.mChIdx != 0) {
            n = (short)((this.mChars[0] == '<') ? 1 : -1);
        }
        else {
            n = 0;
        }
        while (n >= 0 && this.mChIdx < this.mChars.length) {
            final int read;
            final char c = ((read = reader.read()) >= 0) ? ((char)read) : '\uffff';
            this.mChars[this.mChIdx++] = c;
            switch (n) {
                case 0: {
                    switch (c) {
                        case '<': {
                            n = 1;
                            continue;
                        }
                        case '\ufeff': {
                            final int read2;
                            final char c2 = ((read2 = reader.read()) >= 0) ? ((char)read2) : '\uffff';
                            this.mChars[this.mChIdx - 1] = c2;
                            n = (short)((c2 == '<') ? 1 : -1);
                            continue;
                        }
                        default: {
                            n = -1;
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    n = (short)((c == '?') ? 2 : -1);
                    continue;
                }
                case 2: {
                    n = (short)((c == 'x') ? 3 : -1);
                    continue;
                }
                case 3: {
                    n = (short)((c == 'm') ? 4 : -1);
                    continue;
                }
                case 4: {
                    n = (short)((c == 'l') ? 5 : -1);
                    continue;
                }
                case 5: {
                    switch (c) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            n = 6;
                            continue;
                        }
                        default: {
                            n = -1;
                            continue;
                        }
                    }
                    break;
                }
                case 6: {
                    switch (c) {
                        case '?': {
                            n = 7;
                            continue;
                        }
                        case '\uffff': {
                            n = -2;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (c) {
                        case '>':
                        case '\uffff': {
                            n = -2;
                            continue;
                        }
                        default: {
                            n = 6;
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
        this.mChLen = this.mChIdx;
        this.mChIdx = 0;
        if (n == -1) {
            return xmlenc;
        }
        this.mChIdx = 5;
        int i = 0;
        while (i >= 0) {
            final char getch = this.getch();
            switch (i) {
                case 0: {
                    if (this.chtyp(getch) != ' ') {
                        this.bkch();
                        i = 1;
                        continue;
                    }
                    continue;
                }
                case 1:
                case 2:
                case 3: {
                    switch (this.chtyp(getch)) {
                        case 'A':
                        case '_':
                        case 'a': {
                            this.bkch();
                            final String lowerCase = this.name(false).toLowerCase();
                            if ("version".equals(lowerCase)) {
                                if (i != 1) {
                                    this.panic("");
                                }
                                if (!"1.0".equals(this.eqstr('='))) {
                                    this.panic("");
                                }
                                this.mInp.xmlver = '\u0100';
                                i = 2;
                                continue;
                            }
                            if ("encoding".equals(lowerCase)) {
                                if (i != 2) {
                                    this.panic("");
                                }
                                this.mInp.xmlenc = this.eqstr('=').toUpperCase();
                                xmlenc = this.mInp.xmlenc;
                                i = 3;
                                continue;
                            }
                            if ("standalone".equals(lowerCase)) {
                                if (i == 1 || this.mPh >= 0) {
                                    this.panic("");
                                }
                                final String lowerCase2 = this.eqstr('=').toLowerCase();
                                if (lowerCase2.equals("yes")) {
                                    this.mIsSAlone = true;
                                }
                                else if (lowerCase2.equals("no")) {
                                    this.mIsSAlone = false;
                                }
                                else {
                                    this.panic("");
                                }
                                this.mIsSAloneSet = true;
                                i = 4;
                                continue;
                            }
                            this.panic("");
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        case '?': {
                            if (i == 1) {
                                this.panic("");
                            }
                            this.bkch();
                            i = 4;
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (this.chtyp(getch)) {
                        case '?': {
                            if (this.getch() != '>') {
                                this.panic("");
                            }
                            if (this.mPh <= 0) {
                                this.mPh = 1;
                            }
                            i = -1;
                            continue;
                        }
                        case ' ': {
                            continue;
                        }
                        default: {
                            this.panic("");
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    this.panic("");
                    continue;
                }
            }
        }
        return xmlenc;
    }
    
    private Reader enc(final String s, final InputStream inputStream) throws UnsupportedEncodingException {
        if (s.equals("UTF-8")) {
            return new ReaderUTF8(inputStream);
        }
        if (s.equals("UTF-16LE")) {
            return new ReaderUTF16(inputStream, 'l');
        }
        if (s.equals("UTF-16BE")) {
            return new ReaderUTF16(inputStream, 'b');
        }
        return new InputStreamReader(inputStream, s);
    }
    
    protected void push(final Input mInp) {
        this.mInp.chLen = this.mChLen;
        this.mInp.chIdx = this.mChIdx;
        mInp.next = this.mInp;
        this.mInp = mInp;
        this.mChars = mInp.chars;
        this.mChLen = mInp.chLen;
        this.mChIdx = mInp.chIdx;
    }
    
    protected void pop() {
        if (this.mInp.src != null) {
            try {
                this.mInp.src.close();
            }
            catch (final IOException ex) {}
            this.mInp.src = null;
        }
        this.mInp = this.mInp.next;
        if (this.mInp != null) {
            this.mChars = this.mInp.chars;
            this.mChLen = this.mInp.chLen;
            this.mChIdx = this.mInp.chIdx;
        }
        else {
            this.mChars = null;
            this.mChLen = 0;
            this.mChIdx = 0;
        }
    }
    
    protected char chtyp(final char c) {
        if (c < '\u0080') {
            return (char)Parser.asctyp[c];
        }
        return (c != '\uffff') ? 'X' : 'Z';
    }
    
    protected char getch() throws IOException {
        if (this.mChIdx >= this.mChLen) {
            if (this.mInp.src == null) {
                this.pop();
                return this.getch();
            }
            final int read = this.mInp.src.read(this.mChars, 0, this.mChars.length);
            if (read < 0) {
                if (this.mInp != this.mDoc) {
                    this.pop();
                    return this.getch();
                }
                this.mChars[0] = '\uffff';
                this.mChLen = 1;
            }
            else {
                this.mChLen = read;
            }
            this.mChIdx = 0;
        }
        return this.mChars[this.mChIdx++];
    }
    
    protected void bkch() throws Exception {
        if (this.mChIdx <= 0) {
            this.panic("");
        }
        --this.mChIdx;
    }
    
    protected void setch(final char c) {
        this.mChars[this.mChIdx] = c;
    }
    
    protected Pair find(final Pair pair, final char[] array) {
        for (Pair next = pair; next != null; next = next.next) {
            if (next.eqname(array)) {
                return next;
            }
        }
        return null;
    }
    
    protected Pair pair(final Pair next) {
        Pair mDltd;
        if (this.mDltd != null) {
            mDltd = this.mDltd;
            this.mDltd = mDltd.next;
        }
        else {
            mDltd = new Pair();
        }
        mDltd.next = next;
        return mDltd;
    }
    
    protected Pair del(final Pair mDltd) {
        final Pair next = mDltd.next;
        mDltd.name = null;
        mDltd.value = null;
        mDltd.chars = null;
        mDltd.list = null;
        mDltd.next = this.mDltd;
        this.mDltd = mDltd;
        return next;
    }
    
    static {
        (NONS = new char[1])[0] = '\0';
        (XML = new char[4])[0] = '\u0004';
        Parser.XML[1] = 'x';
        Parser.XML[2] = 'm';
        Parser.XML[3] = 'l';
        (XMLNS = new char[6])[0] = '\u0006';
        Parser.XMLNS[1] = 'x';
        Parser.XMLNS[2] = 'm';
        Parser.XMLNS[3] = 'l';
        Parser.XMLNS[4] = 'n';
        Parser.XMLNS[5] = 's';
        int i = 0;
        asctyp = new byte[128];
        while (i < 32) {
            final byte[] asctyp2 = Parser.asctyp;
            final int n = i;
            i = (short)(i + 1);
            asctyp2[n] = 122;
        }
        Parser.asctyp[9] = 32;
        Parser.asctyp[13] = 32;
        Parser.asctyp[10] = 32;
        while (i < 48) {
            final byte[] asctyp3 = Parser.asctyp;
            final int n2 = i;
            final int n3 = i;
            i = (short)(i + 1);
            asctyp3[n2] = (byte)n3;
        }
        while (i <= 57) {
            final byte[] asctyp4 = Parser.asctyp;
            final int n4 = i;
            i = (short)(i + 1);
            asctyp4[n4] = 100;
        }
        while (i < 65) {
            final byte[] asctyp5 = Parser.asctyp;
            final int n5 = i;
            final int n6 = i;
            i = (short)(i + 1);
            asctyp5[n5] = (byte)n6;
        }
        while (i <= 90) {
            final byte[] asctyp6 = Parser.asctyp;
            final int n7 = i;
            i = (short)(i + 1);
            asctyp6[n7] = 65;
        }
        while (i < 97) {
            final byte[] asctyp7 = Parser.asctyp;
            final int n8 = i;
            final int n9 = i;
            i = (short)(i + 1);
            asctyp7[n8] = (byte)n9;
        }
        while (i <= 122) {
            final byte[] asctyp8 = Parser.asctyp;
            final int n10 = i;
            i = (short)(i + 1);
            asctyp8[n10] = 97;
        }
        while (i < 128) {
            final byte[] asctyp9 = Parser.asctyp;
            final int n11 = i;
            final int n12 = i;
            i = (short)(i + 1);
            asctyp9[n11] = (byte)n12;
        }
        nmttyp = new byte[128];
        int j;
        for (j = 0; j < 48; j = (short)(j + 1)) {
            Parser.nmttyp[j] = -1;
        }
        while (j <= 57) {
            final byte[] nmttyp2 = Parser.nmttyp;
            final int n13 = j;
            j = (short)(j + 1);
            nmttyp2[n13] = 2;
        }
        while (j < 65) {
            final byte[] nmttyp3 = Parser.nmttyp;
            final int n14 = j;
            j = (short)(j + 1);
            nmttyp3[n14] = -1;
        }
        for (int k = 91; k < 97; k = (short)(k + 1)) {
            Parser.nmttyp[k] = -1;
        }
        for (int l = 123; l < 128; l = (short)(l + 1)) {
            Parser.nmttyp[l] = -1;
        }
        Parser.nmttyp[95] = 0;
        Parser.nmttyp[58] = 1;
        Parser.nmttyp[46] = 2;
        Parser.nmttyp[45] = 2;
        Parser.nmttyp[32] = 3;
        Parser.nmttyp[9] = 3;
        Parser.nmttyp[13] = 3;
        Parser.nmttyp[10] = 3;
    }
}
