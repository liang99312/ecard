/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeptDelPanel.java
 *
 * Created on 2010-7-23, 23:59:57
 */
package org.jhrcore.client.dept;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptSelectDlg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.ValidateEntity;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class DeptDelPanel extends javax.swing.JPanel implements IModuleCode{

    private final String module_code = "DeptMng.miDel";
    private DeptCode src_dept;
    private FTable ftable;
    private DeptCode dst_dept;
    private List<IPickWindowCloseListener> listeners = new ArrayList<IPickWindowCloseListener>();

    public void addPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public void delPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.remove(listener);
    }

    public DeptDelPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form DeptDelPanel */
    public DeptDelPanel(DeptCode src_dept) {
        this.src_dept = src_dept;
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        ComponentUtil.setSysFuntion(this, module_code);
        jtfSrc.setText(src_dept.getContent() + "{" + src_dept.getDept_code() + "}");
        List<String> fields = new ArrayList<String>();
        fields.add("deptCode.dept_code");
        fields.add("deptCode.content");
        fields.add("a0190");
        fields.add("a0101");
        fields.add("a0191");
        ftable = new FTable(A01.class, fields, false, false, false, module_code) {

            @Override
            public Color getCellBackgroud(String fileName, Object cellValue, Object row_obj) {
                if (row_obj instanceof A01) {
                    if (!((A01) row_obj).getDeptCode().getDept_code().startsWith(src_dept.getDept_code())) {
                        return Color.BLUE;
                    }
                }
                return null;
            }
        };
        PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 bp join fetch bp.deptCode where bp.a01_key in");
        ftable.setObjects(CommUtil.fetchEntities("select a01_key from A01 a01 where a01.deptCode.dept_code like '" + src_dept.getDept_code() + "%' order by a01.deptCode.dept_code,a01.a0190"));
        pnlMain.add(ftable, BorderLayout.CENTER);
        ftable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMain.updateUI();
        lblTotal.setText(ftable.getObjects().size() + "");
        lblWait.setText(ftable.getObjects().size() + "");

    }

    private void setupEvents() {
        btnDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ValidateEntity ve = new ValidateEntity() {

                    @Override
                    public boolean isEntityValidate(Object obj) {
                        if (obj == null) {
                            return false;
                        }
                        if (obj instanceof DeptCode) {
                            DeptCode dc = (DeptCode) obj;
                            if (dc.getDept_code().startsWith(src_dept.getDept_code())) {
                                MsgUtil.showInfoMsg(DeptMngMsg.msgDeptCanNotRevocat);
                                return false;

                            }
                        }
                        return true;
                    }
                };
                DeptSelectDlg rdDlg = new DeptSelectDlg(UserContext.getDepts(false), dst_dept, TreeSelectMod.leafSelectMod, ve);
                ContextManager.locateOnScreenCenter(rdDlg);
                rdDlg.setVisible(true);
                if (rdDlg.isClick_ok()) {
                    dst_dept = rdDlg.getCurDept();
                    jtfDst.setText(dst_dept.getContent() + "{" + dst_dept.getDept_code() + "}");
                }
            }
        });
        btnSetDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dst_dept == null) {
//                    JOptionPane.showMessageDialog(null, "请先设置新部门", "错误", JOptionPane.ERROR_MESSAGE);
                    MsgUtil.showErrorMsg(DeptMngMsg.msgDeptCanNotRevocat);
                    return;
                }
                List list = ftable.getAllSelectObjects();
                for (Object obj : list) {
                    ((A01) obj).setDeptCode(dst_dept);
                }
                int needChangeNum = 0;
                for (Object obj : ftable.getObjects()) {
                    if (obj instanceof A01) {
                        if ((((A01) obj).getDeptCode().getDept_code()).startsWith(src_dept.getDept_code())) {
                            needChangeNum++;
                        }
                    } else {
                        needChangeNum++;
                    }
                }
                lblWait.setText(needChangeNum + "");
                pnlMain.updateUI();
            }
        });
        CloseAction.doCloseAction(btnCancel);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List depts = new ArrayList();
                depts.add(src_dept);
                List list = ftable.getObjects();
                for (Object obj : list) {
                    A01 a01 = (A01) obj;
                    if (a01.getDeptCode().getDept_code().startsWith(src_dept.getDept_code())) {
//                        JOptionPane.showMessageDialog(null, a01.getA0101() + "未设置部门");
                        MsgUtil.showInfoMsg(a01.getA0101() + DeptMngMsg.msgDeptSetupFail);
                        return;
                    }
                    depts.add(a01);
                }
                ValidateSQLResult validateSQLResult = DeptImpl.delDeptChgA01s(depts);
                if (validateSQLResult.getResult() == 0) {
                    for (IPickWindowCloseListener listener : listeners) {
                        listener.pickClose();
                    }
                } else {
                    MsgUtil.showHRSaveErrorMsg(validateSQLResult);
                }
                ModelFrame.close();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jtfSrc = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtfDst = new javax.swing.JTextField();
        btnDept = new javax.swing.JButton();
        btnSetDept = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblWait = new javax.swing.JLabel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setText("被撤销部门：");
        jToolBar1.add(jLabel1);

        jtfSrc.setEditable(false);
        jtfSrc.setMaximumSize(new java.awt.Dimension(120, 22));
        jtfSrc.setPreferredSize(new java.awt.Dimension(120, 22));
        jToolBar1.add(jtfSrc);

        jLabel2.setText(" 新部门：");
        jToolBar1.add(jLabel2);

        jtfDst.setEditable(false);
        jtfDst.setMaximumSize(new java.awt.Dimension(120, 22));
        jtfDst.setPreferredSize(new java.awt.Dimension(120, 22));
        jToolBar1.add(jtfDst);

        btnDept.setText("...");
        jToolBar1.add(btnDept);

        btnSetDept.setText("设置新部门");
        jToolBar1.add(btnSetDept);

        pnlMain.setBorder(javax.swing.BorderFactory.createTitledBorder("待调整人员："));
        pnlMain.setLayout(new java.awt.BorderLayout());

        btnOk.setText("确定");

        btnCancel.setText("取消");

        jLabel3.setText("总人数：");

        jLabel4.setText("未调整人数：");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblWait, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addGap(48, 48, 48))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOk)
                        .addComponent(btnCancel))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblWait, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDept;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSetDept;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField jtfDst;
    private javax.swing.JTextField jtfSrc;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblWait;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return this.module_code;
    }
}
