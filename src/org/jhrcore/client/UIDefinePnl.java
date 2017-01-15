/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UIDefinePnl.java
 *
 * Created on 2013-5-3, 15:12:27
 */
package org.jhrcore.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.property.ClientProperty;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author mxliteboss
 */
public class UIDefinePnl extends javax.swing.JPanel {

    private JTree paraTree;
    private CheckTreeNode rootNode = new CheckTreeNode("���в���");
    private List<SysParameter> paras = new ArrayList();
    private BeanPanel beanPanel = new BeanPanel();

    /** Creates new form UIDefinePnl */
    public UIDefinePnl() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        beanPanel.setDisable_fields(Arrays.asList(new String[]{"sysparameter_code", "sysparameter_name"}));
        paras.addAll(ClientProperty.getInstance().getAllParas());
        SysUtil.sortListByStr(paras, "sysparameter_code");
        CheckTreeNode tmp = rootNode;
        for (SysParameter sp : paras) {
            while (tmp != rootNode && tmp.getUserObject() instanceof SysParameter && !sp.getSysparameter_code().startsWith(((SysParameter) tmp.getUserObject()).getSysparameter_code())) {
                tmp = (CheckTreeNode) tmp.getParent();
            }
            CheckTreeNode cur = new CheckTreeNode(sp);
            tmp.add(cur);
            tmp = cur;
        }
        paraTree = new JTree(rootNode);
        HRRendererView.getCommMap().initTree(paraTree);
        pnlLeft.add(new JScrollPane(paraTree));
        pnlMain.add(new JScrollPane(beanPanel));
    }

    private void setupEvents() {
        paraTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                editPara();
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                savePara();
            }
        });
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ModelFrame.close();
            }
        });
    }

    private void editPara() {
        Object obj = paraTree.getLastSelectedPathComponent();
        if (obj == null) {
            return;
        }
        CheckTreeNode node = (CheckTreeNode) obj;
        if (node.getUserObject() instanceof SysParameter) {
            beanPanel.setBean(node.getUserObject());
            beanPanel.setFields(Arrays.asList(new String[]{"sysparameter_code", "sysparameter_name", "sysparameter_value"}));
            beanPanel.setEditable(node.isLeaf());
            beanPanel.bind();
        }
    }

    private void savePara() {
        if (beanPanel.isEditable()) {
            SysParameter sp = (SysParameter) beanPanel.getBean();
            ConfigManager.getConfigManager().setProperty(sp.getSysparameter_code(), sp.getSysparameter_value());
            ConfigManager.getConfigManager().save2();
            MsgUtil.showHRSaveSuccessMsg(null);
            BaseMainFrame.refreshUI();
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

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnClose = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        btnSave.setText("����");
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnSave);
        toolbar.add(jSeparator1);

        btnClose.setText("�˳�");
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnClose);

        pnlMain.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}