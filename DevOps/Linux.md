# Linux

#### Help
- `man command` - get the manual and read up on the given command.
- `man -k text` - searches for the given text across the entire manual

#### Turning off and on
- After turning on the system the user has is prompted the `lilo boot:` where he has the ability to start the default OS, or start another.
- Turning off via `Ctrl` + `Alt` + `F1`, and then `Ctrl` + `Ctrl` + `Alt` + `Del`

#### Users and Keyboard combos
- Changing password: `passwd`
- `logout`
- `exit`
- `Shift` + `PageUp` / `PageDown` --> scrolling
- `Ctrl` + `c` --> Abort program
- `Ctrl` + `z` --> Pause program
- `Ctrl` + `w` --> Delete last word
- `Ctrl` + `u` --> Delete entire line
- `Ctrl` + `s` --> Freeze console
- `Ctrl` + `q` --> Unfreeze console

#### File management
- `ls` - listing files
- `ls -l` - list with details
  - `drwxrwxr-x  2 amrood amrood      4096 Dec 25 09:59 uml`
  - First Column − Represents the file type and the permission given on the file. Below is the description of all type of files.
    - `-` - Regular file, such as an ASCII text file, binary executable, or hard link.
    - `b` - Block special file. Block input/output device file such as a physical hard drive.
    - `c` - Character special file. Raw input/output device file such as a physical hard drive.
    - `d` - Directory file that contains a listing of other files and directories.
    - `l` - Symbolic link file. Links on any regular file.
    - `p` - Named pipe. A mechanism for interprocess communications.
    - `s` - Socket used for interprocess communication.
  - Second Column − Represents the number of memory blocks taken by the file or directory.
  - Third Column − Represents the owner of the file. This is the Unix user who created this file.
  - Fourth Column − Represents the group of the owner. Every Unix user will have an associated group.
  - Fifth Column − Represents the file size in bytes.
  - Sixth Column − Represents the date and the time when this file was created or modified for the last time.
  - Seventh Column − Represents the file or the directory name.
- `*` matches one or more characters, while `?` matches a single character.
  - `ls -l *.gz`
- Invisible files begin with `.`
- `ls -a` - list all files including hidden files
- `vi filename` - to create/edit a file and open it in VIM
- `cat fileName` - to print the file content
- `cat -b filename` - to display the line numbers
- `wc filename` - wordcount in filename
- `cp source_file destination_file` - copy
- `mv old_file new_file` - rename/move
- `rm filename` - remove/delete

#### Directories
- `cd ~` - home directory
- `cd -` - go in your last directory
- `pwd` - display the full path of the current working directory
- `mkdir dirname` - create directory
- `mkdir -p /tmp/amrood/test` - create the directory and all the parent dirs if they don't exist.
- `rmdir dirname` - remove directory
- `cd dirname` - changing directory
- `mv olddir newdir` - moving directory
- `.` is the current dir while `..` is the parent dir

#### File permissions
- Every file in Unix has the following attributes
  - **Owner permissions** − The owner's permissions determine what actions the owner of the file can perform on the file.
  - **Group permissions** − The group's permissions determine what actions a user, who is a member of the group that a file belongs to, can perform on the file.
  - **Other (global) permissions** − The permissions for others indicate what action all other users can perform on the file.
- `ls -l` to display permissions as well.
  - Every file in Unix has the following attributes −
  - The permissions are broken into groups of threes, and each position in the group denotes a specific permission, in this order: read (r), write (w), execute (x)
    - The first three characters (2-4) represent the permissions for the file's owner. For example, -rwxr-xr-- represents that the owner has read (r), write (w) and execute (x) permission.
    - The second group of three characters (5-7) consists of the permissions for the group to which the file belongs. For example, -rwxr-xr-- represents that the group has read (r) and execute (x) permission, but no write permission.
    - The last group of three characters (8-10) represents the permissions for everyone else. For example, -rwxr-xr-- represents that there is read (r) only permission.
  - For directories execution means executing commands in the dir (execute the ls or the cd command.)
- `chmod` to change premission
  - Symbolic mode
    - ``+`` Adds the designated permission(s) to a file or directory.
    - ``-`` Removes the designated permission(s) from a file or directory.
    - ``=`` Sets the designated permission(s).
    - Example:
      - `chmod o+wx testfile`, `chmod u-x testfile`, `chmod g = rx testfile` = `chmod o+wx,u-x,g = rx testfile`
  - Using chmod with Absolute Permissions
    - 0	No permission	---
    - 1	Execute permission	--x
    - 2	Write permission	-w-
    - 3	Execute and write permission: 1 (execute) + 2 (write) = 3	-wx
    - 4	Read permission	r--
    - 5	Read and execute permission: 4 (read) + 1 (execute) = 5	r-x
    - 6	Read and write permission: 4 (read) + 2 (write) = 6	rw-
    - 7	All permissions: 4 (read) + 2 (write) + 1 (execute) = 7	rwx
    - Script:
      - ``chmod 755 testfile``, `chmod 743 testfile` etc.
- ``chown`` − The chown command stands for "change owner" and is used to change the owner of a file.
  - ``chown user testfile`` --> The value of the user can be either the name of a user on the system or the user id (uid) of a user on the system.
- ``chgrp`` − The chgrp command stands for "change group" and is used to change the group of a file.
  - `chgrp group filelist`
- Additional permissions are given to programs via a mechanism known as the ``Set User ID (SUID)`` and ``Set Group ID (SGID)`` bits.
  - When you execute a program that has the SUID bit enabled, you inherit the permissions of that program's owner. Programs that do not have the SUID bit set are run with the permissions of the user who started the program.
  - This is the case with SGID as well. Normally, programs execute with your group permissions, but instead your group will be changed just for this program to the group owner of the program.
  - The SUID and SGID bits will appear as the letter "s" if the permission is available. The SUID "s" bit will be located in the permission bits where the owners’ execute permission normally resides.
    - `-r-sr-xr-x  1   root   bin  19031 Feb 7 13:47  /usr/bin/passwd*`
    - A capital letter S in the execute position instead of a lowercase s indicates that the execute bit is not set.
