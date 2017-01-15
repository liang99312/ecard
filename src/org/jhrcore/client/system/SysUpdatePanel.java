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
                File file = FileChooserUtil.getZIPFile("请选择更新文件");
                if (file == null) {
                    return;
                }
                updateVersion(file);
            }
        });
        btnUpdateFun.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooserUtil.getXMLFile("请选择更新文件");
                if (file == null) {
                    return;
                }
                updateFun(file);
            }
        });
    }

    private void updateFun(File file) {
        if (!file.getName().endsWith(".xml")) {
            MsgUtil.showErrorMsg("您选择的不是合法的WEBHR更新程序");
            return;
        }
        ValidateSQLResult result = SysImpl.updateFun(file);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg("更新成功");
        } else {
            MsgUtil.showErrorMsg(result);
        }
    }

    private void updateVersion(File selectedFile) {
        if (!ZipUtil.isZip(selectedFile)) {
            MsgUtil.showErrorMsg("您选择的不是合法的WEBHR更新程序");
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
            MsgUtil.showErrorMsg("您选择的不是合法的WEBHR更新程序");
            return;
        }
        jtpMain.setSelectedIndex(1);
        String msg = "当前系统版本号：" + AboutAction.getVersion() + "\n";
        msg += "您当前选择的更新文件版本为：" + newVersion + "\n";
        msg += "包含以下文件\n";
        for (String file : files) {
            msg += file + "\n";
        }
        textPane.setText(msg);
        if (MsgUtil.showNotConfirmDialog("是否开始更新版本？")) {
            return;
        }
        textPane.setText("第一步：正在上传更新文件\n");
        ValidateSQLResult result = SysImpl.uploadFile(selectedFile, "$/" + selectedFile.getName());
        if (result.getResult() == 0) {
            textPane.setText(textPane.getText() + "更新文件上传完毕\n");
        } else {
            textPane.setText(textPane.getText() + "更新文件上传失败,更新终止");
            return;
        }
        textPane.setText(textPane.getText() + "第二步：正在关闭HR服务\n");
        result = SysImpl.closeService();
        if (result.getResult() == 0) {
            textPane.setText(textPane.getText() + "HR服务正常关闭\n");
        } else {
            textPane.setText(textPane.getText() + "HR服务关闭失败,更新终止");
            return;
        }
        textPane.setText(textPane.getText() + "第三步：正在更新程序\n");
        result = SysImpl.updateVersion();
        if (result.getResult() == 0) {
            textPane.setText(textPane.getText() + "版本更新完毕，请及时重启HR服务\n");
        } else {
            textPane.setText(textPane.getText() + "版本更新失败,更新终止");
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

        btnUpdate.setText("更新程序");
        btnUpdate.setFocusable(false);
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnUpdate);

        btnUpdateFun.setText("更新菜单");
        btnUpdateFun.setFocusable(false);
        btnUpdateFun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdateFun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnUpdateFun);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("版本更新日志", jPanel1);

        pnlUpdate.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("更新进度", pnlUpdate);

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
