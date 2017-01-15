/*     */ package org.jhrcore.client.ecard.leave;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
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
/*     */ import org.jhrcore.util.PublicUtil;
/*     */ import org.jhrcore.util.UtilTool;
/*     */ 
/*     */ public class AddLeaveManyDayDialog
/*     */   extends JDialog implements IModuleCode
/*     */ {
/*  42 */   private JhrDatePicker jdpStart = new JhrDatePicker();
/*  43 */   private JhrDatePicker jdpEnd = new JhrDatePicker();
/*  44 */   private boolean click_ok = false;
/*     */   private ValidateSQLResult result;
/*     */   private Ecard_leave k_leave_new;
/*     */   private BeanPanel beanPanel;
/*  48 */   private List fields = new ArrayList();
/*  49 */   private String flag = "leave";
/*     */   public static final String module_code = "Ecard_leave.miAddManyDay";
/*     */   
/*     */   public boolean isClick_ok() {
/*  53 */     return this.click_ok;
/*     */   }
/*     */   
/*     */   public ValidateSQLResult getResult() {
/*  57 */     return this.result;
/*     */   }
/*     */   
/*     */   public AddLeaveManyDayDialog(Frame parent, String flag)
/*     */   {
/*  62 */     super(parent);
/*  63 */     this.flag = flag;
/*  64 */     setTitle("多日连续增加");
/*  65 */     initComponents();
/*  66 */     initOthers();
/*  67 */     setupEvents();
/*     */   }
/*     */   
/*     */   public AddLeaveManyDayDialog() {
/*  71 */     setTitle("多日连续增加");
/*  72 */     initComponents();
/*  73 */     initOthers();
/*  74 */     setupEvents();
/*     */   }
/*     */   
/*     */ 
/*     */   private JButton btnCancel;
/*     */   
/*     */   private JButton btnOk;
/*     */   private JLabel jLabel3;
/*     */   private JLabel jLabel6;
/*     */   private JLabel jLabel7;
/*     */   private void initComponents()
/*     */   {
/*  86 */     this.jPanel1 = new JPanel();
/*  87 */     this.jSeparator1 = new JSeparator();
/*  88 */     this.btnOk = new JButton();
/*  89 */     this.btnCancel = new JButton();
/*  90 */     this.jPanel4 = new JPanel();
/*  91 */     this.jLabel3 = new JLabel();
/*  92 */     this.jPanel2 = new JPanel();
/*  93 */     this.jLabel7 = new JLabel();
/*  94 */     this.jLabel8 = new JLabel();
/*  95 */     this.jPanel3 = new JPanel();
/*  96 */     this.jLabel6 = new JLabel();
/*  97 */     this.pnlMain = new JPanel();
/*     */     
/*  99 */     setDefaultCloseOperation(2);
/* 100 */     setModal(true);
/*     */     
/* 102 */     this.btnOk.setText("确定");
/*     */     
/* 104 */     this.btnCancel.setText("取消");
/*     */     
/* 106 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 107 */     this.jPanel1.setLayout(jPanel1Layout);
/* 108 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSeparator1, GroupLayout.Alignment.TRAILING, -1, 474, 32767).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap(277, 32767).addComponent(this.btnOk).addGap(43, 43, 43).addComponent(this.btnCancel).addGap(40, 40, 40)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 118 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jSeparator1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.btnCancel).addComponent(this.btnOk)).addContainerGap()));
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
/* 129 */     this.jPanel4.setPreferredSize(new Dimension(474, 122));
/*     */     
/* 131 */     this.jLabel3.setText("开始日期");
/*     */     
/* 133 */     this.jPanel2.setLayout(new BorderLayout());
/*     */     
/* 135 */     this.jLabel7.setText("（含此日）");
/*     */     
/* 137 */     this.jLabel8.setText("（含此日）");
/*     */     
/* 139 */     this.jPanel3.setLayout(new BorderLayout());
/*     */     
/* 141 */     this.jLabel6.setText("结束日期");
/*     */     
/* 143 */     GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
/* 144 */     this.jPanel4.setLayout(jPanel4Layout);
/* 145 */     jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(jPanel4Layout.createSequentialGroup().addComponent(this.jLabel3).addGap(18, 18, 18).addComponent(this.jPanel2, -2, 87, -2)).addGroup(jPanel4Layout.createSequentialGroup().addComponent(this.jLabel6).addGap(18, 18, 18).addComponent(this.jPanel3, -2, 87, -2))).addGap(18, 18, 18).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel8).addComponent(this.jLabel7)).addContainerGap(-1, 32767)));
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
/* 164 */     jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGap(32, 32, 32).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel7, GroupLayout.Alignment.TRAILING, -2, 22, -2).addComponent(this.jLabel3, GroupLayout.Alignment.TRAILING, -2, 22, -2).addComponent(this.jPanel2, GroupLayout.Alignment.TRAILING, -2, 22, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jPanel3, -2, 22, -2).addComponent(this.jLabel6)).addComponent(this.jLabel8, -2, 22, -2)).addContainerGap(40, 32767)));
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
/* 181 */     this.pnlMain.setLayout(new BorderLayout());
/*     */     
/* 183 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 184 */     getContentPane().setLayout(layout);
/* 185 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -1, -1, 32767).addContainerGap()).addComponent(this.jPanel4, -1, 484, 32767).addComponent(this.pnlMain, -1, 484, 32767));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jPanel4, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.pnlMain, -1, 204, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPanel1, -2, -1, -2)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */     pack();
/*     */   }
/*     */   
/*     */ 
/*     */   private JLabel jLabel8;
/*     */   
/*     */   private JPanel jPanel1;
/*     */   
/*     */   private JPanel jPanel2;
/*     */   
/*     */   private JPanel jPanel3;
/*     */   
/*     */   private JPanel jPanel4;
/*     */   
/*     */   private JSeparator jSeparator1;
/*     */   private JPanel pnlMain;
/*     */   private void initOthers()
/*     */   {
/* 221 */     Calendar c = Calendar.getInstance();
/* 222 */     c.add(1, 1);
/* 223 */     this.jdpEnd.setDate(c.getTime());
/* 224 */     this.jPanel2.add(this.jdpStart, "Center");
/* 225 */     this.jPanel3.add(this.jdpEnd, "Center");
/* 226 */     this.k_leave_new = ((Ecard_leave)UtilTool.createUIDEntity(Ecard_leave.class));
/*     */     
/* 228 */     this.beanPanel = new BeanPanel();
/* 229 */     this.beanPanel.setColumns(1);
/* 230 */     this.pnlMain.add(new JScrollPane(this.beanPanel), "Center");
/* 231 */     List<TempFieldInfo> leave_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_leave.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*     */     
/* 233 */     for (TempFieldInfo tfi : leave_infos) {
/* 234 */       if ((!tfi.getField_name().equalsIgnoreCase("Ecard_leave_date")) && (!tfi.getField_name().equalsIgnoreCase("Ecard_leave_week")))
/*     */       {
/*     */ 
/* 237 */         this.fields.add(tfi.getField_name()); }
/*     */     }
/* 239 */     this.beanPanel.setBean(this.k_leave_new);
/* 240 */     this.beanPanel.setFields(this.fields);
/* 241 */     this.beanPanel.setEditable(true);
/* 242 */     this.beanPanel.bind();
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/* 246 */     CloseAction.doCloseAction(this.btnCancel);
/* 247 */     this.btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 251 */         String k_name = AddLeaveManyDayDialog.this.k_leave_new.getEcard_leave_name();
/* 252 */         if ((k_name == null) || (k_name.trim().equals(""))) {
/* 253 */           MsgUtil.showInfoMsg("请输入休息日名称");
/* 254 */           return;
/*     */         }
/* 256 */         Date startDate = AddLeaveManyDayDialog.this.jdpStart.getDate();
/* 257 */         Date endDate = AddLeaveManyDayDialog.this.jdpEnd.getDate();
/* 258 */         if (startDate.after(endDate)) {
/* 259 */           MsgUtil.showInfoMsg("结束日期不能早于开始日期");
/* 260 */           return;
/*     */         }
/* 262 */         String startTime = DateUtil.toStringForQuery(startDate);
/* 263 */         String endTime = DateUtil.toStringForQuery(endDate);
/* 264 */         String hql = "from Ecard_leave k where 1=1";
/* 265 */         hql = hql + " and k.ecard_leave_flag ='" + AddLeaveManyDayDialog.this.flag + "' and k.k_leave_date>= " + startTime + " and k.k_leave_date<= " + endTime;
/* 266 */         List exist_list = CommUtil.fetchEntities(hql);
/* 267 */         Hashtable<String, Ecard_leave> exist_keys = new Hashtable();
/* 268 */         for (Object obj : exist_list) {
/* 269 */           Ecard_leave k_leave = (Ecard_leave)obj;
/* 270 */           exist_keys.put(DateUtil.DateToStr(k_leave.getEcard_leave_date()), k_leave);
/*     */         }
/* 272 */         List<String> all_dates = new ArrayList();
/* 273 */         all_dates.add(DateUtil.DateToStr(startDate));
/* 274 */         Calendar c = Calendar.getInstance();
/* 275 */         c.setTime(startDate);
/* 276 */         while (c.getTime().before(endDate)) {
/* 277 */           String day = DateUtil.DateToStr(c.getTime());
/* 278 */           if (!all_dates.contains(day)) {
/* 279 */             all_dates.add(day);
/*     */           }
/* 281 */           c.add(5, 1);
/*     */         }
/* 283 */         String day = DateUtil.DateToStr(c.getTime());
/* 284 */         if (!all_dates.contains(day)) {
/* 285 */           all_dates.add(day);
/*     */         }
/*     */         
/* 288 */         List saveList = new ArrayList();
/* 289 */         for (String tmp : all_dates) {
/* 290 */           Ecard_leave k_leave = (Ecard_leave)exist_keys.get(tmp);
/* 291 */           if (k_leave == null) {
/* 292 */             k_leave = (Ecard_leave)UtilTool.createUIDEntity(Ecard_leave.class);
/*     */             
/*     */ 
/*     */ 
/* 296 */             PublicUtil.copyProperties(AddLeaveManyDayDialog.this.k_leave_new, k_leave, AddLeaveManyDayDialog.this.fields, AddLeaveManyDayDialog.this.fields);
/* 297 */             k_leave.setEcard_leave_flag(AddLeaveManyDayDialog.this.flag);
/* 298 */             saveList.add(k_leave);
/* 299 */             k_leave.setEcard_leave_name(k_name);
/* 300 */             k_leave.setEcard_leave_date(DateUtil.StrToDate(tmp));
/* 301 */             k_leave.setEcard_leave_week(DateUtil.getDateWeek(k_leave.getEcard_leave_date()));
/*     */           } }
/* 303 */         AddLeaveManyDayDialog.this.result = CommUtil.saveList(saveList);
/* 304 */         AddLeaveManyDayDialog.this.click_ok = true;
/* 305 */         AddLeaveManyDayDialog.this.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public String getModuleCode()
/*     */   {
/* 312 */     return "Ecard_leave.miAddManyDay";
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\leave\AddLeaveManyDayDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */