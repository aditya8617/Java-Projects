//Simulates directory and its properties

import java.util.*;

public class DirectoryNode {
  
  private DirectoryNode parent = null; //parent of this directory
  private String name = null; //dir name
  private ArrayList<DirectoryNode> subDir = new ArrayList<DirectoryNode>(); // list of its subdirectories
  private ArrayList<FileNode> files = new ArrayList<FileNode>(); //list of iles in this directory
  
  private ArrayList<String> readAccess = new ArrayList<String>(); // read permission for this directory
  private ArrayList<String> writeAccess = new ArrayList<String>(); //write permissions
  private ArrayList<String> execAccess = new ArrayList<String>(); //exec permissions
  private ArrayList<String> ownerList = new ArrayList<String>(); //list of owners
  private ArrayList<String> grpList = new ArrayList<String>(); // list of groups
  private ArrayList<String> grpPermList = new ArrayList<String>();
  
  public DirectoryNode(String nam) //constructor
  {
    this.name = nam;
  }
  
  public DirectoryNode(DirectoryNode par, String nam) //constructor
  {
    this.parent = par;
    this.name = nam;
    readAccess.add(nam);
    writeAccess.add(nam);
    execAccess.add(nam);
  }
  
  public String mainOwner() // creator of directory or for user super created the directory
  {
    return ownerList.get(0);
  }
  
  public ArrayList<DirectoryNode> getSubDirList() //returns list of all subdirs
  {
    return subDir;
  }
  
  public ArrayList<FileNode> getFileList() //returns list of all files in the directory
  {
    return files;
  }
  
  //checks if a user has any permissions on this directory
  public Boolean isOwner(String own)
  {
    return ownerList.contains(own);
  }
  
  //gives specified user complete access
  public void addCompAccess(String nam)
  {
    readAccess.add(nam);
    writeAccess.add(nam);
    execAccess.add(nam);
    ownerList.add(nam);
  }
  
  // removes all access from specified user
  public void rmCompAccess(String nam)
  {
    readAccess.remove(nam);
    writeAccess.remove(nam);
    execAccess.remove(nam);
    ownerList.remove(nam);
  }
  
  // checks if specified user has write permission on this dir
  public Boolean hasWritePerm(String user)
  {
    return (user.equals("super") || writeAccess.contains(user));
  }
  
  // checks if specified user has read permission on this dir
  public Boolean hasReadPerm(String user)
  {
    return (user.equals("super") || readAccess.contains(user));
  }
  
  // checks if specified user has exec permission on this dir
  public Boolean hasExecPerm(String user)
  {
    return (user.equals("super") || execAccess.contains(user));
  }
  
  //edits access for specified user.
  public void editAccess(String nam, String edit)
  {
    if(edit.indexOf("+") > -1)
    {
      addAccess(nam, edit);
    }else
    {
      rmAccess(nam, edit);
    }
  }
  
  //private function which adds acces for specified user.
  private void addAccess(String nam, String perm)
  {
    if(perm.indexOf("r") > -1)
    {
      readAccess.add(nam);
    }
    if(perm.indexOf("w") > -1)
    {
      writeAccess.add(nam);
    }
    if(perm.indexOf("x") > -1)
    {
      execAccess.add(nam);
    }
    if(!ownerList.contains(nam))
    {
      ownerList.add(nam);
    }
  }
 
//private function which removes acces from specified user.
  private void rmAccess(String nam, String perm)
  {
    if(perm.indexOf("r") > -1)
    {
      readAccess.remove(nam);
    }
    if(perm.indexOf("w") > -1)
    {
      writeAccess.remove(nam);
    }
    if(perm.indexOf("x") > -1)
    {
      execAccess.remove(nam);
    }
    if(!readAccess.contains(nam) && !writeAccess.contains(nam) && !execAccess.contains(nam))
    {
      ownerList.remove(nam);
    }
  }
  
  //gives exec permission to specified user for this dir
  public void editExecPerm(String user)
  {
    execAccess.add(user);
  }
  
  public String dirName()
  {
    return this.name;
  }
  
  // returns parent directory of this directory.
  public DirectoryNode dirParent()
  {
    return this.parent;
  }
  
  //adds a subdir
  public void addSubDir(DirectoryNode nam)
  {
    this.subDir.add(nam);
  }

  //adds a file to this directory
  public void addFile(FileNode f)
  {
    this.files.add(f);
  }
  
