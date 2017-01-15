/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.jhrcore.client.CommUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.TransferAccessory;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.A01;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.util.FileChooserUtil;
import org.jhrcore.util.FileUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class PersonPhotoDialog implements IModuleCode {

    private static PersonPhotoDialog personPhotoDlg = null;
    private A01 cur_person;
    private File photo_url = null;
    private JButton btnIn = new JButton("调入");
    private JButton btnDown = new JButton("调出");
    private JButton btnOut = new JButton("清除");
    private JButton btnShow = new JButton("隐藏");
    private JPanel pnlPhoto = new JPanel(new BorderLayout());
    private JToolBar toolbar = new JToolBar();
    private boolean show_falg = true;//true 显示 false 隐藏
    private JLabel lbl_person = null;
    private String module_code = "EmpMng.beanTablePanel2";

    public boolean isShow_falg() {
        return show_falg;
    }

    public void setShow_falg(boolean show_falg) {
        this.show_falg = show_falg;
    }

    public static PersonPhotoDialog getPersonPhotoDlg(A01 person) {
        if (personPhotoDlg == null) {
            personPhotoDlg = new PersonPhotoDialog();
        }
        personPhotoDlg.setPerson(person);
        return personPhotoDlg;
    }

    public static void setNull() {
        personPhotoDlg = null;
    }

    public JPanel getPhotoPanel() {
        return pnlPhoto;
    }

    public JPanel getPhotoNoToolBar() {
        Icon tmpIcon = null;
        if (isShow_falg() && cur_person != null && cur_person.getPic_path() != null) {
            BufferedImage tmpImage = TransferAccessory.downloadPicture(cur_person.getPic_path());
            if (tmpImage != null) {
                tmpIcon = new ImageIcon(tmpImage.getScaledInstance(120, 150, Image.SCALE_DEFAULT));
            }
        }
        JLabel label = new JLabel(tmpIcon);
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setMaximumSize(new Dimension(150, 175));
        pnl.setPreferredSize(new Dimension(150, 175));
        pnl.add(label, BorderLayout.NORTH);
        return pnl;
    }

    /**
     * 设置当前界面显示的照片为目标人员的照片
     * @param person：目标人员
     */
    public void setPerson(A01 person) {
        Icon tmpIcon = null;
        cur_person = person;
        if (isShow_falg() && person != null && person.getPic_path() != null) {
            BufferedImage tmpImage = TransferAccessory.downloadPicture(person.getPic_path());
            if (tmpImage != null) {
                tmpIcon = new ImageIcon(tmpImage.getScaledInstance(120, 150, Image.SCALE_DEFAULT));
            }
        }
        updatePhotoUI(new JLabel(tmpIcon));
    }

    private void updatePhotoUI(JLabel label) {
        lbl_person = label;
        if (label != null) {
            JPanel pnl = new JPanel(new BorderLayout());
            pnl.add(toolbar, BorderLayout.NORTH);
            pnl.add(label, BorderLayout.CENTER);
            pnl.setMaximumSize(new Dimension(150, 175));
            pnlPhoto.removeAll();
            pnlPhoto.add(pnl, BorderLayout.NORTH);
            pnlPhoto.add(new JPanel(), BorderLayout.CENTER);
            pnlPhoto.setPreferredSize(new Dimension(150, 175));
            pnl.updateUI();
            pnlPhoto.updateUI();
        }
    }

    public PersonPhotoDialog() {
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        btnIn.setEnabled(UserContext.hasFunctionRight("EmpMng.btnIn_photo"));
        btnOut.setEnabled(UserContext.hasFunctionRight("EmpMng.btnOut_photo"));
        btnDown.setEnabled(UserContext.hasFunctionRight("EmpMng.btnDown_photo"));
        btnIn.setToolTipText("可以调入员工的照片");
        btnOut.setToolTipText("可以删除员工的照片");
        btnDown.setToolTipText("可以保存员工的照片");
        btnShow.setToolTipText("设置是否显示员工的照片，隐藏可以加快查看速度！");
        toolbar.add(btnIn);
        toolbar.add(btnDown);
        toolbar.add(btnOut);
        toolbar.add(btnShow);
        toolbar.setFloatable(false);
    }

    private void setupEvents() {
        btnIn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addPhoto();
            }
        });
        btnDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                down_pic();
            }
        });
        btnOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delPhoto();
            }
        });
        btnShow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShow_falg()) {
                    setShow_falg(false);
                    btnShow.setText(EmpMngMsg.msg117.toString());
                } else {
                    setShow_falg(true);
                    btnShow.setText(EmpMngMsg.msg118.toString());
                }
                btnOut.setEnabled(show_falg && UserContext.hasFunctionRight("EmpMng.btnOut_photo"));
                btnIn.setEnabled(show_falg && UserContext.hasFunctionRight("EmpMng.btnIn_photo"));
                btnShow.updateUI();
                setPerson(cur_person);
            }
        });
    }

    private void delPhoto() {
        if (MsgUtil.showNotConfirmDialog(EmpMngMsg.msg116)) {
            return;
        }
        updatePhotoUI(new JLabel());
        TransferAccessory.deletePicture(cur_person.getPic_path());
        cur_person.setPic_path(null);
        CommUtil.excuteSQL("update A01 set pic_path=null where a01_key='" + cur_person.getA01_key() + "'");
    }

    private void down_pic() {
        if (cur_person.getPic_path() == null || cur_person.getPic_path().isEmpty()) {
            MsgUtil.showErrorMsg(EmpMngMsg.msg115);
            return;
        }
        File p_file = FileChooserUtil.getDirectoryForExport(EmpMngMsg.ttl027);
        if (p_file == null) {
            return;
        }
        String fileName = "";
        String path = p_file.getPath();
        String fileType = cur_person.getPic_path().substring(cur_person.getPic_path().lastIndexOf("."));
        fileName = cur_person.getA0101().concat("(").concat(cur_person.getA0190()).concat(")").concat(fileType);
        fileName = path + "/" + fileName;
        BufferedImage tmpImage = TransferAccessory.downloadPicture(cur_person.getPic_path());
        if (tmpImage != null) {
            File new_file = new File(fileName);
            try {
                ImageIO.write(tmpImage, fileType.substring(1), new_file);
            } catch (IOException ex) {
                HrLog.error(this.getClass(), ex);
            }
        }
        MsgUtil.showInfoMsg(EmpMngMsg.msg114);
    }

    private void addPhoto() {
        File file = FileChooserUtil.getPICFile(CommMsg.SELECTPIC_MESSAGE);
        if (file == null) {
            return;
        }
        int flag = TransferAccessory.checkPic(file);
        if (flag == -1) {
            BufferedImage origImage = null;
            try {
                origImage = ImageIO.read(file);
            } catch (IOException ex) {
                MsgUtil.showInfoMsg(EmpMngMsg.msg113);
                return;
            }
            photo_url = file;
            Icon tmpIcon = null;
            if (origImage != null) {
                tmpIcon = new ImageIcon(origImage.getScaledInstance(120, 150, Image.SCALE_DEFAULT));
            }
            JLabel label = new JLabel(tmpIcon);
            if (photo_url != null) {
                cur_person.setPic_path(cur_person.getDeptCode().getDept_code() + "/" + cur_person.getA0190().trim() + FileUtil.getFileType(file));
                CommUtil.excuteSQL("update A01 set pic_path='" + cur_person.getPic_path() + "' where a01_key='" + cur_person.getA01_key() + "'");
                TransferAccessory.uploadPicture(photo_url, cur_person.getPic_path());
            }
            updatePhotoUI(label);
        } else if (flag == 2) {
            MsgUtil.showErrorMsg(EmpMngMsg.msg050);
        } else {
            MsgUtil.showErrorMsg(EmpMngMsg.msg051);
        }

    }

    public JLabel getLbl_person() {
        return lbl_person;
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
