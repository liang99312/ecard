/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.right;

import org.jhrcore.right.FuntionModifyMngPanel2;
import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author yangzhou
 */
public class FuntionModifyPlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return FuntionModifyMngPanel2.class;
    }

    @Override
    public String getGroupCode() {
        return "ϵͳά��";
    }

    @Override
    public String getClassName() {
        return "ģ��ά��";
    }
}
