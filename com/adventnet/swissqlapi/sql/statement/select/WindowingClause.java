package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class WindowingClause
{
    String between;
    String and;
    String unbounded;
    String preceding;
    String following;
    String current;
    String row;
    SelectColumn windowExpr;
    WindowingClause firstWindow;
    WindowingClause secondWindow;
    String rowsOrRange;
    
    public void setUnbounded(final String unbounded) {
        this.unbounded = unbounded;
    }
    
    public void setPreceding(final String preceding) {
        this.preceding = preceding;
    }
    
    public void setFollowing(final String following) {
        this.following = following;
    }
    
    public void setBetween(final String between) {
        this.between = between;
    }
    
    public void setAnd(final String and) {
        this.and = and;
    }
    
    public void setCurrent(final String current) {
        this.current = current;
    }
    
    public void setRow(final String row) {
        this.row = row;
    }
    
    public void setRowsOrRange(final String rowsOrRange) {
        this.rowsOrRange = rowsOrRange;
    }
    
    public void setFirstWindow(final WindowingClause firstWindow) {
        this.firstWindow = firstWindow;
    }
    
    public void setSecondWindow(final WindowingClause secondWindow) {
        this.secondWindow = secondWindow;
    }
    
    public void setWindowExpr(final SelectColumn windowExpr) {
        this.windowExpr = windowExpr;
    }
    
    public String getUnbounded() {
        return this.unbounded;
    }
    
    public String getPreceding() {
        return this.preceding;
    }
    
    public String getFollowing() {
        return this.following;
    }
    
    public String getBetween() {
        return this.between;
    }
    
    public String getAnd() {
        return this.and;
    }
    
    public String getCurrent() {
        return this.current;
    }
    
    public String getRow() {
        return this.row;
    }
    
    public String getRowsOrRange() {
        return this.rowsOrRange;
    }
    
    public WindowingClause getFirstWindow() {
        return this.firstWindow;
    }
    
    public WindowingClause getSecondWindow() {
        return this.secondWindow;
    }
    
    public SelectColumn getWindowExpr() {
        return this.windowExpr;
    }
    
    public WindowingClause toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WindowingClause windowingClause = new WindowingClause();
        if (this.getRowsOrRange() != null) {
            windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
        }
        if (this.getBetween() != null) {
            windowingClause.setBetween("BETWEEN");
        }
        if (this.getFirstWindow() != null) {
            windowingClause.setFirstWindow(this.getFirstWindow().toNetezza(to_sqs, from_sqs));
        }
        if (this.getAnd() != null) {
            windowingClause.setAnd("AND");
        }
        if (this.getSecondWindow() != null) {
            windowingClause.setSecondWindow(this.getSecondWindow().toNetezza(to_sqs, from_sqs));
        }
        if (this.getUnbounded() != null) {
            windowingClause.setUnbounded("UNBOUNDED");
        }
        if (this.getWindowExpr() != null) {
            windowingClause.setWindowExpr(this.getWindowExpr().toNetezzaSelect(to_sqs, from_sqs));
        }
        if (this.getCurrent() != null) {
            windowingClause.setCurrent("CURRENT");
        }
        if (this.getRow() != null) {
            windowingClause.setRow("ROW");
        }
        if (this.getPreceding() != null) {
            windowingClause.setPreceding("PRECEDING");
        }
        if (this.getFollowing() != null) {
            windowingClause.setFollowing("FOLLOWING");
        }
        return windowingClause;
    }
    
    public WindowingClause toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WindowingClause windowingClause = new WindowingClause();
        if (this.getRowsOrRange() != null) {
            windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
        }
        if (this.getBetween() != null) {
            windowingClause.setBetween("BETWEEN");
        }
        if (this.getFirstWindow() != null) {
            windowingClause.setFirstWindow(this.getFirstWindow().toTeradata(to_sqs, from_sqs));
        }
        if (this.getAnd() != null) {
            windowingClause.setAnd("AND");
        }
        if (this.getSecondWindow() != null) {
            windowingClause.setSecondWindow(this.getSecondWindow().toTeradata(to_sqs, from_sqs));
        }
        if (this.getUnbounded() != null) {
            windowingClause.setUnbounded("UNBOUNDED");
        }
        if (this.getWindowExpr() != null) {
            windowingClause.setWindowExpr(this.getWindowExpr().toTeradataSelect(to_sqs, from_sqs));
        }
        if (this.getCurrent() != null) {
            windowingClause.setCurrent("CURRENT");
        }
        if (this.getRow() != null) {
            windowingClause.setRow("ROW");
        }
        if (this.getPreceding() != null) {
            windowingClause.setPreceding("PRECEDING");
        }
        if (this.getFollowing() != null) {
            windowingClause.setFollowing("FOLLOWING");
        }
        return windowingClause;
    }
    
    public WindowingClause toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WindowingClause windowingClause = new WindowingClause();
        if (this.getRowsOrRange() != null) {
            windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
        }
        if (this.getBetween() != null) {
            windowingClause.setBetween("BETWEEN");
        }
        if (this.getFirstWindow() != null) {
            windowingClause.setFirstWindow(this.getFirstWindow().toMySQL(to_sqs, from_sqs));
        }
        if (this.getAnd() != null) {
            windowingClause.setAnd("AND");
        }
        if (this.getSecondWindow() != null) {
            windowingClause.setSecondWindow(this.getSecondWindow().toMySQL(to_sqs, from_sqs));
        }
        if (this.getUnbounded() != null) {
            windowingClause.setUnbounded("UNBOUNDED");
        }
        if (this.getWindowExpr() != null) {
            windowingClause.setWindowExpr(this.getWindowExpr().toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.getCurrent() != null) {
            windowingClause.setCurrent("CURRENT");
        }
        if (this.getRow() != null) {
            windowingClause.setRow("ROW");
        }
        if (this.getPreceding() != null) {
            windowingClause.setPreceding("PRECEDING");
        }
        if (this.getFollowing() != null) {
            windowingClause.setFollowing("FOLLOWING");
        }
        return windowingClause;
    }
    
    public WindowingClause toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WindowingClause windowingClause = new WindowingClause();
        if (this.getRowsOrRange() != null) {
            windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
        }
        if (this.getBetween() != null) {
            windowingClause.setBetween("BETWEEN");
        }
        if (this.getFirstWindow() != null) {
            windowingClause.setFirstWindow(this.getFirstWindow().toVectorWise(to_sqs, from_sqs));
        }
        if (this.getAnd() != null) {
            windowingClause.setAnd("AND");
        }
        if (this.getSecondWindow() != null) {
            windowingClause.setSecondWindow(this.getSecondWindow().toVectorWise(to_sqs, from_sqs));
        }
        if (this.getUnbounded() != null) {
            windowingClause.setUnbounded("UNBOUNDED");
        }
        if (this.getWindowExpr() != null) {
            windowingClause.setWindowExpr(this.getWindowExpr().toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.getCurrent() != null) {
            windowingClause.setCurrent("CURRENT");
        }
        if (this.getRow() != null) {
            windowingClause.setRow("ROW");
        }
        if (this.getPreceding() != null) {
            windowingClause.setPreceding("PRECEDING");
        }
        if (this.getFollowing() != null) {
            windowingClause.setFollowing("FOLLOWING");
        }
        return windowingClause;
    }
    
    public WindowingClause toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WindowingClause windowingClause = new WindowingClause();
        if (this.getRowsOrRange() != null) {
            windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
        }
        if (this.getBetween() != null) {
            windowingClause.setBetween("BETWEEN");
        }
        if (this.getFirstWindow() != null) {
            windowingClause.setFirstWindow(this.getFirstWindow().toPostgreSQL(to_sqs, from_sqs));
        }
        if (this.getAnd() != null) {
            windowingClause.setAnd("AND");
        }
        if (this.getSecondWindow() != null) {
            windowingClause.setSecondWindow(this.getSecondWindow().toPostgreSQL(to_sqs, from_sqs));
        }
        if (this.getUnbounded() != null) {
            windowingClause.setUnbounded("UNBOUNDED");
        }
        if (this.getWindowExpr() != null) {
            windowingClause.setWindowExpr(this.getWindowExpr().toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.getCurrent() != null) {
            windowingClause.setCurrent("CURRENT");
        }
        if (this.getRow() != null) {
            windowingClause.setRow("ROW");
        }
        if (this.getPreceding() != null) {
            windowingClause.setPreceding("PRECEDING");
        }
        if (this.getFollowing() != null) {
            windowingClause.setFollowing("FOLLOWING");
        }
        return windowingClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.getRowsOrRange() != null) {
            sb.append(" " + this.getRowsOrRange().toUpperCase() + " ");
        }
        if (this.getBetween() != null) {
            sb.append(" BETWEEN ");
        }
        if (this.getFirstWindow() != null) {
            sb.append(this.getFirstWindow().toString());
        }
        if (this.getAnd() != null) {
            sb.append(" AND ");
        }
        if (this.getSecondWindow() != null) {
            sb.append(this.getSecondWindow().toString());
        }
        if (this.getUnbounded() != null) {
            sb.append(" UNBOUNDED ");
        }
        if (this.getWindowExpr() != null) {
            sb.append(this.getWindowExpr().toString());
        }
        if (this.getCurrent() != null) {
            sb.append(" CURRENT ");
        }
        if (this.getRow() != null) {
            sb.append(" ROW ");
        }
        if (this.getPreceding() != null) {
            sb.append(" PRECEDING ");
        }
        if (this.getFollowing() != null) {
            sb.append(" FOLLOWING ");
        }
        return sb.toString();
    }
}
