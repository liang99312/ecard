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
/*    */ public class EcardPosPlugin
/*    */   extends CommTask
/*    */ {
/*    */   public String getGroupCode()
/*    */   {
/* 17 */     return "信用卡管理";
/*    */   }
/*    */   
/*    */   public Class getModuleClass()
/*    */   {
/* 22 */     return EcardPosPanel.class;
/*    */   }
/*    */   
/*    */   public Object getClassName()
/*    */   {
/* 27 */     return "";
/*    */   }
/*    */ }


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\EcardPosPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */