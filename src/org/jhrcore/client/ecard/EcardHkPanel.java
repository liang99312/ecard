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
/*     */ import org.jhrcore.client.ecard.ui.EditBeanPanel;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.entity.ecard.Ecard_ru;
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
/*     */ public class EcardHkPanel extends JPanel
/*     */ {
/*     */   private CardPanel cardPanel;
/*     */   private FTable ftable_hk;
/*  60 */   private List<TempFieldInfo> all_infos_hk = new ArrayList();
/*  61 */   private List<TempFieldInfo> default_infos_hk = new ArrayList();
/*  62 */   private List<TempFieldInfo> default_orders_hk = new ArrayList();
/*  63 */   private String order_sql_hk = "e.ecard_code,e.ru_date";
/*     */   private QueryScheme qs_hk;
/*     */   private String sum_sql_hk;
/*     */   private Object curTreeNode;
/*     */   private Object curEcard_ru;
/*  68 */   private BeanPanel beanPanel_hk = new BeanPanel();
/*  69 */   private BeanPanel ebeanPanel = new BeanPanel();
/*  70 */   private boolean editable = false;
/*  71 */   private boolean editStyle = false;
/*     */   public static final String module_code = "EcardHkPanel";
/*     */   private JButton btnAdd;
/*     */   
/*  75 */   public EcardHkPanel() { initComponents();
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
/*  88 */         EcardHkPanel.this.buildDataRu(str, e);
/*     */       }
/*     */       
/*     */       public void deleteData(Ecard e)
/*     */       {
/*  93 */         EcardHkPanel.this.deleteDataRu(e);
/*     */       }
/*  95 */     });
/*  96 */     this.pnlCard.add(this.cardPanel, "Center");
/*  97 */     this.ftable_hk = new FTable(Ecard_ru.class, true, true, true, "EcardHkPanel");
/*  98 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_ru.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*  99 */     for (TempFieldInfo tfi : shift_infos) {
/* 100 */       this.default_infos_hk.add(tfi);
/* 101 */       this.all_infos_hk.add(tfi);
/*     */     }
/* 103 */     this.ftable_hk.setAll_fields(this.all_infos_hk, this.default_infos_hk, this.default_orders_hk, "EcardHkPanel");
/* 104 */     this.order_sql_hk = SysUtil.getOrderString(this.ftable_hk.getCurOrderScheme(), "e", this.order_sql_hk, this.all_infos_hk);
/* 105 */     this.ftable_hk.setRight_allow_flag(true);
/* 106 */     this.pnlTable.add(this.ftable_hk);
/*     */     
/* 108 */     this.pnlBeanRu.add(new JScrollPane(this.beanPanel_hk));
/* 109 */     this.pnlBeanCard.add(new JScrollPane(this.ebeanPanel)); }
/*     */   
/*     */   private JButton btnSearch;
/*     */   
/* 113 */   private void setupEvents() { this.cardPanel.getCardTree().addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
/*     */     {
/*     */       public void valueChanged(TreeSelectionEvent e)
/*     */       {
/* 117 */         EcardHkPanel.this.curTreeNode = e.getPath().getLastPathComponent();
/* 118 */         if (EcardHkPanel.this.curTreeNode != null) {
/* 119 */           if ((((DefaultMutableTreeNode)EcardHkPanel.this.curTreeNode).getUserObject() instanceof Ecard)) {
/* 120 */             Ecard temp_card = (Ecard)((DefaultMutableTreeNode)EcardHkPanel.this.curTreeNode).getUserObject();
/* 121 */             EcardHkPanel.this.ebeanPanel.setBean(temp_card);
/* 122 */             EcardHkPanel.this.ebeanPanel.bind();
/*     */           }
/* 124 */           EcardHkPanel.this.fetchData(null);
/*     */         }
/*     */         
/*     */       }
/* 128 */     });
/* 129 */     this.ftable_hk.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 133 */         CommEditAction.doRowSaveAction(EcardHkPanel.this.curEcard_ru, EcardHkPanel.this.editable);
/* 134 */         if (EcardHkPanel.this.curEcard_ru == EcardHkPanel.this.ftable_hk.getCurrentRow()) {
/* 135 */           return;
/*     */         }
/* 137 */         EcardHkPanel.this.curEcard_ru = EcardHkPanel.this.ftable_hk.getCurrentRow();
/* 138 */         BeanPanel.refreshUIForTable(EcardHkPanel.this.ftable_hk, EcardHkPanel.this.beanPanel_hk, (EcardHkPanel.this.editable) && (EcardHkPanel.this.editStyle));
/* 139 */         ContextManager.setStatusBar(EcardHkPanel.this.ftable_hk.getObjects().size());
/*     */       }
/* 141 */     });
/* 142 */     this.ftable_hk.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 146 */         EcardHkPanel.this.order_sql_hk = SysUtil.getOrderString(showScheme, "e", EcardHkPanel.this.order_sql_hk, EcardHkPanel.this.all_infos_hk);
/* 147 */         EcardHkPanel.this.fetchData(EcardHkPanel.this.ftable_hk.getCur_query_scheme());
/*     */       }
/* 149 */     });
/* 150 */     this.ftable_hk.addPickQueryExListener(new IPickQueryExListener()
/*     */     {
/*     */       public void pickQuery(QueryScheme qs_hk)
/*     */       {
/* 154 */         EcardHkPanel.this.fetchData(qs_hk);
/*     */       }
/* 156 */     });
/* 157 */     this.ftable_hk.addPickFieldSetListener(new com.foundercy.pf.control.listener.IPickFieldSetListener()
/*     */     {
/*     */       public void pickField(ShowScheme showScheme)
/*     */       {
/* 161 */         BeanPanel.refreshUIForTable(EcardHkPanel.this.ftable_hk, EcardHkPanel.this.beanPanel_hk, (EcardHkPanel.this.editable) && (EcardHkPanel.this.editStyle));
/*     */       }
/* 163 */     });
/* 164 */     this.ftable_hk.addPickColumnSumListener(new IPickColumnSumListener()
/*     */     {
/*     */       public String pickSumSQL()
/*     */       {
/* 168 */         return EcardHkPanel.this.sum_sql_hk + "@sql";
/*     */       }
/* 170 */     });
/* 171 */     this.ftable_hk.addMouseListener(new java.awt.event.MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e)
/*     */       {
/* 175 */         if (e.getClickCount() >= 2) {
/* 176 */           if (EcardHkPanel.this.curEcard_ru == null) {
/* 177 */             return;
/*     */           }
/* 179 */           EditBeanPanel pnl = new EditBeanPanel(EcardHkPanel.this.curEcard_ru);
/* 180 */           ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑汇款信息", 650, 500, false);
/* 181 */           mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */           {
/*     */             public void pickClose()
/*     */             {
/* 185 */               EcardHkPanel.this.fetchData(null);
/*     */             }
/* 187 */           });
/* 188 */           mf.setVisible(true);
/*     */         }
/*     */       }
/* 191 */     });
/* 192 */     this.btnAdd.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 196 */         CalcEcardRuPanel pnl = new CalcEcardRuPanel("hk");
/* 197 */         ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "生成汇款数据", 380, 220, false);
/* 198 */         mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */         {
/*     */           public void pickClose()
/*     */           {
/* 202 */             EcardHkPanel.this.fetchData(null);
/*     */           }
/* 204 */         });
/* 205 */         mf.setVisible(true);
/*     */       }
/* 207 */     });
/* 208 */     this.btnEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 212 */         if (EcardHkPanel.this.curEcard_ru == null) {
/* 213 */           return;
/*     */         }
/* 215 */         EditBeanPanel pnl = new EditBeanPanel(EcardHkPanel.this.curEcard_ru);
/* 216 */         ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑汇款信息", 650, 500, false);
/* 217 */         mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */         {
/*     */           public void pickClose()
/*     */           {
/* 221 */             EcardHkPanel.this.fetchData(null);
/*     */           }
/* 223 */         });
/* 224 */         mf.setVisible(true);
/*     */       }
/* 226 */     });
/* 227 */     this.btnSearch.addActionListener(new ActionListener()
/*     */     {
/*     */ 
/*     */       public void actionPerformed(ActionEvent e) {
/* 231 */         EcardHkPanel.this.fetchData(null); }
/*     */     }); }
/*     */   
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   
/* 237 */   private void fetchData(QueryScheme qs_hk) { if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/* 238 */       String ym_from = this.tf_ym_from.getText();
/* 239 */       String ym_to = this.tf_ym_to.getText();
/* 240 */       if ((!SysUtil.check_month(ym_to)) || (!SysUtil.check_month(ym_from))) {
/* 241 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/* 242 */         return;
/*     */       }
/* 244 */       String where_sql = "";
/* 245 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/* 246 */       if ((t_obj instanceof String)) {
/* 247 */         if (!"所有卡".equals(t_obj.toString()))
/*     */         {
/* 249 */           if ("已激活".equals(t_obj.toString())) {
/* 250 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/* 251 */           } else if ("已停止".equals(t_obj.toString())) {
/* 252 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/* 253 */           } else if ("普养".equals(t_obj.toString())) {
/* 254 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 255 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 256 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*     */             } else {
/* 258 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*     */             }
/* 260 */           } else if ("中养".equals(t_obj.toString())) {
/* 261 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 262 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 263 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*     */             } else {
/* 265 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*     */             }
/* 267 */           } else if ("精养".equals(t_obj.toString())) {
/* 268 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 269 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 270 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*     */             } else
/* 272 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*     */           }
/*     */         }
/* 275 */       } else if ((t_obj instanceof Ecard)) {
/* 276 */         Ecard e = (Ecard)t_obj;
/* 277 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/*     */       }
/*     */       
/* 280 */       String sql = " from Ecard_ru e where 1=1";
/* 281 */       sql = sql + " and ru_ym >='" + ym_from + "' and ru_ym<='" + ym_to + "'";
/* 282 */       if (qs_hk != null) {
/* 283 */         sql = sql + " and e.ecard_ru_key in(" + qs_hk.buildSql() + ")";
/*     */       }
/* 285 */       sql = sql + where_sql;
/* 286 */       this.sum_sql_hk = sql;
/* 287 */       sql = "select ecard_ru_key" + sql + " order by " + this.order_sql_hk;
/* 288 */       PublicUtil.getProps_value().setProperty(Ecard_ru.class.getName(), "from Ecard_ru e where e.ecard_ru_key in");
/* 289 */       List list = CommUtil.selectSQL(org.jhrcore.util.DbUtil.tranSQL(sql));
/* 290 */       this.ftable_hk.setObjects(list);
/* 291 */       refreshStatus(); } }
/*     */   
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel3;
/*     */   private JSplitPane jSplitPane1;
/* 296 */   private void refreshStatus() { ContextManager.setStatusBar(this.ftable_hk.getObjects().size()); }
/*     */   
/*     */ 
/*     */   private void deleteDataRu(Ecard e) {
/* 300 */     if (JOptionPane.showConfirmDialog(null, "确定删除信用卡（" + e.toString() + "）" + this.tf_ym_from.getText() + "的汇款数据？", "询问", 2, 3) != 0)
/*     */     {
/* 302 */       return;
/*     */     }
/* 304 */     String sql = "delete from Ecard_ru where ecard_key='" + e.getEcard_key() + "' and ru_ym='" + this.tf_ym_from.getText() + "'";
/* 305 */     ValidateSQLResult result = CommUtil.excuteSQL(sql);
/* 306 */     if (result.getResult() == 0) {
/* 307 */       JOptionPane.showMessageDialog(null, "删除数据成功");
/* 308 */       fetchData(null);
/*     */     } else {
/* 310 */       MsgUtil.showHRSaveErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   private void buildDataRu(String str, Ecard e) {
/* 315 */     if (JOptionPane.showConfirmDialog(null, "确定生成信用卡（" + str + "）" + this.tf_ym_from.getText() + "的汇款数据？", "询问", 2, 3) != 0)
/*     */     {
/* 317 */       return;
/*     */     }
/* 319 */     List<String> keys = new ArrayList();
/* 320 */     if (e != null) {
/* 321 */       keys.add(e.getEcard_key());
/* 322 */       str = "指定卡";
/*     */     }
/* 324 */     ValidateSQLResult result = org.jhrcore.iservice.impl.EcardImpl.calcHuiKuan(this.tf_ym_from.getText(), str, keys);
/* 325 */     if (result.getResult() == 0) {
/* 326 */       JOptionPane.showMessageDialog(null, "生成数据成功");
/* 327 */       fetchData(null);
/*     */     } else {
/* 329 */       MsgUtil.showHRSaveErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   private JSplitPane jSplitPane2;
/*     */   private JTabbedPane jTabbedPane1;
/*     */   private JPanel pnlBeanCard;
/*     */   private JPanel pnlBeanRu;
/*     */   private JPanel pnlCard;
/*     */   private JPanel pnlTable;
/*     */   private JTextField tf_ym_from;
/*     */   private JTextField tf_ym_to;
/*     */   private JToolBar toolBar;
/* 342 */   private void initComponents() { this.jSplitPane1 = new JSplitPane();
/* 343 */     this.jPanel1 = new JPanel();
/* 344 */     this.toolBar = new JToolBar();
/* 345 */     this.btnAdd = new JButton();
/* 346 */     this.btnEdit = new JButton();
/* 347 */     this.jLabel1 = new JLabel();
/* 348 */     this.tf_ym_from = new JTextField();
/* 349 */     this.jLabel2 = new JLabel();
/* 350 */     this.tf_ym_to = new JTextField();
/* 351 */     this.btnSearch = new JButton();
/* 352 */     this.jSplitPane2 = new JSplitPane();
/* 353 */     this.pnlTable = new JPanel();
/* 354 */     this.jPanel3 = new JPanel();
/* 355 */     this.jTabbedPane1 = new JTabbedPane();
/* 356 */     this.pnlBeanRu = new JPanel();
/* 357 */     this.pnlBeanCard = new JPanel();
/* 358 */     this.pnlCard = new JPanel();
/*     */     
/* 360 */     this.jSplitPane1.setDividerLocation(200);
/* 361 */     this.jSplitPane1.setOneTouchExpandable(true);
/*     */     
/* 363 */     this.toolBar.setFloatable(false);
/* 364 */     this.toolBar.setRollover(true);
/*     */     
/* 366 */     this.btnAdd.setText("生成数据");
/* 367 */     this.btnAdd.setFocusable(false);
/* 368 */     this.btnAdd.setHorizontalTextPosition(0);
/* 369 */     this.btnAdd.setVerticalTextPosition(3);
/* 370 */     this.toolBar.add(this.btnAdd);
/*     */     
/* 372 */     this.btnEdit.setText("微调");
/* 373 */     this.btnEdit.setFocusable(false);
/* 374 */     this.btnEdit.setHorizontalTextPosition(0);
/* 375 */     this.btnEdit.setVerticalTextPosition(3);
/* 376 */     this.toolBar.add(this.btnEdit);
/*     */     
/* 378 */     this.jLabel1.setText(" 年月起：");
/* 379 */     this.toolBar.add(this.jLabel1);
/*     */     
/* 381 */     this.tf_ym_from.setText("201601");
/* 382 */     this.tf_ym_from.setMaximumSize(new Dimension(60, Integer.MAX_VALUE));
/* 383 */     this.tf_ym_from.setMinimumSize(new Dimension(60, 21));
/* 384 */     this.tf_ym_from.setName("");
/* 385 */     this.tf_ym_from.setPreferredSize(new Dimension(60, 21));
/* 386 */     this.toolBar.add(this.tf_ym_from);
/*     */     
/* 388 */     this.jLabel2.setText(" 止：");
/* 389 */     this.toolBar.add(this.jLabel2);
/*     */     
/* 391 */     this.tf_ym_to.setText("201601");
/* 392 */     this.tf_ym_to.setMaximumSize(new Dimension(60, Integer.MAX_VALUE));
/* 393 */     this.tf_ym_to.setMinimumSize(new Dimension(60, 21));
/* 394 */     this.tf_ym_to.setName("");
/* 395 */     this.tf_ym_to.setPreferredSize(new Dimension(60, 21));
/* 396 */     this.toolBar.add(this.tf_ym_to);
/*     */     
/* 398 */     this.btnSearch.setText("查询");
/* 399 */     this.btnSearch.setFocusable(false);
/* 400 */     this.btnSearch.setHorizontalTextPosition(0);
/* 401 */     this.btnSearch.setVerticalTextPosition(3);
/* 402 */     this.toolBar.add(this.btnSearch);
/*     */     
/* 404 */     this.jSplitPane2.setDividerLocation(340);
/* 405 */     this.jSplitPane2.setDividerSize(3);
/* 406 */     this.jSplitPane2.setOrientation(0);
/*     */     
/* 408 */     this.pnlTable.setLayout(new BorderLayout());
/* 409 */     this.jSplitPane2.setTopComponent(this.pnlTable);
/*     */     
/* 411 */     this.pnlBeanRu.setLayout(new BorderLayout());
/* 412 */     this.jTabbedPane1.addTab("汇款信息", this.pnlBeanRu);
/*     */     
/* 414 */     this.pnlBeanCard.setLayout(new BorderLayout());
/* 415 */     this.jTabbedPane1.addTab("信用卡信息", this.pnlBeanCard);
/*     */     
/* 417 */     GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
/* 418 */     this.jPanel3.setLayout(jPanel3Layout);
/* 419 */     jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/* 423 */     jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 428 */     this.jSplitPane2.setRightComponent(this.jPanel3);
/*     */     
/* 430 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 431 */     this.jPanel1.setLayout(jPanel1Layout);
/* 432 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, -1, 32767).addComponent(this.jSplitPane2));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 437 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.toolBar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSplitPane2)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 445 */     this.jSplitPane1.setRightComponent(this.jPanel1);
/*     */     
/* 447 */     this.pnlCard.setLayout(new BorderLayout());
/* 448 */     this.jSplitPane1.setLeftComponent(this.pnlCard);
/*     */     
/* 450 */     GroupLayout layout = new GroupLayout(this);
/* 451 */     setLayout(layout);
/* 452 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 644, 32767));
/*     */     
/*     */ 
/*     */ 
/* 456 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 418, 32767));
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardHkPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */