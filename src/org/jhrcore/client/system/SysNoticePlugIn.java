/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author Administrator
 */
public class SysNoticePlugIn extends CommTask {

    @Override
    public Class getModuleClass() {
        return SysNoticePanel.class;
    }

    @Override
    public String getGroupCode() {
        return "ϵͳά��";
    }

    @Override
    public String getClassName() {
        return "ƽ̨ά��";
    }
}
