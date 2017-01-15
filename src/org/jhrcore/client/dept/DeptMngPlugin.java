/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.dept;

import org.jhrcore.msg.ModuleGroupMsg;
import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author yangzhou
 */
public class DeptMngPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return DeptMngPanel.class;
    }

    @Override
    public String getGroupCode() {
        return "Dept";
    }

    @Override
    public Object getClassName() {
        return ModuleGroupMsg.Dept_Management;
    }
}
