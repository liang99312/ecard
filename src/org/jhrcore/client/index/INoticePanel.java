/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.index;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jhrcore.client.BaseMainFrame;
import org.jhrcore.client.desk.MyNoticePlugin;
import org.jhrcore.msg.sys.SysIndexMsg;
import org.jhrcore.ui.task.CommTask;
import org.jhrcore.ui.task.IWaitWork;

/**
 *
 * @author mxliteboss
 */
public class INoticePanel extends IndexInnerPnl {

    public INoticePanel() { //我的公告
        super(SysIndexMsg.ttlCompnotice.toString(), new String[]{SysIndexMsg.ttlTitle.toString(), SysIndexMsg.ttlRelDate.toString()});
    }

    @Override
    public void refreshData() {
        super.setObjects(IndexUtil.getNoticeData(true));
    }

    @Override
    public void doClick() {
        CommTask task = new MyNoticePlugin();
        task.setDefaultModuleName("公司公告");
        BaseMainFrame.getBaseMainFrame().modulePlugInAction(task);
        JPanel pnl = BaseMainFrame.getBaseMainFrame().getCur_Panel();
        if (pnl instanceof IWaitWork) {
            ((IWaitWork) pnl).initForWait(getObjects(), getCurRowObj());
        }
    }

    @Override
    public void doMore() {
        this.doClick();
    }
}
