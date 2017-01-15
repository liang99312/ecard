/*      */ package org.jhrcore.client.ecard;
/*      */ 
/*      */ import com.foundercy.pf.control.listener.IPickColumnSumListener;
/*      */ import com.foundercy.pf.control.listener.IPickFieldOrderListener;
/*      */ import com.foundercy.pf.control.listener.IPickFieldSetListener;
/*      */ import com.foundercy.pf.control.table.FBaseTableColumn;
/*      */ import com.foundercy.pf.control.table.FBaseTableColumnModel;
/*      */ import com.foundercy.pf.control.table.FTable;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Hashtable;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import javax.swing.GroupLayout;
/*      */ import javax.swing.GroupLayout.Alignment;
/*      */ import javax.swing.GroupLayout.ParallelGroup;
/*      */ import javax.swing.GroupLayout.SequentialGroup;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.JTree;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.event.TreeSelectionEvent;
/*      */ import javax.swing.tree.DefaultMutableTreeNode;
/*      */ import javax.swing.tree.TreePath;
/*      */ import org.jhrcore.client.CommUtil;
/*      */ import org.jhrcore.client.ecard.ui.CardPanel;
/*      */ import org.jhrcore.client.ecard.ui.EditBeanPanel;
/*      */ import org.jhrcore.client.ecard.ui.EditChuPanel;
/*      */ import org.jhrcore.entity.base.TempFieldInfo;
/*      */ import org.jhrcore.entity.ecard.Ecard;
/*      */ import org.jhrcore.entity.ecard.Ecard_chu;
/*      */ import org.jhrcore.entity.ecard.Ecard_ru;
/*      */ import org.jhrcore.entity.query.QueryScheme;
/*      */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*      */ import org.jhrcore.entity.showstyle.ShowScheme;
/*      */ import org.jhrcore.rebuild.EntityBuilder;
/*      */ import org.jhrcore.ui.BeanPanel;
/*      */ import org.jhrcore.ui.ContextManager;
/*      */ import org.jhrcore.ui.ModelFrame;
/*      */ import org.jhrcore.ui.listener.IPickWindowCloseListener;
/*      */ import org.jhrcore.util.DateUtil;
/*      */ import org.jhrcore.util.DbUtil;
/*      */ import org.jhrcore.util.MsgUtil;
/*      */ import org.jhrcore.util.PublicUtil;
/*      */ import org.jhrcore.util.SysUtil;
/*      */ 
/*      */ public class EcardPlanPanel extends JPanel
/*      */ {
/*      */   private CardPanel cardPanel;
/*      */   private FTable ftable3;
/*      */   private Object curTreeNode;
/*   66 */   private BeanPanel ebeanPanel = new BeanPanel();
/*      */   public static final String module_code = "EcardPlanPanel";
/*   68 */   private java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MM-dd");
/*   69 */   private DecimalFormat df = new DecimalFormat("###.####");
/*   70 */   private int hk = 0; private int xf = 0; private int hz = 0;
/*   71 */   private String curYm = "";
/*   72 */   private String ecardManager = "刷卡管理员：";
/*   73 */   private String ecardName = "";
/*      */   
/*      */   private FTable ftable_xf;
/*   76 */   private List<TempFieldInfo> all_infos_xf = new ArrayList();
/*   77 */   private List<TempFieldInfo> default_infos_xf = new ArrayList();
/*   78 */   private List<TempFieldInfo> default_orders_xf = new ArrayList();
/*   79 */   private String order_sql_xf = "e.ecard_code,e.chu_date";
/*      */   
/*      */   private String sum_sql_xf;
/*      */   private Object curEcard_chu;
/*      */   private FTable ftable_hk;
/*   84 */   private List<TempFieldInfo> all_infos_hk = new ArrayList();
/*   85 */   private List<TempFieldInfo> default_infos_hk = new ArrayList();
/*   86 */   private List<TempFieldInfo> default_orders_hk = new ArrayList();
/*   87 */   private String order_sql_hk = "e.ecard_code,e.ru_date";
/*      */   private String sum_sql_hk;
/*      */   private Object curEcard_ru;
/*      */   private JButton btnEdit;
/*      */   
/*   92 */   public EcardPlanPanel() { initComponents();
/*   93 */     initOthers();
/*   94 */     setupEvents(); }
/*      */   
/*      */   private JButton btnExcel;
/*      */   
/*   98 */   private void initOthers() { this.tf_ym.setText(DateUtil.DateToStr(new java.util.Date(), "yyyyMM"));
/*   99 */     this.cardPanel = new CardPanel("build");
/*  100 */     this.cardPanel.addBuildDataSetListener(new org.jhrcore.client.ecard.listener.IBuildDataSetListener()
/*      */     {
/*      */       public void buildData(String str, Ecard e, int index)
/*      */       {
/*  104 */         if (index == 2) {
/*  105 */           EcardPlanPanel.this.buildDataChu(str, e);
/*  106 */         } else if (index == 1) {
/*  107 */           EcardPlanPanel.this.buildDataRu(str, e);
/*      */         }
/*      */       }
/*      */       
/*      */       public void deleteData(Ecard e)
/*      */       {
/*  113 */         EcardPlanPanel.this.deleteAllData(e);
/*      */       }
/*  115 */     });
/*  116 */     this.pnlCard.add(this.cardPanel, "Center");
/*  117 */     this.pnlBeanCard.add(new javax.swing.JScrollPane(this.ebeanPanel));
/*      */     
/*  119 */     this.ftable_xf = new FTable(Ecard_chu.class, true, true, true, "EcardPlanPanel");
/*  120 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_chu.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*  121 */     for (TempFieldInfo tfi : shift_infos) {
/*  122 */       this.default_infos_xf.add(tfi);
/*  123 */       this.all_infos_xf.add(tfi);
/*      */     }
/*  125 */     this.ftable_xf.setAll_fields(this.all_infos_xf, this.default_infos_xf, this.default_orders_xf, "EcardPlanPanel");
/*  126 */     this.order_sql_xf = SysUtil.getOrderString(this.ftable_xf.getCurOrderScheme(), "e", this.order_sql_xf, this.all_infos_xf);
/*  127 */     this.ftable_xf.setRight_allow_flag(true);
/*  128 */     this.ftable_xf.lockColumns(2);
/*  129 */     this.pnlXf.add(this.ftable_xf);
/*      */     
/*  131 */     this.ftable_hk = new FTable(Ecard_ru.class, true, true, true, "EcardPlanPanel");
/*  132 */     List<TempFieldInfo> chu_infos = EntityBuilder.getCommFieldInfoListOf(Ecard_ru.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*  133 */     for (TempFieldInfo tfi : chu_infos) {
/*  134 */       this.default_infos_hk.add(tfi);
/*  135 */       this.all_infos_hk.add(tfi);
/*      */     }
/*  137 */     this.ftable_hk.setAll_fields(this.all_infos_hk, this.default_infos_hk, this.default_orders_hk, "EcardPlanPanel");
/*  138 */     this.order_sql_hk = SysUtil.getOrderString(this.ftable_hk.getCurOrderScheme(), "e", this.order_sql_hk, this.all_infos_hk);
/*  139 */     this.ftable_hk.setRight_allow_flag(true);
/*  140 */     this.ftable_hk.lockColumns(2);
/*  141 */     this.pnlHk.add(this.ftable_hk);
/*      */     
/*  143 */     this.btnXf.setVisible(false); }
/*      */   
/*      */   private JButton btnHk;
/*      */   
/*  147 */   private void setupEvents() { this.btnSearch.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  151 */         EcardPlanPanel.this.hk = 0;
/*  152 */         EcardPlanPanel.this.xf = 0;
/*  153 */         EcardPlanPanel.this.hz = 0;
/*  154 */         EcardPlanPanel.this.curYm = EcardPlanPanel.this.tf_ym.getText();
/*  155 */         EcardPlanPanel.this.fetchData();
/*      */       }
/*  157 */     });
/*  158 */     this.btnExcel.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  162 */         if (EcardPlanPanel.this.tabPanel.getSelectedIndex() == 2) {
/*  163 */           EcardPlanPanel.this.ftable_xf.exportData(EcardPlanPanel.this.ecardManager, EcardPlanPanel.this.ecardName);
/*  164 */         } else if (EcardPlanPanel.this.tabPanel.getSelectedIndex() == 1) {
/*  165 */           EcardPlanPanel.this.ftable_hk.exportData(EcardPlanPanel.this.ecardManager, EcardPlanPanel.this.ecardName);
/*      */         } else {
/*  167 */           EcardPlanPanel.this.ftable3.exportData(EcardPlanPanel.this.ecardManager, EcardPlanPanel.this.ecardName);
/*      */         }
/*      */       }
/*  170 */     });
/*  171 */     this.cardPanel.getCardTree().addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
/*      */     {
/*      */       public void valueChanged(TreeSelectionEvent e)
/*      */       {
/*  175 */         EcardPlanPanel.this.curTreeNode = e.getPath().getLastPathComponent();
/*  176 */         EcardPlanPanel.this.ecardManager = "";
/*  177 */         EcardPlanPanel.this.ecardName = "";
/*  178 */         if (EcardPlanPanel.this.curTreeNode != null) {
/*  179 */           if ((((DefaultMutableTreeNode)EcardPlanPanel.this.curTreeNode).getUserObject() instanceof Ecard)) {
/*  180 */             Ecard temp_card = (Ecard)((DefaultMutableTreeNode)EcardPlanPanel.this.curTreeNode).getUserObject();
/*  181 */             EcardPlanPanel.this.ebeanPanel.setBean(temp_card);
/*  182 */             EcardPlanPanel.this.ebeanPanel.bind();
/*      */           }
/*  184 */           EcardPlanPanel.this.hk = 0;
/*  185 */           EcardPlanPanel.this.xf = 0;
/*  186 */           EcardPlanPanel.this.hz = 0;
/*  187 */           EcardPlanPanel.this.fetchData();
/*      */         }
/*      */       }
/*  190 */     });
/*  191 */     this.tabPanel.addChangeListener(new javax.swing.event.ChangeListener()
/*      */     {
/*      */       public void stateChanged(javax.swing.event.ChangeEvent e)
/*      */       {
/*  195 */         EcardPlanPanel.this.fetchData();
/*      */       }
/*      */       
/*  198 */     });
/*  199 */     this.ftable_xf.addListSelectionListener(new ListSelectionListener()
/*      */     {
/*      */       public void valueChanged(ListSelectionEvent e)
/*      */       {
/*  203 */         if (EcardPlanPanel.this.curEcard_chu == EcardPlanPanel.this.ftable_xf.getCurrentRow()) {
/*  204 */           return;
/*      */         }
/*  206 */         EcardPlanPanel.this.curEcard_chu = EcardPlanPanel.this.ftable_xf.getCurrentRow();
/*      */       }
/*  208 */     });
/*  209 */     this.ftable_xf.addPickFieldOrderListener(new IPickFieldOrderListener()
/*      */     {
/*      */       public void pickOrder(ShowScheme showScheme)
/*      */       {
/*  213 */         EcardPlanPanel.this.order_sql_xf = SysUtil.getOrderString(showScheme, "e", EcardPlanPanel.this.order_sql_xf, EcardPlanPanel.this.all_infos_xf);
/*  214 */         EcardPlanPanel.this.xf = 0;
/*  215 */         EcardPlanPanel.this.fetchData_xf(EcardPlanPanel.this.ftable_xf.getCur_query_scheme());
/*      */       }
/*  217 */     });
/*  218 */     this.ftable_xf.addPickQueryExListener(new com.foundercy.pf.control.listener.IPickQueryExListener()
/*      */     {
/*      */       public void pickQuery(QueryScheme qs)
/*      */       {
/*  222 */         EcardPlanPanel.this.xf = 0;
/*  223 */         EcardPlanPanel.this.fetchData_xf(qs);
/*      */       }
/*  225 */     });
/*  226 */     this.ftable_xf.addPickFieldSetListener(new IPickFieldSetListener()
/*      */     {
/*      */       public void pickField(ShowScheme showScheme)
/*      */       {
/*  230 */         BeanPanel.refreshUIForTable(EcardPlanPanel.this.ftable_xf, null, false);
/*      */       }
/*  232 */     });
/*  233 */     this.ftable_xf.addPickColumnSumListener(new IPickColumnSumListener()
/*      */     {
/*      */       public String pickSumSQL()
/*      */       {
/*  237 */         return EcardPlanPanel.this.sum_sql_xf + "@sql";
/*      */       }
/*  239 */     });
/*  240 */     this.ftable_xf.addMouseListener(new MouseAdapter()
/*      */     {
/*      */       public void mouseClicked(MouseEvent e)
/*      */       {
/*  244 */         if (e.getClickCount() >= 2) {
/*  245 */           if (EcardPlanPanel.this.curEcard_chu == null) {
/*  246 */             return;
/*      */           }
/*  248 */           EditChuPanel pnl = new EditChuPanel(EcardPlanPanel.this.curEcard_chu);
/*  249 */           ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
/*  250 */           mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*      */           {
/*      */             public void pickClose()
/*      */             {
/*  254 */               EcardPlanPanel.this.xf = 0;
/*  255 */               EcardPlanPanel.this.fetchData_xf(null);
/*      */             }
/*  257 */           });
/*  258 */           mf.setVisible(true);
/*      */         }
/*      */         
/*      */       }
/*  262 */     });
/*  263 */     this.ftable_hk.addListSelectionListener(new ListSelectionListener()
/*      */     {
/*      */       public void valueChanged(ListSelectionEvent e)
/*      */       {
/*  267 */         if (EcardPlanPanel.this.curEcard_ru == EcardPlanPanel.this.ftable_hk.getCurrentRow()) {
/*  268 */           return;
/*      */         }
/*  270 */         EcardPlanPanel.this.curEcard_ru = EcardPlanPanel.this.ftable_hk.getCurrentRow();
/*      */       }
/*  272 */     });
/*  273 */     this.ftable_hk.addPickFieldOrderListener(new IPickFieldOrderListener()
/*      */     {
/*      */       public void pickOrder(ShowScheme showScheme)
/*      */       {
/*  277 */         EcardPlanPanel.this.order_sql_hk = SysUtil.getOrderString(showScheme, "e", EcardPlanPanel.this.order_sql_hk, EcardPlanPanel.this.all_infos_hk);
/*  278 */         EcardPlanPanel.this.hk = 0;
/*  279 */         EcardPlanPanel.this.fetchData_hk(EcardPlanPanel.this.ftable_hk.getCur_query_scheme());
/*      */       }
/*  281 */     });
/*  282 */     this.ftable_hk.addPickQueryExListener(new com.foundercy.pf.control.listener.IPickQueryExListener()
/*      */     {
/*      */       public void pickQuery(QueryScheme qs_hk)
/*      */       {
/*  286 */         EcardPlanPanel.this.hk = 0;
/*  287 */         EcardPlanPanel.this.fetchData_hk(qs_hk);
/*      */       }
/*  289 */     });
/*  290 */     this.ftable_hk.addPickFieldSetListener(new IPickFieldSetListener()
/*      */     {
/*      */       public void pickField(ShowScheme showScheme)
/*      */       {
/*  294 */         BeanPanel.refreshUIForTable(EcardPlanPanel.this.ftable_hk, null, false);
/*      */       }
/*  296 */     });
/*  297 */     this.ftable_hk.addPickColumnSumListener(new IPickColumnSumListener()
/*      */     {
/*      */       public String pickSumSQL()
/*      */       {
/*  301 */         return EcardPlanPanel.this.sum_sql_hk + "@sql";
/*      */       }
/*  303 */     });
/*  304 */     this.ftable_hk.addMouseListener(new MouseAdapter()
/*      */     {
/*      */       public void mouseClicked(MouseEvent e)
/*      */       {
/*  308 */         if (e.getClickCount() >= 2) {
/*  309 */           if (EcardPlanPanel.this.curEcard_ru == null) {
/*  310 */             return;
/*      */           }
/*  312 */           EditBeanPanel pnl = new EditBeanPanel(EcardPlanPanel.this.curEcard_ru);
/*  313 */           ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑汇款信息", 650, 500, false);
/*  314 */           mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*      */           {
/*      */             public void pickClose()
/*      */             {
/*  318 */               EcardPlanPanel.this.hk = 0;
/*  319 */               EcardPlanPanel.this.fetchData_hk(null);
/*      */             }
/*  321 */           });
/*  322 */           mf.setVisible(true);
/*      */         }
/*      */         
/*      */       }
/*  326 */     });
/*  327 */     this.btnEdit.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  331 */         if (EcardPlanPanel.this.tabPanel.getSelectedIndex() == 2) {
/*  332 */           EcardPlanPanel.this.curEcard_chu = EcardPlanPanel.this.ftable_xf.getCurrentRow();
/*  333 */           EcardPlanPanel.this.editChu();
/*  334 */         } else if (EcardPlanPanel.this.tabPanel.getSelectedIndex() == 1) {
/*  335 */           EcardPlanPanel.this.curEcard_ru = EcardPlanPanel.this.ftable_hk.getCurrentRow();
/*  336 */           EcardPlanPanel.this.editRu();
/*      */         }
/*  338 */         else if (EcardPlanPanel.this.ftable3.getCurrentRow() != null) {
/*  339 */           Object[] obj = (Object[])EcardPlanPanel.this.ftable3.getCurrentRow();
/*  340 */           if ((obj.length == 5) && (obj[4] != null)) {
/*  341 */             if ("".equals(obj[3].toString())) {
/*  342 */               Object object = CommUtil.fetchEntityBy("from Ecard_ru where ecard_ru_key='" + obj[4].toString() + "'");
/*  343 */               if (object != null) {
/*  344 */                 EcardPlanPanel.this.curEcard_ru = object;
/*  345 */                 EcardPlanPanel.this.editRu();
/*      */               }
/*      */             } else {
/*  348 */               Object object = CommUtil.fetchEntityBy("from Ecard_chu where ecard_chu_key='" + obj[4].toString() + "'");
/*  349 */               if (object != null) {
/*  350 */                 EcardPlanPanel.this.curEcard_chu = object;
/*  351 */                 EcardPlanPanel.this.editChu();
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*  359 */     });
/*  360 */     this.btnHk.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  364 */         EcardPlanPanel.this.buildData(1);
/*      */       }
/*      */       
/*  367 */     });
/*  368 */     this.btnXf.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  372 */         EcardPlanPanel.this.buildData(2);
/*      */       }
/*  374 */     });
/*  375 */     this.cb_fl.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  379 */         EcardPlanPanel.this.xf = 0;
/*  380 */         EcardPlanPanel.this.fetchData_xf(null);
/*      */       }
/*  382 */     });
/*  383 */     this.cb_fl.setEnabled(false); }
/*      */   
/*      */   private JButton btnSearch;
/*      */   
/*  387 */   private void buildData(int index) { if ((this.cardPanel.getCardTree().getSelectionPath() == null) || (this.cardPanel.getCardTree().getSelectionPath().getLastPathComponent() == null)) {
/*  388 */       JOptionPane.showMessageDialog(null, "请选择信用卡");
/*  389 */       return;
/*      */     }
/*  391 */     DefaultMutableTreeNode curTreeNode = (DefaultMutableTreeNode)this.cardPanel.getCardTree().getSelectionPath().getLastPathComponent();
/*  392 */     Object obj = curTreeNode.getUserObject();
/*  393 */     Ecard ecard = null;
/*  394 */     String string = "";
/*  395 */     if ((obj instanceof Ecard)) {
/*  396 */       ecard = (Ecard)obj;
/*  397 */       string = ecard.toString();
/*      */     }
/*      */     else {
/*  400 */       JOptionPane.showMessageDialog(null, "请选择信用卡");
/*  401 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  434 */     if (index == 2) {
/*  435 */       buildDataChu(string, ecard);
/*  436 */     } else if (index == 1)
/*  437 */       buildDataRu(string, ecard);
/*      */   }
/*      */   
/*      */   private JButton btnXf;
/*      */   
/*  442 */   private void editRu() { if (this.curEcard_ru == null) {
/*  443 */       return;
/*      */     }
/*  445 */     EditBeanPanel pnl = new EditBeanPanel(this.curEcard_ru);
/*  446 */     ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑汇款信息", 650, 500, false);
/*  447 */     mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*      */     {
/*      */       public void pickClose()
/*      */       {
/*  451 */         EcardPlanPanel.this.hk = 0;
/*  452 */         EcardPlanPanel.this.hz = 0;
/*  453 */         EcardPlanPanel.this.xf = 0;
/*  454 */         EcardPlanPanel.this.fetchData();
/*      */       }
/*  456 */     });
/*  457 */     mf.setVisible(true); }
/*      */   
/*      */   private JComboBox cb_fl;
/*      */   
/*  461 */   private void editChu() { if (this.curEcard_chu == null) {
/*  462 */       return;
/*      */     }
/*  464 */     EditChuPanel pnl = new EditChuPanel(this.curEcard_chu);
/*  465 */     ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
/*  466 */     mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*      */     {
/*      */       public void pickClose()
/*      */       {
/*  470 */         EcardPlanPanel.this.xf = 0;
/*  471 */         EcardPlanPanel.this.hz = 0;
/*  472 */         EcardPlanPanel.this.fetchData();
/*      */       }
/*  474 */     });
/*  475 */     mf.setVisible(true); }
/*      */   
/*      */   private JLabel jLabel1;
/*      */   
/*  479 */   private void fetchData() { this.cb_fl.setEnabled(false);
/*  480 */     if (this.tabPanel.getSelectedIndex() == 2) {
/*  481 */       this.cb_fl.setEnabled(true);
/*  482 */       fetchData_xf(null);
/*  483 */     } else if (this.tabPanel.getSelectedIndex() == 1) {
/*  484 */       fetchData_hk(null);
/*      */     } else {
/*  486 */       fetchData_hz(); } }
/*      */   
/*      */   private JLabel jLabel2;
/*      */   private JPanel jPanel1;
/*      */   private JPanel jPanel3;
/*  491 */   private void fetchData_hk(QueryScheme qs) { if (this.hk > 0) {
/*  492 */       return;
/*      */     }
/*  494 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/*  495 */       String ym = this.tf_ym.getText();
/*  496 */       if (!SysUtil.check_month(ym)) {
/*  497 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/*  498 */         return;
/*      */       }
/*  500 */       String where_sql = "";
/*  501 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/*  502 */       if ((t_obj instanceof String)) {
/*  503 */         if (!"所有卡".equals(t_obj.toString()))
/*      */         {
/*  505 */           if ("已激活".equals(t_obj.toString())) {
/*  506 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/*  507 */           } else if ("已停止".equals(t_obj.toString())) {
/*  508 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/*  509 */           } else if ("普养".equals(t_obj.toString())) {
/*  510 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  511 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  512 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*      */             } else {
/*  514 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*      */             }
/*  516 */           } else if ("中养".equals(t_obj.toString())) {
/*  517 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  518 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  519 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*      */             } else {
/*  521 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*      */             }
/*  523 */           } else if ("精养".equals(t_obj.toString())) {
/*  524 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  525 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  526 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*      */             } else {
/*  528 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*      */             }
/*      */           } else {
/*  531 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  532 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  533 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*      */             } else {
/*  535 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*      */             }
/*  537 */             this.ecardManager = ("刷卡管理员：" + t_obj.toString());
/*      */           } }
/*  539 */       } else if ((t_obj instanceof Ecard)) {
/*  540 */         Ecard e = (Ecard)t_obj;
/*  541 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/*  542 */         this.ecardManager = ("刷卡管理员：" + e.getEcard_manager());
/*  543 */         this.ecardName = (e.getEcard_name() + "刷卡表");
/*      */       }
/*      */       
/*  546 */       String sql = " from Ecard_ru e where 1=1";
/*  547 */       sql = sql + " and ru_ym ='" + ym + "'";
/*  548 */       if (qs != null) {
/*  549 */         sql = sql + " and e.ecard_ru_key in(" + qs.buildSql() + ")";
/*      */       }
/*  551 */       sql = sql + where_sql;
/*  552 */       this.sum_sql_hk = sql;
/*  553 */       sql = "select ecard_ru_key" + sql + " order by " + this.order_sql_hk;
/*  554 */       PublicUtil.getProps_value().setProperty(Ecard_ru.class.getName(), "from Ecard_ru e where e.ecard_ru_key in");
/*  555 */       List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
/*  556 */       this.ftable_hk.setObjects(list);
/*  557 */       refreshStatus();
/*  558 */       this.hk += 1;
/*      */     }
/*      */   }
/*      */   
/*      */   private void fetchData_xf(QueryScheme qs) {
/*  563 */     if (this.xf > 0) {
/*  564 */       return;
/*      */     }
/*  566 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/*  567 */       String ym = this.tf_ym.getText();
/*  568 */       if (!SysUtil.check_month(ym)) {
/*  569 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/*  570 */         return;
/*      */       }
/*  572 */       String where_sql = "";
/*  573 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/*  574 */       if ((t_obj instanceof String)) {
/*  575 */         if (!"所有卡".equals(t_obj.toString()))
/*      */         {
/*  577 */           if ("已激活".equals(t_obj.toString())) {
/*  578 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/*  579 */           } else if ("已停止".equals(t_obj.toString())) {
/*  580 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/*  581 */           } else if ("普养".equals(t_obj.toString())) {
/*  582 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  583 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  584 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*      */             } else {
/*  586 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*      */             }
/*  588 */           } else if ("中养".equals(t_obj.toString())) {
/*  589 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  590 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  591 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*      */             } else {
/*  593 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*      */             }
/*  595 */           } else if ("精养".equals(t_obj.toString())) {
/*  596 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  597 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  598 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*      */             } else {
/*  600 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*      */             }
/*      */           } else {
/*  603 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  604 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  605 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*      */             } else {
/*  607 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*      */             }
/*  609 */             this.ecardManager = ("刷卡管理员：" + t_obj.toString());
/*      */           } }
/*  611 */       } else if ((t_obj instanceof Ecard)) {
/*  612 */         Ecard e = (Ecard)t_obj;
/*  613 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/*  614 */         this.ecardManager = ("刷卡管理员：" + e.getEcard_manager());
/*  615 */         this.ecardName = (e.getEcard_name() + "刷卡表");
/*      */       }
/*  617 */       if (!"所有".equals(this.cb_fl.getSelectedItem())) {
/*  618 */         where_sql = where_sql + " and e.chu_fl='" + this.cb_fl.getSelectedItem().toString() + "'";
/*      */       }
/*      */       
/*  621 */       String sql = " from Ecard_chu e where 1=1";
/*  622 */       sql = sql + " and chu_ym ='" + ym + "'";
/*  623 */       if (qs != null) {
/*  624 */         sql = sql + " and e.ecard_chu_key in(" + qs.buildSql() + ")";
/*      */       }
/*  626 */       sql = sql + where_sql;
/*  627 */       this.sum_sql_xf = sql;
/*  628 */       sql = "select ecard_chu_key" + sql + " order by " + this.order_sql_xf;
/*  629 */       PublicUtil.getProps_value().setProperty(Ecard_chu.class.getName(), "from Ecard_chu e where e.ecard_chu_key in");
/*  630 */       List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
/*  631 */       this.ftable_xf.setObjects(list);
/*  632 */       refreshStatus();
/*  633 */       refreshStatus();
/*  634 */       this.xf += 1;
/*      */     }
/*      */   }
/*      */   
/*      */   private void fetchData_hz() {
/*  639 */     if (this.hz > 0) {
/*  640 */       return;
/*      */     }
/*  642 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/*  643 */       this.pnlHz.removeAll();
/*  644 */       String ym = this.tf_ym.getText();
/*  645 */       if (!SysUtil.check_month(ym)) {
/*  646 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/*  647 */         return;
/*      */       }
/*  649 */       String where_sql = "";
/*  650 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/*  651 */       if ((t_obj instanceof String)) {
/*  652 */         if (!"所有卡".equals(t_obj.toString()))
/*      */         {
/*  654 */           if ("已激活".equals(t_obj.toString())) {
/*  655 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/*  656 */           } else if ("已停止".equals(t_obj.toString())) {
/*  657 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/*  658 */           } else if ("普养".equals(t_obj.toString())) {
/*  659 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  660 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  661 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*      */             } else {
/*  663 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*      */             }
/*  665 */           } else if ("中养".equals(t_obj.toString())) {
/*  666 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  667 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  668 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*      */             } else {
/*  670 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*      */             }
/*  672 */           } else if ("精养".equals(t_obj.toString())) {
/*  673 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  674 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  675 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*      */             } else {
/*  677 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*      */             }
/*      */           } else {
/*  680 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/*  681 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/*  682 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*      */             } else {
/*  684 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*      */             }
/*  686 */             this.ecardManager = ("刷卡管理员：" + t_obj.toString());
/*      */           } }
/*  688 */       } else if ((t_obj instanceof Ecard)) {
/*  689 */         Ecard e = (Ecard)t_obj;
/*  690 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/*  691 */         this.ecardManager = ("刷卡管理员：" + e.getEcard_manager());
/*  692 */         this.ecardName = (e.getEcard_name() + "刷卡表");
/*      */       }
/*      */       
/*  695 */       String sql = " from Ecard_chu e where e.chu_ym='" + ym + "' ";
/*  696 */       sql = sql + where_sql;
/*  697 */       sql = sql + " order by e.ecard_key,e.chu_date,e.chu_je desc";
/*  698 */       List chuDataList = CommUtil.fetchEntities(sql);
/*      */       
/*  700 */       sql = " from Ecard_ru e where e.ru_ym='" + ym + "' ";
/*  701 */       sql = sql + where_sql;
/*  702 */       sql = sql + " order by e.ecard_key,e.ru_date";
/*  703 */       List ruDataList = CommUtil.fetchEntities(sql);
/*      */       
/*  705 */       if (ruDataList.isEmpty()) {
/*  706 */         this.pnlHz.updateUI();
/*  707 */         return;
/*      */       }
/*  709 */       Hashtable<String, List<Ecard_chu>> chuTable = new Hashtable();
/*  710 */       for (Object obj : chuDataList) {
/*  711 */         Ecard_chu c = (Ecard_chu)obj;
/*  712 */         List<Ecard_chu> cList = null;
/*  713 */         if (chuTable.containsKey(c.getEcard_key() + DateUtil.DateToStr(c.getChu_date()))) {
/*  714 */           cList = (List)chuTable.get(c.getEcard_key() + DateUtil.DateToStr(c.getChu_date()));
/*      */         } else {
/*  716 */           cList = new ArrayList();
/*  717 */           chuTable.put(c.getEcard_key() + DateUtil.DateToStr(c.getChu_date()), cList);
/*      */         }
/*  719 */         cList.add(c);
/*      */       }
/*  721 */       List<Object[]> dataList = new ArrayList();
/*  722 */       String eKey = "";
/*  723 */       Ecard_ru hzRu = null;
/*  724 */       Ecard_chu hzChu = null;
/*  725 */       int tempIndex = -1;
/*  726 */       for (Object obj : ruDataList) {
/*  727 */         Ecard_ru r = (Ecard_ru)obj;
/*  728 */         if (!r.getEcard_key().equals(eKey)) {
/*  729 */           eKey = r.getEcard_key();
/*  730 */           if (tempIndex > -1) {
/*  731 */             Object[] zobjs = { "", "", "", "" };
/*  732 */             if (hzRu != null) {
/*  733 */               zobjs[0] = ("总汇款：" + hzRu.getRu_zonge());
/*      */             }
/*  735 */             if (hzChu != null) {
/*  736 */               zobjs[1] = ("手续费：" + hzChu.getChu_sxf());
/*  737 */               zobjs[2] = ("到账：" + (hzChu.getChu_zonge().floatValue() - hzChu.getChu_sxf()));
/*      */             }
/*  739 */             dataList.add(tempIndex, zobjs);
/*      */           }
/*  741 */           dataList.add(new Object[4]);
/*  742 */           Object[] hobjs = { r.getEcard_name(), (r.getEcard_code() != null) && (r.getEcard_code().length() > 4) ? r.getEcard_code().substring(r.getEcard_code().length() - 4, r.getEcard_code().length()) : r.getEcard_code(), r.getEcard_bank(), "" };
/*      */           
/*      */ 
/*  745 */           dataList.add(hobjs);
/*  746 */           tempIndex = dataList.size();
/*  747 */           hzRu = new Ecard_ru();
/*  748 */           hzRu.setRu_zonge(0);
/*  749 */           hzChu = new Ecard_chu();
/*  750 */           hzChu.setChu_sxf(0.0F);
/*  751 */           hzChu.setChu_zonge(Float.valueOf(0.0F));
/*      */         }
/*  753 */         Object[] objs = new Object[5];
/*  754 */         objs[0] = DateUtil.DateToStr(r.getRu_date());
/*  755 */         objs[1] = "汇款";
/*  756 */         objs[2] = r.getRu_je();
/*  757 */         objs[3] = "";
/*  758 */         objs[4] = r.getEcard_ru_key();
/*  759 */         hzRu.setRu_zonge(hzRu.getRu_zonge() + r.getRu_je().intValue());
/*  760 */         dataList.add(objs);
/*  761 */         List<Ecard_chu> chuList = (List)chuTable.get(r.getEcard_key() + DateUtil.DateToStr(r.getRu_date()));
/*  762 */         if (chuList != null) {
/*  763 */           for (Ecard_chu c : chuList) {
/*  764 */             Object[] cobjs = new Object[5];
/*  765 */             cobjs[0] = "";
/*  766 */             cobjs[2] = c.getChu_je();
/*  767 */             cobjs[3] = "囗交易成功";
/*  768 */             cobjs[4] = c.getEcard_chu_key();
/*  769 */             hzChu.setChu_zonge(Float.valueOf(hzChu.getChu_zonge().floatValue() + c.getChu_je().floatValue()));
/*  770 */             float tempFl = SysUtil.objToFloat(c.getChu_fl()).floatValue();
/*  771 */             cobjs[1] = ("【机器：" + c.getEpos_code() + "/" + c.getEpos_name() + "(" + SysUtil.round(tempFl * 100.0F, 2) + "%)】");
/*  772 */             c.setChu_sxf(tempFl * c.getChu_je().floatValue());
/*  773 */             hzChu.setChu_sxf(hzChu.getChu_sxf() + c.getChu_sxf());
/*  774 */             dataList.add(cobjs);
/*      */           }
/*      */         }
/*      */       }
/*  778 */       if (tempIndex > -1) {
/*  779 */         Object[] zobjs = { "", "", "", "" };
/*  780 */         if (hzRu != null) {
/*  781 */           zobjs[0] = ("总汇款：" + hzRu.getRu_zonge());
/*      */         }
/*  783 */         if (hzChu != null) {
/*  784 */           zobjs[1] = ("手续费：" + hzChu.getChu_sxf());
/*  785 */           zobjs[2] = ("到账：" + (hzChu.getChu_zonge().floatValue() - hzChu.getChu_sxf()));
/*      */         }
/*  787 */         dataList.add(tempIndex, zobjs);
/*      */       }
/*  789 */       if (dataList.size() > 0) {
/*  790 */         dataList.remove(0);
/*      */       }
/*      */       
/*  793 */       List headerList = new ArrayList();
/*  794 */       headerList.add("日期");
/*  795 */       headerList.add("项目");
/*  796 */       headerList.add("金额");
/*  797 */       headerList.add("状态");
/*  798 */       this.ftable3 = new FTable(headerList);
/*  799 */       this.ftable3.setObjects(dataList);
/*  800 */       this.ftable3.getColumnModel().getColumn(1).setPreferredWidth(200);
/*  801 */       this.ftable3.getColumnModel().getColumn(1).setMaxWidth(200);
/*  802 */       this.ftable3.updateUI();
/*      */       
/*  804 */       this.pnlHz.setLayout(new BorderLayout());
/*  805 */       this.pnlHz.add(this.ftable3, "Center");
/*  806 */       this.pnlHz.updateUI();
/*  807 */       this.ftable3.setRight_allow_flag(false);
/*  808 */       refreshStatus();
/*  809 */       this.hz += 1;
/*      */     }
/*      */   }
/*      */   
/*      */   private void buildDataRu(String str, Ecard e) {
/*  814 */     if (JOptionPane.showConfirmDialog(null, "确定生成信用卡（" + str + "）" + this.tf_ym.getText() + "的数据？", "询问", 2, 3) != 0)
/*      */     {
/*  816 */       return;
/*      */     }
/*  818 */     List<String> keys = new ArrayList();
/*  819 */     if (e != null) {
/*  820 */       keys.add(e.getEcard_key());
/*  821 */       str = "指定卡";
/*      */     }
/*  823 */     ValidateSQLResult result = org.jhrcore.iservice.impl.EcardImpl.calcHuiKuan(this.tf_ym.getText(), str, keys);
/*  824 */     if (result.getResult() == 0) {
/*  825 */       JOptionPane.showMessageDialog(null, "生成数据成功");
/*  826 */       this.hz = 0;
/*  827 */       this.hk = 0;
/*  828 */       this.xf = 0;
/*  829 */       fetchData();
/*      */     } else {
/*  831 */       MsgUtil.showHRSaveErrorMsg(result);
/*      */     }
/*      */   }
/*      */   
/*      */   private void deleteAllData(Ecard e) {
/*  836 */     if (JOptionPane.showConfirmDialog(null, "确定删除信用卡（" + e.toString() + "）" + this.tf_ym.getText() + "的数据？", "询问", 2, 3) != 0)
/*      */     {
/*  838 */       return;
/*      */     }
/*  840 */     String sql = "delete from Ecard_ru where ecard_key='" + e.getEcard_key() + "' and ru_ym='" + this.tf_ym.getText() + "';";
/*  841 */     sql = sql + "delete from Ecard_chu where ecard_key='" + e.getEcard_key() + "' and chu_ym='" + this.tf_ym.getText() + "'";
/*  842 */     ValidateSQLResult result = CommUtil.excuteSQLs(sql, ";");
/*  843 */     if (result.getResult() == 0) {
/*  844 */       JOptionPane.showMessageDialog(null, "删除数据成功");
/*  845 */       this.hz = 0;
/*  846 */       this.hk = 0;
/*  847 */       this.xf = 0;
/*  848 */       fetchData();
/*      */     } else {
/*  850 */       MsgUtil.showHRSaveErrorMsg(result);
/*      */     }
/*      */   }
/*      */   
/*      */   private void buildDataChu(String str, Ecard e) {
/*  855 */     if (JOptionPane.showConfirmDialog(null, "确定生成信用卡（" + str + "）" + this.tf_ym.getText() + "的消费数据？", "询问", 2, 3) != 0)
/*      */     {
/*  857 */       return;
/*      */     }
/*  859 */     List<String> keys = new ArrayList();
/*  860 */     if (e != null) {
/*  861 */       keys.add(e.getEcard_key());
/*  862 */       str = "指定卡";
/*      */     }
/*  864 */     ValidateSQLResult result = org.jhrcore.iservice.impl.EcardImpl.calcXiaoFei(this.tf_ym.getText(), str, keys);
/*  865 */     if (result.getResult() == 0) {
/*  866 */       JOptionPane.showMessageDialog(null, "生成数据成功");
/*  867 */       this.hz = 0;
/*  868 */       this.xf = 0;
/*  869 */       fetchData();
/*      */     } else {
/*  871 */       MsgUtil.showHRSaveErrorMsg(result);
/*      */     }
/*      */   }
/*      */   
/*      */   private void refreshStatus() {
/*  876 */     if (this.tabPanel.getSelectedIndex() == 1) {
/*  877 */       ContextManager.setStatusBar(this.ftable_hk.getObjects().size());
/*  878 */     } else if (this.tabPanel.getSelectedIndex() == 2) {
/*  879 */       ContextManager.setStatusBar(this.ftable_xf.getObjects().size());
/*      */     } else {
/*  881 */       ContextManager.setStatusBar(this.ftable3.getObjects().size());
/*      */     }
/*      */   }
/*      */   
/*      */   private JSplitPane jSplitPane1;
/*      */   private JSplitPane jSplitPane2;
/*      */   private JTabbedPane jTabbedPane1;
/*      */   private JPanel pnlBeanCard;
/*      */   private JPanel pnlCard;
/*      */   private JPanel pnlHk;
/*      */   
/*      */   private void initComponents()
/*      */   {
/*  894 */     this.jSplitPane1 = new JSplitPane();
/*  895 */     this.jPanel1 = new JPanel();
/*  896 */     this.toolBar = new JToolBar();
/*  897 */     this.btnHk = new JButton();
/*  898 */     this.btnXf = new JButton();
/*  899 */     this.btnEdit = new JButton();
/*  900 */     this.jLabel1 = new JLabel();
/*  901 */     this.tf_ym = new JTextField();
/*  902 */     this.jLabel2 = new JLabel();
/*  903 */     this.cb_fl = new JComboBox();
/*  904 */     this.btnSearch = new JButton();
/*  905 */     this.btnExcel = new JButton();
/*  906 */     this.jSplitPane2 = new JSplitPane();
/*  907 */     this.pnlTable = new JPanel();
/*  908 */     this.tabPanel = new JTabbedPane();
/*  909 */     this.pnlHz = new JPanel();
/*  910 */     this.pnlHk = new JPanel();
/*  911 */     this.pnlXf = new JPanel();
/*  912 */     this.jPanel3 = new JPanel();
/*  913 */     this.jTabbedPane1 = new JTabbedPane();
/*  914 */     this.pnlBeanCard = new JPanel();
/*  915 */     this.pnlCard = new JPanel();
/*      */     
/*  917 */     this.jSplitPane1.setDividerLocation(200);
/*  918 */     this.jSplitPane1.setOneTouchExpandable(true);
/*      */     
/*  920 */     this.toolBar.setFloatable(false);
/*  921 */     this.toolBar.setRollover(true);
/*      */     
/*  923 */     this.btnHk.setText("生成数据");
/*  924 */     this.btnHk.setFocusable(false);
/*  925 */     this.btnHk.setHorizontalTextPosition(0);
/*  926 */     this.btnHk.setVerticalTextPosition(3);
/*  927 */     this.toolBar.add(this.btnHk);
/*      */     
/*  929 */     this.btnXf.setText("生成消费数据");
/*  930 */     this.btnXf.setFocusable(false);
/*  931 */     this.btnXf.setHorizontalTextPosition(0);
/*  932 */     this.btnXf.setVerticalTextPosition(3);
/*  933 */     this.toolBar.add(this.btnXf);
/*      */     
/*  935 */     this.btnEdit.setText("微调");
/*  936 */     this.btnEdit.setFocusable(false);
/*  937 */     this.btnEdit.setHorizontalTextPosition(0);
/*  938 */     this.btnEdit.setVerticalTextPosition(3);
/*  939 */     this.toolBar.add(this.btnEdit);
/*      */     
/*  941 */     this.jLabel1.setText(" 年月：");
/*  942 */     this.toolBar.add(this.jLabel1);
/*      */     
/*  944 */     this.tf_ym.setText("201601");
/*  945 */     this.tf_ym.setMaximumSize(new Dimension(60, Integer.MAX_VALUE));
/*  946 */     this.tf_ym.setMinimumSize(new Dimension(60, 21));
/*  947 */     this.tf_ym.setName("");
/*  948 */     this.tf_ym.setPreferredSize(new Dimension(60, 21));
/*  949 */     this.toolBar.add(this.tf_ym);
/*      */     
/*  951 */     this.jLabel2.setText(" 费率：");
/*  952 */     this.toolBar.add(this.jLabel2);
/*      */     
/*  954 */     this.cb_fl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "所有", "0.0038", "0.0078", "0.0125" }));
/*  955 */     this.cb_fl.setMaximumSize(new Dimension(75, 21));
/*  956 */     this.cb_fl.setMinimumSize(new Dimension(75, 21));
/*  957 */     this.cb_fl.setPreferredSize(new Dimension(75, 21));
/*  958 */     this.toolBar.add(this.cb_fl);
/*      */     
/*  960 */     this.btnSearch.setText("查询");
/*  961 */     this.btnSearch.setFocusable(false);
/*  962 */     this.btnSearch.setHorizontalTextPosition(0);
/*  963 */     this.btnSearch.setVerticalTextPosition(3);
/*  964 */     this.toolBar.add(this.btnSearch);
/*      */     
/*  966 */     this.btnExcel.setText("导出Excel");
/*  967 */     this.btnExcel.setFocusable(false);
/*  968 */     this.btnExcel.setHorizontalTextPosition(0);
/*  969 */     this.btnExcel.setVerticalTextPosition(3);
/*  970 */     this.toolBar.add(this.btnExcel);
/*      */     
/*  972 */     this.jSplitPane2.setDividerLocation(340);
/*  973 */     this.jSplitPane2.setDividerSize(3);
/*  974 */     this.jSplitPane2.setOrientation(0);
/*      */     
/*  976 */     this.pnlHz.setLayout(new BorderLayout());
/*  977 */     this.tabPanel.addTab("数据汇总", this.pnlHz);
/*      */     
/*  979 */     this.pnlHk.setLayout(new BorderLayout());
/*  980 */     this.tabPanel.addTab("汇款数据", this.pnlHk);
/*      */     
/*  982 */     this.pnlXf.setLayout(new BorderLayout());
/*  983 */     this.tabPanel.addTab("消费数据", this.pnlXf);
/*      */     
/*  985 */     GroupLayout pnlTableLayout = new GroupLayout(this.pnlTable);
/*  986 */     this.pnlTable.setLayout(pnlTableLayout);
/*  987 */     pnlTableLayout.setHorizontalGroup(pnlTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.tabPanel));
/*      */     
/*      */ 
/*      */ 
/*  991 */     pnlTableLayout.setVerticalGroup(pnlTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.tabPanel));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  996 */     this.jSplitPane2.setTopComponent(this.pnlTable);
/*      */     
/*  998 */     this.pnlBeanCard.setLayout(new BorderLayout());
/*  999 */     this.jTabbedPane1.addTab("信用卡信息", this.pnlBeanCard);
/*      */     
/* 1001 */     GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
/* 1002 */     this.jPanel3.setLayout(jPanel3Layout);
/* 1003 */     jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*      */     
/*      */ 
/*      */ 
/* 1007 */     jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1012 */     this.jSplitPane2.setRightComponent(this.jPanel3);
/*      */     
/* 1014 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 1015 */     this.jPanel1.setLayout(jPanel1Layout);
/* 1016 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, -1, 32767).addComponent(this.jSplitPane2));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1021 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.toolBar, -2, 25, -2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSplitPane2)));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1029 */     this.jSplitPane1.setRightComponent(this.jPanel1);
/*      */     
/* 1031 */     this.pnlCard.setLayout(new BorderLayout());
/* 1032 */     this.jSplitPane1.setLeftComponent(this.pnlCard);
/*      */     
/* 1034 */     GroupLayout layout = new GroupLayout(this);
/* 1035 */     setLayout(layout);
/* 1036 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 644, 32767));
/*      */     
/*      */ 
/*      */ 
/* 1040 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 418, 32767));
/*      */   }
/*      */   
/*      */   private JPanel pnlHz;
/*      */   private JPanel pnlTable;
/*      */   private JPanel pnlXf;
/*      */   private JTabbedPane tabPanel;
/*      */   private JTextField tf_ym;
/*      */   private JToolBar toolBar;
/*      */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardPlanPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */