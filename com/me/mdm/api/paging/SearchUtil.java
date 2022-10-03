package com.me.mdm.api.paging;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.me.mdm.api.paging.annotations.BaseAPICustomerIDParam;
import com.me.mdm.api.paging.annotations.BaseAPILoginUserIDparam;
import com.me.mdm.api.paging.annotations.AllCustomerSearchParam;
import com.me.mdm.api.paging.annotations.SearchParams;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.lang.annotation.Annotation;
import com.me.mdm.api.paging.annotations.SearchParam;
import com.adventnet.ds.query.Criteria;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.List;

public class SearchUtil
{
    private static List<Field> getFields(final Class<?> clazz) {
        final List<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
        final Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getFields(clazz.getSuperclass()));
        }
        return fields;
    }
    
    public static Criteria setSearchCriteria(final Object valueObject) throws Exception {
        Criteria criteria = null;
        Long userID = null;
        Long customerID = null;
        Object allCustVal = null;
        Field allCustField = null;
        final List<Field> fields = getFields(valueObject.getClass());
        for (final Field field : fields) {
            field.setAccessible(true);
            final Object value;
            if ((value = field.get(valueObject)) != null) {
                if (field.isAnnotationPresent(SearchParam.class)) {
                    criteria = MDMDBUtil.andCriteria(criteria, getCriteriaForSearchParam(value, field));
                }
                if (field.isAnnotationPresent(SearchParams.class)) {
                    criteria = MDMDBUtil.andCriteria(criteria, getCriteriaForSearchParams(value, field));
                }
                if (field.isAnnotationPresent(AllCustomerSearchParam.class)) {
                    allCustVal = value;
                    allCustField = field;
                }
                if (field.isAnnotationPresent(BaseAPILoginUserIDparam.class)) {
                    userID = (Long)value;
                }
                if (!field.isAnnotationPresent(BaseAPICustomerIDParam.class)) {
                    continue;
                }
                customerID = (Long)value;
            }
        }
        if (customerID != null || (allCustVal != null && allCustField != null)) {
            final Criteria customerCri = setCustomerFilter(userID, customerID, allCustVal, allCustField);
            criteria = MDMDBUtil.andCriteria(criteria, customerCri);
        }
        return criteria;
    }
    
    private static Criteria getCriteriaForSearchParam(final Object value, final Field field) {
        final SearchParam searchParam = field.getAnnotation(SearchParam.class);
        final Criteria criteria = new Criteria(Column.getColumn(searchParam.tableName(), searchParam.columnName()), value, searchParam.comparator(), false);
        return criteria;
    }
    
    private static Criteria getCriteriaForSearchParams(final Object value, final Field field) {
        final SearchParam[] searchParams = field.getAnnotation(SearchParams.class).value();
        Criteria criteria = null;
        for (final SearchParam searchParam : searchParams) {
            final Criteria newCriteria = new Criteria(Column.getColumn(searchParam.tableName(), searchParam.columnName()), value, searchParam.comparator(), false);
            criteria = MDMDBUtil.orCriteria(criteria, newCriteria);
        }
        return criteria;
    }
    
    private static Criteria setCustomerFilter(final Long userID, final Long customerID, final Object allCustVal, final Field allCustField) throws Exception {
        AllCustomerSearchParam allCustomerSearchParam = null;
        if (allCustField != null) {
            allCustomerSearchParam = allCustField.getAnnotation(AllCustomerSearchParam.class);
        }
        return setCustomerFilter(userID, customerID, allCustVal, allCustomerSearchParam);
    }
    
    public static Criteria setCustomerFilter(final Long userID, final Long customerID, final Object allCustVal, final AllCustomerSearchParam allCustomerSearchParam) throws Exception {
        Criteria cri = null;
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            Criteria customerCri = null;
            final Criteria userCri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0);
            if (customerID == null && allCustVal != null) {
                if (!String.valueOf(allCustVal).equalsIgnoreCase("all")) {
                    List<Long> customerList = new ArrayList<Long>();
                    final String[] customerArray = ((String)allCustVal).split(",");
                    for (int i = 0; i < customerArray.length; ++i) {
                        customerList.add(Long.parseLong(customerArray[i]));
                    }
                    customerList = MDMCustomerInfoUtil.getInstance().getValidCustomerIds(customerList);
                    if (customerList.isEmpty()) {
                        throw new APIHTTPException("COM0015", new Object[] { "Customer Id does not exists" });
                    }
                    final Long[] custAr = customerList.toArray(new Long[customerList.size()]);
                    customerCri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)custAr, 8);
                    if (allCustomerSearchParam != null) {
                        customerCri = MDMDBUtil.andCriteria(customerCri, new Criteria(Column.getColumn(allCustomerSearchParam.tableName(), allCustomerSearchParam.columnName()), (Object)custAr, allCustomerSearchParam.comparator()));
                    }
                }
            }
            else if (customerID != null) {
                customerCri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 0);
                if (allCustomerSearchParam != null) {
                    customerCri = MDMDBUtil.andCriteria(customerCri, new Criteria(Column.getColumn(allCustomerSearchParam.tableName(), allCustomerSearchParam.columnName()), (Object)customerID, 0));
                }
            }
            cri = MDMDBUtil.andCriteria(customerCri, userCri);
        }
        return cri;
    }
}
