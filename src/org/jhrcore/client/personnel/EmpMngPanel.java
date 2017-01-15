 /*
 * EmpMngPanel2.java
 *
 * Created on 2008年9月9日, 下午1:32
 */
package org.jhrcore.client.personnel;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.ui.DeptPersonPanel;
import org.jhrcore.client.personnel.comm.IPersonContainer;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import org.jhrcore.client.personnel.comm.ImportXLSDialog;
import org.jhrcore.client.personnel.comm.PersonContainer;
import org.jhrcore.util.PublicUtil;
import com.foundercy.pf.control.listener.IPickColumnSumListener;
import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickFieldSetListener;
import com.foundercy.pf.control.listener.IPickPopupListener;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jhrcore.comm.BeanManager;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.PinYinMa;
import org.jhrcore.util.TransferAccessory;
import org.jhrcore.mutil.ReportUtil;
import org.jhrcore.client.personnel.comm.EmpChangeAction;
import org.jhrcore.client.personnel.comm.ICCardWritePanel;
import org.jhrcore.ui.listener.IPickPersonClassListener;
import org.jhrcore.ui.listener.IPickPersonListener;
import org.jhrcore.ui.listener.IPickStyleListner;
import org.jhrcore.client.personnel.comm.EmpCardUtil;
import org.jhrcore.client.personnel.comm.PersonContainerAction;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.BasePersonAppendix;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickDeptListener;
import org.jhrcore.ui.listener.IPickDeptRefreshListener;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.TableListCellRender;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.ui.listener.IPickBeanPanelEditListener;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.StringUtil;

/**
 *
 * @author yangzhou
 */
public class EmpMngPanel extends JPanel implements IModulePanel, IPersonContainer {

    private JButton btnPersonChange = new JButton("人员调配");
    private JButton btnReadCard = new JButton("读入身份证信息");
    private JButton btnContainer = new JButton("人员容器");
    private JButton btnReport = new JButton("常用报表");
    private JButton btnAdd = new JButton("新增");
    private JButton btnEdit = new JButton("编辑");
    private JButton btnDel = new JButton("删除");
    private JButton btnSave = new JButton("保存");
    private JButton btnView = new JButton("浏览");
    private JButton btnCancel = new JButton("取消");
    private JButton btnTools = new JButton("工具");
    private JButton btnFind = new JButton("IC卡人员");
    private JButton btnWrite = new JButton("IC卡写入");
    private JPopupMenu toolPopupMenu = new JPopupMenu();
    private JLabel lblSearch = new JLabel("查找:");
    private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
    private JMenu mEditWay = new JMenu("编辑方式");
    private JMenuItem mi_photo_up = new JMenuItem("批量上传照片");
    private JMenuItem mi_doc = new JMenuItem("个人文档");
    private JMenuItem mi_personAppendixView = new JMenuItem("同时查看多个附表");
    private JMenuItem cardEdit = new JMenuItem("卡片编辑");
    private JMenuItem tableEdit = new JMenuItem("网格编辑");
    private JMenuItem addAppendix = new JMenuItem("新增人事附表");
    private JMenuItem addBatchAppendixs = new JMenuItem("批量新增附表");
    private JMenuItem addAppendixs = new JMenuItem("同时录入多张附表");
    private JMenu m_personIn = new JMenu("导入");
    private JMenuItem mi_personInfoIn = new JMenuItem("导入人员信息");
    private JMenuItem mi_personAppendixIn = new JMenuItem("导入附表信息");
    private JMenu m_personOut = new JMenu("导出");
    private JMenuItem mi_personInfoOut = new JMenuItem("人员基本信息");
    private JMenuItem mi_personAppendixOut = new JMenuItem("导出当前附表信息");
    private JMenuItem mi_exportPhoto = new JMenuItem("导出人员照片");
    private JMenu mi_caculate = new JMenu("自动计算");
    private JMenuItem mi_setShowItems = new JMenuItem("设置显示字段");
    private JMenuItem mi_replace = new JMenuItem("替换");
    private JMenuItem mi_search = new JMenuItem("查询");
    private JMenu m_virtualDept = new JMenu("虚拟部门");
    private JMenuItem mi_virtualDeptIn = new JMenuItem("加入虚拟部门");
    private JMenuItem mi_virtualDeptOut = new JMenuItem("移出虚拟部门");
    private JMenuItem mi_personChangeLog = new JMenuItem("人员增删日志");
    private JMenuItem mi_personNoSet = new JMenuItem("员工序号调整");
    private JTextField cbBoxSearch = new JTextField();
    private JCheckBox chbCurColumn = new JCheckBox("当前列", false);
    private JCheckBox chbNewRecord = new JCheckBox("查看最新记录", false);
    private JMenuItem miContainer = new JMenuItem("发送到人员容器");
    private JMenuItem mi_IDCardRead = new JMenuItem("读身份证");
    private JMenuItem mi_finger = new JMenuItem("采集指纹");
    private JMenuItem mi_changeG10 = new JMenuItem("调整岗位");
    private JMenuItem mi_chgFlagNew = new JMenuItem("调整附表为最新");
    private JMenu mAnnex = new JMenu("附表审批");
    private JMenuItem mi_Submit = new JMenuItem("提交");
    private JMenuItem mi_UnSubmit = new JMenuItem("取消提交");
    private JMenu mSysChange = new JMenu("系统业务");
    private JMenuItem miPym = new JMenuItem("生成拼音码");
    private JMenuItem miViewBackData = new JMenuItem("历史数据查询");
    private JCheckBox jcb_dept_search = new JCheckBox("当前部门");
    private DeptPersonPanel deptPersonPanel;
    private A01 curren_person;//当前人员
    private DeptCode curren_dept;//当前部门
    private FTable beanTablePanel1;//人员主表信息
    private FTable beanTablePanel2;//附表
    private List appendixList = new ArrayList();//附表列表
    private JListBinding bindPersonOtherTable;
    private Class<?> appendixClass = null;
    private Class<?> cur_person_class = null;
    private EntityDef appendixEntity = null;
    private boolean editState = false;
    private int showStyle = 0;//0：网格形式；1：卡片形式
    private int oldStyle = 0;
    private int tabIndex = 0;//卡片索引
    private int flag = 0;//显示最新记录的标志
    private boolean editWay = false;//false:网格编辑;true:卡片编辑
    private BasePersonAppendix basePersonAppendix = null;
    private BeanPanel beanPanel = new BeanPanel();//人员基本信息卡片显示
    private BeanPanel beanPanel2 = new BeanPanel();//附表基本信息卡片显示
    private BeanPanel beanPanel3 = new BeanPanel();//附表页面人员基本信息卡片显示
    private ListSelectionListener personListSelectionListener;
    private ListSelectionListener appendix_listener;
    private Hashtable<Integer, Integer> tabKeys = new Hashtable<Integer, Integer>();
    //用于记录人员表允许显示的所有字段
    private List<TempFieldInfo> person_all_fields = new ArrayList<TempFieldInfo>();
    //用于记录人员表默认显示字段
    private List<TempFieldInfo> person_default_fields = new ArrayList<TempFieldInfo>();
    //用于记录当前附表的所有显示字段
    private List<TempFieldInfo> appendix_all_fields = new ArrayList<TempFieldInfo>();
    //用于记录当前附表的默认显示字段
    private List<TempFieldInfo> appendix_default_fields = new ArrayList<TempFieldInfo>();
    //用于记录人员表的排序Sql
    private String person_order_sql = "DeptCode.dept_code,A01.a0190";
    //用于记录人员附表的排序Sql
    private String appendix_order_sql = "DeptCode.dept_code,A01.a0190,a_id";
    private IPickFieldOrderListener person_order_listener;
    private IPickQueryExListener person_query_listener;
    private IPickFieldSetListener person_field_listener;
    private IPickColumnSumListener column_sum_listener;
    private MouseAdapter mouse_listener;
    private A01 beanPanel3_person = null;
    private String cur_hql;
    private String cur_hql_appdenx;
    private Hashtable<String, JComponent> all_change_items = new Hashtable<String, JComponent>();
    private boolean changeState = false;
    private Hashtable<String, String> id_update_keys = new Hashtable<String, String>();
    private Hashtable<String, TempFieldInfo> a01_field_keys = new Hashtable<String, TempFieldInfo>();
    private List<TempFieldInfo> a01_fields = new ArrayList<TempFieldInfo>();
    public static final String module_code = "EmpMng";
    private HrLog log = new HrLog(module_code);
    private IPickWindowCloseListener change_listener;
    private SysParameter id_parameter = null;
    private List<String> disedit_fields = new ArrayList<String>();//调配模板中设置的不可编辑字段
    private IPickPersonListener pick_person_listener;//部门树人员行选择监听器
    private JTabbedPane tabPane = new JTabbedPane();//人员附表审核不同状态tab页
    private boolean f_dept = false;
    private boolean isAppendixCheck = false;
    private IPickWindowCloseListener wfListener = null;

    @Override
    public void showContainerInfo() {
        refreshPersonList(null, null);
        fetch_PersonInfo(showStyle, tabIndex);
    }

    /**
     * 权限设置
     */
    @Override
    public void setFunctionRight() {
        setEditState(editState);
        setMainPanelState(editState);
        setAppendixPanelState(editState);
    }

    private void setMainPanelState(boolean editting) {
        if (showStyle == 0) {
            beanTablePanel1.setEditable(editting);
        } else {
            beanPanel.setShow_scheme(beanTablePanel1.getCur_show_scheme());
            beanPanel.setFields(beanTablePanel1.getFields());
            beanPanel.setEditable(editting);
            beanPanel.bind();
        }
    }

    private void setAppendixPanelState(boolean editting) {
        if (appendixClass == null) {
            return;
        }
        if (beanTablePanel2 != null) {
            beanTablePanel2.setEditable(!editWay && editting);
        }
        int ind = tabPnlOtherPerson.getSelectedIndex();
        if (ind == 0) {
            if (beanTablePanel2 != null) {
                beanPanel2.setFields(beanTablePanel2.getFields());
                beanPanel2.setBean(beanTablePanel2.getCurrentRow());
            }
            beanPanel2.setEditable(editWay && editting);
            beanPanel2.bind();
        } else if (ind == 1) {
            beanPanel3.bind();
        } else {
            if (appendixEntity != null) {
                BasePersonAppendix bpa = (BasePersonAppendix) beanTablePanel2.getCurrentRow();
                
                pnlWF.removeAll();
                pnlWF.setLayout(new BorderLayout());
                pnlWF.updateUI();
            }
        }
    }

