/*     */ package org.jhrcore.client.ecard;
/*     */ 
/*     */ import com.foundercy.pf.control.listener.IPickColumnSumListener;
/*     */ import com.foundercy.pf.control.listener.IPickFieldOrderListener;
/*     */ import com.foundercy.pf.control.listener.IPickQueryExListener;
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.client.ecard.ui.CalcEcardRuPanel;
/*     */ import org.jhrcore.client.ecard.ui.CardPanel;
/*     */ import org.jhrcore.client.ecard.ui.EditChuPanel;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.entity.ecard.Ecard_chu;
/*     */ import org.jhrcore.entity.query.QueryScheme;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.entity.showstyle.ShowScheme;
/*     */ import org.jhrcore.rebuild.EntityBuilder;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ContextManager;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.ui.listener.CommEditAction;
/*     */ import org.jhrcore.ui.listener.IPickWindowCloseListener;
/*     */ import org.jhrcore.util.DateUtil;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ import org.jhrcore.util.PublicUtil;
/*     */ import org.jhrcore.util.SysUtil;
/*     */ 
/*     */ public class EcardXfPanel extends JPanel
/*     */ {
/*     */   private CardPanel cardPanel;
/*     */   private FTable ftable_xf;
/*  60 */   private List<TempFieldInfo> all_infos_xf = new ArrayList();
/*  61 */   private List<TempFieldInfo> default_infos_xf = new ArrayList();
/*  62 */   private List<TempFieldInfo> default_orders_xf = new ArrayList();
/*  63 */   private String order_sql_xf = "e.ecard_code,e.chu_date";
/*     */   private QueryScheme qs;
/*     */   private String sum_sql_xf;
/*     */   private Object curTreeNode;
/*     */   private Object curEcard_chu;
/*  68 */   private BeanPanel beanPanel_xf = new BeanPanel();
/*  69 */   private BeanPanel ebeanPanel = new BeanPanel();
/*  70 */   private boolean editable = false;
/*  71 */   private boolean editStyle = false;
/*     */   public static final String module_code = "EcardXfPanel";
/*     */   private JButton btnAdd;
/*     */   
/*  75 */   public EcardXfPanel() { initComponents();
/*  76 */     initOthers();
/*  77 */     setupEvents(); }
/*     */   
/*     */   private JButton btnEdit;
/*     */   
/*  81 */   private void initOthers() { this.tf_ym_from.setText(DateUtil.DateToStr(new Date(), "yyyyMM"));
/*  82 */     this.tf_ym_to.setText(DateUtil.DateToStr(new Date(), "yyyyMM"));
/*  83 */     this.cardPanel = new CardPanel("build");
/*  84 */     this.cardPanel.addBuildDataSetListener(new org.jhrcore.client.ecard.listener.IBuildDataSetListener()
/*     */     {
/*     */       public void buildData(String str, Ecard e, int index)
/*     */       {
/*  88 */         EcardXfPanel.this.buildDataChu(str, e);
/*     */       }
/*     */       
/*     */       public void deleteData(Ecard e)
/*     */       {
/*  93 */         EcardXfPanel.this.deleteDataChu(e);
/*     */       }
/*  95 */     });
/*  96 */     this.pnlCard.add(this.cardPanel, "Center");
/*  97 */     this.ftable_xf = new FTable(Ecard_chu.class, true, true, true, "EcardXfPanel");
/*  98 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_chu.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*  99 */     for (TempFieldInfo tfi : shift_infos) {
/* 100 */       this.default_infos_xf.add(tfi);
/* 101 */       this.all_infos_xf.add(tfi);
/*     */     }
/* 103 */     this.ftable_xf.setAll_fields(this.all_infos_xf, this.default_infos_xf, this.default_orders_xf, "EcardXfPanel");
/* 104 */     this.order_sql_xf = SysUtil.getOrderString(this.ftable_xf.getCurOrderScheme(), "e", this.order_sql_xf, this.all_infos_xf);
/* 105 */     this.ftable_xf.setRight_allow_flag(true);
/* 106 */     this.pnlTable.add(this.ftable_xf);
/*     */     
/* 108 */     this.pnlBeanChu.add(new JScrollPane(this.beanPanel_xf));
/* 109 */     this.pnlBeanCard.add(new JScrollPane(this.ebeanPanel)); }
/*     */   
/*     */   private JButton btnSearch;
/*     */   
/* 113 */   private void setupEvents() { this.cardPanel.getCardTree().addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
/*     */     {
/*     */       public void valueChanged(TreeSelectionEvent e)
/*     */       {
/* 117 */         EcardXfPanel.this.curTreeNode = e.getPath().getLastPathComponent();
/* 118 */         if (EcardXfPanel.this.curTreeNode != null) {
/* 119 */           if ((((DefaultMutableTreeNode)EcardXfPanel.this.curTreeNode).getUserObject() instanceof Ecard)) {
/* 120 */             Ecard temp_card = (Ecard)((DefaultMutableTreeNode)EcardXfPanel.this.curTreeNode).getUserObject();
/* 121 */             EcardXfPanel.this.ebeanPanel.setBean(temp_card);
/* 122 */             EcardXfPanel.this.ebeanPanel.bind();
/*     */           }
/* 124 */           EcardXfPanel.this.fetchData(null);
/*     */         }
/*     */       }
/* 127 */     });
/* 128 */     this.ftable_xf.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 132 */         CommEditAction.doRowSaveAction(EcardXfPanel.this.curEcard_chu, EcardXfPanel.this.editable);
/* 133 */         if (EcardXfPanel.this.curEcard_chu == EcardXfPanel.this.ftable_xf.getCurrentRow()) {
/* 134 */           return;
/*     */         }
/* 136 */         EcardXfPanel.this.curEcard_chu = EcardXfPanel.this.ftable_xf.getCurrentRow();
/* 137 */         BeanPanel.refreshUIForTable(EcardXfPanel.this.ftable_xf, EcardXfPanel.this.beanPanel_xf, (EcardXfPanel.this.editable) && (EcardXfPanel.this.editStyle));
/* 138 */         ContextManager.setStatusBar(EcardXfPanel.this.ftable_xf.getObjects().size());
/*     */       }
/* 140 */     });
/* 141 */     this.ftable_xf.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 145 */         EcardXfPanel.this.order_sql_xf = SysUtil.getOrderString(showScheme, "e", EcardXfPanel.this.order_sql_xf, EcardXfPanel.this.all_infos_xf);
/* 146 */         EcardXfPanel.this.fetchData(EcardXfPanel.this.ftable_xf.getCur_query_scheme());
/*     */       }
/* 148 */     });
/* 149 */     this.ftable_xf.addPickQueryExListener(new IPickQueryExListener()
/*     */     {
/*     */       public void pickQuery(QueryScheme qs)
/*     */       {
/* 153 */         EcardXfPanel.this.fetchData(qs);
/*     */       }
/* 155 */     });
/* 156 */     this.ftable_xf.addPickFieldSetListener(new com.foundercy.pf.control.listener.IPickFieldSetListener()
/*     */     {
/*     */       public void pickField(ShowScheme showScheme)
/*     */       {
/* 160 */         BeanPanel.refreshUIForTable(EcardXfPanel.this.ftable_xf, EcardXfPanel.this.beanPanel_xf, (EcardXfPanel.this.editable) && (EcardXfPanel.this.editStyle));
/*     */       }
/* 162 */     });
/* 163 */     this.ftable_xf.addPickColumnSumListener(new IPickColumnSumListener()
/*     */     {
/*     */       public String pickSumSQL()
/*     */       {
/* 167 */         return EcardXfPanel.this.sum_sql_xf + "@sql";
/*     */       }
/* 169 */     });
/* 170 */     this.ftable_xf.addMouseListener(new java.awt.event.MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e)
/*     */       {
/* 174 */         if (e.getClickCount() >= 2) {
/* 175 */           if (EcardXfPanel.this.curEcard_chu == null) {
/* 176 */             return;
/*     */           }
/* 178 */           EditChuPanel pnl = new EditChuPanel(EcardXfPanel.this.curEcard_chu);
/* 179 */           ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
/* 180 */           mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */           {
/*     */             public void pickClose()
/*     */             {
/* 184 */               EcardXfPanel.this.fetchData(null);
/*     */             }
/* 186 */           });
/* 187 */           mf.setVisible(true);
/*     */         }
/*     */       }
/* 190 */     });
/* 191 */     this.btnAdd.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 195 */         CalcEcardRuPanel pnl = new CalcEcardRuPanel("xf");
/* 196 */         ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "生成消费数据", 380, 220, false);
/* 197 */         mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */         {
/*     */           public void pickClose()
/*     */           {
/* 201 */             EcardXfPanel.this.fetchData(null);
/*     */           }
/* 203 */         });
/* 204 */         mf.setVisible(true);
/*     */       }
/* 206 */     });
/* 207 */     this.btnEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 211 */         if (EcardXfPanel.this.curEcard_chu == null) {
/* 212 */           return;
/*     */         }
/* 214 */         EditChuPanel pnl = new EditChuPanel(EcardXfPanel.this.curEcard_chu);
/* 215 */         ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
/* 216 */         mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */         {
/*     */           public void pickClose()
/*     */           {
/* 220 */             EcardXfPanel.this.fetchData(null);
/*     */           }
/* 222 */         });
/* 223 */         mf.setVisible(true);
/*     */       }
/* 225 */     });
/* 226 */     this.btnSearch.addActionListener(new ActionListener()
/*     */     {
/*     */ 
/*     */       public void actionPerformed(ActionEvent e) {
/* 230 */         EcardXfPanel.this.fetchData(null); }
/*     */     }); }
/*     */   
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   
/* 236 */   private void fetchData(QueryScheme qs) { if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/* 237 */       String ym_from = this.tf_ym_from.getText();
/* 238 */       String ym_to = this.tf_ym_to.getText();
/* 239 */       if ((!SysUtil.check_month(ym_to)) || (!SysUtil.check_month(ym_from))) {
/* 240 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/* 241 */         return;
/*     */       }
/* 243 */       String where_sql = "";
/* 244 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/* 245 */       if ((t_obj instanceof String)) {
/* 246 */         if (!"所有卡".equals(t_obj.toString()))
/*     */         {
/* 248 */           if ("已激活".equals(t_obj.toString())) {
/* 249 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/* 250 */           } else if ("已停止".equals(t_obj.toString())) {
/* 251 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/* 252 */           } else if ("普养".equals(t_obj.toString())) {
/* 253 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 254 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 255 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*     */             } else {
/* 257 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*     */             }
/* 259 */           } else if ("中养".equals(t_obj.toString())) {
/* 260 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 261 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 262 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*     */             } else {
/* 264 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*     */             }
/* 266 */           } else if ("精养".equals(t_obj.toString())) {
/* 267 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 268 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 269 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*     */             } else
/* 271 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*     */           }
/*     */         }
/* 274 */       } else if ((t_obj instanceof Ecard)) {
/* 275 */         Ecard e = (Ecard)t_obj;
/* 276 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/*     */       }
/*     */       
/* 279 */       String sql = " from Ecard_chu e where 1=1";
/* 280 */       sql = sql + " and chu_ym >='" + ym_from + "' and chu_ym<='" + ym_to + "'";
/* 281 */       if (qs != null) {
/* 282 */         sql = sql + " and e.ecard_chu_key in(" + qs.buildSql() + ")";
/*     */       }
/* 284 */       sql = sql + where_sql;
/* 285 */       this.sum_sql_xf = sql;
/* 286 */       sql = "select ecard_chu_key" + sql + " order by " + this.order_sql_xf;
/* 287 */       PublicUtil.getProps_value().setProperty(Ecard_chu.class.getName(), "from Ecard_chu e where e.ecard_chu_key in");
/* 288 */       List list = CommUtil.selectSQL(org.jhrcore.util.DbUtil.tranSQL(sql));
/* 289 */       this.ftable_xf.setObjects(list);
/* 290 */       refreshStatus(); } }
/*     */   
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel3;
/*     */   private JSplitPane jSplitPane1;
/* 295 */   private void refreshStatus() { ContextManager.setStatusBar(this.ftable_xf.getObjects().size()); }
/*     */   
/*     */ 
/*     */   private void deleteDataChu(Ecard e) {
/* 299 */     if (JOptionPane.showConfirmDialog(null, "确定删除信用卡（" + e.toString() + "）" + this.tf_ym_from.getText() + "的消费数据？", "询问", 2, 3) != 0)
/*     */     {
/* 301 */       return;
/*     */     }
/* 303 */     String sql = "delete from Ecard_chu where ecard_key='" + e.getEcard_key() + "' and chu_ym='" + this.tf_ym_from.getText() + "'";
/* 304 */     ValidateSQLResult result = CommUtil.excuteSQL(sql);
/* 305 */     if (result.getResult() == 0) {
/* 306 */       JOptionPane.showMessageDialog(null, "删除数据成功");
/* 307 */       fetchData(null);
/*     */     } else {
/* 309 */       MsgUtil.showHRSaveErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   private void buildDataChu(String str, Ecard e) {
/* 314 */     if (JOptionPane.showConfirmDialog(null, "确定生成信用卡（" + str + "）" + this.tf_ym_from.getText() + "的消费数据？", "询问", 2, 3) != 0)
/*     */     {
/* 316 */       return;
/*     */     }
/* 318 */     List<String> keys = new ArrayList();
/* 319 */     if (e != null) {
/* 320 */       keys.add(e.getEcard_key());
/* 321 */       str = "指定卡";
/*     */     }
/* 323 */     ValidateSQLResult result = org.jhrcore.iservice.impl.EcardImpl.calcXiaoFei(this.tf_ym_from.getText(), str, keys);
/* 324 */     if (result.getResult() == 0) {
/* 325 */       JOptionPane.showMessageDialog(null, "生成数据成功");
/* 326 */       fetchData(null);
/*     */     } else {
/* 328 */       MsgUtil.showHRSaveErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   private JSplitPane jSplitPane2;
/*     */   private JTabbedPane jTabbedPane1;
/*     */   private JPanel pnlBeanCard;
/*     */   private JPanel pnlBeanChu;
/*     */   private JPanel pnlCard;
/*     */   private JPanel pnlTable;
/*     */   private JTextField tf_ym_from;
/*     */   private JTextField tf_ym_to;
/*     */   private JToolBar toolBar;
/* 341 */   private void initComponents() { this.jSplitPane1 = new JSplitPane();
/* 342 */     this.jPanel1 = new JPanel();
/* 343 */     this.toolBar = new JToolBar();
/* 344 */     this.btnAdd = new JButton();
/* 345 */     this.btnEdit = new JButton();
/* 346 */     this.jLabel1 = new JLabel();
/* 347 */     this.tf_ym_from = new JTextField();
/* 348 */     this.jLabel2 = new JLabel();
/* 349 */     this.tf_ym_to = new JTextField();
/* 350 */     this.btnSearch = new JButton();
/* 351 */     this.jSplitPane2 = new JSplitPane();
/* 352 */     this.pnlTable = new JPanel();
/* 353 */     this.jPanel3 = new JPanel();
/* 354 */     this.jTabbedPane1 = new JTabbedPane();
/* 355 */     this.pnlBeanChu = new JPanel();
/* 356 */     this.pnlBeanCard = new JPanel();
/* 357 */     this.pnlCard = new JPanel();
/*     */     
/* 359 */     this.jSplitPane1.setDividerLocation(200);
/* 360 */     this.jSplitPane1.setOneTouchExpandable(true);
/*     */     
/* 362 */     this.toolBar.setFloatable(false);
/* 363 */     this.toolBar.setRollover(true);
/*     */     
/* 365 */     this.btnAdd.setText("生成数据");
/* 366 */     this.btnAdd.setFocusable(false);
/* 367 */     this.btnAdd.setHorizontalTextPosition(0);
/* 368 */     this.btnAdd.setVerticalTextPosition(3);
/* 369 */     this.toolBar.add(this.btnAdd);
/*     */     
/* 371 */     this.btnEdit.setText("微调");
/* 372 */     this.btnEdit.setFocusable(false);
/* 373 */     this.btnEdit.setHorizontalTextPosition(0);
/* 374 */     this.btnEdit.setVerticalTextPosition(3);
/* 375 */     this.toolBar.add(this.btnEdit);
/*     */     
/* 377 */     this.jLabel1.setText(" 年月起：");
/* 378 */     this.toolBar.add(this.jLabel1);
/*     */     
/* 380 */     this.tf_ym_from.setText("201601");
/* 381 */     this.tf_ym_from.setMaximumSize(new Dimension(60, Integer.MAX_VALUE));
/* 382 */     this.tf_ym_from.setMinimumSize(new Dimension(60, 21));
/* 383 */     this.tf_ym_from.setName("");
/* 384 */     this.tf_ym_from.setPreferredSize(new Dimension(60, 21));
/* 385 */     this.toolBar.add(this.tf_ym_from);
/*     */     
/* 387 */     this.jLabel2.setText(" 止：");
/* 388 */     this.toolBar.add(this.jLabel2);
/*     */     
/* 390 */     this.tf_ym_to.setText("201601");
/* 391 */     this.tf_ym_to.setMaximumSize(new Dimension(60, Integer.MAX_VALUE));
/* 392 */     this.tf_ym_to.setMinimumSize(new Dimension(60, 21));
/* 393 */     this.tf_ym_to.setName("");
/* 394 */     this.tf_ym_to.setPreferredSize(new Dimension(60, 21));
/* 395 */     this.toolBar.add(this.tf_ym_to);
/*     */     
/* 397 */     this.btnSearch.setText("查询");
/* 398 */     this.btnSearch.setFocusable(false);
/* 399 */     this.btnSearch.setHorizontalTextPosition(0);
/* 400 */     this.btnSearch.setVerticalTextPosition(3);
/* 401 */     this.toolBar.add(this.btnSearch);
/*     */     
/* 403 */     this.jSplitPane2.setDividerLocation(340);
/* 404 */     this.jSplitPane2.setDividerSize(3);
/* 405 */     this.jSplitPane2.setOrientation(0);
/*     */     
/* 407 */     this.pnlTable.setLayout(new BorderLayout());
/* 408 */     this.jSplitPane2.setTopComponent(this.pnlTable);
/*     */     
/* 410 */     this.pnlBeanChu.setLayout(new BorderLayout());
/* 411 */     this.jTabbedPane1.addTab("汇款信息", this.pnlBeanChu);
/*     */     
/* 413 */     this.pnlBeanCard.setLayout(new BorderLayout());
/* 414 */     this.jTabbedPane1.addTab("信用卡信息", this.pnlBeanCard);
/*     */     
/* 416 */     GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
/* 417 */     this.jPanel3.setLayout(jPanel3Layout);
/* 418 */     jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/* 422 */     jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 427 */     this.jSplitPane2.setRightComponent(this.jPanel3);
/*     */     
/* 429 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 430 */     this.jPanel1.setLayout(jPanel1Layout);
/* 431 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, -1, 32767).addComponent(this.jSplitPane2));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 436 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.toolBar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSplitPane2)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 444 */     this.jSplitPane1.setRightComponent(this.jPanel1);
/*     */     
/* 446 */     this.pnlCard.setLayout(new BorderLayout());
/* 447 */     this.jSplitPane1.setLeftComponent(this.pnlCard);
/*     */     
/* 449 */     GroupLayout layout = new GroupLayout(this);
/* 450 */     setLayout(layout);
/* 451 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 644, 32767));
/*     */     
/*     */ 
/*     */ 
/* 455 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 418, 32767));
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardXfPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */