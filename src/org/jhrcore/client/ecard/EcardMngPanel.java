/*     */ package org.jhrcore.client.ecard;
/*     */ 
/*     */ import com.foundercy.pf.control.listener.IPickColumnSumListener;
/*     */ import com.foundercy.pf.control.listener.IPickFieldOrderListener;
/*     */ import com.foundercy.pf.control.listener.IPickFieldSetListener;
/*     */ import com.foundercy.pf.control.listener.IPickQueryExListener;
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.JTree;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.event.TreeSelectionListener;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.client.UserContext;
/*     */ import org.jhrcore.client.ecard.ui.AddEcardPanel;
/*     */ import org.jhrcore.client.ecard.ui.CardPanel;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.entity.query.QueryScheme;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.entity.showstyle.ShowScheme;
/*     */ import org.jhrcore.msg.CommMsg;
/*     */ import org.jhrcore.mutil.ReportUtil;
/*     */ import org.jhrcore.rebuild.EntityBuilder;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ContextManager;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.ui.listener.CommEditAction;
/*     */ import org.jhrcore.ui.listener.IPickWindowCloseListener;
/*     */ import org.jhrcore.ui.task.IModulePanel;
/*     */ import org.jhrcore.util.ComponentUtil;
/*     */ import org.jhrcore.util.DbUtil;
/*     */ import org.jhrcore.util.ImageUtil;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.PublicUtil;
/*     */ import org.jhrcore.util.SysUtil;
/*     */ 
/*     */ public class EcardMngPanel extends JPanel implements IModulePanel
/*     */ {
/*  69 */   private JButton btnAdd = new JButton("新增");
/*  70 */   private JButton btnEdit = new JButton("编辑");
/*  71 */   private JButton btnView = new JButton("浏览");
/*  72 */   private JButton btnSave = new JButton("保存");
/*  73 */   private JButton btnCancel = new JButton("取消");
/*  74 */   private JButton btnDel = new JButton("删除");
/*  75 */   private JButton btnReport = new JButton("常用报表");
/*  76 */   private JButton btnTool = new JButton("工具");
/*  77 */   private JLabel jLabel7 = new JLabel(" 查找：");
/*  78 */   private JTextField comBoxSearch = new JTextField();
/*  79 */   private JCheckBox chbCurColumn = new JCheckBox("当前列", false);
/*  80 */   private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
/*  81 */   private JMenu mEditWay = new JMenu("编辑方式");
/*  82 */   private JMenuItem miCardEdit = new JMenuItem("卡片编辑");
/*  83 */   private JMenuItem miTableEdit = new JMenuItem("网格编辑");
/*  84 */   private JMenuItem miExport = new JMenuItem("导出Excel");
/*  85 */   private List pay_system_list = new ArrayList();
/*  86 */   private JPopupMenu menu = new JPopupMenu();
/*     */   private FTable ftable;
/*  88 */   private List<TempFieldInfo> all_infos = new ArrayList();
/*  89 */   private List<TempFieldInfo> default_infos = new ArrayList();
/*  90 */   private List<TempFieldInfo> default_orders = new ArrayList();
/*  91 */   private String order_sql = "ecard_code";
/*  92 */   private Object curEcard = null;
/*  93 */   private BeanPanel beanPanel = new BeanPanel();
/*  94 */   private boolean editable = false;
/*  95 */   private boolean editStyle = true;
/*     */   public static final String module_code = "EcardMng";
/*     */   private String sum_sql;
/*  98 */   private CardPanel cardPanel = null;
/*     */   private Object curTreeNode;
/*     */   private JPanel jPanel2;
/*     */   private JSplitPane jSplitPane1;
/*     */   private JSplitPane jspRight;
/*     */   
/*     */   public EcardMngPanel() {
/* 105 */     initComponents();
/* 106 */     initOthers();
/* 107 */     setupEvents();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initComponents()
/*     */   {
/* 119 */     this.jSplitPane1 = new JSplitPane();
/* 120 */     this.pnlLeft = new JPanel();
/* 121 */     this.jPanel2 = new JPanel();
/* 122 */     this.toolBar = new JToolBar();
/* 123 */     this.jtpMain = new JTabbedPane();
/* 124 */     this.jspRight = new JSplitPane();
/* 125 */     this.pnlTable = new JPanel();
/* 126 */     this.pnlCard = new JPanel();
/*     */     
/* 128 */     this.jSplitPane1.setDividerLocation(200);
/* 129 */     this.jSplitPane1.setOneTouchExpandable(true);
/*     */     
/* 131 */     this.pnlLeft.setLayout(new BorderLayout());
/* 132 */     this.jSplitPane1.setLeftComponent(this.pnlLeft);
/*     */     
/* 134 */     this.toolBar.setFloatable(false);
/* 135 */     this.toolBar.setRollover(true);
/* 136 */     this.toolBar.setMaximumSize(new Dimension(1240, 24));
/* 137 */     this.toolBar.setMinimumSize(new Dimension(106, 24));
/* 138 */     this.toolBar.setPreferredSize(new Dimension(106, 24));
/*     */     
/* 140 */     this.jspRight.setDividerLocation(250);
/* 141 */     this.jspRight.setOrientation(0);
/* 142 */     this.jspRight.setOneTouchExpandable(true);
/*     */     
/* 144 */     this.pnlTable.setLayout(new BorderLayout());
/* 145 */     this.jspRight.setTopComponent(this.pnlTable);
/*     */     
/* 147 */     this.pnlCard.setLayout(new BorderLayout());
/* 148 */     this.jspRight.setRightComponent(this.pnlCard);
/*     */     
/* 150 */     this.jtpMain.addTab("基本信息", this.jspRight);
/*     */     
/* 152 */     GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
/* 153 */     this.jPanel2.setLayout(jPanel2Layout);
/* 154 */     jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, 474, 32767).addComponent(this.jtpMain));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.toolBar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jtpMain, -1, 400, 32767)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */     this.jSplitPane1.setRightComponent(this.jPanel2);
/*     */     
/* 169 */     GroupLayout layout = new GroupLayout(this);
/* 170 */     setLayout(layout);
/* 171 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1));
/*     */     
/*     */ 
/*     */ 
/* 175 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private JTabbedPane jtpMain;
/*     */   
/*     */   private JPanel pnlCard;
/*     */   
/*     */   private JPanel pnlLeft;
/*     */   
/*     */   private JPanel pnlTable;
/*     */   
/*     */   private JToolBar toolBar;
/*     */   
/*     */   private void initOthers()
/*     */   {
/* 192 */     this.cardPanel = new CardPanel("");
/* 193 */     this.pnlLeft.add(this.cardPanel, "Center");
/* 194 */     initToolBar();
/* 195 */     final Calendar c = Calendar.getInstance();
/* 196 */     c.add(2, 2);
/* 197 */     this.ftable = new FTable(Ecard.class, true, true, true, "EcardMngPanel")
/*     */     {
/*     */       public Color getCellForegroud(String fileName, Object cellValue, Object row_obj)
/*     */       {
/* 201 */         if ((row_obj != null) && ((row_obj instanceof Ecard))) {
/* 202 */           Ecard e = (Ecard)row_obj;
/* 203 */           if ((e.getEcard_ddate() != null) && (!e.getEcard_ddate().after(c.getTime()))) {
/* 204 */             return Color.RED;
/*     */           }
/*     */         }
/* 207 */         return null;
/*     */       }
/* 209 */     };
/* 210 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard.class, EntityBuilder.COMM_FIELD_VISIBLE);
/* 211 */     for (TempFieldInfo tfi : shift_infos) {
/* 212 */       this.default_infos.add(tfi);
/* 213 */       this.all_infos.add(tfi);
/*     */     }
/* 215 */     this.ftable.setAll_fields(this.all_infos, this.default_infos, this.default_orders, "EcardMngPanel");
/* 216 */     this.ftable.lockColumns(0);
/* 217 */     this.ftable.lockColumns(1);
/* 218 */     this.order_sql = SysUtil.getOrderString(this.ftable.getCurOrderScheme(), "Ecard", this.order_sql, this.all_infos);
/* 219 */     this.ftable.setRight_allow_flag(true);
/* 220 */     this.pnlTable.add(this.ftable);
/* 221 */     this.pnlCard.add(new JScrollPane(this.beanPanel));
/* 222 */     this.beanPanel.setColumns(3);
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/* 226 */     this.cardPanel.getCardTree().addTreeSelectionListener(new TreeSelectionListener()
/*     */     {
/*     */       public void valueChanged(TreeSelectionEvent e)
/*     */       {
/* 230 */         EcardMngPanel.this.curTreeNode = e.getPath().getLastPathComponent();
/* 231 */         if (EcardMngPanel.this.curTreeNode != null) {
/* 232 */           EcardMngPanel.this.fetchMainData(null, null);
/*     */         }
/*     */       }
/* 235 */     });
/* 236 */     this.ftable.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 240 */         CommEditAction.doRowSaveAction(EcardMngPanel.this.curEcard, EcardMngPanel.this.editable);
/* 241 */         if (EcardMngPanel.this.curEcard == EcardMngPanel.this.ftable.getCurrentRow()) {
/* 242 */           return;
/*     */         }
/* 244 */         EcardMngPanel.this.curEcard = EcardMngPanel.this.ftable.getCurrentRow();
/* 245 */         BeanPanel.refreshUIForTable(EcardMngPanel.this.ftable, EcardMngPanel.this.beanPanel, (EcardMngPanel.this.editable) && (EcardMngPanel.this.editStyle));
/* 246 */         ContextManager.setStatusBar(EcardMngPanel.this.ftable.getObjects().size());
/*     */       }
/* 248 */     });
/* 249 */     this.ftable.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 253 */         EcardMngPanel.this.order_sql = SysUtil.getOrderString(showScheme, "Ecard", EcardMngPanel.this.order_sql, EcardMngPanel.this.all_infos);
/* 254 */         EcardMngPanel.this.fetchMainData(EcardMngPanel.this.ftable.getCur_query_scheme(), null);
/*     */       }
/* 256 */     });
/* 257 */     this.ftable.addPickQueryExListener(new IPickQueryExListener()
/*     */     {
/*     */       public void pickQuery(QueryScheme qs)
/*     */       {
/* 261 */         EcardMngPanel.this.fetchMainData(qs, null);
/*     */       }
/* 263 */     });
/* 264 */     this.ftable.addPickFieldSetListener(new IPickFieldSetListener()
/*     */     {
/*     */       public void pickField(ShowScheme showScheme)
/*     */       {
/* 268 */         BeanPanel.refreshUIForTable(EcardMngPanel.this.ftable, EcardMngPanel.this.beanPanel, (EcardMngPanel.this.editable) && (EcardMngPanel.this.editStyle));
/*     */       }
/* 270 */     });
/* 271 */     this.ftable.addPickColumnSumListener(new IPickColumnSumListener()
/*     */     {
/*     */       public String pickSumSQL()
/*     */       {
/* 275 */         return EcardMngPanel.this.sum_sql + "@sql";
/*     */       }
/*     */       
/* 278 */     });
/* 279 */     this.btnEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 283 */         EcardMngPanel.this.setEditState(true, EcardMngPanel.this.editStyle);
/*     */       }
/* 285 */     });
/* 286 */     this.btnView.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 290 */         if (EcardMngPanel.this.curEcard != null) {
/* 291 */           Ecard ecard = (Ecard)EcardMngPanel.this.curEcard;
/* 292 */           if ((ecard.getEcard_manager() == null) || (ecard.getEcard_manager().equals(""))) {
/* 293 */             ecard.setEcard_manager("系统管理员");
/*     */           }
/* 295 */           CommEditAction.doViewAction(EcardMngPanel.this.curEcard, EcardMngPanel.this.ftable, EcardMngPanel.this.beanPanel);
/*     */         }
/* 297 */         EcardMngPanel.this.setEditState(false, EcardMngPanel.this.editStyle);
/*     */       }
/* 299 */     });
/* 300 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 304 */         CommEditAction.doCancelAction(EcardMngPanel.this.curEcard, EcardMngPanel.this.ftable, EcardMngPanel.this.beanPanel);
/* 305 */         EcardMngPanel.this.setEditState(EcardMngPanel.this.editable, EcardMngPanel.this.editStyle);
/*     */       }
/* 307 */     });
/* 308 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 312 */         if (EcardMngPanel.this.curEcard != null) {
/* 313 */           Ecard ecard = (Ecard)EcardMngPanel.this.curEcard;
/* 314 */           if ((ecard.getEcard_manager() == null) || (ecard.getEcard_manager().equals(""))) {
/* 315 */             ecard.setEcard_manager("系统管理员");
/*     */           }
/* 317 */           CommEditAction.doSaveAction(EcardMngPanel.this.curEcard, EcardMngPanel.this.ftable, EcardMngPanel.this.beanPanel);
/*     */         }
/* 319 */         EcardMngPanel.this.setEditState(EcardMngPanel.this.editable, EcardMngPanel.this.editStyle);
/*     */       }
/* 321 */     });
/* 322 */     this.btnDel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 326 */         EcardMngPanel.this.delObj();
/*     */       }
/* 328 */     });
/* 329 */     ActionListener search_listener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 333 */         EcardMngPanel.this.doQuickSearch(EcardMngPanel.this.comBoxSearch.getText());
/*     */       }
/* 335 */     };
/* 336 */     this.btnSearch.addActionListener(search_listener);
/* 337 */     this.comBoxSearch.addActionListener(search_listener);
/*     */     
/* 339 */     this.btnReport.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 343 */         ReportUtil.buildCommReportMenu(EcardMngPanel.this.btnReport, "EcardMng", null);
/*     */       }
/* 345 */     });
/* 346 */     this.miExport.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 350 */         EcardMngPanel.this.ftable.exportData();
/*     */       }
/* 352 */     });
/* 353 */     ComponentUtil.refreshJSplitPane(this.jspRight, "EcardMngPanel.jspRight");
/* 354 */     fetchMainData(null, null);
/*     */   }
/*     */   
/*     */   private void initToolBar()
/*     */   {
/* 359 */     this.toolBar.add(this.btnAdd);
/* 360 */     this.toolBar.add(this.btnEdit);
/* 361 */     this.toolBar.add(this.btnView);
/* 362 */     this.toolBar.add(this.btnSave);
/* 363 */     this.toolBar.add(this.btnCancel);
/* 364 */     this.toolBar.add(this.btnDel);
/* 365 */     this.toolBar.addSeparator();
/* 366 */     this.toolBar.add(this.btnTool);
/* 367 */     this.toolBar.add(this.jLabel7);
/* 368 */     this.toolBar.add(this.comBoxSearch);
/* 369 */     this.toolBar.add(this.btnSearch);
/* 370 */     this.toolBar.add(this.chbCurColumn);
/*     */     
/* 372 */     this.menu.add(this.mEditWay);
/* 373 */     this.menu.addSeparator();
/* 374 */     this.menu.add(this.miExport);
/* 375 */     this.mEditWay.add(this.miTableEdit);
/* 376 */     this.mEditWay.add(this.miCardEdit);
/* 377 */     ComponentUtil.setSize(this.comBoxSearch, 120, 22);
/* 378 */     ComponentUtil.setSize(this.btnSearch, 22, 22);
/* 379 */     this.btnTool.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 383 */         EcardMngPanel.this.menu.show(EcardMngPanel.this.btnTool, 0, 25);
/*     */       }
/* 385 */     });
/* 386 */     this.btnAdd.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 390 */         AddEcardPanel pnl = new AddEcardPanel();
/* 391 */         ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "登记信用卡", 700, 580, false);
/* 392 */         mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */         {
/*     */           public void pickClose()
/*     */           {
/* 396 */             EcardMngPanel.this.cardPanel.refreshTree();
/* 397 */             EcardMngPanel.this.fetchMainData(null, null);
/*     */           }
/* 399 */         });
/* 400 */         mf.setVisible(true);
/*     */       }
/*     */       
/* 403 */     });
/* 404 */     this.miTableEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 408 */         EcardMngPanel.this.setEditState(EcardMngPanel.this.editable, false);
/*     */       }
/* 410 */     });
/* 411 */     this.miCardEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 415 */         EcardMngPanel.this.setEditState(EcardMngPanel.this.editable, true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void fetchMainData(QueryScheme qs, String s_where)
/*     */   {
/* 422 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/* 423 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/* 424 */       String where_sql = "";
/* 425 */       if ((t_obj instanceof String)) {
/* 426 */         if (!"所有卡".equals(t_obj.toString()))
/*     */         {
/* 428 */           if ("已激活".equals(t_obj.toString())) {
/* 429 */             where_sql = where_sql + " and ecard_state = '已激活'";
/* 430 */           } else if ("已停止".equals(t_obj.toString())) {
/* 431 */             where_sql = where_sql + " and ecard_state = '已停止'";
/* 432 */           } else if ("普养".equals(t_obj.toString())) {
/* 433 */             where_sql = where_sql + " and ecard_type = '普养'";
/* 434 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 435 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 436 */               where_sql = where_sql + " and ecard_state = '已激活'";
/*     */             } else {
/* 438 */               where_sql = where_sql + " and ecard_state = '已停止'";
/*     */             }
/* 440 */           } else if ("中养".equals(t_obj.toString())) {
/* 441 */             where_sql = where_sql + " and ecard_type = '中养'";
/* 442 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 443 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 444 */               where_sql = where_sql + " and ecard_state = '已激活'";
/*     */             } else {
/* 446 */               where_sql = where_sql + " and ecard_state = '已停止'";
/*     */             }
/* 448 */           } else if ("精养".equals(t_obj.toString())) {
/* 449 */             where_sql = where_sql + " and ecard_type = '精养'";
/* 450 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 451 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 452 */               where_sql = where_sql + " and ecard_state = '已激活'";
/*     */             } else {
/* 454 */               where_sql = where_sql + " and ecard_state = '已停止'";
/*     */             }
/*     */           } else {
/* 457 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 458 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 459 */               where_sql = " and ecard_state = '已激活' and ecard_manager = '" + t_obj.toString() + "'";
/*     */             } else
/* 461 */               where_sql = " and ecard_state = '已停止' and ecard_manager = '" + t_obj.toString() + "'";
/*     */           }
/*     */         }
/* 464 */       } else if ((t_obj instanceof Ecard)) {
/* 465 */         Ecard ecard = (Ecard)t_obj;
/* 466 */         where_sql = where_sql + " and ecard_key = '" + ecard.getEcard_key() + "'";
/*     */       }
/* 468 */       this.ftable.setCur_query_scheme(qs);
/* 469 */       String sql = " from Ecard where 1=1";
/* 470 */       if (qs != null) {
/* 471 */         sql = sql + " and Ecard.ecard_key in(" + qs.buildSql() + ")";
/*     */       }
/* 473 */       if ((s_where != null) && (!s_where.trim().equals(""))) {
/* 474 */         sql = sql + " and (" + s_where + ")";
/*     */       }
/* 476 */       sql = sql + where_sql;
/* 477 */       this.sum_sql = sql;
/* 478 */       sql = "select ecard_key" + sql + " order by " + this.order_sql;
/* 479 */       PublicUtil.getProps_value().setProperty(Ecard.class.getName(), "from Ecard n where n.ecard_key in");
/* 480 */       List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
/* 481 */       this.ftable.setObjects(list);
/* 482 */       refreshStatus();
/*     */     }
/*     */   }
/*     */   
/*     */   private void doQuickSearch(String text) {
/* 487 */     if ((text == null) || (text.trim().equals(""))) {
/* 488 */       return;
/*     */     }
/* 490 */     text = SysUtil.getQuickSearchText(text);
/* 491 */     String s_where = "";
/* 492 */     if (this.chbCurColumn.isSelected()) {
/* 493 */       s_where = this.ftable.getQuickSearchSQL("Ecard", text);
/*     */     } else {
/* 495 */       s_where = "(upper(ecard_code) like '" + text + "%' or upper(ecard_name) like '" + text + "%')";
/*     */     }
/* 497 */     fetchMainData(this.ftable.getCur_query_scheme(), s_where);
/*     */   }
/*     */   
/*     */   private void setEditState(boolean editable, boolean editStyle) {
/* 501 */     this.editStyle = editStyle;
/* 502 */     this.editable = editable;
/* 503 */     ComponentUtil.setIcon(this.miTableEdit, editStyle ? "blank" : "editWay");
/* 504 */     ComponentUtil.setIcon(this.miCardEdit, editStyle ? "editWay" : "blank");
/* 505 */     this.btnEdit.setEnabled((UserContext.hasFunctionRight("EcardMng.btnEdit")) && (!editable));
/* 506 */     this.btnDel.setEnabled((UserContext.hasFunctionRight("EcardMng.btnEdit")) && (!editable));
/* 507 */     this.btnCancel.setEnabled((UserContext.hasFunctionRight("EcardMng.btnEdit")) && (editable));
/* 508 */     this.btnSave.setEnabled((UserContext.hasFunctionRight("EcardMng.btnEdit")) && (editable));
/* 509 */     this.btnView.setEnabled((UserContext.hasFunctionRight("EcardMng.btnEdit")) && (editable));
/* 510 */     this.ftable.editingStopped();
/* 511 */     this.ftable.setEditable((editable) && (!editStyle));
/* 512 */     BeanPanel.refreshUIForTable(this.ftable, this.beanPanel, (editable) && (editStyle));
/*     */   }
/*     */   
/*     */   private void delObj() {
/* 516 */     List<String> keys = this.ftable.getSelectKeys();
/* 517 */     if (keys.isEmpty()) {
/* 518 */       return;
/*     */     }
/* 520 */     if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
/* 521 */       return;
/*     */     }
/* 523 */     ValidateSQLResult result = CommUtil.deleteObjs("Ecard", "ecard_key", keys);
/* 524 */     if (result.getResult() == 0) {
/* 525 */       MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
/* 526 */       this.ftable.deleteSelectedRows();
/* 527 */       this.cardPanel.refreshTree();
/*     */     } else {
/* 529 */       MsgUtil.showHRDelErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFunctionRight()
/*     */   {
/* 535 */     ComponentUtil.setSysFuntion(this, "EcardMng");
/* 536 */     this.ftable.setExportItemEnable(this.miExport.isEnabled());
/* 537 */     setEditState(false, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void pickClose() {}
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 546 */     refreshStatus();
/*     */   }
/*     */   
/*     */   private void refreshStatus() {
/* 550 */     ContextManager.setStatusBar(this.ftable.getObjects().size());
/*     */   }
/*     */   
/*     */   public String getModuleCode()
/*     */   {
/* 555 */     return "EcardMng";
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardMngPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */