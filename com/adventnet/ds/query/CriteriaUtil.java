package com.adventnet.ds.query;

public class CriteriaUtil
{
    public static String getComparatorString(final int qc) {
        switch (qc) {
            case 0: {
                return "EQUAL";
            }
            case 1: {
                return "NOT_EQUAL";
            }
            case 2: {
                return "LIKE";
            }
            case 3: {
                return "NOT_LIKE";
            }
            case 12: {
                return "CONTAINS";
            }
            case 13: {
                return "NOT_CONTAINS";
            }
            case 10: {
                return "STARTS_WITH";
            }
            case 11: {
                return "ENDS_WITH";
            }
            case 8: {
                return "IN";
            }
            case 9: {
                return "NOT_IN";
            }
            case 14: {
                return "BETWEEN";
            }
            case 15: {
                return "NOT_BETWEEN";
            }
            case 4: {
                return "GREATER_EQUAL";
            }
            case 5: {
                return "GREATER_THAN";
            }
            case 6: {
                return "LESS_EQUAL";
            }
            case 7: {
                return "LESS_THAN";
            }
            default: {
                return "";
            }
        }
    }
    
    public static int getConstraint(final String queryConstraint) {
        switch (queryConstraint) {
            case "EQUAL": {
                return 0;
            }
            case "NOT_EQUAL": {
                return 1;
            }
            case "LIKE": {
                return 2;
            }
            case "NOT_LIKE": {
                return 3;
            }
            case "CONTAINS": {
                return 12;
            }
            case "NOT_CONTAINS": {
                return 13;
            }
            case "STARTS_WITH": {
                return 10;
            }
            case "ENDS_WITH": {
                return 11;
            }
            case "IN": {
                return 8;
            }
            case "NOT_IN": {
                return 9;
            }
            case "BETWEEN": {
                return 14;
            }
            case "NOT_BETWEEN": {
                return 15;
            }
            case "GREATER_EQUAL": {
                return 4;
            }
            case "GREATER_THAN": {
                return 5;
            }
            case "LESS_EQUAL": {
                return 6;
            }
            case "LESS_THAN": {
                return 7;
            }
            default: {
                return -1;
            }
        }
    }
}
