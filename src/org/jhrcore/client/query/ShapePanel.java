/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ShapePanel.java
 *
 * Created on 2009-2-24, 21:28:10
 */
package org.jhrcore.client.query;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Rotation;
import org.jhrcore.entity.query.QueryAnalysisField;
import org.jhrcore.entity.query.QueryAnalysisScheme;

/**
 *
 * @author Administrator
 */
public class ShapePanel extends javax.swing.JPanel {
    private QueryAnalysisScheme cur_QueryAnalysisScheme;
    private QATableModel model;
    private List<String> x_items = new ArrayList<String>();
    private List<String> y_items = new ArrayList<String>();
    private Hashtable<String, Integer> ht_field_column = new Hashtable<String, Integer>();
    private String chart_flag = "Pie";
    private boolean d3_flag = false;

    protected void createChart(String title) {
        if (jComboBox1.getSelectedItem() == null || jComboBox2.getSelectedItem() == null) {
            return;
        }
        pnlChart.removeAll();
        String x_text = jComboBox1.getSelectedItem().toString();
        String y_text = jComboBox2.getSelectedItem().toString();
        int x_ind = ht_field_column.get(jComboBox1.getSelectedItem().toString());
        int y_ind = ht_field_column.get(jComboBox2.getSelectedItem().toString());
        JFreeChart chart = null;
        if (chart_flag.equals("Pie")) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            for (int row = 0; row < model.getRowCount(); row++) {
                Object[] tmp_objs = model.getObjects().get(row);
                dataset.setValue(tmp_objs[x_ind] == null ? "" : tmp_objs[x_ind].toString(), //row);
                        Float.valueOf((tmp_objs[y_ind] == null || tmp_objs[y_ind].toString().trim().equals("")) ? "0" : tmp_objs[y_ind].toString()));

            }
            if (d3_flag) {
                chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
                PiePlot3D pieplot3d = (PiePlot3D) chart.getPlot();
                //设置开始角度
                pieplot3d.setStartAngle(150D);
                //设置方向为”顺时针方向“
                pieplot3d.setDirection(Rotation.CLOCKWISE);
                //设置透明度，0.5F为半透明，1为不透明，0为全透明
                pieplot3d.setForegroundAlpha(0.5F);
            } else {
                chart = ChartFactory.createRingChart(title, dataset, true, true, true);
                chart = ChartFactory.createPieChart(title, dataset, true, true, false);
            }
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}{2}", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
            plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            plot.setNoDataMessage("No data available");
            plot.setCircular(true);
            plot.setLabelGap(0.02);
            LegendTitle lt = chart.getLegend();
            lt.setPosition(RectangleEdge.RIGHT);
            lt.setHorizontalAlignment(HorizontalAlignment.LEFT);
            chart.setBackgroundPaint(new Color(199, 237, 204));
            pnlChart.add(new ChartPanel(chart), BorderLayout.CENTER);
        } else if (chart_flag.equals("Line") || chart_flag.equals("Bar") || chart_flag.equals("Area")) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            if (chart_flag.equals("Line")) {
                for (int row = 0; row < model.getRowCount(); row++) {
                    Object[] tmp_objs = model.getObjects().get(row);
                    dataset.setValue(Float.valueOf((tmp_objs[y_ind] == null || tmp_objs[y_ind].toString().trim().equals("")) ? "0" : tmp_objs[y_ind].toString()), y_text, tmp_objs[x_ind] == null ? "" : tmp_objs[x_ind].toString());
                }
            } else {
                for (int row = 0; row < model.getRowCount(); row++) {
                    Object[] tmp_objs = model.getObjects().get(row);
                    dataset.setValue(Float.valueOf((tmp_objs[y_ind] == null || tmp_objs[y_ind].toString().trim().equals("")) ? "0" : tmp_objs[y_ind].toString()), tmp_objs[x_ind] == null ? "" : tmp_objs[x_ind].toString(), y_text);
                }
            }

            if (chart_flag.equals("Line")) {
                if (d3_flag) {
                    chart = ChartFactory.createLineChart3D(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                } else {
                    chart = ChartFactory.createLineChart(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                }
                CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
                LineAndShapeRenderer localLineAndShapeRenderer=(LineAndShapeRenderer) categoryPlot.getRenderer();
                localLineAndShapeRenderer.setBaseShapesVisible(true);
                localLineAndShapeRenderer.setDrawOutlines(true);
                localLineAndShapeRenderer.setUseFillPaint(true);
                localLineAndShapeRenderer.setBaseFillPaint(Color.green);
                //把ValueMarker的第一个参数该成你要的均值
                ValueMarker valueMarker = new ValueMarker(7D, new Color(200,
                    200, 255), new BasicStroke(1.0F), new Color(200, 200, 255),
                        new BasicStroke(1.0F), 1.0F);
                categoryPlot.addRangeMarker(valueMarker, Layer.BACKGROUND);
                CategoryTextAnnotation categoryTextAnnotation = new CategoryTextAnnotation(
				"杨周基准线", "X坐标的一个值", 8D);
                categoryTextAnnotation.setCategoryAnchor(CategoryAnchor.END);
                categoryTextAnnotation.setFont(new Font("SansSerif", 0, 12));
                categoryTextAnnotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
                categoryPlot.addAnnotation(categoryTextAnnotation);

            } else if (chart_flag.equals("Bar")) {
                if (d3_flag) {
                    chart = ChartFactory.createBarChart3D(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                } else {
                    chart = ChartFactory.createBarChart(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                }
                CategoryPlot categoryPlot=(CategoryPlot) chart.getPlot();
                BarRenderer barRenderer=(BarRenderer) categoryPlot.getRenderer();
                barRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
                barRenderer.setBaseItemLabelsVisible(true);
                barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
            } else if (chart_flag.equals("Area")) {
                chart = ChartFactory.createAreaChart(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
            }
            CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
            CategoryAxis categoryAxis = categoryPlot.getDomainAxis();
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            categoryPlot.setNoDataMessage("没有显示数据");
            pnlChart.add(new ChartPanel(chart), BorderLayout.CENTER);
        }

        pnlChart.updateUI();
    }
    public ShapePanel(QueryAnalysisScheme cur_QueryAnalysisScheme, QATableModel model) {
        initComponents();
        this.cur_QueryAnalysisScheme = cur_QueryAnalysisScheme;
        this.model = model;

        x_items.clear();
        y_items.clear();
        ht_field_column.clear();
        int ind = 0;
        for (QueryAnalysisField queryAnalysisField : cur_QueryAnalysisScheme.getQueryAnalysisFields()) {
            if (queryAnalysisField.getStat_type().equals("普通")) {
                x_items.add(queryAnalysisField.getField_caption());
            } else {
                y_items.add(queryAnalysisField.getField_caption());
            }
            ht_field_column.put(queryAnalysisField.getField_caption(), ind);
            ind++;
        }

        SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, x_items, jComboBox1).bind();
        SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, y_items, jComboBox2).bind();

        createChart("");
        setupEvents();
    }
    private void setupEvents() {
        jrbLine.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chart_flag = "Line";
                createChart("");
            }
        });
        jrbBar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chart_flag = "Bar";
                createChart("");
            }
        });
        jcb3d.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                d3_flag = !d3_flag;
                createChart("");
            }
        });
        jrbPie.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chart_flag = "Pie";
                createChart("");
            }
        });
        jrbArea.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chart_flag = "Area";
                createChart("");
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jrbBar = new javax.swing.JRadioButton();
        jrbPie = new javax.swing.JRadioButton();
        jrbLine = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        pnlChart = new javax.swing.JPanel();
        jrbArea = new javax.swing.JRadioButton();
        jcb3d = new javax.swing.JCheckBox();

        buttonGroup1.add(jrbBar);
        jrbBar.setText("直方图");
        jrbBar.setName("jRadioButton1"); // NOI18N

        buttonGroup1.add(jrbPie);
        jrbPie.setSelected(true);
        jrbPie.setText("饼图");

        buttonGroup1.add(jrbLine);
        jrbLine.setText("曲线图");

        jLabel1.setText("X:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Y:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        pnlChart.setLayout(new java.awt.GridLayout(1, 0));

        buttonGroup1.add(jrbArea);
        jrbArea.setText("区域图");

        jcb3d.setText("3D");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jrbBar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrbPie)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrbLine)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrbArea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jcb3d)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(176, 176, 176))
            .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbBar)
                    .addComponent(jrbPie)
                    .addComponent(jrbLine)
                    .addComponent(jLabel1)
                    .addComponent(jrbArea)
                    .addComponent(jcb3d)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JCheckBox jcb3d;
    private javax.swing.JRadioButton jrbArea;
    private javax.swing.JRadioButton jrbBar;
    private javax.swing.JRadioButton jrbLine;
    private javax.swing.JRadioButton jrbPie;
    private javax.swing.JPanel pnlChart;
    // End of variables declaration//GEN-END:variables
}
