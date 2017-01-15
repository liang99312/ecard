/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RegisterDesignViewPanel.java
 *
 * Created on 2010-10-9, 11:44:31
 */
package org.jhrcore.client.personnel.register;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.entity.A01;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.ClassAnnotation;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.msg.emp.EmpRegisterMsg;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ShowFieldDialog;
import org.jhrcore.util.ComponentUtil;

/**
 *
 * @author hflj
 */
public class RegisterDesignViewPanel extends WizardPanel implements IModuleCode{

    private RegisterDesignPara registerDesignPara;
    private JComboBoxBinding class_binding;
    private List person_classes = new ArrayList();
    private Logger log = Logger.getLogger(RegisterDesignViewPanel.class.getName());
    private Class empClass = A01.class;
    private Hashtable<String, ShowScheme> scheme_keys = new Hashtable<String, ShowScheme>();
    private String module_code = "EmpRegister.menuRegister.view";

    /** Creates new form RegisterDesignViewPanel */
    public RegisterDesignViewPanel(RegisterDesignPara registerDesignPara) {
        this.registerDesignPara = registerDesignPara;
        for (Object obj : registerDesignPara.getShowSchemes()) {
            scheme_keys.put(((ShowScheme) obj).getEntity_name(), ((ShowScheme) obj));
        }
        initComponents();
        initOthers();
        setupEvents();
    }

    public RegisterDesignViewPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }
    

    private void initOthers() {
        String[] class_str = registerDesignPara.getRegister_class_para().getSysparameter_value().split(";");
        for (String cs : class_str) {
            try {
                Class c = Class.forName("org.jhrcore.entity." + cs);
                ClassAnnotation ca = (ClassAnnotation) c.getAnnotation(ClassAnnotation.class);
                EntityDef ed = (EntityDef) UtilTool.createUIDEntity(EntityDef.class);
                ed.setEntityName(cs);
                ed.setEntityCaption(ca.displayName());
                person_classes.add(ed);
            } catch (ClassNotFoundException ex) {
                log.error(ex);
                continue;
            }
        }
        class_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ, person_classes, jComboBox1);
        class_binding.bind();
    }

    private void setupEvents() {
        jComboBox1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = jComboBox1.getSelectedItem();
                if (obj != null) {
                    EntityDef ed = (EntityDef) obj;
                    try {
                        empClass = Class.forName("org.jhrcore.entity." + ed.getEntityName());
                    } catch (ClassNotFoundException ex) {
                        log.error(ex);
                    }
                    refreshUI(empClass);
                }
            }
        });
        btnFieldSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (empClass == null || empClass.getSuperclass() == null || !empClass.getSuperclass().getSimpleName().equals("A01")) {
                    return;
                }
                ShowFieldDialog sfDlg = new ShowFieldDialog(JOptionPane.getFrameForComponent(btnFieldSet), empClass, new ArrayList(), new ArrayList(), "RegisterDesign", scheme_keys.get("RegisterDesign."+empClass.getSimpleName()),false);
                ContextManager.locateOnMainScreenCenter(sfDlg);
                sfDlg.setVisible(true);
                if (sfDlg.getFields().size() > 0) {
                    scheme_keys.put("RegisterDesign." + empClass.getSimpleName(), sfDlg.getCurShowScheme());
                    refreshUI(empClass);
                }
            }
        });
        if (person_classes.size() > 0) {
            jComboBox1.setSelectedIndex(0);
        }
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void refreshUI(Class empClass) {
        if (empClass == null || empClass.getSuperclass() == null || !empClass.getSuperclass().getSimpleName().equals("A01")) {
            return;
        }
        ShowScheme ss = scheme_keys.get("RegisterDesign." + empClass.getSimpleName());
        jPanel1.removeAll();
        jPanel1.setLayout(new BorderLayout());
        if (ss != null) {
            Object obj = UtilTool.createUIDEntity(empClass);
            BeanPanel beanPanel = new BeanPanel(obj);
            beanPanel.setShow_scheme(ss);
            beanPanel.bind();
            jPanel1.add(new JScrollPane(beanPanel));
        }
        jPanel1.updateUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        btnFieldSet = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setText("人员类别：");
        jToolBar1.add(jLabel1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setMaximumSize(new java.awt.Dimension(120, 32767));
        jToolBar1.add(jComboBox1);

        btnFieldSet.setText("设置录入字段");
        btnFieldSet.setFocusable(false);
        btnFieldSet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFieldSet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnFieldSet);

        jPanel1.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public void beforeLeave() {
        List list = new ArrayList();
        list.addAll(scheme_keys.values());
        registerDesignPara.setShowSchemes(list);
    }

    @Override
    public String getTitle() {
        return EmpRegisterMsg.msg029.toString();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFieldSet;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
