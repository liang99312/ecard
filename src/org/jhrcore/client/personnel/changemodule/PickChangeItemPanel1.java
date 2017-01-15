/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PickChangeItemPanel1.java
 *
 * Created on 2010-11-9, 11:30:13
 */
package org.jhrcore.client.personnel.changemodule;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.A01;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeItem;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.report.ReportDef;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ReportSelectManyDlg;
import org.jhrcore.ui.SearchTreeFieldDialog;
import org.jhrcore.ui.ShowFieldTreeModel;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class PickChangeItemPanel1 extends WizardPanel implements IModuleCode {

    private ChangeScheme changeScheme;
    private FTable ftableItem;
    private JButton btnReportSet = new JButton("设置");
    private List reports;
    private List<ReportDef> selectedReports = new ArrayList<ReportDef>();
    private JTextField jtf = new JTextField();
    private ShowFieldTreeModel sftModel;
    private JTree fieldTree;
    private Hashtable<String, TempFieldInfo> fieldKeys = new Hashtable<String, TempFieldInfo>();
    private String module_code = "EmpChangeScheme.btnAdd";

    public PickChangeItemPanel1() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form PickChangeItemPanel1 */
    public PickChangeItemPanel1(CreateChangeSchemePara createPara) {
        super();
        this.setLayout(new BorderLayout());
        this.changeScheme = createPara.getChangeScheme();
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        ftableItem = new FTable(ChangeItem.class, new String[]{"fieldName", "displayName", "comm_flag", "field_type"}, false, false, false, "PickChangeItem");
        List<TempFieldInfo> infos = EntityBuilder.getDeclareFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE_ALL);
        for (TempFieldInfo tfi : infos) {
            fieldKeys.put(tfi.getField_name().replace("_code_", ""), tfi);
        }
        sftModel = new ShowFieldTreeModel(infos);
        fieldTree = new JTree(sftModel);
        fieldTree.setRootVisible(false);
        fieldTree.setShowsRootHandles(true);
        HRRendererView.getCommMap().initTree(fieldTree);
        BeanPanel beanPanel = new BeanPanel();
        beanPanel.setBean(changeScheme);
        beanPanel.setEditable(true);
        List<String> scheme_fields = EntityBuilder.getCommFieldNameListOf(ChangeScheme.class, EntityBuilder.COMM_FIELD_VISIBLE);
        if (!UserContext.hasModuleRight("SysWorkFlow")) {
            changeScheme.setCheck_flag(false);
            scheme_fields.remove("check_flag");
        }
        beanPanel.setFields(scheme_fields);
        beanPanel.bind();
        JPanel pnlReport = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlReport.add(new JLabel("       调令单   "));
        jtf.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jtf.setPreferredSize(new Dimension(420, 24));
        pnlReport.add(jtf);
        pnlReport.add(btnReportSet);
        pnlField.add(new JScrollPane(fieldTree), BorderLayout.CENTER);
        pnlScheme.add(beanPanel, BorderLayout.CENTER);
        pnlScheme.add(pnlReport, BorderLayout.SOUTH);
        pnlItem.add(ftableItem, BorderLayout.CENTER);
        ftableItem.setEditable(true);
        List list = CommUtil.selectSQL("select reportDef_key,report_class,report_name,rd.order_no,rd.module_key from ReportDef rd,ModuleInfo mi where rd.module_key=mi.module_key and mi.module_code='Emp' order by rd.order_no");
        reports = new ArrayList();//CommUtil.fetchEntities("from ReportDef rd join fetch rd.moduleInfo where rd.moduleInfo.module_code='Emp' order by rd.order_no");
        for (Object obj : list) {
            Object[] objs = (Object[]) obj;
            ModuleInfo mi = UserContext.getModuleInfo(SysUtil.objToStr(objs[4], "@"));
            if (mi == null) {
                continue;
            }
            ReportDef rd = new ReportDef();
            rd.setReportDef_key(objs[0].toString());
            rd.setReport_class(SysUtil.objToStr(objs[1]));
            rd.setReport_name(SysUtil.objToStr(objs[2]));
            rd.setOrder_no(SysUtil.objToInt(objs[3]));
            rd.setModuleInfo(mi);
            reports.add(rd);
        }
        if (changeScheme.getNew_flag() == 0 && changeScheme.getReport_key() != null) {
            String[] keys = changeScheme.getReport_key().split(";");
            HashSet<String> r_keys = new HashSet<String>();
            for (String key : keys) {
                r_keys.add(key);
            }
            for (Object obj : reports) {
                ReportDef report = (ReportDef) obj;
                if (r_keys.contains(report.getReportDef_key())) {
                    selectedReports.add(report);
                }
            }
        }
        updateReportText();
        SearchTreeFieldDialog.doQuickSearch("人员基本信息表", fieldTree);
    }

    private void setupEvents() {
        btnReportSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ReportSelectManyDlg rsmDlg = new ReportSelectManyDlg(JOptionPane.getFrameForComponent(btnReportSet), reports, selectedReports);
                ContextManager.locateOnMainScreenCenter(rsmDlg);
                rsmDlg.setVisible(true);
                if (rsmDlg.isClick_ok()) {
                    selectedReports.clear();
                    selectedReports.addAll(rsmDlg.getSelectReports());
                    updateReportText();
                }
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath tp = fieldTree.getSelectionPath();
                if (tp == null || tp.getLastPathComponent() == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                addField(node);
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = ftableItem.getSelectObjects();
                if (list.isEmpty()) {
                    return;
                }
                removeItem(list);
            }
        });
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            if (fieldKeys.get(ci.getFieldName().replace("_code_", "")) != null) {
                ci.setDisplayName(fieldKeys.get(ci.getFieldName().replace("_code_", "")).getCaption_name());
                ftableItem.addObject(ci);
            }
        }
        if (changeScheme != null && changeScheme.getChangeItems() != null) {
            Enumeration enumt = ((DefaultMutableTreeNode) sftModel.getRoot()).breadthFirstEnumeration();
            List<DefaultMutableTreeNode> removeNodes = new ArrayList<DefaultMutableTreeNode>();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                Object obj = node.getUserObject();
                if (obj instanceof TempFieldInfo) {
                    TempFieldInfo tfi = (TempFieldInfo) obj;
                    for (Object ci_obj : ftableItem.getObjects()) {
                        ChangeItem ci = (ChangeItem) ci_obj;
                        if (ci.getFieldName().equals(tfi.getField_name())) {
                            removeNodes.add(node);
                            break;
                        }
                    }
                }
            }
            for (DefaultMutableTreeNode node : removeNodes) {
                node.removeFromParent();
            }
            DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) sftModel.getRoot()).getFirstChild();
            TreePath tp = new TreePath(selectNode.getPath());
            fieldTree.setSelectionPath(tp);
            fieldTree.expandPath(tp);
            fieldTree.updateUI();
        }
        ftableItem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlItem.updateUI();
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void addField(DefaultMutableTreeNode node) {
        if (node == null || node.getUserObject() == null || !(node.getUserObject() instanceof TempFieldInfo)) {
            return;
        }
        TempFieldInfo tfi = (TempFieldInfo) node.getUserObject();
        ChangeItem ci = (ChangeItem) UtilTool.createUIDEntity(ChangeItem.class);
        ci.setFieldName(tfi.getField_name());
        ci.setDisplayName(tfi.getCaption_name());
        ci.setChangeScheme(changeScheme);
        ci.setField_type(tfi.getField_type());
        ftableItem.addObject(ci);
        DefaultMutableTreeNode selectNode = node.getNextSibling();
        if (selectNode == null) {
            selectNode = node.getPreviousSibling();
        }
        if (selectNode == null) {
            selectNode = (DefaultMutableTreeNode) node.getParent();
        }
        node.removeFromParent();
        fieldTree.setSelectionPath(new TreePath(selectNode.getPath()));
        fieldTree.updateUI();
        ftableItem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlItem.updateUI();
    }

    private void removeItem(List list) {
        for (Object obj : list) {
            ChangeItem ci = (ChangeItem) obj;
            sftModel.addNode(fieldKeys.get(ci.getFieldName().replace("_code_", "")));
        }
        fieldTree.updateUI();
        ftableItem.deleteSelectedRows();
        ftableItem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlItem.updateUI();
    }

    private void updateReportText() {
        String text = "";
        String report_keys = "";
        for (ReportDef report : selectedReports) {
            text = report.getReport_name() + ";" + text;
            report_keys = report.getReportDef_key() + ";" + report_keys;
        }
        changeScheme.setReport_key(report_keys);
        jtf.setText(text);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlScheme = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pnlField = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        pnlItem = new javax.swing.JPanel();

        pnlScheme.setBorder(javax.swing.BorderFactory.createTitledBorder("模板信息："));
        pnlScheme.setLayout(new java.awt.BorderLayout());

        pnlField.setBorder(javax.swing.BorderFactory.createTitledBorder("人员基本信息表："));
        pnlField.setLayout(new java.awt.BorderLayout());

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/select_one.png"))); // NOI18N

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/remove_one.png"))); // NOI18N

        pnlItem.setBorder(javax.swing.BorderFactory.createTitledBorder("已选变动项目："));
        pnlItem.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(pnlField, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlItem, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(286, 286, 286))
            .addComponent(pnlField, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
            .addComponent(pnlItem, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlScheme, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlScheme, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlField;
    private javax.swing.JPanel pnlItem;
    private javax.swing.JPanel pnlScheme;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isValidate() {
        ftableItem.editingStopped();
        String msg = "";
        if ((changeScheme.getChangeScheme_name() == null) || (changeScheme.getChangeScheme_name().replace(" ", "").equals(""))) {
            msg = EmpChangeSchemeMsg.msg007.toString();
        } else {
            if (CommUtil.exists("select 1 from ChangeScheme where changeScheme_key<>'" + changeScheme.getChangeScheme_key() + "' and changeScheme_name='" + changeScheme.getChangeScheme_name() + "'")) {
                msg = "[" + changeScheme.getChangeScheme_name() + "]"+EmpChangeSchemeMsg.msg008.toString();
            }
        }
        if (ftableItem.getObjects().isEmpty()) {
            msg = EmpChangeSchemeMsg.msg009.toString();
        }
        if (!msg.equals("")) {
//            JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.INFORMATION_MESSAGE);
            MsgUtil.showInfoMsg(msg);
            return false;
        }
        return true;
    }

    @Override
    public void beforeLeave() {
        changeScheme.getChangeItems().clear();
        for (Object obj : ftableItem.getObjects()) {
            changeScheme.getChangeItems().add((ChangeItem) obj);
        }
        if (!changeScheme.contains("a0191")) {
            changeScheme.setNewPersonClassName(null);
        }
    }

    @Override
    public String getTitle() {
        return EmpChangeSchemeMsg.msg006.toString();
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
