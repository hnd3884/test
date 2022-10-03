package org.apache.lucene.facet.taxonomy.writercache;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

class CharBlockArray implements Appendable, Serializable, CharSequence
{
    private static final long serialVersionUID = 1L;
    private static final int DefaultBlockSize = 32768;
    List<Block> blocks;
    Block current;
    int blockSize;
    int length;
    
    CharBlockArray() {
        this(32768);
    }
    
    CharBlockArray(final int blockSize) {
        this.blocks = new ArrayList<Block>();
        this.blockSize = blockSize;
        this.addBlock();
    }
    
    private void addBlock() {
        this.current = new Block(this.blockSize);
        this.blocks.add(this.current);
    }
    
    int blockIndex(final int index) {
        return index / this.blockSize;
    }
    
    int indexInBlock(final int index) {
        return index % this.blockSize;
    }
    
    @Override
    public CharBlockArray append(final CharSequence chars) {
        return this.append(chars, 0, chars.length());
    }
    
    @Override
    public CharBlockArray append(final char c) {
        if (this.current.length == this.blockSize) {
            this.addBlock();
        }
        this.current.chars[this.current.length++] = c;
        ++this.length;
        return this;
    }
    
    @Override
    public CharBlockArray append(final CharSequence chars, final int start, final int length) {
        for (int end = start + length, i = start; i < end; ++i) {
            this.append(chars.charAt(i));
        }
        return this;
    }
    
    public CharBlockArray append(final char[] chars, final int start, final int length) {
        int offset = start;
        int toCopy;
        Block current;
        for (int remain = length; remain > 0; remain -= toCopy, current = this.current, current.length += toCopy) {
            if (this.current.length == this.blockSize) {
                this.addBlock();
            }
            toCopy = remain;
            final int remainingInBlock = this.blockSize - this.current.length;
            if (remainingInBlock < toCopy) {
                toCopy = remainingInBlock;
            }
            System.arraycopy(chars, offset, this.current.chars, this.current.length, toCopy);
            offset += toCopy;
        }
        this.length += length;
        return this;
    }
    
    public CharBlockArray append(final String s) {
        int remain = s.length();
        int offset = 0;
        while (remain > 0) {
            if (this.current.length == this.blockSize) {
                this.addBlock();
            }
            int toCopy = remain;
            final int remainingInBlock = this.blockSize - this.current.length;
            if (remainingInBlock < toCopy) {
                toCopy = remainingInBlock;
            }
            s.getChars(offset, offset + toCopy, this.current.chars, this.current.length);
            offset += toCopy;
            remain -= toCopy;
            final Block current = this.current;
            current.length += toCopy;
        }
        this.length += s.length();
        return this;
    }
    
    @Override
    public char charAt(final int index) {
        final Block b = this.blocks.get(this.blockIndex(index));
        return b.chars[this.indexInBlock(index)];
    }
    
    @Override
    public int length() {
        return this.length;
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        int remaining = end - start;
        final StringBuilder sb = new StringBuilder(remaining);
        int blockIdx = this.blockIndex(start);
        int indexInBlock = this.indexInBlock(start);
        while (remaining > 0) {
            final Block b = this.blocks.get(blockIdx++);
            final int numToAppend = Math.min(remaining, b.length - indexInBlock);
            sb.append(b.chars, indexInBlock, numToAppend);
            remaining -= numToAppend;
            indexInBlock = 0;
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Block b : this.blocks) {
            sb.append(b.chars, 0, b.length);
        }
        return sb.toString();
    }
    
    void flush(final OutputStream out) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(out);
            oos.writeObject(this);
            oos.flush();
        }
        finally {
            if (oos != null) {
                oos.close();
            }
        }
    }
    
    public static CharBlockArray open(final InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(in);
            final CharBlockArray a = (CharBlockArray)ois.readObject();
            return a;
        }
        finally {
            if (ois != null) {
                ois.close();
            }
        }
    }
    
    static final class Block implements Serializable, Cloneable
    {
        private static final long serialVersionUID = 1L;
        final char[] chars;
        int length;
        
        Block(final int size) {
            this.chars = new char[size];
            this.length = 0;
        }
    }
}
