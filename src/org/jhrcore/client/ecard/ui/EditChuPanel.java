/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import com.fr.view.core.DateUtil;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.ecard.Ecard_chu;
/*     */ import org.jhrcore.entity.ecard.Epos;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.msg.CommMsg;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ContextManager;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.SysUtil;
/*     */ 
/*     */ public class EditChuPanel
/*     */   extends JPanel
/*     */ {
/*  31 */   private BeanPanel beanPanel = new BeanPanel();
/*     */   private Object curObj;
/*     */   private EposSelectDlg dlg;
/*     */   private JPanel bPanel;
/*     */   
/*  36 */   public EditChuPanel() { initComponents();
/*  37 */     initOthers();
/*  38 */     setupEvents();
/*     */   }
/*     */   
/*     */   public EditChuPanel(Object obj) {
/*  42 */     initComponents();
/*  43 */     this.curObj = obj;
/*  44 */     initOthers();
/*  45 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  49 */     this.bPanel.add(new JScrollPane(this.beanPanel), "Center");
/*  50 */     this.beanPanel.setBean(this.curObj);
/*  51 */     this.beanPanel.setEditable(true);
/*  52 */     this.beanPanel.bind();
/*     */   }
/*     */   
/*     */   private void setupEvents()
/*     */   {
/*  57 */     this.btnFl.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  61 */         if (EditChuPanel.this.dlg == null) {
/*  62 */           EditChuPanel.this.dlg = new EposSelectDlg();
/*     */         }
/*  64 */         EditChuPanel.this.dlg.setIsOk();
/*  65 */         EditChuPanel.this.dlg.setTitle("选择Pos机");
/*  66 */         ContextManager.locateOnMainScreenCenter(EditChuPanel.this.dlg);
/*  67 */         EditChuPanel.this.dlg.setVisible(true);
/*  68 */         if ((EditChuPanel.this.dlg.isOk()) && 
/*  69 */           (EditChuPanel.this.dlg.getSelectObject() != null)) {
/*  70 */           Epos tempPos = EditChuPanel.this.dlg.getSelectObject();
/*  71 */           Ecard_chu chu = (Ecard_chu)EditChuPanel.this.curObj;
/*  72 */           boolean existFlag = CommUtil.exists("select 1 from Ecard_leave where ecard_leave_flag='holiday' and ecard_leave_date='" + DateUtil.DateToStr(chu.getChu_date()) + "'");
/*  73 */           if (existFlag) {
/*  74 */             float fl = (float)(0.002D + Float.valueOf(tempPos.getEpos_fei()).floatValue());
/*  75 */             chu.setChu_fl("" + SysUtil.round(fl, 4));
/*     */           } else {
/*  77 */             chu.setChu_fl(tempPos.getEpos_fei());
/*     */           }
/*     */           
/*  80 */           chu.setChu_item(tempPos.getEpos_item());
/*  81 */           chu.setEpos_code(tempPos.getEpos_code());
/*  82 */           chu.setEpos_name(tempPos.getEpos_name());
/*  83 */           EditChuPanel.this.beanPanel.bind();
/*     */         }
/*     */         
/*     */       }
/*  87 */     });
/*  88 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  92 */         ValidateSQLResult result = CommUtil.updateEntity(EditChuPanel.this.beanPanel.getBean());
/*  93 */         if (result.getResult() == 0) {
/*  94 */           MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
/*  95 */           ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(EditChuPanel.this.btnClose));
/*     */         } else {
/*  97 */           MsgUtil.showHRSaveErrorMsg(result);
/*     */         }
/*     */       }
/* 100 */     });
/* 101 */     this.btnClose.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 105 */         ModelFrame.close((ModelFrame)JOptionPane.getFrameForComponent(EditChuPanel.this.btnClose));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private JButton btnClose;
/*     */   
/*     */   private JButton btnFl;
/*     */   
/*     */   private JButton btnSave;
/*     */   private JToolBar jToolBar1;
/*     */   private void initComponents()
/*     */   {
/* 119 */     this.bPanel = new JPanel();
/* 120 */     this.btnSave = new JButton();
/* 121 */     this.btnClose = new JButton();
/* 122 */     this.jToolBar1 = new JToolBar();
/* 123 */     this.btnFl = new JButton();
/*     */     
/* 125 */     this.bPanel.setLayout(new BorderLayout());
/*     */     
/* 127 */     this.btnSave.setText("保存");
/*     */     
/* 129 */     this.btnClose.setText("关闭");
/*     */     
/* 131 */     this.jToolBar1.setFloatable(false);
/* 132 */     this.jToolBar1.setRollover(true);
/*     */     
/* 134 */     this.btnFl.setText("设置费率");
/* 135 */     this.btnFl.setFocusable(false);
/* 136 */     this.btnFl.setHorizontalTextPosition(0);
/* 137 */     this.btnFl.setVerticalTextPosition(3);
/* 138 */     this.jToolBar1.add(this.btnFl);
/*     */     
/* 140 */     GroupLayout layout = new GroupLayout(this);
/* 141 */     setLayout(layout);
/* 142 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.bPanel, -1, -1, 32767).addGroup(layout.createSequentialGroup().addContainerGap(213, 32767).addComponent(this.btnSave).addGap(33, 33, 33).addComponent(this.btnClose).addGap(67, 67, 67)).addComponent(this.jToolBar1, -1, -1, 32767));
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
/* 153 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jToolBar1, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.bPanel, -1, 259, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnSave).addComponent(this.btnClose)).addContainerGap()));
/*     */   }
/*     */ }

