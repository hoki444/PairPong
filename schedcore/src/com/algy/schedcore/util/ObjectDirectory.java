package com.algy.schedcore.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;



public class ObjectDirectory <T> implements Iterable<T> {
    public static interface DirItem <T> extends Iterable<T> {
        public String getName();
        public Iterable<DirItem<T>> subdirs();
        public Iterable<T> subobjs();
        public Iterable<Entry<String, T>> subobjEntries();
    }
    
    private static String concat(String a, String b) {
        if (a.isEmpty())
            return b;
        else {
            return a + "/" + b;
        }
    }

    private static class DirItemImpl<T> implements DirItem<T> {
        private HashMap<String, DirItemImpl<T>> subdirs = new HashMap<String, DirItemImpl<T>>();
        private HashMap<String, T> subobjs = new HashMap<String, T>();
        private String reprName;
        
        public DirItemImpl(String reprName) {
            this.reprName = reprName;
        }
        
        public boolean hasSubDir(String name) {
            return subdirs.containsKey(name);
        }
        
        public DirItemImpl<T> getSubDir (String name) {
            return subdirs.get(name);
        }

        public void setSubDir (String name, DirItemImpl<T> diritem) {
            subdirs.put(name, diritem);
        }
        
        public DirItemImpl<T> removeSubDir (String name) {
            return subdirs.remove(name);
        }
        
        public boolean hasObj(String name) {
            return subobjs.containsKey(name);
        }
        
        @Override
        public String toString() {
            return "DirItemImpl (" + reprName + ") " + subdirs + " and " + subobjs;
        }
        
        public T getObj (String name) {
            return subobjs.get(name);
        }

        public void setObj (String name, T obj) {
            subobjs.put(name, obj);
        }
        
