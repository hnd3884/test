package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class PathQueryNode extends QueryNodeImpl
{
    private List<QueryText> values;
    
    public PathQueryNode(final List<QueryText> pathElements) {
        this.values = null;
        this.values = pathElements;
        if (pathElements.size() <= 1) {
            throw new RuntimeException("PathQuerynode requires more 2 or more path elements.");
        }
    }
    
    public List<QueryText> getPathElements() {
        return this.values;
    }
    
    public void setPathElements(final List<QueryText> elements) {
        this.values = elements;
    }
    
    public QueryText getPathElement(final int index) {
        return this.values.get(index);
    }
    
    public CharSequence getFirstPathElement() {
        return this.values.get(0).value;
    }
    
    public List<QueryText> getPathElements(final int startIndex) {
        final List<QueryText> rValues = new ArrayList<QueryText>();
        for (int i = startIndex; i < this.values.size(); ++i) {
            try {
                rValues.add(this.values.get(i).clone());
            }
            catch (final CloneNotSupportedException ex) {}
        }
        return rValues;
    }
    
    private CharSequence getPathString() {
        final StringBuilder path = new StringBuilder();
        for (final QueryText pathelement : this.values) {
            path.append("/").append(pathelement.value);
        }
        return path.toString();
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        final StringBuilder path = new StringBuilder();
        path.append("/").append(this.getFirstPathElement());
        for (final QueryText pathelement : this.getPathElements(1)) {
            final CharSequence value = escaper.escape(pathelement.value, Locale.getDefault(), EscapeQuerySyntax.Type.STRING);
            path.append("/\"").append(value).append("\"");
        }
        return path.toString();
    }
    
    @Override
    public String toString() {
        final QueryText text = this.values.get(0);
        return "<path start='" + text.begin + "' end='" + text.end + "' path='" + (Object)this.getPathString() + "'/>";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final PathQueryNode clone = (PathQueryNode)super.cloneTree();
        if (this.values != null) {
            final List<QueryText> localValues = new ArrayList<QueryText>();
            for (final QueryText value : this.values) {
                localValues.add(value.clone());
            }
            clone.values = localValues;
        }
        return clone;
    }
    
    public static class QueryText implements Cloneable
    {
        CharSequence value;
        int begin;
        int end;
        
        public QueryText(final CharSequence value, final int begin, final int end) {
            this.value = null;
            this.value = value;
            this.begin = begin;
            this.end = end;
        }
        
        public QueryText clone() throws CloneNotSupportedException {
            final QueryText clone = (QueryText)super.clone();
            clone.value = this.value;
            clone.begin = this.begin;
            clone.end = this.end;
            return clone;
        }
        
        public CharSequence getValue() {
            return this.value;
        }
        
        public int getBegin() {
            return this.begin;
        }
        
        public int getEnd() {
            return this.end;
        }
        
        @Override
        public String toString() {
            return (Object)this.value + ", " + this.begin + ", " + this.end;
        }
    }
}
