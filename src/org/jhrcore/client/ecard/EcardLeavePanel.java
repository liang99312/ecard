/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard;

import com.foundercy.pf.control.listener.IPickFieldOrderListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.ecard.leave.AddLeaveManyDayDialog;
import org.jhrcore.client.ecard.leave.AddLeaveOneDayDialog;
import org.jhrcore.client.ecard.leave.AddLeaveWeekDayDialog;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.ecard.Ecard;
import org.jhrcore.entity.ecard.Ecard_leave;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.ui.listener.CommEditAction;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Jane
 */
public class EcardLeavePanel extends javax.swing.JPanel {

    private FTable ftable_leave;
    private JPopupMenu pp = new JPopupMenu();
    private JMenuItem miAddOneDay = new JMenuItem("逐日增加");
    private JMenuItem miAddManyDay = new JMenuItem("多日连续增加");
    private JMenuItem miAddWeekDay = new JMenuItem("按星期增加");
    private String order_sql = "e.ecard_leave_date desc";
    private JLabel jLabel1 = new JLabel(" 起 ");
    private JLabel jLabel2 = new JLabel(" 止 ");
    private JhrDatePicker spDateFrom = new JhrDatePicker();
    private JhrDatePicker spDateTo = new JhrDatePicker();
    private JButton btnSearch = new JButton("查询");
    private Object curObj;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public EcardLeavePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        initToolBar();
        ftable_leave = new FTable(Ecard_leave.class, true, true, true, "");
        List<TempFieldInfo> all_infos = new ArrayList<TempFieldInfo>();
        all_infos.addAll(EntityBuilder.getCommFieldInfoListOf(Ecard_leave.class, EntityBuilder.COMM_FIELD_VISIBLE));
        ftable_leave.setAll_fields(all_infos, all_infos, new ArrayList(), "EcardLeavePanel");
        order_sql = SysUtil.getOrderString(ftable_leave.getCurOrderScheme(), "e", order_sql, ftable_leave.getAll_fields());
        List<String> fields = new ArrayList<String>();
        fields.add("ecard_leave_date");
        ftable_leave.setDisable_fields(fields);
        ftable_leave.setRight_allow_flag(true);
        ftable_leave.removeColSumItem();
        pnlRight.add(ftable_leave, BorderLayout.CENTER);
        PublicUtil.getProps_value().setProperty(Ecard_leave.class.getName(), "from Ecard_leave n where n.ecard_leave_key in");
    }

    private void initToolBar() {
        pp.add(miAddOneDay);
        pp.add(miAddManyDay);
        pp.add(miAddWeekDay);
        toolbar.add(jLabel1);
        toolbar.add(spDateFrom);
        toolbar.add(jLabel2);
        toolbar.add(spDateTo);
        toolbar.add(btnSearch);
    }

    private void setMainState(boolean editting) {
        btnEdit.setEnabled(UserContext.hasFunctionRight("Ecard_leave.btnEdit") && !editting);
        btnCancel.setEnabled(UserContext.hasFunctionRight("Ecard_leave.btnCancel") && editting);
        btnDel.setEnabled(UserContext.hasFunctionRight("Ecard_leave.btnDel") && !editting);
        btnView.setEnabled(UserContext.hasFunctionRight("Ecard_leave.btnDel") && editting);
        btnSave.setEnabled(UserContext.hasFunctionRight("Ecard_leave.btnDel") && editting);
        ftable_leave.setEditable(editting);
    }

    private void setupEvents() {
        btnSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fetchMainData(null);
            }
        });
        ftable_leave.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(curObj != ftable_leave.getCurrentRow()){
                    curObj = ftable_leave.getCurrentRow();
                }
            }
        });
        ftable_leave.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
            }
        });
        ftable_leave.addPickFieldOrderListener(new IPickFieldOrderListener() {

            @Override
            public void pickOrder(ShowScheme showScheme) {
                order_sql = SysUtil.getOrderString(showScheme, "k", order_sql, ftable_leave.getAll_fields());
                fetchMainData(null);
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveDay(ftable_leave.getCurrentRow());
            }
        });
        miAddOneDay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addDay(0);
            }
        });
        miAddManyDay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addDay(1);
            }
        });
        miAddWeekDay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addDay(2);
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pp.show(btnAdd, 0, 30);
            }
        });
        btnView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CommEditAction.doViewAction(curObj, ftable_leave);
                setMainState(false);
            }
        });
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setMainState(true);
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CommEditAction.doCancelAction(curObj, ftable_leave);
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ftable_leave.editingStopped();
                List<String> keys = ftable_leave.getSelectKeys();
                if (keys.isEmpty()) {
                    return;
                }
