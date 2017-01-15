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
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.msg.CommMsg;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ 
/*     */ 
/*     */ public class EditBeanPanel
/*     */   extends JPanel
/*     */ {
/*  26 */   private BeanPanel beanPanel = new BeanPanel();
/*     */   private Object curObj;
/*     */   private JPanel bPanel;
/*     */   
/*  30 */   public EditBeanPanel() { initComponents();
/*  31 */     initOthers();
/*  32 */     setupEvents();
/*     */   }
/*     */   
/*     */   public EditBeanPanel(Object obj) {
/*  36 */     initComponents();
/*  37 */     this.curObj = obj;
/*  38 */     initOthers();
/*  39 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  43 */     this.bPanel.add(new JScrollPane(this.beanPanel), "Center");
/*  44 */     this.beanPanel.setBean(this.curObj);
/*  45 */     this.beanPanel.setEditable(true);
/*  46 */     this.beanPanel.bind();
/*     */   }
/*     */   
/*     */   private void setupEvents()
/*     */   {
/*  51 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  55 */         ValidateSQLResult result = CommUtil.updateEntity(EditBeanPanel.this.curObj);
/*  56 */         if (result.getResult() == 0) {
/*  57 */           MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
/*  58 */           ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(EditBeanPanel.this.btnClose));
/*     */         } else {
/*  60 */           MsgUtil.showHRSaveErrorMsg(result);
/*     */         }
/*     */       }
/*  63 */     });
/*  64 */     this.btnClose.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  68 */         ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(EditBeanPanel.this.btnClose));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private JButton btnClose;
/*     */   
/*     */ 
/*     */   private JButton btnSave;
/*     */   
/*     */   private void initComponents()
/*     */   {
/*  82 */     this.bPanel = new JPanel();
/*  83 */     this.btnSave = new JButton();
/*  84 */     this.btnClose = new JButton();
/*     */     
/*  86 */     this.bPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
/*  87 */     this.bPanel.setLayout(new BorderLayout());
/*     */     
/*  89 */     this.btnSave.setText("保存");
/*     */     
/*  91 */     this.btnClose.setText("关闭");
/*     */     
/*  93 */     GroupLayout layout = new GroupLayout(this);
/*  94 */     setLayout(layout);
/*  95 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.bPanel, -1, -1, 32767).addGroup(layout.createSequentialGroup().addContainerGap(213, 32767).addComponent(this.btnSave).addGap(33, 33, 33).addComponent(this.btnClose).addGap(67, 67, 67)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 105 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.bPanel, -1, 278, 32767).addGap(18, 18, 18).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnSave).addComponent(this.btnClose)).addContainerGap()));
/*     */   }
/*     */ }

