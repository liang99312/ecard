package org.jhrcore.client.ecard.listener;

import org.jhrcore.entity.ecard.Ecard;

public abstract interface IBuildDataSetListener
{
  public abstract void buildData(String paramString, Ecard paramEcard, int paramInt);
  
  public abstract void deleteData(Ecard paramEcard);
}


/* Location:              E:\cspros\weifu\ecard_backup\hrserver\hrclient.jar!\org\jhrcore\client\ecard\listener\IBuildDataSetListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */