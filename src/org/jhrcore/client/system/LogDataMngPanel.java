/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogDataMngPanel.java
 *
 * Created on 2013-4-22, 15:26:58
 */
package org.jhrcore.client.system;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.system.comm.LogFieldDefinePnl;
import org.jhrcore.comm.HrLog;
import org.jhrcore.entity.base.EntityClass;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.LogData;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author mxliteboss
 */
public class LogDataMngPanel extends javax.swing.JPanel implements IModulePanel {

    private List<TempFieldInfo> logInfos;
    private FTable ftable;
    private String order_sql = "li.logDate desc";
    public static final String module_code = "SysLogData";
    private JButton btnDefine = new JButton("设置日志字段");
    private JButton btnDel = new JButton("删除");
    private JhrDatePicker jdpStart = new JhrDatePicker();
    private JhrDatePicker jdpEnd = new JhrDatePicker();
    private JLabel lblStart = new JLabel(" 查询日期起: ");
    private JLabel lblEnd = new JLabel(" 止: ");
    private JLabel lblSearch = new JLabel(" 查找: ");
    private JCheckBox jcbCurColumn = new JCheckBox("当前列");
    private JTextField jtfSearch = new JTextField();
    private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
    private JTree moduleTree;
    private HrLog log = new HrLog(module_code);

    /** Creates new form LogDataMngPanel */
    public LogDataMngPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        List modules = CommImpl.getSysModule(true, true, false);//CommUtil.fetchEntities("from ModuleInfo mi left join fetch mi.entityClasss ec left join fetch ec.entityDefs order by mi.order_no");
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("所有模块");
        for (Object obj : modules) {
            ModuleInfo mi = (ModuleInfo) obj;
            DefaultMutableTreeNode miNode = new DefaultMutableTreeNode(mi);
            node.add(miNode);
            for (EntityClass ec : mi.getEntityClasss()) {
                DefaultMutableTreeNode ecNode = new DefaultMutableTreeNode(ec);
                for (EntityDef ed : ec.getEntityDefs()) {
                    ecNode.add(new DefaultMutableTreeNode(ed));
                }
                miNode.add(ecNode);
            }
        }
        moduleTree = new JTree(node);
        HRRendererView.getRebuildMap(moduleTree).initTree(moduleTree);
        pnlLeft.add(new JScrollPane(moduleTree));
        logInfos = EntityBuilder.getCommFieldInfoListOf(LogData.class, EntityBuilder.COMM_FIELD_VISIBLE);
        ftable = new FTable(LogData.class, true, true, false, module_code);
        ftable.setRight_allow_flag(true);
        ftable.removeSumAndReplaceItem();
        ftable.setAll_fields(logInfos, logInfos, module_code);
        mainPnl.add(new JScrollPane(ftable), BorderLayout.CENTER);
        order_sql = SysUtil.getOrderString(ftable.getCurOrderScheme(), "li", order_sql, logInfos);
        initBar();
    }

    private void initBar() {
        jdpStart.setDate(DateUtil.getCurMonthFirstDay());
        jdpEnd.setDate(new Date());
        jToolBar2.add(lblStart);
        jToolBar2.add(jdpStart);
        jToolBar2.add(lblEnd);
        jToolBar2.add(jdpEnd);
        jToolBar2.add(lblSearch);
        jToolBar2.add(jtfSearch);
        jToolBar2.add(jcbCurColumn);
        jToolBar2.add(btnSearch);
        jToolBar2.add(btnDel);
        jToolBar2.add(btnDefine);
        ComponentUtil.setSize(jtfSearch, 120, 22);
        ComponentUtil.setSize(btnSearch, 24, 24);
    }

    private void setupEvents() {
        moduleTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                fetchMain(null, null);
            }
        });
        ftable.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                order_sql = SysUtil.getOrderString(showScheme, "li", order_sql, logInfos);
                fetchMain(ftable.getCur_query_scheme(), null);
            }
        });

        ftable.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchMain(qs, null);
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quickSearch();
            }
        };
        btnSearch.addActionListener(al);
        jtfSearch.addActionListener(al);
        btnDefine.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LogFieldDefinePnl pnl = new LogFieldDefinePnl();
                ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, btnDefine.getText());
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> keys = ftable.getSelectKeys();
                if (keys.isEmpty()) {
                    return;
                }
                if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(),
                        "确定要删除选中的记录吗", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
                ValidateSQLResult vs = CommUtil.deleteObjs("LogData", "LogData_key", keys);
                if (vs.getResult() == 0) {
                    ftable.deleteSelectedRows();
                    JOptionPane.showMessageDialog(null, "删除成功");
                    log.info("删除日志成功");
                } else {
                    MsgUtil.showHRDelErrorMsg(vs);
                }
            }
        });
        ComponentUtil.initTreeSelection(moduleTree);
    }

    /**
     * 界面查询主函数
     * @param qs：查询方案，为NULL表示未使用查询方案
     * @param s_where：快速定位条件
     */
    private void fetchMain(QueryScheme qs, String s_where) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) moduleTree.getLastSelectedPathComponent();
        if (node == null || node.getUserObject() == null) {
            return;
        }
        Object obj = node.getUserObject();
        String key = "";
        if (obj instanceof EntityDef) {
            key = ",'" + ((EntityDef) obj).getEntityName() + "'";
        } else {
            Enumeration enumt = node.breadthFirstEnumeration();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode cnode = (DefaultMutableTreeNode) enumt.nextElement();
                if (cnode.getUserObject() instanceof EntityDef) {
                    key += ",'" + ((EntityDef) cnode.getUserObject()).getEntityName() + "'";
                }
            }
        }
        if (key.equals("")) {
            return;
        }
        String hql = "select logData_key from LogData li where li.logTable in(" + key.substring(1) + ")";
        hql += " and li.logDate between " + DateUtil.toStringForQuery(jdpStart.getDate(), "yyyy-MM-dd HH:mm:ss") + " and " + DateUtil.toStringForQuery(DateUtil.getNextDay(jdpEnd.getDate()));
        if (s_where != null && !s_where.trim().equals("")) {
            hql += " and (" + s_where + ")";
        }
        if (qs != null) {
            ftable.setCur_query_scheme(qs);
            hql = qs.buildHql(hql, "li");
        }
        if (!UserContext.isSA) {
            hql += " and li.person_key='" + UserContext.person_key + "'";
        }
        hql += "order by " + order_sql;
        PublicUtil.getProps_value().setProperty(LogData.class.getName(), "from LogData where logData_key in ");
        ftable.setObjects(CommUtil.fetchEntities(hql));
        this.refresh();
    }

    private void quickSearch() {
        String val = jtfSearch.getText().trim().toUpperCase();
        if (val.equals("")) {
            fetchMain(ftable.getCur_query_scheme(), null);
            return;
        }
        val = SysUtil.getQuickSearchText(val);
        String s_where = "";
        if (jcbCurColumn.isSelected()) {
            s_where = ftable.getQuickSearchSQL("li", val);
        } else {
            s_where = SysUtil.getQuickSearchSQL("beforestate;afterstate", val);
        }
        fetchMain(ftable.getCur_query_scheme(), s_where);
    }

    @Override
    public void setFunctionRight() {
        ComponentUtil.setSysFuntionNew(this);
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
        ContextManager.setStatusBar(ftable.getObjects().size());
    }

    @Override
    public String getModuleCode() {
        return module_code;
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
        jToolBar2 = new javax.swing.JToolBar();
        mainPnl = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(150);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        mainPnl.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
            .addComponent(mainPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel mainPnl;
    private javax.swing.JPanel pnlLeft;
    // End of variables declaration//GEN-END:variables
}
