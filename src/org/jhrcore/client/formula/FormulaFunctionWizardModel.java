/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.formula;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import org.jhrcore.ui.WizardModel;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.base.TempFieldInfo;

/**
 *
 * @author mxliteboss
 */
public class FormulaFunctionWizardModel implements WizardModel {

    private CreateNewFunctionParam createNewFunctionParam = new CreateNewFunctionParam();
    
    public FormulaFunctionWizardModel(List<TempFieldInfo> function_fields) {
        createNewFunctionParam.setFun_fields(function_fields);
    }
    @Override
    public int getTotalStep() {
        return 2;
    }
    @Override
    public String getWizardName() {
        return "函数使用向导";
    }
    @Override
    public WizardPanel getPanel(int step) {
        if (step == 0) {
            return new FunctionSelectPanel(createNewFunctionParam);
        }
        return new FunctionCreatePanel(createNewFunctionParam);
    }
    @Override
    public Point getLocation() {
        return new Point(200, 100);
    }
    @Override
    public Dimension getSize() {
        return new Dimension(600, 320);
    }
    @Override
    public void afterFinished() {        
    }

    public CreateNewFunctionParam getCreateNewFunctionParam() {
        return createNewFunctionParam;
    }
    
}

