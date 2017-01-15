/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.msg.CommMsg;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.UtilTool;
/*     */ 
/*     */ public class AddEcardPanel extends JPanel
/*     */ {
/*  28 */   private BeanPanel beanPanel = new BeanPanel();
/*     */   private Ecard ecard;
/*     */   private JPanel bPanel;
/*     */   
/*  32 */   public AddEcardPanel() { initComponents();
/*  33 */     initOthers();
/*  34 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  38 */     this.bPanel.add(new JScrollPane(this.beanPanel), "Center");
/*  39 */     this.ecard = ((Ecard)UtilTool.createUIDEntity(Ecard.class));
/*  40 */     this.beanPanel.setBean(this.ecard);
/*  41 */     this.beanPanel.setEditable(true);
/*  42 */     this.beanPanel.bind();
/*     */   }
/*     */   
/*     */   private void setupEvents()
/*     */   {
/*  47 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  51 */         if (!AddEcardPanel.this.checkEcard()) {
/*  52 */           return;
/*     */         }
/*  54 */         if ((AddEcardPanel.this.ecard.getEcard_manager() == null) || (AddEcardPanel.this.ecard.getEcard_manager().equals(""))) {
/*  55 */           AddEcardPanel.this.ecard.setEcard_manager("系统管理员");
/*     */         }
/*  57 */         ValidateSQLResult result = CommUtil.saveEntity(AddEcardPanel.this.ecard);
/*  58 */         if (result.getResult() == 0) {
/*  59 */           MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
/*  60 */           if (AddEcardPanel.this.cb_continue.isSelected()) {
/*  61 */             AddEcardPanel.this.ecard = ((Ecard)UtilTool.createUIDEntity(Ecard.class));
/*  62 */             AddEcardPanel.this.beanPanel.setBean(AddEcardPanel.this.ecard);
/*  63 */             AddEcardPanel.this.beanPanel.setEditable(true);
/*  64 */             AddEcardPanel.this.beanPanel.bind();
/*     */           } else {
/*  66 */             ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(AddEcardPanel.this.btnClose));
/*     */           }
/*     */         } else {
/*  69 */           MsgUtil.showHRSaveErrorMsg(result);
/*     */         }
/*     */       }
/*  72 */     });
/*  73 */     this.btnClose.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  77 */         ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(AddEcardPanel.this.btnClose));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private boolean checkEcard() {
/*  83 */     if ((this.ecard.getEcard_name() == null) || ("".equals(this.ecard.getEcard_name()))) {
/*  84 */       JOptionPane.showMessageDialog(null, "请输入账户名");
/*  85 */       return false;
/*     */     }
/*  87 */     if ((this.ecard.getEcard_code() == null) || ("".equals(this.ecard.getEcard_code()))) {
/*  88 */       JOptionPane.showMessageDialog(null, "请输入信用卡号");
/*  89 */       return false;
/*     */     }
/*  91 */     if ((this.ecard.getEcard_bank() == null) || ("".equals(this.ecard.getEcard_bank()))) {
/*  92 */       JOptionPane.showMessageDialog(null, "请输入发卡银行");
/*  93 */       return false;
/*     */     }
/*  95 */     if ((this.ecard.getEcard_edu() == null) || (this.ecard.getEcard_edu().intValue() == 0)) {
/*  96 */       JOptionPane.showMessageDialog(null, "请输入额度");
/*  97 */       return false;
/*     */     }
/*  99 */     if ((this.ecard.getD_zhangdan() == null) || (this.ecard.getD_zhangdan().intValue() == 0)) {
/* 100 */       JOptionPane.showMessageDialog(null, "请输入账单日");
/* 101 */       return false;
/*     */     }
/* 103 */     if ((this.ecard.getD_huankuan() == null) || (this.ecard.getD_huankuan().intValue() == 0)) {
/* 104 */       JOptionPane.showMessageDialog(null, "请输入汇款天数");
/* 105 */       return false;
/*     */     }
/* 107 */     if ((this.ecard.getM_zonge() == null) || (this.ecard.getM_zonge().intValue() == 0)) {
/* 108 */       JOptionPane.showMessageDialog(null, "请输入汇款总额");
/* 109 */       return false;
/*     */     }
/* 111 */     if ((this.ecard.getM_hkstart() == null) || (this.ecard.getM_hkstart().intValue() == 0)) {
/* 112 */       JOptionPane.showMessageDialog(null, "请输入汇款开始日");
/* 113 */       return false;
/*     */     }
/* 115 */     if ((this.ecard.getM_hkend() == null) || (this.ecard.getM_hkend().intValue() == 0)) {
/* 116 */       JOptionPane.showMessageDialog(null, "请输入汇款结束日");
/* 117 */       return false;
/*     */     }
/* 119 */     if ((this.ecard.getM_cishu() == null) || (this.ecard.getM_cishu().intValue() == 0)) {
/* 120 */       JOptionPane.showMessageDialog(null, "请输入汇款次数");
/* 121 */       return false;
/*     */     }
/* 123 */     if ((this.ecard.getX_cishu() == null) || (this.ecard.getX_cishu().intValue() == 0)) {
/* 124 */       JOptionPane.showMessageDialog(null, "请输入消费次数");
/* 125 */       return false;
/*     */     }
/* 127 */     if (this.ecard.getEcard_jifen().intValue() > 60) {
/* 128 */       JOptionPane.showMessageDialog(null, "最大一次汇款不能大于60%");
/* 129 */       return false;
/*     */     }
/* 131 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private JButton btnClose;
/*     */   
/*     */   private JButton btnSave;
/*     */   
/*     */   private JCheckBox cb_continue;
/*     */   
/*     */   private void initComponents()
/*     */   {
/* 143 */     this.bPanel = new JPanel();
/* 144 */     this.btnSave = new JButton();
/* 145 */     this.btnClose = new JButton();
/* 146 */     this.cb_continue = new JCheckBox();
/*     */     
/* 148 */     this.bPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
/* 149 */     this.bPanel.setLayout(new BorderLayout());
/*     */     
/* 151 */     this.btnSave.setText("保存");
/*     */     
/* 153 */     this.btnClose.setText("关闭");
/*     */     
/* 155 */     this.cb_continue.setText("保存后继续添加");
/*     */     
/* 157 */     GroupLayout layout = new GroupLayout(this);
/* 158 */     setLayout(layout);
/* 159 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.bPanel, -1, -1, 32767).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.cb_continue).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 98, 32767).addComponent(this.btnSave).addGap(33, 33, 33).addComponent(this.btnClose).addGap(67, 67, 67)));
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
/* 171 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.bPanel, -1, 278, 32767).addGap(18, 18, 18).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnSave).addComponent(this.btnClose).addComponent(this.cb_continue)).addContainerGap()));
/*     */   }
/*     */ }
