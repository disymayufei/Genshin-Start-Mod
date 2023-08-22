package cn.disy920.genshin_start.utils;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class LnkParser {
    @Nullable
    public static String getLnkFile(File lnkFile) {
        RandomAccessFile r = null;
        ByteArrayOutputStream bos = null;
        String filename = null;
        try {
            r = new RandomAccessFile(lnkFile, "r");
            byte[] bys = new byte[4];

            // 定位到 Shell item ID list 段的长度定义起始地址
            // 以便于计算下一段（即文件位置信息段的起始地址）
            r.seek(0x4c);
            r.read(bys, 0, 2);
            int offset = bytes2Int(bys, 0, 2);

            // 获得文件位置信息段的起始地址
            int fileLocationInfoSagement = offset + 0x4e;

            // 获得本地路径信息段的起始地址
            int filePathInfoSagement = fileLocationInfoSagement + 0x10;

            // 定位到本地路径信息段，以便获得本地路径信息的偏移地址
            r.seek(filePathInfoSagement);

            // 获得本地路径信息的偏移
            r.read(bys, 0, 4);
            int filePathInfoOffset = fileLocationInfoSagement + bytes2Int(bys, 0, 4);

            // 定位到本地路径信息起始地址，开始读取路径信息
            r.seek(filePathInfoOffset);
            bos = new ByteArrayOutputStream();
            for(byte b = 0; (b = r.readByte()) != 0;) {
                bos.write(b);
            }
            // 将读出路径信息字节存入 byte 数组中
            bys = bos.toByteArray();

            // 采用本地编码获得路径信息文件名称
            filename = new String(bys);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filename;
    }

    public static int bytes2Int(byte[] bys, int start, int len) {
        int n = 0;
        for(int i = start, k = start + len % 5; i < k; i++) {
            n += (bys[i] & 0xff) << (i * 8);
        }
        return n;
    }
}
