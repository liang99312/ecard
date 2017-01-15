/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoginUserMngPanel.java
 *
 * Created on 2010-12-13, 17:21:25
 */
package org.jhrcore.client.system;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickFieldSetListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.LogInfo;
import org.jhrcore.entity.base.LoginUser;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptPanel;
import org.jhrcore.ui.HrTextPane;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.renderer.TableListCellRender;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class LoginUserMngPanel extends javax.swing.JPanel implements IModulePanel {

    private DeptPanel deptPanel;
    private JList jlsUser = new JList();
    private JListBinding userBinding;
    private List<LoginUser> users = new ArrayList<LoginUser>();
    private DeptCode curDept;
    private BeanPanel beanPanel = new BeanPanel();
    private int infoCount = 0;//员工信息计数器
    private int logCount = 0;//操作日志计数器
    private LoginUser cur_user = null;
    private FTable ftable_log = null;
    private FTable ftable_user = null;//用于辅助设置显示字段
    private String log_order_sql = "lu.log_date desc";
    private JhrDatePicker jdpStart = new JhrDatePicker();
    private JhrDatePicker jdpEnd = new JhrDatePicker();
    private JLabel lblStart = new JLabel(" 查询日期起: ");
    private JLabel lblEnd = new JLabel(" 止: ");
    private JLabel lblSearch = new JLabel(" 查找: ");
    private JCheckBox jcbCurColumn = new JCheckBox("当前列");
    private JTextField jtfSearch = new JTextField();
    private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
    private JToolBar toolbarLog = new JToolBar();
    private List<TempFieldInfo> logInfos;
    public static final String module_code = "SysUserMng";

    /** Creates new form LoginUserMngPanel */
    public LoginUserMngPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        deptPanel = new DeptPanel(UserContext.getDepts(false));
        pnlLeft.add(deptPanel);
        userBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, users, jlsUser);
        userBinding.bind();
        jlsUser.setCellRenderer(new TableListCellRender());
        jlsUser.setLayoutOrientation(JList.VERTICAL_WRAP);
        jlsUser.setVisibleRowCount(-1);
        jlsUser.setFixedCellHeight(25);
        pnlUser.add(new JScrollPane(jlsUser));
        pnlUserInfo.add(new JScrollPane(beanPanel), BorderLayout.CENTER);
        ftable_log = new FTable(LogInfo.class, true, true, false, module_code);
        ftable_log.setRight_allow_flag(true);
        ftable_log.removeSumAndReplaceItem();
        pnlUserLog.add(ftable_log);
        initBar();
        pnlUserLog.add(toolbarLog, BorderLayout.NORTH);
        logInfos = EntityBuilder.getCommFieldInfoListOf(LogInfo.class, EntityBuilder.COMM_FIELD_VISIBLE);
        log_order_sql = SysUtil.getOrderString(ftable_log.getCurOrderScheme(), "lu", log_order_sql, logInfos);
        ftable_user = new FTable(A01.class, true, false, false, module_code);
        List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
        EntityBuilder.buildInfo(DeptCode.class, all_fields, default_fields, "deptCode");
        EntityBuilder.buildInfo(A01.class, all_fields, default_fields, "");
        ftable_user.setAll_fields(all_fields, default_fields, new ArrayList(), module_code);
    }

    private void setupEvents() {
        btnMsg.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] users = jlsUser.getSelectedValues();
                if (users == null || users.length == 0) {
                    return;
                }
                sendMsg(users);
            }
        });
        jlsUser.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (cur_user == jlsUser.getSelectedValue()) {
                    return;
                }
                cur_user = (LoginUser) jlsUser.getSelectedValue();
                infoCount = 0;
                logCount = 0;
                fetchUserInfo(cur_user);
            }
        });
        jTabbedPane1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                fetchUserInfo(cur_user);
            }
        });
        deptPanel.getDeptTree().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getPath() == null || e.getPath().getLastPathComponent() == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                if (node.getUserObject() instanceof DeptCode) {
                    curDept = (DeptCode) node.getUserObject();
                    fetchUserState(jcbOnline.isSelected());
                }
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigManager.getConfigManager().setProperty("LoginUserMngPanel.showOnline", jcbOnline.isSelected() ? "1" : "0");
                ConfigManager.getConfigManager().save2();
                fetchUserState(jcbOnline.isSelected());
            }
        };
        btnQuit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] users = jlsUser.getSelectedValues();
                if (users == null || users.length == 0) {
                    return;
                }
                disconnect(users);
            }
        });
        jcbOnline.addActionListener(al);
        ftable_log.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                log_order_sql = SysUtil.getOrderString(showScheme, "lu", log_order_sql, logInfos);
                fetchUserLog(cur_user, ftable_log.getCur_query_scheme(), null);
            }
        });
        ftable_log.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchUserLog(cur_user, qs, null);
            }
        });
        btnField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ftable_user.setShowFields();
            }
        });
        ftable_user.addPickFieldSetListener(new IPickFieldSetListener() {

            @Override
            public void pickField(ShowScheme showScheme) {
                beanPanel.setShow_scheme(showScheme);
                beanPanel.setFields(ftable_user.getFields());
                beanPanel.bind();
            }
        });

        ActionListener alSearch = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String val = jtfSearch.getText().trim().toUpperCase();
                if (val.equals("")) {
                    fetchUserLog(cur_user, ftable_log.getCur_query_scheme(), null);
                    return;
                }
                val = SysUtil.getQuickSearchText(val);
                String s_where = "";
                if (jcbCurColumn.isSelected()) {
                    s_where = " lu.messgae like '" + val + "' ";
                } else {
                    s_where = ftable_log.getQuickSearchSQL("lu", val);
                }
                if (!s_where.equals("")) {
                    fetchUserLog(cur_user, ftable_log.getCur_query_scheme(), s_where);
                }
            }
        };
        btnSearch.addActionListener(alSearch);
        jtfSearch.addActionListener(alSearch);
        Object obj = deptPanel.getDeptTree().getSelectionPath();
        if (obj != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) deptPanel.getDeptTree().getSelectionPath().getLastPathComponent();
            if (node.getUserObject() instanceof DeptCode) {
                curDept = (DeptCode) node.getUserObject();
            }
        }
        String showOnline = ConfigManager.getConfigManager().getProperty("LoginUserMngPanel.showOnline");
        jcbOnline.setSelected(!"0".equals(showOnline));
        btnRefresh.addActionListener(al);
        fetchUserState(jcbOnline.isSelected());
    }

    private void initBar() {
        jdpStart.setDate(DateUtil.getCurMonthFirstDay());
        jdpEnd.setDate(new Date());
        toolbarLog.setFloatable(false);
        toolbarLog.add(lblStart);
        toolbarLog.add(jdpStart);
        toolbarLog.add(lblEnd);
        toolbarLog.add(jdpEnd);
        toolbarLog.add(lblSearch);
        toolbarLog.add(jtfSearch);
        toolbarLog.add(jcbCurColumn);
        toolbarLog.add(btnSearch);
        ComponentUtil.setSize(jtfSearch, 120, 22);
        ComponentUtil.setSize(btnSearch, 24, 24);
    }

    /**
     * 该方法用于强制指定用户（数）组下线
     * @param users：指定用户（数）组
     */
    private void disconnect(Object[] users) {
        if (JOptionPane.showConfirmDialog(null, "确定要将选择的用户强制下线？", "询问",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
            return;
        }
        List list = new ArrayList();
        LoginUser option_lu = null;
        for (Object obj : users) {
            LoginUser lu = (LoginUser) obj;
            if (lu.getUser_code().equals(UserContext.person_key)) {
                option_lu = lu;
            } else {
                list.add(lu);
            }
        }
        if (option_lu != null) {
            list.remove(option_lu);
        } else {
            option_lu = new LoginUser();
            option_lu.setUser_name(UserContext.person_name);
        }
        list.add(0, option_lu);
        ValidateSQLResult validateSQLResult = CommImpl.disconnect(list, "强制下线");
        if (validateSQLResult.getResult() == 0) {
            fetchUserState(jcbOnline.isSelected());
        } else {
            MsgUtil.showHRSaveErrorMsg(validateSQLResult);
        }
    }

    /**
     * 该方法用于强制指定用户（数）组下线
     * @param users：指定用户（数）组
     */
    private void sendMsg(Object[] users) {
        HrTextPane textPane = new HrTextPane();
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(textPane);
        pnl.setPreferredSize(new Dimension(400, 300));
        if (ModalDialog.doModal(ContextManager.getMainFrame(), pnl, "请输入消息内容：")) {
            String text = textPane.getText();
            if (text == null || text.trim().equals("")) {
                JOptionPane.showMessageDialog(null, "不允许发送空消息！");
                return;
            }
            List list = new ArrayList();
            LoginUser option_lu = null;
            for (Object obj : users) {
                LoginUser lu = (LoginUser) obj;
                if (lu.getUser_code().equals(UserContext.person_key)) {
                    option_lu = lu;
                } else {
                    list.add(lu);
                }
            }
            if (option_lu != null) {
                list.remove(option_lu);
            } else {
                option_lu = new LoginUser();
                option_lu.setUser_name(UserContext.person_name);
            }
            list.add(0, option_lu);
            ValidateSQLResult validateSQLResult = CommImpl.sendMsg(list, text);
            if (validateSQLResult.getResult() == 0) {
                fetchUserState(jcbOnline.isSelected());
            } else {
                MsgUtil.showHRSaveErrorMsg(validateSQLResult);
            }
        }

    }

    /**
     * 该方法用于初始化计数器
     */
    private void initCount() {
        infoCount = 0;
        logCount = 0;
    }

    /**
     * 该方法用于显示当前选择用户的信息/操作日志
     * @param lu：当前选择用户
     */
    private void fetchUserInfo(LoginUser lu) {
        int tabIndex = jTabbedPane1.getSelectedIndex();
        if (tabIndex == 0) {
            if (infoCount > 0) {
                return;
            }
            infoCount++;
            if (lu != null) {
                A01 a01 = (A01) CommUtil.fetchEntityBy("from A01 bp join fetch bp.deptCode where bp.a01_key='" + lu.getUser_code() + "'");
                beanPanel.setBean(a01);
            } else {
                beanPanel.setBean(new A01());
            }
            beanPanel.setShow_scheme(ftable_user.getCur_show_scheme());
            beanPanel.setFields(ftable_user.getFields());
            beanPanel.bind();
        } else {
            if (logCount > 0) {
                return;
            }
            logCount++;
            if (lu == null) {
                ftable_log.deleteAllRows();
            } else {
                fetchUserLog(lu, null, null);
            }
        }
    }

    /**
     * 该方法用于查询指定用户指定查询方案下的日志记录
     * @param lu：指定用户
     * @param qs：指定查询方案，为NULL时表示未使用查询方案
     */
    private void fetchUserLog(LoginUser lu, QueryScheme qs, String s_where) {
        if (lu == null) {
            ftable_log.deleteAllRows();
            return;
        }
        ftable_log.setCur_query_scheme(qs);
        String sql = "select logInfo_key from LogInfo lu where person_key='" + lu.getUser_code() + "' ";
        if (qs != null) {
            sql += " and lu.logInfo_key in(" + qs.buildSql() + ") ";
        }
        if (s_where != null && !s_where.trim().equals("")) {
            sql += " and (" + s_where + ")";
        }
        sql += " and lu.log_date between " + DateUtil.toStringForQuery(jdpStart.getDate(), "yyyy-MM-dd HH:mm:ss") + " and "
                + DateUtil.toStringForQuery(DateUtil.getNextDay(jdpEnd.getDate()));
        sql += " order by " + log_order_sql;
        PublicUtil.getProps_value().setProperty(LogInfo.class.getName(), "from LogInfo li where li.logInfo_key in");
        ftable_log.setObjects(CommUtil.selectSQL(sql));
        refresh();
    }

    /**
     * 该方法用于查询当前部门的系统用户,并根据需要决定是否仅显示当前在线用户
     * @param selected：是否仅显示当前在线用户
     */
    private void fetchUserState(boolean selected) {
        initCount();
        users.clear();
        if (curDept != null) {
            List list = CommImpl.getLoginUsers(curDept, UserContext.getDept_right_rea_str("d"));
            if (selected) {
                for (Object obj : list) {
                    LoginUser lu = (LoginUser) obj;
                    if (lu.getUser_state().equals("在线")) {
                        users.add(lu);
                    }
                }
            } else {
                users.addAll(list);
            }
        }
        userBinding.unbind();
        userBinding.bind();
        if (users.size() > 0) {
            jlsUser.setSelectedIndex(0);
        } else {
            fetchUserInfo(null);
        }
        jlsUser.updateUI();
        refresh();
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
        btnRefresh = new javax.swing.JButton();
        btnMsg = new javax.swing.JButton();
        btnQuit = new javax.swing.JButton();
        jcbOnline = new javax.swing.JCheckBox();
        btnField = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        pnlUser = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlUserInfo = new javax.swing.JPanel();
        pnlUserLog = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOneTouchExpandable(true);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        btnRefresh.setText("刷新");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnRefresh);

        btnMsg.setText("发送通知");
        btnMsg.setFocusable(false);
        btnMsg.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMsg.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnMsg);

        btnQuit.setText("强制下线");
        btnQuit.setFocusable(false);
        btnQuit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnQuit);

        jcbOnline.setSelected(true);
        jcbOnline.setText("仅显示在线用户");
        jcbOnline.setFocusable(false);
        jcbOnline.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jcbOnline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(jcbOnline);

        btnField.setText("设置显示字段");
        btnField.setFocusable(false);
        btnField.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnField.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnField);

        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setDividerSize(2);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlUser.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setTopComponent(pnlUser);

        pnlUserInfo.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("用户信息", pnlUserInfo);

        pnlUserLog.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("操作日志", pnlUserLog);

        jSplitPane2.setRightComponent(jTabbedPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void setFunctionRight() {
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
        String msg = "当前用户数：" + users.size();
        if (jTabbedPane1.getSelectedIndex() == 1) {
            msg += " 当前日志记录数：" + ftable_log.getObjects().size();

        }
        ContextManager.setStatusBar(1, msg);
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnField;
    private javax.swing.JButton btnMsg;
    private javax.swing.JButton btnQuit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox jcbOnline;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlUser;
    private javax.swing.JPanel pnlUserInfo;
    private javax.swing.JPanel pnlUserLog;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
