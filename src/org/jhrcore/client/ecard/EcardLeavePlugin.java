/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author admin
 */
public class EcardLeavePlugin extends CommTask {

    @Override
    public String getGroupCode() {
        return "���ÿ�����";
    }

    @Override
    public Class getModuleClass() {
        return EcardLeavePanel.class;
    }

    @Override
    public Object getClassName() {
        return "";
    }
}
