/*     */ package org.jhrcore.client.ecard;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.SysParameter;
/*     */ import org.jhrcore.entity.salary.ValidateSQLResult;
/*     */ import org.jhrcore.ui.task.IModulePanel;
/*     */ import org.jhrcore.util.ComponentUtil;
/*     */ import org.jhrcore.util.MsgUtil;
/*     */ 
/*     */ 
/*     */ public class EcardParamPanel
/*     */   extends JPanel
/*     */   implements IModulePanel
/*     */ {
/*  32 */   private List<SysParameter> paramList = new ArrayList();
/*     */   private SysParameter sp_py;
/*     */   private SysParameter sp_zy;
/*     */   
/*  36 */   public EcardParamPanel() { initComponents();
/*  37 */     initOthers();
/*  38 */     setupEvents();
/*     */   }
/*     */   
/*     */ 
/*     */   private SysParameter sp_jy;
/*     */   private JButton btnRefresh;
/*     */   private JButton btnSave;
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   private JLabel jLabel3;
/*     */   private void initComponents()
/*     */   {
/*  50 */     this.toolBar = new JToolBar();
/*  51 */     this.btnRefresh = new JButton();
/*  52 */     this.btnSave = new JButton();
/*  53 */     this.jPanel1 = new JPanel();
/*  54 */     this.jPanel2 = new JPanel();
/*  55 */     this.jLabel1 = new JLabel();
/*  56 */     this.jLabel2 = new JLabel();
/*  57 */     this.jLabel3 = new JLabel();
/*  58 */     this.tf_py = new JTextField();
/*  59 */     this.tf_jy = new JTextField();
/*  60 */     this.tf_zy = new JTextField();
/*  61 */     this.jLabel4 = new JLabel();
/*  62 */     this.jLabel5 = new JLabel();
/*  63 */     this.jLabel6 = new JLabel();
/*     */     
/*  65 */     this.toolBar.setFloatable(false);
/*  66 */     this.toolBar.setRollover(true);
/*  67 */     this.toolBar.setMaximumSize(new Dimension(1240, 24));
/*  68 */     this.toolBar.setMinimumSize(new Dimension(106, 24));
/*  69 */     this.toolBar.setPreferredSize(new Dimension(106, 24));
/*     */     
/*  71 */     this.btnRefresh.setText("刷新");
/*  72 */     this.btnRefresh.setFocusable(false);
/*  73 */     this.btnRefresh.setHorizontalTextPosition(0);
/*  74 */     this.btnRefresh.setVerticalTextPosition(3);
/*  75 */     this.toolBar.add(this.btnRefresh);
/*     */     
/*  77 */     this.btnSave.setText("保存");
/*  78 */     this.btnSave.setFocusable(false);
/*  79 */     this.btnSave.setHorizontalTextPosition(0);
/*  80 */     this.btnSave.setVerticalTextPosition(3);
/*  81 */     this.toolBar.add(this.btnSave);
/*     */     
/*  83 */     this.jPanel2.setBorder(BorderFactory.createTitledBorder("养卡成本"));
/*     */     
/*  85 */     this.jLabel1.setText("普养：");
/*     */     
/*  87 */     this.jLabel2.setText("中养：");
/*     */     
/*  89 */     this.jLabel3.setText("精养：");
/*     */     
/*  91 */     this.tf_py.setHorizontalAlignment(4);
/*     */     
/*  93 */     this.tf_jy.setHorizontalAlignment(4);
/*     */     
/*  95 */     this.tf_zy.setHorizontalAlignment(4);
/*     */     
/*  97 */     this.jLabel4.setText("%");
/*     */     
/*  99 */     this.jLabel5.setText("%");
/*     */     
/* 101 */     this.jLabel6.setText("%");
/*     */     
/* 103 */     GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
/* 104 */     this.jPanel2.setLayout(jPanel2Layout);
/* 105 */     jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(54, 54, 54).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.jLabel3).addGap(18, 18, 18).addComponent(this.tf_jy, -2, 111, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel6, -1, -1, 32767)).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addComponent(this.jLabel2).addGap(18, 18, 18).addComponent(this.tf_zy, -2, 111, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel5, -1, -1, 32767)).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addComponent(this.jLabel1).addGap(18, 18, 18).addComponent(this.tf_py, -2, 111, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel4))).addContainerGap(439, 32767)));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 130 */     jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(23, 23, 23).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.tf_py, -2, -1, -2).addComponent(this.jLabel4)).addGap(18, 18, 18).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.tf_zy, -2, -1, -2).addComponent(this.jLabel5)).addGap(18, 18, 18).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.tf_jy, -2, -1, -2).addComponent(this.jLabel6)).addContainerGap(-1, 32767)));
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
/*     */ 
/*     */ 
/* 151 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 152 */     this.jPanel1.setLayout(jPanel1Layout);
/* 153 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel2, -1, -1, 32767));
/*     */     
/*     */ 
/*     */ 
/* 157 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jPanel2, -2, -1, -2).addGap(0, 245, 32767)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 164 */     GroupLayout layout = new GroupLayout(this);
/* 165 */     setLayout(layout);
/* 166 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.toolBar, -1, 680, 32767).addComponent(this.jPanel1, -1, -1, 32767));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 171 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(1, 1, 1).addComponent(this.toolBar, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPanel1, -1, -1, 32767)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private JLabel jLabel4;
/*     */   
/*     */ 
/*     */   private JLabel jLabel5;
/*     */   
/*     */ 
/*     */   private JLabel jLabel6;
/*     */   
/*     */   private JPanel jPanel1;
/*     */   
/*     */   private JPanel jPanel2;
/*     */   
/*     */   private JTextField tf_jy;
/*     */   
/*     */   private JTextField tf_py;
/*     */   
/*     */   private JTextField tf_zy;
/*     */   
/*     */   private JToolBar toolBar;
/*     */   
/*     */   private void initOthers()
/*     */   {
/* 198 */     refreshData();
/*     */   }
/*     */   
/*     */   private void refreshData() {
/* 202 */     this.paramList = (List<SysParameter>) CommUtil.fetchEntities("from SysParameter where sysParameter_key in('ecard_cb_py','ecard_cb_zy','ecard_cb_jy')");
/* 203 */     for (SysParameter sp : this.paramList) {
/* 204 */       if ("ecard_cb_py".equals(sp.getSysParameter_key())) {
/* 205 */         this.sp_py = sp;
/* 206 */       } else if ("ecard_cb_zy".equals(sp.getSysParameter_key())) {
/* 207 */         this.sp_zy = sp;
/* 208 */       } else if ("ecard_cb_jy".equals(sp.getSysParameter_key())) {
/* 209 */         this.sp_jy = sp;
/*     */       }
/*     */     }
/* 212 */     if (this.sp_py == null) {
/* 213 */       this.sp_py = new SysParameter();
/* 214 */       this.sp_py.setSysParameter_key("ecard_cb_py");
/* 215 */       this.sp_py.setSysparameter_code("ecard_cb_py");
/* 216 */       this.sp_py.setSysparameter_value("0.55");
/* 217 */       CommUtil.saveOrUpdate(this.sp_py);
/*     */     }
/* 219 */     this.tf_py.setText(this.sp_py.getSysparameter_value());
/*     */     
/* 221 */     if (this.sp_zy == null) {
/* 222 */       this.sp_zy = new SysParameter();
/* 223 */       this.sp_zy.setSysParameter_key("ecard_cb_zy");
/* 224 */       this.sp_zy.setSysparameter_code("ecard_cb_zy");
/* 225 */       this.sp_zy.setSysparameter_value("0.6");
/* 226 */       CommUtil.saveOrUpdate(this.sp_zy);
/*     */     }
/* 228 */     this.tf_zy.setText(this.sp_zy.getSysparameter_value());
/*     */     
/* 230 */     if (this.sp_jy == null) {
/* 231 */       this.sp_jy = new SysParameter();
/* 232 */       this.sp_jy.setSysParameter_key("ecard_cb_jy");
/* 233 */       this.sp_jy.setSysparameter_code("ecard_cb_jy");
/* 234 */       this.sp_jy.setSysparameter_value("0.65");
/* 235 */       CommUtil.saveOrUpdate(this.sp_jy);
/*     */     }
/* 237 */     this.tf_jy.setText(this.sp_jy.getSysparameter_value());
/*     */   }
/*     */   
/*     */   private void setupEvents()
/*     */   {
/* 242 */     this.btnSave.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 246 */         EcardParamPanel.this.saveParams();
/*     */       }
/*     */       
/* 249 */     });
/* 250 */     this.btnRefresh.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 254 */         EcardParamPanel.this.refreshData();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void saveParams() {
/* 260 */     List list = new ArrayList();
/* 261 */     this.sp_py.setSysparameter_value(this.tf_py.getText().trim());
/* 262 */     this.sp_zy.setSysparameter_value(this.tf_zy.getText().trim());
/* 263 */     this.sp_jy.setSysparameter_value(this.tf_jy.getText().trim());
/* 264 */     list.add(this.sp_py);
/* 265 */     list.add(this.sp_zy);
/* 266 */     list.add(this.sp_jy);
/* 267 */     ValidateSQLResult result = CommUtil.saveParameters(list);
/* 268 */     if (result.getResult() == 0) {
/* 269 */       JOptionPane.showMessageDialog(null, "保存成功");
/*     */     } else {
/* 271 */       MsgUtil.showHRSaveErrorMsg(result);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFunctionRight()
/*     */   {
/* 277 */     ComponentUtil.setSysFuntion(this, "EmpRegister");
/*     */   }
/*     */   
/*     */ 
/*     */   public void pickClose() {}
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 286 */     refreshStatus();
/*     */   }
/*     */   
/*     */ 
/*     */   private void refreshStatus() {}
/*     */   
/*     */   public String getModuleCode()
/*     */   {
/* 294 */     return "EmpRegister";
/*     */   }
/*     */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardParamPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */