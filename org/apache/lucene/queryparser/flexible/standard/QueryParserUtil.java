package org.apache.lucene.queryparser.flexible.standard;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;

public final class QueryParserUtil
{
    public static Query parse(final String[] queries, final String[] fields, final Analyzer analyzer) throws QueryNodeException {
        if (queries.length != fields.length) {
            throw new IllegalArgumentException("queries.length != fields.length");
        }
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        final StandardQueryParser qp = new StandardQueryParser();
        qp.setAnalyzer(analyzer);
        for (int i = 0; i < fields.length; ++i) {
            final Query q = qp.parse(queries[i], fields[i]);
            if (q != null) {
                bQuery.add(q, BooleanClause.Occur.SHOULD);
            }
        }
        return (Query)bQuery.build();
    }
    
    public static Query parse(final String query, final String[] fields, final BooleanClause.Occur[] flags, final Analyzer analyzer) throws QueryNodeException {
        if (fields.length != flags.length) {
            throw new IllegalArgumentException("fields.length != flags.length");
        }
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        final StandardQueryParser qp = new StandardQueryParser();
        qp.setAnalyzer(analyzer);
        for (int i = 0; i < fields.length; ++i) {
            final Query q = qp.parse(query, fields[i]);
            if (q != null) {
                bQuery.add(q, flags[i]);
            }
        }
        return (Query)bQuery.build();
    }
    
    public static Query parse(final String[] queries, final String[] fields, final BooleanClause.Occur[] flags, final Analyzer analyzer) throws QueryNodeException {
        if (queries.length != fields.length || queries.length != flags.length) {
            throw new IllegalArgumentException("queries, fields, and flags array have have different length");
        }
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        final StandardQueryParser qp = new StandardQueryParser();
        qp.setAnalyzer(analyzer);
        for (int i = 0; i < fields.length; ++i) {
            final Query q = qp.parse(queries[i], fields[i]);
            if (q != null) {
                bQuery.add(q, flags[i]);
            }
        }
        return (Query)bQuery.build();
    }
    
    public static String escape(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