    private void setEditState(boolean editing) {
        editState = editing;
        ComponentUtil.setSysTableFuntion(beanTablePanel1, module_code);
        ComponentUtil.setSysTableFuntion(beanTablePanel2, module_code);
        if (beanTablePanel1 != null) {
            beanTablePanel1.setExportItemEnable(UserContext.hasFunctionRight("EmpMng.mi_personInfoOut"));
        }
        if (beanTablePanel2 != null) {
            beanTablePanel2.setExportItemEnable(UserContext.hasFunctionRight("EmpMng.mi_personAppendixOut"));
        }
        ComponentUtil.setCompEnable(this, btnReadCard, tabIndex == 0 && editing);
        ComponentUtil.setCompEnable(this, btnAdd, tabIndex == 1 && !editing);
        ComponentUtil.setCompEnable(this, btnEdit, !editing);
        ComponentUtil.setCompEnable(this, btnDel, !editing);
        ComponentUtil.setCompEnable(this, btnView, editing);
        ComponentUtil.setCompEnable(this, btnCancel, editing);
        ComponentUtil.setCompEnable(this, btnSave, editing);
        setAddEditState();
    }

    private void setAddEditState() {
        ComponentUtil.setIcon(tableEdit, editWay ? "blank" : "editWay");
        ComponentUtil.setIcon(cardEdit, editWay ? "editWay" : "blank");
        boolean editable;
        boolean addable = false;
        if (tabIndex == 0) {
            editable = cur_person_class != null && UserContext.hasEntityEditRight("A01") && UserContext.hasEntityEditRight(cur_person_class.getSimpleName());
        } else {
            editable = appendixClass != null && UserContext.hasEntityEditRight(appendixClass.getSimpleName());
            addable = appendixClass != null && UserContext.hasEntityAddRight(appendixClass.getSimpleName());
        }
        ComponentUtil.setCompEnable(this, btnEdit, editable && !editState);
        ComponentUtil.setCompEnable(this, addBatchAppendixs, addable);
        ComponentUtil.setCompEnable(this, addAppendix, addable);
    }

    /**
     * Creates new form EmpMngPanel2
     */
    public EmpMngPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spMain = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        toolbar1 = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        tabPnlPerson = new javax.swing.JTabbedPane();
        pnlPersonMain = new javax.swing.JPanel();
        pnlPersonOthers = new javax.swing.JPanel();
        splitPnlOther = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        tabPnlOtherPerson = new javax.swing.JTabbedPane();
        pnlCardOtherPerson = new javax.swing.JPanel();
        scroPanePerson = new javax.swing.JScrollPane();
        pnlCardMainPerson = new javax.swing.JPanel();
        pnlWF = new javax.swing.JPanel();
        pnlOtherPersonTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listAppendix = new javax.swing.JList();

        spMain.setDividerLocation(200);
        spMain.setOneTouchExpandable(true);

        pnlLeft.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlLeft.setLayout(new java.awt.BorderLayout());
        spMain.setLeftComponent(pnlLeft);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        toolbar1.setFloatable(false);
        toolbar1.setRollover(true);

        tabPnlPerson.setAlignmentX(0.0F);
        tabPnlPerson.setAlignmentY(0.0F);
        tabPnlPerson.setFocusable(false);
        tabPnlPerson.setOpaque(true);

        pnlPersonMain.setLayout(new java.awt.BorderLayout());
        tabPnlPerson.addTab("人员基本信息", pnlPersonMain);

        splitPnlOther.setDividerSize(3);
        splitPnlOther.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        pnlCardOtherPerson.setLayout(new java.awt.BorderLayout());
        tabPnlOtherPerson.addTab("\u5361\u7247\u4fe1\u606f", pnlCardOtherPerson);

        pnlCardMainPerson.setLayout(new java.awt.BorderLayout());
        scroPanePerson.setViewportView(pnlCardMainPerson);

        tabPnlOtherPerson.addTab("\u4eba\u5458\u57fa\u672c\u4fe1\u606f", scroPanePerson);

        pnlWF.setLayout(new java.awt.BorderLayout());
        tabPnlOtherPerson.addTab("流程概况", pnlWF);

        jSplitPane2.setBottomComponent(tabPnlOtherPerson);

        pnlOtherPersonTable.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setLeftComponent(pnlOtherPersonTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
        );

        splitPnlOther.setRightComponent(jPanel3);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 47));

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
            .addComponent(splitPnlOther, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
        );

        tabPnlPerson.addTab("\u4eba\u5458\u9644\u8868", pnlPersonOthers);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPnlPerson)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPnlPerson)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addComponent(toolbar1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolbar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spMain.setRightComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spMain, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spMain)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JList listAppendix;
    private javax.swing.JPanel pnlCardMainPerson;
    private javax.swing.JPanel pnlCardOtherPerson;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlOtherPersonTable;
    private javax.swing.JPanel pnlPersonMain;
    private javax.swing.JPanel pnlPersonOthers;
    private javax.swing.JPanel pnlWF;
    private javax.swing.JScrollPane scroPanePerson;
    private javax.swing.JSplitPane spMain;
    private javax.swing.JSplitPane splitPnlOther;
    private javax.swing.JTabbedPane tabPnlOtherPerson;
    private javax.swing.JTabbedPane tabPnlPerson;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JToolBar toolbar1;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        wfListener = new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                fetch_PersonInfo(showStyle, tabIndex);
            }
        };
        ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
        deptPersonPanel = new DeptPersonPanel(A01.class, UserContext.getDepts(false));
        pnlLeft.add(deptPersonPanel, BorderLayout.CENTER);
        Object changeitem = CommUtil.fetchEntityBy("select sysparameter_value from SysParameter sp where sp.sysParameter_key='EmpChange.disEditableChangeField'");
        if (!(changeitem == null || changeitem.toString().trim().equals(""))) {
            String[] item_fields = changeitem.toString().split(";");
            for (String field : item_fields) {
                disedit_fields.add(field);
            }
        }
        initToolBar();
        List entity_list = CommUtil.fetchEntities("from EntityDef ed where ed.entityClass.entityType_code='ANNEX' order by ed.order_no");
        for (Object obj : entity_list) {
            EntityDef tmp_def = (EntityDef) obj;
            if (UserContext.hasEntityViewRight(tmp_def.getEntityName())) {
                appendixList.add(tmp_def);
            }
        }
        bindPersonOtherTable = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, appendixList, listAppendix);
        bindPersonOtherTable.bind();
        listAppendix.setCellRenderer(new TableListCellRender());
        listAppendix.setLayoutOrientation(JList.VERTICAL_WRAP);
        listAppendix.setVisibleRowCount(-1);
        listAppendix.setFixedCellHeight(25);
        pnlCardOtherPerson.add(new JScrollPane(beanPanel2), BorderLayout.CENTER);
        pnlCardMainPerson.add(beanPanel3, BorderLayout.CENTER);
        chbNewRecord.setVisible(false);
        a01_fields = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : a01_fields) {
            a01_field_keys.put(tfi.getField_name().replace("_code_", "").replace(" ", ""), tfi);
        }
    }

    private void setupEvents() {
        
        mi_chgFlagNew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeFlagNew();
            }
        });
        mi_Submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                submitAnnexCheck();
            }
        });
        mi_UnSubmit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                unSubmitAnnexCheck();
            }
        });
//        mi_processWf.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                processAnnexCheck();
//            }
//        });
        mi_personNoSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                ChangePersonNoDlg dlg = new ChangePersonNoDlg(beanTablePanel1.getAllSelectObjects(), person_all_fields, person_default_fields);
                dlg.setTitle(EmpMngMsg.ttl004.toString());
                dlg.setSize(800, 600);
                ContextManager.locateOnMainScreenCenter(dlg);
                dlg.setVisible(true);
                if (dlg.isChange_flag()) {
                    refreshPersonList(beanTablePanel1.getCur_query_scheme(), null);
                    fetch_PersonInfo(showStyle, tabIndex);
                }
            }
        });
        btnReadCard.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curren_person == null) {
                    return;
                }
                if (tabIndex != 0) {
                    return;
                }
                id_parameter = (SysParameter) CommUtil.fetchEntityBy("from SysParameter s where s.sysParameter_key ='Register.id_cmp'");
                if (id_parameter != null) {
                    String strs = id_parameter.getSysparameter_value();
                    String[] tmp_strs = strs.split(";");
                    for (String tmp_s : tmp_strs) {
                        String[] field_strs = tmp_s.split(":");
                        id_update_keys.put(field_strs[0], field_strs[1]);
                    }
                } else {
                    MsgUtil.showErrorMsg(EmpMngMsg.msg003);
                    return;
                }
                log.info(e);
                IDCardRead idc = new IDCardRead();
                if (!idc.getLinkM()) {
                    MsgUtil.showErrorMsg(EmpMngMsg.msg004);
                    return;
                }
                String personName = idc.getPersonName().replace(" ", "");
                if ("".equals(personName)) {
                    MsgUtil.showErrorMsg(EmpMngMsg.msg005);
                    return;
                }
                String cardNo = idc.getPersonIDCardNo().replace(" ", "").toLowerCase();
                String cardName = idc.getPersonName().replace(" ", "").toLowerCase();
                if (!cardNo.equals(curren_person.getA0177().toLowerCase()) || !cardName.equals(curren_person.getA0101().toLowerCase())) {
                    if (MsgUtil.showNotConfirmDialog(EmpMngMsg.msg006)) {
                        return;
                    }
                }
                if (updatePerson(idc) == 1) {
                    ValidateSQLResult result = CommUtil.updateEntity(curren_person);
                    if (result.getResult() == 0) {
                        MsgUtil.showHRSaveSuccessMsg(ContextManager.getMainFrame());
                        if (showStyle == 0) {
                            beanTablePanel1.setCurrentRow(curren_person);
                        } else {
                            beanPanel.setBean(curren_person);
                            beanPanel.bind();
                        }
                    } else {
                        MsgUtil.showHRSaveErrorMsg(result);
                    }
                }
                idc = null;
            }
        });
        mi_search.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (tabIndex == 0) {
                    if (showStyle == 1) {
                        return;
                    }
                    beanTablePanel1.query();
                } else if (tabIndex == 1) {
                    beanTablePanel2.query();
                }
            }
        });
        mi_replace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (tabIndex == 0) {
                    if (showStyle == 1) {
                        return;
                    }
                    beanTablePanel1.replaceData();
                } else if (tabIndex == 1) {
                    beanTablePanel2.replaceData();
                }
            }
        });
        mi_setShowItems.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (tabIndex == 0) {
                    beanTablePanel1.setShowFields();
                    if (showStyle == 1) {
                        beanPanel.setFields(beanTablePanel1.getFields());
                        beanPanel.bind();
                        beanPanel.updateUI();
                    }
                } else if (tabIndex == 1) {
                    beanTablePanel2.setShowFields();
                }
            }
        });
        mi_IDCardRead.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                IDCardReadDlg iDCardReadDlg = new IDCardReadDlg(beanTablePanel1.getFields(), deptPersonPanel.getPersonClassName());
                ContextManager.locateOnMainScreenCenter(iDCardReadDlg);
                iDCardReadDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                iDCardReadDlg.setVisible(true);
            }
        });
        mi_finger.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
