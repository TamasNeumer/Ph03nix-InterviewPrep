# Flask

## First install and run
- `pip3 install Flask`
```py
from flask import Flask
app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'Hello, World!'
```
- Save it as `hello.py`
- `export FLASK_APP=hello.py`
- `flask run`

If you have the debugger disabled or trust the users on your network, you can make the server publicly available simply by adding:
- `flask run --host=0.0.0.0`

Debug mode:
- `export FLASK_DEBUG=1`

## Routing
```py
@app.route('/')
def index():
    return 'Index Page'

@app.route('/hello')
def hello():
    return 'Hello, World'
```

## Variable rules
- To add variable parts to a URL you can mark these special sections as <variable_name>.
- Optionally a converter can be used by specifying a rule with <converter:variable_name>.
  - The following converters exist:
    - string, int, float, path, any (matches one of the items provided), uuid (accepts UUID strings)
```py
@app.route('/user/<username>')
def show_user_profile(username):
    # show the user profile for that user
    return 'User %s' % username

@app.route('/post/<int:post_id>')
def show_post(post_id):
    # show the post with the given id, the id is an integer
    return 'Post %d' % post_id
```

## Redirectiong to "/"
- **If you define your URL's end with '/'**: Accessing it without a trailing slash will cause Flask to redirect to the canonical URL **with** the trailing slash.
- **If you define your URL's end without '/'**: Accessing the URL with a trailing slash will produce a 404 “Not Found” error.

## URL Building
To build a URL to a specific function you can use the url_for() function. It accepts the name of the function as first argument and a number of keyword arguments, each corresponding to the variable part of the URL rule.

```js
@app.route('/login')
  def login(): pass

url_for('login', next='/') // /login?next=/
url_for('profile', username='John Doe') // /user/John%20Doe
```

## HTTP Methods:
HTTP (the protocol web applications are speaking) knows different methods for accessing URLs. By default, a route only answers to GET requests, but that can be changed by providing the methods argument to the route() decorator.
```py
from flask import request

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        do_the_login()
    else:
        show_the_login_form()
```

## Rendering templates
To render a template you can use the render_template() method. Provide the name of the template and the variables you want to pass to the template engine as keyword arguments.
```py
from flask import render_template

@app.route('/hello/')
@app.route('/hello/<name>')
def hello(name=None):
    return render_template('hello.html', name=name)
```
```
Case 1: a module:
/application.py
/templates
    /hello.html

Case 2: a package:
/application
    /__init__.py
    /templates
        /hello.html
```

## The Request Object
The current request method is available by using the method attribute. To access form data (data transmitted in a POST or PUT request) you can use the form attribute.
```py
@app.route('/login', methods=['POST', 'GET'])
def login():
    error = None
    if request.method == 'POST':
        if valid_login(request.form['username'],
                       request.form['password']):
            return log_the_user_in(request.form['username'])
        else:
            error = 'Invalid username/password'
    # the code below is executed if the request method
    # was GET or the credentials were invalid
    return render_template('login.html', error=error)
```
- What happens if the key does not exist in the form attribute? In that case a special KeyError is raised. You can catch it like a standard KeyError but if you don’t do that, a HTTP 400 Bad Request error page is shown instead.
- To access parameters submitted in the URL (?key=value) you can use the args attribute:
  - `searchword = request.args.get('key', '')`

## File Uploads
- Set the enctype="multipart/form-data" attribute on your HTML form, otherwise the browser will not transmit your files at all.
- Uploaded files are stored in memory or at a temporary location on the filesystem. You can access those files by looking at the files attribute on the request object. Each uploaded file is stored in that dictionary. It behaves just like a standard Python file object, but it also has a save() method that allows you to store that file on the filesystem of the server.

```py
from flask import request
from werkzeug.utils import secure_filename

@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        f = request.files['the_file']
        f.save('/var/www/uploads/' + secure_filename(f.filename))
```
## Configs
Configuration becomes more useful if you can store it in a separate file, ideally located outside the actual application package.
```py
app = Flask(__name__)
app.config.from_object('yourapplication.default_settings')
app.config.from_envvar('YOURAPPLICATION_SETTINGS', silent=True)
```
-  This environment variable can be set on Linux or OS X with the export command in the shell before starting the server:
  - `export YOURAPPLICATION_SETTINGS=/path/to/settings.cfg`
- On windows:
  - `set YOURAPPLICATION_SETTINGS=\path\to\settings.cfg`
