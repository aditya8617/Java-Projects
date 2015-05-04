// Class simulating a file and it's properties

import java.util.*;

public class FileNode {
  
  private String name = null; //file name
  
  private ArrayList<String> readAccess = new ArrayList<String>();  //list of users that can read this file
  private ArrayList<String> writeAccess = new ArrayList<String>(); //list of users that can write to this file
  private ArrayList<String> execAccess = new ArrayList<String>(); // list of users that can see this file but not read it or write to it
  private ArrayList<String> ownerList = new ArrayList<String>(); // list of users who have some type of access to this file.
  private ArrayList<String> grpList = new ArrayList<String>(); //groups that have some type of access to this file
  private ArrayList<String> grpPermList = new ArrayList<String>(); // list of type of access a group has on this file
  
  public FileNode(String nam) //constructor
  {
    this.name = nam;
  }
  
  public String fileName()
  {
    return this.name;
  }
  
  public String mainOwner()
  {
    return ownerList.get(0); //main owner is orignal creator of the file.
  }
  
  // checks if  agiven user is one of the owners of the file
  public Boolean isOwner(String own)
  {
    return ownerList.contains(own);
  }
  
  //gives specified user complete ownership of this file
  public void addCompAccess(String nam) 
  {
    readAccess.add(nam);
    writeAccess.add(nam);
    execAccess.add(nam);
    ownerList.add(nam);
  }
  
  // removes all type of access for the specified user.
  public void rmCompAccess(String nam)
  {
    readAccess.remove(nam);
    writeAccess.remove(nam);
    execAccess.remove(nam);
    ownerList.remove(nam);
  }
  
  // edits permissions to this file for the specified user
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
  
  //private function which gives access to given user
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
  
  //private function which removes access from given user 
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
  
  // prints list of owners that have any access to this file
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
  
  //prints permissions for a specified owner
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
  
  // adds group to a groups list
  public void addGroup(String grp, String perm)
  {
    grpList.add(grp);
    grpPermList.add(perm.substring(1));
  }
  
  // adds permissions for the groups
  public void printGrpPerm()
  {
    for(int i = 0; i < grpList.size(); i++)
    {
      System.out.println("group:" + grpList.get(i) + " " + grpPermList.get(i));
    }
  }
  
  // checks if a given user has write permissions to this file
  public Boolean hasWritePerm(String user)
  {
    return (user.equals("super") || writeAccess.contains(user));
  }
  
  //checks if given user has read permissiosn to this file
  public Boolean hasReadPerm(String user)
  {
    return (user.equals("super") || readAccess.contains(user));
  }
  
  //checks if a given user has executive permissions to this file
  public Boolean hasExecPerm(String user)
  {
    return (user.equals("super") || execAccess.contains(user));
  }
  
  // checks if given user has all rwx permisssions.
  public Boolean hasAllPerm(String user)
  {
    return (hasWritePerm(user) && hasReadPerm(user) && hasExecPerm(user));
  }
  
  //prints to console the list of owners and type of permission they have on this file
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