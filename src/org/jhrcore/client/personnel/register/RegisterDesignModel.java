/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.register;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.jhrcore.client.CommUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.client.UserContext;
import org.jhrcore.ui.WizardModel;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpRegisterMsg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class RegisterDesignModel implements WizardModel, IModuleCode {

    private RegisterDesignPara registerDesignPara;
    private RegisterDesignViewPanel registerDesignViewPanel;
    private RegisterDesignDeptPanel registerDesignDeptPanel;
    private HrLog log = new HrLog("EmpRegister.入职登记设置");
    private String module_code = "EmpRegister.menuRegister";

    public RegisterDesignModel() {
        registerDesignPara = new RegisterDesignPara();
        SysParameter register_class_para = null;
        SysParameter register_appendix_para = null;
        SysParameter register_field_para = null;
        SysParameter register_a0177_para = null;
        SysParameter register_check_para = null;
        List list = CommUtil.fetchEntities("from SysParameter sp where sp.sysParameter_key in('Register.person_class','Register.appendix','Register.field_flag','Register.a0177_flag','Register.check_flag')");
        if (list != null) {
            for (Object obj : list) {
                SysParameter sp = (SysParameter) obj;
                if (sp.getSysParameter_key().equals("Register.person_class")) {
                    register_class_para = sp;
                } else if (sp.getSysParameter_key().equals("Register.appendix")) {
                    register_appendix_para = sp;
                } else if (sp.getSysParameter_key().equals("Register.field_flag")) {
                    register_field_para = sp;
                } else if (sp.getSysParameter_key().equals("Register.check_flag")) {
                    register_check_para = sp;
                } else {
                    register_a0177_para = sp;
                }
            }
        }
        if (register_class_para == null) {
            register_class_para = new SysParameter();
            register_class_para.setSysParameter_key("Register.person_class");
            register_class_para.setSysparameter_roleid(UserContext.person_code);
            register_class_para.setSysparameter_code("Register.person_class");
            register_class_para.setSysparameter_name("入职登记人员类别设置");
            register_class_para.setSysparameter_value("");
        }
        if (register_appendix_para == null) {
            register_appendix_para = new SysParameter();
            register_appendix_para.setSysParameter_key("Register.appendix");
            register_appendix_para.setSysparameter_roleid(UserContext.person_code);
            register_appendix_para.setSysparameter_code("Register.appendix");
            register_appendix_para.setSysparameter_name("入职登记录入附表设置");
            register_appendix_para.setSysparameter_value("");
        }
        if (register_field_para == null) {
            register_field_para = new SysParameter();
            register_field_para.setSysParameter_key("Register.field_flag");
            register_field_para.setSysparameter_roleid(UserContext.person_code);
            register_field_para.setSysparameter_code("Register.field_flag");
            register_field_para.setSysparameter_name("入职登记浏览字段输入设置");
            register_field_para.setSysparameter_value("0");
        }
        if (register_a0177_para == null) {
            register_a0177_para = new SysParameter();
            register_a0177_para.setSysParameter_key("Register.a0177_flag");
            register_a0177_para.setSysparameter_roleid(UserContext.person_code);
            register_a0177_para.setSysparameter_code("Register.a0177_flag");
            register_a0177_para.setSysparameter_name("入职登记身份证重复允许录入设置");
            register_a0177_para.setSysparameter_value("0");
        }
        if (register_check_para == null) {
            register_check_para = new SysParameter();
            register_check_para.setSysParameter_key("Register.check_flag");
            register_check_para.setSysparameter_roleid(UserContext.person_code);
            register_check_para.setSysparameter_code("Register.check_flag");
            register_check_para.setSysparameter_name("入职登记是否需要审批");
            register_check_para.setSysparameter_value("0");
        }
        registerDesignPara.setRegister_appendix_para(register_appendix_para);
        registerDesignPara.setRegister_class_para(register_class_para);
        registerDesignPara.setRegister_field_para(register_field_para);
        registerDesignPara.setRegister_a0177_para(register_a0177_para);
        registerDesignPara.setRegister_check_para(register_check_para);
        String ss_code = "'@@'";
        if (register_class_para.getSysparameter_value() != null) {
            for (String code : register_class_para.getSysparameter_value().split(";")) {
                ss_code += ",'RegisterDesign." + code + "'";
            }
        }
        registerDesignPara.setShowSchemes(CommUtil.fetchEntities("from ShowScheme ss left join fetch ss.showSchemeDetails where ss.entity_name in(" + ss_code + ")"));
    }

    @Override
    public int getTotalStep() {
        return 3;
    }

    @Override
    public String getWizardName() {
        return EmpRegisterMsg.ttl010.toString();
    }

    @Override
    public WizardPanel getPanel(int step) {
        if (step == 0) {
            return new RegisterDesignParaPanel(registerDesignPara);
        }
        if (step == 1) {
            if (registerDesignViewPanel == null) {
                registerDesignViewPanel = new RegisterDesignViewPanel(registerDesignPara);
            }
            return registerDesignViewPanel;
        }
        if (registerDesignDeptPanel == null) {
            registerDesignDeptPanel = new RegisterDesignDeptPanel(registerDesignPara);
        }
        return registerDesignDeptPanel;
    }

    @Override
    public Point getLocation() {
        return new Point(200, 100);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(700, 500);
    }

    @Override
    public void afterFinished() {
        List paras = new ArrayList();
        paras.add(registerDesignPara.getRegister_a0177_para());
        paras.add(registerDesignPara.getRegister_appendix_para());
        paras.add(registerDesignPara.getRegister_class_para());
        paras.add(registerDesignPara.getRegister_field_para());
        paras.add(registerDesignPara.getRegister_check_para());
        ValidateSQLResult validateSQLResult = RSImpl.saveRegisterDesign(paras, registerDesignPara.getShowSchemes(), registerDesignPara.getRegister_depts());
        if (validateSQLResult.getResult() == 0) {
            log.info("$入职登记设置保存成功");
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
        } else {
            log.info("$入职登记设置保存失败");
            MsgUtil.showHRSaveErrorMsg(validateSQLResult);
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
