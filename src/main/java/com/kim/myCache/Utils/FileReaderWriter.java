package com.kim.myCache.Utils;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@SuppressWarnings("unchecked")
public class FileReaderWriter {
    private static final String INDEX_SUFFIX = ".index";
    private static final String DATA_SUFFIX = ".data";
    private String root;
    private File indexFile;
    private Map<Integer, String> indexMap;

    public FileReaderWriter(String root) {
        this.root = root;
        loadIndex();
    }

    private void loadIndex() {
        ObjectInputStream objectInputStream = null;
        try {
            indexFile = new File(root + File.separator + INDEX_SUFFIX);
            if (!indexFile.exists()) {
                initIndexFile();
            }
            objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(indexFile)));
            indexMap = (Map<Integer, String>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initIndexFile() {

    }
}
