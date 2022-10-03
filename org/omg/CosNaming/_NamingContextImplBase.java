package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import java.util.Dictionary;
import org.omg.CORBA.DynamicImplementation;

public abstract class _NamingContextImplBase extends DynamicImplementation implements NamingContext
{
    private static final String[] _type_ids;
    private static Dictionary _methods;
    
    @Override
    public String[] _ids() {
        return _NamingContextImplBase._type_ids.clone();
    }
    
    @Override
    public void invoke(final ServerRequest serverRequest) {
        switch (_NamingContextImplBase._methods.get(serverRequest.op_name())) {
            case 0: {
                final NVList create_list = this._orb().create_list(0);
                final Any create_any = this._orb().create_any();
                create_any.type(NameHelper.type());
                create_list.add_value("n", create_any, 1);
                final Any create_any2 = this._orb().create_any();
                create_any2.type(ORB.init().get_primitive_tc(TCKind.tk_objref));
                create_list.add_value("obj", create_any2, 1);
                serverRequest.params(create_list);
                final NameComponent[] extract = NameHelper.extract(create_any);
                final org.omg.CORBA.Object extract_Object = create_any2.extract_Object();
                try {
                    this.bind(extract, extract_Object);
                }
                catch (final NotFound notFound) {
                    final Any create_any3 = this._orb().create_any();
                    NotFoundHelper.insert(create_any3, notFound);
                    serverRequest.except(create_any3);
                    return;
                }
                catch (final CannotProceed cannotProceed) {
                    final Any create_any4 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any4, cannotProceed);
                    serverRequest.except(create_any4);
                    return;
                }
                catch (final InvalidName invalidName) {
                    final Any create_any5 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any5, invalidName);
                    serverRequest.except(create_any5);
                    return;
                }
                catch (final AlreadyBound alreadyBound) {
                    final Any create_any6 = this._orb().create_any();
                    AlreadyBoundHelper.insert(create_any6, alreadyBound);
                    serverRequest.except(create_any6);
                    return;
                }
                final Any create_any7 = this._orb().create_any();
                create_any7.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any7);
                break;
            }
            case 1: {
                final NVList create_list2 = this._orb().create_list(0);
                final Any create_any8 = this._orb().create_any();
                create_any8.type(NameHelper.type());
                create_list2.add_value("n", create_any8, 1);
                final Any create_any9 = this._orb().create_any();
                create_any9.type(NamingContextHelper.type());
                create_list2.add_value("nc", create_any9, 1);
                serverRequest.params(create_list2);
                final NameComponent[] extract2 = NameHelper.extract(create_any8);
                final NamingContext extract3 = NamingContextHelper.extract(create_any9);
                try {
                    this.bind_context(extract2, extract3);
                }
                catch (final NotFound notFound2) {
                    final Any create_any10 = this._orb().create_any();
                    NotFoundHelper.insert(create_any10, notFound2);
                    serverRequest.except(create_any10);
                    return;
                }
                catch (final CannotProceed cannotProceed2) {
                    final Any create_any11 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any11, cannotProceed2);
                    serverRequest.except(create_any11);
                    return;
                }
                catch (final InvalidName invalidName2) {
                    final Any create_any12 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any12, invalidName2);
                    serverRequest.except(create_any12);
                    return;
                }
                catch (final AlreadyBound alreadyBound2) {
                    final Any create_any13 = this._orb().create_any();
                    AlreadyBoundHelper.insert(create_any13, alreadyBound2);
                    serverRequest.except(create_any13);
                    return;
                }
                final Any create_any14 = this._orb().create_any();
                create_any14.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any14);
                break;
            }
            case 2: {
                final NVList create_list3 = this._orb().create_list(0);
                final Any create_any15 = this._orb().create_any();
                create_any15.type(NameHelper.type());
                create_list3.add_value("n", create_any15, 1);
                final Any create_any16 = this._orb().create_any();
                create_any16.type(ORB.init().get_primitive_tc(TCKind.tk_objref));
                create_list3.add_value("obj", create_any16, 1);
                serverRequest.params(create_list3);
                final NameComponent[] extract4 = NameHelper.extract(create_any15);
                final org.omg.CORBA.Object extract_Object2 = create_any16.extract_Object();
                try {
                    this.rebind(extract4, extract_Object2);
                }
                catch (final NotFound notFound3) {
                    final Any create_any17 = this._orb().create_any();
                    NotFoundHelper.insert(create_any17, notFound3);
                    serverRequest.except(create_any17);
                    return;
                }
                catch (final CannotProceed cannotProceed3) {
                    final Any create_any18 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any18, cannotProceed3);
                    serverRequest.except(create_any18);
                    return;
                }
                catch (final InvalidName invalidName3) {
                    final Any create_any19 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any19, invalidName3);
                    serverRequest.except(create_any19);
                    return;
                }
                final Any create_any20 = this._orb().create_any();
                create_any20.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any20);
                break;
            }
            case 3: {
                final NVList create_list4 = this._orb().create_list(0);
                final Any create_any21 = this._orb().create_any();
                create_any21.type(NameHelper.type());
                create_list4.add_value("n", create_any21, 1);
                final Any create_any22 = this._orb().create_any();
                create_any22.type(NamingContextHelper.type());
                create_list4.add_value("nc", create_any22, 1);
                serverRequest.params(create_list4);
                final NameComponent[] extract5 = NameHelper.extract(create_any21);
                final NamingContext extract6 = NamingContextHelper.extract(create_any22);
                try {
                    this.rebind_context(extract5, extract6);
                }
                catch (final NotFound notFound4) {
                    final Any create_any23 = this._orb().create_any();
                    NotFoundHelper.insert(create_any23, notFound4);
                    serverRequest.except(create_any23);
                    return;
                }
                catch (final CannotProceed cannotProceed4) {
                    final Any create_any24 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any24, cannotProceed4);
                    serverRequest.except(create_any24);
                    return;
                }
                catch (final InvalidName invalidName4) {
                    final Any create_any25 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any25, invalidName4);
                    serverRequest.except(create_any25);
                    return;
                }
                final Any create_any26 = this._orb().create_any();
                create_any26.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any26);
                break;
            }
            case 4: {
                final NVList create_list5 = this._orb().create_list(0);
                final Any create_any27 = this._orb().create_any();
                create_any27.type(NameHelper.type());
                create_list5.add_value("n", create_any27, 1);
                serverRequest.params(create_list5);
                final NameComponent[] extract7 = NameHelper.extract(create_any27);
                org.omg.CORBA.Object resolve;
                try {
                    resolve = this.resolve(extract7);
                }
                catch (final NotFound notFound5) {
                    final Any create_any28 = this._orb().create_any();
                    NotFoundHelper.insert(create_any28, notFound5);
                    serverRequest.except(create_any28);
                    return;
                }
                catch (final CannotProceed cannotProceed5) {
                    final Any create_any29 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any29, cannotProceed5);
                    serverRequest.except(create_any29);
                    return;
                }
                catch (final InvalidName invalidName5) {
                    final Any create_any30 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any30, invalidName5);
                    serverRequest.except(create_any30);
                    return;
                }
                final Any create_any31 = this._orb().create_any();
                create_any31.insert_Object(resolve);
                serverRequest.result(create_any31);
                break;
            }
            case 5: {
                final NVList create_list6 = this._orb().create_list(0);
                final Any create_any32 = this._orb().create_any();
                create_any32.type(NameHelper.type());
                create_list6.add_value("n", create_any32, 1);
                serverRequest.params(create_list6);
                final NameComponent[] extract8 = NameHelper.extract(create_any32);
                try {
                    this.unbind(extract8);
                }
                catch (final NotFound notFound6) {
                    final Any create_any33 = this._orb().create_any();
                    NotFoundHelper.insert(create_any33, notFound6);
                    serverRequest.except(create_any33);
                    return;
                }
                catch (final CannotProceed cannotProceed6) {
                    final Any create_any34 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any34, cannotProceed6);
                    serverRequest.except(create_any34);
                    return;
                }
                catch (final InvalidName invalidName6) {
                    final Any create_any35 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any35, invalidName6);
                    serverRequest.except(create_any35);
                    return;
                }
                final Any create_any36 = this._orb().create_any();
                create_any36.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any36);
                break;
            }
            case 6: {
                final NVList create_list7 = this._orb().create_list(0);
                final Any create_any37 = this._orb().create_any();
                create_any37.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
                create_list7.add_value("how_many", create_any37, 1);
                final Any create_any38 = this._orb().create_any();
                create_any38.type(BindingListHelper.type());
                create_list7.add_value("bl", create_any38, 2);
                final Any create_any39 = this._orb().create_any();
                create_any39.type(BindingIteratorHelper.type());
                create_list7.add_value("bi", create_any39, 2);
                serverRequest.params(create_list7);
                final int extract_ulong = create_any37.extract_ulong();
                final BindingListHolder bindingListHolder = new BindingListHolder();
                final BindingIteratorHolder bindingIteratorHolder = new BindingIteratorHolder();
                this.list(extract_ulong, bindingListHolder, bindingIteratorHolder);
                BindingListHelper.insert(create_any38, bindingListHolder.value);
                BindingIteratorHelper.insert(create_any39, bindingIteratorHolder.value);
                final Any create_any40 = this._orb().create_any();
                create_any40.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any40);
                break;
            }
            case 7: {
                serverRequest.params(this._orb().create_list(0));
                final NamingContext new_context = this.new_context();
                final Any create_any41 = this._orb().create_any();
                NamingContextHelper.insert(create_any41, new_context);
                serverRequest.result(create_any41);
                break;
            }
            case 8: {
                final NVList create_list8 = this._orb().create_list(0);
                final Any create_any42 = this._orb().create_any();
                create_any42.type(NameHelper.type());
                create_list8.add_value("n", create_any42, 1);
                serverRequest.params(create_list8);
                final NameComponent[] extract9 = NameHelper.extract(create_any42);
                NamingContext bind_new_context;
                try {
                    bind_new_context = this.bind_new_context(extract9);
                }
                catch (final NotFound notFound7) {
                    final Any create_any43 = this._orb().create_any();
                    NotFoundHelper.insert(create_any43, notFound7);
                    serverRequest.except(create_any43);
                    return;
                }
                catch (final AlreadyBound alreadyBound3) {
                    final Any create_any44 = this._orb().create_any();
                    AlreadyBoundHelper.insert(create_any44, alreadyBound3);
                    serverRequest.except(create_any44);
                    return;
                }
                catch (final CannotProceed cannotProceed7) {
                    final Any create_any45 = this._orb().create_any();
                    CannotProceedHelper.insert(create_any45, cannotProceed7);
                    serverRequest.except(create_any45);
                    return;
                }
                catch (final InvalidName invalidName7) {
                    final Any create_any46 = this._orb().create_any();
                    InvalidNameHelper.insert(create_any46, invalidName7);
                    serverRequest.except(create_any46);
                    return;
                }
                final Any create_any47 = this._orb().create_any();
                NamingContextHelper.insert(create_any47, bind_new_context);
                serverRequest.result(create_any47);
                break;
            }
            case 9: {
                serverRequest.params(this._orb().create_list(0));
                try {
                    this.destroy();
                }
                catch (final NotEmpty notEmpty) {
                    final Any create_any48 = this._orb().create_any();
                    NotEmptyHelper.insert(create_any48, notEmpty);
                    serverRequest.except(create_any48);
                    return;
                }
                final Any create_any49 = this._orb().create_any();
                create_any49.type(this._orb().get_primitive_tc(TCKind.tk_void));
                serverRequest.result(create_any49);
                break;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
    }
    
    static {
        _type_ids = new String[] { "IDL:omg.org/CosNaming/NamingContext:1.0" };
        (_NamingContextImplBase._methods = new Hashtable()).put("bind", new Integer(0));
        _NamingContextImplBase._methods.put("bind_context", new Integer(1));
        _NamingContextImplBase._methods.put("rebind", new Integer(2));
        _NamingContextImplBase._methods.put("rebind_context", new Integer(3));
        _NamingContextImplBase._methods.put("resolve", new Integer(4));
        _NamingContextImplBase._methods.put("unbind", new Integer(5));
        _NamingContextImplBase._methods.put("list", new Integer(6));
        _NamingContextImplBase._methods.put("new_context", new Integer(7));
        _NamingContextImplBase._methods.put("bind_new_context", new Integer(8));
        _NamingContextImplBase._methods.put("destroy", new Integer(9));
    }
}
