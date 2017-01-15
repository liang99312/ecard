/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel;

import org.jhrcore.comm.CodeManager;
import org.jhrcore.comm.ConfigManager;

/**
 *
 * @author Administrator
 */
public class IDCardRead {

    private static String idc_type;
    private String port_str;
    private String p_name = "";
    private String p_sex = "";
    private String p_born = "";
    private String p_grant = "";
    private String p_id = "";
    private String p_sd = "";
    private String p_ed = "";
    private String p_adress = "";
    private String p_nation = "";
    private String p_n = "";
    private String p_s = "";
    
    //姓名
    private native String Syn_Name();
    //性别

    private native String Syn_Sex();
    //民族

    private native String Syn_Nation();
    //出生日期

    private native String Syn_Born();
    //住址

    private native String Syn_Address();
    //身份证号

    private native String Syn_IDCardNo();
    //签发机关

    private native String Syn_GrantDept();
    //身份证起始有效时间

    private native String Syn_UserLifeBegin();
    //身份证失效时间

    private native String Syn_UserLifeEnd();
    //

    private native String Syn_Reserved();

    private native String Syn_PhotoFileName();

    private native String Syn_GetState();

    private native String Syn_GetSAMID();

    private native boolean getLink();

    private native String getLink(String str);

    private native String getInfo(String str);
    //性别

    public IDCardRead() {
        init();
    }

    static {
        idc_type = ConfigManager.getConfigManager().getProperty("id_card_type");
        if ("1".equals(idc_type)) {
            System.loadLibrary("DelphiIDAction");
        } else {
            System.loadLibrary("IDCardReadC");
        }
    }

    public String getSAMID() {
        return Syn_GetSAMID();
    }

    public boolean getLinkM() {
        if ("1".equals(idc_type)) {
            if ("1".equals(getLink(port_str))) {
                return true;
            } else {
                return false;
            }
        } else {
            return getLink();
        }
    }

    public String getPersonName() {
        if ("1".equals(idc_type)) {
            return p_name;
        } else {
            return Syn_Name();
        }

    }

    public String getPersonSex() {
        if ("1".equals(idc_type)) {
            return p_s;
        } else {
            return Syn_Sex();
        }

    }

    public String getPersonBorn() {
        if ("1".equals(idc_type)) {
            return p_born;
        } else {
            return Syn_Born();
        }

    }

    public String getPersonGrantDept() {
        if ("1".equals(idc_type)) {
            return p_grant;
        } else {
            return Syn_GrantDept();
        }

    }

    public String getPersonAddress() {
        if ("1".equals(idc_type)) {
            return p_adress;
        } else {
            return Syn_Address();
        }

    }

    public String getPersonIDCardNo() {
        if ("1".equals(idc_type)) {
            return p_id;
        } else {
            return Syn_IDCardNo();
        }

    }

    public String getPersonUserLifeBegin() {
        if ("1".equals(idc_type)) {
            return p_sd;
        } else {
            return Syn_UserLifeBegin();
        }

    }

    public String getPersonUserLifeEnd() {
        if ("1".equals(idc_type)) {
            return p_ed;
        } else {
            return Syn_UserLifeEnd();
        }

    }

    public String getPersonReserved() {
        return Syn_Reserved();
    }

    public String getPersonPhotoFileName() {
        if ("1".equals(idc_type)) {
            return System.getProperty("user.dir") + "\\zp.bmp";
        } else {
            return Syn_PhotoFileName();
        }

    }

    public String getPersonNation() {
        if ("1".equals(idc_type)) {
            return p_nation;
        } else {
            return Syn_Nation();
        }

    }

    public void init() {
        port_str = ConfigManager.getConfigManager().getProperty("id_card_port");
//        idc_type = ConfigManager.getConfigManager().getProperty("id_card_type");
        if ("1".equals(idc_type)) {
            String result = getInfo(port_str);
            if (result == null || "".equals(result.replace(" ", ""))) {
                return;
            }
            String[] strs = result.split(";");
            if (strs.length < 8) {
                return;
            }
            p_name = strs[0];
            p_sex = strs[1];
            p_s = "";
            p_s = CodeManager.getCodeManager().getCodeIdBy("性别", p_sex.replace(" ", ""));
            p_born = strs[2];
            p_grant = strs[3];
            p_id = strs[4];
            p_sd = strs[5];
            p_ed = strs[6];
            p_adress = strs[7];
            p_n = strs[8];
            p_nation = "";
            p_nation = CodeManager.getCodeManager().getCodeIdBy("民族", p_n.replace(" ", "") + "族");
            if (p_nation == null || "".equals(p_nation)) {
                p_nation = CodeManager.getCodeManager().getCodeIdBy("民族 (GB/T 3304)", p_n.replace(" ", "") + "族");
            }
        }
    }

    public static void main(String args[]) {
//        IDCardRead ic = new IDCardRead();
//        if (ic.getLinkM()) {
//            System.out.println("link.......");
//        } else {
//            System.out.println("not link.......");
//        }
//
//        String name = ic.getPersonName();
//        String name2 = ic.getPersonBorn();
//        String name3 = ic.getPersonAddress();
//        String name4 = ic.getPersonGrantDept();
//        String name5 = ic.getPersonIDCardNo();
//        String name6 = ic.getPersonNation();
//        String name7 = ic.getPersonSex();
//        String name8 = ic.getPersonUserLifeBegin();
//        String name9 = ic.getPersonUserLifeEnd();
//        String name10 = ic.getPersonPhotoFileName();
////
//        System.out.println("name:::" + name + "/" + name2 + "/" + name3 + "/" + name4 + "/" + name5
//                + "/" + name6);
//        System.out.println("name:::" + name6 + "/" + name7 + "/" + name8 + "/" + name9 + "/" + name10
//                + "/" + name6);
//        System.out.println("n:" + name);

    }
}
