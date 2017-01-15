/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EmpRegisterPanel.java
 *
 * Created on 2010-7-3, 17:25:13
 */
package org.jhrcore.client.personnel.register;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.mutil.ReportUtil;
import org.jhrcore.client.personnel.EmpNoRulePanel;
import org.jhrcore.client.personnel.IPickPersonImportListener;
import org.jhrcore.client.personnel.ImportA01ExcelPanel;
import org.jhrcore.ui.DeptPersonPanel;
import org.jhrcore.ui.listener.IPickPersonClassListener;
import org.jhrcore.client.personnel.comm.IPickRegisterListener;
import org.jhrcore.client.personnel.comm.PersonContainer;
import org.jhrcore.comm.FieldTrigerManager;
import org.jhrcore.ui.WizardDialog;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.A01Chg;
import org.jhrcore.entity.BasePersonChange;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpRegisterMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickDeptListener;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.ui.task.IWaitWork;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class EmpRegisterPanel extends javax.swing.JPanel implements IModulePanel, IWaitWork {

    private DeptPersonPanel deptPersonPanel;
    private DeptCode curren_dept;//当前部门
    private FTable ftable_main;//人员主表信息
    private FTable ftable_un_change;
    private Class<?> cur_person_class = null;
    private List<TempFieldInfo> person_all_fields = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
    private List types = new ArrayList();
    private String person_order_sql = "DeptCode.dept_code,A01.a0190";
    private String change_order_sql = "A01.a0190";
    private boolean canStartWork = false;
    private IPickFieldOrderListener person_order_listener;
    private IPickQueryExListener person_query_listener;
    private JPopupMenu pp = new JPopupMenu();
    private JMenu ppSystem = new JMenu("系统设置");
    private JMenuItem menuRegister = new JMenuItem("入职登记设置");
    private JMenuItem mi_personCodeRule = new JMenuItem("人员编号规则设置");
    private JMenuItem miTimeSet = new JMenuItem("入职时间设置");
    private JButton btnTool = new JButton("工具");
    private JButton btnReport = new JButton("常用报表");
    private JMenuItem miImport = new JMenuItem("导入Excel");
    private JMenuItem miExport = new JMenuItem("导出Excel");
    private int tabIndex = 0;
    private boolean need_check = true;
    private ChangeScheme add_scheme;
    public static final String module_code = "EmpRegister";
    private HrLog log = new HrLog(module_code);
    private JComboBoxBinding type_binding = null;
    private ActionListener al_type = null;

    /** Creates new form EmpRegisterPanel */
    public EmpRegisterPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        initToolBar();
        deptPersonPanel = new DeptPersonPanel(A01.class, UserContext.getDepts(false));
        pnlLeft.add(deptPersonPanel, BorderLayout.CENTER);
        add_scheme = EmpUtil.getChangeScheme("EmpScheme_Add");
        type_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ, types, jcbType);
        type_binding.bind();
    }

    private void setupEvents() {
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String type = jcbType.getSelectedItem().toString();
                if (!type.equals(EmpRegisterMsg.ttlCommit.toString())) {
                    MsgUtil.showErrorMsg(EmpRegisterMsg.msg001);
                    return;
                }
                log.info(e);
                List<String> delKeys = ftable_main.getSelectKeys();
                if (delKeys.isEmpty()) {
                    return;
                }
                if (MsgUtil.showNotConfirmDialog(EmpRegisterMsg.msg002)) {
                    return;
                }
                ValidateSQLResult validateSQLResult = RSImpl.cancelRegisterFlow(delKeys);
                if (validateSQLResult.getResult() == 0) {
                    ftable_main.deleteSelectedRows();
                    log.info("取消审核成功");
                    MsgUtil.showInfoMsg(CommMsg.ACTIONSUCCESS_MESSAGE);
                } else {
                    log.info("取消审核失败，原因" + validateSQLResult.getMsg());
                    MsgUtil.showHRSaveErrorMsg(validateSQLResult);
                }
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String val = jtfSearch.getText().trim().toUpperCase();
                if (val == null || val.equals("")) {
                    return;
                }
                log.info(e);
                val = SysUtil.getQuickSearchText(val);
                String hql = " upper(A01.pydm) like '" + val + "' or upper(A01.a0101) like '" + val + "' or upper(A01.a0190) like '" + val + "' ";
                fetchMainData(null, hql);
            }
        };
        jtfSearch.addActionListener(al);
        btnQuickSearch.addActionListener(al);
        person_order_listener = new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                person_order_sql = SysUtil.getSQLOrderString(showScheme, person_order_sql, person_all_fields);
                fetchMainData(ftable_main.getCur_query_scheme(), null);
            }
        };
        deptPersonPanel.addPickPersonClassListner(new IPickPersonClassListener() {

            @Override
            public void pickPersonClass(Class personClass) {
                initComp(tabIndex, personClass, need_check);
                fetchMainData(null, null);
            }
        });
        deptPersonPanel.addPickDeptListener(new IPickDeptListener() {

            @Override
            public void pickDept(Object dept) {
                if (dept instanceof DeptCode) {
                    curren_dept = (DeptCode) dept;
                } else {
                    curren_dept = (DeptCode) ((DefaultMutableTreeNode) ((DefaultMutableTreeNode) deptPersonPanel.getDeptPanel().getDeptTree().getLastSelectedPathComponent()).getParent()).getUserObject();
                }
                fetchMainData(null, null);
            }
        });
        al_type = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                fetchMainData(null, null);
            }
        };
        jcbType.addActionListener(al_type);
        person_query_listener = new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchMainData(qs, null);
            }
        };
        deptPersonPanel.addPickQueryExListener(person_query_listener);
        btnRegister.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                log.info(arg0);
                register();
            }
        });
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                editPerson();
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (tabIndex == 0) {
                    String type = jcbType.getSelectedItem().toString();
                    if (need_check && !type.equals(EmpRegisterMsg.ttlUnCommit.toString())) {
                        MsgUtil.showErrorMsg(EmpRegisterMsg.msg003);
                        return;
                    }
                    List<String> a01_keys = ftable_main.getSelectKeys();
                    if (a01_keys.isEmpty()) {
                        return;
                    }
                    if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
                        return;
                    }
                    ValidateSQLResult validateSQLResult = RSImpl.delNewPerson(a01_keys);
                    if (validateSQLResult.getResult() != 0) {
                        MsgUtil.showHRDelErrorMsg(validateSQLResult);
                    } else {
                        fetchMainData(null, null);
                        log.info("删除操作成功#结果提示:" + validateSQLResult.getResult());
                    }
                }
            }
        });
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tabIndex = jtpMain.getSelectedIndex();
                log.info(e);
                initComp(tabIndex, cur_person_class, need_check);
                bindType();
            }
        });
        btnIn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                checkPerson();
            }
        });
        btnUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                checkPerson();
            }
        });
        btnProcess.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                List list = ftable_un_change.getSelectObjects();
                if (list.isEmpty()) {
                    return;
                } else if (list.size() > 800) {
                    MsgUtil.showErrorMsg(CommMsg.MAXROW_MESSAGE);
                    return;
                }
                list = ftable_un_change.getAllSelectObjects();
                String state = ((BasePersonChange) list.get(0)).getChg_state();
                List<String> data = new ArrayList<String>();
                for (Object obj : list) {
                    BasePersonChange bpc = (BasePersonChange) obj;
                    if (bpc.getChg_state().equals(state) && !data.contains(bpc.getOrder_no())) {
                        data.add(bpc.getOrder_no());
                    }
                }
            }
        });
        ChangeListener cl = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refreshBottomData();
            }
        };
        jtpBottom.addChangeListener(cl);
        jtpBottom1.addChangeListener(cl);
        curren_dept = (DeptCode) deptPersonPanel.getCurDept();
        initComp(tabIndex, deptPersonPanel.getPersonClass(), "1".equals(initCheck_flag().getSysparameter_value()));
        bindType();
    }

    private void bindType() {
        types.clear();
        if (tabIndex == 0) {
            if (need_check) {
                types.add(EmpRegisterMsg.ttlUnCommit);
                types.add(EmpRegisterMsg.ttlCommit);
                types.add(EmpRegisterMsg.ttlStore);
            } else {
                types.add(EmpRegisterMsg.ttlUnStore);
                types.add(EmpRegisterMsg.ttlStore);
            }
        } else if (tabIndex == 1) {
            types.add(EmpRegisterMsg.ttlProcess);
        } else {
            types.add(EmpRegisterMsg.ttlProcessed);
            types.add(EmpRegisterMsg.ttlCompleted);
            types.add(EmpRegisterMsg.ttlRevoked);
        }
        jcbType.removeActionListener(al_type);
        type_binding.unbind();
        type_binding.bind();
        jcbType.addActionListener(al_type);
        jcbType.setSelectedIndex(0);
    }

    private void refreshBottomData() {
        Object cur_obj = ftable_un_change.getCurrentRow();
        pnlChart.removeAll();
        pnlChart1.removeAll();
        if (cur_obj == null || ((BasePersonChange) cur_obj).getBasePersonChange_key() == null) {
            pnlChart.updateUI();
            pnlChart1.updateUI();
            return;
        }
    }

    /**
     * 根据相关参数改变当前界面显示及初始化相关界面
     * @param tabIndex：当前卡片索引
     * @param personClass：人员类别
     * @param need_check：是否需要审批
     */
    private void initComp(int tabIndex, Class personClass, boolean need_check) {
        if (this.need_check != need_check) {
            this.need_check = need_check;
            if (need_check) {
                if (jtpMain.getComponents().length == 1) {
                    jtpMain.add(EmpRegisterMsg.ttlProcessEvents.toString(), pnlUnChange);
                    jtpMain.add(EmpRegisterMsg.ttlProcessedEvents.toString(), pnlProcess);
                    jtpMain.updateUI();
                }
            } else {
                jtpMain.remove(2);
                jtpMain.remove(1);
                jtpMain.setSelectedIndex(0);
                jtpMain.updateUI();
            }
        }
        if (tabIndex == 0) {
            if (cur_person_class != personClass) {
                cur_person_class = personClass;
                pnlMain.removeAll();
                ftable_main = new FTable(cur_person_class, true, true, false, "EmpRegister") {

                    @Override
                    public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                        String type = jcbType.getSelectedItem().toString();
                        if (type.equals(EmpRegisterMsg.ttlCommit.toString())) {
                            return new Color(238, 238, 238);
                        } else if (type.equals("所有人员")) {
                            if (row_obj instanceof A01) {
                                if (((A01) row_obj).getA0193() == 0) {
                                    return new Color(238, 238, 238);
                                }
                            }
                        }
                        return null;
                    }
                };
                ftable_main.removeSumAndReplaceItem();
                ftable_main.setRight_allow_flag(true);
                initPersonFields();
                ftable_main.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                ftable_main.addPickFieldOrderListener(person_order_listener);
                ftable_main.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() >= 2) {
                            editPerson();
                        }
                    }
                });
                ftable_main.addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        ContextManager.setStatusBar(ftable_main.getObjects().size());
                    }
                });
                ftable_main.addPickQueryExListener(person_query_listener);
                pnlMain.add(ftable_main, BorderLayout.CENTER);
                pnlMain.updateUI();
            }
        } else {
            if (need_check) {
                if (ftable_un_change == null) {
                    EntityBuilder.buildInfo(DeptCode.class, all_fields, default_fields, "a01.deptCode");
                    EntityBuilder.buildInfo(A01.class, all_fields, default_fields, "a01");
                    EntityBuilder.buildInfo(BasePersonChange.class, all_fields, default_fields);
                    ftable_un_change = new FTable(BasePersonChange.class, true, true, false, "EmpChange2") {

                        @Override
                        public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                            if (row_obj instanceof BasePersonChange) {
                                if (!((BasePersonChange) row_obj).getChg_state().equals("开始")) {
                                    return new Color(238, 238, 238);
                                }
                            }
                            return null;
                        }
                    };
                    ftable_un_change.setAll_fields(all_fields, default_fields, "EmpChange2");
                    ftable_un_change.setRight_allow_flag(true);
                    ftable_un_change.removeSumAndReplaceItem();
                    ftable_un_change.addPickFieldOrderListener(new IPickFieldOrderListener() {

                        @Override
                        public void pickOrder(ShowScheme showScheme) {
                            change_order_sql = SysUtil.getSQLOrderString(showScheme, change_order_sql, all_fields);
                            fetchMainData(null, null);
                        }
                    });
                    ftable_un_change.addListSelectionListener(new ListSelectionListener() {

                        Object cur_obj;

                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            if (cur_obj == ftable_un_change.getCurrentRow()) {
                                return;
                            }
                            cur_obj = ftable_un_change.getCurrentRow();
                            ContextManager.setStatusBar(ftable_un_change.getObjects().size());
                            refreshBottomData();
                        }
                    });
                    ftable_un_change.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() >= 2) {
                                editPerson();
                            }
                        }
                    });
                    change_order_sql = SysUtil.getSQLOrderString(ftable_un_change.getCurOrderScheme(), change_order_sql, all_fields);
                }
            }
            if (tabIndex == 1) {
                pnlTop.removeAll();
                pnlTop.add(ftable_un_change, BorderLayout.CENTER);
                pnlTop.updateUI();
            } else {
                pnlTop1.removeAll();
                pnlTop1.add(ftable_un_change, BorderLayout.CENTER);
                pnlTop1.updateUI();
            }
        }
        setMainState();
    }

    private SysParameter initCheck_flag() {
        SysParameter sp = (SysParameter) CommUtil.fetchEntityBy("from SysParameter sp where sp.sysParameter_key='Register.check_flag'");
        if (sp == null) {
            sp = new SysParameter();
            sp.setSysParameter_key("Register.check_flag");
            sp.setSysparameter_value(CommUtil.selectSQL("select check_flag from ChangeScheme where changeScheme_key='EmpScheme_Add'").get(0).toString());
            sp.setSysparameter_code("Register.check_flag");
            sp.setSysparameter_roleid(UserContext.person_code);
            CommUtil.saveOrUpdate(sp);
        }
        return sp;
    }

    private void checkPerson() {
        String type = jcbType.getSelectedItem().toString();
        Object msg = null;
        if (need_check && !type.equals(EmpRegisterMsg.ttlUnCommit.toString())) {
            msg = EmpRegisterMsg.msg004;
        } else if (!need_check && !type.equals(EmpRegisterMsg.ttlUnStore.toString())) {
            msg = EmpRegisterMsg.msg005;
        }
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return;
        }
        List<String> list = ftable_main.getSelectKeys();
        if (list.isEmpty()) {
            return;
        }
        if (!EmpUtil.canRegister("submit")) {
            return;
        }
        ValidateSQLResult result1 = DeptImpl.checkWeaveForReg(ftable_main.getAllObjects());
        if (result1.getResult() == 1) {
            MsgUtil.showHRMsg(result1, EmpRegisterMsg.msg006);
            return;
        } else if (result1.getResult() == 2) {
            if (MsgUtil.showNotConfirmDialog(result1.getMsg() + EmpRegisterMsg.msg007)) {
                return;
            }
        }
        if (!need_check) {
            ValidateSQLResult result = RSImpl.addPersonForNoCheck(list, EmpUtil.getCommUserLog());
            if (result.getResult() == 0) {
                fetchMainData(null, null);
                MsgUtil.showInfoMsg(CommMsg.COMMITSUCCESS);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
            return;
        }
        if (!canStartWork) {
            MsgUtil.showErrorMsg(CommMsg.NOWORKFLOW);
            return;
        }
        if (MsgUtil.showNotConfirmDialog(EmpRegisterMsg.msg008)) {
            return;
        }
        List list2 = ftable_main.getAllSelectObjects();
        Class c = null;
        try {
            c = Class.forName("org.jhrcore.entity.PersonChange_" + add_scheme.getChangeScheme_no());
        } catch (ClassNotFoundException ex) {
            return;
        }
        
        List chgs = new ArrayList();
        Date date = CommUtil.getServerDate();
        for (Object obj : list2) {
            A01 a01 = (A01) obj;
            BasePersonChange bpc = (BasePersonChange) UtilTool.createUIDEntity(c);
            bpc.setAction_date(date);
            bpc.setApply_date(date);
            bpc.setChangescheme_key("EmpScheme_Add");
            bpc.setChg_user(UserContext.person_name);
            bpc.setA01(a01);
            bpc.setChg_type("新增");
            bpc.setReason("入职新增");
            bpc.setChg_state("开始");
            chgs.add(bpc);
            A01Chg a01Chg = new A01Chg();
            a01Chg.setA01Chg_key(UtilTool.getUID());
            a01Chg.setChgfield("人员状态标识");
            a01Chg.setBasePersonChange(bpc);
            a01Chg.setA_dept_code(a01.getDeptCode().getDept_code());
            a01Chg.setA_full_name(a01.getDeptCode().getDept_full_name());
            a01Chg.setBeforestate("2");
            a01Chg.setAfterstate("0");
            Set<A01Chg> a01Chgs = new HashSet<A01Chg>();
            a01Chgs.add(a01Chg);
            bpc.setA01Chgs(a01Chgs);
            chgs.add(a01Chg);
        }
        //触发校验规则
        if (!ftable_main.getSelectObjects().isEmpty()) {
            List l = new ArrayList();
            l.addAll(ftable_main.getAllSelectObjects());
            if (!FieldTrigerManager.getFieldTrigerManager().checkObjs(l)) {
                return;
            }
            ValidateSQLResult result = RSImpl.UpdateA01s(l);
            if (result.getResult() != 0) {
                MsgUtil.showHRSaveErrorMsg(result);
                return;
            }
        }//触发校验规则
        String uid = new UID().toString();
//        
//        List<List> resultList = new ArrayList<List>();
//        resultList.add(chgs);
//        resultList.add(new ArrayList());
//        resultList.add(new ArrayList());
//        ValidateSQLResult result = RSImpl.createPersonChange(resultList, add_scheme.getChangeScheme_key(), false, wfd, wfl, conn, EmpUtil.getCommUserLog());
//        if (result.getResult() == 0) {
//            MsgUtil.showInfoMsg(CommMsg.COMMITSUCCESS);
//            fetchMainData(null, null);
//        } else {
//            MsgUtil.showHRSaveErrorMsg(result);
//        }
    }

    private void initToolBar() {
        toolbar.addSeparator();
        toolbar.add(btnReport);
        toolbar.addSeparator();
        toolbar.add(btnTool);
        ppSystem.add(menuRegister);
        ppSystem.add(mi_personCodeRule);
        ppSystem.add(miTimeSet);
        pp.add(ppSystem);
        pp.addSeparator();
        pp.add(miImport);
        pp.addSeparator();
        pp.add(miExport);
        menuRegister.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (WizardDialog.showWizard(new RegisterDesignModel())) {
                    initComp(tabIndex, deptPersonPanel.getPersonClass(), "1".equals(initCheck_flag().getSysparameter_value()));
                    bindType();
                }
            }
        });
        mi_personCodeRule.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                EmpNoRulePanel pnlNoRule = new EmpNoRulePanel("PersonNo",true);
                ModelFrame.showModel(ContextManager.getMainFrame(), pnlNoRule, true, EmpRegisterMsg.ttl001, 800, 600);
            }
        });
        miTimeSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterTimeDlg dlg = new RegisterTimeDlg(ContextManager.getMainFrame());
                ContextManager.locateOnMainScreenCenter(dlg);
                dlg.setVisible(true);
            }
        });
        btnTool.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pp.show(btnTool, 0, 25);
            }
        });
        btnReport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = null;
                if (PersonContainer.getPersonContainer().isVisible()) {
                    list = PersonContainer.getPersonContainer().getAllPersons();
                } else {
                    list = getMainFTable().getAllSelectObjects();
                }
                ReportUtil.buildCommReportMenu(btnReport, module_code, EmpUtil.getReportParaMap(list));
            }
        });
        miExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getMainFTable().exportData();
            }
        });
        miImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (!EmpUtil.canRegister("register")) {
                    return;
                }
                ImportA01ExcelPanel panel = new ImportA01ExcelPanel(person_all_fields, person_all_fields, module_code + ".miImport");
                panel.AddPickPersonImportListener(new IPickPersonImportListener() {

                    @Override
                    public void refreshData() {
                        fetchMainData(null, null);
                    }

                    @Override
                    public void importPersons() {
                    }
                });
                ModelFrame.showModel(ContextManager.getMainFrame(), panel, true, EmpRegisterMsg.ttl002, 700, 600);
            }
        });
    }

    /**
     * 初始化人员表显示字段
     */
    private void initPersonFields() {
        person_all_fields.clear();
        List<TempFieldInfo> person_default_fields = new ArrayList<TempFieldInfo>();
        EntityBuilder.buildInfo(DeptCode.class, person_all_fields, person_default_fields, "deptCode");
        EntityBuilder.buildInfo(A01.class, person_all_fields, person_default_fields, "");
        ftable_main.setAll_fields(person_all_fields, person_default_fields, "EmpRegister");
    }

    /**
     * 用于制造人员的Sql语句
     * @param qs：查询方案，如果为空，则表示是界面内响应，非查询结果
     * @return：匹配的sql语句
     */
    private String buildPersonSeq(QueryScheme qs, String s_where) {
        ftable_main.setCur_query_scheme(qs);
        String hql = "from A01 A01,DeptCode where A01.DeptCode_key=DeptCode.DeptCode_key and DeptCode.dept_code like '" + curren_dept.getDept_code() + "%'";
        String type = jcbType.getSelectedItem().toString();
        if (need_check) {
            hql += "";
            if (type.equals("未提交审核")) {
                //<>撤销 以便于撤销的人员可以再次发起审核
                hql += " and (A01.a0193=2 or A01.a0193=3) and not exists(select 1 from BasePersonChange bpc where bpc.a01_key=A01.a01_key and bpc.changescheme_key='EmpScheme_Add' and bpc.chg_state<>'撤销')";
            } else if (type.equals("已提交审核")) {
                hql += " and (A01.a0193=2 or A01.a0193=3) and exists(select 1 from BasePersonChange bpc where bpc.a01_key=A01.a01_key and bpc.changescheme_key='EmpScheme_Add'"
                        + " and bpc.chg_state<>'撤销' )";
            } else if (type.equals("已入库")) {
                hql += "  and A01.a0193=0 and exists(select 1 from BasePersonChange bpc where bpc.a01_key=A01.a01_key and bpc.changescheme_key='EmpScheme_Add')";
            }
        } else {
            hql += " and exists(select 1 from RyChgLog rcl where A01.a01_key=rcl.a01_key and (rcl.chg_type='入职新增' or rcl.chg_type='招聘入职')) ";
            if (type.equals("未入库")) {
                hql += " and (A01.a0193=2 or A01.a0193=3)";
            } else if (type.equals("已入库")) {
                hql += " and A01.a0193=0";
            }
        }
        if (qs != null) {
            hql += " and A01.a01_key in(" + qs.buildSql() + ")";
        }
        if (s_where != null) {
            hql += " and (" + s_where + ")";
        }
        hql += " and (" + UserContext.getPerson_class_right_str(cur_person_class, "A01") + ")";
        hql += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
        hql = UserContext.getEntityRightSQL("A01", hql, "A01");
        hql += " order by " + person_order_sql;
        return hql;
    }

    private void fetchMainData(QueryScheme qs, String s_where) {
        if (curren_dept == null) {
            return;
        }
        if (cur_person_class == null) {
            return;
        }
        if (tabIndex == 0) {
            String hql = buildPersonSeq(qs, s_where);
            PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in ");
            PublicUtil.getProps_value().setProperty(cur_person_class.getName(), "from " + cur_person_class.getSimpleName() + " bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in");
            List<String> keys = (List<String>) CommUtil.selectSQL(DbUtil.tranSQL("select A01.a01_key " + hql));
            ftable_main.setObjects(keys);
        } else {
            String hql = " from BasePersonChange,A01,DeptCode where  BasePersonChange.A01_KEY=A01.A01_KEY and A01.deptCode_key=DeptCode.deptCode_key";
            if (qs != null) {
                hql += "and BasePersonChange.basePersonChange_key in(" + qs.buildSql() + ")";
            }
            if (curren_dept == null) {
                String s_container = PersonContainer.getPersonContainer().getA01KeyStr();
                hql += " and A01.a01_key in(" + s_container + ")";
            } else {
                hql += " and DeptCode.dept_code like '" + curren_dept.getDept_code() + "%'";
                hql += " and (" + UserContext.getPerson_class_right_str(deptPersonPanel.getPersonClass(), "A01") + ")";
            }
            String type = jcbType.getSelectedItem().toString();
            hql += " and BasePersonChange.changescheme_key='EmpScheme_Add'";
            if (type.equals("待处理")) {
                hql += " and BasePersonChange.chg_state<>'审批通过' and BasePersonChange.chg_state<>'撤销'";
                if (!UserContext.isSA) {
                    hql += " and exists(select 1 from WfStepPerson wsp where wsp.a01_key='" + UserContext.person_key + "' "
                            + " and wsp.wf_no=BasePersonChange.order_no and wsp.wf_state=BasePersonChange.chg_state)";
                }
            } else if (type.equals("已处理")) {
                hql += " and BasePersonChange.chg_state<>'审批通过' and BasePersonChange.chg_state<>'撤销'";
                if (!UserContext.isSA) {
                    hql += " and exists(select 1 from WfStepPerson wl where wl.a01_key='" + UserContext.person_key + "' and wl.wf_no=BasePersonChange.order_no)";
                }
            } else if (type.equals("已完成")) {
                hql += " and BasePersonChange.chg_state='审批通过'";
            } else if (type.equals("已撤销")) {
                hql += " and BasePersonChange.chg_state='撤销'";
            }
            if (s_where != null) {
                hql = hql + " and (" + s_where + ")";
            }
            if (!UserContext.isSA) {
                hql += " and (" + UserContext.getDept_right_rea_str("DeptCode") + ")";
                hql = UserContext.getEntityRightSQL("A01", hql, "A01");
            }
            PublicUtil.getProps_value().setProperty(BasePersonChange.class.getName(), "from BasePersonChange bpc join fetch bpc.a01 join fetch bpc.a01.deptCode left join fetch bpc.a01.g10 where bpc.basePersonChange_key in");
            ftable_un_change.setObjects(CommUtil.selectSQL(DbUtil.tranSQL("select BasePersonChange.basePersonChange_key " + hql + " order by " + change_order_sql)));
        }
    }

    private void editPerson() {
        boolean edit_flag = true;
        Object obj = ftable_main.getCurrentRow();
        if (!need_check && tabIndex == 0 && ((A01) obj).getA01_key() != null && (((A01) obj).getA0193() == 0)) {
            MsgUtil.showInfoMsg(EmpRegisterMsg.msg009);
            return;
        }
        if (tabIndex != 0) {
            obj = ftable_un_change.getCurrentRow();
            if (!"开始".equals(((BasePersonChange) obj).getChg_state())) {
                edit_flag = false;
            }
            obj = ((BasePersonChange) obj).getA01();
        }
        if (obj == null || ((A01) obj).getA01_key() == null) {
            return;
        }
        if (tabIndex == 0) {
            if (CommUtil.exists("select 1 from BasePersonChange where a01_key='" + ((A01) obj).getA01_key() + "' and chg_state<>'开始' and chg_state<>'撤销'")) {
                MsgUtil.showErrorMsg(EmpRegisterMsg.msg010);
                return;
            }
        }
        Hashtable<String, SysParameter> sys_paras = new Hashtable<String, SysParameter>();
        List schemes = CommUtil.fetchEntities("from ShowScheme ss left join fetch ss.showSchemeDetails where ss.entity_name like 'RegisterDesign.%'");
        Hashtable<String, ShowScheme> scheme_keys = new Hashtable<String, ShowScheme>();
        for (Object scheme : schemes) {
            scheme_keys.put(((ShowScheme) scheme).getEntity_name(), (ShowScheme) scheme);
        }
        if (!validateRegisterPara(sys_paras, scheme_keys)) {
            return;
        }
        RegisterPanel pnlRegister = new RegisterPanel((A01) obj, sys_paras, scheme_keys, edit_flag);
        ModelFrame.showModel(ContextManager.getMainFrame(), pnlRegister, true, EmpRegisterMsg.ttl003, 850, 700);
    }

    private boolean validateRegisterPara(Hashtable<String, SysParameter> sys_paras, Hashtable<String, ShowScheme> scheme_keys) {
        List list = CommUtil.fetchEntities("from SysParameter sp where sp.sysParameter_key in('Register.appendix','Register.field_flag','Register.id_cmp','Register.person_class','Register.a0177_flag','Register.check_flag')");
        if (list == null) {
            return false;
        }
        for (Object obj : list) {
            SysParameter sp = (SysParameter) obj;
            sys_paras.put(sp.getSysParameter_key(), sp);
        }
        Object msg = null;
        if (sys_paras.get("Register.person_class") == null || sys_paras.get("Register.person_class").getSysparameter_value().replace(" ", "").equals("")) {
            msg = EmpRegisterMsg.msg011;
        } else if (sys_paras.get("Register.check_flag") == null || sys_paras.get("Register.a0177_flag") == null || sys_paras.get("Register.appendix") == null || sys_paras.get("Register.field_flag") == null) {
            msg = EmpRegisterMsg.msg012;
        } else {
            String[] personClasses = sys_paras.get("Register.person_class").getSysparameter_value().split(";");
            for (String personClass : personClasses) {
                if (scheme_keys.get("RegisterDesign." + personClass) == null) {
                    msg = EmpRegisterMsg.msg013;
                }
            }
        }
        if (msg != null) {
            MsgUtil.showErrorMsg(msg);
            return false;
        }
        return true;
    }

    /**
     * 入职登记入口
     */
    private void register() {
        if (!EmpUtil.canRegister("register")) {
            return;
        }
        Hashtable<String, SysParameter> sys_paras = new Hashtable<String, SysParameter>();
        List schemes = CommUtil.fetchEntities("from ShowScheme ss left join fetch ss.showSchemeDetails where ss.entity_name like 'RegisterDesign.%'");
        Hashtable<String, ShowScheme> scheme_keys = new Hashtable<String, ShowScheme>();
        for (Object obj : schemes) {
            scheme_keys.put(((ShowScheme) obj).getEntity_name(), (ShowScheme) obj);
        }
        if (!validateRegisterPara(sys_paras, scheme_keys)) {
            return;
        }
        DeptCode tmp_dept = curren_dept;
        List list = CommUtil.selectSQL("select sysparameter_value from SysParameter where sysparameter_code='Register.Dept'");
        boolean allowRegister = EmpUtil.isAllowRegister(list, curren_dept);
        if (!allowRegister) {
            tmp_dept = null;
        }
        RegisterPanel pnlRegister = new RegisterPanel(tmp_dept, cur_person_class.getSimpleName(), sys_paras, scheme_keys, list);
        pnlRegister.addPickRegisterListener(new IPickRegisterListener() {

            @Override
            public void pickRegister() {
                fetchMainData(null, null);
            }
        });
        ModelFrame.showModel(ContextManager.getMainFrame(), pnlRegister, true, EmpRegisterMsg.ttl003, 850, 700);
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
        btnIn = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnProcess = new javax.swing.JButton();
        toolbar1 = new javax.swing.JToolBar();
        btnRegister = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jcbType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jtfSearch = new javax.swing.JTextField();
        btnQuickSearch = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        pnlMain = new javax.swing.JPanel();
        pnlUnChange = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        pnlTop = new javax.swing.JPanel();
        jtpBottom = new javax.swing.JTabbedPane();
        pnlChart = new javax.swing.JPanel();
        pnlProcess = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        pnlTop1 = new javax.swing.JPanel();
        jtpBottom1 = new javax.swing.JTabbedPane();
        pnlChart1 = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        btnIn.setText("人员入库");
        btnIn.setFocusable(false);
        btnIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnIn);

        btnUp.setText("提交审核");
        btnUp.setFocusable(false);
        btnUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnUp);

        btnCancel.setText("取消提交");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnCancel);

        btnProcess.setText("业务处理");
        btnProcess.setFocusable(false);
        btnProcess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProcess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnProcess);

        toolbar1.setFloatable(false);
        toolbar1.setRollover(true);

        btnRegister.setText("入职登记");
        btnRegister.setFocusable(false);
        btnRegister.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRegister.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar1.add(btnRegister);

        btnEdit.setText("编辑");
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar1.add(btnEdit);

        btnDel.setText("删除");
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar1.add(btnDel);

        jLabel2.setText(" 类型：");
        toolbar1.add(jLabel2);

        jcbType.setMaximumSize(new java.awt.Dimension(100, 32767));
        toolbar1.add(jcbType);

        jLabel1.setText(" 查找：");
        toolbar1.add(jLabel1);

        jtfSearch.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        toolbar1.add(jtfSearch);

        btnQuickSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search.png"))); // NOI18N
        btnQuickSearch.setFocusable(false);
        btnQuickSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuickSearch.setMaximumSize(new java.awt.Dimension(23, 23));
        btnQuickSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar1.add(btnQuickSearch);

        pnlMain.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("人员基本信息", pnlMain);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        pnlTop.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setTopComponent(pnlTop);

        pnlChart.setLayout(new java.awt.BorderLayout());
        jtpBottom.addTab("流程概况", pnlChart);

        jSplitPane2.setRightComponent(jtpBottom);

        javax.swing.GroupLayout pnlUnChangeLayout = new javax.swing.GroupLayout(pnlUnChange);
        pnlUnChange.setLayout(pnlUnChangeLayout);
        pnlUnChangeLayout.setHorizontalGroup(
            pnlUnChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
        pnlUnChangeLayout.setVerticalGroup(
            pnlUnChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );

        jtpMain.addTab("待处理业务", pnlUnChange);

        jSplitPane3.setDividerLocation(250);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setOneTouchExpandable(true);

        pnlTop1.setLayout(new java.awt.BorderLayout());
        jSplitPane3.setTopComponent(pnlTop1);

        pnlChart1.setLayout(new java.awt.BorderLayout());
        jtpBottom1.addTab("流程概况", pnlChart1);

        jSplitPane3.setRightComponent(jtpBottom1);

        javax.swing.GroupLayout pnlProcessLayout = new javax.swing.GroupLayout(pnlProcess);
        pnlProcess.setLayout(pnlProcessLayout);
        pnlProcessLayout.setHorizontalGroup(
            pnlProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
        pnlProcessLayout.setVerticalGroup(
            pnlProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );

        jtpMain.addTab("已处理业务", pnlProcess);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
            .addComponent(toolbar1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolbar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnIn;
    private javax.swing.JButton btnProcess;
    private javax.swing.JButton btnQuickSearch;
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JComboBox jcbType;
    private javax.swing.JTextField jtfSearch;
    private javax.swing.JTabbedPane jtpBottom;
    private javax.swing.JTabbedPane jtpBottom1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlChart;
    private javax.swing.JPanel pnlChart1;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlProcess;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTop1;
    private javax.swing.JPanel pnlUnChange;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JToolBar toolbar1;
    // End of variables declaration//GEN-END:variables

    private void setMainState() {
        ComponentUtil.setVisible(this, btnProcess, need_check && tabIndex == 1);
        ComponentUtil.setVisible(this, btnUp, need_check && tabIndex == 0);
        ComponentUtil.setVisible(this, btnIn, !need_check && tabIndex == 0);
        ComponentUtil.setVisible(this, btnDel, tabIndex == 0);
        btnCancel.setVisible(btnUp.isVisible());
        getMainFTable().setExportItemEnable(UserContext.hasFunctionRight(module_code + ".miExport"));
    }

    private FTable getMainFTable() {
        if (tabIndex == 0) {
            return ftable_main;
        } else {
            return ftable_un_change;
        }
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
        ContextManager.setStatusBar(getMainFTable().getObjects().size());
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }

    @Override
    public void initForWait(List data, Object row) {
        jtpMain.setSelectedIndex(1);
        deptPersonPanel.locateRoot();
        curren_dept = (DeptCode) deptPersonPanel.getCurDept();
        cur_person_class = deptPersonPanel.getPersonClass();
        initComp(tabIndex, deptPersonPanel.getPersonClass(), Boolean.valueOf(CommUtil.fetchEntityBy("select check_flag from ChangeScheme where changeScheme_key='EmpScheme_Add'").toString()));
        fetchMainData(null, null);
    }
}
