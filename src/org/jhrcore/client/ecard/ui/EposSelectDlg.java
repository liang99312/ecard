/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard.ui;

import com.foundercy.pf.control.table.FTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.ecard.Epos;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.listener.CommEditAction;
import org.jhrcore.util.DbUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Jane
 */
public class EposSelectDlg extends javax.swing.JDialog {

    private FTable ftable;
    private Binding cb_binding;
    private boolean isOk = false;
    
    public boolean isOk(){
        return isOk;
    }
    
    public void setIsOk(){
        isOk=false;
    }
    
    public Epos getSelectObject(){
        Epos e = null;
        if(ftable.getCurrentRow() != null){
            e = (Epos) ftable.getCurrentRow();
        }
        return e;
    }
    
    
    public EposSelectDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public EposSelectDlg() {
        initComponents();
        initOthers();
        setupEvents();
    }
    
    private void initOthers() {
        bindingCb();
        ftable = new FTable(Epos.class, true, true, true);
        List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Epos.class, EntityBuilder.COMM_FIELD_VISIBLE);
        ftable.setAll_fields(shift_infos, shift_infos, shift_infos,"");
        ftable.setRight_allow_flag(true);
        pnlTable.add(ftable);
    }
    
    private void setupEvents() {
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(ftable.getCurrentRow() == null){
                    JOptionPane.showMessageDialog(null, "请选择Pos机");
                    return;
                }
                isOk = true;
                dispose();
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isOk = false;
                dispose();
            }
        });
        cb_fl.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fetchMainData(ftable.getCur_query_scheme(), null);
            }
        });
        ActionListener search_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doQuickSearch(tfSearch.getText());
            }
        };
        ftable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    if(ftable.getCurrentRow() == null){
                        JOptionPane.showMessageDialog(null, "请选择Pos机");
                        return;
                    }
                    isOk = true;
                    dispose();
                }
            }
        });
        btnSearch.addActionListener(search_listener);
        tfSearch.addActionListener(search_listener);
        fetchMainData(null, null);
    }
    
    private void bindingCb(){
        List flList = CommUtil.selectSQL("select distinct epos_fei from Epos");
        flList.add("所有");
        if(cb_binding == null){
            cb_binding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ, flList, cb_fl);
        }else{
            cb_binding.unbind();
        }
        cb_binding.bind();
    }
    
    private void fetchMainData(QueryScheme qs, String s_where) {
        String state = "";
        if(cb_fl.getSelectedItem() == null){
            state = "所有";
        }else{
            state = cb_fl.getSelectedItem().toString();
        }
        ftable.setCur_query_scheme(qs);
        String sql = " from Epos where 1=1";
        if (qs != null) {
            sql += " and Epos.epos_key in(" + qs.buildSql() + ")";
        }
        if (s_where != null && !s_where.trim().equals("")) {
            sql += " and (" + s_where + ")";
        }
        if (!state.equals("所有")) {
            sql += " and epos_fei = '" + state + "'";
        }
        sql = "select epos_key" + sql + " order by epos_code";
        PublicUtil.getProps_value().setProperty(Epos.class.getName(), "from Epos n where n.epos_key in");
        List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
        ftable.setObjects(list);
    }
    
    private void doQuickSearch(String text) {
        if (text == null || text.trim().equals("")) {
            return;
        }
        text = SysUtil.getQuickSearchText(text);
        String s_where = "";
        s_where = "(upper(epos_code) like '" + text + "%' or upper(ecard_name) like '" + text + "%')";
        fetchMainData(ftable.getCur_query_scheme(), s_where);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTable = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        cb_fl = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tfSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        pnlTable.setLayout(new java.awt.BorderLayout());

        btnOk.setText("确定");

        btnCancel.setText("关闭");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel2.setText(" 费率：");
        jToolBar1.add(jLabel2);

        cb_fl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "已激活", "已停止", "所有卡" }));
        cb_fl.setMaximumSize(new java.awt.Dimension(80, 23));
        jToolBar1.add(cb_fl);

        jLabel1.setText("  查询：");
        jToolBar1.add(jLabel1);

        tfSearch.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        tfSearch.setMinimumSize(new java.awt.Dimension(120, 21));
        tfSearch.setPreferredSize(new java.awt.Dimension(120, 21));
        jToolBar1.add(tfSearch);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search.png"))); // NOI18N
        jToolBar1.add(btnSearch);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addGap(55, 55, 55)
                .addComponent(btnCancel)
                .addGap(49, 49, 49))
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cb_fl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTextField tfSearch;
    // End of variables declaration//GEN-END:variables
}
