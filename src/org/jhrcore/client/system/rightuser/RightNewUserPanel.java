/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RightNewUserPanel.java
 *
 * Created on 2011-7-26, 15:57:43
 */
package org.jhrcore.client.system.rightuser;

import com.foundercy.pf.control.table.FTable;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.personnel.comm.PersonContainer;
import org.jhrcore.client.util.PickA01Pnl;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.right.Role;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RightImpl;
import org.jhrcore.query3.QueryParamDialog;
import org.jhrcore.query3.QuerySchemeDialog;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.RoleModel;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class RightNewUserPanel extends javax.swing.JPanel {

    private JLabel lbl = new JLabel("查找:");
    private JTextField jtfText = new JTextField();
    private JButton btnAdd = new JButton("添加");
    private JButton btnDel = new JButton("删除");
    private JButton btnQuery = new JButton("查询");
    private JButton btnSelect = new JButton("直观挑选");
    private FTable ftableA01 = null;
    private HashSet<String> a01Keys = new HashSet<String>();
    private JTree tree;
    private Hashtable<String, List<String>> a01RoleKeys = new Hashtable<String, List<String>>();
    private List roles = null;
    private boolean forRole = false;
    private List<IPickWindowCloseListener> listeners = new ArrayList<IPickWindowCloseListener>();

    public void addPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public void delPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.remove(listener);
    }

    /** Creates new form RightNewUserPanel */
    public RightNewUserPanel(List roles, boolean forRole) {
        this.roles = roles;
        this.forRole = forRole;
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        toolbar.add(lbl);
        toolbar.add(jtfText);
        toolbar.add(btnAdd);
        toolbar.add(btnDel);
        toolbar.add(btnQuery);
        toolbar.add(btnSelect);
        ComponentUtil.setSize(jtfText, 120, 22);
        List<TempFieldInfo> allInfos = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> defaultInfos = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> deptInfos = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : deptInfos) {
            if (tfi.getField_name().equals("content")) {
                defaultInfos.add(tfi);
            }
            allInfos.add(tfi);
            tfi.setField_name("deptCode." + tfi.getField_name());
        }
        List<TempFieldInfo> a01Infos = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : a01Infos) {
            if (tfi.getField_name().equals("a0101") || tfi.getField_name().equals("a0190") || tfi.getField_name().equals("a0191")) {
                defaultInfos.add(tfi);
            }
            allInfos.add(tfi);
        }
        ftableA01 = new FTable(A01.class, true, false, false, "RightNewUserPanel");
        ftableA01.setAll_fields(allInfos, defaultInfos, new ArrayList(), "RightNewUserPanel");
        pnlA01.add(ftableA01);
        List<Role> useRoles = new ArrayList<Role>();
        for (Object obj : roles) {
            Role r = (Role) obj;
            if (r.getRole_key().equals(UserContext.role_id)) {
                continue;
            }
            useRoles.add(r);
        }
        tree = new JTree(new RoleModel(useRoles));
        pnlRole.add(new JScrollPane(tree));
        Enumeration enumt1 = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
        while (enumt1.hasMoreElements()) {
            CheckTreeNode cNode = (CheckTreeNode) enumt1.nextElement();
            Object obj = cNode.getUserObject();
            if (obj instanceof Role) {
                cNode.setSelected(forRole);
            }
        }
        HRRendererView.getRoleMap().initTree(tree, TreeSelectMod.nodeCheckMod);
        tree.setEditable(!forRole);
    }

    private void setupEvents() {
        btnSelect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> person_list = null;
                if (PersonContainer.getPersonContainer().isVisible()) {
                    person_list = PersonContainer.getPersonContainer().getFTable().getAllKeys();
                } else {
                    String s_where = "  not exists(select 1 from RoleA01 ra where ra.a01PassWord.a01=bp and ra.role.role_key<>'&&&'";
                    if (forRole) {
                        s_where += " and ra.role.role_key='" + ((Role) roles.get(0)).getRole_key() + "'";
                    } else if (!UserContext.isSA) {
                        s_where += " and ra.role.role_key='" + UserContext.role_id + "'";
                    } else {
                        s_where = " (1=1";
                    }
                    s_where += ")";
                    PickA01Pnl pnlPickPerson = new PickA01Pnl(s_where, a01Keys);
                    if (ModalDialog.doModal(JOptionPane.getFrameForComponent(btnSelect), pnlPickPerson, "加入员工")) {
                        person_list = pnlPickPerson.getFtable2().getAllKeys();
                    }
                }
                if (person_list == null || person_list.isEmpty()) {
                    return;
                }
                person_list.removeAll(a01Keys);
                addUser(person_list);
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quickSearch();
            }
        };
        jtfText.addActionListener(al);
        btnAdd.addActionListener(al);
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = ftableA01.getSelectObjects();
                if (list.isEmpty()) {
                    return;
                }
                for (Object obj : list) {
                    String key = ((A01) obj).getA01_key();
                    a01Keys.remove(key);
                    a01RoleKeys.remove(key);
                }
                ftableA01.deleteSelectedRows();
            }
        });
        btnQuery.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                QuerySchemeDialog schemeDlg = new QuerySchemeDialog(JOptionPane.getFrameForComponent(btnQuery), A01.class, "RightNewUserPanel.A01");
                ContextManager.locateOnScreenCenter(schemeDlg);
                schemeDlg.setVisible(true);
                if (schemeDlg.getQueryScheme() != null) {
                    if (!QueryParamDialog.ShowQueryParamDialog(btnQuery, schemeDlg.getQueryScheme())) {
                        return;
                    }
                    pickPerson(schemeDlg.getQueryScheme());
                }
            }
        });
        ftableA01.addListSelectionListener(new ListSelectionListener() {

            Object cur_obj = null;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (cur_obj == ftableA01.getCurrentRow()) {
                    return;
                }
                if (forRole) {
                    return;
                }
                if (cur_obj != null && ((A01) cur_obj).getA01_key() != null) {
                    List list = ComponentUtil.getCheckedObjs(tree);
                    List<String> roles = new ArrayList<String>();
                    for (Object obj : list) {
                        roles.add(((Role) obj).getRole_key());
                    }
                    a01RoleKeys.put(((A01) cur_obj).getA01_key(), roles);
                }
                cur_obj = ftableA01.getCurrentRow();
                A01 a01 = (A01) cur_obj;
                List list = (a01 == null || a01.getA01_key() == null) ? null : a01RoleKeys.get(a01.getA01_key());
                Enumeration enumt1 = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
                if (list == null || list.isEmpty()) {
                    while (enumt1.hasMoreElements()) {
                        CheckTreeNode cNode = (CheckTreeNode) enumt1.nextElement();
                        Object obj = cNode.getUserObject();
                        if (obj instanceof Role) {
                            cNode.setSelected(false);
                        }
                    }
                } else {
                    while (enumt1.hasMoreElements()) {
                        CheckTreeNode cNode = (CheckTreeNode) enumt1.nextElement();
                        Object obj = cNode.getUserObject();
                        if (obj instanceof Role && list.contains(((Role) obj).getRole_key())) {
                            cNode.setSelected(true);
                        }
                    }
                }
                tree.updateUI();
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String[]> users = new ArrayList<String[]>();
                if (forRole) {
                    String roleKey = ((Role) roles.get(0)).getRole_key();
                    for (Object obj : ftableA01.getObjects()) {
                        users.add(new String[]{((A01) obj).getA01_key(), roleKey});
                    }
                } else {
                    if (ftableA01.getCurrentRow() != null && ((A01) ftableA01.getCurrentRow()).getA01_key() != null) {
                        List list = ComponentUtil.getCheckedObjs(tree);
                        List<String> roles = new ArrayList<String>();
                        for (Object obj : list) {
                            roles.add(((Role) obj).getRole_key());
                        }
                        a01RoleKeys.put(((A01) ftableA01.getCurrentRow()).getA01_key(), roles);
                    }
                    for (Object obj : ftableA01.getObjects()) {
                        A01 a01 = (A01) obj;
                        List<String> rs = a01RoleKeys.get(a01.getA01_key());
                        if (rs == null || rs.isEmpty()) {
                            continue;
                        }
                        for (String r : rs) {
                            if (!UserContext.isSA && UserContext.hasRoleRight(r)) {
                                continue;
                            }
                            users.add(new String[]{a01.getA01_key(), r});
                        }
                    }
                }
                ValidateSQLResult result = RightImpl.addUser(users);
                if (result.getResult() == 0) {
                    ftableA01.deleteSelectedRows();
                    a01RoleKeys.clear();
                    a01Keys.clear();
                    for (IPickWindowCloseListener listener : listeners) {
                        listener.pickClose();
                    }
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }
        });
        CloseAction.doCloseAction(btnClose);
    }

    private void quickSearch() {
        String text = jtfText.getText();
        if (text == null || text.trim().equals("")) {
            return;
        }
        pickPerson(null);
    }

    private void pickPerson(QueryScheme queryScheme) {
        String hql = " select bp.a01_key from A01 bp";
        if (queryScheme == null) {
            String text = SysUtil.getQuickSearchText(jtfText.getText().toUpperCase());
            hql += " where (upper(bp.a0190) like '" + text + "' or upper(bp.a0101) like '" + text + "' or upper(bp.pydm) like '" + text + "')";
        } else {
            hql = hql + " where  bp in(" + queryScheme.buildHql("from A01 ed ") + ")";
        }
        hql += " and (" + UserContext.getDept_right_rea_str("bp.deptCode") + ") and (" + UserContext.getPerson_class_right_str(A01.class, "bp") + ") and bp.a0193=0";
//        hql += " and not exists(select 1 from RoleA01 ra where ra.a01PassWord.a01=bp";
//        if (forRole) {
//            hql += " and ra.role.role_key='" + ((Role) roles.get(0)).getRole_key() + "'";
//        }
//        hql += ")";
        List ll = CommUtil.fetchEntities(hql);
        List result_a01s = null;
        ll.removeAll(a01Keys);
        if (ll.size() > 1) {
            List<String> fields = new ArrayList<String>();
            fields.add("deptCode.dept_code");
            fields.add("deptCode.content");
            fields.add("a0190");
            fields.add("a0101");
            FTable tmp_table = new FTable(A01.class, fields, false, false, false, "RightNewUserPanel");
            PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 bp join fetch bp.deptCode where bp.a01_key in");
            tmp_table.setObjects(ll);
            if (!ModalDialog.doModal(btnAdd, tmp_table, "请选择")) {
                return;
            }
            result_a01s = tmp_table.getSelectKeys();
        } else if (ll.size() == 1) {
            result_a01s = ll;
        }
        addUser(result_a01s);
    }

    private void addUser(List<String> result_a01s) {
        if (result_a01s != null && result_a01s.size() > 0) {
            a01Keys.addAll(result_a01s);
            ftableA01.addObjects(result_a01s);
            int ind = ftableA01.getObjects().size() - 1;
            ftableA01.setRowSelectionInterval(ind, ind);
        }
        ftableA01.updateUI();
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
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        pnlA01 = new javax.swing.JPanel();
        pnlRole = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        jSplitPane1.setDividerLocation(400);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        pnlA01.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlA01.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
            .addComponent(pnlA01, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlA01, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel3);

        pnlRole.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlRole);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
        );

        btnSave.setText("保存");

        btnClose.setText("关闭");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(btnSave)
                .addGap(18, 18, 18)
                .addComponent(btnClose)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose)
                    .addComponent(btnSave))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnlA01;
    private javax.swing.JPanel pnlRole;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