//                RegisterFingerPanel panel = new RegisterFingerPanel(curren_person, 1);
//                ModelFrame.showModel(ContextManager.getMainFrame(), panel, true, "采集指纹：", 550, 500);
            }
        });
        btnFind.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex == 1) {
                    return;
                }
                log.info(e);
                String ic_str = ConfigManager.getConfigManager().getProperty("ic_card_no");
                String idc_type = ConfigManager.getConfigManager().getProperty("dukaqi_ic_card_type");
                A01 f_a01 = null;
                if ("1".equals(idc_type)) {
                    String field_name = EmpCardUtil.getCard_no();
                    if (field_name == null || "".equals(field_name)) {
                        MsgUtil.showErrorMsg(EmpMngMsg.msg007);
                        return;
                    }
                    ICCardRead iCCardRead = new ICCardRead();
                    String s = iCCardRead.getLink(ic_str);
                    if (!"1".equals(s)) {
//                        JOptionPane.showMessageDialog(null, s);
                        MsgUtil.showInfoMsg(s);
                        iCCardRead = null;
                        return;
                    }
                    String temp_card_no = EmpCardUtil.getIC_no(iCCardRead.getInfo(ic_str, "0", "-1"));
                    String temp_a0190 = StringUtil.toStringHex(iCCardRead.getInfo(ic_str, "0", "1")).replace(" ", "").replace(" ", "");
                    String f_a0190 = temp_a0190.replaceAll(" ", "").replaceAll(" ", "");
                    if (!"".equals(f_a0190)) {
                        cbBoxSearch.setText(f_a0190);
                        f_a01 = (A01) CommUtil.fetchEntityBy("from A01 a join fetch a.deptCode d where a.a0190 = '" + f_a0190 + "'");
                    }
                    if (f_a01 == null) {
                        cbBoxSearch.setText(temp_card_no);
                        String hql = "A01." + field_name + " = '" + temp_card_no + "'";
                        hql = buildPersonSeq(beanTablePanel1.getCur_query_scheme(), hql);
                        List<String> keys = (List<String>) CommUtil.selectSQL("select A01.a01_key " + hql);
                        deptPersonPanel.setList_person(keys);
                        cur_hql = hql.substring(0, hql.indexOf("order by "));
                        beanTablePanel1.setObjects(keys);
                        return;
                    }
                } else {
                    ICCardRead r = new ICCardRead();
                    String str = r.getInfo(ic_str, "7", "0");
                    if (str.startsWith("错误:")) {
//                        JOptionPane.showMessageDialog(null, str);
                        MsgUtil.showInfoMsg(str);
                        return;
                    }
                    str += r.getInfo(ic_str, "7", "1");
                    str += r.getInfo(ic_str, "7", "2");
                    str = StringUtil.toStringHex(str);
                    String f_a0190 = str.substring(0, 12);
                    f_a0190 = f_a0190.trim();
                    f_a0190 = f_a0190.replaceAll(" ", "").replaceAll(" ", "");
                    cbBoxSearch.setText(f_a0190);
                    f_a01 = (A01) CommUtil.fetchEntityBy("from A01 a join fetch a.deptCode d where a.a0190 = '" + f_a0190 + "'");
                }

                if (f_a01 != null) {
                    boolean t_flag = false;
                    if (!f_a01.getDeptCode().getDept_code().equals(curren_dept.getDept_code())) {
                        deptPersonPanel.locateDeptObj(f_a01.getDeptCode());
                        curren_dept = (DeptCode) deptPersonPanel.getCurDept();
                        t_flag = true;
                    }
                    if (!f_a01.getA0191().equals(deptPersonPanel.getPersonClassName())) {
                        deptPersonPanel.locateType(f_a01.getA0191());
                        cur_person_class = deptPersonPanel.getPersonClass();
                        changePersonClass(cur_person_class);
                        refreshChangeSchemeMenu(cur_person_class);
                        t_flag = true;
                    }
                    if (t_flag) {
                        refreshPersonList(null, null);
                        if (showStyle == 0) {
                            fetch_PersonInfo(showStyle, tabIndex);
                            locatePerson(f_a01);
                        } else {
                            deptPersonPanel.locatePerson(f_a01);
                        }
                    } else {
                        locatePerson(f_a01);
                    }
                } else {
                    MsgUtil.showErrorMsg(EmpMngMsg.msg008);
                }
            }
        });
        btnWrite.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex == 1) {
                    return;
                }
                if ("1".equals(ConfigManager.getConfigManager().getProperty("dukaqi_ic_card_type"))) {
                    ModelFrame.showModel(ContextManager.getMainFrame(), new ICCardWritePanel(curren_person), true, EmpMngMsg.ttl005, 600, 460);
                } else {
                    if (curren_person == null) {
                        return;
                    }
                    if (MsgUtil.showNotConfirmDialog(EmpMngMsg.msg009)) {
                        return;
                    }
                    EmpCardUtil.WriteA01(curren_person);
                }
            }
        });
        deptPersonPanel.getDeptPanel().addPickDeptRefreshListener(new IPickDeptRefreshListener() {

            @Override
            public List<DeptCode> refreshDepts() {
                return UserContext.getDepts(false);
            }
        });
        mi_doc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curren_person.getA0190() == null) {
                    return;
                }
                log.info(e);
                PersonnelDocDlg personDocDlg = new PersonnelDocDlg(curren_person);
                personDocDlg.setTitle(EmpMngMsg.ttl006.toString());
                ContextManager.locateOnMainScreenCenter(personDocDlg);
                personDocDlg.setVisible(true);
            }
        });
        mi_photo_up.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                EmpAddPhotosDlg eapDlg = new EmpAddPhotosDlg(ContextManager.getMainFrame());
                ContextManager.locateOnMainScreenCenter(eapDlg);
                eapDlg.setVisible(true);
            }
        });
        addAppendixs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                List list = null;
                if (showStyle == 0) {
                    list = beanTablePanel1.getAllSelectObjects();
                } else {
                    list = deptPersonPanel.getSelectedPersons();
                }
                AddAppendixsPanel eivPnl = new AddAppendixsPanel(list, curren_person);
                ModelFrame.showModel(ContextManager.getMainFrame(), eivPnl, true, EmpMngMsg.ttl007, 800, 700);
            }
        });
        mi_personInfoOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (cur_person_class == null) {
                    return;
                }
                log.info(arg0);
                beanTablePanel1.exportData();
            }
        });
        mi_personAppendixOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (appendixClass == null) {
                    return;
                }
                log.info(e);
                beanTablePanel2.exportData();
            }
        });
        mi_exportPhoto.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex != 0) {
                    MsgUtil.showInfoMsg(EmpMngMsg.msgSelectA01Pnl);
                    return;
                }
                List<String> selected = new ArrayList();
                if (showStyle == 0) {
                    selected.addAll(beanTablePanel1.getSelectKeys());
                } else {
                    selected.addAll(deptPersonPanel.getFTable().getSelectKeys());
                }
                ExportA01PhotoDlg exportDlg = new ExportA01PhotoDlg(curren_dept, selected);
                ContextManager.locateOnMainScreenCenter(exportDlg);
                exportDlg.setVisible(true);
            }
        });
        addBatchAppendixs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                List tmp_list = new ArrayList();
                if (showStyle == 0) {
                    tmp_list = beanTablePanel1.getAllSelectObjects();
                } else {
                    tmp_list = deptPersonPanel.getSelectedPersons();
                }
                EmpAddAppendixPanel pnl = new EmpAddAppendixPanel();
                   
                    ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, EmpMngMsg.ttl008);
            }
        });
        mi_personInfoIn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex == 1) {
                    MsgUtil.showInfoMsg(EmpMngMsg.msgSelectA01Pnl);
                    return;
                }
                log.info(e);
                ImportA01ExcelPanel panel = new ImportA01ExcelPanel(person_all_fields, person_default_fields, module_code + ".mi_personInfoIn");
                panel.AddPickPersonImportListener(new IPickPersonImportListener() {

                    @Override
                    public void refreshData() {
                        refreshPersonList(null, null);
                        fetch_PersonInfo(showStyle, tabIndex);
                    }

                    @Override
                    public void importPersons() {
                    }
                });
                ModelFrame.showModel(ContextManager.getMainFrame(), panel, true, EmpMngMsg.ttl009, 700, 600);
            }
        });
        mi_personAppendixIn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex == 0) {
                    MsgUtil.showInfoMsg(EmpMngMsg.msgSelectAppendixPnl);
                    return;
                }
                log.info(e);
                ImportXLSDialog importxls_dlg = new ImportXLSDialog(ContextManager.getMainFrame(), appendixClass, module_code + ".mi_personAppendixIn");
                importxls_dlg.addPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        fetch_PersonInfo(showStyle, tabIndex);
                    }
                });
                ContextManager.locateOnMainScreenCenter(importxls_dlg);
                importxls_dlg.setVisible(true);
            }
        });
        ActionListener al_view = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                List list = new ArrayList();
                list.addAll(beanTablePanel1.getAllSelectObjects());
                if (list.isEmpty()) {
                    return;
                }
                EmpInfoViewPanel eivPnl = new EmpInfoViewPanel(list, curren_person, module_code);
                ModelFrame.showModel(ContextManager.getMainFrame(), eivPnl, true, EmpMngMsg.ttl010, 900, 700);
            }
        };
        mi_personAppendixView.addActionListener(al_view);
        ActionListener outVirtualListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (!curren_dept.isVirtual()) {
                    MsgUtil.showErrorMsg(EmpMngMsg.msg012);
                    return;
                }
                List list = beanTablePanel1.getSelectKeys();
                if (list.isEmpty()) {
                    return;
                }
                outVirtualDept(list, curren_dept);
            }
        };
        mi_virtualDeptOut.addActionListener(outVirtualListener);
        ActionListener toVirtualListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = beanTablePanel1.getSelectObjects();
                if (list.isEmpty()) {
                    return;
                }
                log.info(e);
                EmpToVirtualDeptPanel pnlEtvd = new EmpToVirtualDeptPanel(list);
                ModelFrame.showModel(ContextManager.getMainFrame(), pnlEtvd, true, EmpMngMsg.ttl011, 800, 600);
            }
        };
        mi_virtualDeptIn.addActionListener(toVirtualListener);
        ActionListener al_container = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                PersonContainer.getPersonContainer().addPerson(beanTablePanel1.getSelectObjects());
                PersonContainer.getPersonContainer().setVisible(true);
            }
        };
        miContainer.addActionListener(al_container);
        btnContainer.addActionListener(PersonContainerAction.getAction());
        chbNewRecord.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (chbNewRecord.isSelected()) {
                    flag = 1;
                } else {
                    flag = 0;
                }
                fetch_PersonInfo(showStyle, tabIndex);
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String val = cbBoxSearch.getText().trim();
                if (val == null || val.equals("")) {
                    return;
                }
                log.info(e);
                val = SysUtil.getQuickSearchText(val);
                String hql = "";
                if (chbCurColumn.isSelected()) {
                    hql = beanTablePanel1.getQuickSearchSQL("A01", val);
                } else {
                    hql = SysUtil.getQuickSearchSQL(new String[]{"A01.pydm", "A01.a0101", "A01.a0177", "A01.a0190"}, val);
                }
                refreshPersonList(null, hql);
                fetch_PersonInfo(showStyle, tabIndex);
            }
        };
        cbBoxSearch.addActionListener(al);
        btnSearch.addActionListener(al);
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                setEditState(true);
                editObject();
            }
        });
        btnView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                cancelEdit(tabIndex, tabIndex, showStyle, true, false);
                setEditState(false);
                setMainPanelState(editState);
                setAppendixPanelState(editState);
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                cancelEdit(tabIndex, tabIndex, showStyle, false, false);
                setEditState(editState);
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                if (tabIndex == 0) {
                    beanTablePanel1.editingStopped();
                } else if (tabIndex == 1) {
                    beanTablePanel2.editingStopped();
                }
                saveObject(tabIndex, null);
                if (tabIndex == 0) {
                    setMainPanelState(editState);
                } else {
                    setAppendixPanelState(editState);
                }
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                delObject();
            }
        });
        cardEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                editWay = true;
                setAddEditState();
                if (tabIndex == 1) {
                    setAppendixPanelState(editState);
                }
            }
        });
        tableEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                editWay = false;
                setAddEditState();
                if (tabIndex == 1) {
                    setAppendixPanelState(editState);
                }
            }
        });
        mi_personChangeLog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                ModelFrame.showModel(ContextManager.getMainFrame(), new EmpLogPanel(), true, EmpMngMsg.ttl012, 700, 550);
            }
        });
        addAppendix.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                addAppendix();
                setEditState(editState);
            }
        });
        deptPersonPanel.addPickStyleListner(new IPickStyleListner() {

            @Override
            public void pickStyle(int style) {
                if (tabIndex == 0) {
                    beanTablePanel1.editingStopped();
                } else if (tabIndex == 1) {
                    beanTablePanel2.editingStopped();
                }
                String a01_k = curren_person == null ? "-1" : curren_person.getA01_key();
                cancelEdit(tabIndex, tabIndex, showStyle, true, false);
                showStyle = style;
                oldStyle = style;
                if (showStyle == 1) {
                    if (tabIndex == 1 && f_dept) {
                        refreshPersonList(null, null);
                        f_dept = false;
                    }
                    if (!"-1".equals(a01_k)) {
                        deptPersonPanel.locatePerson(a01_k);
                        curren_person = (A01) deptPersonPanel.getCurPerson();
                    }
                }
                ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
                fetch_PersonInfo(showStyle, tabIndex);
            }
        });
        deptPersonPanel.addPickPersonClassListner(new IPickPersonClassListener() {

            @Override
            public void pickPersonClass(Class personClass) {
                ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
                changePersonClass(personClass);
                refreshChangeSchemeMenu(personClass);
            }
        });
        pick_person_listener = new IPickPersonListener() {

            int fetch_PersonInfo_tag = 0;

            @Override
            public void pickPerson(final Object person) {
                if (person == null) {
                    return;
                }
                if (curren_person == person) {
                    return;
                }
                fetch_PersonInfo_tag++;
                final int tmp_tag = fetch_PersonInfo_tag;
                Runnable run = new Runnable() {

                    @Override
                    public void run() {
                        if (tmp_tag < fetch_PersonInfo_tag) {
                            return;
                        }
                        cancelEdit(tabIndex, tabIndex, showStyle, true, false);
                        curren_person = (A01) person;
                        ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
                        fetch_PersonInfo(showStyle, tabIndex);
                    }
                };
                SwingUtilities.invokeLater(run);
            }
        };
        deptPersonPanel.addPickDeptListener(new IPickDeptListener() {

            @Override
            public void pickDept(Object dept) {
                if (dept instanceof DeptCode) {
                    curren_dept = (DeptCode) dept;
                } else {
                    
                }
                if (showStyle != 0 || tabIndex == 0 || tabIndex == 1) {
                    refreshPersonList(null, null);
                }
                ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
                fetch_PersonInfo(showStyle, tabIndex);
                if (showStyle == 0 && tabIndex == 1) {
                    f_dept = true;
                }
            }
        });
        listAppendix.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                log.info(e);
                changeAppendix();
                setEditState(editState);
            }
        });
        person_field_listener = new IPickFieldSetListener() {

            @Override
            public void pickField(ShowScheme showScheme) {
                beanPanel.setShow_scheme(showScheme);
                beanPanel.setFields(beanTablePanel1.getFields());
            }
        };
        person_order_listener = new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                person_order_sql = SysUtil.getSQLOrderString(showScheme, person_order_sql, person_all_fields);
                refreshPersonList(beanTablePanel1.getCur_query_scheme(), null);
                fetch_PersonInfo(showStyle, tabIndex);
            }
        };
        mouse_listener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    deptPersonPanel.changeShowStyleWithListener();
                }
            }
        };
        person_query_listener = new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
//                if ("cxwfbry".equalsIgnoreCase(qs.getQueryScheme_key())) {
//                    if (tabIndex != 1 || deptPersonPanel.getCurDept() == null || deptPersonPanel.getPersonClass() == null || appendixClass == null) {
//                        return;
//                    } else {
//                        String a0191_str = " and " + UserContext.getPerson_class_right_str(deptPersonPanel.getPersonClass(), "A01");
//                        hql = " from A01,DeptCode  where A01.deptCode_key=DeptCode.deptCode_key and DeptCode.dept_code like '" + curren_dept.getDept_code() + "%'" + a0191_str
//                                + " and (" + UserContext.getDept_right_rea_str("DeptCode") + ") and not exists (select 1 from " + appendixClass.getSimpleName() + " where " + appendixClass.getSimpleName() + ".a01_key = A01.a01_key)";
//                    }
//                } else {
//                }
                String hql = buildPersonSeq(qs, null);
                ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
                List<String> keys = (List<String>) CommUtil.selectSQL("select A01.a01_key " + hql);
                deptPersonPanel.setList_person(keys);
                fetch_PersonInfo(showStyle, tabIndex);
            }
        };
        deptPersonPanel.addPickQueryExListener(person_query_listener);
        personListSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                BeanManager.updateEntity(curren_person, editState);
                if (curren_person == beanTablePanel1.getCurrentRow()) {
                    return;
                }
                curren_person = (A01) beanTablePanel1.getCurrentRow();
                refresh();
            }
        };
        column_sum_listener = new IPickColumnSumListener() {

            @Override
            public String pickSumSQL() {
                return cur_hql + "@sql";
            }
        };
        tabPnlPerson.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (changeState) {
                    return;
                }
                log.info(e);
                int old_index = tabIndex;
                int old_style = showStyle;
                tabIndex = tabPnlPerson.getSelectedIndex();
                if (tabIndex == 0) {
                    chbNewRecord.setVisible(false);
                    if (oldStyle != showStyle) {
                        deptPersonPanel.changeShowStyle();
                    }
                    showStyle = deptPersonPanel.getI_showstyle();
                    oldStyle = showStyle;
                } else {
                    chbNewRecord.setVisible(true);
                    deptPersonPanel.locatePerson(curren_person);
                }
                A01 temp_a01 = curren_person;
                cancelEdit(old_index, tabIndex, old_style, true, true);
                if (tabKeys.get(tabIndex) == 1) {
                    if (tabIndex == 0) {
                        refreshPersonList(null, null);
                        deptPersonPanel.delPickPersonListner(pick_person_listener);
                        deptPersonPanel.locatePerson(temp_a01);
                        deptPersonPanel.addPickPersonListener(pick_person_listener);
                        curren_person = temp_a01;
                        ComponentUtil.initTabKeys(tabPnlPerson, tabKeys);
                    }
                    fetch_PersonInfo(showStyle, tabIndex);
                    tabKeys.put(tabIndex, 0);
                }
            }
        });
        btnReport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = null;
                if (PersonContainer.getPersonContainer().isVisible()) {
                    list = PersonContainer.getPersonContainer().getAllPersons();
                } else {
                    list = beanTablePanel1.getAllSelectObjects();
                }
                ReportUtil.buildCommReportMenu(btnReport, module_code, EmpUtil.getReportParaMap(list));
            }
        });
        appendix_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Runnable run = new Runnable() {

                    @Override
                    public void run() {
                        BeanManager.updateEntity(basePersonAppendix, editState);
                        ContextManager.setStatusBar(beanTablePanel2.getObjects().size());
                        if (basePersonAppendix == (BasePersonAppendix) beanTablePanel2.getCurrentRow()) {
                            return;
                        }
                        basePersonAppendix = (BasePersonAppendix) beanTablePanel2.getCurrentRow();
                        if (basePersonAppendix != null && basePersonAppendix.getA01() != null) {
                            String a01_key = basePersonAppendix.getA01().getA01_key();
                            curren_person = (A01) CommUtil.fetchEntityBy("from A01 a join fetch a.deptCode left join fetch a.g10 where a.a01_key = '" + a01_key + "' ");
                            if (showStyle == 1) {
                                deptPersonPanel.locatePerson(curren_person);
                            }
                        }
                        if (basePersonAppendix == null) {
                            return;
                        }
                        beanPanel2.setBean(basePersonAppendix);
                        beanPanel3.setBean(curren_person);
                        setEditState(editState);
                        setAppendixPanelState(editState);
                    }
                };
                SwingUtilities.invokeLater(run);
            }
        };
        tabPnlOtherPerson.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setAppendixPanelState(editState);
            }
        });
        btnTools.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buildToolsMenu(toolPopupMenu);
                toolPopupMenu.show(btnTools, 0, 25);
            }
        });

        miPym.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curren_dept == null) {
                    return;
                }
                log.info(e);
                EmpPymCreateDlg epcDlg = new EmpPymCreateDlg(ContextManager.getMainFrame(), true, curren_dept, beanTablePanel1.getAllSelectObjects());
                epcDlg.setSize(700, 600);
                ContextManager.locateOnMainScreenCenter(epcDlg);
                epcDlg.setVisible(true);
            }
        });
        curren_dept = (DeptCode) deptPersonPanel.getCurDept();
        cur_person_class = deptPersonPanel.getPersonClass();
        changePersonClass(cur_person_class);
        refreshChangeSchemeMenu(cur_person_class);
    }

    //根据人员类别刷新调配模板
    private void refreshChangeSchemeMenu(Class personClass) {
        String new_class_name = personClass.getSimpleName();
        if (new_class_name.equals("A01")) {
            for (String key : all_change_items.keySet()) {
                all_change_items.get(key).setVisible(true);
            }
            return;
        }
        for (String key : all_change_items.keySet()) {
            String[] keys = key.split("\\_");
            if (keys.length <= 1) {
                continue;
            }
            JMenuItem mi = (JMenuItem) all_change_items.get(key);
            if (new_class_name.equals(keys[1])) {
                mi.setVisible(false);
                continue;
            }
            String old_class = keys[2];
            if (old_class == null || old_class.trim().equals("")) {
                mi.setVisible(false);
                continue;
            }
            String[] old_classes = old_class.split("\\;");
            boolean include_this_a0191 = false;
            for (String old : old_classes) {
                if (old.equals(new_class_name)) {
                    include_this_a0191 = true;
                    break;
                }
            }
            mi.setVisible(include_this_a0191);
        }
        for (String key : all_change_items.keySet()) {
            if (key.contains("_")) {
                continue;
            }
            JMenu mi = (JMenu) all_change_items.get(key);
            int child_count = mi.getItemCount();
            boolean visible = false;
            for (int i = 0; i < child_count; i++) {
                JMenuItem c_item = mi.getItem(i);
                if (c_item.isVisible()) {
                    visible = true;
                }
            }
            mi.setVisible(visible);
        }
    }

    private void cancelEdit(int tabIndex, int new_index, int showStyle, boolean save_flag, boolean ch_flag) {
        if (!editState) {
            return;
        }
        if (tabIndex == 0) {
            beanTablePanel1.editingStopped();
        } else if (tabIndex == 1) {
            beanTablePanel2.editingStopped();
        }
        Object obj = null;
        if (tabIndex == 0) {
            obj = curren_person;
        } else {
            obj = basePersonAppendix;
        }
        boolean changed = BeanManager.isChanged(obj);
        if (save_flag && changed) {
            if (ch_flag && showStyle == 0) {
                changeState = true;
                tabPnlPerson.setSelectedIndex(tabIndex);
                deptPersonPanel.changeShowStyle();
            }
            save_flag = !MsgUtil.showNotConfirmDialog(CommMsg.SAVECONFIRM_MESSAGE);
            if (ch_flag && showStyle == 0) {
                tabPnlPerson.setSelectedIndex(new_index);
                deptPersonPanel.changeShowStyle();
                changeState = false;
            }
        } else {
            save_flag = false;
        }
        if (obj == null) {
            return;
        }
        if (save_flag) {
            BeanManager.updateEntity(obj, editState);
        } else {
            BeanManager.remove(obj);
            if (tabIndex == 0) {
                if (obj != null) {
                    obj = (A01) CommUtil.fetchEntityBy("from " + cur_person_class.getSimpleName() + " bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key='" + ((A01) obj).getA01_key() + "'");
                    beanTablePanel1.replaceRow(obj, "a01_key");
                    deptPersonPanel.getFTable().replaceRow(obj, "a01_key");
                    beanTablePanel1.updateUI();
                    deptPersonPanel.getFTable().updateUI();
                    beanPanel.setBean(obj);
                    curren_person = (A01) obj;
                }
                setMainPanelState(editState);
            } else {
                if (obj != null) {
                    String hql = "from " + appendixClass.getSimpleName() + " bpa join fetch bpa.a01 join fetch bpa.a01.deptCode  where bpa.basePersonAppendix_key='" + ((BasePersonAppendix) obj).getBasePersonAppendix_key() + "'";
                    obj = (BasePersonAppendix) CommUtil.fetchEntityBy(hql);
                    beanTablePanel2.replaceRow(obj, "basePersonAppendix_key");
                    beanTablePanel2.updateUI();
                    beanPanel2.setBean(obj);
                    basePersonAppendix = (BasePersonAppendix) obj;
                }
                setAppendixPanelState(editState);
            }
        }
    }

    /**
     * 用于制造人员的Sql语句
     *
     * @param qs：查询方案，如果为空，则表示是界面内响应，非查询结果
     * @return：匹配的sql语句
     */
    private String buildPersonSeq(QueryScheme qs, String s_where) {
        s_where = SysUtil.objToStr(s_where);
        PublicUtil.getProps_value().setProperty(A01.class.getName(), "from  A01 bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in");
        PublicUtil.getProps_value().setProperty(cur_person_class.getName(), "from " + cur_person_class.getSimpleName() + " bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in");
        beanTablePanel1.setCur_query_scheme(qs);
        String hql = "from A01 left join G10 on A01.G10_KEY=G10.G10_KEY,DeptCode  where A01.DEPTCODE_KEY=DeptCode.deptCode_key ";
        if (PersonContainer.isShow_flag()) {
            hql += " and A01.a01_key in(" + PersonContainer.getA01KeyStr() + ")";
        } else {
            if (qs != null) {
                hql += " and A01.a01_key in(" + qs.buildSql() + ")";
            }
            if (s_where.equals("") || jcb_dept_search.isSelected()) {
                if (curren_dept.isVirtual()) {
                    hql += "and exists (select 1 from VirtualDeptPerson vdp where vdp.deptCode_key='" + curren_dept.getDeptCode_key() + "' and vdp.a01_key=A01.a01_key)";
                } else {
                    hql += "and DeptCode.dept_code like '" + curren_dept.getDept_code() + "%'";
                }
            }
            if (!s_where.equals("")) {
                hql += " and " + s_where;
            }
        }
        hql += " and A01.a0193=0 ";
        hql += " and (" + UserContext.getPerson_class_right_str(cur_person_class, "A01") + ")";
        hql += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
        hql = UserContext.getEntityRightSQL(cur_person_class.getSimpleName(), hql, "A01");
        if (!cur_person_class.getSimpleName().equals("A01")) {
            hql = UserContext.getEntityRightSQL("A01", hql, "A01");
        }
        hql += " order by " + person_order_sql;
        return hql;

    }

    private void refreshPersonList(QueryScheme qs, String s_where) {
        if (curren_dept == null) {
            return;
        }
        if (cur_person_class == null) {
            return;
        }
        deptPersonPanel.delPickPersonListner(pick_person_listener);
        cur_hql = buildPersonSeq(qs, s_where);
        List<String> keys = (List<String>) CommUtil.selectSQL(DbUtil.tranSQL("select A01.a01_key " + cur_hql, "rule"));
        deptPersonPanel.setList_person(keys);
        if (deptPersonPanel.getCurPerson() instanceof A01) {
            curren_person = (A01) deptPersonPanel.getCurPerson();
        }
        cur_hql = cur_hql.substring(0, cur_hql.indexOf("order by "));
        deptPersonPanel.addPickPersonListener(pick_person_listener);
    }

    /**
     * 人员类别改变
     *
     * @param personClass
     */
    private void changePersonClass(Class personClass) {
        cur_person_class = personClass;
        beanTablePanel1 = new FTable(cur_person_class, true, true, true, module_code);
        initPersonFields();
        beanTablePanel1.setRight_allow_flag(true);
        beanTablePanel1.setAll_fields(person_all_fields, person_default_fields, null, module_code);
        person_order_sql = SysUtil.getSQLOrderString(beanTablePanel1.getCurOrderScheme(), person_order_sql, person_all_fields);
        beanTablePanel1.addPickFieldOrderListener(person_order_listener);
        beanTablePanel1.addPickQueryExListener(person_query_listener);
        beanTablePanel1.addPickFieldSetListener(person_field_listener);
        beanTablePanel1.addListSelectionListener(personListSelectionListener);
        beanTablePanel1.addPickColumnSumListener(column_sum_listener);
        beanTablePanel1.addMouseListener(mouse_listener);
        beanTablePanel1.addPickPopupListener(new IPickPopupListener() {

            @Override
            public void addMenuItem(JPopupMenu pp) {
                pp.add(new JPopupMenu.Separator());
                pp.add(miContainer);
                pp.add(mi_personAppendixView);
                pp.add(m_virtualDept);
                m_virtualDept.add(mi_virtualDeptIn);
                m_virtualDept.add(mi_virtualDeptOut);
            }
        });
        beanPanel.setShow_scheme(beanTablePanel1.getCur_show_scheme());
        beanPanel.setFields(beanTablePanel1.getFields());
        beanPanel.setDisable_fields(disedit_fields);
        beanTablePanel1.setDisable_fields(disedit_fields);
        refreshPersonList(null, null);
        if (showStyle != 1) {
            fetch_PersonInfo(showStyle, tabIndex);
        }
    }

    private void locatePerson(A01 bp) {
        int row = 0;
        if (bp != null) {
            List list = beanTablePanel1.getObjects();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof A01) {
                    A01 bp1 = (A01) list.get(i);
                    if (bp1.getA01_key().equals(bp.getA01_key())) {
                        row = i;
                        break;
                    }
                } else {
                    if (list.get(i).equals(bp.getA01_key())) {
                        row = i;
                        break;
                    }
                }
            }
        }
        beanTablePanel1.setRowSelectionInterval(row, row);
        beanTablePanel1.getVerticalScrollBar().setValue((row - 5) * beanTablePanel1.getRowHeight());
    }

    /**
     * 各界面响应主函数
     *
     * @param style：当前显示方式，为0时针对部门，为1时针对具体个人
     * @param tabIndex：当前卡片索引
     */
    public void fetch_PersonInfo(int style, int tabIndex) {
        fetchMainPanel(style, tabIndex);
        refresh();
    }

    public void initForIndex(String entityName) {
        tabPnlPerson.setSelectedIndex(1);
        if (entityName != null && entityName.trim().length() > 0) {
            try {
                Class curClass = Class.forName(entityName);
                for (Object obj : appendixList) {
                    EntityDef ef = (EntityDef) obj;
                    if (ef.getEntityName().equals(curClass.getSimpleName())) {
                        listAppendix.setSelectedValue(obj, true);
                        if (tabPane.getTabCount() >= 3) {
                            tabPane.setSelectedIndex(2);
                        }
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void fetchMainPanel(int style, int tabIndex) {
        if (tabIndex == 0) {
            pnlPersonMain.removeAll();
            if (style == 0) {
                beanTablePanel1.removeListSelectionListener(personListSelectionListener);
                beanTablePanel1.setObjects(deptPersonPanel.getList_person());
                locatePerson(curren_person);
                beanTablePanel1.addListSelectionListener(personListSelectionListener);
                personListSelectionListener.valueChanged(null);
                pnlPersonMain.add(beanTablePanel1, BorderLayout.CENTER);
            } else {
                JPanel pnl = new JPanel(new BorderLayout());
                beanPanel.setBean(curren_person);
                pnl.add(beanPanel, BorderLayout.CENTER);
                pnl.add(PersonPhotoDialog.getPersonPhotoDlg(curren_person).getPhotoPanel(), BorderLayout.EAST);
                pnlPersonMain.add(new JScrollPane(pnl), BorderLayout.CENTER);
            }
            setMainPanelState(editState);
            pnlPersonMain.updateUI();
        } else {
            if (appendixClass == null) {
                if (appendixList.size() > 0) {
                    listAppendix.setSelectedIndex(0);
                }
                return;
            }
            refreshAppendix(null);
        }
        String tmp_str1 = "a";
        if (beanPanel3_person != null) {
            tmp_str1 = beanPanel3_person.getA0190();
        }
        String tmp_str2 = "b";
        if (curren_person != null) {
            tmp_str2 = curren_person.getA0190();
        }
        if ((tmp_str1 == null) || (tmp_str2 == null)) {
            tmp_str1 = "a";
            tmp_str2 = "a";
        }
        if (!tmp_str1.equals(tmp_str2)) {
            beanPanel3.setBean(curren_person);
            beanPanel3_person = curren_person;
            setAppendixPanelState(editState);
            beanPanel3.updateUI();
        }
        setEditState(editState);
    }

    private void refreshAppendix(QueryScheme qs) {
        String entityName = appendixClass.getSimpleName();
        String sql = "from " + entityName + ",A01,DeptCode where " + entityName + ".a01_key=A01.A01_KEY AND A01.deptCode_key=DeptCode.deptCode_key";
        if (curren_person != null && showStyle == 1) {
            sql += " and A01.a01_key ='" + curren_person.getA01_key() + "'";
        } else if (curren_dept != null) {
            sql += " and DeptCode.dept_code like '" + curren_dept.getDept_code() + "%' and A01.a0193=0";
        }
        sql += " and (" + UserContext.getPerson_class_right_str(cur_person_class, "A01") + ")";
        sql += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
        if (flag == 1) {
            sql += " and " + entityName + ".last_flag = '最新'";
        }
        sql = UserContext.getEntityRightSQL(entityName, sql, entityName);
        sql = UserContext.getEntityRightSQL(cur_person_class.getSimpleName(), sql, "A01");
        if (!cur_person_class.getSimpleName().equals("A01")) {
            sql = UserContext.getEntityRightSQL("A01", sql, "A01");
        }
        if (isAppendixCheck) {
            String str = "";
            
            String state = "";
            if (tabPane.getSelectedIndex() == 1) {
                if (str.contains("开始") || UserContext.isSA || UserContext.hasFunctionRight("EmpMng.btnAdd")) {
                    state = "未提交";
                }
                sql += " and " + entityName + ".wf_state = '" + state + "' ";
            } else if (tabPane.getSelectedIndex() == 2) {
                if (str.contains("开始") || str.replace("开始", "").length() > 2 || UserContext.isSA) {
                    // state = "已提交";
                    sql += " and " + entityName + ".wf_state != '未提交' and " + entityName + ".wf_state != '已审核' ";
                }
                //sql += " and " + entityName + ".wf_state = '" + state + "' ";
            } else if (tabPane.getSelectedIndex() == 0) {
                state = "已审核";
                sql += " and " + entityName + ".wf_state = '" + state + "' ";
            }
        }
        if (qs != null) {
            sql += " and basePersonAppendix_key in(" + qs.buildSql() + ")";
        }
        sql += " and " + entityName + ".a01_key in(select a01_key " + cur_hql.replace("left join G10 on A01.G10_KEY=G10.G10_KEY", "") + ")";
        cur_hql_appdenx = sql;
        sql += " order by " + appendix_order_sql;
        beanTablePanel2.removeListSelectionListener(appendix_listener);
        PublicUtil.getProps_value().setProperty(appendixClass.getName(), "from " + entityName + " a join fetch a.a01 join fetch a.a01.deptCode left join fetch a.a01.g10 where a.basePersonAppendix_key in");
        List<String> keys = (List<String>) CommUtil.selectSQL(DbUtil.tranSQL("select basePersonAppendix_key " + sql, "rule"));
        beanTablePanel2.setObjects(keys);
        beanTablePanel2.addListSelectionListener(appendix_listener);
        appendix_listener.valueChanged(null);
    }

    /**
     * 选择不同附表时的相关响应
     */
    private void changeAppendix() {
        if (listAppendix.getSelectedValue() == null) {
            return;
        }
        appendixEntity = (EntityDef) listAppendix.getSelectedValue();
        if (appendixClass != null && appendixEntity.getEntityName().equals(appendixClass.getSimpleName())) {
            return;
        }
        try {
            appendixClass = Class.forName("org.jhrcore.entity." + appendixEntity.getEntityName());
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        }

        appendix_order_sql = appendixEntity.getEntityName() + ".a_id";
        isAppendixCheck = EmpUtil.getCheckAppendixTable().contains(appendixClass.getSimpleName());
        beanTablePanel2 = new FTable(appendixClass, true, true, true, module_code) {

            @Override
            public Color getCellForegroud(String fileName, Object cellValue, Object row_obj) {
                if (fileName.equals("last_flag")) {
                    if (cellValue != null && "最新".equals(cellValue.toString())) {
                        return Color.RED;
                    }
                }
                return null;
            }
        };
        beanTablePanel2.setRight_allow_flag(true);
        initAppendixFields();
        beanTablePanel2.setAll_fields(appendix_all_fields, appendix_default_fields, null, module_code);
        beanTablePanel2.setRight_allow_flag(true);
        appendix_order_sql = SysUtil.getSQLOrderString(beanTablePanel2.getCurOrderScheme(), appendix_order_sql, appendix_all_fields);
        pnlOtherPersonTable.removeAll();
        beanTablePanel2.addListSelectionListener(appendix_listener);
        beanTablePanel2.addPickColumnSumListener(new IPickColumnSumListener() {

            @Override
            public String pickSumSQL() {
                return cur_hql_appdenx + "@sql";
            }
        });
        beanTablePanel2.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                appendix_order_sql = SysUtil.getSQLOrderString(showScheme, appendix_order_sql, appendix_all_fields);
                fetch_PersonInfo(showStyle, tabIndex);
            }
        });
        beanTablePanel2.addPickFieldSetListener(new IPickFieldSetListener() {

            @Override
            public void pickField(ShowScheme showScheme) {
                beanPanel2.setShow_scheme(showScheme);
                beanPanel2.setFields(beanTablePanel2.getFields());
                beanPanel2.bind();
            }
        });
        beanTablePanel2.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                refreshAppendix(qs);
            }
        });
        tabPnlOtherPerson.remove(pnlWF);
        if (isAppendixCheck) {
            JPanel pnl = new JPanel();
            pnl.setLayout(new BorderLayout());
            pnl.add(beanTablePanel2, BorderLayout.CENTER);
            beanTablePanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            tabPane.removeAll();
            tabPane.addTab(EmpMngMsg.ttlApp.toString(), pnl);
            tabPane.addTab(EmpMngMsg.ttlUnCommit.toString(), new JPanel());
            tabPane.addTab(EmpMngMsg.ttlProcess.toString(), new JPanel());
            tabPane.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel pnl = (JPanel) tabPane.getSelectedComponent();
                    if (pnl != null) {
                        pnl.removeAll();
                        pnl.setLayout(new BorderLayout());
                        pnl.add(beanTablePanel2, BorderLayout.CENTER);
                        pnl.updateUI();
                    }
                    fetch_PersonInfo(showStyle, tabIndex);
                }
            });
            pnlOtherPersonTable.add(tabPane, BorderLayout.CENTER);
        } else {
            pnlOtherPersonTable.add(beanTablePanel2, BorderLayout.CENTER);
        }
        pnlOtherPersonTable.updateUI();
        tabPnlOtherPerson.updateUI();
        fetch_PersonInfo(showStyle, tabIndex);
    }

    private void changeFlagNew() {
        if (tabIndex == 0) {
            return;
        }
        if (basePersonAppendix == null) {
            return;
        }
        List list = new ArrayList();
        beanTablePanel2.editingStopped();
        list.addAll(beanTablePanel2.getAllObjects());
        if (list.isEmpty()) {
            return;
        }
        List<String[]> data = new ArrayList<String[]>();
        for (Object obj : list) {
            BasePersonAppendix bpa = (BasePersonAppendix) obj;
            String[] str = new String[]{"'" + bpa.getA01().getA01_key() + "'"};
            if (!data.contains(str)) {
                data.add(str);
            }
        }
        ValidateSQLResult result = RSImpl.updateAppendix(appendixClass.getSimpleName(), data);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(CommMsg.ACTIONSUCCESS_MESSAGE);
            fetch_PersonInfo(showStyle, tabIndex);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    private void submitAnnexCheck() {
        Object msg = null;
        if (tabIndex != 1) {
            msg = EmpMngMsg.msgSelectAppendixPnl;
        } else if (tabPane.getSelectedIndex() != 1) {
            msg = EmpMngMsg.msg014;
        } 
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return;
        }
        List selectedList = beanTablePanel2.getAllSelectObjects();
        if (selectedList.isEmpty()) {
            return;
        }
    }

    private void unSubmitAnnexCheck() {
        Object msg = null;
        if (tabIndex != 1) {
            msg = EmpMngMsg.msgSelectAppendixPnl;
        } else if (tabPane.getSelectedIndex() != 2) {
            msg = EmpMngMsg.msg017;
        }
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return;
        }
        List selectedList = beanTablePanel2.getSelectKeys();
        if (selectedList.isEmpty()) {
            return;
        }
        
    }

