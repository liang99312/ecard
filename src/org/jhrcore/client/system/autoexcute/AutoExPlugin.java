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
        return "ϵͳά��";
    }

    @Override
    public String getClassName() {
        return "ƽ̨ά��";
    }
}
