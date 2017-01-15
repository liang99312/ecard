/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SysUpdatePanel.java
 *
 * Created on 2013-5-14, 22:21:42
 */
package org.jhrcore.client.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jhrcore.client.AboutAction;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.SysImpl;
import org.jhrcore.ui.HrTextPane;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.util.FileChooserUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.ZipUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mxliteboss
 */
public class SysUpdatePanel extends javax.swing.JPanel implements IModulePanel {

    public static final String module_code = "Sys_Update";
    private HrTextPane textPane = new HrTextPane();

    /** Creates new form SysUpdatePanel */
    public SysUpdatePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        pnlUpdate.add(textPane);
    }

    private void setupEvents() {
        btnUpdate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooserUtil.getZIPFile("��ѡ������ļ�");
                if (file == null) {
                    return;
                }
                updateVersion(file);
            }
        });
        btnUpdateFun.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooserUtil.getXMLFile("��ѡ������ļ�");
                if (file == null) {
                    return;
                }
                updateFun(file);
            }
        });
    }

    private void updateFun(File file) {
        if (!file.getName().endsWith(".xml")) {
            MsgUtil.showErrorMsg("��ѡ��Ĳ��ǺϷ���WEBHR���³���");
            return;
        }
        ValidateSQLResult result = SysImpl.updateFun(file);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg("���³ɹ�");
        } else {
            MsgUtil.showErrorMsg(result);
        }
    }

    private void updateVersion(File selectedFile) {
        if (!ZipUtil.isZip(selectedFile)) {
            MsgUtil.showErrorMsg("��ѡ��Ĳ��ǺϷ���WEBHR���³���");
            return;
        }
        List<String> files = ZipUtil.getFiles(selectedFile);
        String path = System.getProperty("user.dir") + "/update/";
        ZipUtil.unZip(selectedFile.getPath(), path);
        String newVersion = "V9.1";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new File(path + "webhr.xml"));
            NodeList list = doc.getElementsByTagName("version");
            int len = list.getLength();
            for (int i = 0; i < len; i++) {
                Node node = list.item(i);
                newVersion = node.getTextContent();
            }
        } catch (Exception ex) {
            MsgUtil.showErrorMsg("��ѡ��Ĳ��ǺϷ���WEBHR���³���");
            return;
        }
        jtpMain.setSelectedIndex(1);
        String msg = "��ǰϵͳ�汾�ţ�" + AboutAction.getVersion() + "\n";
        msg += "����ǰѡ��ĸ����ļ��汾Ϊ��" + newVersion + "\n";
        msg += "���������ļ�\n";
        for (String file : files) {
            msg += file + "\n";
        }
        textPane.setText(msg);
        if (MsgUtil.showNotConfirmDialog("�Ƿ�ʼ���°汾��")) {
            return;
        }
        textPane.setText("��һ���������ϴ������ļ�\n");
        ValidateSQLResult result = SysImpl.uploadFile(selectedFile, "$/" + selectedFile.getName());
        if (result.getResult() == 0) {
            textPane.setText(textPane.getText() + "�����ļ��ϴ����\n");
        } else {
            textPane.setText(textPane.getText() + "�����ļ��ϴ�ʧ��,������ֹ");
            return;
        }
        textPane.setText(textPane.getText() + "�ڶ��������ڹر�HR����\n");
        result = SysImpl.closeService();
        if (result.getResult() == 0) {
            textPane.setText(textPane.getText() + "HR���������ر�\n");
        } else {
            textPane.setText(textPane.getText() + "HR����ر�ʧ��,������ֹ");
            return;
        }
        textPane.setText(textPane.getText() + "�����������ڸ��³���\n");
        result = SysImpl.updateVersion();
        if (result.getResult() == 0) {
            textPane.setText(textPane.getText() + "�汾������ϣ��뼰ʱ����HR����\n");
        } else {
            textPane.setText(textPane.getText() + "�汾����ʧ��,������ֹ");
            MsgUtil.showErrorMsg(result.getMsg());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnUpdate = new javax.swing.JButton();
        btnUpdateFun = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pnlUpdate = new javax.swing.JPanel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnUpdate.setText("���³���");
        btnUpdate.setFocusable(false);
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnUpdate);

        btnUpdateFun.setText("���²˵�");
        btnUpdateFun.setFocusable(false);
        btnUpdateFun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdateFun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnUpdateFun);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("�汾������־", jPanel1);

        pnlUpdate.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("���½���", pnlUpdate);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateFun;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlUpdate;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFunctionRight() {
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
