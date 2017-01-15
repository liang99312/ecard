/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EmpChangeUsePanel.java
 *
 * Created on 2009-4-21, 9:12:13
 */
package org.jhrcore.client.personnel.changemodule;

import com.foundercy.pf.control.table.FTable;
import com.foundercy.pf.control.table.RowChangeListner;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.personnel.comm.IPickRowSelectListener;
import org.jhrcore.client.personnel.comm.PersonSelectPanel;
import org.jhrcore.client.report.ReportPanel;
import org.jhrcore.client.personnel.comm.EmpCardUtil;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.BasePersonChange;
import org.jhrcore.entity.A01Chg;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.ClassAnnotation;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeField;
import org.jhrcore.entity.change.ChangeItem;
import org.jhrcore.entity.change.ChangeMethod;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.report.ReportDef;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.entity.showstyle.ShowSchemeDetail;
import org.jhrcore.entity.showstyle.ShowSchemeGroup;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpJdMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.EditorFactory;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.UIItemGroup;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class EmpChangeUsePanel extends javax.swing.JPanel implements IModuleCode {

    private ChangeScheme changeScheme;
    private BasePersonChange basePersonChange = null;
    private PersonSelectPanel personSelectPanel;
    private BeanPanel beanPanel = new BeanPanel();
    private List person_list;
    private Logger log = Logger.getLogger(EmpChangeUsePanel.class.getName());
    private Class cur_class = null;
    private List<ShowSchemeGroup> groups = null;
    private boolean isBatch = true;
    
    private List<IPickWindowCloseListener> listeners = new ArrayList<IPickWindowCloseListener>();
    private String changeSchemeSQL = "";//方案使用约束条件

    private HashSet<String> wfNos = new HashSet<String>();
    private boolean include_a0191 = false;
    private List<String> writeFields = new ArrayList<String>();
    private List<String> disableFields = new ArrayList<String>();
    private JLabel lbl = new JLabel("调令报表：");
    private JButton btnShow = new JButton("显示卡片");
    private JComboBox jcbReport = new JComboBox();
    private JButton btnWrite = new JButton("写卡");
    private JButton btnExcute = new JButton("执行");
    private JButton btnShowSet = new JButton("显示设置");
    private JMenuItem miShowOnePage = new JMenuItem("主附表同一卡片显示");
    private JMenuItem miShowTwoPage = new JMenuItem("附表同一卡片显示");
    private JMenuItem miShowManyPage = new JMenuItem("附表分开显示");
    private String new_a0191 = null;
    private String mutilTabAppendix = "2";//1;主附表均在同一界面；2：主附表分开；3：附表分开显示
    private Hashtable<BeanPanel, JPanel> appendixPnl = new Hashtable();
    private FTable ftable = null;
    private FTable ftableAppendix = null;
    private boolean showCard = true;
    private String module_code = "EmpJd.btnout.doEmpChangeAction";

    public void addPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public void delPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.remove(listener);
    }

    public EmpChangeUsePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    public EmpChangeUsePanel(ChangeScheme changeScheme, List list) {
        this.person_list = list;
        this.changeScheme = changeScheme;
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

        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlPerson = new javax.swing.JPanel();
        jtpMain = new javax.swing.JTabbedPane();
        pnl_EmpChange = new javax.swing.JPanel();
        pnlAppendix = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(627, 680));

        btnOk.setText("保存"); // NOI18N

        btnCancel.setText("退出"); // NOI18N

        jSplitPane1.setDividerLocation(270);
        jSplitPane1.setDividerSize(1);
        jSplitPane1.setLastDividerLocation(5);

        pnlPerson.setBorder(javax.swing.BorderFactory.createTitledBorder("调动信息："));
        pnlPerson.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlPerson);

        pnl_EmpChange.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("\u53d8\u52a8\u4fe1\u606f", pnl_EmpChange);

        pnlAppendix.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlAppendix.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("附表处理方式", pnlAppendix);

        jSplitPane1.setRightComponent(jtpMain);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(497, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlAppendix;
    private javax.swing.JPanel pnlPerson;
    private javax.swing.JPanel pnl_EmpChange;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        include_a0191 = changeScheme.contains("a0191");
        if (changeScheme.getQueryScheme_key() != null && !changeScheme.getQueryScheme_key().equals("")) {
            QueryScheme qs = (QueryScheme) CommUtil.fetchEntityBy("from QueryScheme qs left join fetch qs.conditions where qs.queryScheme_key='" + changeScheme.getQueryScheme_key() + "'");
            if (qs != null && qs.getConditions().size() > 0) {
                changeSchemeSQL = qs.buildHql("from A01 bp where 1=1", "bp");
                changeSchemeSQL = changeSchemeSQL.substring(17);
            }
        }
        disableFields.add("new_a0191");
//        disableFields.add("chg_state");
        disableFields.add("chg_type");
        disableFields.add("action_date");
        disableFields.add("chg_user");
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            disableFields.add("old_" + ci.getFieldName());
            writeFields.add("new_" + ci.getFieldName());
        }
        for (ChangeField ci : changeScheme.getChangeFields()) {
            if (ci.isFrom_import()) {
                disableFields.add(ci.getAppendix_field());
                continue;
            }
            writeFields.add(ci.getAppendix_field());
        }
        beanPanel.setEditForChangeScheme(changeScheme.isAll_dept_flag());
        try {
            buildBeanPanel(changeScheme, beanPanel);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex);
        }
        pnl_EmpChange.add(new JScrollPane(beanPanel));
        personSelectPanel = new PersonSelectPanel(cur_class, changeScheme.getChangeScheme_key());
        ftable = personSelectPanel.getFtable();
        ftable.removeShowOrderItem();
        ftable.setEditForChangeScheme(changeScheme.isAll_dept_flag());
        EditorFactory.editForChangeScheme = changeScheme.isAll_dept_flag();
        EditorFactory.onlyChildDept = true;
        EditorFactory.onlyChildCode = true;
        personSelectPanel.addWrite_fields(writeFields);
        refreshForReport();
        pnlPerson.add(personSelectPanel, BorderLayout.CENTER);
        JPanel pnl = personSelectPanel.getPnlPersons();
        JToolBar pnlTool = new JToolBar();
        pnlTool.add(btnShowSet);
        pnlTool.add(btnShow);
        pnlTool.add(lbl);
        pnlTool.add(jcbReport);
        pnlTool.add(btnExcute);
        pnlTool.add(btnWrite);
        pnlTool.setFloatable(false);
        pnlTool.setPreferredSize(new Dimension(pnl.getWidth(), 25));
        pnl.removeAll();
        pnl.add(ftable, BorderLayout.CENTER);
        pnl.add(pnlTool, BorderLayout.NORTH);
        ComponentUtil.setSize(jcbReport, 150, 22);
        btnWrite.setVisible(false);
        ftableAppendix = new FTable(ChangeMethod.class, false, false, false, "");
        if (changeScheme.getChangeMethods().isEmpty()) {
            jtpMain.remove(pnlAppendix);
        } else {
            List list = new ArrayList();
            for (ChangeMethod cm : changeScheme.getChangeMethods()) {
                ChangeMethod cm1 = (ChangeMethod) UtilTool.createUIDEntity(ChangeMethod.class);
                cm1.setAppendix_displayname(cm.getAppendix_displayname());
                cm1.setAppendix_name(cm.getAppendix_name());
                cm1.setMethod(cm.getMethod());
                cm1.setChangeScheme(cm.getChangeScheme());
                list.add(cm1);
            }
            ftableAppendix.setObjects(list);
            ftableAppendix.setEditable(true);
            pnlAppendix.add(ftableAppendix);
        }
    }

    private void setupEvents() {
        JOptionPane.getFrameForComponent(btnOk).addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                EditorFactory.editForChangeScheme = false;
                EditorFactory.onlyChildDept = false;
                EditorFactory.onlyChildCode = false;
            }
        });
        ftable.addRowChangeListner(new RowChangeListner() {

            @Override
            public void rowChanged(Object obj) {
                refreshCardUI(obj);
            }
        });
        personSelectPanel.addPickPersonSelectListener(new IPickRowSelectListener() {

            Object cur_obj = null;

            @Override
            public List pickPerson(String hql) {
                hql += " and not exists(select 1 from BasePersonChange mpc where mpc.a01=bp and mpc.changescheme_key='" + changeScheme.getChangeScheme_key() + "' and  mpc.chg_state<>'审批通过' and mpc.chg_state<>'撤销')";
                if (changeSchemeSQL != null && !changeSchemeSQL.equals("")) {
                    hql += " and (" + changeSchemeSQL + ")";
                }
                if (include_a0191) {
                    if (changeScheme.getOldPersonClassName() != null && !changeScheme.getOldPersonClassName().trim().equals("")) {
                        String[] ss = changeScheme.getOldPersonClassName().split("\\;");
                        String a091_str = "'-1'";
                        for (String str : ss) {
                            try {
                                Class c = Class.forName("org.jhrcore.entity." + str);
                                ClassAnnotation ca = (ClassAnnotation) c.getAnnotation(ClassAnnotation.class);
                                a091_str += ",'" + ca.displayName() + "'";
                            } catch (ClassNotFoundException ex) {
                                log.error(ex);
                                continue;
                            }
                        }
                        hql += " and bp.a0191 in(" + a091_str + ")";
                    }
                }
                return CommUtil.fetchEntities(hql);
            }

            @Override
            public void pickRow(Object a01) {
                if (cur_obj == a01) {
                    return;
                }
                cur_obj = a01;
                refreshCardUI(cur_obj);
            }

            @Override
            public void pickFields(List<String> fields) {
            }

            @Override
            public void addRows(List list) {
                Date date = CommUtil.getServerDate();
                for (Object obj : list) {
                    initChangeInfo((BasePersonChange) obj, ((BasePersonChange) obj).getA01(), date);
                }
                if (!list.isEmpty()) {
                    refreshCardUI(list.get(list.size() - 1));
                }
            }
        });
        personSelectPanel.addPickDelListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                if (personSelectPanel.getObjects().isEmpty()) {
                    if (wfNos != null) {
                        wfNos.clear();
                    }
                    btnExcute.setEnabled(false);
                    btnOk.setEnabled(true);
                    btnWrite.setVisible(false);
                    personSelectPanel.setSearchable(true);
                }
                try {
                    buildBeanPanel(changeScheme, beanPanel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error(ex);
                }
            }
        });
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                ValidateSQLResult result = saveChange();
                if (result == null) {
                    return;
                }
                if (result.getResult() == 0) {
                    MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }
        });
        CloseAction.doCloseAction(btnCancel);
        btnWrite.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = ftable.getCurrentRow();
                A01 a01 = null;
                if (obj instanceof BasePersonChange) {
                    a01 = ((BasePersonChange) obj).getA01();
                } else if (obj instanceof A01) {
                    a01 = (A01) obj;
                }
                if (a01 == null) {
                    return;
                }
                EmpCardUtil.WriteA01(a01);
            }
        });
        btnExcute.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = jcbReport.getSelectedItem();
                if (obj == null || (!(obj instanceof ReportDef))) {
                    return;
                }
                if (wfNos.isEmpty()) {
                    return;
                }
                Set a0190s = new HashSet();
                for (Object data : ftable.getAllSelectObjects()) {
                    a0190s.add(((BasePersonChange) data).getA01().getA0190());
                }
                Map map = new HashMap();
                map.put("批次号", wfNos.toArray());
                map.put("人员编号", a0190s.toArray());
                ReportPanel.excute_report((JFrame) JOptionPane.getFrameForComponent(btnExcute), (ReportDef) obj, map);
            }
        });
        final JPopupMenu pp = new JPopupMenu();
        pp.add(miShowOnePage);
        pp.add(miShowTwoPage);
        pp.add(miShowManyPage);
        mutilTabAppendix = ConfigManager.getConfigManager().getProperty("Property.Emp.Change.showMethod");
        mutilTabAppendix = (mutilTabAppendix == null || mutilTabAppendix.trim().equals("")) ? "1" : mutilTabAppendix;
        setShowMethod(mutilTabAppendix);
        miShowOnePage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setShowMethod("1");
            }
        });
        miShowTwoPage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setShowMethod("2");
            }
        });
        miShowManyPage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setShowMethod("3");
            }
        });
        btnShowSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pp.show(btnShowSet, 0, 25);
            }
        });
        btnShow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showCard(!showCard);
            }
        });
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refreshCardUI(basePersonChange);
            }
        });
        showCard(showCard);
        personSelectPanel.addObject(person_list);
        if (include_a0191) {
            try {
                String a0191 = changeScheme.getNewPersonClassName();
                Class c1 = Class.forName("org.jhrcore.entity." + a0191);
                ClassAnnotation ca = (ClassAnnotation) c1.getAnnotation(ClassAnnotation.class);
                new_a0191 = ca == null ? null : ca.displayName();
            } catch (Exception e) {
                log.error(e);
            }
        }
        Date date = CommUtil.getServerDate();
        for (Object obj : personSelectPanel.getObjects()) {
            BasePersonChange bpc = (BasePersonChange) obj;
            initChangeInfo(bpc, bpc.getA01(), date);
        }
        ftable.setRowSelectionInterval(0, 0);
        if (ftable.getObjects().size() == 1) {
            refreshCardUI(ftable.getCurrentRow());
        }
        personSelectPanel.updateUI();
    }

    private void initChangeInfo(BasePersonChange bpc, A01 a01, Date date) {
        if (bpc.getNew_flag() == 0) {
            return;
        }
        if (a01 != null) {
            setImportValueBy(a01, bpc);
        }
        bpc.setA01(a01);
        bpc.setChg_type(changeScheme.getChangeScheme_name());
        bpc.setChg_user(UserContext.person_name);
        bpc.setApply_date(date);
        bpc.setChg_state("待审批");
        bpc.setAction_date(date);
        bpc.setChange_ht_flag(changeScheme.isChange_ht_flag());
        if (include_a0191) {
            PublicUtil.setValueBy2(bpc, "new_a0191", new_a0191);
        }
        if (bpc.getNew_flag() == 1) {
            bpc.setPay_date(date);
            bpc.assignEntityKey(bpc.getBasePersonChange_key());
            bpc.setNew_flag(2);
        }
    }

    public void buildBeanPanel(ChangeScheme changeScheme, final BeanPanel beanPanel) throws Exception {
        String entityName = "org.jhrcore.entity.PersonChange_" + changeScheme.getChangeScheme_no();
        cur_class = Class.forName(entityName);
        entityName = cur_class.getSimpleName();
        groups = UIItemGroup.exist_groups.get("EmpChangeUse_" + entityName);
        if (groups == null) {
            groups = new ArrayList<ShowSchemeGroup>();
        } else {
            groups.clear();
        }
        if (basePersonChange == null) {
            basePersonChange = (BasePersonChange) UtilTool.createUIDEntity(cur_class);
            initChangeInfo(basePersonChange, null, CommUtil.getServerDate());
        }
        ShowScheme ss = (ShowScheme) UtilTool.createUIDEntity(ShowScheme.class);
        List<TempFieldInfo> change_infos = EntityBuilder.getCommFieldInfoListOf(BasePersonChange.class, EntityBuilder.COMM_FIELD_VISIBLE_ALL);
        Set<ShowSchemeDetail> details = new HashSet<ShowSchemeDetail>();
        List<TempFieldInfo> cis = new ArrayList<TempFieldInfo>();
        int ind = 0;
        List<String> baseFields = new ArrayList<String>();
        ss.setEntity_name("EmpChangeUse_" + cur_class.getSimpleName());
        ss.setShowSchemeDetails(details);
        ss.setGroup_flag(true);
//        baseFields.add("chg_state");
//        baseFields.add("chg_type");
        baseFields.add("apply_date");
        baseFields.add("reason");
//        baseFields.add("action_date");
        baseFields.add("pay_date");
//        baseFields.add("chg_user");
        if (changeScheme.contains("deptCode")) {
            baseFields.add("change_ht_flag");
        }
        for (ChangeField cf : changeScheme.getChangeFields()) {
            if (cf.getAppendix_name().equals("BasePersonChange")) {
                baseFields.add(SysUtil.tranField(cf.getAppendix_field()));
            }
        }
        for (TempFieldInfo tfi : change_infos) {
            if (tfi.getField_name().equals("order_no")) {
                continue;
            }
            if (!baseFields.contains(SysUtil.tranField(tfi.getField_name()))) {
                continue;
            }
            ind++;
            details.add(EmpUtil.createShowDetail(entityName, tfi.getField_name(), EmpJdMsg.ttl010.toString(), ind, groups));
        }
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            TempFieldInfo tfi = EntityBuilder.getTempFieldInfoByName("A01", ci.getFieldName(), false);
            if (tfi == null) {
                continue;
            }
            cis.add(tfi);
        }
        SysUtil.sortListByInteger(cis, "order_no");
        for (TempFieldInfo tfi : cis) {
            details.add(EmpUtil.createShowDetail(entityName, "old_" + tfi.getField_name(), EmpJdMsg.ttl009.toString(), ind, groups));
            ind++;
            details.add(EmpUtil.createShowDetail(entityName, "new_" + tfi.getField_name(), EmpJdMsg.ttl009.toString(), ind, groups));
            ind++;
        }
        for (final ChangeField cf : changeScheme.getChangeFields()) {
            if ("1".equals(mutilTabAppendix) && !cf.getAppendix_name().equals("BasePersonChange")) {
                details.add(EmpUtil.createShowDetail(entityName, cf.getAppendix_field(), cf.getAppendix_displayname(), ind, groups));
                ind++;
            }
        }
        UIItemGroup.exist_groups.put("EmpChangeUse_" + entityName, groups);
        beanPanel.setBean(basePersonChange, true);
        tranScheme(ss);
        beanPanel.setShow_scheme(ss);
        beanPanel.setDisable_fields(disableFields);
        if (ftable != null) {
            ftable.setDisable_fields(disableFields);
        }
        List<String> fields = EntityBuilder.getCommFieldNameListOf(cur_class, EntityBuilder.COMM_FIELD_VISIBLE_ALL);
        beanPanel.setFields(fields);
        beanPanel.bind();
        for (JPanel pnl : appendixPnl.values()) {
            jtpMain.remove(pnl);
        }
        appendixPnl.clear();
        if ("1".equals(mutilTabAppendix)) {
            return;
        }
        HashMap<String, Set<ChangeField>> hm = new HashMap<String, Set<ChangeField>>();
        for (ChangeField cf : changeScheme.getChangeFields()) {
            if (cf.getAppendix_name().equals("BasePersonChange")) {
                continue;
            }
            Set<ChangeField> lt = hm.get(cf.getAppendix_displayname());
            if (lt == null) {
                lt = new HashSet<ChangeField>();
                hm.put(cf.getAppendix_displayname(), lt);
            }
            lt.add(cf);
        }
        ind = 1;
        if ("3".equals(mutilTabAppendix)) {
            for (String en : hm.keySet()) {
                BeanPanel bp = new BeanPanel();
                bp.setDisable_fields(disableFields);
                bp.setColumns(2);
                buildAppendixBeanPanel(en, bp, hm.get(en));
                JPanel pnl = new JPanel(new BorderLayout());
                pnl.add(new JScrollPane(bp));
                jtpMain.insertTab(en, null, pnl, en, ind);
                appendixPnl.put(bp, pnl);
                ind++;
            }
        } else if ("2".equals(mutilTabAppendix)) {
            if (hm.keySet().size() > 0) {
                BeanPanel bp = new BeanPanel();
                bp.setDisable_fields(disableFields);
                bp.setColumns(2);
                buildAppendixBeanPanel("PersonChange", bp, changeScheme.getChangeFields());
                JPanel pnl = new JPanel(new BorderLayout());
                pnl.add(new JScrollPane(bp));
                jtpMain.insertTab(EmpJdMsg.ttl008.toString(), null, pnl, EmpJdMsg.ttl008.toString(), ind);
                appendixPnl.put(bp, pnl);
            }
        }
    }

    private void addPropertyListener(final BasePersonChange bpc) {
        if (ftable != null) {
            for (Object obj : ftable.getObjects()) {
                BasePersonChange personChange = (BasePersonChange) obj;
                PropertyChangeListener[] p_listeners = personChange.getPropertyChangeListeners();
                for (PropertyChangeListener listener : p_listeners) {
                    personChange.removePropertyChangeListener(listener);
                }
            }
        }
        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String field = evt.getPropertyName();
                Object value = evt.getNewValue();
                List list = ftable.getSelectObjects();
                for (ChangeItem ci : changeScheme.getChangeItems()) {
                    boolean comm_flag = ci.isComm_flag();
                    String name = "new_" + ci.getFieldName();
                    if (name.equals(field) && comm_flag) {
                        list = ftable.getObjects();
                        break;
                    }
                }
                List<ChangeField> import_fields = new ArrayList<ChangeField>();
                for (ChangeField cf : changeScheme.getChangeFields()) {
                    if ("更新".equals(cf.getC_type())) {
                        continue;
                    }
                    boolean comm_flag = cf.isComm_flag();
                    String name = cf.getAppendix_field();
                    boolean isWrite = !cf.isFrom_import();
                    if (name.equals(field) && comm_flag && isWrite) {
                        list = ftable.getObjects();
                    } else if (isWrite || "A01".equals(cf.getImport_name())) {
                        continue;
                    }
                    import_fields.add(cf);
                }
                if (list == null) {
                    list = ftable.getSelectObjects();
                }
                for (Object obj : list) {
                    if (obj == bpc) {
                        continue;
                    }
                    PublicUtil.setValueBy2(obj, field, value);
                }
                for (Object obj : list) {
                    for (ChangeField cf : import_fields) {
                        try {
                            String fieldName = cf.getImport_field();
                            if (!fieldName.equals(evt.getPropertyName())) {
                                continue;
                            }
                            Method method = obj.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{});
                            Object tmp_obj = method.invoke(obj, new Object[]{});
                            Class field_class = obj.getClass().getField(fieldName).getType();
                            Class appendix_class = Class.forName("org.jhrcore.entity." + cf.getAppendix_name());
                            fieldName = cf.getAppendix_field();
                            Class appendix_field_class = appendix_class.getField(fieldName).getType();
                            tmp_obj = PublicUtil.getDefaultValueForType(tmp_obj, appendix_field_class.getSimpleName(), field_class.getSimpleName());
                            method = obj.getClass().getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{appendix_field_class});
                            method.invoke(obj, new Object[]{tmp_obj});
                            beanPanel.bind();
                        } catch (Exception ex) {
                            log.error(ex);
                        }
                    }
                }
                personSelectPanel.updateUI();
            }
        };
        bpc.addPropertyChangeListener(listener);
    }

    private void tranScheme(ShowScheme ss) {
        List<TempFieldInfo> infos = EntityBuilder.getCommFieldInfoListOf(cur_class);
        Hashtable<String, Integer> orderKeys = new Hashtable();
        for (TempFieldInfo tfi : infos) {
            orderKeys.put(tfi.getField_name(), tfi.getOrder_no());
        }
        for (ShowSchemeDetail ssd : ss.getShowSchemeDetails()) {
            Integer order = orderKeys.get(ssd.getField_name());
            if (order != null) {
                ssd.setOrder_no(order);
            }
        }
    }

    private void buildAppendixBeanPanel(String entityName, final BeanPanel beanPanel, Set<ChangeField> fields) {
        groups = UIItemGroup.exist_groups.get("EmpChangeUse_" + entityName);
        if (groups == null) {
            groups = new ArrayList<ShowSchemeGroup>();
        }
        ShowScheme ss = (ShowScheme) UtilTool.createUIDEntity(ShowScheme.class);
        Set<ShowSchemeDetail> details = new HashSet<ShowSchemeDetail>();
        ss.setGroup_flag(true);
        int ind = 0;
        ss.setEntity_name("EmpChangeUse_" + entityName);
        ss.setShowSchemeDetails(details);
        for (final ChangeField cf : fields) {
            if (cf.getAppendix_name().equals("BasePersonChange")) {
                continue;
            }
            details.add(EmpUtil.createShowDetail(entityName, cf.getAppendix_field(), cf.getAppendix_displayname(), ind, groups));
            ind++;
        }
        UIItemGroup.exist_groups.put("EmpChangeUse_" + entityName, groups);
        beanPanel.setBean(basePersonChange, true);
        tranScheme(ss);
        beanPanel.setShow_scheme(ss);
        beanPanel.bind();
    }

    private void refreshCardUI(Object cur_obj) {
        if (cur_obj == null) {
            return;
        }
        basePersonChange = (BasePersonChange) cur_obj;
        addPropertyListener(basePersonChange);
        beanPanel.setBean(basePersonChange);
        beanPanel.bind();
        for (BeanPanel bp : appendixPnl.keySet()) {
            bp.setBean(basePersonChange);
            bp.bind();
        }
    }

    private void setImportValueBy(A01 basePerson, BasePersonChange change) {
        if (basePerson == null) {
            return;
        }
        // 使用basePerson来设置变动单的引入项目
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            String fieldName = ci.getFieldName();
            String importName = fieldName;
            try {
                Method method = A01.class.getMethod("get" + importName.substring(0, 1).toUpperCase() + importName.substring(1), new Class[]{});
                Object tmp_obj = method.invoke(basePerson, new Object[]{});
                Class field_class = A01.class.getField(importName).getType();
                fieldName = "old_" + ci.getFieldName();
                method = change.getClass().getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{field_class});
                method.invoke(change, new Object[]{tmp_obj});
            } catch (Exception ex) {
                log.error(ex);
            }
        }
        for (ChangeField cf : changeScheme.getChangeFields()) {
            if ("更新".equals(cf.getC_type())) {
                continue;
            }
            try {
                String fieldName = cf.getImport_field();
                Object tmp_obj = null;
                Method method = null;
                Class field_class = null;
                if ("A01".equals(cf.getImport_name())) {
                    method = A01.class.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{});
                    tmp_obj = method.invoke(basePerson, new Object[]{});
                    field_class = A01.class.getField(fieldName).getType();
                } else if (cf.isFrom_import()) {
                    method = change.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{});
                    tmp_obj = method.invoke(change, new Object[]{});
                    field_class = change.getClass().getField(fieldName).getType();
                }
                if (field_class == null) {
                    continue;
                }
                Class appendix_class = Class.forName("org.jhrcore.entity." + cf.getAppendix_name());
                fieldName = cf.getAppendix_field();
                Class appendix_field_class = appendix_class.getField(fieldName).getType();
                tmp_obj = PublicUtil.getDefaultValueForType(tmp_obj, appendix_field_class.getSimpleName(), field_class.getSimpleName());
                method = change.getClass().getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{appendix_field_class});
                method.invoke(change, new Object[]{tmp_obj});
            } catch (Exception ex) {
                log.error(ex);
            }
        }
    }

    /**
     * 该方法用于将指定对象值根据业务需要更改输出值，仅针对A01CHG表中的变动前/后
     * @param tmp_obj：指定对象
     * @return：更改后的输出值
     */
    private String transDataForA01Chg(Object tmp_obj) {
        tmp_obj = tmp_obj == null ? "" : tmp_obj;
        if (tmp_obj instanceof DeptCode) {
            tmp_obj = ((DeptCode) tmp_obj).getContent() + "{" + ((DeptCode) tmp_obj).getDept_code() + "}";
        } else if (tmp_obj.getClass().getSimpleName().toLowerCase().equals("boolean")) {
            tmp_obj = Boolean.valueOf(tmp_obj.toString()) ? "1" : "0";
        } else if (tmp_obj instanceof Date) {
            tmp_obj = DateUtil.DateToStr((Date) tmp_obj, "yyyy-MM-dd HH:mm:ss");
        }
        return tmp_obj.toString();
    }
    // 根据BasePersonChange生成变动主表记录

    private void createMainPersonChange(BasePersonChange tmp_bpc, List save_list) {
        DeptCode dc = null;
        boolean change_dept_flag = changeScheme.contains("deptCode");
        List list = new ArrayList();
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            A01Chg mainPersonChange = (A01Chg) UtilTool.createUIDEntity(A01Chg.class);
            mainPersonChange.setChgdate(tmp_bpc.getApply_date());
            mainPersonChange.setBasePersonChange(tmp_bpc);
            mainPersonChange.setChgfield(ci.getDisplayName());
            String fieldName = ci.getFieldName();
            try {
                fieldName = "old_" + ci.getFieldName();
                Method method = tmp_bpc.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{});
                Object tmp_obj = method.invoke(tmp_bpc, new Object[]{});
                mainPersonChange.setBeforestate(transDataForA01Chg(tmp_obj));
                fieldName = "new_" + ci.getFieldName();
                method = tmp_bpc.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{});
                tmp_obj = method.invoke(tmp_bpc, new Object[]{});
                mainPersonChange.setAfterstate(transDataForA01Chg(tmp_obj));
                mainPersonChange.setChgfieldname(fieldName);
                mainPersonChange.setChangeScheme_key(ci.getChangeScheme().getChangeScheme_key());
                A01 person = tmp_bpc.getA01();
                mainPersonChange.setB_dept_code(person.getDeptCode().getDept_code());
                mainPersonChange.setB_full_name(person.getDeptCode().getDept_full_name());
                mainPersonChange.setA_dept_code(person.getDeptCode().getDept_code());
                mainPersonChange.setA_full_name(person.getDeptCode().getDept_full_name());
                if (ci.getFieldName().equals("deptCode") && change_dept_flag) {
                    dc = (DeptCode) tmp_obj;
                }
                list.add(mainPersonChange);
            } catch (Exception ex) {
                log.error(ex);
            }
            save_list.add(mainPersonChange);
        }
        if (change_dept_flag && dc != null) {
            for (Object obj : list) {
                ((A01Chg) obj).setA_dept_code(dc.getDept_code());
                ((A01Chg) obj).setA_full_name(dc.getDept_full_name());
            }
        }
    }

    private ValidateSQLResult saveChange() {
        List save_list = new ArrayList();
        List update_list = new ArrayList();
        String method = "";
        ftableAppendix.editingStopped();
        for (Object obj : ftableAppendix.getObjects()) {
            ChangeMethod cm = (ChangeMethod) obj;
            method += cm.getAppendix_name() + "|" + cm.getMethod() + ";";
        }
        for (Object obj : personSelectPanel.getObjects()) {
            BasePersonChange baseChange = (BasePersonChange) obj;
            if (baseChange.getNew_flag() > 0) {
                save_list.add(baseChange);
                baseChange.setAppendix_type(method);
            } else {
                update_list.add(baseChange);
            }
            createMainPersonChange(baseChange, save_list);
        }
        List<List> result_list = new ArrayList<List>();
        result_list.add(save_list);
        result_list.add(update_list);
        
        ValidateSQLResult result = new ValidateSQLResult();
//        RSImpl.createPersonChange(result_list, changeScheme.getChangeScheme_key(), isBatch, wfd, wfInsLog, conn, EmpUtil.getCommUserLog());
        for (IPickWindowCloseListener listener : listeners) {
            listener.pickClose();
        }
        if (result.getResult() == 0) {
            if (wfNos.isEmpty()) {
                List list = CommUtil.selectSQL("select order_no,chg_state from BasePersonChange where basePersonChange_key in ", ftable.getAllKeys());
                String state = "";
                for (Object obj : list) {
                    Object[] objs = (Object[]) obj;
                    if (wfNos.contains(objs[0].toString())) {
                        continue;
                    }
                    wfNos.add(objs[0].toString());
                    state = objs[1].toString();
                }
                btnWrite.setVisible(UserContext.hasFunctionRight("EmpMng.btnWrite") && (EmpJdMsg.msg038.toString()).equals(state));
            }
            btnExcute.setEnabled(true);
            btnOk.setEnabled(false);
            personSelectPanel.setSearchable(false);
        }
        return result;
    }

    public void refreshForReport() {
        btnExcute.setEnabled(wfNos != null && wfNos.size() > 0);
        if (wfNos != null && wfNos.size() > 0) {
            String state = CommUtil.fetchEntityBy("select wf_state from WfInstance where wf_no='" + wfNos.toArray()[0] + "'").toString();
            btnWrite.setVisible((EmpJdMsg.ttl007.toString()).equals(state) && UserContext.hasFunctionRight("EmpMng.btnWrite"));
        }
        String key = changeScheme.getReport_key() == null ? "" : changeScheme.getReport_key();
        if (key.equals("") || jcbReport.getItemCount() > 0) {
            return;
        }
        String[] keys = key.split("\\;");
        String search_key = "'@@@'";
        for (String report_key : keys) {
            search_key = search_key + ",'" + report_key + "'";
        }
        List list = CommUtil.fetchEntities("from ReportDef rd join fetch rd.moduleInfo where rd.reportDef_key in(" + search_key + ")");
        List rightList = new ArrayList();
        for (Object obj : list) {
            ReportDef rd = (ReportDef) obj;
            if (UserContext.hasReportRight(rd.getReportDef_key())) {
                rightList.add(obj);
            }
        }
        SwingBindings.createJComboBoxBinding(UpdateStrategy.READ, rightList, jcbReport).bind();
    }

    private void setShowMethod(String method) {
        mutilTabAppendix = method;
        ConfigManager.getConfigManager().setProperty("Property.Emp.Change.showMethod", method);
        ConfigManager.getConfigManager().save2();
        ComponentUtil.setIcon(miShowOnePage, "1".equals(mutilTabAppendix) ? "select" : "blank");
        ComponentUtil.setIcon(miShowTwoPage, "2".equals(mutilTabAppendix) ? "select" : "blank");
        ComponentUtil.setIcon(miShowManyPage, "3".equals(mutilTabAppendix) ? "select" : "blank");
        try {
            buildBeanPanel(changeScheme, beanPanel);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex);
        }
        ftable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    }

    private void showCard(boolean visible) {
        this.showCard = visible;
        btnShow.setText(showCard ? EmpJdMsg.msg036.toString() : EmpJdMsg.msg037.toString());
        jPanel1.removeAll();
        jPanel1.setLayout(new BorderLayout());
        if (visible) {
            JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlPerson, jtpMain);
            jsp.setDividerSize(3);
            jPanel1.add(jsp);
            ComponentUtil.refreshJSplitPane(jsp, "EmpChangeUsePanel.jsp");
        } else {
            jPanel1.add(pnlPerson);
        }
        jPanel1.updateUI();
    }
    public void addProcessPanel(JPanel pnl) {
        if (pnl != null) {
            jtpMain.add(EmpJdMsg.ttl005.toString(), pnl);
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