  //checks if specified file exists in this directory
  public Boolean isAFile(String f)
  {
    Iterator<FileNode> it = files.iterator();
    
    while(it.hasNext())
    {
      FileNode temp = it.next();
      if(temp.fileName().equals(f))
      {
        return true;
      }
    }
    return false;
  }
  
  // returns reference to a file of this directory
  public FileNode getFile(String f)
  {
    Iterator<FileNode> it = files.iterator();
    
    while(it.hasNext())
    {
      FileNode temp = it.next();
      if(temp.fileName().equals(f))
      {
        return temp;
      }
    }
    return null;
  }
  
  // retruns directory reference of a existing subdir
  public DirectoryNode getSubDir(String nam)
  {
    Iterator<DirectoryNode> it = subDir.iterator();
    
    while(it.hasNext())
    {
      DirectoryNode temp = it.next();
      if(temp.dirName().equals(nam))
      {
        return temp;
      }
    }
    return null;
  }
  
  // checks if a given directory is a subdir of this directory
  public Boolean isSubDir(DirectoryNode sDir)
  {
    Iterator<DirectoryNode> it = subDir.iterator();
    
    while(it.hasNext())
    {
      DirectoryNode temp = it.next();
      if(temp.dirName().equals(sDir.dirName()))
      {
        return true;
      }
    }
    return false;
  }
  
  //deletes an existing subdir 
  public Boolean deleteSubDir(DirectoryNode sDir)
  {
    Iterator<DirectoryNode> it = subDir.iterator();
    
    while(it.hasNext())
    {
      DirectoryNode temp = it.next();
      if(temp.dirName().equals(sDir.dirName()))
      {
        return subDir.remove(temp);
      }
    }
    return false;
  }
  
  //deletes an existing file
  public Boolean deleteFile(FileNode f)
  {
    Iterator<FileNode> it = files.iterator();
    
    while(it.hasNext())
    {
      FileNode temp = it.next();
      if(temp.fileName().equals(f.fileName()))
      {
        return files.remove(temp);
      }
    }
    return false;
  }
  
  //deletes all subdirs
  public void deleteAllSubDir()
  {
    subDir.clear();
  }
  
  //deletes all files
  public void deleteAllFiles()
  {
    files.clear();
  }
  
  //prints list of directory owners
  public void printOwnerList()
  {
    String owner = ownerList.get(0);
    System.out.print("user::");
    printPerm(owner);
    System.out.println();
    for(int i = 1; i < ownerList.size(); i++)
    {
      owner = ownerList.get(i);
      System.out.print("user:" + owner + " ");
      printPerm(owner);
      System.out.println();
    }
  }
  
  // private function prints permissions for the directory owners
  private void printPerm(String owner)
  {
    if(hasReadPerm(owner))
    {
      System.out.print("r");
    }
    if(hasWritePerm(owner))
    {
      System.out.print("w");
    }
    if(hasExecPerm(owner))
    {
      System.out.print("x");
    }
  }
  
  // adds group to group owners list
  public void addGroup(String grp, String perm)
  {
    grpList.add(grp);
    grpPermList.add(perm.substring(1));
  }
  
  public void printGrpPerm()
  {
    for(int i = 0; i < grpList.size(); i++)
    {
      System.out.println("group:" + grpList.get(i) + " " + grpPermList.get(i));
    }
  }
  
  // prints all subdirs in this directory
  public void printSubDirs()
  {
    System.out.print("subdir(s): ");
    int size = subDir.size();
    for(int i = 0; i < size; i++)
    {
      DirectoryNode temp = subDir.get(i);
      if(i == size-1)
      {
        System.out.print(temp.dirName());
      }else
      {
        System.out.print(temp.dirName() + ", ");
      }
    }
    System.out.println();
  }
  
  //prints all files in thid directory
  public void printFiles()
  {
    System.out.print("File(s): ");
    int size = files.size();
    for(int i = 0; i < size; i++)
    {
      FileNode temp = files.get(i);
      if(i == size-1)
      {
        System.out.print(temp.fileName());
      }else
      {
        System.out.print(temp.fileName() + ", ");
      }
    }
    System.out.println();
  }
  
  //prints all owner permissions
  public void printPerm()
  {
    System.out.print("Permissions: ");
    int size = ownerList.size();
    for(int i = 0; i < size; i++)
    {
      String temp = ownerList.get(i);
      System.out.print(temp);
      System.out.print("(");
      printPerm(temp);
      System.out.print(")");
      if(i != size-1)
      {
        System.out.print(", ");
      }
    }
    System.out.println();
  }
}