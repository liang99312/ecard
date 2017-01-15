/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RightUserPanel.java
 *
 * Created on 2011-7-12, 11:09:41
 */
package org.jhrcore.client.system.rightuser;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickPopupListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.A01PassWord;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.right.Role;
import org.jhrcore.entity.right.RoleA01;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.RightImpl;
import org.jhrcore.mutil.RightUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptPanel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.RoleModel;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class RightUserPanel extends javax.swing.JPanel implements IPickRightPersonListener, IModuleCode {

    private DeptPanel deptPanel;//部门树
    private JMenuItem miAddDept = new JMenuItem(" 授权");
    private JMenuItem miBackDept = new JMenuItem(" 收回");
    private HashSet<String> cur_role_dept = new HashSet<String>();
    private FTable ftableRole;//当前用户权限角色
    private FTable ftableA01;//当前角色对应人员
    private A01PassWord curA01 = null;
    private RoleA01 curRoleA01 = null;
    private String order_sql = "DeptCode.dept_code,A01.a0190";
    private String text = "@";
    private String module_code = "SysUser";
    private List roles = null;
    private JMenu setLoadType = new JMenu("设置登陆方式");
    private JMenuItem mi_pw = new JMenuItem("密码");
    private JMenuItem mi_finger = new JMenuItem("指纹");
    private JMenuItem mi_fpw = new JMenuItem("密码+指纹");

    /** Creates new form RightUserPanel */
    public RightUserPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        List<TempFieldInfo> all_infos = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> default_infos = new ArrayList<TempFieldInfo>();
        EntityBuilder.buildInfo(DeptCode.class, all_infos, default_infos, "a01.deptCode");
        EntityBuilder.buildInfo(A01.class, all_infos, default_infos, "a01");
        EntityBuilder.buildInfo(A01PassWord.class, all_infos, default_infos);
        ftableA01 = new FTable(A01PassWord.class, true, false, false, "RightPersonPanel");
        ftableA01.setAll_fields(all_infos, default_infos, "RightPersonPanel");
        ftableA01.setRight_allow_flag(true);
        ftableA01.removeItemByCodes("query;sum;replace");
        pnlPerson.add(ftableA01);
        ComponentUtil.setIcon(miAddDept, "give_right.png");
        ComponentUtil.setIcon(miBackDept, "refuse_right.png");
        ComponentUtil.setSysCompFuntion(miAddDept, module_code + ".miAddDept");
        ComponentUtil.setSysCompFuntion(miBackDept, module_code + ".miBackDept");
        deptPanel = new DeptPanel(UserContext.getDepts(false), 1);
        deptPanel.getPopupMenu().removeAll();
        deptPanel.getPopupMenu().add(miAddDept);
        deptPanel.getPopupMenu().add(miBackDept);
        HRRendererView.getDeptRightMap(deptPanel.getDeptTree()).initTree(deptPanel.getDeptTree());
        pnlDept.add(deptPanel, BorderLayout.CENTER);
        List<String> fields = new ArrayList<String>();
        fields.add("role.role_code");
        fields.add("role.role_name");
        fields.add("role.parent_code");
        ftableRole = new FTable(RoleA01.class, fields, false, false, false, "RightUserPanel");
        pnlRole.add(ftableRole);

        setLoadType.add(mi_pw);
        setLoadType.add(mi_finger);
        setLoadType.add(mi_fpw);
        ComponentUtil.setIcon(new Object[]{setLoadType, mi_pw, mi_finger, mi_fpw}, "blank");
        ftableA01.addPickPopupListener(new IPickPopupListener() {

            @Override
            public void addMenuItem(JPopupMenu pp) {
                if (UserContext.hasFunctionRight("SysUser.setLoadType")) {
                    pp.add(setLoadType);
                }
            }
        });
    }

    private void setupEvents() {
        mi_pw.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setLoad_type("密码");
            }
        });
        mi_finger.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setLoad_type("指纹");
            }
        });
        mi_fpw.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setLoad_type("密码+指纹");
            }
        });
        ftableA01.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                order_sql = SysUtil.getSQLOrderString(showScheme, order_sql, ftableA01.getAll_fields());
                fetchA01(ftableA01.getCur_query_scheme(), text);
            }
        });
        ftableA01.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchA01(qs, text);
            }
        });
        ftableA01.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (curA01 == ftableA01.getCurrentRow()) {
                    return;
                }
                curA01 = (A01PassWord) ftableA01.getCurrentRow();
                refresh();
                refreshMain();
            }
        });
        miAddDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                giveDeptRight(1);
            }
        });
        miBackDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                giveDeptRight(0);
            }
        });
        ftableRole.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (curRoleA01 == ftableRole.getCurrentRow()) {
                    return;
                }
                curRoleA01 = (RoleA01) ftableRole.getCurrentRow();
                refreshDeptRight(curRoleA01);
            }
        });
        ComponentUtil.setSysFuntionNew(this);
        ComponentUtil.setIcon(miAddDept, "give_right.png");
        ComponentUtil.setIcon(miBackDept, "refuse_right.png");
    }

    private void refreshMain() {
        if (curA01 == null) {
            return;
        }
        String sql = "from RoleA01 ra join fetch ra.role where ra.a01PassWord.a01PassWord_key='" + curA01.getA01PassWord_key() + "'"
                + "order by ra.role.role_code";
        ftableRole.setObjects(CommUtil.fetchEntities(sql));
    }

    private void refreshDeptRight(RoleA01 ra) {
        refreshA01DeptRight(ra);
        refreshDeptRight();
    }

    /**
     * 该方法用于对当前用户授予/取消部门权限
     * @param mod：1：授予；0：拒绝
     */
    private void giveDeptRight(int mod) {
        if (curRoleA01 == null) {
            return;
        }
        if (!UserContext.isSA && curRoleA01.getRoleA01_key().equals(UserContext.rolea01_key)) {
            return;
        }
        DefaultMutableTreeNode right_node = (DefaultMutableTreeNode) deptPanel.getDeptTree().getSelectionPath().getLastPathComponent();
        if (right_node == null) {
            return;
        }
        if (!UserContext.isSA) {
            String role_code = curRoleA01.getRole().getRole_code();
            boolean disable = role_code.equals(UserContext.cur_role.getRole_code());
            if (disable) {
                JOptionPane.showMessageDialog(null, "越权操作，您不具备当前选择角色的权限，不允许对用户进行授权操作");
                return;
            }
        }
        String deptCode_key = ((DeptCode) right_node.getUserObject()).getDeptCode_key();
        ValidateSQLResult result = RightUtil.defineDeptRight(deptCode_key, mod, curRoleA01.getRoleA01_key(), UserContext.isSA, UserContext.rolea01_key);
        if (result.getResult() == 0) {
            refreshA01DeptRight(curRoleA01);
            refreshDeptRight();
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    /**
     * 该方法用于查询系统用户
     * @param qs：为null时表示不使用查询方案
     */
    private void fetchA01(QueryScheme qs, String text) {
        ftableA01.setCur_query_scheme(qs);
        String sql = "select A01PassWord.a01PassWord_key from A01PassWord,A01,DeptCode where";
        sql += " A01PassWord.a01_key=A01.a01_key and A01.deptCode_key=DeptCode.deptCode_key ";
        sql += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
        sql += " and (" + UserContext.getPerson_class_right_str(A01.class, "A01") + ")";
        sql += " and A01.a0193=0 and exists(select 1 from RoleA01 ra where ra.a01PassWord_key=A01PassWord.a01Password_key "
                + "and ra.role_key<>'&&&' ";
        if (!UserContext.isSA) {
            sql += " and exists(select 1 from role r,role r1 where ra.role_key=r.role_key and r1.role_key='" + UserContext.role_id + "'";
            if (UserContext.sql_dialect.equals("sqlserver")) {
                sql += " and charindex(r1.role_code,r.role_code)=1";
            } else if (UserContext.sql_dialect.equals("oracle")) {
                sql += " and instr(r.role_code,r1.role_code)=1";
            } else {
                sql += " and locate(r1.role_code,r.role_code)=1";
            }
            sql += ")";
        }
        sql += ")";
        if (qs != null) {
            sql = " and A01PassWord.a01PassWord_key in(" + qs.buildSql() + ")";
        }
        if (text != null && !text.trim().equals("")) {
            sql += " and (upper(A01.a0101) like '" + text + "' or upper(A01.a0190) like '" + text + "' or upper(A01.pydm) like '" + text + "' or upper(A01.a0177) like '" + text + "')";
        }
        sql += " order by " + order_sql;
        List list = CommUtil.selectSQL(sql);
        PublicUtil.getProps_value().setProperty(A01PassWord.class.getName(), "from A01PassWord apw join fetch apw.a01 join fetch apw.a01.deptCode where apw.a01PassWord_key in");
        ftableA01.setObjects(list);
    }

    //刷新部门权限
    public void refreshDeptRight() {
        RightUtil.refreshDeptRight((DefaultMutableTreeNode) deptPanel.getDeptTree().getModel().getRoot(), cur_role_dept);
        deptPanel.getDeptTree().updateUI();
    }

    /**
     * 刷新指定权限人员的部门权限
     * @param a01pass：指定的权限人员
     */
    private void refreshA01DeptRight(RoleA01 a01pass) {
        cur_role_dept.clear();
        if (a01pass == null) {
            return;
        }
        List list = CommUtil.fetchEntities("select rd.deptCode.deptCode_key from RoleDept rd  where rd.roleA01.roleA01_key='" + a01pass.getRoleA01_key() + "'");
        for (Object obj : list) {
            cur_role_dept.add(obj.toString());
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

        jspMain = new javax.swing.JSplitPane();
        pnlPerson = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlRole = new javax.swing.JPanel();
        pnlDept = new javax.swing.JPanel();

        jspMain.setDividerLocation(220);
        jspMain.setDividerSize(3);

        pnlPerson.setLayout(new java.awt.BorderLayout());
        jspMain.setLeftComponent(pnlPerson);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(1);

        pnlRole.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlRole);

        pnlDept.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlDept);

        jspMain.setRightComponent(jSplitPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspMain, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspMain, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jspMain;
    private javax.swing.JPanel pnlDept;
    private javax.swing.JPanel pnlPerson;
    private javax.swing.JPanel pnlRole;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fetchData(List roles, String text, boolean refresh) {
        if (refresh || this.roles == null) {
            this.roles = roles;
            this.text = text;
            fetchA01(null, text);
        }
        refreshDeptRight();
        refresh();
    }

    @Override
    public void refresh() {
        deptPanel.updateUIView();
        ContextManager.setStatusBar(ftableA01.getObjects().size());
    }

    @Override
    public void defineRight(List roles) {
        if (curA01 == null) {
            return;
        }
        if (curA01.getA01PassWord_key().equals(UserContext.passKey)) {
            JOptionPane.showMessageDialog(null, "不能对自己进行授权");
            return;
        }
        List<String> users = new ArrayList<String>();
        users.add(curA01.getA01PassWord_key());
        List<String> roleKeys = new ArrayList<String>();
        List list = ftableRole.getAllObjects();
        for (Object obj : list) {
            roleKeys.add(((RoleA01) obj).getRole().getRole_key());
        }
        List<Role> mRoles = new ArrayList<Role>();
        for (Object obj : roles) {
            Role r = (Role) obj;
            if (r.getRole_key().equals(UserContext.role_id)) {
                continue;
            }
            mRoles.add(r);
        }
        JTree tree = new JTree(new RoleModel(mRoles));
        HashSet<String> set = new HashSet<String>();
        Enumeration enumt1 = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
        List<CheckTreeNode> expandNodes = new ArrayList<CheckTreeNode>();
        while (enumt1.hasMoreElements()) {
            CheckTreeNode cNode = (CheckTreeNode) enumt1.nextElement();
            Object obj = cNode.getUserObject();
            if (obj instanceof Role && roleKeys.contains(((Role) obj).getRole_key())) {
                cNode.setSelected(true);
                expandNodes.add(cNode);
                set.add(((Role) obj).getRole_key());
            }
        }
        HRRendererView.getRoleMap().initTree(tree, TreeSelectMod.nodeCheckMod);
        for (CheckTreeNode cn : expandNodes) {
            TreePath tp = new TreePath(cn.getPath());
            tree.addSelectionPath(tp);
            tree.scrollPathToVisible(tp);
        }
        tree.updateUI();
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(tree));
        pnl.setPreferredSize(new Dimension(350, 400));
        if (ModalDialog.doModal(ContextManager.getMainFrame(), pnl, "分配权限：")) {
            List result = ComponentUtil.getCheckedObjs(tree);
            List<String[]> data = new ArrayList<String[]>();
            for (Object obj : result) {
                Role r = (Role) obj;
                data.add(new String[]{curA01.getA01PassWord_key(), r.getRole_key(), "1"});
                set.remove(r.getRole_key());
            }
            for (String key : set) {
                data.add(new String[]{curA01.getA01PassWord_key(), key, "0"});
            }

            ValidateSQLResult vs = RightImpl.defineUserRole(data);
            if (vs.getResult() == 0) {
                refreshMain();
                JOptionPane.showMessageDialog(null, "保存成功");
            } else {
                MsgUtil.showHRSaveErrorMsg(vs);
            }
        }
    }

    @Override
    public void delUser() {
//        List<String> list = ftableA01.getSelectKeys();
//        if (list.isEmpty()) {
//            return;
//        }
//        if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(),
//                "确定要删除选中的用户吗", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
//            return;
//        }
//        ValidateSQLResult result = RightUtil.delUser(DbUtil.getQueryForMID("", list, "", ""), null);
//        if (result.getResult() == 0) {
//            ftableA01.deleteSelectedRows();
//        } else {
//            FormulaTextDialog.showErrorMsg(result.getMsg(), FormulaTextDialog.error_del_msg);
//        }
    }

    @Override
    public void registerFinger() {
        String temp_key = "-1";
        if (ftableA01.getCurrentRow() != null) {
            A01PassWord temp_apw = (A01PassWord) ftableA01.getCurrentRow();
            temp_key = temp_apw.getA01PassWord_key();
        }
//        RegisterFingerPanel panel = new RegisterFingerPanel(temp_key);
//        ModelFrame.showModel(ContextManager.getMainFrame(), panel, true, "采集指纹：", 550, 500);
    }

    @Override
    public void addUser() {
        RightNewUserPanel pnlRnu = new RightNewUserPanel(roles, false);
        pnlRnu.addPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                fetchData(roles, text, true);
            }
        });
        ModelFrame.showModel(ContextManager.getMainFrame(), pnlRnu, true, "新增用户：", 700, 600);
    }

    @Override
    public void setPass() {
        if (curA01 == null) {
            return;
        }
        SetPassWordDialog epDlg = new SetPassWordDialog(curA01.getA01());
        ContextManager.locateOnMainScreenCenter(epDlg);
        epDlg.setVisible(true);
    }

    //设置登陆方式
    private void setLoad_type(String l_type) {
        List<String> select_keys = ftableA01.getSelectKeys();
        if (select_keys.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请选择用户");
            return;
        }
        if (JOptionPane.showConfirmDialog(null, "确定将选择的用户的登陆方式设置成：" + l_type + "？", "询问",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        ValidateSQLResult result = CommUtil.excuteSQLs("update A01PassWord set load_type='" + l_type + "' where a01PassWord_key in", select_keys);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg("设置成功");
            fetchA01(ftableA01.getCur_query_scheme(), text);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
