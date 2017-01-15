/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ICCardWritePanel.java
 *
 * Created on 2012-6-18, 14:38:04
 */
package org.jhrcore.client.personnel.comm;

import com.foundercy.pf.control.table.FTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.personnel.ICCardRead;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.StringUtil;

/**
 *
 * @author Administrator
 */
public class ICCardWritePanel extends javax.swing.JPanel implements IModuleCode {

    private A01 cur_a01;
    private String field_name = "";
    private ICCardRead iCCardRead = null;
    private String ic_str = "";
    private List<String> s_fields = new ArrayList<String>();
    private FTable tmp_table = new FTable(A01.class, true, false, false, "PersonSelectPanel");
    private String module_code = "EmpMng.btnWrite";

    public ICCardWritePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form ICCardWritePanel */
    public ICCardWritePanel(A01 cur_a01) {
        this.cur_a01 = cur_a01;
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        field_name = EmpCardUtil.getCard_no();
        setA01(cur_a01);
        ic_str = ConfigManager.getConfigManager().getProperty("ic_card_no");
        s_fields.add("a0190");
        s_fields.add("a0101");
        s_fields.add("a0177");
        s_fields.add("pydm");
        s_fields.add(field_name);

        List<TempFieldInfo> dept_infos1 = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
        List<TempFieldInfo> person_infos1 = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        List<TempFieldInfo> a01_all_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> a01_default_fields = new ArrayList<TempFieldInfo>();
        for (TempFieldInfo tfi : dept_infos1) {
            tfi.setField_name("deptCode." + tfi.getField_name());
            a01_all_fields.add(tfi);
            if (tfi.getField_name().equals("deptCode.content")) {
                a01_default_fields.add(tfi);
            }
        }
        for (TempFieldInfo tfi : person_infos1) {
            if (tfi.getField_name().equals("deptCode")) {
                continue;
            }
            a01_all_fields.add(tfi);
            if (tfi.getField_name().equals("a0190") || tfi.getField_name().equals("a0101")) {
                a01_default_fields.add(tfi);
            }
        }
        tmp_table.setRight_allow_flag(true);
        tmp_table.setAll_fields(a01_all_fields, a01_default_fields, new ArrayList(), "PersonModelDialog");
        tmp_table.removeItemByCodes("query;order;sum;replace");
    }

    private void setA01(A01 a01) {
        if (a01 != null) {
            tf_a0190.setText(a01.getA0190());
            tf_a0101.setText(a01.getA0101());
            if (a01.getA0107() != null) {
                String a0107 = a01.getA0107();
                if (!"".equals(a0107)) {
                    if ("1".equals(a0107) || "01".equals(a0107)) {
                        tf_a0107.setText(EmpMngMsg.msg045.toString());
                    } else if ("2".equals(a0107) || "02".equals(a0107)) {
                        tf_a0107.setText(EmpMngMsg.msg046.toString());
                    } else {
                        tf_a0107.setText(a0107);
                    }
                } else {
                    tf_a0107.setText("");
                }
            }
            if (field_name != null && !"".equalsIgnoreCase(field_name)) {
                if (PublicUtil.getProperty(a01, field_name) != null) {
                    String tmp_card = PublicUtil.getProperty(a01, field_name).toString();
                    if (tmp_card == null) {
                        tmp_card = "";
                    }
                    tf_card.setText(tmp_card);
                } else {
                    tf_card.setText("");
                }
            }
        } else {
            tf_a0190.setText("");
            tf_a0101.setText("");
            tf_card.setText("");
            tf_a0107.setText("");
        }
    }

