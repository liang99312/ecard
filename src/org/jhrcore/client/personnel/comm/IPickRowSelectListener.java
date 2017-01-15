/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel.comm;

import java.util.List;

/**
 *
 * @author mxliteboss
 */
public interface IPickRowSelectListener {
    public List pickPerson(String hql);
    public void pickRow(Object a01);
    public void pickFields(List<String> fields);
    public void addRows(List list);
}
