/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.index;

import javax.swing.JPanel;
import org.jhrcore.client.BaseMainFrame;
import org.jhrcore.client.desk.MyToolTipPanel;
import org.jhrcore.client.desk.MyToolTipPlugin;
import org.jhrcore.msg.sys.SysIndexMsg;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.task.CommTask;
import org.jhrcore.ui.task.IWaitWork;

/**
 *
 * @author mxliteboss
 */
public class IToolTipPanel extends IndexInnerPnl {

    public IToolTipPanel() {//我的提醒
        super(SysIndexMsg.msgprompting.toString(), new String[]{SysIndexMsg.msgNotice.toString(), SysIndexMsg.msgNum.toString()});
    }

    @Override
    public void refreshData() {
        super.setObjects(IndexUtil.getToolTipData(true));
    }

    @Override
    public void doClick() {
        Object obj = getCurRowObj();
        JPanel pnl = MyToolTipPanel.getToolTipPanel(obj);
        if (pnl != null) {
            ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "提醒信息查看：");
        }
    }

    @Override
    public void doMore() {
        CommTask task = new MyToolTipPlugin();
        task.setDefaultModuleName("我的提醒");
        BaseMainFrame.getBaseMainFrame().modulePlugInAction(task);
        JPanel pnl = BaseMainFrame.getBaseMainFrame().getCur_Panel();
        if (pnl instanceof IWaitWork) {
            ((IWaitWork) pnl).initForWait(getObjects(), getCurRowObj());
        }
    }
}
