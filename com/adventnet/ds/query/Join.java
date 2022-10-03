package com.adventnet.ds.query;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class Join implements Cloneable, Externalizable
{
    public static final int LEFT_JOIN = 1;
    public static final int INNER_JOIN = 2;
    private String[] referencedTableColumns;
    private String[] baseTableColumns;
    private String baseTableName;
    private String referencedTableName;
    private String referencedTableAlias;
    private String baseTableAlias;
    private int joinType;
    private boolean straightJoin;
    private Criteria criteria;
    private int[] baseTableColumnIndices;
    private int[] referencedTableColumnIndices;
    Table baseTable;
    Table referencedTable;
    int hashCode;
    
    public Join() {
        this.referencedTableColumns = null;
        this.baseTableColumns = null;
        this.baseTableName = null;
        this.referencedTableName = null;
        this.referencedTableAlias = null;
        this.baseTableAlias = null;
        this.straightJoin = false;
        this.baseTableColumnIndices = null;
        this.referencedTableColumnIndices = null;
        this.baseTable = null;
        this.referencedTable = null;
        this.hashCode = -1;
    }
    
    public Join(final String baseTableName, final String baseTableAlias, final String referencedTableName, final String referencedTableAlias, final Criteria criteria, final int joinType) {
        this.referencedTableColumns = null;
        this.baseTableColumns = null;
        this.baseTableName = null;
        this.referencedTableName = null;
        this.referencedTableAlias = null;
        this.baseTableAlias = null;
        this.straightJoin = false;
        this.baseTableColumnIndices = null;
        this.referencedTableColumnIndices = null;
        this.baseTable = null;
        this.referencedTable = null;
        this.hashCode = -1;
        this.baseTableName = baseTableName;
        this.baseTableAlias = baseTableAlias;
        this.referencedTableName = referencedTableName;
        this.referencedTableAlias = referencedTableAlias;
        this.criteria = criteria;
        this.joinType = joinType;
        this.baseTable = ((this.baseTableAlias == null) ? new Table(this.baseTableName) : new Table(this.baseTableName, this.baseTableAlias));
        this.referencedTable = ((this.referencedTableAlias == null) ? new Table(this.referencedTableName) : new Table(this.referencedTableName, this.referencedTableAlias));
        this.validateJoin();
    }
    
    public Join(final String baseTableName, final String referencedTableName, final Criteria criteria, final int joinType) {
        this(baseTableName, baseTableName, referencedTableName, referencedTableName, criteria, joinType);
    }
    
    public Join(final String baseTableName, final String referencedTableName, final String[] baseTableColumns, final String[] referencedTableColumns, final int joinType) {
        this(baseTableName, referencedTableName, baseTableColumns, referencedTableColumns, baseTableName, referencedTableName, joinType);
    }
    
    public Join(final String baseTableName, final String referencedTableName, final String[] baseTableColumns, final String[] referencedTableColumns, final String baseTableAlias, final String referencedTableAlias, final int joinType) {
        this.referencedTableColumns = null;
        this.baseTableColumns = null;
        this.baseTableName = null;
        this.referencedTableName = null;
        this.referencedTableAlias = null;
        this.baseTableAlias = null;
        this.straightJoin = false;
        this.baseTableColumnIndices = null;
        this.referencedTableColumnIndices = null;
        this.baseTable = null;
        this.referencedTable = null;
        this.hashCode = -1;
        this.baseTableName = baseTableName;
        this.referencedTableName = referencedTableName;
        this.baseTableColumns = baseTableColumns;
        this.referencedTableColumns = referencedTableColumns;
        this.baseTableAlias = baseTableAlias;
        this.referencedTableAlias = referencedTableAlias;
        this.joinType = joinType;
        this.baseTable = ((this.baseTableAlias == null) ? new Table(this.baseTableName) : new Table(this.baseTableName, this.baseTableAlias));
        this.referencedTable = ((this.referencedTableAlias == null) ? new Table(this.referencedTableName) : new Table(this.referencedTableName, this.referencedTableAlias));
        this.validateJoin();
    }
    
    public Join(final Table baseTable, final Table referencedTable, final String[] baseTableColumns, final String[] referencedTableColumns, final int joinType) {
        this(baseTable.getTableName(), referencedTable.getTableName(), baseTableColumns, referencedTableColumns, baseTable.getTableAlias(), referencedTable.getTableAlias(), joinType);
        this.baseTable = baseTable;
        this.referencedTable = referencedTable;
    }
    
    public Join(final Table baseTable, final Table referencedTable, final Criteria ct, final int joinType) {
        this(baseTable.getTableName(), baseTable.getTableAlias(), referencedTable.getTableName(), referencedTable.getTableAlias(), ct, joinType);
        this.baseTable = baseTable;
        this.referencedTable = referencedTable;
    }
    
    public Table getBaseTable() {
        return this.baseTable;
    }
    
    public String[] getBaseTableColumns() {
        return this.baseTableColumns;
    }
    
    public String[] getReferencedTableColumns() {
        return this.referencedTableColumns;
    }
    
    public Table getReferencedTable() {
        return this.referencedTable;
    }
    
    public int getNumberOfColumns() {
        return this.baseTableColumns.length;
    }
    
    public String getBaseTableName() {
        return this.baseTableName;
    }
    
    public String getReferencedTableName() {
        return this.referencedTableName;
    }
    
    public String getBaseTableColumn(final int index) {
        return this.baseTableColumns[index];
    }
    
    public String getReferencedTableColumn(final int index) {
        return this.referencedTableColumns[index];
    }
    
    public int getJoinType() {
        return this.joinType;
    }
    
    public String getBaseTableAlias() {
        return this.baseTableAlias;
    }
    
    public String getReferencedTableAlias() {
        return this.referencedTableAlias;
    }
    
    public Criteria getCriteria() {
        return this.criteria;
    }
    
    public void setCriteria(final Criteria criteria) {
        this.criteria = criteria;
    }
    
    public int[] getBaseTableColumnIndices() {
        return this.baseTableColumnIndices;
    }
    
    public int[] getReferencedTableColumnIndices() {
        return this.referencedTableColumnIndices;
    }
    
    public void setBaseTableColumnIndices(final int[] baseTableColumnIndices) {
        this.baseTableColumnIndices = baseTableColumnIndices;
    }
    
    public void setReferencedTableColumnIndices(final int[] referencedTableColumnIndices) {
        this.referencedTableColumnIndices = referencedTableColumnIndices;
    }
    
    public void setOptimize(final boolean straightJoin) {
        this.straightJoin = straightJoin;
    }
    
    public boolean isOptimized() {
        return this.straightJoin;
    }
    
    public Object clone() {
        if (this.criteria != null) {
            return new Join(this.baseTableName, this.baseTableAlias, this.referencedTableName, this.referencedTableAlias, (Criteria)this.criteria.clone(), this.joinType);
        }
        return new Join(this.baseTableName, this.referencedTableName, this.cloneArr(this.baseTableColumns), this.cloneArr(this.referencedTableColumns), this.baseTableAlias, this.referencedTableAlias, this.joinType);
    }
    
    private String[] cloneArr(final String[] array) {
        final String[] newArray = new String[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }
    
    private void validateJoin() {
        if (this.baseTableName == null || this.baseTableAlias == null || this.referencedTableName == null || this.referencedTableAlias == null) {
            throw new IllegalArgumentException("TableNames and AliasNames cannot be null.");
        }
        if (this.criteria == null && (this.baseTableColumns == null || this.referencedTableColumns == null)) {
            throw new IllegalArgumentException("Either Criteria or basetable and referencetable columns should be valid");
        }
        if (this.joinType != 2 && this.joinType != 1) {
            throw new IllegalArgumentException("No other joinType other than LEFT JOIN or INNER JOIN is allowed.");
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("Join := ");
        String baseAlias = this.baseTableName;
        buf.append(this.baseTableName);
        if (this.baseTableAlias != null) {
            buf.append(" AS ").append(this.baseTableAlias);
            baseAlias = this.baseTableAlias;
        }
        if (this.joinType == 2) {
            buf.append(" INNER JOIN ");
        }
        else {
            buf.append(" LEFT JOIN ");
        }
        String referencedAlias = this.referencedTableName;
        buf.append(this.referencedTableName);
        if (this.referencedTableAlias != null) {
            buf.append(" AS ").append(this.referencedTableAlias);
            referencedAlias = this.referencedTableAlias;
        }
        buf.append(" ON ");
        if (this.criteria == null) {
            for (int i = 0; i < this.baseTableColumns.length; ++i) {
                if (i != 0) {
                    buf.append(" AND ");
                }
                buf.append(baseAlias).append(".").append(this.baseTableColumns[i]);
                buf.append("=");
                buf.append(referencedAlias).append(".").append(this.referencedTableColumns[i]);
            }
        }
        else {
            buf.append(this.criteria.toString());
        }
        return buf.toString();
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.hashCode(this.baseTableName) + this.hashCode(this.referencedTableName) + this.hashCode(this.referencedTableAlias) + this.hashCode(this.baseTableAlias) + this.hashCode(this.baseTableColumns) + this.hashCode(this.referencedTableColumns) + this.joinType + this.hashCode(this.criteria) + this.hashCode(this.baseTableColumnIndices) + this.hashCode(this.referencedTableColumnIndices);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj != null) ? obj.hashCode() : 0;
    }
    
    private int hashCode(final int[] values) {
        int hashCode = 0;
        if (values == null) {
            return 0;
        }
        for (int i = 0; i < values.length; ++i) {
            hashCode += values[i];
        }
        return hashCode;
    }
    
    private int hashCode(final String[] values) {
        int hashCode = 0;
        if (values == null) {
            return 0;
        }
        for (int i = 0; i < values.length; ++i) {
            hashCode += this.hashCode(values[i]);
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object obj) {
        Join passedJoin = null;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Join)) {
            return false;
        }
        passedJoin = (Join)obj;
        if (!passedJoin.getBaseTableName().equals(this.baseTableName) || !passedJoin.getBaseTableAlias().equals(this.baseTableAlias) || !passedJoin.getReferencedTableName().equals(this.referencedTableName) || !passedJoin.getReferencedTableAlias().equals(this.referencedTableAlias) || passedJoin.getJoinType() != this.joinType) {
            return false;
        }
        if (this.criteria != null || passedJoin.getCriteria() != null) {
            return passedJoin.getCriteria() != null && this.getCriteria() != null && passedJoin.getCriteria().equals(this.getCriteria());
        }
        final int size = this.getNumberOfColumns();
        if (this.getNumberOfColumns() == passedJoin.getNumberOfColumns()) {
            for (int i = 0; i < size; ++i) {
                if (!this.getBaseTableColumn(i).equals(passedJoin.getBaseTableColumn(i))) {
                    return false;
                }
                if (!this.getReferencedTableColumn(i).equals(passedJoin.getReferencedTableColumn(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        int size = 0;
        if (this.referencedTableColumns != null) {
            size = this.referencedTableColumns.length;
            out.writeInt(size);
            for (int i = 0; i < size; ++i) {
                out.writeUTF(this.referencedTableColumns[i]);
            }
        }
        else {
            out.writeInt(size);
        }
        size = 0;
        if (this.baseTableColumns != null) {
            size = this.baseTableColumns.length;
            out.writeInt(size);
            for (int i = 0; i < size; ++i) {
                out.writeUTF(this.baseTableColumns[i]);
            }
        }
        else {
            out.writeInt(size);
        }
        out.writeUTF(this.baseTableName);
        out.writeUTF(this.referencedTableName);
        out.writeUTF(this.referencedTableAlias);
        out.writeUTF(this.baseTableAlias);
        out.writeInt(this.joinType);
        if (this.criteria != null) {
            out.writeInt(1);
            this.criteria.writeExternal(out);
        }
        else {
            out.writeInt(0);
        }
        size = 0;
        if (this.baseTableColumnIndices != null) {
            size = this.baseTableColumnIndices.length;
            out.writeInt(size);
            for (int i = 0; i < size; ++i) {
                out.writeInt(this.baseTableColumnIndices[i]);
            }
        }
        else {
            out.writeInt(size);
        }
        size = 0;
        if (this.referencedTableColumnIndices != null) {
            size = this.referencedTableColumnIndices.length;
            out.writeInt(size);
            for (int i = 0; i < size; ++i) {
                out.writeInt(this.referencedTableColumnIndices[i]);
            }
        }
        else {
            out.writeInt(size);
        }
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        if (size != 0) {
            this.referencedTableColumns = new String[size];
            for (int i = 0; i < size; ++i) {
                this.referencedTableColumns[i] = in.readUTF();
            }
        }
        size = in.readInt();
        if (size != 0) {
            this.baseTableColumns = new String[size];
            for (int i = 0; i < size; ++i) {
                this.baseTableColumns[i] = in.readUTF();
            }
        }
        this.baseTableName = in.readUTF();
        this.referencedTableName = in.readUTF();
        this.referencedTableAlias = in.readUTF();
        this.baseTableAlias = in.readUTF();
        this.joinType = in.readInt();
        final int critInt = in.readInt();
        if (critInt == 1) {
            (this.criteria = new Criteria()).readExternal(in);
        }
        size = in.readInt();
        if (size != 0) {
            this.baseTableColumnIndices = new int[size];
            for (int j = 0; j < size; ++j) {
                this.baseTableColumnIndices[j] = in.readInt();
            }
        }
        size = in.readInt();
        if (size != 0) {
            this.referencedTableColumnIndices = new int[size];
            for (int j = 0; j < size; ++j) {
                this.referencedTableColumnIndices[j] = in.readInt();
            }
        }
    }
}
