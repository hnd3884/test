package org.apache.lucene.queryparser.surround.parser;

public interface QueryParserConstants
{
    public static final int EOF = 0;
    public static final int _NUM_CHAR = 1;
    public static final int _TERM_CHAR = 2;
    public static final int _WHITESPACE = 3;
    public static final int _STAR = 4;
    public static final int _ONE_CHAR = 5;
    public static final int _DISTOP_NUM = 6;
    public static final int OR = 8;
    public static final int AND = 9;
    public static final int NOT = 10;
    public static final int W = 11;
    public static final int N = 12;
    public static final int LPAREN = 13;
    public static final int RPAREN = 14;
    public static final int COMMA = 15;
    public static final int COLON = 16;
    public static final int CARAT = 17;
    public static final int TRUNCQUOTED = 18;
    public static final int QUOTED = 19;
    public static final int SUFFIXTERM = 20;
    public static final int TRUNCTERM = 21;
    public static final int TERM = 22;
    public static final int NUMBER = 23;
    public static final int Boost = 0;
    public static final int DEFAULT = 1;
    public static final String[] tokenImage = { "<EOF>", "<_NUM_CHAR>", "<_TERM_CHAR>", "<_WHITESPACE>", "\"*\"", "\"?\"", "<_DISTOP_NUM>", "<token of kind 7>", "<OR>", "<AND>", "<NOT>", "<W>", "<N>", "\"(\"", "\")\"", "\",\"", "\":\"", "\"^\"", "<TRUNCQUOTED>", "<QUOTED>", "<SUFFIXTERM>", "<TRUNCTERM>", "<TERM>", "<NUMBER>" };
}
