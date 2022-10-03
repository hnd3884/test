package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class grouping_id extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("GROUP_ID")) {
            final GroupByStatement gbs = from_sqs.getGroupByStatement();
            if (gbs != null) {
                final Vector arguments = new Vector();
                final Vector non_rollupColumns = new Vector();
                final Vector groupByItemList = gbs.getGroupByItemList();
                for (int i_count = 0; i_count < groupByItemList.size(); ++i_count) {
                    if (groupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = groupByItemList.elementAt(i_count);
                        final String scStr = sc.toString().toUpperCase();
                        if (!scStr.startsWith("ROLLUP") && !scStr.startsWith("CUBE")) {
                            non_rollupColumns.add(scStr);
                        }
                    }
                }
                for (int i_count = 0; i_count < groupByItemList.size(); ++i_count) {
                    if (groupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = groupByItemList.elementAt(i_count);
                        final Vector v_ce = sc.getColumnExpression();
                        if (v_ce.elementAt(0) instanceof FunctionCalls) {
                            final FunctionCalls fc = v_ce.elementAt(0);
                            final String s_fn = fc.getFunctionName().getColumnName();
                            if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                                final Vector rollupArgs = fc.getFunctionArguments();
                                if (rollupArgs.size() != non_rollupColumns.size() + 1) {
                                    break;
                                }
                                for (int k = 0; k < rollupArgs.size(); ++k) {
                                    final Object rollupArg = rollupArgs.get(k);
                                    if (rollupArg instanceof SelectColumn && !non_rollupColumns.contains(rollupArg.toString().toUpperCase())) {
                                        arguments.add(rollupArg);
                                    }
                                }
                            }
                        }
                    }
                    else if (groupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                        final FunctionCalls fc2 = groupByItemList.elementAt(i_count);
                        final String s_fn2 = fc2.getFunctionName().getColumnName();
                        if (s_fn2.equalsIgnoreCase("cube") || s_fn2.equalsIgnoreCase("rollup")) {
                            final Vector rollupArgs2 = fc2.getFunctionArguments();
                            if (rollupArgs2.size() != non_rollupColumns.size() + 1) {
                                break;
                            }
                            for (int i = 0; i < rollupArgs2.size(); ++i) {
                                final Object rollupArg2 = rollupArgs2.get(i);
                                if (rollupArg2 instanceof SelectColumn && !non_rollupColumns.contains(rollupArg2.toString().toUpperCase())) {
                                    arguments.add(rollupArg2);
                                }
                            }
                        }
                    }
                }
                this.functionName.setColumnName("GROUPING");
                this.setFunctionArguments(arguments);
            }
        }
        else {
            final Vector arguments2 = new Vector();
            for (int i_count2 = 0; i_count2 < this.functionArguments.size(); ++i_count2) {
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    arguments2.addElement(this.functionArguments.elementAt(i_count2).toTeradataSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.addElement(this.functionArguments.elementAt(i_count2));
                }
            }
            final SelectColumn newFunc = new SelectColumn();
            final Vector newFuncExp = new Vector();
            final int n = arguments2.size();
            newFuncExp.add("(");
            for (int j = 0; j < n; ++j) {
                final FunctionCalls groupingFn = new FunctionCalls();
                final TableColumn tcn = new TableColumn();
                tcn.setColumnName("GROUPING");
                groupingFn.setFunctionName(tcn);
                final Vector groupingFnArgs = new Vector();
                groupingFnArgs.add(arguments2.get(j));
                groupingFn.setFunctionArguments(groupingFnArgs);
                newFuncExp.add(groupingFn);
                newFuncExp.add("*");
                newFuncExp.add("" + Math.pow(2.0, n - (j + 1)));
                if (j != n - 1) {
                    newFuncExp.add("+");
                }
            }
            newFunc.setColumnExpression(newFuncExp);
            this.setFunctionName(null);
            final Vector newArgs = new Vector();
            newArgs.add(newFunc);
            this.setFunctionArguments(newArgs);
        }
    }
}
