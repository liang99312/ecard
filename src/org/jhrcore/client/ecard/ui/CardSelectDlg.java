/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import com.foundercy.pf.control.listener.IPickFieldOrderListener;
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.entity.query.QueryScheme;
/*     */ import org.jhrcore.entity.showstyle.ShowScheme;
/*     */ import org.jhrcore.rebuild.EntityBuilder;
/*     */ import org.jhrcore.util.ComponentUtil;
/*     */ import org.jhrcore.util.PublicUtil;
/*     */ import org.jhrcore.util.SysUtil;
/*     */ 
/*     */ public class CardSelectDlg extends JDialog
/*     */ {
/*     */   private FTable ftable;
/*     */   private FTable ftable2;
/*  41 */   private List<TempFieldInfo> all_infos = new ArrayList();
/*  42 */   private List<TempFieldInfo> default_infos = new ArrayList();
/*  43 */   private String order_sql = "ecard_code";
/*  44 */   private JLabel jLabel7 = new JLabel(" 查找：");
/*  45 */   private JTextField comBoxSearch = new JTextField();
/*  46 */   private JCheckBox chbCurColumn = new JCheckBox("当前列", false);
/*  47 */   private JButton btnSearch = new JButton("", org.jhrcore.util.ImageUtil.getSearchIcon());
/*  48 */   private List<String> select_keys = new ArrayList();
/*  49 */   private boolean isOk = false;
/*     */   private JButton btnAdd;
/*     */   
/*  52 */   public boolean isOk() { return this.isOk; }
/*     */   
/*     */   private JButton btnCancel;
/*     */   public List<String> getSelectKeys() {
/*  56 */     return this.select_keys;
/*     */   }
/*     */   
/*     */   public CardSelectDlg(Frame parent, boolean modal) {
/*  60 */     super(parent, modal);
/*  61 */     initComponents();
/*     */   }
/*     */   
/*     */   public CardSelectDlg() {
/*  65 */     initComponents();
/*  66 */     initOthers();
/*  67 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  71 */     initTools();
/*  72 */     this.ftable = new FTable(Ecard.class, true, true, true);
/*  73 */     this.ftable2 = new FTable(Ecard.class, true, true, true);
/*  74 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Ecard.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*  75 */     for (TempFieldInfo tfi : shift_infos) {
/*  76 */       this.default_infos.add(tfi);
/*  77 */       this.all_infos.add(tfi);
/*     */     }
/*  79 */     this.ftable.setAll_fields(this.all_infos, this.default_infos, new ArrayList(), "");
/*  80 */     this.ftable2.setAll_fields(this.all_infos, this.default_infos, new ArrayList(), "");
/*  81 */     this.ftable.setRight_allow_flag(true);
/*  82 */     this.pnlTable.add(this.ftable);
/*  83 */     this.pnlTable2.add(this.ftable2);
/*  84 */     fetchMainData(null, "");
/*     */   }
/*     */   
/*     */   private void initTools() {
/*  88 */     this.toolBar.add(this.jLabel7);
/*  89 */     this.toolBar.add(this.comBoxSearch);
/*  90 */     this.toolBar.add(this.btnSearch);
/*  91 */     this.toolBar.add(this.chbCurColumn);
/*  92 */     ComponentUtil.setSize(this.comBoxSearch, 120, 22);
/*  93 */     ComponentUtil.setSize(this.btnSearch, 22, 22);
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/*  97 */     ActionListener search_listener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 101 */         CardSelectDlg.this.doQuickSearch(CardSelectDlg.this.comBoxSearch.getText());
/*     */       }
/* 103 */     };
/* 104 */     this.btnSearch.addActionListener(search_listener);
/* 105 */     this.comBoxSearch.addActionListener(search_listener);
/* 106 */     this.cb_state.addItemListener(new java.awt.event.ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 110 */         CardSelectDlg.this.fetchMainData(CardSelectDlg.this.ftable.getCur_query_scheme(), null);
/*     */       }
/* 112 */     });
/* 113 */     this.ftable.addPickFieldOrderListener(new IPickFieldOrderListener()
/*     */     {
/*     */       public void pickOrder(ShowScheme showScheme)
/*     */       {
/* 117 */         CardSelectDlg.this.order_sql = SysUtil.getSQLOrderString(showScheme, CardSelectDlg.this.order_sql, CardSelectDlg.this.all_infos);
/* 118 */         CardSelectDlg.this.fetchMainData(CardSelectDlg.this.ftable.getCur_query_scheme(), null);
/*     */       }
/* 120 */     });
/* 121 */     this.ftable.addPickQueryExListener(new com.foundercy.pf.control.listener.IPickQueryExListener()
/*     */     {
/*     */       public void pickQuery(QueryScheme qs)
/*     */       {
/* 125 */         CardSelectDlg.this.fetchMainData(qs, null);
/*     */       }
/* 127 */     });
/* 128 */     this.btnAdd.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 132 */         if (CardSelectDlg.this.ftable.getSelectObjects().isEmpty()) {
/* 133 */           JOptionPane.showMessageDialog(null, "请选择信用卡");
/* 134 */           return;
/*     */         }
/* 136 */         for (Object obj : CardSelectDlg.this.ftable.getSelectObjects()) {
/* 137 */           Ecard ecard = (Ecard)obj;
/* 138 */           CardSelectDlg.this.select_keys.add(ecard.getEcard_key());
/*     */         }
/* 140 */         CardSelectDlg.this.ftable2.addObjects(CardSelectDlg.this.ftable.getSelectObjects());
/* 141 */         CardSelectDlg.this.ftable.deleteSelectedRows();
/*     */       }
/*     */       
/* 144 */     });
/* 145 */     this.btnDel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 149 */         if (CardSelectDlg.this.ftable2.getSelectObjects().isEmpty()) {
/* 150 */           JOptionPane.showMessageDialog(null, "请选择信用卡");
/* 151 */           return;
/*     */         }
/* 153 */         for (Object obj : CardSelectDlg.this.ftable2.getSelectObjects()) {
/* 154 */           Ecard ecard = (Ecard)obj;
/* 155 */           CardSelectDlg.this.select_keys.remove(ecard.getEcard_key());
/*     */         }
/* 157 */         CardSelectDlg.this.ftable2.deleteSelectedRows();
/*     */       }
/* 159 */     });
/* 160 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 164 */         CardSelectDlg.this.isOk = false;
/* 165 */         CardSelectDlg.this.dispose();
/*     */       }
/* 167 */     });
/* 168 */     this.btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 172 */         if (CardSelectDlg.this.select_keys.isEmpty()) {
/* 173 */           JOptionPane.showMessageDialog(null, "没有选择信用卡");
/* 174 */           return;
/*     */         }
/* 176 */         CardSelectDlg.this.isOk = true;
/* 177 */         CardSelectDlg.this.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void doQuickSearch(String text) {
/* 183 */     if ((text == null) || (text.trim().equals(""))) {
/* 184 */       return;
/*     */     }
/* 186 */     text = SysUtil.getQuickSearchText(text);
/* 187 */     String s_where = "";
/* 188 */     if (this.chbCurColumn.isSelected()) {
/* 189 */       s_where = this.ftable.getQuickSearchSQL("Ecard", text);
/*     */     } else {
/* 191 */       s_where = "(upper(ecard_code) like '" + text + "%' or upper(ecard_name) like '" + text + "%')";
/*     */     }
/* 193 */     fetchMainData(this.ftable.getCur_query_scheme(), s_where);
/*     */   }
/*     */   
/*     */   private void fetchMainData(QueryScheme qs, String s_where) {
/* 197 */     String state = this.cb_state.getSelectedItem().toString();
/* 198 */     this.ftable.setCur_query_scheme(qs);
/* 199 */     String sql = " from Ecard where 1=1";
/* 200 */     if (qs != null) {
/* 201 */       sql = sql + " and Ecard.ecard_key in(" + qs.buildSql() + ")";
/*     */     }
/* 203 */     if ((s_where != null) && (!s_where.trim().equals(""))) {
/* 204 */       sql = sql + " and (" + s_where + ")";
/*     */     }
/* 206 */     if (!state.equals("所有卡")) {
/* 207 */       sql = sql + " and ecard_state = '" + state + "'";
/*     */     }
/* 209 */     sql = "select ecard_key" + sql + " order by " + this.order_sql;
/* 210 */     PublicUtil.getProps_value().setProperty(Ecard.class.getName(), "from Ecard n where n.ecard_key in");
/* 211 */     List list = CommUtil.selectSQL(org.jhrcore.util.DbUtil.tranSQL(sql));
/* 212 */     list.removeAll(this.select_keys);
/* 213 */     this.ftable.setObjects(list);
/*     */   }
/*     */   
/*     */   private JButton btnDel;
/*     */   private JButton btnOk;
/*     */   private JComboBox cb_state;
/*     */   private JLabel jLabel1;
/*     */   private JPanel jPanel1;
/*     */   private JPanel pnlTable;
/*     */   private JPanel pnlTable2;
/*     */   private JToolBar toolBar;
/*     */   private void initComponents()
/*     */   {
/* 226 */     this.jPanel1 = new JPanel();
/* 227 */     this.toolBar = new JToolBar();
/* 228 */     this.jLabel1 = new JLabel();
/* 229 */     this.cb_state = new JComboBox();
/* 230 */     this.pnlTable = new JPanel();
/* 231 */     this.btnAdd = new JButton();
/* 232 */     this.btnDel = new JButton();
/* 233 */     this.pnlTable2 = new JPanel();
/* 234 */     this.btnCancel = new JButton();
/* 235 */     this.btnOk = new JButton();
/*     */     
/* 237 */     setDefaultCloseOperation(2);
/* 238 */     setModal(true);
/*     */     
/* 240 */     this.toolBar.setFloatable(false);
/* 241 */     this.toolBar.setRollover(true);
/* 242 */     this.toolBar.setMaximumSize(new Dimension(1240, 24));
/* 243 */     this.toolBar.setMinimumSize(new Dimension(106, 24));
/* 244 */     this.toolBar.setPreferredSize(new Dimension(106, 24));
/*     */     
/* 246 */     this.jLabel1.setText("状态：");
/* 247 */     this.toolBar.add(this.jLabel1);
/*     */     
/* 249 */     this.cb_state.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "已激活", "已停止", "所有卡" }));
/* 250 */     this.cb_state.setMaximumSize(new Dimension(80, 23));
/* 251 */     this.toolBar.add(this.cb_state);
/*     */     
/* 253 */     this.pnlTable.setPreferredSize(new Dimension(300, 196));
/* 254 */     this.pnlTable.setLayout(new BorderLayout());
/*     */     
/* 256 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 257 */     this.jPanel1.setLayout(jPanel1Layout);
/* 258 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, 579, 32767).addComponent(this.pnlTable, -1, -1, 32767));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 263 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.toolBar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.pnlTable, -1, -1, 32767)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 271 */     this.btnAdd.setText("添加");
/*     */     
/* 273 */     this.btnDel.setText("删除");
/*     */     
/* 275 */     this.pnlTable2.setBorder(javax.swing.BorderFactory.createTitledBorder("已选信用卡"));
/* 276 */     this.pnlTable2.setLayout(new BorderLayout());
/*     */     
/* 278 */     this.btnCancel.setText("关闭");
/*     */     
/* 280 */     this.btnOk.setText("确定 ");
/*     */     
/* 282 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 283 */     getContentPane().setLayout(layout);
/* 284 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel1, -1, -1, 32767).addGroup(layout.createSequentialGroup().addGap(75, 75, 75).addComponent(this.btnAdd).addGap(42, 42, 42).addComponent(this.btnDel).addContainerGap(-1, 32767)).addComponent(this.pnlTable2, -1, -1, 32767).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.btnOk).addGap(76, 76, 76).addComponent(this.btnCancel).addGap(96, 96, 96)));
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
/* 301 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.btnDel).addComponent(this.btnAdd)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.pnlTable2, -1, 139, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnCancel).addComponent(this.btnOk)).addGap(6, 6, 6)));
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
/* 318 */     pack();
/*     */   }
/*     */ }

