package de.pioneerdu.pms.plugins.remotedelete;

import java.io.File;

public class FolderStatistics {

  protected int totalFolder=0;
  protected int totalFile=0;

  public long getFolderSize(File folder) {
    totalFolder++; 
        System.out.println("Folder: " + folder.getName());
    long foldersize = 0;

    File[] filelist = folder.listFiles();
    for (int i = 0; i < filelist.length; i++) {
      if (filelist[i].isDirectory()) {
        foldersize += getFolderSize(filelist[i]);
      } else {
        totalFile++;
        foldersize += filelist[i].length();
      }
    }
        return foldersize;
  }
  public int getTotalFolder() {
    return totalFolder;
  }
  public int getTotalFile() {
    return totalFile;
  }
}
