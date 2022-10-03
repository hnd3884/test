package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.ArrayList;

public class LocalOrGlobalPartitionTable
{
    private boolean createTableLocalOrGlobalPartition;
    private String globalPartitionSyntax;
    private ArrayList columnList;
    private ArrayList partitionArrayList;
    private String localPartitionSyntax;
    private String localPartitionName;
    private ArrayList localPhysicalAttributesArrayList;
    
    public void setCreateTableLocalOrGlobalPartition(final boolean createTableLocalOrGlobalPartition) {
        this.createTableLocalOrGlobalPartition = createTableLocalOrGlobalPartition;
    }
    
    public void setGlobalPartitionSyntax(final String globalPartitionSyntax) {
        this.globalPartitionSyntax = globalPartitionSyntax;
    }
    
    public void setColumnList(final ArrayList columnList) {
        this.columnList = columnList;
    }
    
    public void setGlobalPartitionArrayList(final ArrayList partitionArrayList) {
        this.partitionArrayList = partitionArrayList;
    }
    
    public void setLocalPartitionSyntax(final String localPartitionSyntax) {
        this.localPartitionSyntax = localPartitionSyntax;
    }
    
    public void setLocalPartitionName(final String localPartitionName) {
        this.localPartitionName = localPartitionName;
    }
    
    public void setLocalPartitionArrayList(final ArrayList localPhysicalAttributesArrayList) {
        this.localPhysicalAttributesArrayList = localPhysicalAttributesArrayList;
    }
    
    public boolean getCreateTableLocalOrGlobalPartition() {
        return this.createTableLocalOrGlobalPartition;
    }
    
    public String getGlobalPartitionSyntax() {
        return this.globalPartitionSyntax;
    }
    
    public ArrayList getColumnList() {
        return this.columnList;
    }
    
    public ArrayList getGlobalPartitionArrayList() {
        return this.partitionArrayList;
    }
    
    public String getLocalPartitionSyntax() {
        return this.localPartitionSyntax;
    }
    
    public String getLocalPartitionName() {
        return this.localPartitionName;
    }
    
    public ArrayList getLocalPartitionArrayList() {
        return this.localPhysicalAttributesArrayList;
    }
    
