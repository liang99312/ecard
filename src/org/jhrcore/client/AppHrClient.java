/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client;

import org.jhrcore.comm.LogRecorder;
import org.jhrcore.util.SysUtil;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.jhrcore.client.index.IndexPanelPlugIn;
import org.jhrcore.serviceproxy.LocatorManager;
import org.jhrcore.serviceproxy.RmiLocator;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.comm.CommThreadPool;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.salary.*;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.rebuild.*;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Administrator
 */
public class AppHrClient {

    private static Logger log = Logger.getLogger(AppHrClient.class.getSimpleName());
    private static String userid = null;
    private static String pw = null;
    private static Timer clentTimer;
    //1000（毫秒）*60（秒）* 10（分）
    //serverCheckTime:服务器端对客户端连接定时清理的时间间隔
    private static long serverCheckTime = 1000 * 60;
    public static String loginCode = new UID().toString();
    
    public AppHrClient(){
        start();
    }

    public void start() {
        System.setProperty("java.awt.im.style", "on-the-spot");
        String path = System.getProperty("user.dir");
        File log_file;
        boolean bFile = false;
        log_file = new File(path + "/log/");
        bFile = log_file.exists();
        if (!bFile) {
            bFile = log_file.mkdir();
        }
        RollingFileAppender tmp = new RollingFileAppender();
        try {
            tmp.setFile(path + "/log/" + this.getClass().getSimpleName() + ".log", true, true,
                    1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tmp.setName("longshine");
        tmp.setMaxFileSize("1000KB");
        tmp.setMaxBackupIndex(20);
        PatternLayout lo = new org.apache.log4j.PatternLayout();
        lo.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n");
        tmp.setLayout(lo);
        Logger.getRootLogger().addAppender(tmp);
        Logger.getRootLogger().addAppender(new LogRecorder());
        login();//登录

        ////初始化程序基础类
        long time1 = System.currentTimeMillis();
        List<ModuleInfo> list = CommImpl.getSysModule(true, true, true);
        long time2 = System.currentTimeMillis();
        System.out.println(UserContext.person_code + ":login:" + (time2 - time1));
        //CommUtil.fetchEntities("from ModuleInfo a left join fetch a.entityClasss b left join fetch b.entityDefs c left join fetch c.fieldDefs where a.used=1");
    //    EntityBuilder.buildEntities(list);
        long time3 = System.currentTimeMillis();
        log.error(UserContext.person_code + ":buildentity:" + (time3 - time2));
//        PayA01Builder.buildPayA01("org.jhrcore.entity.salary.PayA01", list_A01toPay);
        CommThreadPool.getClientThreadPool().handleEvent(new Runnable() {

            @Override
            public void run() {
                EntityBuilder.putInitClass();
            }
        });
        long time4 = System.currentTimeMillis();
        log.error(UserContext.person_code + ":buildpay:" + (time4 - time3));
        TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
        TimeZone.setDefault(tz);
        BaseMainFrame fm = createUI();//创建主界面
        CodeManager.getCodeManager().fillCodes(CommImpl.getSysCodes());//CommUtil.fetchEntities("from Code where used=1 order by code_tag");
        long time5 = System.currentTimeMillis();
        log.error(UserContext.person_code + ":buildcode:" + (time5 - time4));
        preStart();//初始化报表
        fm.refreshSession();//初始化主界面及程序缓存
        fm.setEnabled(true);
        long time6 = System.currentTimeMillis();
        log.error(UserContext.person_code + ":buildui:" + (time6 - time5));
        ContextManager.getStatusBar().updateUI();
        testServer("login");
        clentTimer = new Timer();
        clentTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                testServer("online");
            }
        }, new Date(), serverCheckTime);
        Hashtable<String, String> securyInfos = CommUtil.getSecuryInfo(false);
        String verType = securyInfos.get("verType");
        int remainDay = SysUtil.objToInt(securyInfos.get("remainDay"));
        if ("Beta".equals(verType) && remainDay < 7) {
            JOptionPane.showMessageDialog(null, "您当前使用的是试用版软件，目前使用期限还剩 " + remainDay + " 天");
        }
        BaseMainFrame.getBaseMainFrame().modulePlugInAction(new IndexPanelPlugIn());//首页
        BaseMainFrame.refreshUI();
    }

    private void testServer(String type) {
//        long now = System.currentTimeMillis();
//        long last_oper_time = UserContext.last_oper_time;
//        if (!UserContext.isSA && (now - UserContext.last_oper_time) > sessionTime) {
//            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "连接超时");
//            System.exit(0);
//            return;
//        }
        String user_code = UserContext.person_key + "|" + UserContext.getPerson_ip() + "|" + UserContext.getPerson_mac() + "|" + UserContext.person_code + "|" + loginCode;
        ValidateSQLResult result = CommUtil.connectServer(type, user_code);
//        UserContext.last_oper_time = last_oper_time;
        if (result == null) {
            log.error(UserContext.person_name + ";" + RmiLocator.rmiPort + ";服务调用失败");
            return;
        }
//        if (ContextManager.getMainFrame() != null) {
//            ((MainFrame) ContextManager.getMainFrame()).refershMsgUI(result.getInsert_result());
//        }
        if (result.getResult() > 0) {
            clentTimer = null;//可能存在异常
//            JOptionPane.showMessageDialog(MainFrame.getMainFrame(), result.getMsg());
//            System.exit(0);
        } else if (result.getMsg() != null && !result.getMsg().trim().equals("") && result.getMsg().startsWith("@MSG")) {
            MsgUtil.showHRMsg(result.getMsg().substring(4), "您收到一条新消息：");
        }
    }

