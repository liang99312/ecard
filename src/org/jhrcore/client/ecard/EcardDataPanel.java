/*     */ package org.jhrcore.client.ecard;
/*     */ 
/*     */ import com.foundercy.pf.control.table.FBaseTableColumnModel;
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
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
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.client.ecard.ui.CardPanel;
/*     */ import org.jhrcore.client.ecard.ui.EditChuPanel;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.entity.ecard.Ecard_chu;
/*     */ import org.jhrcore.entity.ecard.Ecard_ru;
/*     */ import org.jhrcore.ui.BeanPanel;
/*     */ import org.jhrcore.ui.ContextManager;
/*     */ import org.jhrcore.ui.ModelFrame;
/*     */ import org.jhrcore.ui.listener.IPickWindowCloseListener;
/*     */ import org.jhrcore.util.DateUtil;
/*     */ import org.jhrcore.util.SysUtil;
/*     */ 
/*     */ public class EcardDataPanel extends JPanel
/*     */ {
/*     */   private CardPanel cardPanel;
/*     */   private FTable ftable;
/*     */   private FTable ftable2;
/*     */   private FTable ftable3;
/*     */   private Object curTreeNode;
/*  51 */   private BeanPanel ebeanPanel = new BeanPanel();
/*     */   public static final String module_code = "EcardHkPanel";
/*  53 */   private SimpleDateFormat format = new SimpleDateFormat("MM-dd");
/*  54 */   private DecimalFormat df = new DecimalFormat("###.####");
/*  55 */   private int hk = 0; private int xf = 0; private int hz = 0;
/*  56 */   private String curYm = "";
/*  57 */   private String ecardManager = "刷卡管理员：";
/*  58 */   private String ecardName = "";
/*     */   private Object curEcard_ru;
/*     */   private Object curEcard_chu;
/*     */   
/*  62 */   public EcardDataPanel() { initComponents();
/*  63 */     initOthers();
/*  64 */     setupEvents(); }
/*     */   
/*     */   private JButton btnEdit;
/*     */   
/*  68 */   private void initOthers() { this.tf_ym.setText(DateUtil.DateToStr(new Date(), "yyyyMM"));
/*  69 */     this.cardPanel = new CardPanel("");
/*  70 */     this.pnlCard.add(this.cardPanel, "Center");
/*  71 */     this.pnlBeanCard.add(new javax.swing.JScrollPane(this.ebeanPanel)); }
/*     */   
/*     */   private JButton btnExcel;
/*     */   
/*  75 */   private void setupEvents() { this.btnSearch.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  79 */         if (!curYm.equals(tf_ym.getText())) {
/*  80 */           hk = 0;
/*  81 */           xf = 0;
/*  82 */           hz = 0;
/*  83 */           curYm = tf_ym.getText();
/*     */         }
/*  85 */         fetchData();
/*     */       }
/*     */       
/*  88 */     });
/*  89 */     this.btnEdit.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  93 */         if ((tabPanel.getSelectedIndex() == 0) && 
/*  94 */           (ftable3.getCurrentRow() != null)) {
/*  95 */           Object[] obj = (Object[])ftable3.getCurrentRow();
/*  96 */           if ((obj.length == 5) && (obj[4] != null)) {
/*  97 */             if ("".equals(obj[3].toString())) {
/*  98 */               Object object = CommUtil.fetchEntityBy("from Ecard_ru where ecard_ru_key='" + obj[4].toString() + "'");
/*  99 */               if (object != null) {
/* 100 */                 curEcard_ru = object;
/* 101 */                 editRu();
/*     */               }
/*     */             } else {
/* 104 */               Object object = CommUtil.fetchEntityBy("from Ecard_chu where ecard_chu_key='" + obj[4].toString() + "'");
/* 105 */               if (object != null) {
/* 106 */                 curEcard_chu = object;
/* 107 */                 editChu();
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 115 */     });
/* 116 */     this.btnExcel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 120 */         if (tabPanel.getSelectedIndex() == 2) {
/* 121 */           ftable2.exportData(ecardManager, ecardName);
/* 122 */         } else if (tabPanel.getSelectedIndex() == 1) {
/* 123 */           ftable.exportData(ecardManager, ecardName);
/*     */         } else {
/* 125 */           ftable3.exportData(ecardManager, ecardName);
/*     */         }
/*     */       }
/* 128 */     });
/* 129 */     this.cardPanel.getCardTree().addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
/*     */     {
/*     */       public void valueChanged(TreeSelectionEvent e)
/*     */       {
/* 133 */         curTreeNode = e.getPath().getLastPathComponent();
/* 134 */         ecardManager = "";
/* 135 */         ecardName = "";
/* 136 */         if (curTreeNode != null) {
/* 137 */           if ((((DefaultMutableTreeNode)curTreeNode).getUserObject() instanceof Ecard)) {
/* 138 */             Ecard temp_card = (Ecard)((DefaultMutableTreeNode)curTreeNode).getUserObject();
/* 139 */             ebeanPanel.setBean(temp_card);
/* 140 */             ebeanPanel.bind();
/*     */           }
/* 142 */           hk = 0;
/* 143 */           xf = 0;
/* 144 */           hz = 0;
/* 145 */           fetchData();
/*     */         }
/*     */       }
/* 148 */     });
/* 149 */     this.tabPanel.addChangeListener(new javax.swing.event.ChangeListener()
/*     */     {
/*     */ 
/*     */       public void stateChanged(javax.swing.event.ChangeEvent e) {
/* 153 */         fetchData(); } }); }
/*     */   
/*     */   private JButton btnSearch;
/*     */   private JComboBox cb_fl;
/*     */   private JLabel jLabel1;
/*     */   
/* 159 */   private void editRu() { if (this.curEcard_ru == null) {
/* 160 */       return;
/*     */     }
/* 162 */     org.jhrcore.client.ecard.ui.EditBeanPanel pnl = new org.jhrcore.client.ecard.ui.EditBeanPanel(this.curEcard_ru);
/* 163 */     ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑汇款信息", 650, 500, false);
/* 164 */     mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */     {
/*     */       public void pickClose()
/*     */       {
/* 168 */         hk = 0;
/* 169 */         hz = 0;
/* 170 */         fetchData();
/*     */       }
/* 172 */     });
/* 173 */     mf.setVisible(true); }
/*     */   
/*     */   private JLabel jLabel2;
/*     */   
/* 177 */   private void editChu() { if (this.curEcard_chu == null) {
/* 178 */       return;
/*     */     }
/* 180 */     EditChuPanel pnl = new EditChuPanel(this.curEcard_chu);
/* 181 */     ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnl, true, "编辑消费信息", 650, 500, false);
/* 182 */     mf.addIPickWindowCloseListener(new IPickWindowCloseListener()
/*     */     {
/*     */       public void pickClose()
/*     */       {
/* 186 */         xf = 0;
/* 187 */         hz = 0;
/* 188 */         fetchData();
/*     */       }
/* 190 */     });
/* 191 */     mf.setVisible(true); }
/*     */   
/*     */   private JPanel jPanel1;
/*     */   
/* 195 */   private void fetchData() { this.cb_fl.setEnabled(false);
/* 196 */     if (this.tabPanel.getSelectedIndex() == 2) {
/* 197 */       fetchData_xf();
/* 198 */       this.cb_fl.setEnabled(true);
/* 199 */     } else if (this.tabPanel.getSelectedIndex() == 1) {
/* 200 */       fetchData_hk();
/*     */     } else {
/* 202 */       fetchData_hz();
/*     */     } }
/*     */   
/*     */   private JPanel jPanel3;
/*     */   private JSplitPane jSplitPane1;
/* 207 */   private void fetchData_hk() { if (this.hk > 0) {
/* 208 */       return;
/*     */     }
/* 210 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/* 211 */       this.pnlHk.removeAll();
/* 212 */       String ym = this.tf_ym.getText();
/* 213 */       if (!SysUtil.check_month(ym)) {
/* 214 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/* 215 */         return;
/*     */       }
/* 217 */       String where_sql = "";
/* 218 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/* 219 */       if ((t_obj instanceof String)) {
/* 220 */         if (!"所有卡".equals(t_obj.toString()))
/*     */         {
/* 222 */           if ("已激活".equals(t_obj.toString())) {
/* 223 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/* 224 */           } else if ("已停止".equals(t_obj.toString())) {
/* 225 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/* 226 */           } else if ("普养".equals(t_obj.toString())) {
/* 227 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 228 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 229 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*     */             } else {
/* 231 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*     */             }
/* 233 */           } else if ("中养".equals(t_obj.toString())) {
/* 234 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 235 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 236 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*     */             } else {
/* 238 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*     */             }
/* 240 */           } else if ("精养".equals(t_obj.toString())) {
/* 241 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 242 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 243 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*     */             } else {
/* 245 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*     */             }
/*     */           } else {
/* 248 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 249 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 250 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*     */             } else {
/* 252 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*     */             }
/* 254 */             this.ecardManager = ("刷卡管理员：" + t_obj.toString());
/*     */           } }
/* 256 */       } else if ((t_obj instanceof Ecard)) {
/* 257 */         Ecard e = (Ecard)t_obj;
/* 258 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/* 259 */         this.ecardManager = ("刷卡管理员：" + e.getEcard_manager());
/* 260 */         this.ecardName = (e.getEcard_name() + "刷卡表");
/*     */       }
/*     */       
/* 263 */       String sql = " from Ecard_ru e where e.ru_ym='" + ym + "' ";
/* 264 */       sql = sql + where_sql;
/* 265 */       sql = sql + " order by e.ecard_key,e.ru_date";
/* 266 */       List dataList = CommUtil.fetchEntities(sql);
/* 267 */       if (dataList.isEmpty()) {
/* 268 */         this.pnlHk.updateUI();
/* 269 */         return;
/*     */       }
/* 271 */       Date maxDate = null;
/* 272 */       Date minDate = DateUtil.StrToDate(ym + "01", "yyyyMMdd");
/* 273 */       List keys = new ArrayList();
/* 274 */       Hashtable<String, Integer> djTable = new Hashtable();
/* 275 */       List<Ecard_ru> ruList = new ArrayList();
/* 276 */       for (Object obj : dataList) {
/* 277 */         Ecard_ru ru = (Ecard_ru)obj;
/* 278 */         String d_key = DateUtil.DateToStr(ru.getRu_date(), "yyyy-MM-dd");
/* 279 */         if (djTable.containsKey(d_key)) {
/* 280 */           Integer f = (Integer)djTable.get(d_key);
/* 281 */           f = Integer.valueOf(f.intValue() + ru.getRu_je().intValue());
/* 282 */           djTable.remove(d_key);
/* 283 */           djTable.put(d_key, f);
/*     */         } else {
/* 285 */           djTable.put(d_key, ru.getRu_je());
/*     */         }
/* 287 */         if ((maxDate == null) || (maxDate.before(ru.getRu_date()))) {
/* 288 */           maxDate = ru.getRu_date();
/*     */         }
/* 290 */         if (!keys.contains(ru.getEcard_key())) {
/* 291 */           ru.setRu_zonge(ru.getRu_je().intValue());
/* 292 */           ruList.add(ru);
/* 293 */           keys.add(ru.getEcard_key());
/*     */         } else {
/* 295 */           Ecard_ru temp_ru = (Ecard_ru)ruList.get(keys.indexOf(ru.getEcard_key()));
/* 296 */           temp_ru.setRu_zonge(temp_ru.getRu_zonge() + ru.getRu_je().intValue());
/*     */         }
/*     */       }
/* 299 */       List headerList = new ArrayList();
/* 300 */       headerList.add("账户名称");
/* 301 */       headerList.add("卡号");
/* 302 */       headerList.add("银行");
/* 303 */       headerList.add("年月");
/* 304 */       headerList.add("汇款总额");
/* 305 */       Calendar c = Calendar.getInstance();
/* 306 */       c.setTime(minDate);
/* 307 */       maxDate = DateUtil.getNextDay(maxDate);
/* 308 */       int hs = 5;
/* 309 */       List<String> days = new ArrayList();
/* 310 */       while (!c.getTime().after(maxDate)) {
/* 311 */         headerList.add(this.format.format(c.getTime()));
/* 312 */         hs++;
/* 313 */         days.add(DateUtil.DateToStr(c.getTime(), "yyyy-MM-dd"));
/* 314 */         c.add(5, 1);
/*     */       }
/* 316 */       List objects = new ArrayList();
/* 317 */       int sum = 0;
/* 318 */       for (Ecard_ru ru : ruList) {
/* 319 */         Object[] objs = new Object[hs];
/* 320 */         objs[0] = ru.getEcard_name();
/* 321 */         objs[1] = ru.getEcard_code();
/* 322 */         objs[2] = ru.getEcard_bank();
/* 323 */         objs[3] = ym;
/* 324 */         objs[4] = this.df.format(ru.getRu_zonge());
/* 325 */         sum += ru.getRu_zonge();
/* 326 */         objects.add(objs);
/*     */       }
/* 328 */       for (Object obj : dataList) {
/* 329 */         Ecard_ru ru = (Ecard_ru)obj;
/* 330 */         Object[] objs = (Object[])objects.get(keys.indexOf(ru.getEcard_key()));
/* 331 */         objs[(5 + days.indexOf(DateUtil.DateToStr(ru.getRu_date(), "yyyy-MM-dd")))] = this.df.format(ru.getRu_je());
/*     */       }
/* 333 */       Object[] objs = new Object[hs];
/* 334 */       objs[0] = "合计：";
/* 335 */       objs[1] = ("计数：" + objects.size());
/* 336 */       objs[4] = Integer.valueOf(sum);
/* 337 */       for (String k : djTable.keySet()) {
/* 338 */         Integer f = (Integer)djTable.get(k);
/* 339 */         int index = days.indexOf(k);
/* 340 */         if (index > -1) {
/* 341 */           objs[(5 + index)] = f;
/*     */         }
/*     */       }
/* 344 */       objects.add(objs);
/* 345 */       this.ftable = new FTable(headerList);
/* 346 */       this.ftable.setObjects(objects);
/* 347 */       this.ftable.lockColumns(2);
/* 348 */       this.pnlHk.setLayout(new BorderLayout());
/* 349 */       this.pnlHk.add(this.ftable, "Center");
/* 350 */       this.pnlHk.updateUI();
/* 351 */       this.ftable.setRight_allow_flag(false);
/* 352 */       refreshStatus();
/* 353 */       this.hk += 1;
/*     */     }
/*     */   }
/*     */   
/*     */   private void fetchData_xf() {
/* 358 */     if (this.xf > 0) {
/* 359 */       return;
/*     */     }
/* 361 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/* 362 */       this.pnlXf.removeAll();
/* 363 */       String ym = this.tf_ym.getText();
/* 364 */       if (!SysUtil.check_month(ym)) {
/* 365 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/* 366 */         return;
/*     */       }
/* 368 */       String where_sql = "";
/* 369 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/* 370 */       if ((t_obj instanceof String)) {
/* 371 */         if (!"所有卡".equals(t_obj.toString()))
/*     */         {
/* 373 */           if ("已激活".equals(t_obj.toString())) {
/* 374 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/* 375 */           } else if ("已停止".equals(t_obj.toString())) {
/* 376 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/* 377 */           } else if ("普养".equals(t_obj.toString())) {
/* 378 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 379 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 380 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*     */             } else {
/* 382 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*     */             }
/* 384 */           } else if ("中养".equals(t_obj.toString())) {
/* 385 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 386 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 387 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*     */             } else {
/* 389 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*     */             }
/* 391 */           } else if ("精养".equals(t_obj.toString())) {
/* 392 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 393 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 394 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*     */             } else {
/* 396 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*     */             }
/*     */           } else {
/* 399 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 400 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 401 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*     */             } else {
/* 403 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*     */             }
/* 405 */             this.ecardManager = ("刷卡管理员：" + t_obj.toString());
/*     */           } }
/* 407 */       } else if ((t_obj instanceof Ecard)) {
/* 408 */         Ecard e = (Ecard)t_obj;
/* 409 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/* 410 */         this.ecardManager = ("刷卡管理员：" + e.getEcard_manager());
/* 411 */         this.ecardName = (e.getEcard_name() + "刷卡表");
/*     */       }
/* 413 */       if (!"所有".equals(this.cb_fl.getSelectedItem())) {
/* 414 */         where_sql = where_sql + " and e.chu_fl='" + this.cb_fl.getSelectedItem().toString() + "'";
/*     */       }
/* 416 */       String sql = " from Ecard_chu e where e.chu_ym='" + ym + "' ";
/* 417 */       sql = sql + where_sql;
/* 418 */       sql = sql + " order by e.ecard_key,e.chu_date,e.chu_je desc";
/* 419 */       List dataList = CommUtil.fetchEntities(sql);
/* 420 */       if (dataList.isEmpty()) {
/* 421 */         this.pnlXf.updateUI();
/* 422 */         return;
/*     */       }
/* 424 */       Date maxDate = null;
/* 425 */       Date minDate = DateUtil.StrToDate(ym + "01", "yyyyMMdd");
/* 426 */       List keys = new ArrayList();
/* 427 */       List<Ecard_chu> chuList = new ArrayList();
/* 428 */       Hashtable<String, Float> djTable = new Hashtable();
/* 429 */       for (Object obj : dataList) {
/* 430 */         Ecard_chu chu = (Ecard_chu)obj;
/* 431 */         float tempFl = Float.parseFloat(chu.getChu_fl());
/* 432 */         chu.setChu_sxf(tempFl * chu.getChu_je().floatValue());
/* 433 */         String d_key = DateUtil.DateToStr(chu.getChu_date(), "yyyy-MM-dd");
/* 434 */         if (djTable.containsKey(d_key)) {
/* 435 */           Float f = (Float)djTable.get(d_key);
/* 436 */           f = Float.valueOf(f.floatValue() + chu.getChu_je().floatValue() - chu.getChu_sxf());
/* 437 */           djTable.remove(d_key);
/* 438 */           djTable.put(d_key, f);
/*     */         } else {
/* 440 */           djTable.put(d_key, Float.valueOf(chu.getChu_je().floatValue() - chu.getChu_sxf()));
/*     */         }
/* 442 */         if ((maxDate == null) || (maxDate.before(chu.getChu_date()))) {
/* 443 */           maxDate = chu.getChu_date();
/*     */         }
/* 445 */         if (!keys.contains(chu.getEcard_key())) {
/* 446 */           chu.setChu_zonge(chu.getChu_je());
/* 447 */           chu.setChu_sxf(chu.getChu_sxf());
/* 448 */           chuList.add(chu);
/* 449 */           keys.add(chu.getEcard_key());
/*     */         } else {
/* 451 */           Ecard_chu temp_chu = (Ecard_chu)chuList.get(keys.indexOf(chu.getEcard_key()));
/* 452 */           temp_chu.setChu_zonge(Float.valueOf(temp_chu.getChu_zonge().floatValue() + chu.getChu_je().floatValue()));
/* 453 */           temp_chu.setChu_sxf(temp_chu.getChu_sxf() + chu.getChu_sxf());
/*     */         }
/*     */       }
/* 456 */       List headerList = new ArrayList();
/* 457 */       headerList.add("账户名称");
/* 458 */       headerList.add("卡号");
/* 459 */       headerList.add("银行");
/* 460 */       headerList.add("年月");
/* 461 */       headerList.add("消费总额");
/* 462 */       headerList.add("手续费总额");
/* 463 */       Calendar c = Calendar.getInstance();
/* 464 */       c.setTime(minDate);
/* 465 */       maxDate = DateUtil.getNextDay(maxDate);
/* 466 */       int hs = 6;
/* 467 */       List<String> days = new ArrayList();
/* 468 */       while (!c.getTime().after(maxDate)) {
/* 469 */         headerList.add(this.format.format(c.getTime()));
/* 470 */         hs++;
/* 471 */         days.add(DateUtil.DateToStr(c.getTime(), "yyyy-MM-dd"));
/* 472 */         c.add(5, 1);
/*     */       }
/* 474 */       List objects = new ArrayList();
/* 475 */       float sum = 0.0F;
/* 476 */       float sxSum = 0.0F;
/* 477 */       for (Ecard_chu chu : chuList) {
/* 478 */         Object[] objs = new Object[hs];
/* 479 */         objs[0] = chu.getEcard_name();
/* 480 */         objs[1] = chu.getEcard_code();
/* 481 */         objs[2] = chu.getEcard_bank();
/* 482 */         objs[3] = ym;
/* 483 */         objs[4] = Integer.valueOf(Math.round(chu.getChu_zonge().floatValue()));
/* 484 */         sum += chu.getChu_zonge().floatValue();
/* 485 */         objs[5] = Double.valueOf(SysUtil.round(chu.getChu_sxf(), 3));
/* 486 */         sxSum = (float)(sxSum + SysUtil.round(chu.getChu_sxf(), 3));
/* 487 */         objects.add(objs);
/*     */       }
/* 489 */       for (Object obj : dataList) {
/* 490 */         Ecard_chu chu = (Ecard_chu)obj;
/* 491 */         Object[] objs = (Object[])objects.get(keys.indexOf(chu.getEcard_key()));
/* 492 */         String s = "";
/* 493 */         Object o = objs[(6 + days.indexOf(DateUtil.DateToStr(chu.getChu_date(), "yyyy-MM-dd")))];
/* 494 */         if (o != null) {
/* 495 */           s = o.toString() + "；";
/*     */         }
/* 497 */         objs[(6 + days.indexOf(DateUtil.DateToStr(chu.getChu_date(), "yyyy-MM-dd")))] = (s + this.df.format(chu.getChu_je()) + "/" + chu.getChu_fl() + "/" + chu.getChu_item() + chu.getEpos_code());
/*     */       }
/*     */       
/* 500 */       Object[] objs = new Object[hs];
/* 501 */       objs[0] = "合计：";
/* 502 */       objs[1] = ("计数：" + objects.size());
/* 503 */       objs[3] = ("到账：" + SysUtil.round(sum - sxSum, 3));
/* 504 */       objs[4] = Integer.valueOf(Math.round(sum));
/* 505 */       objs[5] = Float.valueOf(sxSum);
/* 506 */       for (String k : djTable.keySet()) {
/* 507 */         Float f = (Float)djTable.get(k);
/* 508 */         int index = days.indexOf(k);
/* 509 */         if (index > -1) {
/* 510 */           objs[(6 + index)] = Double.valueOf(SysUtil.round(f.floatValue(), 1));
/*     */         }
/*     */       }
/* 513 */       objects.add(objs);
/*     */       
/* 515 */       this.ftable2 = new FTable(headerList);
/* 516 */       this.ftable2.setObjects(objects);
/* 517 */       this.ftable2.lockColumns(2);
/* 518 */       this.pnlXf.setLayout(new BorderLayout());
/* 519 */       this.pnlXf.add(this.ftable2, "Center");
/* 520 */       this.pnlXf.updateUI();
/* 521 */       this.ftable2.setRight_allow_flag(false);
/* 522 */       refreshStatus();
/* 523 */       this.xf += 1;
/*     */     }
/*     */   }
/*     */   
/*     */   private void fetchData_hz() {
/* 528 */     if (this.hz > 0) {
/* 529 */       return;
/*     */     }
/* 531 */     if ((this.curTreeNode instanceof DefaultMutableTreeNode)) {
/* 532 */       this.pnlHz.removeAll();
/* 533 */       String ym = this.tf_ym.getText();
/* 534 */       if (!SysUtil.check_month(ym)) {
/* 535 */         JOptionPane.showMessageDialog(null, "请输入正确的年月");
/* 536 */         return;
/*     */       }
/* 538 */       String where_sql = "";
/* 539 */       Object t_obj = ((DefaultMutableTreeNode)this.curTreeNode).getUserObject();
/* 540 */       if ((t_obj instanceof String)) {
/* 541 */         if (!"所有卡".equals(t_obj.toString()))
/*     */         {
/* 543 */           if ("已激活".equals(t_obj.toString())) {
/* 544 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活')";
/* 545 */           } else if ("已停止".equals(t_obj.toString())) {
/* 546 */             where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止')";
/* 547 */           } else if ("普养".equals(t_obj.toString())) {
/* 548 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 549 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 550 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '普养')";
/*     */             } else {
/* 552 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '普养')";
/*     */             }
/* 554 */           } else if ("中养".equals(t_obj.toString())) {
/* 555 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 556 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 557 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '中养')";
/*     */             } else {
/* 559 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '中养')";
/*     */             }
/* 561 */           } else if ("精养".equals(t_obj.toString())) {
/* 562 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 563 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 564 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_type = '精养')";
/*     */             } else {
/* 566 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_type = '精养')";
/*     */             }
/*     */           } else {
/* 569 */             DefaultMutableTreeNode p_node = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.curTreeNode).getParent();
/* 570 */             if ("已激活".equals(p_node.getUserObject().toString())) {
/* 571 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已激活' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*     */             } else {
/* 573 */               where_sql = " and e.ecard_key in(select ecard.ecard_key from Ecard ecard where ecard.ecard_state = '已停止' and ecard.ecard_manager = '" + t_obj.toString() + "')";
/*     */             }
/* 575 */             this.ecardManager = ("刷卡管理员：" + t_obj.toString());
/*     */           } }
/* 577 */       } else if ((t_obj instanceof Ecard)) {
/* 578 */         Ecard e = (Ecard)t_obj;
/* 579 */         where_sql = " and e.ecard_key='" + e.getEcard_key() + "'";
/* 580 */         this.ecardManager = ("刷卡管理员：" + e.getEcard_manager());
/* 581 */         this.ecardName = (e.getEcard_name() + "刷卡表");
/*     */       }
/*     */       
/* 584 */       String sql = " from Ecard_chu e where e.chu_ym='" + ym + "' ";
/* 585 */       sql = sql + where_sql;
/* 586 */       sql = sql + " order by e.ecard_key,e.chu_date,e.chu_je desc";
/* 587 */       List chuDataList = CommUtil.fetchEntities(sql);
/*     */       
/* 589 */       sql = " from Ecard_ru e where e.ru_ym='" + ym + "' ";
/* 590 */       sql = sql + where_sql;
/* 591 */       sql = sql + " order by e.ecard_key,e.ru_date";
/* 592 */       List ruDataList = CommUtil.fetchEntities(sql);
/*     */       
/* 594 */       if (ruDataList.isEmpty()) {
/* 595 */         this.pnlHz.updateUI();
/* 596 */         return;
/*     */       }
/* 598 */       Hashtable<String, List<Ecard_chu>> chuTable = new Hashtable();
/* 599 */       for (Object obj : chuDataList) {
/* 600 */         Ecard_chu c = (Ecard_chu)obj;
/* 601 */         List<Ecard_chu> cList = null;
/* 602 */         if (chuTable.containsKey(c.getEcard_key() + DateUtil.DateToStr(c.getChu_date()))) {
/* 603 */           cList = (List)chuTable.get(c.getEcard_key() + DateUtil.DateToStr(c.getChu_date()));
/*     */         } else {
/* 605 */           cList = new ArrayList();
/* 606 */           chuTable.put(c.getEcard_key() + DateUtil.DateToStr(c.getChu_date()), cList);
/*     */         }
/* 608 */         cList.add(c);
/*     */       }
/* 610 */       List<Object[]> dataList = new ArrayList();
/* 611 */       String eKey = "";
/* 612 */       Ecard_ru hzRu = null;
/* 613 */       Ecard_chu hzChu = null;
/* 614 */       int tempIndex = -1;
/* 615 */       for (Object obj : ruDataList) {
/* 616 */         Ecard_ru r = (Ecard_ru)obj;
/* 617 */         if (!r.getEcard_key().equals(eKey)) {
/* 618 */           eKey = r.getEcard_key();
/* 619 */           if (tempIndex > -1) {
/* 620 */             Object[] zobjs = { "", "", "", "" };
/* 621 */             if (hzRu != null) {
/* 622 */               zobjs[0] = ("总汇款：" + hzRu.getRu_zonge());
/*     */             }
/* 624 */             if (hzChu != null) {
/* 625 */               zobjs[1] = ("手续费：" + hzChu.getChu_sxf());
/* 626 */               zobjs[2] = ("到账：" + (hzChu.getChu_zonge().floatValue() - hzChu.getChu_sxf()));
/*     */             }
/* 628 */             dataList.add(tempIndex, zobjs);
/*     */           }
/* 630 */           dataList.add(new Object[4]);
/* 631 */           Object[] hobjs = { r.getEcard_name(), (r.getEcard_code() != null) && (r.getEcard_code().length() > 4) ? r.getEcard_code().substring(r.getEcard_code().length() - 4, r.getEcard_code().length()) : r.getEcard_code(), r.getEcard_bank(), "" };
/*     */           
/*     */ 
/* 634 */           dataList.add(hobjs);
/* 635 */           tempIndex = dataList.size();
/* 636 */           hzRu = new Ecard_ru();
/* 637 */           hzRu.setRu_zonge(0);
/* 638 */           hzChu = new Ecard_chu();
/* 639 */           hzChu.setChu_sxf(0.0F);
/* 640 */           hzChu.setChu_zonge(Float.valueOf(0.0F));
/*     */         }
/* 642 */         Object[] objs = new Object[5];
/* 643 */         objs[0] = DateUtil.DateToStr(r.getRu_date());
/* 644 */         objs[1] = "汇款";
/* 645 */         objs[2] = r.getRu_je();
/* 646 */         objs[3] = "";
/* 647 */         objs[4] = r.getEcard_ru_key();
/* 648 */         hzRu.setRu_zonge(hzRu.getRu_zonge() + r.getRu_je().intValue());
/* 649 */         dataList.add(objs);
/* 650 */         List<Ecard_chu> chuList = (List)chuTable.get(r.getEcard_key() + DateUtil.DateToStr(r.getRu_date()));
/* 651 */         if (chuList != null) {
/* 652 */           for (Ecard_chu c : chuList) {
/* 653 */             Object[] cobjs = new Object[5];
/* 654 */             cobjs[0] = "";
/* 655 */             cobjs[2] = c.getChu_je();
/* 656 */             cobjs[3] = "囗交易成功";
/* 657 */             cobjs[4] = c.getEcard_chu_key();
/* 658 */             hzChu.setChu_zonge(Float.valueOf(hzChu.getChu_zonge().floatValue() + c.getChu_je().floatValue()));
/* 659 */             float tempFl = Float.parseFloat(c.getChu_fl());
/* 660 */             cobjs[1] = ("【机器：" + c.getEpos_code() + "/" + c.getEpos_name() + "(" + tempFl * 100.0F + "%)】");
/* 661 */             c.setChu_sxf(tempFl * c.getChu_je().floatValue());
/* 662 */             hzChu.setChu_sxf(hzChu.getChu_sxf() + c.getChu_sxf());
/* 663 */             dataList.add(cobjs);
/*     */           }
/*     */         }
/*     */       }
/* 667 */       if (tempIndex > -1) {
/* 668 */         Object[] zobjs = { "", "", "", "" };
/* 669 */         if (hzRu != null) {
/* 670 */           zobjs[0] = ("总汇款：" + hzRu.getRu_zonge());
/*     */         }
/* 672 */         if (hzChu != null) {
/* 673 */           zobjs[1] = ("手续费：" + hzChu.getChu_sxf());
/* 674 */           zobjs[2] = ("到账：" + (hzChu.getChu_zonge().floatValue() - hzChu.getChu_sxf()));
/*     */         }
/* 676 */         dataList.add(tempIndex, zobjs);
/*     */       }
/* 678 */       if (dataList.size() > 0) {
/* 679 */         dataList.remove(0);
/*     */       }
/*     */       
/* 682 */       List headerList = new ArrayList();
/* 683 */       headerList.add("日期");
/* 684 */       headerList.add("项目");
/* 685 */       headerList.add("金额");
/* 686 */       headerList.add("状态");
/* 687 */       this.ftable3 = new FTable(headerList);
/* 688 */       this.ftable3.setObjects(dataList);
/* 689 */       this.ftable3.getColumnModel().getColumn(1).setPreferredWidth(200);
/* 690 */       this.ftable3.getColumnModel().getColumn(1).setMaxWidth(200);
/* 691 */       this.ftable3.updateUI();
/*     */       
/* 693 */       this.pnlHz.setLayout(new BorderLayout());
/* 694 */       this.pnlHz.add(this.ftable3, "Center");
/* 695 */       this.pnlHz.updateUI();
/* 696 */       this.ftable3.setRight_allow_flag(false);
/* 697 */       refreshStatus();
/* 698 */       this.hz += 1;
/*     */     }
/*     */   }
/*     */   
/*     */   private void refreshStatus() {
/* 703 */     if (this.tabPanel.getSelectedIndex() == 1) {
/* 704 */       ContextManager.setStatusBar(this.ftable.getObjects().size());
/* 705 */     } else if (this.tabPanel.getSelectedIndex() == 2) {
/* 706 */       ContextManager.setStatusBar(this.ftable2.getObjects().size());
/*     */     } else
/* 708 */       ContextManager.setStatusBar(this.ftable3.getObjects().size()); }
/*     */   
/*     */   private JSplitPane jSplitPane2;
/*     */   private JTabbedPane jTabbedPane1;
/*     */   private JPanel pnlBeanCard;
/*     */   private JPanel pnlCard;
/*     */   private JPanel pnlHk;
/*     */   private JPanel pnlHz;
/*     */   private JPanel pnlTable;
/*     */   private JPanel pnlXf;
/*     */   private JTabbedPane tabPanel;
/*     */   private JTextField tf_ym;
/*     */   private JToolBar toolBar;
/* 721 */   private void initComponents() { this.jSplitPane1 = new JSplitPane();
/* 722 */     this.jPanel1 = new JPanel();
/* 723 */     this.toolBar = new JToolBar();
/* 724 */     this.btnEdit = new JButton();
/* 725 */     this.jLabel1 = new JLabel();
/* 726 */     this.tf_ym = new JTextField();
/* 727 */     this.jLabel2 = new JLabel();
/* 728 */     this.cb_fl = new JComboBox();
/* 729 */     this.btnSearch = new JButton();
/* 730 */     this.btnExcel = new JButton();
/* 731 */     this.jSplitPane2 = new JSplitPane();
/* 732 */     this.pnlTable = new JPanel();
/* 733 */     this.tabPanel = new JTabbedPane();
/* 734 */     this.pnlHz = new JPanel();
/* 735 */     this.pnlHk = new JPanel();
/* 736 */     this.pnlXf = new JPanel();
/* 737 */     this.jPanel3 = new JPanel();
/* 738 */     this.jTabbedPane1 = new JTabbedPane();
/* 739 */     this.pnlBeanCard = new JPanel();
/* 740 */     this.pnlCard = new JPanel();
/*     */     
/* 742 */     this.jSplitPane1.setDividerLocation(200);
/* 743 */     this.jSplitPane1.setOneTouchExpandable(true);
/*     */     
/* 745 */     this.toolBar.setFloatable(false);
/* 746 */     this.toolBar.setRollover(true);
/*     */     
/* 748 */     this.btnEdit.setText("微调");
/* 749 */     this.btnEdit.setFocusable(false);
/* 750 */     this.btnEdit.setHorizontalTextPosition(0);
/* 751 */     this.btnEdit.setVerticalTextPosition(3);
/* 752 */     this.toolBar.add(this.btnEdit);
/*     */     
/* 754 */     this.jLabel1.setText(" 年月：");
/* 755 */     this.toolBar.add(this.jLabel1);
/*     */     
/* 757 */     this.tf_ym.setText("201601");
/* 758 */     this.tf_ym.setMaximumSize(new Dimension(60, Integer.MAX_VALUE));
/* 759 */     this.tf_ym.setMinimumSize(new Dimension(60, 21));
/* 760 */     this.tf_ym.setName("");
/* 761 */     this.tf_ym.setPreferredSize(new Dimension(60, 21));
/* 762 */     this.toolBar.add(this.tf_ym);
/*     */     
/* 764 */     this.jLabel2.setText(" 费率：");
/* 765 */     this.toolBar.add(this.jLabel2);
/*     */     
/* 767 */     this.cb_fl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "所有", "0.0038", "0.0078", "0.0125" }));
/* 768 */     this.cb_fl.setMaximumSize(new Dimension(65, 21));
/* 769 */     this.cb_fl.setMinimumSize(new Dimension(65, 21));
/* 770 */     this.cb_fl.setPreferredSize(new Dimension(65, 21));
/* 771 */     this.toolBar.add(this.cb_fl);
/*     */     
/* 773 */     this.btnSearch.setText("查询");
/* 774 */     this.btnSearch.setFocusable(false);
/* 775 */     this.btnSearch.setHorizontalTextPosition(0);
/* 776 */     this.btnSearch.setVerticalTextPosition(3);
/* 777 */     this.toolBar.add(this.btnSearch);
/*     */     
/* 779 */     this.btnExcel.setText("导出Excel");
/* 780 */     this.btnExcel.setFocusable(false);
/* 781 */     this.btnExcel.setHorizontalTextPosition(0);
/* 782 */     this.btnExcel.setVerticalTextPosition(3);
/* 783 */     this.toolBar.add(this.btnExcel);
/*     */     
/* 785 */     this.jSplitPane2.setDividerLocation(340);
/* 786 */     this.jSplitPane2.setDividerSize(3);
/* 787 */     this.jSplitPane2.setOrientation(0);
/*     */     
/* 789 */     this.pnlHz.setLayout(new BorderLayout());
/* 790 */     this.tabPanel.addTab("数据汇总", this.pnlHz);
/*     */     
/* 792 */     this.pnlHk.setLayout(new BorderLayout());
/* 793 */     this.tabPanel.addTab("汇款数据", this.pnlHk);
/*     */     
/* 795 */     this.pnlXf.setLayout(new BorderLayout());
/* 796 */     this.tabPanel.addTab("消费数据", this.pnlXf);
/*     */     
/* 798 */     GroupLayout pnlTableLayout = new GroupLayout(this.pnlTable);
/* 799 */     this.pnlTable.setLayout(pnlTableLayout);
/* 800 */     pnlTableLayout.setHorizontalGroup(pnlTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.tabPanel));
/*     */     
/*     */ 
/*     */ 
/* 804 */     pnlTableLayout.setVerticalGroup(pnlTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.tabPanel));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 809 */     this.jSplitPane2.setTopComponent(this.pnlTable);
/*     */     
/* 811 */     this.pnlBeanCard.setLayout(new BorderLayout());
/* 812 */     this.jTabbedPane1.addTab("信用卡信息", this.pnlBeanCard);
/*     */     
/* 814 */     GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
/* 815 */     this.jPanel3.setLayout(jPanel3Layout);
/* 816 */     jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/* 820 */     jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTabbedPane1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 825 */     this.jSplitPane2.setRightComponent(this.jPanel3);
/*     */     
/* 827 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 828 */     this.jPanel1.setLayout(jPanel1Layout);
/* 829 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, -1, 32767).addComponent(this.jSplitPane2));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 834 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.toolBar, -2, 25, -2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSplitPane2)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 842 */     this.jSplitPane1.setRightComponent(this.jPanel1);
/*     */     
/* 844 */     this.pnlCard.setLayout(new BorderLayout());
/* 845 */     this.jSplitPane1.setLeftComponent(this.pnlCard);
/*     */     
/* 847 */     GroupLayout layout = new GroupLayout(this);
/* 848 */     setLayout(layout);
/* 849 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 644, 32767));
/*     */     
/*     */ 
/*     */ 
/* 853 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 418, 32767));
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardDataPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */