/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * QueryShapePanel.java
 *
 * Created on 2009-8-27, 23:30:55
 */
package org.jhrcore.client.query;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
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
import org.jhrcore.util.SysUtil;
import org.jhrcore.queryanalysis.AnalyseField;
import org.jhrcore.util.ChartUtil;

/**
 *
 * @author mxliteboss
 */
public class QueryShapePanel extends javax.swing.JPanel {

    private List<AnalyseField> x_items = new ArrayList<AnalyseField>();
    private List<AnalyseField> y_items = new ArrayList<AnalyseField>();
    private List<AnalyseField> x_fields = new ArrayList<AnalyseField>();
    private AnalyseField x_field;
    private String chart_flag = "Pie";
    private boolean d3_flag = false;
    private List<Object[]> show_data = new ArrayList<Object[]>();
    private int row_index = -1;
    private boolean is_row = false;
    private JComboBoxBinding x_binding;
    private JComboBoxBinding y_binding;
    private ActionListener al_x;
    private ActionListener al_y;
    private boolean x_change_flag = false;
    private DecimalFormat df_num = new DecimalFormat("0.00");
    private DecimalFormat df_int = new DecimalFormat("#");
    private boolean no_x_fields_flag = false;
    private int curIndex = -1;

    protected void createChart(String title) {
        System.out.println("create chart>>>>>>>>>>");
        if (jComboBox1.getSelectedItem() == null || jComboBox2.getSelectedItem() == null) {
            return;
        }
        is_row = jcbRow.isSelected();
        pnlChart.removeAll();
        AnalyseField y_af = (AnalyseField) jComboBox2.getSelectedItem();
        row_index = jComboBox1.getSelectedIndex();
        String x_text = x_field.getField_caption();
        String y_text = y_af.getField_caption();
        JFreeChart chart = null;
        int data_size = show_data.size();
        BigDecimal avg_data = new BigDecimal(0);
        if (data_size == 0) {
            return;
        }
        List<AnalyseField> x_compare_fields = x_fields;
        List<AnalyseField> y_compare_fields = x_items;
        if (no_x_fields_flag) {
            x_compare_fields = x_items;
            if (x_change_flag) {
                y_compare_fields = y_items;
            }
        }
        int x_len = x_compare_fields.size();
        int analyse_index = jComboBox2.getSelectedIndex();
        Object[] row_data = null;
        if (x_change_flag) {
            row_data = new Object[x_len];
            int ind = 0;
            int y_len = x_items.size();
            int row_len = row_index;
            if (no_x_fields_flag) {
                row_len = 0;
                y_len = 1;
            }
            for (Object[] objs : show_data) {
                ind++;
                if ((analyse_index * y_len + row_len + 1) >= objs.length) {
                    continue;
                }
                if (ind > row_data.length) {
                    break;
                }

                row_data[ind - 1] = objs[analyse_index * y_len + row_len + 1];//数据错位修改
            }
        } else {
            row_data = show_data.get(row_index);
        }
        if (row_data == null) {
            return;
        }
        if (chart_flag.equals("Pie")) {
            if (row_index == -1) {
                return;
            }
            DefaultPieDataset dataset = new DefaultPieDataset();
            if (x_change_flag) {
                for (int i = 0; i < x_len; i++) {
                    if ((i) >= row_data.length) {
                        continue;
                    }
                    dataset.setValue(x_compare_fields.get(i) //row);
                            , //row);
                            SysUtil.objToFloat(row_data[i]));
                }
            } else {
                for (int i = 0; i < x_len; i++) {
                    if ((analyse_index * x_len + i + 1) >= row_data.length) {
                        continue;
                    }
                    dataset.setValue(x_compare_fields.get(i) //row);
                            , //row);
                            Float.valueOf(row_data[analyse_index * x_len + i + 1].toString()));
                }
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
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({2})", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
            plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            plot.setNoDataMessage("没有显示数据");
            plot.setCircular(true);
            plot.setLabelGap(0.02);
            LegendTitle lt = chart.getLegend();
            lt.setPosition(RectangleEdge.RIGHT);
            lt.setHorizontalAlignment(HorizontalAlignment.LEFT);
            chart.setBackgroundPaint(new Color(199, 237, 204));
            ChartUtil.configFont(chart);
            pnlChart.add(new ChartPanel(chart), BorderLayout.CENTER);
        } else if (chart_flag.equals("Line") || chart_flag.equals("Bar") || chart_flag.equals("Area")) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            if (x_change_flag) {
                if (!is_row) {
                    x_len = x_items.size();
                }
            }
            if (chart_flag.equals("Line")) {
                if (is_row) {
                    if (x_change_flag) {
                        for (int col = 0; col < x_len; col++) {
                            if (col >= row_data.length) {
                                continue;
                            }

                            BigDecimal d = SysUtil.objToBigDecimal(row_data[col]);
                            avg_data = avg_data.add(d);
                            dataset.setValue(d, (Comparable) x_items.get(row_index), (Comparable) x_compare_fields.get(col));
                        }
                    } else {
                        for (int col = 0; col < x_len; col++) {
                            if ((analyse_index * x_len + col + 1) >= row_data.length) {
                                continue;
                            }

                            BigDecimal d = SysUtil.objToBigDecimal(row_data[analyse_index * x_len + col + 1]);
                            avg_data = avg_data.add(d);
                            dataset.setValue(d, (Comparable) x_items.get(row_index), (Comparable) x_compare_fields.get(col));
                        }
                    }

                } else {
                    if (x_change_flag) {
                        int tmp_len = x_len;
                        if (no_x_fields_flag) {
                            x_len = y_items.size();
                            tmp_len = 0;
                        }
                        for (int row = 0; row < data_size; row++) {
                            for (int col = 0; col < x_len; col++) {
                                if ((analyse_index * tmp_len + col + 1) >= show_data.get(row).length) {
                                    continue;
                                }

                                dataset.setValue(Float.valueOf(show_data.get(row)[analyse_index * tmp_len + col + 1].toString()), (Comparable) y_compare_fields.get(col), (Comparable) x_compare_fields.get(row));
                            }
                        }
                    } else {
                        for (int row = 0; row < data_size; row++) {
                            for (int col = 0; col < x_len; col++) {
                                if ((analyse_index * x_len + col + 1) >= show_data.get(row).length) {
                                    continue;
                                }

                                dataset.setValue(Float.valueOf(show_data.get(row)[analyse_index * x_len + col + 1].toString()), (Comparable) y_compare_fields.get(row), (Comparable) x_compare_fields.get(col));
                            }
                        }
                    }
                }
            } else {
                if (is_row) {
                    if (x_change_flag) {
                        for (int col = 0; col < x_len; col++) {
                            if (col >= row_data.length) {
                                continue;
                            }

                            BigDecimal d = SysUtil.objToBigDecimal(row_data[col]);
                            avg_data = avg_data.add(d);
                            dataset.setValue(d, (Comparable) x_fields.get(col), (Comparable) x_items.get(row_index));
                        }
                    } else {
                        for (int col = 0; col < x_len; col++) {
                            if ((analyse_index * x_len + col + 1) >= row_data.length) {
                                continue;
                            }

                            BigDecimal d = SysUtil.objToBigDecimal(row_data[analyse_index * x_len + col + 1]);
                            avg_data = avg_data.add(d);
                            dataset.setValue(d, (Comparable) x_fields.get(col), (Comparable) x_items.get(row_index));
                        }
                    }

                } else {
                    if (x_change_flag) {
                        int tmp_len = x_len;
                        if (no_x_fields_flag) {
                            x_len = y_items.size();
                            tmp_len = 0;
                        }
                        for (int row = 0; row < data_size; row++) {
                            for (int col = 0; col < x_len; col++) {
                                if ((analyse_index * tmp_len + col + 1) >= show_data.get(row).length) {
                                    continue;
                                }

                                dataset.setValue(Float.valueOf(show_data.get(row)[analyse_index * tmp_len + col + 1].toString()), (Comparable) x_compare_fields.get(row), (Comparable) y_compare_fields.get(col));
                            }
                        }
                    } else {
                        for (int row = 0; row < data_size; row++) {
                            for (int col = 0; col < x_len; col++) {
                                if ((analyse_index * x_len + col + 1) >= show_data.get(row).length) {
                                    continue;
                                }

                                dataset.setValue(Float.valueOf(show_data.get(row)[analyse_index * x_len + col + 1].toString()), (Comparable) x_compare_fields.get(col), (Comparable) y_compare_fields.get(row));
                            }
                        }
                    }

                }
            }
            if (chart_flag.equals("Line")) {
                if (d3_flag) {
                    chart = ChartFactory.createLineChart3D(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                } else {
                    chart = ChartFactory.createLineChart(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                }
            } else if (chart_flag.equals("Bar")) {
                if (d3_flag) {
                    chart = ChartFactory.createBarChart3D(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                } else {
                    chart = ChartFactory.createBarChart(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
                }
            } else if (chart_flag.equals("Area")) {
                chart = ChartFactory.createAreaChart(title, x_text, y_text, dataset, PlotOrientation.VERTICAL, true, true, true);
            }
            CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
            CategoryAxis categoryAxis = categoryPlot.getDomainAxis();
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            categoryPlot.setNoDataMessage("没有显示数据");
            if (chart_flag.equals("Line")) {
                LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer) categoryPlot.getRenderer();
                localLineAndShapeRenderer.setBaseShapesVisible(true);
                localLineAndShapeRenderer.setDrawOutlines(true);
                localLineAndShapeRenderer.setUseFillPaint(true);
                localLineAndShapeRenderer.setBaseFillPaint(Color.green);
                localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
                localLineAndShapeRenderer.setBaseItemLabelsVisible(true);
                localLineAndShapeRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
            } else if (chart_flag.equals("Bar")) {
                BarRenderer barRenderer = (BarRenderer) categoryPlot.getRenderer();
                barRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
                barRenderer.setBaseItemLabelsVisible(true);
                barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
            }
            addAvgLine(is_row, avg_data.doubleValue() / x_len, categoryPlot);
            ChartUtil.configFont(chart);
            pnlChart.add(new ChartPanel(chart), BorderLayout.CENTER);
        }
        pnlChart.updateUI();
        System.out.println("create chart<<<<<<<<");
    }

    /**
     * 为图形增加平均线
     * @param is_row：是否当前行，仅在当前行是显示平均线
     * @param avg_num：平均值
     * @param categoryPlot：当前图片对象
     */
    private void addAvgLine(boolean is_row, double avg_num, CategoryPlot categoryPlot) {
        if (is_row) {
            AnalyseField af = (AnalyseField) jComboBox2.getSelectedItem();
            DecimalFormat df;
            if (af.getStat_operator().equals("count")) {
                df = df_int;
            } else {
                df = df_num;
            }
            //把ValueMarker的第一个参数该成你要的均值
            ValueMarker valueMarker = new ValueMarker(avg_num, new Color(100,
                    200, 255), new BasicStroke(1.0F), new Color(200, 200, 255),
                    new BasicStroke(1.0F), 1.0F);
            categoryPlot.addRangeMarker(valueMarker, Layer.BACKGROUND);
            CategoryTextAnnotation categoryTextAnnotation = new CategoryTextAnnotation(
                    "平均(" + df.format(avg_num) + ")", x_fields.get(0), avg_num);
            categoryTextAnnotation.setCategoryAnchor(CategoryAnchor.END);
            categoryTextAnnotation.setFont(new Font("SansSerif", 0, 10));
            categoryTextAnnotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
            categoryPlot.addAnnotation(categoryTextAnnotation);
        }
    }

    /** Creates new form QueryShapePanel */
    public QueryShapePanel(List<AnalyseField> analyse_fields, List<AnalyseField> x_fields, List<Object[]> show_data, boolean x_change_flag, boolean no_x_fields_flag) {
        initComponents();
        x_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, x_items, jComboBox1);
        y_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, y_items, jComboBox2);
        x_binding.bind();
        y_binding.bind();
        showShape(analyse_fields, x_fields, show_data, x_change_flag, no_x_fields_flag);
        jcbRow.setEnabled(false);
        setupEvents();
    }

    public void showShape(List<AnalyseField> analyse_fields, List<AnalyseField> x_fields, List<Object[]> show_data, boolean x_change_flag, boolean no_x_fields_flag) {
        System.out.println("show shape>>>>>>>>>>>");
        this.x_change_flag = x_change_flag;
        curIndex = -1;
        x_items.clear();
        y_items.clear();
        this.x_fields.clear();
        this.show_data = show_data;
        this.no_x_fields_flag = no_x_fields_flag;
        int x_len = x_fields.size();
        int data_len = this.show_data.size();
        int y_len = data_len;
        y_items.addAll(analyse_fields);
        if (show_data.size() == 0) {
            return;
        }
        if (this.x_change_flag) {
            if (no_x_fields_flag) {
                x_len = data_len;
                for (int i = 0; i < x_len; i++) {
                    x_items.add((AnalyseField) show_data.get(i)[0]);
                }
            } else {
                for (int i = 1; i < x_len; i++) {
                    x_items.add(x_fields.get(i));
                }
            }
            x_field = (AnalyseField) show_data.get(0)[0];
            for (int i = 0; i < y_len; i++) {
                this.x_fields.add((AnalyseField) show_data.get(i)[0]);
            }
        } else {
            for (int i = 0; i < y_len; i++) {
                x_items.add((AnalyseField) show_data.get(i)[0]);
            }
            x_field = x_fields.get(1);
            for (int i = 1; i < x_len; i++) {
                this.x_fields.add(x_fields.get(i));
            }
        }
        jComboBox1.removeActionListener(al_x);
        jComboBox2.removeActionListener(al_y);
        x_binding.unbind();
        x_binding.bind();
        y_binding.unbind();
        y_binding.bind();
        createChart("");
        jComboBox1.addActionListener(al_x);
        jComboBox2.addActionListener(al_y);
        System.out.println("show shape<<<<<<<<<<<<<");
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
        jcbRow = new javax.swing.JCheckBox();

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
        jrbArea.setContentAreaFilled(false);

        jcb3d.setText("3D");

        jcbRow.setText("当前记录");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbRow)
                .addGap(133, 133, 133))
            .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
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
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbRow))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JCheckBox jcb3d;
    private javax.swing.JCheckBox jcbRow;
    private javax.swing.JRadioButton jrbArea;
    private javax.swing.JRadioButton jrbBar;
    private javax.swing.JRadioButton jrbLine;
    private javax.swing.JRadioButton jrbPie;
    private javax.swing.JPanel pnlChart;
    // End of variables declaration//GEN-END:variables

    private void setupEvents() {
        jrbLine.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jcbRow.setEnabled(true);
                chart_flag = "Line";
                createChart("");
            }
        });
        jrbBar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chart_flag = "Bar";
                jcbRow.setEnabled(true);
                createChart("");
            }
        });
        jcbRow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
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
                jcbRow.setEnabled(false);
                chart_flag = "Pie";
                createChart("");
            }
        });
        jrbArea.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chart_flag = "Area";
                jcbRow.setEnabled(true);
                createChart("");
            }
        });
        al_y = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = jComboBox2.getSelectedItem();
                if (obj == null) {
                    return;
                }
                createChart("");
            }
        };
        al_x = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = jComboBox1.getSelectedItem();
                if (obj == null) {
                    return;
                }
                createChart("");
            }
        };
        jComboBox1.addActionListener(al_x);
        jComboBox2.addActionListener(al_y);
    }

    public void refreshPic(int row_index, int col_index) {
        int changeIndex = row_index;
        if (x_change_flag) {
            changeIndex = col_index;
        }
        if (curIndex == changeIndex) {
            return;
        }
        this.curIndex = changeIndex;
        if (x_items.size() > changeIndex && changeIndex >= 0) {
            jComboBox1.setSelectedIndex(changeIndex);
        } else if (x_items.size() > 0) {
            jComboBox1.setSelectedIndex(0);
        }
    }
}
