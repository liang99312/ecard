/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.query;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JOptionPane;
import org.jhrcore.client.CommUtil;
import org.jhrcore.ui.WizardModel;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.query.QueryAnalysisScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.queryanalysis.PickFieldSelectPanel;
import org.jhrcore.queryanalysis.QueryConditionPanel;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Owner
 */
public class QuerySchemeWizardModel implements WizardModel {

    protected QueryAnalysisScheme queryAnalysisScheme;
    protected QueryScheme queryScheme;

    public QueryAnalysisScheme getQueryAnalysisScheme() {
        return queryAnalysisScheme;
    }

    public QuerySchemeWizardModel(QueryAnalysisScheme queryAnalysisScheme) {
        this.queryAnalysisScheme = queryAnalysisScheme;
        String scheme_type = "查询统计(" + queryAnalysisScheme.getQueryAnalysisScheme_key() + ")";
        queryScheme = (QueryScheme) CommUtil.fetchEntityBy("from QueryScheme qs left join fetch qs.conditions where qs.scheme_type='" + scheme_type + "'");
        if (queryScheme == null) {
            queryScheme = (QueryScheme) UtilTool.createUIDEntity(QueryScheme.class);
        }
    }

    @Override
    public int getTotalStep() {
        return 3;
    }

    @Override
    public String getWizardName() {
        return "查询方案向导";
    }

    @Override
    public WizardPanel getPanel(int step) {
        if (step == 0) {
            return new QueryAnalysisNamePanel(queryAnalysisScheme);
        }
        if (step == 1) {
            return new PickFieldSelectPanel(queryAnalysisScheme);
        }
        if (step == 2) {
            String entityname = queryAnalysisScheme.getModuleInfo().getQuery_entity_name();
            entityname = entityname.substring(entityname.lastIndexOf(".") + 1);
            queryScheme.setQueryEntity(entityname);
            return new QueryConditionPanel(queryScheme);
        }
        return null;
    }

    @Override
    public Point getLocation() {
        return new Point(10, 10);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(610, 500);
    }

    @Override
    public void afterFinished() {
        queryScheme.setScheme_type("查询统计(" + queryAnalysisScheme.getQueryAnalysisScheme_key() + ")");
        ValidateSQLResult result = CommImpl.saveQueryAnalysisScheme(queryAnalysisScheme, queryScheme);
        if (result.getResult() == 0) {
            JOptionPane.showMessageDialog(null, "保存成功");
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }
}
