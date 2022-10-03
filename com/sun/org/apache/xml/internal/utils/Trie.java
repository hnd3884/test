package com.sun.org.apache.xml.internal.utils;

public class Trie
{
    public static final int ALPHA_SIZE = 128;
    Node m_Root;
    private char[] m_charBuffer;
    
    public Trie() {
        this.m_charBuffer = new char[0];
        this.m_Root = new Node();
    }
    
    public Object put(final String key, final Object value) {
        final int len = key.length();
        if (len > this.m_charBuffer.length) {
            this.m_charBuffer = new char[len];
        }
        Node node = this.m_Root;
        for (int i = 0; i < len; ++i) {
            final Node nextNode = node.m_nextChar[Character.toUpperCase(key.charAt(i))];
            if (nextNode == null) {
                while (i < len) {
                    final Node newNode = new Node();
                    node.m_nextChar[Character.toUpperCase(key.charAt(i))] = newNode;
                    node.m_nextChar[Character.toLowerCase(key.charAt(i))] = newNode;
                    node = newNode;
                    ++i;
                }
                break;
            }
            node = nextNode;
        }
        final Object ret = node.m_Value;
        node.m_Value = value;
        return ret;
    }
    
    public Object get(final String key) {
        final int len = key.length();
        if (this.m_charBuffer.length < len) {
            return null;
        }
        Node node = this.m_Root;
        switch (len) {
            case 0: {
                return null;
            }
            case 1: {
                final char ch = key.charAt(0);
                if (ch < '\u0080') {
                    node = node.m_nextChar[ch];
                    if (node != null) {
                        return node.m_Value;
                    }
                }
                return null;
            }
            default: {
                key.getChars(0, len, this.m_charBuffer, 0);
                for (int i = 0; i < len; ++i) {
                    final char ch2 = this.m_charBuffer[i];
                    if ('\u0080' <= ch2) {
                        return null;
                    }
                    node = node.m_nextChar[ch2];
                    if (node == null) {
                        return null;
                    }
                }
                return node.m_Value;
            }
        }
    }
    
    class Node
    {
        Node[] m_nextChar;
        Object m_Value;
        
        Node() {
            this.m_nextChar = new Node[128];
            this.m_Value = null;
        }
    }
}
