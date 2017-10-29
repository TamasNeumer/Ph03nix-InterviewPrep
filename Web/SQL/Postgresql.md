# PostgreSQL

## Installation
- [Ubuntu](https://help.ubuntu.com/community/PostgreSQL)
  - `sudo apt-get install postgresql postgresql-contrib`
  - `apt-cache search postgres`
  - `sudo apt-get install pgadmin3`
  - `sudo -u postgres psql postgres`
  - `\password postgres`
  - `sudo -u postgres createdb mydb`

  - `sudo service postgresql restart`
  - `service postgresql status` to check status

  - As postgres superuser
    - `\l` to list DBs
    - `ALTER USER postgres WITH PASSWORD 'myPW'`
    - `CREATE USER puppy WITH PASSWORD 'myPW'`
    - `\du` to list the users
    - `ALTER USER puppy WITH SUPERUSER;`

  - For further help: `man psql` (as normal user)

- [Installing PGAdmin4](https://askubuntu.com/questions/831262/how-to-install-pgadmin-4-in-desktop-mode-on-ubuntu-16-04)
  - `sudo apt-get install virtualenv python-pip libpq-dev python-dev`
  - `virtualenv pgadmin4`
  - `cd pgadmin4`
  - `source bin/activate`
  - `pip install https://ftp.postgresql.org/pub/pgadmin/pgadmin4/v1.6/pip/pgadmin4-1.6-py2.py3-none-any.whl`
  - `echo "SERVER_MODE = False" >> lib/python2.7/site-packages/pgadmin4/config_local.py`
  - `python lib/python2.7/site-packages/pgadmin4/pgAdmin4.py`
  - Access at http://localhost:5050

  - Or you can use pgAdmin III (sudo apt-get...)

  - Create a new connection
    - Name: localhost
    - Host: 127.0.0.1
    - Port: 5432
    - username postgres
    - pw: password that was changed.

- [Enabling user access](http://suite.opengeo.org/docs/latest/dataadmin/pgGettingStarted/firstconnect.html)
  - As a super user, open /etc/postgresql/9.6/main/pg_hba.conf (Ubuntu) and add the following lines:
    - `local   all             all                                      md5`
    - `host    all             all             ::1/128                 md5`
  - `sudo service postgresql restart`
  - To test your connection using psql, run the following command:
    - `psql -U postgres -W`
    - and enter your password when prompted. You should be able to access the psql console.

#### Integrating with JDBC
- Download the [JDBC Driver](https://jdbc.postgresql.org/download.html) for postgres.
- Copy this file to your class path.
- (add `export CLASSPATH=/usr/local/lib/javalibs/*` to your bash.rc)
- Compile .java file with `javac Main.java`
- Execute with `java -cp $CLASSPATH:. Main`
