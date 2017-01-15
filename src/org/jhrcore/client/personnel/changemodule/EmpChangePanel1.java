/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EmpChangePanel1.java
 *
 * Created on 2010-5-23, 10:08:18
 */
package org.jhrcore.client.personnel.changemodule;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.mutil.ReportUtil;
import org.jhrcore.client.personnel.ICCardRead;
import org.jhrcore.ui.DeptPersonPanel;
import org.jhrcore.ui.listener.IPickPersonClassListener;
import org.jhrcore.ui.listener.IPickPersonListener;
import org.jhrcore.ui.listener.IPickStyleListner;
import org.jhrcore.client.personnel.comm.PersonContainer;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.A01Chg;
import org.jhrcore.entity.BasePersonChange;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeItem;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.ui.listener.IPickDeptListener;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.emp.EmpChangeMsg;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.task.IWaitWork;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.StringUtil;

/**
 *
 * @author mxliteboss
 */
public class EmpChangePanel1 extends javax.swing.JPanel implements IModulePanel, IWaitWork {

    private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
    private JhrDatePicker date_start = new JhrDatePicker();
    private JhrDatePicker date_end = new JhrDatePicker();
    private DeptPersonPanel deptPersonPanel = null;
    private FTable btp_change_main; //变动主表信息
    private FTable ftable_work_flow;
    private DeptCode cur_dept;
    private ListSelectionListener main_change_listener;
    private FTable log_table;
    private A01 cur_person;
    private int cur_style = 0;
    private JComboBoxBinding scheme_binding;
    //用于记录所有允许显示的字段
    private List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
    //用于记录默认显示字段
    private List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
    //用于记录所有允许显示的字段
    private List<TempFieldInfo> appendix_all_fields = new ArrayList<TempFieldInfo>();
    //用于记录默认显示字段
    private List<TempFieldInfo> appendix_default_fields = new ArrayList<TempFieldInfo>();
    private String change_order_sql = "A01.a0190";
    private String appendix_order_sql = "A01.a0190";
    private IPickQueryExListener query_listener;
    private Hashtable<String, ChangeScheme> scheme_keys = new Hashtable<String, ChangeScheme>();
    private Class cur_class = A01.class;
    private List list_change_type = new ArrayList();
    private List list_all_type = new ArrayList();
    private ActionListener type_listener;
    private ActionListener search_listener;
    private int tabIndex = 0;//主卡片索引
    private int select_num = 0;//变动主表卡片计数器
    private int select_num1 = 0;//待处理变动卡片计数器
    private int select_num2 = 0;//处理中变动卡片计数器
    private int select_num3 = 0;//变动详细信息卡片计数器
    private boolean follow = true;//更随主表
    private boolean showCancel = false;//仅显示可撤销的
    private boolean showRecovery = false;//仅显示可还原的
    private List change_data;
    private List unchange_data;
    private List dochange_data;
//    private WorkFlowDef wfd = null;
    private String change_main_sql = "";
    public static final String module_code = "EmpChange";
    private HrLog log = new HrLog(module_code);
    private JCheckBox jcbTime = new JCheckBox("起止日期");
    private JButton btnFind = new JButton("IC卡人员");
    private JMenuItem miPayDate = new JMenuItem("修改起薪日期");
    private JMenuItem miShowCancel = new JMenuItem("仅显示可撤销变动");
    private JMenuItem miRecovery = new JMenuItem("还原变动");
    private JMenuItem miShowRecovery = new JMenuItem("仅显示可还原变动");
    private JMenuItem miFollow = new JMenuItem("跟随主表");
    private JPopupMenu pp = new JPopupMenu();
    private JLabel lbl = new JLabel("调动类型");
    private JLabel lblTo = new JLabel(" 到 ");
    private JComboBox jcbDept = new JComboBox(new String[]{"所有", "调入", "调出"});
    private ChangeScheme curScheme = null;
    private BeanPanel beanPanel = new BeanPanel();

    /** Creates new form EmpChangePanel1 */
    public EmpChangePanel1() {
        initComponents();
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

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        btnUse = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnTool = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnReport = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        jtfSearch = new javax.swing.JTextField();
        btnQuickSearch = new javax.swing.JButton();
        toolbar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jcbType = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jtpMain = new javax.swing.JTabbedPane();
        pnlChg = new javax.swing.JPanel();
        pnlUnChg = new javax.swing.JPanel();
        pnlChgWait = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jtpLog = new javax.swing.JTabbedPane();
        pnlLog = new javax.swing.JPanel();
        pnlInfo = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOneTouchExpandable(true);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        btnUse.setText("处理业务");
        btnUse.setFocusable(false);
        btnUse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnUse);