    private void setupEvents() {
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                ModelFrame.close((ModelFrame) JOptionPane.getFrameForComponent(btnCancel));
                ModelFrame.close();
            }
        });
        btnRead.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setICCard();
            }
        });
        btnSaveCard.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveCard();
            }
        });
        btnWrite.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                writeCard();
            }
        });

        tf_search.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cur_a01 = null;
                String val = tf_search.getText().replace(" ", "").toUpperCase();
                String where_sql = "1=0";
                for (String f_name : s_fields) {
                    where_sql += " or " + f_name + "='" + val + "'";
                }
                List a01_list = CommUtil.fetchEntities("from A01 bp join fetch bp.deptCode where (" + where_sql
                        + ")");
                if (!a01_list.isEmpty()) {
                    if (a01_list.size() > 1) {
                        tmp_table.setObjects(a01_list);
                        if (!ModalDialog.doModal(tf_search, tmp_table, EmpMngMsg.ttl029, tmp_table)) {
                            return;
                        }
                        cur_a01 = (A01) tmp_table.getCurrentRow();
                    } else {
                        cur_a01 = (A01) a01_list.get(0);
                    }
                }
                setA01(cur_a01);
            }
        });

        if (field_name == null || "".equalsIgnoreCase(field_name)) {
//            JOptionPane.showMessageDialog(null, "请到工具中设置员工IC卡号");
            MsgUtil.showInfoMsg(EmpMngMsg.msg128);
//            ModelFrame.close((ModelFrame) JOptionPane.getFrameForComponent(btnCancel));
            ModelFrame.close();
        }
    }

    private void writeCard() {
        if (cur_a01 == null) {
//            JOptionPane.showMessageDialog(null, "请寻找人员");
            MsgUtil.showInfoMsg(EmpMngMsg.msg129);
            return;
        }
        if (iCCardRead == null) {
            iCCardRead = new ICCardRead();
            String s = iCCardRead.getLink(ic_str);
            if (!"1".equals(s)) {
                MsgUtil.showInfoMsg(s);
                iCCardRead = null;
                return;
            }
        }
        if (MsgUtil.showNotConfirmDialog(EmpMngMsg.msg130)) {
            return;
        }
        if (EmpCardUtil.WriteA01_mf800(cur_a01, iCCardRead) == 0) {
            ic_a0190.setText(tf_a0190.getText());
            ic_a0101.setText(tf_a0101.getText());
            ic_a0107.setText(tf_a0107.getText());
        }
    }

    private void setICCard() {
        iCCardRead = new ICCardRead();
        String s = iCCardRead.getLink(ic_str);
        if (!"1".equals(s)) {
//            JOptionPane.showMessageDialog(null, s);
            MsgUtil.showInfoMsg(s);
            iCCardRead = null;
            return;
        }
        String temp_card_no = EmpCardUtil.getIC_no(iCCardRead.getInfo(ic_str, "0", "-1"));
        ic_card.setText(temp_card_no);
        String temp_a0190 = StringUtil.toStringHex(iCCardRead.getInfo(ic_str, "0", "1")).replace(" ", "").replace(" ", "");
        ic_a0190.setText(temp_a0190);
        String temp_a0101 = StringUtil.toStringHex(iCCardRead.getInfo(ic_str, "0", "2")).replace(" ", "").replace(" ", "");
        ic_a0101.setText(temp_a0101);
        String temp_a0107 = StringUtil.toStringHex(iCCardRead.getInfo(ic_str, "0", "4")).replace(" ", "").replace(" ", "");
        ic_a0107.setText(temp_a0107);
    }

    private void saveCard() {
        if (cur_a01 == null) {
//            JOptionPane.showMessageDialog(null, "请寻找人员");
            MsgUtil.showInfoMsg(EmpMngMsg.msg129);
            return;
        }
        if (ic_card.getText() == null || "".equals(ic_card.getText().replace(" ", ""))) {
//            JOptionPane.showMessageDialog(null, "请读IC卡");
            MsgUtil.showInfoMsg(EmpMngMsg.msg131);
            return;
        }
        if (field_name == null || "".equalsIgnoreCase(field_name)) {
//            JOptionPane.showMessageDialog(null, "请到工具中设置员工IC卡号");
            MsgUtil.showInfoMsg(EmpMngMsg.msg132);
            return;
        }
        if (MsgUtil.showNotConfirmDialog(EmpMngMsg.msg133)) {
            return;
        }
//        PublicUtil.setValueBy2(cur_a01, field_name, ic_card.getText().replace(" ", ""));
        ValidateSQLResult result = CommUtil.excuteSQL("update a01 set " + field_name + "='" + ic_card.getText().replace(" ", "") + "' where a01_key='" + cur_a01.getA01_key() + "'");
        if (result.getResult() == 0) {
            tf_card.setText(ic_card.getText().replace(" ", ""));
            MsgUtil.showHRSaveSuccessMsg(null);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tf_search = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tf_card = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tf_a0190 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tf_a0101 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tf_a0107 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ic_card = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ic_a0190 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        ic_a0101 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        ic_a0107 = new javax.swing.JTextField();
        btnRead = new javax.swing.JButton();
        btnSaveCard = new javax.swing.JButton();
        btnWrite = new javax.swing.JButton();

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnCancel.setText("退出");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(398, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addGap(95, 95, 95))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("人员信息"));

        jLabel1.setText("查    找：");

        jLabel2.setText("卡    号：");

        tf_card.setBackground(new java.awt.Color(255, 255, 255));
        tf_card.setEditable(false);

        jLabel3.setText("人员编号：");

        tf_a0190.setBackground(new java.awt.Color(255, 255, 255));
        tf_a0190.setEditable(false);

        jLabel4.setText("姓    名：");

        tf_a0101.setBackground(new java.awt.Color(255, 255, 255));
        tf_a0101.setEditable(false);

        jLabel5.setText("性    别：");

        tf_a0107.setBackground(new java.awt.Color(255, 255, 255));
        tf_a0107.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tf_search, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tf_card)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_a0190, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_a0101, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_a0107, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tf_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tf_card, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tf_a0190, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tf_a0101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tf_a0107, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(128, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("IC信息"));

        jLabel6.setText("卡    号：");

        ic_card.setBackground(new java.awt.Color(255, 255, 255));
        ic_card.setEditable(false);

        jLabel7.setText("人员编号：");

        ic_a0190.setBackground(new java.awt.Color(255, 255, 255));
        ic_a0190.setEditable(false);

        jLabel8.setText("姓    名：");

        ic_a0101.setBackground(new java.awt.Color(255, 255, 255));
        ic_a0101.setEditable(false);

        jLabel9.setText("性    别：");

        ic_a0107.setBackground(new java.awt.Color(255, 255, 255));
        ic_a0107.setEditable(false);

        btnRead.setText("读IC卡");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ic_a0107, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ic_a0190, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ic_card, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                    .addComponent(btnRead)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ic_a0101, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRead)
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ic_card, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(ic_a0190, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ic_a0101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(ic_a0107, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(129, Short.MAX_VALUE))
        );

        btnSaveCard.setText("保存卡号");

        btnWrite.setText("写入IC卡");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnWrite)
                    .addComponent(btnSaveCard))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(btnSaveCard)
                        .addGap(37, 37, 37)
                        .addComponent(btnWrite)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRead;
    private javax.swing.JButton btnSaveCard;
    private javax.swing.JButton btnWrite;
    private javax.swing.JTextField ic_a0101;
    private javax.swing.JTextField ic_a0107;
    private javax.swing.JTextField ic_a0190;
    private javax.swing.JTextField ic_card;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField tf_a0101;
    private javax.swing.JTextField tf_a0107;
    private javax.swing.JTextField tf_a0190;
    private javax.swing.JTextField tf_card;
    private javax.swing.JTextField tf_search;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
