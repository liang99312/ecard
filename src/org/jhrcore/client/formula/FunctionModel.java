/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.formula;

/**
 *
 * @author mxliteboss
 */
public class FunctionModel {
    public String funtion_code;
    public String function_name;
    public String function_caption;
    public String function_type;

    public String getFunction_caption() {
        return function_caption;
    }

    public void setFunction_caption(String function_caption) {
        this.function_caption = function_caption;
    }

    public String getFunction_name() {
        return function_name;
    }

    public void setFunction_name(String function_name) {
        this.function_name = function_name;
    }

    public String getFunction_type() {
        return function_type;
    }

    public void setFunction_type(String function_type) {
        this.function_type = function_type;
    }

    public String getFuntion_code() {
        return funtion_code;
    }

    public void setFuntion_code(String funtion_code) {
        this.funtion_code = funtion_code;
    }
    @Override
    public String toString(){
        return function_name;
    }
}
