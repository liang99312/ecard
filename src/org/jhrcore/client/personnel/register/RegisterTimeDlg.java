/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RegisterTimePanel.java
 *
 * Created on 2012-9-25, 11:31:24
 */
package org.jhrcore.client.personnel.register;

import com.fr.view.core.DateUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpRegisterMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class RegisterTimeDlg extends javax.swing.JDialog implements IModuleCode{

    private JhrDatePicker jhrStart = new JhrDatePicker();
    private JhrDatePicker jhrEnd = new JhrDatePicker();
    private boolean click_ok = false;
    private SysParameter spLimit;
    private SysParameter spTime;
    private String module_code = "EmpRegister.miTimeSet";

    /** Creates new form RegisterTimePanel */
    public RegisterTimeDlg(java.awt.Frame parent) {
        super(parent);
        this.setTitle("入职时间设置:");
        initComponents();
        initOthers();
        setupEvents();
    }

    public RegisterTimeDlg() {
        this.setTitle("入职时间设置:");
        initComponents();
        initOthers();
        setupEvents();
    }
    

    private void initOthers() {
        pnlStart.add(jhrStart);
        pnlEnd.add(jhrEnd);
        List list = CommUtil.fetchEntities("from SysParameter where sysParameter_key in('" + EmpUtil.spLimitCode + "','" + EmpUtil.spTimeCode + "')");
        for (Object obj : list) {
            SysParameter para = (SysParameter) obj;
            if (para.getSysParameter_key().equals(EmpUtil.spLimitCode)) {
                spLimit = para;
            } else if (para.getSysParameter_key().equals(EmpUtil.spTimeCode)) {
                spTime = para;
            }
        }
        if (spLimit == null) {
            spLimit = new SysParameter();
            spLimit.setSysParameter_key(EmpUtil.spLimitCode);
            spLimit.setSysparameter_code(EmpUtil.spLimitCode);
            //0:不限制;1：限制入职登记；2限制入职提交；3入职登记、提交均限制
            spLimit.setSysparameter_value("0");
            CommUtil.saveOrUpdate(spLimit);
        }
        if (spTime == null) {
            spTime = new SysParameter();
            spTime.setSysParameter_key(EmpUtil.spTimeCode);
            //0:按时间限制；1：按周期限制
            spTime.setSysparameter_code("0");
            spTime.setSysparameter_value("");
            CommUtil.saveOrUpdate(spTime);
        }
        String limit = spLimit.getSysparameter_value();
        jcbRegister.setSelected("1".equals(limit) || "3".equals(limit));
        jcbSubmit.setSelected("2".equals(limit) || "3".equals(limit));
        String code = spTime.getSysparameter_code();
        jcbTime.setSelected("0".equals(code));
        jcbWeek.setSelected("1".equals(code));
        String value = spTime.getSysparameter_value();
        if (value != null && !value.trim().equals("")) {
            String[] strs = value.split(";");
            if (jcbTime.isSelected()) {
                Date date = DateUtil.StrToDate(strs[0]);
                jhrStart.setDate(date);
                if (strs.length > 1) {
                    date = DateUtil.StrToDate(strs[1]);
                    jhrEnd.setDate(date);
                }
            } else if (jcbWeek.isSelected()) {
                jtfStart.setText(strs[0]);
                if (strs.length > 1) {
                    jtfEnd.setText(strs[1]);
                }
            }
        }
    }

    private void setupEvents() {
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        ActionListener al_refresh = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshUI();
            }
        };
        jcbTime.addActionListener(al_refresh);
        jcbWeek.addActionListener(al_refresh);
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        refreshUI();
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void refreshUI() {
        boolean isWeek = jcbWeek.isSelected();
        jhrStart.setEditable(!isWeek);
        jhrEnd.setEditable(!isWeek);
        jtfStart.setEditable(isWeek);
        jtfEnd.setEditable(isWeek);
    }

    private void save() {
        String limit = "0";
        if (jcbRegister.isSelected() && jcbSubmit.isSelected()) {
            limit = "3";
        } else if (jcbRegister.isSelected()) {
            limit = "1";
        } else if (jcbSubmit.isSelected()) {
            limit = "2";
        }
        spLimit.setSysparameter_value(limit);
        String code = "0";
        String value = "";
        if (jcbWeek.isSelected()) {
            code = "1";
            String start = jtfStart.getText();
            String end = jtfEnd.getText();
            int start_day = SysUtil.objToInt(start, -1);
            int end_day = SysUtil.objToInt(end, -1);
            String msg = "";
            if (start_day <= 0 || start_day > 31) {
                msg = EmpRegisterMsg.msg042.toString();
            } else if (end_day <= 0 || end_day > 31) {
                msg = EmpRegisterMsg.msg043.toString();
            }
            if (!msg.equals("")) {
                msg += EmpRegisterMsg.msg044.toString();
//                JOptionPane.showMessageDialog(null, msg, "错误", JOptionPane.ERROR_MESSAGE);
                MsgUtil.showErrorMsg(msg);
                return;
            }
            value = start_day + ";" + end_day;
        } else {
            Date start_date = jhrStart.getDate();
            Date end_date = jhrEnd.getDate();
            value = DateUtil.DateToStr(start_date) + ";" + DateUtil.DateToStr(end_date);
        }
        spTime.setSysparameter_value(value);
        spTime.setSysparameter_code(code);
        List list = new ArrayList();
        list.add(spLimit);
        list.add(spTime);
        ValidateSQLResult result = CommUtil.saveParameters(list);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    public boolean isClick_ok() {
        return click_ok;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jcbRegister = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jcbTime = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        pnlStart = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlEnd = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jcbWeek = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jtfStart = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtfEnd = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jcbSubmit = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jcbRegister.setText("限制入职登记时间");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("按时间限制："));

        buttonGroup1.add(jcbTime);
        jcbTime.setText("按时间");

        jLabel1.setText("开始时间：");

        pnlStart.setPreferredSize(new java.awt.Dimension(100, 25));
        pnlStart.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("结束时间：");

        pnlEnd.setPreferredSize(new java.awt.Dimension(100, 25));
        pnlEnd.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbTime)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(4, 4, 4)
                        .addComponent(pnlStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(242, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jcbTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("按周期（月）限制："));

        buttonGroup1.add(jcbWeek);
        jcbWeek.setText("按周期（月）");

        jLabel3.setText("开始日期：");

        jLabel4.setText("结束日期：");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfStart, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jcbWeek))
                .addContainerGap(242, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbWeek, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        btnSave.setText("保存");

        btnClose.setText("关闭");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(464, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addGap(18, 18, 18)
                .addComponent(btnClose)
                .addGap(25, 25, 25))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnClose))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jcbSubmit.setText("限制入职提交时间");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jcbRegister)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcbSubmit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbRegister)
                    .addComponent(jcbSubmit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox jcbRegister;
    private javax.swing.JCheckBox jcbSubmit;
    private javax.swing.JCheckBox jcbTime;
    private javax.swing.JCheckBox jcbWeek;
    private javax.swing.JTextField jtfEnd;
    private javax.swing.JTextField jtfStart;
    private javax.swing.JPanel pnlEnd;
    private javax.swing.JPanel pnlStart;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
