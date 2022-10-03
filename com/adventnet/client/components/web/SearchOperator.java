package com.adventnet.client.components.web;

public class SearchOperator
{
    private int value;
    private String displayString;
    private int numberOfArguments;
    public static final int IS_EMPTY_VAL = 16;
    public static final int IS_NOT_EMPTY_VAL = 17;
    public static final int IS_TRUE_VAL = 18;
    public static final int IS_FALSE_VAL = 19;
    public static final int IS_BLANK_VAL = 25;
    public static final SearchOperator IS_EQUAL;
    public static final SearchOperator IS_NOT_EQUAL;
    public static final SearchOperator LESS_THAN;
    public static final SearchOperator GREATER_THAN;
    public static final SearchOperator LESS_EQUAL;
    public static final SearchOperator GREATER_EQUAL;
    public static final SearchOperator BETWEEN;
    public static final SearchOperator NOT_BETWEEN;
    public static final SearchOperator CONTAINS;
    public static final SearchOperator NOT_CONTAINS;
    public static final SearchOperator LIKE;
    public static final SearchOperator NOT_LIKE;
    public static final SearchOperator STARTS_WITH;
    public static final SearchOperator ENDS_WITH;
    public static final SearchOperator IS_BEFORE;
    public static final SearchOperator IS_AFTER;
    public static final SearchOperator IS_EMPTY;
    public static final SearchOperator IS_NOT_EMPTY;
    public static final SearchOperator IS_TRUE;
    public static final SearchOperator IS_FALSE;
    public static final SearchOperator IS_BLANK;
    public static final SearchOperator[] NUMERIC_SEARCH_OPEARTORS;
    public static final SearchOperator[] STRING_SEARCH_OPERATORS;
    public static final SearchOperator[] BOOLEAN_SEARCH_OPERATORS;
    public static final SearchOperator[] DATE_SEARCH_OPERATORS;
    
    public SearchOperator(final int value, final String displayString, final int numberOfArguments) {
        this.value = value;
        this.displayString = displayString;
        this.numberOfArguments = numberOfArguments;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public String getDisplayString() {
        return this.displayString;
    }
    
    public int getNumberOfArguments() {
        return this.numberOfArguments;
    }
    
    static {
        IS_EQUAL = new SearchOperator(0, "is", 1);
        IS_NOT_EQUAL = new SearchOperator(1, "is not", 1);
        LESS_THAN = new SearchOperator(7, "less than", 1);
        GREATER_THAN = new SearchOperator(5, "greater than", 1);
        LESS_EQUAL = new SearchOperator(6, "less or equal", 1);
        GREATER_EQUAL = new SearchOperator(4, "greater or equal", 1);
        BETWEEN = new SearchOperator(14, "between", 2);
        NOT_BETWEEN = new SearchOperator(15, "not between", 2);
        CONTAINS = new SearchOperator(12, "contains", 1);
        NOT_CONTAINS = new SearchOperator(13, "not contains", 1);
        LIKE = new SearchOperator(2, "like", 1);
        NOT_LIKE = new SearchOperator(3, "not like", 1);
        STARTS_WITH = new SearchOperator(10, "starts with", 1);
        ENDS_WITH = new SearchOperator(11, "ends with", 1);
        IS_BEFORE = new SearchOperator(7, "is before", 1);
        IS_AFTER = new SearchOperator(5, "is after", 1);
        IS_EMPTY = new SearchOperator(16, "is empty", 0);
        IS_NOT_EMPTY = new SearchOperator(17, "is not empty", 0);
        IS_TRUE = new SearchOperator(18, "is true", 0);
        IS_FALSE = new SearchOperator(19, "is false", 0);
        IS_BLANK = new SearchOperator(25, "--------------", 0);
        NUMERIC_SEARCH_OPEARTORS = new SearchOperator[] { SearchOperator.IS_EQUAL, SearchOperator.IS_NOT_EQUAL, SearchOperator.LESS_THAN, SearchOperator.GREATER_THAN, SearchOperator.LESS_EQUAL, SearchOperator.GREATER_EQUAL, SearchOperator.IS_EMPTY, SearchOperator.IS_NOT_EMPTY };
        STRING_SEARCH_OPERATORS = new SearchOperator[] { SearchOperator.IS_EQUAL, SearchOperator.IS_NOT_EQUAL, SearchOperator.CONTAINS, SearchOperator.NOT_CONTAINS, SearchOperator.STARTS_WITH, SearchOperator.ENDS_WITH, SearchOperator.LIKE, SearchOperator.NOT_LIKE, SearchOperator.IS_EMPTY, SearchOperator.IS_NOT_EMPTY };
        BOOLEAN_SEARCH_OPERATORS = new SearchOperator[] { SearchOperator.IS_BLANK, SearchOperator.IS_TRUE, SearchOperator.IS_FALSE, SearchOperator.IS_EMPTY, SearchOperator.IS_NOT_EMPTY };
        DATE_SEARCH_OPERATORS = new SearchOperator[] { SearchOperator.IS_EQUAL, SearchOperator.IS_NOT_EQUAL, SearchOperator.IS_AFTER, SearchOperator.IS_BEFORE, SearchOperator.IS_EMPTY, SearchOperator.IS_NOT_EMPTY };
    }
}
