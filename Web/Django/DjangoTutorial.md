# Django

## Install
#### Setup virtualenv environment
- `mkdir ~/.virtualenvs`
- `python3 -m venv ~/.virtualenvs/djangodev`
- `sudo apt-get install python3-pip`
- `pip3 install virtualenv`
- `virtualenv --python=`which python3`
- `~/.virtualenvs/djangodev`
- `source ~/.virtualenvs/djangodev/bin/activate`

#### Setup django project
- `django-admin startproject mysite`

#### Connecting to postgres
- `pip install psycopg2`
- Go to project/settings.py and change the DATABASE part:

```
DATABASES = {
    'default': {
        'ENGINE':  'django.db.backends.postgresql_psycopg2',
        'NAME': 'mydb',
        'USER': 'postgres',
        'PASSWORD': 'password',
        'HOST': 'localhost',
        'PORT': '',
    }
}
```
- run `python manage.py migrate`
  - This should create the default tables in your db, that you should see using pgAdmin3
  - Now that your **environment** – a “project” – is set up, you’re set to start doing work.


## Part 1 - First View
**Application setup**
- `python manage.py startapp polls`

**urls**
- The **environment** `urls.py` is responsible for the routing. It uses regex to match the URL parts and then return the requested views.
  - In this case we want to **forward** requests coming to the "poll" project to the poll project's url router.
  - The include function does exactly that. It strips the matched part from `url.../polls/blah` and thus forwards `/blah` to the polls.urls.

  ```python
  urlpatterns = [
      url(r'^polls/', include('polls.urls')),
      url(r'^admin/', admin.site.urls),
  ]
  ```

- Then in the `polls/url.py` we define the following:

 ```python
 urlpatterns = [
     url(r'^$', views.index, name='index'),
 ]
 ```
    - If there is nothing behind the `/` we return the index.
