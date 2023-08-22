package cn.disy920.genshin_start.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class GenshinUtil {

    private static Process genshinProcess = null;
    private static Thread maxGenshinWindowThread = null;

    public static boolean containGenshinStartCommand(String command) {
        String[] commandNodes;

        if (command.contains(",")) {
            commandNodes = command.split(",");
        }
        else if (command.contains("，")) {
            commandNodes = command.split("，");
        }
        else if (command.contains(" ")) {
            commandNodes = command.split(" ");
        }
        else {
            return false;
        }

        if (commandNodes.length != 2) {
            return false;
        }

        String firstNode = toPinyin(commandNodes[0]);
        String secondNode = toPinyin(commandNodes[1]);

        return (firstNode.equals("yuanshen") || firstNode.equals("yuanshiren")) && secondNode.equals("qidong");
    }

    public static PROCESS_STATUS startGenshin() {
        try {
            if (genshinProcess != null && genshinProcess.isAlive()) {
                WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "原神");
                if (hwnd == null) {
                    return PROCESS_STATUS.STARTING;
                }

                return PROCESS_STATUS.STARTED;
            }

            if (isGenshinRunning()) {
                return PROCESS_STATUS.STARTED;
            }
        } catch (Exception e) {
            return PROCESS_STATUS.FAILED_WITH_EXCEPTION;
        }

        try {
            File genshinLnk = new File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\原神\\原神.lnk");
            if (!genshinLnk.isFile()) {
                return PROCESS_STATUS.NOT_EXISTS;
            }

            String installPath = LnkParser.getLnkFile(genshinLnk);
            if (installPath != null) {
                String gamePath = installPath.replace("launcher.exe", "Genshin Impact Game\\YuanShen.exe");
                genshinProcess = Runtime.getRuntime().exec("cmd /c \"" + gamePath + "\"");
            }
            else {
                return PROCESS_STATUS.NOT_EXISTS;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return PROCESS_STATUS.FAILED_WITH_EXCEPTION;
        }

        maxGenshinWindow();
        return PROCESS_STATUS.STARTING;
    }

    private static String toPinyin(String chinese){
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char c : newChar) {
            if (c > 128) {
                try {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                    if (pinyin != null && pinyin.length > 0) {
                        pinyinStr.append(pinyin[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(c);
            }
        }
        return pinyinStr.toString();
    }

    public static boolean isGenshinRunning() throws Exception {
        String buffer;

        Process process = Runtime.getRuntime().exec("TASKLIST /NH /FI \"IMAGENAME eq YuanShen.exe\"");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while ((buffer = bufferedReader.readLine()) != null) {
            if (buffer.startsWith("YuanShen.exe")) {
                return true;
            }

        }

        process.waitFor();
        process.destroy();

        return false;
    }

    private static void maxGenshinWindow() {
        if (maxGenshinWindowThread != null && !maxGenshinWindowThread.isInterrupted()) {
            maxGenshinWindowThread.interrupt();
        }

        maxGenshinWindowThread = new Thread(() -> {
            try {
                WinDef.HWND hwnd;
                do {
                    hwnd = User32.INSTANCE.FindWindow(null, "原神");
                }
                while (hwnd == null && isGenshinRunning() && !Thread.currentThread().isInterrupted());

                User32.INSTANCE.ShowWindow(hwnd, 9);
                User32.INSTANCE.SetForegroundWindow(hwnd);  // bring genshin to front
            }
            catch (Exception ignored) {}

        }, "Max Genshin Window Thread");

        maxGenshinWindowThread.start();
    }

    public enum PROCESS_STATUS {
        STARTING,
        STARTED,
        NOT_EXISTS,
        FAILED_WITH_EXCEPTION
    }
}
