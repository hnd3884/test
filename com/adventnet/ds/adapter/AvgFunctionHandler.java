package com.adventnet.ds.adapter;

import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;

public class AvgFunctionHandler extends AbstractFunctionHandler
{
    @Override
    public void init(final SelectQuery actualSQ, final SelectQuery sqForDataRetrieval) throws DataSourceException {
        super.init(actualSQ, sqForDataRetrieval);
        this.doPreProcessing();
    }
    
    private void doPreProcessing() {
        final List actual = this.actualSQ.getSelectColumns();
        final List retrieved = this.sqForDataRetrieval.getSelectColumns();
        for (int retSize = retrieved.size(), i = 0; i < retSize; ++i) {
            final Column col = retrieved.get(i);
            if (col.getColumn() != null && col.getFunction() == 6) {
                final Column countCol = col.getColumn();
                final boolean countExist = this.doesCountExist(countCol, retrieved);
                if (!countExist) {
                    this.sqForDataRetrieval.addSelectColumn(countCol.count());
                }
            }
        }
    }
    
    private boolean doesCountExist(final Column col, final List retrieved) {
        for (int retSize = retrieved.size(), i = 0; i < retSize; ++i) {
            final Column retCol = retrieved.get(i);
            if (retCol.getColumn().equals(col) && retCol.getFunction() == 2) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void processNextRow(final List rows, final List dataList) {
        final int[] indexes = this.getAvgIndexes();
        final int indxLength = indexes.length;
        final List avgVals = new ArrayList(indxLength);
        final List countVals = new ArrayList(indxLength);
        for (int i = 0; i < indxLength; ++i) {
            countVals.add(null);
            avgVals.add(null);
        }
        final int[] countIndexes = this.getCountIndexes(indexes);
        final int selectSize = this.actualSQ.getSelectColumns().size();
        final int dataRetSize = this.sqForDataRetrieval.getSelectColumns().size();
        for (int size = rows.size(), j = 0; j < size; ++j) {
            final List currRow = rows.get(j);
            int currIndex = 0;
            for (int k = 0; k < selectSize; ++k) {
                final Object value = currRow.get(k);
                if (currIndex < indexes.length && k == indexes[currIndex]) {
                    final Object count = currRow.get(countIndexes[currIndex]);
                    if (countVals.size() == 0 || countVals.get(currIndex) == null) {
                        if (count instanceof Integer) {
                            countVals.set(currIndex, count);
                        }
                        else if (count instanceof Double) {
                            countVals.set(currIndex, count);
                        }
                        else if (count instanceof Float) {
                            countVals.set(currIndex, count);
                        }
                        else if (count instanceof Long) {
                            countVals.set(currIndex, count);
                        }
                    }
                    else if (count instanceof Integer) {
                        countVals.set(currIndex, new Integer(countVals.get(currIndex) + (int)count));
                    }
                    else if (count instanceof Double) {
                        countVals.set(currIndex, new Double(countVals.get(currIndex) + (double)count));
                    }
                    else if (count instanceof Float) {
                        countVals.set(currIndex, new Float(countVals.get(currIndex) + (float)count));
                    }
                    else if (count instanceof Long) {
                        countVals.set(currIndex, new Long(countVals.get(currIndex) + (long)count));
                    }
                    if (avgVals.get(currIndex) == null) {
                        if (value instanceof Integer) {
                            avgVals.set(currIndex, new Integer((int)value * (int)count));
                        }
                        else if (value instanceof Double) {
                            if (count instanceof Integer) {
                                avgVals.set(currIndex, new Double((double)value * (int)count));
                            }
                            else {
                                avgVals.set(currIndex, new Double((double)value * (double)count));
                            }
                        }
                        else if (value instanceof Long) {
                            if (count instanceof Integer) {
                                avgVals.set(currIndex, new Long((long)value * (int)count));
                            }
                            else {
                                avgVals.set(currIndex, new Long((long)value * (long)count));
                            }
                        }
                        else if (value instanceof Float) {
                            if (count instanceof Integer) {
                                avgVals.set(currIndex, new Float((float)value * (int)count));
                            }
                            else {
                                avgVals.set(currIndex, new Float((float)value * (float)count));
                            }
                        }
                    }
                    else if (value instanceof Integer) {
                        avgVals.set(currIndex, new Integer(avgVals.get(currIndex) + (int)value * (int)count));
                    }
                    else if (value instanceof Double) {
                        if (count instanceof Integer) {
                            avgVals.set(currIndex, new Double(avgVals.get(currIndex) + (double)value * (int)count));
                        }
                        else {
                            avgVals.set(currIndex, new Double(avgVals.get(currIndex) + (double)value * (double)count));
                        }
                    }
                    else if (value instanceof Long) {
                        if (count instanceof Integer) {
                            avgVals.set(currIndex, new Long(avgVals.get(currIndex) + (long)value * (int)count));
                        }
                        else {
                            avgVals.set(currIndex, new Long(avgVals.get(currIndex) + (long)value * (long)count));
                        }
                    }
                    else if (value instanceof Float) {
                        if (count instanceof Integer) {
                            avgVals.set(currIndex, new Float(avgVals.get(currIndex) + (float)value * (int)count));
                        }
                        else {
                            avgVals.set(currIndex, new Float(avgVals.get(currIndex) + (float)value * (float)count));
                        }
                    }
                    ++currIndex;
                }
                else if (j == 0 && dataList.get(k) instanceof Column) {
                    dataList.set(k, value);
                }
            }
        }
        for (int indexesLength = indexes.length, l = 0; l < indexesLength; ++l) {
            final Object avgVal = avgVals.get(l);
            final Object countVal = countVals.get(l);
            if (avgVal instanceof Integer) {
                dataList.set(indexes[l], new Integer((int)avgVal / (int)countVal));
            }
            else if (avgVal instanceof Long) {
                if (countVal instanceof Integer) {
                    dataList.set(indexes[l], new Long((long)avgVal / (int)countVal));
                }
                else {
                    dataList.set(indexes[l], new Long((long)avgVal / (long)countVal));
                }
            }
            else if (avgVal instanceof Double) {
                if (countVal instanceof Integer) {
                    dataList.set(indexes[l], new Double((double)avgVal / (int)countVal));
                }
                else {
                    dataList.set(indexes[l], new Double((double)avgVal / (double)countVal));
                }
            }
            else if (avgVal instanceof Float) {
                if (countVal instanceof Integer) {
                    dataList.set(indexes[l], new Float((float)avgVal / (int)countVal));
                }
                else {
                    dataList.set(indexes[l], new Float((float)avgVal / (float)countVal));
                }
            }
        }
    }
    
    private int[] getAvgIndexes() {
        final List selectCols = this.actualSQ.getSelectColumns();
        final int selSize = selectCols.size();
        final int[] indexes = new int[selSize];
        int currIndex = 0;
        for (int i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() != null && col.getFunction() == 6) {
                indexes[currIndex] = i;
                ++currIndex;
            }
        }
        return indexes;
    }
    
    private int[] getCountIndexes(final int[] avgIndexes) {
        final int[] countIndexes = new int[avgIndexes.length];
        final List selCols = this.sqForDataRetrieval.getSelectColumns();
        final List actualCols = this.actualSQ.getSelectColumns();
        for (int selSize = selCols.size(), i = 0; i < selSize; ++i) {
            final Column col = selCols.get(i);
            if (col.getColumn() != null && col.getFunction() == 2) {
                final Column column = col.getColumn();
                for (int avgLength = avgIndexes.length, j = 0; j < avgLength; ++j) {
                    final Column retCol = actualCols.get(avgIndexes[j]);
                    if (retCol.equals(column)) {
                        countIndexes[j] = i;
                    }
                }
            }
        }
        return countIndexes;
    }
}
