package net.sf.jsqlparser.util;

import java.util.List;
import java.util.ArrayList;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

public final class SelectUtils
{
    private static final String NOT_SUPPORTED_YET = "Not supported yet.";
    
    private SelectUtils() {
    }
    
    public static Select buildSelectFromTableAndExpressions(final Table table, final Expression... expr) {
        final SelectItem[] list = new SelectItem[expr.length];
        for (int i = 0; i < expr.length; ++i) {
            list[i] = new SelectExpressionItem(expr[i]);
        }
        return buildSelectFromTableAndSelectItems(table, list);
    }
    
    public static Select buildSelectFromTableAndExpressions(final Table table, final String... expr) throws JSQLParserException {
        final SelectItem[] list = new SelectItem[expr.length];
        for (int i = 0; i < expr.length; ++i) {
            list[i] = new SelectExpressionItem(CCJSqlParserUtil.parseExpression(expr[i]));
        }
        return buildSelectFromTableAndSelectItems(table, list);
    }
    
    public static Select buildSelectFromTableAndSelectItems(final Table table, final SelectItem... selectItems) {
        final Select select = new Select();
        final PlainSelect body = new PlainSelect();
        body.addSelectItems(selectItems);
        body.setFromItem(table);
        select.setSelectBody(body);
        return select;
    }
    
    public static Select buildSelectFromTable(final Table table) {
        return buildSelectFromTableAndSelectItems(table, new AllColumns());
    }
    
    public static void addExpression(final Select select, final Expression expr) {
        select.getSelectBody().accept(new SelectVisitor() {
            @Override
            public void visit(final PlainSelect plainSelect) {
                plainSelect.getSelectItems().add(new SelectExpressionItem(expr));
            }
            
            @Override
            public void visit(final SetOperationList setOpList) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
            @Override
            public void visit(final WithItem withItem) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    
    public static Join addJoin(final Select select, final Table table, final Expression onExpression) {
        if (select.getSelectBody() instanceof PlainSelect) {
            final PlainSelect plainSelect = (PlainSelect)select.getSelectBody();
            List<Join> joins = plainSelect.getJoins();
            if (joins == null) {
                joins = new ArrayList<Join>();
                plainSelect.setJoins(joins);
            }
            final Join join = new Join();
            join.setRightItem(table);
            join.setOnExpression(onExpression);
            joins.add(join);
            return join;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void addGroupBy(final Select select, final Expression expr) {
        select.getSelectBody().accept(new SelectVisitor() {
            @Override
            public void visit(final PlainSelect plainSelect) {
                plainSelect.addGroupByColumnReference(expr);
            }
            
            @Override
            public void visit(final SetOperationList setOpList) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
            @Override
            public void visit(final WithItem withItem) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
}
