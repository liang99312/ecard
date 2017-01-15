/*     */ package org.jhrcore.client.ecard;
/*     */ 
/*     */ import com.foundercy.pf.control.listener.IPickColumnSumListener;
/*     */ import com.foundercy.pf.control.listener.IPickFieldOrderListener;
/*     */ import com.foundercy.pf.control.listener.IPickFieldSetListener;
/*     */ import com.foundercy.pf.control.listener.IPickQueryExListener;
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
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
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding;
/*     */ import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
/*     */ import org.jdesktop.beansbinding.Binding;
/*     */ import org.jdesktop.swingbinding.SwingBindings;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.client.UserContext;
/*     */ import org.jhrcore.client.ecard.ui.AddEposPanel;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Epos;
/*     */ import org.jhrcore.entity.query.QueryScheme;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.entity.showstyle.ShowScheme;
/*     */ import org.jhrcore.msg.CommMsg;
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
/*     */ public class EcardPosPanel extends JPanel implements IModulePanel
/*     */ {
/*  66 */   private JButton btnAdd = new JButton("新增");
/*  67 */   private JButton btnEdit = new JButton("编辑");
/*  68 */   private JButton btnView = new JButton("浏览");
/*  69 */   private JButton btnSave = new JButton("保存");
/*  70 */   private JButton btnCancel = new JButton("取消");
/*  71 */   private JButton btnDel = new JButton("删除");
/*  72 */   private JButton btnTool = new JButton("工具");
/*  73 */   private JLabel jLabel7 = new JLabel(" 查找：");
/*  74 */   private JTextField comBoxSearch = new JTextField();
/*  75 */   private JCheckBox chbCurColumn = new JCheckBox("当前列", false);
/*  76 */   private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
/*  77 */   private JMenu mEditWay = new JMenu("编辑方式");
/*  78 */   private JMenuItem miCardEdit = new JMenuItem("卡片编辑");
/*  79 */   private JMenuItem miTableEdit = new JMenuItem("网格编辑");
/*  80 */   private JMenuItem miExport = new JMenuItem("导出Excel");
/*  81 */   private List pay_system_list = new ArrayList();
/*  82 */   private JPopupMenu menu = new JPopupMenu();
/*     */   private FTable ftable;
/*  84 */   private List<TempFieldInfo> all_infos = new ArrayList();
/*  85 */   private List<TempFieldInfo> default_infos = new ArrayList();
/*  86 */   private List<TempFieldInfo> default_orders = new ArrayList();
/*  87 */   private String order_sql = "epos_code";
/*  88 */   private Object curEpos = null;
/*  89 */   private BeanPanel beanPanel = new BeanPanel();
/*  90 */   private boolean editable = false;
/*  91 */   private boolean editStyle = false;
/*     */   public static final String module_code = "EposMng";
/*     */   private String sum_sql;
/*     */   private Binding cb_binding;
/*     */   private JComboBox cb_fl;
/*     */   private JLabel jLabel1;
/*     */   
/*     */   public EcardPosPanel()
/*     */   {
/* 100 */     initComponents();
/* 101 */     initOthers();
/* 102 */     setupEvents();
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
/* 114 */     this.toolBar = new JToolBar();
/* 115 */     this.jLabel1 = new JLabel();
/* 116 */     this.cb_fl = new JComboBox();
/* 117 */     this.jtpMain = new JTabbedPane();
/* 118 */     this.jspRight = new JSplitPane();
/* 119 */     this.pnlTable = new JPanel();
/* 120 */     this.pnlCard = new JPanel();
/*     */     
/* 122 */     this.toolBar.setFloatable(false);
/* 123 */     this.toolBar.setRollover(true);
/* 124 */     this.toolBar.setMaximumSize(new Dimension(1240, 24));
/* 125 */     this.toolBar.setMinimumSize(new Dimension(106, 24));
/* 126 */     this.toolBar.setPreferredSize(new Dimension(106, 24));
/*     */     
/* 128 */     this.jLabel1.setText("费率：");
/* 129 */     this.toolBar.add(this.jLabel1);
/*     */     
/* 131 */     this.cb_fl.setModel(new DefaultComboBoxModel(new String[] { "已激活", "已停止", "所有卡" }));
/* 132 */     this.cb_fl.setMaximumSize(new Dimension(80, 23));
/* 133 */     this.toolBar.add(this.cb_fl);
/*     */     
/* 135 */     this.jspRight.setDividerLocation(250);
/* 136 */     this.jspRight.setOrientation(0);
/* 137 */     this.jspRight.setOneTouchExpandable(true);
/*     */     
/* 139 */     this.pnlTable.setLayout(new BorderLayout());
/* 140 */     this.jspRight.setTopComponent(this.pnlTable);
/*     */     
/* 142 */     this.pnlCard.setLayout(new BorderLayout());
/* 143 */     this.jspRight.setRightComponent(this.pnlCard);
/*     */     
/* 145 */     this.jtpMain.addTab("基本信息", this.jspRight);
/*     */     
/* 147 */     GroupLayout layout = new GroupLayout(this);
/* 148 */     setLayout(layout);
/* 149 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, -1, 32767).addComponent(this.jtpMain, -1, 680, 32767));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 154 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(1, 1, 1).addComponent(this.toolBar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jtpMain, -1, 400, 32767).addGap(1, 1, 1)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private JSplitPane jspRight;
/*     */   
/*     */ 
/*     */   private JTabbedPane jtpMain;
/*     */   
/*     */ 
/*     */   private JPanel pnlCard;
/*     */   
/*     */ 
/*     */   private JPanel pnlTable;
/*     */   
/*     */ 
/*     */   private JToolBar toolBar;
/*     */   
/*     */   private void initOthers()
/*     */   {
/* 175 */     bindingCb();
/* 176 */     initToolBar();
/* 177 */     this.ftable = new FTable(Epos.class, true, true, true, "EcardPosPanel");
/* 178 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Epos.class, EntityBuilder.COMM_FIELD_VISIBLE);
/* 179 */     for (TempFieldInfo tfi : shift_infos) {
/* 180 */       this.default_infos.add(tfi);
/* 181 */       this.all_infos.add(tfi);
/*     */     }
/* 183 */     this.ftable.setAll_fields(this.all_infos, this.default_infos, this.default_orders, "EcardPosPanel");
/* 184 */     this.order_sql = SysUtil.getSQLOrderString(this.ftable.getCurOrderScheme(), this.order_sql, this.all_infos);
/* 185 */     this.ftable.setRight_allow_flag(true);
/* 186 */     this.pnlTable.add(this.ftable);
/* 187 */     this.pnlCard.add(new JScrollPane(this.beanPanel));
/* 188 */     this.beanPanel.setColumns(3);
/*     */   }
/*     */   
/*     */   private void bindingCb() {
/* 192 */     List flList = CommUtil.selectSQL("select distinct epos_fei from Epos");
/* 193 */     flList.add("所有");
/* 194 */     if (this.cb_binding == null) {
/* 195 */       this.cb_binding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ, flList, this.cb_fl);
/*     */     } else {
/* 197 */       this.cb_binding.unbind();
/*     */     }
/* 199 */     this.cb_binding.bind();
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/* 203 */     this.cb_fl.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 207 */         EcardPosPanel.this.fetchMainData(EcardPosPanel.this.ftable.getCur_query_scheme(), null);
/*     */       }
/* 209 */     });
/* 210 */     this.ftable.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 214 */         CommEditAction.doRowSaveAction(EcardPosPanel.this.curEpos, EcardPosPanel.this.editable);
/* 215 */         if (EcardPosPanel.this.curEpos == EcardPosPanel.this.ftable.getCurrentRow()) {
/* 216 */           return;
/*     */         }
/* 218 */         EcardPosPanel.this.curEpos = EcardPosPanel.this.ftable.getCurrentRow();
/* 219 */         BeanPanel.refreshUIForTable(EcardPosPanel.this.ftable, EcardPosPanel.this.beanPanel, (EcardPosPanel.this.editable) && (EcardPosPanel.this.editStyle));
/* 220 */         ContextManager.setStatusBar(EcardPosPanel.this.ftable.getObjects().size());
/*     */       }
/* 222 */     });
/* 223 */     this.ftable.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 227 */         EcardPosPanel.this.order_sql = SysUtil.getSQLOrderString(showScheme, EcardPosPanel.this.order_sql, EcardPosPanel.this.all_infos);
/* 228 */         EcardPosPanel.this.fetchMainData(EcardPosPanel.this.ftable.getCur_query_scheme(), null);
/*     */       }
/* 230 */     });
/* 231 */     this.ftable.addPickQueryExListener(new IPickQueryExListener()
/*     */     {
/*     */       public void pickQuery(QueryScheme qs)
/*     */       {
/* 235 */         EcardPosPanel.this.fetchMainData(qs, null);
/*     */       }
/* 237 */     });
/* 238 */     this.ftable.addPickFieldSetListener(new IPickFieldSetListener()
/*     */     {
/*     */       public void pickField(ShowScheme showScheme)
/*     */       {
/* 242 */         BeanPanel.refreshUIForTable(EcardPosPanel.this.ftable, EcardPosPanel.this.beanPanel, (EcardPosPanel.this.editable) && (EcardPosPanel.this.editStyle));
/*     */       }
/* 244 */     });
/* 245 */     this.ftable.addPickColumnSumListener(new IPickColumnSumListener()
/*     */     {
/*     */       public String pickSumSQL()
/*     */       {
/* 249 */         return EcardPosPanel.this.sum_sql + "@sql";
/*     */       }
/*     */       
/* 252 */     });
/* 253 */     this.btnEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 257 */         EcardPosPanel.this.setEditState(true, EcardPosPanel.this.editStyle);
/*     */       }
/* 259 */     });
/* 260 */     this.btnView.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 264 */         CommEditAction.doViewAction(EcardPosPanel.this.curEpos, EcardPosPanel.this.ftable, EcardPosPanel.this.beanPanel);
/* 265 */         EcardPosPanel.this.setEditState(false, EcardPosPanel.this.editStyle);
/*     */       }
/* 267 */     });
/* 268 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 272 */         CommEditAction.doCancelAction(EcardPosPanel.this.curEpos, EcardPosPanel.this.ftable, EcardPosPanel.this.beanPanel);
/* 273 */         EcardPosPanel.this.setEditState(EcardPosPanel.this.editable, EcardPosPanel.this.editStyle);
/*     */       }
/* 275 */     });
/* 276 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 280 */         CommEditAction.doSaveAction(EcardPosPanel.this.curEpos, EcardPosPanel.this.ftable, EcardPosPanel.this.beanPanel);
/* 281 */         EcardPosPanel.this.setEditState(EcardPosPanel.this.editable, EcardPosPanel.this.editStyle);
/*     */       }
/* 283 */     });
/* 284 */     this.btnDel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 288 */         EcardPosPanel.this.delObj();
/*     */       }
/* 290 */     });
/* 291 */     ActionListener search_listener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 295 */         EcardPosPanel.this.doQuickSearch(EcardPosPanel.this.comBoxSearch.getText());
/*     */       }
/* 297 */     };
/* 298 */     this.btnSearch.addActionListener(search_listener);
/* 299 */     this.comBoxSearch.addActionListener(search_listener);
/* 300 */     this.miExport.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 304 */         EcardPosPanel.this.ftable.exportData();
/*     */       }
/* 306 */     });
/* 307 */     ComponentUtil.refreshJSplitPane(this.jspRight, "EcardPosPanel.jspRight");
/* 308 */     fetchMainData(null, null);
/*     */   }
/*     */   
/*     */   private void initToolBar()
/*     */   {
/* 313 */     this.toolBar.add(this.btnAdd);
/* 314 */     this.toolBar.add(this.btnEdit);
/* 315 */     this.toolBar.add(this.btnView);
/* 316 */     this.toolBar.add(this.btnSave);
/* 317 */     this.toolBar.add(this.btnCancel);
/* 318 */     this.toolBar.add(this.btnDel);
/* 319 */     this.toolBar.addSeparator();
/* 320 */     this.toolBar.add(this.btnTool);
/* 321 */     this.toolBar.add(this.jLabel7);
/* 322 */     this.toolBar.add(this.comBoxSearch);
/* 323 */     this.toolBar.add(this.btnSearch);
/* 324 */     this.toolBar.add(this.chbCurColumn);
/*     */     
/* 326 */     this.menu.add(this.mEditWay);
/* 327 */     this.menu.addSeparator();
/* 328 */     this.menu.add(this.miExport);
/* 329 */     this.mEditWay.add(this.miTableEdit);
/* 330 */     this.mEditWay.add(this.miCardEdit);
/* 331 */     ComponentUtil.setSize(this.comBoxSearch, 120, 22);
/* 332 */     ComponentUtil.setSize(this.btnSearch, 22, 22);
/* 333 */     this.btnTool.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 337 */         EcardPosPanel.this.menu.show(EcardPosPanel.this.btnTool, 0, 25);
/*     */       }
/* 339 */     });
/* 340 */     this.btnAdd.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 344 */         AddEposPanel pnl = new AddEposPanel();
/* 345 */         ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "登记Pos机", 650, 500, false);
/* 346 */         mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */         {
/*     */           public void pickClose()
/*     */           {
/* 350 */             EcardPosPanel.this.bindingCb();
/* 351 */             EcardPosPanel.this.fetchMainData(null, null);
/*     */           }
/* 353 */         });
/* 354 */         mf.setVisible(true);
/*     */       }
/*     */       
/* 357 */     });
/* 358 */     this.miTableEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 362 */         EcardPosPanel.this.setEditState(EcardPosPanel.this.editable, false);
/*     */       }
/* 364 */     });
/* 365 */     this.miCardEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 369 */         EcardPosPanel.this.setEditState(EcardPosPanel.this.editable, true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void fetchMainData(QueryScheme qs, String s_where)
/*     */   {
/* 376 */     String state = "";
/* 377 */     if (this.cb_fl.getSelectedItem() == null) {
/* 378 */       state = "所有";
/*     */     } else {
/* 380 */       state = this.cb_fl.getSelectedItem().toString();
/*     */     }
/* 382 */     this.ftable.setCur_query_scheme(qs);
/* 383 */     String sql = " from Epos where 1=1";
/* 384 */     if (qs != null) {
/* 385 */       sql = sql + " and Epos.epos_key in(" + qs.buildSql() + ")";
/*     */     }
/* 387 */     if ((s_where != null) && (!s_where.trim().equals(""))) {
/* 388 */       sql = sql + " and (" + s_where + ")";
/*     */     }
/* 390 */     if (!state.equals("所有")) {
/* 391 */       sql = sql + " and epos_fei = '" + state + "'";
/*     */     }
/* 393 */     this.sum_sql = sql;
/* 394 */     sql = "select epos_key" + sql + " order by " + this.order_sql;
/* 395 */     PublicUtil.getProps_value().setProperty(Epos.class.getName(), "from Epos n where n.epos_key in");
/* 396 */     List list = CommUtil.selectSQL(DbUtil.tranSQL(sql));
/* 397 */     this.ftable.setObjects(list);
/* 398 */     refreshStatus();
/*     */   }
/*     */   
/*     */   private void doQuickSearch(String text) {
/* 402 */     if ((text == null) || (text.trim().equals(""))) {
/* 403 */       return;
/*     */     }
/* 405 */     text = SysUtil.getQuickSearchText(text);
/* 406 */     String s_where = "";
/* 407 */     if (this.chbCurColumn.isSelected()) {
/* 408 */       s_where = this.ftable.getQuickSearchSQL("Epos", text);
/*     */     } else {
/* 410 */       s_where = "(upper(epos_code) like '" + text + "%' or upper(ecard_name) like '" + text + "%')";
/*     */     }
/* 412 */     fetchMainData(this.ftable.getCur_query_scheme(), s_where);
/*     */   }
/*     */   
/*     */   private void setEditState(boolean editable, boolean editStyle) {
/* 416 */     this.editStyle = editStyle;
/* 417 */     this.editable = editable;
/* 418 */     ComponentUtil.setIcon(this.miTableEdit, editStyle ? "blank" : "editWay");
/* 419 */     ComponentUtil.setIcon(this.miCardEdit, editStyle ? "editWay" : "blank");
/* 420 */     this.btnEdit.setEnabled((UserContext.hasFunctionRight("EposMng.btnEdit")) && (!editable));
/* 421 */     this.btnDel.setEnabled((UserContext.hasFunctionRight("EposMng.btnEdit")) && (!editable));
/* 422 */     this.btnCancel.setEnabled((UserContext.hasFunctionRight("EposMng.btnEdit")) && (editable));
/* 423 */     this.btnSave.setEnabled((UserContext.hasFunctionRight("EposMng.btnEdit")) && (editable));
/* 424 */     this.btnView.setEnabled((UserContext.hasFunctionRight("EposMng.btnEdit")) && (editable));
/* 425 */     this.ftable.editingStopped();
/* 426 */     this.ftable.setEditable((editable) && (!editStyle));
/* 427 */     BeanPanel.refreshUIForTable(this.ftable, this.beanPanel, (editable) && (editStyle));
/*     */   }
/*     */   
/*     */   private void delObj() {
/* 431 */     List<String> keys = this.ftable.getSelectKeys();
/* 432 */     if (keys.isEmpty()) {
/* 433 */       return;
/*     */     }
/* 435 */     if (MsgUtil.showNotConfirmDialog(CommMsg.DEL_MESSAGE)) {
/* 436 */       return;
/*     */     }
/* 438 */     ValidateSQLResult result = CommUtil.deleteObjs("Epos", "epos_key", keys);
/* 439 */     if (result.getResult() == 0) {
/* 440 */       MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
/* 441 */       this.ftable.deleteSelectedRows();
/*     */     } else {
/* 443 */       MsgUtil.showHRDelErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFunctionRight()
/*     */   {
/* 449 */     ComponentUtil.setSysFuntion(this, "EposMng");
/* 450 */     this.ftable.setExportItemEnable(this.miExport.isEnabled());
/* 451 */     setEditState(false, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public void pickClose() {}
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 460 */     refreshStatus();
/*     */   }
/*     */   
/*     */   private void refreshStatus() {
/* 464 */     ContextManager.setStatusBar(this.ftable.getObjects().size());
/*     */   }
/*     */   
/*     */   public String getModuleCode()
/*     */   {
/* 469 */     return "EposMng";
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardPosPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */