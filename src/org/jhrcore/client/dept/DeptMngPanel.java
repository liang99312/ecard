/*
 * EmpMngPanel2.java
 *
 * Created on 2008年9月9日, 下午1:32
 */
package org.jhrcore.client.dept;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import com.foundercy.pf.control.listener.IPickColumnSumListener;
import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickFieldSetListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.ITableCellEditable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTree;
import org.jhrcore.comm.BeanManager;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.mutil.ReportUtil;
import org.jhrcore.client.personnel.comm.EmpCardUtil;
import org.jhrcore.entity.BaseDeptAppendix;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.mutil.DeptUtil;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptPanel;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.sanalyse.SAnalysePanel;
import org.jhrcore.ui.sanalyse.SAnalyseScheme;
import org.jhrcore.ui.sanalyse.SAnalyseUtil;
import org.jhrcore.ui.listener.IPickBeanPanelEditListener;
import org.jhrcore.ui.listener.IPickDeptRefreshListener;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.TableListCellRender;

/**
 *
 * @author  yangzhou
 */
public class DeptMngPanel extends JPanel implements IModulePanel {

    private DeptPanel deptPanel;
    private JButton btnAdd = new JButton("新增");
    private JButton btnEdit = new JButton("编辑");
    private JButton btnDel = new JButton("删除");
    private JButton btnSave = new JButton("保存");
    private JButton btnView = new JButton("浏览");
    private JButton btnCancel = new JButton("取消");
    private JButton btnAnalyse = new JButton("分析");
    private JButton btnTool = new JButton("工具");
    private JButton btnReport = new JButton("常用报表");
    private JMenuItem mi_export = new JMenuItem("导出Excel");
    private JMenuItem btnSetGrade = new JMenuItem("设置级次");
    private JMenuItem mi_search = new JMenuItem("查询");
    private JMenuItem mi_setShowItems = new JMenuItem("设置显示字段");
    private JMenuItem mi_replace = new JMenuItem("替换");
    private JMenu btnEditWay = new JMenu("编辑方式");
    private JMenuItem btnDeptLog = new JMenuItem("部门日志");
    private JMenuItem cardEdit = new JMenuItem("卡片编辑");
    private JMenuItem tableEdit = new JMenuItem("网格编辑");
//    private JMenuItem miShowDept = new JMenuItem("显示删除部门");
    private JMenuItem miDeptCheck = new JMenuItem("部门调整");
    private JMenuItem miDeptMidSet = new JMenuItem("中间部门设置");
    private JMenu mChange = new JMenu("变更");
    private JMenuItem miUnit = new JMenuItem("合并");
    private JMenuItem miTransfer = new JMenuItem("转移");
    private JMenuItem miDel = new JMenuItem("撤销");
    private JMenuItem deptImport = new JMenuItem("导入部门");
    private JMenuItem miChangePxCode = new JMenuItem("部门排序调整");
    private JMenuItem miViewBackData = new JMenuItem("历史数据查询");
    private BeanPanel beanPanel1 = new BeanPanel();//附表中部门基本信息
    private BeanPanel beanPanel2 = new BeanPanel();//附表信息
    private BeanPanel beanPanel = new BeanPanel();//部门信息
    private DeptCode curren_dept;
    private FTable ftable;
    private FTable beanTablePanel1;
    private List appendixList = new ArrayList();
    private JListBinding bindPersonOtherTable;
    private Class<?> appendixClass = null;
    private boolean editState = false;
    private Object cur_obj;
    private BaseDeptAppendix baseDeptAppendix;
    private int tabIndex = 0;
    private boolean editWay = false;//0:网格编辑；1：卡片编辑
    private ListSelectionListener dept_listener;
    private ListSelectionListener appendix_listener;
    //部门排序
    private String dept_order_sql = "d.px_code";
    //附表排序
    private String appendix_order_sql = "a_id";
    //用于记录部门表允许显示的所有字段
    private List<TempFieldInfo> dept_infos;
    //用于记录当前附表允许显示的所有字段
    private List<TempFieldInfo> appendix_infos = new ArrayList<TempFieldInfo>();
    private Hashtable<Integer, Integer> tabKeys = new Hashtable<Integer, Integer>();
//    private boolean show_del_dept = false;
    private CheckTreeNode cur_node;
    private SysParameter deptGrade;
    private boolean appendix_init_flag = false;
    private String cur_sql;
    private String cur_sql_appdenx;
    private boolean changeState = false;
    private IPickColumnSumListener column_sum_listener;
    private IPickColumnSumListener column_sum_listener_appdenx;
    private ITableCellEditable iTableCellEditable;
    private HrLog log = new HrLog("DeptMng");
    //模块标识
    public static final String module_code = "DeptMng";
//    private String codeShowDelDept = "UI.DeptMng.show_del_dept";

    /**
     * 设置界面操作权限
     * */
    @Override
    public void setFunctionRight() {
        setEditState(editState);
    }

    /**
     * 设置界面按钮的可操作性，包含权限
     * */
    private void setPanelState(boolean editting) {
        if (tabIndex == 0) {
            ftable.setEditable(editting && !editWay);
            beanPanel.setEditable(editting && editWay);
            beanPanel.setBean(ftable.getCurrentRow(), ftable.getFields());
            beanPanel.setShow_scheme(ftable.getCur_show_scheme());
            beanPanel.bind();
            ftable.editingStopped();
        } else {
            if (tabbPnlDept.getSelectedIndex() == 0) {
                if (beanTablePanel1 != null) {
                    beanPanel2.setBean(beanTablePanel1.getCurrentRow(), beanTablePanel1.getFields());
                    beanPanel2.setEditable(editting && editWay);
                    beanPanel2.setShow_scheme(beanTablePanel1.getCur_show_scheme());
                    beanTablePanel1.setEditable(editting && !editWay);
                    beanPanel2.bind();
                }
            } else {
                beanPanel1.setBean(curren_dept);
                beanPanel1.bind();
            }
        }
        setAddEditState();
    }

