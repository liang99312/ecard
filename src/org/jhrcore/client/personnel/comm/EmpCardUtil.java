/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.comm;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.log4j.Logger;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.personnel.ICCardRead;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.Code;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.StringUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author hflj
 */
public class EmpCardUtil implements IModuleCode {

    private static Logger log = Logger.getLogger(EmpCardUtil.class.getName());
    private final String module_code = "DeptMng.ftable";

    public static void WriteA01(A01 a01) {
        if (a01 == null || a01.getA01_key() == null) {
            return;
        }
        a01 = (A01) CommUtil.fetchEntityBy("from A01 a01 join fetch a01.deptCode where a01_key='" + a01.getA01_key() + "'");
        try {
            String ic_str = ConfigManager.getConfigManager().getProperty("ic_card_no");
            ICCardRead r = new ICCardRead();
            String dept_code = a01.getDeptCode().getDept_code();
            String content = a01.getDeptCode().getContent().replace(" ", "");
            if (dept_code.getBytes("GBK").length > 16) {
                dept_code = StringUtil.bSubString(dept_code, 16);
            } else {
                while (dept_code.getBytes("GBK").length < 16) {
                    dept_code += " ";
                }
            }
            String h_dept_code = StringUtil.toHexString(dept_code);
            while (h_dept_code.length() < 32) {
                h_dept_code += "0";
            }

            if (content.getBytes("GBK").length > 48) {
                content = StringUtil.bSubString(content, 48);
            } else {
                while (content.getBytes("GBK").length < 48) {
                    content += " ";
                }
            }

            String h_content = StringUtil.toHexString(content);
            while (h_content.length() < 96) {
                h_content += "0";
            }
            SysParameter sp = null;
            Object object = CommUtil.fetchEntityBy("from SysParameter where sysParameter_key = 'ICCardPosition'");
            if (object == null) {
//                JOptionPane.showMessageDialog(null, "请到工具的系统设置中设置IC岗位字段");
                MsgUtil.showInfoMsg(DeptMngMsg.msgSysForIC);
                return;
            } else {
                sp = (SysParameter) object;
            }
            String field_name = sp.getSysparameter_value().replace(" ", "").toLowerCase();
            if ("".equals(field_name)) {
//                JOptionPane.showMessageDialog(null, "请到工具的系统设置中设置IC岗位字段");
                MsgUtil.showInfoMsg(DeptMngMsg.msgSysForIC);
                return;
            }
            String field_name_s = "";
            List<TempFieldInfo> list = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
            for (TempFieldInfo tfi : list) {
                if (tfi.getField_name().toLowerCase().startsWith(field_name)) {
                    field_name_s = tfi.getField_name().toLowerCase();
                    break;
                }
            }
            if ("".equals(field_name_s)) {
//                JOptionPane.showMessageDialog(null, "请到工具的系统设置中设置IC岗位字段");
                MsgUtil.showInfoMsg(DeptMngMsg.msgSysForIC);
                return;
            }
            String p_str = "";
            String p_code = "";
            Object object2 = null;
            Method method;
            method = A01.class.getMethod("get" + field_name_s.substring(0, 1).toUpperCase() + field_name_s.substring(1), new Class[]{});
            object2 = method.invoke(a01, new Object[]{});
            if (object2 != null) {
                if (object2 instanceof Code) {
                    Code code = (Code) object2;
                    p_str = code.getCode_name();
                    p_code = code.getCode_id();
                } else {
                    p_str = object2.toString();
                    p_code = object2.toString();
                }
            }

            String a0107 = a01.getA0107();
            if ("01".equals(a01.getA0107()) || "1".equals(a01.getA0107())) {
                a0107 = "男";
            }
            if ("02".equals(a01.getA0107()) || "2".equals(a01.getA0107())) {
                a0107 = "女";
            }
            String a0190 = a01.getA0190().replace(" ", "");
            if (a0190.getBytes("GBK").length > 12) {
                a0190 = StringUtil.bSubString(a0190, 12);
            } else {
                while (a0190.getBytes("GBK").length < 12) {
                    a0190 += " ";
                }
            }

            String a0101 = a01.getA0101().replace(" ", "");
            if (a0101.getBytes("GBK").length > 12) {
                a0101 = StringUtil.bSubString(a0101, 12);
            } else {
                while (a0101.getBytes("GBK").length < 12) {
                    a0101 += " ";
                }
            }

            if (p_str.getBytes("GBK").length > 20) {
                p_str = StringUtil.bSubString(p_str, 20);
            } else {
                while (p_str.getBytes("GBK").length < 20) {
                    p_str += " ";
                }
            }

            if (a0107.getBytes("GBK").length > 2) {
                a0107 = StringUtil.bSubString(a0107, 2);
            } else {
                while (a0107.getBytes("GBK").length < 2) {
                    a0107 += " ";
                }
            }
            String person_info = a0190 + a0101 + p_str + a0107;
            String h_person_info = StringUtil.toHexString(person_info);
//            System.out.println(h_person_info);
            while (h_person_info.length() < 96) {
                h_person_info += "0";
            }

            if (p_code.getBytes("GBK").length > 16) {
                p_code = StringUtil.bSubString(p_code, 16);
            } else {
                while (p_code.getBytes("GBK").length < 16) {
                    p_code += " ";
                }
            }

            String h_p_code = StringUtil.toHexString(p_code);
            while (h_p_code.length() < 32) {
                h_p_code += "0";
            }

            String result = r.setInfo(ic_str, "6", "0", h_dept_code);
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }

            result = r.setInfo(ic_str, "6", "1", h_p_code);
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }

