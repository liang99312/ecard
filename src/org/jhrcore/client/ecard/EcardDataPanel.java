/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.ecard.ui.CardPanel;
import org.jhrcore.entity.ecard.Ecard;
import org.jhrcore.entity.ecard.Ecard_chu;
import org.jhrcore.entity.ecard.Ecard_ru;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Jane
 */
public class EcardDataPanel extends javax.swing.JPanel {

    private CardPanel cardPanel;
    private FTable ftable;
    private FTable ftable2;
    private Object curTreeNode;
    private BeanPanel ebeanPanel = new BeanPanel();
    public static final String module_code = "EcardHkPanel";
    private SimpleDateFormat format = new SimpleDateFormat("MM-dd");
    private int hk = 0, xf = 0;
    private String curYm = "";

    public EcardDataPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        tf_ym.setText(DateUtil.DateToStr(new Date(), "yyyyMM"));
        cardPanel = new CardPanel();
        pnlCard.add(cardPanel, BorderLayout.CENTER);
        pnlBeanCard.add(new JScrollPane(ebeanPanel));
    }

    private void setupEvents() {
        btnSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!curYm.equals(tf_ym.getText())) {
                    hk = 0;
                    xf = 0;
                    curYm = tf_ym.getText();
                }
                fetchData();
            }
        });
        btnExcel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabPanel.getSelectedIndex() == 1) {
                    ftable2.exportData();
                } else {
                    ftable.exportData();
                }
            }
        });
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
                    hk = 0;
                    xf = 0;
                    fetchData();
                }
            }
        });
        tabPanel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                fetchData();
            }
        });
    }

    private void fetchData() {
        if (tabPanel.getSelectedIndex() == 1) {
            fetchData_xf();
        } else {
            fetchData_hk();
        }
    }

    private void fetchData_hk() {
        if (hk > 0) {
            return;
        }
        if (curTreeNode instanceof DefaultMutableTreeNode) {
            pnlHk.removeAll();
            String ym = tf_ym.getText();
            if (!SysUtil.check_month(ym)) {
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

            String sql = " from Ecard_ru e where e.ru_ym='" + ym + "' ";
            sql += where_sql;
            sql = sql + " order by e.ecard_key,e.ru_date";
            List dataList = CommUtil.fetchEntities(sql);
            if (dataList.isEmpty()) {
                pnlHk.updateUI();
                return;
            }
            Date maxDate = null;
            Date minDate = DateUtil.StrToDate(ym + "01", "yyyyMMdd");
            List keys = new ArrayList<String>();
            Hashtable<String, Integer> djTable = new Hashtable<String, Integer>();
            List<Ecard_ru> ruList = new ArrayList<Ecard_ru>();
            for (Object obj : dataList) {
                Ecard_ru ru = (Ecard_ru) obj;
                String d_key = DateUtil.DateToStr(ru.getRu_date(), "yyyy-MM-dd");
                if (djTable.containsKey(d_key)) {
                    Integer f = (Integer) djTable.get(d_key);
                    f = f + ru.getRu_je();
                    djTable.remove(d_key);
                    djTable.put(d_key, f);
                } else {
                    djTable.put(d_key, ru.getRu_je());
                }
                if (maxDate == null || maxDate.before(ru.getRu_date())) {
                    maxDate = ru.getRu_date();
                }
                if (!keys.contains(ru.getEcard_key())) {
                    ru.setRu_zonge(ru.getRu_je());
                    ruList.add(ru);
                    keys.add(ru.getEcard_key());
                } else {
                    Ecard_ru temp_ru = ruList.get(keys.indexOf(ru.getEcard_key()));
                    temp_ru.setRu_zonge(temp_ru.getRu_zonge() + ru.getRu_je());
                }
            }
            List headerList = new ArrayList<String>();
            headerList.add("账户名称");
            headerList.add("卡号");
            headerList.add("银行");
            headerList.add("年月");
            headerList.add("汇款总额");
            Calendar c = Calendar.getInstance();
            c.setTime(minDate);
            maxDate = DateUtil.getNextDay(maxDate);
            int hs = 5;
            List<String> days = new ArrayList<String>();
            while (!c.getTime().after(maxDate)) {
                headerList.add(format.format(c.getTime()));
                hs++;
                days.add(DateUtil.DateToStr(c.getTime(), "yyyy-MM-dd"));
                c.add(Calendar.DATE, 1);
            }
            List objects = new ArrayList();
            int sum = 0;
            for (Ecard_ru ru : ruList) {
                Object[] objs = new Object[hs];
                objs[0] = ru.getEcard_name();
                objs[1] = ru.getEcard_code();
                objs[2] = ru.getEcard_bank();
                objs[3] = ym;
                objs[4] = ru.getRu_zonge();
                sum += ru.getRu_zonge();
                objects.add(objs);
            }
            for (Object obj : dataList) {
                Ecard_ru ru = (Ecard_ru) obj;
                Object[] objs = (Object[]) objects.get(keys.indexOf(ru.getEcard_key()));
                objs[5 + days.indexOf(DateUtil.DateToStr(ru.getRu_date(), "yyyy-MM-dd"))] = ru.getRu_je();
            }
            Object[] objs = new Object[hs];
            objs[0] = "合计：";
            objs[1] = "计数："+objects.size();
            objs[4] = sum;
            for (String k : djTable.keySet()) {
                Integer f = djTable.get(k);
                int index = days.indexOf(k);
                if (index > -1) {
                    objs[5 + index] = f;
                }
            }
            objects.add(objs);
            ftable = new FTable(headerList);
            ftable.setObjects(objects);
            pnlHk.setLayout(new BorderLayout());
            pnlHk.add(ftable, BorderLayout.CENTER);
            pnlHk.updateUI();
            ftable.setRight_allow_flag(false);
            refreshStatus();
            hk++;
        }
    }

    private void fetchData_xf() {
        if (xf > 0) {
            return;
        }
        if (curTreeNode instanceof DefaultMutableTreeNode) {
            pnlXf.removeAll();
            String ym = tf_ym.getText();
            if (!SysUtil.check_month(ym)) {
                JOptionPane.showMessageDialog(null, "请输入正确的年月");
                return;
            }
            String where_sql = "";
            Object t_obj = ((DefaultMutableTreeNode) curTreeNode).getUserObject();
            if (t_obj instanceof String) {
                if ("所有卡".equals(t_obj.toString())) {

                } else {
                    where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '" + t_obj.toString() + "')";
                }
            } else if (t_obj instanceof Ecard) {
                Ecard e = (Ecard) t_obj;
                where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
            }

            String sql = " from Ecard_chu e where e.chu_ym='" + ym + "' ";
            sql += where_sql;
            sql = sql + " order by e.ecard_key,e.chu_date";
            List dataList = CommUtil.fetchEntities(sql);
            if (dataList.isEmpty()) {
                pnlXf.updateUI();
                return;
            }
            Date maxDate = null;
            Date minDate = DateUtil.StrToDate(ym + "01", "yyyyMMdd");
            List keys = new ArrayList<String>();
            List<Ecard_chu> chuList = new ArrayList<Ecard_chu>();
            for (Object obj : dataList) {
                Ecard_chu chu = (Ecard_chu) obj;
                float tempFl = Float.parseFloat(chu.getChu_fl());
                chu.setChu_sxf(tempFl * chu.getChu_je());
                if (maxDate == null || maxDate.before(chu.getChu_date())) {
                    maxDate = chu.getChu_date();
                }
                if (!keys.contains(chu.getEcard_key())) {
                    chu.setChu_zonge(chu.getChu_je());
                    chu.setChu_sxf(chu.getChu_sxf());
                    chuList.add(chu);
                    keys.add(chu.getEcard_key());
                } else {
                    Ecard_chu temp_chu = chuList.get(keys.indexOf(chu.getEcard_key()));
                    temp_chu.setChu_zonge(temp_chu.getChu_zonge() + chu.getChu_je());
                    temp_chu.setChu_sxf(temp_chu.getChu_sxf() + chu.getChu_sxf());
                }
            }
            List headerList = new ArrayList<String>();
            headerList.add("账户名称");
            headerList.add("卡号");
            headerList.add("银行");
            headerList.add("年月");
            headerList.add("消费总额");
            headerList.add("手续费总额");
            Calendar c = Calendar.getInstance();
            c.setTime(minDate);
            maxDate = DateUtil.getNextDay(maxDate);
            int hs = 6;
            List<String> days = new ArrayList<String>();
            while (!c.getTime().after(maxDate)) {
                headerList.add(format.format(c.getTime()));
                hs++;
                days.add(DateUtil.DateToStr(c.getTime(), "yyyy-MM-dd"));
                c.add(Calendar.DATE, 1);
            }
            List objects = new ArrayList();
            for (Ecard_chu chu : chuList) {
                Object[] objs = new Object[hs];
                objs[0] = chu.getEcard_name();
                objs[1] = chu.getEcard_code();
                objs[2] = chu.getEcard_bank();
                objs[3] = ym;
                objs[4] = Math.floor(chu.getChu_zonge());
                objs[5] = chu.getChu_sxf();
                objects.add(objs);
            }
            for (Object obj : dataList) {
                Ecard_chu chu = (Ecard_chu) obj;
                Object[] objs = (Object[]) objects.get(keys.indexOf(chu.getEcard_key()));
                objs[6 + days.indexOf(DateUtil.DateToStr(chu.getChu_date(), "yyyy-MM-dd"))] = chu.getChu_je() + "/" + chu.getChu_fl() + "/" + chu.getChu_item() + chu.getEpos_code();
            }

            ftable2 = new FTable(headerList);
            ftable2.setObjects(objects);

            pnlXf.setLayout(new BorderLayout());
            pnlXf.add(ftable2, BorderLayout.CENTER);
            pnlXf.updateUI();
            ftable2.setRight_allow_flag(false);
            refreshStatus();
            xf++;
        }
    }

    private void refreshStatus() {
        if (tabPanel.getSelectedIndex() == 0) {
            ContextManager.setStatusBar(ftable.getObjects().size());
        } else if (tabPanel.getSelectedIndex() == 1) {
            ContextManager.setStatusBar(ftable2.getObjects().size());
        }
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
        jLabel1 = new javax.swing.JLabel();
        tf_ym = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        pnlTable = new javax.swing.JPanel();
        tabPanel = new javax.swing.JTabbedPane();
        pnlHk = new javax.swing.JPanel();
        pnlXf = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlBeanCard = new javax.swing.JPanel();
        pnlCard = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOneTouchExpandable(true);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        jLabel1.setText(" 年月：");
        toolBar.add(jLabel1);

        tf_ym.setText("201601");
        tf_ym.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        tf_ym.setMinimumSize(new java.awt.Dimension(60, 21));
        tf_ym.setName(""); // NOI18N
        tf_ym.setPreferredSize(new java.awt.Dimension(60, 21));
        toolBar.add(tf_ym);

        btnSearch.setText("查询");
        btnSearch.setFocusable(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnSearch);

        btnExcel.setText("导出Excel");
        btnExcel.setFocusable(false);
        btnExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnExcel);

        jSplitPane2.setDividerLocation(340);
        jSplitPane2.setDividerSize(3);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlHk.setLayout(new java.awt.BorderLayout());
        tabPanel.addTab("汇款数据", pnlHk);

        pnlXf.setLayout(new java.awt.BorderLayout());
        tabPanel.addTab("消费数据", pnlXf);

        javax.swing.GroupLayout pnlTableLayout = new javax.swing.GroupLayout(pnlTable);
        pnlTable.setLayout(pnlTableLayout);
        pnlTableLayout.setHorizontalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPanel)
        );
        pnlTableLayout.setVerticalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPanel)
        );

        jSplitPane2.setTopComponent(pnlTable);

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
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnlBeanCard;
    private javax.swing.JPanel pnlCard;
    private javax.swing.JPanel pnlHk;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlXf;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTextField tf_ym;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
