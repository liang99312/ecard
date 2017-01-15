/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChangeLogoDialog.java
 *
 * Created on 2010-8-28, 11:28:24
 */
package org.jhrcore.client;

import org.jhrcore.util.TransferAccessory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.util.FileChooserUtil;
import org.jhrcore.util.ImageUtil;

/**
 *
 * @author hflj
 */
public class ChangeLogoDialog extends javax.swing.JDialog {

    private File fileLogo = null;
    private File fileIcon = null;
    private String title = "设置标题与LOGO：";
    private Logger log = Logger.getLogger(title);

    /** Creates new form ChangeLogoDialog */
    public ChangeLogoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle(title);
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        jtfHrTitle.setText(ContextManager.getHrTitle());
        byte[] imgbyte = RSImpl.downloadPicture("@Logo");
        if (imgbyte != null) {
            try {
                fileLogo = new File(System.getProperty("user.dir") + "/hr_logo.jpg");
                if (!fileLogo.getParentFile().exists()) {
                    fileLogo.getParentFile().mkdirs();
                }
                if (!fileLogo.exists()) {
                    fileLogo.createNewFile();
                }
                BufferedOutputStream output = new BufferedOutputStream(
                        new FileOutputStream(fileLogo));
                output.write(imgbyte);
                output.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
        byte[] iconbyte = RSImpl.downloadPicture("@Icon");
        if (iconbyte != null) {
            try {
                fileIcon = new File(System.getProperty("user.dir") + "/frame_icon.png");
                if (!fileIcon.getParentFile().exists()) {
                    fileIcon.getParentFile().mkdirs();
                }
                if (!fileIcon.exists()) {
                    fileIcon.createNewFile();
                }
                BufferedOutputStream output = new BufferedOutputStream(
                        new FileOutputStream(fileIcon));
                output.write(iconbyte);
                output.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /** Closes the dialog */
    private void setupEvents() {
        btnFileChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooserUtil.getPICFile(CommMsg.SELECTFILE_MESSAGE);
                if (file == null) {
                    return;
                }
                fileLogo = file;
                String file_type = (file.getName().substring(file.getName().lastIndexOf(".") + 1) + "").toLowerCase();
                if (!(file_type.equals("jpg") || file_type.equals("png"))) {
                    JOptionPane.showMessageDialog(null, "LOGO图片格式仅允许为JPG,PNG！", "LOGO图片选择错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                jtfLogoPath.setText(file.getPath());
                previewLogoPic(file);
            }
        });
        btnFileChooser1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooserUtil.getPICFile(CommMsg.SELECTFILE_MESSAGE);
                if (file == null) {
                    return;
                }
                fileIcon = file;
                String file_type = (file.getName().substring(file.getName().lastIndexOf(".") + 1) + "").toLowerCase();
                if (!(file_type.equals("jpg") || file_type.equals("png"))) {
                    JOptionPane.showMessageDialog(null, "ICON图片格式仅允许为JPG,PNG！", "ICON图片选择错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                jtfLogoPath1.setText(file.getPath());
                previewIconPic(file);
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {//保存前上传服务器
                if (jtfHrTitle.getText() == null || jtfHrTitle.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "标题不可为空！");
                    return;
                }
                ContextManager.setHrTitle(jtfHrTitle.getText());
                TransferAccessory.uploadPicture(fileLogo, "@Logo" + ((fileLogo == null) ? "" : ("hr_logo" + fileLogo.getName().substring(fileLogo.getName().lastIndexOf(".")).toLowerCase())));              
                TransferAccessory.uploadPicture(fileIcon, "@Icon" + ((fileIcon == null) ? "" : ("frame_icon" + fileIcon.getName().substring(fileIcon.getName().lastIndexOf(".")).toLowerCase())));
                BaseMainFrame.getBaseMainFrame().initLogo();
                JOptionPane.showMessageDialog(null, "设置成功！");
                log.info("$" + btnSave.getText());
            }
        });
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("$" + btnClose.getText());
                dispose();
            }
        });

        btnDefault.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("$" + btnDefault.getText());
                fileLogo = null;
                previewLogoPic(null);
            }
        });
        btnDefault1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("$" + btnDefault.getText());
                fileIcon = null;
                previewIconPic(null);
            }
        });
        previewLogoPic(fileLogo);
        previewIconPic(fileIcon);
    }

    private void previewLogoPic(File fm) {
        if (fm == null) {
            lblLogo.setIcon(ImageUtil.getIcon("hr_logo_3.png"));
            lblLogo.updateUI();
            return;
        }
        Icon icon = TransferAccessory.getIconFromFile(fm, 150, 30);
        if (icon != null) {
            lblLogo.setIcon(icon);
        }
        lblLogo.updateUI();
    }

    private void previewIconPic(File fm) {
        if (fm == null) {
            lblLogo1.setIcon(ImageUtil.getIcon("frame_icon.png"));
            lblLogo1.updateUI();
            return;
        }
        Icon icon = TransferAccessory.getIconFromFile(fm, 40, 40);
        if (icon != null) {
            lblLogo1.setIcon(icon);
        }
        lblLogo1.updateUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btnFileChooser = new javax.swing.JButton();
        jtfLogoPath = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtfHrTitle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnDefault = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jtfLogoPath1 = new javax.swing.JTextField();
        btnFileChooser1 = new javax.swing.JButton();
        btnDefault1 = new javax.swing.JButton();
        lblLogo = new javax.swing.JLabel();
        lblLogo1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        btnFileChooser.setText("选择图片");

        jtfLogoPath.setEnabled(false);
        jtfLogoPath.setMaximumSize(new java.awt.Dimension(6, 21));

        jLabel1.setText(" Logo：");

        jLabel6.setText(" 标题：");

        jLabel2.setText(" 预览：");

        btnDefault.setText("默认LOGO");

        jLabel7.setText("小图标：");

        jtfLogoPath1.setEnabled(false);
        jtfLogoPath1.setMaximumSize(new java.awt.Dimension(6, 21));

        btnFileChooser1.setText("选择图片");

        btnDefault1.setText("默认图标");

        lblLogo1.setMaximumSize(new java.awt.Dimension(58, 37));
        lblLogo1.setMinimumSize(new java.awt.Dimension(58, 37));
        lblLogo1.setPreferredSize(new java.awt.Dimension(58, 37));

        jLabel3.setText(" 预览：");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblLogo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jtfHrTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                    .addComponent(jtfLogoPath1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                    .addComponent(jtfLogoPath, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnFileChooser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDefault))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnFileChooser1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDefault1)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jtfHrTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtfLogoPath, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFileChooser)
                    .addComponent(btnDefault))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jtfLogoPath1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFileChooser1)
                    .addComponent(btnDefault1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(lblLogo1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        btnClose.setText("关闭");

        btnSave.setText("保存");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(425, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose)
                .addGap(21, 21, 21))
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnSave))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel4.setText("说明：1.LOGO图按150*30的比例，能取得最佳显示效果");

        jLabel5.setText("2.小图标按30*30的比例，能取得最佳显示效果");

        jLabel8.setText("3.图标允许为JPG/PNG格式");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                            .addComponent(jLabel5))))
                .addContainerGap(214, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnDefault1;
    private javax.swing.JButton btnFileChooser;
    private javax.swing.JButton btnFileChooser1;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jtfHrTitle;
    private javax.swing.JTextField jtfLogoPath;
    private javax.swing.JTextField jtfLogoPath1;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblLogo1;
    // End of variables declaration//GEN-END:variables
}
