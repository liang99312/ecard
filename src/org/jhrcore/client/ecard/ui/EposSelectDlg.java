/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import com.foundercy.pf.control.table.FTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
import org.jdesktop.beansbinding.AutoBinding;
/*     */ import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
/*     */ import org.jdesktop.beansbinding.Binding;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.base.TempFieldInfo;
/*     */ import org.jhrcore.entity.ecard.Epos;
/*     */ import org.jhrcore.entity.query.QueryScheme;
/*     */ import org.jhrcore.rebuild.EntityBuilder;
/*     */ import org.jhrcore.util.PublicUtil;
/*     */ 
/*     */ public class EposSelectDlg extends JDialog
/*     */ {
/*     */   private FTable ftable;
/*     */   private Binding cb_binding;
/*  43 */   private boolean isOk = false;
/*     */   private JButton btnCancel;
/*     */   
/*  46 */   public boolean isOk() { return this.isOk; }
/*     */   
/*     */   private JButton btnOk;
/*     */   public void setIsOk() {
/*  50 */     this.isOk = false;
/*     */   }
/*     */   
/*     */   public Epos getSelectObject() {
/*  54 */     Epos e = null;
/*  55 */     if (this.ftable.getCurrentRow() != null) {
/*  56 */       e = (Epos)this.ftable.getCurrentRow();
/*     */     }
/*  58 */     return e;
/*     */   }
/*     */   
/*     */   public EposSelectDlg(Frame parent, boolean modal)
/*     */   {
/*  63 */     super(parent, modal);
/*  64 */     initComponents();
/*     */   }
/*     */   
/*     */   public EposSelectDlg() {
/*  68 */     initComponents();
/*  69 */     initOthers();
/*  70 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  74 */     bindingCb();
/*  75 */     this.ftable = new FTable(Epos.class, true, true, true);
/*  76 */     List<TempFieldInfo> shift_infos = EntityBuilder.getCommFieldInfoListOf(Epos.class, EntityBuilder.COMM_FIELD_VISIBLE);
/*  77 */     this.ftable.setAll_fields(shift_infos, shift_infos, shift_infos, "");
/*  78 */     this.ftable.setRight_allow_flag(true);
/*  79 */     this.pnlTable.add(this.ftable);
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/*  83 */     this.btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  87 */         if (EposSelectDlg.this.ftable.getCurrentRow() == null) {
/*  88 */           JOptionPane.showMessageDialog(null, "请选择Pos机");
/*  89 */           return;
/*     */         }
/*  91 */         EposSelectDlg.this.isOk = true;
/*  92 */         EposSelectDlg.this.dispose();
/*     */       }
/*  94 */     });
/*  95 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  99 */         EposSelectDlg.this.isOk = false;
/* 100 */         EposSelectDlg.this.dispose();
/*     */       }
/* 102 */     });
/* 103 */     this.cb_fl.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 107 */         EposSelectDlg.this.fetchMainData(EposSelectDlg.this.ftable.getCur_query_scheme(), null);
/*     */       }
/* 109 */     });
/* 110 */     ActionListener search_listener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 114 */         EposSelectDlg.this.doQuickSearch(EposSelectDlg.this.tfSearch.getText());
/*     */       }
/* 116 */     };
/* 117 */     this.ftable.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e)
/*     */       {
/* 121 */         if (e.getClickCount() >= 2) {
/* 122 */           if (EposSelectDlg.this.ftable.getCurrentRow() == null) {
/* 123 */             JOptionPane.showMessageDialog(null, "请选择Pos机");
/* 124 */             return;
/*     */           }
/* 126 */           EposSelectDlg.this.isOk = true;
/* 127 */           EposSelectDlg.this.dispose();
/*     */         }
/*     */       }
/* 130 */     });
/* 131 */     this.btnSearch.addActionListener(search_listener);
/* 132 */     this.tfSearch.addActionListener(search_listener);
/* 133 */     fetchMainData(null, null);
/*     */   }
/*     */   
/*     */   private void bindingCb() {
/* 137 */     List flList = CommUtil.selectSQL("select distinct epos_fei from Epos");
/* 138 */     flList.add("所有");
/* 139 */     if (this.cb_binding == null) {
/* 140 */       this.cb_binding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ, flList, this.cb_fl);
/*     */     } else {
/* 142 */       this.cb_binding.unbind();
/*     */     }
/* 144 */     this.cb_binding.bind();
/*     */   }
/*     */   
/*     */   private void fetchMainData(QueryScheme qs, String s_where) {
/* 148 */     String state = "";
/* 149 */     if (this.cb_fl.getSelectedItem() == null) {
/* 150 */       state = "所有";
/*     */     } else {
/* 152 */       state = this.cb_fl.getSelectedItem().toString();
/*     */     }
/* 154 */     this.ftable.setCur_query_scheme(qs);
/* 155 */     String sql = " from Epos where 1=1";
/* 156 */     if (qs != null) {
/* 157 */       sql = sql + " and Epos.epos_key in(" + qs.buildSql() + ")";
/*     */     }
/* 159 */     if ((s_where != null) && (!s_where.trim().equals(""))) {
/* 160 */       sql = sql + " and (" + s_where + ")";
/*     */     }
/* 162 */     if (!state.equals("所有")) {
/* 163 */       sql = sql + " and epos_fei = '" + state + "'";
/*     */     }
/* 165 */     sql = "select epos_key" + sql + " order by epos_code";
/* 166 */     PublicUtil.getProps_value().setProperty(Epos.class.getName(), "from Epos n where n.epos_key in");
/* 167 */     List list = CommUtil.selectSQL(org.jhrcore.util.DbUtil.tranSQL(sql));
/* 168 */     this.ftable.setObjects(list);
/*     */   }
/*     */   
/*     */   private void doQuickSearch(String text) {
/* 172 */     if ((text == null) || (text.trim().equals(""))) {
/* 173 */       return;
/*     */     }
/* 175 */     text = org.jhrcore.util.SysUtil.getQuickSearchText(text);
/* 176 */     String s_where = "";
/* 177 */     s_where = "(upper(epos_code) like '" + text + "%' or upper(ecard_name) like '" + text + "%')";
/* 178 */     fetchMainData(this.ftable.getCur_query_scheme(), s_where);
/*     */   }
/*     */   
/*     */   private JButton btnSearch;
/*     */   private JComboBox cb_fl;
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   private JToolBar jToolBar1;
/*     */   private JPanel pnlTable;
/*     */   private JTextField tfSearch;
/*     */   private void initComponents()
/*     */   {
/* 190 */     this.pnlTable = new JPanel();
/* 191 */     this.btnOk = new JButton();
/* 192 */     this.btnCancel = new JButton();
/* 193 */     this.jToolBar1 = new JToolBar();
/* 194 */     this.jLabel2 = new JLabel();
/* 195 */     this.cb_fl = new JComboBox();
/* 196 */     this.jLabel1 = new JLabel();
/* 197 */     this.tfSearch = new JTextField();
/* 198 */     this.btnSearch = new JButton();
/*     */     
/* 200 */     setDefaultCloseOperation(2);
/* 201 */     setModal(true);
/*     */     
/* 203 */     this.pnlTable.setLayout(new BorderLayout());
/*     */     
/* 205 */     this.btnOk.setText("确定");
/*     */     
/* 207 */     this.btnCancel.setText("关闭");
/*     */     
/* 209 */     this.jToolBar1.setFloatable(false);
/* 210 */     this.jToolBar1.setRollover(true);
/*     */     
/* 212 */     this.jLabel2.setText(" 费率：");
/* 213 */     this.jToolBar1.add(this.jLabel2);
/*     */     
/* 215 */     this.cb_fl.setModel(new DefaultComboBoxModel(new String[] { "已激活", "已停止", "所有卡" }));
/* 216 */     this.cb_fl.setMaximumSize(new Dimension(80, 23));
/* 217 */     this.jToolBar1.add(this.cb_fl);
/*     */     
/* 219 */     this.jLabel1.setText("  查询：");
/* 220 */     this.jToolBar1.add(this.jLabel1);
/*     */     
/* 222 */     this.tfSearch.setMaximumSize(new Dimension(120, Integer.MAX_VALUE));
/* 223 */     this.tfSearch.setMinimumSize(new Dimension(120, 21));
/* 224 */     this.tfSearch.setPreferredSize(new Dimension(120, 21));
/* 225 */     this.jToolBar1.add(this.tfSearch);
/*     */     
/* 227 */     this.btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search.png")));
/* 228 */     this.jToolBar1.add(this.btnSearch);
/*     */     
/* 230 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 231 */     getContentPane().setLayout(layout);
/* 232 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.pnlTable, -1, -1, 32767).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.btnOk).addGap(55, 55, 55).addComponent(this.btnCancel).addGap(49, 49, 49)).addComponent(this.jToolBar1, GroupLayout.Alignment.TRAILING, -1, 516, 32767));
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
/* 243 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jToolBar1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.pnlTable, -1, 350, 32767).addGap(11, 11, 11).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnOk).addComponent(this.btnCancel)).addContainerGap()));
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
/* 256 */     pack();
/*     */   }
/*     */ }
