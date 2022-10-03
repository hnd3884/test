package com.lowagie.text.pdf.events;

import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import com.lowagie.text.Chunk;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.lowagie.text.pdf.PdfPageEventHelper;

public class IndexEvents extends PdfPageEventHelper
{
    private Map indextag;
    private long indexcounter;
    private List indexentry;
    private Comparator comparator;
    
    public IndexEvents() {
        this.indextag = new TreeMap();
        this.indexcounter = 0L;
        this.indexentry = new ArrayList();
        this.comparator = new Comparator() {
            @Override
            public int compare(final Object arg0, final Object arg1) {
                final Entry en1 = (Entry)arg0;
                final Entry en2 = (Entry)arg1;
                int rt = 0;
                if (en1.getIn1() != null && en2.getIn1() != null && (rt = en1.getIn1().compareToIgnoreCase(en2.getIn1())) == 0 && en1.getIn2() != null && en2.getIn2() != null && (rt = en1.getIn2().compareToIgnoreCase(en2.getIn2())) == 0 && en1.getIn3() != null && en2.getIn3() != null) {
                    rt = en1.getIn3().compareToIgnoreCase(en2.getIn3());
                }
                return rt;
            }
        };
    }
    
    @Override
    public void onGenericTag(final PdfWriter writer, final Document document, final Rectangle rect, final String text) {
        this.indextag.put(text, new Integer(writer.getPageNumber()));
    }
    
    public Chunk create(final String text, final String in1, final String in2, final String in3) {
        final Chunk chunk = new Chunk(text);
        final String tag = "idx_" + this.indexcounter++;
        chunk.setGenericTag(tag);
        chunk.setLocalDestination(tag);
        final Entry entry = new Entry(in1, in2, in3, tag);
        this.indexentry.add(entry);
        return chunk;
    }
    
    public Chunk create(final String text, final String in1) {
        return this.create(text, in1, "", "");
    }
    
    public Chunk create(final String text, final String in1, final String in2) {
        return this.create(text, in1, in2, "");
    }
    
    public void create(final Chunk text, final String in1, final String in2, final String in3) {
        final String tag = "idx_" + this.indexcounter++;
        text.setGenericTag(tag);
        text.setLocalDestination(tag);
        final Entry entry = new Entry(in1, in2, in3, tag);
        this.indexentry.add(entry);
    }
    
    public void create(final Chunk text, final String in1) {
        this.create(text, in1, "", "");
    }
    
    public void create(final Chunk text, final String in1, final String in2) {
        this.create(text, in1, in2, "");
    }
    
    public void setComparator(final Comparator aComparator) {
        this.comparator = aComparator;
    }
    
    public List getSortedEntries() {
        final Map grouped = new HashMap();
        for (int i = 0; i < this.indexentry.size(); ++i) {
            final Entry e = this.indexentry.get(i);
            final String key = e.getKey();
            final Entry master = grouped.get(key);
            if (master != null) {
                master.addPageNumberAndTag(e.getPageNumber(), e.getTag());
            }
            else {
                e.addPageNumberAndTag(e.getPageNumber(), e.getTag());
                grouped.put(key, e);
            }
        }
        final List sorted = new ArrayList(grouped.values());
        Collections.sort((List<Object>)sorted, this.comparator);
        return sorted;
    }
    
    public class Entry
    {
        private String in1;
        private String in2;
        private String in3;
        private String tag;
        private List pagenumbers;
        private List tags;
        
        public Entry(final String aIn1, final String aIn2, final String aIn3, final String aTag) {
            this.pagenumbers = new ArrayList();
            this.tags = new ArrayList();
            this.in1 = aIn1;
            this.in2 = aIn2;
            this.in3 = aIn3;
            this.tag = aTag;
        }
        
        public String getIn1() {
            return this.in1;
        }
        
        public String getIn2() {
            return this.in2;
        }
        
        public String getIn3() {
            return this.in3;
        }
        
        public String getTag() {
            return this.tag;
        }
        
        public int getPageNumber() {
            int rt = -1;
            final Integer i = IndexEvents.this.indextag.get(this.tag);
            if (i != null) {
                rt = i;
            }
            return rt;
        }
        
        public void addPageNumberAndTag(final int number, final String tag) {
            this.pagenumbers.add(new Integer(number));
            this.tags.add(tag);
        }
        
        public String getKey() {
            return this.in1 + "!" + this.in2 + "!" + this.in3;
        }
        
        public List getPagenumbers() {
            return this.pagenumbers;
        }
        
        public List getTags() {
            return this.tags;
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append(this.in1).append(' ');
            buf.append(this.in2).append(' ');
            buf.append(this.in3).append(' ');
            for (int i = 0; i < this.pagenumbers.size(); ++i) {
                buf.append(this.pagenumbers.get(i)).append(' ');
            }
            return buf.toString();
        }
    }
}