- The silent switch just tells Flask to not complain if no such environment key is set.
Here is an example of a configuration file:
```py
# Example configuration
DEBUG = False
SECRET_KEY = '?\xbf,\xb4\x8d\xa3"<\x9c\xb0@\x0f5\xab,w\xee\x8d$0\x13\x8b83'
```


## Cookies
- To access cookies you can use the cookies attribute.
- To set cookies you can use the set_cookie method of response objects.
- The cookies attribute of request objects is a dictionary with all the cookies the client transmits.
- If you want to use sessions, do not use the cookies directly but instead use the Sessions in Flask that add some security on top of cookies for you.

```py
from flask import request

@app.route('/')
def index():
    username = request.cookies.get('username')
    # use cookies.get(key) instead of cookies[key] to not get a
    # KeyError if the cookie is missing.

    from flask import make_response

@app.route('/')
def index():
    resp = make_response(render_template(...))
    resp.set_cookie('username', 'the username')
    return resp
```

# Tutorial
## 0. Folder setup
```
/flaskr
    /flaskr
        /static
        /templates
```
  - Files inside of the static folder are available to users of the application via HTTP, css and js files go here.
  - Inside the templates folder, Flask will look for Jinja2 templates.

## 1. Database schema
schema.sql in the flaskr/flaskr:
```sql
drop table if exists entries;
create table entries (
  id integer primary key autoincrement,
  title text not null,
  'text' text not null
);
```

## Step 2: Application Setup Code
```py
# all the imports
import os
import sqlite3
from flask import Flask, request, session, g, redirect, url_for, abort, \
     render_template, flash

app = Flask(__name__) # create the application instance :)
app.config.from_object(__name__) # load config from this file , flaskr.py

# Load default config and override config from an environment variable
app.config.update(dict(
    DATABASE=os.path.join(app.root_path, 'flaskr.db'),
    SECRET_KEY='development key',
    USERNAME='admin',
    PASSWORD='default'
))
app.config.from_envvar('FLASKR_SETTINGS', silent=True)
```

## Step 3: flaskr as a package
- Create `setup.py`, `__init__.py` and `MANIFEST.in` in the projects root directory.

```
/flaskr
    /flaskr
        __init__.py
        /static
        /templates
        flaskr.py
        schema.sql
    setup.py
    MANIFEST.in
```
The `setup.py`
```py
from setuptools import setup

setup(
    name='flaskr',
    packages=['flaskr'],
    include_package_data=True,
    install_requires=[
        'flask',
    ],
)
```
When using setuptools, it is also necessary to specify any special files that should be included in your package (in the MANIFEST.in). In this case, the static and templates directories need to be included, as well as the schema.

`MANIFEST.in`
```in
graft flaskr/templates
graft flaskr/static
include flaskr/schema.sql
```
To simplify locating the application, add the following import statement into this file, flaskr/__init__.py:
```py
from .flaskr import app
```
This import statement brings the application instance into the top-level of the application package. When it is time to run the application, the Flask development server needs the location of the app instance. This import statement simplifies the location process.

Now it's time to install the app from the root folder. Execute the following comamnd: `pip3  install --editable .` Now start the application with the following commands.
- On windows use **set**
-  Never leave debug mode activated in a production system, because it will allow users to execute code on the server!
```
export FLASK_APP=flaskr
export FLASK_DEBUG=true
flask run
```
 ## Step 4: Database Connections
 Creating and closing database connections all the time is very inefficient, so you will need to keep it around for longer. Because database connections encapsulate a transaction, you will need to make sure that only one request at a time uses the connection. An elegant way to do this is by utilizing the application context.

 Flask provides two contexts: the **application context** and the **request context**.
 -  ``request`` variable is the request object associated with the current request
 - ``g`` is a general purpose variable associated with the current application context


The first time the function is called, it will create a database connection for the current context, and successive calls will return the already established connection:
```py
def get_db():
    """Opens a new database connection if there is none yet for the
    current application context.
    """
    if not hasattr(g, 'sqlite_db'):
        g.sqlite_db = connect_db()
    return g.sqlite_db
```
For disconnecting Flask provides us with the ``teardown_appcontext()`` decorator. It’s executed every time the application context tears down.
```py
@app.teardown_appcontext
def close_db(error):
    """Closes the database again at the end of the request."""
    if hasattr(g, 'sqlite_db'):
        g.sqlite_db.close()
```
