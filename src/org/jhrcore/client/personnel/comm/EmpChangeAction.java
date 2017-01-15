/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.comm;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.personnel.changemodule.EmpChangeUsePanel;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.change.ChangeItem;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.msg.emp.EmpJdMsg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class EmpChangeAction implements IModuleCode {

    private String module_code = "EmpJd.btnout";

    public static void doEmpChangeAction(JComponent component, ChangeScheme changeScheme, List person_list, Class cur_person_class, final IPickWindowCloseListener listener) {
        if (person_list == null || person_list.isEmpty()) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(component), "请选择变动人员", "提示", JOptionPane.INFORMATION_MESSAGE);
            MsgUtil.showInfoMsg(EmpJdMsg.msg001);
            return;
        }
        JFrame frame = (JFrame) JOptionPane.getFrameForComponent(component);
        boolean include_a0191 = false;
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            if ("a0191".equals(ci.getFieldName())) {
                include_a0191 = true;
                break;
            }
        }
        List<String> keys = new ArrayList<String>();
        for (Object obj : person_list) {
            if (obj instanceof A01) {
                keys.add(((A01) obj).getA01_key());
            } else {
                keys.add(obj.toString());
            }
        }
        if (include_a0191) {
            List arList = CommUtil.selectSQL("select 1 from A01black where a01_key in", keys);
            if (!arList.isEmpty()) {
                MsgUtil.showInfoMsg(EmpJdMsg.msg049);
                return;
            }
        }
        if (cur_person_class.getSimpleName().equals("A01") && include_a0191) {
            if (changeScheme.getNewPersonClassName() != null && !changeScheme.getNewPersonClassName().trim().equals("")) {
                List a0191s = CommUtil.selectSQL("select t.entityName,t.entityCaption from tabname t,EntityClass ec where t.entityClass_key=ec.entityClass_key and ec.entityType_code='CLASS'");
                Hashtable<String, String> a0191_keys = new Hashtable<String, String>();
                for (Object obj : a0191s) {
                    Object[] objs = (Object[]) obj;
                    a0191_keys.put(objs[0].toString(), objs[1].toString());
                }
                changeScheme.setNewPersonClass(a0191_keys.get(changeScheme.getNewPersonClassName()));
                String old_className = changeScheme.getOldPersonClassName();
                if (old_className != null && !old_className.trim().equals("")) {
                    String[] old_classes = old_className.split(";");
                    String old_class = "'@@@'";
                    for (String c : old_classes) {
                        old_class = old_class + ",'" + a0191_keys.get(c) + "'";
                    }
                    changeScheme.setOldPersonClass(old_class);
                }
            }
            person_list = CommUtil.fetchEntities("select a01_key from A01 where a0191 in (" + changeScheme.getOldPersonClass() + ") and a0191<>'" + changeScheme.getNewPersonClass() + "' and a01_key in ", keys);
            if (person_list.isEmpty()) {
//                JOptionPane.showMessageDialog(frame, "当前选择人员无法执行当前调动", "提示", JOptionPane.INFORMATION_MESSAGE);
                MsgUtil.showInfoMsg(EmpJdMsg.msg002);
                return;
            }
            keys.clear();
            keys.addAll(person_list);
        }
        if (changeScheme.getQueryScheme_key() != null && !changeScheme.getQueryScheme_key().trim().equals("")) {
            QueryScheme qs = (QueryScheme) CommUtil.fetchEntityBy("from QueryScheme qs left join fetch qs.conditions where qs.queryScheme_key='" + changeScheme.getQueryScheme_key() + "'");
            if (qs != null && qs.getConditions().size() > 0) {
                String hql = qs.buildHql("from A01 ed");
                person_list = CommUtil.fetchEntities("select ed.a01_key " + hql + " and ed.a01_key in ", keys);
                if (person_list.isEmpty()) {
//                    JOptionPane.showMessageDialog(frame, "当前选择人员无法执行当前调动", "提示", JOptionPane.INFORMATION_MESSAGE);
                    MsgUtil.showInfoMsg(EmpJdMsg.msg002);
                    return;
                }
                keys.clear();
                keys.addAll(person_list);
            }
        }
        if (!"借调".equals(changeScheme.getScheme_type())) {
            List list = CommUtil.selectSQL("select A01_key from A01jd where a01_key in ", keys);
            keys.removeAll(list);
            if (keys.isEmpty()) {
//                JOptionPane.showMessageDialog(frame, "当前选择人员无法执行当前调动", "提示", JOptionPane.INFORMATION_MESSAGE);
                MsgUtil.showInfoMsg(EmpJdMsg.msg002);
                return;
            }
        }
        List data = (List<A01>) CommUtil.fetchEntities("from A01 bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in ", keys);
        EmpChangeUsePanel pnlEcu = new EmpChangeUsePanel(changeScheme, data);
        pnlEcu.addPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                if (listener != null) {
                    listener.pickClose();
                }
            }
        });
        JFrame fm = ModelFrame.showModel(frame, pnlEcu, true, changeScheme.getChangeScheme_name(), ((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50), ((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 30));;
        fm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fm.setLocation(0, 0);
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
