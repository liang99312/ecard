/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChangePayDatePanel.java
 *
 * Created on 2011-7-27, 17:05:55
 */
package org.jhrcore.client.personnel.changemodule;

import com.foundercy.pf.control.listener.IPickFieldSetListener;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.personnel.PersonPhotoDialog;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.BasePersonChange;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

public class ChangePayDatePanel extends javax.swing.JPanel implements IModuleCode {

    private JLabel lbl = new JLabel("  选择起薪日期:");
    private JButton btnSet = new JButton("设置起薪日期");
    private JButton btnSetField = new JButton("设置人事字段");
    private JButton btnOk = new JButton("关闭");
    private FTable ftable;
    private JhrDatePicker date_start = new JhrDatePicker();
    private List<IPickWindowCloseListener> listners = new ArrayList();
    private FTable ftableChg;
    private BeanPanel beanPanelEmp = new BeanPanel();
    private BeanPanel beanPanelChg = new BeanPanel();
    private Hashtable<String, ChangeScheme> scheme_keys = null;
    private BasePersonChange cur_bpc = null;
    private FTable ftable_emp = null;
    private String module_code = "EmpChange.miPayDate";

    public void addIPickWindowCloseListener(IPickWindowCloseListener listener) {
        listners.add(listener);
    }

    public ChangePayDatePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    public ChangePayDatePanel(FTable f, Hashtable<String, ChangeScheme> scheme_keys) {
        this.ftable = f;
        this.scheme_keys = scheme_keys;
        initComponents();
        initOthers();
        setupEvents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlMain = new javax.swing.JPanel();
        jtpMain = new javax.swing.JTabbedPane();
        pnlEmp = new javax.swing.JPanel();
        pnlChg = new javax.swing.JPanel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(2);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlMain.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setTopComponent(pnlMain);

        pnlEmp.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("人员基本信息", pnlEmp);

        pnlChg.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("调动消息", pnlChg);

        jSplitPane1.setRightComponent(jtpMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlChg;
    private javax.swing.JPanel pnlEmp;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTop;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        ftableChg = new FTable(BasePersonChange.class, ftable.getFields(), false, false, false, "");
        ftableChg.setObjects(ftable.getSelectObjects());
        jToolBar1.add(lbl);
        jToolBar1.add(date_start);
        jToolBar1.add(btnSet);
        jToolBar1.add(btnSetField);
        jToolBar1.add(btnOk);
        pnlMain.add(ftableChg, BorderLayout.CENTER);
        pnlMain.setBorder(new EtchedBorder());
        pnlEmp.add(new JScrollPane(beanPanelEmp));
        pnlChg.add(new JScrollPane(beanPanelChg));
        ftable_emp = new FTable(A01.class, true, false, false, "ChangePayDatePanel");
        List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> dept_fields = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : dept_fields) {
            if (tfi.getField_name().equals("dept_code") || tfi.getField_name().equals("content")) {
                default_fields.add(tfi);
            }
            all_fields.add(tfi);
            tfi.setField_name("deptCode." + tfi.getField_name());
        }
        List<TempFieldInfo> a01_fields = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        default_fields.addAll(a01_fields);
        all_fields.addAll(a01_fields);
        ftable_emp.setAll_fields(all_fields, default_fields, new ArrayList(), "ChangePayDatePanel");
    }

    private void setupEvents() {
        btnSetField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ftable_emp.setShowFields();
            }
        });
        ftable_emp.addPickFieldSetListener(new IPickFieldSetListener() {

            @Override
            public void pickField(ShowScheme showScheme) {
                refreshUI(cur_bpc);
            }
        });
        ftableChg.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (cur_bpc == ftableChg.getCurrentRow()) {
                    return;
                }
                cur_bpc = (BasePersonChange) ftableChg.getCurrentRow();
                refreshUI(cur_bpc);
            }
        });
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refreshUI(cur_bpc);
            }
        });
        btnSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> list = ftableChg.getSelectKeys();
                if (list.isEmpty()) {
                    return;
                }
                Date date = date_start.getDate();
                SysUtil.sortStrList(list);
                String sql = "update BasePersonChange set pay_date=" + DateUtil.toStringForQuery(date, "yyyy-MM-dd HH:mm:ss") + " where basePersonChange_key in ";
                ValidateSQLResult vs = CommUtil.excuteSQLs(sql, list);
                if (vs.getResult() == 0) {
                    for (Object obj : ftableChg.getAllSelectObjects()) {
                        BasePersonChange bpc = (BasePersonChange) obj;
                        bpc.setPay_date(date);
                    }
                    ftableChg.updateUI();
                    MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
                } else {
                    MsgUtil.showHRSaveErrorMsg(vs);
                }
            }
        });
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (IPickWindowCloseListener listner : listners) {
                    listner.pickClose();
                }
                ModelFrame.close();
            }
        });
        cur_bpc = (BasePersonChange) ftable.getCurrentRow();
        refreshUI(cur_bpc);
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void refreshUI(BasePersonChange bpc) {
        if (bpc == null || bpc.getBasePersonChange_key() == null) {
            return;
        }
        int ind = jtpMain.getSelectedIndex();
        if (ind == 0) {
            beanPanelEmp.setBean(bpc.getA01());
            beanPanelEmp.setShow_scheme(ftable_emp.getCur_show_scheme());
            beanPanelEmp.setFields(ftable_emp.getFields());
            beanPanelEmp.bind();
            pnlEmp.removeAll();
            JPanel pnl = new JPanel(new BorderLayout());
            pnl.add(beanPanelEmp);
            JLabel lblPhoto = PersonPhotoDialog.getPersonPhotoDlg(bpc.getA01()).getLbl_person();
            JPanel pnlPhoto = new JPanel(new BorderLayout());
            pnlPhoto.add(lblPhoto, BorderLayout.NORTH);
            pnlPhoto.add(new JPanel());
            pnl.add(pnlPhoto, BorderLayout.EAST);
            pnlEmp.add(new JScrollPane(pnl));
            pnlEmp.updateUI();
        } else {
            EmpUtil.refreshUIByChange(bpc, beanPanelChg, scheme_keys.get(bpc.getChangescheme_key()));
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
