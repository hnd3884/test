package com.adventnet.customview.service.i18n;

import java.util.MissingResourceException;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.AllowedValues;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.TreeSet;
import com.adventnet.ds.query.SortColumn;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.customview.CustomViewException;
import com.adventnet.ds.query.Criteria;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;

public class I18nHandler
{
    private SelectQuery select;
    private I18nServiceConfiguration i18nService;
    private Hashtable sortHash;
    private Hashtable allowedHash;
    private Hashtable colVsLocaleHash;
    private ResourceBundle resBundle;
    private Locale locale;
    private Comparator comparator;
    
    public I18nHandler(final I18nServiceConfiguration i18nService) {
        this.select = null;
        this.i18nService = null;
        this.sortHash = new Hashtable();
        this.allowedHash = new Hashtable();
        this.colVsLocaleHash = new Hashtable();
        this.i18nService = i18nService;
        this.resBundle = i18nService.getResourceBundle();
        this.locale = this.resBundle.getLocale();
        this.comparator = Collator.getInstance(this.locale);
    }
    
    public void convertToDSCriteria(final SelectQuery select) throws CustomViewException {
        this.select = select;
        final Criteria original = select.getCriteria();
        if (original == null) {
            return;
        }
        final Criteria modified = this.processCriteria(original);
        select.setCriteria(modified);
    }
    
    private Criteria processCriteria(final Criteria criteria) throws CustomViewException {
        Criteria leftCriteria = criteria.getLeftCriteria();
        Criteria rightCriteria = criteria.getRightCriteria();
        final String operator = criteria.getOperator();
        if (leftCriteria != null || rightCriteria != null) {
            if (leftCriteria != null) {
                leftCriteria = this.processCriteria(leftCriteria);
            }
            if (rightCriteria != null) {
                rightCriteria = this.processCriteria(rightCriteria);
            }
            if (leftCriteria != null && rightCriteria != null) {
                if (operator == " AND ") {
                    return leftCriteria.and(rightCriteria);
                }
                if (operator == " OR ") {
                    return leftCriteria.or(rightCriteria);
                }
            }
            return criteria;
        }
        final Column column = criteria.getColumn();
        if (column.getColumnAlias() == null) {
            column.setColumnAlias(column.getColumnName());
        }
        if (this.isI18nColumn(column)) {
            return this.processSingleCriteria(criteria);
        }
        return criteria;
    }
    
    private Criteria processSingleCriteria(final Criteria criteria) throws CustomViewException {
        final Column col = criteria.getColumn();
        int comparator = criteria.getComparator();
        final Object value = criteria.getValue();
        String[] mappedValues = null;
        Criteria modCriteria = null;
        if (comparator == 8 || comparator == 9) {
            mappedValues = this.getMappedValues(col, (String[])value);
            modCriteria = new Criteria(col, (Object)mappedValues, comparator);
            return modCriteria;
        }
        String strVal = (String)value;
        final String[] obj = { strVal };
        mappedValues = this.getMappedValues(col, obj);
        if (strVal.indexOf("*") == -1 && strVal.indexOf("?") == -1) {
            if (mappedValues.length == 0) {
                strVal = "";
                modCriteria = new Criteria(col, (Object)strVal, comparator);
            }
            else {
                modCriteria = new Criteria(col, (Object)mappedValues[0], comparator);
            }
        }
        else {
            comparator = 8;
            modCriteria = new Criteria(col, (Object)mappedValues, comparator);
        }
        return modCriteria;
    }
    
    private String[] getMappedValues(final Column col, final String[] values) throws CustomViewException {
        final Properties mappedProps = this.getLocaleMappings(col);
        final List modVals = new ArrayList();
        final int valSize = values.length;
        String value = null;
        for (int i = 0; i < valSize; ++i) {
            value = values[i];
            value = value.replaceAll("[*]", ".*");
            value = value.replace('?', '.');
            final Enumeration enumer = mappedProps.propertyNames();
            while (enumer.hasMoreElements()) {
                final String key = enumer.nextElement();
                final String mappedValue = mappedProps.getProperty(key);
                if (Pattern.matches(value, mappedValue)) {
                    modVals.add(key);
                }
            }
        }
        final String[] a = new String[modVals.size()];
        return modVals.toArray(a);
    }
    
