/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RightRolePanel.java
 *
 * Created on 2011-7-24, 22:08:25
 */
package org.jhrcore.client.system.rightuser;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickPopupListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.A01PassWord;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.right.Role;
import org.jhrcore.entity.right.RoleA01;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.RightImpl;
import org.jhrcore.mutil.RightUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptPanel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.RoleModel;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.ui.renderer.RenderderMap;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class RightRolePanel extends javax.swing.JPanel implements IPickRightPersonListener, IModuleCode {

    private JTree roleTree = new JTree();//角色树
    private DefaultMutableTreeNode cur_node;//当前角色树节点
    private boolean init_flag = false;
    private FTable ftableA01 = null;
    private RoleA01 curA01 = null;
    private DeptPanel deptPanel = null;
//    private JPopupMenu popMenuSetRight = new JPopupMenu();
    private JMenuItem miAddDept = new JMenuItem(" 授权");
    private JMenuItem miBackDept = new JMenuItem(" 收回");
    private HashSet<String> cur_role_dept = new HashSet<String>();
    private RoleModel roleModel = null;
    private String text = "";
    private String module_code = "SysUser";
    private String order_sql = "DeptCode.dept_code,A01.a0190";
    private JMenu setLoadType = new JMenu("设置登陆方式");
    private JMenuItem mi_pw = new JMenuItem("密码");
    private JMenuItem mi_finger = new JMenuItem("指纹");
    private JMenuItem mi_fpw = new JMenuItem("密码+指纹");

    /** Creates new form RightRolePanel */
    public RightRolePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        ComponentUtil.setSysFuntionNew(this);
        ComponentUtil.setIcon(miAddDept, "give_right.png");
        ComponentUtil.setIcon(miBackDept, "refuse_right.png");
        RenderderMap map = new RenderderMap();
        map.setIcon("Role", "code");
        map.initTree(roleTree);
        pnlRole.setLayout(new BorderLayout());
        pnlRole.add(new JScrollPane(roleTree), BorderLayout.CENTER);
        ComponentUtil.setSysCompFuntion(miAddDept, module_code + ".miAddDept");
        ComponentUtil.setSysCompFuntion(miBackDept, module_code + ".miBackDept");
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
        roleTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) roleTree.getLastSelectedPathComponent();
                cur_node = node;
                if (cur_node == null) {
                    return;
                }
                initRolePerson();
                refreshRolePerson();
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
        ComponentUtil.refreshJSplitPane(jspMain, "RightRolePanel.jspMain");
        ComponentUtil.refreshJSplitPane(jSplitPane1, "RightRolePanel.jSplitPane1");
    }

    private void initRolePerson() {
        if (!init_flag) {
            init_flag = true;
            List<TempFieldInfo> all_infos = new ArrayList<TempFieldInfo>();
            List<TempFieldInfo> default_infos = new ArrayList<TempFieldInfo>();
            List<TempFieldInfo> default_order_infos = new ArrayList<TempFieldInfo>();
            List<TempFieldInfo> deptInfos = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
            for (TempFieldInfo tfi : deptInfos) {
                if (tfi.getField_name().equals("dept_full_name")) {
                    default_infos.add(tfi);
                } else if (tfi.getField_name().equals("dept_code")) {
                    default_order_infos.add(tfi);
                }
                all_infos.add(tfi);
                tfi.setField_name("a01PassWord.a01.deptCode." + tfi.getField_name());
            }
            List<TempFieldInfo> a01Infos = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
            for (TempFieldInfo tfi : a01Infos) {
                String fieldName = tfi.getField_name();
                if (fieldName.equals("a0190") || fieldName.equals("a0101")) {
                    default_infos.add(tfi);
                    if (fieldName.equals("a0190")) {
                        default_order_infos.add(tfi);
                    }
                }
                tfi.setField_name("a01PassWord.a01." + tfi.getField_name());
                all_infos.add(tfi);
            }
            List<TempFieldInfo> a01PassInfos = EntityBuilder.getCommFieldInfoListOf(A01PassWord.class, EntityBuilder.COMM_FIELD_VISIBLE);
            for (TempFieldInfo tfi : a01PassInfos) {
                tfi.setField_name("a01PassWord." + tfi.getField_name());
                all_infos.add(tfi);
            }
            ftableA01 = new FTable(RoleA01.class, true, false, false, "RightRolePanel");
            ftableA01.setAll_fields(all_infos, default_infos, default_order_infos, "RightRolePanel");
            ftableA01.setRight_allow_flag(true);
            ftableA01.removeItemByCodes("query;sum;replace");
            ftableA01.addPickFieldOrderListener(new IPickFieldOrderListener() {

                @Override
                public void pickOrder(ShowScheme showScheme) {
                    order_sql = SysUtil.getSQLOrderString(showScheme, "DeptCode.dept_code,A01.a0190", ftableA01.getAll_fields());
                    refreshRolePerson();
                }
            });
            ftableA01.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (curA01 == ftableA01.getCurrentRow()) {
                        return;
                    }
                    curA01 = (RoleA01) ftableA01.getCurrentRow();
                    refreshA01DeptRight((RoleA01) curA01);
                    refreshDeptRight();
                }
            });
            pnlPerson.add(ftableA01, BorderLayout.CENTER);
            pnlDeptRight.removeAll();
            deptPanel = new DeptPanel(UserContext.getDepts(false), 1);
            HRRendererView.getDeptRightMap(deptPanel.getDeptTree()).initTree(deptPanel.getDeptTree());
            pnlDeptRight.add(deptPanel, BorderLayout.CENTER);
            pnlDeptRight.updateUI();
            deptPanel.getPopupMenu().removeAll();
            deptPanel.getPopupMenu().add(miAddDept);
            deptPanel.getPopupMenu().add(miBackDept);
            ComponentUtil.initTreeSelection(roleTree);
            order_sql = SysUtil.getSQLOrderString(ftableA01.getCurOrderScheme(), "DeptCode.dept_code,A01.a0190", ftableA01.getAll_fields());

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
    }
    //刷新角色人员

    private void refreshRolePerson() {
        if (cur_node == null) {
            return;
        }
        Role role = null;
        boolean root_flag = false;
        if (cur_node == roleModel.getRoot()) {
            root_flag = true;
        } else if (cur_node.getUserObject() instanceof Role) {
            role = (Role) cur_node.getUserObject();
        }
        if (role == null && !root_flag) {
            ftableA01.setObjects(new ArrayList());
            return;
        }
        if (role == null && !UserContext.isSA) {
            return;
        }
        String hql = "select ra.roleA01_key from A01PassWord,A01,DeptCode,RoleA01 ra where ra.a01PassWord_key=A01PassWord.a01PassWord_key and A01PassWord.a01_key=A01.a01_key and A01.deptCode_key=DeptCode.deptCode_key and A01.a0193=0";
        hql += " and  ra.role_key<>'&&&'";
        if (!root_flag) {
            hql += " and ra.role_key='" + role.getRole_key() + "'";
        }
        if (text != null && !text.trim().equals("")) {
            hql += " and (upper(A01.a0190) like '" + text + "' or upper(A01.a0101) like '" + text + "' or upper(A01.pydm) like '" + text + "' or upper(A01.a0177) like '" + text + "')";
        }
        if (!UserContext.isSA && UserContext.hasRoleRight(role.getRole_key())) {
            hql += " and A01.a01_key ='" + UserContext.person_key + "'";
        } else {
            hql += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ") and (" + UserContext.getPerson_class_right_str(A01.class, "A01") + ")";
        }

        hql += " order by " + order_sql;
        List list = CommUtil.selectSQL(hql);
        PublicUtil.getProps_value().setProperty(RoleA01.class.getName(), "from RoleA01 ra join fetch ra.a01PassWord join fetch ra.a01PassWord.a01 join fetch ra.a01PassWord.a01.deptCode where ra.roleA01_key in");
        ftableA01.setObjects(list);
        refresh();
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
    //刷新部门权限

    public void refreshDeptRight() {
        RightUtil.refreshDeptRight((DefaultMutableTreeNode) deptPanel.getDeptTree().getModel().getRoot(), cur_role_dept);
        deptPanel.getDeptTree().updateUI();
    }

    /**
     * 该方法用于对当前用户授予/取消部门权限
     * @param mod：1：授予；0：拒绝
     */
    private void giveDeptRight(int mod) {
        if (curA01 == null) {
            return;
        }
        DefaultMutableTreeNode right_node = (DefaultMutableTreeNode) deptPanel.getDeptTree().getSelectionPath().getLastPathComponent();
        if (right_node == null) {
            return;
        }
        String deptCode_key = ((DeptCode) right_node.getUserObject()).getDeptCode_key();
        ValidateSQLResult result = RightUtil.defineDeptRight(deptCode_key, mod, curA01.getRoleA01_key(), UserContext.isSA, UserContext.isSA ? "-1" : UserContext.rolea01_key);
        if (result.getResult() == 0) {
            refreshA01DeptRight(curA01);
            refreshDeptRight();
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

        jspMain = new javax.swing.JSplitPane();
        pnlRole = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlPerson = new javax.swing.JPanel();
        pnlDeptRight = new javax.swing.JPanel();

        jspMain.setDividerLocation(200);
        jspMain.setDividerSize(3);

        pnlRole.setLayout(new java.awt.BorderLayout());
        jspMain.setLeftComponent(pnlRole);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(2);

        pnlPerson.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlPerson);

        pnlDeptRight.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlDeptRight);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
        );

        jspMain.setRightComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspMain, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspMain, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jspMain;
    private javax.swing.JPanel pnlDeptRight;
    private javax.swing.JPanel pnlPerson;
    private javax.swing.JPanel pnlRole;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fetchData(List roles, String text, boolean refresh) {
        this.text = text;
        if (roleModel == null) {
            roleModel = new RoleModel(roles);
            roleTree.setModel(roleModel);
            initRolePerson();
            refreshRolePerson();
        } else {
            if (refresh) {
                refreshRolePerson();
            }
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
    }

    @Override
    /**
     * 给当前节点对应角色增加人员
     * @param node：当前节点，当当前节点上对象不为角色时，不执行操作
     */
    public void addUser() {
        if (cur_node == null) {
            return;
        }
        Object obj = cur_node.getUserObject();
        if (obj == null || !(obj instanceof Role)) {
            return;
        }
        final Role role = (Role) obj;
        if (!UserContext.isSA && UserContext.hasRoleRight(role.getRole_key())) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "权限不足，不能增加",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List list = new ArrayList();
        list.add(role);
        RightNewUserPanel pnlRnu = new RightNewUserPanel(list, true);
        pnlRnu.addPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                fetchData(new ArrayList(), text, true);
            }
        });
        ModelFrame.showModel(ContextManager.getMainFrame(), pnlRnu, true, "新增用户：", 700, 600);
    }

    @Override
    public void delUser() {
        if (cur_node == null) {
            return;
        }
        Object obj = cur_node.getUserObject();
        if (obj == null || !(obj instanceof Role)) {
            return;
        }
        List<String> apw_keys = ftableA01.getSelectKeys();
        if (apw_keys.isEmpty()) {
            return;
        }
        if (apw_keys.size() > 500) {
            return;
        }
        final Role role = (Role) obj;
        if (!UserContext.isSA && UserContext.hasRoleRight(role.getRole_key())) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "权限不足，不能删除",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!UserContext.isSA && apw_keys.contains(UserContext.rolea01_key)) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "权限不足，不能删除",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(null, "确定要删除用户？", "提示", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        List<String> a01passKeys = new ArrayList();
        List list = ftableA01.getAllSelectObjects();
        for (Object row : list) {
            a01passKeys.add(((RoleA01) row).getA01PassWord().getA01PassWord_key());
        }
        ValidateSQLResult result = RightImpl.delUser(DbUtil.getQueryForMID("", apw_keys, "", ""), DbUtil.getQueryForMID("", a01passKeys, "", ""));
        if (result.getResult() == 0) {
            ftableA01.deleteSelectedRows();
        } else {
            MsgUtil.showHRDelErrorMsg(result);
        }
    }

    @Override
    public void setPass() {
        if (curA01 == null || curA01.getA01PassWord() == null) {
            return;
        }
        SetPassWordDialog epDlg = new SetPassWordDialog(curA01.getA01PassWord().getA01());
        ContextManager.locateOnMainScreenCenter(epDlg);
        epDlg.setVisible(true);
    }

    @Override
    public void registerFinger() {
        String temp_key = "-1";
        if (ftableA01.getCurrentRow() != null) {
            RoleA01 temp_apw = (RoleA01) ftableA01.getCurrentRow();
            if (temp_apw.getA01PassWord() != null) {
                temp_key = temp_apw.getA01PassWord().getA01PassWord_key();
            }
        }
//        RegisterFingerPanel panel = new RegisterFingerPanel(temp_key);
//        ModelFrame.showModel(ContextManager.getMainFrame(), panel, true, "采集指纹：", 550, 500);
    }

    //设置登陆方式
    private void setLoad_type(String l_type) {
        List<String> select_keys = new ArrayList<String>();
        if (ftableA01.getSelectObjects().isEmpty()) {
            JOptionPane.showMessageDialog(null, "请选择用户");
            return;
        }

        for (Object obj : ftableA01.getAllSelectObjects()) {
            RoleA01 temp_apw = (RoleA01) obj;
            if (temp_apw.getA01PassWord() != null) {
                select_keys.add(temp_apw.getA01PassWord().getA01PassWord_key());
            }
        }

        if (JOptionPane.showConfirmDialog(null, "确定将选择的用户的登陆方式设置成：" + l_type + "？", "询问",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        ValidateSQLResult result = CommUtil.excuteSQLs("update A01PassWord set load_type='" + l_type + "' where a01PassWord_key in", select_keys);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg("设置成功");
            refreshRolePerson();
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
