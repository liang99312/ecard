/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.index;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author Administrator
 */
public class IndexPanelPlugIn extends CommTask {

    @Override
    public Class getModuleClass() {
        return IndexPnl.class;
    }
    
    @Override
    public String toString(){
        return "ÎÒµÄÊ×Ò³";
    }
}
