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
        super("����");


        putValue(SHORT_DESCRIPTION, "���ڱ����");
    }

    public void actionPerformed(ActionEvent arg0) {
        String verType = getSecuryInfo("verType");
        String msg = "���ÿ��������" + ("Beta".equals(verType) ? "(���ð�)" : "(��׼��)")
                + "\n"
                + "�汾�ţ�" + getVersion()
                + "\n"
                + "��Ȩ���У�(C) 2009-2011 HFLangJi Corp"
                + ("Beta".equals(verType) ? ("\nʹ�����޻�ʣ��" + getSecuryInfo("remainDay") + "��") : "")
                + "\n"
                + "ʹ��Ȩ��";
        JOptionPane.showMessageDialog(ContextManager.getMainFrame(), msg, "����", JOptionPane.INFORMATION_MESSAGE);
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
