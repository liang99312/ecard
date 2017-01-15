/*     */ package org.jhrcore.client.ecard.ui;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import org.jhrcore.client.CommUtil;
/*     */ import org.jhrcore.entity.ecard.Ecard;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CardTreeModel
/*     */   extends DefaultTreeModel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  22 */   private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("所有卡");
/*  23 */   private String sql = "from Ecard order by ecard_name,ecard_code";
/*     */   private List list;
/*  25 */   private String type = "type";
/*     */   
/*     */   public void setType(String type) {
/*  28 */     this.type = type;
/*     */   }
/*     */   
/*     */   public List getList() {
/*  32 */     return this.list;
/*     */   }
/*     */   
/*     */   public CardTreeModel() {
/*  36 */     super(new DefaultMutableTreeNode());
/*  37 */     setRoot(this.rootNode);
/*  38 */     rebuildTree();
/*     */   }
/*     */   
/*     */   public CardTreeModel(String str) {
/*  42 */     super(new DefaultMutableTreeNode());
/*  43 */     setRoot(this.rootNode);
/*  44 */     if ("noCard".equals(str)) {
/*  45 */       buildTree_noCard();
/*     */     } else {
/*  47 */       rebuildTree();
/*     */     }
/*     */   }
/*     */   
/*     */   public void buildTree_noCard() {
/*  52 */     this.rootNode.removeAllChildren();
/*  53 */     DefaultMutableTreeNode jhNode = new DefaultMutableTreeNode("已激活");
/*  54 */     DefaultMutableTreeNode tzNode = new DefaultMutableTreeNode("已停止");
/*  55 */     DefaultMutableTreeNode jpNode = new DefaultMutableTreeNode("普养");
/*  56 */     DefaultMutableTreeNode jzNode = new DefaultMutableTreeNode("中养");
/*  57 */     DefaultMutableTreeNode jjNode = new DefaultMutableTreeNode("精养");
/*  58 */     DefaultMutableTreeNode ypNode = new DefaultMutableTreeNode("普养");
/*  59 */     DefaultMutableTreeNode yzNode = new DefaultMutableTreeNode("中养");
/*  60 */     DefaultMutableTreeNode yjNode = new DefaultMutableTreeNode("精养");
/*  61 */     this.rootNode.add(jhNode);
/*  62 */     jhNode.add(jpNode);
/*  63 */     jhNode.add(jzNode);
/*  64 */     jhNode.add(jjNode);
/*  65 */     this.rootNode.add(tzNode);
/*  66 */     tzNode.add(ypNode);
/*  67 */     tzNode.add(yzNode);
/*  68 */     tzNode.add(yjNode);
/*     */   }
/*     */   
/*     */   public void buildTree(List list) {
/*  72 */     this.rootNode.removeAllChildren();
/*  73 */     DefaultMutableTreeNode jhNode = new DefaultMutableTreeNode("已激活");
/*  74 */     DefaultMutableTreeNode tzNode = new DefaultMutableTreeNode("已停止");
/*  75 */     DefaultMutableTreeNode jpNode = new DefaultMutableTreeNode("普养");
/*  76 */     DefaultMutableTreeNode jzNode = new DefaultMutableTreeNode("中养");
/*  77 */     DefaultMutableTreeNode jjNode = new DefaultMutableTreeNode("精养");
/*  78 */     DefaultMutableTreeNode ypNode = new DefaultMutableTreeNode("普养");
/*  79 */     DefaultMutableTreeNode yzNode = new DefaultMutableTreeNode("中养");
/*  80 */     DefaultMutableTreeNode yjNode = new DefaultMutableTreeNode("精养");
/*  81 */     this.rootNode.add(jhNode);
/*  82 */     jhNode.add(jpNode);
/*  83 */     jhNode.add(jzNode);
/*  84 */     jhNode.add(jjNode);
/*  85 */     this.rootNode.add(tzNode);
/*  86 */     tzNode.add(ypNode);
/*  87 */     tzNode.add(yzNode);
/*  88 */     tzNode.add(yjNode);
/*  89 */     for (Object obj : list) {
/*  90 */       Ecard d = (Ecard)obj;
/*  91 */       if ("已激活".equals(d.getEcard_state())) {
/*  92 */         if ("精养".equals(d.getEcard_type())) {
/*  93 */           jjNode.add(new DefaultMutableTreeNode(d));
/*  94 */         } else if ("中养".equals(d.getEcard_type())) {
/*  95 */           jzNode.add(new DefaultMutableTreeNode(d));
/*     */         } else {
/*  97 */           jpNode.add(new DefaultMutableTreeNode(d));
/*     */         }
/*     */       }
/* 100 */       else if ("精养".equals(d.getEcard_type())) {
/* 101 */         yjNode.add(new DefaultMutableTreeNode(d));
/* 102 */       } else if ("中养".equals(d.getEcard_type())) {
/* 103 */         yzNode.add(new DefaultMutableTreeNode(d));
/*     */       } else {
/* 105 */         ypNode.add(new DefaultMutableTreeNode(d));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void buildTree_manager(List list)
/*     */   {
/* 112 */     this.rootNode.removeAllChildren();
/* 113 */     DefaultMutableTreeNode jhNode = new DefaultMutableTreeNode("已激活");
/* 114 */     DefaultMutableTreeNode tzNode = new DefaultMutableTreeNode("已停止");
/* 115 */     Hashtable<String, DefaultMutableTreeNode> mTable = new Hashtable();
/* 116 */     this.rootNode.add(jhNode);
/* 117 */     this.rootNode.add(tzNode);
/* 118 */     for (Object obj : list) {
/* 119 */       Ecard d = (Ecard)obj;
/* 120 */       String mString = d.getEcard_manager();
/* 121 */       mString = (mString == null) || (mString.equals("")) ? "系统管理员" : mString;
/* 122 */       if ("已激活".equals(d.getEcard_state())) {
/* 123 */         if (mTable.containsKey("已激活_" + mString)) {
/* 124 */           ((DefaultMutableTreeNode)mTable.get("已激活_" + mString)).add(new DefaultMutableTreeNode(d));
/*     */         } else {
/* 126 */           DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(mString);
/* 127 */           mTable.put("已激活_" + mString, pNode);
/* 128 */           jhNode.add(pNode);
/* 129 */           pNode.add(new DefaultMutableTreeNode(d));
/*     */         }
/*     */       }
/* 132 */       else if (mTable.containsKey("已停止_" + mString)) {
/* 133 */         ((DefaultMutableTreeNode)mTable.get("已停止_" + mString)).add(new DefaultMutableTreeNode(d));
/*     */       } else {
/* 135 */         DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(mString);
/* 136 */         mTable.put("已停止_" + mString, pNode);
/* 137 */         tzNode.add(pNode);
/* 138 */         pNode.add(new DefaultMutableTreeNode(d));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public DefaultMutableTreeNode getNodeByDept(Ecard e)
/*     */   {
/* 145 */     DefaultMutableTreeNode resultNode = null;
/* 146 */     Enumeration deptEnum = this.rootNode.depthFirstEnumeration();
/* 147 */     String val = e.getEcard_key();
/* 148 */     while (deptEnum.hasMoreElements()) {
/* 149 */       DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode)deptEnum.nextElement();
/* 150 */       if ((tmpNode.getUserObject() instanceof Ecard)) {
/* 151 */         Ecard e1 = (Ecard)tmpNode.getUserObject();
/* 152 */         String field_val = e1.getEcard_key();
/* 153 */         if (val.equals(field_val)) {
/* 154 */           resultNode = tmpNode;
/* 155 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 159 */     return resultNode;
/*     */   }
/*     */   
/*     */   public void rebuildTree() {
/* 163 */     this.list = CommUtil.fetchEntities(this.sql);
/* 164 */     if (this.type.equals("manager")) {
/* 165 */       buildTree_manager(this.list);
/*     */     } else {
/* 167 */       buildTree(this.list);
/*     */     }
/*     */   }
/*     */ }

