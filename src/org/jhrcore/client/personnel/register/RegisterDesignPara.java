/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel.register;

import java.util.ArrayList;
import java.util.List;
import org.jhrcore.entity.SysParameter;

/**
 *
 * @author hflj
 */
public class RegisterDesignPara {
    private SysParameter register_class_para;
    private SysParameter register_appendix_para;
    private SysParameter register_field_para;
    private SysParameter register_a0177_para;
    private SysParameter register_check_para;
    private List register_depts;
    private List showSchemes = new ArrayList();

    public SysParameter getRegister_appendix_para() {
        return register_appendix_para;
    }

    public void setRegister_appendix_para(SysParameter register_appendix_para) {
        this.register_appendix_para = register_appendix_para;
    }

    public SysParameter getRegister_class_para() {
        return register_class_para;
    }

    public void setRegister_class_para(SysParameter register_class_para) {
        this.register_class_para = register_class_para;
    }

    public SysParameter getRegister_field_para() {
        return register_field_para;
    }

    public void setRegister_field_para(SysParameter register_field_para) {
        this.register_field_para = register_field_para;
    }

    public SysParameter getRegister_a0177_para() {
        return register_a0177_para;
    }

    public void setRegister_a0177_para(SysParameter register_a0177_para) {
        this.register_a0177_para = register_a0177_para;
    }

    public List getShowSchemes() {
        return showSchemes;
    }

    public void setShowSchemes(List showSchemes) {
        this.showSchemes = showSchemes;
    }

    public SysParameter getRegister_check_para() {
        return register_check_para;
    }

    public void setRegister_check_para(SysParameter register_check_para) {
        this.register_check_para = register_check_para;
    }

    public List getRegister_depts() {
        return register_depts;
    }

    public void setRegister_depts(List register_depts) {
        this.register_depts = register_depts;
    }

}
