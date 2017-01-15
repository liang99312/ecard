/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel;

import org.jhrcore.comm.ConfigManager;


/**
 *
 * @author Administrator
 */
public class ICCardRead {

    private static String idc_type;
    
    //获取信息
    private native String ICgetInfo(String str,String sector_no,String block_no);
    //写入信息
    private native String ICsetInfo(String str,String sector_no,String block_no,String info);

    private native String ICgetLink(String str);
    static {
        idc_type = ConfigManager.getConfigManager().getProperty("dukaqi_ic_card_type");
        if ("1".equals(idc_type)) {
            System.loadLibrary("DelphiActionMF_800");
        } else {
            System.loadLibrary("DelphiAction");
        }
    }

    public String getLink(String str){
        return ICgetLink(str);
    }
    public String getInfo(String str,String sector_no,String block_no){
        return ICgetInfo(str,sector_no,block_no);
    }
    public String setInfo(String str,String sector_no,String block_no,String info){
        return ICsetInfo(str,sector_no,block_no,info);
    }    

    public static void main(String args[]){
//        ICCardRead ic = new ICCardRead();
//        if(ic.getLink("6")){
//            System.out.println("link.......");
//        }else{
//            System.out.println("not link.......");
//        }
//        String s = ic.getLink("6");
//        System.out.println(s);E79086E5B7A5E7A88BE5B88800000000
//        System.out.println(ic.getInfo("6","7","0"));BC8CE794B7EFBC8C5A30363034E58AA9

//        String str2 = ic.ICsetInfo("6","7","0","30313030383030332CE69D8EE6BBA8EF");
//        String str21 = ic.ICsetInfo("6","7","1","BC8CE794B7EFBC8C5A30363034E58AA9");
//        System.out.println("Str2::" + str2);30313030373339322CE78E8BE995BFE5869B2C3031
//        String str = ic.getInfo("6","6","0") + ic.getInfo("6","6","1") +ic.getInfo("6","7","0") + ic.getInfo("6","7","1") + ic.getInfo("6","7","2")+ic.getInfo("6","8","0") + ic.getInfo("6","8","1") + ic.getInfo("6","8","2");
        //"6","7","0"   30313030383030332CE69D8EE6BBA8EF
//        str = ic.getInfo("6","7","0");
//        str = str.replace("\n", "");
//        if(str != null){
//            System.out.println(StringUtil.toStringHex(str));
//        }
        
        //5-1-20080514-291628-2654186623
        //5-1-20080514-291628-2654186623

    }
}
