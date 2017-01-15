/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author mxliteboss
 */
public class SysUpdatePlugin extends CommTask {

    private static final long serialVersionUID = 1L;

    @Override
    public String getGroupCode() {
        return "系统维护";
    }
    
    @Override
    public Class getModuleClass() {
        return SysUpdatePanel.class;
    }

    @Override
    public String getClassName() {
        return "平台维护";
    }
}