    private void setEditState(boolean editting) {
        ComponentUtil.setSysTableFuntion(ftable, module_code);
        ComponentUtil.setSysTableFuntion(beanTablePanel1, module_code);
        editState = editting;
        ComponentUtil.setCompEnable(this, btnEdit, !editting);
        ComponentUtil.setCompEnable(this, btnDel, !editting);
        ComponentUtil.setCompEnable(this, btnView, editting);
        ComponentUtil.setCompEnable(this, btnCancel, editting);
        ComponentUtil.setCompEnable(this, btnSave, editting);
        setAddEditState();
    }

    private void setAddEditState() {
        ComponentUtil.setIcon(tableEdit, editWay ? "blank" : "editWay");
        ComponentUtil.setIcon(cardEdit, editWay ? "editWay" : "blank");
        boolean addable = false;
        boolean editable = false;
        if (tabIndex == 0) {
            addable = UserContext.hasEntityAddRight("DeptCode");
            editable = UserContext.hasEntityEditRight("DeptCode") && !editState;
        } else if (tabIndex == 1) {
            addable = appendixClass != null && UserContext.hasEntityAddRight(appendixClass.getSimpleName());
            editable = appendixClass != null && UserContext.hasEntityEditRight(appendixClass.getSimpleName()) && !editState;
        }
        ComponentUtil.setCompEnable(this, btnAdd, addable);
        ComponentUtil.setCompEnable(this, btnEdit, editable);
    }

    /** Creates new form EmpMngPanel2 */
    public DeptMngPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spMain = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        jtpLeft = new javax.swing.JTabbedPane();
        pnlDeptTree = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();
        tabPnlDept = new javax.swing.JTabbedPane();
        pnlDeptMainInfo = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlDeptTop = new javax.swing.JPanel();
        pnlDeptBottom = new javax.swing.JPanel();
        pnlPersonOthers = new javax.swing.JPanel();
        splitPnlOther = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        tabbPnlDept = new javax.swing.JTabbedPane();
        pnlCardOtherDept = new javax.swing.JPanel();
        pnlDept = new javax.swing.JPanel();
        pnlOtherDeptTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listAppendix = new javax.swing.JList();
        toolbar = new javax.swing.JToolBar();

        spMain.setDividerLocation(200);
        spMain.setOneTouchExpandable(true);

        pnlDeptTree.setLayout(new java.awt.BorderLayout());
        jtpLeft.addTab("部门", pnlDeptTree);

