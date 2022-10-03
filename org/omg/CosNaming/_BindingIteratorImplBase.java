package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import java.util.Dictionary;
import org.omg.CORBA.DynamicImplementation;

public abstract class _BindingIteratorImplBase extends DynamicImplementation implements BindingIterator
{
    private static final String[] _type_ids;
    private static Dictionary _methods;
    
    @Override
    public String[] _ids() {
        return _BindingIteratorImplBase._type_ids.clone();
    }
    
    @Override
    public void invoke(final ServerRequest serverRequest) {
        switch (_BindingIteratorImplBase._methods.get(serverRequest.op_name())) {
            case 0: {
                final NVList create_list = this._orb().create_list(0);
                final Any create_any = this._orb().create_any();
                create_any.type(BindingHelper.type());
                create_list.add_value("b", create_any, 2);
                serverRequest.params(create_list);
                final BindingHolder bindingHolder = new BindingHolder();
                final boolean next_one = this.next_one(bindingHolder);
                BindingHelper.insert(create_any, bindingHolder.value);
                final Any create_any2 = this._orb().create_any();
                create_any2.insert_boolean(next_one);
                serverRequest.result(create_any2);
                break;
            }
            case 1: {
                final NVList create_list2 = this._orb().create_list(0);
                final Any create_any3 = this._orb().create_any();
                create_any3.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
                create_list2.add_value("how_many", create_any3, 1);
                final Any create_any4 = this._orb().create_any();
                create_any4.type(BindingListHelper.type());
                create_list2.add_value("bl", create_any4, 2);
                serverRequest.params(create_list2);
                final int extract_ulong = create_any3.extract_ulong();
                final BindingListHolder bindingListHolder = new BindingListHolder();
                final boolean next_n = this.next_n(extract_ulong, bindingListHolder);
                BindingListHelper.insert(create_any4, bindingListHolder.value);
                final Any create_any5 = this._orb().create_any();
                create_any5.insert_boolean(next_n);
                serverRequest.result(create_any5);
                break;
            }
            case 2: {
                serverRequest.params(this._orb().create_list(0));
                this.destroy();
                final Any create_any6 = this._orb().create_any();
                create_any6.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any6);
                break;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
    }
    
    static {
        _type_ids = new String[] { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
        (_BindingIteratorImplBase._methods = new Hashtable()).put("next_one", new Integer(0));
        _BindingIteratorImplBase._methods.put("next_n", new Integer(1));
        _BindingIteratorImplBase._methods.put("destroy", new Integer(2));
    }
}