    public LocalOrGlobalPartitionTable toANSI() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toDB2() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toInformix() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toMSSQLServer() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toSybase() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toMySQL() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toOracle() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        if (partition.getCreateTableLocalOrGlobalPartition()) {
            partition.getCreateTableLocalOrGlobalPartition();
        }
        if (partition.getGlobalPartitionSyntax() != null) {
            partition.getGlobalPartitionSyntax();
        }
        if (partition.getGlobalPartitionArrayList() != null) {
            final ArrayList tempPartitionArrayList = partition.getGlobalPartitionArrayList();
            final ArrayList oraclePartitionListAttributes = new ArrayList();
            for (int i = 0; i < tempPartitionArrayList.size(); ++i) {
                final PartitionListAttributes partitionListAttributes = tempPartitionArrayList.get(i);
                final PartitionListAttributes oraclePartitionListAttributesClause = partitionListAttributes.toOracle();
                oraclePartitionListAttributes.add(oraclePartitionListAttributesClause);
            }
            partition.setGlobalPartitionArrayList(oraclePartitionListAttributes);
        }
        if (partition.getColumnList() != null) {
            final ArrayList oracleColumnList = new ArrayList();
            final ArrayList tempColumnList = partition.getColumnList();
            for (int i = 0; i < tempColumnList.size(); ++i) {
                final SelectColumn selectColumn = tempColumnList.get(i);
                final SelectColumn tempSelectColumn = selectColumn.toOracleSelect(null, null);
                oracleColumnList.add(tempSelectColumn);
            }
            partition.setColumnList(oracleColumnList);
        }
        if (partition.getLocalPartitionName() != null) {
            final String tempLocalPartitionName = partition.getLocalPartitionName();
            partition.setLocalPartitionName(tempLocalPartitionName);
        }
        if (partition.getLocalPartitionSyntax() != null) {
            partition.getLocalPartitionSyntax();
        }
        if (partition.getLocalPartitionArrayList() != null) {
            final ArrayList tempLocalPhysicalAttributesArrayList = partition.getLocalPartitionArrayList();
            final ArrayList oracleLocalPhysicalAttributesArrayList = new ArrayList();
            for (int i = 0; i < tempLocalPhysicalAttributesArrayList.size(); ++i) {
                final PhysicalAttributesClause physicalAttributesClause = tempLocalPhysicalAttributesArrayList.get(i);
                final PhysicalAttributesClause oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
                oracleLocalPhysicalAttributesArrayList.add(oraclePhysicalAttributesClause);
            }
            partition.setLocalPartitionArrayList(oracleLocalPhysicalAttributesArrayList);
        }
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toPostgreSQL() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toNetezza() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public LocalOrGlobalPartitionTable toTeradata() throws ConvertException {
        final LocalOrGlobalPartitionTable partition = this.copyObjectValues();
        partition.setGlobalPartitionSyntax(null);
        partition.setCreateTableLocalOrGlobalPartition(false);
        partition.setColumnList(null);
        partition.setLocalPartitionName(null);
        partition.setGlobalPartitionArrayList(null);
        partition.setLocalPartitionSyntax(null);
        partition.setLocalPartitionArrayList(null);
        return partition;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public LocalOrGlobalPartitionTable copyObjectValues() {
        final LocalOrGlobalPartitionTable dupLocalOrGlobalPartitionTable = new LocalOrGlobalPartitionTable();
        dupLocalOrGlobalPartitionTable.setGlobalPartitionSyntax(this.globalPartitionSyntax);
        dupLocalOrGlobalPartitionTable.setCreateTableLocalOrGlobalPartition(this.createTableLocalOrGlobalPartition);
        dupLocalOrGlobalPartitionTable.setColumnList(this.columnList);
        dupLocalOrGlobalPartitionTable.setGlobalPartitionArrayList(this.partitionArrayList);
        dupLocalOrGlobalPartitionTable.setLocalPartitionSyntax(this.localPartitionSyntax);
        dupLocalOrGlobalPartitionTable.setLocalPartitionName(this.localPartitionName);
        dupLocalOrGlobalPartitionTable.setLocalPartitionArrayList(this.localPhysicalAttributesArrayList);
        if (this.globalPartitionSyntax != null) {
            dupLocalOrGlobalPartitionTable.getGlobalPartitionSyntax();
        }
        if (this.partitionArrayList != null) {
            dupLocalOrGlobalPartitionTable.getGlobalPartitionArrayList();
        }
        if (this.localPartitionSyntax != null) {
            dupLocalOrGlobalPartitionTable.getLocalPartitionSyntax();
        }
        if (this.localPhysicalAttributesArrayList != null) {
            dupLocalOrGlobalPartitionTable.getLocalPartitionArrayList();
        }
        return dupLocalOrGlobalPartitionTable;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.createTableLocalOrGlobalPartition) {
            sb.append("\t");
        }
        if (this.globalPartitionSyntax != null) {
            sb.append(this.globalPartitionSyntax.toUpperCase());
        }
        if (this.columnList != null) {
            sb.append("( ");
            final int size = this.columnList.size();
            for (int i = 0; i < this.columnList.size(); ++i) {
                final SelectColumn selectColumn = this.columnList.get(i);
                sb.append(selectColumn);
                if (i < size - 1) {
                    sb.append(",");
                }
            }
            sb.append(")  \n");
        }
        if (this.partitionArrayList != null) {
            if (this.createTableLocalOrGlobalPartition) {
                sb.append("\t");
            }
            sb.append("(\n");
            final int size = this.partitionArrayList.size();
            for (int i = 0; i < this.partitionArrayList.size(); ++i) {
                final PartitionListAttributes partitionListAttributes = this.partitionArrayList.get(i);
                if (this.createTableLocalOrGlobalPartition) {
                    sb.append("\t");
                }
                sb.append("\t" + partitionListAttributes.toString() + " ");
                if (i < size - 1) {
                    sb.append(",\n");
                }
            }
            if (this.createTableLocalOrGlobalPartition) {
                sb.append("\n\t ) \n");
            }
            else {
                sb.append("\n)\n");
            }
        }
        if (this.localPartitionSyntax != null) {
            if (this.createTableLocalOrGlobalPartition) {
                sb.append("\t");
            }
            sb.append(this.localPartitionSyntax.toUpperCase() + " ");
        }
        if (this.localPartitionName != null) {
            sb.append(this.localPartitionName + "\n\t");
        }
        if (this.localPhysicalAttributesArrayList != null) {
            for (int j = 0; j < this.localPhysicalAttributesArrayList.size(); ++j) {
                final PhysicalAttributesClause physicalAttributesClause = this.localPhysicalAttributesArrayList.get(j);
                if (this.createTableLocalOrGlobalPartition) {
                    sb.append("\t");
                }
                sb.append(physicalAttributesClause.toString() + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
