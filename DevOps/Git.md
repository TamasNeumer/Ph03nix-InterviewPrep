# Git

#### Setting up a repository
- `git init`
  - Creates a git **working** repository in the current folder.
  - By default, git init will initialize the Git configuration to the .git subdirectory path.
  - "I use a working directory created with ``git init`` or ``git clone`` when I want to add, edit and delete files in myproject locally on my dev machine."
- `git init --bare`
  - The --bare flag creates a repository that doesn’t have a working directory, making it impossible to edit files and commit changes in that repository.
  - A bare repository is for **SHARING**, **it's not intented for use on your local machine.**
  - A bare Git repository is typically used as a Remote Repository that is sharing a repository among several different people. You don't do work right inside the remote repository so there's no Working Tree (the files in your project that you edit), just bare repository data.
- `git clone`
  - Copy another git repository to your local machine.
- `git config`
  - `git config user.email`
  - Configuration levels:
    - `--local`
      - By default, git config will write to a local level if no configuration option is passed. Local level configuration is applied to the context repository git config gets invoked in.
    - `--global`
      - Global level configuration is user-specific, meaning it is applied to an operating system user.
    - `--system`
      - System-level configuration is applied across an entire machine. This covers all users on an operating system and all repos.

#### Saving changes
- `git add`
  - The git add command adds a change in the working directory to the staging area. It tells Git that you want to include updates to a particular file in the next commit.
  - Usage:
    - `git add <file>`
      - Stage all changes in ``<file>`` for the next commit.
    - `git add <directory>`
      - Stage all changes in ``<directory>`` for the next commit.
    - `git add -p`
      - Begin an interactive staging session that lets you choose portions of a file to add to the next commit.
- The staging area
  - It helps to think of it as a buffer between the working directory and the project history.
  - You can add different commits to a given staging area. And then push more commpits later in a single batch.
  - Iit makes it easier to split up a feature into atomic commits, keep related commits grouped together, and clean up local history before publishing it to the central repository.
- `git commit`
  - The ``git commit`` command commits the staged snapshot to the project history. Committed snapshots can be thought of as “safe” versions of a project—Git will never change them unless you explicity ask it to. Along with ``git add``, this is one of the most important Git commands.
- `git stash`
  - The ``git stash`` command takes your uncommitted changes (both staged and unstaged), saves them away for later use, and then reverts them from your working copy.
  - Use `git stash pop` to re-apply your previously stashed changes.
  - `git stash apply` doesn't pop it, but keeps it saved in the stash.
  - **Important**
    - By default, running git stash **will stash**:
      - changes that have been added to your index (staged changes)
      - changes made to files that are currently tracked by Git (unstaged changes)
    - But it will **not** stash:
      - new files in your working copy that have not yet been staged
      - files that have been ignored
- `.gitignore`
  - Ignored files are usually build artifacts and machine generated files that can be derived from your repository source or should otherwise not be committed.
  - ``.gitignore`` file must be edited and committed by hand when you have new files that you wish to ignore.
  - You can add a file that is normally ignored by "forcing" the adding: `git add -f debug.log`

#### Inspecting a repository
- `git status`
  - The git status command displays the state of the working directory and the staging area. It lets you see which changes have been staged, which haven’t, and which files aren’t being tracked by Git. Status output does not show you any information regarding the committed project history. For this, you need to use ``git log``.
- `git log`
  - The git log command displays committed snapshots. It lets you list the project history, filter it, and search for specific changes.
  - `git log -n <limit>`
    - Limit the number of commits by <limit>. For example, git log -n 3 will display only 3 commits.
  - `git log --oneline`
    - Condense each commit to a single line. This is useful for getting a high-level overview of the project history.
  - `git log --author="<pattern>"`
    - Search for commits by a particular author. The <pattern> argument can be a plain string or a regular expression.
  - `git log --grep="<pattern>"`
    - Search for commits with a commit message that matches ``<pattern>``, which can be a plain string or a regular expression.
  - `git log <since>..<until>`
    - Show only commits that occur between ``<since>`` and ``<until>``.
  - `git log <file>`
    - Only display commits that include the specified file. This is an easy way to see the history of a particular file.

