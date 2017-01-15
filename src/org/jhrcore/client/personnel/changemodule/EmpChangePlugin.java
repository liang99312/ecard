package org.jhrcore.client.personnel.changemodule;

import org.jhrcore.msg.ModuleGroupMsg;
import org.jhrcore.ui.task.CommTask;

public class EmpChangePlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return EmpChangePanel1.class;
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
