/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CaculateImportA01Pnl.java
 *
 * Created on 2010-5-18, 21:50:15
 */
package org.jhrcore.client.personnel;

import com.foundercy.pf.control.listener.IPickPopupListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.personnel.comm.PersonContainer;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.A01Chg;
import org.jhrcore.entity.BasePersonChange;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.client.personnel.comm.EmpChangeAction;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author admin
 */
public class ResultImportA01Pnl extends javax.swing.JPanel implements IModuleCode {

    private FTable n_ftable;
    private FTable d_ftable;
    private FTable z_ftable;
    private FTable a0191_ftable;
    private FTable other_ftable;
    private List<String> u_keys;
    private List<String> a0191_keys;
    private JMenuItem miDel = new JMenuItem("删除人员");
    private JMenuItem miSendContainer = new JMenuItem("加入人员容器");
    private JButton btnExport = new JButton("导出新增人员");
    private JButton btnImport = new JButton("导入人员信息");
    private JButton btnRecom = new JButton("重新比对");
    private JButton btnCancel = new JButton("退出");
    private int cur_index = 0;
    private SysParameter del_para;
    private Logger log = Logger.getLogger(ResultImportA01Pnl.class.getName());
    private List<TempFieldInfo> person_default_fields;
    private List<TempFieldInfo> person_all_fields;
    private List new_a01s;
    private List<IPickPersonImportListener> listeners = new ArrayList<IPickPersonImportListener>();
    private List<String> n_fields;
    private List<DeptCode> select_depts = new ArrayList<DeptCode>();
    private String personClass;
    private JButton btnPersonChange = new JButton("人员调配");
    private Hashtable<String, JComponent> all_change_items = new Hashtable<String, JComponent>();
    private Hashtable<String, String> a0191_table;
    private String module_code = "EmpMng.mi_personInfoIn.btnCantrast";

    public void addIPickRefreshDataListenr(IPickPersonImportListener listener) {
        listeners.add(listener);
    }

    public void delIPickRefreshDataListener(IPickPersonImportListener listener) {
        listeners.remove(listener);
    }

    /**
     * Creates new form ResultImportA01Dlg
     */
    public ResultImportA01Pnl(List new_a01s, List<String> n_keys, List<String> u_keys, SysParameter del_para, List<TempFieldInfo> person_all_fields, List<TempFieldInfo> person_default_fields, List<String> n_fields) {
//        this.n_keys = n_keys;
        this.new_a01s = new_a01s;
        this.u_keys = u_keys;
        this.del_para = del_para;
        this.person_all_fields = person_all_fields;
        this.person_default_fields = person_default_fields;
        this.n_fields = n_fields;
        initComponents();
        initOthers();
        setupEvents();
    }

    public ResultImportA01Pnl() {
        initComponents();
        initOthers();
        setupEvents();
    }

