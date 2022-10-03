package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.Date;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import java.util.Calendar;
import java.text.DateFormat;
import org.apache.lucene.document.DateTools;
import java.util.TimeZone;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class TermRangeQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof TermRangeQueryNode) {
            final TermRangeQueryNode termRangeNode = (TermRangeQueryNode)node;
            final FieldQueryNode upper = termRangeNode.getUpperBound();
            final FieldQueryNode lower = termRangeNode.getLowerBound();
            DateTools.Resolution dateRes = null;
            boolean inclusive = false;
            Locale locale = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOCALE);
            if (locale == null) {
                locale = Locale.getDefault();
            }
            TimeZone timeZone = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.TIMEZONE);
            if (timeZone == null) {
                timeZone = TimeZone.getDefault();
            }
            final CharSequence field = termRangeNode.getField();
            String fieldStr = null;
            if (field != null) {
                fieldStr = field.toString();
            }
            final FieldConfig fieldConfig = this.getQueryConfigHandler().getFieldConfig(fieldStr);
            if (fieldConfig != null) {
                dateRes = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION);
            }
            if (termRangeNode.isUpperInclusive()) {
                inclusive = true;
            }
            String part1 = lower.getTextAsString();
            String part2 = upper.getTextAsString();
            try {
                final DateFormat df = DateFormat.getDateInstance(3, locale);
                df.setLenient(true);
                if (part1.length() > 0) {
                    final Date d1 = df.parse(part1);
                    part1 = DateTools.dateToString(d1, dateRes);
                    lower.setText(part1);
                }
                if (part2.length() > 0) {
                    Date d2 = df.parse(part2);
                    if (inclusive) {
                        final Calendar cal = Calendar.getInstance(timeZone, locale);
                        cal.setTime(d2);
                        cal.set(11, 23);
                        cal.set(12, 59);
                        cal.set(13, 59);
                        cal.set(14, 999);
                        d2 = cal.getTime();
                    }
                    part2 = DateTools.dateToString(d2, dateRes);
                    upper.setText(part2);
                }
            }
            catch (final Exception ex) {}
        }
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