        public T removeObj (String name) {
            return subobjs.remove(name);
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private Stack<DirItem<T>> stack = new Stack<DirItem<T>>();
                private Iterator<T> objs = null;
                {
                    stack.push(DirItemImpl.this);
                    prepareStack();
                }
                
                private void prepareStack () {
                    while (!stack.isEmpty() && (objs == null || !objs.hasNext())) {
                        DirItem<T> item = stack.pop();
                        objs = item.subobjs().iterator();
                        for (DirItem<T> subdir : item.subdirs()) {
                            stack.push(subdir);
                        }
                    }
                }

                @Override
                public boolean hasNext() {
                    return objs != null && objs.hasNext();
                }

                @Override
                public T next() {
                    T result = objs.next();
                    prepareStack();
                    return result;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        public Iterable <Entry<String, T>> entries () {
            return new Iterable<Entry<String,T>>() {
                @Override
                public Iterator<Entry<String, T>> iterator() {
                    return new Iterator<Entry<String,T>>() {
                        private Stack<DirItem<T>> stack = new Stack<DirItem<T>>();
                        private Iterator<Entry<String, T>> objs = null;
                        private String curDirName;
                        {
                            stack.push(DirItemImpl.this);
                            prepareStack();
                        }
                        
                        private void prepareStack () {
                            while (!stack.isEmpty() && (objs == null || !objs.hasNext())) {
                                DirItem<T> item = stack.pop();
                                curDirName = item.getName();
                                objs = item.subobjEntries().iterator();
                                for (DirItem<T> subdir : item.subdirs()) {
                                    stack.push(subdir);
                                }
                            }
                        }

                        @Override
                        public boolean hasNext() {
                            return objs != null && objs.hasNext();
                        }

                        @Override
                        public Entry<String, T> next() {
                            Entry<String, T> entry = objs.next();
                            final String resultKey = concat(curDirName, entry.getKey());
                            final T resultValue = entry.getValue();
                            Entry<String, T> result = new Entry<String, T>() {
                                private T value = resultValue;
                                @Override
                                public String getKey() {
                                    return resultKey;
                                }

                                @Override
                                public T getValue() {
                                    return value;
                                }

                                @Override
                                public T setValue(T value) {
                                    T old = this.value;
                                    this.value = value;
                                    return old;
                                }
                            };
                            prepareStack();
                            return result;
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        }

        @Override
        public Iterable<DirItem<T>> subdirs() {
            return new Iterable<DirItem<T>>() {
                @Override
                public Iterator<DirItem<T>> iterator() {
                    return new Iterator<DirItem<T>>() {
                        private Iterator<DirItemImpl<T>> iter = subdirs.values().iterator();
                        @Override
                        public boolean hasNext() {
                            return iter.hasNext();
                        }

                        @Override
                        public DirItem<T> next() {
                            return iter.next();
                        }

                        @Override
                        public void remove() {
                            iter.remove();
                        }
                    };
                }
            };
        }

        @Override
        public Iterable<T> subobjs() {
            return subobjs.values();
        }

        @Override
        public String getName() {
            return reprName;
        }

        @Override
        public Iterable<Entry<String, T>> subobjEntries() {
            return subobjs.entrySet();
        }
    }
    
    private DirItemImpl<T> root = new DirItemImpl<T>("");
    
    public DirItem<T> rootDir() {
        return root;
    }

    public boolean hasDir (String dirname) {
        ArrayList<String> dirnames = SlashName.parseDirname(dirname);
        return searchDir(dirnames, false) != null;
    }

    public DirItem<T> getDir(String dirname) {
        ArrayList<String> dirnames = SlashName.parseDirname(dirname);
        return searchDir(dirnames, false);
    }

    public boolean putDir (String dirname, boolean cascade) {
        ArrayList<String> dirnames = SlashName.parseDirname(dirname);
        int size = dirnames.size();
        if (size == 0)
            return true;
        String lastName = dirnames.get(size - 1);
        
        DirItemImpl<T> dirItem = searchDir(dirnames.subList(0, size - 1), cascade);
        
        if (dirItem == null)
            return false;
        else if (!dirItem.hasSubDir(lastName))
            dirItem.setSubDir(lastName,
                              new DirItemImpl<T>(dirItem.getName().equals("")?  
                                lastName :
                                dirItem.getName() + "/" + lastName));
        return true;
    }

    public Iterable<T> removeDir (String dirname) {
        ArrayList<String> dirnames = SlashName.parseDirname(dirname);
        if (dirnames.size() == 0) {
            Iterable<T> result = root;
            root = new DirItemImpl<T>("");
            return result;
        } else {
            String lastName = dirnames.get(dirnames.size() - 1);
            DirItemImpl<T> superDir = searchDir(dirnames.subList(0, dirnames.size() - 1), false);
            if (superDir == null)
                return IterableUtil.getEmptyIterable();
            else {
                DirItemImpl<T> subdir = superDir.removeSubDir(lastName);
                if (subdir == null)
                    return IterableUtil.getEmptyIterable();
                else
                    return subdir;
            }
        }
    }
    
    public boolean has (String objpath) {
        SlashName assetName = SlashName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, false);
        if (dir == null)
            return false;
        return dir.hasObj(assetName.objname);
    }
    

    public boolean put (String objpath, T obj, boolean makeDir) {
        SlashName assetName = SlashName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, makeDir);
        if (dir == null)
            return false;
        dir.setObj(assetName.objname, obj);
        return true;
    }

    public T get(String objpath) {
        SlashName assetName = SlashName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, false);
        if (dir == null)
            return null;
        return dir.getObj(assetName.objname);
    }

    
    
    public T remove (String objpath) {
        SlashName assetName = SlashName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, false);
        if (dir == null)
            return null;
        return dir.removeObj(assetName.objname);
    }


    @Override
    public Iterator<T> iterator() {
        return root.iterator();
    }
    
    public Iterable<Entry<String, T>> entries () {
        return root.entries();
    }
    
    private DirItemImpl <T> searchDir (Iterable<String> dirnames, boolean makedir) {
        DirItemImpl <T> iterDir = root;
        String itername = "";
        for (String curDirName : dirnames) {
            itername = concat(itername, curDirName);
            if (!iterDir.hasSubDir(curDirName)) {
                if (makedir)
                    iterDir.setSubDir(curDirName, new DirItemImpl<T>(itername));
                else
                    return null;
            }
            iterDir = iterDir.getSubDir(curDirName);
        }
        return iterDir;
    }
    

}