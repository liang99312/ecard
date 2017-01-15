/*    */ package org.jhrcore.client.ecard;
/*    */ 
/*    */ import org.jhrcore.ui.task.CommTask;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EcardXfPlugin
/*    */   extends CommTask
/*    */ {
/*    */   public String getGroupCode()
/*    */   {
/* 17 */     return "信用卡管理";
/*    */   }
/*    */   
/*    */   public Class getModuleClass()
/*    */   {
/* 22 */     return EcardXfPanel.class;
/*    */   }
/*    */   
/*    */   public Object getClassName()
/*    */   {
/* 27 */     return "";
/*    */   }
/*    */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardXfPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */