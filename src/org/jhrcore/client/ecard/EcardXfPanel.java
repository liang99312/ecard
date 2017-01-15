/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard;

import com.foundercy.pf.control.listener.IPickColumnSumListener;
import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickFieldSetListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.ecard.ui.CalcEcardRuPanel;
import org.jhrcore.client.ecard.ui.CardPanel;
import org.jhrcore.client.ecard.ui.EditChuPanel;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.ecard.Ecard;
import org.jhrcore.entity.ecard.Ecard_chu;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.CommEditAction;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Jane
 */
public class EcardXfPanel extends javax.swing.JPanel {

    private CardPanel cardPanel;
    private FTable ftable;
    private List<TempFieldInfo> all_infos = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> default_infos = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> default_orders = new ArrayList<TempFieldInfo>();
    private String order_sql = "e.ecard_code,e.chu_date";
    private QueryScheme qs;
    private String sum_sql;
    private Object curTreeNode;
    private Object curEcard_chu;
    private BeanPanel beanPanel = new BeanPanel();
    private BeanPanel ebeanPanel = new BeanPanel();
    private boolean editable = false;
    private boolean editStyle = false;//false:网格；true：卡片
    public static final String module_code = "EcardXfPanel";

    public EcardXfPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        tf_ym_from.setText(DateUtil.DateToStr(new Date(), "yyyyMM"));
        tf_ym_to.setText(DateUtil.DateToStr(new Date(), "yyyyMM"));
        cardPanel = new CardPanel();
        pnlCard.add(cardPanel, BorderLayout.CENTER);
        ftable = new FTable(Ecard_chu.class, true, true, true, "EcardXfPanel");
        List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_chu.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : shift_infos) {
            default_infos.add(tfi);
            all_infos.add(tfi);
        }
        ftable.setAll_fields(all_infos, default_infos, default_orders, "EcardXfPanel");
        order_sql = SysUtil.getSQLOrderString(ftable.getCurOrderScheme(), order_sql, all_infos);
        ftable.setRight_allow_flag(true);
        pnlTable.add(ftable);

        pnlBeanChu.add(new JScrollPane(beanPanel));
        pnlBeanCard.add(new JScrollPane(ebeanPanel));
    }

    private void setupEvents() {
        cardPanel.getCardTree().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                curTreeNode = e.getPath().getLastPathComponent();
                if (curTreeNode != null) {
                    if (((DefaultMutableTreeNode) curTreeNode).getUserObject() instanceof Ecard) {
                        Ecard temp_card = (Ecard) ((DefaultMutableTreeNode) curTreeNode).getUserObject();
                        ebeanPanel.setBean(temp_card);
                        ebeanPanel.bind();
                    }
                    fetchData(null);
                }
            }
        });
        ftable.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                CommEditAction.doRowSaveAction(curEcard_chu, editable);
                if (curEcard_chu == ftable.getCurrentRow()) {
                    return;
                }
                curEcard_chu = ftable.getCurrentRow();
                BeanPanel.refreshUIForTable(ftable, beanPanel, editable && editStyle);
                ContextManager.setStatusBar(ftable.getObjects().size());
            }
        });
        ftable.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                order_sql = SysUtil.getSQLOrderString(showScheme, order_sql, all_infos);
                fetchData(ftable.getCur_query_scheme());
            }
        });
        ftable.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchData(qs);
            }
        });
        ftable.addPickFieldSetListener(new IPickFieldSetListener() {

            @Override
            public void pickField(ShowScheme showScheme) {
                BeanPanel.refreshUIForTable(ftable, beanPanel, editable && editStyle);
            }
        });
        ftable.addPickColumnSumListener(new IPickColumnSumListener() {

            @Override
            public String pickSumSQL() {
                return sum_sql + "@sql";
            }
        });
        ftable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    if (curEcard_chu == null) {
                        return;
                    }
                    EditChuPanel pnl = new EditChuPanel(curEcard_chu);
                    ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
                    mf.addIPickWindowCloseListener(new IPickWindowCloseListener() {

                        @Override
                        public void pickClose() {
                            fetchData(null);
                        }
                    });
                    mf.setVisible(true);
                }
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CalcEcardRuPanel pnl = new CalcEcardRuPanel("xf");
                ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "生成消费数据", 380, 220, false);
                mf.addIPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        fetchData(null);
                    }
                });
                mf.setVisible(true);
            }
        });
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(curEcard_chu == null){
                    return;
                }
                EditChuPanel pnl = new EditChuPanel(curEcard_chu);
                ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
                mf.addIPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        fetchData(null);
                    }
                });
                mf.setVisible(true);
            }
        });
        btnSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fetchData(null);
            }
        });
    }

    private void fetchData(QueryScheme qs) {
        if (curTreeNode instanceof DefaultMutableTreeNode) {
            String ym_from = tf_ym_from.getText();
            String ym_to = tf_ym_to.getText();
            if(!SysUtil.check_month(ym_to) || !SysUtil.check_month(ym_from)){
                JOptionPane.showMessageDialog(null, "请输入正确的年月");
                return;
            }
            String where_sql = "";
            Object t_obj = ((DefaultMutableTreeNode) curTreeNode).getUserObject();
            if (t_obj instanceof String) {
                if ("所有卡".equals(t_obj.toString())) {
                    
                }else if("已激活".equals(t_obj.toString())){
                    where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
                }else if("已停止".equals(t_obj.toString())){
                    where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
                }else if("普养".equals(t_obj.toString())){
                    DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) curTreeNode).getParent();
                    if("已激活".equals(p_node.getUserObject().toString())){
                        where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
                    }else{
                        where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
                    }
                }else if("中养".equals(t_obj.toString())){
                    DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) curTreeNode).getParent();
                    if("已激活".equals(p_node.getUserObject().toString())){
                        where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
                    }else{
                        where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
                    }
                }else if("精养".equals(t_obj.toString())){
                    DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) curTreeNode).getParent();
                    if("已激活".equals(p_node.getUserObject().toString())){
                        where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
                    }else{
                        where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
                    }
                }
            } else if (t_obj instanceof Ecard) {
                Ecard e = (Ecard) t_obj;
                where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
            }

            String sql = " from Ecard_chu e where 1=1";
            sql = sql + " and chu_ym >='" + ym_from + "' and chu_ym<='" + ym_to + "'";
            if (qs != null) {
                sql += " and e.ecard_chu_key in(" + qs.buildSql() + ")";
            }
            sql += where_sql;
            sum_sql = sql;
            sql = "select ecard_chu_key" + sql + " order by " + order_sql;
            PublicUtil.getProps_value().setProperty(Ecard_chu.class.getName(), "from Ecard_chu e where e.ecard_chu_key in");
            List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
            ftable.setObjects(list);
            refreshStatus();
        }
    }

    private void refreshStatus() {
        ContextManager.setStatusBar(ftable.getObjects().size());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tf_ym_from = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tf_ym_to = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        pnlTable = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlBeanChu = new javax.swing.JPanel();
        pnlBeanCard = new javax.swing.JPanel();
        pnlCard = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOneTouchExpandable(true);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        btnAdd.setText("生成数据");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnAdd);

        btnEdit.setText("微调");
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnEdit);

        jLabel1.setText(" 年月起：");
        toolBar.add(jLabel1);

        tf_ym_from.setText("201601");
        tf_ym_from.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        tf_ym_from.setMinimumSize(new java.awt.Dimension(60, 21));
        tf_ym_from.setName(""); // NOI18N
        tf_ym_from.setPreferredSize(new java.awt.Dimension(60, 21));
        toolBar.add(tf_ym_from);

        jLabel2.setText(" 止：");
        toolBar.add(jLabel2);

        tf_ym_to.setText("201601");
        tf_ym_to.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        tf_ym_to.setMinimumSize(new java.awt.Dimension(60, 21));
        tf_ym_to.setName(""); // NOI18N
        tf_ym_to.setPreferredSize(new java.awt.Dimension(60, 21));
        toolBar.add(tf_ym_to);

        btnSearch.setText("查询");
        btnSearch.setFocusable(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnSearch);

        jSplitPane2.setDividerLocation(340);
        jSplitPane2.setDividerSize(3);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlTable.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setTopComponent(pnlTable);

        pnlBeanChu.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("汇款信息", pnlBeanChu);

        pnlBeanCard.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("信用卡信息", pnlBeanCard);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jSplitPane2.setRightComponent(jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane2)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane2))
        );

        jSplitPane1.setRightComponent(jPanel1);

        pnlCard.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlCard);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnlBeanCard;
    private javax.swing.JPanel pnlBeanChu;
    private javax.swing.JPanel pnlCard;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTextField tf_ym_from;
    private javax.swing.JTextField tf_ym_to;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
