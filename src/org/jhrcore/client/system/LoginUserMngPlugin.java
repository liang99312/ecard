/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author hflj
 */
public class LoginUserMngPlugin extends CommTask {

    @Override
    public String getGroupCode() {
        return "ϵͳά��";
    }

    @Override
    public Class getModuleClass() {
        return LoginUserMngPanel.class;
    }

    @Override
    public String getClassName() {
        return "��־����";
    }
}
