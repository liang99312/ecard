/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.changemodule;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.ui.WizardModel;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.change.*;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Administrator
 */
public class ChangeSchemeWizardModel implements WizardModel, IModuleCode {

    private ChangeScheme changeScheme;
    private PickChangeItemPanel1 pickChangeItemPanel;
    private boolean includePersonType = false;
    private PickPersonAppendix pickPersonAppendix;
    private PickImportValuePanel pickImportValuePanel;
    private PickChangePersonPanel pickChangeReportPanel;
    private PickUpdateFieldPanel pickUpdateFieldPanel;
    private Hashtable<String, Object> exist_keys = new Hashtable<String, Object>();
    private CreateChangeSchemePara createPara;
    private List<IPickWindowCloseListener> listeners = new ArrayList<IPickWindowCloseListener>();
    private String module_code = "EmpChangeScheme.btnEdit";

    public void addPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public void delPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public ChangeSchemeWizardModel(ChangeScheme changeScheme) {
        createPara = new CreateChangeSchemePara();
        this.changeScheme = changeScheme;
        createPara.setChangeScheme(changeScheme);
        if (changeScheme.getNew_flag() == 0) {
            for (ChangeField changeField : changeScheme.getChangeFields()) {
                exist_keys.put(changeField.getChangeField_key(), changeField);
            }
            for (ChangeItem changeItem : changeScheme.getChangeItems()) {
                exist_keys.put(changeItem.getChangeItem_key(), changeItem);
            }
//            for (ChangeMethod changeMethod : changeScheme.getChangeMethods()) {
//                exist_keys.put(changeMethod.getChangeMethod_key(), changeMethod);
//            }
        }
    }

    @Override
    public int getTotalStep() {
        return includePersonType ? 6 : 5;
    }

    @Override
    public String getWizardName() {
        return EmpChangeSchemeMsg.msg025.toString();
    }

    @Override
    public WizardPanel getPanel(int step) {
        if (step != 0) {
            includePersonType = changeScheme.contains("a0191");
        }
        if (step == 0) {
            if (pickChangeItemPanel == null) {
                pickChangeItemPanel = new PickChangeItemPanel1(createPara);
            }
            return pickChangeItemPanel;
        }
        if (!includePersonType) {
            step = step + 1;
        }

        if (step == 1) {
            return new PickNewPersonClass2(changeScheme);
        }
        if (step == 2) {
            if (pickPersonAppendix == null) {
                pickPersonAppendix = new PickPersonAppendix(createPara);
            } else {
                pickPersonAppendix.reload();
            }
            return pickPersonAppendix;
        }
        if (step == 3) {
            if (pickImportValuePanel == null) {
                pickImportValuePanel = new PickImportValuePanel(createPara);
            } else {
                pickImportValuePanel.rebuild();
            }
            return pickImportValuePanel;
        }
        if (step == 4) {
            if (pickUpdateFieldPanel == null) {
                pickUpdateFieldPanel = new PickUpdateFieldPanel(createPara);
            } else {
                pickUpdateFieldPanel.rebuild();
            }
            return pickUpdateFieldPanel;
        }
        if (pickChangeReportPanel == null) {
            pickChangeReportPanel = new PickChangePersonPanel(createPara);
        }
        return pickChangeReportPanel;
    }

    @Override
    public Point getLocation() {
        return new Point(10, 10);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(800, 650);
    }

    @Override
    public void afterFinished() {
        boolean tableUpdate = changeScheme.getNew_flag() == 1;
        if (changeScheme.getNew_flag() == 1) {
            changeScheme.setChangeScheme_no(SysUtil.objToInt(CommUtil.fetchNewNoBy("EmpScheme", 1)));
        } else {
            for (ChangeField cf : changeScheme.getChangeFields()) {
                cf.setChangeScheme(changeScheme);
                if (exist_keys.get(cf.getChangeField_key()) == null) {
                    tableUpdate = true;
                }
            }
            for (ChangeItem ci : changeScheme.getChangeItems()) {
                ci.setChangeScheme(changeScheme);
                if (exist_keys.get(ci.getChangeItem_key()) == null) {
                    tableUpdate = true;
                }
            }
            for (ChangeMethod cm : changeScheme.getChangeMethods()) {
                cm.setChangeScheme(changeScheme);
            }
        }
        QueryScheme qs = createPara.getQueryScheme();
        if (qs.getConditions().isEmpty()) {
            qs = null;
            changeScheme.setQueryScheme_key(null);
        } else {
            changeScheme.setQueryScheme_key(qs.getQueryScheme_key());
        }
        ValidateSQLResult validateSQLResult = RSImpl.saveChangeScheme(changeScheme, qs, tableUpdate, UserContext.role_id);
        if (validateSQLResult.getResult() != 0) {
            MsgUtil.showHRSaveErrorMsg(validateSQLResult);
            changeScheme = null;
        } else {
            changeScheme.setNew_flag(0);
        }
        for (IPickWindowCloseListener listener : listeners) {
            listener.pickClose();
        }
    }

    public ChangeScheme getChangeScheme() {
        return changeScheme;
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
