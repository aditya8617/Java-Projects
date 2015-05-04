About project:
•	File System simulator is a simulation for hierarchy of directory and file objects using linked list and array list which could be modified via standard UNIX commands.
•	It implements access control list (ACL) to manage permissions for multiple users in the simulation.
•	It implements RSA algorithm to save user passwords, validate user logins, and manage access to different directories and files in the simulation. 

Assumptions:

- No generated files will be tempered with.
- Only 2 groups "Faculty" and "Student" exist.

Compile all 6 .java files and run FileSystemSimulator.java

Decisions:

- No 2 users can have same user name.
- Username, folder name and file name must be aplha-numeric. "_" is the only special allowed character. Spaces is considered a invalid character.
- A given directory cannot have a directory with the same name as a file and vice-versa. The program will complain if the user tries to create one.
- All names (user, groups, file & folder) are in lowercase.
- setacl Lisa.Students +rx file1 will not set any permissions if Lisa doesn't belong to group students.
- setacl *.* +rwx dir1 will set rwx permissions on dir1 for all users from both groups.
- User can exit system by typing "exit" at any point.
- A user can log off by typing "logout" anytime. A new user can than login.
- 2 users cannot be simultaneously using the system.
- if a user is given permission to a file he or she gets access permission to all parent directories. The user cannot modify the directories or read other files in the directory.
- One user can be added at a time and only by super user.
- All system users are saved in users.txt along with their type.
- super.dat saves the public & private keys used for system encryption & decryption.
- password.dat saves the encrypted list of passwords.
- A username.dat file is created with the temp generated password for the user.
- For first login user must enter his/her own password 8 characters long.
- System reset can be done by deleting users.txt, password.dat & super.dat files. Reset command can do this automatically as long as files are not locked.

Correct syntax for all supported commands(Sample commands)

adduser aditya, student
adduser james, faculty

reset

mkdir d1
mkdir d1, d2,...., dn

rmdir d1
rmdir d1, d2,...., dn
rmdir *

create f1
create f1, f2,...., fn

delete f1
delete f1, f2,...., fn
delete *

cd d1
cd ..

read f1
write f1

setacl user.group permString objName

getacl objName

chown username filename

print