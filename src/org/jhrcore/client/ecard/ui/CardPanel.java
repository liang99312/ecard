/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.GroupLayout.Alignment;
/*     */ import javax.swing.GroupLayout.ParallelGroup;
/*     */ import javax.swing.GroupLayout.SequentialGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.JTree;
import javax.swing.LayoutStyle;
/*     */ import javax.swing.LayoutStyle.ComponentPlacement;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreeModel;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.jhrcore.client.ecard.listener.IBuildDataSetListener;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ import org.jhrcore.ui.renderer.HRRendererView;
/*     */ 
/*     */ public class CardPanel extends JPanel
/*     */ {
/*     */   private CardTreeModel model;
/*     */   private JTree tree;
/*  40 */   private Set<DefaultMutableTreeNode> selected_nodes = new java.util.HashSet();
/*  41 */   private String search_text = "";
/*     */   private Ecard cur_card;
/*  43 */   private String c_type = "";
/*  44 */   private String p_type = "type";
/*  45 */   private JPopupMenu popupMenu = new JPopupMenu();
/*  46 */   private JMenuItem miRuData = new JMenuItem("生成数据");
/*  47 */   private JMenuItem miDelData = new JMenuItem("删除数据");
/*  48 */   private JMenuItem miChuData = new JMenuItem("生成消费数据");
/*  49 */   private JMenuItem miType = new JMenuItem("按养卡方式");
/*  50 */   private JMenuItem miManager = new JMenuItem("按刷卡管理员");
/*  51 */   private JMenuItem miRefresh = new JMenuItem("刷新数据");
/*  52 */   private List<IBuildDataSetListener> listeners = new ArrayList();
/*     */   private JButton btnSearch;
/*     */   
/*  55 */   public void addBuildDataSetListener(IBuildDataSetListener listener) { this.listeners.add(listener); }
/*     */   
/*     */   public void delBuildDataSetListener(IBuildDataSetListener listener)
/*     */   {
/*  59 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */   public String getType() {
/*  63 */     return this.p_type;
/*     */   }
/*     */   
/*     */   public CardPanel() {
/*  67 */     initComponents();
/*  68 */     initOthers();
/*  69 */     setupEvents();
/*     */   }
/*     */   
/*     */   public CardPanel(String str) {
/*  73 */     this.c_type = str;
/*  74 */     initComponents();
/*  75 */     initOthers();
/*  76 */     setupEvents();
/*     */   }
/*     */   
/*     */   private void initOthers() {
/*  80 */     this.popupMenu.add(this.miRuData);
/*  81 */     this.popupMenu.add(this.miDelData);
/*  82 */     this.popupMenu.add(this.miType);
/*  83 */     this.popupMenu.add(this.miManager);
/*  84 */     this.popupMenu.add(this.miRefresh);
/*  85 */     this.model = new CardTreeModel(this.c_type);
/*  86 */     this.tree = new JTree(this.model);
/*  87 */     this.tree.setRootVisible(true);
/*  88 */     this.tree.setShowsRootHandles(true);
/*  89 */     HRRendererView.getEcardMap(this.tree).initTree(this.tree);
/*  90 */     this.pnlCard.add(new JScrollPane(this.tree), "Center");
/*     */   }
/*     */   
/*     */   private void setupEvents() {
/*  94 */     this.miType.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  98 */         CardPanel.this.model.setType("type");
/*  99 */         CardPanel.this.p_type = "type";
/* 100 */         CardPanel.this.refreshTree();
/*     */       }
/* 102 */     });
/* 103 */     this.miManager.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 107 */         CardPanel.this.model.setType("manager");
/* 108 */         CardPanel.this.p_type = "manager";
/* 109 */         CardPanel.this.refreshTree();
/*     */       }
/* 111 */     });
/* 112 */     this.miDelData.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 116 */         CardPanel.this.deleteData();
/*     */       }
/* 118 */     });
/* 119 */     this.miRuData.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 123 */         CardPanel.this.buildData(1);
/*     */       }
/* 125 */     });
/* 126 */     this.miChuData.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 130 */         CardPanel.this.buildData(2);
/*     */       }
/* 132 */     });
/* 133 */     this.tree.addMouseListener(new java.awt.event.MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e)
/*     */       {
/* 137 */         if (e.getButton() == 3) {
/* 138 */           if ("build".equals(CardPanel.this.c_type)) {
/* 139 */             CardPanel.this.miDelData.setVisible(true);
/* 140 */             CardPanel.this.miRuData.setVisible(true);
/* 141 */             CardPanel.this.miChuData.setVisible(true);
/* 142 */             CardPanel.this.popupMenu.show(CardPanel.this.tree, e.getX(), e.getY());
/* 143 */           } else if ("".equals(CardPanel.this.c_type)) {
/* 144 */             CardPanel.this.miDelData.setVisible(false);
/* 145 */             CardPanel.this.miRuData.setVisible(false);
/* 146 */             CardPanel.this.miChuData.setVisible(false);
/* 147 */             CardPanel.this.popupMenu.show(CardPanel.this.tree, e.getX(), e.getY());
/*     */           }
/*     */         }
/*     */       }
/* 151 */     });
/* 152 */     ActionListener alSearch = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 156 */         CardPanel.this.searchNode();
/*     */       }
/* 158 */     };
/* 159 */     this.miRefresh.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 163 */         CardPanel.this.refreshTree();
/*     */       }
/* 165 */     });
/* 166 */     this.btnSearch.addActionListener(alSearch);
/* 167 */     this.tf_search.addActionListener(alSearch);
/*     */   }
/*     */   
/*     */   private void deleteData() {
/* 171 */     if ((this.tree.getSelectionPath() == null) || (this.tree.getSelectionPath().getLastPathComponent() == null)) {
/* 172 */       JOptionPane.showMessageDialog(null, "请选择节点");
/* 173 */       return;
/*     */     }
/* 175 */     DefaultMutableTreeNode curTreeNode = (DefaultMutableTreeNode)this.tree.getSelectionPath().getLastPathComponent();
/* 176 */     Object obj = curTreeNode.getUserObject();
/* 177 */     Ecard ecard = null;
/* 178 */     String string = "";
/* 179 */     if ((obj instanceof Ecard)) {
/* 180 */       ecard = (Ecard)obj;
/* 181 */       string = ecard.toString();
/*     */     } else {
/* 183 */       JOptionPane.showMessageDialog(null, "请选择信用卡");
/* 184 */       return;
/*     */     }
/* 186 */     for (IBuildDataSetListener listener : this.listeners) {
/* 187 */       listener.deleteData(ecard);
/*     */     }
/*     */   }
/*     */   
/*     */   private void buildData(int index) {
/* 192 */     if ((this.tree.getSelectionPath() == null) || (this.tree.getSelectionPath().getLastPathComponent() == null)) {
/* 193 */       JOptionPane.showMessageDialog(null, "请选择节点");
/* 194 */       return;
/*     */     }
/* 196 */     DefaultMutableTreeNode curTreeNode = (DefaultMutableTreeNode)this.tree.getSelectionPath().getLastPathComponent();
/* 197 */     Object obj = curTreeNode.getUserObject();
/* 198 */     Ecard ecard = null;
/* 199 */     String string = "";
/* 200 */     if ((obj instanceof Ecard)) {
/* 201 */       ecard = (Ecard)obj;
/* 202 */       string = ecard.toString();
/*     */     }
/*     */     else {
/* 205 */       JOptionPane.showMessageDialog(null, "请选择信用卡");
/* 206 */       return;
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 239 */     for (IBuildDataSetListener listener : this.listeners) {
/* 240 */       listener.buildData(string, ecard, index);
/*     */     }
/*     */   }
/*     */   
/*     */   public void refreshTree() {
/* 245 */     this.model.rebuildTree();
/* 246 */     this.tree.updateUI();
/* 247 */     expandTree(this.tree);
/*     */   }
/*     */   
/*     */   public void expandTree(JTree tree) {
/* 251 */     DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
/* 252 */     expandAll(tree, new TreePath(root), true);
/*     */   }
/*     */   
/*     */ 
/*     */   private JLabel jLabel2;
/*     */   
/*     */   private JToolBar jToolBar2;
/*     */   
/*     */   private JPanel pnlCard;
/*     */   private JTextField tf_search;
/*     */   private void expandAll(JTree tree, TreePath parent, boolean expand)
/*     */   {
/* 264 */     DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent.getLastPathComponent();
/* 265 */     Enumeration e; if (node.getChildCount() >= 0) {
/* 266 */       for (e = node.children(); e.hasMoreElements();) {
/* 267 */         DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
/* 268 */         TreePath path = parent.pathByAddingChild(n);
/* 269 */         expandAll(tree, path, expand);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 274 */     if (expand) {
/* 275 */       tree.expandPath(parent);
/*     */     } else {
/* 277 */       tree.collapsePath(parent);
/*     */     }
/*     */   }
/*     */   
/*     */   public void searchNode() {
/* 282 */     String val = this.tf_search.getText();
/* 283 */     if (val.equals("")) {
/* 284 */       return;
/*     */     }
/* 286 */     if (!this.search_text.equals(val)) {
/* 287 */       this.selected_nodes.clear();
/*     */     }
/* 289 */     this.search_text = val;
/* 290 */     DefaultMutableTreeNode node = locateEmp(this.search_text);
/* 291 */     if (node == null) {
/* 292 */       this.selected_nodes.clear();
/* 293 */       node = locateEmp(this.search_text);
/*     */     }
/* 295 */     if (node == null) {
/* 296 */       return;
/*     */     }
/* 298 */     org.jhrcore.util.ComponentUtil.initTreeSelection(this.tree, node);
/* 299 */     this.cur_card = ((Ecard)node.getUserObject());
/*     */   }
/*     */   
/*     */   public DefaultMutableTreeNode locateEmp(String val) {
/* 303 */     DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
/* 304 */     DefaultMutableTreeNode resultNode = null;
/* 305 */     Enumeration deptEnum = node.depthFirstEnumeration();
/*     */     
/* 307 */     Pattern p = Pattern.compile(val);
/*     */     try {
/* 309 */       while (deptEnum.hasMoreElements()) {
/* 310 */         DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)deptEnum.nextElement();
/* 311 */         if ((tmpNode.getUserObject() instanceof Ecard))
/*     */         {
/*     */ 
/* 314 */           Ecard e1 = (Ecard)tmpNode.getUserObject();
/* 315 */           String field_val = null;
/*     */           
/* 317 */           field_val = e1.getEcard_name();
/* 318 */           if (field_val != null) {
/* 319 */             Matcher m = p.matcher(field_val);
/* 320 */             if (m.find()) {
/* 321 */               if (this.selected_nodes.contains(tmpNode)) {
/*     */                 continue;
/*     */               }
/* 324 */               resultNode = tmpNode;
/* 325 */               this.selected_nodes.add(resultNode);
/* 326 */               break;
/*     */             }
/*     */           }
/*     */           
/* 330 */           field_val = e1.getEcard_code();
/* 331 */           if (field_val != null) {
/* 332 */             p = Pattern.compile(val);
/* 333 */             Matcher m = p.matcher(field_val);
/* 334 */             if (m.find()) {
/* 335 */               if (!this.selected_nodes.contains(tmpNode))
/*     */               {
/*     */ 
/* 338 */                 resultNode = tmpNode;
/* 339 */                 this.selected_nodes.add(resultNode);
/* 340 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (SecurityException e) {
/* 347 */       e.printStackTrace();
/*     */     } catch (IllegalArgumentException e) {
/* 349 */       e.printStackTrace();
/*     */     }
/* 351 */     return resultNode;
/*     */   }
/*     */   
/*     */   public JTree getCardTree() {
/* 355 */     return this.tree;
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
/* 367 */     this.jToolBar2 = new JToolBar();
/* 368 */     this.jLabel2 = new JLabel();
/* 369 */     this.tf_search = new JTextField();
/* 370 */     this.btnSearch = new JButton();
/* 371 */     this.pnlCard = new JPanel();
/*     */     
/* 373 */     this.jToolBar2.setFloatable(false);
/* 374 */     this.jToolBar2.setRollover(true);
/* 375 */     this.jToolBar2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
/* 376 */     this.jToolBar2.setMinimumSize(new Dimension(73, 24));
/* 377 */     this.jToolBar2.setPreferredSize(new Dimension(73, 24));
/*     */     
/* 379 */     this.jLabel2.setText(" 查找：");
/* 380 */     this.jToolBar2.add(this.jLabel2);
/* 381 */     this.jToolBar2.add(this.tf_search);
/*     */     
/* 383 */     this.btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search.png")));
/* 384 */     this.btnSearch.setFocusable(false);
/* 385 */     this.btnSearch.setHorizontalTextPosition(0);
/* 386 */     this.btnSearch.setVerticalTextPosition(3);
/* 387 */     this.jToolBar2.add(this.btnSearch);
/*     */     
/* 389 */     this.pnlCard.setLayout(new BorderLayout());
/*     */     
/* 391 */     GroupLayout layout = new GroupLayout(this);
/* 392 */     setLayout(layout);
/* 393 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.pnlCard, -1, -1, 32767).addComponent(this.jToolBar2, -1, 233, 32767));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 398 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jToolBar2, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.pnlCard, -1, 416, 32767)));
/*     */   }
/*     */ }