#### Undoing changes
- **Viewing an old revision**
  -  Use `git low` to review your previous comments. You can view all commits across all branches by executing ``git log --branches=*.``
  - When you have found a commit reference to the point in history you want to visit, you can utilize the ``git checkout`` command to visit that commit. For example if you want to visit the commit of id "a1e8fb5", then execute `git checkout a1e8fb5`. During the normal course of development, the ``HEAD`` usually points to ``master`` or some other local branch, but when you check out a previous commit, ``HEAD`` no longer points to a branch—it points directly to a commit. This is called a “detached ``HEAD”`` state.
  - Nothing you do in here will be saved in your repository. To continue developing, you need to get back to the “current” state of your project: `git checkout master`
- **Undoing a committed snapshot**
  - As discussed before checking out a specific commit will put the repo in a "detached HEAD" state. In a detached state, any new commits you make will be orphaned when you change branches back to an established branch. Orphaned commits are up for deletion by Git's garbage collector.
  - **In order to preserve the changes** execute create a new branch. `git checkout -b new_branch_without_crazy_commit.`
- **How to undo a public commit with git revert**
  - If we execute ``git revert HEAD``, Git will create a new commit with the inverse of the last commit. As you can see the current state has been set to the state that was present before "try something crazy"
  ```
  git log --oneline
  e2f9a78 Revert "Try something crazy"
  872fa7e Try something crazy
  a1e8fb5 Make some important changes to hello.txt
  ```
  -  This solution is a satisfactory undo. This is the ideal 'undo' method for working with public shared repositories.
  - Use this method when going back in a **public** repository.
- **How to undo a comit with git reset**
  - If we invoke ``git reset --hard a1e8fb5`` the commit history is reset to that specified commit. Note: **All the commits that followed the `a1e8fb5` are gone for ever!**
  - This method of undoing changes has the cleanest effect on history.
  - If we have a shared remote repository that has the ``872fa7e`` commit pushed to it, and we try to ``git push`` a branch where we have reset the history, Git will catch this and throw an error. Git will assume that the branch being pushed is not up to date because of it's missing commits. In these scenarios, ``git revert`` should be the preferred undo method. Use the `git revert` method when
- **Undoing the last commit**
  - `git commit --amend` Once you have made more changes in the working directory and staged them for commit by using ``git add``. Then execute `git commit --amend`. This will have Git open the configured system editor and let you modify the last commit message.

#### Git rebase
- Rebasing is the process of moving or combining a sequence of commits to a new base commit. (i.e. you have a feature branch, which was created 3 commits back on the master. Now you want to add all commits of this feature branch to the master, but not as a merge, but as a series of commits.)
  - Before: master a, b, c; feature: d e
  - Now: master a, b, c, d, e
- The primary reason for rebasing is to maintain a linear project history.
- Rebasing is a common way to integrate upstream changes into your **local** repository.
- **You should never rebase commits once they've been pushed to a public repository.**
- `git rebase` This automatically rebases the current branch onto ``<base>``, which can be any kind of commit reference
- `git rebase --interactive`
  - This rebases the current branch onto <base> but uses an **interactive rebasing session**.
  - This opens an editor where you can enter commands (described below) for each commit to be rebased. These commands determine how individual commits will be transferred to the new base. You can also reorder the commit listing to change the order of the commits themselves. Once you've specified commands for each commit in the rebase, Git will begin playing back commits applying the rebase commands.
    - ``git rebase -- d`` means during playback the commit will be discarded from the final combined commit block.
    - ``git rebase -- p`` leaves the commit as is. It will not modify the commit's message or content and will still be an individual commit in the branches history.
    - ``git rebase -- x`` during playback executes a command line shell script on each marked commit. A useful example would be to run your codebase's test suite on specific commits, which may help identify regressions during a rebase.
  - **Summary:** Most developers like to use an interactive rebase to polish a feature branch before merging it into the main code base. This gives them the opportunity to squash insignificant commits, delete obsolete ones, and make sure everything else is in order before committing to the “official” project history. To everybody else, it will look like the entire feature was developed in a single series of well-planned commits The real power of interactive rebasing can be seen in the history of the resulting master branch. To everybody else, it looks like you're a brilliant developer who implemented the new feature with the perfect amount of commits the first time around.
