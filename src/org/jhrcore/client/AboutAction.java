package org.jhrcore.client;

import java.awt.event.ActionEvent;

import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.jhrcore.ui.ContextManager;

public class AboutAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static Hashtable<String, String> securyInfos;

    public AboutAction() {
        super("关于");


        putValue(SHORT_DESCRIPTION, "关于本软件");
    }

    public void actionPerformed(ActionEvent arg0) {
        String verType = getSecuryInfo("verType");
        String msg = "信用卡管理软件" + ("Beta".equals(verType) ? "(试用版)" : "(标准版)")
                + "\n"
                + "版本号：" + getVersion()
                + "\n"
                + "版权所有：(C) 2009-2011 HFLangJi Corp"
                + ("Beta".equals(verType) ? ("\n使用期限还剩：" + getSecuryInfo("remainDay") + "天") : "")
                + "\n"
                + "使用权：";
        JOptionPane.showMessageDialog(ContextManager.getMainFrame(), msg, "关于", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String getSecuryInfo(String tag) {
        if (securyInfos == null) {
            securyInfos = CommUtil.getSecuryInfo(true);
        }
        return securyInfos.get(tag);
    }

    public static String getVersion() {
        return getSecuryInfo("version") == null ? "9.1" : getSecuryInfo("version");
    }
}
