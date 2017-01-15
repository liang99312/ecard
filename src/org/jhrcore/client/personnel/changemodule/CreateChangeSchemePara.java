/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel.changemodule;

import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.query.QueryScheme;

/**
 *
 * @author hflj
 */
public class CreateChangeSchemePara {
    private ChangeScheme changeScheme;
    private QueryScheme queryScheme;

    public ChangeScheme getChangeScheme() {
        return changeScheme;
    }

    public void setChangeScheme(ChangeScheme changeScheme) {
        this.changeScheme = changeScheme;
    }

    public QueryScheme getQueryScheme() {
        return queryScheme;
    }

    public void setQueryScheme(QueryScheme queryScheme) {
        this.queryScheme = queryScheme;
    }
    
}
