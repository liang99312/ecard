//package org.jhrcore.right;
//
//import com.foundercy.pf.control.table.FTable;
//import com.foundercy.pf.control.table.RowChangeListner;
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import javax.swing.BorderFactory;
//import javax.swing.GroupLayout;
//import javax.swing.Icon;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JMenuItem;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//import javax.swing.JScrollPane;
//import javax.swing.JSplitPane;
//import javax.swing.JTabbedPane;
//import javax.swing.JToolBar;
//import javax.swing.JTree;
//import javax.swing.LayoutStyle;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.tree.DefaultMutableTreeNode;
//import org.jhrcore.client.CommUtil;
//import org.jhrcore.client.SysUtil;
//import org.jhrcore.client.UserContext;
//import org.jhrcore.client.personnel.comm.DeptPersonPanel;
//import org.jhrcore.client.personnel.comm.IPickPersonClassListener;
//import org.jhrcore.client.report.ReportModel;
//import org.jhrcore.comm.CodeManager;
//import org.jhrcore.comm.PublicUtil;
//import org.jhrcore.entity.A01;
//import org.jhrcore.entity.A01PassWord;
//import org.jhrcore.entity.Code;
//import org.jhrcore.entity.DeptCode;
//import org.jhrcore.entity.SysParameter;
//import org.jhrcore.entity.UtilTool;
//import org.jhrcore.entity.base.EntityClass;
//import org.jhrcore.entity.base.EntityDef;
//import org.jhrcore.entity.base.FieldDef;
//import org.jhrcore.entity.base.ModuleInfo;
//import org.jhrcore.entity.query.QueryScheme;
//import org.jhrcore.entity.report.ReportDef;
//import org.jhrcore.entity.right.A01DeptRight;
//import org.jhrcore.entity.right.FuntionRight;
//import org.jhrcore.entity.right.Role;
//import org.jhrcore.entity.right.RoleCode;
//import org.jhrcore.entity.right.RoleDept;
//import org.jhrcore.entity.right.RoleEntity;
//import org.jhrcore.entity.right.RoleField;
//import org.jhrcore.entity.right.RoleFuntion;
//import org.jhrcore.entity.right.RoleReport;
//import org.jhrcore.entity.right.RoleRightTemp;
//import org.jhrcore.entity.salary.ValidateSQLResult;
//import org.jhrcore.maintian.CodeSelectManyDlg;
//import org.jhrcore.ui.ContextManager;
//import org.jhrcore.ui.CopyCurRightDialog;
//import org.jhrcore.ui.DeptPanel;
//import org.jhrcore.ui.FormulaTextDialog;
//import org.jhrcore.ui.ModulePanel;
//
//public class Right2 extends ModulePanel
//{
//  private JButton btnAdd = new JButton("增加角色或组");
//  private JButton btnDel = new JButton("删除");
//  private JButton btnPersonAdd = new JButton("加入员工");
//  private JButton btnPersonDel = new JButton("移出员工");
//  private JCheckBox cxboxCopy = new JCheckBox("同步应用子级用户");
//  private JTree roleTree = new JTree();
//  private JTree reportTree = new JTree();
//  private JScrollPane scrollPaneFuntionRight;
//  private JTree funtionTree = new JTree();
//  private JTree fieldTree = new JTree();
//  private JPopupMenu jpopmenu = new JPopupMenu();
//  private JPopupMenu popMenuSetRight = new JPopupMenu();
//  private JPopupMenu popMenuSetRigth1 = new JPopupMenu();
//  private JMenuItem addItem = new JMenuItem("增加角色或组");
//  private JMenuItem change = new JMenuItem("修改角色或组属性");
//  private JMenuItem copyRight = new JMenuItem("复制指定角色权限");
//  private JMenuItem flashRight = new JMenuItem("同步应用二级角色");
//  private JMenuItem addRightItem = new JMenuItem(" 授权");
//  private JMenuItem addRightItem1 = new JMenuItem(" 授权");
//  private JMenuItem viewRightItem = new JMenuItem(" 查看");
//  private JMenuItem backRightItem1 = new JMenuItem(" 收回");
//  private JMenuItem backRightItem = new JMenuItem(" 收回");
//  private JLabel lbl = new JLabel(" 查找：");
//  private JComboBox cbBoxSearch = new JComboBox();
//  private JCheckBox chbCurColumn = new JCheckBox("当前列", false);
//  private JButton btnSearch = new JButton("", ContextManager.getImageIconByName("search.png"));
//  private final ImageIcon backRightItemIcon = (ImageIcon)ContextManager.getImageIconByName("RefuseRight.jpg");
//  private final ImageIcon addRightItemIcon = (ImageIcon)ContextManager.getImageIconByName("GiveRight.jpg");
//  private final ImageIcon viewRightItemIcon = (ImageIcon)ContextManager.getImageIconByName("ViewRight.jpg");
//  private DefaultMutableTreeNode cur_node;
//  private int cur_tabIndex = 0;
//  private DeptPersonPanel deptPersonPanel;
//  private FTable beanTablePanel3;
//  private FTable beanTablePanel1;
//  private FTable codeTable;
//  private FTable entityTable;
//  private List<String> columnList = new ArrayList();
//  private DeptPanel deptPanel;
//  private JScrollPane scrollPaneTree;
//  private RebuildTreeModel rebuildTreeModel;
//  private Set<FieldDef> field_right_changes = new HashSet();
//  private java.util.Hashtable<String, A01DeptRight> cur_role_dept = new java.util.Hashtable();
//  private java.util.Hashtable<String, RoleFuntion> cur_role_funtion = new java.util.Hashtable();
//  private java.util.Hashtable<String, RoleField> cur_role_field = new java.util.Hashtable();
//  private java.util.Hashtable<String, RoleReport> cur_role_report = new java.util.Hashtable();
//  private java.util.Hashtable<String, RoleCode> cur_role_code = new java.util.Hashtable();
//  private java.util.Hashtable<String, RoleEntity> cur_role_entity = new java.util.Hashtable();
//  private int dept_change_flag = 0;
//  private int funtion_change_flag = 1;
//  private int field_change_flag = 0;
//  private int entity_change_flag = 0;
//  private int code_change_flag = 0;
//  private int report_change_flag = 0;
//  private int person_change_flag = 0;
//  private boolean copy_flag = false;
//  private String last_search_val = "";
//  private int last_locate_position = -1;
//  private RoleCode change_code;
//  private RoleEntity change_entity;
//  private RoleEntity cur_entity;
//  private RoleModel roleModel;
//  private List<String> default_locate_fields = new ArrayList();
//  private List<DeptCode> depts = new ArrayList();
//  private static final Icon blankIcon = ContextManager.getImageIconByName("blank_2.png");
//  private List funtionRight_list = new ArrayList();
//  private SysParameter sp;
//  private Role cur_parent_role = null;
//  private HashSet<String> fieldDef_set = new HashSet();
//  private HashSet<ReportDef> report_set = new HashSet();
//  private StringBuffer cur_string = new StringBuffer();
//  private StringBuffer update_cur_string = new StringBuffer();
//  private String db_type = UserContext.sql_dialect;
//  private JPanel jPanel1;
//  private JPanel jPanel2;
//  private JSplitPane jSplitPane1;
//  private JSplitPane jSplitPane2;
//  private JTabbedPane pnl1;
//  private JPanel pnlCodeRight;
//  private JPanel pnlDept;
//  private JPanel pnlDeptPerson;
//  private JPanel pnlDeptRight;
//  private JPanel pnlEntityRight;
//  private JPanel pnlFieldRight;
//  private JPanel pnlFuntionRight;
//  private JPanel pnlPerson;
//  private JPanel pnlPersonFix;
//  private JPanel pnlPersonMain;
//  private JPanel pnlReport;
//  private JPanel pnlRole;
//  private JPanel pnlRolePerson;
//  private JSplitPane spPaneMain;
//  private JToolBar toolbar;
//
//  public Right2()
//  {
//    super(new BorderLayout());
//    initComponents();
//    initOthers();
//    setupEvents();
//  }
//
//  private void setMainState(int index) {
//    if (index == 6) {
//      btnPersonAdd.setEnabled(UserContext.hasFunctionRight("Right.btnPersonAdd"));
//      btnPersonDel.setEnabled(UserContext.hasFunctionRight("Right.btnPersonDel"));
//      btnSearch.setEnabled(true);
//      cbBoxSearch.setEnabled(true);
//      cbBoxSearch.setEditable(true);
//    } else {
//      btnPersonAdd.setEnabled(false);
//      btnPersonDel.setEnabled(false);
//      btnSearch.setEnabled(false);
//      cbBoxSearch.setEnabled(false);
//    }
//  }
//
//  public void setFunctionRight()
//  {
//    btnAdd.setEnabled(UserContext.hasFunctionRight("Right.btnAdd"));
//    btnDel.setEnabled(UserContext.hasFunctionRight("Right.btnDel"));
//    cxboxCopy.setEnabled(UserContext.hasFunctionRight("Right.cxboxCopy"));
//    addItem.setEnabled(UserContext.hasFunctionRight("Right.btnAdd"));
//    change.setEnabled(UserContext.hasFunctionRight("Right.change"));
//    flashRight.setEnabled(UserContext.hasFunctionRight("Right.flashRight"));
//    copyRight.setEnabled(UserContext.hasFunctionRight("Right.copyRight"));
//  }
//
//  public void setFunctionText()
//  {
//    btnAdd.setText(UserContext.getFuntionName("Right.btnAdd"));
//    btnDel.setText(UserContext.getFuntionName("Right.btnDel"));
//    btnPersonAdd.setText(UserContext.getFuntionName("Right.btnPersonAdd"));
//    btnPersonDel.setText(UserContext.getFuntionName("Right.btnPersonDel"));
//    cxboxCopy.setText(UserContext.getFuntionName("Right.cxboxCopy"));
//    addItem.setText(UserContext.getFuntionName("Right.btnAdd"));
//    change.setText(UserContext.getFuntionName("Right.change"));
//    flashRight.setText(UserContext.getFuntionName("Right.flashRight"));
//    copyRight.setText(UserContext.getFuntionName("Right.copyRight"));
//  }
//
//  private void initComponents()
//  {
//    spPaneMain = new JSplitPane();
//    jPanel1 = new JPanel();
//    toolbar = new JToolBar();
//    jPanel2 = new JPanel();
//    pnl1 = new JTabbedPane();
//    pnlFuntionRight = new JPanel();
//    pnlDeptRight = new JPanel();
//    pnlFieldRight = new JPanel();
//    pnlEntityRight = new JPanel();
//    pnlCodeRight = new JPanel();
//    pnlReport = new JPanel();
//    pnlPersonFix = new JPanel();
//    jSplitPane1 = new JSplitPane();
//    pnlDept = new JPanel();
//    jSplitPane2 = new JSplitPane();
//    pnlDeptPerson = new JPanel();
//    pnlPersonMain = new JPanel();
//    pnlRolePerson = new JPanel();
//    pnlPerson = new JPanel();
//    pnlRole = new JPanel();
//
//    spPaneMain.setDividerLocation(200);
//    spPaneMain.setDividerSize(3);
//    spPaneMain.setName("spPaneMain");
//
//    jPanel1.setName("jPanel1");
//
//    toolbar.setFloatable(false);
//    toolbar.setRollover(true);
//    toolbar.setName("toolbar");
//
//    jPanel2.setName("jPanel2");
//
//    pnl1.setName("pnl1");
//    pnl1.setPreferredSize(new Dimension(469, 549));
//
//    pnlFuntionRight.setName("pnlFuntionRight");
//    pnlFuntionRight.setLayout(new BorderLayout());
//    pnl1.addTab("操作功能权限", pnlFuntionRight);
//
//    pnlDeptRight.setName("pnlDeptRight");
//    pnlDeptRight.setLayout(new BorderLayout());
//    pnl1.addTab("部门权限", pnlDeptRight);
//
//    pnlFieldRight.setName("pnlFieldRight");
//    pnlFieldRight.setLayout(new BorderLayout());
//    pnl1.addTab("表中字段权限", pnlFieldRight);
//
//    pnlEntityRight.setBorder(BorderFactory.createEtchedBorder());
//    pnlEntityRight.setName("pnlEntityRight");
//    pnlEntityRight.setLayout(new BorderLayout());
//    pnl1.addTab("表记录增删权限", pnlEntityRight);
//
//    pnlCodeRight.setBorder(BorderFactory.createEtchedBorder());
//    pnlCodeRight.setName("pnlCodeRight");
//    pnlCodeRight.setLayout(new BorderLayout());
//    pnl1.addTab("关联代码权限", pnlCodeRight);
//
//    pnlReport.setName("pnlReport");
//    pnlReport.setLayout(new BorderLayout());
//    pnl1.addTab("报表权限", pnlReport);
//
//    pnlPersonFix.setName("pnlPersonFix");
//
//    jSplitPane1.setDividerLocation(200);
//    jSplitPane1.setDividerSize(3);
//    jSplitPane1.setName("jSplitPane1");
//
//    pnlDept.setName("pnlDept");
//    pnlDept.setLayout(new BorderLayout());
//    jSplitPane1.setLeftComponent(pnlDept);
//
//    jSplitPane2.setDividerLocation(300);
//    jSplitPane2.setDividerSize(3);
//    jSplitPane2.setOrientation(0);
//    jSplitPane2.setName("jSplitPane2");
//
//    pnlDeptPerson.setBorder(BorderFactory.createTitledBorder("当前部门人员"));
//    pnlDeptPerson.setName("pnlDeptPerson");
//
//    pnlPersonMain.setBorder(BorderFactory.createEtchedBorder());
//    pnlPersonMain.setName("pnlPersonMain");
//    pnlPersonMain.setLayout(new BorderLayout());
//
//    GroupLayout pnlDeptPersonLayout = new GroupLayout(pnlDeptPerson);
//    pnlDeptPerson.setLayout(pnlDeptPersonLayout);
//    pnlDeptPersonLayout.setHorizontalGroup(pnlDeptPersonLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(pnlPersonMain, -1, 344, 32767));
//
//    pnlDeptPersonLayout.setVerticalGroup(pnlDeptPersonLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(pnlPersonMain, -1, 270, 32767));
//
//    jSplitPane2.setTopComponent(pnlDeptPerson);
//
//    pnlRolePerson.setBorder(BorderFactory.createTitledBorder("当前角色对应人员"));
//    pnlRolePerson.setName("pnlRolePerson");
//
//    pnlPerson.setBorder(BorderFactory.createEtchedBorder());
//    pnlPerson.setName("pnlPerson");
//    pnlPerson.setLayout(new BorderLayout());
//
//    GroupLayout pnlRolePersonLayout = new GroupLayout(pnlRolePerson);
//    pnlRolePerson.setLayout(pnlRolePersonLayout);
//    pnlRolePersonLayout.setHorizontalGroup(pnlRolePersonLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(pnlPerson, -1, 344, 32767));
//
//    pnlRolePersonLayout.setVerticalGroup(pnlRolePersonLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(pnlPerson, -1, 205, 32767));
//
//    jSplitPane2.setRightComponent(pnlRolePerson);
//
//    jSplitPane1.setRightComponent(jSplitPane2);
//
//    GroupLayout pnlPersonFixLayout = new GroupLayout(pnlPersonFix);
//    pnlPersonFix.setLayout(pnlPersonFixLayout);
//    pnlPersonFixLayout.setHorizontalGroup(pnlPersonFixLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jSplitPane1, -1, 566, 32767));
//
//    pnlPersonFixLayout.setVerticalGroup(pnlPersonFixLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jSplitPane1, -1, 540, 32767));
//
//    pnl1.addTab("角色对应人员", pnlPersonFix);
//
//    GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
//    jPanel2.setLayout(jPanel2Layout);
//    jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 571, 32767).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(pnl1, -1, 571, 32767)));
//
//    jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 569, 32767).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(pnl1, -1, 569, 32767)));
//
//    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
//    jPanel1.setLayout(jPanel1Layout);
//    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(toolbar, -1, 571, 32767).addComponent(jPanel2, -1, -1, 32767));
//
//    jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(toolbar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2, -1, -1, 32767)));
//
//    spPaneMain.setRightComponent(jPanel1);
//
//    pnlRole.setName("pnlRole");
//
//    GroupLayout pnlRoleLayout = new GroupLayout(pnlRole);
//    pnlRole.setLayout(pnlRoleLayout);
//    pnlRoleLayout.setHorizontalGroup(pnlRoleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 199, 32767));
//
//    pnlRoleLayout.setVerticalGroup(pnlRoleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 600, 32767));
//
//    spPaneMain.setLeftComponent(pnlRole);
//
//    GroupLayout layout = new GroupLayout(pnlRole);
//    setLayout(layout);
//    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(spPaneMain, -1, 775, 32767));
//
//    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(spPaneMain, -1, 602, 32767));
//  }
//
//  private void initToolBar()
//  {
//    toolbar.add(btnAdd);
//    toolbar.add(btnDel);
//    toolbar.add(cxboxCopy);
//    toolbar.add(btnPersonAdd);
//    toolbar.add(btnPersonDel);
//    toolbar.add(lbl);
//    cbBoxSearch.setMaximumSize(new Dimension(150, 24));
//    toolbar.add(cbBoxSearch);
//    toolbar.add(btnSearch);
//    toolbar.add(chbCurColumn);
//  }
//
//  private void initOthers() {
//    sp = ((SysParameter)CommUtil.fetchEntityBy("from SysParameter sys where sys.sysparameter_code='1' and sys.sysparameter_name='部门级次设置'"));
//    funtionRight_list = CommUtil.fetchEntities("from FuntionRight ed where ed.fun_parent_code!='ROOT' order by ed.fun_code");
//    depts.clear();
//    Iterator i = UserContext.depts.iterator();
//    while (true) { DeptCode dc = null;
//      while (true) { if (!(i.hasNext())) break; dc = (DeptCode)i.next();
//        if (!(dc.isDel_flag()))
//          break;
//      }
//      depts.add(dc);
//    }
//    initToolBar();
//    default_locate_fields.add("a0190");
//    default_locate_fields.add("a0101");
//    default_locate_fields.add("pydm");
//    columnList.add("a0190");
//    columnList.add("a0101");
//    columnList.add("deptCode.dept_full_name");
//    jpopmenu.add(addItem);
//    jpopmenu.addSeparator();
//    jpopmenu.add(change);
//    jpopmenu.addSeparator();
//    jpopmenu.add(copyRight);
//    jpopmenu.add(flashRight);
//    addRightItem.setIcon(addRightItemIcon);
//    addRightItem1.setIcon(addRightItemIcon);
//    viewRightItem.setIcon(viewRightItemIcon);
//    backRightItem.setIcon(backRightItemIcon);
//    backRightItem1.setIcon(backRightItemIcon);
//    popMenuSetRight.add(addRightItem);
//    popMenuSetRight.add(backRightItem);
//    popMenuSetRigth1.add(addRightItem1);
//    popMenuSetRigth1.add(viewRightItem);
//    popMenuSetRigth1.add(backRightItem1);
//
//    roleModel = new RoleModel();
//    roleTree.setModel(roleModel);
//    roleTree.setSelectionRow(2);
//    roleTree.setCellRenderer(new CellRoleTreeRenderer());
//    pnlRole.setLayout(new BorderLayout());
//    scrollPaneTree = new JScrollPane(roleTree);
//    scrollPaneTree.setAutoscrolls(true);
//    pnlRole.add(scrollPaneTree, "Center");
//
//    funtionTree.setModel(new FuntionTreeModel());
//    funtionTree.setCellRenderer(new CellFuntionTreeRenderer());
//    scrollPaneFuntionRight = new JScrollPane(funtionTree);
//    scrollPaneFuntionRight.setAutoscrolls(true);
//    pnlFuntionRight.add(scrollPaneFuntionRight, "Center");
//    flashRight.setIcon(blankIcon);
//    copyRight.setIcon(blankIcon);
//    change.setIcon(blankIcon);
//    addItem.setIcon(blankIcon);
//    setMainState(0);
//  }
//
//  private void setupEvents() {
//    ActionListener listener = new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        if (chbCurColumn.isSelected())
//          locateEmp(beanTablePanel1.getCurrentColumnIndex());
//        else
//          locateEmp(-1);
//
//      }
//
//    };
//    cbBoxSearch.addActionListener(listener);
//    btnSearch.addActionListener(listener);
//    pnl1.addChangeListener(new ChangeListener()
//    {
//      public void stateChanged(ChangeEvent e)
//      {
//        long time1 = System.currentTimeMillis();
////        access302(RightPanel. pnl1.getSelectedIndex());
//        refreshRoleTree(cur_node, cur_tabIndex);
//        setMainState(cur_tabIndex);
//        long time2 = System.currentTimeMillis();
//        System.out.println("time:" + (time2 - time1));
//      }
//
//    });
//    cxboxCopy.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
////        RightPanel.access$802(RightPanel. !(RightPanel.copy_flag));
//      }
//
//    });
//    btnPersonDel.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        removePerson();
//      }
//
//    });
//    btnPersonAdd.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        addPerson();
//      }
//
//    });
//    addRightItem.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        giveRight(RightPanel.cur_node, RightPanel.cur_tabIndex);
//      }
//
//    });
//    addRightItem1.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        giveRight(cur_node, cur_tabIndex);
//      }
//
//    });
//    backRightItem.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        backRight(cur_tabIndex, cur_node);
//      }
//
//    });
//    backRightItem1.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        backRight(cur_tabIndex, cur_node);
//      }
//
//    });
//    viewRightItem.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        RightPanel.viewRight(RightPanel.cur_tabIndex, RightPanel.cur_node);
//      }
//
//    });
//    roleTree.addTreeSelectionListener(new TreeSelectionListener()
//    {
//      public void valueChanged(TreeSelectionEvent e)
//      {
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode)RightPanel.roleTree.getLastSelectedPathComponent();
//        RightPanel.access$502(RightPanel. node);
//        if (RightPanel.cur_node == null)
//          return;
//
//        RightPanel.initPanelChangeFlag();
//        RightPanel.refreshRoleTree(RightPanel.cur_node, RightPanel.cur_tabIndex);
//      }
//
//    });
//    roleTree.addMouseListener(new MouseAdapter()
//    {
//      public void mousePressed(MouseEvent e)
//      {
//        if (e.getButton() == 3)
//          RightPanel.jpopmenu.show(e.getComponent(), e.getX(), e.getY());
//
//      }
//
//    });
//    btnAdd.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        RightPanel.btnAddAction();
//      }
//
//    });
//    addItem.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        RightPanel.btnAddAction();
//      }
//
//    });
//    change.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        RightPanel.btnChangeAction(e);
//      }
//
//    });
//    btnDel.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        RightPanel.delObject();
//      }
//
//    });
//    funtionTree.addMouseListener(new MouseAdapter()
//    {
//      public void mousePressed(MouseEvent e)
//      {
//        if (e.getButton() == 3)
//          RightPanel.popMenuSetRight.show(e.getComponent(), e.getX(), e.getY());
//
//      }
//
//    });
//    copyRight.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        if (RightPanel.cur_node == null)
//          return;
//
//        if (RightPanel.cur_node == RightPanel.roleTree.getModel().getRoot())
//          return;
//
//        Role role = (Role)RightPanel.cur_node.getUserObject();
//        CopyRightDialog crDialog = new CopyRightDialog(role);
//        crDialog.addPickRoleCopyListener(new IPickRoleCopyListener(role)
//        {
//          public void refreshDeptRight(List<Role> roles)
//          {
//            RightPanel.refreshCurRoleDept((Role)roles.get(0));
//            List right_nodes = new ArrayList();
//            if ((RightPanel.deptPanel == null) || (RightPanel.deptPanel.getDeptTree() == null) || (RightPanel.deptPanel.getDeptTree().getModel() == null))
//              return;
//
//            java.util.Enumeration enumt = ((DefaultMutableTreeNode)RightPanel.deptPanel.getDeptTree().getModel().getRoot()).breadthFirstEnumeration();
//            while (enumt.hasMoreElements()) {
//              DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumt.nextElement();
//              DeptCode dept = (DeptCode)node.getUserObject();
//              if (RightPanel.cur_role_dept.get(dept.getDeptCode_key()) != null)
//                right_nodes.add(node);
//            }
//
//            RightPanel.copyDeptRight(val$role, right_nodes);
//          }
//
//          public void refreshFuntionRight(List<Role> roles)
//          {
//            RightPanel.copyFuntionRight(val$role);
//          }
//
//        });
//        ContextManager.locateOnMainScreenCenter(crDialog);
//        crDialog.setVisible(true);
//        crDialog.setTitle("复制权限");
//        RightPanel.initPanelChangeFlag();
//        RightPanel.refreshRoleTree(RightPanel.cur_node, RightPanel.cur_tabIndex);
//      }
//
//    });
//    flashRight.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        if (RightPanel.cur_node == null)
//          return;
//
//        if (RightPanel.cur_node == RightPanel.roleTree.getModel().getRoot())
//          return;
//
//        Role role = (Role)RightPanel.cur_node.getUserObject();
//        List roles = new ArrayList();
//        java.util.Enumeration enumt = RightPanel.cur_node.children();
//        while (enumt.hasMoreElements()) {
//          DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumt.nextElement();
//          roles.add((Role)node.getUserObject());
//        }
//        if (roles.size() > 0) {
//          CopyParentRightDialog copyParentRightDialog = new CopyParentRightDialog(role, roles);
//          copyParentRightDialog.addPickRoleDeptListener(new IPickRoleCopyListener()
//          {
//            public void refreshDeptRight(List<Role> roles)
//            {
//              List right_nodes = new ArrayList();
//              java.util.Enumeration enumt = ((DefaultMutableTreeNode)RightPanel.deptPanel.getDeptTree().getModel().getRoot()).breadthFirstEnumeration();
//              while (enumt.hasMoreElements()) {
//                DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumt.nextElement();
//                DeptCode dept = (DeptCode)node.getUserObject();
//                if (RightPanel.cur_role_dept.get(dept.getDeptCode_key()) != null)
//                  right_nodes.add(node);
//              }
//
//              for (Role role : roles)
//                RightPanel.copyDeptRight(role, right_nodes);
//            }
//
//            public void refreshFuntionRight(List<Role> roles)
//            {
//              for (Role role : roles)
//                RightPanel.copyFuntionRight(role);
//
//            }
//
//          });
//          ContextManager.locateOnMainScreenCenter(copyParentRightDialog);
//          copyParentRightDialog.setModal(true);
//          copyParentRightDialog.setTitle("复制父级角色权限");
//          copyParentRightDialog.setVisible(true);
//          RightPanel.initPanelChangeFlag();
//          RightPanel.refreshRoleTree(RightPanel.cur_node, RightPanel.cur_tabIndex);
//        }
//      }
//
//    });
//    roleTree.expandRow(1);
//    roleTree.setSelectionRow(0);
//  }
//
//  private void copyFuntionRight(Role role) {
//    refreshCurRoleFuntion(role);
//    refreshFuntionRight();
//    HashSet save_funtions = new HashSet();
//    HashSet update_funtions = new HashSet();
//    java.util.Enumeration enumt = ((DefaultMutableTreeNode)funtionTree.getModel().getRoot()).breadthFirstEnumeration();
//    while (true) { FuntionRight fr;
//      RoleFuntion exist_funtion;
//      while (true) { while (true) { if (!(enumt.hasMoreElements())) break label213;
//          DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumt.nextElement();
//          fr = (FuntionRight)node.getUserObject();
//          if (fr.getFun_flag() != 0)
//            break;
//        }
//
//        if (cur_role_funtion.get(fr.getFuntionRight_key()) == null) {
//          roleFuntion = (RoleFuntion)UtilTool.createEntityAndAssignUID(RoleFuntion.class);
//          roleFuntion.setRole(role);
//          roleFuntion.setFuntionRight(fr);
//          roleFuntion.setFun_flag(Integer.valueOf(fr.getFun_flag()));
//          save_funtions.add(roleFuntion); break label210:
//        }
//        exist_funtion = (RoleFuntion)cur_role_funtion.get(fr.getFuntionRight_key());
//        if (exist_funtion.getFun_flag().intValue() != fr.getFun_flag())
//          break;
//      }
//      RoleFuntion roleFuntion = exist_funtion;
//      roleFuntion.setFun_flag(Integer.valueOf(fr.getFun_flag()));
//      label210: update_funtions.add(roleFuntion);
//    }
//
//    if (save_funtions.size() > 0)
//      label213: CommUtil.saveSet(save_funtions);
//
//    if (update_funtions.size() > 0)
//      CommUtil.updateSet(update_funtions);
//
//    refreshCurRoleFuntion(role);
//    refreshFuntionRight();
//    funtionTree.updateUI();
//  }
//
//  private void copyDeptRight(Role role, List<DefaultMutableTreeNode> right_nodes) {
//    for (DefaultMutableTreeNode node : right_nodes) {
//      refreshCurRoleDept(role);
//      refreshDeptRight();
//      giveDeptRight(role, node, 1);
//    }
//  }
//
//  private void initPanelChangeFlag() {
//    if (dept_change_flag != 0)
//      dept_change_flag = 1;
//
//    funtion_change_flag = 1;
//    if (field_change_flag != 0)
//      field_change_flag = 1;
//
//    if (code_change_flag != 0)
//      code_change_flag = 1;
//
//    if (entity_change_flag != 0)
//      entity_change_flag = 1;
//
//    if (report_change_flag != 0)
//      report_change_flag = 1;
//  }
//
//  private void giveRight(DefaultMutableTreeNode node, int tabIndex)
//  {
//    if (node == null)
//      return;
//
//    Object obj = node.getUserObject();
//    if (obj == null)
//      return;
//
//    if (obj instanceof Role) {
//      List list = new ArrayList();
//      java.util.Enumeration enumt1 = cur_node.breadthFirstEnumeration();
//      while (true) { DefaultMutableTreeNode tmpNode;
//        while (true) { if (!(enumt1.hasMoreElements())) break label93;
//          tmpNode = (DefaultMutableTreeNode)enumt1.nextElement();
//          if (tmpNode != cur_node)
//            break;
//        }
//        list.add((Role)tmpNode.getUserObject());
//      }
//      label93: Role role = (Role)obj;
//      DefaultMutableTreeNode right_node = null;
//      if (tabIndex == 0) {
//        DefaultMutableTreeNode tmpFuntionNode = (DefaultMutableTreeNode)funtionTree.getSelectionPath().getLastPathComponent();
//        if (tmpFuntionNode == null)
//          return;
//
//        FuntionRight tmpFuntionRight = (FuntionRight)tmpFuntionNode.getUserObject();
//        if (tmpFuntionRight.getFun_flag() == 1)
//          return;
//
//        right_node = tmpFuntionNode;
//      } else if (tabIndex == 1) {
//        DefaultMutableTreeNode tmpDeptNode = (DefaultMutableTreeNode)deptPanel.getDeptTree().getSelectionPath().getLastPathComponent();
//        if (tmpDeptNode == null)
//          return;
//
//        right_node = tmpDeptNode;
//      } else if (tabIndex == 2) {
//        DefaultMutableTreeNode tmpFieldNode = (DefaultMutableTreeNode)fieldTree.getSelectionPath().getLastPathComponent();
//        if (tmpFieldNode == null)
//          return;
//
//        right_node = tmpFieldNode;
//      } else if (tabIndex == 5) {
//        DefaultMutableTreeNode tmpReportNode = (DefaultMutableTreeNode)reportTree.getSelectionPath().getLastPathComponent();
//        if (tmpReportNode == null)
//          return;
//
//        right_node = tmpReportNode;
//      }
//      if ((copy_flag) && (list.size() > 0)) {
//        Role tmp_role = cur_parent_role;
//        CopyCurRightDialog copyDialog = new CopyCurRightDialog(list, right_node);
//        copyDialog.addPickRoleSelectionListener(new IPickRoleSelectionListener(tabIndex, role, tmp_role)
//        {
//          public void copyRight(List<Role> roles, DefaultMutableTreeNode right_node)
//          {
//            if (val$tabIndex == 0) {
//              RightPanel.refreshCurRoleFuntion(val$role);
//              RightPanel.refreshFuntionRight();
//              RightPanel.giveFuntionRight(val$role, right_node, 1);
//            } else if (val$tabIndex == 1) {
//              RightPanel.refreshCurRoleDept(val$role);
//              RightPanel.refreshDeptRight();
////              RightPanel.giveDeptRight(val$role, right_node, 1);
////            } else if (val$tabIndex == 2) {
////              RightPanel.refreshCurRoleField(val$role);
////              RightPanel.refreshFieldRight();
////              RightPanel.giveFieldRight(val$role, right_node, 1);
////            } else if (val$tabIndex == 5) {
////              RightPanel.refreshCurRoleReport(val$role);
////              RightPanel.refreshReportRight();
////              RightPanel.giveReportRight(val$role, right_node, 1);
////            }
////            RightPanel.access$3602(RightPanel. (Role)RightPanel.cur_node.getUserObject());
////            for (Role t_role : roles)
////              if (val$tabIndex == 0) {
////                RightPanel.refreshCurRoleFuntion(t_role);
////                RightPanel.refreshFuntionRight();
////                RightPanel.giveFuntionRight(t_role, right_node, 1);
////              } else if (val$tabIndex == 1) {
////                RightPanel.refreshCurRoleDept(t_role);
////                RightPanel.refreshDeptRight();
////                RightPanel.giveDeptRight(t_role, right_node, 1);
////              } else if (val$tabIndex == 2) {
////                RightPanel.refreshCurRoleField(t_role);
////                RightPanel.refreshFieldRight();
////                RightPanel.giveFieldRight(t_role, right_node, 1);
////              } else if (val$tabIndex == 5) {
////                RightPanel.refreshCurRoleReport(t_role);
////                RightPanel.refreshReportRight();
////                RightPanel.giveReportRight(t_role, right_node, 1);
////              }
////
////            RightPanel.access$3602(RightPanel. val$tmp_role);
////          }
////
////        });
////        ContextManager.locateOnMainScreenCenter(copyDialog);
////        copyDialog.setVisible(true);
////      }
////      else if (tabIndex == 0) {
////        giveFuntionRight(role, right_node, 1);
////      } else if (tabIndex == 1) {
//        giveDeptRight(role, right_node, 1);
////      } else if (tabIndex == 2) {
////        giveFieldRight(role, right_node, 1);
////      } else if (tabIndex == 5) {
////        giveReportRight(role, right_node, 1);
////      } else if (tabIndex == 4) {
////        giveCodeRight(role, codeTable.getSelectObjects());
////      } else if (tabIndex == 3) {
////        giveEntityRight(role, entityTable.getSelectObjects(), 1);
////        entityTable.updateUI();
////      }
////    }
////  }
//
//  private void viewRight(int tabIndex, DefaultMutableTreeNode node)
//  {
//    if (node == null)
//      return;
//
//    Object obj = node.getUserObject();
//    if (obj == null)
//      return;
//
//    Role role = (Role)obj;
//    if (tabIndex == 2) {
//      DefaultMutableTreeNode tmpFieldNode = (DefaultMutableTreeNode)fieldTree.getSelectionPath().getLastPathComponent();
//      if (tmpFieldNode == null)
//        return;
//
//      giveFieldRight(role, tmpFieldNode, 2);
//    } else if (tabIndex == 5) {
//      DefaultMutableTreeNode tmpReportNode = (DefaultMutableTreeNode)reportTree.getSelectionPath().getLastPathComponent();
//      if (tmpReportNode == null)
//        return;
//
//      giveReportRight(role, tmpReportNode, 2);
//    }
//  }
//
//  private void backRight(int tabIndex, DefaultMutableTreeNode node)
//  {
//    if (node == null)
//      return;
//
//    Object obj = node.getUserObject();
//    if (obj == null)
//      return;
//
//    if (obj instanceof Role) {
//      Role role = (Role)obj;
//      StringBuffer tmp_sb = null;
//      if (node.getChildCount() > 0) {
//        tmp_sb = new StringBuffer();
//        java.util.Enumeration enumt = node.breadthFirstEnumeration();
//        while (enumt.hasMoreElements()) {
//          DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode)enumt.nextElement();
//          Role tmp_role = (Role)tmp_node.getUserObject();
//          if (tmp_role != role)
//            tmp_sb.append("'" + tmp_role.getRole_key() + "',");
//        }
//
//        tmp_sb.append("'-1'");
//      }
//      if (tabIndex == 0) {
//        DefaultMutableTreeNode tmpFuntionNode = (DefaultMutableTreeNode)funtionTree.getSelectionPath().getLastPathComponent();
//        if (tmpFuntionNode == null)
//          return;
//
//        giveFuntionRight(role, tmpFuntionNode, 0);
//        if ((tmp_sb != null) && (!("'-1'".equals(cur_string.toString()))))
//          CommUtil.excuteSQL("delete from roleFuntion where role_key in(" + tmp_sb + ") and funtionRight_key in (" + cur_string + ")");
//      }
//      else if (tabIndex == 1) {
//        DefaultMutableTreeNode tmpDeptNode = (DefaultMutableTreeNode)deptPanel.getDeptTree().getSelectionPath().getLastPathComponent();
//        if (tmpDeptNode == null)
//          return;
//
//        giveDeptRight(role, tmpDeptNode, 0);
//      } else if (tabIndex == 2) {
//        DefaultMutableTreeNode tmpFieldNode = (DefaultMutableTreeNode)fieldTree.getSelectionPath().getLastPathComponent();
//        if (tmpFieldNode == null)
//          return;
//
//        giveFieldRight(role, tmpFieldNode, 0);
//        if ((tmp_sb != null) && (cur_string != null) && (!("'-1'".equals(cur_string.toString()))))
//          CommUtil.excuteSQL("delete from roleField where role_key in(" + tmp_sb + ") and field_name in (" + cur_string + ")");
//      }
//      else if (tabIndex == 5) {
//        DefaultMutableTreeNode tmpReportNode = (DefaultMutableTreeNode)reportTree.getSelectionPath().getLastPathComponent();
//        if (tmpReportNode == null)
//          return;
//
//        giveReportRight(role, tmpReportNode, 0);
//        if ((tmp_sb != null) && (cur_string != null) && (!("'-1'".equals(cur_string.toString()))))
//          CommUtil.excuteSQL("delete from roleReport where role_key in(" + tmp_sb + ") and reportDef_key in (" + cur_string + ")");
//
//      }
//      else if (tabIndex == 3) {
//        List delObjects = new ArrayList();
//        for (Iterator i$ = entityTable.getSelectObjects().iterator(); i$.hasNext(); ) { Object obj2 = i$.next();
//          RoleEntity rc = (RoleEntity)obj2;
//          if (cur_role_entity.get(rc.getEntityDef().getEntity_key()) != null) {
//            delObjects.add(cur_role_entity.get(rc.getEntityDef().getEntity_key()));
//            cur_role_entity.remove(rc.getEntityDef().getEntity_key());
//          }
//          rc.setAdd_flag(false);
//          rc.setDel_flag(false);
//          rc.setEdit_flag(false);
//          rc.setView_flag(false);
//        }
//        if (delObjects.size() > 0)
//          CommUtil.deleteList(delObjects);
//
//        entityTable.updateUI();
//      }
//    }
//  }
//
//  private void delObject()
//  {
//    if (cur_node == null)
//      return;
//
//    if (cur_node.getChildCount() > 0) {
//      JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "该角色下有子角色，请删除子角色后再做此操作", "错误", 0);
//      return;
//    }
//    Object obj = cur_node.getUserObject();
//
//    if (obj instanceof Role) {
//      if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(), "确定要删除[" + ((Role)obj).getRole_name() + "]吗", "询问", 2, 3) != 0)
//      {
//        return;
//      }
//      Role tmp_role = (Role)obj;
//      ((DefaultMutableTreeNode)cur_node.getParent()).remove(cur_node);
//      CommUtil.excuteSQL("delete from a01password where role_key ='" + tmp_role.getRole_key() + "'");
//      CommUtil.deleteEntity(obj);
//      roleTree.updateUI();
//    }
//  }
//
//  private void btnAddAction()
//  {
//    if (cur_node == null)
//      return;
//
//    Object role = cur_node.getUserObject();
//    if ((!(UserContext.getPerson_code().equals(UserContext.sysManName))) && (role instanceof String))
//    {
//      return;
//    }
//    AddRoleDialog addRoleDialog = new AddRoleDialog(role);
//    ContextManager.locateOnScreenCenter(addRoleDialog);
//    addRoleDialog.setTitle("增加用户");
//    addRoleDialog.addIPickRoleGroupListener(new IPickRoleGroupListener()
//    {
//      public void perform()
//      {
//        RightPanel.performAdd();
//      }
//
//    });
//    addRoleDialog.setVisible(true);
//  }
//
//  private void btnChangeAction(ActionEvent e) {
//    if (cur_node == roleTree.getModel().getRoot())
//      return;
//
//    Role role = (Role)cur_node.getUserObject();
//
//    EditRoleDialog editRoleDailog = new EditRoleDialog(role);
//    ContextManager.locateOnScreenCenter(editRoleDailog);
//    editRoleDailog.setTitle("编辑用户");
//    editRoleDailog.addIPickRoleGroupListener(new IPickRoleGroupListener()
//    {
//      public void perform()
//      {
//        RightPanel.performAdd();
//      }
//
//    });
//    editRoleDailog.setVisible(true);
//  }
//
//  private void performAdd() {
//    RoleModel model = (RoleModel)roleTree.getModel();
//    model.fresh();
//    roleTree.updateUI();
//  }
//
//  private void refreshRoleTree(DefaultMutableTreeNode node, int tab_index)
//  {
//    if (node != null) {
//      Object obj = node.getUserObject();
//      DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode)node.getParent();
//      Object parent_obj = null;
//      Role parent_role = null;
//      if (parent_node != null)
//        if ("全部角色".equals(parent_node.toString()))
//          cur_parent_role = null;
//        else
//          parent_obj = parent_node.getUserObject();
//
//
//      boolean change_flag = false;
//      if (obj instanceof Role) {
//        Role role = (Role)obj;
//        if (parent_obj instanceof Role) {
//          parent_role = (Role)parent_obj;
//          cur_parent_role = parent_role;
//          change_flag = true;
//        } else {
//          cur_parent_role = null;
//        }
//        if (tab_index == 0) {
//          if (funtion_change_flag == 1) {
//            if (change_flag)
//            {
//              funtionTree.setModel(new FuntionTreeModel(parent_role, funtionRight_list));
//            }
//            else funtionTree.setModel(new FuntionTreeModel());
//
//            refreshCurRoleFuntion(role);
//            refreshFuntionRight();
//            funtionTree.updateUI();
//            funtion_change_flag += 1;
//          }
//        } else if (tab_index == 1) {
//          initDeptRight(true);
//          if (dept_change_flag == 1) {
//            rebuildDeptTree(change_flag, parent_role);
//            refreshCurRoleDept(role);
//            refreshDeptRight();
//            deptPanel.getDeptTree().updateUI();
//            dept_change_flag += 1;
//          }
//        }
//        else if (tab_index == 2) {
//          initFieldRight();
//          if (field_change_flag == 1) {
//            if (change_flag)
//            {
//              fieldTree.setModel(new RebuildTreeModel(parent_role));
//            }
//            else fieldTree.setModel(new RebuildTreeModel());
//
//            refreshCurRoleField(role);
//            refreshFieldRight();
//            fieldTree.updateUI();
//            field_change_flag += 1;
//          }
//        } else if (tab_index == 3) {
//          initEntityRight();
//          if (entity_change_flag == 1) {
//            refreshCurRoleEntity(role);
//            refreshEntityRight();
//            entity_change_flag += 1;
//          }
//          pnlEntityRight.updateUI();
//        } else if (tab_index == 4) {
//          initCodeRight();
//          if (code_change_flag == 1) {
//            refreshCurRoleCode(role);
//            refreshCodeRight();
//            codeTable.updateUI();
//            code_change_flag += 1;
//          }
//        } else if (tab_index == 5) {
//          initReportRight();
//          if (report_change_flag == 1) {
//            if (change_flag)
//              reportTree.setModel(new ReportModel(parent_role));
//            else
//              reportTree.setModel(new ReportModel());
//
//            refreshCurRoleReport(role);
//            refreshReportRight();
//            reportTree.updateUI();
//            report_change_flag += 1;
//          }
//        } else if (tab_index == 6) {
//          initRolePerson();
//          refreshRolePerson(role);
//        }
//      }
//      else if (tab_index == 0) {
//        funtionTree.setModel(new FuntionTreeModel());
//        funtionTree.updateUI();
//      } else if (tab_index == 1) {
//        dept_change_flag = 0;
//        initDeptRight(true);
//      } else if (tab_index == 2) {
//        fieldTree.setModel(new RebuildTreeModel());
//        initFieldRight();
//      } else if (tab_index == 3) {
//        entity_change_flag = 0;
//        initEntityRight();
//        cur_role_entity.clear();
//        entity_change_flag = 1;
//      } else if (tab_index == 4) {
//        initCodeRight();
//      } else if (tab_index == 5) {
//        reportTree.setModel(new ReportModel());
//        reportTree.updateUI();
//        initReportRight();
//      } else if (tab_index == 6) {
//        initRolePerson();
//      }
//    }
//  }
//
//  private void rebuildDeptTree(boolean flag, Role role)
//  {
//    if (flag) {
//      depts.clear();
//      HashSet<String> deptcode_set = new HashSet<String>();
//      List<DeptCode> dept_list = new ArrayList<DeptCode>();
//      List roleDept_list = CommUtil.fetchEntities("from RoleDept rd join fetch rd.role join fetch rd.deptCode where rd.role.role_key ='" + role.getRole_key() + "'");
//      for (Iterator i$ = roleDept_list.iterator(); i$.hasNext(); ) { Object rd_obj = i$.next();
//        RoleDept rd = (RoleDept)rd_obj;
//        DeptCode tmp_dept = rd.getDeptCode();
//        int level = tmp_dept.getGrade().intValue();
//        for (int i = 1; i < level; ++i) {
//          int st = getDeptCodeLength(i);
//          String tmp_dept_code_str = tmp_dept.getDept_code().substring(0, st);
//          System.out.println("code:::" + tmp_dept_code_str);
//          if (!(deptcode_set.contains(tmp_dept_code_str)))
//            deptcode_set.add(tmp_dept_code_str);
//        }
//
//        dept_list.add(rd.getDeptCode());
//      }
//      StringBuffer strbuf = new StringBuffer();
//      if (dept_list.size() > 0){
//        for (DeptCode dc : dept_list){
//          strbuf.append("dept_code like '" + dc.getDept_code() + "%' or ");
//        }
//      }
//
//      if (deptcode_set.size() > 0) {
//        StringBuffer s_sb = new StringBuffer();
//        for (String tmp_string : deptcode_set){
//          s_sb.append("'" + tmp_string + "',");
//        }
//
//        s_sb.append("'-1'");
//        strbuf.append("dept_code in (" + s_sb + ") or ");
//      }
//      strbuf.append("1 = 0");
//
//      List d_list = CommUtil.fetchEntities("from DeptCode where " + strbuf + "order by dept_code");
//      depts.addAll(d_list);
//      dept_change_flag = 0;
//      initDeptRight(false);
//    } else {
//      dept_change_flag = 0;
//      initDeptRight(true);
//    }
//  }
//
//  private int getDeptCodeLength(int level)
//  {
//    int len = 0;
//    String[] tmp = sp.getSysparameter_value().split(";");
//    for (int i = 0; i < level; ++i)
//      len += Integer.valueOf(tmp[i]).intValue();
//
//    return len;
//  }
//
//  private void initFieldRight()
//  {
//    if (field_change_flag == 0) {
//      rebuildTreeModel = new RebuildTreeModel();
//      fieldTree = new JTree(rebuildTreeModel);
//      fieldTree.setCellRenderer(new CellRebuildTreeRenderer());
//      pnlFieldRight.add(new JScrollPane(fieldTree), "Center");
//      fieldTree.addMouseListener(new MouseAdapter()
//      {
//        public void mousePressed(MouseEvent e)
//        {
//          if (e.getButton() == 3)
//            RightPanel.popMenuSetRigth1.show(e.getComponent(), e.getX(), e.getY());
//
//        }
//
//      });
//      field_change_flag += 1;
//    }
//  }
//
//  private void getRihgtField()
//  {
//    Iterator i$;
//    fieldDef_set.clear();
//    if (cur_parent_role != null) {
//      List fun_list = CommUtil.fetchEntities("from RoleField rf join fetch rf.role where rf.role.role_key = '" + cur_parent_role.getRole_key() + "'");
//      for (i$ = fun_list.iterator(); i$.hasNext(); ) { Object obj = i$.next();
//        RoleField rf = (RoleField)obj;
//        if (rf.getFun_flag() == 2)
//          fieldDef_set.add(rf.getField_name());
//      }
//    }
//  }
//
//  private void getRihgtReport()
//  {
//    Iterator i$;
//    report_set.clear();
//    if (cur_parent_role != null) {
//      List fun_list = CommUtil.fetchEntities("from RoleReport rf join fetch rf.role join fetch rf.reportDef where rf.role.role_key = '" + cur_parent_role.getRole_key() + "'");
//      for (i$ = fun_list.iterator(); i$.hasNext(); ) { Object obj = i$.next();
//        RoleReport rf = (RoleReport)obj;
//        if (rf.getFun_flag() == 2)
//          report_set.add(rf.getReportDef());
//      }
//    }
//  }
//
//  private void initEntityRight()
//  {
//    if (entity_change_flag == 0) {
//      pnlEntityRight.removeAll();
//      List fields = new ArrayList();
//      fields.add("entityDef.entityClass.moduleInfo.module_name");
//      fields.add("entityDef.entityCaption");
//      fields.add("add_flag");
//      fields.add("edit_flag");
//      fields.add("view_flag");
//      fields.add("del_flag");
//      fields.add("right_sql");
//      entityTable = new FTable(RoleEntity.class, fields, false, false, false, "RightPanel");
//      List tmp_entitys = CommUtil.fetchEntities("from EntityDef ed join fetch ed.entityClass ec join fetch ec.moduleInfo mi order by mi.order_no ");
//      if (tmp_entitys != null) {
//        List list = new ArrayList();
//        for (Iterator i$ = tmp_entitys.iterator(); i$.hasNext(); ) { Object obj1 = i$.next();
//          EntityDef ed = (EntityDef)obj1;
//          RoleEntity roleEntity = (RoleEntity)UtilTool.createEntityAndAssignUID(RoleEntity.class);
//          roleEntity.setEntityDef(ed);
//          list.add(roleEntity);
//        }
//        entityTable.setObjects(list);
//      } else {
//        entityTable.setObjects(new ArrayList()); }
//      entityTable.updateUI();
//      entityTable.setEditable(true);
//      pnlEntityRight.add(entityTable, "Center");
//      entityTable.addRowChangeListner(new RowChangeListner()
//      {
//        public void rowChanged(Object obj)
//        {
//          RightPanel.access$3902(RightPanel. (RoleEntity)obj);
//          boolean view_flag = RightPanel.change_entity.isView_flag();
//          if ((RightPanel.change_entity.isAdd_flag()) || (RightPanel.change_entity.isEdit_flag()) || (RightPanel.change_entity.isDel_flag()))
//            view_flag = true;
//          RightPanel.change_entity.setView_flag(view_flag);
//          RightPanel.entityTable.updateUI();
//        }
//
//      });
//      entityTable.addListSelectionListener(new Object()
//      {
//        public void valueChanged(ListSelectionEvent e)
//        {
//          if (RightPanel.cur_node == null)
//            return;
//
//          Object obj = RightPanel.cur_node.getUserObject();
//          if (!(obj instanceof Role))
//            return;
//
//          if (RightPanel.change_entity != null) {
//            List rcs = new ArrayList();
//            rcs.add(RightPanel.change_entity);
//            RightPanel.giveEntityRight((Role)RightPanel.cur_node.getUserObject(), rcs, 0);
//            RightPanel.access$3902(RightPanel. null);
//          }
//        }
//
//      });
//      entityTable.addMouseListener(new MouseAdapter()
//      {
//        public void mouseClicked(MouseEvent e)
//        {
//          if (RightPanel.cur_node == null)
//            return;
//
//          Object obj1 = RightPanel.cur_node.getUserObject();
//          if (obj1 == null)
//            return;
//
//          if (!(obj1 instanceof Role))
//            return;
//
//          if (e.getButton() == 3) {
//            RightPanel.popMenuSetRight.show(e.getComponent(), e.getX(), e.getY());
//            return;
//          }
//          if (e.getClickCount() != 2)
//            return;
//
//          Object obj = RightPanel.entityTable.getCurrentRow();
//          if (obj == null)
//            return;
//
//          RoleQueryDialog rqDialog = new RoleQueryDialog(((RoleEntity)obj).getEntityDef());
//          ContextManager.locateOnMainScreenCenter(rqDialog);
//          rqDialog.setVisible(true);
//          if (rqDialog.isClick_ok()) {
//            QueryScheme qs = rqDialog.getQueryScheme();
//            if (qs != null)
//              RightPanel.cur_entity.setRight_sql(qs.buildSql());
//          }
//
//        }
//
//      });
//      entity_change_flag += 1;
//    }
//  }
//
//  private void initCodeRight()
//  {
//    if (code_change_flag == 0) {
//      List fields = new ArrayList();
//      fields.add("code.code_name");
//      fields.add("add_flag");
//      fields.add("edit_flag");
//      fields.add("del_flag");
//      fields.add("code_limit");
//      codeTable = new FTable(RoleCode.class, fields, false, false, false, "RightPanel");
//      List code_rights = new ArrayList();
//      List tmp = CodeManager.getCodeManager().getCode_types();
//      for (Code code : tmp) {
//        RoleCode roleCode = (RoleCode)UtilTool.createEntityAndAssignUID(RoleCode.class);
//        roleCode.setCode(code);
//        code_rights.add(roleCode);
//      }
//      codeTable.setObjects(code_rights);
//      codeTable.updateUI();
//      codeTable.setEditable(true);
//      pnlCodeRight.add(codeTable, "Center");
//      codeTable.addRowChangeListner(new RowChangeListner()
//      {
//        public void rowChanged(Object obj)
//        {
//          RightPanel.access$4302(RightPanel. (RoleCode)obj);
//        }
//
//      });
//      codeTable.addListSelectionListener(new ListSelectionListener()
//      {
//        public void valueChanged(ListSelectionEvent e)
//        {
//          if (RightPanel.cur_node == null)
//            return;
//
//          Object obj = RightPanel.cur_node.getUserObject();
//          if (!(obj instanceof Role))
//            return;
//
//          if (RightPanel.change_code != null) {
//            List rcs = new ArrayList();
//            rcs.add(RightPanel.change_code);
//            RightPanel.giveCodeRight((Role)RightPanel.cur_node.getUserObject(), rcs);
//            RightPanel.access$4302(RightPanel. null);
//          }
//        }
//
//      });
//      codeTable.addMouseListener(new MouseAdapter()
//      {
//        public void mouseClicked(MouseEvent e)
//        {
//          if (RightPanel.cur_node == null)
//            return;
//
//          if (e.getButton() == 3) {
//            RightPanel.popMenuSetRight.show(e.getComponent(), e.getX(), e.getY());
//            return;
//          }
//          if (e.getClickCount() != 2)
//            return;
//
//          Object obj1 = RightPanel.cur_node.getUserObject();
//          if (obj1 == null)
//            return;
//
//          if (!(obj1 instanceof Role))
//            return;
//
//          Object obj = RightPanel.codeTable.getCurrentRow();
//          if (obj == null)
//            return;
//
//          RoleCode rc = (RoleCode)obj;
//          Code code = rc.getCode();
//          List codes = CodeManager.getCodeManager().getCodeListBy(code.getCode_type());
//          CodeSelectManyDlg csmDlg = new CodeSelectManyDlg(codes);
//          ContextManager.locateOnMainScreenCenter(csmDlg);
//          csmDlg.setVisible(true);
//          if (csmDlg.isClick_ok()) {
//            List select_codes = csmDlg.getSelect_codes();
//            RightPanel.sortCodes(select_codes);
//            HashSet select_keys = new HashSet();
//            List result_codes = new ArrayList();
//            boolean exists = false;
//            Iterator i$ = select_codes.iterator();
//            while (true) { Code c;
//              while (true) { if (!(i$.hasNext())) break label301; c = (Code)i$.next();
//                exists = false;
//                for (String key : select_keys)
//                  if (c.getCode_id().startsWith(key)) {
//                    exists = true;
//                    break;
//                  }
//
//                if (!(exists))
//                  break;
//              }
//              select_keys.add(c.getCode_id());
//              result_codes.add(c);
//            }
//            String code_limit = "";
//            String code_sql = "";
//            for (Code c : result_codes) {
//              code_limit = code_limit + ";" + c.getCode_name();
//              code_sql = code_sql + ";" + c.getCode_id();
//            }
//            if (!(code_limit.equals(""))) {
//              code_limit = code_limit.substring(1);
//              code_sql = code_sql.substring(1);
//            }
//            rc.setCode_limit(code_limit);
//            rc.setRight_sql(code_sql);
//            RightPanel.codeTable.updateUI();
//          }
//        }
//
//      });
//      code_change_flag += 1;
//    }
//  }
//
//  private void sortCodes(List<Code> fieldList)
//  {
//    Collections.sort(fieldList, new Comparator()
//    {
//      public int compare(Object arg0, Object arg1)
//      {
//        Code field0 = (Code)arg0;
//        Code field1 = (Code)arg1;
//
//        Integer order_no0 = Integer.valueOf((field0 == null) ? 0 : Integer.valueOf(field0.getCode_id()).intValue());
//        Integer order_no1 = Integer.valueOf((field1 == null) ? 0 : Integer.valueOf(field1.getCode_id()).intValue());
//
//        return order_no0.compareTo(order_no1);
//      }
//    });
//  }
//
//  private void giveEntityRight(Role role, List<RoleEntity> res, int type)
//  {
//    List updateList = new ArrayList();
//    List saveList = new ArrayList();
//    List delList = new ArrayList();
//    Iterator i$ = res.iterator();
//    while (true) { RoleEntity roleCode;
//      while (true) { if (!(i$.hasNext())) break label352; RoleEntity rc = (RoleEntity)i$.next();
//
//        if (cur_role_entity.get(rc.getEntityDef().getEntity_key()) != null)
//          roleCode = (RoleEntity)cur_role_entity.get(rc.getEntityDef().getEntity_key());
//        else
//          roleCode = (RoleEntity)UtilTool.createEntityAndAssignUID(RoleEntity.class);
//
//        if (type == 1) {
//          rc.setAdd_flag(true);
//          rc.setDel_flag(true);
//          rc.setEdit_flag(true);
//          rc.setView_flag(true);
//        }
//        roleCode.setEntityDef(rc.getEntityDef());
//        roleCode.setAdd_flag(rc.isAdd_flag());
//        roleCode.setDel_flag(rc.isDel_flag());
//        roleCode.setEdit_flag(rc.isEdit_flag());
//        roleCode.setRight_sql(rc.getRight_sql());
//        roleCode.setRole(role);
//        if ((rc.isAdd_flag()) || (rc.isDel_flag()) || (rc.isEdit_flag()))
//          roleCode.setView_flag(true);
//        else
//          roleCode.setView_flag(rc.isView_flag());
//
//        if (roleCode.getNew_flag() != 1) break;
//        if ((roleCode.isAdd_flag()) || (roleCode.isDel_flag()) || (roleCode.isEdit_flag()) || (roleCode.isView_flag()))
//          break;
//      }
//      saveList.add(roleCode); continue;
//
//      if ((!(roleCode.isAdd_flag())) && (!(roleCode.isDel_flag())) && (!(roleCode.isEdit_flag())) && (!(roleCode.isView_flag())))
//        label294: delList.add(roleCode);
//      else
//        updateList.add(roleCode);
//
//    }
//
//    List list = new ArrayList();
//    list.add(saveList);
//    list.add(updateList);
//    list.add(delList);
//    ValidateSQLResult result = CommUtil.save_update_del(list, "sud");
//    if (result.getResult() == 0)
//    {
//      for (RoleEntity rc : saveList) {
//        rc.setNew_flag(0);
//        cur_role_entity.put(rc.getEntityDef().getEntity_key(), rc);
//      }
//      for (RoleEntity re : delList)
//        cur_role_entity.remove(re.getEntityDef().getEntity_key());
//
//      UserContext.showSuccessMsg(null);
//    } else {
//      FormulaTextDialog.showErrorMsg(result.getMsg());
//    }
//  }
//
//  private void giveCodeRight(Role role, List<RoleCode> rcs) {
//    List updateList = new ArrayList();
//    List saveList = new ArrayList();
//    List delList = new ArrayList();
//    Iterator i$ = rcs.iterator();
//    while (true) { RoleCode roleCode;
//      while (true) { if (!(i$.hasNext())) break; RoleCode rc = (RoleCode)i$.next();
//
//        if (cur_role_code.get(rc.getCode().getCode_key()) != null)
//          roleCode = (RoleCode)cur_role_code.get(rc.getCode().getCode_key());
//        else
//          roleCode = (RoleCode)UtilTool.createEntityAndAssignUID(RoleCode.class);
//
//        roleCode.setCode(rc.getCode());
//        roleCode.setAdd_flag(rc.isAdd_flag());
//        roleCode.setDel_flag(rc.isDel_flag());
//        roleCode.setEdit_flag(rc.isEdit_flag());
//        roleCode.setRight_sql(rc.getRight_sql());
//        roleCode.setRole(role);
//        if (roleCode.getNew_flag() != 1) break label213;
//        if ((roleCode.isAdd_flag()) || (roleCode.isDel_flag()) || (roleCode.isEdit_flag()))
//          break;
//      }
//      saveList.add(roleCode); continue;
//
//      if ((!(roleCode.isAdd_flag())) && (!(roleCode.isDel_flag())) && (!(roleCode.isEdit_flag())))
//        label213: delList.add(roleCode);
//      else
//        updateList.add(roleCode);
//
//    }
//
//    List list = new ArrayList();
//    list.add(saveList);
//    list.add(updateList);
//    list.add(delList);
//    ValidateSQLResult result = CommUtil.save_update_del(list, "sud");
//    if (result.getResult() == 0) {
//      Object obj;
//      for (Iterator i$ = saveList.iterator(); i$.hasNext(); ) { obj = i$.next();
//        RoleCode rc = (RoleCode)obj;
//        rc.setNew_flag(0);
//        cur_role_code.put(rc.getCode().getCode_key(), rc);
//      }
//      for (i$ = delList.iterator(); i$.hasNext(); ) { obj = i$.next();
//        RoleCode re = (RoleCode)obj;
//        cur_role_code.remove(re.getCode().getCode_key());
//      }
//      UserContext.showSuccessMsg(null);
//    } else {
//      FormulaTextDialog.showErrorMsg(result.getMsg());
//    }
//  }
//
//  private void initReportRight()
//  {
//    if (report_change_flag == 0) {
//      reportTree.setModel(new ReportModel());
//      reportTree.setCellRenderer(new CellReportTreeRenderer());
//      pnlReport.add(new JScrollPane(reportTree), "Center");
//      reportTree.addMouseListener(new MouseAdapter()
//      {
//        public void mousePressed(MouseEvent e)
//        {
//          if (e.getButton() == 3)
//            RightPanel.popMenuSetRigth1.show(e.getComponent(), e.getX(), e.getY());
//
//        }
//
//      });
//      report_change_flag += 1;
//    }
//  }
//
//  private void initRolePerson()
//  {
//    if (person_change_flag == 0) {
//      List fields = new ArrayList();
//      fields.add("a01.a0190");
//      fields.add("a01.a0101");
//      fields.add("a01.deptCode.dept_full_name");
//      beanTablePanel3 = new FTable(A01PassWord.class);
//      beanTablePanel3.setFields(fields);
//      pnlPerson.add(beanTablePanel3, "Center");
//      beanTablePanel1 = new FTable(A01.class);
//      beanTablePanel1.setFields(columnList);
//      pnlPersonMain.add(beanTablePanel1, "Center");
//      deptPersonPanel = new DeptPersonPanel(true, "1", A01.class, depts, false);
//      deptPersonPanel.removeQuickButton();
//      pnlDept.add(deptPersonPanel, "Center");
//      deptPersonPanel.addPickDeptListener(new org.jhrcore.ui.listener.IPickDeptListener()
//      {
//        public void pickDept(Object dept)
//        {
//          RightPanel.pickPerson();
//        }
//
//      });
//      deptPersonPanel.addPickPersonClassListner(new IPickPersonClassListener()
//      {
//        public void pickPersonClass(Class personClass)
//        {
//          RightPanel.pickPerson();
//        }
//
//      });
//      pickPerson();
//      person_change_flag += 1;
//    }
//  }
//
//  private void pickPerson() {
//    String entity_name = deptPersonPanel.getPersonClass().getSimpleName();
//    DeptCode dept = (DeptCode)deptPersonPanel.getCurDept();
//    String hql = " from " + entity_name + " bp join fetch bp.deptCode where bp.a0193 = 0 and bp.deptCode.dept_code like '" + dept.getDept_code() + "%'";
//    if (!(UserContext.person_right_str.equals(UserContext.sysManName)))
//      hql = hql + " and bp.deptCode.deptCode_key in(" + UserContext.person_right_str + ")";
//
//    PublicUtil.getProps_value().setProperty(A01.class.getName(), hql.substring(0, hql.indexOf("where")) + "where bp.a01_key in");
//    PublicUtil.getProps_value().setProperty(entity_name, hql.substring(0, hql.indexOf("where")) + "where bp.a01_key in");
//    List keys = CommUtil.fetchEntities("select bp.a01_key " + hql.replace("join fetch bp.deptCode", ""));
//    beanTablePanel1.setObjects(keys);
//  }
//
//    private void initDeptRight(boolean all_flag) {
//        if (dept_change_flag == 0) {
//            if (all_flag) {
//                depts.clear();
//                Iterator i = UserContext.depts.iterator();
//                while (true) {
//                    DeptCode dc = null;
//                    while (true) {
//                        if (!(i.hasNext())) {
//                            break;
//                        }
//                        dc = (DeptCode) i.next();
//                        if (!(dc.isDel_flag())) {
//                            break;
//                        }
//                    }
//                    dc.setFun_flag(0);
//                    depts.add(dc);
//                }
//            }
//            pnlDeptRight.removeAll();
//            deptPanel = new DeptPanel(depts, 1);
//            deptPanel.getDeptTree().setCellRenderer(new CellDeptTreeRenderer());
//            pnlDeptRight.add(deptPanel, "Center");
//            pnlDeptRight.updateUI();
//            deptPanel.getDeptTree().addMouseListener(new MouseAdapter() {
//
//                public void mousePressed(MouseEvent e) {
//                    if (e.getButton() == 3) {
//                        RightPanel.popMenuSetRight.show(e.getComponent(), e.getX(), e.getY());
//                    }
//
//                }
//            });
//            dept_change_flag += 1;
//        }
//    }
//
//  private void refreshFuntionRight()
//  {
//    FuntionRight funtionRight;
//    DefaultMutableTreeNode no;
//    DefaultMutableTreeNode root = (DefaultMutableTreeNode)funtionTree.getModel().getRoot();
//    java.util.Enumeration enumt = root.breadthFirstEnumeration();
//    List parent_nodes = new ArrayList();
//    if (cur_role_funtion.size() > 0) {
//      while (true) { while (true) { if (!(enumt.hasMoreElements())) break label145;
//          no = (DefaultMutableTreeNode)enumt.nextElement();
//          if (no != root)
//            break;
//        }
//        funtionRight = (FuntionRight)no.getUserObject();
//        if (cur_role_funtion.get(funtionRight.getFuntionRight_key()) != null) {
//          funtionRight.setFun_flag(((RoleFuntion)cur_role_funtion.get(funtionRight.getFuntionRight_key())).getFun_flag().intValue());
//          if (funtionRight.getFun_flag() == 1)
//            parent_nodes.add(no);
//        }
//        else {
//          funtionRight.setFun_flag(0);
//        }
//      }
//      label145: refreshParentFuntion(parent_nodes); } else {
//      while (true) {
//        while (true) { if (!(enumt.hasMoreElements())) return;
//          no = (DefaultMutableTreeNode)enumt.nextElement();
//          if (no != root)
//            break;
//        }
//        funtionRight = (FuntionRight)no.getUserObject();
//        funtionRight.setFun_flag(0);
//      }
//    }
//  }
//
//  private void refreshParentFuntion(List<DefaultMutableTreeNode> parent_nodes) {
//    for (DefaultMutableTreeNode node : parent_nodes) {
//      FuntionRight parent_fr = (FuntionRight)node.getUserObject();
//      java.util.Enumeration enumt = node.children();
//      int fun_flag = 1;
//      while (enumt.hasMoreElements()) {
//        DefaultMutableTreeNode child_node = (DefaultMutableTreeNode)enumt.nextElement();
//        FuntionRight fr = (FuntionRight)child_node.getUserObject();
//        if (fr.getFun_flag() != 1) {
//          fun_flag = 2;
//          break;
//        }
//      }
//      parent_fr.setFun_flag(fun_flag);
//      node.setUserObject(parent_fr);
//      fun_flag = 1;
//      while (node != funtionTree.getModel().getRoot()) {
//        node = (DefaultMutableTreeNode)node.getParent();
//        FuntionRight parent_fr1 = (FuntionRight)node.getUserObject();
//        java.util.Enumeration enumt1 = node.children();
//        while (enumt1.hasMoreElements()) {
//          DefaultMutableTreeNode child_node = (DefaultMutableTreeNode)enumt1.nextElement();
//          FuntionRight fr = (FuntionRight)child_node.getUserObject();
//          if (fr.getFun_flag() != 1)
//            fun_flag = 2;
//        }
//
//        parent_fr1.setFun_flag(fun_flag);
//        node.setUserObject(parent_fr1);
//      }
//    }
//  }
//
//    public void refreshDeptRight() {
//        DeptCode dept;
//        DefaultMutableTreeNode no = null;
//        Set<DefaultMutableTreeNode> refreshNode = new HashSet<DefaultMutableTreeNode>();
//        DefaultMutableTreeNode root = (DefaultMutableTreeNode) deptPanel.getDeptTree().getModel().getRoot();
//        java.util.Enumeration enumt = root.breadthFirstEnumeration();
//        if (cur_role_dept.size() > 0) {
//            while (true) {
//                while (true) {
//                    if (!(enumt.hasMoreElements())) {
//                        break;
//                    }
//                    no = (DefaultMutableTreeNode) enumt.nextElement();
//                    if (no != root) {
//                        break;
//                    }
//                }
//                dept = (DeptCode) no.getUserObject();
//                if (cur_role_dept.get(dept.getDeptCode_key()) != null) {
//                    refreshNode.add(no);
//                } else {
//                    dept.setFun_flag(0);
//                }
//            }
//        }
//        while (true) {
//            while (true) {
//                if (!(enumt.hasMoreElements())) {
//                    break;
//                }
//                no = (DefaultMutableTreeNode) enumt.nextElement();
//                if (no != root) {
//                    break;
//                }
//            }
//            dept = (DeptCode) no.getUserObject();
//            dept.setFun_flag(0);
//        }
//
//        for (DefaultMutableTreeNode node : refreshNode) {
//            refreshDeptNode(node);
//        }
//    }
//
//  private void refreshDeptNode(DefaultMutableTreeNode node)
//  {
//    DeptCode dept;
//    java.util.Enumeration enumt = node.breadthFirstEnumeration();
//    DefaultMutableTreeNode root = (DefaultMutableTreeNode)deptPanel.getDeptTree().getModel().getRoot();
//    while (enumt.hasMoreElements()) {
//      DefaultMutableTreeNode no = (DefaultMutableTreeNode)enumt.nextElement();
//      dept = (DeptCode)no.getUserObject();
//      dept.setFun_flag(1);
//    }
//    while (node != root) {
//      DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)node.getParent();
//      if (tmpParent_node == root)
//        return;
//
//      dept = (DeptCode)tmpParent_node.getUserObject();
//      dept.setFun_flag(2);
//      node = tmpParent_node;
//    }
//  }
//
//  private void refreshFieldRight()
//  {
//    java.util.Enumeration enumt;
//    DefaultMutableTreeNode tmpNode;
//    Object cur_obj;
//    FieldDef fieldDef;
//    Set refresh_nodes = new HashSet();
//    DefaultMutableTreeNode root = (DefaultMutableTreeNode)fieldTree.getModel().getRoot();
//    if (cur_role_field.size() > 0) {
//      enumt = root.breadthFirstEnumeration();
//      while (enumt.hasMoreElements()) {
//        tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//        cur_obj = tmpNode.getUserObject();
//        if (cur_obj instanceof FieldDef) {
//          fieldDef = (FieldDef)cur_obj;
//          if (cur_role_field.get(fieldDef.getEntityDef().getEntityName() + "." + fieldDef.getField_name()) != null)
//            fieldDef.setFun_flag(((RoleField)cur_role_field.get(fieldDef.getEntityDef().getEntityName() + "." + fieldDef.getField_name())).getFun_flag());
//          else
//            fieldDef.setFun_flag(0);
//        }
//
//        if (cur_obj instanceof EntityDef)
//          refresh_nodes.add(tmpNode);
//      }
//
//      for (DefaultMutableTreeNode node : refresh_nodes)
//        checkFieldRightChanges(node);
//    }
//    else {
//      enumt = root.breadthFirstEnumeration();
//      while (enumt.hasMoreElements()) {
//        tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//        cur_obj = tmpNode.getUserObject();
//        if (cur_obj instanceof FieldDef) {
//          fieldDef = (FieldDef)cur_obj;
//          fieldDef.setFun_flag(0);
//        } else if (cur_obj instanceof EntityDef) {
//          EntityDef entityDef = (EntityDef)cur_obj;
//          entityDef.setFun_flag(0);
//        } else if (cur_obj instanceof ModuleInfo) {
//          ModuleInfo moduleInfo = (ModuleInfo)cur_obj;
//          moduleInfo.setFun_flag(0);
//        } else if (cur_obj instanceof RoleRightTemp) {
//          RoleRightTemp roleRightTemp = (RoleRightTemp)cur_obj;
//          roleRightTemp.setFun_flag(0);
//        }
//      }
//    }
//  }
//
//  private void refreshEntityRight() {
//    List list = entityTable.getObjects();
//    for (Iterator i$ = list.iterator(); i$.hasNext(); ) { Object obj = i$.next();
//      RoleEntity re = (RoleEntity)obj;
//      RoleEntity re1 = (RoleEntity)cur_role_entity.get(re.getEntityDef().getEntity_key());
//      if (re1 != null) {
//        re.setAdd_flag(re1.isAdd_flag());
//        re.setDel_flag(re1.isDel_flag());
//        re.setEdit_flag(re1.isEdit_flag());
//        re.setRight_sql(re1.getRight_sql());
//        re.setView_flag(re1.isView_flag());
//      } else {
//        re.setAdd_flag(false);
//        re.setEdit_flag(false);
//        re.setDel_flag(false);
//        re.setView_flag(false);
//        re.setRight_sql(null);
//      }
//    }
//    entityTable.updateUI();
//  }
//
//  private void refreshCodeRight() {
//    List list = codeTable.getObjects();
//    for (Iterator i$ = list.iterator(); i$.hasNext(); ) { Object obj = i$.next();
//      RoleCode rc = (RoleCode)obj;
//      RoleCode rc1 = (RoleCode)cur_role_code.get(rc.getCode().getCode_key());
//      if (rc1 != null) {
//        rc.setAdd_flag(rc1.isAdd_flag());
//        rc.setDel_flag(rc1.isDel_flag());
//        rc.setEdit_flag(rc1.isEdit_flag());
//        rc.setRight_sql(rc1.getRight_sql());
//        String code_limit = "";
//        if ((rc1.getRight_sql() != null) && (!(rc1.getRight_sql().equals("")))) {
//          String[] limits = rc1.getRight_sql().split(";");
//          for (String limit : limits)
//            code_limit = code_limit + ";" + CodeManager.getCodeManager().getCodeNameBy(rc.getCode().getCode_type(), limit);
//
//          code_limit = code_limit.substring(1);
//        }
//        rc.setCode_limit(code_limit);
//      } else {
//        rc.setAdd_flag(false);
//        rc.setDel_flag(false);
//        rc.setEdit_flag(false);
//        rc.setRight_sql("");
//        rc.setCode_limit("");
//      }
//    }
//    codeTable.setObjects(list);
//  }
//
//  private void refreshReportRight()
//  {
//    DefaultMutableTreeNode no;
//    Object tmpObj;
//    ReportDef tt;
//    List refresh_class = new ArrayList();
//    List refresh_indexs = new ArrayList();
//    List refresh_report = new ArrayList();
//    List refresh_class_node = new ArrayList();
//    DefaultMutableTreeNode root = (DefaultMutableTreeNode)reportTree.getModel().getRoot();
//    java.util.Enumeration enumt = root.breadthFirstEnumeration();
//    if (cur_role_report.size() > 0) {
//      while (enumt.hasMoreElements()) {
//        no = (DefaultMutableTreeNode)enumt.nextElement();
//        tmpObj = no.getUserObject();
//        if (tmpObj instanceof ReportDef) {
//          tt = (ReportDef)no.getUserObject();
//          if (cur_role_report.get(tt.getReportDef_key()) != null)
//            tt.setFun_flag(((RoleReport)cur_role_report.get(tt.getReportDef_key())).getFun_flag());
//          else
//            tt.setFun_flag(0);
//
//          refresh_report.add(tt);
//          refresh_class_node.add(no);
//        }
//      }
//      for (int i = 0; i < refresh_report.size(); ++i)
//        if (refresh_class.indexOf(((ReportDef)refresh_report.get(i)).getReport_class()) == -1)
//          refresh_indexs.add(Integer.valueOf(i));
//
//
//      for (Integer index : refresh_indexs)
//        checkReportRightChanges((DefaultMutableTreeNode)refresh_class_node.get(index.intValue()));
//    }
//    else
//    {
//      while (enumt.hasMoreElements()) {
//        no = (DefaultMutableTreeNode)enumt.nextElement();
//        tmpObj = no.getUserObject();
//        if (tmpObj instanceof ReportDef) {
//          tt = (ReportDef)no.getUserObject();
//          tt.setFun_flag(0);
//        } else if (tmpObj instanceof RoleRightTemp) {
//          RoleRightTemp roleRightTemp = (RoleRightTemp)no.getUserObject();
//          roleRightTemp.setFun_flag(0);
//        }
//      }
//    }
//  }
//
//  private void refreshRolePerson(Role role)
//  {
//    List list = CommUtil.fetchEntities("from A01PassWord apw join fetch apw.a01 bp join fetch bp.deptCode where apw.role_key='" + role.getRole_key() + "'");
//    beanTablePanel3.setObjects(list);
//  }
//
//  private void refreshCurRoleFuntion(Role role) {
//    cur_role_funtion.clear();
//    Role tmpRole = (Role)CommUtil.fetchEntityBy("from Role r left join fetch r.roleFuntions rf join fetch rf.funtionRight where r.role_key='" + role.getRole_key() + "'");
//    if (tmpRole != null)
//      for (RoleFuntion roleFuntion : tmpRole.getRoleFuntions())
//        cur_role_funtion.put(roleFuntion.getFuntionRight().getFuntionRight_key(), roleFuntion);
//  }
//
//  private void refreshCurRoleDept(Role role)
//  {
//    cur_role_dept.clear();
//    Role tmpRole = (Role)CommUtil.fetchEntityBy("from Role r left join fetch r.roleDepts rd join fetch rd.deptCode where r.role_key='" + role.getRole_key() + "'");
//    if (tmpRole != null)
//      for (RoleDept roleDept : tmpRole.getRoleDepts())
//        cur_role_dept.put(roleDept.getDeptCode().getDeptCode_key(), roleDept);
//  }
//
//  private void refreshCurRoleDept(A01PassWord role)
//  {
//    cur_role_dept.clear();
//    A01PassWord tmpRole = (A01PassWord)CommUtil.fetchEntityBy("from A01PassWord r left join fetch r.a01DeptRights rd join fetch rd.deptCode where r.a01PassWord_key='" + role.getA01PassWord_key() + "'");
//    if (tmpRole != null)
//      for (A01DeptRight roleDept : tmpRole.getA01DeptRights())
//        cur_role_dept.put(roleDept.getDeptCode().getDeptCode_key(), roleDept);
//  }
//
//  private void refreshCurRoleField(Role role)
//  {
//    cur_role_field.clear();
//    Role tmpRole = (Role)CommUtil.fetchEntityBy("from Role r left join fetch r.roleFields where r.role_key='" + role.getRole_key() + "'");
//    for (RoleField roleField : tmpRole.getRoleFields())
//      cur_role_field.put(roleField.getField_name(), roleField);
//  }
//
//  private void refreshCurRoleEntity(Role role)
//  {
//    cur_role_entity.clear();
//    Role tmpRole = (Role)CommUtil.fetchEntityBy("from Role r join fetch r.roleEntitys re join fetch re.entityDef ed join fetch ed.entityClass ec join fetch ec.moduleInfo where r.role_key = '" + role.getRole_key() + "'");
//    if (tmpRole != null)
//      for (RoleEntity roleEntity : tmpRole.getRoleEntitys())
//        cur_role_entity.put(roleEntity.getEntityDef().getEntity_key(), roleEntity);
//  }
//
//  private void refreshCurRoleCode(Role role)
//  {
//    cur_role_code.clear();
//    Role tmpRole = (Role)CommUtil.fetchEntityBy("from Role r join fetch r.roleCodes rc join fetch rc.code where r.role_key='" + role.getRole_key() + "'");
//    if (tmpRole != null)
//      for (RoleCode roleCode : tmpRole.getRoleCodes())
//        cur_role_code.put(roleCode.getCode().getCode_key(), roleCode);
//  }
//
//  private void refreshCurRoleReport(Role role)
//  {
//    cur_role_report.clear();
//    Role tmp_role = (Role)CommUtil.fetchEntityBy("from Role r left join fetch r.roleReports rr join fetch rr.reportDef where r.role_key='" + role.getRole_key() + "'");
//    if (tmp_role != null)
//      for (RoleReport roleReport : tmp_role.getRoleReports())
//        cur_role_report.put(roleReport.getReportDef().getReportDef_key(), roleReport);
//  }
//
//  private void giveFuntionRight(Role role, DefaultMutableTreeNode tmpFuntionNode, int fun_flag)
//  {
//    HashSet del_funtion = new HashSet();
//    HashSet save_funtion = new HashSet();
//    HashSet update_funtion = new HashSet();
//    cur_string = new StringBuffer();
//    java.util.Enumeration enumt = tmpFuntionNode.breadthFirstEnumeration();
//
//    while (enumt.hasMoreElements()) {
//      RoleFuntion tmpRoleFuntion;
//      DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//      FuntionRight tmpNodeRight = (FuntionRight)tmpNode.getUserObject();
//      tmpNodeRight.setFun_flag(fun_flag);
//      if (cur_role_funtion.get(tmpNodeRight.getFuntionRight_key()) == null) {
//        if (fun_flag == 1) {
//          tmpRoleFuntion = (RoleFuntion)UtilTool.createEntityAndAssignUID(RoleFuntion.class);
//          tmpRoleFuntion.setRole(role);
//          tmpRoleFuntion.setFuntionRight(tmpNodeRight);
//          tmpRoleFuntion.setFun_flag(Integer.valueOf(fun_flag));
//          save_funtion.add(tmpRoleFuntion);
//        }
//      } else {
//        tmpRoleFuntion = (RoleFuntion)cur_role_funtion.get(tmpNodeRight.getFuntionRight_key());
//        if (tmpRoleFuntion.getFun_flag().intValue() != fun_flag) {
//          tmpRoleFuntion.setFun_flag(Integer.valueOf(fun_flag));
//          if (fun_flag == 1) {
//            update_funtion.add(tmpRoleFuntion);
//          } else if (fun_flag == 0) {
//            del_funtion.add(tmpRoleFuntion);
//            cur_string.append("'" + tmpRoleFuntion.getFuntionRight().getFuntionRight_key() + "',");
//          }
//        }
//      }
//    }
//
//    while (tmpFuntionNode != funtionTree.getModel().getRoot()) {
//      RoleFuntion tmpRoleFuntion;
//      DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)tmpFuntionNode.getParent();
//      FuntionRight tmpParent_right = (FuntionRight)tmpParent_node.getUserObject();
//      tmpParent_right.setFun_flag(fun_flag);
//      java.util.Enumeration enum1 = tmpParent_node.children();
//      boolean exist_flag = false;
//      while (enum1.hasMoreElements()) {
//        DefaultMutableTreeNode enum1Node = (DefaultMutableTreeNode)enum1.nextElement();
//        FuntionRight enum1FuntionRight = (FuntionRight)enum1Node.getUserObject();
//        if (enum1FuntionRight.getFun_flag() != fun_flag) {
//          tmpParent_right.setFun_flag(2);
//          exist_flag = true;
//          break;
//        }
//      }
//      if (cur_role_funtion.get(tmpParent_right.getFuntionRight_key()) == null) {
//        if (fun_flag == 1) {
//          tmpRoleFuntion = (RoleFuntion)UtilTool.createEntityAndAssignUID(RoleFuntion.class);
//          tmpRoleFuntion.setRole(role);
//          tmpRoleFuntion.setFuntionRight(tmpParent_right);
//          tmpRoleFuntion.setFun_flag(Integer.valueOf(tmpParent_right.getFun_flag()));
//          save_funtion.add(tmpRoleFuntion);
//        }
//      } else {
//        tmpRoleFuntion = (RoleFuntion)cur_role_funtion.get(tmpParent_right.getFuntionRight_key());
//        if (tmpRoleFuntion.getFun_flag().intValue() != tmpParent_right.getFun_flag()) {
//          tmpRoleFuntion.setFun_flag(Integer.valueOf(tmpParent_right.getFun_flag()));
//          if (fun_flag == 1) {
//            update_funtion.add(tmpRoleFuntion);
//          } else if ((fun_flag == 0) &&
//            (!(exist_flag))) {
//            del_funtion.add(tmpRoleFuntion);
//            cur_string.append("'" + tmpRoleFuntion.getFuntionRight().getFuntionRight_key() + "',");
//          }
//        }
//      }
//
//      tmpFuntionNode = tmpParent_node;
//    }
//    cur_string.append("'-1'");
//    if (save_funtion.size() > 0)
//      CommUtil.saveSet(save_funtion);
//
//    if (update_funtion.size() > 0)
//      CommUtil.updateSet(update_funtion);
//
//    if (del_funtion.size() > 0)
//      CommUtil.deleteSet(del_funtion);
//
//    refreshCurRoleFuntion(role);
//    funtionTree.updateUI();
//  }
//
//  private void giveDeptRight(Role role, DefaultMutableTreeNode tmpDeptNode, int fun_flag) {
//    String tp_sql;
//    List dept_code_list;
//    DefaultMutableTreeNode enum1Node;
//    DeptCode enum1Dept;
//    DeptCode tmpDept = (DeptCode)tmpDeptNode.getUserObject();
//    if (tmpDept.getFun_flag() == fun_flag) {
//      return;
//    }
//
//    if ((fun_flag == 1) && (!(role.getParent_code().equals("ROOT")))) {
//      tp_sql = "";
//      if (db_type.equals("sqlserver"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.dept_code like d2.dept_code + '%' and d2.deptCode_key in (select deptCode_key from roleDept where role_key ='" + cur_parent_role.getRole_key() + "') order by d1.grade,d1.dept_code";
//      else if (db_type.equals("oracle"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.dept_code like d2.dept_code || '%' and d2.deptCode_key in (select deptCode_key from roleDept where role_key ='" + cur_parent_role.getRole_key() + "') order by d1.grade,d1.dept_code";
//      else if (db_type.equals("db2"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where locate(d2.dept_code, d1.dept_code) > 0 and d2.deptCode_key in (select deptCode_key from roleDept where role_key ='" + cur_parent_role.getRole_key() + "') order by d1.grade,d1.dept_code";
//
//      CommUtil.excuteSQL("delete from roleDept where role_key ='" + role.getRole_key() + "' and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')");
//      dept_code_list = CommUtil.selectSQL(tp_sql);
//      if (dept_code_list.contains(tmpDept.getDept_code())) {
//        RoleDept p_save_role_dept = (RoleDept)UtilTool.createEntityAndAssignUID(RoleDept.class);
//        p_save_role_dept.setRole(role);
//        p_save_role_dept.setFun_flag(1);
//        DefaultMutableTreeNode p_node = null;
//        if (((DeptCode)tmpDeptNode.getUserObject()).getParent_code().toUpperCase().equals("ROOT")) {
//          System.out.println("rrrrrrrrrrrr");
//          p_node = tmpDeptNode;
//        } else {
//          p_node = (DefaultMutableTreeNode)tmpDeptNode.getParent();
//        }
//        DeptCode p_dept = (DeptCode)p_node.getUserObject();
//        if (dept_code_list.contains(p_dept.getDept_code())) {
//          DeptCode s_dept = null;
//          while (dept_code_list.contains(((DeptCode)tmpDeptNode.getUserObject()).getDept_code())) {
//            DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)tmpDeptNode.getParent();
//            if (tmpParent_node.getUserObject() instanceof String)
//              break;
//
//            DeptCode tmpParent_dept = (DeptCode)tmpParent_node.getUserObject();
//            tmpParent_dept.setFun_flag(1);
//            java.util.Enumeration enum1 = tmpParent_node.children();
//            while (enum1.hasMoreElements()) {
//              enum1Node = (DefaultMutableTreeNode)enum1.nextElement();
//              enum1Dept = (DeptCode)enum1Node.getUserObject();
//              if ((enum1Dept.getFun_flag() != 1) && (enum1Dept != tmpDept)) {
//                tmpParent_dept.setFun_flag(2);
//                break;
//              }
//            }
//            if (tmpParent_dept.getFun_flag() == 1)
//              s_dept = tmpParent_dept;
//
//            tmpDeptNode = tmpParent_node;
//          }
//          if (s_dept == null) {
//            p_save_role_dept.setDeptCode(tmpDept);
//          } else {
//            p_save_role_dept.setDeptCode(s_dept);
//            CommUtil.excuteSQL("delete from roleDept where role_key ='" + role.getRole_key() + "' and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + s_dept.getDept_code() + "%'); ");
//          }
//        } else {
//          p_save_role_dept.setDeptCode(tmpDept);
//        }
//        CommUtil.saveEntity(p_save_role_dept);
//      } else {
//        String sql_su = "";
//        if (db_type.equals("sqlserver"))
//          sql_su = sql_su + "insert into roleDept(role_dept_key, fun_flag, role_key, deptCode_key) select newid(),1,'" + role.getRole_key() + "',rd.deptCode_key from roleDept rd,deptCode d where rd.role_key ='" + cur_parent_role.getRole_key() + "' and rd.deptCode_key = d.deptCode_key and d.deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%');";
//        else if (db_type.equals("oracle"))
//          sql_su = sql_su + "insert into roleDept(role_dept_key, fun_flag, role_key, deptCode_key) select sys_guid(),1,'" + role.getRole_key() + "',rd.deptCode_key from roleDept rd,deptCode d where rd.role_key ='" + cur_parent_role.getRole_key() + "' and rd.deptCode_key = d.deptCode_key and d.deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%');";
//        else if (db_type.equals("db2")) {
//          sql_su = sql_su + "insert into roleDept(role_dept_key, fun_flag, role_key, deptCode_key) select sys_guid(),1,'" + role.getRole_key() + "',rd.deptCode_key from roleDept rd,deptCode d where rd.role_key ='" + cur_parent_role.getRole_key() + "' and rd.deptCode_key = d.deptCode_key and d.deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%');";
//        }
//
//        ValidateSQLResult result = CommUtil.excuteSQLs(sql_su, ";");
//        if (result.getResult() == 0){}else{
//
//        FormulaTextDialog.showErrorMsg(result.getMsg());
//      }
//      }
//
//      refreshCurRoleDept(role);
//      refreshDeptRight();
//    } else if ((fun_flag == 0) && (cur_node.getChildCount() > 0)) {
//      tp_sql = "";
//      if (db_type.equals("sqlserver"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.deptCode_key != d2.deptCode_key and d1.dept_code like d2.dept_code + '%' and d2.deptCode_key in (select deptCode_key from roleDept where role_key ='" + role.getRole_key() + "')";
//      else if (db_type.equals("oracle"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.deptCode_key != d2.deptCode_key and d1.dept_code like d2.dept_code || '%' and d2.deptCode_key in (select deptCode_key from roleDept where role_key ='" + role.getRole_key() + "')";
//      else if (db_type.equals("db2"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.deptCode_key != d2.deptCode_key and locate(d2.dept_code, d1.dept_code) > 0 and d2.deptCode_key in (select deptCode_key from roleDept where role_key ='" + role.getRole_key() + "')";
//
//      dept_code_list = CommUtil.selectSQL(tp_sql);
//      List p_deptcode_key = CommUtil.selectSQL("select d1.dept_code from deptcode d1 where d1.deptCode_key in (select deptCode_key from roleDept where role_key ='" + role.getRole_key() + "')");
//      String tmp_dept_code = "";
//      int start = 0;
//      List codes = new ArrayList();
//      if (dept_code_list.contains(tmpDept.getDept_code())) {
//        for (int i = tmpDept.getGrade().intValue(); i > 0; --i) {
//          int st = getDeptCodeLength(i);
//          String tmp_dept_code_str = tmpDept.getDept_code().substring(0, st);
//          codes.add(tmp_dept_code_str);
//          System.out.println("code:::" + tmp_dept_code_str);
//          if (p_deptcode_key.contains(tmp_dept_code_str)) {
//            tmp_dept_code = tmp_dept_code_str;
//            start = i;
//            System.out.println("i:::" + i);
//            break;
//          }
//        }
//        String tmp_str = "";
//        int a = 0;
//        StringBuffer str_s = new StringBuffer();
//        for (int i = tmpDept.getGrade().intValue(); i > start; --i) {
//          System.out.println(i);
//          System.out.println("code::" + ((String)codes.get(a)));
//          str_s.append("'" + ((String)codes.get(a)) + "',");
//          if (db_type.equals("sqlserver"))
//            tmp_str = tmp_str + "insert into RoleDept(role_dept_key, fun_flag, role_key, deptCode_key)select r.role_key + '_' + d.deptCode_key, 1, r.role_key, d.deptCode_key from (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code = '" + ((String)codes.get(a)) + "' and a.parent_code = b.parent_code and a.dept_code <>  '" + ((String)codes.get(a)) + "') d,(select role_key from RoleDept where deptCode_key in (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code like '" + tmp_dept_code + "%' and a.dept_code = b.parent_code)and role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code + '%')) r;";
//          else if (db_type.equals("oracle"))
//            tmp_str = tmp_str + "insert into RoleDept(role_dept_key, fun_flag, role_key, deptCode_key)select r.role_key || '_' || d.deptCode_key, 1, r.role_key, d.deptCode_key from (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code = '" + ((String)codes.get(a)) + "' and a.parent_code = b.parent_code and a.dept_code <>  '" + ((String)codes.get(a)) + "') d,(select role_key from RoleDept where deptCode_key in (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code like '" + tmp_dept_code + "%' and a.dept_code = b.parent_code)and role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%')) r;";
//          else if (db_type.equals("db2")) {
//            tmp_str = tmp_str + "insert into RoleDept(role_dept_key, fun_flag, role_key, deptCode_key)select r.role_key || '_' || d.deptCode_key, 1, r.role_key, d.deptCode_key from (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code = '" + ((String)codes.get(a)) + "' and a.parent_code = b.parent_code and a.dept_code <>  '" + ((String)codes.get(a)) + "') d,(select role_key from RoleDept where deptCode_key in (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code like '" + tmp_dept_code + "%' and a.dept_code = b.parent_code)and role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and locate(b.role_code,a.role_code) > 0)) r;";
//          }
//
//          ++a;
//        }
//        str_s.append("'" + tmp_dept_code + "'");
//        if (db_type.equals("sqlserver"))
//          tmp_str = tmp_str + "delete from roleDept where role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code + '%')and deptCode_key in (select deptCode_key from deptCode where dept_code in (" + str_s + "));";
//        else if (db_type.equals("oracle"))
//          tmp_str = tmp_str + "delete from roleDept where role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%')and deptCode_key in (select deptCode_key from deptCode where dept_code in (" + str_s + "));";
//        else if (db_type.equals("db2")) {
//          tmp_str = tmp_str + "delete from roleDept where role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and locate(b.role_code, a.role_code) > 0) and deptCode_key in (select deptCode_key from deptCode where dept_code in (" + str_s + "));";
//        }
//
//        System.out.println(tmp_str);
//        ValidateSQLResult result = CommUtil.excuteSQLs(tmp_str, ";");
//        if (result.getResult() == 0)
//
//        FormulaTextDialog.showErrorMsg(result.getMsg());
//      }
//      else
//      {
//        String tmp_sql = "";
//        if (db_type.equals("sqlserver"))
//          tmp_sql = "delete from roleDept where role_key in(select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code + '%') and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')";
//        else if (db_type.equals("oracle"))
//          tmp_sql = "delete from roleDept where role_key in(select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%') and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')";
//        else if (db_type.equals("db2"))
//          tmp_sql = "delete from roleDept where role_key in(select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and locate(b.role_code, a.role_code) > 0) and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')";
//
//        CommUtil.excuteSQL(tmp_sql);
//      }
//      refreshCurRoleDept(role);
//      refreshDeptRight();
//    } else {
//      HashSet del_role_depts = new HashSet();
//      HashSet save_role_depts = new HashSet();
//      RoleDept save_role_dept = (RoleDept)UtilTool.createEntityAndAssignUID(RoleDept.class);
//      if (fun_flag == 1) {
//        save_role_dept.setRole(role);
//        save_role_dept.setDeptCode(tmpDept);
//        save_role_dept.setFun_flag(1);
//        save_role_depts.add(save_role_dept);
//      }
//      java.util.Enumeration enumt = tmpDeptNode.breadthFirstEnumeration();
//
//      while (enumt.hasMoreElements()) {
//        DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//        DeptCode tmpNodeDept = (DeptCode)tmpNode.getUserObject();
//        tmpNodeDept.setFun_flag(fun_flag);
//        if (cur_role_dept.get(tmpNodeDept.getDeptCode_key()) != null) {
//          RoleDept tmpRoleDept = (RoleDept)cur_role_dept.get(tmpNodeDept.getDeptCode_key());
//          del_role_depts.add(tmpRoleDept);
//          cur_role_dept.remove(tmpNodeDept.getDeptCode_key());
//        }
//      }
//
//      while (tmpDeptNode != deptPanel.getDeptTree().getModel().getRoot()) {
//        DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)tmpDeptNode.getParent();
//        if (tmpParent_node.getUserObject() instanceof String)
//          break;
//
//        DeptCode tmpParent_dept = (DeptCode)tmpParent_node.getUserObject();
//        tmpParent_dept.setFun_flag(fun_flag);
//        java.util.Enumeration enum1 = tmpParent_node.children();
//        while (enum1.hasMoreElements()) {
//          DefaultMutableTreeNode enum1Node2 = (DefaultMutableTreeNode)enum1.nextElement();
//          DeptCode enum1Dept2 = (DeptCode)enum1Node2.getUserObject();
//          if (enum1Dept2.getFun_flag() != fun_flag) {
//            tmpParent_dept.setFun_flag(2);
//            break;
//          }
//        }
//        int tmp_flag = tmpParent_dept.getFun_flag();
//        java.util.Enumeration enum2 = tmpParent_node.children();
//        if (tmp_flag == 1) {
//          if (fun_flag != 1) {return;}
//          save_role_dept.setDeptCode(tmpParent_dept);
//          while (true) { if (!(enum2.hasMoreElements())) return;
//            enum1Node = (DefaultMutableTreeNode)enum2.nextElement();
//            enum1Dept = (DeptCode)enum1Node.getUserObject();
//            if (cur_role_dept.get(enum1Dept.getDeptCode_key()) != null) {
//              del_role_depts.add(cur_role_dept.get(enum1Dept.getDeptCode_key()));
//              cur_role_dept.remove(enum1Dept.getDeptCode_key());
//            }
//          }
//        }
//
//        RoleDept roleDept = null;
//        boolean flag = false;
//        DefaultMutableTreeNode tmp_node = tmpParent_node;
//        if (fun_flag == 1){
//          if (cur_role_dept.get(tmpParent_dept.getDeptCode_key()) != null) {
//            roleDept = (RoleDept)cur_role_dept.get(tmpParent_dept.getDeptCode_key());
//            flag = true;
//          }
//        }
//        else {
//          while ((tmp_node != null) &&
//            (tmp_node.getUserObject() instanceof DeptCode)) {
//            DeptCode tmp_dept = (DeptCode)tmp_node.getUserObject();
//            RoleDept tmp_roleDept = (RoleDept)cur_role_dept.get(tmp_dept.getDeptCode_key());
//            if (tmp_roleDept != null) {
//              roleDept = tmp_roleDept;
//              flag = true;
//              break;
//            }
//            tmp_node = (DefaultMutableTreeNode)tmp_node.getParent();
//          }
//
//        }
//
//        if (flag)
//        {
//          del_role_depts.add(roleDept);
//          cur_role_dept.remove(tmpParent_dept.getDeptCode_key());
//          while (enum2.hasMoreElements()) {
//            DefaultMutableTreeNode enum1Node3 = (DefaultMutableTreeNode)enum2.nextElement();
//            DeptCode enum1Dept3 = (DeptCode)enum1Node3.getUserObject();
//            if (enum1Dept3.getFun_flag() == 1) {
//              RoleDept save_dept = (RoleDept)UtilTool.createEntityAndAssignUID(RoleDept.class);
//              save_dept.setRole(role);
//              save_dept.setDeptCode(enum1Dept3);
//              save_dept.setFun_flag(1);
//              save_role_depts.add(save_dept);
//            }
//          }
//
//        }
//
//        tmpDeptNode = tmpParent_node;
//      }
//      if (del_role_depts.size() > 0)
//        CommUtil.deleteSet(del_role_depts);
//
//      CommUtil.saveSet(save_role_depts);
//      refreshCurRoleDept(role);
//    }
//
//    deptPanel.getDeptTree().updateUI();
//  }
//
//  private void giveDeptRight3(A01PassWord role, DefaultMutableTreeNode tmpDeptNode, int fun_flag) {
//    String tp_sql;
//    List dept_code_list;
//    DefaultMutableTreeNode enum1Node;
//    DeptCode enum1Dept;
//    A01PassWord parent_apw = UserContext.getUserA01PassWord();
//    DeptCode tmpDept = (DeptCode)tmpDeptNode.getUserObject();
//    if (tmpDept.getFun_flag() == fun_flag) {
//      return;
//    }
//
//    if ((fun_flag == 1) && (!(UserContext.getPerson_code().equals(UserContext.sysManName)))) {
//      tp_sql = "";
//      if (db_type.equals("sqlserver"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.dept_code like d2.dept_code + '%' and d2.deptCode_key in (select deptCode_key from A01DeptRight where a01PassWord_key ='" + parent_apw.getA01PassWord_key() + "') order by d1.grade,d1.dept_code";
//      else if (db_type.equals("oracle"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.dept_code like d2.dept_code || '%' and d2.deptCode_key in (select deptCode_key from roleDept where a01PassWord_key ='" + parent_apw.getA01PassWord_key() + "') order by d1.grade,d1.dept_code";
//      else if (db_type.equals("db2"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where locate(d2.dept_code, d1.dept_code) > 0 and d2.deptCode_key in (select deptCode_key from roleDept where a01PassWord_key ='" + parent_apw.getA01PassWord_key() + "') order by d1.grade,d1.dept_code";
//
//      CommUtil.excuteSQL("delete from A01DeptRight where a01PassWord_key ='" + role.getA01PassWord_key() + "' and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')");
//      dept_code_list = CommUtil.selectSQL(tp_sql);
//      if (dept_code_list.contains(tmpDept.getDept_code())) {
//        A01DeptRight p_save_role_dept = (A01DeptRight)UtilTool.createEntityAndAssignUID(A01DeptRight.class);
//        p_save_role_dept.setA01PassWord(role);
//        p_save_role_dept.setFun_flag(1);
//        DefaultMutableTreeNode p_node = null;
//        if (((DeptCode)tmpDeptNode.getUserObject()).getParent_code().toUpperCase().equals("ROOT")) {
//          System.out.println("rrrrrrrrrrrr");
//          p_node = tmpDeptNode;
//        } else {
//          p_node = (DefaultMutableTreeNode)tmpDeptNode.getParent();
//        }
//        DeptCode p_dept = (DeptCode)p_node.getUserObject();
//        if (dept_code_list.contains(p_dept.getDept_code())) {
//          DeptCode s_dept = null;
//          while (dept_code_list.contains(((DeptCode)tmpDeptNode.getUserObject()).getDept_code())) {
//            DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)tmpDeptNode.getParent();
//            if (tmpParent_node.getUserObject() instanceof String)
//              break;
//
//            DeptCode tmpParent_dept = (DeptCode)tmpParent_node.getUserObject();
//            tmpParent_dept.setFun_flag(1);
//            java.util.Enumeration enum1 = tmpParent_node.children();
//            while (enum1.hasMoreElements()) {
//              enum1Node = (DefaultMutableTreeNode)enum1.nextElement();
//              enum1Dept = (DeptCode)enum1Node.getUserObject();
//              if ((enum1Dept.getFun_flag() != 1) && (enum1Dept != tmpDept)) {
//                tmpParent_dept.setFun_flag(2);
//                break;
//              }
//            }
//            if (tmpParent_dept.getFun_flag() == 1)
//              s_dept = tmpParent_dept;
//
//            tmpDeptNode = tmpParent_node;
//          }
//          if (s_dept == null) {
//            p_save_role_dept.setDeptCode(tmpDept);
//          } else {
//            p_save_role_dept.setDeptCode(s_dept);
//            CommUtil.excuteSQL("delete from A01DeptRight where a01PassWord_key ='" + role.getA01PassWord_key() + "' and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + s_dept.getDept_code() + "%'); ");
//          }
//        } else {
//          p_save_role_dept.setDeptCode(tmpDept);
//        }
//        CommUtil.saveEntity(p_save_role_dept);
//      } else {
//        String sql_su = "";
//        if (db_type.equals("sqlserver"))
//          sql_su = sql_su + "insert into A01DeptRight(a01DeptRight_key, fun_flag, a01PassWord_key, deptCode_key) select newid(),1,'" + role.getA01PassWord_key() + "',rd.deptCode_key from roleDept rd,deptCode d where rd.a01PassWord_key ='" + parent_apw.getA01PassWord_key() + "' and rd.deptCode_key = d.deptCode_key and d.deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%');";
//        else if (db_type.equals("oracle"))
//          sql_su = sql_su + "insert into A01DeptRight(a01DeptRight_key, fun_flag, a01PassWord_key, deptCode_key) select sys_guid(),1,'" + role.getA01PassWord_key() + "',rd.deptCode_key from roleDept rd,deptCode d where rd.a01PassWord_key ='" + parent_apw.getA01PassWord_key() + "' and rd.deptCode_key = d.deptCode_key and d.deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%');";
//        else if (db_type.equals("db2")) {
//          sql_su = sql_su + "insert into A01DeptRight(a01DeptRight_key, fun_flag, a01PassWord_key, deptCode_key) select sys_guid(),1,'" + role.getA01PassWord_key() + "',rd.deptCode_key from roleDept rd,deptCode d where rd.a01PassWord_key ='" + parent_apw.getA01PassWord_key() + "' and rd.deptCode_key = d.deptCode_key and d.deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%');";
//        }
//
//        ValidateSQLResult result = CommUtil.excuteSQLs(sql_su, ";");
//        if (result.getResult() == 0){}else{
//
//        FormulaTextDialog.showErrorMsg(result.getMsg());
//      }
//      }
//
//      refreshCurRoleDept(role);
//      refreshDeptRight();
//    } else if ((fun_flag == 0) && (cur_node.getChildCount() > 0)) {
//      tp_sql = "";
//      if (db_type.equals("sqlserver"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.deptCode_key != d2.deptCode_key and d1.dept_code like d2.dept_code + '%' and d2.deptCode_key in (select deptCode_key from A01DeptRight where a01PassWord_key ='" + role.getA01PassWord_key() + "')";
//      else if (db_type.equals("oracle"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.deptCode_key != d2.deptCode_key and d1.dept_code like d2.dept_code || '%' and d2.deptCode_key in (select deptCode_key from A01DeptRight where a01PassWord_key ='" + role.getA01PassWord_key() + "')";
//      else if (db_type.equals("db2"))
//        tp_sql = "select d1.dept_code from deptcode d1,deptcode d2 where d1.deptCode_key != d2.deptCode_key and locate(d2.dept_code, d1.dept_code) > 0 and d2.deptCode_key in (select deptCode_key from A01DeptRight where a01PassWord_key ='" + role.getA01PassWord_key() + "')";
//
//      dept_code_list = CommUtil.selectSQL(tp_sql);
//      List p_deptcode_key = CommUtil.selectSQL("select d1.dept_code from deptcode d1 where d1.deptCode_key in (select deptCode_key from A01DeptRight where a01PassWord_key ='" + role.getA01PassWord_key() + "')");
//      String tmp_dept_code = "";
//      int start = 0;
//      List codes = new ArrayList();
//      if (dept_code_list.contains(tmpDept.getDept_code())) {
//        for (int i = tmpDept.getGrade().intValue(); i > 0; --i) {
//          int st = getDeptCodeLength(i);
//          String tmp_dept_code_str = tmpDept.getDept_code().substring(0, st);
//          codes.add(tmp_dept_code_str);
//          System.out.println("code:::" + tmp_dept_code_str);
//          if (p_deptcode_key.contains(tmp_dept_code_str)) {
//            tmp_dept_code = tmp_dept_code_str;
//            start = i;
//            System.out.println("i:::" + i);
//            break;
//          }
//        }
//        String tmp_str = "";
//        int a = 0;
//        StringBuffer str_s = new StringBuffer();
//        for (int i = tmpDept.getGrade().intValue(); i > start; --i) {
//          System.out.println(i);
//          System.out.println("code::" + ((String)codes.get(a)));
//          str_s.append("'" + ((String)codes.get(a)) + "',");
//          if (db_type.equals("sqlserver"))
//            tmp_str = tmp_str + "insert into A01DeptRight(a01DeptRight_key, fun_flag, a01PassWord_key, deptCode_key)" +
//                    "select r.a01PassWord_key + '_' + d.deptCode_key, 1, r.a01PassWord_key, d.deptCode_key from " +
//                    "(select a.deptCode_key from DeptCode a, DeptCode b " +
//                    "where b.dept_code = '" + ((String)codes.get(a)) + "' and a.parent_code = b.parent_code and a.dept_code <>  '" + ((String)codes.get(a)) + "') d," +
//                    "(select a01PassWord_key from A01DeptRight where deptCode_key in " +
//                    "(select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code like '" + tmp_dept_code + "%' and a.dept_code = b.parent_code)" +
//                    "and a01PassWord_key in (select a.a01PassWord_key from A01PassWord a,Role a1,Role b1, A01PassWord b where a.role_key = a1.role_key and b.role_key = b1.role_key and b.a01PassWord_key ='" + role.getA01PassWord_key() + "' and a1.role_code like b1.role_code + '%')) r;";
//          else if (db_type.equals("oracle"))
//            tmp_str = tmp_str + "insert into A01DeptRight(a01DeptRight_key, fun_flag, role_key, deptCode_key)select r.role_key || '_' || d.deptCode_key, 1, r.role_key, d.deptCode_key from (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code = '" + ((String)codes.get(a)) + "' and a.parent_code = b.parent_code and a.dept_code <>  '" + ((String)codes.get(a)) + "') d,(select role_key from RoleDept where deptCode_key in (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code like '" + tmp_dept_code + "%' and a.dept_code = b.parent_code)and role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%')) r;";
//          else if (db_type.equals("db2")) {
//            tmp_str = tmp_str + "insert into A01DeptRight(a01DeptRight_key, fun_flag, role_key, deptCode_key)select r.role_key || '_' || d.deptCode_key, 1, r.role_key, d.deptCode_key from (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code = '" + ((String)codes.get(a)) + "' and a.parent_code = b.parent_code and a.dept_code <>  '" + ((String)codes.get(a)) + "') d,(select role_key from RoleDept where deptCode_key in (select a.deptCode_key from DeptCode a, DeptCode b where b.dept_code like '" + tmp_dept_code + "%' and a.dept_code = b.parent_code)and role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and locate(b.role_code,a.role_code) > 0)) r;";
//          }
//
//          ++a;
//        }
//        str_s.append("'" + tmp_dept_code + "'");
//        if (db_type.equals("sqlserver"))
//          tmp_str = tmp_str + "delete from A01DeptRight where a01PassWord_key in (select a.a01PassWord_key from A01PassWord a,Role a1,Role b1, A01PassWord b where a.role_key = a1.role_key and b.role_key = b1.role_key and b.a01PassWord_key ='" + role.getA01PassWord_key() + "' and a1.role_code like b1.role_code + '%')and deptCode_key in (select deptCode_key from deptCode where dept_code in (" + str_s + "));";
//        else if (db_type.equals("oracle"))
//          tmp_str = tmp_str + "delete from roleDept where role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%')and deptCode_key in (select deptCode_key from deptCode where dept_code in (" + str_s + "));";
//        else if (db_type.equals("db2")) {
//          tmp_str = tmp_str + "delete from roleDept where role_key in (select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and locate(b.role_code, a.role_code) > 0) and deptCode_key in (select deptCode_key from deptCode where dept_code in (" + str_s + "));";
//        }
//
//        System.out.println(tmp_str);
//        ValidateSQLResult result = CommUtil.excuteSQLs(tmp_str, ";");
//        if (result.getResult() == 0)
//
//        FormulaTextDialog.showErrorMsg(result.getMsg());
//      }
//      else
//      {
//        String tmp_sql = "";
//        if (db_type.equals("sqlserver"))
//          tmp_sql = "delete from A01DeptRight where a01PassWord_key in(select a.a01PassWord_key from A01PassWord a,Role a1,Role b1, A01PassWord b where a.role_key = a1.role_key and b.role_key = b1.role_key and b.a01PassWord_key ='" + role.getA01PassWord_key() + "' and a1.role_code like b1.role_code + '%') and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')";
//        else if (db_type.equals("oracle"))
//          tmp_sql = "delete from roleDept where role_key in(select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%') and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')";
//        else if (db_type.equals("db2"))
//          tmp_sql = "delete from roleDept where role_key in(select a.role_key from Role a, Role b where b.role_key ='" + role.getRole_key() + "' and locate(b.role_code, a.role_code) > 0) and deptCode_key in (select deptCode_key from deptCode where dept_code like '" + tmpDept.getDept_code() + "%')";
//
//        CommUtil.excuteSQL(tmp_sql);
//      }
//      refreshCurRoleDept(role);
//      refreshDeptRight();
//    } else {
//      HashSet del_role_depts = new HashSet();
//      HashSet save_role_depts = new HashSet();
//      A01DeptRight save_role_dept = (A01DeptRight)UtilTool.createEntityAndAssignUID(A01DeptRight.class);
//      if (fun_flag == 1) {
//        save_role_dept.setA01PassWord(role);
//        save_role_dept.setDeptCode(tmpDept);
//        save_role_dept.setFun_flag(1);
//        save_role_depts.add(save_role_dept);
//      }
//      java.util.Enumeration enumt = tmpDeptNode.breadthFirstEnumeration();
//
//      while (enumt.hasMoreElements()) {
//        DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//        DeptCode tmpNodeDept = (DeptCode)tmpNode.getUserObject();
//        tmpNodeDept.setFun_flag(fun_flag);
//        if (cur_role_dept.get(tmpNodeDept.getDeptCode_key()) != null) {
//          A01DeptRight tmpRoleDept = (A01DeptRight)cur_role_dept.get(tmpNodeDept.getDeptCode_key());
//          del_role_depts.add(tmpRoleDept);
//          cur_role_dept.remove(tmpNodeDept.getDeptCode_key());
//        }
//      }
//
//      while (tmpDeptNode != deptPanel.getDeptTree().getModel().getRoot()) {
//        DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)tmpDeptNode.getParent();
//        if (tmpParent_node.getUserObject() instanceof String)
//          break;
//
//        DeptCode tmpParent_dept = (DeptCode)tmpParent_node.getUserObject();
//        tmpParent_dept.setFun_flag(fun_flag);
//        java.util.Enumeration enum1 = tmpParent_node.children();
//        while (enum1.hasMoreElements()) {
//          DefaultMutableTreeNode enum1Node2 = (DefaultMutableTreeNode)enum1.nextElement();
//          DeptCode enum1Dept2 = (DeptCode)enum1Node2.getUserObject();
//          if (enum1Dept2.getFun_flag() != fun_flag) {
//            tmpParent_dept.setFun_flag(2);
//            break;
//          }
//        }
//        int tmp_flag = tmpParent_dept.getFun_flag();
//        java.util.Enumeration enum2 = tmpParent_node.children();
//        if (tmp_flag == 1) {
//          if (fun_flag != 1) {return;}
//          save_role_dept.setDeptCode(tmpParent_dept);
//          while (true) { if (!(enum2.hasMoreElements())) return;
//            enum1Node = (DefaultMutableTreeNode)enum2.nextElement();
//            enum1Dept = (DeptCode)enum1Node.getUserObject();
//            if (cur_role_dept.get(enum1Dept.getDeptCode_key()) != null) {
//              del_role_depts.add(cur_role_dept.get(enum1Dept.getDeptCode_key()));
//              cur_role_dept.remove(enum1Dept.getDeptCode_key());
//            }
//          }
//        }
//
//        A01DeptRight roleDept = null;
//        boolean flag = false;
//        DefaultMutableTreeNode tmp_node = tmpParent_node;
//        if (fun_flag == 1)
//          if (cur_role_dept.get(tmpParent_dept.getDeptCode_key()) != null) {
//            roleDept = (A01DeptRight)cur_role_dept.get(tmpParent_dept.getDeptCode_key());
//            flag = true;
//          }
//        else {
//          while ((tmp_node != null) &&
//            (tmp_node.getUserObject() instanceof DeptCode)) {
//            DeptCode tmp_dept = (DeptCode)tmp_node.getUserObject();
//            A01DeptRight tmp_roleDept = (A01DeptRight)cur_role_dept.get(tmp_dept.getDeptCode_key());
//            if (tmp_roleDept != null) {
//              roleDept = tmp_roleDept;
//              flag = true;
//              break;
//            }
//            tmp_node = (DefaultMutableTreeNode)tmp_node.getParent();
//          }
//
//        }
//
//        if (flag)
//        {
//          del_role_depts.add(roleDept);
//          cur_role_dept.remove(tmpParent_dept.getDeptCode_key());
//          while (enum2.hasMoreElements()) {
//            DefaultMutableTreeNode enum1Node3 = (DefaultMutableTreeNode)enum2.nextElement();
//            DeptCode enum1Dept3 = (DeptCode)enum1Node3.getUserObject();
//            if (enum1Dept3.getFun_flag() == 1) {
//              A01DeptRight save_dept = (A01DeptRight)UtilTool.createEntityAndAssignUID(A01DeptRight.class);
//              save_dept.setA01PassWord(role);
//              save_dept.setDeptCode(enum1Dept3);
//              save_dept.setFun_flag(1);
//              save_role_depts.add(save_dept);
//            }
//          }
//
//        }
//
//        tmpDeptNode = tmpParent_node;
//      }
//      if (del_role_depts.size() > 0)
//        CommUtil.deleteSet(del_role_depts);
//
//      CommUtil.saveSet(save_role_depts);
//      refreshCurRoleDept(role);
//    }
//
//    deptPanel.getDeptTree().updateUI();
//  }
//
//  private void giveFieldRight(Role role, DefaultMutableTreeNode tmpFieldNode, int fun_flag) {
//    field_right_changes.clear();
//    cur_string = new StringBuffer();
//    update_cur_string = new StringBuffer();
//    getRihgtField();
//    Object cur_obj = tmpFieldNode.getUserObject();
//    if (cur_obj instanceof FieldDef) {
//      FieldDef cur_field = (FieldDef)cur_obj;
//      if (cur_field.getFun_flag() == fun_flag)
//        return;
//
//      cur_field.setFun_flag(fun_flag);
//      field_right_changes.add(cur_field);
//    } else if (cur_obj instanceof EntityDef) {
//      EntityDef cur_entity_def = (EntityDef)cur_obj;
//      cur_entity_def.setFun_flag(fun_flag);
//      Set fieldDefs = cur_entity_def.getFieldDefs();
//      java.util.Enumeration tmp_enuer = tmpFieldNode.children();
//      while (tmp_enuer.hasMoreElements()) {
//        DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode)tmp_enuer.nextElement();
//        if (tmp_node.getUserObject() instanceof FieldDef) {
//          FieldDef tmp_def = (FieldDef)tmp_node.getUserObject();
//          field_right_changes.add(tmp_def);
//        }
//      }
//    } else if (cur_obj instanceof ModuleInfo) {
//      ModuleInfo cur_module = (ModuleInfo)cur_obj;
//      checkRightModule(cur_module, fun_flag, tmpFieldNode);
//    } else if (cur_obj instanceof RoleRightTemp) {
//      RoleRightTemp tmp = (RoleRightTemp)cur_obj;
//      tmp.setFun_flag(fun_flag);
//      java.util.Enumeration enumt = tmpFieldNode.children();
//      while (enumt.hasMoreElements()) {
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumt.nextElement();
//        ModuleInfo mi = (ModuleInfo)node.getUserObject();
//        checkRightModule(mi, fun_flag, node);
//      }
//    }
//    checkFieldRightChanges(tmpFieldNode);
//    HashSet field_right_change = new HashSet();
//    HashSet update_field_right_change = new HashSet();
//    for (FieldDef field_change : field_right_changes)
//      if (cur_role_field.get(field_change.getEntityDef().getEntityName() + "." + field_change.getField_name()) == null) {
//        RoleField role_field_new = (RoleField)UtilTool.createEntityAndAssignUID(RoleField.class);
//        role_field_new.setRole(role);
//        if (fieldDef_set.contains(field_change.getEntityDef().getEntityName() + "." + field_change.getField_name()))
//          role_field_new.setFun_flag(2);
//        else
//          role_field_new.setFun_flag(fun_flag);
//
//        role_field_new.setField_name(field_change.getEntityDef().getEntityName() + "." + field_change.getField_name());
//        field_right_change.add(role_field_new);
//      } else {
//        RoleField role_field_change = (RoleField)cur_role_field.get(field_change.getEntityDef().getEntityName() + "." + field_change.getField_name());
//        if (fieldDef_set.contains(field_change.getEntityDef().getEntityName() + "." + field_change.getField_name()))
//          role_field_change.setFun_flag(2);
//        else
//          role_field_change.setFun_flag(fun_flag);
//
//        update_field_right_change.add(role_field_change);
//        if (fun_flag == 2)
//          update_cur_string.append("'" + role_field_change.getField_name() + "',");
//
//        cur_string.append("'" + role_field_change.getField_name() + "',");
//      }
//
//
//    if (fun_flag == 0) {
//      if (update_field_right_change.size() > 0) {
//        cur_string.append("'-1'");
//        CommUtil.deleteSet(update_field_right_change);
//      }
//    } else {
//      if (field_right_change.size() > 0)
//        CommUtil.saveSet(field_right_change);
//
//      if (update_field_right_change.size() > 0) {
//        CommUtil.updateSet(update_field_right_change);
//        if ((fun_flag == 2) && (cur_node.getChildCount() > 0)) {
//          update_cur_string.append("'-1'");
//          String tmp_sql = "";
//          if (db_type.equals("sqlserver"))
//            tmp_sql = "update roleField set fun_flag = 2 where role_key in (select a.role_key from Role a, Role b where a.role_key != b.role_key and b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code + '%') and field_name in (" + update_cur_string + ")";
//          else if (db_type.equals("oracle"))
//            tmp_sql = "update roleField set fun_flag = 2 where role_key in (select a.role_key from Role a, Role b where a.role_key != b.role_key and b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%') and field_name in (" + update_cur_string + ")";
//          else if (db_type.equals("db2"))
//            tmp_sql = "update roleField set fun_flag = 2 where role_key in (select a.role_key from Role a, Role b where a.role_key != b.role_key and b.role_key ='" + role.getRole_key() + "' and locate(b.role_code,a.role_code) > 0) and field_name in (" + update_cur_string + ")";
//
//          CommUtil.excuteSQL(tmp_sql);
//        }
//
//      }
//
//    }
//
//    refreshCurRoleField(role);
//    refreshFieldRight();
//    fieldTree.updateUI();
//  }
//
//  private void checkRightModule(ModuleInfo cur_module, int fun_flag, DefaultMutableTreeNode treeNode) {
//    if ((cur_module.getFun_flag() == fun_flag) && (fun_flag != 2))
//      return;
//
//    System.out.println("sdsd");
//    cur_module.setFun_flag(fun_flag);
//
//    Set module_fields = new HashSet();
//
//    java.util.Enumeration tmp_enuer = treeNode.depthFirstEnumeration();
//    while (tmp_enuer.hasMoreElements()) {
//      DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode)tmp_enuer.nextElement();
//      if (tmp_node.getUserObject() instanceof FieldDef) {
//        FieldDef tmp_def = (FieldDef)tmp_node.getUserObject();
//        module_fields.add(tmp_def);
//      }
//    }
//    field_right_changes.addAll(getFieldChange(module_fields, fun_flag));
//    System.out.println("size:::" + field_right_changes.size());
//  }
//
//  private List<FieldDef> getFieldChange(Set<FieldDef> module_fields, int fun_flag) {
//    List list = new ArrayList();
//    for (FieldDef module_field : module_fields)
//      if (module_field.getFun_flag() != fun_flag) {
//        module_field.setFun_flag(fun_flag);
//        list.add(module_field);
//      }
//
//    return list;
//  }
//
//  private void giveReportRight(Role role, DefaultMutableTreeNode tmpReportNode, int fun_flag) {
//    Object cur_obj = tmpReportNode.getUserObject();
//    getRihgtReport();
//    cur_string = null;
//    if (cur_obj instanceof ReportDef) {
//      ReportDef cur_report = (ReportDef)cur_obj;
//      if (cur_report.getFun_flag() == fun_flag)
//        return;
//    }
//    else if (cur_obj instanceof RoleRightTemp) {
//      RoleRightTemp cur_roleRightTemp = (RoleRightTemp)cur_obj;
//      if (cur_roleRightTemp.getFun_flag() == fun_flag)
//        return;
//    }
//
//    cur_string = new StringBuffer();
//    update_cur_string = new StringBuffer();
//    HashSet save_role_report = new HashSet();
//    HashSet update_role_report = new HashSet();
//    if (cur_obj instanceof ReportDef) {
//      ReportDef cur_report = (ReportDef)cur_obj;
//      cur_report.setFun_flag(fun_flag);
//      if (cur_role_report.get(cur_report.getReportDef_key()) == null) {
//        RoleReport unselect_report = (RoleReport)UtilTool.createEntityAndAssignUID(RoleReport.class);
//        unselect_report.setRole(role);
//        unselect_report.setReportDef(cur_report);
//        if (report_set.contains(cur_report))
//          unselect_report.setFun_flag(2);
//        else
//          unselect_report.setFun_flag(fun_flag);
//
//        save_role_report.add(unselect_report);
//      } else {
//        RoleReport select_report = (RoleReport)cur_role_report.get(cur_report.getReportDef_key());
//        if (report_set.contains(cur_report))
//          select_report.setFun_flag(2);
//        else
//          select_report.setFun_flag(fun_flag);
//
//        update_role_report.add(select_report);
//        if (fun_flag == 2)
//          update_cur_string.append("'" + cur_report.getReportDef_key() + "',");
//
//        cur_string.append("'" + cur_report.getReportDef_key() + "',");
//      }
//    } else if (cur_obj instanceof RoleRightTemp) {
//      RoleRightTemp cur_right = (RoleRightTemp)cur_obj;
//      cur_right.setFun_flag(fun_flag);
//      Set reportDefs = new HashSet();
//      java.util.Enumeration enumt = tmpReportNode.breadthFirstEnumeration();
//      boolean flag_fun = false;
//      while (enumt.hasMoreElements()) {
//        Object child_obj = ((DefaultMutableTreeNode)enumt.nextElement()).getUserObject();
//        if (child_obj instanceof ReportDef) {
//          ReportDef child_report = (ReportDef)child_obj;
//          if (child_report.getFun_flag() != fun_flag) {
//            child_report.setFun_flag(fun_flag);
//            if (cur_role_report.get(child_report.getReportDef_key()) == null) {
//              RoleReport unselect_report = (RoleReport)UtilTool.createEntityAndAssignUID(RoleReport.class);
//              unselect_report.setRole(role);
//              unselect_report.setReportDef(child_report);
//              if (report_set.contains(child_report)) {
//                flag_fun = true;
//                unselect_report.setFun_flag(2);
//              } else {
//                unselect_report.setFun_flag(fun_flag);
//              }
//              save_role_report.add(unselect_report);
//            } else {
//              RoleReport select_report = (RoleReport)cur_role_report.get(child_report.getReportDef_key());
//              if (report_set.contains(child_report)) {
//                select_report.setFun_flag(2);
//                flag_fun = true;
//              } else {
//                select_report.setFun_flag(fun_flag);
//              }
//              update_role_report.add(select_report);
//              if (fun_flag == 2)
//                update_cur_string.append("'" + child_report.getReportDef_key() + "',");
//
//              cur_string.append("'" + child_report.getReportDef_key() + "',");
//            }
//            reportDefs.add(child_report);
//          }
//        } else if (child_obj instanceof RoleRightTemp) {
//          RoleRightTemp child_right_temp = (RoleRightTemp)child_obj;
//          if (flag_fun)
//            child_right_temp.setFun_flag(2);
//          else
//            child_right_temp.setFun_flag(fun_flag);
//        }
//      }
//
//    }
//
//    cur_string.append("'-1'");
//    checkReportRightChanges(tmpReportNode);
//    if (fun_flag == 0) {
//      if (update_role_report.size() > 0)
//        CommUtil.deleteSet(update_role_report);
//    }
//    else {
//      if (save_role_report.size() > 0)
//        CommUtil.saveSet(save_role_report);
//
//      if (update_role_report.size() > 0) {
//        CommUtil.updateSet(update_role_report);
//        if ((fun_flag == 2) && (cur_node.getChildCount() > 0)) {
//          update_cur_string.append("'-1'");
//          String tmp_sql = "";
//          if (db_type.equals("sqlserver"))
//            tmp_sql = "update roleReport set fun_flag = 2 where role_key in (select a.role_key from Role a, Role b where a.role_key != b.role_key and b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code + '%') and reportDef_key in (" + update_cur_string + ")";
//          else if (db_type.equals("oracle"))
//            tmp_sql = "update roleReport set fun_flag = 2 where role_key in (select a.role_key from Role a, Role b where a.role_key != b.role_key and b.role_key ='" + role.getRole_key() + "' and a.role_code like b.role_code || '%') and reportDef_key in (" + update_cur_string + ")";
//          else if (db_type.equals("db2"))
//            tmp_sql = "update roleReport set fun_flag = 2 where role_key in (select a.role_key from Role a, Role b where a.role_key != b.role_key and b.role_key ='" + role.getRole_key() + "' and locate(b.role_code,a.role_code) > 0) and reportDef_key in (" + update_cur_string + ")";
//
//          CommUtil.excuteSQL(tmp_sql);
//        }
//      }
//    }
//    refreshCurRoleReport(role);
//    refreshReportRight();
//    reportTree.updateUI();
//  }
//
//  private void checkReportRightChanges(DefaultMutableTreeNode node) {
//    while (node != reportTree.getModel().getRoot()) {
//      DefaultMutableTreeNode tmpParent_node = (DefaultMutableTreeNode)node.getParent();
//      java.util.Enumeration enumt = tmpParent_node.children();
//      Object parent_obj = tmpParent_node.getUserObject();
//      int cur_fun_flag = 1;
//      List child_flags = new ArrayList();
//      while (enumt.hasMoreElements()) {
//        DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//        Object tmp_obj = tmpNode.getUserObject();
//        if (tmp_obj instanceof ReportDef) {
//          ReportDef tmp_report_def = (ReportDef)tmp_obj;
//          child_flags.add(Integer.valueOf(tmp_report_def.getFun_flag()));
//        } else if (tmp_obj instanceof RoleRightTemp) {
//          RoleRightTemp tmp_entity_def = (RoleRightTemp)tmp_obj;
//          child_flags.add(Integer.valueOf(tmp_entity_def.getFun_flag()));
//        }
//      }
//      cur_fun_flag = getFunFlag(child_flags);
//      if (parent_obj instanceof RoleRightTemp) {
//        RoleRightTemp roleRightTemp = (RoleRightTemp)parent_obj;
//        roleRightTemp.setFun_flag(cur_fun_flag);
//      }
//
//      node = tmpParent_node;
//    }
//  }
//
//  private void checkFieldRightChanges(DefaultMutableTreeNode node) {
//    while (node != fieldTree.getModel().getRoot()) {
//      Object parent_obj = node.getUserObject();
//      child_flags = getChildFlag(node);
//      cur_fun_flag = getFunFlag(child_flags);
//      if (parent_obj instanceof EntityDef)
//        ((EntityDef)parent_obj).setFun_flag(cur_fun_flag);
//      else if (parent_obj instanceof ModuleInfo)
//        ((ModuleInfo)parent_obj).setFun_flag(cur_fun_flag);
//
//      node = (DefaultMutableTreeNode)node.getParent();
//    }
//    RoleRightTemp parent_right = (RoleRightTemp)node.getUserObject();
//    List child_flags = getChildFlag(node);
//    int cur_fun_flag = getFunFlag(child_flags);
//    parent_right.setFun_flag(cur_fun_flag);
//    fieldTree.updateUI();
//  }
//
//  private List<Integer> getChildFlag(DefaultMutableTreeNode node) {
//    java.util.Enumeration enumt = node.children();
//    List child_flags = new ArrayList();
//    while (enumt.hasMoreElements()) {
//      DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)enumt.nextElement();
//      Object tmp_obj = tmpNode.getUserObject();
//      if (tmp_obj instanceof FieldDef) {
//        FieldDef tmp_field_def = (FieldDef)tmp_obj;
//        child_flags.add(Integer.valueOf(tmp_field_def.getFun_flag()));
//      } else if (tmp_obj instanceof EntityDef) {
//        EntityDef tmp_entity_def = (EntityDef)tmp_obj;
//        child_flags.add(Integer.valueOf(tmp_entity_def.getFun_flag()));
//      } else if (tmp_obj instanceof EntityClass) {
//        EntityClass tmp_entity_class = (EntityClass)tmp_obj;
//        child_flags.add(Integer.valueOf(tmp_entity_class.getFun_flag()));
//      } else if (tmp_obj instanceof ModuleInfo) {
//        ModuleInfo tmp_module_info = (ModuleInfo)tmp_obj;
//        child_flags.add(Integer.valueOf(tmp_module_info.getFun_flag()));
//      }
//    }
//    return child_flags;
//  }
//
//  private int getFunFlag(List<Integer> child_flags) {
//    int cur_fun_flag = 0;
//    if ((child_flags.contains(Integer.valueOf(0))) || (child_flags.contains(Integer.valueOf(2))))
//      cur_fun_flag = 2;
//
//    if (child_flags.contains(Integer.valueOf(0)))
//      if ((child_flags.contains(Integer.valueOf(1))) || (child_flags.contains(Integer.valueOf(2))))
//        cur_fun_flag = 2;
//      else
//        cur_fun_flag = 0;
//
//    else if (child_flags.contains(Integer.valueOf(2)))
//      cur_fun_flag = 2;
//    else
//      cur_fun_flag = 1;
//
//    if (child_flags.size() == 0)
//      cur_fun_flag = 0;
//
//    return cur_fun_flag;
//  }
//
//  private void addPerson() {
//    List tmpList = beanTablePanel1.getSelectObjects();
//    if (tmpList.size() == 0)
//      return;
//
//    List selectList = CommUtil.selectSQL("select distinct a01_key from A01PassWord");
//    HashSet newPersons = new HashSet();
//    Role role = (Role)cur_node.getUserObject();
//    Iterator i$ = tmpList.iterator();
//    while (true) { boolean isNew;
//      A01 bp;
//      while (true) { if (!(i$.hasNext())) break label142; Object obj = i$.next();
//        isNew = true;
//        bp = (A01)obj;
//        if (!(selectList.contains(bp.getA01_key())))
//        {
//          break;
//        }
//
//      }
//
//      if (isNew) {
//        A01PassWord apw = (A01PassWord)UtilTool.createEntityAndAssignUID(A01PassWord.class);
//        apw.setA01(bp);
//        apw.setRole_key(role.getRole_key());
//        newPersons.add(apw);
//      }
//    }
//    if (newPersons.size() > 0)
//         CommUtil.saveSet(newPersons);
//
//    refreshRolePerson(role);
//  }
//
//  private void removePerson() {
//    List tmpList = beanTablePanel3.getSelectObjects();
//    if (tmpList.size() == 0)
//      return;
//
//    int len = tmpList.size();
//    int mod_len = len / 100;
//    int rel_len = mod_len + ((len % 100 == 0) ? 0 : 1);
//    StringBuffer str = new StringBuffer();
//    for (int i = 0; i < rel_len; ++i) {
//      int j;
//      str.append("delete from A01PASSWORD WHERE a01password_key in(");
//      StringBuffer row_str = new StringBuffer();
//      row_str.append("'-1'");
//      if (i < mod_len)
//        for (j = 0; j < 100; ++j) {
//          Object obj = tmpList.get(i * 100 + j);
//          if (obj instanceof A01PassWord) {
//            row_str.append(",'");
//            row_str.append(((A01PassWord)obj).getA01PassWord_key());
//            row_str.append("'");
//          } else {
//            row_str.append(",'");
//            row_str.append(obj.toString());
//            row_str.append("'");
//          }
//        }
//      else
//        for (j = 0; j < 100; ++j) {
//          int col = i * 100 + j;
//          if (col >= len)
//            break;
//
//          Object obj = tmpList.get(col);
//          if (obj instanceof A01PassWord) {
//            row_str.append(",'");
//            row_str.append(((A01PassWord)obj).getA01PassWord_key());
//            row_str.append("'");
//          } else {
//            row_str.append(",'");
//            row_str.append(obj.toString());
//            row_str.append("'");
//          }
//        }
//
//      str.append(row_str.toString());
//      str.append(");");
//    }
//    ValidateSQLResult result = CommUtil.excuteSQLs(str.toString(), ";");
//    if (result.getResult() == 0) {
//      beanTablePanel3.deleteSelectedRows();
//      pnl1.updateUI();
//    } else {
//      JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(pnlCodeRight), "移除员工失败");
//      FormulaTextDialog.showErrorMsg(result.getMsg());
//    }
//  }
//
//  private void locateEmp(int col) {
//    String val = cbBoxSearch.getSelectedItem().toString().toUpperCase();
//    last_locate_position = SysUtil.locateEmp(col, val, last_search_val, last_locate_position, beanTablePanel1, default_locate_fields);
//    if (last_locate_position > -1) {
//      beanTablePanel1.setRowSelectionInterval(last_locate_position, last_locate_position);
//      beanTablePanel1.getVerticalScrollBar().setValue(last_locate_position * beanTablePanel1.getRowHeight());
//      boolean b_contain = false;
//      for (int i = 0; i < cbBoxSearch.getItemCount(); ++i)
//        if (cbBoxSearch.getItemAt(i).equals(val)) {
//          b_contain = true;
//          break;
//        }
//
//      if (!(b_contain)) {
//        cbBoxSearch.addItem(val);
//        if (cbBoxSearch.getItemCount() > 10)
//          cbBoxSearch.removeItemAt(0);
//      }
//
//      beanTablePanel1.updateUI();
//    }
//  }
//}