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
public class EcardPosPlugin extends CommTask {

    @Override
    public String getGroupCode() {
        return "信用卡管理";
    }

    @Override
    public Class getModuleClass() {
        return EcardPosPanel.class;
    }

    @Override
    public Object getClassName() {
        return "";
    }
}
