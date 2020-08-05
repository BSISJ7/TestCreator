package com.BSISJ7.TestCreator.utilities;

import javax.swing.*;

public class DuplicateChecker {

    public static boolean dupStringChk(String newValue, String[] list) {

        boolean isDuplicate = false;

        for (int x = 0; x < list.length; x++) {
			/*if(newValue instanceof String){
				if(((String)newValue).equalsIgnoreCase(((String)data[x]))){
					isDuplicate = true;
					break;
				}
			}*/
            if (newValue.equalsIgnoreCase(list[x])) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }

    public static boolean dupStringChk(String newValue, DefaultListModel list) {
        boolean isDuplicate = false;

        for (int x = 0; x < list.size(); x++) {
            if (newValue.equalsIgnoreCase((String) list.getElementAt(x))) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }

    public static boolean dupStringChk(String newValue, DefaultListModel list, int currentIndex) {
        boolean isDuplicate = false;

        for (int x = 0; x < list.size(); x++) {
            if (currentIndex == x)
                continue;
            if (newValue.equalsIgnoreCase((String) list.getElementAt(x))) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }
}
