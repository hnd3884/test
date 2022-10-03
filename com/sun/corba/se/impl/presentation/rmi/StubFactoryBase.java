package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public abstract class StubFactoryBase implements PresentationManager.StubFactory
{
    private String[] typeIds;
    protected final PresentationManager.ClassData classData;
    
    protected StubFactoryBase(final PresentationManager.ClassData classData) {
        this.typeIds = null;
        this.classData = classData;
    }
    
    @Override
    public synchronized String[] getTypeIds() {
        if (this.typeIds == null) {
            if (this.classData == null) {
                this.typeIds = StubAdapter.getTypeIds(this.makeStub());
            }
            else {
                this.typeIds = this.classData.getTypeIds();
            }
        }
        return this.typeIds;
    }
}
