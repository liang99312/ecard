/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.autoexcute;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author mxliteboss
 */
public class AutoExPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return AutoExPanel.class;
    }

    public String getGroupCode() {
        return "系统维护";
    }

    @Override
    public String getClassName() {
        return "平台维护";
    }
}
