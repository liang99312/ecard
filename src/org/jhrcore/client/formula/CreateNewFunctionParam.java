/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.formula;

import java.util.List;
import org.jhrcore.entity.base.TempFieldInfo;

/**
 *
 * @author mxliteboss
 */
public class CreateNewFunctionParam {
    public List fun_infos;
    private List<TempFieldInfo> fun_fields;
    public Object fun_para;
    private FunctionModel functionModel;

    public List<String> getFun_infos() {
        return fun_infos;
    }

    public void setFun_infos(List fun_infos) {
        this.fun_infos = fun_infos;
    }

    public Object getFun_para() {
        return fun_para;
    }

    public void setFun_para(Object fun_para) {
        this.fun_para = fun_para;
    }

    public FunctionModel getFunctionModel() {
        return functionModel;
    }

    public void setFunctionModel(FunctionModel functionModel) {
        this.functionModel = functionModel;
    }

    public List<TempFieldInfo> getFun_fields() {
        return fun_fields;
    }

    public void setFun_fields(List<TempFieldInfo> fun_fields) {
        this.fun_fields = fun_fields;
    }

}
