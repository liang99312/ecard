/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.util;

import ikeycom.CoiKey;
import ikeycom.ComFailException;
import ikeycom.IiKey;
import ikeycom.SFileInfo;
import ikeycom.iKeyFlag;

/**
 *
 * @author zhongzhao
 */
public class UKeyUtil {

    //验证UKey是否安装正确的驱动程序
    public static boolean verifyUKeyDriver() {
        boolean isSuccessed = false;

        IiKey iKey = null;


        try {
            // new CoiKey instance can be created by new.
            // For every CoiKey instance created, a corresponding iKey COM object is created.
            // The ikey COM object will be removed when the CoiKey instance is recycled.
            iKey = (IiKey) new CoiKey();

            int libver = 0, drvver = 0;
            String data = "";

            libver = iKey.GetLibVersion();
            System.out.print("GetLibVersion succeeded\n");
            drvver = iKey.GetDriverVersion();
            System.out.print("GetDriverVersion succeeded\n");
            data = "    Library Version : " + Integer.toHexString(libver) + " Driver Version : " + Integer.toHexString(drvver);
            System.out.print(data + "\n");

            isSuccessed = true;
        } catch (Throwable e) {
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            e.printStackTrace(System.err);
            //return;
        }
        
        return isSuccessed;
    }

    //验证UKey是否插入
    public static boolean verifyUKeyInsert() {
        boolean isSuccessed = false;

        IiKey iKey = null;


        try {
            // new CoiKey instance can be created by new.
            // For every CoiKey instance created, a corresponding iKey COM object is created.
            // The ikey COM object will be removed when the CoiKey instance is recycled.
            iKey = (IiKey) new CoiKey();
        } catch (Throwable e) {
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            e.printStackTrace(System.err);
            //return;
        }

        int libver = 0, drvver = 0;
        String data = "";

        libver = iKey.GetLibVersion();
        System.out.print("GetLibVersion succeeded\n");
        drvver = iKey.GetDriverVersion();
        System.out.print("GetDriverVersion succeeded\n");
        data = "    Library Version : " + Integer.toHexString(libver) + " Driver Version : " + Integer.toHexString(drvver);
        System.out.print(data + "\n");

        try {
            // Create an iKey context.
            iKey.CreateContext(0, iKeyFlag.IKEY_API_VERSION);
            System.out.print("CreateContext succeeded\n");

            try {
                // Open an iKey device. See iKey manual and iKeyFlag class for more
                // information about possible parameter values.
                iKey.OpenDevice(iKeyFlag.IKEY_OPEN_FIRST, null);
                System.out.print("OpenDevice succeeded, found first iKey\n");

                isSuccessed = true;
            } catch (ComFailException e) {
                System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
            } finally {

                // DeleteContext delete the current iKey context.
                iKey.DeleteContext();
                System.out.print("DeleteContext succeed!\n");
            }
        } catch (ComFailException e) {
            System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
        }

        return isSuccessed;
    }

