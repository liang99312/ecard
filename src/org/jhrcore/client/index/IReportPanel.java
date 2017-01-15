/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.index;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jhrcore.client.BaseMainFrame;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.desk.MyReportPanel;
import org.jhrcore.client.desk.MyReportPlugin;
import org.jhrcore.entity.CommMap;
import org.jhrcore.msg.sys.SysIndexMsg;
import org.jhrcore.ui.task.IWaitWork;
import org.jhrcore.util.DateUtil;

/**
 *
 * @author mxliteboss
 */
public class IReportPanel extends IndexInnerPnl {

    private List list;

    public IReportPanel() {
        // super("我的邮件", new String[]{"主题", "发件人", "发送时间"});
        super(SysIndexMsg.ttlReport.toString(), new String[]{SysIndexMsg.ttlReportName.toString(), SysIndexMsg.ttlReportDate.toString()});
    }

    @Override
    public void refreshData() {
        String hql = "from CommMap mi where 1=1 and mi.user_key='" + UserContext.rolea01_key + "' and mi.map_type='report' order by mi.c_date desc";
        list = CommUtil.fetchEntities(hql);
        List result = new ArrayList();
        for (Object obj : list) {
            CommMap cm = (CommMap) obj;
            Object[] objs = new Object[2];
            objs[0] = cm.getMap_name();
            objs[1] = DateUtil.DateToStr(cm.getC_date(), "yyyy-MM-dd");
            result.add(objs);
        }
        super.setObjects(result);
    }

    @Override
    public void doClick() {
        Object cm = list.isEmpty() ? null : list.get(getCurRowIndex());
        if (cm != null) {
            MyReportPanel.execReport((CommMap) cm);
        }
    }

    @Override
    public void doMore() {
        BaseMainFrame.getBaseMainFrame().modulePlugInAction(new MyReportPlugin());
        JPanel pnl = BaseMainFrame.getBaseMainFrame().getCur_Panel();
        if (getCurRowIndex() >= 0 && pnl instanceof IWaitWork) {
            ((IWaitWork) pnl).initForWait(list, list.isEmpty() ? null : list.get(getCurRowIndex()));
        }
    }
}