    public ResultImportA01Pnl(Hashtable<String, String> a0191_table, String personClass, List<String> a0191_keys, List<DeptCode> select_depts, List new_a01s, List<String> n_keys, List<String> u_keys, List<TempFieldInfo> person_all_fields, List<TempFieldInfo> person_default_fields, List<String> n_fields) {
//        this.n_keys = n_keys;
        this.personClass = personClass;
        this.a0191_keys = a0191_keys;
        this.new_a01s = new_a01s;
        this.u_keys = u_keys;
        this.select_depts = select_depts;
        this.person_all_fields = person_all_fields;
        this.person_default_fields = person_default_fields;
        this.n_fields = n_fields;
        this.a0191_table = a0191_table;
        initComponents();
        initOthers();
        setupEvents();
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        n_panel = new javax.swing.JPanel();
        d_panel = new javax.swing.JPanel();
        a0191_pnl = new javax.swing.JPanel();
        pnlOther = new javax.swing.JPanel();
        z_panel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();

        n_panel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("新增人员", n_panel);

        d_panel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("不在导入表中人员", d_panel);

        a0191_pnl.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("人员类别差异人员", a0191_pnl);

        pnlOther.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("存在其他部门人员", pnlOther);

        z_panel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("在导入表中人员", z_panel);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel a0191_pnl;
    private javax.swing.JPanel d_panel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel n_panel;
    private javax.swing.JPanel pnlOther;
    private javax.swing.JPanel z_panel;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        buildTool();
        n_ftable = new FTable(A01.class, true, true, false, "Emp11");
        List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
        for (TempFieldInfo tmp_tfi : person_all_fields) {
            if (n_fields.contains(tmp_tfi.getField_name().toLowerCase())) {
                all_fields.add(tmp_tfi);
            }
        }
        n_ftable.setAll_fields(all_fields, all_fields, new ArrayList(), "Emp11");
        n_ftable.setRight_allow_flag(false);
        n_panel.add(n_ftable, BorderLayout.CENTER);
        d_ftable = new FTable(A01.class, true, true, false, "Emp");
        d_ftable.setAll_fields(person_all_fields, person_default_fields, new ArrayList(), "Emp");
        d_ftable.setRight_allow_flag(true);
        d_panel.add(d_ftable, BorderLayout.CENTER);
        z_ftable = new FTable(A01.class, true, true, false, "Em3p");
        z_ftable.setAll_fields(person_all_fields, person_default_fields, new ArrayList(), "Em3p");
        z_ftable.setRight_allow_flag(true);
        z_panel.add(z_ftable, BorderLayout.CENTER);
        a0191_ftable = new FTable(A01.class, true, true, false, "Em2p");
        a0191_ftable.setAll_fields(person_all_fields, person_default_fields, new ArrayList(), "Em2p");
        a0191_ftable.setRight_allow_flag(true);
        a0191_pnl.add(a0191_ftable, BorderLayout.CENTER);
        other_ftable = new FTable(A01.class, true, true, false, "Em2p");
        other_ftable.setAll_fields(person_all_fields, person_default_fields, new ArrayList(), "Em2p");
        other_ftable.setRight_allow_flag(true);
        pnlOther.add(other_ftable, BorderLayout.CENTER);
        String code = "query;order;sum;replace";
        n_ftable.removeItemByCodes(code);
        d_ftable.removeItemByCodes(code);
        z_ftable.removeItemByCodes(code);
        a0191_ftable.removeItemByCodes(code);
        other_ftable.removeItemByCodes(code);
        IPickPopupListener listener = new IPickPopupListener() {

            @Override
            public void addMenuItem(JPopupMenu pp) {
                pp.add(miSendContainer);
                if (cur_index == 4 || cur_index == 1) {
                    pp.add(miDel);
                }
            }
        };
        n_ftable.addPickPopupListener(listener);
        d_ftable.addPickPopupListener(listener);
        z_ftable.addPickPopupListener(listener);
        a0191_ftable.addPickPopupListener(listener);
        other_ftable.addPickPopupListener(listener);
        cantrastA01(0);

    }

