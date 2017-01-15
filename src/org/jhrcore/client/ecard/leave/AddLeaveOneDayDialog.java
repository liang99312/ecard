/*     */ package org.jhrcore.client.ecard.leave;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSeparator;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Ecard_leave;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.rebuild.EntityBuilder;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.JhrDatePicker;
/*     */ import org.jhrcore.ui.action.CloseAction;
/*     */ import org.jhrcore.ui.task.IModuleCode;
/*     */ import org.jhrcore.util.DateUtil;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.UtilTool;
/*     */ 
/*     */ public class AddLeaveOneDayDialog
/*     */   extends JDialog implements IModuleCode
/*     */ {
/*  38 */   private JhrDatePicker jdp = new JhrDatePicker();
/*  39 */   private boolean click_ok = false;
/*     */   private ValidateSQLResult result;
/*     */   private Ecard_leave ecard_leave;
/*     */   private BeanPanel beanPanel;
/*  43 */   private String flag = "leave";
/*     */   public static final String module_code = "Ecard_leave.miAddOneDay";
/*     */   
/*  46 */   public boolean isClick_ok() { return this.click_ok; }
/*     */   
/*     */   public ValidateSQLResult getResult()
/*     */   {
/*  50 */     return this.result;
/*     */   }
/*     */   
/*     */   public AddLeaveOneDayDialog(Frame parent, String flag)
/*     */   {
/*  55 */     super(parent);
/*  56 */     this.flag = flag;
/*  57 */     setTitle("逐日增加");
/*  58 */     initComponents();
/*  59 */     initOthers();
/*  60 */     setupEvents();
/*     */   }
/*     */   
/*     */   public AddLeaveOneDayDialog() {
/*  64 */     setTitle("逐日增加");
/*  65 */     initComponents();
/*  66 */     initOthers();
/*  67 */     setupEvents();
/*     */   }
/*     */   
/*     */ 
/*     */   private JButton btnCancel;
/*     */   
/*     */   private JButton btnOk;
/*     */   
/*     */   private JLabel jLabel3;
/*     */   
/*     */   private void initComponents()
/*     */   {
/*  79 */     this.jPanel1 = new JPanel();
/*  80 */     this.jSeparator1 = new JSeparator();
/*  81 */     this.btnOk = new JButton();
/*  82 */     this.btnCancel = new JButton();
/*  83 */     this.jPanel3 = new JPanel();
/*  84 */     this.jLabel3 = new JLabel();
/*  85 */     this.jPanel2 = new JPanel();
/*  86 */     this.pnlMain = new JPanel();
/*     */     
/*  88 */     setDefaultCloseOperation(2);
/*  89 */     setModal(true);
/*     */     
/*  91 */     this.btnOk.setText("确定");
/*     */     
/*  93 */     this.btnCancel.setText("取消");
/*     */     
/*  95 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/*  96 */     this.jPanel1.setLayout(jPanel1Layout);
/*  97 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSeparator1, GroupLayout.Alignment.TRAILING, -1, 474, 32767).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap(260, 32767).addComponent(this.btnOk).addGap(60, 60, 60).addComponent(this.btnCancel).addGap(40, 40, 40)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 107 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jSeparator1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.btnCancel).addComponent(this.btnOk)).addContainerGap()));
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
/* 118 */     this.jLabel3.setText("休息日期");
/*     */     
/* 120 */     this.jPanel2.setLayout(new BorderLayout());
/*     */     
/* 122 */     GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
/* 123 */     this.jPanel3.setLayout(jPanel3Layout);
/* 124 */     jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.jLabel3).addGap(18, 18, 18).addComponent(this.jPanel2, -2, 104, -2).addContainerGap(283, 32767)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */     jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel2, -1, -1, 32767).addComponent(this.jLabel3, -2, 22, -2)).addGap(42, 42, 42)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 143 */     this.pnlMain.setLayout(new BorderLayout());
/*     */     
/* 145 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 146 */     getContentPane().setLayout(layout);
/* 147 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel1, -1, -1, 32767).addComponent(this.jPanel3, -1, -1, 32767).addComponent(this.pnlMain, -1, 474, 32767));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 153 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jPanel3, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.pnlMain, -1, 187, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPanel1, -2, -1, -2)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     pack();
/*     */   }
/*     */   
/*     */ 
/*     */   private JPanel jPanel1;
/*     */   
/*     */   private JPanel jPanel2;
/*     */   
/*     */   private JPanel jPanel3;
/*     */   
/*     */   private JSeparator jSeparator1;
/*     */   
/*     */   private JPanel pnlMain;
/*     */   private void initOthers()
/*     */   {
/* 178 */     this.ecard_leave = ((Ecard_leave)UtilTool.createUIDEntity(Ecard_leave.class));
/* 179 */     this.jPanel2.add(this.jdp, "Center");
/* 180 */     this.beanPanel = new BeanPanel();
/* 181 */     this.beanPanel.setColumns(1);
/* 182 */     this.pnlMain.add(new JScrollPane(this.beanPanel), "Center");
/* 183 */     List<TempFieldInfo> leave_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_leave.class, EntityBuilder.COMM_FIELD_VISIBLE);
/* 184 */     List fields = new ArrayList();
/* 185 */     for (TempFieldInfo tfi : leave_infos) {
/* 186 */       if ((!tfi.getField_name().equalsIgnoreCase("Ecard_leave_date")) && (!tfi.getField_name().equalsIgnoreCase("Ecard_leave_week")))
/*     */       {
/*     */ 
/* 189 */         fields.add(tfi.getField_name()); }
/*     */     }
/* 191 */     this.beanPanel.setBean(this.ecard_leave);
/* 192 */     this.beanPanel.setFields(fields);
/* 193 */     this.beanPanel.setEditable(true);
/* 194 */     this.beanPanel.bind();
/*     */   }
/*     */   
/* 197 */   private void setupEvents() { CloseAction.doCloseAction(this.btnCancel);
/* 198 */     this.btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 202 */         AddLeaveOneDayDialog.this.ecard_leave.setEcard_leave_date(AddLeaveOneDayDialog.this.jdp.getDate());
/* 203 */         System.out.print(DateUtil.toStringForQuery(AddLeaveOneDayDialog.this.jdp.getDate()));
/* 204 */         AddLeaveOneDayDialog.this.ecard_leave.setEcard_leave_flag(AddLeaveOneDayDialog.this.flag);
/* 205 */         AddLeaveOneDayDialog.this.ecard_leave.setEcard_leave_week(DateUtil.getDateWeek(AddLeaveOneDayDialog.this.ecard_leave.getEcard_leave_date()));
/* 206 */         if (AddLeaveOneDayDialog.this.ecard_leave.getEcard_leave_name().trim().equals("")) {
/* 207 */           MsgUtil.showInfoMsg("请输入休息日名称");
/* 208 */           return;
/*     */         }
/* 210 */         if (CommUtil.exists("select 1 from Ecard_leave where ecard_leave_flag ='" + AddLeaveOneDayDialog.this.flag + "' and ecard_leave_date=" + DateUtil.toStringForQuery(AddLeaveOneDayDialog.this.jdp.getDate()))) {
/* 211 */           if (MsgUtil.showNotConfirmDialog("该日期已添加")) {
/* 212 */             return;
/*     */           }
/* 214 */           AddLeaveOneDayDialog.this.result = CommUtil.excuteSQL("update Ecard_leave set ecard_leave_name='" + AddLeaveOneDayDialog.this.ecard_leave.getEcard_leave_name() + "' where ecard_leave_date=" + DateUtil.toStringForQuery(AddLeaveOneDayDialog.this.jdp.getDate()));
/*     */         } else {
/* 216 */           AddLeaveOneDayDialog.this.result = CommUtil.saveEntity(AddLeaveOneDayDialog.this.ecard_leave); }
/* 217 */         AddLeaveOneDayDialog.this.click_ok = true;
/* 218 */         AddLeaveOneDayDialog.this.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public String getModuleCode()
/*     */   {
/* 226 */     return "Ecard_leave.miAddOneDay";
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\leave\AddLeaveOneDayDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */