package com.adventnet.ds.query;

import java.util.Iterator;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.zoho.conf.AppResources;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.Date;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Stack;
import com.adventnet.persistence.Row;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.Externalizable;

public class Criteria implements Externalizable, Cloneable
{
    private static final long serialVersionUID = -1975632235835568619L;
    private static final Logger OUT;
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    private String operator;
    private Criteria leftCriteria;
    private Criteria rightCriteria;
    private Criterion criterion;
    private boolean negated;
    private static boolean userDefinedCaseSensitiveFlag;
    private boolean treatNullAsValue;
    private boolean validate;
    int hashCode;
    
    public Criteria() {
        this.operator = null;
        this.leftCriteria = null;
        this.rightCriteria = null;
        this.criterion = null;
        this.negated = false;
        this.treatNullAsValue = false;
        this.validate = true;
        this.hashCode = -1;
    }
    
    public Criteria(final Column column, final Object value, final int comparator) {
        this(column, value, comparator, Criteria.userDefinedCaseSensitiveFlag);
    }
    
    public Criteria(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
        this.operator = null;
        this.leftCriteria = null;
        this.rightCriteria = null;
        this.criterion = null;
        this.negated = false;
        this.treatNullAsValue = false;
        this.validate = true;
        this.hashCode = -1;
        this.criterion = new Criterion(column, value, comparator, caseSensitive);
    }
    
    public void treatNullAsValue(final boolean nullAsValue) {
        this.treatNullAsValue = nullAsValue;
    }
    
    public boolean isNullTreatedAsValue() {
        return this.treatNullAsValue;
    }
    
    public void validateInput() {
        if (!this.validate) {
            return;
        }
        if (this.leftCriteria == null && this.rightCriteria == null) {
            this.criterion.validateInput();
        }
        else {
            this.leftCriteria.validateInput();
            this.rightCriteria.validateInput();
        }
        this.validate = false;
    }
    
    @Deprecated
    public boolean matches(final Properties properties) {
        this.validateInput();
        boolean result = false;
        if (this.criterion != null) {
            result = this.criterion.matches(properties);
        }
        else {
            final boolean leftResult = this.leftCriteria.matches(properties);
            final boolean rightResult = this.rightCriteria.matches(properties);
            if (" AND ".equalsIgnoreCase(this.operator)) {
                result = (leftResult && rightResult);
            }
            else {
                result = (leftResult || rightResult);
            }
        }
        if (this.negated) {
            result = !result;
        }
        return result;
    }
    
    public boolean matches(final Map map) {
        this.validateInput();
        boolean result = false;
        if (this.criterion != null) {
            result = this.criterion.matches(map, this.treatNullAsValue);
        }
        else {
            final boolean leftResult = this.leftCriteria.matches(map);
            final boolean rightResult = this.rightCriteria.matches(map);
            if (" AND ".equalsIgnoreCase(this.operator)) {
                result = (leftResult && rightResult);
            }
            else {
                result = (leftResult || rightResult);
            }
        }
        if (this.negated) {
            result = !result;
        }
        return result;
    }
    
    public boolean matches(final Row row) {
        this.validateInput();
        boolean result = false;
        if (this.criterion != null) {
            result = this.criterion.matches(row, this.treatNullAsValue);
        }
        else if (" AND ".equalsIgnoreCase(this.operator)) {
            result = (this.leftCriteria.matches(row) && this.rightCriteria.matches(row));
        }
        else {
            result = (this.leftCriteria.matches(row) || this.rightCriteria.matches(row));
        }
        if (this.negated) {
            result = !result;
        }
        return result;
    }
    
    public Criteria and(final Column column, final Object value, final int comparator) {
        return this.and(new Criteria(column, value, comparator, Criteria.userDefinedCaseSensitiveFlag));
    }
    
    public Criteria and(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
        return this.and(new Criteria(column, value, comparator, caseSensitive));
    }
    
    public Criteria and(final Criteria criteria) {
        if (null == criteria) {
            Criteria.OUT.fine("Passed criteria is null, hence returning left criteria alone!");
            return this;
        }
        final Criteria newCriteria = new Criteria();
        newCriteria.leftCriteria = this;
        newCriteria.rightCriteria = criteria;
        newCriteria.operator = " AND ";
        return newCriteria;
    }
    
    public Criteria or(final Column column, final Object value, final int comparator) {
        return this.or(new Criteria(column, value, comparator, Criteria.userDefinedCaseSensitiveFlag));
    }
    
    public Criteria or(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
        return this.or(new Criteria(column, value, comparator, caseSensitive));
    }
    
    public Criteria or(final Criteria criteria) {
        if (null == criteria) {
            Criteria.OUT.fine("Passed criteria is null, hence returning left criteria alone!");
            return this;
        }
        final Criteria newCriteria = new Criteria();
        newCriteria.leftCriteria = this;
        newCriteria.rightCriteria = criteria;
        newCriteria.operator = " OR ";
        return newCriteria;
    }
    
    public Criteria negate() {
        final Criteria modified = (Criteria)this.clone();
        modified.negated = !this.negated;
        return modified;
    }
    
    public boolean isNegate() {
        return this.negated;
    }
    
    public Criteria getLeftCriteria() {
        return this.leftCriteria;
    }
    
    public Criteria getRightCriteria() {
        return this.rightCriteria;
    }
    
    public Column getColumn() {
        if (this.criterion != null) {
            return this.criterion.column;
        }
        return null;
    }
    
    public Object getValue() {
        if (this.criterion != null) {
            return this.criterion.value;
        }
        return null;
    }
    
    public boolean containsTemplateValues() {
        if (this.criterion != null) {
            return this.criterion.value instanceof String && this.criterion.value.toString().indexOf("${") != -1;
        }
        boolean returnVal = this.leftCriteria.containsTemplateValues();
        if (returnVal) {
            return true;
        }
        returnVal = this.rightCriteria.containsTemplateValues();
        return returnVal;
    }
    
    public boolean isCaseSensitive() {
        boolean result = true;
        if (this.criterion != null) {
            result = this.criterion.caseSensitive;
        }
        return result;
    }
    
    public int getComparator() {
        if (this.criterion != null) {
            return this.criterion.comparator;
        }
        return -1;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    void setCriterion(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
        this.criterion = new Criterion(column, value, comparator, caseSensitive);
    }
    
    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff = this.getStringBuffer(buff);
        return buff.toString();
    }
    
    protected StringBuffer getStringBuffer(final StringBuffer buff) {
        if (this.negated) {
            buff.append(" NOT ");
        }
        buff.append("(");
        if (this.criterion != null) {
            buff.append(this.criterion.toString());
        }
        else if (this.leftCriteria != null) {
            this.leftCriteria.getStringBuffer(buff);
        }
        if (this.rightCriteria != null) {
            buff.append(this.operator);
            this.rightCriteria.getStringBuffer(buff);
        }
        buff.append(")");
        return buff;
    }
    
    public Object clone() {
        Criteria newCriteria = null;
        Criteria leftCriteria = null;
        Criteria rightCriteria = null;
        leftCriteria = this.getLeftCriteria();
        if (leftCriteria == null) {
            final Column column = this.getColumn();
            final Column newColumn = (Column)column.clone();
            if (null != this.getValue() && this.getValue() instanceof DerivedColumn) {
                final DerivedColumn dc = (DerivedColumn)((DerivedColumn)this.getValue()).clone();
                newCriteria = new Criteria(newColumn, dc, this.getComparator(), this.isCaseSensitive());
            }
            else {
                newCriteria = new Criteria(newColumn, this.getValue(), this.getComparator(), this.isCaseSensitive());
            }
            newCriteria.negated = this.negated;
            newCriteria.treatNullAsValue = this.treatNullAsValue;
            return newCriteria;
        }
        rightCriteria = this.getRightCriteria();
        newCriteria = (Criteria)leftCriteria.clone();
        if (rightCriteria != null) {
            final Criteria newRightCriteria = (Criteria)rightCriteria.clone();
            if (" AND ".equals(this.getOperator())) {
                newCriteria = newCriteria.and(newRightCriteria);
            }
            else {
                newCriteria = newCriteria.or(newRightCriteria);
            }
            newCriteria.negated = this.negated;
        }
        newCriteria.treatNullAsValue = this.treatNullAsValue;
        return newCriteria;
    }
    
    @Deprecated
    public Criteria(final String criteriaString) {
        this(criteriaString, true);
    }
    
    @Deprecated
    public Criteria(final String criteriaString, final boolean caseSensitive) {
        this.operator = null;
        this.leftCriteria = null;
        this.rightCriteria = null;
        this.criterion = null;
        this.negated = false;
        this.treatNullAsValue = false;
        this.validate = true;
        this.hashCode = -1;
        final Criteria criteria = getCriteria(criteriaString, caseSensitive);
        this.operator = criteria.getOperator();
        this.leftCriteria = criteria.getLeftCriteria();
        this.rightCriteria = criteria.getRightCriteria();
        this.criterion = criteria.criterion;
    }
    
