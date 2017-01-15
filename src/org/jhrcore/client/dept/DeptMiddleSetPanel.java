/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeptMiddleSetPanel.java
 *
 * Created on 2010-11-16, 18:14:01
 */
package org.jhrcore.client.dept;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.JCheckBoxList;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.ShowFieldTreeModel;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class DeptMiddleSetPanel extends javax.swing.JPanel implements IModuleCode {

    private FTable ftable;
    private JCheckBoxList jlsDeptLevel;
    private List deptLevelList = new ArrayList();
    private final String module_code = "DeptMng.miDeptMidSet";

    /** Creates new form DeptMiddleSetPanel */
    public DeptMiddleSetPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        ComponentUtil.setSysFuntion(this, module_code);
        List<String> fields = new ArrayList<String>();
        fields.add("字段名");
        fields.add("字段描述");
        fields.add("生成规则");
        ftable = new FTable(fields);
        pnlLeft.add(ftable, BorderLayout.CENTER);
        Object obj = CommUtil.fetchEntityBy("select max(grade) from DeptCode");
        int max_level = 1;
        max_level = (obj == null) ? max_level : Integer.valueOf(obj.toString());
        for (int i = 1; i <= max_level; i++) {
            deptLevelList.add("第" + i + "级");
        }
        jlsDeptLevel = new JCheckBoxList(deptLevelList);
        pnlRight.add(new JScrollPane(jlsDeptLevel));
        List list = CommUtil.fetchEntities("from SysParameter s where sysParameter_code like 'Dept.MidDept%'");
        for (Object s_obj : list) {
            SysParameter sp = (SysParameter) s_obj;
            Object[] row_obj = new Object[4];
            row_obj[0] = sp.getSysparameter_code().substring(12).replace("_code_", "");
            TempFieldInfo tfi = EntityBuilder.getTempFieldInfoByName(DeptCode.class.getName(), row_obj[0].toString(), true);
            if (tfi == null) {
                continue;
            }
            row_obj[1] = tfi.getCaption_name();
            row_obj[2] = sp.getSysparameter_value();
            row_obj[3] = sp.getSysParameter_key();
            ftable.addObject(row_obj);
        }
    }

    private void setupEvents() {
        ftable.addListSelectionListener(new ListSelectionListener() {

            Object cur_obj = null;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (cur_obj == ftable.getCurrentRow()) {
                    return;
                }
                cur_obj = ftable.getCurrentRow();
                if (cur_obj instanceof Object[]) {
                    Object[] objs = (Object[]) cur_obj;
                    String value = objs[2] == null ? "" : objs[2].toString();
                    String[] values = value.split("\\+");
                    HashSet<String> valueKeys = new HashSet<String>();
                    for (String v : values) {
                        valueKeys.add(v);
                    }
                    int len = deptLevelList.size();
                    jlsDeptLevel.ClearSelectAll();
                    for (int i = 0; i < len; i++) {
                        if (valueKeys.contains(deptLevelList.get(i))) {
                            jlsDeptLevel.CheckedItem(i);
                        }
                    }
                    jlsDeptLevel.updateUI();
                }
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<TempFieldInfo> dept_infos = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
                List<TempFieldInfo> middle_infos = new ArrayList<TempFieldInfo>();
                HashSet<String> exist_infos = new HashSet<String>();
                for (Object obj : ftable.getObjects()) {
                    Object[] row_obj = (Object[]) obj;
                    exist_infos.add(row_obj[0].toString());
                }
                for (TempFieldInfo tfi : dept_infos) {
                    if (tfi.getField_type().equals("String")) {
                        if (!tfi.getField_mark().equals(DeptMngMsg.msg019.toString())) {
                            continue;
                        }
                        if (exist_infos.contains(tfi.getField_name())) {
                            continue;
                        }
                        middle_infos.add(tfi);
                    }
                }
                ShowFieldTreeModel sfModel = new ShowFieldTreeModel(middle_infos);
                JTree tree = new JTree(sfModel);
                HRRendererView.getCommMap().initTree(tree);
                DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) sfModel.getRoot();
                while (selectNode.getChildCount() > 0) {
                    selectNode = (DefaultMutableTreeNode) selectNode.getFirstChild();
                }
                tree.setSelectionPath(new TreePath(selectNode.getPath()));
                tree.expandPath(new TreePath(selectNode.getPath()));
                tree.updateUI();
                tree.setShowsRootHandles(true);
                tree.setRootVisible(false);
                JPanel pnl = new JPanel(new BorderLayout());
                pnl.add(new JScrollPane(tree));
                pnl.setPreferredSize(new Dimension(350, 400));
                if (ModalDialog.doModal(btnAdd, pnl, DeptMngMsg.msg020.toString())) {
                    TreePath tp = tree.getSelectionPath();
                    if (tp == null || tp.getLastPathComponent() == null) {
                        return;
                    }
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                    if (node.getUserObject() instanceof TempFieldInfo) {
                        TempFieldInfo tfi = (TempFieldInfo) node.getUserObject();
                        SysParameter sp = new SysParameter();
                        sp.setSysParameter_key("Dept.MidDept" + tfi.getField_name());
                        sp.setSysparameter_code("Dept.MidDept" + tfi.getField_name());
                        sp.setSysparameter_name(DeptMngMsg.msg021.toString());
                        sp.setSysparameter_roleid(UserContext.person_name + "{" + UserContext.person_code + "}");
                        ValidateSQLResult result = CommUtil.saveOrUpdate(sp);
                        if (result.getResult() == 0) {
                            Object[] objs = new Object[4];
                            objs[0] = tfi.getField_name();
                            objs[1] = tfi.getCaption_name();
                            objs[3] = sp.getSysParameter_key();
//                            JOptionPane.showMessageDialog(null, "新增成功！");
                            MsgUtil.showInfoMsg(CommMsg.ADDSUSSESS_MESSAGE);
                            ftable.addObject(objs);
                            ftable.setRowSelectionInterval(ftable.getObjects().size() - 1, ftable.getObjects().size() - 1);
                        } else {
                            MsgUtil.showHRSaveErrorMsg(result);
                        }
                    }
                }
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = ftable.getCurrentRow();
                if (obj == null) {
                    return;
                }
                Object[] objs = (Object[]) obj;
                if (objs[3] == null || objs[3].toString().trim().equals("")) {
                    return;
                }
                List list = jlsDeptLevel.getCheckedObjects();
                String value = "";
                for (Object level_obj : list) {
                    value = value + "+" + level_obj;
                }
                if (!value.equals("")) {
                    value = value.substring(1);
                }
                ValidateSQLResult result = CommUtil.excuteSQL("update SysParameter set sysparameter_value='" + value + "' where sysparameter_key='" + objs[3] + "'");
                if (result.getResult() == 0) {
//                    JOptionPane.showMessageDialog(null, "保存成功！");
                    MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
                    objs[2] = value;
                    ftable.updateUI();
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = ftable.getSelectObjects();
                if (list.isEmpty()) {
                    return;
                }
                if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
                    return;
                }
                List<String> delKeys = new ArrayList<String>();
                for (Object obj : list) {
                    Object[] objs = (Object[]) obj;
                    delKeys.add(objs[3].toString());
                }
                ValidateSQLResult result = CommUtil.excuteSQLs("delete from SysParameter where sysparameter_key in ", delKeys);
                if (result.getResult() == 0) {
                    MsgUtil.showInfoMsg(CommMsg.DEL_MESSAGE);
                    ftable.deleteSelectedRows();
                } else {
                    MsgUtil.showHRDelErrorMsg(result);
                }
            }
        });
        btnCreate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = new ArrayList();
                if (jCheckBox1.isSelected()) {
                    list.addAll(ftable.getObjects());
                } else if (ftable.getCurrentRow() != null) {
                    list.add(ftable.getCurrentRow());
                }
                if (list.isEmpty()) {
                    return;
                }
                String sql = "";
                if (UserContext.sql_dialect.equals("sqlserver")) {
                    for (Object obj : list) {
                        Object[] objs = (Object[]) obj;
                        if (objs[2] == null || objs[2].toString().trim().equals("")) {
                            return;
                        }
                        sql += "update DeptCode set " + objs[0].toString() + "='';";
                        String[] plus_str = objs[2].toString().split("\\+");
                        int len = plus_str.length;
                        for (int i = 0; i < len; i++) {
                            String grade = plus_str[i].trim().replace("第", "").replace("级", "");
                            if (i == 0) {
                                sql += "update d1 set " + objs[0].toString() + "=d.content from deptcode d,deptcode d1 where d.grade=" + grade + " and charindex(d.dept_code,d1.dept_code)=1 and d1.grade>=" + grade + ";";
                            } else {
                                sql += "update d1 set " + objs[0].toString() + "=d1." + objs[0].toString() + "+'\\'+" + "d.content from deptcode d,deptcode d1 where d.grade=" + grade + " and charindex(d.dept_code,d1.dept_code)=1 and d1.grade>=" + grade + ";";
                            }
                        }
                    }
                } else {
                    for (Object obj : list) {
                        Object[] objs = (Object[]) obj;
                        if (objs[2] == null || objs[2].toString().trim().equals("")) {
                            return;
                        }
                        sql += "update DeptCode set " + objs[0].toString() + "='';";
                        String[] plus_str = objs[2].toString().split("\\+");
                        int len = plus_str.length;
                        for (int i = 0; i < len; i++) {
                            String grade = plus_str[i].trim().replace("第", "").replace("级", "");
                            if (i == 0) {
                                sql += "update DeptCode d1 set " + objs[0].toString() + "=(select d.content from deptcode d where d.grade=" + grade + " and instr(d1.dept_code,d.dept_code)=1) where d1.grade>=" + grade + ";";
                            } else {
                                sql += "update DeptCode d1 set " + objs[0].toString() + "=(select d1." + objs[0].toString() + "||'\\'||" + "d.content from deptcode d where d.grade=" + grade + " and instr(d1.dept_code,d.dept_code)=1) where d1.grade>=" + grade + ";";
                            }
                        }
                    }
                }

                ValidateSQLResult result = CommUtil.excuteSQLs(sql, ";");
                if (result.getResult() == 0) {
//                    JOptionPane.showMessageDialog(null, "生成成功,需刷新缓存或重新登录查看效果");
                    MsgUtil.showInfoMsg(DeptMngMsg.msgRebuidSuccess);
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
//                String sql = "update DeptCode set ";
            }
        });
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                ModelFrame.close((ModelFrame) JOptionPane.getFrameForComponent(btnClose));
                ModelFrame.close();
            }
        });
        ftable.setRowSelectionInterval(0, 0);
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
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAdd.setText("新增中间部门字段");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAdd);

        btnSave.setText("保存");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSave);

        btnDel.setText("删除");
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDel);

        btnCreate.setText("生成中间部门值");
        btnCreate.setFocusable(false);
        btnCreate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCreate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnCreate);

        jCheckBox1.setText("所有中间字段");
        jCheckBox1.setFocusable(false);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCheckBox1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jCheckBox1);

        btnClose.setText("关闭");

        jLabel1.setText("说明：");

        jLabel2.setText("1，默认只生成当前行的中间部门值，勾上所有中间字段，则全部重新生成");

        jLabel3.setText("2，当勾选多个级次部门名称时，中间自动加\\隔开");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addGap(61, 61, 61))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)))
                .addContainerGap(22, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        jSplitPane1.setDividerLocation(400);
        jSplitPane1.setDividerSize(2);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        pnlRight.setBorder(javax.swing.BorderFactory.createTitledBorder("对应级次的部门名称"));
        pnlRight.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlRight);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlRight;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
