/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.comm;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author mxliteboss
 */
public class PersonContainerAction extends AbstractAction {

    public PersonContainerAction() {
    }

    public static PersonContainerAction getAction() {
        return new PersonContainerAction();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        PersonContainer.getPersonContainer().setVisible(true);
    }
}
