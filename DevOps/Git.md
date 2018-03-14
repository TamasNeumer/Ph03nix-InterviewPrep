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

#### Syncing
- When you clone a repository with ``git clone``, it automatically creates a remote connection called ``origin`` pointing back to the cloned repository.
- `git fetch`
  - Fetch all of the branches from the repository. This also downloads all of the required commits and files from the other repository.
- `git checkout <branchname>`
  - Move to another branch.
- `git pull`
  - `git fetch` + `git merge` in a single command.
- `git push <remote> <branch>`
  - Push the specified branch to <remote>, along with all of the necessary commits and internal objects.
  - Git prevents you from overwriting the central repository’s history by refusing push requests when they result in a non-fast-forward merge. So, if the remote history has diverged from your history, you need to pull the remote branch and merge it into your local one, then try pushing again. The ``--force`` flag overrides this behavior and makes the remote repository’s branch match your local one, deleting any upstream changes that may have occurred since you last pulled. The only time you should ever need to force push is when you realize that the commits you just shared were not quite right and you fixed them with a ``git commit --amend`` or an interactive rebase. **However, you must be absolutely certain that none of your teammates have pulled those commits before using the ``--force`` option.**

#### Making a pull request
- Once their feature branch is ready, the developer files a pull request via their Bitbucket account. This lets everybody involved know that they need to review the code and merge it into the ``master`` branch.
- After receiving the pull request, the project maintainer has to decide what to do. If the feature is ready to go, they can simply merge it into master and close the pull request. But, if there are problems with the proposed changes, they can post feedback in the pull request. Follow-up commits will show up right next to the relevant comments.

#### Using branches
- `git branch` - list all the branches
- `git branch <branch>` - create a new branch
- `git branch -d <branch>` - delete a branch. This is a “safe” operation in that Git prevents you from deleting the branch if it has unmerged changes.
- ``git branch -D <branch>`` Force delete the specified branch, even if it has unmerged changes. This is the command to use if you want to permanently throw away all of the commits associated with a particular line of development.

#### Comparing Workflows
- **Centralized Workflow**
  -  the default development branch is called ``master`` and all changes are committed into this branch. This workflow doesn’t require any other branches besides ``master``.
  - Developers work on their local repository and finally publish changes to the official project, developers "push" their local ``master`` branch to the central repository.
  - If other people pushed, while you were working on your local copy, you are forced to pull, merge the changes, and commit the merged repo.
- **Feature branching**
  - The core idea behind the Feature Branch Workflow is that all feature development should take place in a dedicated branch instead of the ``master`` branch. This encapsulation makes it easy for multiple developers to work on a particular feature without disturbing the main codebase. It also means the ``master`` branch should never contain broken code, which is a huge advantage for continuous integration environments.
  - Feature branches should have descriptive names, like animated-menu-items or issue-#1061. The idea is to give a clear, highly-focused purpose to each branch.
  - Once done and pushed you create a pull-request, that is reviewed by the product managemnet, and if everything is fine it is merged into the ``master``.
- **Gitflow Workflow**
  - Gitflow is ideally suited for projects that have a scheduled release cycle.
  - In addition to feature branches, it uses individual branches for preparing, maintaining, and recording releases.
  - The **git-flow toolset is an actual command line tool that has an installation process**. --> A wrapper around the git, that you can download and install!
  - **Master and Develop**
    - Instead of a single ``master`` branch, this workflow uses two branches to record the history of the project. The ``master`` branch stores the official release history, and the ``develop`` branch serves as an integration branch for features. It's also convenient to **tag all commits in the master branch with a version number**.
    - `git flow init` will create the master and develop branch for you.
  - You follow the **Featre branching** here as well, but instead of the ``master``, you make the pull requests to the ``develop``.
  - **Release branches**
    - Once develop has acquired enough features for a release (or a predetermined release date is approaching), you fork a release branch off of develop.
    - Creating this branch starts the next release cycle, so no new features can be added after this point—only bug fixes, documentation generation, and other release-oriented tasks should go in this branch.
    - Once it's ready to ship, the ``release`` branch gets merged into ``master`` and tagged with a version number.
    - Using a dedicated branch to prepare releases makes it possible for one team to polish the current release while another team continues working on features for the next release.
  - **Hotfix branches**
    - Maintenance or ``“hotfix”`` branches are used to quickly patch production releases. ``Hotfix`` branches are a lot like ``release`` branches and ``feature`` branches except they're based on ``master`` instead of ``develop``. This is the only branch that should fork directly off of ``master``. As soon as the fix is complete, it should be merged into both ``master`` and ``develop`` (or the current release branch), and ``master`` should be tagged with an updated version number.

      ![Test](https://www.atlassian.com/dam/jcr:61ccc620-5249-4338-be66-94d563f2843c/05%20(2).svg)

#### Git tagging
- Git supports two types of tags: *lightweight* and *annotated*.
  - A lightweight tag is just a pointer to a specific commit.
  - Annotated tags, however, are stored as full objects in the Git database. They’re checksummed; contain the tagger name, email, and date; have a tagging message; and can be signed and verified with GNU Privacy Guard (GPG).
- **Annotated Tags**
  - `git tag -a v1.4 -m "my version 1.4"`
    - the `-a` flag tells git that this is an annotated tag
    - then (opeinally) a message is specified
- **Lightweight Tags**
  - `git tag v1.4`
- **Creating releases by tags**
  - `git push origin <tagname>`
    - This command will create a release tag in your repo.
- **Deleting a tag**
  - `git tag -d <tagname>` (locally)
  - `git push origin -d <tagname>` (remote)

#### Forking Workflow
- tbd.
