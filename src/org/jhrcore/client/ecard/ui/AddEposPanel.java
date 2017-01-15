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
/*     */ import org.jhrcore.entity.ecard.Epos;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.msg.CommMsg;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.UtilTool;
/*     */ 
/*     */ public class AddEposPanel extends JPanel
/*     */ {
/*  28 */   private BeanPanel beanPanel = new BeanPanel();
/*     */   private Epos epos;
/*     */   private JPanel bPanel;
/*     */   
/*  32 */   public AddEposPanel() { initComponents();
/*  33 */     initOthers();
/*  34 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  38 */     this.bPanel.add(new JScrollPane(this.beanPanel), "Center");
/*  39 */     this.epos = ((Epos)UtilTool.createUIDEntity(Epos.class));
/*  40 */     this.beanPanel.setBean(this.epos);
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
/*  51 */         ValidateSQLResult result = CommUtil.saveEntity(AddEposPanel.this.epos);
/*  52 */         if (result.getResult() == 0) {
/*  53 */           MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
/*  54 */           if (AddEposPanel.this.cb_continue.isSelected()) {
/*  55 */             AddEposPanel.this.epos = ((Epos)UtilTool.createUIDEntity(Epos.class));
/*  56 */             AddEposPanel.this.beanPanel.setBean(AddEposPanel.this.epos);
/*  57 */             AddEposPanel.this.beanPanel.setEditable(true);
/*  58 */             AddEposPanel.this.beanPanel.bind();
/*     */           } else {
/*  60 */             ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(AddEposPanel.this.btnClose));
/*     */           }
/*     */         } else {
/*  63 */           MsgUtil.showHRSaveErrorMsg(result);
/*     */         }
/*     */       }
/*  66 */     });
/*  67 */     this.btnClose.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  71 */         ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(AddEposPanel.this.btnClose));
/*     */       }
/*     */     });
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
/*  85 */     this.bPanel = new JPanel();
/*  86 */     this.btnSave = new JButton();
/*  87 */     this.btnClose = new JButton();
/*  88 */     this.cb_continue = new JCheckBox();
/*     */     
/*  90 */     this.bPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
/*  91 */     this.bPanel.setLayout(new BorderLayout());
/*     */     
/*  93 */     this.btnSave.setText("保存");
/*     */     
/*  95 */     this.btnClose.setText("关闭");
/*     */     
/*  97 */     this.cb_continue.setText("保存后继续添加");
/*     */     
/*  99 */     GroupLayout layout = new GroupLayout(this);
/* 100 */     setLayout(layout);
/* 101 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.bPanel, -1, -1, 32767).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.cb_continue).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 98, 32767).addComponent(this.btnSave).addGap(33, 33, 33).addComponent(this.btnClose).addGap(67, 67, 67)));
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
/* 113 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.bPanel, -1, 278, 32767).addGap(18, 18, 18).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnSave).addComponent(this.btnClose).addComponent(this.cb_continue)).addContainerGap()));
/*     */   }
/*     */ }
