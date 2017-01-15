/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.right;
//package org.jhrcore.ui;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author Administrator
 */
public class RightPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return RightPanel.class;
    }

    @Override
    public String getGroupCode() {
        return "系统维护";
    }

    @Override
    public String getClassName() {
        return "权限维护";
    }
}