    //验证UKey的密码是否正确
    public static boolean verifyUKeyPswd(String pswd) {
        boolean isSuccessed = false;

        IiKey iKey = null;


        try {
            // new CoiKey instance can be created by new.
            // For every CoiKey instance created, a corresponding iKey COM object is created.
            // The ikey COM object will be removed when the CoiKey instance is recycled.
            iKey = (IiKey) new CoiKey();
        } catch (Throwable e) {
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            e.printStackTrace(System.err);
            //return;
        }

        int libver = 0, drvver = 0;
        String data = "";

        libver = iKey.GetLibVersion();
        System.out.print("GetLibVersion succeeded\n");
        drvver = iKey.GetDriverVersion();
        System.out.print("GetDriverVersion succeeded\n");
        data = "    Library Version : " + Integer.toHexString(libver) + " Driver Version : " + Integer.toHexString(drvver);
        System.out.print(data + "\n");

        try {
            // Create an iKey context.
            iKey.CreateContext(0, iKeyFlag.IKEY_API_VERSION);
            System.out.print("CreateContext succeeded\n");

            try {
                // Open an iKey device. See iKey manual and iKeyFlag class for more
                // information about possible parameter values.
                iKey.OpenDevice(iKeyFlag.IKEY_OPEN_FIRST, null);
                System.out.print("OpenDevice succeeded, found first iKey\n");

                try {

                    try {
                        byte[] pin = new byte[8];
                        pin[0] = 0x78;
                        pin[1] = 0x56;
                        pin[2] = 0x34;
                        pin[3] = 0x12;
                        
                        // Verify verifies the user PIN, SO PIN for the iKey
                        iKey.Verify(iKeyFlag.IKEY_VERIFY_USER_PIN, pin, 4);
                        System.out.print("Verify succeed.\n");

                        isSuccessed = true;
                    } catch (ComFailException e) {
                        System.out.print("Verify Failed, " + e.getMessage() + "\n");
                    }

                } catch (ComFailException e) {
                    System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
                } finally {
                    // CloseDevice close the iKey device that is currently open.
                    iKey.CloseDevice();
                    System.out.print("CloseDevice succeed!\n");
                }
            } catch (ComFailException e) {
                System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
            } finally {

                // DeleteContext delete the current iKey context.
                iKey.DeleteContext();
                System.out.print("DeleteContext succeed!\n");
            }
        } catch (ComFailException e) {
            System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
        }

        return isSuccessed;
    }

