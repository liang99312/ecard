/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MyReportPanel.java
 *
 * Created on 2013-9-30, 17:14:04
 */
package org.jhrcore.client.desk;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import com.fr.cell.editor.AbstractCellEditor;
import com.fr.report.parameter.Parameter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.report.ReportModel;
import org.jhrcore.client.report.ReportPanel;
import org.jhrcore.client.system.comm.AddCommMapUserPnl;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.CommMap;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.report.ReportDef;
import org.jhrcore.entity.right.Role;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.mutil.ReportUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.task.IWaitWork;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.util.UtilTool;

/**
 *
 * @author mxliteboss
 */
public class MyReportPanel extends javax.swing.JPanel implements IModulePanel, IWaitWork {

    public static String module_code = "MyReport";
    private List<ReportDef> reports = new ArrayList();
    private List modules = null;
    private JTree myReportTree;
    private CheckTreeNode rootNode = new CheckTreeNode("所有报表");
    private CheckTreeNode myNode = new CheckTreeNode("我的报表");
    private CheckTreeNode shareNode = new CheckTreeNode("共享报表");
    private CheckTreeNode cur_node;
    private CommMap cm = null;
    private FTable ftable;
    private BeanPanel beanPanel = new BeanPanel();
    private List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
    private String order_sql = "A01.a0190";
    private int tabIndex = 0;

    /** Creates new form MyReportPanel */
    public MyReportPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        rootNode.add(myNode);
        rootNode.add(shareNode);
        List list = CommUtil.fetchEntities("from CommMap cm where cm.user_key='" + UserContext.rolea01_key + "' and cm.map_type='report' order by c_date desc");
        for (Object obj : list) {
            CommMap cm = (CommMap) obj;
            CheckTreeNode node = new CheckTreeNode(cm);
            if (cm.getC_user_key().equals(UserContext.rolea01_key)) {
                myNode.add(node);
            } else {
                shareNode.add(node);
            }
        }
        myReportTree = new JTree(rootNode);
        HRRendererView.getReportMap().initTree(myReportTree);
        pnlLeft.add(new JScrollPane(myReportTree));
        pnlInfo.add(new JScrollPane(beanPanel));
        ftable = new FTable(CommMap.class, true, true, false, module_code) {

            @Override
            public Color getCellForegroud(String fileName, Object cellValue, Object row_obj) {
                if (row_obj == null) {
                    return null;
                }
                CommMap comm = (CommMap) row_obj;
                if (comm.getUser_key().equals(comm.getC_user_key())) {
                    return Color.red;
                }
                return null;
            }
        };
        List<TempFieldInfo> all_fields_ls = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> default_fields_ls = new ArrayList<TempFieldInfo>();
        EntityBuilder.buildInfo(A01.class, all_fields_ls, default_fields_ls, "#A01");
        EntityBuilder.buildInfo(Role.class, all_fields_ls, default_fields_ls, "#Role");
        EntityBuilder.buildInfo(CommMap.class, all_fields_ls, default_fields_ls);
        for (TempFieldInfo info : all_fields_ls) {
            if (info.getField_name().contains("select_flag")) {
                continue;
            }
            all_fields.add(info);
        }
        for (TempFieldInfo info : default_fields_ls) {
            if (info.getField_name().contains("select_flag")) {
                continue;
            }
            default_fields.add(info);
        }
        ftable.getOther_entitys().put("Role", "Role Role,RoleA01 RoleA01,CommMap com");
        ftable.getOther_entity_keys().put("Role", "Role.role_key=RoleA01.role_key and RoleA01.rolea01_key=com.user_key and com.commMap_key ");
        ftable.getOther_entitys().put("A01", "A01 A01,A01PassWord A01PassWord,RoleA01 RoleA01,CommMap com");
        ftable.getOther_entity_keys().put("A01", "A01.a01_key=A01PassWord.a01_key and A01PassWord.a01password_key=RoleA01.a01password_key and RoleA01.rolea01_key=com.user_key and com.commMap_key ");
        ftable.setAll_fields(all_fields, default_fields, module_code);
        ftable.setRight_allow_flag(true);

