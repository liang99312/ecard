/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RightPanel1.java
 *
 * Created on 2010-3-28, 16:29:37
 */
package org.jhrcore.client.system.right;

import org.jhrcore.ui.FuntionTreeModel;
import com.foundercy.pf.control.table.FTable;
import com.foundercy.pf.control.table.ITableCellEditable;
import com.foundercy.pf.control.table.RowChangeListner;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.report.ReportModel;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.Code;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.EntityClass;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.report.ReportDef;
import org.jhrcore.entity.right.FuntionRight;
import org.jhrcore.entity.right.Role;
import org.jhrcore.entity.right.RoleCode;
import org.jhrcore.entity.right.RoleEntity;
import org.jhrcore.entity.right.RoleField;
import org.jhrcore.entity.right.RoleFuntion;
import org.jhrcore.entity.right.RoleReport;
import org.jhrcore.entity.right.RoleRightTemp;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.ui.CodeSelectDialog;
import org.jhrcore.mutil.RightUtil;
import org.jhrcore.query3.QuerySchemePanel;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.RoleModel;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.ui.renderer.RenderderMap;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class RightPanel extends JPanel implements IModulePanel {

    private JButton btnAdd = new JButton("增加角色或组");
    private JButton btnDel = new JButton("删除");
    private JCheckBox cxboxCopy = new JCheckBox("同步应用子级用户");
    private JTree roleTree = new JTree();//角色树
    private JTree reportTree = new JTree();//报表树
    private JTree funtionTree = new JTree();//功能权限树
    private JTree fieldTree = new JTree();//表及字段树
    private JPopupMenu jpopmenu = new JPopupMenu();
    private JPopupMenu popMenuSetRight = new JPopupMenu();
    private JMenuItem addItem = new JMenuItem("增加角色或组");
    private JMenuItem change = new JMenuItem("修改角色或组属性");
    private JMenuItem copyRight = new JMenuItem("复制指定角色权限");
    private JMenuItem flashRight = new JMenuItem("同步应用二级角色");
    private JMenuItem addRightItem = new JMenuItem(" 授权");
    private JMenuItem viewRightItem = new JMenuItem(" 查看");
    private JMenuItem backRightItem = new JMenuItem(" 收回");
    private DefaultMutableTreeNode cur_node;//当前角色树节点
    private int cur_tabIndex = 0;//当前选项卡索引
    private FTable codeTable;//编码权限
    private FTable entityTable;//记录集权限
    private RebuildTreeModel rebuildTreeModel;
    private JTree moduleTree = null;
    private Hashtable<String, RoleFuntion> cur_role_funtion = new Hashtable<String, RoleFuntion>();
    private Hashtable<String, RoleField> cur_role_field = new Hashtable<String, RoleField>();
    private Hashtable<String, RoleReport> cur_role_report = new Hashtable<String, RoleReport>();
    private Hashtable<String, RoleCode> cur_role_code = new Hashtable<String, RoleCode>();
    private Hashtable<String, RoleCode> p_role_code = new Hashtable<String, RoleCode>();
    private Hashtable<String, RoleEntity> cur_role_entity = new Hashtable<String, RoleEntity>();
    private Hashtable<String, RoleEntity> p_role_entity = new Hashtable<String, RoleEntity>();
    private int funtion_change_flag = 1;
    private int field_change_flag = 0;
    private int entity_change_flag = 0;
    private int code_change_flag = 0;
    private int report_change_flag = 0;
    private boolean copy_flag = false;
    private RoleModel roleModel;
    private MouseAdapter ma_right = null;
    private ActionListener al_eadd = null;
    private ActionListener al_eedit = null;
    private ActionListener al_eview = null;
    private ActionListener al_edel = null;
    private List field_module = null;
    private List report_module = null;
    private List<ReportDef> reports = null;
    public static final String module_code = "SysRight";

    /** Creates new form rightPanel */
    public RightPanel() {
        super(new BorderLayout());
        initComponents();
        initOthers();
        setupEvents();
    }

    @Override
    public void setFunctionRight() {
        ComponentUtil.setSysCompFuntion(addItem, "SysRight.btnAdd");
        ComponentUtil.setIcon(addRightItem, "give_right.png");
        ComponentUtil.setIcon(viewRightItem, "view_right.png");
        ComponentUtil.setIcon(backRightItem, "refuse_right.png");
        btnSetEntity.setEnabled(UserContext.hasFunctionRight("SysRight.pnlEntityRightMain"));
        btnSetCode.setEnabled(UserContext.hasFunctionRight("SysRight.pnlCodeRightMain"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spPaneMain = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        pnl1 = new javax.swing.JTabbedPane();
        pnlFuntionRight = new javax.swing.JPanel();
        pnlFieldRight = new javax.swing.JPanel();
        pnlEntityRightMain = new javax.swing.JPanel();
        jspEntity = new javax.swing.JSplitPane();
        pnlEntityLeft = new javax.swing.JPanel();
        pnlEntityRight = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        e_addFlag = new javax.swing.JCheckBox();
        e_editFlag = new javax.swing.JCheckBox();
        e_viewFlag = new javax.swing.JCheckBox();
        e_delFlag = new javax.swing.JCheckBox();
        btnSetEntity = new javax.swing.JButton();
        pnlEntityTable = new javax.swing.JPanel();
        pnlCodeRightMain = new javax.swing.JPanel();
        pnlCodeRight = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        c_addFlag = new javax.swing.JCheckBox();
        c_editFlag = new javax.swing.JCheckBox();
        c_viewFlag = new javax.swing.JCheckBox();
        c_delFlag = new javax.swing.JCheckBox();
        btnSetCode = new javax.swing.JButton();
        pnlReport = new javax.swing.JPanel();
        pnlRole = new javax.swing.JPanel();

        spPaneMain.setDividerLocation(200);
        spPaneMain.setDividerSize(3);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        pnl1.setPreferredSize(new java.awt.Dimension(469, 549));

        pnlFuntionRight.setLayout(new java.awt.BorderLayout());
        pnl1.addTab("操作功能权限", pnlFuntionRight);

        pnlFieldRight.setLayout(new java.awt.BorderLayout());
        pnl1.addTab("表中字段权限", pnlFieldRight);

        jspEntity.setDividerLocation(200);

        pnlEntityLeft.setLayout(new java.awt.BorderLayout());
        jspEntity.setLeftComponent(pnlEntityLeft);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setText("权限类别：");
        jToolBar1.add(jLabel1);

        e_addFlag.setText("新增");
        e_addFlag.setFocusable(false);
        e_addFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        e_addFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(e_addFlag);

        e_editFlag.setText("编辑");
        e_editFlag.setFocusable(false);
        e_editFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        e_editFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(e_editFlag);

        e_viewFlag.setText("查看");
        e_viewFlag.setFocusable(false);
        e_viewFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        e_viewFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(e_viewFlag);

        e_delFlag.setText("删除");
        e_delFlag.setFocusable(false);
        e_delFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        e_delFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(e_delFlag);

        btnSetEntity.setText("授权");
        btnSetEntity.setFocusable(false);
        btnSetEntity.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSetEntity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSetEntity);

        pnlEntityTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlEntityTable.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlEntityRightLayout = new javax.swing.GroupLayout(pnlEntityRight);
        pnlEntityRight.setLayout(pnlEntityRightLayout);
        pnlEntityRightLayout.setHorizontalGroup(
            pnlEntityRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
            .addComponent(pnlEntityTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );
        pnlEntityRightLayout.setVerticalGroup(
            pnlEntityRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEntityRightLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlEntityTable, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
        );

        jspEntity.setRightComponent(pnlEntityRight);

        javax.swing.GroupLayout pnlEntityRightMainLayout = new javax.swing.GroupLayout(pnlEntityRightMain);
        pnlEntityRightMain.setLayout(pnlEntityRightMainLayout);
        pnlEntityRightMainLayout.setHorizontalGroup(
            pnlEntityRightMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspEntity, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
        );
        pnlEntityRightMainLayout.setVerticalGroup(
            pnlEntityRightMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspEntity, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
        );

        pnl1.addTab("表记录增删权限", pnlEntityRightMain);

        pnlCodeRightMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnlCodeRight.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlCodeRight.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        jLabel2.setText("权限类别：");
        jToolBar2.add(jLabel2);

        c_addFlag.setText("新增");
        c_addFlag.setFocusable(false);
        c_addFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        c_addFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(c_addFlag);

        c_editFlag.setText("编辑");
        c_editFlag.setFocusable(false);
        c_editFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        c_editFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(c_editFlag);

        c_viewFlag.setText("查看");
        c_viewFlag.setFocusable(false);
        c_viewFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        c_viewFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(c_viewFlag);

        c_delFlag.setText("删除");
        c_delFlag.setFocusable(false);
        c_delFlag.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        c_delFlag.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(c_delFlag);

        btnSetCode.setText("授权");
        btnSetCode.setFocusable(false);
        btnSetCode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSetCode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnSetCode);

        javax.swing.GroupLayout pnlCodeRightMainLayout = new javax.swing.GroupLayout(pnlCodeRightMain);
        pnlCodeRightMain.setLayout(pnlCodeRightMainLayout);
        pnlCodeRightMainLayout.setHorizontalGroup(
            pnlCodeRightMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCodeRight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
        );
        pnlCodeRightMainLayout.setVerticalGroup(
            pnlCodeRightMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCodeRightMainLayout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pnlCodeRight, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
        );

        pnl1.addTab("关联代码权限", pnlCodeRightMain);

        pnlReport.setLayout(new java.awt.BorderLayout());
        pnl1.addTab("报表权限", pnlReport);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnl1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 446, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnl1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spPaneMain.setRightComponent(jPanel1);

        javax.swing.GroupLayout pnlRoleLayout = new javax.swing.GroupLayout(pnlRole);
        pnlRole.setLayout(pnlRoleLayout);
        pnlRoleLayout.setHorizontalGroup(
            pnlRoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
        );
        pnlRoleLayout.setVerticalGroup(
            pnlRoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 477, Short.MAX_VALUE)
        );

        spPaneMain.setLeftComponent(pnlRole);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSetCode;
    private javax.swing.JButton btnSetEntity;
    private javax.swing.JCheckBox c_addFlag;
    private javax.swing.JCheckBox c_delFlag;
    private javax.swing.JCheckBox c_editFlag;
    private javax.swing.JCheckBox c_viewFlag;
    private javax.swing.JCheckBox e_addFlag;
    private javax.swing.JCheckBox e_delFlag;
    private javax.swing.JCheckBox e_editFlag;
    private javax.swing.JCheckBox e_viewFlag;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JSplitPane jspEntity;
    private javax.swing.JTabbedPane pnl1;
    private javax.swing.JPanel pnlCodeRight;
    private javax.swing.JPanel pnlCodeRightMain;
    private javax.swing.JPanel pnlEntityLeft;
    private javax.swing.JPanel pnlEntityRight;
    private javax.swing.JPanel pnlEntityRightMain;
    private javax.swing.JPanel pnlEntityTable;
    private javax.swing.JPanel pnlFieldRight;
    private javax.swing.JPanel pnlFuntionRight;
    private javax.swing.JPanel pnlReport;
    private javax.swing.JPanel pnlRole;
    private javax.swing.JSplitPane spPaneMain;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    private void initToolBar() {
        toolbar.add(btnAdd);
        toolbar.add(btnDel);
        toolbar.add(cxboxCopy);
    }

    private void initOthers() {
        initToolBar();
        jpopmenu.add(addItem);
        jpopmenu.addSeparator();
        jpopmenu.add(change);
        jpopmenu.addSeparator();
        jpopmenu.add(copyRight);
        jpopmenu.add(flashRight);
        popMenuSetRight.add(addRightItem);
        popMenuSetRight.add(backRightItem);
        popMenuSetRight.add(viewRightItem);
        //初始化左边的树
        roleModel = new RoleModel();
        roleTree.setModel(roleModel);
        roleTree.setSelectionRow(2);
        RenderderMap map = new RenderderMap();
        map.setIcon("Role", "code");
        map.initTree(roleTree);
        pnlRole.setLayout(new BorderLayout());
        pnlRole.add(new JScrollPane(roleTree), BorderLayout.CENTER);
        funtionTree.setModel(new FuntionTreeModel());
        HRRendererView.getFunRightMap(funtionTree).initTree(funtionTree, TreeSelectMod.nodeManySelectMod);
        pnlFuntionRight.add(new JScrollPane(funtionTree), BorderLayout.CENTER);
    }

    private void setupEvents() {
        pnl1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                cur_tabIndex = pnl1.getSelectedIndex();
                refreshRoleTree(cur_node, cur_tabIndex);
                refresh();
            }
        });
        cxboxCopy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                copy_flag = !copy_flag;
            }
        });
        ActionListener al_add = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                defineRight(cur_node, 1);
            }
        };
        addRightItem.addActionListener(al_add);
        ActionListener al_back = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                defineRight(cur_node, 0);
            }
        };
        backRightItem.addActionListener(al_back);
        viewRightItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                defineRight(cur_node, 2);
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
                initPanelChangeFlag();
                refreshRoleTree(cur_node, cur_tabIndex);
            }
        });
        roleTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    jpopmenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        ActionListener al_add_role = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_node == null) {
                    return;
                }
                Object role = cur_node.getUserObject();
                if (!UserContext.isSA && role instanceof String) {
                    return;
                }
                editRole(role, true);
            }
        };
        btnAdd.addActionListener(al_add_role);
        addItem.addActionListener(al_add_role);
        change.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_node == null) {
                    return;
                }
                Object role = cur_node.getUserObject();
                if (role instanceof Role) {
                    editRole(role, false);
                }
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delObject();
            }
        });
        ma_right = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popMenuSetRight.remove(viewRightItem);
                    if (cur_tabIndex == 1 || cur_tabIndex == 4) {
                        popMenuSetRight.add(viewRightItem, 1);
                    }
                    popMenuSetRight.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        if (UserContext.hasFunctionRight("SysRight.pnlFuntionRight")) {
            funtionTree.addMouseListener(ma_right);
        }
        copyRight.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_node == null) {
                    return;
                }
                if (cur_node == roleTree.getModel().getRoot()) {
                    return;
                }
                final Role role = (Role) cur_node.getUserObject();
                CopyRightDialog crDialog = new CopyRightDialog(role);
                ContextManager.locateOnMainScreenCenter(crDialog);
                crDialog.setTitle("复制权限");
                crDialog.setVisible(true);
                if (crDialog.isClick_ok()) {
                    initPanelChangeFlag();
                    refreshRoleTree(cur_node, cur_tabIndex);
                }
            }
        });
        flashRight.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_node == null) {
                    return;
                }
                if (cur_node == roleTree.getModel().getRoot()) {
                    return;
                }
                Role role = (Role) cur_node.getUserObject();
                List<Role> roles = new ArrayList<Role>();
                Enumeration enumt = cur_node.children();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                    roles.add((Role) node.getUserObject());
                }
                if (roles.size() > 0) {
                    CopyParentRightDialog cprDlg = new CopyParentRightDialog(role, roles);
                    ContextManager.locateOnMainScreenCenter(cprDlg);
                    cprDlg.setVisible(true);
                    if (cprDlg.isClick_ok()) {
                        initPanelChangeFlag();
                        refreshRoleTree(cur_node, cur_tabIndex);
                    }
                }
            }
        });
        c_addFlag.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectCode("add");
            }
        });
        c_editFlag.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectCode("edit");
            }
        });
        c_viewFlag.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectCode("view");
            }
        });
        c_delFlag.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectCode("del");
            }
        });
        al_eadd = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectEntity("add");
            }
        };
        e_addFlag.addActionListener(al_eadd);
        al_eedit = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectEntity("edit");
            }
        };
        e_editFlag.addActionListener(al_eedit);
        al_edel = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectEntity("del");
            }
        };
        e_delFlag.addActionListener(al_edel);
        al_eview = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectEntity("view");
            }
        };
        e_viewFlag.addActionListener(al_eview);
        ActionListener al_right = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                defineRight(cur_node, 1);
            }
        };
        btnSetCode.addActionListener(al_right);
        btnSetEntity.addActionListener(al_right);
        ComponentUtil.refreshJSplitPane(spPaneMain, "JSplitPane.RightPanel.spPaneMain");
        roleTree.expandRow(1);
        roleTree.setSelectionRow(0);
    }

    private void selectEntity(String code) {
        List list = entityTable.getSelectObjects();
        for (Object obj : list) {
            RoleEntity rc = (RoleEntity) obj;
            List<String> r_fields = getEntityRightFields(rc);
            if (code.equals("add") && r_fields.contains("add_flag")) {
                rc.setAdd_flag(e_addFlag.isSelected());
            } else if (code.equals("edit") && r_fields.contains("edit_flag")) {
                rc.setEdit_flag(e_editFlag.isSelected());
            } else if (code.equals("del") && r_fields.contains("del_flag")) {
                rc.setDel_flag(e_delFlag.isSelected());
            } else if (code.equals("view") && r_fields.contains("view_flag")) {
                rc.setView_flag(e_viewFlag.isSelected());
            }
            rc.setView_flag(rc.isView_flag() || rc.isAdd_flag() || rc.isDel_flag() || rc.isEdit_flag());
        }
        entityTable.updateUI();
    }

    private void selectCode(String code) {
        List list = codeTable.getSelectObjects();
        for (Object obj : list) {
            RoleCode rc = (RoleCode) obj;
            List<String> r_fields = getCodeRightFields(rc);
            if (code.equals("add") && r_fields.contains("add_flag")) {
                rc.setAdd_flag(c_addFlag.isSelected());
            } else if (code.equals("edit") && r_fields.contains("edit_flag")) {
                rc.setEdit_flag(c_editFlag.isSelected());
            } else if (code.equals("del") && r_fields.contains("del_flag")) {
                rc.setDel_flag(c_delFlag.isSelected());
            } else if (code.equals("view") && r_fields.contains("view_flag")) {
                rc.setView_flag(c_viewFlag.isSelected());
            }
        }
        codeTable.updateUI();
    }

    /**
     * 新增/编辑角色主函数
     * @param role：指定角色
     * @param isNew：true：新增；false：编辑
     */
    private void editRole(Object role, boolean isNew) {
        RoleEditDlg reDlg = new RoleEditDlg(ContextManager.getMainFrame(), role, isNew);
        ContextManager.locateOnMainScreenCenter(reDlg);
        reDlg.setVisible(true);
        if (reDlg.isClick_ok()) {
            Role cur_role = reDlg.getCur_role();
            if (isNew) {
                cur_node.add(new DefaultMutableTreeNode(cur_role));
            } else {
                cur_node.setUserObject(cur_role);
            }
            roleTree.updateUI();
        }
    }

    private void initPanelChangeFlag() {
        funtion_change_flag = 1;
        if (field_change_flag != 0) {
            field_change_flag = 1;
        }
        if (code_change_flag != 0) {
            code_change_flag = 1;
        }
        if (entity_change_flag != 0) {
            entity_change_flag = 1;
        }
        if (report_change_flag != 0) {
            report_change_flag = 1;
        }
    }

    /**
     * 授权入口函数
     * @param node：当前角色节点
     * @param tabIndex：当前权限卡片索引
     */
    private void defineRight(DefaultMutableTreeNode node, int mod) {
        if (mod == 2 && cur_tabIndex != 0 && cur_tabIndex != 1 && cur_tabIndex != 4) {
            return;
        }
        final Role role = getCurrenRole(node);
        if (role == null) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "权限不足，不能授权",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (cur_tabIndex == 0) {
            doFunRight(node, role, mod);
        } else if (cur_tabIndex == 1) {
            doFieldRight(node, role, mod);
        } else if (cur_tabIndex == 2) {
            entityTable.editingStopped();
            RightUtil.giveEntityRight(UserContext.role_id, node, entityTable.getSelectObjects(), mod, copy_flag);
            refreshCurRoleEntity(role);
            refreshEntityRight();
        } else if (cur_tabIndex == 3) {
            codeTable.editingStopped();
            RightUtil.giveCodeRight(UserContext.role_id, node, codeTable.getSelectObjects(), mod, copy_flag);
        } else if (cur_tabIndex == 4) {
            doReportRight(node, role, mod);
        }
    }

    private void doFunRight(DefaultMutableTreeNode node, Role role, int mod) {
        List<Role> sel_roles = RightUtil.getRole(node, copy_flag, mod);
        if (sel_roles == null || sel_roles.isEmpty()) {
            return;
        }
        List sel_nodes = getSelectObjs(cur_tabIndex);
        Hashtable<String, Hashtable<String, RoleFuntion>> rfs = new Hashtable<String, Hashtable<String, RoleFuntion>>();
        Hashtable<String, RoleFuntion> rf = new Hashtable<String, RoleFuntion>();
        for (String key : cur_role_funtion.keySet()) {
            rf.put(key, cur_role_funtion.get(key));
        }
        rfs.put(role.getRole_key(), rf);
        boolean success = RightUtil.giveFuntionRight(sel_roles, sel_nodes, mod, rfs, (DefaultMutableTreeNode) funtionTree.getModel().getRoot());
        if (success) {
            for (String key : rf.keySet()) {
                cur_role_funtion.put(key, rf.get(key));
            }
        }
        refreshFuntionRight();
    }

    private void doFieldRight(DefaultMutableTreeNode node, Role role, int mod) {
        List<Role> sel_roles = RightUtil.getRole(node, copy_flag, mod);
        if (sel_roles == null || sel_roles.isEmpty()) {
            return;
        }
        List sel_nodes = getSelectObjs(cur_tabIndex);
        Hashtable<String, Hashtable<String, RoleField>> rfs = new Hashtable<String, Hashtable<String, RoleField>>();
        Hashtable<String, RoleField> rf = new Hashtable<String, RoleField>();
        for (String key : cur_role_field.keySet()) {
            rf.put(key, cur_role_field.get(key));
        }
        rfs.put(role.getRole_key(), rf);
        boolean success = RightUtil.giveFieldRight(sel_roles, sel_nodes, mod, rfs, (DefaultMutableTreeNode) fieldTree.getModel().getRoot());
        if (success) {
            for (String key : rf.keySet()) {
                cur_role_field.put(key, rf.get(key));
            }
        }
        refreshFieldRight();
    }

    private void doReportRight(DefaultMutableTreeNode node, Role role, int mod) {
        List<Role> sel_roles = RightUtil.getRole(node, copy_flag, mod);
        if (sel_roles == null || sel_roles.isEmpty()) {
            return;
        }
        List sel_nodes = getSelectObjs(cur_tabIndex);
        Hashtable<String, Hashtable<String, RoleReport>> rfs = new Hashtable<String, Hashtable<String, RoleReport>>();
        Hashtable<String, RoleReport> rf = new Hashtable<String, RoleReport>();
        for (String key : cur_role_report.keySet()) {
            rf.put(key, cur_role_report.get(key));
        }
        rfs.put(role.getRole_key(), rf);
        boolean success = RightUtil.giveReportRight(sel_roles, sel_nodes, mod, rfs, (DefaultMutableTreeNode) reportTree.getModel().getRoot());
        if (success) {
            for (String key : rf.keySet()) {
                cur_role_report.put(key, rf.get(key));
            }
        }
        refreshReportRight();
    }

    /**
     * 该方法用于获取当前选择并且可以授权的角色
     * @param tabIndex：当前TAB页索引
     * @param node：当前角色树选择节点
     * @return：角色 ，为NULL表示当前节点不允许授权
     */
    private Role getCurrenRole(DefaultMutableTreeNode node) {
        if (node == null) {
            return null;
        }
        Object obj = node.getUserObject();
        if (obj == null) {
            return null;
        }
        if (obj instanceof Role) {
            Role role = (Role) obj;
            if ("&&&".equals(role.getRole_name())) {
                if (!(cur_tabIndex == 0 || cur_tabIndex == 1 || cur_tabIndex == 2 || cur_tabIndex == 4)) {
                    return null;
                }
            }
            if (!UserContext.isSA && UserContext.hasRoleRight(role.getRole_key())) {
                return null;
            }
            return role;
        }
        return null;
    }

    private List getSelectObjs(int tabIndex) {
        List list = new ArrayList();
        TreePath[] tps = null;
        if (tabIndex == 0) {
            tps = funtionTree.getSelectionPaths();
        } else if (tabIndex == 1) {
            tps = fieldTree.getSelectionPaths();
        } else if (tabIndex == 4) {
            tps = reportTree.getSelectionPaths();
        }
        if (tps != null) {
            for (TreePath tp : tps) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                if (node != null) {
                    list.add(node);
                }
            }
        }
        return list;
    }

    /**
     * 删除当前角色
     */
    private void delObject() {
        if (cur_node == null) {
            return;
        }
        if (cur_node.getChildCount() > 0) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "该角色下有子角色，请删除子角色后再做此操作", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object obj = cur_node.getUserObject();
        if (obj instanceof Role) {
            if ("&&&".equals(((Role) obj).getRole_key()) || "OutUser".equals(((Role) obj).getRole_key())) {
                JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "该角色为系统角色，不能删除", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Role tmp_role = (Role) obj;
            if (UserContext.role_id.equals(tmp_role.getRole_key())) {
                JOptionPane.showMessageDialog(null, "不能删除自己所在的角色。");
                return;
            }
            if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(),
                    "确定要删除[" + ((Role) obj).getRole_name() + "]吗", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }

            String hql = "delete from workflowa01 where a01password_key in (select apw.a01password_key from a01password apw,RoleA01 ra where ra.a01password_key=apw.a01password_key and ra.role_key ='" + tmp_role.getRole_key() + "');";
            hql += "delete from RoleDept where exists(select 1 from rolea01 ra where ra.rolea01_key=RoleDept.rolea01_key and ra.role_key='" + tmp_role.getRole_key() + "');";
            hql += "delete from rolea01 where role_key ='" + tmp_role.getRole_key() + "';";
            hql += "delete from RoleEntity where role_key='" + tmp_role.getRole_key() + "';";
            hql += "delete from RoleField where role_key='" + tmp_role.getRole_key() + "';";
            hql += "delete from RoleFuntion where role_key='" + tmp_role.getRole_key() + "';";
            hql += "delete from RoleCode where role_key='" + tmp_role.getRole_key() + "';";
            hql += "delete from RoleReport where role_key='" + tmp_role.getRole_key() + "';";
            hql += "delete from Role where role_key='" + tmp_role.getRole_key() + "';";
            ValidateSQLResult result = CommUtil.excuteSQLs(hql, ";");
            if (result.getResult() == 0) {
                DefaultMutableTreeNode node = ComponentUtil.getNextNode(cur_node);
                cur_node.removeFromParent();
                ComponentUtil.initTreeSelection(roleTree, node);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }

        }
    }
