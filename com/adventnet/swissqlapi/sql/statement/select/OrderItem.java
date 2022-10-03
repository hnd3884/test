package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class OrderItem
{
    private SelectColumn orderSpecifier;
    private String Order;
    private String UsingOperator;
    private String nullsOrder;
    private UserObjectContext context;
    
    public OrderItem() {
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setOrderSpecifier(final SelectColumn sc) {
        this.orderSpecifier = sc;
    }
    
    public void setOrder(final String s_o) {
        this.Order = s_o;
    }
    
    public void setNullsOrder(final String nullsOrder) {
        this.nullsOrder = nullsOrder;
    }
    
    public String getNullsOrder() {
        return this.nullsOrder;
    }
    
    public void setUsingOperator(final String s_uo) {
        this.UsingOperator = s_uo;
    }
    
    public SelectColumn getOrderSpecifier() {
        return this.orderSpecifier;
    }
    
    public String getOrder() {
        return this.Order;
    }
    
    public String getUsingOperator() {
        return this.UsingOperator;
    }
    
    public OrderItem toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toANSISelect(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            this.Order = " ";
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            final SelectColumn teradataOrderSpecifier = this.orderSpecifier.toTeradataSelect(to_sqs, from_sqs);
            final String scStr = this.orderSpecifier.getTheCoreSelectItem().trim();
            oi.setOrderSpecifier(teradataOrderSpecifier);
            boolean aliasPresent = false;
            if (to_sqs != null && from_sqs != null && to_sqs.getSelectStatement() != null && from_sqs.getSelectStatement().getSelectItemList().size() == to_sqs.getSelectStatement().getSelectItemList().size()) {
                for (int sci = 0; sci < from_sqs.getSelectStatement().getSelectItemList().size(); ++sci) {
                    final String al = from_sqs.getSelectStatement().getSelectItemList().get(sci).getTheCoreSelectItem().trim();
                    if (al != null && al.equalsIgnoreCase(scStr)) {
                        aliasPresent = true;
                        teradataOrderSpecifier.getColumnExpression().clear();
                        teradataOrderSpecifier.getColumnExpression().add(to_sqs.getSelectStatement().getSelectItemList().get(sci).getTheCoreSelectItem());
                        break;
                    }
                    final String aliasName = from_sqs.getSelectStatement().getSelectItemList().get(sci).getAliasName();
                    if (aliasName != null && aliasName.equalsIgnoreCase(scStr)) {
                        teradataOrderSpecifier.getColumnExpression().clear();
                        teradataOrderSpecifier.getColumnExpression().add(to_sqs.getSelectStatement().getSelectItemList().get(sci).getTheCoreSelectItem());
                        break;
                    }
                }
            }
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            this.Order = " ";
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
            oi.setOrder(this.Order + " NULLS LAST ");
        }
        else if (this.Order != null) {
            oi.setOrder(this.Order + " NULLS FIRST ");
        }
        else {
            oi.setOrder(" NULLS FIRST ");
        }
        if (this.UsingOperator != null) {
            oi.setUsingOperator(this.UsingOperator);
        }
        return oi;
    }
    
    public OrderItem toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            oi.setOrder(null);
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toMSSQLServerSelect(to_sqs, from_sqs));
        }
        if (this.nullsOrder != null) {
            if (this.nullsOrder.trim().equalsIgnoreCase("NULLS FIRST")) {
                oi.setNullsOrder(null);
                oi.setOrder("ASC");
            }
            else if (this.nullsOrder.trim().equalsIgnoreCase("NULLS LAST")) {
                oi.setNullsOrder(null);
                oi.setOrder("DESC");
            }
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            oi.setOrder(null);
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        oi.setObjectContext(this.context);
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toSybaseSelect(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            oi.setOrder(null);
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toDB2Select(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            oi.setOrder(null);
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            this.orderSpecifier.setIsOrderItem(true);
            oi.setOrderSpecifier(this.orderSpecifier.toOracleSelect(to_sqs, from_sqs));
        }
        oi.setObjectContext(this.context);
        if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            if (this.nullsOrder != null) {
                oi.setOrder(this.Order + " " + this.nullsOrder);
            }
            else {
                oi.setOrder(this.Order);
            }
        }
        else if (this.nullsOrder != null) {
            oi.setOrder(this.nullsOrder);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toInformixSelect(to_sqs, from_sqs));
        }
        if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            if (this.nullsOrder != null) {
                oi.setOrder(this.Order + " " + this.nullsOrder);
            }
            else {
                oi.setOrder(this.Order);
            }
        }
        else if (this.nullsOrder != null) {
            oi.setOrder(this.nullsOrder);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toTimesTenSelect(to_sqs, from_sqs));
        }
        oi.setObjectContext(this.context);
        if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            if (this.nullsOrder != null) {
                oi.setOrder(this.Order + " " + this.nullsOrder);
            }
            else {
                oi.setOrder(this.Order);
            }
        }
        else if (this.nullsOrder != null) {
            oi.setOrder(this.nullsOrder);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    public OrderItem toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toNetezzaSelect(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            this.Order = " ";
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        return oi;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.orderSpecifier != null) {
            this.orderSpecifier.setObjectContext(this.context);
            sb.append(this.orderSpecifier);
        }
        if (this.Order != null) {
            sb.append(" " + this.Order.toUpperCase());
        }
        if (this.nullsOrder != null) {
            sb.append(" " + this.nullsOrder.toUpperCase());
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("using")) {
            sb.append(" " + this.UsingOperator.toUpperCase());
        }
        return sb.toString();
    }
    
    public OrderItem toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderItem oi = new OrderItem();
        if (this.orderSpecifier != null) {
            oi.setOrderSpecifier(this.orderSpecifier.toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
            oi.setOrder(null);
        }
        else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
            this.Order = " ";
        }
        else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
            oi.setOrder(this.Order);
        }
        else if (this.UsingOperator != null) {
            if (this.UsingOperator.equalsIgnoreCase("<") || this.UsingOperator.equalsIgnoreCase("<=") || this.UsingOperator.equalsIgnoreCase("~") || this.UsingOperator.equalsIgnoreCase("~*") || this.UsingOperator.equalsIgnoreCase("!~") || this.UsingOperator.equalsIgnoreCase("!~*") || this.UsingOperator.equalsIgnoreCase("*")) {
                oi.setOrder("ASC");
            }
            else if (this.UsingOperator.equalsIgnoreCase(">") || this.UsingOperator.equalsIgnoreCase(">=") || this.UsingOperator.equalsIgnoreCase("/")) {
                oi.setOrder("DESC");
            }
            else if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                oi = null;
            }
        }
        if (oi.getOrder() != null && oi.getOrder().equalsIgnoreCase("DESC")) {
            oi.setNullsOrder(" NULLS LAST");
        }
        else {
            oi.setNullsOrder(" NULLS FIRST");
        }
        return oi;
    }
}