    private void cantrastA01(int flag) {
        StringBuffer tmp_sb = new StringBuffer();
        String fetch_hql2 = "from A01 pba join fetch pba.deptCode where 1 = 1";
        String fetch_hql = "from A01 pba join fetch pba.deptCode where a0193 = 0 and a0191 ='" + personClass + "'";
        if (select_depts.size() > 0) {
            for (DeptCode dept : select_depts) {
                tmp_sb.append("dept_code like '" + dept.getDept_code() + "%' or ");
            }
            tmp_sb.append("1=0");
            fetch_hql += " and pba.deptCode.deptCode_key in (select deptCode_key from DeptCode where " + tmp_sb.toString() + ")";
            fetch_hql2 += " and pba.deptCode.deptCode_key in (select deptCode_key from DeptCode where " + tmp_sb.toString() + ")";
        }
        PublicUtil.getProps_value().setProperty(A01.class.getName(), fetch_hql.substring(0, fetch_hql.indexOf("where")) + "where pba.a01_key in");
        List<String> keys = (List<String>) CommUtil.fetchEntities("select pba.a01_key " + fetch_hql.replace("join fetch pba.deptCode", " "));
        List<String> keys2 = (List<String>) CommUtil.fetchEntities("select pba.a01_key " + fetch_hql2.replace("join fetch pba.deptCode", " "));
        List<String> otherDeptPerson_keys = new ArrayList<String>();
        for (String str : u_keys) {
            if (!keys2.contains(str)) {
                otherDeptPerson_keys.add(str);
            }
            keys.remove(str);
        }
        n_ftable.setObjects(new_a01s);
        d_ftable.setObjects(keys);
        z_ftable.setObjects(u_keys);
        other_ftable.setObjects(otherDeptPerson_keys);
        if (flag == 0) {
            a0191_ftable.setObjects(a0191_keys);
        } else {
            List a0191_list = new ArrayList();
            int size = a0191_table.keySet().size();
            int mod_len = size / 100;
            int re_len = mod_len + (size % 100 == 0 ? 0 : 1);
            List<String> temp_keys = new ArrayList<String>();
            temp_keys.addAll(a0191_table.keySet());
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < re_len; i++) {
                str = new StringBuffer();
                str.append("'@@@@'");
                if (i < mod_len) {
                    for (int j = 0; j < 100; j++) {
                        str.append(",'");
                        str.append(temp_keys.get(i * 100 + j));
                        str.append("'");
                    }
                } else {
                    for (int j = 0; j < 100; j++) {
                        int ind = i * 100 + j;
                        if (ind >= size) {
                            break;
                        }
                        str.append(",'");
                        str.append(temp_keys.get(ind));
                        str.append("'");
                    }
                }
                a0191_list.addAll(CommUtil.selectSQL("select a01_key,a0191 from A01 a01  where a01.a01_key in (" + str.toString() + ")"));
            }
            List t_a01s = new ArrayList();
            if (a0191_list != null) {
                for (Object obj : a0191_list) {
                    Object[] objs = (Object[]) obj;
                    if (!a0191_table.get(objs[0].toString()).equals(objs[1])) {
                        t_a01s.add(objs[0].toString());
                    }
                }
                a0191_ftable.setObjects(t_a01s);
            } else {
                a0191_ftable.setObjects(new ArrayList());
            }
        }

    }

    private void buildTool() {
        buildEmpChangeMenu();
        jToolBar1.add(btnExport);
        jToolBar1.add(btnImport);
        jToolBar1.add(btnPersonChange);
        jToolBar1.add(btnRecom);
        jToolBar1.addSeparator();
        jToolBar1.add(btnCancel);
    }

    private void setupEvents() {
        btnRecom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cantrastA01(1);
            }
        });
        btnImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (IPickPersonImportListener listener : listeners) {
                    listener.importPersons();
                }
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_index == 1) {
                    if (d_ftable.getSelectObjects().size() == 0) {
                        return;
                    }
                    PersonContainer.getPersonContainer().addPerson(d_ftable.getSelectObjects());
                    d_ftable.deleteSelectedRows();
                    d_ftable.updateUI();
                } else if (cur_index == 2) {
                    if (a0191_ftable.getSelectObjects().size() == 0) {
                        return;
                    }
                    PersonContainer.getPersonContainer().addPerson(a0191_ftable.getSelectObjects());
                    a0191_ftable.deleteSelectedRows();
                    a0191_ftable.updateUI();
                } else if (cur_index == 3) {
                    if (other_ftable.getSelectObjects().size() == 0) {
                        return;
                    }
                    PersonContainer.getPersonContainer().addPerson(other_ftable.getSelectObjects());
                    other_ftable.deleteSelectedRows();
                    other_ftable.updateUI();
                } else if (cur_index == 4) {
                    if (z_ftable.getSelectObjects().size() == 0) {
                        return;
                    }
                    PersonContainer.getPersonContainer().addPerson(z_ftable.getSelectObjects());
                    z_ftable.deleteSelectedRows();
                    z_ftable.updateUI();
                }
                PersonContainer.getPersonContainer().setVisible(true);
            }
        };
        miSendContainer.addActionListener(al);
        btnExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (IPickPersonImportListener listener : listeners) {
                    listener.refreshData();
                }
            }
        });
        miDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delObject();
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ModelFrame.close();
            }
        });
        jTabbedPane1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                cur_index = jTabbedPane1.getSelectedIndex();
                setMainState(cur_index);
            }
        });
        setMainState(cur_index);
    }

    private void buildEmpChangeMenu() {
        final JPopupMenu toolMenu = new JPopupMenu();
        Hashtable<String, JMenu> first_menu_set = new Hashtable<String, JMenu>();
        List list = EmpUtil.getUserChangeSchemeList();
        for (Object obj : list) {
            ChangeScheme c = (ChangeScheme) obj;
            if (c.getChangeScheme_type() == null || c.getChangeScheme_type().replace(" ", "").equals("")) {
                toolMenu.add(getEmpChangeItem(c));
            } else {
                JMenu first_menu = first_menu_set.get(c.getChangeScheme_type());
                if (first_menu == null) {
                    first_menu = new JMenu(c.getChangeScheme_type());
                    ComponentUtil.setIcon(this, "blank");
                    first_menu_set.put(c.getChangeScheme_type(), first_menu);
                    toolMenu.add(first_menu);
                }
                first_menu.add(getEmpChangeItem(c));
            }

        }
        btnPersonChange.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                toolMenu.show(btnPersonChange, 0, btnPersonChange.getHeight());
            }
        });
    }

    private JMenuItem getEmpChangeItem(final ChangeScheme c) {
        JMenuItem mi = new JMenuItem(c.getChangeScheme_name());
        if (c.contains("a0191")) {
            all_change_items.put(c.getChangeScheme_no() + "_" + c.getNewPersonClassName() + "_" + c.getOldPersonClassName(), mi);
        }
        ComponentUtil.setIcon(mi, "blank");
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List person_list = new ArrayList();
                // 如果人员容器可见，那么对人员容器选种的人员进行变动
                if (cur_index == 1) {
                    person_list = d_ftable.getAllSelectObjects();
                } else if (cur_index == 2) {
                    person_list = a0191_ftable.getAllSelectObjects();
                } else if (cur_index == 3) {
                    person_list = other_ftable.getAllSelectObjects();
                }
                EmpChangeAction.doEmpChangeAction(btnPersonChange, c, person_list, A01.class, null);
            }
        });

        return mi;
    }

    private void setMainState(int index) {
        if (index == 0) {
            btnExport.setEnabled(true);
            miSendContainer.setEnabled(false);
            miDel.setEnabled(false);
        } else if (index == 1 || index == 2 || index == 3) {
            btnExport.setEnabled(false);
            miSendContainer.setEnabled(true);
            miDel.setEnabled(true);
        } else if (index == 4) {
            btnExport.setEnabled(false);
            miSendContainer.setEnabled(false);
            miDel.setEnabled(true);
        }
    }

    private void delObject() {
        List<A01> del_persons = new ArrayList<A01>();
        del_persons.addAll(d_ftable.getAllSelectObjects());
        if (del_persons.size() == 0) {
            return;
        }
        if (MsgUtil.showNotConfirmDialog(EmpMngMsg.msg122)) {
            return;
        }
        int len = del_persons.size();
        int mod_len = len / 100;
        int rel_len = mod_len + (len % 100 > 0 ? 1 : 0);
        List<String> a01_keys = new ArrayList<String>();
        for (int i = 0; i < rel_len; i++) {
            StringBuffer str_pay_key = new StringBuffer();
            if (i < mod_len) {
                for (int j = 0; j < 100; j++) {
                    Object rcl = del_persons.get(i * 100 + j);
                    str_pay_key.append("'");
                    A01 t_a01 = (A01) rcl;
                    str_pay_key.append(t_a01.getA01_key());
                    str_pay_key.append("',");
                }
                str_pay_key.append("'-1'");
            } else {
                for (int j = 0; j < 100; j++) {
                    if ((i * 100 + j) >= len) {
                        break;
                    }
                    Object rcl = del_persons.get(i * 100 + j);
                    str_pay_key.append("'");
                    A01 t_a01 = (A01) rcl;
                    str_pay_key.append(t_a01.getA01_key());
                    str_pay_key.append("',");
                }
                str_pay_key.append("'-1'");
            }
            a01_keys.add(str_pay_key.toString());
        }
        StringBuffer str2 = new StringBuffer();
        for (A01 a01 : del_persons) {
            str2.append("'");
            str2.append(a01.getA01_key());
            str2.append("',");
        }
        str2.append("'-1'");
        if (del_para.getSysparameter_value().equals("0")) {
            for (String keys : a01_keys) {
                String str = keys;
                CommUtil.excuteSQL("update A01 set a0193=1 where a01_key in(" + str.toString() + ")");
                Date date = new Date();
                Format format = new SimpleDateFormat("yyyy-MM-dd");
                String ex_sql = "insert into RyChgLog (RyChgLog_key,a01_key,chg_date,chg_ip,chg_mac,chg_user,chg_type,beforestate,afterstate,chg_field,a0101,a0190,dept_name) ";
                String db_type = UserContext.sql_dialect;
                if (db_type.equals("oracle")) {
                    ex_sql += "select Sys_guid(),a.a01_key,to_date('" + format.format(date) + "','yyyy-MM-dd'),'" + UserContext.getPerson_ip() + "','" + UserContext.getPerson_mac() + "','" + UserContext.person_code + "','删除','0','1','a0193',a.a0101,a.a0190,d.content from a01 a,deptCode d where a.deptCode_key = d.deptCode_key and  a.a01_key in(" + str.toString() + ")";
                } else if (db_type.equals("sqlserver")) {
                    ex_sql += "select newid(),a.a01_key,'" + format.format(date) + "','" + UserContext.getPerson_ip() + "','" + UserContext.getPerson_mac() + "','" + UserContext.person_code + "','删除','0','1','a0193',a.a0101,a.a0190,d.content from a01 a,deptCode d where a.deptCode_key = d.deptCode_key and a01_key in(" + str.toString() + ")";
                } else if (db_type.equals("db2")) {
                    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ex_sql += "select Sys_guid(),a.a01_key,to_date('" + format.format(date) + "','yyyy-mm-dd hh24:mi:ss'),'" + UserContext.getPerson_ip() + "','" + UserContext.getPerson_mac() + "','" + UserContext.person_code + "','删除','0','1','a0193',a.a0101,a.a0190,d.content from a01 a,deptCode d where a.deptCode_key = d.deptCode_key and a01_key in(" + str.toString() + ")";
                }
                CommUtil.excuteSQL(ex_sql);
            }
            d_ftable.deleteSelectedRows();
        } else {
            List list = new ArrayList();
            for (String keys : a01_keys) {
                String str = keys;
                List tmp_list = CommUtil.fetchEntities("select a01_key from A01 a01 where a01_key in(" + str + ") and not exists(select 1 from BasePersonChange bpc where bpc.a01=a01 and bpc.chg_type='删除')");
                list.addAll(tmp_list);
            }
            //       List list = CommUtil.fetchEntities("select a01_key from A01 a01 where a01_key in(" + str2.toString() + ") and not exists(select 1 from BasePersonChange bpc where bpc.a01=a01 and bpc.chg_type='删除')");
            HashSet<String> key = new HashSet<String>();
            for (Object obj : list) {
                key.add(obj.toString());
            }
            List<A01> del_persons1 = new ArrayList<A01>();
            for (A01 a01 : del_persons) {
                if (key.contains(a01.getA01_key())) {
                    del_persons1.add(a01);
                }
            }
            if (del_persons1.size() == 0) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(miDel), "已在变动中", "提示", JOptionPane.INFORMATION_MESSAGE);
                MsgUtil.showInfoMsg(EmpMngMsg.msg123);
                return;
            }
            HashSet<BasePersonChange> save_changes = new HashSet<BasePersonChange>();
            Date date = new Date();
            String user = UserContext.person_name;
            ChangeScheme cs = (ChangeScheme) CommUtil.fetchEntityBy("from ChangeScheme c left join fetch c.changeItems  where c.changeScheme_name='删除' ");
            String entityName = "org.jhrcore.entity" + "." + "PersonChange_" + cs.getChangeScheme_no();
            Class the_class = null;
            try {
                the_class = Class.forName(entityName);
                for (A01 a01 : del_persons1) {
                    BasePersonChange bpc = (BasePersonChange) UtilTool.createUIDEntity(the_class);
                    bpc.setA01(a01);
                    bpc.setChangescheme_key(cs.getChangeScheme_key());
                    bpc.setApply_date(date);
//                        bpc.setChg_type("删除");
                    bpc.setChg_type(EmpMngMsg.msg124.toString());
                    bpc.setReason(EmpMngMsg.msg124.toString());
                    bpc.setChg_user(user);
                    bpc.setChg_state(EmpMngMsg.msg125.toString());
                    bpc = EmpUtil.setImportValueBy(cs, bpc, 0, 1);
                    HashSet<A01Chg> logs = new HashSet<A01Chg>();
                    A01Chg rcl = (A01Chg) UtilTool.createUIDEntity(A01Chg.class);
                    rcl.setChgdate(date);
                    rcl.setChgfield(EmpMngMsg.msg126.toString());
                    rcl.setBeforestate("0");
                    rcl.setAfterstate("1");
                    rcl.setBasePersonChange(bpc);
                    logs.add(rcl);
                    bpc.setA01Chgs(logs);
                    save_changes.add(bpc);
                }
            } catch (ClassNotFoundException ex) {
                log.error(ex);
            }
            ValidateSQLResult result = CommUtil.saveSet(save_changes);
            if (result.getResult() == 0) {
                MsgUtil.showInfoMsg(EmpMngMsg.msg127);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        }

    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
