package net.sf.jsqlparser.statement.create.table;

import java.util.Iterator;
import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.ArrayList;
import java.util.List;

public class ColDataType
{
    private String dataType;
    private List<String> argumentsStringList;
    private String characterSet;
    private List<Integer> arrayData;
    
    public ColDataType() {
        this.arrayData = new ArrayList<Integer>();
    }
    
    public List<String> getArgumentsStringList() {
        return this.argumentsStringList;
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    public void setArgumentsStringList(final List<String> list) {
        this.argumentsStringList = list;
    }
    
    public void setDataType(final String string) {
        this.dataType = string;
    }
    
    public String getCharacterSet() {
        return this.characterSet;
    }
    
    public void setCharacterSet(final String characterSet) {
        this.characterSet = characterSet;
    }
    
    public List<Integer> getArrayData() {
        return this.arrayData;
    }
    
    public void setArrayData(final List<Integer> arrayData) {
        this.arrayData = arrayData;
    }
    
    @Override
    public String toString() {
        final StringBuilder arraySpec = new StringBuilder();
        for (final Integer item : this.arrayData) {
            arraySpec.append("[");
            if (item != null) {
                arraySpec.append(item);
            }
            arraySpec.append("]");
        }
        return this.dataType + ((this.argumentsStringList != null) ? (" " + PlainSelect.getStringList(this.argumentsStringList, true, true)) : "") + arraySpec.toString() + ((this.characterSet != null) ? (" CHARACTER SET " + this.characterSet) : "");
    }
}
