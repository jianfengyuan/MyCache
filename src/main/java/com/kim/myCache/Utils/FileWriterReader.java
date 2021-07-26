package com.kim.myCache.Utils;

import com.kim.myCache.Entities.Entry;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @program: MyCache
 * @description:
 * @author: Kim_yuan
 * @create: 2021-07-25 16:28
 **/
public class FileWriterReader {
    private static final String DATA_SUFFIX = ".data";
    private String root;
    private Map<Integer, String> indexMap;
    private static final Set<Character> ILLEGALS = new HashSet<>();
    static {
        ILLEGALS.add('/');
        ILLEGALS.add('\\');
        ILLEGALS.add('<');
        ILLEGALS.add('>');
        ILLEGALS.add(':');
        ILLEGALS.add('"');
        ILLEGALS.add('|');
        ILLEGALS.add('?');
        ILLEGALS.add(';');
        ILLEGALS.add('*');
        ILLEGALS.add('.');
    }

    public FileWriterReader(String root) {
        indexMap = new ConcurrentHashMap<>();
        this.root = root;
    }


    public <K, V> Boolean add(K k, Entry<K, V> entry) {
        int hash = hash(k);
        String history = indexMap.get(hash);
        if (history != null) {
            new File(history).delete();
        }
        String filename = getFileName(UUID.randomUUID().toString());
        String path = new StringBuilder().append(root).append(File.separator)
                .append(filename).append(DATA_SUFFIX).toString();
        File data = new File(path);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(data)));
            oos.writeObject(entry);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        indexMap.put(hash, path);
        return true;
    }

    public <T, K> T read(K k) {
        int hash = hash(k);
        String path = indexMap.get(hash);
        if (path == null) {
            return null;
        }
        T data = (T) loadData(path);
        if (data == null) {
            remove(k);
        }
        return data;
    }

    public <K> Boolean remove(K k) {
        int hash = hash(k);
        return remove(hash);
    }

    public Boolean remove(int hash) {
        String path = indexMap.get(hash);
        if (path == null) {
            return false;
        }
        indexMap.remove(hash);
        File file = new File(path);
        return file.delete();
    }

    public void clear() {
        String path;
        File file;
        Set<Integer> keySet = indexMap.keySet();
        for (int k :
                keySet) {
            path = indexMap.get(k);
            file = new File(path);
            file.delete();
        }
        indexMap.clear();
    }

    private Object loadData(String path) {
        File dataFile = new File(path);
        if (!dataFile.exists()) {
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(dataFile)));
            return ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileName(String name){
        int len = name.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c <= ' ' || c >= 127 || (c >= 'A' && c <= 'Z' || ILLEGALS.contains(c))) {
                sb.append("%");
                sb.append(String.format("%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /***
     * @Author kim_yuan
     * @Description Hash the key like Hashmap in JDK.
     * @Date 9:49 上午 26/7/21
     * @param key
     * @return int
     **/
    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
