/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.register;

import org.jhrcore.msg.ModuleGroupMsg;
import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author yangzhou
 */
public class EmpRegisterPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return EmpRegisterPanel.class;
    }

    @Override
    public String getGroupCode() {
        return "Emp";
    }

    @Override
    public Object getClassName() {
        return ModuleGroupMsg.Change_Mange;
    }
}
