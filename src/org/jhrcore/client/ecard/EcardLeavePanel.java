/*     */ package org.jhrcore.client.ecard;
/*     */ 
/*     */ import com.foundercy.pf.control.listener.IPickFieldOrderListener;
/*     */ import com.foundercy.pf.control.listener.IPickQueryExListener;
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.client.UserContext;
/*     */ import org.jhrcore.client.ecard.leave.AddLeaveManyDayDialog;
/*     */ import org.jhrcore.client.ecard.leave.AddLeaveOneDayDialog;
/*     */ import org.jhrcore.client.ecard.leave.AddLeaveWeekDayDialog;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Ecard_leave;
/*     */ import org.jhrcore.entity.query.QueryScheme;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.entity.showstyle.ShowScheme;
/*     */ import org.jhrcore.msg.CommMsg;
/*     */ import org.jhrcore.rebuild.EntityBuilder;
/*     */ import org.jhrcore.ui.ContextManager;
/*     */ import org.jhrcore.ui.JhrDatePicker;
/*     */ import org.jhrcore.ui.listener.CommEditAction;
/*     */ import org.jhrcore.util.DateUtil;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.PublicUtil;
/*     */ import org.jhrcore.util.SysUtil;
/*     */ 
/*     */ public class EcardLeavePanel extends JPanel
/*     */ {
/*     */   private FTable ftable_leave;
/*     */   private FTable ftable_holiday;
/*  57 */   private JPopupMenu pp = new JPopupMenu();
/*  58 */   private JMenuItem miAddOneDay = new JMenuItem("逐日增加");
/*  59 */   private JMenuItem miAddManyDay = new JMenuItem("多日连续增加");
/*  60 */   private JMenuItem miAddWeekDay = new JMenuItem("按星期增加");
/*  61 */   private String order_sql = "e.ecard_leave_date desc";
/*  62 */   private String order_sql_h = "e.ecard_leave_date desc";
/*  63 */   private JLabel jLabel1 = new JLabel(" 起 ");
/*  64 */   private JLabel jLabel2 = new JLabel(" 止 ");
/*  65 */   private JhrDatePicker spDateFrom = new JhrDatePicker();
/*  66 */   private JhrDatePicker spDateTo = new JhrDatePicker();
/*  67 */   private JButton btnSearch = new JButton("查询");
/*     */   private Object curObj;
/*  69 */   private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
/*  70 */   private int flag1 = 0; private int flag2 = 0;
/*     */   private JButton btnAdd;
/*     */   
/*  73 */   public EcardLeavePanel() { initComponents();
/*  74 */     initOthers();
/*  75 */     setupEvents();
/*     */   }
/*     */   
/*     */   private JButton btnCancel;
/*  79 */   private void initOthers() { initToolBar();
/*  80 */     this.ftable_leave = new FTable(Ecard_leave.class, true, true, true, "");
/*  81 */     this.ftable_holiday = new FTable(Ecard_leave.class, true, true, true, "");
/*  82 */     List<TempFieldInfo> all_infos = new ArrayList();
/*  83 */     all_infos.addAll(EntityBuilder.getCommFieldInfoListOf(Ecard_leave.class, EntityBuilder.COMM_FIELD_VISIBLE));
/*  84 */     this.ftable_leave.setAll_fields(all_infos, all_infos, new ArrayList(), "EcardLeavePanel");
/*  85 */     this.ftable_holiday.setAll_fields(all_infos, all_infos, new ArrayList(), "EcardLeavePanel");
/*  86 */     this.order_sql = SysUtil.getOrderString(this.ftable_leave.getCurOrderScheme(), "e", this.order_sql, this.ftable_leave.getAll_fields());
/*  87 */     this.order_sql_h = SysUtil.getOrderString(this.ftable_holiday.getCurOrderScheme(), "e", this.order_sql_h, this.ftable_holiday.getAll_fields());
/*  88 */     List<String> fields = new ArrayList();
/*  89 */     fields.add("ecard_leave_date");
/*  90 */     this.ftable_leave.setDisable_fields(fields);
/*  91 */     this.ftable_leave.setRight_allow_flag(true);
/*  92 */     this.ftable_leave.removeColSumItem();
/*  93 */     this.pnlRight.add(this.ftable_leave, "Center");
/*  94 */     this.ftable_holiday.setDisable_fields(fields);
/*  95 */     this.ftable_holiday.setRight_allow_flag(true);
/*  96 */     this.ftable_holiday.removeColSumItem();
/*  97 */     this.pnlHoliday.add(this.ftable_holiday, "Center");
/*  98 */     PublicUtil.getProps_value().setProperty(Ecard_leave.class.getName(), "from Ecard_leave n where n.ecard_leave_key in");
/*     */   }
/*     */   
/*     */   private void initToolBar() {
/* 102 */     this.pp.add(this.miAddOneDay);
/* 103 */     this.pp.add(this.miAddManyDay);
/* 104 */     this.pp.add(this.miAddWeekDay);
/* 105 */     this.toolbar.add(this.jLabel1);
/* 106 */     this.toolbar.add(this.spDateFrom);
/* 107 */     this.toolbar.add(this.jLabel2);
/* 108 */     this.toolbar.add(this.spDateTo);
/* 109 */     this.toolbar.add(this.btnSearch);
/* 110 */     Calendar c = Calendar.getInstance();
/* 111 */     c.add(1, 1);
/* 112 */     this.spDateTo.setDate(c.getTime());
/*     */   }
/*     */   
/*     */   private void setMainState(boolean editting) {
/* 116 */     this.btnEdit.setEnabled((UserContext.hasFunctionRight("Ecard_leave.btnEdit")) && (!editting));
/* 117 */     this.btnCancel.setEnabled((UserContext.hasFunctionRight("Ecard_leave.btnCancel")) && (editting));
/* 118 */     this.btnDel.setEnabled((UserContext.hasFunctionRight("Ecard_leave.btnDel")) && (!editting));
/* 119 */     this.btnView.setEnabled((UserContext.hasFunctionRight("Ecard_leave.btnDel")) && (editting));
/* 120 */     this.btnSave.setEnabled((UserContext.hasFunctionRight("Ecard_leave.btnDel")) && (editting));
/* 121 */     this.ftable_leave.setEditable(editting);
/* 122 */     this.ftable_holiday.setEditable(editting);
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/* 126 */     this.btnSearch.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 130 */         EcardLeavePanel.this.flag1 = 0;
/* 131 */         EcardLeavePanel.this.flag2 = 0;
/* 132 */         EcardLeavePanel.this.fetchMainData(null);
/*     */       }
/* 134 */     });
/* 135 */     this.ftable_leave.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 139 */         if (EcardLeavePanel.this.curObj != EcardLeavePanel.this.ftable_leave.getCurrentRow()) {
/* 140 */           EcardLeavePanel.this.curObj = EcardLeavePanel.this.ftable_leave.getCurrentRow();
/*     */         }
/*     */       }
/* 143 */     });
/* 144 */     this.ftable_leave.addPickQueryExListener(new IPickQueryExListener()
/*     */     {
/*     */ 
/*     */       public void pickQuery(QueryScheme qs) {}
/*     */ 
/* 149 */     });
/* 150 */     this.ftable_leave.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 154 */         EcardLeavePanel.this.flag1 = 0;
/* 155 */         EcardLeavePanel.this.order_sql = SysUtil.getOrderString(showScheme, "e", EcardLeavePanel.this.order_sql, EcardLeavePanel.this.ftable_leave.getAll_fields());
/* 156 */         EcardLeavePanel.this.fetchMainData(null);
/*     */       }
/* 158 */     });
/* 159 */     this.ftable_holiday.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 163 */         if (EcardLeavePanel.this.curObj != EcardLeavePanel.this.ftable_holiday.getCurrentRow()) {
/* 164 */           EcardLeavePanel.this.curObj = EcardLeavePanel.this.ftable_holiday.getCurrentRow();
/*     */         }
/*     */       }
/* 167 */     });
/* 168 */     this.ftable_holiday.addPickQueryExListener(new IPickQueryExListener()
/*     */     {
/*     */ 
/*     */       public void pickQuery(QueryScheme qs) {}
/*     */ 
/* 173 */     });
/* 174 */     this.ftable_holiday.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 178 */         EcardLeavePanel.this.order_sql_h = SysUtil.getOrderString(showScheme, "e", EcardLeavePanel.this.order_sql_h, EcardLeavePanel.this.ftable_holiday.getAll_fields());
/* 179 */         EcardLeavePanel.this.flag2 = 0;
/* 180 */         EcardLeavePanel.this.fetchMainData(null);
/*     */       }
/* 182 */     });
/* 183 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 187 */         EcardLeavePanel.this.saveDay(EcardLeavePanel.this.ftable_leave.getCurrentRow());
/*     */       }
/* 189 */     });
/* 190 */     this.miAddOneDay.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 194 */         EcardLeavePanel.this.addDay(0);
/*     */       }
/* 196 */     });
/* 197 */     this.miAddManyDay.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 201 */         EcardLeavePanel.this.addDay(1);
/*     */       }
/* 203 */     });
/* 204 */     this.miAddWeekDay.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 208 */         EcardLeavePanel.this.addDay(2);
/*     */       }
/* 210 */     });
/* 211 */     this.btnAdd.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 215 */         EcardLeavePanel.this.pp.show(EcardLeavePanel.this.btnAdd, 0, 30);
/*     */       }
/* 217 */     });
/* 218 */     this.btnView.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 222 */         CommEditAction.doViewAction(EcardLeavePanel.this.curObj, EcardLeavePanel.this.ftable_leave);
/* 223 */         EcardLeavePanel.this.setMainState(false);
/*     */       }
/* 225 */     });
/* 226 */     this.btnEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 230 */         EcardLeavePanel.this.setMainState(true);
/*     */       }
/* 232 */     });
/* 233 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 237 */         CommEditAction.doCancelAction(EcardLeavePanel.this.curObj, EcardLeavePanel.this.ftable_leave);
/*     */       }
/* 239 */     });
/* 240 */     this.btnDel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 244 */         List<String> keys = null;
/* 245 */         if (EcardLeavePanel.this.jTabbedPane1.getSelectedIndex() == 0) {
/* 246 */           EcardLeavePanel.this.ftable_leave.editingStopped();
/* 247 */           keys = EcardLeavePanel.this.ftable_leave.getSelectKeys();
/*     */         } else {
/* 249 */           EcardLeavePanel.this.ftable_holiday.editingStopped();
/* 250 */           keys = EcardLeavePanel.this.ftable_holiday.getSelectKeys();
/*     */         }
/* 252 */         if (keys.isEmpty()) {
/* 253 */           return;
/*     */         }
/*     */         
/*     */ 
/* 257 */         if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
/* 258 */           return;
/*     */         }
/* 260 */         ValidateSQLResult result = CommUtil.deleteObjs("Ecard_leave", "Ecard_leave_key", keys);
/* 261 */         if (result.getResult() == 0) {
/* 262 */           MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
/* 263 */           if (EcardLeavePanel.this.jTabbedPane1.getSelectedIndex() == 0) {
/* 264 */             EcardLeavePanel.this.ftable_leave.deleteSelectedRows();
/*     */           } else {
/* 266 */             EcardLeavePanel.this.ftable_holiday.deleteSelectedRows();
/*     */           }
/*     */         } else {
/* 269 */           MsgUtil.showHRDelErrorMsg(result);
/*     */         }
/*     */       }
/* 272 */     });
/* 273 */     this.jTabbedPane1.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e)
/*     */       {
/* 277 */         EcardLeavePanel.this.fetchMainData(null);
/*     */       }
/* 279 */     });
/* 280 */     setMainState(false);
/*     */   }
/*     */   
/*     */   private void saveDay(Object obj) {
/* 284 */     if (obj == null) {
/* 285 */       return;
/*     */     }
/* 287 */     if (this.jTabbedPane1.getSelectedIndex() == 0) {
/* 288 */       this.ftable_leave.stopEditing();
/*     */     } else {
/* 290 */       this.ftable_holiday.stopEditing();
/*     */     }
/* 292 */     Ecard_leave ecard_leave = (Ecard_leave)obj;
/* 293 */     if ((ecard_leave.getEcard_leave_name() == null) || (ecard_leave.getEcard_leave_name().trim().equals(""))) {
/* 294 */       MsgUtil.showInfoMsg("请输入名称");
/* 295 */       return;
/*     */     }
/* 297 */     ValidateSQLResult result = CommUtil.updateEntity(obj);
/* 298 */     if (result.getResult() == 0) {
/* 299 */       MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
/*     */     } else {
/* 301 */       MsgUtil.showHRSaveErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   private void addDay(int type) {
/* 306 */     String flag = "leave";
/* 307 */     if (this.jTabbedPane1.getSelectedIndex() == 0) {
/* 308 */       flag = "leave";
/*     */     } else {
/* 310 */       flag = "holiday";
/*     */     }
/* 312 */     ValidateSQLResult result = null;
/* 313 */     if (type == 0) {
/* 314 */       AddLeaveOneDayDialog alodDlg = new AddLeaveOneDayDialog(ContextManager.getMainFrame(), flag);
/* 315 */       ContextManager.locateOnMainScreenCenter(alodDlg);
/* 316 */       alodDlg.setDefaultCloseOperation(2);
/* 317 */       alodDlg.setVisible(true);
/* 318 */       if (alodDlg.isClick_ok()) {
/* 319 */         result = alodDlg.getResult();
/*     */       }
/* 321 */     } else if (type == 1) {
/* 322 */       AddLeaveManyDayDialog alodDlg = new AddLeaveManyDayDialog(ContextManager.getMainFrame(), flag);
/* 323 */       ContextManager.locateOnMainScreenCenter(alodDlg);
/* 324 */       alodDlg.setDefaultCloseOperation(2);
/* 325 */       alodDlg.setVisible(true);
/* 326 */       if (alodDlg.isClick_ok()) {
/* 327 */         result = alodDlg.getResult();
/*     */       }
/* 329 */     } else if (type == 2) {
/* 330 */       AddLeaveWeekDayDialog alodDlg = new AddLeaveWeekDayDialog(ContextManager.getMainFrame(), flag);
/* 331 */       ContextManager.locateOnMainScreenCenter(alodDlg);
/* 332 */       alodDlg.setDefaultCloseOperation(2);
/* 333 */       alodDlg.setVisible(true);
/* 334 */       if (alodDlg.isClick_ok()) {
/* 335 */         result = alodDlg.getResult();
/*     */       }
/*     */     }
/* 338 */     this.flag1 = 0;
/* 339 */     this.flag2 = 0;
/* 340 */     fetchMainData(null);
/* 341 */     if (result == null) {
/* 342 */       return;
/*     */     }
/* 344 */     if (result.getResult() == 0) {
/* 345 */       fetchMainData(null);
/*     */     }
/*     */     else {
/* 348 */       MsgUtil.showInfoMsg(CommMsg.ADDFAIL_MESSAGE);
/*     */     }
/*     */   }
/*     */   
/*     */   private void fetchMainData(QueryScheme qs) {
/* 353 */     if (this.jTabbedPane1.getSelectedIndex() == 0) {
/* 354 */       if (this.flag1 <= 0) {}
/*     */ 
/*     */ 
/*     */     }
/* 358 */     else if (this.flag2 > 0) {
/* 359 */       return;
/*     */     }
/*     */     
/* 362 */     String sel_hql = "from Ecard_leave e where 1=1";
/* 363 */     sel_hql = sel_hql + " and e.ecard_leave_date >='" + this.format.format(this.spDateFrom.getDate()) + "'";
/* 364 */     Date d = DateUtil.getNextDay(this.spDateTo.getDate());
/* 365 */     sel_hql = sel_hql + " and e.ecard_leave_date <'" + this.format.format(d) + "'";
/* 366 */     if (qs != null) {
/* 367 */       sel_hql = sel_hql + " and e in (" + qs.buildHql("from Ecard_leave ed ") + ")";
/*     */     }
/* 369 */     if (this.jTabbedPane1.getSelectedIndex() == 0) {
/* 370 */       sel_hql = sel_hql + " and e.ecard_leave_flag = 'leave'";
/* 371 */       if (!"".equals(this.order_sql)) {
/* 372 */         sel_hql = sel_hql + " order by " + this.order_sql;
/*     */       }
/*     */     } else {
/* 375 */       sel_hql = sel_hql + " and e.ecard_leave_flag = 'holiday'";
/* 376 */       if (!"".equals(this.order_sql_h)) {
/* 377 */         sel_hql = sel_hql + " order by " + this.order_sql_h;
/*     */       }
/*     */     }
/* 380 */     List list = CommUtil.fetchEntities(sel_hql);
/* 381 */     Set<Ecard_leave> leaves = new HashSet();
/* 382 */     leaves.addAll(list);
/*     */     
/* 384 */     if (this.jTabbedPane1.getSelectedIndex() == 0) {
/* 385 */       this.ftable_leave.setObjects(list);
/* 386 */       qs = null;
/* 387 */       this.flag1 += 1;
/* 388 */       ContextManager.setStatusBar(this.ftable_leave.getObjects().size());
/* 389 */       this.ftable_leave.updateUI();
/*     */     } else {
/* 391 */       this.ftable_holiday.setObjects(list);
/* 392 */       qs = null;
/* 393 */       this.flag2 += 1;
/* 394 */       ContextManager.setStatusBar(this.ftable_holiday.getObjects().size());
/* 395 */       this.ftable_holiday.updateUI();
/*     */     }
/*     */   }
/*     */   
/*     */   private JButton btnDel;
/*     */   private JButton btnEdit;
/*     */   private JButton btnSave;
/*     */   private JButton btnView;
/*     */   private JTabbedPane jTabbedPane1;
/*     */   private JPanel pnlHoliday;
/*     */   private JPanel pnlRight;
/*     */   private JToolBar toolbar;
/*     */   private void initComponents() {
/* 408 */     this.toolbar = new JToolBar();
/* 409 */     this.btnAdd = new JButton();
/* 410 */     this.btnEdit = new JButton();
/* 411 */     this.btnView = new JButton();
/* 412 */     this.btnSave = new JButton();
/* 413 */     this.btnCancel = new JButton();
/* 414 */     this.btnDel = new JButton();
/* 415 */     this.jTabbedPane1 = new JTabbedPane();
/* 416 */     this.pnlRight = new JPanel();
/* 417 */     this.pnlHoliday = new JPanel();
/*     */     
/* 419 */     this.toolbar.setFloatable(false);
/* 420 */     this.toolbar.setRollover(true);
/*     */     
/* 422 */     this.btnAdd.setText("新增");
/* 423 */     this.btnAdd.setFocusable(false);
/* 424 */     this.btnAdd.setHorizontalTextPosition(0);
/* 425 */     this.btnAdd.setVerticalTextPosition(3);
/* 426 */     this.toolbar.add(this.btnAdd);
/*     */     
/* 428 */     this.btnEdit.setText("编辑");
/* 429 */     this.btnEdit.setFocusable(false);
/* 430 */     this.btnEdit.setHorizontalTextPosition(0);
/* 431 */     this.btnEdit.setVerticalTextPosition(3);
/* 432 */     this.toolbar.add(this.btnEdit);
/*     */     
/* 434 */     this.btnView.setText("浏览");
/* 435 */     this.btnView.setFocusable(false);
/* 436 */     this.btnView.setHorizontalTextPosition(0);
/* 437 */     this.btnView.setVerticalTextPosition(3);
/* 438 */     this.toolbar.add(this.btnView);
/*     */     
/* 440 */     this.btnSave.setText("保存");
/* 441 */     this.btnSave.setFocusable(false);
/* 442 */     this.btnSave.setHorizontalTextPosition(0);
/* 443 */     this.btnSave.setVerticalTextPosition(3);
/* 444 */     this.toolbar.add(this.btnSave);
/*     */     
/* 446 */     this.btnCancel.setText("取消");
/* 447 */     this.btnCancel.setFocusable(false);
/* 448 */     this.btnCancel.setHorizontalTextPosition(0);
/* 449 */     this.btnCancel.setVerticalTextPosition(3);
/* 450 */     this.toolbar.add(this.btnCancel);
/*     */     
/* 452 */     this.btnDel.setText("删除");
/* 453 */     this.btnDel.setFocusable(false);
/* 454 */     this.btnDel.setHorizontalTextPosition(0);
/* 455 */     this.btnDel.setVerticalTextPosition(3);
/* 456 */     this.toolbar.add(this.btnDel);
/*     */     
/* 458 */     this.pnlRight.setLayout(new BorderLayout());
/* 459 */     this.jTabbedPane1.addTab("不汇款日", this.pnlRight);
/*     */     
/* 461 */     this.pnlHoliday.setLayout(new BorderLayout());
/* 462 */     this.jTabbedPane1.addTab("刷卡假日", this.pnlHoliday);
/*     */     
/* 464 */     GroupLayout layout = new GroupLayout(this);
/* 465 */     setLayout(layout);
/* 466 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolbar, -1, -1, 32767).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 471 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.toolbar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTabbedPane1)));
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardLeavePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */