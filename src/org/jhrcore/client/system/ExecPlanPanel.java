/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExecPlanPanel.java
 *
 * Created on 2013-9-25, 11:52:00
 */
package org.jhrcore.client.system;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.CommExecPlan;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.ui.JhrDatePicker;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.UtilTool;

/**
 *
 * @author mxliteboss
 */
public class ExecPlanPanel extends javax.swing.JPanel {

    private JhrDatePicker jdpStartTime = new JhrDatePicker("HH:mm");
    private JhrDatePicker jdpEndTime = new JhrDatePicker("HH:mm");
    private JhrDatePicker jdpStartDate = new JhrDatePicker("yyyy-MM-dd");
    private JhrDatePicker jdpEndDate = new JhrDatePicker("yyyy-MM-dd");
    private CommExecPlan plan;

    /** Creates new form ExecPlanPanel */
    public ExecPlanPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        pnlStartTime.setLayout(new BorderLayout());
        pnlEndTime.setLayout(new BorderLayout());
        pnlStartDate.setLayout(new BorderLayout());
        pnlEndDate.setLayout(new BorderLayout());
        pnlStartTime.add(jdpStartTime);
        pnlEndTime.add(jdpEndTime);
        pnlStartDate.add(jdpStartDate);
        pnlEndDate.add(jdpEndDate);
        plan = (CommExecPlan) CommUtil.fetchEntityBy("from CommExecPlan");
        if (plan != null) {
            jcbbCyc.setSelectedItem(plan.getCyc_type());
            refreshCycValue();
            updateRate();
        } else {
            plan = (CommExecPlan) UtilTool.createUIDEntity(CommExecPlan.class);
        }
    }

    private void setupEvents() {
        jcbbCyc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCyc();
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                plan.setPlan_name("");
                String type = (String) jcbbCyc.getSelectedItem();
                plan.setCyc_type(type);
                String cyc_gapmore = "";
                if (type.equals("每周")) {
                    if (jTextField2.getText() == null || "".equals(jTextField2.getText())) {
                        MsgUtil.showInfoMsg("请输入周计划的执行间隔");
                        jTextField2.requestFocus();
                        return;
                    }
                    if (!isNumeric(jTextField2.getText())) {
                        MsgUtil.showInfoMsg("周计划的执行间隔必须为整数");
                        jTextField2.requestFocus();
                        return;
                    }
                    plan.setCyc_gap(Integer.parseInt(jTextField2.getText()));
                    boolean weekSelect = false;
                    if (jCheckBox1.isSelected()) {
                        cyc_gapmore += "Mon,";
                        weekSelect = true;
                    }
                    if (jCheckBox5.isSelected()) {
                        cyc_gapmore += "Tue,";
                        weekSelect = true;
                    }
                    if (jCheckBox2.isSelected()) {
                        cyc_gapmore += "Wed,";
                        weekSelect = true;
                    }
                    if (jCheckBox6.isSelected()) {
                        cyc_gapmore += "Thu,";
                        weekSelect = true;
                    }
                    if (jCheckBox3.isSelected()) {
                        cyc_gapmore += "Fri,";
                        weekSelect = true;
                    }
                    if (jCheckBox4.isSelected()) {
                        cyc_gapmore += "Sat,";
                        weekSelect = true;
                    }
                    if (jCheckBox7.isSelected()) {
                        cyc_gapmore += "Sun,";
                        weekSelect = true;
                    }
                    if (!weekSelect) {
                        MsgUtil.showInfoMsg("周计划请至少选择一天");
                        return;
                    }
                    plan.setCyc_gapmore(cyc_gapmore);
                } else if (type.equals("每天")) {
                    if (jTextField3.getText() == null || "".equals(jTextField3.getText())) {
                        MsgUtil.showInfoMsg("请输入按天计划的执行间隔");
                        jTextField3.requestFocus();
                        return;
                    }
                    if (!isNumeric(jTextField3.getText())) {
                        MsgUtil.showInfoMsg("天计划的执行间隔必须为整数");
                        jTextField3.requestFocus();
                        return;
                    }
                    plan.setCyc_gap(Integer.parseInt(jTextField3.getText()));
                    plan.setCyc_gapmore("");
                } else {
                    if (jTextField4.getText() == null || "".equals(jTextField4.getText())) {
                        MsgUtil.showInfoMsg("请输入月计划的执行间隔");
                        jTextField4.requestFocus();
                        return;
                    }
                    if (!isNumeric(jTextField4.getText())) {
                        MsgUtil.showInfoMsg("月计划的执行间隔必须为整数");
                        jTextField4.requestFocus();
                        return;
                    }
                    plan.setCyc_gap(Integer.parseInt(jTextField4.getText()));
                    boolean monthSelect = false;
                    if (jCheckBox39.isSelected()) {
                        cyc_gapmore += "(1),";
                        monthSelect = true;
                    }
                    if (jCheckBox8.isSelected()) {
                        cyc_gapmore += "(2),";
                        monthSelect = true;
                    }
                    if (jCheckBox10.isSelected()) {
                        cyc_gapmore += "(3),";
                        monthSelect = true;
                    }
                    if (jCheckBox12.isSelected()) {
                        cyc_gapmore += "(4),";
                        monthSelect = true;
                    }
                    if (jCheckBox13.isSelected()) {
                        cyc_gapmore += "(5),";
                        monthSelect = true;
                    }
                    if (jCheckBox15.isSelected()) {
                        cyc_gapmore += "(6),";
                        monthSelect = true;
                    }
                    if (jCheckBox16.isSelected()) {
                        cyc_gapmore += "(7),";
                        monthSelect = true;
                    }
                    if (jCheckBox17.isSelected()) {
                        cyc_gapmore += "(8),";
                        monthSelect = true;
                    }
                    if (jCheckBox18.isSelected()) {
                        cyc_gapmore += "(9),";
                        monthSelect = true;
                    }
                    if (jCheckBox19.isSelected()) {
                        cyc_gapmore += "(10),";
                        monthSelect = true;
                    }
                    if (jCheckBox20.isSelected()) {
                        cyc_gapmore += "(11),";
                        monthSelect = true;
                    }
                    if (jCheckBox21.isSelected()) {
                        cyc_gapmore += "(12),";
                        monthSelect = true;
                    }
                    if (jCheckBox22.isSelected()) {
                        cyc_gapmore += "(13),";
                        monthSelect = true;
                    }
                    if (jCheckBox23.isSelected()) {
                        cyc_gapmore += "(14),";
                        monthSelect = true;
                    }
                    if (jCheckBox24.isSelected()) {
                        cyc_gapmore += "(15),";
                        monthSelect = true;
                    }
                    if (jCheckBox25.isSelected()) {
                        cyc_gapmore += "(16),";
                        monthSelect = true;
                    }
                    if (jCheckBox9.isSelected()) {
                        cyc_gapmore += "(17),";
                        monthSelect = true;
                    }
                    if (jCheckBox11.isSelected()) {
                        cyc_gapmore += "(18),";
                        monthSelect = true;
                    }
                    if (jCheckBox26.isSelected()) {
                        cyc_gapmore += "(19),";
                        monthSelect = true;
                    }
                    if (jCheckBox27.isSelected()) {
                        cyc_gapmore += "(20),";
                        monthSelect = true;
                    }
                    if (jCheckBox28.isSelected()) {
                        cyc_gapmore += "(21),";
                        monthSelect = true;
                    }
                    if (jCheckBox29.isSelected()) {
                        cyc_gapmore += "(22),";
                        monthSelect = true;
                    }
                    if (jCheckBox30.isSelected()) {
                        cyc_gapmore += "(23),";
                        monthSelect = true;
                    }
                    if (jCheckBox32.isSelected()) {
                        cyc_gapmore += "(24),";
                        monthSelect = true;
                    }
                    if (jCheckBox33.isSelected()) {
                        cyc_gapmore += "(25),";
                        monthSelect = true;
                    }
                    if (jCheckBox34.isSelected()) {
                        cyc_gapmore += "(26),";
                        monthSelect = true;
                    }
                    if (jCheckBox35.isSelected()) {
                        cyc_gapmore += "(27),";
                        monthSelect = true;
                    }
                    if (jCheckBox36.isSelected()) {
                        cyc_gapmore += "(28),";
                        monthSelect = true;
                    }
                    if (jCheckBox37.isSelected()) {
                        cyc_gapmore += "(29),";
                        monthSelect = true;
                    }
                    if (jCheckBox38.isSelected()) {
                        cyc_gapmore += "(30),";
                        monthSelect = true;
                    }
                    if (jCheckBox40.isSelected()) {
                        cyc_gapmore += "(31),";
                        monthSelect = true;
                    }
                    if (!monthSelect) {
                        MsgUtil.showInfoMsg("月计划请至少选择一天");
                        return;
                    }
                    plan.setCyc_gapmore(cyc_gapmore);
                }
                if (jRadioButton1.isSelected()) {
                    plan.setDay_type("执行一次");
                } else {
                    plan.setDay_type("执行周期");
                    if (jTextField5.getText() == null || "".equals(jTextField5.getText())) {
                        MsgUtil.showInfoMsg("请输入每天执行间隔");
                        jTextField5.requestFocus();
                        return;
                    }
                    if (!isNumeric(jTextField5.getText())) {
                        MsgUtil.showInfoMsg("每天的执行间隔必须为整数");
                        jTextField5.requestFocus();
                        return;
                    }
                    plan.setDay_gapunit((String) jComboBox2.getSelectedItem());
                    plan.setDay_gap(Integer.parseInt(jTextField5.getText()));
                    plan.setDay_start(jdpStartTime.getDate());
                    plan.setDay_end(jdpEndTime.getDate());
                }
                plan.setCyc_start(jdpStartDate.getDate());
                if (jRadioButton3.isSelected()) {
                    plan.setCyc_end(jdpEndDate.getDate());
                } else {
                    plan.setCyc_end(null);
                }
                plan.setPlan_mark(jtfMark.getText());
                ValidateSQLResult vs = CommUtil.saveOrUpdate(plan);
                if (vs.getResult() == 0) {
                    MsgUtil.showInfoMsg("保存成功");
                } else {
                    MsgUtil.showErrorMsg(vs.getMsg());
                }
            }
        });
        refreshCyc();
    }

    private void refreshCyc() {
        String type = (String) jcbbCyc.getSelectedItem();
        jtpCyc.removeAll();
        if (type.equals("每周")) {
            jtpCyc.add(type, pnlCycWeek);
        } else if (type.equals("每天")) {
            jtpCyc.add(type, pnlCycDay);
        } else {
            jtpCyc.add(type, pnlCycMonth);
        }
        jtpCyc.updateUI();
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private void refreshCycValue() {
        String type = (String) jcbbCyc.getSelectedItem();
        jtpCyc.removeAll();
        String cyc_gapmore = plan.getCyc_gapmore();
        if (type.equals("每周")) {
            jtpCyc.add(type, pnlCycWeek);
            jTextField2.setText(plan.getCyc_gap().toString());
            if (cyc_gapmore.contains("Mon")) {
                jCheckBox1.setSelected(true);
            }
            if (cyc_gapmore.contains("Tue")) {
                jCheckBox5.setSelected(true);
            }
            if (cyc_gapmore.contains("Wed")) {
                jCheckBox2.setSelected(true);
            }
            if (cyc_gapmore.contains("Thu")) {
                jCheckBox6.setSelected(true);
            }
            if (cyc_gapmore.contains("Fri")) {
                jCheckBox3.setSelected(true);
            }
            if (cyc_gapmore.contains("Sat")) {
                jCheckBox4.setSelected(true);
            }
            if (cyc_gapmore.contains("Sun")) {
                jCheckBox7.setSelected(true);
            }

        } else if (type.equals("每天")) {
            jtpCyc.add(type, pnlCycDay);
            jTextField3.setText(plan.getCyc_gap().toString());
        } else {
            jtpCyc.add(type, pnlCycMonth);
            jTextField4.setText(plan.getCyc_gap().toString());
            if (cyc_gapmore.contains("(1)")) {
                jCheckBox39.setSelected(true);
            }
            if (cyc_gapmore.contains("(2)")) {
                jCheckBox8.setSelected(true);
            }
            if (cyc_gapmore.contains("(3)")) {
                jCheckBox10.setSelected(true);
            }
            if (cyc_gapmore.contains("(4)")) {
                jCheckBox12.setSelected(true);
            }
            if (cyc_gapmore.contains("(5)")) {
                jCheckBox13.setSelected(true);
            }
            if (cyc_gapmore.contains("(6)")) {
                jCheckBox15.setSelected(true);
            }
            if (cyc_gapmore.contains("(7)")) {
                jCheckBox16.setSelected(true);
            }
            if (cyc_gapmore.contains("(8)")) {
                jCheckBox17.setSelected(true);
            }
            if (cyc_gapmore.contains("(9)")) {
                jCheckBox18.setSelected(true);
            }
            if (cyc_gapmore.contains("(10)")) {
                jCheckBox19.setSelected(true);
            }
            if (cyc_gapmore.contains("(11)")) {
                jCheckBox20.setSelected(true);
            }
            if (cyc_gapmore.contains("(12)")) {
                jCheckBox21.setSelected(true);
            }
            if (cyc_gapmore.contains("(13)")) {
                jCheckBox22.setSelected(true);
            }
            if (cyc_gapmore.contains("(14)")) {
                jCheckBox23.setSelected(true);
            }
            if (cyc_gapmore.contains("(15)")) {
                jCheckBox24.setSelected(true);
            }
            if (cyc_gapmore.contains("(16)")) {
                jCheckBox25.setSelected(true);
            }
            if (cyc_gapmore.contains("(17)")) {
                jCheckBox9.setSelected(true);
            }
            if (cyc_gapmore.contains("(18)")) {
                jCheckBox11.setSelected(true);
            }
            if (cyc_gapmore.contains("(19)")) {
                jCheckBox26.setSelected(true);
            }
            if (cyc_gapmore.contains("(20)")) {
                jCheckBox27.setSelected(true);
            }
            if (cyc_gapmore.contains("(21)")) {
                jCheckBox28.setSelected(true);
            }
            if (cyc_gapmore.contains("(22)")) {
                jCheckBox29.setSelected(true);
            }
            if (cyc_gapmore.contains("(23)")) {
                jCheckBox30.setSelected(true);
            }
            if (cyc_gapmore.contains("(24)")) {
                jCheckBox32.setSelected(true);
            }
            if (cyc_gapmore.contains("(25)")) {
                jCheckBox33.setSelected(true);
            }
            if (cyc_gapmore.contains("(26)")) {
                jCheckBox34.setSelected(true);
            }
            if (cyc_gapmore.contains("(27)")) {
                jCheckBox35.setSelected(true);
            }
            if (cyc_gapmore.contains("(28)")) {
                jCheckBox36.setSelected(true);
            }
            if (cyc_gapmore.contains("(29)")) {
                jCheckBox37.setSelected(true);
            }
            if (cyc_gapmore.contains("(30)")) {
                jCheckBox38.setSelected(true);
            }
            if (cyc_gapmore.contains("(31)")) {
                jCheckBox40.setSelected(true);
            }
        }
        jtpCyc.updateUI();
    }

    private void updateRate() {
        if ("执行一次".equals(plan.getDay_type())) {
            jRadioButton1.setSelected(true);
        } else if ("执行周期".equals(plan.getDay_type())) {
            jRadioButton2.setSelected(true);
            jTextField5.setText(plan.getDay_gap().toString());
            jComboBox2.setSelectedItem(plan.getDay_gapunit());
            jdpStartTime.setDate(plan.getDay_start());
            jdpEndTime.setDate(plan.getDay_end());
        }
        jdpStartDate.setDate(plan.getCyc_start());
        if (plan.getCyc_end() == null) {
            jRadioButton4.setSelected(true);
        } else {
            jRadioButton3.setSelected(true);
            jdpEndDate.setDate(plan.getCyc_end());
        }
        jtfMark.setText(plan.getPlan_mark());
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jcbbCyc = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jtpCyc = new javax.swing.JTabbedPane();
        pnlCycWeek = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        pnlCycDay = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pnlCycMonth = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jCheckBox15 = new javax.swing.JCheckBox();
        jCheckBox16 = new javax.swing.JCheckBox();
        jCheckBox17 = new javax.swing.JCheckBox();
        jCheckBox18 = new javax.swing.JCheckBox();
        jCheckBox19 = new javax.swing.JCheckBox();
        jCheckBox20 = new javax.swing.JCheckBox();
        jCheckBox21 = new javax.swing.JCheckBox();
        jCheckBox22 = new javax.swing.JCheckBox();
        jCheckBox23 = new javax.swing.JCheckBox();
        jCheckBox24 = new javax.swing.JCheckBox();
        jCheckBox25 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox26 = new javax.swing.JCheckBox();
        jCheckBox27 = new javax.swing.JCheckBox();
        jCheckBox28 = new javax.swing.JCheckBox();
        jCheckBox29 = new javax.swing.JCheckBox();
        jCheckBox30 = new javax.swing.JCheckBox();
        jCheckBox32 = new javax.swing.JCheckBox();
        jCheckBox33 = new javax.swing.JCheckBox();
        jCheckBox34 = new javax.swing.JCheckBox();
        jCheckBox35 = new javax.swing.JCheckBox();
        jCheckBox36 = new javax.swing.JCheckBox();
        jCheckBox37 = new javax.swing.JCheckBox();
        jCheckBox38 = new javax.swing.JCheckBox();
        jCheckBox39 = new javax.swing.JCheckBox();
        jCheckBox40 = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        pnlStartTime = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        pnlEndTime = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        pnlStartDate = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        pnlEndDate = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        jtfMark = new javax.swing.JTextField();

        jLabel2.setText("频率");

        jLabel3.setText("执行：");

        jcbbCyc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "每月", "每周", "每天" }));

        jLabel4.setText("执行间隔：");

        jTextField2.setText("1");

        jLabel5.setText("周, 在");

        jCheckBox1.setText("星期一");

        jCheckBox2.setText("星期三");

        jCheckBox3.setText("星期五");

        jCheckBox4.setText("星期六");

        jCheckBox5.setText("星期二");

        jCheckBox6.setText("星期四");

        jCheckBox7.setText("星期日");

        javax.swing.GroupLayout pnlCycWeekLayout = new javax.swing.GroupLayout(pnlCycWeek);
        pnlCycWeek.setLayout(pnlCycWeekLayout);
        pnlCycWeekLayout.setHorizontalGroup(
            pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCycWeekLayout.createSequentialGroup()
                .addGroup(pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCycWeekLayout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addGroup(pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlCycWeekLayout.createSequentialGroup()
                            .addComponent(jCheckBox5)
                            .addGap(18, 18, 18)
                            .addComponent(jCheckBox6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox7))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlCycWeekLayout.createSequentialGroup()
                            .addComponent(jCheckBox1)
                            .addGap(18, 18, 18)
                            .addComponent(jCheckBox2)
                            .addGap(18, 18, 18)
                            .addComponent(jCheckBox3)
                            .addGap(18, 18, 18)
                            .addComponent(jCheckBox4))))
                .addContainerGap(298, Short.MAX_VALUE))
        );
        pnlCycWeekLayout.setVerticalGroup(
            pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCycWeekLayout.createSequentialGroup()
                .addGroup(pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCycWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpCyc.addTab("每周", pnlCycWeek);

        jTextField3.setText("1");

        jLabel6.setText("天");

        javax.swing.GroupLayout pnlCycDayLayout = new javax.swing.GroupLayout(pnlCycDay);
        pnlCycDay.setLayout(pnlCycDayLayout);
        pnlCycDayLayout.setHorizontalGroup(
            pnlCycDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCycDayLayout.createSequentialGroup()
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addContainerGap(530, Short.MAX_VALUE))
        );
        pnlCycDayLayout.setVerticalGroup(
            pnlCycDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCycDayLayout.createSequentialGroup()
                .addGroup(pnlCycDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        jtpCyc.addTab("每天", pnlCycDay);

        jTextField4.setText("1");

        jLabel7.setText("月, 在");

        jCheckBox8.setText(" 2");

        jCheckBox10.setText(" 3");

        jCheckBox12.setText(" 4");

        jCheckBox13.setText(" 5");

        jCheckBox15.setText(" 6");

        jCheckBox16.setText(" 7");

        jCheckBox17.setText(" 8");

        jCheckBox18.setText(" 9");

        jCheckBox19.setText("10");

        jCheckBox20.setText("11");

        jCheckBox21.setText("12");

        jCheckBox22.setText("13");

        jCheckBox23.setText("14");

        jCheckBox24.setText("15");

        jCheckBox25.setText("16");

        jCheckBox9.setText("17");

        jCheckBox11.setText("18");

        jCheckBox26.setText("19");

        jCheckBox27.setText("20");

        jCheckBox28.setText("21");

        jCheckBox29.setText("22");

        jCheckBox30.setText("23");

        jCheckBox32.setText("24");

        jCheckBox33.setText("25");

        jCheckBox34.setText("26");

        jCheckBox35.setText("27");

        jCheckBox36.setText("28");

        jCheckBox37.setText("29");

        jCheckBox38.setText("30");

        jCheckBox39.setText(" 1");

        jCheckBox40.setText("31");

        javax.swing.GroupLayout pnlCycMonthLayout = new javax.swing.GroupLayout(pnlCycMonth);
        pnlCycMonth.setLayout(pnlCycMonthLayout);
        pnlCycMonthLayout.setHorizontalGroup(
            pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCycMonthLayout.createSequentialGroup()
                .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCycMonthLayout.createSequentialGroup()
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addGroup(pnlCycMonthLayout.createSequentialGroup()
                        .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCycMonthLayout.createSequentialGroup()
                                .addComponent(jCheckBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlCycMonthLayout.createSequentialGroup()
                                .addComponent(jCheckBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox27, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox28, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCycMonthLayout.createSequentialGroup()
                                .addComponent(jCheckBox29, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox30, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox32, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox33, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox34, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox35, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox36, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox37, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox38, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox40, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2))
                            .addGroup(pnlCycMonthLayout.createSequentialGroup()
                                .addComponent(jCheckBox16, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox17, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox18, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox19, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox20, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox21, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox22, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox23, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox24, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox39)
                                    .addComponent(jCheckBox25, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCycMonthLayout.setVerticalGroup(
            pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCycMonthLayout.createSequentialGroup()
                .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(pnlCycMonthLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBox39)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox17, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox18, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox20, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox25, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCycMonthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox26, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox27, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox28, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox29, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox30, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox32, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox33, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox34, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox35, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox36, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox37, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox38, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox40, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpCyc.addTab("每月", pnlCycMonth);

        jLabel8.setText("每天频率");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("执行一次");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("执行间隔");

        jPanel4.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "小时", "分钟" }));

        jLabel9.setText("开始时间：");

        pnlStartTime.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout pnlStartTimeLayout = new javax.swing.GroupLayout(pnlStartTime);
        pnlStartTime.setLayout(pnlStartTimeLayout);
        pnlStartTimeLayout.setHorizontalGroup(
            pnlStartTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        pnlStartTimeLayout.setVerticalGroup(
            pnlStartTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jLabel10.setText("结束时间：");

        pnlEndTime.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout pnlEndTimeLayout = new javax.swing.GroupLayout(pnlEndTime);
        pnlEndTime.setLayout(pnlEndTimeLayout);
        pnlEndTimeLayout.setHorizontalGroup(
            pnlEndTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        pnlEndTimeLayout.setVerticalGroup(
            pnlEndTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jLabel11.setText("持续时间");

        jLabel12.setText("开始日期：");

        pnlStartDate.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout pnlStartDateLayout = new javax.swing.GroupLayout(pnlStartDate);
        pnlStartDate.setLayout(pnlStartDateLayout);
        pnlStartDateLayout.setHorizontalGroup(
            pnlStartDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        pnlStartDateLayout.setVerticalGroup(
            pnlStartDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setText("结束日期");

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("无结束日期");

        pnlEndDate.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout pnlEndDateLayout = new javax.swing.GroupLayout(pnlEndDate);
        pnlEndDate.setLayout(pnlEndDateLayout);
        pnlEndDateLayout.setHorizontalGroup(
            pnlEndDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        pnlEndDateLayout.setVerticalGroup(
            pnlEndDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jLabel13.setText("摘要");

        jLabel14.setText("说明：");

        btnSave.setText("保存");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtpCyc, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jcbbCyc, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnSave))))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton4)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButton3)
                                .addGap(18, 18, 18)
                                .addComponent(pnlEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(266, 266, 266))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfMark, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbbCyc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtpCyc, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton2)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(pnlStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnlEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton3))
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jtfMark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox18;
    private javax.swing.JCheckBox jCheckBox19;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox20;
    private javax.swing.JCheckBox jCheckBox21;
    private javax.swing.JCheckBox jCheckBox22;
    private javax.swing.JCheckBox jCheckBox23;
    private javax.swing.JCheckBox jCheckBox24;
    private javax.swing.JCheckBox jCheckBox25;
    private javax.swing.JCheckBox jCheckBox26;
    private javax.swing.JCheckBox jCheckBox27;
    private javax.swing.JCheckBox jCheckBox28;
    private javax.swing.JCheckBox jCheckBox29;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox30;
    private javax.swing.JCheckBox jCheckBox32;
    private javax.swing.JCheckBox jCheckBox33;
    private javax.swing.JCheckBox jCheckBox34;
    private javax.swing.JCheckBox jCheckBox35;
    private javax.swing.JCheckBox jCheckBox36;
    private javax.swing.JCheckBox jCheckBox37;
    private javax.swing.JCheckBox jCheckBox38;
    private javax.swing.JCheckBox jCheckBox39;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox40;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JComboBox jcbbCyc;
    private javax.swing.JTextField jtfMark;
    private javax.swing.JTabbedPane jtpCyc;
    private javax.swing.JPanel pnlCycDay;
    private javax.swing.JPanel pnlCycMonth;
    private javax.swing.JPanel pnlCycWeek;
    private javax.swing.JPanel pnlEndDate;
    private javax.swing.JPanel pnlEndTime;
    private javax.swing.JPanel pnlStartDate;
    private javax.swing.JPanel pnlStartTime;
    // End of variables declaration//GEN-END:variables
}
