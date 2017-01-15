/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel.comm;

import org.jhrcore.entity.query.QueryScheme;

/**
 *
 * @author mxliteboss
 */
public interface ILocatePerson {
    public void locatePerson(int row);
    public void refreshData(QueryScheme qs,String order_sql);
    public void setPay_key(String pay_key);
}