//对应角色节点的刷新操作

    private void refreshRoleTree(DefaultMutableTreeNode node, int tab_index) {
        if (node != null) {
            Object obj = node.getUserObject();
            DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) node.getParent();
            Object parent_obj = null;
            Role parent_role = null;
            if (parent_node != null) {
                parent_obj = parent_node.getUserObject();
            }
            Role role = null;
            if (obj instanceof Role) {
                role = (Role) obj;
            }
            if (parent_obj instanceof Role) {
                parent_role = (Role) parent_obj;
            }
            if (tab_index == 0) {
                if (funtion_change_flag == 1) {
                    funtionTree.setModel(new FuntionTreeModel(parent_role));
                    refreshCurRoleFuntion(role);
                    refreshFuntionRight();
                    funtionTree.updateUI();
                    funtion_change_flag++;
                }
            } else if (tab_index == 1) {
                initFieldRight();
                if (field_change_flag == 1) {
                    fieldTree.setModel(new RebuildTreeModel(parent_role, field_module));
                    refreshCurRoleField(role);
                    refreshFieldRight();
                    field_change_flag++;
                }
            } else if (tab_index == 2) {
                initEntityRight();
                if (entity_change_flag == 1) {
                    refreshCurRoleEntity(role);
                    refreshEntityRight();
                    entity_change_flag++;
                }
                pnlEntityRight.updateUI();
            } else if (tab_index == 3) {
                initCodeRight();
                if (code_change_flag == 1) {
                    refreshCurRoleCode(role);
                    refreshCodeRight();
                    codeTable.updateUI();
                    code_change_flag++;
                }
            } else if (tab_index == 4) {
                initReportRight();
                if (report_change_flag == 1) {
                    reportTree.setModel(new ReportModel(report_module, reports, parent_role == null ? null : parent_role.getRole_key()));
                    refreshCurRoleReport(role);
                    refreshReportRight();
                    reportTree.updateUI();
                    report_change_flag++;
                }
            }
        }
    }
    //初始化字段权限

    private void initFieldRight() {
        if (field_change_flag == 0) {
            if (field_module == null) {
                field_module = CommImpl.getSysModule(true, true, true);//CommUtil.fetchEntities("from ModuleInfo mi  left join fetch mi.entityClasss et left join fetch et.entityDefs ed left join fetch ed.fieldDefs where used=1 order by mi.order_no");
            }
            rebuildTreeModel = new RebuildTreeModel(null, field_module);
            fieldTree = new JTree(rebuildTreeModel);
            HRRendererView.getFieldRightMap(fieldTree).initTree(fieldTree, TreeSelectMod.nodeManySelectMod);
            pnlFieldRight.add(new JScrollPane(fieldTree), BorderLayout.CENTER);
            if (UserContext.hasFunctionRight("SysRight.pnlFieldRight")) {
                fieldTree.addMouseListener(ma_right);
            }
            field_change_flag++;
        }
    }
    //初始化表权限

    private void initEntityRight() {
        if (entity_change_flag == 0) {
            if (entityTable == null) {
                List<String> fields = new ArrayList<String>();
                fields.add("entityDef.entityName");
                fields.add("entityDef.entityCaption");
                fields.add("add_flag");
                fields.add("edit_flag");
                fields.add("view_flag");
                fields.add("del_flag");
                fields.add("right_sql");
                fields.add("edit_sql");
                entityTable = new FTable(RoleEntity.class, fields, false, false, false, "RightPanel") {

                    @Override
                    public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                        RoleEntity rc = (RoleEntity) row_obj;
                        if (!getEntityRightFields(rc).contains(fileName)) {
                            return new Color(238, 238, 238);
                        }
                        return null;
                    }
                };
                entityTable.setITableCellEditable(new ITableCellEditable() {

                    @Override
                    public int getCellEditable(Object obj, String fileName) {
                        RoleEntity rc = (RoleEntity) obj;
                        if (!getEntityRightFields(rc).contains(fileName)) {
                            return -1;
                        }
                        return 0;
                    }
                });
                entityTable.setEditable(UserContext.hasFunctionRight("SysRight.pnlEntityRightMain"));
                pnlEntityTable.add(entityTable, BorderLayout.CENTER);
                entityTable.addListSelectionListener(new ListSelectionListener() {

                    Object obj = null;

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (obj == entityTable.getCurrentRow()) {
                            return;
                        }
                        List list = entityTable.getSelectObjects();
                        RoleEntity s_obj = (RoleEntity) obj;
                        if (list.size() > 0) {
                            s_obj = (RoleEntity) list.get(0);
                        }
                        if (s_obj == null) {
                            return;
                        }
                        e_addFlag.removeActionListener(al_eadd);
                        e_editFlag.removeActionListener(al_eedit);
                        e_viewFlag.removeActionListener(al_eview);
                        e_delFlag.removeActionListener(al_edel);
                        e_addFlag.setSelected(s_obj.isAdd_flag());
                        e_editFlag.setSelected(s_obj.isEdit_flag());
                        e_viewFlag.setSelected(s_obj.isView_flag());
                        e_delFlag.setSelected(s_obj.isDel_flag());
                        e_addFlag.addActionListener(al_eadd);
                        e_editFlag.addActionListener(al_eedit);
                        e_viewFlag.addActionListener(al_eview);
                        e_delFlag.addActionListener(al_edel);
                    }
                });
                entityTable.addRowChangeListner(new RowChangeListner() {

                    @Override
                    public void rowChanged(Object obj) {
                        RoleEntity change_entity = (RoleEntity) obj;
                        boolean view_flag = change_entity.isView_flag();
                        if (change_entity.isAdd_flag() || change_entity.isEdit_flag() || change_entity.isDel_flag()) {
                            view_flag = true;
                        }
                        change_entity.setView_flag(view_flag);
                        entityTable.updateUI();
                    }
                });
                if (UserContext.hasFunctionRight("SysRight.pnlEntityRightMain")) {
                    entityTable.addMouseListener(ma_right);
                }
                entityTable.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() < 2 || cur_node == null) {
                            return;
                        }
                        if (!UserContext.hasFunctionRight("SysRight.pnlEntityRightMain")) {
                            return;
                        }
                        Object obj1 = cur_node.getUserObject();
                        if (obj1 == null || !(obj1 instanceof Role)) {
                            return;
                        }
                        Object obj = entityTable.getCurrentRow();
                        if (obj == null || !(obj instanceof RoleEntity || (((RoleEntity) obj).getEntityDef() == null))) {
                            return;
                        }
                        String field_name = entityTable.getColumnModel().getColumn(entityTable.getCurrentColumnIndex()).getId();
                        if (field_name.equals("right_sql")) {
                            defineEntitySQL((RoleEntity) obj, "view");
                        } else if (field_name.equals("edit_sql")) {
                            defineEntitySQL((RoleEntity) obj, "edit");
                        }
                    }
                });
            }
            if (moduleTree == null) {
                List modules = CommImpl.getSysModule(true, true, false);//CommUtil.fetchEntities("from ModuleInfo mi left join fetch mi.entityClasss ec left join fetch ec.entityDefs order by mi.order_no");
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("所有模块");
                for (Object obj : modules) {
                    ModuleInfo mi = (ModuleInfo) obj;
                    DefaultMutableTreeNode miNode = new DefaultMutableTreeNode(mi);
                    node.add(miNode);
                    for (EntityClass ec : mi.getEntityClasss()) {
                        miNode.add(new DefaultMutableTreeNode(ec));
                    }
                }
                moduleTree = new JTree(node);
                HRRendererView.getRebuildMap(moduleTree).initTree(moduleTree);
                pnlEntityLeft.add(new JScrollPane(moduleTree));
                moduleTree.addTreeSelectionListener(new TreeSelectionListener() {

                    @Override
                    public void valueChanged(TreeSelectionEvent e) {
                        refreshModuleEntity();
                        refreshEntityRight();
                    }
                });
                ComponentUtil.initTreeSelection(moduleTree);
                ComponentUtil.refreshJSplitPane(jspEntity, "JSplitPane.RightPanel.jspEntity");
            }
            entity_change_flag++;
        }
    }

    /**
     * 该方法用于对指定角色 表权限表定义其数据权限范围
     * @param re：角色 表权限表
     */
    private void defineEntitySQL(RoleEntity re, String code) {
        EntityDef cur_def = re.getEntityDef();
        String packageName = EntityBuilder.getPackage(cur_def);
        QueryScheme queryScheme = null;
        Class cl;
        QuerySchemePanel querySchemePanel = null;
        boolean viewFlag = code.equals("view");
        try {
            cl = Class.forName(packageName + cur_def.getEntityName());
            querySchemePanel = new QuerySchemePanel(cl, true);
            if (re.getQueryScheme_key() != null && !re.getQueryScheme_key().trim().equals("")) {
                String hql = "from QueryScheme qs left join fetch qs.conditions where qs.queryScheme_key='"
                        + (viewFlag ? re.getQueryScheme_key() : re.getQuerySchemeEdit_key()) + "'";
                queryScheme = (QueryScheme) CommUtil.fetchEntityBy(hql);
            }
            if (queryScheme == null) {
                queryScheme = (QueryScheme) UtilTool.createUIDEntity(QueryScheme.class);
            }
            querySchemePanel.setNoPara();
            queryScheme.setQueryEntity(cur_def.getEntityName());
            querySchemePanel.setQueryScheme(queryScheme);
        } catch (ClassNotFoundException ex) {
            return;
        }
        String title = viewFlag ? "设置查看条件" : "设置编辑条件";
        if (ModalDialog.doModal(entityTable, querySchemePanel, title)) {
            String sql = "";
            String script = "";
            String key = "";
            if (queryScheme.getConditions() != null && !queryScheme.getConditions().isEmpty()) {
                queryScheme.setPerson_code(UserContext.person_code);
                queryScheme.setCondition_expression(querySchemePanel.getQueryText());
                queryScheme.setScheme_type("记录集权限（" + cur_def.getEntityName() + "）");
                ValidateSQLResult result = CommUtil.saveQueryScheme(queryScheme);
                if (result.getResult() == 0) {
                    if(viewFlag){
                        sql = queryScheme.buildSimpleSQL("@@");
                    }else{
                        script = queryScheme.buildScript();
                    }
                    key = queryScheme.getQueryScheme_key();
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }
            if (viewFlag) {
                re.setRight_sql(sql);
                re.setQueryScheme_key(key);
            } else {
                re.setEdit_sql(script);
                re.setQuerySchemeEdit_key(key);
            }
        }
    }

    private void refreshModuleEntity() {
        Role role = getCurrenRole(cur_node);
        entityTable.deleteAllRows();
        Enumeration enumt = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) moduleTree.getLastSelectedPathComponent();
        if (node != null && (!(node.getUserObject() instanceof String))) {
            enumt = node.breadthFirstEnumeration();
        } else {
            enumt = ((DefaultMutableTreeNode) moduleTree.getModel().getRoot()).breadthFirstEnumeration();
        }
        while (enumt.hasMoreElements()) {
            DefaultMutableTreeNode c_node = (DefaultMutableTreeNode) enumt.nextElement();
            if (c_node.getUserObject() instanceof EntityClass) {
                EntityClass ec = (EntityClass) c_node.getUserObject();
                for (EntityDef ed : ec.getEntityDefs()) {
                    RoleEntity re = (RoleEntity) UtilTool.createUIDEntity(RoleEntity.class);
                    re.setEntityDef(ed);
                    re.setRole(role);
                    entityTable.addObject(re);
                }
            }
        }
    }
    //初始化编码权限

    private void initCodeRight() {
        if (code_change_flag == 0) {
            List<String> fields = new ArrayList<String>();
            fields.add("code.code_name");
            fields.add("add_flag");
            fields.add("edit_flag");
            fields.add("view_flag");
            fields.add("del_flag");
            fields.add("code_limit");
            fields.add("code_limit_edit");
            codeTable = new FTable(RoleCode.class, fields, false, false, false, "RightPanel") {

                @Override
                public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                    RoleCode rc = (RoleCode) row_obj;
                    if (!getCodeRightFields(rc).contains(fileName)) {
                        return new Color(238, 238, 238);
                    }
                    return null;
                }
            };
            codeTable.setITableCellEditable(new ITableCellEditable() {

                @Override
                public int getCellEditable(Object obj, String fileName) {
                    RoleCode rc = (RoleCode) obj;
                    if (!getCodeRightFields(rc).contains(fileName)) {
                        return -1;
                    }
                    return 0;
                }
            });
            List<RoleCode> code_rights = new ArrayList<RoleCode>();
            List<Code> tmp = CodeManager.getCodeManager().getCode_types();
            for (Code code : tmp) {
                RoleCode roleCode = (RoleCode) UtilTool.createUIDEntity(RoleCode.class);
                roleCode.setCode(code);
                code_rights.add(roleCode);
            }
            codeTable.setObjects(code_rights);
            codeTable.setEditable(UserContext.hasFunctionRight("SysRight.pnlCodeRightMain"));
            pnlCodeRight.add(codeTable, BorderLayout.CENTER);
            if (UserContext.hasFunctionRight("SysRight.pnlCodeRightMain")) {
                codeTable.addMouseListener(ma_right);
            }
            codeTable.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() < 2 || cur_node == null) {
                        return;
                    }
                    if (!UserContext.hasFunctionRight("SysRight.pnlCodeRightMain")) {
                        return;
                    }
                    Object obj1 = cur_node.getUserObject();
                    if (obj1 == null || !(obj1 instanceof Role)) {
                        return;
                    }
                    Object obj = codeTable.getCurrentRow();
                    if (obj == null) {
                        return;
                    }
                    String field_name = codeTable.getColumnModel().getColumn(codeTable.getCurrentColumnIndex()).getId();
                    if (field_name.equals("code_limit")) {
                        defineCodeSQL((RoleCode) obj, "view");
                    } else if (field_name.equals("code_limit_edit")) {
                        defineCodeSQL((RoleCode) obj, "edit");
                    }

                }
            });
            code_change_flag++;
        }
    }

    private void defineCodeSQL(RoleCode rc, String flag) {
        boolean viewFlag = flag.equals("view");
        String field = viewFlag ? "right_sql" : "edit_sql";
        String viewField = viewFlag ? "code_limit" : "code_limit_edit";
        String title = viewFlag ? "设置查看条件" : "设置编辑条件";
        Code code = rc.getCode();
        List<Code> selectCodes = new ArrayList<Code>();
        List<Code> codes = CodeManager.getCodeManager().getCodeListBy(code.getCode_type());
        String sql = (String) PublicUtil.getProperty(rc, field);
        if (sql != null && !sql.trim().equals("")) {
            List s_codes = Arrays.asList(sql.split(";"));
            for (Code c : codes) {
                if (s_codes.contains(c.getCode_id())) {
                    selectCodes.add(c);
                }
            }
        }
        CodeSelectDialog csmDlg = new CodeSelectDialog(codes, code.getCode_type(), selectCodes, TreeSelectMod.nodeCheckMod);
        csmDlg.setTitle(title);
        ContextManager.locateOnMainScreenCenter(csmDlg);
        csmDlg.setVisible(true);
        if (csmDlg.isClick_ok()) {
            List<Code> select_codes = csmDlg.getSelectCodes(false);
            SysUtil.sortListByStr(select_codes, "code_id");
            HashSet<String> select_keys = new HashSet<String>();
            List<Code> result_codes = new ArrayList<Code>();
            boolean exists = false;
            for (Code c : select_codes) {
                exists = false;
                for (String key : select_keys) {
                    if (c.getCode_id().startsWith(key)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    continue;
                }
                select_keys.add(c.getCode_id());
                result_codes.add(c);
            }
            String code_limit = "";
            String code_sql = "";
            for (Code c : result_codes) {
                code_limit = code_limit + ";" + c.getCode_name();
                code_sql = code_sql + ";" + c.getCode_id();
            }
            if (!code_limit.equals("")) {
                code_limit = code_limit.substring(1);
                code_sql = code_sql.substring(1);
            }
            PublicUtil.setValueBy2(rc, field, code_sql);
            PublicUtil.setValueBy2(rc, viewField, code_limit);
            codeTable.updateUI();
        }
    }
    //初始化报表权限

    private void initReportRight() {
        if (report_change_flag == 0) {
            Hashtable<String, ModuleInfo> miKeys = new Hashtable<String, ModuleInfo>();
            if (report_module == null) {
                report_module = CommImpl.getSysModule(false, false, false);//CommUtil.fetchEntities("from ModuleInfo order by order_no");
            }
            for (Object obj : report_module) {
                ModuleInfo mi = (ModuleInfo) obj;
                miKeys.put(mi.getModule_key(), mi);
            }
            if (reports == null) {
                reports = new ArrayList();
                List list = CommUtil.fetchEntities("select rd.reportDef_key,rd.report_class,rd.report_name,rd.moduleInfo.module_key,rd.order_no from ReportDef rd  order by rd.moduleInfo.order_no,rd.report_class,rd.order_no");
                for (Object obj : list) {
                    Object[] objs = (Object[]) obj;
                    ModuleInfo mi = miKeys.get(objs[3] == null ? "" : objs[3].toString());
                    if (mi == null) {
                        continue;
                    }
                    ReportDef rd = new ReportDef();
                    rd.setModuleInfo(mi);
                    rd.setReportDef_key(objs[0].toString());
                    rd.setReport_class(objs[1] == null ? "" : objs[1].toString());
                    rd.setReport_name(objs[2] == null ? "" : objs[2].toString());
                    rd.setOrder_no(SysUtil.objToInt(objs[4]));
                    reports.add(rd);
                }
            }
            reportTree.setModel(new ReportModel(report_module, reports, null));
            HRRendererView.getReportRightMap(reportTree).initTree(reportTree, TreeSelectMod.nodeManySelectMod);
            pnlReport.add(new JScrollPane(reportTree), BorderLayout.CENTER);
            if (UserContext.hasFunctionRight("SysRight.pnlReport")) {
                reportTree.addMouseListener(ma_right);
            }
            report_change_flag++;
        }
    }

    //刷新功能权限
    private void refreshFuntionRight() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) funtionTree.getModel().getRoot();
        List<DefaultMutableTreeNode> parent_nodes = RightUtil.initFunTree(cur_role_funtion, root);
        RightUtil.refreshParentFuntion(parent_nodes, root);
        funtionTree.updateUI();
        refresh();
    }
    //刷新字段权限树

    private void refreshFieldRight() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) fieldTree.getModel().getRoot();
        Set<DefaultMutableTreeNode> refresh_nodes = RightUtil.initFieldTree(cur_role_field, root);
        for (DefaultMutableTreeNode node : refresh_nodes) {
            checkFieldRightChanges(node);
        }
        fieldTree.updateUI();
        refresh();
    }
    //刷新表权限

    private void refreshEntityRight() {
        Role role = getCurrenRole(cur_node);
        List list = entityTable.getObjects();
        for (Object obj : list) {
            RoleEntity re = (RoleEntity) obj;
            RoleEntity re1 = cur_role_entity.get(re.getEntityDef().getEntity_key());
            if (re1 != null) {
                re.setAdd_flag(re1.isAdd_flag());
                re.setDel_flag(re1.isDel_flag());
                re.setEdit_flag(re1.isEdit_flag());
                re.setView_flag(re1.isView_flag());
                re.setRight_sql(re1.getRight_sql());
                re.setEdit_sql(re1.getEdit_sql());
                re.setQueryScheme_key(re1.getQueryScheme_key());
                re.setQuerySchemeEdit_key(re1.getQuerySchemeEdit_key());
            } else {
                re.setAdd_flag(false);
                re.setEdit_flag(false);
                re.setDel_flag(false);
                re.setView_flag(false);
                re.setRight_sql(null);
                re.setEdit_sql(null);
                re.setQueryScheme_key(null);
                re.setQuerySchemeEdit_key(null);
            }
            re.setRole(role);
        }
        entityTable.updateUI();
        refresh();
    }

    private List<String> getEntityRightFields(RoleEntity rc) {
        List<String> fields = new ArrayList();
        if (rc == null) {
            return fields;
        }
        if (UserContext.isSA && rc.getRole() != null && rc.getRole().getParent_code().equals("ROOT")) {
            fields.add("add_flag");
            fields.add("edit_flag");
            fields.add("view_flag");
            fields.add("del_flag");
            return fields;
        }
        RoleEntity p_rc = p_role_entity.get(rc.getEntityDef().getEntity_key());
        if (p_rc == null) {
            return fields;
        }
        if (p_rc.isAdd_flag()) {
            fields.add("add_flag");
        }
        if (p_rc.isEdit_flag()) {
            fields.add("edit_flag");
        }
        if (p_rc.isView_flag()) {
            fields.add("view_flag");
        }
        if (p_rc.isDel_flag()) {
            fields.add("del_flag");
        }
        return fields;
    }
    //刷新编码权限

    private void refreshCodeRight() {
        Role role = getCurrenRole(cur_node);
        List list = codeTable.getObjects();
        for (Object obj : list) {
            RoleCode rc = (RoleCode) obj;
            rc.setRole(role);
            RoleCode rc1 = cur_role_code.get(rc.getCode().getCode_key());
            if (rc1 != null) {
                rc.setAdd_flag(rc1.isAdd_flag());
                rc.setDel_flag(rc1.isDel_flag());
                rc.setEdit_flag(rc1.isEdit_flag());
                rc.setView_flag(rc1.isView_flag());
                rc.setRight_sql(rc1.getRight_sql());
                String code_limit = "";
                if (rc1.getRight_sql() != null && !rc1.getRight_sql().equals("")) {
                    String[] limits = rc1.getRight_sql().split(";");
                    for (String limit : limits) {
                        code_limit = code_limit + ";" + CodeManager.getCodeManager().getCodeNameBy(rc.getCode().getCode_type(), limit);
                    }
                    code_limit = code_limit.substring(1);
                }
                rc.setCode_limit(code_limit);
                rc.setEdit_sql(rc1.getEdit_sql());
                code_limit = "";
                if (rc1.getEdit_sql() != null && !rc1.getEdit_sql().equals("")) {
                    String[] limits = rc1.getEdit_sql().split(";");
                    for (String limit : limits) {
                        code_limit = code_limit + ";" + CodeManager.getCodeManager().getCodeNameBy(rc.getCode().getCode_type(), limit);
                    }
                    code_limit = code_limit.substring(1);
                }
                rc.setCode_limit_edit(code_limit);
            } else {
                rc.setAdd_flag(false);
                rc.setDel_flag(false);
                rc.setEdit_flag(false);
                rc.setView_flag(false);
                rc.setRight_sql("");
                rc.setEdit_sql("");
                rc.setCode_limit("");
                rc.setCode_limit_edit("");
            }
        }
        codeTable.setObjects(list);
        refresh();
    }

    private List<String> getCodeRightFields(RoleCode rc) {
        List<String> fields = new ArrayList();
        if (UserContext.isSA && rc.getRole() != null && rc.getRole().getParent_code().equals("ROOT")) {
            fields.add("add_flag");
            fields.add("edit_flag");
            fields.add("view_flag");
            fields.add("del_flag");
            return fields;
        }
        RoleCode p_rc = p_role_code.get(rc.getCode().getCode_key());
        if (p_rc == null) {
            return fields;
        }
        if (p_rc.isAdd_flag()) {
            fields.add("add_flag");
        }
        if (p_rc.isEdit_flag()) {
            fields.add("edit_flag");
        }
        if (p_rc.isView_flag()) {
            fields.add("view_flag");
        }
        if (p_rc.isDel_flag()) {
            fields.add("del_flag");
        }
        return fields;
    }
    //刷新报表权限树

    private void refreshReportRight() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.reportTree.getModel().getRoot();
        Set<DefaultMutableTreeNode> refresh_class_node = RightUtil.initReportTree(cur_role_report, root);
        for (DefaultMutableTreeNode node : refresh_class_node) {
            checkReportRightChanges(node);
        }
        reportTree.updateUI();
        refresh();
    }
    //刷新当前角色功能权限缓存

    private void refreshCurRoleFuntion(Role role) {
        cur_role_funtion.clear();
        if (role == null) {
            return;
        }
        List list = CommUtil.selectSQL("select funtionright_key,fun_flag from RoleFuntion rf where rf.role_key='" + role.getRole_key() + "'");
        for (Object obj : list) {
            Object[] objs = (Object[]) obj;
            RoleFuntion rf = new RoleFuntion();
            rf.setRole(role);
            FuntionRight fr = UserContext.getFunByKey(objs[0].toString());
            if (fr == null) {
                continue;
            }
            rf.setFuntionRight(fr);
            rf.setFun_flag(SysUtil.objToInt(objs[1].toString()));
            cur_role_funtion.put(fr.getFuntionRight_key(), rf);
        }
    }
    //刷新指定角色的字段权限缓存

    private void refreshCurRoleField(Role role) {
        cur_role_field.clear();
        if (role == null) {
            return;
        }
        List list = CommUtil.fetchEntities("from RoleField r where r.role.role_key='" + role.getRole_key() + "'");
        for (Object obj : list) {
            RoleField rf = (RoleField) obj;
            rf.setRole(role);
            cur_role_field.put(rf.getField_name(), rf);
        }
    }
    //刷新指定角色的表权限缓存

    private void refreshCurRoleEntity(Role role) {
        cur_role_entity.clear();
        p_role_entity.clear();
        if (role == null) {
            return;
        }
        List list = CommUtil.fetchEntities("from Role r join fetch r.roleEntitys re join fetch re.entityDef ed join fetch ed.entityClass ec join fetch ec.moduleInfo where r.role_key = '" + role.getRole_key() + "' or r.role_code='" + role.getParent_code() + "'");
        for (Object obj : list) {
            Role r = (Role) obj;
            if (r.getRole_key().equals(role.getRole_key())) {
                for (RoleEntity roleEntity : r.getRoleEntitys()) {
                    cur_role_entity.put(roleEntity.getEntityDef().getEntity_key(), roleEntity);
                }
            } else {
                for (RoleEntity roleEntity : r.getRoleEntitys()) {
                    p_role_entity.put(roleEntity.getEntityDef().getEntity_key(), roleEntity);
                }
            }
        }
    }
    //刷新指定角色的编码权限缓存

    private void refreshCurRoleCode(Role role) {
        cur_role_code.clear();
        p_role_code.clear();
        if (role == null) {
            return;
        }
        List list = CommUtil.fetchEntities("from Role r join fetch r.roleCodes rc join fetch rc.code where r.role_key='" + role.getRole_key() + "' or r.role_key in(select role_key from Role where role_code='" + role.getParent_code() + "')");
        for (Object obj : list) {
            Role r = (Role) obj;
            if (r.getRole_key().equals(role.getRole_key())) {
                for (RoleCode rc : r.getRoleCodes()) {
                    cur_role_code.put(rc.getCode().getCode_key(), rc);
                }
            } else {
                for (RoleCode rc : r.getRoleCodes()) {
                    p_role_code.put(rc.getCode().getCode_key(), rc);
                }
            }
        }
    }
    //刷新指定角色的报表权限缓存

    private void refreshCurRoleReport(Role role) {
        cur_role_report.clear();
        if (role == null) {
            return;
        }
        Hashtable<String, ReportDef> rdKeys = new Hashtable<String, ReportDef>();
        for (ReportDef rd : reports) {
            rdKeys.put(rd.getReportDef_key(), rd);
        }
        List list = CommUtil.selectSQL("select reportdef_key,fun_flag from RoleReport rr where rr.role_key='" + role.getRole_key() + "'");
        for (Object obj : list) {
            Object[] objs = (Object[]) obj;
            ReportDef rd = rdKeys.get(objs[0].toString());
            if (rd == null) {
                continue;
            }
            RoleReport rr = new RoleReport();
            rr.setRole(role);
            rr.setReportDef(rd);
            rr.setFun_flag(SysUtil.objToInt(objs[1]));
            cur_role_report.put(objs[0].toString(), rr);
        }
    }

    private void checkReportRightChanges(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode tmpParent_node = node;
        while (tmpParent_node != reportTree.getModel().getRoot()) {
            Enumeration enumt = tmpParent_node.children();
            Object parent_obj = tmpParent_node.getUserObject();
            int cur_fun_flag = 1;
            List<Integer> child_flags = new ArrayList<Integer>();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
                Object tmp_obj = tmpNode.getUserObject();
                if (tmp_obj instanceof ReportDef) {
                    ReportDef tmp_report_def = (ReportDef) tmp_obj;
                    child_flags.add(tmp_report_def.getFun_flag());
                } else if (tmp_obj instanceof RoleRightTemp) {
                    RoleRightTemp tmp_entity_def = (RoleRightTemp) tmp_obj;
                    child_flags.add(tmp_entity_def.getFun_flag());
                }
            }
            cur_fun_flag = getFunFlag(child_flags);
            if (parent_obj instanceof RoleRightTemp) {
                RoleRightTemp roleRightTemp = (RoleRightTemp) parent_obj;
                roleRightTemp.setFun_flag(cur_fun_flag);
            }
            tmpParent_node = (DefaultMutableTreeNode) tmpParent_node.getParent();
        }
    }

    private void checkFieldRightChanges(DefaultMutableTreeNode node) {
        while (node != fieldTree.getModel().getRoot()) {
            Object parent_obj = node.getUserObject();
            List<Integer> child_flags = getChildFlag(node);
            int cur_fun_flag = getFunFlag(child_flags);
            if (parent_obj instanceof EntityDef) {
                ((EntityDef) parent_obj).setFun_flag(cur_fun_flag);
            } else if (parent_obj instanceof ModuleInfo) {
                ((ModuleInfo) parent_obj).setFun_flag(cur_fun_flag);
            }
            node = (DefaultMutableTreeNode) node.getParent();
        }
        RoleRightTemp parent_right = (RoleRightTemp) node.getUserObject();
        List<Integer> child_flags = getChildFlag(node);
        int cur_fun_flag = getFunFlag(child_flags);
        parent_right.setFun_flag(cur_fun_flag);
    }

    private List<Integer> getChildFlag(DefaultMutableTreeNode node) {
        Enumeration enumt = node.children();
        List<Integer> child_flags = new ArrayList<Integer>();
        while (enumt.hasMoreElements()) {
            DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
            Object tmp_obj = tmpNode.getUserObject();
            if (tmp_obj instanceof FieldDef) {
                FieldDef tmp_field_def = (FieldDef) tmp_obj;
                child_flags.add(tmp_field_def.getFun_flag());
            } else if (tmp_obj instanceof EntityDef) {
                EntityDef tmp_entity_def = (EntityDef) tmp_obj;
                child_flags.add(tmp_entity_def.getFun_flag());
            } else if (tmp_obj instanceof EntityClass) {
                EntityClass tmp_entity_class = (EntityClass) tmp_obj;
                child_flags.add(tmp_entity_class.getFun_flag());
            } else if (tmp_obj instanceof ModuleInfo) {
                ModuleInfo tmp_module_info = (ModuleInfo) tmp_obj;
                child_flags.add(tmp_module_info.getFun_flag());
            }
        }
        return child_flags;
    }

    private int getFunFlag(List<Integer> child_flags) {
        int cur_fun_flag = 0;
        if (child_flags.contains(0) || child_flags.contains(2)) {
            cur_fun_flag = 2;
        }
        if (child_flags.contains(0)) {
            if (child_flags.contains(1) || child_flags.contains(2)) {
                cur_fun_flag = 2;
            } else {
                cur_fun_flag = 0;
            }
        } else if (child_flags.contains(2)) {
            cur_fun_flag = 2;
        } else {
            cur_fun_flag = 1;
        }
        if (child_flags.isEmpty()) {
            cur_fun_flag = 0;
        }
        return cur_fun_flag;
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
        if (cur_tabIndex == 2) {
            ContextManager.setStatusBar(entityTable.getObjects().size());
        } else if (cur_tabIndex == 3) {
            ContextManager.setStatusBar(codeTable.getObjects().size());
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
