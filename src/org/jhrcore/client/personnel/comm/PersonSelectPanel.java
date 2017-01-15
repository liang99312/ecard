/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PersonSelectPanel.java
 *
 * Created on 2009-4-28, 18:12:24
 */
package org.jhrcore.client.personnel.comm;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jhrcore.client.UserContext;
import com.foundercy.pf.control.listener.IPickFieldSetListener;
import com.foundercy.pf.control.listener.IPickPopupListener;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.jhrcore.client.CommUtil;
import org.jhrcore.comm.FieldTrigerManager;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.entity.showstyle.ShowSchemeDetail;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.query3.QueryParamDialog;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.WriteFieldDialog;
import org.jhrcore.query3.QuerySchemeDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.query3.QuerySchemePanel2;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class PersonSelectPanel extends javax.swing.JPanel implements IModuleCode {

    private FTable ftable;
    private List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
    private JMenuItem miDel = new JMenuItem("删除");
    private JMenuItem miFieldSet = new JMenuItem("设置输入字段");
    private List<IPickRowSelectListener> iPickPersonSelectListeners = new ArrayList<IPickRowSelectListener>();
    //用于显示记录数大于1的搜索结果
    private FTable tmp_table = new FTable(A01.class, true, false, false, "PersonModelDialog");
    private Object cur_obj;
    private ListSelectionListener person_listener;
    private Class cur_class = A01.class;
    private boolean only_person_flag = true;
    private List<String> write_fields = new ArrayList<String>();
    private HashSet<String> exist_keys = new HashSet<String>();
    private JLabel lbl = new JLabel("查找：");
    private JTextField jtfSearch = new JTextField();
    private JButton btnAdd = new JButton("添加");
    private JButton btnDel = new JButton("删除");
    private JButton btnQuery = new JButton("查询");
    private JButton btnSelect = new JButton("直观挑选");
    private JButton btnReplace = new JButton("替换");
    private boolean add_flag = false;
    private List<IPickWindowCloseListener> iPickWindowCloseListeners = new ArrayList<IPickWindowCloseListener>();
    private ShowScheme showScheme = null;
    private String changeScheme_key = "";
//    private String type = "";
    private String module_code = "EmpMng.addAppendixs.initOthers";

    public void addPickDelListener(IPickWindowCloseListener listener) {
        iPickWindowCloseListeners.add(listener);
    }

    public void delPickDelListener(IPickWindowCloseListener listener) {
        iPickWindowCloseListeners.add(listener);
    }

    public void setChangeScheme_key(String changeScheme_key) {
        this.changeScheme_key = changeScheme_key;
    }

    public Class getCur_class() {
        return cur_class;
    }

    public void setCur_class(Class cur_class) {
        this.cur_class = cur_class;
    }

    public List<String> getWrite_fields() {
        return write_fields;
    }

    public void setWrite_fields(List<String> write_fields) {
        this.write_fields.clear();
        this.write_fields.addAll(write_fields);
        updateFields();
    }

    public void addWrite_fields(List<String> write_fields) {
        for (String field : write_fields) {
            if (this.write_fields.contains(field)) {
                continue;
            }
            this.write_fields.add(field);
        }
        updateFields();
    }

    public void addPickPersonSelectListener(IPickRowSelectListener listener) {
        iPickPersonSelectListeners.add(listener);
    }

    public void delPickPersonSelectListener(IPickRowSelectListener listener) {
        iPickPersonSelectListeners.remove(listener);
    }

    /**
     * Creates new form PersonSelectPanel
     */
    public PersonSelectPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    public PersonSelectPanel(Class cs) {
        cur_class = cs;
        initComponents();
        initOthers();
        setupEvents();
    }

//    public PersonSelectPanel(Class cs, String changeScheme_key, String type) {
//        this.changeScheme_key = changeScheme_key;
//        this.type = type;
//        cur_class = cs;
//        initComponents();
//        initOthers();
//        setupEvents();
//    }
    public PersonSelectPanel(Class cs, String changeScheme_key) {
        this.changeScheme_key = changeScheme_key;
        cur_class = cs;
        initComponents();
        initOthers();
        setupEvents();
    }

    public PersonSelectPanel(Class cs, boolean add_flag) {
        cur_class = cs;
        this.add_flag = add_flag;
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

        pnlPersons = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();

        setPreferredSize(new java.awt.Dimension(320, 338));

        pnlPersons.setPreferredSize(new java.awt.Dimension(100, 300));
        pnlPersons.setLayout(new java.awt.BorderLayout());

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
            .addComponent(pnlPersons, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPersons, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlPersons;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        only_person_flag = cur_class.getSimpleName().equals("A01");
        List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> a01_all_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> a01_default_fields = new ArrayList<TempFieldInfo>();
        EntityBuilder.buildInfo(DeptCode.class, a01_all_fields, a01_default_fields, "deptCode");
        EntityBuilder.buildInfo(A01.class, a01_all_fields, a01_default_fields, "");
        if (only_person_flag) {
            EntityBuilder.buildInfo(DeptCode.class, all_fields, default_fields, "deptCode");
        } else {
            EntityBuilder.buildInfo(DeptCode.class, all_fields, default_fields, "a01.deptCode");
        }
        EntityBuilder.buildInfo(A01.class, all_fields, default_fields, only_person_flag ? "" : "a01");
        ftable = new FTable(cur_class, true, false, false, "PersonSelectPanel") {

            @Override
            public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                if (fileName.contains(".")) {
                    return new Color(238, 238, 238);
                }
                return null;
            }
        };
        ftable.setRight_allow_flag(true);
        ftable.setAll_fields(all_fields, default_fields, "PersonSelectPanel");
        ftable.removeItemByCodes("query;order;sum;replace");
        ftable.addPickPopupListener(new IPickPopupListener() {

            @Override
            public void addMenuItem(JPopupMenu pp) {
                pp.add(miDel);
                if (!only_person_flag) {
                    pp.add(miFieldSet);
                }
            }
        });
        if (!only_person_flag) {
            ftable.setEditable(true);
            if (!"".equals(changeScheme_key)) {
                Object obj = CommUtil.fetchEntityBy("from ShowScheme ss join fetch ss.showSchemeDetails where ss.entity_name ='" + changeScheme_key + "' and person_code='" + UserContext.person_code + "'");
                if (obj == null) {
                    showScheme = (ShowScheme) UtilTool.createUIDEntity(ShowScheme.class);
                    showScheme.setEntity_name(changeScheme_key);
                    showScheme.setField_right_flag(false);
                    showScheme.setGroup_flag(false);
                    showScheme.setPerson_code(UserContext.person_code);
                    showScheme.setUsed_flag(false);
                } else {
                    showScheme = (ShowScheme) obj;
                    write_fields.clear();
                    Set<ShowSchemeDetail> details = showScheme.getShowSchemeDetails();
                    List<ShowSchemeDetail> dss = new ArrayList<ShowSchemeDetail>();
                    dss.addAll(details);
                    SysUtil.sortListByInteger(dss, "order_no");
                    for (ShowSchemeDetail ssd : dss) {
                        write_fields.add(ssd.getField_name());
                    }
                    updateFields();
                    for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
                        listener.pickFields(write_fields);
                    }
                }
            }
        }
        pnlPersons.add(ftable, BorderLayout.CENTER);
        ftable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tmp_table.setRight_allow_flag(true);
        tmp_table.setAll_fields(a01_all_fields, a01_default_fields, new ArrayList(), "PersonModelDialog");
        tmp_table.removeItemByCodes("query;order;sum;replace");
        if (add_flag) {
            if (PersonContainer.getPersonContainer().isVisible()) {
                List list = PersonContainer.getPersonContainer().getFTable().getAllSelectObjects();
                addObject(list, false);
            }
        }
        toolbar.add(lbl);
        toolbar.add(jtfSearch);
        toolbar.add(btnAdd);
        toolbar.add(btnDel);
        toolbar.add(btnQuery);
        toolbar.add(btnSelect);
        toolbar.add(btnReplace);
        ComponentUtil.setSize(jtfSearch, 100, 22);
    }

    private void setupEvents() {
        btnQuery.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                QuerySchemeDialog schemeDlg = new QuerySchemeDialog(JOptionPane.getFrameForComponent(btnQuery), A01.class, "PersonSelectPanel." + cur_class.getSimpleName());
                ContextManager.locateOnScreenCenter(schemeDlg);
                schemeDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                schemeDlg.setVisible(true);
                if (schemeDlg.getQueryScheme() != null) {
                    if (!QueryParamDialog.ShowQueryParamDialog(btnQuery, schemeDlg.getQueryScheme())) {
                        return;
                    }
                    pickPerson(schemeDlg.getQueryScheme());
                }
            }
        });
        btnSelect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                QuerySchemePanel2 pnl2 = new QuerySchemePanel2();
                QueryScheme qs = (QueryScheme) UtilTool.createUIDEntity(QueryScheme.class);
                qs.setQueryEntity("A01");
                qs.setQuery_type(0);
                pnl2.setQueryScheme(qs);
                if (ModalDialog.doModal(JOptionPane.getFrameForComponent(btnSelect), pnl2, EmpMngMsg.ttl035)) {
                    addObject(pnl2.getSelectObjects(), false);
                }
            }
        });
        ActionListener field_set_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WriteFieldDialog wfDlg = new WriteFieldDialog(cur_class, new ArrayList(), write_fields);
                ContextManager.locateOnMainScreenCenter(wfDlg);
                wfDlg.setVisible(true);
                if (wfDlg.getSelect_details() != null) {
                    List<String> fields = ftable.getFields();
                    fields.removeAll(write_fields);
                    ftable.setFields(fields);
                    write_fields.clear();
                    List<ShowSchemeDetail> details = wfDlg.getSelect_details();
                    int order_no = 0;
                    Set<ShowSchemeDetail> ds = new HashSet<ShowSchemeDetail>();
                    for (ShowSchemeDetail ssd : details) {
                        write_fields.add(ssd.getField_name());
                        ssd.setOrder_no(order_no);
                        order_no++;
                        ssd.setShowScheme(showScheme);
                        ds.add(ssd);
                    }
                    if (!"".equals(changeScheme_key)) {
                        showScheme.setShowSchemeDetails(ds);
                        ValidateSQLResult result = CommImpl.saveShowScheme(showScheme, "detail");
                        if (result.getResult() == 0) {
                            updateFields();
                            for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
                                listener.pickFields(write_fields);
                            }
                        } else {
                            MsgUtil.showInfoMsg(EmpMngMsg.msg145);
                        }
                    } else {
                        updateFields();
                        for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
                            listener.pickFields(write_fields);
                        }
                    }
                }
            }
        };
        miFieldSet.addActionListener(field_set_listener);
        ftable.addPickFieldSetListener(new IPickFieldSetListener() {

            @Override
            public void pickField(ShowScheme showScheme) {
                updateFields();
            }
        });
        ActionListener del_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (only_person_flag) {
                    for (Object obj : ftable.getSelectObjects()) {
                        if (obj instanceof A01) {
                            A01 a01 = (A01) obj;
                            exist_keys.remove(a01.getA01_key());
                        } else {
                            exist_keys.remove(obj.toString());
                        }
                    }
                } else {
                    for (Object obj : ftable.getSelectObjects()) {
                        if (obj.getClass().getName().equals(cur_class.getName())) {
                            Object tmp_obj = PublicUtil.getProperty(obj, "a01");
                            if (tmp_obj != null) {
                                exist_keys.remove(((A01) tmp_obj).getA01_key());
                            }
                        } else if (obj instanceof A01) {
                            A01 a01 = (A01) obj;
                            exist_keys.remove(a01.getA01_key());
                        }
                    }
                }
                ftable.deleteSelectedRows();
                for (IPickWindowCloseListener listener : iPickWindowCloseListeners) {
                    listener.pickClose();
                }
            }
        };
        miDel.addActionListener(del_listener);
        btnDel.addActionListener(del_listener);
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quickSearch();
            }
        };
        jtfSearch.addActionListener(al);
        btnAdd.addActionListener(al);
        person_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (cur_obj == ftable.getCurrentRow()) {
                    return;
                }
                cur_obj = ftable.getCurrentRow();
                if (cur_obj == null) {
                    return;
                }
                for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
                    listener.pickRow(cur_obj);
                }
            }
        };
        ftable.addListSelectionListener(person_listener);
        btnReplace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ftable.editingStopped();
                replaceData();
            }
        });
        ComponentUtil.setSysFuntionNew(this, false);
    }

    public void setSearchable(boolean searchable) {
        jtfSearch.setEnabled(searchable);
        btnAdd.setEnabled(searchable);
        jtfSearch.setEnabled(searchable);
        btnQuery.setEnabled(searchable);
        btnDel.setEnabled(searchable);
        btnSelect.setEnabled(searchable);
    }

    public FTable getFtable() {
        return ftable;
    }

    public void delCurRow() {
        ftable.deleteRow(ftable.getCurrentRowIndex());
        pnlPersons.updateUI();
    }

    private void quickSearch() {
        String text = jtfSearch.getText();
        if (text == null || text.trim().equals("")) {
            return;
        }
        pickPerson(null);
    }

    public void pickRow(int row) {
        if (row < 0) {
            row = 0;
        }
        if (ftable.getObjects().size() > row) {
            ftable.setRowSelectionInterval(row, row);
        }
    }

    private void updateFields() {
        List<String> fields = ftable.getFields();
        fields.addAll(write_fields);
        ftable.setFields(fields);
        for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
            listener.pickFields(write_fields);
        }
    }

    private void pickPerson(QueryScheme queryScheme) {
        String hql = "select bp.a01_key from A01 bp where";
        if (queryScheme == null) {
            String text = SysUtil.getQuickSearchText(jtfSearch.getText());
            hql += " (upper(bp.a0190) like '" + text + "' or upper(bp.a0101) like '" + text + "' or upper(bp.pydm) like '" + text + "')";
        } else {
            hql = hql + " bp in(" + queryScheme.buildHql("from A01 ed ") + ")";
        }
        hql += " and " + UserContext.getDept_right_rea_str("bp.deptCode") + " and " + UserContext.getPerson_class_right_str(A01.class, "bp");
        List<A01> result_a01s = null;
        for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
            List ll = listener.pickPerson(hql);
            List remove_list = new ArrayList();
            for (Object obj : ll) {
                if (obj instanceof A01) {
                    if (exist_keys.contains(((A01) obj).getA01_key())) {
                        remove_list.add(obj);
                    }
                } else {
                    if (exist_keys.contains(obj.toString())) {
                        remove_list.add(obj.toString());
                    }
                }
            }
            ll.removeAll(remove_list);
            if (ll.size() > 1) {
                tmp_table.setObjects(ll);
                PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in");
                if (!ModalDialog.doModal(btnAdd, tmp_table, EmpMngMsg.ttl029, tmp_table)) {
                    return;
                }
                result_a01s = tmp_table.getAllSelectObjects();
            } else if (ll.size() == 1) {
                result_a01s = (List<A01>) CommUtil.fetchEntities("from A01 bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in", ll);
            }
        }
        addObject(result_a01s, false);
    }

    public void addObject(List result_a01s) {
        addObject(result_a01s, add_flag);
    }

    /**
     * 此方法用于将指定对象LIST加入到当前网格中，但加入时会考虑一些情况：
     * 当testContainer为true时，若人员容器处于打开状态，则不会向网格中加入对象
     * 加入对象时会根据only_person_flag来决定加入对象的类型，此做法旨在通用人员挑选与数据编辑
     * only_person_flag为TRUE：直接将人员加入到当前网格
     * only_person_flag为FALSE：根据当前主类按人员生成新的对象，并在指定其对应人员后加入到网格，从而达到在网格状态下编辑
     * 对于已经存在的记录忽略，以exist_keys保持当前存在的KEY
     *
     * @param result_a01s：需要加入的对象LIST
     * @param testContainer ：是否需要检测人员容器
     */
    public void addObject(List result_a01s, boolean testContainer) {
        if (result_a01s == null) {
            return;
        }
        if (testContainer && PersonContainer.getPersonContainer().isVisible()) {
            return;
        }
        if (only_person_flag) {
            for (Object obj : result_a01s) {
                if (obj instanceof A01) {
                    A01 a01 = (A01) obj;
                    if (exist_keys.contains(a01.getA01_key())) {
                        continue;
                    }
                    exist_keys.add(a01.getA01_key());
                    ftable.addObject(a01);
                } else {
                    if (exist_keys.contains(obj.toString())) {
                        continue;
                    }
                    exist_keys.add(obj.toString());
                    ftable.addObject(obj);
                }
            }
        } else {
            List list = new ArrayList();
            for (Object obj : result_a01s) {
                if (obj.getClass().getName().equals(cur_class.getName())) {
                    Object tmp_obj = PublicUtil.getProperty(obj, "a01");
                    if (tmp_obj != null) {
                        if (exist_keys.contains(((A01) tmp_obj).getA01_key())) {
                            continue;
                        }
                        exist_keys.add(((A01) tmp_obj).getA01_key());
                        list.add(obj);
                    }
                } else if (obj instanceof A01) {
                    A01 a01 = (A01) obj;
                    if (exist_keys.contains(a01.getA01_key())) {
                        continue;
                    }
                    exist_keys.add(a01.getA01_key());
                    try {
                        boolean key_assign_flag = false;
                        Class<?>[] super_classs = null;
                        if (cur_class.getSuperclass() != null) {
                            super_classs = cur_class.getSuperclass().getInterfaces();
                        }
                        if (super_classs != null) {
                            for (Class c : super_classs) {
                                if (c.getSimpleName().equals("KeyInterface")) {
                                    key_assign_flag = true;
                                    break;
                                }
                            }
                        }
                        if (!key_assign_flag) {
                            super_classs = cur_class.getInterfaces();
                            if (super_classs != null) {
                                for (Class c : super_classs) {
                                    if (c.getSimpleName().equals("KeyInterface")) {
                                        key_assign_flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                        Object tmp_obj;
                        if (key_assign_flag) {
                            tmp_obj = UtilTool.createUIDEntity(cur_class);
                        } else {
                            tmp_obj = cur_class.newInstance();
                        }
                        Method method = cur_class.getMethod("setA01", new Class[]{A01.class});
                        method.invoke(tmp_obj, new Object[]{a01});
                        list.add(tmp_obj);
                    } catch (Exception ex) {
                        HrLog.error(this.getClass(), ex);
                    }
                }
            }
            for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
                listener.addRows(list);
            }
            ftable.addObjects(list);
        }
//        int len = ftable.getObjects().size();
//        if (len >= 1) {
//            ftable.setRowSelectionInterval(len - 1, len - 1);
//        }
        for (IPickRowSelectListener listener : iPickPersonSelectListeners) {
            listener.pickRow(ftable.getCurrentRow());
        }
        pnlPersons.updateUI();
    }

    public void replaceData() {
        ReplaceValuePanel pnlCr = new ReplaceValuePanel(cur_class, write_fields, ftable);
        pnlCr.addPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                pnlPersons.updateUI();
                FieldTrigerManager.getFieldTrigerManager().trigerObjs(ftable.getObjects());
                for (IPickWindowCloseListener listener : iPickWindowCloseListeners) {
                    listener.pickClose();
                }
            }
        });
        ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(ftable), pnlCr, true, EmpMngMsg.ttl034, 400, 500);
    }

    public JPanel getPnlPersons() {
        return pnlPersons;
    }

    public List getObjects() {
        return ftable.getAllObjects();
    }

    public void delObjects() {
        exist_keys.clear();
        ftable.deleteAllRows();
    }

    public void setEnable(String code, boolean enable) {
        JComponent c = null;
        if ("add".equals(code)) {
            c = btnAdd;
            jtfSearch.setEditable(enable);
        } else if ("query".equals(code)) {
            c = btnQuery;
        } else if ("select".equals(code)) {
            c = btnSelect;
        } else if ("replace".equals(code)) {
            c = btnReplace;
        }
        if (c != null) {
            c.setEnabled(enable);
        }
    }

    public void setReplaceEnable(boolean enable) {
        btnReplace.setVisible(enable);
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
