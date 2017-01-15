package org.jhrcore.client.personnel.changemodule;

import org.jhrcore.msg.ModuleGroupMsg;
import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author Administrator
 */
public class EmpChangeSchemePlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return EmpChangeSchemePanel.class;
    }

    @Override
    public String getGroupCode() {
        return "Emp";
    }

    @Override
    public Object getClassName() {
        //        return "ª˘¥°…Ë÷√";
        return ModuleGroupMsg.Basic_Settings;
    }
}