    public static boolean isInsertUKey() {
        boolean isSuccessed = false;

        IiKey iKey = null;


        try {
            // new CoiKey instance can be created by new.
            // For every CoiKey instance created, a corresponding iKey COM object is created.
            // The ikey COM object will be removed when the CoiKey instance is recycled.
            iKey = (IiKey) new CoiKey();
        } catch (Throwable e) {
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            System.out.print("Create COM object failed : " + e.getMessage() + "\n");
            e.printStackTrace(System.err);
            //return;
        }

        int libver = 0, drvver = 0;
        String data = "";

        libver = iKey.GetLibVersion();
        System.out.print("GetLibVersion succeeded\n");
        drvver = iKey.GetDriverVersion();
        System.out.print("GetDriverVersion succeeded\n");
        data = "    Library Version : " + Integer.toHexString(libver) + " Driver Version : " + Integer.toHexString(drvver);
        System.out.print(data + "\n");

        try {
            // Create an iKey context.
            iKey.CreateContext(0, iKeyFlag.IKEY_API_VERSION);
            System.out.print("CreateContext succeeded\n");

            try {
                // Open an iKey device. See iKey manual and iKeyFlag class for more
                // information about possible parameter values.
                iKey.OpenDevice(iKeyFlag.IKEY_OPEN_FIRST, null);
                System.out.print("OpenDevice succeeded, found first iKey\n");

                try {
                    try {
                        byte[] pin = new byte[8];
                        pin[0] = 0x78;
                        pin[1] = 0x56;
                        pin[2] = 0x34;
                        pin[3] = 0x12;

                        //120 86 52 18

                        
                        // Verify verifies the user PIN, SO PIN for the iKey
                        iKey.Verify(iKeyFlag.IKEY_VERIFY_USER_PIN, pin, 4);
                        System.out.print("Verify succeed.\n");

                        isSuccessed = true;
                    } catch (ComFailException e) {
                        System.out.print("Verify Failed, " + e.getMessage() + "\n");
                    }

                    try {
                        String temp = "";

                        temp = "rainbow";

                        byte[] sopin = new byte[temp.length()];
                        sopin = temp.getBytes();


                        // Verify verifies the user PIN, SO PIN for the iKey
                        iKey.Verify(iKeyFlag.IKEY_VERIFY_SO_PIN, sopin, temp.length());
                        System.out.print("SOPIN Verify succeed.\n");
                    } catch (ComFailException e) {
                        System.out.print("SOPIN Verify Failed, " + e.getMessage() + "\n");
                    }

                    iKey.DeleteFile(0, 1);
                    System.out.print("DeleteFile 1 succeed.\n");

                    String str = "郎霁软件技术有限公司";
                    int length = str.getBytes().length == 0 ? 4 : str.getBytes().length;

                    System.out.println("content:" + str);
                    System.out.println("content length:" + length);

                    SFileInfo fi = new SFileInfo();
                    fi.lId = 1;
                    fi.lFlags = 0;
                    fi.lFileSize = length;
                    fi.chFileType = iKeyFlag.IKEY_FILETYPE_COUNTER;
                    fi.chReadAccess = iKeyFlag.IKEY_ACCESS_ANYONE;
                    fi.chWriteAccess = iKeyFlag.IKEY_ACCESS_ANYONE;
                    fi.chCryptAccess = iKeyFlag.IKEY_ACCESS_NONE;

                    // CreateFile create a file under the current directory according to the
                    // ID and other information supplied.
                    iKey.CreateFile(0, fi);
                    System.out.print("CreatFile 1 succeed.\n");



                    byte[] content = new byte[length]; //64
                    int[] size = new int[1];
                    int upper, lower;
                    // OpenFile opens the file that specified by the ID. Other information about the file
                    // is returned if the file is openned successfully.
                    iKey.OpenFile(iKeyFlag.IKEY_FILE_READ + iKeyFlag.IKEY_FILE_WRITE, 1, fi);
                    System.out.print("OpenFile 1 succeed.\n");
                    System.out.print("     Granted Access Right : 0x" + Integer.toHexString(fi.chGrantedAccess) + "\n");

                    //content[0] = content[1] = content[2] = content[3] = 0x55;
                    // Write write the data to the file that is currently open.
                    //iKey.Write(0, 0, content, 4, size);
                    iKey.Write(0, 0, str.getBytes(), length, size);
                    System.out.print("Write succeed. " + Integer.toString(size[0]) + " bytes written.\n");

                    content = new byte[length]; //64
                    // Read read the data from the file that is currently open.
                    iKey.Read(0, 0, content, length, size);
                    System.out.print("Read succeed. " + Integer.toString(size[0]) + " bytes read.\n");
                    data = "    File Content : ";
//                    for (int i = 0; i < size[0]; i++) {
//                        upper = lower = content[i];
//                        lower &= 0xF;
//                        upper >>= 4;
//                        upper &= 0xF;
//
//                        data = data + ByteToHx(upper) + ByteToHx(lower);
//                    }
                    System.out.print(data + "\n");
                    System.out.print("------------" + new String(content) + "--------------\n");

                    // CloseFile close the file that is currently open.
                    iKey.CloseFile();
                    System.out.print("CloseFile 1 succeed.\n");
                } catch (ComFailException e) {
                    System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
                } finally {
                    // CloseDevice close the iKey device that is currently open.
                    iKey.CloseDevice();
                    System.out.print("CloseDevice succeed!\n");
                }
            } catch (ComFailException e) {
                System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
            } finally {

                // DeleteContext delete the current iKey context.
                iKey.DeleteContext();
                System.out.print("DeleteContext succeed!\n");
            }
        } catch (ComFailException e) {
            System.out.print("COM Exception: " + getErrorMessages(e.getHResult()) + "\n");
        }

        return isSuccessed;
    }

    public static String readKey() {
        String content = "";

        return content;
    }

    public static void writeKey(String content) {
    }

    public static void main(String[] args) {
//        boolean isSuccessed = isInsertUKey();
//        if (isSuccessed) {
//            System.out.println("<<<<<<<<<<<<<<<<<open UKey successed!>>>>>>>>>>>>>>>>>>>");
//        } else {
//            System.out.println("<<<<<<<<<<<<<<<<<can not find UKey!>>>>>>>>>>>>>>>>>>>");
//        }

        int[] str = new int[4];
        str[0] = 120;
        str[1] = 86;
        str[2] = 52;
        str[3] = 18;

        System.out.println(Integer.toHexString(str[0]));
    }

