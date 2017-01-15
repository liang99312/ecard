/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.desk;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author mxliteboss
 */
public class MyNoticePlugin extends CommTask {

    @Override
    public Class getModuleClass() {
        return MyNoticePanel.class;
    }

    @Override
    public String getGroupCode() {
        return "¹¤×÷×ÀÃæ";
    }
    
}