/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard.ui;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.ecard.Ecard;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Jane
 */
public class CardSelectDlg extends javax.swing.JDialog {

    private FTable ftable,ftable2;
    private List<TempFieldInfo> all_infos = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> default_infos = new ArrayList<TempFieldInfo>();
    private String order_sql = "ecard_code";
    private JLabel jLabel7 = new JLabel(" 查找：");
    private JTextField comBoxSearch = new JTextField();
    private JCheckBox chbCurColumn = new JCheckBox("当前列", false);
    private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
    private List<String> select_keys = new ArrayList<String>();
    private boolean isOk = false;
    
    public boolean isOk(){
        return isOk;
    }
    
    public List<String> getSelectKeys(){
        return select_keys;
    }
    
    public CardSelectDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CardSelectDlg() {
        initComponents();
        initOthers();
        setupEvents();
    }
    
    private void initOthers(){
        initTools();
        ftable = new FTable(Ecard.class, true, true, true);
        ftable2 = new FTable(Ecard.class, true, true, true);
        List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : shift_infos) {
            default_infos.add(tfi);
            all_infos.add(tfi);
        }
        ftable.setAll_fields(all_infos, default_infos, new ArrayList<TempFieldInfo>(), "");
        ftable2.setAll_fields(all_infos, default_infos, new ArrayList<TempFieldInfo>(), "");
        ftable.setRight_allow_flag(true);
        pnlTable.add(ftable);
        pnlTable2.add(ftable2);
        fetchMainData(null,"");
    }
    
    private void initTools(){
        toolBar.add(jLabel7);
        toolBar.add(comBoxSearch);
        toolBar.add(btnSearch);
        toolBar.add(chbCurColumn);
        ComponentUtil.setSize(comBoxSearch, 120, 22);
        ComponentUtil.setSize(btnSearch, 22, 22);
    }
    
    private void setupEvents(){
        ActionListener search_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doQuickSearch(comBoxSearch.getText());
            }
        };
        btnSearch.addActionListener(search_listener);
        comBoxSearch.addActionListener(search_listener);
        cb_state.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fetchMainData(ftable.getCur_query_scheme(), null);
            }
        });
        ftable.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                order_sql = SysUtil.getSQLOrderString(showScheme, order_sql, all_infos);
                fetchMainData(ftable.getCur_query_scheme(), null);
            }
        });
        ftable.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                fetchMainData(qs, null);
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(ftable.getSelectObjects().isEmpty()){
                    JOptionPane.showMessageDialog(null, "请选择信用卡");
                    return;
                }
                for(Object obj:ftable.getSelectObjects()){
                    Ecard ecard = (Ecard) obj;
                    select_keys.add(ecard.getEcard_key());
                }
                ftable2.addObjects(ftable.getSelectObjects());
                ftable.deleteSelectedRows();
                
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(ftable2.getSelectObjects().isEmpty()){
                    JOptionPane.showMessageDialog(null, "请选择信用卡");
                    return;
                }
                for(Object obj:ftable2.getSelectObjects()){
                    Ecard ecard = (Ecard) obj;
                    select_keys.remove(ecard.getEcard_key());
                }
                ftable2.deleteSelectedRows();
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isOk = false;
                dispose();
            }
        });
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(select_keys.isEmpty()){
                    JOptionPane.showMessageDialog(null, "没有选择信用卡");
                    return;
                }
                isOk = true;
                dispose();
            }
        });
    }
    
    private void doQuickSearch(String text) {
        if (text == null || text.trim().equals("")) {
            return;
        }
        text = SysUtil.getQuickSearchText(text);
        String s_where = "";
        if (chbCurColumn.isSelected()) {
            s_where = ftable.getQuickSearchSQL("Ecard", text);
        } else {
            s_where = "(upper(ecard_code) like '" + text + "%' or upper(ecard_name) like '" + text + "%')";
        }
        fetchMainData(ftable.getCur_query_scheme(), s_where);
    }
    
    private void fetchMainData(QueryScheme qs, String s_where) {
        String state = cb_state.getSelectedItem().toString();
        ftable.setCur_query_scheme(qs);
        String sql = " from Ecard where 1=1";
        if (qs != null) {
            sql += " and Ecard.ecard_key in(" + qs.buildSql() + ")";
        }
        if (s_where != null && !s_where.trim().equals("")) {
            sql += " and (" + s_where + ")";
        }
        if(!state.equals("所有卡")){
            sql += " and ecard_state = '"+state+"'";
        }
        sql = "select ecard_key" + sql + " order by " + order_sql;
        PublicUtil.getProps_value().setProperty(Ecard.class.getName(), "from Ecard n where n.ecard_key in");
        List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
        list.removeAll(select_keys);
        ftable.setObjects(list);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        cb_state = new javax.swing.JComboBox();
        pnlTable = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        pnlTable2 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setMaximumSize(new java.awt.Dimension(1240, 24));
        toolBar.setMinimumSize(new java.awt.Dimension(106, 24));
        toolBar.setPreferredSize(new java.awt.Dimension(106, 24));

        jLabel1.setText("状态：");
        toolBar.add(jLabel1);

        cb_state.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "已激活", "已停止", "所有卡" }));
        cb_state.setMaximumSize(new java.awt.Dimension(80, 23));
        toolBar.add(cb_state);

        pnlTable.setPreferredSize(new java.awt.Dimension(300, 196));
        pnlTable.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
            .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAdd.setText("添加");

        btnDel.setText("删除");

        pnlTable2.setBorder(javax.swing.BorderFactory.createTitledBorder("已选信用卡"));
        pnlTable2.setLayout(new java.awt.BorderLayout());

        btnCancel.setText("关闭");

        btnOk.setText("确定 ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(btnAdd)
                .addGap(42, 42, 42)
                .addComponent(btnDel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pnlTable2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addGap(76, 76, 76)
                .addComponent(btnCancel)
                .addGap(96, 96, 96))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDel)
                    .addComponent(btnAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTable2, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox cb_state;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTable2;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