        javax.swing.GroupLayout pnlLeftLayout = new javax.swing.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
        );

        spMain.setLeftComponent(pnlLeft);

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);

        pnlDeptTop.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setTopComponent(pnlDeptTop);

        pnlDeptBottom.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlDeptBottom);

        javax.swing.GroupLayout pnlDeptMainInfoLayout = new javax.swing.GroupLayout(pnlDeptMainInfo);
        pnlDeptMainInfo.setLayout(pnlDeptMainInfoLayout);
        pnlDeptMainInfoLayout.setHorizontalGroup(
            pnlDeptMainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
        );
        pnlDeptMainInfoLayout.setVerticalGroup(
            pnlDeptMainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );

        tabPnlDept.addTab("\u90e8\u95e8\u57fa\u672c\u4fe1\u606f", pnlDeptMainInfo);

        splitPnlOther.setDividerLocation(34);
        splitPnlOther.setDividerSize(3);
        splitPnlOther.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(300);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        pnlCardOtherDept.setLayout(new java.awt.BorderLayout());
        tabbPnlDept.addTab("\u5361\u7247\u4fe1\u606f", pnlCardOtherDept);

        pnlDept.setLayout(new java.awt.BorderLayout());
        tabbPnlDept.addTab("部门基本信息", pnlDept);

        jSplitPane2.setBottomComponent(tabbPnlDept);

        pnlOtherDeptTable.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setLeftComponent(pnlOtherDeptTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
        );

        splitPnlOther.setRightComponent(jPanel1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(40, 40));

        jScrollPane1.setViewportView(listAppendix);

        splitPnlOther.setLeftComponent(jScrollPane1);

        javax.swing.GroupLayout pnlPersonOthersLayout = new javax.swing.GroupLayout(pnlPersonOthers);
        pnlPersonOthers.setLayout(pnlPersonOthersLayout);
        pnlPersonOthersLayout.setHorizontalGroup(
            pnlPersonOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPnlOther)
        );
        pnlPersonOthersLayout.setVerticalGroup(
            pnlPersonOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPnlOther, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );

        tabPnlDept.addTab("\u90e8\u95e8\u9644\u8868", pnlPersonOthers);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
            .addComponent(tabPnlDept)
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightLayout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPnlDept))
        );

        spMain.setRightComponent(pnlRight);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spMain, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spMain)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jtpLeft;
    private javax.swing.JList listAppendix;
    private javax.swing.JPanel pnlCardOtherDept;
    private javax.swing.JPanel pnlDept;
    private javax.swing.JPanel pnlDeptBottom;
    private javax.swing.JPanel pnlDeptMainInfo;
    private javax.swing.JPanel pnlDeptTop;
    private javax.swing.JPanel pnlDeptTree;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlOtherDeptTable;
    private javax.swing.JPanel pnlPersonOthers;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JSplitPane spMain;
    private javax.swing.JSplitPane splitPnlOther;
    private javax.swing.JTabbedPane tabPnlDept;
    private javax.swing.JTabbedPane tabbPnlDept;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        ComponentUtil.initTabKeys(tabPnlDept, tabKeys);
        deptGrade = DeptUtil.getDeptGrade();
        dept_infos = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
        initToolBar();
        ftable = new FTable(DeptCode.class, true, true, true, "DeptMng");
        ftable.setRight_allow_flag(true);
        iTableCellEditable = new ITableCellEditable() {

            @Override
            public int getCellEditable(Object obj, String fileName) {
                if (obj instanceof DeptCode) {
                    DeptCode tmp_dept = (DeptCode) obj;
                    return haveDeptRight(tmp_dept) ? 0 : -1;
                }
                return 0;
            }
        };
        ftable.setITableCellEditable(iTableCellEditable);
        ftable.setAll_fields(dept_infos, dept_infos, module_code);
        List<String> disFields = Arrays.asList(new String[]{"px_code", "parent_code", "end_flag", "del_flag"});
        ftable.setDisable_fields(disFields);
        beanPanel.setDisable_fields(disFields);
        dept_order_sql = SysUtil.getOrderString(ftable.getCurOrderScheme(), "d", dept_order_sql, dept_infos);
        deptPanel = new DeptPanel(UserContext.getDepts(false), 1);
        pnlDeptTree.add(deptPanel, BorderLayout.CENTER);
        pnlDeptTop.add(ftable, BorderLayout.CENTER);
        pnlDeptBottom.add(new JScrollPane(beanPanel));
        List entity_list = CommUtil.fetchEntities("from EntityDef ed where ed.entityClass.entityType_code='DEPT'");
        for (Object obj : entity_list) {
            EntityDef tmp_def = (EntityDef) obj;
            if (UserContext.hasEntityViewRight(tmp_def.getEntityName())) {
                appendixList.add(tmp_def);
            }
        }
        if (appendixList.isEmpty()) {
            tabPnlDept.remove(pnlPersonOthers);
        }
    }

    private void initAppendix() {
        bindPersonOtherTable = SwingBindings.createJListBinding(UpdateStrategy.READ, appendixList, listAppendix);
        bindPersonOtherTable.bind();
        pnlDept.add(new JScrollPane(beanPanel1), BorderLayout.CENTER);
        pnlCardOtherDept.add(new JScrollPane(beanPanel2), BorderLayout.CENTER);
        listAppendix.setCellRenderer(new TableListCellRender());
        listAppendix.setLayoutOrientation(JList.VERTICAL_WRAP);
        listAppendix.setVisibleRowCount(-1);
        listAppendix.setFixedCellHeight(25);
        int count = appendixList.size();
        if (count > 0) {
            listAppendix.setSelectedIndex(0);
        }
    }

    private void initToolBar() {
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnView);
        toolbar.add(btnSave);
        toolbar.add(btnCancel);
        toolbar.add(btnDel);
        toolbar.add(btnAnalyse);
        toolbar.addSeparator();
        toolbar.add(btnReport);
        toolbar.addSeparator();
        toolbar.add(btnTool);
        buildToolMenu();
    }

    private void setupEvents() {
        btnAnalyse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SAnalyseUtil.createAnalyse(btnAnalyse, "PayAnalyse", "简易统计分析");
//                SAnalysePanel pnl = new SAnalysePanel(getAnalyseScheme());
//                ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, DeptMngMsg.ttlDeptMiddle);
            }
        });
        miDeptMidSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DeptMiddleSetPanel pnlDms = new DeptMiddleSetPanel();
                ModelFrame.showModel(ContextManager.getMainFrame(), pnlDms, true, DeptMngMsg.ttlDeptMiddle);
            }
        });
        deptPanel.addPickDeptRefreshListener(new IPickDeptRefreshListener() {

            @Override
            public List<DeptCode> refreshDepts() {
                return UserContext.getDepts(false);
            }
        });
        miDeptCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                DeptCheckPanel dlPnl = new DeptCheckPanel();
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(ftable), dlPnl, true, DeptMngMsg.ttlDeptCheck, 800, 600);
            }
        });
        mi_search.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                getFTable().query();
            }
        });
        mi_export.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                getFTable().exportData();
            }
        });
        mi_setShowItems.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                getFTable().setShowFields();
            }
        });
        mi_replace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                getFTable().replaceData();
            }
        });
        deptImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex == 0) {
                    log.info(e);
                    ImportDeptDialog importDlg = new ImportDeptDialog(ContextManager.getMainFrame(), DeptCode.class);
                    ContextManager.locateOnMainScreenCenter(importDlg);
                    importDlg.setVisible(true);
                }
            }
        });
        dept_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ContextManager.setStatusBar(ftable.getObjects().size());
                if (BeanManager.isChanged(cur_obj)) {
                    saveObject(tabIndex, cur_obj);
                }
                if (cur_obj == ftable.getCurrentRow()) {
                    return;
                }
                cur_obj = ftable.getCurrentRow();
                setPanelState(editState);
            }
        };
        ftable.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                selectDeptNode(tabIndex, editWay, qs);
            }
        });
        ftable.addListSelectionListener(dept_listener);
        cardEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                editWay = true;
                setPanelState(editState);
            }
        });
        tableEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                editWay = false;
                setPanelState(editState);
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                addObject();
            }
        });
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                setEditState(true);
                setPanelState(editState);
            }
        });
        btnView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                cancelEdit(tabIndex, tabIndex, true);
                setEditState(false);
                setPanelState(editState);
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                cancelEdit(tabIndex, tabIndex, false);
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                saveObject(tabIndex, null);
                setEditState(editState);
                setPanelState(editState);
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                delObject();
            }
        });
        btnSetGrade.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                DeptGradeDlg dgDlg = new DeptGradeDlg();
                dgDlg.addPickChangeDeptGradeListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        UserContext.getMemoryDept(true);
                        deptPanel.rebuildTree();
                        refresh();
                    }
                });
                ContextManager.locateOnScreenCenter(dgDlg);
                dgDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dgDlg.setVisible(true);
            }
        });
        deptPanel.getDeptTree().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object path = e.getPath();
                if (path == null) {
                    return;
                }
                Object obj = ((TreePath) path).getLastPathComponent();
                if (obj == null) {
                    return;
                }
                ComponentUtil.initTabKeys(tabPnlDept, tabKeys);
                if (obj instanceof CheckTreeNode) {
                    cur_node = (CheckTreeNode) obj;
                    selectDeptNode(tabIndex, editState, null);
                }
            }
        });// 添加到数据中去,点选节点事件
        listAppendix.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                changeAppendix();
                setEditState(editState);
            }
        });
        tabPnlDept.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (changeState) {
                    return;
                }
                int old_index = tabIndex;
                tabIndex = tabPnlDept.getSelectedIndex();
                cancelEdit(old_index, tabIndex, true);
                Integer value = tabKeys.get(tabIndex);
                if (value == 1) {
                    selectDeptNode(tabIndex, editState, null);
                }
                refresh();
            }
        });
        btnDeptLog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                DeptLogPanel dlPnl = new DeptLogPanel();
                ModelFrame.showModel(ContextManager.getMainFrame(), dlPnl, true, DeptMngMsg.ttlDeptLog, 800, 600);
            }
        });
        column_sum_listener = new IPickColumnSumListener() {

            @Override
            public String pickSumSQL() {
                return cur_sql;
            }
        };
        ftable.addPickColumnSumListener(column_sum_listener);
        appendix_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                BeanManager.updateEntity(baseDeptAppendix, editState);
                if (baseDeptAppendix == beanTablePanel1.getCurrentRow()) {
                    return;
                }
                ContextManager.setStatusBar(beanTablePanel1.getObjects().size());
                baseDeptAppendix = (BaseDeptAppendix) beanTablePanel1.getCurrentRow();
                if (baseDeptAppendix == null) {
                    return;
                }
                beanPanel2.setBean(baseDeptAppendix);
                setEditState(editState);
                setPanelState(editState);
            }
        };
        ftable.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                dept_order_sql = SysUtil.getOrderString(showScheme, "d", dept_order_sql, dept_infos);
                selectDeptNode(tabIndex, editState, null);
            }
        });
        tabbPnlDept.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setPanelState(editState);
            }
        });
        miDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_node == null || !(cur_node.getUserObject() instanceof DeptCode)) {
                    return;
                }
                DeptCode dc = (DeptCode) cur_node.getUserObject();
                if (dc.getParent_code().toLowerCase().equals("root")) {
                    MsgUtil.showErrorMsg(DeptMngMsg.msg001);
                    return;
                }
                String str = "select 1 from A01jd b where b.old_deptCode_key in（select d.deptcode_key from DeptCode d where d.dept_code like'" + dc.getDept_code() + "%')";
                if (!CommUtil.selectSQL(str).isEmpty()) {
                    MsgUtil.showErrorMsg(DeptMngMsg.msg002);
                    return;
                }
                DeptDelPanel pnlDd = new DeptDelPanel(dc);
                pnlDd.addPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        UserContext.getMemoryDept(true);
                        deptPanel.rebuildTree();
                    }
                });
                ModelFrame.showModel(ContextManager.getMainFrame(), pnlDd, true, DeptMngMsg.ttlDeptDel, 800, 600);
            }
        });
        miUnit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeDept("unit");
            }
        });
        miTransfer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeDept("trans");
            }
        });
        btnReport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ReportUtil.buildCommReportMenu(btnReport, module_code, null);
            }
        });
        miChangePxCode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DeptPxCodePanel pnl = new DeptPxCodePanel();
                ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, DeptMngMsg.ttlDeptSort, 800, 600, false);
                mf.addIPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        UserContext.getMemoryDept(true);
                        deptPanel.rebuildTree();
                    }
                });
                mf.setVisible(true);
            }
        });
        cur_node = (CheckTreeNode) deptPanel.getDeptTree().getLastSelectedPathComponent();
        if (cur_node != null && cur_node.getUserObject() instanceof DeptCode) {
            curren_dept = (DeptCode) cur_node.getUserObject();
            ContextManager.setMainFrameTitle(curren_dept.getDept_full_name());
            ftable.addObject(curren_dept);
        }
        setEditState(editState);
    }

    private FTable getFTable() {
        if (tabIndex == 1) {
            return beanTablePanel1;
        }
        return ftable;
    }

    /**
     * 该方法用于根据类型参数来进行合并/转移部门操作
     * @param type：unit：合并；trans：转移
     */
    private void changeDept(String type) {
        if (cur_node == null || !(cur_node.getUserObject() instanceof DeptCode)) {
            return;
        }
        DeptCode dc = (DeptCode) cur_node.getUserObject();
        Object msg = null;
        if (dc.getParent_code().toLowerCase().equals("root")) {
            msg = DeptMngMsg.msg001;
        }
        Object title = DeptMngMsg.ttlDeptTran;
        if (type.equals("unit")) {
            if (!((DeptCode) cur_node.getUserObject()).isEnd_flag()) {
                msg = DeptMngMsg.msg004;
            }
            title = DeptMngMsg.ttlDeptUnit;
        }
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return;
        }
        DeptTransUnitPanel pnlDtu = new DeptTransUnitPanel(type, cur_node);
        pnlDtu.addPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                UserContext.getMemoryDept(true);
                deptPanel.rebuildTree();
            }
        });
        ModelFrame.showModel(ContextManager.getMainFrame(), pnlDtu, true, title, 700, 600);
    }

    /**
     * 该方法用于编辑部门，并更新到缓存
     * @param dept：当前编辑部门
     * @param edit_node：对应的部门树节点
     */
    private void editDept(DeptCode dept, CheckTreeNode edit_node) {
        if (dept == null) {
            return;
        }
        DeptCode old_dept = (DeptCode) edit_node.getUserObject();
        String old_code = old_dept.getDept_code();
        String old_content = old_dept.getContent();
        String new_code = dept.getDept_code();
        String new_content = dept.getContent();
        String edit_code = "";
        Object msg = null;
        if (new_code == null || "".equals(new_code.replace(" ", ""))) {
            msg = DeptMngMsg.msg005;
            dept.setDept_code(old_code);
        } else if (new_content == null || "".equals(new_content.replace(" ", ""))) {
            msg = DeptMngMsg.msg005;
            dept.setContent(old_content);
        } else if (!old_code.equals(new_code)) {
            if (old_code.length() != dept.getDept_code().length()) {
                msg = DeptMngMsg.msg007;
                dept.setDept_code(old_code);
            } else if (CommUtil.exists("select 1 from DeptCode where dept_code='" + dept.getDept_code() + "'")) {
                msg = DeptMngMsg.msg008;
                dept.setDept_code(old_code);
            }
            edit_code += "dept_code;";
        }
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return;
        }
        if (!old_content.equals(new_content)) {
            edit_code += "content;";
        }
        if (dept.getParent_code().equalsIgnoreCase("ROOT")) {
            dept.setDept_full_name(dept.getContent());
        } else {
            Object parent_obj = ((CheckTreeNode) edit_node.getParent()).getUserObject();
            dept.setDept_full_name(((DeptCode) parent_obj).getDept_full_name() + "\\" + dept.getContent());
        }
        //检查关联更新
        if (!EmpCardUtil.CheckObject(dept, false)) {
            return;
        }
        ValidateSQLResult result = DeptImpl.editDept(dept, edit_code);
        if (result.getResult() == 0) {
            List<String> fields = EntityBuilder.getCommFieldNameListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
            PublicUtil.copyProperties(dept, old_dept, fields, fields);
            for (DeptCode dc : UserContext.getMemoryDept(false)) {
                if (dc.equals(dept)) {
                    PublicUtil.copyProperties(dept, dc, fields, fields);
                    break;
                }
            }
            SysUtil.sortListByStr(UserContext.getMemoryDept(false), "px_code");
            MsgUtil.showHRSaveSuccessMsg(null);
        } else {
            Enumeration enumt = edit_node.breadthFirstEnumeration();
            int len = old_code.length();
            while (enumt.hasMoreElements()) {
                CheckTreeNode node = (CheckTreeNode) enumt.nextElement();
                if (node.getUserObject() instanceof DeptCode) {
                    DeptCode dc = (DeptCode) node.getUserObject();
                    dc.setDept_code(old_code);
                    dc.setDept_code(old_code + dc.getDept_code().substring(len));
                    dc.setParent_code(old_code + (dc.getParent_code().length() > len ? dc.getParent_code().substring(len) : ""));
                    CheckTreeNode parent = (CheckTreeNode) node.getParent();
                    Object parent_obj = parent.getUserObject();
                    if (parent_obj instanceof DeptCode) {
                        dc.setDept_full_name(((DeptCode) parent_obj).getDept_full_name() + "\\" + dc.getContent());
                    }
                }
            }
            MsgUtil.showHRSaveErrorMsg(result);
        }
        if (!(old_code.equals(new_code) && old_content.equals(new_content))) {
            if (!old_code.equals(new_code)) {
                UserContext.getMemoryDept(true);
                deptPanel.rebuildTree();
                deptPanel.locateDept(dept);
            }
            selectDeptNode(tabIndex, editState, null);
        }
        deptPanel.updateUIView();
    }

    /**
     * 该方法为界面数据刷新主函数
     * @param tabIndex：当前卡片索引
     * @param canModify：是否可编辑
     */
    protected void selectDeptNode(int tabIndex, boolean canModify, QueryScheme qs) {
        Object obj = deptPanel.getDeptTree().getLastSelectedPathComponent();
        if (obj == null || !(obj instanceof CheckTreeNode)) {
            return;
        }
        CheckTreeNode node = (CheckTreeNode) obj;
        Object data = node.getUserObject();
        if (data == null || data instanceof String) {
            return;
        }
        tabKeys.put(tabIndex, 0);
        if (data instanceof DeptCode) {
            curren_dept = (DeptCode) data;
            ContextManager.setMainFrameTitle(curren_dept.getDept_full_name());
        }
        if (tabIndex == 0) {
            ftable.setCur_query_scheme(qs);
            cur_sql = "from DeptCode d where d.dept_code like '" + curren_dept.getDept_code() + "%'";
            cur_sql += " and (" + UserContext.dept_right_str + ")";//部门权限
            if (qs != null) {
                cur_sql = qs.buildHql(cur_sql, "d");
            }
            cur_sql_appdenx = UserContext.getEntityRightSQL("DeptCode", cur_sql_appdenx, "DeptCode");
            PublicUtil.getProps_value().setProperty(DeptCode.class.getName(), "from DeptCode d where d.deptCode_key in");
            List<String> keys = (List<String>) CommUtil.fetchEntities("select d.deptCode_key " + cur_sql + " order by " + dept_order_sql);
            ftable.setObjects(keys);
        } else {
            if (!appendix_init_flag) {
                appendix_init_flag = true;
                initAppendix();
                return;
            }
            if (appendixClass == null) {
                changeAppendix();
                return;
            }
            beanTablePanel1.setCur_query_scheme(qs);
            String tableName = appendixClass.getSimpleName();
            cur_sql_appdenx = "from " + tableName + ",DeptCode where " + tableName + ".deptCode_key=DeptCode.deptCode_key and DeptCode.dept_code like '" + curren_dept.getDept_code() + "%' ";
            cur_sql_appdenx += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
            if (qs != null) {
                cur_sql_appdenx += " and " + tableName + ".baseDeptAppendix_key in(" + qs.buildSql() + ")";
            }
            cur_sql_appdenx = UserContext.getEntityRightSQL("DeptCode", cur_sql_appdenx, "DeptCode");
            cur_sql_appdenx = UserContext.getEntityRightSQL(tableName, cur_sql_appdenx, tableName);
            beanTablePanel1.removeListSelectionListener(appendix_listener);
            PublicUtil.getProps_value().setProperty(appendixClass.getName(), "from " + tableName + " a join fetch a.deptCode where a.baseDeptAppendix_key in");
            beanTablePanel1.setObjects(CommUtil.selectSQL("select " + tableName + ".baseDeptAppendix_key " + cur_sql_appdenx + "order by " + appendix_order_sql));
            beanTablePanel1.addListSelectionListener(appendix_listener);
            appendix_listener.valueChanged(null);
        }
        setPanelState(editState);
    }

    /**
     * 该方法为切换部门附表时的操作
     * */
    private void changeAppendix() {
        if (listAppendix.getSelectedValue() == null) {
            return;
        }
        appendix_order_sql = "a_id";
        EntityDef ed = (EntityDef) listAppendix.getSelectedValue();
        if (appendixClass != null && appendixClass.getSimpleName().equals(ed.getEntityName())) {
            return;
        }
        try {
            appendixClass = Class.forName("org.jhrcore.entity." + ed.getEntityName());
            beanTablePanel1 = new FTable(appendixClass, true, true, true, module_code) {

                @Override
                public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                    if (fileName.contains(".")) {
                        return new Color(238, 238, 238);
                    }
                    return null;
                }
            };
            beanTablePanel1.setRight_allow_flag(true);
            appendix_infos.clear();
            List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();//设置默认显示字段
            EntityBuilder.buildInfo(DeptCode.class, appendix_infos, default_fields, "deptCode");
            EntityBuilder.buildInfo(appendixClass, appendix_infos, default_fields);
            beanTablePanel1.setAll_fields(appendix_infos, default_fields, module_code);
            appendix_order_sql = SysUtil.getSQLOrderString(beanTablePanel1.getCurOrderScheme(), appendix_order_sql, appendix_infos);
            beanTablePanel1.addListSelectionListener(appendix_listener);
            beanTablePanel1.addPickFieldOrderListener(new IPickFieldOrderListener() {

                @Override
                public void pickOrder(ShowScheme showScheme) {
                    appendix_order_sql = SysUtil.getSQLOrderString(beanTablePanel1.getCurOrderScheme(), appendix_order_sql, appendix_infos);
                    selectDeptNode(tabIndex, editState, null);
                }
            });
            beanTablePanel1.addPickQueryExListener(new IPickQueryExListener() {

                @Override
                public void pickQuery(QueryScheme qs) {
                    selectDeptNode(tabIndex, editWay, qs);
                }
            });
            column_sum_listener_appdenx = new IPickColumnSumListener() {

                @Override
                public String pickSumSQL() {
                    log.info("人员附表操作：表格汇总");
                    return cur_sql_appdenx;
                }
            };
            beanTablePanel1.addPickColumnSumListener(column_sum_listener_appdenx);
            beanTablePanel1.addPickFieldSetListener(new IPickFieldSetListener() {

                @Override
                public void pickField(ShowScheme showScheme) {
                    log.info("进入人员附表操作：表格设置显示字段");
                    beanPanel2.setShow_scheme(showScheme);
                    beanPanel2.setFields(beanTablePanel1.getFields());
                    beanPanel2.bind();
                    beanPanel2.updateUI();
                }
            });
            beanTablePanel1.addQueryEntity(DeptCode.class, "DeptCode", "2");
            pnlOtherDeptTable.removeAll();
            pnlOtherDeptTable.add(beanTablePanel1, BorderLayout.CENTER);
            pnlOtherDeptTable.updateUI();
            selectDeptNode(tabIndex, editState, null);
            beanPanel2.setFields(beanTablePanel1.getFields());
        } catch (ClassNotFoundException e) {
            log.error(e);
        }

    }

    /**
     * 该方法为新增部门或部门附表
     * */
    private void addObject() {
        boolean flag = true;
        if (tabIndex == 0) {
            if (!UserContext.isSA) {
                if (cur_node != null && cur_node.getUserObject() instanceof DeptCode) {
                    DeptCode tmp_dept = (DeptCode) cur_node.getUserObject();
                    if (!haveDeptRight(tmp_dept)) {
                        MsgUtil.showErrorMsg(CommMsg.NOPOWER_MESSAGE);
                        return;
                    }
                    CheckTreeNode p_node = (CheckTreeNode) cur_node.getParent();
                    if (p_node != null && p_node.getUserObject() instanceof DeptCode) {
                        flag = haveDeptRight((DeptCode) p_node.getUserObject());
                    }
                    if (!flag && tmp_dept.isDel_flag()) {
                        MsgUtil.showInfoMsg(DeptMngMsg.msgDeptCannotDel);
                        return;
                    }
                }
            }
            CheckTreeNode parent = null;
            if (cur_node == null) {
                parent = (CheckTreeNode) deptPanel.getModuleTreeModel().getRoot();
            } else {
                parent = cur_node;
            }
            final CheckTreeNode parent_node = parent;
            final int level = parent.getLevel();
            String[] deptGradeValue = deptGrade.getSysparameter_value().split(";");
            if (Integer.valueOf(deptGradeValue[level]) <= 0) {
                MsgUtil.showErrorMsg(DeptMngMsg.msg009);
                return;
            }
            DeptAddDlg daDlg = new DeptAddDlg(ContextManager.getMainFrame(), parent, deptGrade, flag);
            daDlg.addPickDeptAddListener(new IPickDeptAddListener() {

                @Override
                public void addDept(DeptCode deptCode) {
                    CheckTreeNode node = new CheckTreeNode(deptCode);
                    Object parent_dept = null;
                    int len = deptCode.getDept_code().length();
                    int len1 = parent_node.isRoot() ? 0 : ((DeptCode) parent_node.getUserObject()).getDept_code().length();
                    if (len == len1) {
                        parent_dept = ((CheckTreeNode) parent_node.getParent()).getUserObject();
                        ((CheckTreeNode) parent_node.getParent()).add(node);
                    } else {
                        parent_dept = parent_node.getUserObject();
                        parent_node.add(node);
                    }
                    if (parent_dept instanceof DeptCode) {
                        ((DeptCode) parent_dept).setEnd_flag(false);
                    }
                    deptPanel.getDeptTree().clearSelection();
                    deptPanel.getDeptTree().addSelectionPath(new TreePath(node.getPath()));
                    deptPanel.updateUIView();
                }
            });
            ContextManager.locateOnMainScreenCenter(daDlg);
            daDlg.setVisible(true);
        } else if (tabIndex == 1) {
            if (cur_node == null || appendixClass == null || curren_dept == null) {
                return;
            }
            IPickBeanPanelEditListener listener = new IPickBeanPanelEditListener() {

                @Override
                public void pickSave(Object obj) {
                    ValidateSQLResult result1 = CommUtil.entity_triger(obj, false);
                    if (result1 != null) {
                        MsgUtil.showHRSaveErrorMsg(result1);
                        return;
                    }
                    ValidateSQLResult result = DeptImpl.addDeptAppendix(obj);
                    if (result.getResult() == 0) {
                        beanTablePanel1.addObject(obj);
                        setPanelState(editState);
                        setEditState(editState);
                        pnlOtherDeptTable.updateUI();
                    } else {
                        MsgUtil.showHRSaveErrorMsg(result);
                    }
                }

                @Override
                public Object getNew() {
                    int tmp_id = 1;
                    Object obj = CommUtil.fetchEntityBy("select max(a.a_id) from " + appendixClass.getSimpleName() + " a where a.deptCode.deptCode_key='" + curren_dept.getDeptCode_key() + "'");
                    if (obj != null) {
                        tmp_id = Integer.valueOf(obj.toString()) + 1;
                    }
                    BaseDeptAppendix baseDeptAppendix = (BaseDeptAppendix) UtilTool.createUIDEntity(appendixClass);
                    baseDeptAppendix.setDeptCode(curren_dept);
                    baseDeptAppendix.setA_id(tmp_id);
                    baseDeptAppendix.setLast_flag("最新");
                    return baseDeptAppendix;
                }
            };
            BeanPanel.editForRepeat(ContextManager.getMainFrame(), beanTablePanel1.getFields(), DeptMngMsg.ttlAddAppendix, null, listener);
        }
    }

    /**
     * 该方法为界面保存操作主函数
     * @param tabIndex:当前卡片索引
     * @param data:需要保存的对象，当为null时表示是点击保存按钮时的操作
     * */
    private void saveObject(int tabIndex, Object data) {
        if (tabIndex == 0) {
            ftable.editingStopped();
            if (data == null) {
                data = ftable.getCurrentRow();
            }
            DeptCode dept = (DeptCode) data;
            CheckTreeNode node = deptPanel.getNodeByDept(dept);
            if (node == null) {
                return;
            }
            editDept(dept, node);
        } else {
            if (data == null) {
                data = beanTablePanel1.getCurrentRow();
            }
            if (data == null) {
                return;
            }
            beanTablePanel1.editingStopped();
            BeanManager.updateEntity(data, editState);
        }
    }

    /**
     * 该方法用于删除操作
     * */
    private void delObject() {
        if (tabIndex == 0) {
            if (!UserContext.isSA) {
                if (cur_node != null && cur_node.getUserObject() instanceof DeptCode) {
                    DeptCode tmp_dept = (DeptCode) cur_node.getUserObject();
                    if (!haveDeptRight(tmp_dept)) {
                        MsgUtil.showErrorMsg(CommMsg.NOPOWER_MESSAGE);
                        return;
                    }
                }
            }
            JTree deptTree = deptPanel.getDeptTree();
            if (deptTree.getSelectionPath() == null || deptTree.getSelectionPath().getLastPathComponent() == deptTree.getModel().getRoot()) {
                return;
            }
            CheckTreeNode temp = (CheckTreeNode) deptTree.getSelectionPath().getLastPathComponent();
            Enumeration enumt = temp.breadthFirstEnumeration();
            List deldepts = new ArrayList();
            while (enumt.hasMoreElements()) {
                CheckTreeNode node = (CheckTreeNode) enumt.nextElement();
                if (node.getUserObject() instanceof DeptCode) {
                    deldepts.add(node.getUserObject());
                }
            }
            if (CommUtil.exists("select 1 from A01 b where b.deptCode.dept_code like '" + ((DeptCode) temp.getUserObject()).getDept_code() + "%'")) {
                MsgUtil.showInfoMsg(DeptMngMsg.msgDeptStaff);
                return;
            }
            String str = "select 1 from A01jd b where b.old_deptCode_key in（select d.deptcode_key from DeptCode d where d.dept_code like'" + ((DeptCode) temp.getUserObject()).getDept_code() + "%')";
            if (!CommUtil.selectSQL(str).isEmpty()) {
                MsgUtil.showInfoMsg(DeptMngMsg.msgDeptHaveStaff);
                return;
            }
            if (MsgUtil.showNotConfirmDialog(DeptMngMsg.msgDeleteDept)) {
                return;
            }
            DeptCode dept = (DeptCode) temp.getUserObject();
            ValidateSQLResult result = DeptImpl.delDept(dept.getDeptCode_key(), EmpUtil.getCommUserLog());
            if (result.getResult() == 0) {
                Enumeration enumt1 = temp.breadthFirstEnumeration();
                while (enumt1.hasMoreElements()) {
                    CheckTreeNode node = (CheckTreeNode) enumt1.nextElement();
                    Object obj = node.getUserObject();
                    if (obj instanceof DeptCode) {
                        ((DeptCode) obj).setDel_flag(true);
                    }
                }
                CheckTreeNode nextNode = (CheckTreeNode) ComponentUtil.getNextNode(temp);
                temp.removeFromParent();
                ComponentUtil.initTreeSelection(deptTree, nextNode);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        } else if (tabIndex == 1) {
            beanTablePanel1.editingStopped();
            List<String> list = beanTablePanel1.getSelectKeys();
            if (list == null || list.isEmpty()) {
                return;
            }
            if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
                return;
            }
            ValidateSQLResult result = CommUtil.deleteObjs(appendixClass.getSimpleName(), "baseDeptAppendix_key", list);//.deleteSet(save_set);
            if (result.getResult() == 0) {
                beanTablePanel1.deleteSelectedRows();
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
            log.info(btnDel.getText() + "#选择" + tabPnlDept.getTitleAt(tabIndex) + "#结果：" + result.getResult() + "(0表示成功，其他表示失败)");
        }
    }

    private boolean haveDeptRight(DeptCode dept) {
        if (UserContext.isSA) {
            return true;
        }
        if (dept == null || dept.getDept_code() == null) {
            return false;
        }
        for (String dept_code : UserContext.dept_codes) {
            if (dept.getDept_code().startsWith(dept_code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 该方法用于浏览/取消编辑
     * @param save_flag：为TRUE时表示浏览，提示是否需要保存；false时，表示取消编辑，会从数据库重读数据
     */
    private void cancelEdit(int old_index, int new_index, boolean save_flag) {
        Object change_obj = null;
        if (old_index == 0) {
            ftable.editingStopped();
            change_obj = BeanManager.getChangeObj(cur_obj);
        } else {
            beanTablePanel1.editingStopped();
            if (BeanManager.isChanged(baseDeptAppendix)) {
                change_obj = baseDeptAppendix;
            }
        }
        if (save_flag && change_obj != null) {
            changeState = true;
            tabPnlDept.setSelectedIndex(old_index);
            save_flag = !MsgUtil.showNotConfirmDialog(CommMsg.SAVECONFIRM_MESSAGE);
            tabPnlDept.setSelectedIndex(new_index);
            changeState = false;
        } else {
            save_flag = false;
        }
        if (save_flag) {
            saveObject(old_index, change_obj);
        } else if (change_obj != null) {
            if (old_index == 0) {
                DeptCode tmp_dept = (DeptCode) cur_obj;
                cur_obj = (DeptCode) CommUtil.fetchEntityBy("from DeptCode d where d.deptCode_key='" + tmp_dept.getDeptCode_key() + "'");
                ftable.replaceRow(cur_obj, "deptCode_key");
            } else {
                BeanManager.remove(change_obj);
                BaseDeptAppendix bda = (BaseDeptAppendix) change_obj;
                bda = (BaseDeptAppendix) CommUtil.fetchEntityBy("from " + appendixClass.getSimpleName() + " a join fetch a.deptCode where a.baseDeptAppendix_key='" + bda.getBaseDeptAppendix_key() + "'");
                baseDeptAppendix = bda;
                beanTablePanel1.replaceRow(bda, "baseDeptAppendix_key");
            }
        }
        setPanelState(editState);
    }

    private void buildToolMenu() {
        final JPopupMenu toolMenu = new JPopupMenu();
        btnTool.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                toolMenu.show(btnTool, 0, 25);
            }
        });
        btnEditWay.add(tableEdit);
        btnEditWay.add(cardEdit);
        mChange.add(miUnit);
        mChange.add(miTransfer);
        mChange.add(miDel);
        toolMenu.add(btnSetGrade);
        toolMenu.add(mChange);
        toolMenu.addSeparator();
        toolMenu.add(btnEditWay);
        toolMenu.add(mi_search);
        toolMenu.add(mi_setShowItems);
        toolMenu.add(mi_replace);
        toolMenu.add(mi_export);
        toolMenu.addSeparator();
        toolMenu.add(deptImport);
        toolMenu.add(miChangePxCode);
        toolMenu.add(miDeptCheck);
        toolMenu.add(miDeptMidSet);
        toolMenu.addSeparator();
        toolMenu.add(btnDeptLog);
        toolMenu.add(miViewBackData);
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
        deptPanel.updateUIView();
        ContextManager.setStatusBar(getFTable());
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
    
    private SAnalyseScheme getAnalyseScheme(){
        SAnalyseScheme analyseScheme = new SAnalyseScheme();
        analyseScheme.setMain_class(DeptCode.class);
        EntityDef d_ed = (EntityDef) CommUtil.fetchEntityBy("from EntityDef where entityName='DeptCode'");
        d_ed.setPackageName("org.jhrcore.entity");
        List<EntityDef> list = new ArrayList<EntityDef>();
        list.add(d_ed);
        list.addAll(appendixList);
        for(Object obj:appendixList){
            EntityDef ed = (EntityDef) obj;
            ed.setPackageName("org.jhrcore.entity");
            analyseScheme.getWhereMap().put(ed.getEntityName(), "DeptCode.deptCode_key=" + ed.getEntityName() + ".deptCode_key");
        }
        analyseScheme.setEntityDefs(list);
        return analyseScheme;
    }
}
