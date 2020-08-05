package com.BSISJ7.TestCreator.utilities;

import java.io.File;

public class ExtensionChecker {

    public static String getExt(File checkFile) {
        //javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        //String filetype = fc.getTypeDescription(new java.io.File(checkFile.getPath()));
        String filetype = checkFile.getName().substring(checkFile.getName().lastIndexOf(".") + 1);
        return filetype;
    }
	
	/*public static boolean isImage(File checkFile){
		return (ExtensionChecker.getExt(checkFile).equalsIgnoreCase("JPEG Image") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("Bitmap Image")
					|| ExtensionChecker.getExt(checkFile).equalsIgnoreCase("IrfanView JPG File") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("JPG File")
					&& !checkFile.getName().equalsIgnoreCase("blank.jpg"));
	}
	
	public static boolean isImage(String imageName){
		File checkFile = new File(imageName);
		return (ExtensionChecker.getExt(checkFile).equalsIgnoreCase("JPEG Image") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("Bitmap Image")
					|| ExtensionChecker.getExt(checkFile).equalsIgnoreCase("IrfanView JPG File") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("JPG File")
					&& !checkFile.getName().equalsIgnoreCase("blank.jpg"));
	}*/

    public static boolean isImage(File checkFile) {
        return (ExtensionChecker.getExt(checkFile).equalsIgnoreCase("jpg") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("bmp")
                && !checkFile.getName().equalsIgnoreCase("blank.jpg"));
    }

    public static boolean isImage(String imageName) {
        File checkFile = new File(imageName);
        return (ExtensionChecker.getExt(checkFile).equalsIgnoreCase("jpg") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("bmp")
                && !checkFile.getName().equalsIgnoreCase("blank.jpg"));
    }
}
