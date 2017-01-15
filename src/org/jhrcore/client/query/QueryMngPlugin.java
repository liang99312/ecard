/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.query;

import org.jhrcore.ui.task.CommTask;

/**
 *
 * @author DB2INST3
 */
public class QueryMngPlugin extends CommTask {

    @Override
    public String getGroupName() {
        return "Query";
    }

    @Override
    public Class getModuleClass() {
        return QueryMngPanel.class;
    }
}