        ftable.removeSumAndReplaceItem();
        pnlUser.add(ftable, BorderLayout.CENTER);
    }

    private void setupEvents() {
        myReportTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                cm = null;
                if (e.getPath() == null || e.getPath().getLastPathComponent() == null) {
                    return;
                }
                cur_node = (CheckTreeNode) e.getPath().getLastPathComponent();
                if (cur_node.getUserObject() instanceof CommMap) {
                    cm = (CommMap) cur_node.getUserObject();
                }
                refreshUI(null);
            }
        });
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refreshUI(null);
                tabIndex = jtpMain.getSelectedIndex();
                setMainState();
            }
        });
        btnExec.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                execReport(cm);
            }
        });
        btnImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                importReport();
            }
        });
        btnUnImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cm == null) {
                    return;
                }
                if (!cm.getUser_key().equals(cm.getC_user_key())) {
                    MsgUtil.showErrorMsg("请选择引入的报表进行取消");
                    return;
                }
                if (MsgUtil.showNotConfirmDialog("你确定取消引入该报表")) {
                    return;
                }
                if (CommUtil.exists("select 1 from CommMap where commMap_key='" + cm.getCommMap_key() + "' and user_key!='" + UserContext.rolea01_key + "'")) {
                    MsgUtil.showErrorMsg("该报表已经共享给其他用户不能取消");
                    return;
                }
                ValidateSQLResult result = CommUtil.deleteEntity(cm);
                if (result.getResult() == 0) {
                    cur_node.removeFromParent();
                    ComponentUtil.initTreeSelection(myReportTree);
                    MsgUtil.showHRSaveSuccessMsg(null);
                } else {
                    MsgUtil.showHRSaveErrorMsg(result.getMsg());
                }
            }
        });
        btnTran.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cm == null) {
                    return;
                }
                AddCommMapUserPnl pnl = new AddCommMapUserPnl(cm);
                pnl.addPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        reshUserTab(null);
                    }
                });
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnTran), pnl, true, "挑选用户", 600, 600);
            }
        });
        btnCanTran.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ftable.getSelectKeys().isEmpty()) {
                    return;
                }
                for (Object obj : ftable.getAllSelectObjects()) {
                    if (!(obj instanceof CommMap)) {
                        continue;
                    }
                    CommMap comm = (CommMap) obj;
                    if (!UserContext.rolea01_key.equals(comm.getC_user_key())) {
                        MsgUtil.showErrorMsg("不能取消引入的报表");
                        return;
                    }
                }

                if (MsgUtil.showNotConfirmDialog("你确定删除所选项吗？")) {
                    return;
                }
                ValidateSQLResult result = CommUtil.excuteSQLs("delete from CommMap where commMap_key in", ftable.getSelectKeys());
                if (result.getResult() == 0) {
                    ftable.deleteSelectedRows();
                    MsgUtil.showHRSaveSuccessMsg(null);
                } else {
                    MsgUtil.showHRSaveErrorMsg(result.getMsg());
                }
            }
        });
        ftable.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                order_sql = SysUtil.getSQLOrderString(showScheme, order_sql, all_fields);
                reshUserTab(ftable.getCur_query_scheme());
            }
        });
        ftable.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                reshUserTab(qs);
            }
        });
        ComponentUtil.initTreeSelection(myReportTree);
    }

    private void refreshUI(QueryScheme qs) {
        int tab = jtpMain.getSelectedIndex();
        if (tab == 0) {
            if (cm != null) {
                beanPanel.setBean(cm);
            } else {
                beanPanel.setBean(new CommMap());
            }
            beanPanel.bind();
        } else {
            if (cm == null) {
                ftable.deleteAllRows();
            } else {
//                List list = CommUtil.fetchEntities("from CommMap where c_user_key='" + cm.getC_user_key() + "' and map_key='" + cm.getMap_key() + "'");
//                ftable.setObjects(list);
                reshUserTab(qs);
            }
        }
    }

    private void reshUserTab(QueryScheme qs) {
        String sql = "from CommMap com,RoleA01 RoleA01,Role Role,A01PassWord A01PassWord,A01 A01 where A01.a01_key=A01PassWord.a01_key and A01PassWord.a01password_key=RoleA01.a01password_key and Role.role_key=RoleA01.role_key and RoleA01.rolea01_key=com.user_key and com.map_key='" + cm.getMap_key() + "' and com.c_user_key='" + cm.getC_user_key() + "'";
        if (qs != null) {
            sql += " and com.commMap_key in(" + qs.buildSql() + ")";
        }
        sql += " order by " + order_sql;
        PublicUtil.getProps_value().setProperty(CommMap.class.getName(), "from CommMap com where com.commMap_key in");
        ftable.setObjects(CommUtil.selectSQL("select com.commMap_key " + sql));
    }

    public static void execReport(CommMap cm) {
        if (cm == null) {
            return;
        }
        ReportDef rd = (ReportDef) CommUtil.fetchEntityBy("from ReportDef rd join fetch rd.moduleInfo where reportDef_key='" + cm.getMap_key() + "'");
        if (rd == null) {
            return;
        }
        String para = SysUtil.objToStr(cm.getMap_para());
        HashMap hm = new HashMap();
        if (!para.equals("")) {
            String[] paras = para.split(";");
            for (String p : paras) {
                String[] op = p.split("\\|");
                String key = op[0];
                Object value = op[1];
                if (op[1].contains(",")) {
                    value = op[1].split(",");
                }
                hm.put(key, value);
            }
        }
        ReportPanel.excute_report(ContextManager.getMainFrame(), rd, hm, false, true, true);
    }

    private void setMainState() {
        ComponentUtil.setCompEnable(this, btnTran, tabIndex == 1);
        ComponentUtil.setCompEnable(this, btnCanTran, tabIndex == 1);
    }

    private void importReport() {
        if (modules == null) {
            modules = CommImpl.getSysModule(false, false, false);
            ReportUtil.initReportModel(modules, reports);
        }
        ReportModel reportModel = new ReportModel(modules, reports, new ArrayList(), UserContext.role_id);
        final JPanel pnl = new JPanel(new BorderLayout());
        final JPanel pnlTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl = new JLabel("报表名称：");
        final JTextField jtfName = new JTextField();
        pnlTool.add(lbl);
        pnlTool.add(jtfName);
        ComponentUtil.setSize(jtfName, 200, 22);
        final JTree reportTree = new JTree(reportModel);
        HRRendererView.getReportMap().initTree(reportTree);
        pnl.add(pnlTool, BorderLayout.NORTH);
        pnl.add(new JScrollPane(reportTree));
        final CommMap cm = (CommMap) UtilTool.createUIDEntity(CommMap.class);
        final HashMap<String, AbstractCellEditor> nameHash = new HashMap<String, AbstractCellEditor>();
        reportTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getPath() == null || e.getPath().getLastPathComponent() == null) {
                    return;
                }
                CheckTreeNode cnode = (CheckTreeNode) e.getPath().getLastPathComponent();
                if (cnode.getUserObject() instanceof ReportDef) {
                    ReportDef rd = (ReportDef) cnode.getUserObject();
                    cm.setMap_key(rd.getReportDef_key());
                    cm.setMap_name(rd.getReport_name());
                    jtfName.setText(cm.getMap_name());
                    Parameter[] meters = ReportUtil.getReportParameters(rd);
                    pnl.removeAll();
                    if (meters.length > 0) {
                        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                        pnl.add(jsp);
                        nameHash.clear();
                        JPanel pnlPara = ReportUtil.getPanelByParas(meters, nameHash);
                        pnlPara.setBorder(javax.swing.BorderFactory.createTitledBorder("报表参数："));
                        JPanel pnlUp = new JPanel(new BorderLayout());
                        pnlUp.add(pnlTool, BorderLayout.NORTH);
                        pnlUp.add(new JScrollPane(reportTree));
                        jsp.add(pnlUp, JSplitPane.TOP);
                        jsp.add(new JScrollPane(pnlPara), JSplitPane.BOTTOM);
                        jsp.setDividerLocation(300);
                    } else {
                        pnl.add(pnlTool, BorderLayout.NORTH);
                        pnl.add(new JScrollPane(reportTree));
                    }
                    pnl.updateUI();
                }
            }
        });
        pnl.setPreferredSize(new Dimension(500, 500));
        //  ModelFrame.s
        if (ModalDialog.doModal(ContextManager.getMainFrame(), pnl, "引入报表")) {
            cm.setC_date(CommUtil.getServerDate());
            cm.setC_user(UserContext.getCurPerson());

            cm.setUser_key(UserContext.rolea01_key);
            cm.setMap_type("report");
            cm.setMap_name(jtfName.getText());
            cm.setC_user_key(UserContext.rolea01_key);
            String para = "";
            if (nameHash.size() > 0) {
                for (String key : nameHash.keySet()) {
                    AbstractCellEditor editor = nameHash.get(key);
                    try {
                        String field = ";" + key + "|";
                        Object obj = editor.getCellEditorValue();
                        String val = obj.toString();
                        if (obj instanceof String[]) {
                            val = "";
                            String[] objs = (String[]) obj;
                            if (objs.length > 0) {
                                for (String value : objs) {
                                    val += "," + value;
                                }
                                val = val.substring(1);
                            }
                        }
                        if (val.equals("")) {
                            continue;
                        }
                        para += field + val;
                    } catch (Exception ex) {
                    }
                }
                if (para.length() > 0) {
                    para = para.substring(1);
                }
                cm.setMap_para(para);
            }
            ValidateSQLResult result = CommUtil.saveEntity(cm);
            if (result.getResult() == 0) {
                CheckTreeNode node = new CheckTreeNode(cm);
                myNode.add(node);
                ComponentUtil.initTreeSelection(myReportTree, node);
                MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
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

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnExec = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnImport = new javax.swing.JButton();
        btnUnImport = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnTran = new javax.swing.JButton();
        btnCanTran = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        pnlInfo = new javax.swing.JPanel();
        pnlUser = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnExec.setText("执行");
        btnExec.setFocusable(false);
        btnExec.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExec.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnExec);
        jToolBar1.add(jSeparator1);

        btnImport.setText("引入报表");
        btnImport.setFocusable(false);
        btnImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnImport);

        btnUnImport.setText("取消引入");
        btnUnImport.setFocusable(false);
        btnUnImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUnImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnUnImport);
        jToolBar1.add(jSeparator2);

        btnTran.setText("报表共享");
        btnTran.setFocusable(false);
        btnTran.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTran.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnTran);

        btnCanTran.setText("取消共享");
        btnCanTran.setFocusable(false);
        btnCanTran.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCanTran.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnCanTran);

        pnlInfo.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("报表信息", pnlInfo);

        pnlUser.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("共享员工", pnlUser);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCanTran;
    private javax.swing.JButton btnExec;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTran;
    private javax.swing.JButton btnUnImport;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlUser;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFunctionRight() {
        setMainState();
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }

    @Override
    public void initForWait(List data, Object row) {
        if (row != null) {
            ComponentUtil.initTreeSelection(myReportTree, ComponentUtil.getNodeByObj(myReportTree, row));
        }
    }
}
