/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.rightuser;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author hflj
 */
public class RightPersonPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return RightPersonPanel.class;
    }

    @Override
    public String getGroupCode() {
        return "ϵͳά��";
    }

    @Override
    public String getClassName() {
        return "Ȩ��ά��";
    }
}
