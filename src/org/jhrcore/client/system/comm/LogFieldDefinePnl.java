/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogFieldDefinePnl.java
 *
 * Created on 2013-4-23, 23:03:35
 */
package org.jhrcore.client.system.comm;

import com.foundercy.pf.control.table.FTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.base.EntityClass;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.iservice.impl.SysImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author mxliteboss
 */
public class LogFieldDefinePnl extends javax.swing.JPanel {

    private JTree moduleTree;
    private CheckTreeNode rootNodeModule = new CheckTreeNode("所有模块");
    private JTree fieldTree;
    private CheckTreeNode rootNodeField = new CheckTreeNode("所有日志表");
    private Hashtable<String, CheckTreeNode> entityNodeKeys = new Hashtable();
    private FTable ftable = new FTable(Arrays.asList(new String[]{"表名", "表的描述", "数据标识"}));
    private String code = "SysLogData";

    /** Creates new form LogFieldDefinePnl */
    public LogFieldDefinePnl() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        Hashtable<String, CheckTreeNode> nodeKeys = new Hashtable();
        List<?> list = CommImpl.getSysModule(true, true, true);//CommUtil.fetchEntities("from ModuleInfo mi  left join fetch mi.entityClasss et left join fetch et.entityDefs ed left join fetch ed.fieldDefs  where mi.module_key<>'ZHTJ' order by mi.order_no");
        for (Object obj : list) {
            ModuleInfo mi = (ModuleInfo) obj;
            CheckTreeNode miNode = new CheckTreeNode(mi);
            for (EntityClass ec : mi.getEntityClasss()) {
                CheckTreeNode ecNode = new CheckTreeNode(ec);
                for (EntityDef ed : ec.getEntityDefs()) {
                    CheckTreeNode edNode = new CheckTreeNode(ed);
                    for (FieldDef fd : ed.getFieldDefs()) {
                        if (fd.isUsed_flag()) {
                            CheckTreeNode fdNode = new CheckTreeNode(fd);
                            edNode.add(fdNode);
                            nodeKeys.put(ed.getEntityName() + "." + fd.getField_name(), fdNode);
                        }
                    }
                    ecNode.add(edNode);
                }
                miNode.add(ecNode);
            }
            rootNodeModule.add(miNode);
        }
        moduleTree = new JTree(rootNodeModule);
        HRRendererView.getRebuildMap(moduleTree).initTree(moduleTree, TreeSelectMod.nodeManySelectMod);
        pnlLeft.add(new JScrollPane(moduleTree));
        fieldTree = new JTree(rootNodeField);
        HRRendererView.getRebuildMap(fieldTree).initTree(fieldTree, TreeSelectMod.nodeManySelectMod);
        pnlField.add(new JScrollPane(fieldTree));
        fieldTree.setShowsRootHandles(true);
        pnlId.add(ftable);
        List paras = CommUtil.fetchEntities("from SysParameter sp where sp.sysParameter_key like '" + code + "%'");
        for (Object obj : paras) {
            SysParameter sp = (SysParameter) obj;
            String key = sp.getSysParameter_key().replace(code + ".", "");
            String value = SysUtil.objToStr(sp.getSysparameter_value());
            if (key.startsWith("field")) {
                key = key.substring(6);
                String[] values = value.split(";");
                for (String v : values) {
                    CheckTreeNode node = nodeKeys.get(key + "." + v);
                    if (node == null) {
                        continue;
                    }
                    addField(node);
                }
            }
        }
        for (Object obj : ftable.getObjects()) {
            Object[] objs = (Object[]) obj;
            for (Object para : paras) {
                SysParameter sp = (SysParameter) para;
                if (sp.getSysParameter_key().replace(code + ".id.", "").equals(objs[0].toString())) {
                    objs[2] = sp.getSysparameter_value();
                    break;
                }
            }
        }
        fieldTree.updateUI();
    }

    private void setupEvents() {
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        btnRemove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                remove();
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        CloseAction.doCloseAction(btnClose);
    }

    private void addField(CheckTreeNode cnode) {
        FieldDef fd = (FieldDef) cnode.getUserObject();
        EntityDef ed = fd.getEntityDef();
        String key = ed.getEntityName();
        CheckTreeNode node = entityNodeKeys.get(key);
        if (node == null) {
            List ids = ftable.getObjects();
            node = new CheckTreeNode(ed);
            rootNodeField.add(node);
            entityNodeKeys.put(key, node);
            ids.add(new String[]{key, ed.getEntityCaption(), ""});
            ftable.setObjects(ids);
        }
        key = ed.getEntityName() + "." + fd.getField_name();
        if (entityNodeKeys.get(key) == null) {
            CheckTreeNode fnode = new CheckTreeNode(fd);
            entityNodeKeys.put(key, fnode);
            node.add(fnode);
        }
    }

    private void add() {
        TreePath[] tps = moduleTree.getSelectionPaths();
        if (tps == null) {
            return;
        }
        if (jtpMain.getSelectedIndex() == 0) {
            for (TreePath tp : tps) {
                CheckTreeNode cnode = (CheckTreeNode) tp.getLastPathComponent();
                if (cnode.getUserObject() instanceof FieldDef) {
                    addField(cnode);
                }
            }
            fieldTree.updateUI();
        } else {
            if (ftable.getObjects().isEmpty()) {
                return;
            }
            Object[] curRow = (Object[]) ftable.getCurrentRow();
            List<FieldDef> fds = new ArrayList();
            for (TreePath tp : tps) {
                CheckTreeNode cnode = (CheckTreeNode) tp.getLastPathComponent();
                if (cnode.getUserObject() instanceof FieldDef) {
                    FieldDef fd = (FieldDef) cnode.getUserObject();
                    if (fd.getEntityDef().getEntityName().equals(curRow[0])) {
                        fds.add(fd);
                    }
                }
            }
            if (fds.isEmpty()) {
                MsgUtil.showErrorMsg("请选择对应表字段");
                return;
            }
            if (fds.size() == 1) {
                curRow[2] = "@" + fds.get(0).getField_name();
            } else {
                String text = "";
                for (FieldDef fd : fds) {
                    text += ",@" + fd.getField_name();
                }
                LogFieldIdDefinePnl pnl = new LogFieldIdDefinePnl(text.substring(1));
                if (ModalDialog.doModal(JOptionPane.getFrameForComponent(btnSave), pnl, "设置标识组合")) {
                    curRow[2] = pnl.getText();
                }
            }
            ftable.updateUI();
        }
    }

    private void remove() {
        if (jtpMain.getSelectedIndex() == 0) {
            TreePath[] tps = fieldTree.getSelectionPaths();
            if (tps == null) {
                return;
            }
            for (TreePath tp : tps) {
                CheckTreeNode cnode = (CheckTreeNode) tp.getLastPathComponent();
                if (cnode.getUserObject() instanceof FieldDef) {
                    cnode.removeFromParent();
                    FieldDef fd = (FieldDef) cnode.getUserObject();
                    EntityDef ed = fd.getEntityDef();
                    String key = ed.getEntityName() + "." + fd.getField_name();
                    entityNodeKeys.remove(key);
                }
            }
            List<String> removeEntitys = new ArrayList();
            Enumeration enumt = rootNodeField.children();
            while (enumt.hasMoreElements()) {
                CheckTreeNode node = (CheckTreeNode) enumt.nextElement();
                if (node.getChildCount() == 0) {
                    EntityDef ed = (EntityDef) node.getUserObject();
                    removeEntitys.add(ed.getEntityName());
                    node.removeFromParent();
                    String key = ed.getEntityName();
                    entityNodeKeys.remove(key);
                }
            }
            List removeList = new ArrayList();
            for (Object obj : ftable.getObjects()) {
                Object[] objs = (Object[]) obj;
                if (removeEntitys.contains(objs[0].toString())) {
                    removeList.add(obj);
                }
            }
            ftable.getObjects().removeAll(removeList);
            ftable.updateUI();
            fieldTree.updateUI();
        } else {
            List list = ftable.getSelectObjects();
            for (Object obj : list) {
                Object[] objs = (Object[]) obj;
                objs[2] = "";
            }
            ftable.updateUI();
        }
    }

    private void save() {
        List list = new ArrayList();
        Enumeration enumt = rootNodeField.breadthFirstEnumeration();
        Hashtable<String, SysParameter> paras = new Hashtable();
        while (enumt.hasMoreElements()) {
            CheckTreeNode node = (CheckTreeNode) enumt.nextElement();
            if (node.getUserObject() instanceof FieldDef) {
                FieldDef fd = (FieldDef) node.getUserObject();
                String entityName = fd.getEntityDef().getEntityName();
                String codeField = code + ".field." + entityName;
                SysParameter sp = paras.get(codeField);
                if (sp == null) {
                    sp = new SysParameter();
                    sp.setSysParameter_key(codeField);
                    sp.setSysparameter_code(codeField);
                    sp.setSysparameter_value("");
                    paras.put(codeField, sp);
                    list.add(sp);
                }
                sp.setSysparameter_value(sp.getSysparameter_value() + fd.getField_name() + ";");
            }
        }
        for (Object obj : ftable.getObjects()) {
            Object[] objs = (Object[]) obj;
            String codeField = code + ".id." + objs[0];
            SysParameter sp = new SysParameter();
            sp.setSysParameter_key(codeField);
            sp.setSysparameter_code(codeField);
            sp.setSysparameter_value(SysUtil.objToStr(objs[2]));
            list.add(sp);
        }
        ValidateSQLResult result = SysImpl.saveLogField(list);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
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

        jPanel1 = new javax.swing.JPanel();
        pnlLeft = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        pnlField = new javax.swing.JPanel();
        pnlId = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        pnlLeft.setLayout(new java.awt.BorderLayout());

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/select_one.png"))); // NOI18N

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/remove_one.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(274, Short.MAX_VALUE))
        );

        pnlField.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("已选日志字段", pnlField);

        pnlId.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlId.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("数据标识设置", pnlId);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
            .addComponent(pnlLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
        );

        btnSave.setText("保存");

        btnClose.setText("关闭");

        jLabel1.setText("说明：可多选字段进行标识组合设置");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 165, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose)
                .addGap(38, 38, 38))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSave)
                        .addComponent(btnClose))
                    .addComponent(jLabel1))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlField;
    private javax.swing.JPanel pnlId;
    private javax.swing.JPanel pnlLeft;
    // End of variables declaration//GEN-END:variables
}
