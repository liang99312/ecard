/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.param;

import org.jhrcore.msg.ModuleGroupMsg;
import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author Administrator
 */
public class EmpParamPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return EmpParamPanel.class;
    }

    @Override
    public String getGroupCode() {
        return "Emp";
    }

    @Override
    public Object getClassName() {
        return ModuleGroupMsg.Basic_Settings;
    }
}
