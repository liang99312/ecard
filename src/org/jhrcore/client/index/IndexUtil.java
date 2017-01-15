/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.report.ReportPanel;
import org.jhrcore.entity.SysNotice;
import org.jhrcore.util.DateUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author mxliteboss
 */
public class IndexUtil {

    private static List toolTipData = new ArrayList();
    private static List noticeData = new ArrayList();

    /**
     * 获取提醒信息
     * @param fetch：是否重读
     * @return 
     */
    public static List getToolTipData(boolean fetch) {
        if (!fetch) {
            return toolTipData;
        }
        toolTipData.clear();
        String s_where = " ";
        if (!UserContext.isSA) {
            s_where = " and cm.user_key='" + UserContext.rolea01_key + "'";
        }
        String scheme_hql = "select aes.scheme_name,aes.content,aes.formula,data_flag,show_flag from AutoExcuteScheme aes where aes.scheme_type='警戒提示' and aes.used_flag=1";
        scheme_hql += " and exists(select 1 from CommMap cm where cm.map_key=aes.autoExcuteScheme_key" + s_where + ")";
        scheme_hql += " order by aes.order_no";
        List list = CommUtil.fetchEntities(scheme_hql);
        for (Object obj : list) {
            Object[] data = (Object[]) obj;
            boolean data_flag = SysUtil.objToBoolean(data[3]);
            boolean show_flag = SysUtil.objToBoolean(data[4]);
            Object[] objs = new Object[4];
            objs[0] = data[0];//aes.getScheme_name();
            objs[2] = data[1];//aes;
            objs[3] = data[2];
            //wake_list.add(objs);
            if (data_flag) {
                String sql = (String) data[2];
                sql = ReportPanel.getNewQuery(sql, null);
                String sql2 = null;
                int ind = sql.toUpperCase().lastIndexOf("ORDER BY");
                if (ind > 0) {
                    sql2 = sql.substring(0, ind);
                } else {
                    sql2 = sql;
                }
                sql2 = "select count(*) from (" + sql2 + "\n) a";
//            if (sql.contains("group") || sql.contains("having")) {
//                sql2 = "select count(*) from (" + sql + "\n) a";
//            } else {
//                sql2 = "select count(*) " + sql.substring(sql.indexOf("from"));
//                if (sql2.indexOf("order by") > 0) {
//                    sql2 = sql2.substring(0, sql2.indexOf("order by"));
//                }
//            }
                List list_temp = CommUtil.selectSQL(sql2);
                if (show_flag && (list_temp == null || list_temp.isEmpty() || list_temp.get(0) == null || Integer.valueOf(list_temp.get(0).toString()) == 0)) {
                    continue;
                }
                objs[1] = list_temp.get(0).toString();
            }
            toolTipData.add(objs);
        }
        return toolTipData;
    }

    /**
     * 获取公告信息
     * @param fetch：是否重读
     * @return 
     */
    public static List getNoticeData(boolean fetch) {
        if (!fetch) {
            return noticeData;
        }
        noticeData.clear();
        String hql = "from SysNotice c where c.used=1 and c.sysNoticeType.code='msg'";
        if (!UserContext.isSA) {
            hql += " and (" + UserContext.dept_right_str.replace("d.", "c.deptCode.") + ") ";
        }
        hql += " order by c.deptCode.dept_code asc,c.subdate desc";
        List list = CommUtil.fetchEntities(hql);
        for (Object obj : list) {
            SysNotice sn = (SysNotice) obj;
            Object[] objs = new Object[3];
            objs[0] = sn.getTitle();
            objs[1] = sn.getSubdate();
            objs[2] = sn;
            if (objs[1] != null) {
                objs[1] = DateUtil.DateToStr((Date) objs[1], "yyyy-MM-dd");
            }
            noticeData.add(objs);
        }
        return noticeData;
    }
}