    private static Criteria getCriteria(final String sqlString, final boolean caseSensitive) {
        final Stack stack = new Stack();
        final StringTokenizer tokenizer = new StringTokenizer(sqlString, "()", true);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken().trim();
            if (token.equals("(")) {
                stack.push("(");
            }
            else if (token.equals(")")) {
                matchLeftParanthesis(stack);
            }
            else if (token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR")) {
                stack.push(token);
            }
            else {
                if (token.equals("")) {
                    continue;
                }
                final StringBuffer stringFormed = new StringBuffer();
                stringFormed.append(token);
                int closeBracesNeeded = 1;
                while (closeBracesNeeded > 0) {
                    final String tk = tokenizer.nextToken();
                    if (tk.equals(")")) {
                        --closeBracesNeeded;
                    }
                    else if (tk.equals("(")) {
                        ++closeBracesNeeded;
                    }
                    else {
                        stringFormed.append(" ").append(tk);
                    }
                }
                final Criteria criteria = formRelationalCriteria(stringFormed.toString(), caseSensitive);
                stack.push(criteria);
                matchLeftParanthesis(stack);
            }
        }
        final Criteria retCriteria = stack.pop();
        return retCriteria;
    }
    
    private static Criteria formRelationalCriteria(final String token, final boolean caseSensitive) {
        final StringTokenizer tokenizer = new StringTokenizer(token);
        final int countTokens = tokenizer.countTokens();
        int currentToken = 0;
        final String columnName = tokenizer.nextToken();
        ++currentToken;
        String relationalOperator = tokenizer.nextToken();
        ++currentToken;
        if (relationalOperator.equals("NOT")) {
            relationalOperator = relationalOperator + " " + tokenizer.nextToken();
            ++currentToken;
        }
        String value = null;
        while (currentToken < countTokens) {
            if (value == null) {
                value = tokenizer.nextToken();
            }
            else {
                value = value + " " + tokenizer.nextToken();
            }
            ++currentToken;
        }
        String[] value2 = null;
        final int comparator = getComparator(relationalOperator.trim());
        if (comparator == 8 || comparator == 9) {
            final StringTokenizer valueTokenizer = new StringTokenizer(value, "',");
            final ArrayList list = new ArrayList();
            while (valueTokenizer.hasMoreTokens()) {
                final String valueToken = valueTokenizer.nextToken();
                list.add(valueToken);
            }
            value2 = new String[list.size()];
            value2 = list.toArray(value2);
        }
        else if (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
            value = value.substring(1, value.length() - 1);
        }
        Column column = null;
        final int index = columnName.indexOf(".");
        if (index != -1) {
            column = Column.getColumn(columnName.substring(0, index), columnName.substring(index + 1));
        }
        else {
            column = null;
        }
        Criteria returnCriteria = null;
        if (comparator == 8 || comparator == 9) {
            returnCriteria = new Criteria(column, value2, comparator, caseSensitive);
        }
        else {
            returnCriteria = new Criteria(column, value, comparator, caseSensitive);
        }
        return returnCriteria;
    }
    
    private static Criteria formLogicalCriteria(Criteria left, final Criteria right, final String operator) {
        if (left == null) {
            return right;
        }
        if (operator.equalsIgnoreCase("AND")) {
            left = left.and(right);
        }
        else if (operator.equalsIgnoreCase("OR")) {
            left = left.or(right);
        }
        else {
            Criteria.OUT.log(Level.WARNING, "Cannot handle this operator : {0}", operator);
        }
        return left;
    }
    
    private static void matchLeftParanthesis(final Stack stack) {
        Criteria left = null;
        String operator = null;
        Criteria right = null;
        for (Object obj = stack.pop(); !obj.equals("("); obj = stack.pop()) {
            if (obj instanceof Criteria) {
                if (right == null) {
                    right = (Criteria)obj;
                }
                else {
                    left = (Criteria)obj;
                }
            }
            else {
                operator = (String)obj;
            }
        }
        final Criteria cr = formLogicalCriteria(left, right, operator);
        stack.push(cr);
    }
    
    private static int getComparator(final String relationalOperator) {
        if (relationalOperator.equals("=")) {
            return 0;
        }
        if (relationalOperator.equals("!=")) {
            return 1;
        }
        if (relationalOperator.equals("LIKE")) {
            return 2;
        }
        if (relationalOperator.equals("NOT LIKE")) {
            return 3;
        }
        if (relationalOperator.equals(">=")) {
            return 4;
        }
        if (relationalOperator.equals(">")) {
            return 5;
        }
        if (relationalOperator.equals("<=")) {
            return 6;
        }
        if (relationalOperator.equals("<")) {
            return 7;
        }
        if (relationalOperator.equals("IN")) {
            return 8;
        }
        if (relationalOperator.equals("NOT IN")) {
            return 9;
        }
        return -1;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.hashCode(this.operator) + (this.negated ? 1 : 0) + this.hashCode(this.leftCriteria) + this.hashCode(this.rightCriteria) + this.hashCode(this.criterion);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        if (obj == null) {
            return 0;
        }
        if (!obj.getClass().isArray()) {
            return obj.hashCode();
        }
        if (obj instanceof int[]) {
            return this.hashCode((int[])obj);
        }
        if (obj instanceof long[]) {
            return this.hashCode((long[])obj);
        }
        if (obj instanceof String[]) {
            return this.hashCode((String[])obj);
        }
        if (obj instanceof Object[]) {
            return this.hashCode((Object[])obj);
        }
        return obj.hashCode();
    }
    
    private int hashCode(final int[] values) {
        final int length = values.length;
        int hashCode = 0;
        for (int i = 0; i < length; ++i) {
            hashCode += values[i];
        }
        return hashCode;
    }
    
    private int hashCode(final long[] values) {
        final int length = values.length;
        int hashCode = 0;
        for (int i = 0; i < length; ++i) {
            hashCode += (int)values[i];
        }
        return hashCode;
    }
    
    private int hashCode(final String[] values) {
        final int length = values.length;
        int hashCode = 0;
        for (int i = 0; i < length; ++i) {
            hashCode += this.hashCode(values[i]);
        }
        return hashCode;
    }
    
    private int hashCode(final Object[] values) {
        final int length = values.length;
        int hashCode = 0;
        for (int i = 0; i < length; ++i) {
            hashCode += this.hashCode(values[i]);
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof Criteria)) {
            return false;
        }
        final Criteria passedCriteria = (Criteria)object;
        boolean equals = false;
        equals = (this.negated == passedCriteria.negated);
        if (!equals) {
            return false;
        }
        equals = this.equals(this.operator, passedCriteria.operator);
        if (!equals) {
            return false;
        }
        equals = this.equals(this.criterion, passedCriteria.criterion);
        if (!equals) {
            return false;
        }
        equals = this.equals(this.leftCriteria, passedCriteria.leftCriteria);
        if (!equals) {
            return false;
        }
        equals = this.equals(this.rightCriteria, passedCriteria.rightCriteria);
        return equals;
    }
    
    private boolean equals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
    
    public void transformValue() {
        if (this.criterion == null) {
            return;
        }
        this.criterion.transformValue();
    }
    
    public void transformValueAsTypeSpecific() {
        if (this.criterion == null) {
            return;
        }
        this.criterion.transformValueAsTypeSpecific();
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        if (this.operator != null) {
            out.writeUTF(this.operator);
        }
        else {
            out.writeUTF("");
        }
        if (this.leftCriteria == null) {
            out.writeUTF("LTNULL");
        }
        else {
            out.writeUTF("LTNOTNULL");
            this.leftCriteria.writeExternal(out);
        }
        if (this.rightCriteria == null) {
            out.writeUTF("RTNULL");
        }
        else {
            out.writeUTF("RTNOTNULL");
            this.rightCriteria.writeExternal(out);
        }
        if (this.criterion == null) {
            out.writeUTF("CRITERION_NULL");
        }
        else {
            out.writeUTF("CRITERION_NOT_NULL");
            this.criterion.writeExternal(out);
        }
        out.writeBoolean(this.negated);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.operator = in.readUTF();
        if (this.operator.trim().length() == 0) {
            this.operator = null;
        }
        final String ltNull = in.readUTF();
        if (ltNull.equals("LTNULL")) {
            this.leftCriteria = null;
        }
        else {
            (this.leftCriteria = new Criteria()).readExternal(in);
        }
        final String rtNull = in.readUTF();
        if (rtNull.equals("RTNULL")) {
            this.rightCriteria = null;
        }
        else {
            (this.rightCriteria = new Criteria()).readExternal(in);
        }
        final String criterionNull = in.readUTF();
        if (criterionNull.equals("CRITERION_NULL")) {
            this.criterion = null;
        }
        else {
            (this.criterion = new Criterion()).readExternal(in);
        }
        this.negated = in.readBoolean();
    }
    
    private boolean isCriteriaTemplate(final String template) {
        return template.startsWith("${") && template.endsWith("}");
    }
    
    void setLeftCriteria(final Criteria leftCriteria) {
        this.leftCriteria = leftCriteria;
    }
    
    void setOperator(final String operator) {
        this.operator = operator;
    }
    
    void setRightCriteria(final Criteria rightCriteria) {
        this.rightCriteria = rightCriteria;
    }
    
    static {
        OUT = Logger.getLogger(Criteria.class.getName());
        Criteria.userDefinedCaseSensitiveFlag = (PersistenceInitializer.getConfigurationValue("CaseSensitiveInCriteria") == null || !PersistenceInitializer.getConfigurationValue("CaseSensitiveInCriteria").equalsIgnoreCase("false"));
    }
    
    private class Criterion implements Externalizable
    {
        private Column column;
        private Object value;
        private int comparator;
        private boolean caseSensitive;
        private boolean isPatternValidated;
        private boolean isInputValidated;
        private boolean isInputTransformedForMatches;
        int hashCode;
        private List<Pattern> convertedGlobToRegExList;
        private String convertedGlobToRegEx;
        private Pattern pattern;
        private Matcher matcher;
        
        Criterion() {
            this.caseSensitive = true;
            this.isPatternValidated = false;
            this.isInputValidated = false;
            this.isInputTransformedForMatches = false;
            this.hashCode = -1;
            this.convertedGlobToRegExList = null;
            this.convertedGlobToRegEx = "";
            this.pattern = null;
            this.matcher = null;
        }
        
        private boolean isStringArray(final Object[] arr) {
            for (final Object val : arr) {
                if (!(val instanceof String)) {
                    return false;
                }
            }
            return true;
        }
        
        Criterion(final Column column, final Object value, final int comparator, final boolean caseSensitive) {
            this.caseSensitive = true;
            this.isPatternValidated = false;
            this.isInputValidated = false;
            this.isInputTransformedForMatches = false;
            this.hashCode = -1;
            this.convertedGlobToRegExList = null;
            this.convertedGlobToRegEx = "";
            this.pattern = null;
            this.matcher = null;
            if (column == null) {
                throw new IllegalArgumentException("Column can not be null");
            }
            this.column = column;
            this.value = value;
            if ((value instanceof String[] || (value instanceof Object[] && this.isStringArray((Object[])value))) && !caseSensitive) {
                final Object[] givenValues = (Object[])this.value;
                final String[] newValues = new String[givenValues.length];
                for (int index = 0; index < givenValues.length; ++index) {
                    newValues[index] = ((givenValues[index] == null) ? null : ((String)givenValues[index]).toUpperCase(Locale.ENGLISH));
                }
            }
            this.comparator = comparator;
            this.caseSensitive = caseSensitive;
        }
        
        private void validateInput() {
            if (Criteria.this.isCriteriaTemplate(String.valueOf(this.value))) {
                return;
            }
            Column c = null;
            if (this.value instanceof Column) {
                c = (Column)this.value;
                if (c.getType() != this.column.getType()) {
                    throw new IllegalArgumentException("Both the Column :: " + this.column + " and valueColumn :: " + c + " should be of same type in the criteria :: " + this);
                }
                if (this.column.getType() == 2004 || c.getType() == 2004) {
                    throw new IllegalArgumentException("BLOB/SBLOB is not supported in Criteria for column :: " + this.column);
                }
                if (this.comparator == 14 || this.comparator == 15) {
                    throw new IllegalArgumentException("Comparator cannot be BETWEEN/NOT_BETWEEN when both comparison arguments are instance of Column");
                }
                this.isInputValidated = true;
            }
            else {
                if (this.column.getType() == 2004) {
                    throw new IllegalArgumentException("BLOB/SBLOB is not supported in Criteria for column :: " + this.column);
                }
                this.transformValue();
                if (this.column.getType() == 16 && !(this.value instanceof Boolean) && !(this.value instanceof boolean[]) && !(this.value instanceof Boolean[]) && !(this.value instanceof String[])) {
                    throw new IllegalArgumentException("Unknown value-type provided for a boolean column [" + this.column + "] ");
                }
                if (this.comparator == 8 || this.comparator == 9) {
                    if (this.value == null) {
                        throw new IllegalArgumentException("Null value cannot be set in the criteria when the comparator is IN/NOT_IN :: [" + this + "]");
                    }
                    if (this.column.getType() == 12) {
                        if (!(this.value instanceof Object[])) {
                            throw new IllegalArgumentException("The criteria value for a CHAR column with a IN/NOT_IN comparator should be of instance Object[]/String[], but here it is " + this.value.getClass().getName());
                        }
                        for (final Object obj : ((Object[])this.value).clone()) {
                            if (!(obj instanceof String)) {
                                throw new IllegalArgumentException("The criteria value for a CHAR column with a IN/NOT_IN comparator can be of instance Object[], but should contain only String values. But here it is " + ((null != obj) ? obj.getClass().getName() : null));
                            }
                        }
                        if (!this.caseSensitive) {
                            final Object[] oldValues = (Object[])this.value;
                            final String[] newValues = new String[oldValues.length];
                            for (int i = 0; i < oldValues.length; ++i) {
                                newValues[i] = ((String)oldValues[i]).toUpperCase(Locale.ENGLISH);
                            }
                            this.value = newValues;
                        }
                    }
                    if (this.value instanceof long[]) {
                        this.value = ((long[])this.value).clone();
                        Arrays.sort((long[])this.value);
                    }
                    else if (this.value instanceof int[]) {
                        this.value = ((int[])this.value).clone();
                        Arrays.sort((int[])this.value);
                    }
                    else if (this.value instanceof double[]) {
                        this.value = ((double[])this.value).clone();
                        Arrays.sort((double[])this.value);
                    }
                    else if (this.value instanceof float[]) {
                        this.value = ((float[])this.value).clone();
                        Arrays.sort((float[])this.value);
                    }
                    else if (this.value instanceof Integer[]) {
                        this.value = ((Integer[])this.value).clone();
                        Arrays.sort((Object[])this.value);
                    }
                    else if (this.value instanceof Long[]) {
                        this.value = ((Long[])this.value).clone();
                        Arrays.sort((Object[])this.value);
                    }
                    else if (this.value instanceof Double[]) {
                        this.value = ((Double[])this.value).clone();
                        Arrays.sort((Object[])this.value);
                    }
                    else if (this.value instanceof Float[]) {
                        this.value = ((Float[])this.value).clone();
                        Arrays.sort((Object[])this.value);
                    }
                    else if (this.value instanceof BigDecimal[]) {
                        this.value = ((BigDecimal[])this.value).clone();
                        Arrays.sort((Object[])this.value);
                    }
                }
                if (this.comparator == 14 || this.comparator == 15) {
                    if (this.value == null) {
                        throw new IllegalArgumentException("Null value cannot be set in the criteria when the comparator is BETWEEN/NOT_BETWEEN :: [" + this + "]");
                    }
                    if (!this.value.getClass().isArray()) {
                        throw new IllegalArgumentException("Respective Object[] should be set when the comparator is BETWEEN/NOT_BETWEEN");
                    }
                    if (Array.getLength(this.value) != 2) {
                        throw new IllegalArgumentException("The value array should contain only 2 inputs for the BETWEEN / NOT_BETWEEN comparators");
                    }
                    switch (this.column.getType()) {
                        case 4: {
                            if (this.value instanceof int[]) {
                                final int[] v = (int[])this.value;
                                final Integer[] j = { v[0], v[1] };
                                this.value = j;
                                break;
                            }
                            break;
                        }
                        case -5: {
                            if (this.value instanceof long[]) {
                                final long[] v2 = (long[])this.value;
                                final Long[] l = { v2[0], v2[1] };
                                this.value = l;
                                break;
                            }
                            break;
                        }
                        case 6: {
                            if (this.value instanceof float[]) {
                                final float[] v3 = (float[])this.value;
                                final Float[] f = { v3[0], v3[1] };
                                Criteria.OUT.log(Level.INFO, "0th value :: v :: {0}, f :: {1}", new Object[] { Arrays.toString(v3), Arrays.toString(f) });
                                Criteria.OUT.log(Level.INFO, "v :: " + v3[0] + ", " + v3[1] + ", f :: " + f[0] + ", " + f[1]);
                                this.value = f;
                                break;
                            }
                            break;
                        }
                        case 8: {
                            if (this.value instanceof double[]) {
                                final double[] v4 = (double[])this.value;
                                final Double[] d = { v4[0], v4[1] };
                                this.value = d;
                                break;
                            }
                            break;
                        }
                    }
                    this.value = ((Object[])this.value).clone();
                    Arrays.sort((Object[])this.value);
                }
                final boolean criteriaValueIsAnArray = this.value != null && this.value.getClass().isArray();
                final boolean isMultiArgumentComparator = 8 == this.comparator || 9 == this.comparator || 14 == this.comparator || 15 == this.comparator;
                final boolean isMultiAndSingleArgComparator = 2 == this.comparator || 3 == this.comparator;
                if (criteriaValueIsAnArray && !isMultiArgumentComparator && !isMultiAndSingleArgComparator) {
                    throw new IllegalArgumentException("Criteria " + this + ". Input array \" " + this.value.toString() + " \" received, which cannot be used with QueryConstants" + this.getString(this.comparator));
                }
                if (this.value != null && !Criteria.this.isCriteriaTemplate(String.valueOf(this.value)) && !criteriaValueIsAnArray && isMultiArgumentComparator && !isMultiAndSingleArgComparator) {
                    throw new IllegalArgumentException("Criteria " + this + ". Input \" " + this.value.toString() + " \" received, which cannot be used with QueryConstants" + this.getString(this.comparator));
                }
                switch (this.column.getType()) {
                    case -6:
                    case -5:
                    case 3:
                    case 4:
                    case 6:
                    case 8: {
                        if (this.value != null && !(this.value instanceof Number) && !(this.value instanceof Number[]) && !(this.value instanceof String) && !(this.value instanceof int[]) && !(this.value instanceof float[]) && !(this.value instanceof long[]) && !(this.value instanceof double[]) && !(this.value instanceof String[]) && !(this.value instanceof UniqueValueHolder) && !(this.value instanceof UniqueValueHolder[]) && !(this.value instanceof Object[])) {
                            throw new IllegalArgumentException("Invalid input \"" + this.value.getClass().getCanonicalName() + "\" received, which cannot be compared for NUMERIC(TINYINT/INTEGER/BIGINT/FLOAT/DOUBLE/DECIMAL) Column :: " + this.column);
                        }
                        break;
                    }
                    case 12: {
                        if (this.value != null && !(this.value instanceof String) && !(this.value instanceof Object[])) {
                            throw new IllegalArgumentException("Invalid input \"" + this.value.getClass().getCanonicalName() + "\" received, which cannot be compared for CHAR Column :: " + this.column);
                        }
                        break;
                    }
                    case 16: {
                        if (this.comparator == 12 || this.comparator == 13 || this.comparator == 10 || this.comparator == 11 || this.comparator == 14 || this.comparator == 15 || this.comparator == 4 || this.comparator == 6 || this.comparator == 5 || this.comparator == 7) {
                            throw new IllegalArgumentException("Comparator :: " + this.comparator + " is not applicable for BOOLEAN columns");
                        }
                        if (this.value != null && !(this.value instanceof Boolean) && !(this.value instanceof Boolean[]) && !(this.value instanceof boolean[]) && !(this.value instanceof String) && !(this.value instanceof String[])) {
                            throw new IllegalArgumentException("Invalid input \"" + this.value.getClass().getCanonicalName() + "\" received, which cannot be compared for BOOLEAN Column :: " + this.column);
                        }
                        if (8 != this.comparator && 9 != this.comparator && (this.value instanceof Boolean[] || this.value instanceof String[])) {
                            throw new IllegalArgumentException("Invalid input in the criterion :: " + this + " \" " + this.value.getClass().getCanonicalName() + " \" received, which cannot be used with QueryConstants " + this.comparator);
                        }
                        break;
                    }
                    case 91:
                    case 92:
                    case 93: {
                        if (this.comparator == 12 || this.comparator == 13 || this.comparator == 10 || this.comparator == 11) {
                            throw new IllegalArgumentException("Comparator :: " + this.comparator + " is not applicable for DATE/TIME/TIMESTAMP columns");
                        }
                        if (this.value == null || this.value instanceof Date || this.value instanceof Date[]) {
                            break;
                        }
                        if (Criteria.this.isCriteriaTemplate(String.valueOf(this.value))) {
                            break;
                        }
                        throw new IllegalArgumentException("Invalid input \"" + this.value.getClass().getCanonicalName() + "\" received, which cannot be compared for DATE Column :: " + this.column);
                    }
                    default: {
                        if (!DataTypeUtil.isUDT(this.column.getDataType())) {
                            throw new IllegalArgumentException("Unknown type " + this.column.getType() + " received for the column :: " + this.column);
                        }
                        if (Criteria.this.isCriteriaTemplate(String.valueOf(this.value))) {
                            this.isInputValidated = true;
                            break;
                        }
                        DataTypeManager.getDataTypeDefinition(this.column.getDataType()).getMeta().validateCriteriaInput(this.column, this.value, this.comparator, this.caseSensitive);
                        break;
                    }
                }
                this.isInputValidated = true;
            }
        }
        
        private void transformValueForMatches() {
            if ((this.column.getType() == 91 || this.column.getType() == 92) && AppResources.getProperty("criteria.validate.datetime.values", "false").equalsIgnoreCase("true")) {
                this.transformValueAsTypeSpecific();
            }
            if ((this.column.getType() == 1 || this.column.getType() == 12) && AppResources.getString("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("true") && !this.caseSensitive) {
                if (this.value instanceof Object[]) {
                    final Object[] values = (Object[])this.value;
                    final String[] rhsValue = new String[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        if (values[i] != null) {
                            rhsValue[i] = values[i].toString().toUpperCase(Locale.ENGLISH);
                        }
                    }
                    this.value = rhsValue;
                }
                else if (this.value instanceof String) {
                    this.value = this.value.toString().toUpperCase(Locale.ENGLISH);
                }
            }
            this.isInputTransformedForMatches = true;
        }
        
        private void transformValue() {
            if (this.value instanceof UniqueValueHolder || this.value instanceof UniqueValueHolder[]) {
                return;
            }
            if ((this.column.getType() == 91 || this.column.getType() == 92) && AppResources.getString("criteria.validate.datetime.values", "false").equalsIgnoreCase("true")) {
                this.transformValueAsTypeSpecific();
            }
            if (this.column.getType() == 16 && !(this.value instanceof Column) && this.value != null && this.comparator != 8 && this.comparator != 9 && !(this.value instanceof Boolean) && (this.value instanceof Integer || this.value instanceof Long || this.value instanceof String)) {
                final String strValue = String.valueOf(this.value);
                if (Criteria.this.isCriteriaTemplate(strValue)) {
                    return;
                }
                this.value = this.getBooleanValue(strValue);
            }
            if (this.value != null && !(this.value instanceof Column)) {
                if (this.comparator == 8 || this.comparator == 9 || this.comparator == 14 || this.comparator == 15) {
                    if (this.value instanceof String) {
                        String str = (String)this.value;
                        if (Criteria.this.isCriteriaTemplate(str)) {
                            return;
                        }
                        if (str.startsWith("(") && str.endsWith(")")) {
                            str = str.substring(1, str.length() - 1);
                            final StringTokenizer tok = new StringTokenizer(str, ",");
                            final int type = this.column.getType();
                            switch (type) {
                                case 1:
                                case 12: {
                                    final String[] vals = new String[tok.countTokens()];
                                    int i = 0;
                                    while (tok.hasMoreTokens()) {
                                        vals[i++] = tok.nextToken();
                                    }
                                    this.value = vals;
                                    break;
                                }
                                case -5: {
                                    final long[] vals2 = new long[tok.countTokens()];
                                    int i = 0;
                                    while (tok.hasMoreTokens()) {
                                        vals2[i++] = Long.parseLong(tok.nextToken().trim());
                                    }
                                    this.value = vals2;
                                    break;
                                }
                                case 16: {
                                    final boolean[] vals3 = new boolean[tok.countTokens()];
                                    int i = 0;
                                    while (tok.hasMoreTokens()) {
                                        vals3[i++] = this.getBooleanValue(tok.nextToken().trim());
                                    }
                                    this.value = vals3;
                                    break;
                                }
                                case -6:
                                case 4: {
                                    final int[] vals4 = new int[tok.countTokens()];
                                    int i = 0;
                                    while (tok.hasMoreTokens()) {
                                        vals4[i++] = Integer.parseInt(tok.nextToken().trim());
                                    }
                                    this.value = vals4;
                                    break;
                                }
                                default: {
                                    Criteria.OUT.log(Level.WARNING, "Unknown type received for tranforming");
                                    break;
                                }
                            }
                        }
                    }
                    else if (this.value instanceof String[]) {
                        final String[] sArray = (String[])this.value;
                        if (this.column.getDataType() != null) {
                            this.value = MetaDataUtil.convertArray(sArray, this.column.getDataType());
                        }
                        else {
                            this.value = MetaDataUtil.convertArray(sArray, this.column.getType());
                        }
                    }
                    else if (this.value instanceof int[] || this.value instanceof long[] || this.value instanceof float[] || this.value instanceof double[] || this.value instanceof boolean[]) {
                        switch (this.column.getType()) {
                            case 4: {
                                if (this.value instanceof int[]) {
                                    final int[] v = (int[])this.value;
                                    final Integer[] j = new Integer[v.length];
                                    for (int index = 0; index < v.length; ++index) {
                                        j[index] = v[index];
                                    }
                                    this.value = j;
                                    break;
                                }
                                break;
                            }
                            case -6: {
                                if (this.value instanceof int[]) {
                                    final int[] v = (int[])this.value;
                                    final Integer[] j = new Integer[v.length];
                                    for (int index = 0; index < v.length; ++index) {
                                        j[index] = v[index];
                                        if (!AppResources.getString("ignore.tinyint.validation", "false").equalsIgnoreCase("true")) {
                                            final int intValue = j[index];
                                            if (intValue < 0 || intValue > 127) {
                                                throw new IllegalArgumentException("TINYINT value out of range. Given value : " + this.value);
                                            }
                                        }
                                    }
                                    this.value = j;
                                    break;
                                }
                                break;
                            }
                            case -5: {
                                if (this.value instanceof long[]) {
                                    final long[] v2 = (long[])this.value;
                                    final Long[] l = new Long[v2.length];
                                    for (int index = 0; index < v2.length; ++index) {
                                        l[index] = v2[index];
                                    }
                                    this.value = l;
                                    break;
                                }
                                break;
                            }
                            case 6: {
                                if (this.value instanceof float[]) {
                                    final float[] v3 = (float[])this.value;
                                    final Float[] f = new Float[v3.length];
                                    for (int index = 0; index < v3.length; ++index) {
                                        f[index] = v3[index];
                                    }
                                    this.value = f;
                                    break;
                                }
                                break;
                            }
                            case 8: {
                                if (this.value instanceof double[]) {
                                    final double[] v4 = (double[])this.value;
                                    final Double[] d = new Double[v4.length];
                                    for (int index = 0; index < v4.length; ++index) {
                                        d[index] = v4[index];
                                    }
                                    this.value = d;
                                    break;
                                }
                                break;
                            }
                            case 16: {
                                if (this.value instanceof boolean[]) {
                                    final boolean[] v5 = (boolean[])this.value;
                                    final Boolean[] b = new Boolean[v5.length];
                                    for (int index = 0; index < v5.length; ++index) {
                                        b[index] = v5[index];
                                    }
                                    this.value = b;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    else {
                        try {
                            if (this.column.getDataType() != null) {
                                MetaDataUtil.validateArray((Object[])this.value, this.column.getDataType());
                            }
                            else {
                                MetaDataUtil.validateArray((Object[])this.value, this.column.getType());
                            }
                            if (this.column.getType() == 12 && this.value instanceof Object[]) {
                                final Object[] oArray = (Object[])this.value;
                                final String[] sArray2 = new String[oArray.length];
                                for (int k = 0; k < oArray.length; ++k) {
                                    sArray2[k] = String.valueOf(oArray[k]);
                                }
                                this.value = sArray2;
                            }
                        }
                        catch (final MetaDataException mde) {
                            throw new IllegalArgumentException(mde.getMessage(), mde);
                        }
                    }
                    try {
                        Array.getLength(this.value);
                        return;
                    }
                    catch (final Exception e) {
                        throw new IllegalArgumentException("Value for BETWEEN/NOT_BETWEEN/IN/NOT_IN comparator is not an array", e);
                    }
                }
                try {
                    if (this.value instanceof Object[]) {
                        throw new IllegalArgumentException("Array value is not valid for " + this.comparator + " comparator");
                    }
                    if (this.value instanceof String) {
                        final String str = (String)this.value;
                        if (Criteria.this.isCriteriaTemplate(str)) {
                            return;
                        }
                        Label_1662: {
                            if (this.checkPatternInNumericalString(str)) {
                                if (null != this.column.getDataType()) {
                                    if (this.column.getDataType().equals("CHAR") || this.column.getDataType().equals("SCHAR") || this.column.getDataType().equals("NCHAR") || this.column.getDataType().equals("TIME") || this.column.getDataType().equals("DATE") || this.column.getDataType().equals("TIMESTAMP")) {
                                        break Label_1662;
                                    }
                                    if (this.column.getDataType().equals("DATETIME")) {
                                        break Label_1662;
                                    }
                                }
                                else if (MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("CHAR") || MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("SCHAR") || MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("NCHAR") || MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("TIME") || MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("DATE") || MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("TIMESTAMP") || MetaDataUtil.getSQLTypeAsString(this.column.getType()).equals("DATETIME")) {
                                    break Label_1662;
                                }
                                return;
                            }
                        }
                        if (this.column.getDataType() != null) {
                            MetaDataUtil.validate(this.value = MetaDataUtil.convert(String.valueOf(this.value), this.column.getDataType()), this.column.getDataType());
                        }
                        else {
                            MetaDataUtil.validate(this.value = MetaDataUtil.convert(String.valueOf(this.value), this.column.getType()), this.column.getType());
                        }
                    }
                    else if (this.column.getDataType() != null) {
                        MetaDataUtil.validate(this.value, this.column.getDataType());
                        String dataType = this.column.getDataType();
                        if (DataTypeUtil.isEDT(dataType)) {
                            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
                        }
                        if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("NCHAR") || dataType.equalsIgnoreCase("SCHAR")) {
                            this.value = MetaDataUtil.convert(String.valueOf(this.value), this.column.getDataType());
                        }
                        else if (dataType.equalsIgnoreCase("INTEGER") && this.value instanceof Long) {
                            this.value = MetaDataUtil.convert(String.valueOf(this.value), this.column.getDataType());
                        }
                    }
                    else {
                        MetaDataUtil.validate(this.value, this.column.getType());
                        if (this.column.getType() == 1 || this.column.getType() == -15 || this.column.getType() == -9 || this.column.getType() == 12 || this.column.getType() == -1 || this.column.getType() == -16) {
                            this.value = MetaDataUtil.convert(String.valueOf(this.value), this.column.getType());
                        }
                    }
                }
                catch (final MetaDataException mde) {
                    throw new IllegalArgumentException(mde.getMessage(), mde);
                }
            }
        }
        
        boolean checkPatternInNumericalString(final String str) {
            return (str.endsWith("*") && str.matches(".*\\d+?\\*+?")) || str.matches("\\**?\\d+?");
        }
        
        private boolean getBooleanValue(final String s) {
            final String strValue = s.trim();
            if (strValue.equalsIgnoreCase("true") || strValue.equals("1") || strValue.equalsIgnoreCase("t")) {
                return Boolean.TRUE;
            }
            if (strValue.equalsIgnoreCase("false") || strValue.equals("0") || strValue.equalsIgnoreCase("f")) {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Unknown value [" + s + "] set for BOOLEAN column [" + this.column + "]");
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == -1) {
                this.hashCode = Criteria.this.hashCode(this.column) + Criteria.this.hashCode(this.value) + this.comparator;
            }
            return this.hashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Criterion)) {
                return false;
            }
            final Criterion ctr = (Criterion)obj;
            boolean equals = false;
            equals = (this.comparator == ctr.comparator);
            if (!equals) {
                return false;
            }
            equals = this.column.equals(ctr.column);
            if (!equals) {
                return false;
            }
            equals = ((this.value == null && ctr.value == null) || this.equalValue(ctr.value));
            if (!equals) {
                return false;
            }
            equals = (this.caseSensitive == ctr.caseSensitive);
            return equals;
        }
        
        private boolean equalValue(final Object value) {
            if (this.value == null) {
                return false;
            }
            if (this.comparator == 8 || this.comparator == 9 || this.comparator == 14 || this.comparator == 15) {
                if (this.value instanceof int[]) {
                    return value instanceof int[] && Arrays.equals((int[])this.value, (int[])value);
                }
                if (this.value instanceof long[]) {
                    return value instanceof long[] && Arrays.equals((long[])this.value, (long[])value);
                }
                if (this.value instanceof String[]) {
                    return value instanceof String[] && Arrays.equals((Object[])this.value, (Object[])value);
                }
                if (this.value instanceof Object[]) {
                    if (value instanceof Object[]) {
                        return Arrays.equals((Object[])this.value, (Object[])value);
                    }
                    return value.getClass().isArray() && Arrays.equals((Object[])this.value, this.getAsObjectArray(value));
                }
            }
            return this.value.equals(value);
        }
        
        private Object[] getAsObjectArray(final Object value) {
            final int length = Array.getLength(value);
            final Object[] obj = new Object[length];
            for (int i = 0; i < length; ++i) {
                obj[i] = Array.get(value, i);
            }
            return obj;
        }
        
        public boolean matches(final Properties props) {
            if (!this.isInputTransformedForMatches) {
                this.transformValueForMatches();
            }
            final String propVal = props.getProperty(this.column.getColumnName());
            return propVal != null && this.matches(propVal, this.value);
        }
        
        private Object getValue(final Row row, final Column c) {
            if (c.getColumnIndex() == -1) {
                c.setType(row.getSQLType(row.findColumn(c.getColumnName())));
                return row.get(c.getColumnName());
            }
            row.getColumns();
            c.setType(row.getSQLType(c.getColumnIndex()));
            return row.get(c.getColumnIndex());
        }
        
        private boolean matches(final Object lhsColumn, final Object rhsColumn, final Map map) {
            if (map.containsKey(rhsColumn) && map.containsKey(lhsColumn)) {
                final Object lhsData = map.get(lhsColumn);
                final Object rhsData = map.get(rhsColumn);
                return this.matches(lhsData, rhsData);
            }
            return false;
        }
        
        private boolean matches(final Object lhsData, final Object rhsData) {
            if (lhsData == null || rhsData == null) {
                return false;
            }
            if (!lhsData.getClass().equals(rhsData.getClass())) {
                throw new IllegalArgumentException("LHS Column and the RHS Column differ in DataTypes.");
            }
            Comparable comparableLHSData = null;
            Comparable comparableRHSData = null;
            if (this.caseSensitive) {
                comparableLHSData = ((lhsData instanceof Boolean) ? lhsData.toString() : lhsData);
                comparableRHSData = ((rhsData instanceof Boolean) ? rhsData.toString() : rhsData);
            }
            else {
                comparableLHSData = ((lhsData instanceof Boolean) ? lhsData.toString() : ((lhsData instanceof String) ? ((String)lhsData).toUpperCase(Locale.ENGLISH) : lhsData));
                comparableRHSData = ((rhsData instanceof Boolean) ? rhsData.toString() : ((rhsData instanceof String) ? ((String)rhsData).toUpperCase(Locale.ENGLISH) : rhsData));
            }
            final int comparedValue = comparableLHSData.compareTo(comparableRHSData);
            switch (this.comparator) {
                case 0:
                case 2:
                case 8:
                case 10:
                case 11:
                case 12: {
                    return comparedValue == 0;
                }
                case 1:
                case 3:
                case 9:
                case 13: {
                    return comparedValue != 0;
                }
                case 4: {
                    return comparedValue >= 0;
                }
                case 5: {
                    return comparedValue > 0;
                }
                case 6: {
                    return comparedValue <= 0;
                }
                case 7: {
                    return comparedValue < 0;
                }
                default: {
                    return false;
                }
            }
        }
        
        public boolean matches(final Row row) {
            return this.matches(row, Criteria.this.treatNullAsValue);
        }
        
        public boolean matches(final Row row, final boolean treatNullAsValue) {
            if (!this.isInputTransformedForMatches) {
                this.transformValueForMatches();
            }
            final Object lhsValue = this.getValue(row, this.column);
            if (this.value instanceof Column) {
                return this.matches(lhsValue, this.getValue(row, (Column)this.value));
            }
            return this.matches(lhsValue, this.value, treatNullAsValue);
        }
        
        public boolean matches(final Map map, final boolean treatNullAsValue) {
            if (!this.isInputValidated && AppResources.getString("validate.criteria.in.matches", "false").equalsIgnoreCase("true")) {
                this.validateInput();
            }
            if (!this.isInputTransformedForMatches) {
                this.transformValueForMatches();
            }
            final Object val = map.get(this.column);
            if (this.value instanceof Column) {
                return this.matches(this.column, this.value, map);
            }
            final boolean keyExist = map.containsKey(this.column);
            return keyExist && this.matches(val, this.value, treatNullAsValue);
        }
        
        private boolean matches(final Object lhsValue, final Object rhsValue, final boolean treatNullAsValue) {
            if (treatNullAsValue && (this.comparator == 3 || this.comparator == 1) && ((lhsValue == null && rhsValue != null) || (rhsValue == null && lhsValue != null))) {
                return true;
            }
            if (lhsValue == null && rhsValue != null) {
                return false;
            }
            if (rhsValue == null) {
                switch (this.comparator) {
                    case 0:
                    case 2: {
                        return lhsValue == rhsValue;
                    }
                    case 1:
                    case 3: {
                        return lhsValue != rhsValue;
                    }
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15: {
                        return false;
                    }
                }
            }
            final int type = this.column.getType();
            if ((lhsValue instanceof UniqueValueHolder || rhsValue instanceof UniqueValueHolder || rhsValue instanceof UniqueValueHolder[]) && !Object[].class.equals(rhsValue.getClass())) {
                return this.matchesForUVH(lhsValue, rhsValue);
            }
            if ((type == 4 || type == -5) && Object[].class.equals(rhsValue.getClass())) {
                return this.checkInHybridArray(lhsValue);
            }
            switch (type) {
                case -6:
                case -5:
                case 3:
                case 4:
                case 6:
                case 8: {
                    if (rhsValue instanceof Number || rhsValue instanceof Number[] || rhsValue instanceof String || rhsValue instanceof String[] || rhsValue instanceof int[] || rhsValue instanceof float[] || rhsValue instanceof long[] || (rhsValue instanceof double[] && lhsValue instanceof Number)) {
                        return this.matches(type, (Number)lhsValue, rhsValue);
                    }
                    throw new IllegalArgumentException("Invalid input \" " + rhsValue.getClass().getCanonicalName() + " \" received for numeric column " + this.column + " which cannot be compared with : " + lhsValue.getClass().getName());
                }
                case 12: {
                    if (rhsValue instanceof String || (rhsValue instanceof Object[] && lhsValue instanceof String)) {
                        return this.matches((String)lhsValue, rhsValue);
                    }
                    throw new IllegalArgumentException("Criteria " + this + ". Unknown type " + rhsValue.getClass().getCanonicalName() + " received for String column " + this.column + " which cannot be compared : " + lhsValue.getClass().getName());
                }
                case 16: {
                    if ((rhsValue instanceof Boolean || rhsValue instanceof Boolean[] || rhsValue instanceof boolean[] || rhsValue instanceof String || rhsValue instanceof String[]) && (lhsValue instanceof Boolean || lhsValue instanceof String)) {
                        return this.matchesForBoolean(lhsValue, rhsValue);
                    }
                    throw new IllegalArgumentException("Criteria " + this + ". The value is an instance of, " + rhsValue.getClass().getCanonicalName() + " for Boolean column " + this.column + " instead of Boolean as required ");
                }
                case 91:
                case 92:
                case 93: {
                    if (rhsValue instanceof Date || rhsValue instanceof Date[] || lhsValue instanceof String || lhsValue instanceof Date) {
                        return this.matches((Date)lhsValue, rhsValue);
                    }
                    throw new IllegalArgumentException("Criteria " + this + ". The value is an instance of, " + rhsValue.getClass().getCanonicalName() + " for Date/Time/Timestamp column " + this.column + " instead of Date/Timestamp/Time as required ");
                }
                default: {
                    if (!DataTypeManager.isDataTypeSupported(this.column.getDataType())) {
                        throw new IllegalArgumentException("Unknown type " + type + " received for column " + this.column + " which cannot be compared : " + lhsValue.getClass().getName());
                    }
                    if (DataTypeManager.getDataTypeDefinition(this.column.getDataType()).getMeta() != null) {
                        return DataTypeManager.getDataTypeDefinition(this.column.getDataType()).getMeta().matches(this.comparator, lhsValue, rhsValue, this.caseSensitive);
                    }
                    throw new IllegalArgumentException("DataTypeMetaInfo is not defined for type :: " + DataTypeManager.getDataTypeDefinition(this.column.getDataType()).getDataType());
                }
            }
        }
        
        private boolean matches(final int type, final Number mapValue, final Object rhsValue) {
            Number valueInCriteria = null;
            Label_0275: {
                if (rhsValue instanceof String) {
                    try {
                        String criValue = rhsValue.toString();
                        if (this.comparator == 2 || this.comparator == 3) {
                            criValue = criValue.replace("?", "");
                            criValue = criValue.replace("*", "");
                        }
                        valueInCriteria = new BigDecimal(criValue);
                        break Label_0275;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new IllegalArgumentException(nfe.getClass() + " Invalid input for Criteria Object value [" + rhsValue.toString() + "], which cannot be compared with " + mapValue.getClass().getName());
                    }
                }
                if (rhsValue instanceof Number) {
                    valueInCriteria = (Number)rhsValue;
                }
                else {
                    if (8 != this.comparator && 9 != this.comparator && 14 != this.comparator && 15 != this.comparator && (this.comparator != 2 || !(rhsValue instanceof String[])) && (this.comparator != 3 || !(rhsValue instanceof String[]))) {
                        throw new IllegalArgumentException("Criteria " + this + ". Input array \" " + rhsValue.toString() + " \" received, which cannot be used with QueryConstants" + this.getString(this.comparator));
                    }
                    final boolean retVal = this.matchesForMultiValues(type, mapValue, rhsValue);
                    return retVal;
                }
            }
            Comparable mValue = null;
            Comparable cValue = null;
            switch (type) {
                case -6:
                case 4: {
                    mValue = mapValue.intValue();
                    cValue = valueInCriteria.intValue();
                    break;
                }
                case 6: {
                    mValue = mapValue.floatValue();
                    cValue = valueInCriteria.floatValue();
                    break;
                }
                case -5: {
                    mValue = mapValue.longValue();
                    cValue = valueInCriteria.longValue();
                    break;
                }
                case 3: {
                    mValue = new BigDecimal(mapValue.toString());
                    cValue = new BigDecimal(valueInCriteria.toString());
                    break;
                }
                default: {
                    mValue = mapValue.doubleValue();
                    cValue = valueInCriteria.doubleValue();
                    break;
                }
            }
            final int compareTo = mValue.compareTo(cValue);
            switch (this.comparator) {
                case 0: {
                    return compareTo == 0;
                }
                case 1: {
                    return compareTo != 0;
                }
                case 5: {
                    return compareTo > 0;
                }
                case 7: {
                    return compareTo < 0;
                }
                case 4: {
                    return compareTo >= 0;
                }
                case 6: {
                    return compareTo <= 0;
                }
                case 12: {
                    return mapValue.toString().contains(rhsValue.toString());
                }
                case 13: {
                    return !mapValue.toString().contains(rhsValue.toString());
                }
                case 10: {
                    return mapValue.toString().startsWith(rhsValue.toString());
                }
                case 11: {
                    return mapValue.toString().endsWith(rhsValue.toString());
                }
                case 2: {
                    if (type == 3) {
                        return compareTo == 0;
                    }
                }
                case 3: {
                    if (type == 3) {
                        return compareTo != 0;
                    }
                    return this.matchesForMultiValues(type, mapValue, rhsValue);
                }
                case 8:
                case 9:
                case 14:
                case 15: {
                    return this.matchesForMultiValues(type, mapValue, rhsValue);
                }
                default: {
                    Criteria.OUT.log(Level.WARNING, "Unknown comparator recieved {0}", this.comparator);
                    return false;
                }
            }
        }
        
        private boolean matchesForMultiValues(final int type, final Object mapValue, final Object rhsValue) {
            switch (this.comparator) {
                case 8: {
                    return this.checkInArray(type, (Number)mapValue, rhsValue);
                }
                case 14: {
                    return this.checkForBetween(type, (Number)mapValue, rhsValue);
                }
                case 9: {
                    return !this.checkInArray(type, (Number)mapValue, rhsValue);
                }
                case 15: {
                    return !this.checkForBetween(type, (Number)mapValue, rhsValue);
                }
                case 2: {
                    return this.checkForLike(mapValue.toString(), rhsValue);
                }
                case 3: {
                    return !this.checkForLike(mapValue.toString(), rhsValue);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean checkForBetween(final int type, final Number mapValue, final Object rhsValue) {
            if (!(rhsValue instanceof Object[])) {
                throw new IllegalArgumentException("QueryConstants BETWEEN/NOTBETWEEN received, Object Value should be an Array ");
            }
            final Object[] between = (Object[])rhsValue;
            if (between.length != 2) {
                throw new IllegalArgumentException("QueryConstants BETWEEN/NOTBETWEEN received, Object Value Array should have two values");
            }
            Arrays.sort(between);
            switch (type) {
                case 8: {
                    return (double)between[0] <= mapValue.doubleValue() && (double)between[1] >= mapValue.doubleValue();
                }
                case 6: {
                    return (float)between[0] <= mapValue.floatValue() && (float)between[1] >= mapValue.floatValue();
                }
                case -5: {
                    return (long)between[0] <= mapValue.longValue() && (long)between[1] >= mapValue.longValue();
                }
                case -6:
                case 4: {
                    return (int)between[0] <= mapValue.intValue() && (int)between[1] >= mapValue.intValue();
                }
                case 3: {
                    final Comparable from = (BigDecimal)between[0];
                    final Comparable to = (BigDecimal)between[1];
                    return (from.compareTo(mapValue) == -1 || from.compareTo(mapValue) == 0) && (to.compareTo(mapValue) == 1 || to.compareTo(mapValue) == 0);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean checkInHybridArray(final Object mapValue) {
            final Object[] criteriaValue = (Object[])this.value;
            for (int length = criteriaValue.length, i = 0; i < length; ++i) {
                if (criteriaValue[i] instanceof String) {
                    try {
                        switch (this.column.getType()) {
                            case 4: {
                                criteriaValue[i] = new Integer(criteriaValue[i].toString());
                                break;
                            }
                            case -5: {
                                criteriaValue[i] = new Long(criteriaValue[i].toString());
                                break;
                            }
                            case 16: {
                                final String s = criteriaValue[i].toString();
                                criteriaValue[i] = ((s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1")) ? Boolean.TRUE : Boolean.FALSE);
                                break;
                            }
                            default: {
                                Criteria.OUT.log(Level.WARNING, " Hybrid array given for unknown datatype");
                                break;
                            }
                        }
                        continue;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new IllegalArgumentException("Invalid input for Criteria value [" + criteriaValue[i].toString() + "], which cannot be compared with " + mapValue.getClass().getName());
                    }
                }
                if (!(criteriaValue[i] instanceof UniqueValueHolder) && !(criteriaValue[i] instanceof Number)) {
                    throw new IllegalArgumentException("Invalid input in the criterion :: " + this + " \" " + this.value.getClass().getCanonicalName() + " \" received, which cannot be compared with : " + criteriaValue.getClass().getName());
                }
            }
            final boolean isContain = Arrays.asList(criteriaValue).contains(mapValue);
            return (this.comparator == 8) ? isContain : (!isContain);
        }
        
        private boolean checkInArray(final int type, final Number mapValue, final Object rhsValue) {
            if (rhsValue instanceof Object[] && type != 3) {
                Arrays.sort((Object[])rhsValue);
                return Arrays.binarySearch((Object[])rhsValue, mapValue) > -1;
            }
            switch (type) {
                case 8: {
                    Arrays.sort((double[])rhsValue);
                    return Arrays.binarySearch((double[])rhsValue, mapValue.doubleValue()) > -1;
                }
                case 6: {
                    Arrays.sort((float[])rhsValue);
                    return Arrays.binarySearch((float[])rhsValue, mapValue.floatValue()) > -1;
                }
                case -5: {
                    Arrays.sort((long[])rhsValue);
                    return Arrays.binarySearch((long[])rhsValue, mapValue.longValue()) > -1;
                }
                case -6:
                case 4: {
                    Arrays.sort((int[])rhsValue);
                    return Arrays.binarySearch((int[])rhsValue, mapValue.intValue()) > -1;
                }
                case 3: {
                    for (int i = 0; i < ((Object[])rhsValue).length; ++i) {
                        if (((BigDecimal)mapValue).compareTo(((BigDecimal[])rhsValue)[i]) == 0) {
                            return true;
                        }
                    }
                    return false;
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean matches(final String mapValue, final Object rhsValue) {
            boolean result = false;
            if (14 != this.comparator && 15 != this.comparator && 8 != this.comparator && 9 != this.comparator && 2 != this.comparator && 3 != this.comparator && rhsValue instanceof Object[]) {
                throw new IllegalArgumentException("Criteria ObjectValue Object[] received, cannot use with QueryConstants " + this.getString(this.comparator));
            }
            switch (this.comparator) {
                case 0: {
                    if (this.caseSensitive) {
                        result = mapValue.equals(rhsValue.toString());
                        break;
                    }
                    result = mapValue.equalsIgnoreCase(rhsValue.toString());
                    break;
                }
                case 1: {
                    if (this.caseSensitive) {
                        result = !mapValue.equals(rhsValue.toString());
                        break;
                    }
                    result = !mapValue.equalsIgnoreCase(rhsValue.toString());
                    break;
                }
                case 2: {
                    if (this.caseSensitive) {
                        result = this.checkForLike(mapValue, rhsValue);
                        break;
                    }
                    result = this.checkForLike(mapValue.toUpperCase(Locale.ENGLISH), rhsValue);
                    break;
                }
                case 12: {
                    if (this.caseSensitive) {
                        result = mapValue.toString().contains(rhsValue.toString());
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = mapValue.toUpperCase(Locale.ENGLISH).contains(rhsValue.toString().toUpperCase(Locale.ENGLISH));
                        break;
                    }
                    result = mapValue.toUpperCase(Locale.ENGLISH).contains(rhsValue.toString());
                    break;
                }
                case 10: {
                    if (this.caseSensitive) {
                        result = mapValue.startsWith(rhsValue.toString());
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = mapValue.toUpperCase(Locale.ENGLISH).startsWith(rhsValue.toString().toUpperCase(Locale.ENGLISH));
                        break;
                    }
                    result = mapValue.toUpperCase(Locale.ENGLISH).startsWith(rhsValue.toString());
                    break;
                }
                case 11: {
                    if (this.caseSensitive) {
                        result = mapValue.endsWith(rhsValue.toString());
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = mapValue.toUpperCase(Locale.ENGLISH).endsWith(rhsValue.toString().toUpperCase(Locale.ENGLISH));
                        break;
                    }
                    result = mapValue.toUpperCase(Locale.ENGLISH).endsWith(rhsValue.toString());
                    break;
                }
                case 3: {
                    if (this.caseSensitive) {
                        result = !this.checkForLike(mapValue, rhsValue);
                        break;
                    }
                    result = !this.checkForLike(mapValue.toUpperCase(Locale.ENGLISH), rhsValue);
                    break;
                }
                case 13: {
                    if (this.caseSensitive) {
                        result = !mapValue.contains(rhsValue.toString());
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = !mapValue.toUpperCase(Locale.ENGLISH).contains(rhsValue.toString().toUpperCase(Locale.ENGLISH));
                        break;
                    }
                    result = !mapValue.toUpperCase(Locale.ENGLISH).contains(rhsValue.toString());
                    break;
                }
                case 8: {
                    if (this.caseSensitive) {
                        result = Arrays.asList((Object[])rhsValue).contains(mapValue);
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = this.checkForCaseInsensitiveIn((Object[])rhsValue, mapValue);
                        break;
                    }
                    result = Arrays.asList((Object[])rhsValue).contains(mapValue.toUpperCase(Locale.ENGLISH));
                    break;
                }
                case 9: {
                    if (this.caseSensitive) {
                        result = !Arrays.asList((Object[])rhsValue).contains(mapValue);
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = !this.checkForCaseInsensitiveIn((Object[])rhsValue, mapValue);
                        break;
                    }
                    result = !Arrays.asList((Object[])rhsValue).contains(mapValue.toUpperCase(Locale.ENGLISH));
                    break;
                }
                case 4: {
                    if (this.caseSensitive) {
                        result = (mapValue.compareTo(String.valueOf(rhsValue)) >= 0);
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue).toUpperCase(Locale.ENGLISH)) >= 0);
                        break;
                    }
                    result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue)) >= 0);
                    break;
                }
                case 5: {
                    if (this.caseSensitive) {
                        result = (mapValue.compareTo(String.valueOf(rhsValue)) > 0);
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue).toUpperCase(Locale.ENGLISH)) > 0);
                        break;
                    }
                    result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue)) > 0);
                    break;
                }
                case 6: {
                    if (this.caseSensitive) {
                        result = (mapValue.compareTo(String.valueOf(rhsValue)) <= 0);
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue).toUpperCase(Locale.ENGLISH)) <= 0);
                        break;
                    }
                    result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue)) <= 0);
                    break;
                }
                case 7: {
                    if (this.caseSensitive) {
                        result = (mapValue.compareTo(String.valueOf(rhsValue)) < 0);
                        break;
                    }
                    if (AppResources.getProperty("transform.value.for.caseinsensitivity", "false").equalsIgnoreCase("false")) {
                        result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue).toUpperCase(Locale.ENGLISH)) < 0);
                        break;
                    }
                    result = (mapValue.toUpperCase(Locale.ENGLISH).compareTo(String.valueOf(rhsValue)) < 0);
                    break;
                }
                case 14: {
                    if (this.caseSensitive) {
                        result = this.checkForBetween(mapValue, rhsValue, true);
                        break;
                    }
                    result = this.checkForBetween(mapValue, rhsValue, false);
                    break;
                }
                case 15: {
                    if (this.caseSensitive) {
                        result = !this.checkForBetween(mapValue, rhsValue, true);
                        break;
                    }
                    result = !this.checkForBetween(mapValue, rhsValue, false);
                    break;
                }
            }
            return result;
        }
        
        private boolean checkForBetween(final String mapValue, final Object value, final boolean isCaseSensitive) {
            final String[] criValue = (String[])value;
            if (criValue.length != 2) {
                throw new IllegalArgumentException("QueryConstants " + this.getString(this.comparator) + " received, Criteria ObjectValue Array should have only two values");
            }
            Arrays.sort(criValue);
            if (isCaseSensitive) {
                if (criValue[0].compareTo(mapValue) <= 0 && criValue[1].compareTo(mapValue) >= 0) {
                    return true;
                }
            }
            else if (criValue[0].compareToIgnoreCase(mapValue) >= 0 && criValue[1].compareToIgnoreCase(mapValue) <= 0) {
                return true;
            }
            return false;
        }
        
        private boolean checkForCaseInsensitiveIn(final Object[] value, final String mapValue) {
            for (int i = 0; i < value.length; ++i) {
                if (value[i].toString().equalsIgnoreCase(mapValue)) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean checkForLike(final String mapValue, final Object value) {
            if (!this.isPatternValidated) {
                this.validatePattern(value);
            }
            if (null != this.convertedGlobToRegExList) {
                for (final Pattern patternObj : this.convertedGlobToRegExList) {
                    if (this.patternMatcher(mapValue, patternObj)) {
                        return true;
                    }
                }
                return false;
            }
            return this.patternMatcher(mapValue, this.pattern);
        }
        
        private void validatePattern(Object patternString) {
            Pattern patterns = null;
            if (patternString instanceof String[]) {
                this.convertedGlobToRegExList = new ArrayList<Pattern>();
                for (String tempStr : (String[])patternString) {
                    if (!Criteria.this.isCaseSensitive()) {
                        tempStr = tempStr.toUpperCase(Locale.ENGLISH);
                    }
                    tempStr = this.convertGlobToRegEx(tempStr);
                    patterns = Pattern.compile(tempStr);
                    this.convertedGlobToRegExList.add(patterns);
                }
            }
            else {
                if (!Criteria.this.isCaseSensitive()) {
                    patternString = patternString.toString().toUpperCase(Locale.ENGLISH);
                }
                this.convertedGlobToRegEx = this.convertGlobToRegEx(patternString.toString());
                this.pattern = Pattern.compile(this.convertedGlobToRegEx);
            }
            this.isPatternValidated = true;
        }
        
        private boolean patternMatcher(final String mapValue, final Pattern patternObj) {
            this.matcher = patternObj.matcher(mapValue);
            return this.matcher.matches();
        }
        
        private String convertGlobToRegEx(String criValue) {
            Criteria.OUT.log(Level.FINE, "Value [{0}]", criValue);
            criValue = criValue.trim();
            final int strLen = criValue.length();
            final StringBuilder sb = new StringBuilder(strLen);
            boolean escaping = false;
            for (final char currentChar : criValue.toCharArray()) {
                switch (currentChar) {
                    case '*': {
                        if (escaping) {
                            sb.append("\\*");
                        }
                        else {
                            sb.append(".*");
                        }
                        escaping = false;
                        break;
                    }
                    case '?': {
                        if (escaping) {
                            sb.append("\\?");
                        }
                        else {
                            sb.append('.');
                        }
                        escaping = false;
                        break;
                    }
                    case '$':
                    case '%':
                    case '(':
                    case ')':
                    case '+':
                    case '.':
                    case '@':
                    case '[':
                    case ']':
                    case '^':
                    case '{':
                    case '|':
                    case '}': {
                        sb.append('\\');
                        sb.append(currentChar);
                        escaping = false;
                        break;
                    }
                    case '\\': {
                        if (escaping) {
                            sb.append("\\\\");
                        }
                        escaping = !escaping;
                        break;
                    }
                    default: {
                        if (escaping) {
                            sb.append("\\\\");
                        }
                        sb.append(currentChar);
                        escaping = false;
                        break;
                    }
                }
            }
            if (escaping) {
                sb.append("\\\\");
            }
            Criteria.OUT.log(Level.FINE, "Converted Criteria value [{0}]", sb.toString());
            return sb.toString();
        }
        
        private String[] toUpperCase(final String[] value) {
            final String[] newValue = value;
            for (int i = 0; i < newValue.length; ++i) {
                newValue[i] = newValue[i].toUpperCase();
            }
            return newValue;
        }
        
        private boolean matches(final Date mapValue, final Object rhsValue) {
            switch (this.comparator) {
                case 0:
                case 2: {
                    return mapValue.equals(rhsValue);
                }
                case 1:
                case 3: {
                    return !mapValue.equals(rhsValue);
                }
                case 4: {
                    return mapValue.compareTo((Date)rhsValue) >= 0;
                }
                case 5: {
                    return mapValue.after((Date)rhsValue);
                }
                case 6: {
                    return mapValue.compareTo((Date)rhsValue) <= 0;
                }
                case 7: {
                    return mapValue.before((Date)rhsValue);
                }
                case 8: {
                    return Arrays.asList((Date[])rhsValue).contains(mapValue);
                }
                case 9: {
                    return !Arrays.asList((Date[])rhsValue).contains(mapValue);
                }
                case 14: {
                    return this.checkForBetween(mapValue, rhsValue);
                }
                case 15: {
                    return !this.checkForBetween(mapValue, rhsValue);
                }
                default: {
                    Criteria.OUT.log(Level.WARNING, "Unknown comparator recieved {0} for Date datatype", this.comparator);
                    return false;
                }
            }
        }
        
        private boolean checkForBetween(final Date mapValue, final Object value) {
            final Date[] between = (Date[])value;
            Arrays.sort(between);
            final Comparable from = between[0];
            final Comparable to = between[1];
            return from.compareTo(mapValue) <= 0 && to.compareTo(mapValue) >= 0;
        }
        
        private boolean matchesForUVH(final Object mapValue, final Object value) {
            if (0 != this.comparator && 1 != this.comparator && 8 != this.comparator && 9 != this.comparator && value instanceof UniqueValueHolder) {
                throw new IllegalArgumentException("Unsupported QueryConstants " + this.getString(this.comparator) + " received, which cannot be used with " + value.getClass() + ". ");
            }
            if (mapValue instanceof UniqueValueHolder && value instanceof UniqueValueHolder) {
                switch (this.comparator) {
                    case 0:
                    case 2: {
                        return mapValue.equals(value);
                    }
                    case 1:
                    case 3: {
                        return !mapValue.equals(value);
                    }
                    case 8: {
                        return Arrays.asList((Object[])value).contains(mapValue);
                    }
                    case 9: {
                        return !Arrays.asList((Object[])value).contains(mapValue);
                    }
                    default: {
                        Criteria.OUT.log(Level.WARNING, "Unknown comparator recieved {0} for UniqueValueHolder", this.comparator);
                        return false;
                    }
                }
            }
            else {
                switch (this.comparator) {
                    case 0:
                    case 8: {
                        return false;
                    }
                    case 1:
                    case 9: {
                        return true;
                    }
                    default: {
                        Criteria.OUT.log(Level.WARNING, "Unknown comparator recieved {0}", this.comparator);
                        return false;
                    }
                }
            }
        }
        
        private boolean matchesForBoolean(final Object mapValue, Object rhsValue) {
            Boolean criteriaValue = false;
            if (rhsValue instanceof Boolean) {
                criteriaValue = rhsValue.equals(true);
            }
            else if (rhsValue instanceof String[]) {
                final String[] s = (String[])rhsValue;
                final Boolean[] b = new Boolean[s.length];
                for (int i = 0; i < s.length; ++i) {
                    if (s[i].equalsIgnoreCase("true")) {
                        b[i] = Boolean.TRUE;
                    }
                    else {
                        if (!s[i].equals("false")) {
                            return false;
                        }
                        b[i] = Boolean.FALSE;
                    }
                }
                rhsValue = b;
            }
            else if (rhsValue instanceof boolean[]) {
                int lengthOfPrimBoolArr = ((boolean[])rhsValue).length;
                final Boolean[] temp = new Boolean[lengthOfPrimBoolArr];
                int index = 0;
                while (lengthOfPrimBoolArr-- > 0) {
                    Array.set(temp, index, ((boolean[])rhsValue)[index++]);
                }
                rhsValue = temp;
            }
            switch (this.comparator) {
                case 0:
                case 2: {
                    return mapValue.equals(criteriaValue);
                }
                case 1:
                case 3: {
                    return !mapValue.equals(criteriaValue);
                }
                case 8: {
                    return Arrays.asList((Object[])rhsValue).contains(mapValue);
                }
                case 9: {
                    return !Arrays.asList((Object[])rhsValue).contains(mapValue);
                }
                default: {
                    Criteria.OUT.log(Level.WARNING, "Unknown comparator recieved {0} for Boolean column", this.comparator);
                    return false;
                }
            }
        }
        
        private void transformValueAsTypeSpecific() {
            try {
                if (this.comparator == 14 || this.comparator == 15 || this.comparator == 8 || this.comparator == 9) {
                    this.value = MetaDataUtil.convertArray(this.value, this.column.getDataType());
                }
                else {
                    this.value = MetaDataUtil.convert((this.value == null) ? null : this.value.toString(), this.column.getDataType());
                }
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException("Exception while converting value " + this.value + " into it's column type " + this.column.getDataType(), e);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append(this.column.toString());
            final String comparatorStr = this.getString(this.comparator);
            String valueStr = null;
            if (this.comparator == 8 || this.comparator == 9 || this.comparator == 14 || this.comparator == 15) {
                if (this.value == null) {
                    if (this.comparator == 9) {
                        valueStr = "( not null )";
                    }
                    else {
                        valueStr = "( null )";
                    }
                }
                else {
                    int valLength = 0;
                    try {
                        valLength = Array.getLength(this.value);
                        final StringBuffer valueBuf = new StringBuffer();
                        valueBuf.append("(");
                        if (valLength == 0) {
                            valueBuf.append("NULL");
                        }
                        for (int i = 0; i < valLength; ++i) {
                            if (i != 0) {
                                valueBuf.append(",");
                            }
                            final Object currVal = Array.get(this.value, i);
                            if (currVal == null) {
                                valueBuf.append("NULL");
                            }
                            else {
                                valueBuf.append("'").append(currVal.toString()).append("'");
                            }
                        }
                        valueBuf.append(")");
                        valueStr = valueBuf.toString();
                    }
                    catch (final IllegalArgumentException excp) {
                        valueStr = "(<IMPROPER ARRAY VALUE FOR IN/NOT_IN COMPARATOR>)";
                    }
                }
            }
            else if (this.value instanceof Column) {
                valueStr = this.value.toString();
            }
            else {
                valueStr = "'" + String.valueOf(this.value) + "'";
                if (this.value == null) {
                    if (this.comparator == 1) {
                        valueStr = " IS NOT NULL ";
                    }
                    else if (this.comparator == 0 || this.comparator == 2) {
                        valueStr = " IS NULL ";
                    }
                    else if (this.comparator == 1 || this.comparator == 3) {
                        valueStr = " IS NOT NULL ";
                    }
                    else {
                        valueStr = " NULL ";
                    }
                }
            }
            if (this.value != null || (this.comparator != 1 && this.comparator != 0)) {
                buf.append(comparatorStr);
            }
            buf.append(valueStr);
            return buf.toString();
        }
        
        protected String getString(final int comparator) {
            switch (comparator) {
                case 0: {
                    return " = ";
                }
                case 1: {
                    return " != ";
                }
                case 2: {
                    return " LIKE ";
                }
                case 3: {
                    return " NOT LIKE ";
                }
                case 4: {
                    return " >= ";
                }
                case 5: {
                    return " > ";
                }
                case 6: {
                    return " <= ";
                }
                case 7: {
                    return " < ";
                }
                case 8: {
                    return " IN ";
                }
                case 9: {
                    return " NOT IN ";
                }
                case 14: {
                    return " BETWEEN ";
                }
                case 15: {
                    return " NOT BETWEEN ";
                }
                case 12: {
                    return " CONTAINS ";
                }
                case 13: {
                    return " NOT CONTAINS ";
                }
                case 11: {
                    return " END_WITH ";
                }
                case 10: {
                    return " STARTS WITH ";
                }
                default: {
                    return null;
                }
            }
        }
        
        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            this.column.writeExternal(out);
            out.writeObject(this.value);
            out.writeInt(this.comparator);
            out.writeBoolean(this.caseSensitive);
        }
        
        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            (this.column = new Column()).readExternal(in);
            this.value = in.readObject();
            this.comparator = in.readInt();
            this.caseSensitive = in.readBoolean();
        }
    }
}