//    public static void main(String[] args) {
//        File file = new File("c:\\webhr/client/webhr.bat");
//        if (file != null && file.exists()) {
//            file.delete();
//        }
//        String serverName = "127.0.0.1";
//        if (args.length >= 1) {
//            serverName = args[0];
//        }
////        RmiLocator.setServerName(serverName);
////        if (args.length < 3) {
////            System.exit(0);
////        }
//        if (args.length >= 3) {
//            ConfigManager.getConfigManager().setProperty("base.ServiceType", "HTTP");
//            ConfigManager.getConfigManager().save2();
//            userid = args[1];
//            pw = args[2];
////            if (args.length >= 4) {
////                RmiLocator.rmiPort = Integer.valueOf(args[3]);
////            }
//        }
//        LocatorManager.getLocatorManager().setServicePara(serverName, args.length < 4 ? null : args[3]);
//        BaseMainFrame.refreshUI();
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                (new AppHrClient()).start();
//            }
//        });
//    }
    
    public static void startClient(){
        File file = new File("c:\\webhr/client/webhr.bat");
        if (file != null && file.exists()) {
            file.delete();
        }
        String serverName = "127.0.0.1";
        
        LocatorManager.getLocatorManager().setServicePara(serverName, "1299");
        BaseMainFrame.refreshUI();
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                (new AppHrClient()).start();
            }
        });
    }

    protected void preStart() {
        FR_Rebuilder.build_fr();
    }

    protected BaseMainFrame createUI() {
        BaseMainFrame fm = new BaseMainFrame(ContextManager.getHrTitle());
        ContextManager.setMainFrame(fm);
        fm.setSize(new Dimension(fm.getToolkit().getScreenSize().width - 50, fm.getToolkit().getScreenSize().height - 50));
        fm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fm.setIconImage(ImageUtil.getIconImage());
        fm.setVisible(true);
        return fm;
    }

    protected void login() {
        if (userid == null) {
            log.info("cs login");
            LoginDialog loginDialog = new LoginDialog(null, true);
            ContextManager.locateOnMainScreenCenter(loginDialog);
            loginDialog.pack();
            loginDialog.setVisible(true);
            ////初始化程序基础类
//            List<ModuleInfo> list = (List<ModuleInfo>) CommUtil.fetchEntities("from ModuleInfo a left join fetch a.entityClasss b left join fetch b.entityDefs c left join fetch c.fieldDefs ");
//            EntityBuilder.buildEntities(list);
//            ////初始化工资变动表
//            List<A01toPay> list_A01toPay = (List<A01toPay>) CommUtil.fetchEntities("from A01toPay a");
//            PayA01Builder.buildPayA01("org.jhrcore.entity.salary.PayA01", list_A01toPay);
        } else {
            if (pw.equals("-1")) {
                pw = "";
            }
            UserContext.setPerson_code(userid);
            UserContext.setPassword(pw);
            UserContext.verifyUser();
            // EntityBuilder.buildClientEntity();
//            List list = CommUtil.selectSQL("select t.entitycaption,t.entityname,s.field_name from system s,tabname t where s.entity_key=t.entity_key order by t.entityname,s.order_no");
//            for (Object obj : list) {
//                Object[] objs = (Object[]) obj;
//                EntityBuilder.getHt_entity_names().put(objs[0].toString(), objs[1].toString());
//                EntityBuilder.ht_fields.add(objs[1].toString().toUpperCase() + "." + objs[2].toString().toUpperCase());
//            }
        }
    }
}
