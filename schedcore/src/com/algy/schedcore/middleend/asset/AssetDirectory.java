package com.algy.schedcore.middleend.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.algy.schedcore.util.IterableUtil;


public class AssetDirectory <T> implements Iterable<T> {
    public static interface DirItem <T> extends Iterable<T> {
        public String getName();
        public Iterable<DirItem<T>> subdirs();
        public Iterable<T> subobjs();
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
                    prepareStack();
                    return objs.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
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
    }
    
    private DirItemImpl<T> root = new DirItemImpl<T>("");
    
    public DirItem<T> rootDir() {
        return root;
    }

    public boolean hasDir (String dirname) {
        ArrayList<String> dirnames = AssetName.parseDirname(dirname);
        return searchDir(dirnames, false) != null;
    }

    public DirItem<T> getDir(String dirname) {
        ArrayList<String> dirnames = AssetName.parseDirname(dirname);
        return searchDir(dirnames, false);
    }

    public boolean putDir (String dirname, boolean cascade) {
        ArrayList<String> dirnames = AssetName.parseDirname(dirname);
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
        ArrayList<String> dirnames = AssetName.parseDirname(dirname);
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
        AssetName assetName = AssetName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, false);
        if (dir == null)
            return false;
        return dir.hasObj(assetName.objname);
    }
    

    public boolean put (String objpath, T obj, boolean makeDir) {
        AssetName assetName = AssetName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, makeDir);
        if (dir == null)
            return false;
        dir.setObj(assetName.objname, obj);
        return true;
    }

    public T get(String objpath) {
        AssetName assetName = AssetName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, false);
        if (dir == null)
            return null;
        return dir.getObj(assetName.objname);
    }

    
    
    public T remove (String objpath) {
        AssetName assetName = AssetName.parse(objpath);
        DirItemImpl<T> dir = searchDir(assetName.dirnames, false);
        if (dir == null)
            return null;
        return dir.removeObj(assetName.objname);
    }


    @Override
    public Iterator<T> iterator() {
        return root.iterator();
    }
    
    private DirItemImpl <T> searchDir (Iterable<String> dirnames, boolean makedir) {
        DirItemImpl <T> iterDir = root;
        String itername = "";
        for (String curDirName : dirnames) {
            if (itername.isEmpty())
                itername += curDirName;
            else {
                itername += "/";
                itername += curDirName;
            }

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