    public void setDSSortOrder(final SelectQuery select) throws CustomViewException {
        this.select = select;
        this.i18nService = this.i18nService;
        final List sortCols = select.getSortColumns();
        final int sortSize = sortCols.size();
        final List modifiedSortCols = new ArrayList();
        for (int i = 0; i < sortSize; ++i) {
            final SortColumn sortCol = sortCols.get(i);
            final Column col = sortCol.getColumn();
            if (this.isI18nColumn(col)) {
                final Properties mappings = this.getLocaleMappings(col);
                final TreeSet sortSet = new TreeSet();
                final Enumeration enumer = mappings.propertyNames();
                while (enumer.hasMoreElements()) {
                    final String key = enumer.nextElement();
                    final String value = mappings.getProperty(key);
                    final SortedData sortData = new SortedData(key, value, this.comparator);
                    sortSet.add(sortData);
                }
                Vector order = this.getDSSortOrder(sortSet, sortCol.isAscending());
                final String type = this.getColumnDefinition(col).getDataType();
                order = this.convertStringToActualType(this.getJavaSQLType(type), order);
                sortCol.setSortOrder(order);
                modifiedSortCols.add(sortCol);
            }
            else {
                modifiedSortCols.add(sortCol);
            }
        }
    }
    
    private int getJavaSQLType(final String dataType) {
        if (dataType.equals("INTEGER")) {
            return 4;
        }
        if (dataType.equals("BIGINT")) {
            return -5;
        }
        if (dataType.equals("CHAR")) {
            return 12;
        }
        if (dataType.equals("BOOLEAN")) {
            return 16;
        }
        if (dataType.equals("FLOAT")) {
            return 6;
        }
        if (dataType.equals("DOUBLE")) {
            return 8;
        }
        throw new IllegalArgumentException("Unknown data type:" + dataType);
    }
    
    public Map getSortOrders(final Column[] columns) throws CustomViewException {
        final Locale locale = null;
        final int len = columns.length;
        final Hashtable hash = new Hashtable();
        for (final Column col : columns) {
            if (this.isI18nColumn(col)) {
                final Properties mappings = this.getLocaleMappings(col);
                final TreeSet sortSet = new TreeSet();
                final Enumeration enumer = mappings.propertyNames();
                while (enumer.hasMoreElements()) {
                    final String key = enumer.nextElement();
                    final String value = mappings.getProperty(key);
                    final SortedData sortData = new SortedData(key, value, this.comparator);
                    sortSet.add(sortData);
                }
                final boolean ascending = true;
                Vector order = this.getDSSortOrder(sortSet, ascending);
                order = this.convertStringToActualType(col.getType(), order);
                hash.put(col, order);
            }
        }
        return hash;
    }
    
    private Vector convertStringToActualType(final int type, final Vector order) {
        if (order == null) {
            return order;
        }
        final Vector newOrder = new Vector();
        final int len = order.size();
        switch (type) {
            case 12: {
                return order;
            }
            case -5: {
                for (int i = 0; i < len; ++i) {
                    newOrder.add(Long.valueOf(order.get(i)));
                }
                return newOrder;
            }
            case 4: {
                for (int i = 0; i < len; ++i) {
                    newOrder.add(Integer.valueOf(order.get(i)));
                }
                return newOrder;
            }
            case 16: {
                for (int i = 0; i < len; ++i) {
                    newOrder.add(Boolean.valueOf(order.get(i)));
                }
                return newOrder;
            }
            case 6: {
                for (int i = 0; i < len; ++i) {
                    newOrder.add(Float.valueOf(order.get(i)));
                }
                return newOrder;
            }
            case 8: {
                for (int i = 0; i < len; ++i) {
                    newOrder.add(Double.valueOf(order.get(i)));
                }
                return newOrder;
            }
            default: {
                return order;
            }
        }
    }
    
