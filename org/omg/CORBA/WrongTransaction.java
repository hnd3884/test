package org.omg.CORBA;

public final class WrongTransaction extends UserException
{
    public WrongTransaction() {
        super(WrongTransactionHelper.id());
    }
    
    public WrongTransaction(final String s) {
        super(WrongTransactionHelper.id() + "  " + s);
    }
}
