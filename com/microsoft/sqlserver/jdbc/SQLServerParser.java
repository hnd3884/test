package com.microsoft.sqlserver.jdbc;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.Iterator;
import org.antlr.v4.runtime.Token;
import java.util.List;

final class SQLServerParser
{
    private static final List<Integer> SELECT_DELIMITING_WORDS;
    private static final List<Integer> INSERT_DELIMITING_WORDS;
    private static final List<Integer> DELETE_DELIMITING_WORDS;
    private static final List<Integer> UPDATE_DELIMITING_WORDS;
    private static final List<Integer> FROM_DELIMITING_WORDS;
    private static final List<Integer> OPERATORS;
    
    static void parseQuery(final SQLServerTokenIterator iter, final SQLServerFMTQuery query) throws SQLServerException {
        Token t = null;
        while (iter.hasNext()) {
            t = iter.next();
            switch (t.getType()) {
                case 1: {
                    for (t = skipTop(iter); t.getType() != 78; t = iter.next()) {
                        if (t.getType() == 89) {
                            final String columnName = findColumnAroundParameter(iter);
                            query.getColumns().add(columnName);
                        }
                        if (t.getType() == 5) {
                            query.getTableTarget().add(getTableTargetChunk(iter, query.getAliases(), SQLServerParser.SELECT_DELIMITING_WORDS));
                            break;
                        }
                        if (!iter.hasNext()) {
                            break;
                        }
                    }
                    continue;
                }
                case 2: {
                    t = skipTop(iter);
                    if (t.getType() != 6) {
                        t = iter.previous();
                    }
                    query.getTableTarget().add(getTableTargetChunk(iter, query.getAliases(), SQLServerParser.INSERT_DELIMITING_WORDS));
                    if (iter.hasNext()) {
                        final List<String> tableValues = getValuesList(iter);
                        boolean valuesFound = false;
                        final int valuesMarker = iter.nextIndex();
                        while (!valuesFound && iter.hasNext()) {
                            t = iter.next();
                            if (t.getType() == 14) {
                                valuesFound = true;
                                do {
                                    query.getValuesList().add(getValuesList(iter));
                                } while (iter.hasNext() && iter.next().getType() == 77);
                                iter.previous();
                            }
                        }
                        if (!valuesFound) {
                            resetIteratorIndex(iter, valuesMarker);
                        }
                        if (query.getValuesList().isEmpty()) {
                            continue;
                        }
                        for (final List<String> ls : query.getValuesList()) {
                            if (tableValues.isEmpty()) {
                                query.getColumns().add("*");
                            }
                            for (int i = 0; i < ls.size(); ++i) {
                                if ("?".equalsIgnoreCase(ls.get(i))) {
                                    if (0 == tableValues.size()) {
                                        query.getColumns().add("?");
                                    }
                                    else if (i < tableValues.size()) {
                                        query.getColumns().add(tableValues.get(i));
                                    }
                                    else {
                                        SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidInsertValuesQuery"), null, false);
                                    }
                                }
                            }
                        }
                        continue;
                    }
                    continue;
                }
                case 3: {
                    t = skipTop(iter);
                    if (t.getType() != 5) {
                        t = iter.previous();
                    }
                    query.getTableTarget().add(getTableTargetChunk(iter, query.getAliases(), SQLServerParser.DELETE_DELIMITING_WORDS));
                    continue;
                }
                case 4: {
                    skipTop(iter);
                    t = iter.previous();
                    query.getTableTarget().add(getTableTargetChunk(iter, query.getAliases(), SQLServerParser.UPDATE_DELIMITING_WORDS));
                    continue;
                }
                case 5: {
                    query.getTableTarget().add(getTableTargetChunk(iter, query.getAliases(), SQLServerParser.FROM_DELIMITING_WORDS));
                    continue;
                }
                case 89: {
                    final int parameterIndex = iter.nextIndex();
                    final String columnName2 = findColumnAroundParameter(iter);
                    query.getColumns().add(columnName2);
                    resetIteratorIndex(iter, parameterIndex);
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
    }
    
    static void resetIteratorIndex(final SQLServerTokenIterator iter, final int index) {
        if (iter.nextIndex() < index) {
            while (iter.nextIndex() != index) {
                iter.next();
            }
        }
        else if (iter.nextIndex() > index) {
            while (iter.nextIndex() != index) {
                iter.previous();
            }
        }
    }
    
    private static String getRoundBracketChunk(final SQLServerTokenIterator iter) throws SQLServerException {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        final Stack<String> s = new Stack<String>();
        s.push("(");
        while (!s.empty() && iter.hasNext()) {
            final Token t = iter.next();
            if (t.getType() == 72) {
                sb.append(")");
                s.pop();
            }
            else if (t.getType() == 71) {
                sb.append("(");
                s.push("(");
            }
            else {
                sb.append(t.getText()).append(" ");
            }
        }
        return sb.toString();
    }
    
    private static String getRoundBracketChunkBefore(final SQLServerTokenIterator iter) {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        final Stack<String> s = new Stack<String>();
        s.push(")");
        while (!s.empty()) {
            final Token t = iter.previous();
            if (t.getType() == 72) {
                sb.append("(");
                s.push(")");
            }
            else if (t.getType() == 71) {
                sb.append(")");
                s.pop();
            }
            else {
                sb.append(t.getText()).append(" ");
            }
        }
        return sb.toString();
    }
    
    static String findColumnAroundParameter(final SQLServerTokenIterator iter) throws SQLServerException {
        final int index = iter.nextIndex();
        iter.previous();
        String value = findColumnBeforeParameter(iter);
        resetIteratorIndex(iter, index);
        if ("".equalsIgnoreCase(value)) {
            value = findColumnAfterParameter(iter);
            resetIteratorIndex(iter, index);
        }
        return value;
    }
    
    private static String findColumnAfterParameter(final SQLServerTokenIterator iter) throws SQLServerException {
        final StringBuilder sb = new StringBuilder();
        while (0 == sb.length() && iter.hasNext()) {
            Token t = iter.next();
            if (t.getType() == 33 && iter.hasNext()) {
                t = iter.next();
            }
            if (!SQLServerParser.OPERATORS.contains(t.getType()) || !iter.hasNext()) {
                return "";
            }
            t = iter.next();
            if (t.getType() == 89) {
                continue;
            }
            if (t.getType() == 71) {
                sb.append(getRoundBracketChunk(iter));
            }
            else {
                sb.append(t.getText());
            }
            for (int i = 0; i < 3 && iter.hasNext(); ++i) {
                t = iter.next();
                if (t.getType() == 66) {
                    sb.append(".");
                    if (iter.hasNext()) {
                        t = iter.next();
                        sb.append(t.getText());
                    }
                }
            }
        }
        return sb.toString();
    }
    
    private static String findColumnBeforeParameter(final SQLServerTokenIterator iter) {
        final StringBuilder sb = new StringBuilder();
        while (0 == sb.length() && iter.hasPrevious()) {
            Token t = iter.previous();
            if (t.getType() == 70 && iter.hasPrevious()) {
                t = iter.previous();
            }
            if (t.getType() == 35 && iter.hasPrevious()) {
                t = iter.previous();
                if (iter.hasPrevious()) {
                    t = iter.previous();
                    if (t.getType() == 34 && iter.hasNext()) {
                        iter.next();
                        continue;
                    }
                    return "";
                }
            }
            if (!SQLServerParser.OPERATORS.contains(t.getType()) || !iter.hasPrevious()) {
                return "";
            }
            t = iter.previous();
            if (t.getType() == 33) {
                t = iter.previous();
            }
            if (t.getType() == 89) {
                continue;
            }
            final Deque<String> d = new ArrayDeque<String>();
            if (t.getType() == 72) {
                d.push(getRoundBracketChunkBefore(iter));
            }
            else {
                d.push(t.getText());
            }
            for (int i = 0; i < 3 && iter.hasPrevious(); ++i) {
                t = iter.previous();
                if (t.getType() == 66) {
                    d.push(".");
                    if (iter.hasPrevious()) {
                        t = iter.previous();
                        d.push(t.getText());
                    }
                }
            }
            final Stream<Object> stream = d.stream();
            final StringBuilder sb2 = sb;
            Objects.requireNonNull(sb2);
            stream.forEach((Consumer<? super Object>)sb2::append);
        }
        return sb.toString();
    }
    
    static List<String> getValuesList(final SQLServerTokenIterator iter) throws SQLServerException {
        Token t = iter.next();
        if (t.getType() == 71) {
            final ArrayList<String> parameterColumns = new ArrayList<String>();
            final Deque<Integer> d = new ArrayDeque<Integer>();
            StringBuilder sb = new StringBuilder();
            do {
                switch (t.getType()) {
                    case 71: {
                        if (!d.isEmpty()) {
                            sb.append('(');
                        }
                        d.push(71);
                        break;
                    }
                    case 72: {
                        if (d.peek() == 71) {
                            d.pop();
                        }
                        if (!d.isEmpty()) {
                            sb.append(')');
                            break;
                        }
                        parameterColumns.add(sb.toString().trim());
                        break;
                    }
                    case 77: {
                        if (d.size() == 1) {
                            parameterColumns.add(sb.toString().trim());
                            sb = new StringBuilder();
                            break;
                        }
                        sb.append(',');
                        break;
                    }
                    default: {
                        sb.append(t.getText());
                        break;
                    }
                }
                if (iter.hasNext() && !d.isEmpty()) {
                    t = iter.next();
                }
                else {
                    if (iter.hasNext() || d.isEmpty()) {
                        continue;
                    }
                    SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidValuesList"), null, false);
                }
            } while (!d.isEmpty());
            return parameterColumns;
        }
        iter.previous();
        return new ArrayList<String>();
    }
    
    static Token skipTop(final SQLServerTokenIterator iter) throws SQLServerException {
        if (!iter.hasNext()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidUserSQL"), null, false);
        }
        Token t = iter.next();
        if (t.getType() == 26) {
            t = iter.next();
            if (t.getType() == 71) {
                getRoundBracketChunk(iter);
            }
            t = iter.next();
            if (t.getType() == 28) {
                t = iter.next();
            }
            if (t.getType() == 17) {
                t = iter.next();
                if (t.getType() == 29) {
                    t = iter.next();
                }
                else {
                    t = iter.previous();
                }
            }
        }
        return t;
    }
    
    static String getCTE(final SQLServerTokenIterator iter) throws SQLServerException {
        if (iter.hasNext()) {
            final Token t = iter.next();
            if (t.getType() == 17) {
                final StringBuilder sb = new StringBuilder("WITH ");
                getCTESegment(iter, sb);
                return sb.toString();
            }
            iter.previous();
        }
        return "";
    }
    
    static void getCTESegment(final SQLServerTokenIterator iter, final StringBuilder sb) throws SQLServerException {
        try {
            sb.append(getTableTargetChunk(iter, null, Arrays.asList(18)));
            iter.next();
            Token t = iter.next();
            sb.append(" AS ");
            if (t.getType() != 71) {
                SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidCTEFormat"), null, false);
            }
            int leftRoundBracketCount = 0;
            do {
                sb.append(t.getText()).append(' ');
                if (t.getType() == 71) {
                    ++leftRoundBracketCount;
                }
                else if (t.getType() == 72) {
                    --leftRoundBracketCount;
                }
                t = iter.next();
            } while (leftRoundBracketCount > 0);
            if (t.getType() == 77) {
                sb.append(", ");
                getCTESegment(iter, sb);
            }
            else {
                iter.previous();
            }
        }
        catch (final NoSuchElementException e) {
            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidCTEFormat"), null, false);
        }
    }
    
    private static String getTableTargetChunk(final SQLServerTokenIterator iter, final List<String> possibleAliases, final List<Integer> delimiters) throws SQLServerException {
        final StringBuilder sb = new StringBuilder();
        if (iter.hasNext()) {
            Token t = iter.next();
            do {
                switch (t.getType()) {
                    case 71: {
                        sb.append(getRoundBracketChunk(iter));
                        break;
                    }
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25: {
                        sb.append(t.getText());
                        t = iter.next();
                        if (t.getType() != 71) {
                            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidOpenqueryCall"), null, false);
                        }
                        sb.append(getRoundBracketChunk(iter));
                        break;
                    }
                    case 18: {
                        sb.append(t.getText());
                        if (iter.hasNext()) {
                            final String s = iter.next().getText();
                            if (possibleAliases != null) {
                                possibleAliases.add(s);
                            }
                            else {
                                SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidCTEFormat"), null, false);
                            }
                            sb.append(" ").append(s);
                            break;
                        }
                        break;
                    }
                    default: {
                        sb.append(t.getText());
                        break;
                    }
                }
                if (!iter.hasNext()) {
                    break;
                }
                sb.append(' ');
                t = iter.next();
            } while (!delimiters.contains(t.getType()) && t.getType() != 78);
            if (iter.hasNext()) {
                iter.previous();
            }
        }
        return sb.toString().trim();
    }
    
    static {
        SELECT_DELIMITING_WORDS = Arrays.asList(8, 10, 9, 11, 12);
        INSERT_DELIMITING_WORDS = Arrays.asList(14, 15, 71, 1, 7, 17, 19);
        DELETE_DELIMITING_WORDS = Arrays.asList(12, 8, 15, 5);
        UPDATE_DELIMITING_WORDS = Arrays.asList(20, 15, 8, 12);
        FROM_DELIMITING_WORDS = Arrays.asList(8, 10, 9, 11, 12, 35);
        OPERATORS = Arrays.asList(50, 51, 52, 53, 54, 55, 57, 58, 59, 60, 61, 62, 63, 64, 80, 81, 82, 83, 84, 30, 31, 34);
    }
}