            result = r.setInfo(ic_str, "7", "0", h_person_info.substring(0, 32));
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }
            result = r.setInfo(ic_str, "7", "1", h_person_info.substring(32, 64));
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }
            result = r.setInfo(ic_str, "7", "2", h_person_info.substring(64, 96));
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }

            result = r.setInfo(ic_str, "8", "0", h_content.substring(0, 32));
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }
            result = r.setInfo(ic_str, "8", "1", h_content.substring(32, 64));
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }
            result = r.setInfo(ic_str, "8", "2", h_content.substring(64, 96));
            if (result.startsWith("错误:")) {
                MsgUtil.showInfoMsg(result);
                return;
            }
            MsgUtil.showInfoMsg(result);
        } catch (UnsupportedEncodingException ex) {
            log.error(ex);
        } catch (NoSuchMethodException ex) {
            log.error(ex);
        } catch (SecurityException ex) {
            log.error(ex);
        } catch (IllegalAccessException ex) {
            log.error(ex);
        } catch (IllegalArgumentException ex) {
            log.error(ex);
        } catch (InvocationTargetException ex) {
            log.error(ex);
        } catch (Exception ex) {
            log.error(ex);
        }

    }

    public static int WriteA01_mf800(A01 a01, ICCardRead iCCardRead) {
        if (a01 == null || a01.getA01_key() == null) {
            return 1;
        }
        try {
            String ic_str = ConfigManager.getConfigManager().getProperty("ic_card_no");
            ICCardRead r = null;
            if (iCCardRead == null) {
                r = new ICCardRead();
                String s = r.getLink(ic_str);
                if (!"1".equals(s)) {
//                    JOptionPane.showMessageDialog(null, s);
                    MsgUtil.showInfoMsg(s);
                    r = null;
                    return 1;
                }
            } else {
                r = iCCardRead;
            }

            String a0107 = a01.getA0107();
            a0107 = a0107 == null ? "   " : a0107;
            if ("01".equals(a01.getA0107()) || "1".equals(a01.getA0107())) {
                a0107 = "男";
            }
            if ("02".equals(a01.getA0107()) || "2".equals(a01.getA0107())) {
                a0107 = "女";
            }
            String a0190 = "  ";
            if (a01.getA0190() == null || "".equals(a01.getA0190().replace(" ", ""))) {
            } else {
                a0190 = a01.getA0190().replace(" ", "");
            }

            String a0101 = "  ";
            if (a01.getA0101() == null || "".equals(a01.getA0101().replace(" ", ""))) {
            } else {
                a0101 = a01.getA0101().replace(" ", "");
            }

            String h_a0190 = StringUtil.toHexString(a0190);
            String h_a0101 = StringUtil.toHexString(a0101);
            String h_a0107 = StringUtil.toHexString(a0107);
            System.out.println(h_a0190);
            System.out.println(h_a0101);
            System.out.println(h_a0107);
            while (h_a0190.length() < 32) {
                h_a0190 += "0";
            }
            while (h_a0101.length() < 32) {
                h_a0101 += "0";
            }
            while (h_a0107.length() < 32) {
                h_a0107 += "0";
            }

            String result = r.setInfo(ic_str, "0", "1", h_a0190);
            if (result.startsWith("错误:")) {
//                JOptionPane.showMessageDialog(null, result);
                MsgUtil.showInfoMsg(result);
                return 1;
            }
            result = r.setInfo(ic_str, "0", "2", h_a0101);
            if (result.startsWith("错误:")) {
//                JOptionPane.showMessageDialog(null, result);
                MsgUtil.showInfoMsg(result);
                return 1;
            }
            result = r.setInfo(ic_str, "0", "4", h_a0107);
            if (result.startsWith("错误:")) {
//                JOptionPane.showMessageDialog(null, result);
                MsgUtil.showInfoMsg(result);
                return 1;
            }
//            JOptionPane.showMessageDialog(null, "写卡成功");
            MsgUtil.showInfoMsg(DeptMngMsg.msgWriteCard);
            return 0;
        } catch (Exception ex) {
            log.error(ex);
            return 1;
        }

    }

    public static String getIC_no(String card_no) {
        String no_str = "";
        if (card_no == null) {
            return no_str;
        }
        card_no = card_no.replace(" ", "");
        if ("".equals(card_no) || card_no.length() != 8) {
            return no_str;
        }
        String s1 = card_no.substring(0, 2);
        String s2 = card_no.substring(2, 4);
        String s3 = card_no.substring(4, 6);
        String s4 = card_no.substring(6, 8);
        String s = s4 + s3 + s2 + s1;
        Long l = Long.parseLong(s, 16);
        no_str = String.valueOf(l);
        return no_str;
    }

    public static String getCard_no() {
        SysParameter parameter = EmpUtil.getParas().get(EmpUtil.ICNOField);
        return SysUtil.objToStr(parameter.getSysparameter_value());
    }

    public static boolean CheckObject(Object obj, boolean isNew) {
        ValidateSQLResult result1 = CommUtil.entity_triger(obj, isNew);
        if (result1 != null) {
            MsgUtil.showHRSaveErrorMsg(result1);
            return false;
        }
        return true;
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
