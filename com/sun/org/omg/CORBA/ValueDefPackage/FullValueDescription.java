package com.sun.org.omg.CORBA.ValueDefPackage;

import org.omg.CORBA.TypeCode;
import com.sun.org.omg.CORBA.Initializer;
import org.omg.CORBA.ValueMember;
import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.OperationDescription;
import org.omg.CORBA.portable.IDLEntity;

public final class FullValueDescription implements IDLEntity
{
    public String name;
    public String id;
    public boolean is_abstract;
    public boolean is_custom;
    public String defined_in;
    public String version;
    public OperationDescription[] operations;
    public AttributeDescription[] attributes;
    public ValueMember[] members;
    public Initializer[] initializers;
    public String[] supported_interfaces;
    public String[] abstract_base_values;
    public boolean is_truncatable;
    public String base_value;
    public TypeCode type;
    
    public FullValueDescription() {
        this.name = null;
        this.id = null;
        this.is_abstract = false;
        this.is_custom = false;
        this.defined_in = null;
        this.version = null;
        this.operations = null;
        this.attributes = null;
        this.members = null;
        this.initializers = null;
        this.supported_interfaces = null;
        this.abstract_base_values = null;
        this.is_truncatable = false;
        this.base_value = null;
        this.type = null;
    }
    
    public FullValueDescription(final String name, final String id, final boolean is_abstract, final boolean is_custom, final String defined_in, final String version, final OperationDescription[] operations, final AttributeDescription[] attributes, final ValueMember[] members, final Initializer[] initializers, final String[] supported_interfaces, final String[] abstract_base_values, final boolean is_truncatable, final String base_value, final TypeCode type) {
        this.name = null;
        this.id = null;
        this.is_abstract = false;
        this.is_custom = false;
        this.defined_in = null;
        this.version = null;
        this.operations = null;
        this.attributes = null;
        this.members = null;
        this.initializers = null;
        this.supported_interfaces = null;
        this.abstract_base_values = null;
        this.is_truncatable = false;
        this.base_value = null;
        this.type = null;
        this.name = name;
        this.id = id;
        this.is_abstract = is_abstract;
        this.is_custom = is_custom;
        this.defined_in = defined_in;
        this.version = version;
        this.operations = operations;
        this.attributes = attributes;
        this.members = members;
        this.initializers = initializers;
        this.supported_interfaces = supported_interfaces;
        this.abstract_base_values = abstract_base_values;
        this.is_truncatable = is_truncatable;
        this.base_value = base_value;
        this.type = type;
    }
}