//    private void processAnnexCheck() {
//        Object msg = null;
//        if (tabIndex != 1) {
//            msg = EmpMngMsg.msgSelectAppendixPnl;
//        } else if (tabPane.getSelectedIndex() != 2) {
//            msg = EmpMngMsg.msg017;
//        } else if (wfd == null) {
//            msg = EmpMngMsg.msg015;
//        }
//        if (msg != null) {
//            MsgUtil.showErrorMsg(msg);
//            return;
//        }
//        AnnexCheckWFProcess process = new AnnexCheckWFProcess();
//        process.addPickWindowCloseListener(new IPickWindowCloseListener() {
//
//            @Override
//            public void pickClose() {
//                fetch_PersonInfo(showStyle, tabIndex);
//            }
//        });
//        process.setCurren_person(curren_person);
//        process.setBasePersonAppendix((BasePersonAppendix) beanTablePanel2.getCurrentRow());
//        process.setDataList(beanTablePanel2.getAllSelectObjects());
//        List wfInstances = CommUtil.fetchEntities("from WfInstance wf where wf.wf_no in ", beanTablePanel2.getSelectKeys());
//        if (wfInstances == null || wfInstances.isEmpty()) {
//            return;
//        }
//        process.processWorkFlowIns(mi_processWf, wfd, (WfInstance) wfInstances.get(0));
//    }
    /**
     * 逐条添加人事附表
     */
    private void addAppendix() {
        if (appendixClass == null) {
            return;
        }
        if (curren_person == null || curren_person.getA01_key() == null) {
            return;
        }
        cancelEdit(tabIndex, tabIndex, showStyle, true, false);
        IPickBeanPanelEditListener listener = new IPickBeanPanelEditListener() {

            @Override
            public void pickSave(Object obj) {
                ValidateSQLResult result1 = CommUtil.entity_triger(obj, false);
                if (result1 != null) {
                    MsgUtil.showHRSaveErrorMsg(result1);
                    return;
                }
                ValidateSQLResult result = RSImpl.addAppendix(obj);
                if (result.getResult() == 0) {
                    fetch_PersonInfo(showStyle, tabIndex);
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }

            @Override
            public Object getNew() {
                int tmp_id = 1;
                Object obj = CommUtil.fetchEntityBy("select max(a.a_id) from " + appendixClass.getSimpleName() + " a where a.a01.a01_key='" + curren_person.getA01_key() + "'");
                if (obj != null) {
                    tmp_id = Integer.valueOf(obj.toString()) + 1;
                }
                BasePersonAppendix basePersonAppendix = (BasePersonAppendix) UtilTool.createUIDEntity(appendixClass);
                basePersonAppendix.setA01(curren_person);
                basePersonAppendix.setA_id(tmp_id);
                basePersonAppendix.setLast_flag("最新");
                if (isAppendixCheck) {
                    PublicUtil.setValueBy2(basePersonAppendix, "wf_state", "未提交");
                    PublicUtil.setValueBy2(basePersonAppendix, "person_code", UserContext.person_code);
                }

                return basePersonAppendix;
            }
        };
        List<String> fields = new ArrayList<String>();
        fields.addAll(beanTablePanel2.getFields());
        fields.remove("wf_state");
        BeanPanel.editForRepeat(ContextManager.getMainFrame(), fields, EmpMngMsg.msg085, null, listener);
    }

    private void editObject() {
        editState = true;
        if (tabIndex == 0) {
            setMainPanelState(editState);
        } else {
            setAppendixPanelState(editState);
        }
    }

    private void saveObject(int tabIndex, Object obj) {
        if (tabIndex == 0) {
            if (showStyle == 1) {
                obj = beanPanel.getChangeObj(null);
            }
            if (obj == null) {
                obj = curren_person;
            }
            ((A01) obj).setPydm(PinYinMa.ctoE(((A01) obj).getA0101()));
        } else {
            if (obj == null) {
                obj = beanTablePanel2.getCurrentRow();
            }
        }
        BeanManager.updateEntity(obj, editState);
        beanPanel.setBean(obj);
        deptPersonPanel.getFTable().updateUI();
        setEditState(editState);
    }

    /**
     * 删除操作
     */
    private void delObject() {
        List delList = new ArrayList();
        boolean delable = false;
        if (tabIndex == 0) {
            if (showStyle == 0) {
                beanTablePanel1.editingStopped();
                delList.addAll(beanTablePanel1.getAllSelectObjects());
            } else {
                if (beanPanel.getBean() == null) {
                    return;
                }
                delList.add(beanPanel.getBean());
            }
            delable = UserContext.hasEntityDelRight(cur_person_class.getSimpleName()) && UserContext.hasEntityDelRight("A01");
        } else {
            delable = UserContext.hasEntityDelRight(appendixClass.getSimpleName());
            beanTablePanel2.editingStopped();
            delList.addAll(beanTablePanel2.getAllSelectObjects());
        }
        if (delList.isEmpty()) {
            return;
        }
        if (!delable) {
            MsgUtil.showErrorMsg(CommMsg.DELNORIGHT_MESSAGE);
            return;
        }
        if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
            return;
        }
        ValidateSQLResult result;
        if (tabIndex == 0) {
            List<String> del_persons = new ArrayList<String>();
            for (Object obj : delList) {
                del_persons.add(((A01) obj).getA01_key());
            }
            String ex_sqls = DbUtil.getQueryForMID("update A01 set a0193=1 where a01_key in ", del_persons);
            Date date = new Date();
            String log_sql = "insert into RyChgLog (RyChgLog_key,a01_key,chg_date,chg_ip,chg_mac,chg_user,chg_type,beforestate,afterstate,chg_field,a0101,a0190,dept_name) ";
            log_sql += "select " + DbUtil.getUIDForDb(UserContext.sql_dialect) + ",a.a01_key," + DateUtil.toStringForQuery(date, "yyyy-MM-dd hh:mm:ss", UserContext.sql_dialect) + ",'" + UserContext.getPerson_ip() + "','" + UserContext.getPerson_mac() + "','" + UserContext.person_code + "','删除','0','1','a0193',a.a0101,a.a0190,d.content from a01 a,deptCode d where a.deptCode_key = d.deptCode_key and  a.a01_key in ";
            ex_sqls = ex_sqls + DbUtil.getQueryForMID(log_sql, del_persons);
            result = CommUtil.excuteSQLs(ex_sqls, ";");
        } else {
            List<String[]> data = new ArrayList<String[]>();
            for (Object obj : delList) {
                BasePersonAppendix bpa = (BasePersonAppendix) obj;
                data.add(new String[]{"'" + bpa.getBasePersonAppendix_key() + "'", "'" + bpa.getA01().getA01_key() + "'"});
            }
            result = RSImpl.delAppendix(appendixClass.getSimpleName(), data);
        }
        if (result.getResult() != 0) {
            MsgUtil.showHRDelErrorMsg(result);
            return;
        }
        MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
        if (tabIndex == 0) {
            if (showStyle == 0) {
                beanTablePanel1.deleteSelectedRows();
            } else {
                deptPersonPanel.delCurRow();
            }
        } else {
            fetch_PersonInfo(showStyle, tabIndex);
        }
    }

    private void outVirtualDept(List list, DeptCode dept) {
        EmpOutVirtualDeptDlg eovdDlg = new EmpOutVirtualDeptDlg(list, dept);
        ContextManager.locateOnMainScreenCenter(eovdDlg);
        eovdDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        eovdDlg.setVisible(true);
        if (eovdDlg.isOut_flag()) {
            refreshPersonList(beanTablePanel1.getCur_query_scheme(), null);
            fetch_PersonInfo(showStyle, tabIndex);
        }
    }

    @Override
    public void locateDeptAndType(A01 bp) {
        curren_person = bp;
        if (showStyle == 0) {
            deptPersonPanel.changeShowStyle();
            showStyle = 1;
        }
        deptPersonPanel.locateType(bp.getA0191());
        deptPersonPanel.locateDeptObj(bp.getDeptCode());
        curren_dept = bp.getDeptCode();
        cur_person_class = deptPersonPanel.getPersonClass();
        refreshPersonList(beanTablePanel1.getCur_query_scheme(), null);
        deptPersonPanel.locatePerson(bp);
        fetchMainPanel(showStyle, tabIndex);
    }

    @Override
    public void pickClose() {
    }

    /**
     * 初始化附表显示字段
     */
    private void initAppendixFields() {
        appendix_all_fields.clear();
        appendix_default_fields.clear();
        EntityBuilder.buildInfo(DeptCode.class, appendix_all_fields, appendix_default_fields, "a01.deptCode");
        EntityBuilder.buildInfo(A01.class, appendix_all_fields, appendix_default_fields, "a01");
        EntityBuilder.buildInfo(appendixClass, appendix_all_fields, appendix_default_fields);
    }

    /**
     * 初始化人员表显示字段
     */
    private void initPersonFields() {
        person_all_fields.clear();
        person_default_fields.clear();
        EntityBuilder.buildInfo(DeptCode.class, person_all_fields, person_default_fields, "deptCode");
        EntityBuilder.buildInfo(deptPersonPanel.getPersonClass(), person_all_fields, person_default_fields);
    }

    private void initToolBar() {
        toolbar.removeAll();
        toolbar1.removeAll();
        buildEmpChangeMenu();
        toolbar.add(btnPersonChange);
        toolbar.add(btnReadCard);
        toolbar.add(btnFind);
        toolbar.add(btnWrite);
        toolbar.addSeparator();
        toolbar.add(btnContainer);
        toolbar.add(btnReport);
        buildAddMenu();
        toolbar1.add(btnAdd);
        toolbar1.add(btnEdit);
        toolbar1.add(btnView);
        toolbar1.add(btnSave);
        toolbar1.add(btnCancel);
        toolbar1.add(btnDel);
        buildToolsMenu(toolPopupMenu);
        toolbar1.addSeparator();
        toolbar1.add(btnTools);
        toolbar1.addSeparator();
        toolbar1.add(lblSearch);
        cbBoxSearch.setEditable(true);
        ComponentUtil.setSize(btnSearch, 22, 24);
        ComponentUtil.setSize(cbBoxSearch, 180, 24);
        toolbar1.add(cbBoxSearch);
        toolbar1.add(btnSearch);
        toolbar1.add(chbCurColumn);
        toolbar1.add(jcb_dept_search);
        toolbar1.add(chbNewRecord);
        jcb_dept_search.setSelected(true);
    }

    /**
     * 创建新增按钮弹出菜单
     */
    private void buildAddMenu() {
        final JPopupMenu addMenu = new JPopupMenu();
        addMenu.add(addAppendix);
        addMenu.addSeparator();
        addMenu.add(addBatchAppendixs);
        addMenu.addSeparator();
        addMenu.add(addAppendixs);
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addMenu.show(btnAdd, 0, 25);
            }
        });
    }

    /**
     * 创建工具菜单项
     *
     * @param toolMenu
     */
    private void buildToolsMenu(final JPopupMenu toolMenu) {
        ComponentUtil.buildAutoExcuteMenu(mi_caculate, module_code);
        toolMenu.removeAll();
        mSysChange.removeAll();
        mEditWay.add(tableEdit);
        mEditWay.add(cardEdit);
        m_personIn.add(mi_personInfoIn);
        m_personIn.add(mi_personAppendixIn);
        m_personIn.add(mi_photo_up);
        m_personIn.add(mi_doc);
        m_personOut.add(mi_personInfoOut);
        m_personOut.add(mi_personAppendixOut);
        m_personOut.add(mi_exportPhoto);
        m_virtualDept.add(mi_virtualDeptIn);
        m_virtualDept.add(mi_virtualDeptOut);
        toolMenu.add(mi_search);
        toolMenu.add(mi_personAppendixView);
        toolMenu.addSeparator();
        toolMenu.add(m_personIn);
        toolMenu.add(m_personOut);
        toolMenu.add(mi_replace);
        toolMenu.add(mi_caculate);
        toolMenu.add(mi_IDCardRead);
        toolMenu.add(mi_finger);
        toolMenu.add(mSysChange);
        mSysChange.add(mi_changeG10);
        mSysChange.add(mi_chgFlagNew);
        mSysChange.add(mi_personNoSet);
        mSysChange.addSeparator();
        mSysChange.add(miPym);
        toolMenu.add(m_virtualDept);
        toolMenu.addSeparator();
        toolMenu.add(mi_setShowItems);
        toolMenu.add(mEditWay);
        toolMenu.addSeparator();
        toolMenu.add(miViewBackData);
        toolMenu.add(mi_personChangeLog);
        mAnnex.removeAll();
        mAnnex.add(mi_Submit);
        mAnnex.add(mi_UnSubmit);
//        mAnnex.addSeparator();
//        mAnnex.add(mi_processWf);
        toolMenu.add(mAnnex);
    }

    /**
     * 创建人员调配菜单
     */
    private void buildEmpChangeMenu() {
        change_listener = new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                FTable ftable = PersonContainer.getPersonContainer().getFTable();
                List list = ftable.getAllKeys();
                ftable.deleteAllRows();
                ftable.setObjects(list);
                if (showStyle != 0 || tabIndex == 0) {
                    refreshPersonList(null, null);
                }
                fetch_PersonInfo(showStyle, tabIndex);
            }
        };
        final JPopupMenu toolMenu = new JPopupMenu();
        Hashtable<String, JMenu> first_menu_set = new Hashtable<String, JMenu>();
        List list = EmpUtil.getUserChangeSchemeList();
        for (Object obj : list) {
            ChangeScheme c = (ChangeScheme) obj;
            if (c.getChangeScheme_type() == null || c.getChangeScheme_type().replace(" ", "").equals("")) {
                toolMenu.add(getEmpChangeItem(c));
            } else {
                JMenu first_menu = first_menu_set.get(c.getChangeScheme_type());
                if (first_menu == null) {
                    first_menu = new JMenu(c.getChangeScheme_type());
                    ComponentUtil.setIcon(first_menu, "blank");
                    first_menu_set.put(c.getChangeScheme_type(), first_menu);
                    toolMenu.add(first_menu);
                    all_change_items.put(c.getChangeScheme_no() + "", first_menu);
                }
                first_menu.add(getEmpChangeItem(c));
            }
        }
        btnPersonChange.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                toolMenu.show(btnPersonChange, 0, btnPersonChange.getHeight());
            }
        });
    }

    private JMenuItem getEmpChangeItem(final ChangeScheme c) {
        JMenuItem mi = new JMenuItem(c.getChangeScheme_name());
        if (c.contains("a0191")) {
            all_change_items.put(c.getChangeScheme_no() + "_" + c.getNewPersonClassName() + "_" + c.getOldPersonClassName(), mi);
        }
        ComponentUtil.setIcon(mi, "blank");
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List person_list = null;
                // 如果人员容器可见，那么对人员容器选种的人员进行变动
                if (PersonContainer.getPersonContainer().isVisible()) {
                    person_list = PersonContainer.getPersonContainer().getFTable().getSelectObjects();
                } else {
                    if (showStyle == 0) {
                        person_list = beanTablePanel1.getAllSelectObjects();
                    } else {
                        if (beanPanel.getBean() == null) {
                            return;
                        }
                        person_list = new ArrayList();
                        person_list.add((A01) beanPanel.getBean());
                    }
                }
                EmpChangeAction.doEmpChangeAction(btnPersonChange, c, person_list, cur_person_class, change_listener);
            }
        });

        return mi;
    }

    private int updatePerson(IDCardRead idcard) {
        int result_flag = 1;
        if (idcard == null) {
            return 0;
        }
        String text = idcard.getPersonIDCardNo().replace(" ", "");
        Object msg = null;
        if (text.equals("")) {
            msg = EmpMngMsg.msg001;
        } else if (!SysUtil.isRightIdentity(text)) {
            msg = EmpMngMsg.msg002;
        }
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return 0;
        }
        String person_class = "";
        if (!(EmpMngMsg.msg043.toString()).equals(deptPersonPanel.getPersonClassName())) {
            person_class = " and a.a0191='" + deptPersonPanel.getPersonClassName() + "'";
        }
        String hql = "from A01 a join fetch a.deptCode where a.a0193=0 " + person_class + " and (a.a0177='" + text + "'";
        if (text.length() == 18) {
            String other_id = text.substring(0, 6);
            other_id += text.substring(8, 17);
            hql += " or a.a0177='" + other_id + "'";
        }
        hql += ")";
        List same_persons = CommUtil.fetchEntities(hql);
        if (same_persons.size() > 1) {
            FTable tmp_ftable = new FTable(A01.class, false, false, false, "Emp");
            tmp_ftable.setFields(beanTablePanel1.getFields());
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(700, 400));
            panel.setLayout(new java.awt.BorderLayout());
            panel.add(tmp_ftable, BorderLayout.CENTER);
            tmp_ftable.setObjects(same_persons);
            ModalDialog.doModal(btnView, panel, EmpMngMsg.ttl001);
            return 0;
        }
        List<String> src_fields = new ArrayList<String>();
        List<String> dst_fields = new ArrayList<String>();
        for (String key : id_update_keys.keySet()) {
            String value = id_update_keys.get(key);
            TempFieldInfo tfi = a01_field_keys.get(value.replace("_code_", ""));
            if (tfi == null) {
                continue;
            }
            src_fields.add(key);
            dst_fields.add(tfi.getField_name());
        }
        PublicUtil.person_copyProperties(idcard, curren_person, src_fields, dst_fields, a01_fields);
        SysParameter register_photo = null;
        Object obj2 = CommUtil.fetchEntityBy("from SysParameter sp where sp.sysParameter_key='Register.save_photo'");
        if (obj2 != null) {
            register_photo = (SysParameter) obj2;
            if ("1".equals(register_photo.getSysparameter_value())) {
                String photoPath = idcard.getPersonPhotoFileName().replace(" ", "");
                photoPath = photoPath.replace("\\", "/");
                photoPath = photoPath.replace("ProgramFiles", "Program Files");
                File photo_url = new File(photoPath);
                if (photo_url.isFile() && photo_url.exists() && TransferAccessory.checkPic(photo_url) == -1) {
                    TransferAccessory.uploadPicture(photo_url, curren_person.getDeptCode().getDept_code() + "", curren_person.getA0190() + "");
                    curren_person.setPic_path(curren_person.getDeptCode().getDept_code() + "/" + curren_person.getA0190() + photo_url.getName().substring(photo_url.getName().lastIndexOf(".")));
                }
            }
        }
        if (showStyle == 0) {
            beanTablePanel1.updateUI();
        } else {
            beanPanel.bind();
        }
        return result_flag;
    }

    @Override
    public void refresh() {
        ContextManager.setMainFrameTitle((curren_person == null || curren_person.getDeptCode() == null) ? "" : curren_person.getDeptCode().getDept_full_name());
        deptPersonPanel.getDeptPanel().updateUIView();
        if (tabIndex == 0) {
            ContextManager.setStatusBar(beanTablePanel1.getObjects().size());
        } else if (beanTablePanel2 != null) {
            ContextManager.setStatusBar(beanTablePanel2.getObjects().size());
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
