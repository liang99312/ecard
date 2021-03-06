/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PickNewPersonClass2.java
 *
 * Created on 2008-12-7, 17:22:38
 */
package org.jhrcore.client.personnel.changemodule;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.util.SysUtil;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Administrator
 */
public class PickNewPersonClass2 extends WizardPanel implements IModuleCode{

    private ChangeScheme changeScheme;
    private EntityDef after_class = null;
    private List<EntityDef> all_classes = new ArrayList<EntityDef>();
    private JComboBoxBinding after_binding;
    private CheckTreeNode rootNode;
    private JTree class_tree;
    private String old_class;
    private String module_code = "EmpChangeScheme.btnEdit.getPanel3";

    public PickNewPersonClass2() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form PickNewPersonClass2 */
    public PickNewPersonClass2(ChangeScheme changeScheme) {
        initComponents();
        this.changeScheme = changeScheme;
        initOthers();
        setupEvents();
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
        jLabel1 = new javax.swing.JLabel();
        jcbClassAfter = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        pnlClass = new javax.swing.JPanel();

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText("变动后的人员类别：");
        jLabel1.setName("jLabel1"); // NOI18N

        jcbClassAfter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbClassAfter.setName("jcbClassAfter"); // NOI18N

        jLabel2.setText("变动前的人员类别：");
        jLabel2.setName("jLabel2"); // NOI18N

        pnlClass.setName("pnlClass"); // NOI18N
        pnlClass.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbClassAfter, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlClass, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbClassAfter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlClass, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(130, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jcbClassAfter;
    private javax.swing.JPanel pnlClass;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isValidate() {
        after_class = (EntityDef) jcbClassAfter.getSelectedItem();
        if (after_class == null) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(jcbClassAfter), "未设置变动后人员类别", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpChangeSchemeMsg.msg016);
            return false;
        }
        old_class = "";
        Enumeration enumt = rootNode.breadthFirstEnumeration();
        while (enumt.hasMoreElements()) {
            CheckTreeNode node = (CheckTreeNode) enumt.nextElement();
            if (node.isSelected()) {
                if (node.getUserObject() instanceof EntityDef) {
                    old_class = old_class + ((EntityDef) node.getUserObject()).getEntityName() + ";";
                }
            }
        }
        if (old_class.trim().equals("")) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(jcbClassAfter), "请选择变动前的人员类别", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpChangeSchemeMsg.msg017);
            return false;
        }
        return true;
    }

    @Override
    public void beforeLeave() {
        changeScheme.setNewPersonClassName(after_class.getEntityName());
        changeScheme.setOldPersonClassName(old_class);
    }

    private void initOthers() {
        List<EntityDef> list1 = SysUtil.getPersonClass();
        for (EntityDef ed : list1) {
            if (ed.getEntityName().equals("A01")) {
                continue;
            }
            all_classes.add(ed);
        }
        after_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, all_classes, jcbClassAfter);
        after_binding.bind();
        rootNode = new CheckTreeNode(EmpChangeSchemeMsg.msg018.toString());
        class_tree = new JTree(rootNode);
        HRRendererView.getCommMap().initTree(class_tree, TreeSelectMod.nodeCheckChildFollowMod);
        class_tree.setShowsRootHandles(true);
        class_tree.expandRow(1);
        pnlClass.add(new JScrollPane(class_tree), BorderLayout.CENTER);
    }

    private void setupEvents() {
        jcbClassAfter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPersonClassTree((EntityDef) jcbClassAfter.getSelectedItem());
            }
        });
        String tmp_s = changeScheme.getNewPersonClassName();
        tmp_s = tmp_s == null ? "" : tmp_s.trim();
        Object selectObj = null;
        for (EntityDef ed : all_classes) {
            if (ed.getEntityName().equals(tmp_s)) {
                selectObj = ed;
                break;
            }
        }
        if (selectObj == null) {
            if (all_classes.size() > 0) {
                jcbClassAfter.setSelectedIndex(0);
            }
        } else {
            jcbClassAfter.setSelectedItem(selectObj);
        }
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void refreshPersonClassTree(EntityDef entityDef) {
        after_class = entityDef;
        rootNode.removeAllChildren();
        old_class = changeScheme.getOldPersonClassName();
        List<String> old_classes = new ArrayList<String>();
        if (old_class != null && !old_class.trim().equals("")) {
            String[] ss = old_class.split("\\;");
            for (String s : ss) {
                old_classes.add(s);
            }
        }
        for (EntityDef ed : all_classes) {
            CheckTreeNode node = new CheckTreeNode(ed);
            if (entityDef != null) {
                if (ed.getEntityName().equals(entityDef.getEntityName())) {
                    continue;
                }
            }
            if (old_classes.contains(ed.getEntityName())) {
                node.setSelected(true);
            }
            rootNode.add(node);
        }
        class_tree.updateUI();
    }

    @Override
    public String getTitle() {
        return EmpChangeSchemeMsg.msg019.toString();
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
