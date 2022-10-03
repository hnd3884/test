package org.apache.xmlbeans;

import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;

public interface XmlCursor extends XmlTokenSource
{
    void dispose();
    
    boolean toCursor(final XmlCursor p0);
    
    void push();
    
    boolean pop();
    
    void selectPath(final String p0);
    
    void selectPath(final String p0, final XmlOptions p1);
    
    boolean hasNextSelection();
    
    boolean toNextSelection();
    
    boolean toSelection(final int p0);
    
    int getSelectionCount();
    
    void addToSelection();
    
    void clearSelections();
    
    boolean toBookmark(final XmlBookmark p0);
    
    XmlBookmark toNextBookmark(final Object p0);
    
    XmlBookmark toPrevBookmark(final Object p0);
    
    QName getName();
    
    void setName(final QName p0);
    
    String namespaceForPrefix(final String p0);
    
    String prefixForNamespace(final String p0);
    
    void getAllNamespaces(final Map p0);
    
    XmlObject getObject();
    
    TokenType currentTokenType();
    
    boolean isStartdoc();
    
    boolean isEnddoc();
    
    boolean isStart();
    
    boolean isEnd();
    
    boolean isText();
    
    boolean isAttr();
    
    boolean isNamespace();
    
    boolean isComment();
    
    boolean isProcinst();
    
    boolean isContainer();
    
    boolean isFinish();
    
    boolean isAnyAttr();
    
    TokenType prevTokenType();
    
    boolean hasNextToken();
    
    boolean hasPrevToken();
    
    TokenType toNextToken();
    
    TokenType toPrevToken();
    
    TokenType toFirstContentToken();
    
    TokenType toEndToken();
    
    int toNextChar(final int p0);
    
    int toPrevChar(final int p0);
    
    boolean toNextSibling();
    
    boolean toPrevSibling();
    
    boolean toParent();
    
    boolean toFirstChild();
    
    boolean toLastChild();
    
    boolean toChild(final String p0);
    
    boolean toChild(final String p0, final String p1);
    
    boolean toChild(final QName p0);
    
    boolean toChild(final int p0);
    
    boolean toChild(final QName p0, final int p1);
    
    boolean toNextSibling(final String p0);
    
    boolean toNextSibling(final String p0, final String p1);
    
    boolean toNextSibling(final QName p0);
    
    boolean toFirstAttribute();
    
    boolean toLastAttribute();
    
    boolean toNextAttribute();
    
    boolean toPrevAttribute();
    
    String getAttributeText(final QName p0);
    
    boolean setAttributeText(final QName p0, final String p1);
    
    boolean removeAttribute(final QName p0);
    
    String getTextValue();
    
    int getTextValue(final char[] p0, final int p1, final int p2);
    
    void setTextValue(final String p0);
    
    void setTextValue(final char[] p0, final int p1, final int p2);
    
    String getChars();
    
    int getChars(final char[] p0, final int p1, final int p2);
    
    void toStartDoc();
    
    void toEndDoc();
    
    boolean isInSameDocument(final XmlCursor p0);
    
    int comparePosition(final XmlCursor p0);
    
    boolean isLeftOf(final XmlCursor p0);
    
    boolean isAtSamePositionAs(final XmlCursor p0);
    
    boolean isRightOf(final XmlCursor p0);
    
    XmlCursor execQuery(final String p0);
    
    XmlCursor execQuery(final String p0, final XmlOptions p1);
    
    ChangeStamp getDocChangeStamp();
    
    void setBookmark(final XmlBookmark p0);
    
    XmlBookmark getBookmark(final Object p0);
    
    void clearBookmark(final Object p0);
    
    void getAllBookmarkRefs(final Collection p0);
    
    boolean removeXml();
    
    boolean moveXml(final XmlCursor p0);
    
    boolean copyXml(final XmlCursor p0);
    
    boolean removeXmlContents();
    
    boolean moveXmlContents(final XmlCursor p0);
    
    boolean copyXmlContents(final XmlCursor p0);
    
    int removeChars(final int p0);
    
    int moveChars(final int p0, final XmlCursor p1);
    
    int copyChars(final int p0, final XmlCursor p1);
    
    void insertChars(final String p0);
    
    void insertElement(final QName p0);
    
    void insertElement(final String p0);
    
    void insertElement(final String p0, final String p1);
    
    void beginElement(final QName p0);
    