        btnCancel.setText("撤销流程");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnCancel);
        toolbar.add(jSeparator2);

        btnTool.setText("工具");
        btnTool.setFocusable(false);
        btnTool.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTool.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnTool);
        toolbar.add(jSeparator1);

        btnReport.setText("常用报表");
        btnReport.setFocusable(false);
        btnReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnReport);
        toolbar.add(jSeparator3);

        jLabel3.setText(" 查找：");
        toolbar.add(jLabel3);

        jtfSearch.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        toolbar.add(jtfSearch);

        btnQuickSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search.png"))); // NOI18N
        btnQuickSearch.setFocusable(false);
        btnQuickSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuickSearch.setMaximumSize(new java.awt.Dimension(24, 24));
        btnQuickSearch.setPreferredSize(new java.awt.Dimension(22, 22));
        btnQuickSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnQuickSearch);

        toolbar1.setFloatable(false);
        toolbar1.setRollover(true);

        jLabel1.setText("变动类型：");
        toolbar1.add(jLabel1);

        jcbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbType.setMaximumSize(new java.awt.Dimension(160, 32767));
        toolbar1.add(jcbType);

        jSplitPane2.setDividerLocation(300);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        pnlChg.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlChg.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("变动主表信息", pnlChg);

        pnlUnChg.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlUnChg.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("待处理变动", pnlUnChg);

        pnlChgWait.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlChgWait.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("处理中变动", pnlChgWait);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
        );

        jSplitPane2.setTopComponent(jPanel4);

        pnlLog.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlLog.setLayout(new java.awt.BorderLayout());
        jtpLog.addTab("变动详细信息", pnlLog);

        pnlInfo.setLayout(new java.awt.BorderLayout());
        jtpLog.addTab("变动单据", pnlInfo);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jtpLog.addTab("流程概况", jPanel1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpLog, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpLog, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel5);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addComponent(toolbar1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolbar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnQuickSearch;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnTool;
    private javax.swing.JButton btnUse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JComboBox jcbType;
    private javax.swing.JTextField jtfSearch;
    private javax.swing.JTabbedPane jtpLog;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlChg;
    private javax.swing.JPanel pnlChgWait;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlLog;
    private javax.swing.JPanel pnlUnChg;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JToolBar toolbar1;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        initToolBar();
        list_all_type.add("全部");
        List list = EmpUtil.getViewChangeSchemeList();
        for (Object obj : list) {
            ChangeScheme cs = (ChangeScheme) obj;
            
            scheme_keys.put(cs.getChangeScheme_key(), cs);
            list_all_type.add(cs);
        }
        scheme_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, list_change_type, jcbType);
        scheme_binding.bind();
        EntityBuilder.buildInfo(DeptCode.class, all_fields, default_fields, "a01.deptCode");
        EntityBuilder.buildInfo(A01.class, all_fields, default_fields, "a01");
        EntityBuilder.buildInfo(BasePersonChange.class, all_fields, default_fields);
        btp_change_main = new FTable(BasePersonChange.class, true, true, false, module_code);
        btp_change_main.setAll_fields(all_fields, default_fields, module_code);
        btp_change_main.setRight_allow_flag(true);
        btp_change_main.removeSumAndReplaceItem();
        change_order_sql = SysUtil.getSQLOrderString(btp_change_main.getCurOrderScheme(), change_order_sql, all_fields);
        pnlChg.add(btp_change_main, BorderLayout.CENTER);
        log_table = new FTable(A01Chg.class, true, true, false, module_code);
        EntityBuilder.buildInfo(A01Chg.class, appendix_all_fields, appendix_default_fields);
        EntityBuilder.buildInfo(BasePersonChange.class, appendix_all_fields, null, "basePersonChange");
        EntityBuilder.buildInfo(A01.class, appendix_all_fields, null, "basePersonChange.a01");
        EntityBuilder.buildInfo(DeptCode.class, appendix_all_fields, null, "basePersonChange.a01.deptCode");
        log_table.setAll_fields(appendix_all_fields, appendix_default_fields, module_code);
        appendix_order_sql = SysUtil.getSQLOrderString(log_table.getCurOrderScheme(), appendix_order_sql, appendix_all_fields);
        log_table.setRight_allow_flag(true);
        log_table.removeSumAndReplaceItem();
        deptPersonPanel = new DeptPersonPanel(false, UserContext.getDepts(false));
        deptPersonPanel.setInit_flag(true);
        deptPersonPanel.setQuery_tab_flag(false);
        deptPersonPanel.init();
        pnlLeft.add(deptPersonPanel, BorderLayout.CENTER);
        pnlLog.add(log_table, BorderLayout.CENTER);
        pnlInfo.add(new JScrollPane(beanPanel));
    }

    private void setupEvents() {
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = new ArrayList();
                List data = btp_change_main.getAllSelectObjects();
                for (Object obj : data) {
                    String order_no = ((BasePersonChange) obj).getOrder_no();
                    if (!list.contains(order_no)) {
                        list.add(order_no);
                    }
                }
                List result_data = null;
                if (UserContext.isSA) {
                    result_data = list;
                } 
                if (result_data.isEmpty()) {
                    MsgUtil.showInfoMsg(EmpChangeMsg.msg001);
                    return;
                }
                if (MsgUtil.showNotConfirmDialog(EmpChangeMsg.msg002)) {
                    return;
                }
            }
        });
        miPayDate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changePayDate();
            }
        });
        btp_change_main.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    changePayDate();
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
                    list = btp_change_main.getAllSelectObjects();
                }
                ReportUtil.buildCommReportMenu(btnReport, module_code, EmpUtil.getReportParaMap(list));
            }
        });
        btnFind.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabIndex != 1) {
                    return;
                }
                log.info(e);
                String ic_str = ConfigManager.getConfigManager().getProperty("ic_card_no");
                ICCardRead r = new ICCardRead();
                String str = r.getInfo(ic_str, "7", "0");
                if (str.startsWith(EmpChangeMsg.msg003.toString())) {
//                    JOptionPane.showMessageDialog(null, str);
                    MsgUtil.showInfoMsg(str);
                    return;
                }
                str += r.getInfo(ic_str, "7", "1");
                str += r.getInfo(ic_str, "7", "2");
                str = StringUtil.toStringHex(str);
                String f_a0190 = str.substring(0, 12);
                f_a0190 = f_a0190.trim();
                f_a0190 = f_a0190.replace(" ", "").replace(" ", "");
                jtfSearch.setText(f_a0190);
                A01 f_a01 = (A01) CommUtil.fetchEntityBy("from A01 a join fetch a.deptCode d where a.a0190 = '" + f_a0190 + "'");
                if (f_a01 != null) {
                    if (!f_a01.getDeptCode().getDept_code().equals(cur_dept.getDept_code())) {
                        deptPersonPanel.locateDeptObj(f_a01.getDeptCode());
                        cur_dept = (DeptCode) deptPersonPanel.getCurDept();
                    }
                    if (!f_a01.getA0191().equals(deptPersonPanel.getPersonClassName())) {
                        deptPersonPanel.locateType(f_a01.getA0191());
                        cur_class = deptPersonPanel.getPersonClass();
                    }
                    initSelectNum(0);
                    String hql = " bpc.a01.a01_key = '" + f_a01.getA01_key() + "'";
                    refreshForTable(cur_dept, null, hql);
                } else {
//                    JOptionPane.showMessageDialog(null, "找不到IC卡对应人员");
                    MsgUtil.showInfoMsg(EmpChangeMsg.msg005);
                }
            }
        });
        btnUse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = btp_change_main.getAllSelectObjects();
                if (tabIndex != 1 || list.isEmpty()) {
                    return;
                }
                Object type_obj = jcbType.getSelectedItem();
                if (!(type_obj instanceof ChangeScheme)) {
//                    JOptionPane.showMessageDialog(null, "请选择具体变动类别");
                    MsgUtil.showInfoMsg(EmpChangeMsg.msg006);
                    return;
                }
                ChangeScheme cs = (ChangeScheme) type_obj;
                if (!cs.isCheck_flag()) {
//                    JOptionPane.showMessageDialog(null, "该变动方案不需要审批");
                    MsgUtil.showInfoMsg(EmpChangeMsg.msg007);
                    return;
                }
                log.info(e);
                String wfNoStr = "";
                String oldState = ((BasePersonChange) list.get(0)).getChg_state();
                int i = 0;
                for (Object obj : list) {
                    BasePersonChange bpc = (BasePersonChange) obj;
                    if (bpc.getChg_state().equals(oldState)) {
                        wfNoStr += ",'" + bpc.getPart_no() + "'";
                        i++;
                        if (i > 500) {
//                            JOptionPane.showMessageDialog(null, "单次审批最大允许500条");
                            MsgUtil.showInfoMsg(EmpChangeMsg.msg008);
                            return;
                        }
                    }
                }
                if (i == 0) {
                    return;
                }
                wfNoStr = wfNoStr.substring(1);
                List wfInstances = CommUtil.fetchEntities("from WfInstance wf where wf.part_no in (" + wfNoStr + ")");
                if (wfInstances == null) {
                    return;
                }
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                String val = jtfSearch.getText().trim().toUpperCase();
                if (val == null || val.equals("")) {
                    return;
                }
                initSelectNum(0);
                val = SysUtil.getQuickSearchText(val);
                String hql = " A01.pydm like '" + val + "' or A01.a0101 like '" + val + "' or A01.a0190 like '" + val + "' ";
                refreshForTable(cur_dept, null, hql);
            }
        };
        jtfSearch.addActionListener(al);
        btnQuickSearch.addActionListener(al);
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tabIndex = jtpMain.getSelectedIndex();
                log.info(e);
                if (tabIndex == 0) {
                    pnlChg.add(btp_change_main, BorderLayout.CENTER);
                    pnlChg.updateUI();
                } else if (tabIndex == 1) {
                    pnlUnChg.add(btp_change_main, BorderLayout.CENTER);
                    pnlUnChg.updateUI();
                } else {
                    pnlChgWait.add(btp_change_main, BorderLayout.CENTER);
                    pnlChgWait.updateUI();
                }
                list_change_type.remove("全部");
                if (tabIndex == 0) {
                    list_change_type.add(0, "全部");
                }
                jcbType.removeActionListener(type_listener);
                scheme_binding.unbind();
                scheme_binding.bind();
                jcbType.addActionListener(type_listener);
                if (curScheme != null) {
                    jcbType.setSelectedItem(curScheme);
                } else {
                    jcbType.setSelectedIndex(0);
                }
                setMainState();
                if (tabIndex == 0) {
                    select_num++;
                } else if (tabIndex == 1) {
                    select_num1++;
                } else {
                    select_num2++;
                }
            }
        });
        miFollow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                follow = !follow;
                refreshMain(null);
            }
        });
        miShowCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                showCancel = !showCancel;
                refreshMain(null);
            }
        });
        miShowRecovery.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                showRecovery = !showRecovery;
                refreshMain(null);
            }
        });
        miRecovery.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = btp_change_main.getCurrentRow();
                if (obj instanceof BasePersonChange) {
                    BasePersonChange bpc = (BasePersonChange) obj;
                    String hql = "select 1 from BasePersonChange bpc,(select max(order_no) as order_no from BasePersonChange bpc where bpc.a01_key='"
                            + bpc.getA01().getA01_key() + "') a where bpc.order_no=a.order_no and bpc.basePersonChange_key='" + bpc.getBasePersonChange_key() + "'";
                    if (CommUtil.selectSQL(hql).size() > 0) {
                        if (CommUtil.exists("from WfInstance wf where wf.part_no='" + bpc.getPart_no() + "')")) {
                            MsgUtil.showErrorMsg(EmpChangeMsg.msg009);
                            return;
                        }
                        if (MsgUtil.showNotConfirmDialog(EmpChangeMsg.msg010)) {
                            return;
                        }
                        ValidateSQLResult result = RSImpl.recoveryChange(bpc);
                        if (result.getResult() == 0) {
                            MsgUtil.showInfoMsg(EmpChangeMsg.msg011);
                            refreshMain(null);
                        } else {
                            MsgUtil.showHRSaveErrorMsg(result);
                        }
                    } else {
                        MsgUtil.showErrorMsg(EmpChangeMsg.msg012);
                    }
                }
            }
        });
        type_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                curScheme = null;
                if (jcbType.getSelectedItem() instanceof ChangeScheme) {
                    curScheme = (ChangeScheme) jcbType.getSelectedItem();
                }
                setMainState();
                refreshMain(null);
            }
        };
        jcbType.addActionListener(type_listener);
        deptPersonPanel.addPickPersonClassListner(new IPickPersonClassListener() {

            @Override
            public void pickPersonClass(Class personClass) {
                initSelectNum(0);
                cur_class = personClass;
                refreshChangeSchemeBind(cur_class, null);
                refreshMain(null);
            }
        });
        deptPersonPanel.addPickPersonListener(new IPickPersonListener() {

            @Override
            public void pickPerson(Object obj) {
                if (cur_person == obj) {
                    return;
                }
                cur_person = (A01) obj;
                if (cur_style == 0) {
                    return;
                }
                if (cur_person == null) {
                    return;
                }
                initSelectNum(0);
                refreshForPerson(cur_dept);
            }
        });
        deptPersonPanel.addPickStyleListner(new IPickStyleListner() {

            @Override
            public void pickStyle(int style) {
                cur_style = style;
                refreshMain(null);
            }
        });
        deptPersonPanel.addPickDeptListener(new IPickDeptListener() {

            @Override
            public void pickDept(Object dept) {
                cur_dept = (DeptCode) dept;
                if (cur_dept != null && cur_dept.getDept_full_name() != null) {
                    ContextManager.setMainFrameTitle(cur_dept.getDept_full_name());
                }
                refreshMain(null);
            }
        });
        jtpLog.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                log.info(e);
                fetchAppendix(null);
            }
        });
        main_change_listener = new ListSelectionListener() {

            Object cur_obj = null;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object obj = btp_change_main.getCurrentRow();
                if (cur_obj == obj) {
                    return;
                }
                cur_obj = obj;
                ContextManager.setStatusBar(btp_change_main.getObjects().size());
                if (cur_obj == null) {
                    return;
                }
                BasePersonChange mainPersonChange = (BasePersonChange) cur_obj;
                String content = "";
                if (mainPersonChange.getA01() == null || mainPersonChange.getA01().getDeptCode() == null || mainPersonChange.getA01().getDeptCode().getDept_full_name() == null) {
                    content = "";
                } else {
                    content = mainPersonChange.getA01().getDeptCode().getDept_full_name();
                }
                if (!content.equals("")) {
                    ContextManager.setMainFrameTitle(content);
                }
                if (jtpLog.getSelectedIndex() == 1) {
                    refreshInfo();
                } else if (follow) {
                    initSelectNum(2);
                    fetchAppendix(null);
                }
            }
        };
        btp_change_main.addListSelectionListener(main_change_listener);
        btp_change_main.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                change_order_sql = SysUtil.getSQLOrderString(showScheme, change_order_sql, all_fields);
                if (PersonContainer.getPersonContainer().isVisible()) {
                    return;
                }
                initSelectNum(1);
                refreshForTable(cur_dept, null, null);
            }
        });
        query_listener = new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                refreshMain(qs);
            }
        };
        btp_change_main.addPickQueryExListener(query_listener);
        log_table.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                appendix_order_sql = SysUtil.getSQLOrderString(showScheme, change_order_sql, log_table.getAll_fields());
                fetchAppendix(log_table.getCur_query_scheme());
            }
        });
        log_table.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchAppendix(qs);
            }
        });
        ComponentUtil.refreshJSplitPane(jSplitPane1, "EmpChangePanel.jSplitPane1");
        ComponentUtil.refreshJSplitPane(jSplitPane2, "EmpChangePanel.jSplitPane2");
        search_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                setMainState();
                refreshMain(null);
            }
        };
        btnSearch.addActionListener(search_listener);
        miShowCancel.addActionListener(search_listener);
        jcbDept.addActionListener(search_listener);
        jcbTime.addActionListener(search_listener);
        cur_dept = (DeptCode) deptPersonPanel.getCurDept();
        cur_class = deptPersonPanel.getPersonClass();
        refreshChangeSchemeBind(cur_class, null);
    }

    private void initToolBar() {
        ComponentUtil.setSize(btnSearch, 24, 24);
        ComponentUtil.setSize(jcbDept, 60, 24);
        ComponentUtil.setSize(jcbType, 160, 24);
        toolbar1.add(lbl);
        toolbar1.add(jcbDept);
        toolbar1.add(jcbTime);
        toolbar1.add(date_start);
        toolbar1.add(lblTo);
        toolbar1.add(date_end);
        toolbar1.add(btnSearch);
        toolbar.add(btnFind);
        date_end.setDate(new Date());
        date_start.setDate(DateUtil.getCurMonthFirstDay());
        pp.add(miPayDate);
        pp.add(miRecovery);
        pp.addSeparator();
        pp.add(miShowRecovery);
        pp.add(miShowCancel);
        pp.add(miFollow);
        btnTool.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pp.show(btnTool, 0, 25);
            }
        });
    }

    private void refreshMain(QueryScheme qs) {
        initSelectNum(0);
        if (PersonContainer.getPersonContainer().isVisible()) {
            refreshForTable(null, qs, null);
        } else {
            refreshForTable(cur_dept, qs, null);
        }
        setMainState();
    }

    private void changePayDate() {
        ChangePayDatePanel pnl = new ChangePayDatePanel(btp_change_main, scheme_keys);
        pnl.addIPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                btp_change_main.updateUI();
            }
        });
        ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnTool), pnl, true, EmpChangeMsg.ttl001);
    }

    /**
     * 根据type值来初始化计数器
     * @param type:type!=2时初始化pnlMain的计数器；type!=1时初始化pnlLog的计数器
     */
    private void initSelectNum(int type) {
        if (type != 2) {
            select_num = 0;
            select_num1 = 0;
            select_num2 = 0;
        }
        if (type != 1) {
            select_num3 = 0;
        }
    }

    private void refreshForPerson(DeptCode dept) {
        if (cur_person == null) {
            return;
        }
        String hql = "select BasePersonChange.basePersonChange_key " + buildMainSeq(dept, null, "A01.a01_key='" + cur_person.getA01_key() + "'");
        hql = DbUtil.tranSQL(hql, "rule");
        btp_change_main.setObjects(CommUtil.selectSQL(hql));
    }

    private void refreshForTable(DeptCode dept, QueryScheme qs, String s_where) {
        if (dept == null && !PersonContainer.getPersonContainer().isVisible()) {
            return;
        }
        if ((select_num > 0 && tabIndex == 0) || (select_num1 > 0 && tabIndex == 1) || (select_num2 > 0 && tabIndex == 2)) {
            setMainData();
            return;
        }
        String hql = buildMainSeq(dept, qs, s_where);
        if (cur_style == 0) {
            PublicUtil.getProps_value().setProperty(BasePersonChange.class.getName(), "from BasePersonChange bpc join fetch bpc.a01  join fetch bpc.a01.deptCode where bpc.basePersonChange_key in");
            hql = DbUtil.tranSQL("select BasePersonChange.basePersonChange_key " + hql, "rule");
            List list = CommUtil.selectSQL(hql);
            if (tabIndex == 0) {
                change_data = list;
            } else if (tabIndex == 1) {
                unchange_data = list;
            } else {
                dochange_data = list;
            }
            setMainData();
        } else {
            hql = DbUtil.tranSQL("select A01.a01_key " + hql, "rule");
            PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 bp join fetch bp.deptCode where bp.a01_key in");
            List<String> data = (List<String>) CommUtil.selectSQL(hql);
            deptPersonPanel.setList_person(data);
        }
        if (!follow) {
            fetchAppendix(null);
        }
    }

    private void setMainData() {
        if (tabIndex == 0) {
            btp_change_main.setObjects(change_data);
        } else if (tabIndex == 1) {
            btp_change_main.setObjects(unchange_data);
        } else {
            btp_change_main.setObjects(dochange_data);
        }
    }

    private String buildMainSeq(DeptCode dept, QueryScheme qs, String s_where) {
        Object scheme_obj = jcbType.getSelectedItem();
        ChangeScheme cur_scheme = null;
        String tableName = "BasePersonChange";
        String hql = " from BasePersonChange,A01,DeptCode  where BasePersonChange.a01_key=A01.a01_key and A01.deptCode_key=DeptCode.deptCode_key ";
        if (scheme_obj instanceof ChangeScheme) {
            cur_scheme = (ChangeScheme) scheme_obj;
            tableName = "PersonChange_" + cur_scheme.getChangeScheme_no();
            hql = hql + " and BasePersonChange.changescheme_key='" + cur_scheme.getChangeScheme_key() + "'";
        } else {
            if (UserContext.isSA) {
                hql += " and BasePersonChange.changescheme_key<>'EmpScheme_Add' and BasePersonChange.changescheme_key<>'EmpScheme_Del'";
            } else {
                String scheme_key = "";
                for (Object obj : list_change_type) {
                    if (obj instanceof ChangeScheme) {
                        String key = ((ChangeScheme) obj).getChangeScheme_key();
                        if (key.equals("EmpScheme_Add") || key.equals("EmpScheme_Del")) {
                            continue;
                        }
                        scheme_key += ",'" + ((ChangeScheme) obj).getChangeScheme_key() + "'";
                    }
                }
                if (scheme_key.equals("")) {
                    hql += " and 1=0";
                } else {
                    hql += " and BasePersonChange.changescheme_key in (" + scheme_key.substring(1) + ")";
                }
            }
        }
        if (qs != null) {
            hql += " and BasePersonChange.basepersonchange_key in(" + qs.buildSql() + ")";
        }
        if (PersonContainer.isShow_flag()) {
            String s_container = PersonContainer.getA01KeyStr();
            if (!s_container.equals("'-1'")) {
                hql += " and A01.a01_key in(" + s_container + ")";
            }
        } else {
            if (cur_class != null) {
                hql += " and (" + UserContext.getPerson_class_right_str(cur_class, "A01") + ")";
            }
            if (dept != null) {
                if (jcbDept.isEnabled()) {
                    String type = jcbDept.getSelectedItem().toString();
                    hql += " and (1=0";
                    if (type.equals("调入") || type.equals("所有")) {
                        hql += " or exists(select 1 from " + tableName + " b,DeptCode d where b.new_deptCode_key=d.deptCode_key and b.basePersonChange_key=BasePersonChange.basepersonchange_key "
                                + " and d.dept_code like '" + dept.getDept_code() + "%' and (" + UserContext.getDept_right_rea_str("d") + "))";
                    }
                    if (type.equals("调出") || type.equals("所有")) {
                        hql += " or exists(select 1 from " + tableName + " b,DeptCode d where b.old_deptCode_key=d.deptCode_key and b.basePersonChange_key=BasePersonChange.basepersonchange_key "
                                + " and d.dept_code like '" + dept.getDept_code() + "%' and (" + UserContext.getDept_right_rea_str("d") + "))";
                    }
                    hql += ")";
                } else {
                    hql += " and DeptCode.dept_code like '" + dept.getDept_code() + "%' and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
                }
            }
        }
        if (tabIndex == 0) {
            hql = hql + " and BasePersonChange.chg_state='审批通过'";
            if (showRecovery) {
                hql += " and exists(select 1 from (select a01_key,max(order_no) as order_no from BasePersonChange group by a01_key) a where a.a01_key=BasePersonChange.a01_key and a.order_no=BasePersonChange.order_no) ";
            }
        } else if (tabIndex == 1) {
            hql = hql + " and BasePersonChange.chg_state<>'审批通过' and BasePersonChange.chg_state<>'撤销'";
            if (!UserContext.isSA) {
                hql += " and exists(select 1 from WfStepPerson wsp where wsp.a01_key='" + UserContext.person_key + "' "
                        + " and wsp.wf_no=BasePersonChange.order_no and wsp.wf_state=BasePersonChange.chg_state)";
            }
        } else {
            hql = hql + " and BasePersonChange.chg_state<>'审批通过' and BasePersonChange.chg_state<>'撤销'";
            if (!UserContext.isSA) {
                hql += " and exists(select 1 from WfStepPerson wl where wl.a01_key='" + UserContext.person_key + "' and wl.wf_no=BasePersonChange.order_no)";
                
            }
        }
        if (jcbTime.isSelected()) {
            Date s_date = date_start.getDate();
            Date e_date = date_end.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(e_date);
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            hql += " and BasePersonChange.action_date>=" + DateUtil.toStringForQuery(s_date) + " and BasePersonChange.action_date<=" + DateUtil.toStringForQuery(c.getTime());
        }
        if (s_where != null) {
            hql += " and (" + s_where + ")";
        }
        hql = UserContext.getEntityRightSQL("A01", hql, "A01");
        change_main_sql = "select BasePersonChange.basePersonChange_key " + hql;
        hql += " order by " + change_order_sql;
        return hql;
    }
    private void fetchAppendix(QueryScheme qs) {
        String change_key = ((BasePersonChange) btp_change_main.getCurrentRow()).getBasePersonChange_key();
        if (jtpLog.getSelectedIndex() == 0) {
            log_table.setCur_query_scheme(qs);
            if (change_key == null) {
                log_table.deleteAllRows();
                return;
            }
            if (select_num3 > 0) {
                return;
            }
            String hql = "select a01Chg_key from A01Chg,BasePersonChange,A01,DeptCode where A01Chg.basePersonChange_key=BasePersonChange.basePersonChange_key and BasePersonChange.a01_key=A01.a01_key and A01.deptCode_key=DeptCode.deptCode_key";
            if (follow) {
                hql += " and BasePersonChange.basePersonChange_key='" + change_key + "'";
            } else {
                hql += " and BasePersonChange.basePersonChange_key in (" + change_main_sql + ")";
            }
            if (qs != null) {
                hql += " and A01Chg.a01Chg_key in(" + qs.buildSql() + ")";
            }
            hql = UserContext.getEntityRightSQL("A01", hql, "A01");
            hql += " order by " + appendix_order_sql;
            PublicUtil.getProps_value().setProperty(A01Chg.class.getName(), "from A01Chg rc join fetch rc.basePersonChange bpc join fetch bpc.a01 a01 join fetch a01.deptCode where rc.a01Chg_key in ");
            log_table.setObjects(CommUtil.selectSQL(DbUtil.tranSQL(hql, "rule")));
        } else if (jtpLog.getSelectedIndex() == 1) {
            refreshInfo();
        } 
    }

    private void refreshInfo() {
        BasePersonChange bpc = (BasePersonChange) btp_change_main.getCurrentRow();
        EmpUtil.refreshUIByChange(bpc, beanPanel, scheme_keys.get(bpc.getChangescheme_key()));
    }

    /**
     * 该方法用于根据人员类别刷新调配模板下拉框
     * @param personClass：人员类别Class
     */
    private void refreshChangeSchemeBind(Class personClass, String changeSchemeName) {
        list_change_type.clear();
        if (tabIndex == 0) {
            list_change_type.add(list_all_type.get(0));
        }
        int len = list_all_type.size();
        if (len > 1) {
            String new_class_name = personClass.getSimpleName();
            for (int i = 1; i < len; i++) {
                ChangeScheme cs = (ChangeScheme) list_all_type.get(i);
                boolean include_a0191 = false;
                if (!new_class_name.equals("A01")) {
                    for (ChangeItem ci : cs.getChangeItems()) {
                        if ("a0191".equals(ci.getFieldName())) {
                            include_a0191 = true;
                            break;
                        }
                    }
                }
                if (include_a0191) {
                    if (cs.getNewPersonClassName() == null || cs.getNewPersonClassName().trim().equals("")) {
                        continue;
                    }
                    if (new_class_name.equals(cs.getNewPersonClassName())) {
                        continue;
                    }
                    if (cs.getOldPersonClassName() == null || cs.getOldPersonClassName().trim().equals("")) {
                        continue;
                    }
                    String[] old_classes = cs.getOldPersonClassName().split("\\;");
                    boolean include_this_a0191 = false;
                    for (String old : old_classes) {
                        if (new_class_name.equals(old)) {
                            include_this_a0191 = true;
                            break;
                        }
                    }
                    if (!include_this_a0191) {
                        continue;
                    }
                }
                list_change_type.add(cs);
            }
        }
        jcbType.removeActionListener(type_listener);
        scheme_binding.unbind();
        scheme_binding.bind();
        Object select_obj = null;
        if (changeSchemeName != null) {
            for (Object obj : list_change_type) {
                if (obj instanceof ChangeScheme) {
                    ChangeScheme cs = (ChangeScheme) obj;
                    if (("org.jhrcore.entity.PersonChange_" + cs.getChangeScheme_no()).equals(changeSchemeName)) {
                        select_obj = obj;
                        curScheme = cs;
                        break;
                    }
                }
            }
        }
        if (select_obj != null) {
            jcbType.setSelectedItem(select_obj);
        }
        jcbType.addActionListener(type_listener);
        jcbType.updateUI();
    }

    private void setMainState() {
        date_start.setEnabled(jcbTime.isSelected());
        date_end.setEnabled(jcbTime.isSelected());
        btnSearch.setEnabled(jcbTime.isSelected());
        btnUse.setEnabled(tabIndex == 1 && UserContext.hasFunctionRight(module_code + ".btnUse"));
        btnCancel.setEnabled(tabIndex == 2 && UserContext.hasFunctionRight(module_code + ".btnCancel"));
        Object obj = jcbType.getSelectedItem();
        if (obj instanceof ChangeScheme) {
            ChangeScheme cs = (ChangeScheme) jcbType.getSelectedItem();
            jcbDept.setEnabled(tabIndex > 0 && cs.contains("deptCode"));
        } else {
            jcbDept.setEnabled(false);
        }
        miRecovery.setEnabled(tabIndex == 0 && UserContext.hasFunctionRight(module_code + ".miRecovery"));
        ComponentUtil.setBooleanIcon(miShowCancel, showCancel);
        ComponentUtil.setBooleanIcon(miShowRecovery, showRecovery);
        ComponentUtil.setBooleanIcon(miFollow, follow);
    }

    @Override
    public void setFunctionRight() {
        setMainState();
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
        deptPersonPanel.getDeptPanel().updateUIView();
        if (tabIndex == 0) {
            ContextManager.setStatusBar(btp_change_main.getObjects().size());
        } else if (ftable_work_flow != null) {
            ContextManager.setStatusBar(ftable_work_flow.getObjects().size());
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }

    @Override
    public void initForWait(List data, Object row) {
        Object[] objs = (Object[]) row;
        deptPersonPanel.locateRoot();
        cur_dept = (DeptCode) deptPersonPanel.getCurDept();
        cur_class = deptPersonPanel.getPersonClass();
        jcbTime.removeActionListener(search_listener);
        jcbTime.setSelected(false);
        jcbTime.addActionListener(search_listener);
        refreshChangeSchemeBind(cur_class, objs[3].toString());
        jtpMain.setSelectedIndex(1);
    }
}
