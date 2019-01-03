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
  - `drwxrwxr-x 2 amrood amrood 4096 Dec 25 09:59 uml`
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
    - `+` Adds the designated permission(s) to a file or directory.
    - `-` Removes the designated permission(s) from a file or directory.
    - `=` Sets the designated permission(s).
    - Example:
      - `chmod o+wx testfile`, `chmod u-x testfile`, `chmod g = rx testfile` = `chmod o+wx,u-x,g = rx testfile`
  - Using chmod with Absolute Permissions
    - 0 No permission ---
    - 1 Execute permission --x
    - 2 Write permission -w-
    - 3 Execute and write permission: 1 (execute) + 2 (write) = 3 -wx
    - 4 Read permission r--
    - 5 Read and execute permission: 4 (read) + 1 (execute) = 5 r-x
    - 6 Read and write permission: 4 (read) + 2 (write) = 6 rw-
    - 7 All permissions: 4 (read) + 2 (write) + 1 (execute) = 7 rwx
    - Script:
      - `chmod 755 testfile`, `chmod 743 testfile` etc.
- `chown` − The chown command stands for "change owner" and is used to change the owner of a file.
  - `chown user testfile` --> The value of the user can be either the name of a user on the system or the user id (uid) of a user on the system.
- `chgrp` − The chgrp command stands for "change group" and is used to change the group of a file.
  - `chgrp group filelist`
- Additional permissions are given to programs via a mechanism known as the `Set User ID (SUID)` and `Set Group ID (SGID)` bits.
  - When you execute a program that has the SUID bit enabled, you inherit the permissions of that program's owner. Programs that do not have the SUID bit set are run with the permissions of the user who started the program.
  - This is the case with SGID as well. Normally, programs execute with your group permissions, but instead your group will be changed just for this program to the group owner of the program.
  - The SUID and SGID bits will appear as the letter "s" if the permission is available. The SUID "s" bit will be located in the permission bits where the owners’ execute permission normally resides.
    - `-r-sr-xr-x 1 root bin 19031 Feb 7 13:47 /usr/bin/passwd*`
    - A capital letter S in the execute position instead of a lowercase s indicates that the execute bit is not set.

#### Environment

- When you log in to the system, the shell undergoes a phase called initialization to set up the environment. This is usually a two-step process that involves the shell reading the following files:
  - `/etc/profile` - maintained by admin, valid for all users
  - `profile` - under the user's control i.e. console customization
- The `PATH` variable specifies the locations in which the shell should look for commands.
  - Here, each of the individual entries separated by the colon character (`:`) are directories.

#### Pipes and Filters

- `grep`
  - `-v` Prints all lines that do not match pattern.
  - `-n` Prints the matched line and its line number.
  - `-l` Prints only the names of files with matching lines (letter "l")
    - `ls -l | grep "Aug"`
  - `-c` Prints only the count of matching lines.
  - `-i` Matches either upper or lowercase.
- `sort`
  - The sort command arranges lines of text alphabetically or numerically.
    - `sort filename`
  - `-n` sort numerically
  - `-r` reverse order
  - etc.

#### Processes

- **Background**
  - A background process runs without being connected to your keyboard. If the background process requires any keyboard input, it waits. The simplest way to start a background process is to add an ampersand (&) at the end of the command.
    - `$ls ch*.doc &`
- **Foreground**
  - By default, every process that you start runs in the foreground. It gets its input from the keyboard and sends its output to the screen.
- `ps` / `ps -f` -> List processes ("process status")
  - UID (User ID), PID (Process ID), PPID (parent process id), C (CPU utilization), STIME (start time), TTY (Terminal type associated with the process), TIME (CPU time taken by the process), CMD (The command that started this process)
- `top`
  - It is an interactive diagnostic tool that updates frequently and shows information about physical and virtual memory, CPU usage, load averages, and your busy processes. (Kinna like Task Manager in Windows or Activity Montior in MAC)
- **Stopping process**
  - `Ctrl + C` (if foreground)
  - `kill -9 PID`

#### Advanced User Management

