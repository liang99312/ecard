/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JTextField;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.iservice.impl.EcardImpl;
/*     */ import org.jhrcore.ui.ContextManager;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.util.DateUtil;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ 
/*     */ public class CalcEcardRuPanel extends javax.swing.JPanel
/*     */ {
/*     */   private CardSelectDlg dlg;
/*  29 */   private List<String> select_keys = new ArrayList();
/*  30 */   private String calcType = "hk";
/*     */   private JButton btnCancel;
/*     */   
/*  33 */   public CalcEcardRuPanel() { initComponents();
/*  34 */     initOthers();
/*  35 */     setupEvents();
/*     */   }
/*     */   
/*     */   public CalcEcardRuPanel(String calcType) {
/*  39 */     initComponents();
/*  40 */     this.calcType = calcType;
/*  41 */     initOthers();
/*  42 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  46 */     this.btnSelect.setVisible(false);
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/*  50 */     this.cb_state.addItemListener(new java.awt.event.ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/*  54 */         CalcEcardRuPanel.this.btnSelect.setVisible(false);
/*  55 */         if ("指定卡".equals(CalcEcardRuPanel.this.cb_state.getSelectedItem().toString())) {
/*  56 */           CalcEcardRuPanel.this.btnSelect.setVisible(true);
/*     */         }
/*     */       }
/*  59 */     });
/*  60 */     this.btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  64 */         if (DateUtil.check_pay_month(CalcEcardRuPanel.this.tf_ym.getText())) {
/*  65 */           JOptionPane.showMessageDialog(null, "请输入正确年月");
/*  66 */           return;
/*     */         }
/*  68 */         ValidateSQLResult result = null;
/*  69 */         if ("hk".equals(CalcEcardRuPanel.this.calcType)) {
/*  70 */           result = EcardImpl.calcHuiKuan(CalcEcardRuPanel.this.tf_ym.getText(), CalcEcardRuPanel.this.cb_state.getSelectedItem().toString(), CalcEcardRuPanel.this.select_keys);
/*  71 */         } else if ("xf".equals(CalcEcardRuPanel.this.calcType)) {
/*  72 */           result = EcardImpl.calcXiaoFei(CalcEcardRuPanel.this.tf_ym.getText(), CalcEcardRuPanel.this.cb_state.getSelectedItem().toString(), CalcEcardRuPanel.this.select_keys);
/*     */         }
/*  74 */         if (result.getResult() == 0) {
/*  75 */           JOptionPane.showMessageDialog(null, "生成数据成功");
/*  76 */           ModelFrame.close();
/*     */         } else {
/*  78 */           MsgUtil.showHRSaveErrorMsg(result);
/*     */         }
/*     */         
/*     */       }
/*  82 */     });
/*  83 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */ 
/*     */       public void actionPerformed(ActionEvent e) {}
/*     */ 
/*     */ 
/*  89 */     });
/*  90 */     this.btnSelect.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  94 */         if (CalcEcardRuPanel.this.dlg == null) {
/*  95 */           CalcEcardRuPanel.this.dlg = new CardSelectDlg();
/*     */         }
/*  97 */         CalcEcardRuPanel.this.dlg.setTitle("选择信用卡");
/*  98 */         ContextManager.locateOnMainScreenCenter(CalcEcardRuPanel.this.dlg);
/*  99 */         CalcEcardRuPanel.this.dlg.setVisible(true);
/* 100 */         if (CalcEcardRuPanel.this.dlg.isOk()) {
/* 101 */           CalcEcardRuPanel.this.select_keys.clear();
/* 102 */           CalcEcardRuPanel.this.select_keys.addAll(CalcEcardRuPanel.this.dlg.getSelectKeys());
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private JButton btnOk;
/*     */   private JButton btnSelect;
/*     */   private JComboBox cb_state;
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   private JTextField tf_ym;
/*     */   private void initComponents()
/*     */   {
/* 117 */     this.jLabel1 = new JLabel();
/* 118 */     this.tf_ym = new JTextField();
/* 119 */     this.jLabel2 = new JLabel();
/* 120 */     this.cb_state = new JComboBox();
/* 121 */     this.btnSelect = new JButton();
/* 122 */     this.btnOk = new JButton();
/* 123 */     this.btnCancel = new JButton();
/*     */     
/* 125 */     this.jLabel1.setText("年   月：");
/*     */     
/* 127 */     this.tf_ym.setText("201601");
/*     */     
/* 129 */     this.jLabel2.setText("信用卡：");
/*     */     
/* 131 */     this.cb_state.setModel(new DefaultComboBoxModel(new String[] { "已激活", "已激活-普养", "已激活-中养", "已激活-精养", "已停止", "已停止-普养", "已停止-中养", "已停止-精养", "所有卡", "指定卡" }));
/*     */     
/* 133 */     this.btnSelect.setText("选择信用卡");
/*     */     
/* 135 */     this.btnOk.setText("生成");
/*     */     
/* 137 */     this.btnCancel.setText("取消");
/*     */     
/* 139 */     GroupLayout layout = new GroupLayout(this);
/* 140 */     setLayout(layout);
/* 141 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(39, 39, 39).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jLabel2, -1, -1, 32767).addComponent(this.jLabel1, -1, -1, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.tf_ym).addComponent(this.cb_state, 0, 173, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btnSelect)).addGroup(layout.createSequentialGroup().addGap(131, 131, 131).addComponent(this.btnOk).addGap(61, 61, 61).addComponent(this.btnCancel))).addContainerGap(-1, 32767)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(31, 31, 31).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.tf_ym, -2, -1, -2)).addGap(29, 29, 29).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2, -2, 17, -2).addComponent(this.cb_state, -2, -1, -2).addComponent(this.btnSelect)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 85, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnOk).addComponent(this.btnCancel)).addContainerGap()));
/*     */   }
/*     */ }