    private Vector getDSSortOrder(final TreeSet sortSet, final boolean isAscending) {
        final int size = sortSet.size();
        final Vector sortVect = new Vector();
        final int i = 0;
        if (isAscending) {
            for (final SortedData sortData : sortSet) {
                sortVect.add(sortData.getKey());
            }
        }
        else {
            for (final SortedData sortData : sortSet) {
                sortVect.insertElementAt(sortData.getKey(), 0);
            }
        }
        return sortVect;
    }
    
    private boolean isI18nColumn(final Column column) throws CustomViewException {
        if (column == null) {
            return false;
        }
        final List allowedVals = this.getAllowedValues(column);
        return allowedVals != null && allowedVals.size() > 0;
    }
    
    private List getAllowedValues(final Column column) throws CustomViewException {
        AllowedValues allowedVals = null;
        if (this.allowedHash.get(column) != null) {
            return this.allowedHash.get(column);
        }
        final ColumnDefinition colDefn = this.getColumnDefinition(column);
        if (colDefn != null) {
            allowedVals = colDefn.getAllowedValues();
        }
        if (allowedVals != null) {
            final List allowedList = allowedVals.getValueList();
            this.allowedHash.put(column, allowedList);
            return allowedList;
        }
        return null;
    }
    
    private ColumnDefinition getColumnDefinition(final Column column) throws CustomViewException {
        final Table table = this.getTable(column);
        ColumnDefinition colDefn = null;
        try {
            final TableDefinition tabDefn = MetaDataUtil.getTableDefinitionByName(table.getTableName());
            if (tabDefn != null) {
                colDefn = tabDefn.getColumnDefinitionByName(column.getColumnName());
            }
        }
        catch (final MetaDataException mde) {
            throw new CustomViewException((Throwable)mde);
        }
        return colDefn;
    }
    
    private Properties getLocaleMappings(final Column column) throws CustomViewException {
        if (this.colVsLocaleHash.get(column) != null) {
            return this.colVsLocaleHash.get(column);
        }
        final Properties localeMappings = new Properties();
        final List allowedValues = this.getAllowedValues(column);
        if (column.getColumnAlias() == null) {
            column.setColumnAlias(column.getColumnName());
        }
        final String namespace = this.getTable(column).getTableName() + "_" + column.getColumnName() + "_";
        for (int size = allowedValues.size(), i = 0; i < size; ++i) {
            final String allowedVal = String.valueOf(allowedValues.get(i));
            try {
                localeMappings.setProperty(allowedVal, this.resBundle.getString(namespace + allowedVal));
            }
            catch (final MissingResourceException mre) {
                localeMappings.setProperty(allowedVal, allowedVal);
            }
        }
        this.colVsLocaleHash.put(column, localeMappings);
        return localeMappings;
    }
    
    private Table getTable(final Column column) {
        final String tabAlias = column.getTableAlias();
        final List tabList = this.select.getTableList();
        for (int size = tabList.size(), i = 0; i < size; ++i) {
            final Table table = tabList.get(i);
            if (table.getTableAlias().equals(tabAlias)) {
                return table;
            }
        }
        return null;
    }
    
    private class SortedData implements Comparable
    {
        Object key;
        String value;
        Comparator comparator;
        
        SortedData(final Object key, final String value, final Comparator comparator) {
            this.key = null;
            this.value = null;
            this.comparator = null;
            this.key = key;
            this.value = value;
            this.comparator = comparator;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public void setKey(final Object key) {
            this.key = key;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
        
        @Override
        public int compareTo(final Object comparedObj) {
            final SortedData sortData = (SortedData)comparedObj;
            return this.comparator.compare(this.value, sortData.value);
        }
    }
}
