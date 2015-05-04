//Class User which has properties, name and type.

public class User {
  private String name = null;
  private String type = null;
  
  public User(String nam, String typ) //constructor
  {
    this.name = nam;
    this.type = typ;
  }
  
  public User(String nam) //constructor
  {
    this.name = nam;
  }
  
  public String userName()
  {
    return this.name;
  }
  
  public String userType()
  {
    return this.type;
  }
  
  public void changeType(String typ)
  {
    this.type = typ;
  }
}