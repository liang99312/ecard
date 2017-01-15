/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EmpPymCreateDlg.java
 *
 * Created on 2010-7-19, 20:41:13
 */
package org.jhrcore.client.personnel;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class EmpPymCreateDlg extends javax.swing.JDialog implements IModuleCode {

    private FTable ftable;
    private DeptCode cur_dept;
    private List list;
    private int type = 0;//0: 当前选择人员；1：当前部门；2：所有人员
    private List<String> keys = new ArrayList<String>();
    private HrLog log = new HrLog("EmpMng." + this.getTitle());
    private String module_code = "EmpMng.b_pinyinma";

    public EmpPymCreateDlg() {
        this.setTitle("生成拼音码");
        initComponents();
        initOthers();
        setupEvents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /** Creates new form EmpPymCreateDlg */
    public EmpPymCreateDlg(java.awt.Frame parent, boolean modal, DeptCode dc, List list) {
        super(parent, modal);
        this.setTitle("生成拼音码");
        this.cur_dept = dc;
        this.list = list;
        initComponents();
        initOthers();
        setupEvents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initOthers() {
        for (Object obj : list) {
            if (obj instanceof A01) {
                keys.add(((A01) obj).getA01_key());
            } else {
                keys.add(obj.toString());
            }
        }
        List<String> fields = new ArrayList<String>();
        fields.add("deptCode.dept_code");
        fields.add("deptCode.content");
        fields.add("a0190");
        fields.add("a0101");
        fields.add("pydm");
        ftable = new FTable(A01.class, fields, false, false, false, "EmpPymCreatePanel");
        ftable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(ftable, BorderLayout.CENTER);
    }

    private void setupEvents() {
        ActionListener al_type = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fetchMainData();
            }
        };
        jRadioButton1.addActionListener(al_type);
        jRadioButton2.addActionListener(al_type);
        jRadioButton3.addActionListener(al_type);
        jCheckBox1.addActionListener(al_type);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ftable.getObjects().isEmpty()) {
                    MsgUtil.showErrorMsg(EmpMngMsg.msg031);
                    return;
                }
                log.info(e);
                String s_where = "1=1";
                if (type == 1) {
                    s_where += " and d.dept_code like '" + cur_dept.getDept_code() + "%'";
                }
                if (!UserContext.isSA) {
                    s_where += "and (" + UserContext.dept_right_str + ")";
                }
                List<String> create_keys = new ArrayList<String>();
                if (type == 0) {
                    for (Object obj : ftable.getObjects()) {
                        if (obj instanceof A01) {
                            create_keys.add(((A01) obj).getA01_key());
                        } else {
                            create_keys.add(obj.toString());
                        }
                    }
                }
                ValidateSQLResult result = RSImpl.createPersonPYM(type, jCheckBox1.isSelected(), s_where, create_keys);
                if (result != null) {
                    if (result.getResult() == 0) {
                        MsgUtil.showInfoMsg(EmpMngMsg.msg032);
                        log.info("拼音码生成成功");
                        fetchMainData();
                    } else {
                        MsgUtil.showHRSaveErrorMsg(result);
                    }

                }
            }
        });
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                dispose();
            }
        });
        fetchMainData();
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void fetchMainData() {
        type = getType2();
        boolean isAll = jCheckBox1.isSelected();
        String hql = "1=1";
        PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 a01 join fetch a01.deptCode where a01.a01_key in ");
        if (type == 1) {
            hql = "d.dept_code like '" + cur_dept.getDept_code() + "%'";
        }
        if (!UserContext.isSA) {
            hql += " and (" + UserContext.dept_right_str + ")";
        }
        hql = "select a01.a01_key from A01 a01,DeptCode d where a01.deptCode_key=d.deptCode_key and " + hql;
        if (!isAll) {
            hql += "and rtrim(ltrim(" + DbUtil.getNull_strForDB(UserContext.sql_dialect) + "(a01.pydm,'@')))='@'";
        }
        hql += "and rtrim(ltrim(" + DbUtil.getNull_strForDB(UserContext.sql_dialect) + "(a01.a0101,'@')))<>'@'";
        if (type == 0) {
            ftable.setObjects(CommUtil.selectSQL(hql + " and a01.a01_key in ", keys));
        } else {
            ftable.setObjects(CommUtil.selectSQL(hql + " order by d.dept_code,a01.a0190"));
        }
        jPanel2.updateUI();
    }

    public int getType2() {
        if (jRadioButton2.isSelected()) {
            return 1;
        } else if (jRadioButton3.isSelected()) {
            return 2;
        }
        return 0;
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
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnOk = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("生成对象："));

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("当前选择人员");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("当前部门");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("所有人员");

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("覆盖已有记录");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap(129, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3)
                    .addComponent(jCheckBox1)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("人员预览："));
        jPanel2.setLayout(new java.awt.BorderLayout());

        btnOk.setText("生成");

        btnClose.setText("关闭");

        jLabel1.setText("注意：默认对于姓名为空的记录自动过滤");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addGap(39, 39, 39))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnClose)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOk;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