    private static String getErrorMessages(int hr) {
        String msg = "";
        switch (hr) {
            case iKeyFlag.RB_CANNOT_OPEN_DRIVER:
                msg = "Failed To Open iKey Driver.";
                break;
            case iKeyFlag.RB_INVALID_DRVR_VERSION:
                msg = "Invalid iKey Driver Version.";
                break;
            case iKeyFlag.RB_INVALID_COMMAND:
                msg = "Invalid Command.";
                break;
            case iKeyFlag.RB_ACCESS_DENIED:
                msg = "Access Denied.";
                break;
            case iKeyFlag.RB_ALREADY_ZERO:
                msg = "Already Zero.";
                break;
            case iKeyFlag.RB_UNIT_NOT_FOUND:
                msg = "iKey Not Found.";
                break;
            case iKeyFlag.RB_DEVICE_REMOVED:
                msg = "iKey Has Been Removed.";
                break;
            case iKeyFlag.RB_COMMUNICATIONS_ERROR:
                msg = "Communication Error.";
                break;
            case iKeyFlag.RB_DIR_NOT_FOUND:
                msg = "Directory Not Found.";
                break;
            case iKeyFlag.RB_FILE_NOT_FOUND:
                msg = "File Not Found.";
                break;
            case iKeyFlag.RB_MEM_CORRUPT:
                msg = "memory Corrupted.";
                break;
            case iKeyFlag.RB_INTERNAL_HW_ERROR:
                msg = "Internel Hardware Error.";
                break;
            case iKeyFlag.RB_INVALID_RESP_SIZE:
                msg = "Invalid Response Size.";
                break;
            case iKeyFlag.RB_PIN_EXPIRED:
                msg = "PIN Expired.";
                break;
            case iKeyFlag.RB_ALREADY_EXISTS:
                msg = "Item Already Exist.";
                break;
            case iKeyFlag.RB_NOT_ENOUGH_MEMORY:
                msg = "Not Enough Memory.";
                break;
            case iKeyFlag.RB_INVALID_PARAMETER:
                msg = "Invalid Parameter.";
                break;
            case iKeyFlag.RB_ALIGNMENT_ERROR:
                msg = "Alignment Error.";
                break;
            case iKeyFlag.RB_INPUT_TOO_LONG:
                msg = "Input Too Long.";
                break;
            case iKeyFlag.RB_INVALID_FILE_SELECTED:
                msg = "Invalid File Selected.";
                break;
            case iKeyFlag.RB_DEVICE_IN_USE:
                msg = "iKey Device In Use.";
                break;
            case iKeyFlag.RB_INVALID_API_VERSION:
                msg = "Invalid iKey API Version.";
                break;
            case iKeyFlag.RB_TIME_OUT_ERROR:
                msg = "iKey Time Out.";
                break;
            case iKeyFlag.RB_ITEM_NOT_FOUND:
                msg = "Item Not Found.";
                break;
            case iKeyFlag.RB_COMMAND_ABORTED:
                msg = "Command Aborted.";
                break;
            case iKeyFlag.RB_INVALID_STATUS:
                msg = "Invalid Status.";
                break;
            case iKeyFlag.RB_LIBRARY_NOT_FOUND:
                msg = "iKey Library DLL Not Found.";
                break;
            case iKeyFlag.RB_LIBRARY_OBSOLETE:
                msg = "iKey Library DLL Obsolete.";
                break;
            case iKeyFlag.RB_LIBRARY_MISMATCH:
                msg = "iKey Library DLL Mismatch.";
                break;
            default:
                msg = "Unknown iKey Error.";
        }

        return msg;
    }

    private static char ByteToHx(int In_int) {
        char result;

        if (In_int >= 0 && In_int <= 9) {
            result = (char) ('0' + In_int);
        } else if (In_int >= 10 && In_int <= 15) {
            result = (char) ('A' + In_int - 10);
        } else {
            result = 'O';
        }

        return result;
    }

    // This routine converts 4 element byte array into a Java int
    private static int BytesToInt(byte[] ba) {
        int result = 0;
        int temp = 0;

        temp = ba[3];
        result = temp & 0xFF;
        temp = ba[2] & 0xFF;
        result = result * 0x100 + temp;
        temp = ba[1] & 0xFF;
        result = result * 0x100 + temp;
        temp = ba[0] & 0xFF;
        result = result * 0x100 + temp;

        return result;
    }
}
