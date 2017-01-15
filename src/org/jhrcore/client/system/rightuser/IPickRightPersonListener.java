/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.system.rightuser;

import java.util.List;

/**
 *
 * @author hflj
 */
public interface IPickRightPersonListener {
    public void fetchData(List roles,String text,boolean refresh);
    public void refresh();
    public void defineRight(List roles);
    public void addUser();
    public void delUser();
    public void setPass();
    public void registerFinger();
}
