/*
 * FileSystemSimulator is the main class. All the methods implementing commands are defined in this class. The class reads in 2 text files users.txt and groups.txt and creates a user list from them.
 * For PA3 added chpassword & adduser commands. Modified login functionality to add password encryption.
 */

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.math.BigInteger;

public class FileSystemSimulator {
  
  private static ArrayList<User> userList = new ArrayList<User>(); //list of users using the system
  private static DirectoryNode root = new DirectoryNode("Root"); //The root directory.
  private static DirectoryNode iterator = null; //current directory of the logged in user
  private static String PATH = ""; //current path of the logged in user
  private static String CURRENT_USER = ""; //current user
  // new PA 3 globals
  private static int PASS_LENGTH = 8; 
  private static BigInteger p = new BigInteger("10585858787394598436956327946543865"); 
  private static BigInteger q = new BigInteger("689798797034765034605236406520346502");
  private static RSA r = null;
  
  public static void main(String[] args){
    createSuper();
    setSecurity();
    try
    {
      File file = new File("users.txt");
      if(file.exists())
      {
        Scanner scanner = new Scanner(file);
        readUsers(scanner);
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    login();
  }
  
  //creates security mechanism of the system using RSA algorithm. 
  private static void setSecurity()
  {
    try
    {
      File file = new File("super.dat"); //super.dat will never be created by system.
      
      if(!file.exists())
      {
        r = new RSA(p, q);
        appendFile(file, r.getPublicKey(), true);
        appendFile(file, r.getPrivateKey(), false);
        appendFile(file, r.getN(), false);
      }
      else
      {
        Scanner scanner = new Scanner(file);
        BigInteger x = BigInteger.ZERO;
        BigInteger y = BigInteger.ZERO;
        BigInteger z = BigInteger.ZERO;
        
        int counter = 0;
        while(scanner.hasNext() && counter < 3)
        {
          String s = scanner.next();
          if(counter == 0)
            x = new BigInteger(s);
          else if(counter == 1)
            y = new BigInteger(s);
          else
            z = new BigInteger(s);
          counter++;
        }
        r = new RSA(x, y, z);
      }
      
      file = new File("password.dat");
      if(!file.exists())
      {
        encryptPass("MySuperPassword");
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }
  
  // asks user for the input and sends it to other functions for execution.
  private static void askCommand()
  {
    String input = "";
    Boolean exit = false;
    while(!exit)
    {
      System.out.println("Type a command or refer to System readme.txt for list of typical commands and their usage. Type \"exit\" to shutdown the system.");
      System.out.print(PATH + ":");
      Scanner scanIn = new Scanner(System.in);
      input = scanIn.nextLine().toLowerCase();
      exit = input.equals("exit");
      if(exit)
      {
        return;
      }
      exit = input.equals("logout");
      if(exit)
      {
        login();
      }else
      {
        if(checkSyntax(input)) // checks if entered input meets expected format.
        {
          input = input.replace(",", ""); // gets rid of commas for ease of use.
          exeCommand(input);
        }else
        {
          System.out.println("Invalid syntax.");
        }
      }
    }
  }
  
  // logs in valid users with valid passwords only.
  private static void login()
  {
    String userName = "";
    String pass = "";
    Boolean valid = false;
    do
    {
      System.out.println("Please type username to log in or type \"exit\" to quit: ");
      Scanner scanIn = new Scanner(System.in);
      userName = scanIn.next().toLowerCase();
      if(userName.equals("exit"))
      {
        return;
      }
      System.out.println("Please type your password.");
      pass = scanIn.next();
      if(isValidUser(userName))
        valid = isValidPassword(userName, pass);
    } while(!valid);
    
    if(valid)
    {
      iterator = root.getSubDir(userName);
      if(iterator != null)
      {
        PATH = "\\\\root\\" + userName;
        CURRENT_USER = userName;
        askCommand();
      }else //Not expecting to be executed.
      {
        System.out.println("System crash. All data has been corrupted. Please reset system");
      }
    }
  }
  
  //parses first word of user input and identifies the command user wants executed. Uses String switch only available from Java 7.0_7 onwards.
  private static void exeCommand(String input)
  {
    String[] buffer = input.split(" ");
    
    if(buffer.length > 1)
    {
      String command = buffer[0];
        if(command.equals("mkdir"))
          mkdir(buffer);
        else if(command.equals("rmdir"))
          rmdir(buffer);
        else if(command.equals("create"))
          create(buffer);
        else if(command.equals("delete"))
          delete(buffer);
        else if(command.equals("read"))
          readWrite(buffer, "read");
        else if(command.equals("write"))
          readWrite(buffer, "edited");
        else if(command.equals("cd"))
          changeDirectory(buffer);
        else if(command.equals("setacl"))
          setacl(buffer);
        else if(command.equals("chown"))
          chown(buffer);
       else if(command.equals("getacl"))
          getacl(buffer);
       else if(command.equals("adduser"))
          adduser(buffer);
        else
          System.out.println(input + " is not recognized as a valid command");
    }
    else
    {
      if(buffer[0].equals("print"))
      {
        print();
      }else if(buffer[0].equals("chpassword"))
      {
        chpassword();
      }else if(buffer[0].equals("reset"))
      {
        reset();
      }else
      {
        System.out.println("Invalid syntax");
      }
    }
  }
  
  // the change password command
  private static void chpassword()
  {
    int key = isListedUser(CURRENT_USER);
    int index = 0;
    File file = new File("password.dat");
    ArrayList<String> passwords = new ArrayList<String>();
    try
    {
      Scanner scan = new Scanner(file);
      while(scan.hasNextLine())
      {
        System.out.println("here");
        String s = scan.nextLine();
        if(index == key)
        {
          Scanner console = new Scanner(System.in);
          do
          {
            System.out.println("Please enter the new " + PASS_LENGTH + " character password:");
            s = console.next();
            if(s.length() != PASS_LENGTH)
            {
              System.out.println("Incorrect password length");
            }
          } while(s.length() != PASS_LENGTH);
          s = encrypt(s);
        }
        passwords.add(s);
        index++;
      }
      Iterator<String> it = passwords.iterator();
      index = 0;
      while(it.hasNext())
      {
        String s = it.next();
        if(index < 1)
          appendFile(file, s, true);
        else
          appendFile(file, s, false);
        index++;
      }
      
    }catch (IOException e)
    {
      System.out.println(e);
    }
  }
  
  private static void reset()
  {
    if(!CURRENT_USER.equals("super"))
    {
      System.out.println("You do not have sufficient permissions to perform this operation.");
      return;
    }
    
    File file = new File("users.txt");
    deleteGenFile(file);
    file = new File("password.dat");
    deleteGenFile(file);
    file = new File("super.dat");
    deleteGenFile(file);
  }
  
  private static void deleteGenFile(File file)
  {
    if(file.delete())
    {
      System.out.println(file.getName() + " deleted.");
    }else
    {
      System.out.println(file.getName() + " could not be deleted. Please manually delete the file to rest system.");
    }
  }
  // the print command
  private static void print()
  {
    printHelper(root, "\\\\root");
  }
  
  // recursive method to print the whole tree data with ACL permissions for each node.
  private static void printHelper(DirectoryNode dNode, String dPath)
  {
    System.out.println();
    System.out.println("Path: " + dPath);
    dNode.printPerm();
    dNode.printSubDirs();
    dNode.printFiles();
    
    ArrayList<FileNode> fList = dNode.getFileList();
    Iterator<FileNode> i = fList.iterator();
    while(i.hasNext())
    {
      FileNode temp = i.next();
      System.out.println("File Path: " + dPath + "\\" + temp.fileName());
      System.out.print("File Permissions: ");
      temp.printPerm();
      System.out.println();
    }
    
    ArrayList<DirectoryNode> dlist = dNode.getSubDirList();
    Iterator<DirectoryNode> it = dlist.iterator();
    while(it.hasNext())
    {
      DirectoryNode temp = it.next();
      printHelper(temp, dPath + "\\" + temp.dirName());
    }
  }
  
  //appends given data to the given file if overwrite is false.
  private static void appendFile(File file, String data, Boolean overwrite)
  {
    try
    {
      if(!file.exists())
      {
        file.createNewFile();
      }
      FileWriter fileWritter = new FileWriter(file.getName(), !overwrite);
      BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
      bufferWritter.write(data);
      bufferWritter.newLine();
      bufferWritter.close();
    }
    catch (IOException e)
    {
      System.out.println(e);
    }
  }
  
  // the create user command for super user
  private static void adduser(String[] buffer)
  {
    if(!CURRENT_USER.equals("super"))
    {
      System.out.println("You do not have sufficient permission to add users to the system.");
      return;
    }
    
    if(buffer.length == 3)
    {
      String uName = buffer[1];
      String uType = buffer[2];
      
      if(isListedUser(uName) < 0)
      {
        if(isType(uType))
        {
          User uTemp = new User(uName, uType);
          userList.add(uTemp);
          DirectoryNode dirNode = new DirectoryNode(root, uName);
          root.addSubDir(dirNode);
          root.editExecPerm(uName);
          dirNode.addCompAccess(uName);
          
          File file = new File("users.txt");
          appendFile(file, uName + " " + uType, false);
          System.out.println("User " + uName + " added to the system.");
          
          //write password to file simulating an e-mail sent to the user
          String fName = uName + ".dat";
          file = new File(fName);
          String pass = genPass();
          appendFile(file, pass, true);
          encryptPass(pass);
        }
        else
        {
          System.out.println("Invalid user type.");
        }
      }
      else
      {
        System.out.println("User name already exists. Pick a different user name.");
      }
    }
    else
    {
      System.out.println("Invalid Syntax");
    }
  }
  
  private static String genPass()
  {
    Random rnd = new Random();
    String password = "";
    int count = 0;
    while(count < PASS_LENGTH)
    {
      int i = rnd.nextInt(94) + 32; //Ascii chars between 32 & 126 are supported as valid inputs for generated password
      char c = (char) i;
      password += c;
      count++;
    }
    return password;
  }
  //the getacl command. 
  private static void getacl(String[] buffer)
  {
    if(buffer.length == 2) //any other lengths would indicate invalid syntax
    {
      String obj = buffer[1];
      FileNode fNode = iterator.getFile(obj);
      DirectoryNode dNode = iterator.getSubDir(obj);
      
      if( fNode != null) //file exists
      {
        System.out.println("#object: " + fNode.fileName());
        String owner = fNode.mainOwner();
        System.out.println("#owner: " + owner);
        String ownerType = "";
        for(int i = 0; i < userList.size(); i++)
        {
          User uTemp = userList.get(i);
          if(uTemp.userName().equals(owner))
          {
            ownerType = uTemp.userType();
          }
        }
        System.out.println("#group: " + ownerType);
        System.out.println("user::");
        fNode.printOwnerList();
        fNode.printGrpPerm();
      }else if(dNode != null) //directory exists
      {
        System.out.println("#object: " + dNode.dirName());
        String owner = dNode.mainOwner();
        System.out.println("#owner: " + owner);
        String ownerType = "";
        for(int i = 0; i < userList.size(); i++)
        {
          User uTemp = userList.get(i);
          if(uTemp.userName().equals(owner))
          {
            ownerType = uTemp.userType();
          }
        }
        System.out.println("#group: " + ownerType);
        dNode.printOwnerList();
        dNode.printGrpPerm();
      }else
      {
        System.out.println("Object " + obj + " not found in directory " + iterator.dirName() + ".");
      }
    }else
    {
      System.out.println("Invalid syntax");
    }
  }
  
  //setacl command which sets permissions for a user or group
  private static void setacl(String[] buffer)
  {
    int size = buffer.length;
    
    if(size == 4) //any other size would indicate incorrect syntax
    {
      String s = buffer[1];
      String[] temp = s.split("\\.");
      String uName = temp[0];
      String uType = temp[1];
      if(isUser(uName) && isType(uType))
      {
        String obj = buffer[3]; //file or directory name
        String perm = buffer[2]; //permissions
        
        if(iterator.isAFile(obj))
        {
          FileNode fNode = iterator.getFile(obj);
          if(!fNode.isOwner(CURRENT_USER)) //Does current user own the file
          {
            System.out.println("You don't have permission to access file " + obj + ".");
            return;
          }
          if(uName.equals("*"))
          {
            for(int i = 1; i < userList.size(); i++)
            {
              User uTemp = userList.get(i);
              if(uType.equals("*"))
              {
                fNode.editAccess(uTemp.userName(), perm);
                if(i == 1)
                {
                  fNode.addGroup("faculty", perm);
                  fNode.addGroup("student", perm);
                }
              }else //a user type is given
              {
                if(uTemp.userType().equals(uType))
                {
                  fNode.editAccess(uTemp.userName(), perm);
                  
                  if(i == 1)
                  {
                    fNode.addGroup(uType, perm);
                  }
                }
              }
            }
          }else //a username is given
          {
            for(int i = 1; i < userList.size(); i++)
            {
              User uTemp = userList.get(i);
              {
                if(uTemp.userName().equals(uName))
                {
                  if(uTemp.userType().equals(uType) || uType.equals("*"))
                  {
                    fNode.editAccess(uTemp.userName(), perm);
                  }else
                  {
                    System.out.println("User " + uName + " does not belong in type " + uType + ". Hence, no permissions changed.");
                  }
                }
              }
            }
          }
        }else if(iterator.getSubDir(obj) != null) //directory exists
        {
          DirectoryNode dNode = iterator.getSubDir(obj);
          if(!dNode.isOwner(CURRENT_USER))
          {
            System.out.println("You don't have permission to access directory " + obj + ".");
            return;
          }
          if(uName.equals("*"))
          {
            for(int i = 1; i < userList.size(); i++)
            {
              User uTemp = userList.get(i);
              if(uType.equals("*"))
              {
                dNode.editAccess(uTemp.userName(), perm);
                if(i == 1)
                {
                  dNode.addGroup("faculty", perm);
                  dNode.addGroup("student", perm);
                }
              }else //user type is given
              {
                if(uTemp.userType().equals(uType))
                {
                  dNode.editAccess(uTemp.userName(), perm);
                }
                if(i == 1)
                {
                  dNode.addGroup(uType, perm);
                }
              }
            }
          }else //username is given
          {
            for(int i = 1; i < userList.size(); i++)
            {
              User uTemp = userList.get(i);
              {
                if(uTemp.userName().equals(uName))
                {
                  if(uTemp.userType().equals(uType) || uType.equals("*"))
                  {
                    dNode.editAccess(uTemp.userName(), perm);
                  }else //if user doesn't exist in given type no permissions are changed
                  {
                    System.out.println("User " + uName + " does not belong in type " + uType + ". Hence, no permissions changed.");
                  }
                }
              }
            }
          }
        }else //given object was neither a file nor a directory
        {
          System.out.println("Object " + obj + " not found in directory " + iterator.dirName() + ".");
        }
      }else //given user name or user type was invalid.
      {
        System.out.println("User name or type does not exist.");
      }
    }else
    {
      System.out.println("Invalid syntax.");
    }
  }
  
  // command that changes ownership of a file to a specified user.
  private static void chown(String[] buffer)
  {
    int size = buffer.length;
    if(size == 3)
    {
      String uName = buffer[1];
      
      if(isUser(uName))
      {
        String file = buffer[2];
        FileNode fNode = iterator.getFile(file);
        if(fNode != null)
        {
          if(!fNode.hasAllPerm(CURRENT_USER))
          {
            System.out.println("You not have sufficient privileges to change ownership of file " + file + ".");
            return;
          }
          fNode.addCompAccess(uName);
          DirectoryNode temp = iterator;
          while(!temp.equals(root))
          {
            temp.editExecPerm(uName);
            temp = temp.dirParent();
          }
        }else
        {
          System.out.println("File " + file + " does not exist.");
        }
      }else
      {
        System.out.println("User " + uName + " does not exist.");
      }
    }else
    {
      System.out.println("Invalid syntax.");
    }
  }
  
  // creates directory(s) in current working dirrctory if no directory or file with same name exists.
  private static void mkdir(String [] buffer)
  {
    if(!iterator.hasWritePerm(CURRENT_USER))
    {
      System.out.println("You do not have sufficient privileges to create a directory.");
      return;
    }
    int size = buffer.length;
    for(int i = 1; i < size; i++)
    {
      String dirName = buffer[i];
      DirectoryNode dirNode = new DirectoryNode(iterator, dirName);
      if(iterator.isSubDir(dirNode))
      {
        System.out.println(dirName + " already exists as a directory, hence not created.");
      }else if(iterator.isAFile(dirName))
      {
        System.out.println(dirName + " already exists as a file, hence not created.");
      }else
      {
        iterator.addSubDir(dirNode);
        dirNode.addCompAccess(CURRENT_USER);
        System.out.println("Directory " + dirName + " created in directory " + iterator.dirName() + ".");
      }
    }
  }
  
  //removes specified directory(s) if they exist.
  private static void rmdir(String[] buffer)
  {
    if(!iterator.hasWritePerm(CURRENT_USER))
    {
      System.out.println("You do not have sufficient privileges to delete a directory.");
      return;
    }
    int size = buffer.length;
    String dirName = buffer[1];
    if(dirName.equals("*"))
    {
      iterator.deleteAllSubDir();
      System.out.println("All directories deleted from directory " + iterator.dirName() + ".");
    }else
    {
      for(int i = 1; i < size; i++)
      {
        dirName = buffer[i];
        DirectoryNode dirNode = new DirectoryNode(iterator, dirName);
        if(iterator.deleteSubDir(dirNode))
        {
          System.out.println("Directory " + dirName + " removed from directory " + iterator.dirName() + ".");
        }else
        {
          System.out.println("Directory " + dirName + " does not exist in " + iterator.dirName() + ".");
        }
      }
    }
  }
  
  //changes working directory for the loggedin user.
  private static void changeDirectory(String[] buffer)
  {     
    int size = buffer.length;
    
    if(size > 2)
    {
      System.out.println("Invalid syntax.");
    }else
    {
      String dirName = buffer[1];
      if(dirName.equals(".."))
      {
        if(iterator.dirName().equals("Root"))
        {
          System.out.println("You are already in Root directory, the highest directory node.");
        }else
        {
          DirectoryNode dirNode = iterator.dirParent();
          if(!dirNode.hasExecPerm(CURRENT_USER))
          {
            System.out.println("You do not have sufficient privileges to access directory " + dirNode.dirName() + ".");
            return;
          }
          int rem = PATH.lastIndexOf("\\");
          PATH = PATH.substring(0, rem);
          iterator = iterator.dirParent();
        }
      }else
      {
        DirectoryNode dirNode = new DirectoryNode(iterator, dirName);
        if(iterator.isSubDir(dirNode))
        {
          dirNode = iterator.getSubDir(dirName);
          if(!dirNode.hasExecPerm(CURRENT_USER))
          {
            System.out.println("You do not have sufficient privileges to access directory " + dirName + ".");
            return;
          }
          PATH += "\\" + dirName;
          iterator = dirNode;
        }else
        {
          System.out.println("Directory " + dirName + " does not exist in " + iterator.dirName() + ".");
        }
      }
    }
  }
  
  // creates file(s) in the current working directory of the logged in user
  private static void create(String[] buffer)
  {
    if(!iterator.hasWritePerm(CURRENT_USER))
    {
      System.out.println("You do not have sufficient privileges to modify this directory.");
      return;
    }
    int size = buffer.length;
    
    for(int i = 1; i < size; i++)
    {
      String fName = buffer[i];
      FileNode fNode = new FileNode(fName);
      if(iterator.isAFile(fName))
      {
        System.out.println(fName + " already exists as a file, hence not created.");
      }else if(iterator.getSubDir(fName) != null)
      {
        System.out.println(fName + " already exists as a directory, hence not created.");
      }else
      {
        iterator.addFile(fNode);
        fNode.addCompAccess(CURRENT_USER);
        System.out.println("File " + fName + " created in directory " + iterator.dirName() + ".");
      }
    }
  }
  
  // deletes any specified files that exist in the current working directory of logged in user
  private static void delete(String[] buffer)
  {
    if(!iterator.hasWritePerm(CURRENT_USER))
    {
      System.out.println("You do not have sufficient privileges to modify this directory.");
      return;
    }
    int size = buffer.length;
    
    String fName = buffer[1];
    if(fName.equals("*"))
    {
      iterator.deleteAllFiles();
      System.out.println("All files deleted from directory " + iterator.dirName() + ".");
    }else
    {
      for(int i = 1; i < size; i++)
      {
        fName = buffer[i];
        FileNode fNode = new FileNode(fName);
        if(iterator.deleteFile(fNode))
        {
          System.out.println(fName + " deleted from directory " + iterator.dirName() + ".");
        }else
        {
          System.out.println("File " + fName + " does not exist in directory " + iterator.dirName() + ".");
        }
      }
    }
  }
  
  //reads or writes from or to specified files if it exists in current working directory.
  private static void readWrite(String[] buffer, String cmd)
  {
    int size = buffer.length;
    
    if(size > 2)
    {
      System.out.println("Invalid syntax.");
    }else
    {
      String fName = buffer[1];
      FileNode fNode = iterator.getFile(fName);
      if(fNode != null)
      {
        if(cmd.equals("read"))
        {
          if(!fNode.hasReadPerm(CURRENT_USER))
          {
            System.out.println("You do not have sufficient privileges to read this file.");
            return;
          }
        }else
        {
          if(!fNode.hasReadPerm(CURRENT_USER))
          {
            System.out.println("You do not have sufficient privileges to edit this file.");
            return;
          }
        }
        System.out.println("File " + fName + " is being " + cmd + ".");
      }else
      {
        System.out.println("File " + fName + " does not exist in directory " + iterator.dirName() + ".");
      }
    }
  }
  
  // checks if the user input for commans meets the expected format
  private static Boolean checkSyntax(String input)
  {
    String reg1 = "(([a-z])+( )([a-z0-9_])*)"; //for all file-directory commands
    String reg2 = "(([rmidelt])+( )([\\*]))"; //for remove/delete * command only
    String reg3 = "(([cd])+( )([\\.]{2}))"; //for cd .. only
    String reg4 = "(([a-z0-9_]+)(,){1})"; //for individual elements after the file-directory commands
    String reg5 = "(([setacl])+( )([a-z0-9_\\*])+(\\.)([a-z\\*]+)( )([+-])([rwx]+)( )([a-z0-9_]+))"; //setacl command
    //String reg6 = "([getacl]+( )([a-z0-9_]+))"; //for getacl command only
    String reg6 = "([chown]+( )([a-z0-9_]+)( )([a-z0-9_]+))";
    String reg7 = "print";
    String reg8 = "reset";
    String reg9 = "chpassword";
    
    ArrayList<String> regExList = new ArrayList<String>();
    regExList.add(reg1);
    regExList.add(reg2);
    regExList.add(reg3);
    //regExList.add(reg4);
    regExList.add(reg5);
    regExList.add(reg6);
    regExList.add(reg7);
    regExList.add(reg8);
    regExList.add(reg9);
    
    Iterator<String> it = regExList.iterator();
    while(it.hasNext())
    {
      String regEx = it.next();
      Pattern p = Pattern.compile(regEx, Pattern.DOTALL);
      Matcher m = p.matcher(input);
      if(m.matches())
      {
        return true;
      }
    }
    String [] buffer = input.split(" ");
    int size = buffer.length;
    if(size > 1)
    {
      Pattern p = Pattern.compile(reg4, Pattern.DOTALL);
      for(int i = 1; i < size; i++)
      {
        String s = buffer[i];
        Matcher m = p.matcher(s);
        if(!m.matches())
        {
          if(i == size-1)
          {
            s += ",";
            m = p.matcher(s);
            if(!m.matches())
            {
              return false;
            }
          }else
          {
            return false;
          }
        }
      }
      return true;
    }
    
    return false;
  }
  
  // checks if a given user is a valid user of the system and outputs the result to console.
  private static Boolean isValidUser(String user)
  {
    if(isListedUser(user) < 0)
    {
      System.out.println("Invalid username.");
      return false;
    }
    return true;
  }
  
  // checks if a given password is a valid one for the provided username.
  private static Boolean isValidPassword(String uName, String pass)
  {
    if(pass.equals(decryptPass(uName)))
    {
      System.out.println(uName + " logged in.");
      
      try
      {
        String fName = uName + ".dat";
        File file = new File(fName);
        Scanner scan = new Scanner(file);
        if(scan.hasNext())
        {
          String old = scan.next();
          if(old.equals(pass))
          {
            System.out.println("Welcome to the secured SU system. You are logging in for the first time.");
            CURRENT_USER = uName;
            chpassword();
          }
        }
      } catch (IOException e)
      {
        System.out.println(e);
      }
      return true;
    }
    System.out.println("Your password does not match the records.");
    return false;
  }
  
  // decrypts user password from the file.
  private static String decryptPass(String uName)
  {
    String ePass = getEPass(uName);
    String dPass = "";
    String[] buffer = ePass.split(" ");
    for(int i=0; i < buffer.length; i++)
    {
      BigInteger b = new BigInteger(buffer[i]);
      dPass += r.decrypt(b);
    }
    return dPass;
  }
  
  // helper function which gets encrypted password from file.
  private static String getEPass(String uName)
  {
    File file = new File("password.dat");
    String ePass = "";
    int key = isListedUser(uName);
    System.out.println(uName + " " + key);
    try
    {
      Scanner s = new Scanner(file);
      int counter = 0;
      while(s.hasNextLine())
      {
        ePass = s.nextLine();
        if(counter == key)
          return ePass;
        counter++;
      }
    }
    catch (IOException e)
    {
      System.out.println(e);
    }
    return ePass;
  }
  
  // encrypts user password and stores it in password.dat
  private static void encryptPass(String pass)
  {
    String ePass = encrypt(pass);
    File file = new File("password.dat");
    appendFile(file, ePass, false);
  }
  
  private static String encrypt(String pass)
  {
    String ePass = "";
    for(int i=0; i < pass.length(); i++)
    {
      char c = pass.charAt(i);
      ePass += r.encrypt(c) + " ";
    }
    return ePass;
  }
  
  // only checkjs if a given user is valid user
  private static Boolean isUser(String uName)
  {
    if(uName.equals("*"))
    {
      return true;
    }
    return isListedUser(uName) > -1;
  }
  
  // Helper function: checks if given user present in array userList and returns its index in userlist.
  private static int isListedUser(String uName)
  {
    Iterator<User> it = userList.iterator();
    while(it.hasNext())
    {
      User uTemp = it.next();
      if(uName.equals(uTemp.userName()))
      {
        return userList.indexOf(uTemp);
      }
    }
    return -1;
  }
  
  // checks if given type is a valid user type.
  private static Boolean isType(String uType)
  {
    return(uType.equals("faculty") || uType.equals("student") || uType.equals("*"));
  }
  
  //reads the users.txt, creates a "User" object for each user and stores them in an array list.
  private static void readUsers(Scanner s)
  {
    while (s.hasNext())
    {
      String uName = s.next().toLowerCase();
      String uType = s.next().toLowerCase();
      
      if((isListedUser(uName) < 0) && isType(uType))
      {
        User u = new User(uName, uType);
        userList.add(u);
        DirectoryNode dirNode = new DirectoryNode(root, uName);
        root.addSubDir(dirNode);
        root.editExecPerm(uName);
        dirNode.addCompAccess(uName);
      }
      else
      {
        System.out.println("User name or user type already exists.");
      }
    }
  }
  
  private static void createSuper()
  {
        User u = new User("super", "admin");
        userList.add(u);
        DirectoryNode dirNode = new DirectoryNode(root, "super");
        root.addSubDir(dirNode);
  }
}