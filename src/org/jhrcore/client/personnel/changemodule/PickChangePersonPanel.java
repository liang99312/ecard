/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PickChangeReportPanel.java
 *
 * Created on 2010-8-30, 11:23:03
 */
package org.jhrcore.client.personnel.changemodule;

import java.util.Date;
import javax.swing.JScrollPane;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.query3.QuerySchemePanel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.ComponentUtil;

/**
 *
 * @author hflj
 */
public class PickChangePersonPanel extends WizardPanel implements IModuleCode {

    private ChangeScheme changeScheme;
    private QuerySchemePanel pnlQuery;
    private QueryScheme queryScheme;
    private CreateChangeSchemePara createPara;
    private String module_code = "EmpChangeScheme.btnEdit.getPanel";

    public PickChangePersonPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form PickChangeReportPanel */
    public PickChangePersonPanel(CreateChangeSchemePara createPara) {
        this.changeScheme = createPara.getChangeScheme();
        this.queryScheme = createPara.getQueryScheme();
        this.createPara = createPara;
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        if (changeScheme.getQueryScheme_key() != null && !changeScheme.getQueryScheme_key().trim().equals("")) {
            queryScheme = (QueryScheme) CommUtil.fetchEntityBy("from QueryScheme qs left join fetch qs.conditions where qs.queryScheme_key='" + changeScheme.getQueryScheme_key() + "'");
        }
        if (queryScheme == null) {
            queryScheme = new QueryScheme();
            queryScheme.setQueryScheme_key("changeScheme_" + changeScheme.getChangeScheme_key());
        }
        queryScheme.setQueryEntity("A01");
        queryScheme.setPerson_code(UserContext.person_code);
        queryScheme.setMake_date(new Date());
        queryScheme.setQuery_type(0);
        pnlQuery = new QuerySchemePanel(A01.class);
        pnlQuery.setQueryScheme(queryScheme);
        add(new JScrollPane(pnlQuery));
    }

    private void setupEvents() {
        ComponentUtil.setSysFuntionNew(this, false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public void beforeLeave() {
        createPara.setQueryScheme(queryScheme);
    }

    @Override
    public String getTitle() {
        return EmpChangeSchemeMsg.msg010.toString();
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
