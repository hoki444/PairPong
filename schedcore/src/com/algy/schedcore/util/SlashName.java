package com.algy.schedcore.util;

import java.util.ArrayList;

public class SlashName {
    public ArrayList<String> dirnames;
    public String objname;
    public SlashName (ArrayList<String> dirnames, String objname) {
        this.dirnames = dirnames;
        this.objname = objname;
    }
    
    public static ArrayList<String> parseDirname (String s) {
        SlashName slashName = parse(s);
        if (!slashName.objname.equals("")) {
            slashName.dirnames.add(slashName.objname);
        }
        return slashName.dirnames;
    }

    public static SlashName parse(String s) {
        /*
         * AssetName.parse("schedcore.example.ballObject")
         * -> dirnames ["schedcore", "example"]
         *    objname ballObject
         * 
         * "/"? ({letter}+"/")* {letter}+ "/"?
         */
        int idx = 0, len = s.length();
        
        ArrayList<String> dirnames = new ArrayList<String>();
        String objname;
        
        // "/"?
        if (idx < len && s.charAt(idx) == '/') 
            idx++;

        while (idx < len) {
            StringBuilder nameBuilder = new StringBuilder();
            char c = s.charAt(idx);
            if (isLetter(c)) {
                // {letter}+
                nameBuilder.append(c);
                idx++;

                while (idx < len) {
                    c = s.charAt(idx);
                    if (isLetter(c)) {
                        nameBuilder.append(c);
                        idx++; // Consume
                    } else if (c == '/') {
                        break;
                    } else
                        throw new IllegalArgumentException("Expected a letter or dot, " +
                                                           "got '" + c + "'. " + 
                                                           "See col " + (idx + 1));
                }

                dirnames.add(nameBuilder.toString());

                if (idx >= len) { // $
                    break;
                } else { 
                    // "/"
                    idx++; // Consume "/"
                }
            } else
                throw new IllegalArgumentException("Expected a letter, " +
                                                   "got '" + c + "'. " + 
                                                   "See col " + (idx + 1));
        }
        if (dirnames.size() == 0) {
            objname = "";
        } else {
            objname = dirnames.remove(dirnames.size() - 1);
        }
        
        return new SlashName(dirnames, objname);
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String dirname : dirnames) {
            result.append(dirname);
            result.append('/');
        }
        result.append(objname);

        return result.toString();
    }
    private static boolean isLetter(char c) {
        return 
            c >= 'a' && c <= 'z' ||  
            c >= 'A' && c <= 'Z' ||
            c >= '0' && c <= '9' ||
            c == '_';
    }
}