    void beginElement(final String p0);
    
    void beginElement(final String p0, final String p1);
    
    void insertElementWithText(final QName p0, final String p1);
    
    void insertElementWithText(final String p0, final String p1);
    
    void insertElementWithText(final String p0, final String p1, final String p2);
    
    void insertAttribute(final String p0);
    
    void insertAttribute(final String p0, final String p1);
    
    void insertAttribute(final QName p0);
    
    void insertAttributeWithValue(final String p0, final String p1);
    
    void insertAttributeWithValue(final String p0, final String p1, final String p2);
    
    void insertAttributeWithValue(final QName p0, final String p1);
    
    void insertNamespace(final String p0, final String p1);
    
    void insertComment(final String p0);
    
    void insertProcInst(final String p0, final String p1);
    
    public static final class TokenType
    {
        public static final int INT_NONE = 0;
        public static final int INT_STARTDOC = 1;
        public static final int INT_ENDDOC = 2;
        public static final int INT_START = 3;
        public static final int INT_END = 4;
        public static final int INT_TEXT = 5;
        public static final int INT_ATTR = 6;
        public static final int INT_NAMESPACE = 7;
        public static final int INT_COMMENT = 8;
        public static final int INT_PROCINST = 9;
        public static final TokenType NONE;
        public static final TokenType STARTDOC;
        public static final TokenType ENDDOC;
        public static final TokenType START;
        public static final TokenType END;
        public static final TokenType TEXT;
        public static final TokenType ATTR;
        public static final TokenType NAMESPACE;
        public static final TokenType COMMENT;
        public static final TokenType PROCINST;
        private String _name;
        private int _value;
        
        @Override
        public String toString() {
            return this._name;
        }
        
        public int intValue() {
            return this._value;
        }
        
        public boolean isNone() {
            return this == TokenType.NONE;
        }
        
        public boolean isStartdoc() {
            return this == TokenType.STARTDOC;
        }
        
        public boolean isEnddoc() {
            return this == TokenType.ENDDOC;
        }
        
        public boolean isStart() {
            return this == TokenType.START;
        }
        
        public boolean isEnd() {
            return this == TokenType.END;
        }
        
        public boolean isText() {
            return this == TokenType.TEXT;
        }
        
        public boolean isAttr() {
            return this == TokenType.ATTR;
        }
        
        public boolean isNamespace() {
            return this == TokenType.NAMESPACE;
        }
        
        public boolean isComment() {
            return this == TokenType.COMMENT;
        }
        
        public boolean isProcinst() {
            return this == TokenType.PROCINST;
        }
        
        public boolean isContainer() {
            return this == TokenType.STARTDOC || this == TokenType.START;
        }
        
        public boolean isFinish() {
            return this == TokenType.ENDDOC || this == TokenType.END;
        }
        
        public boolean isAnyAttr() {
            return this == TokenType.NAMESPACE || this == TokenType.ATTR;
        }
        
        private TokenType(final String name, final int value) {
            this._name = name;
            this._value = value;
        }
        
        static {
            NONE = new TokenType("NONE", 0);
            STARTDOC = new TokenType("STARTDOC", 1);
            ENDDOC = new TokenType("ENDDOC", 2);
            START = new TokenType("START", 3);
            END = new TokenType("END", 4);
            TEXT = new TokenType("TEXT", 5);
            ATTR = new TokenType("ATTR", 6);
            NAMESPACE = new TokenType("NAMESPACE", 7);
            COMMENT = new TokenType("COMMENT", 8);
            PROCINST = new TokenType("PROCINST", 9);
        }
    }
    
    public abstract static class XmlBookmark
    {
        public XmlMark _currentMark;
        public final Reference _ref;
        
        public XmlBookmark() {
            this(false);
        }
        
        public XmlBookmark(final boolean weak) {
            this._ref = (weak ? new WeakReference(this) : null);
        }
        
        public final XmlCursor createCursor() {
            return (this._currentMark == null) ? null : this._currentMark.createCursor();
        }
        
        public final XmlCursor toBookmark(final XmlCursor c) {
            return (c == null || !c.toBookmark(this)) ? this.createCursor() : c;
        }
        
        public Object getKey() {
            return this.getClass();
        }
    }
    
    public interface ChangeStamp
    {
        boolean hasChanged();
    }
    
    public interface XmlMark
    {
        XmlCursor createCursor();
    }
}
