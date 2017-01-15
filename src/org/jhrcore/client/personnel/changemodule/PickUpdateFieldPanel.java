/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PickUpdateFieldPanel.java
 *
 * Created on 2012-8-16, 17:01:02
 */
package org.jhrcore.client.personnel.changemodule;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.ClassAnnotation;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeField;
import org.jhrcore.entity.change.ChangeMethod;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.SearchTreeFieldDialog;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class PickUpdateFieldPanel extends WizardPanel implements IModuleCode {

    private ChangeScheme changeScheme;
    private FTable ftable;
    private List<ChangeField> listImportAppendixDetail = new ArrayList<ChangeField>();
    private JTree treeChangeField;
    private ChangeFieldTreeModel changeFieldTreeModel;
    private JTree fieldTree;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("所有附表");
    private String module_code = "EmpChangeScheme.btnEdit.getPanel5";

    public PickUpdateFieldPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form PickUpdateFieldPanel */
    public PickUpdateFieldPanel(CreateChangeSchemePara createPara) {
        initComponents();
        this.changeScheme = createPara.getChangeScheme();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        fieldTree = new JTree(rootNode);
        for (ChangeMethod cm : changeScheme.getChangeMethods()) {
            String entity = cm.getAppendix_name();
            try {
                Class c = Class.forName("org.jhrcore.entity." + entity);
                ClassAnnotation ca = (ClassAnnotation) c.getAnnotation(ClassAnnotation.class);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(ca.displayName());
                rootNode.add(node);
                List<TempFieldInfo> fields = EntityBuilder.getCommFieldInfoListOf(c, EntityBuilder.COMM_FIELD_VISIBLE);
                for (TempFieldInfo tfi : fields) {
                    node.add(new DefaultMutableTreeNode(tfi));
                }
            } catch (Exception ex) {
            }
        }
        HRRendererView.getCommMap().initTree(fieldTree);
        fieldTree.setRootVisible(false);
        fieldTree.setShowsRootHandles(true);
        ComponentUtil.initTreeSelection(fieldTree);
        pnlChangeField.add(new JScrollPane(fieldTree));
        changeFieldTreeModel = new ChangeFieldTreeModel(changeScheme);
        treeChangeField = new JTree(changeFieldTreeModel);
        HRRendererView.getCommMap().initTree(treeChangeField);
        treeChangeField.setRootVisible(false);
        treeChangeField.setShowsRootHandles(true);
        pnlImportField.add(new JScrollPane(treeChangeField), BorderLayout.CENTER);
        pnlImportField.updateUI();
        SearchTreeFieldDialog.doQuickSearch(EmpChangeSchemeMsg.ttl004.toString(), treeChangeField);
        ftable = new FTable(ChangeField.class, false, false);
        List<String> fields = new ArrayList<String>();
        fields.add("appendix_displayname");
        fields.add("appendix_field_displayName");
        fields.add("import_displayname");
        fields.add("import_field_displayName");
        ftable.setFields(fields);
        pnlMain.add(ftable, BorderLayout.CENTER);
    }

    private void setupEvents() {
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addField();
            }
        });

        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delField();
            }
        });
        rebuild();
        boolean enable = changeScheme.getChangeScheme_key().equals("EmpScheme_Add") || changeScheme.getChangeScheme_key().equals("EmpScheme_Del");
        btnAdd.setEnabled(!enable);
        btnDel.setEnabled(!enable);
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void addField() {
        int tab = jtpMain.getSelectedIndex();
        TreePath tp = null;
        if (tab == 0) {
            tp = fieldTree.getSelectionPath();
        } else {
            tp = treeChangeField.getSelectionPath();
        }
        if (tp == null) {
            return;
        }
        Object node = tp.getLastPathComponent();
        if (node == null) {
            return;
        }
        Object s_obj = ((DefaultMutableTreeNode) node).getUserObject();
        if (!(s_obj instanceof TempFieldInfo)) {
            return;
        }
        TempFieldInfo fd = (TempFieldInfo) s_obj;
        if (tab == 0) {
            ChangeField changeField = (ChangeField) UtilTool.createUIDEntity(ChangeField.class);
            changeField.setC_type("更新");
            changeField.setChangeScheme(changeScheme);
            changeField.setAppendix_name(fd.getEntity_name());
            changeField.setAppendix_displayname(fd.getEntity_caption());
            changeField.setAppendix_field(fd.getField_name());
            changeField.setAppendix_field_displayName(fd.getCaption_name());
            changeField.setImported("引入");
            changeField.setFrom_import(true);
            changeField.setField_type(fd.getField_type());
            changeScheme.getChangeFields().add(changeField);
            ftable.addObject(changeField);
        } else {
            Object obj = ftable.getCurrentRow();
            if (obj == null || listImportAppendixDetail.isEmpty()) {
                return;
            }
            ChangeField cf = (ChangeField) ftable.getCurrentRow();
            if (!cf.getField_type().equals(fd.getField_type())) {
//                if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(pnlMain),
//                        "数据类型不一致，可能导致引入错误，确定要引入吗？", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
//                    return;
//                }
                if (MsgUtil.showNotConfirmDialog(EmpChangeSchemeMsg.msg012)) {
                    return;
                }
            }
            cf.setImport_field(fd.getField_name());
            cf.setImport_field_displayName(fd.getCaption_name());
            cf.setImport_name(fd.getEntity_name());
            cf.setImport_displayname(fd.getEntity_caption());
        }
        ftable.updateUI();
    }

    private void delField() {
        if (listImportAppendixDetail.isEmpty()) {
            return;
        }
        int tab = jtpMain.getSelectedIndex();
        if (tab == 0) {
            changeScheme.getChangeFields().removeAll(ftable.getSelectObjects());
            ftable.deleteSelectedRows();
        } else {
            for (Object row : ftable.getSelectObjects()) {
                ChangeField cf = (ChangeField) row;
                cf.setImport_field("");
                cf.setImport_field_displayName("");
                cf.setImport_name("");
                cf.setImport_displayname("");
            }
        }
        ftable.updateUI();
    }

    public void rebuild() {
        listImportAppendixDetail.clear();
        for (ChangeField changeField : changeScheme.getChangeFields()) {
            if ("更新".equals(changeField.getC_type())) {
                listImportAppendixDetail.add(changeField);
            }
        }
        ftable.setObjects(listImportAppendixDetail);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        pnlMain = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        pnlChangeField = new javax.swing.JPanel();
        pnlImportField = new javax.swing.JPanel();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("字段对应情况"));

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMain.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
        );

        btnAdd.setText(">");

        btnDel.setText("<");

        pnlChangeField.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("附表字段列表", pnlChangeField);

        pnlImportField.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("引用表信息", pnlImportField);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtpMain, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDel)
                    .addComponent(btnAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDel)
                .addContainerGap(252, Short.MAX_VALUE))
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlChangeField;
    private javax.swing.JPanel pnlImportField;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public void beforeLeave() {
    }

    @Override
    public String getTitle() {
        if (changeScheme.contains("a0191")) {
            return EmpChangeSchemeMsg.msg023.toString();
        }
        return EmpChangeSchemeMsg.msg024.toString();
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