//                if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(),
//                        "确定要删除选中的记录吗", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
                    return;
                }
                ValidateSQLResult result = CommUtil.deleteObjs("Ecard_leave", "Ecard_leave_key", keys);
                if (result.getResult() == 0) {
                    MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
                    ftable_leave.deleteSelectedRows();
                } else {
                    MsgUtil.showHRDelErrorMsg(result);
                }
            }
        });
        setMainState(false);
    }

    private void saveDay(Object obj) {
        if (obj == null) {
            return;
        }
        ftable_leave.stopEditing();
        Ecard_leave ecard_leave = (Ecard_leave) obj;
        if (ecard_leave.getEcard_leave_name() == null || ecard_leave.getEcard_leave_name().trim().equals("")) {
            MsgUtil.showInfoMsg("请输入名称");
            return;
        }
        ValidateSQLResult result = CommUtil.updateEntity(obj);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    private void addDay(int type) {
        ValidateSQLResult result = null;
        if (type == 0) {
            AddLeaveOneDayDialog alodDlg = new AddLeaveOneDayDialog(ContextManager.getMainFrame());
            ContextManager.locateOnMainScreenCenter(alodDlg);
            alodDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            alodDlg.setVisible(true);
            if (alodDlg.isClick_ok()) {
                result = alodDlg.getResult();
            }
        } else if (type == 1) {
            AddLeaveManyDayDialog alodDlg = new AddLeaveManyDayDialog(ContextManager.getMainFrame());
            ContextManager.locateOnMainScreenCenter(alodDlg);
            alodDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            alodDlg.setVisible(true);
            if (alodDlg.isClick_ok()) {
                result = alodDlg.getResult();
            }
        } else if (type == 2) {
            AddLeaveWeekDayDialog alodDlg = new AddLeaveWeekDayDialog(ContextManager.getMainFrame());
            ContextManager.locateOnMainScreenCenter(alodDlg);
            alodDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            alodDlg.setVisible(true);
            if (alodDlg.isClick_ok()) {
                result = alodDlg.getResult();
            }
        }
        fetchMainData(null);
        if (result == null) {
            return;
        }
        if (result.getResult() == 0) {
            fetchMainData(null);
        } else {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnAdd), "新增失败");
            MsgUtil.showInfoMsg(CommMsg.ADDFAIL_MESSAGE);
        }
    }

    private void fetchMainData(QueryScheme qs) {
        String sel_hql = "from Ecard_leave e where 1=1";
        sel_hql = sel_hql + " and e.ecard_leave_date >='" + format.format(spDateFrom.getDate()) + "'";
        Date d = DateUtil.getNextDay(spDateTo.getDate());
        sel_hql = sel_hql + " and e.ecard_leave_date <'" + format.format(d) + "'";
        if (qs != null) {
            sel_hql += " and e in (" + qs.buildHql("from Ecard_leave ed ") + ")";
        }
        if (!"".equals(order_sql)) {
            sel_hql += " order by " + order_sql;
        }
        List list = CommUtil.fetchEntities(sel_hql);
        Set<Ecard_leave> leaves = new HashSet<Ecard_leave>();
        leaves.addAll(list);
        ftable_leave.setObjects(list);
        qs = null;

        ContextManager.setStatusBar(ftable_leave.getObjects().size());
        ftable_leave.updateUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        pnlRight = new javax.swing.JPanel();

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        btnAdd.setText("新增休息日");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnAdd);

        btnEdit.setText("编辑");
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnEdit);

        btnView.setText("浏览");
        btnView.setFocusable(false);
        btnView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnView);

        btnSave.setText("保存");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnSave);

        btnCancel.setText("取消");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnCancel);

        btnDel.setText("删除");
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnDel);

        pnlRight.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(pnlRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnView;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