- **Managing User Groups**
  - `/etc/passwd` − Keeps the user account and password information. This file holds the majority of information about accounts on the Unix system.
  - `/etc/shadow` − Holds the encrypted password of the corresponding account. Not all the systems support this file.
  - `/etc/group` − This file contains the group information for each account.
  - `/etc/gshadow` - This file contains secure group account information.
- **Commands**

  - `useradd`, `usermod`, `userdel`, `groupadd`, `groupmod`, `groupdel`

- https://www.tutorialspoint.com/unix/unix-user-administration.htm

#### Linux in Containers

- **Memory inside Linux Containers - Or why don’t free and top work in a Linux container?**
  - **Cgroups**
    - **cgroups** (abbreviated from control groups) is a Linux kernel feature that limits, accounts for, and isolates the resource usage (CPU, memory, disk I/O, network, etc.) of a collection of processes. You can find more info in the ![Docs](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/6/html/resource_management_guide/sec-memory).
    - most container specific metrics are available at the cgroup filesystem via `/path/to/cgroup/memory.stat`, `/path/to/cgroup/memory.usage_in_bytes`, `/path/to/cgroup/memory.limit_in_bytes` and others. `/sys/fs/cgroup/` **is the recommended location** for cgroup hierarchies, but it is not a standard.
  - **The problem**
    - Most of the Linux tools providing system resource metrics were created before cgroups even existed (e.g.: `free` and `top`, both from procps). They usually read memory metrics from the proc filesystem: `/proc/meminfo, /proc/vmstat`, `/proc/PID/smaps` and others. Unfortunately these are **not** containerized. Meaning that they are not cgroup-aware. Hence **they will always display memory numbers from the host system** (physical or virtual machine) as a whole, which is useless for modern Linux containers (Heroku, Docker, etc.).
  - **Solution**
    - Some kernel developers believe that the best option is an userspace library that processes can use to query their memory usage and available memory.
    - `libmymem` would do all the hard work of figuring out where to pull numbers from (`/proc` vs. `cgroup` vs. `getrlimit(2)` vs. `systemd`, etc.). I am considering starting one. New code could easily benefit from it, but it is unlikely that all existing tools (`free`, `top`, etc.) will just switch to it. For now, we might need to encourage people to stop using those tools inside containers.
- **Linux memory and JVM**

  - We need to understand that the docker switches (`-m`, `–memory` and `–memory-swap`) and the kubernetes switch (`–limits`) instruct the Linux kernel to **kill the process** if it tries to exceed the specified limit, but the JVM is completely unaware of the limits and when it exceeds the limits, bad things happen!
  - Assume that we create a virtual Linux OS with 1GB of RAM and run a Linux container with 150MB restricted drive. If we start a Java application, that exposes an endpoint where we can query the JVM we see the following interesting points:
    - Why is the JVM maximum allowed memory 241.7 MiB?
      - Since the JVM doesn’t know that it’s executing inside a container, it will allow the maximum heap size to be close to 260MB.
    - If this container restricts the memory to 150MB, why does it allow Java to allocate almost 220MB?
      - When we use the parameter “`-m 150M`” in the docker command line, the docker daemon will limit 150M in the RAM and 150M in the Swap. As a result, the process can allocate the 300M and it explains why our process didn’t receive any kill from the Kernel.
  - Bad solution: Have more RAM in the VM! Assume that you have 8GB ram in the virtual machine and limit the container to 600MB. In this case the 8GB environment the JVM will try to have a heap of 2GB! The application will try to allocate more than 1.2GB of memory, which is more than the limit of this container (**600MB in RAM + 600MB in Swap**) and the process will be killed.
  - **Solution**
    - **Java8u131+**
      - From JDK 8u131+ and JDK 9, there’s an experimental VM option that allows the JVM ergonomics to read the memory values from CGgroups. To enable it on, you must explicit set the parameters -XX:+UnlockExperimentalVMOptions and -XX:+UseCGroupMemoryLimitForHeap on the JVM. I.e. add the following flags. The last flag will utilize 100% of memory for the JVM, instead of the default 1/4.
      - `-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1`
    - **Update - Java 10**
      - The Dockerfile for JDK10 doesn’t need any extra flags, and/or even any manual and special ergonomics configuration. Hence: `docker run -it --name mycontainer -p 8080:8080 -m 600M rafabene/java-container:openjdk10` runs perfect.
