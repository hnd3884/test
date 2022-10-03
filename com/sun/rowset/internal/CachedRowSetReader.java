package com.sun.rowset.internal;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.DriverManager;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.RowSetInternal;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.Serializable;
import javax.sql.RowSetReader;

public class CachedRowSetReader implements RowSetReader, Serializable
{
    private int writerCalls;
    private boolean userCon;
    private int startPosition;
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = 5049738185801363801L;
    
    public CachedRowSetReader() {
        this.writerCalls = 0;
        this.userCon = false;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void readData(final RowSetInternal rowSetInternal) throws SQLException {
        Connection connect = null;
        try {
            final CachedRowSet set = (CachedRowSet)rowSetInternal;
            if (set.getPageSize() == 0 && set.size() > 0) {
                set.close();
            }
            this.writerCalls = 0;
            this.userCon = false;
            connect = this.connect(rowSetInternal);
            if (connect == null || set.getCommand() == null) {
                throw new SQLException(this.resBundle.handleGetObject("crsreader.connecterr").toString());
            }
            try {
                connect.setTransactionIsolation(set.getTransactionIsolation());
            }
            catch (final Exception ex) {}
            PreparedStatement preparedStatement = connect.prepareStatement(set.getCommand());
            this.decodeParams(rowSetInternal.getParams(), preparedStatement);
            try {
                preparedStatement.setMaxRows(set.getMaxRows());
                preparedStatement.setMaxFieldSize(set.getMaxFieldSize());
                preparedStatement.setEscapeProcessing(set.getEscapeProcessing());
                preparedStatement.setQueryTimeout(set.getQueryTimeout());
            }
            catch (final Exception ex2) {
                throw new SQLException(ex2.getMessage());
            }
            if (set.getCommand().toLowerCase().indexOf("select") != -1) {
                ResultSet set2 = preparedStatement.executeQuery();
                if (set.getPageSize() == 0) {
                    set.populate(set2);
                }
                else {
                    preparedStatement = connect.prepareStatement(set.getCommand(), 1004, 1008);
                    this.decodeParams(rowSetInternal.getParams(), preparedStatement);
                    try {
                        preparedStatement.setMaxRows(set.getMaxRows());
                        preparedStatement.setMaxFieldSize(set.getMaxFieldSize());
                        preparedStatement.setEscapeProcessing(set.getEscapeProcessing());
                        preparedStatement.setQueryTimeout(set.getQueryTimeout());
                    }
                    catch (final Exception ex3) {
                        throw new SQLException(ex3.getMessage());
                    }
                    set2 = preparedStatement.executeQuery();
                    set.populate(set2, this.startPosition);
                }
                set2.close();
            }
            else {
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            try {
                connect.commit();
            }
            catch (final SQLException ex4) {}
            if (this.getCloseConnection()) {
                connect.close();
            }
        }
        catch (final SQLException ex5) {
            throw ex5;
        }
        finally {
            try {
                if (connect != null && this.getCloseConnection()) {
                    try {
                        if (!connect.getAutoCommit()) {
                            connect.rollback();
                        }
                    }
                    catch (final Exception ex6) {}
                    connect.close();
                }
            }
            catch (final SQLException ex7) {}
        }
    }
    
    public boolean reset() throws SQLException {
        ++this.writerCalls;
        return this.writerCalls == 1;
    }
    
    public Connection connect(final RowSetInternal rowSetInternal) throws SQLException {
        if (rowSetInternal.getConnection() != null) {
            this.userCon = true;
            return rowSetInternal.getConnection();
        }
        if (((RowSet)rowSetInternal).getDataSourceName() != null) {
            try {
                final DataSource dataSource = (DataSource)new InitialContext().lookup(((RowSet)rowSetInternal).getDataSourceName());
                if (((RowSet)rowSetInternal).getUsername() != null) {
                    return dataSource.getConnection(((RowSet)rowSetInternal).getUsername(), ((RowSet)rowSetInternal).getPassword());
                }
                return dataSource.getConnection();
            }
            catch (final NamingException ex) {
                final SQLException ex2 = new SQLException(this.resBundle.handleGetObject("crsreader.connect").toString());
                ex2.initCause(ex);
                throw ex2;
            }
        }
        if (((RowSet)rowSetInternal).getUrl() != null) {
            return DriverManager.getConnection(((RowSet)rowSetInternal).getUrl(), ((RowSet)rowSetInternal).getUsername(), ((RowSet)rowSetInternal).getPassword());
        }
        return null;
    }
    
    private void decodeParams(final Object[] array, final PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof Object[]) {
                final Object[] array2 = (Object[])array[i];
                if (array2.length == 2) {
                    if (array2[0] == null) {
                        preparedStatement.setNull(i + 1, (int)array2[1]);
                    }
                    else if (array2[0] instanceof Date || array2[0] instanceof Time || array2[0] instanceof Timestamp) {
                        System.err.println(this.resBundle.handleGetObject("crsreader.datedetected").toString());
                        if (!(array2[1] instanceof Calendar)) {
                            throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
                        }
                        System.err.println(this.resBundle.handleGetObject("crsreader.caldetected").toString());
                        preparedStatement.setDate(i + 1, (Date)array2[0], (Calendar)array2[1]);
                    }
                    else if (array2[0] instanceof Reader) {
                        preparedStatement.setCharacterStream(i + 1, (Reader)array2[0], (int)array2[1]);
                    }
                    else if (array2[1] instanceof Integer) {
                        preparedStatement.setObject(i + 1, array2[0], (int)array2[1]);
                    }
                }
                else if (array2.length == 3) {
                    if (array2[0] == null) {
                        preparedStatement.setNull(i + 1, (int)array2[1], (String)array2[2]);
                    }
                    else {
                        if (array2[0] instanceof InputStream) {
                            switch ((int)array2[2]) {
                                case 0: {
                                    preparedStatement.setUnicodeStream(i + 1, (InputStream)array2[0], (int)array2[1]);
                                    break;
                                }
                                case 1: {
                                    preparedStatement.setBinaryStream(i + 1, (InputStream)array2[0], (int)array2[1]);
                                    break;
                                }
                                case 2: {
                                    preparedStatement.setAsciiStream(i + 1, (InputStream)array2[0], (int)array2[1]);
                                    break;
                                }
                                default: {
                                    throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
                                }
                            }
                        }
                        if (!(array2[1] instanceof Integer) || !(array2[2] instanceof Integer)) {
                            throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
                        }
                        preparedStatement.setObject(i + 1, array2[0], (int)array2[1], (int)array2[2]);
                    }
                }
                else {
                    preparedStatement.setObject(i + 1, array[i]);
                }
            }
            else {
                preparedStatement.setObject(i + 1, array[i]);
            }
        }
    }
    
    protected boolean getCloseConnection() {
        return !this.userCon;
    }
    
    public void setStartPosition(final int startPosition) {
        this.startPosition = startPosition;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
