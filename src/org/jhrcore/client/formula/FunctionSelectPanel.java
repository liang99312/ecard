/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FunctionSelectPanel.java
 *
 * Created on 2009-5-9, 16:08:53
 */
package org.jhrcore.client.formula;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.ui.WizardPanel;
import org.jhrcore.ui.renderer.TableListCellRender;




/**
 *
 * @author mxliteboss
 */
public class FunctionSelectPanel extends WizardPanel {

    private CreateNewFunctionParam createNewFunctionParam;
    private JListBinding function_binding;
    private List<FunctionModel> function_list = new ArrayList<FunctionModel>();

    /** Creates new form FunctionSelectPanel */
    public FunctionSelectPanel(CreateNewFunctionParam para) {
        createNewFunctionParam = para;
        initComponents();
        initOthers();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlsFunction = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText("请选择函数，按【下一步】设置函数所需参数");

        jScrollPane1.setViewportView(jlsFunction);

        jLabel2.setText("说明：");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(61, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(265, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel3)
                .addContainerGap(274, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void initOthers() {
        FunctionModel fd = new FunctionModel();
        fd.setFuntion_code("Tax");
        fd.setFunction_name("Tax(应税工资额，费用扣除标准)");
        fd.setFunction_type("Float");
        fd.setFunction_caption("计算应纳税额");
        function_list.add(fd);
        function_binding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, function_list, jlsFunction);
        function_binding.bind();
        jlsFunction.setCellRenderer(new FunctionListCellRender());
        jlsFunction.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                FunctionModel fm = (FunctionModel)jlsFunction.getSelectedValue();
                jLabel3.setText(fm.getFunction_caption());
            }

        });
    }

    @Override
    public boolean isValidate() {
        if (jlsFunction.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(jlsFunction), "请选择公式");
            return false;
        }
        return true;
    }

    @Override
    public void beforeLeave() {
        FunctionModel fd = (FunctionModel) jlsFunction.getSelectedValue();
        createNewFunctionParam.setFunctionModel(fd);
    }
    @Override
    public String getTitle(){
        return "第一步：选择函数";
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList jlsFunction;
    // End of variables declaration//GEN-END:variables
}

class FunctionListCellRender extends JLabel implements ListCellRenderer {
    //字段图标(整形)

    private static final Icon fieldDefIconI = new ImageIcon(TableListCellRender.class.getResource("/resources/images/fieldDefIconI.png"));
    //字段图标(字符型)
    private static final Icon fieldDefIconS = new ImageIcon(TableListCellRender.class.getResource("/resources/images/fieldDefIconS.png"));
    //字段图标(数字型)
    private static final Icon fieldDefIconN = new ImageIcon(TableListCellRender.class.getResource("/resources/images/fieldDefIconN.png"));
    //字段图标(日期型)
    private static final Icon fieldDefIconD = new ImageIcon(TableListCellRender.class.getResource("/resources/images/fieldDefIconD.png"));
    //字段图标(日期型)
    private static final Icon fieldDefIconB = new ImageIcon(TableListCellRender.class.getResource("/resources/images/fieldDefIconB.png"));

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(value.toString());
        if (value instanceof FunctionModel) {
            FunctionModel fm = (FunctionModel) value;
            if (fm.getFunction_type().equalsIgnoreCase("Integer")) {
                this.setIcon(fieldDefIconI);
            } else if (fm.getFunction_type().equalsIgnoreCase("String")) {
                this.setIcon(fieldDefIconS);
            } else if (fm.getFunction_type().equalsIgnoreCase("Float")) {
                this.setIcon(fieldDefIconN);
            } else if (fm.getFunction_type().equalsIgnoreCase("Date")) {
                this.setIcon(fieldDefIconD);
            } else if (fm.getFunction_type().equalsIgnoreCase("Boolean")) {
                this.setIcon(fieldDefIconB);
            }
        }
        if (isSelected) {
            this.setBackground(new Color(184, 207, 229));
            this.setForeground(Color.WHITE);
        } else {
            this.setForeground(Color.BLACK);
            this.setBackground(Color.WHITE);
        }
        this.setOpaque(true);
        return this;
    }
}
