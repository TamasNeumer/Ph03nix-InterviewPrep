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
- Go to project/settings.py and change:
  - `TIME_ZONE = 'Europe/Berlin'`
  - the DATABASE part:

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
  - All Django wants is that HttpResponse. Or an exception.

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

## Part 2 - Models and Data management
#### Managing models
- Django classes that extend `model.Model` will be your tables.
- And the classes' props will be your columns in the table.

- You can use an optional first positional argument to a Field to designate a human-readable name. (As done in `pub_date`)


  ```python
  class Question(models.Model):
      question_text = models.CharField(max_length=200)
      pub_date = models.DateTimeField('date published')


  class Choice(models.Model):
      question = models.ForeignKey(Question, on_delete=models.CASCADE)
      choice_text = models.CharField(max_length=200)
      votes = models.IntegerField(default=0)
  ```

- Finally we need to tell our project (mysite) that ie needs to consider the polls project's configuration when doing migrations. To include the app in our project, we need to add a reference to its configuration class in the INSTALLED_APPS. --> mysite/settings.py `INSTALLED_APPS = ['polls.apps.PollsConfig', ...]` (See polls/apps.py --> class PollsConfig)
- In the end apply the changes via `python manage.py makemigrations
`
- You see the resulting file in the polls/migrations folder. To see them in pure SQL format you can run `python manage.py sqlmigrate polls 0001`
- Run `python manage.py check` as a final test to see if we don't mess up anything with the migration.
- Finally `python manage.py migrate`

#### Managing data in the DATABASE
- Once you’ve created your data models, Django automatically gives you a database-abstraction API that lets you create, retrieve, update and delete objects.

- In dir containing manage.py: `export DJANGO_SETTINGS_MODULE="mysite.setting`
- `python`
- `import django` --> (wait a bit, be patient)
- `django.setup()`

```python
from polls.models import Question, Choice
Question.objects.all() # = SELECT * From Question

# Creating a Question
from django.utils import timezone
q = Question(question_text="What's new?", pub_date=timezone.now())
q.save()
q.id # After save q has an assigned id.
Question.objects.all() # <QuerySet [<Question: Question object>]> --> unhelpful
```

- To make it more descriptive we add info to our model.py --> `def __str__(self): return self.question_text`
- Once done so, restart the shell via `python manage.py shell`

```python
from polls.models import Question, Choice
q = Question.objects.get(pk=1)
q.choice_set.all() #Display choice_set
q.choice_set.create(choice_text='Not much', votes=0)
q.choice_set.create(choice_text='The sky', votes=0)
q.choice_set.create(choice_text='Just hacking again', votes=0)
q.choice_set.all()

c = q.choice_set.filter(choice_text__startswith='Just hacking')
c.delete()
q.choice_set.all()
```

#### Django admin
- Run `python manage.py createsuperuser` to set up the credentials.
- Run the server `python manage.py runserver`
- `http://127.0.0.1:8000/admin/`
- Our question model is not seen in the admin interface. To add it go to `admin.py` and add the following:

```python
from django.contrib import admin
from .models import Question
admin.site.register(Question)
```

## Part 3 Writing view templates

#### Decoupling view from python code - Returning a template
- There’s a problem here, though: the page’s design is hard-coded in the view. If you want to change the way the page looks, you’ll have to edit this Python code. So let’s use Django’s template system to separate the design from Python by creating a template that the view can use.
  - Create a `templates` diretory in `polls`, and create a `polls` inside `templates`. Add an `index.html` to this folder. n other words, your template should be at `polls/templates/polls/index.html`. Later you will refer to this as `polls/index.html`
  - Insert the django HTML [template](https://docs.djangoproject.com/en/1.11/intro/tutorial03/)

  ```python
  {% if latest_question_list %}
      <ul>
      {% for question in latest_question_list %}
          <li><a href="/polls/{{ question.id }}/">{{ question.question_text }}</a></li>
      {% endfor %}
      </ul>
  {% else %}
      <p>No polls are available.</p>
  {% endif %}
  ```

  - And make the `view.py` to load our template. Note that we are passing `latest_question_list` in the context, thus it is available in our HTML template as a variable.

  ```python
  from django.template import loader
  from .models import Question

  def index(request):
      latest_question_list = Question.objects.order_by('-pub_date')[:5]
      template = loader.get_template('polls/index.html')
      context = {'latest_question_list': latest_question_list}
      return render(request, 'polls/index.html', context)
      # return HttpResponse(template.render(context, request))
  ```

  - The `render()` method provides a shortcut for rendering the context and returning it as an HttpResponse. and returning it as an HttpResponse.

#### Decoupling view from python code - Returning an error
- Assume that the user is looking for a question with an ID, that doesn't exist. We want to raise a 404 error by rendering it:

  ```python
  def detail(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    return render(request, 'polls/detail.html', {'question': question})
  ```

- Add `{{ question }}` to `polls/detail.html`.
- Now `http://127.0.0.1:8000/polls/1/` should be okay, but `/2/` should give you a 404 page.

- Now you might want to change the `detail.html` to the following:

  ```python
  <h1>{{ question.question_text }}</h1>
  <ul>
  {% for choice in question.choice_set.all %}
      <li>{{ choice.choice_text }}</li>
  {% endfor %}
  </ul>
```
  - question.choice_set.all is interpreted as the Python code question.choice_set.all(), which returns an iterable of Choice objects and is suitable for use in the {% for %} tag.
  - See the [full template guide](https://docs.djangoproject.com/en/1.11/topics/templates/)

- Note that `<li><a href="/polls/{{ question.id }}/">{{ question.question_text }}</a></li>` in `index.html` is still tightly coupled. Since in `polls/urls.py` we have defined the mappings this can be rewritten as: `<li><a href="{% url 'detail' question.id %}">{{ question.question_text }}</a></li>`
- However what happens when you have "detail" url in many of your applications? --> NAMESPACE is the answer. --> Add `app_name = 'polls'` above your `urlpatterns`. Now you can change the referencing in `index.html` to `<li><a href="{% url 'polls:detail' question.id %}">{{ question.question_text }}</a></li>`


## Part 4 Writing forms
#### The voting form
Let's modify detail.html

```python
<h1>{{ question.question_text }}</h1>

{% if error_message %}<p><strong>{{ error_message }}</strong></p>{% endif %}

<form action="{% url 'polls:vote' question.id %}" method="post">
{% csrf_token %}
{% for choice in question.choice_set.all %}
    <input type="radio" name="choice" id="choice{{ forloop.counter }}" value="{{ choice.id }}" />
    <label for="choice{{ forloop.counter }}">{{ choice.choice_text }}</label><br />
{% endfor %}
<input type="submit" value="Vote" />
</form>
```

- The above template displays a radio button for each question choice. The value of each radio button is the associated question choice’s ID. The name of each radio button is "choice". That means, when somebody selects one of the radio buttons and submits the form, it’ll send the POST data choice=value where value is `{{ choice.id }}` the ID of the selected choice. This is the basic concept of HTML forms.
- Since we’re creating a POST form (which can have the effect of modifying data), we need to worry about Cross Site Request Forgeries. Thankfully, you don’t have to worry too hard, because Django comes with a very easy-to-use system for protecting against it. In short, all POST forms that are targeted at internal URLs should use the {% csrf_token %} template tag.

Let's vire up the vote in the url configuration: `url(r'^(?P<question_id>[0-9]+)/vote/$', views.vote, name='vote'),` and change our vote function:

```python
def vote(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    try:
        selected_choice = question.choice_set.get(pk=request.POST['choice'])
    except (KeyError, Choice.DoesNotExist):
        # Redisplay the question voting form.
        return render(request, 'polls/detail.html', {
            'question': question,
            'error_message': "You didn't select a choice.",
        })
    else:
        selected_choice.votes += 1
        selected_choice.save()
        # Always return an HttpResponseRedirect after successfully dealing
        # with POST data. This prevents data from being posted twice if a
        # user hits the Back button.
        return HttpResponseRedirect(reverse('polls:results', args=(question.id,)))

def results(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    return render(request, 'polls/results.html', {'question': question})
```
- Request.POST is a dictionary-like object that lets you access submitted data by key name. Request.POST values are always strings.
- request.POST['choice'] will raise KeyError if choice wasn’t provided in POST data. --> check!

Add the `results.html` that we will use:

```python
<h1>{{ question.question_text }}</h1>

<ul>
{% for choice in question.choice_set.all %}
    <li>{{ choice.choice_text }} -- {{ choice.votes }} vote{{ choice.votes|pluralize }}</li>
{% endfor %}
</ul>

<a href="{% url 'polls:detail' question.id %}">Vote again?</a>
```

#### Django's generic views:
- These views represent a common case of basic Web development: getting data from the database according to a parameter passed in the URL, loading a template and returning the rendered template. Because this is so common, Django provides a shortcut, called the “generic views” system.
- First, open the polls/urls.py URLconf and change it like so:

```python
app_name = 'polls'
urlpatterns = [
    url(r'^$', views.IndexView.as_view(), name='index'),
    url(r'^(?P<pk>[0-9]+)/$', views.DetailView.as_view(), name='detail'),
    url(r'^(?P<pk>[0-9]+)/results/$', views.ResultsView.as_view(), name='results'),
    url(r'^(?P<question_id>[0-9]+)/vote/$', views.vote, name='vote'),
]
```

- Note that the name of the matched pattern in the regexes of the second and third patterns has changed from <question_id> to <pk>.
- Also change the views.py

```python
from django.shortcuts import get_object_or_404, render
from django.http import HttpResponseRedirect
from django.urls import reverse
from django.views import generic

from .models import Choice, Question

class IndexView(generic.ListView):
    template_name = 'polls/index.html'
    context_object_name = 'latest_question_list'

    def get_queryset(self):
        """Return the last five published questions."""
        return Question.objects.order_by('-pub_date')[:5]


class DetailView(generic.DetailView):
    model = Question
    template_name = 'polls/detail.html'


class ResultsView(generic.DetailView):
    model = Question
    template_name = 'polls/results.html'


def vote(request, question_id):
    ... # remains the same
```
- Each generic view needs to know what model it will be acting upon. This is provided using the model attribute.
- The DetailView generic view expects the primary key value captured from the URL to be called "pk", so we’ve changed question_id to pk for the generic views.
- The template_name attribute is used to tell Django to use a specific template name instead of the autogenerated default template name.
- In previous parts of the tutorial, the templates have been provided with a context that contains the question and latest_question_list context variables. For DetailView the question variable is provided automatically – since we’re using a Django model (Question), Django is able to determine an appropriate name for the context variable. However, for ListView, the automatically generated context variable is question_list. To override this we provide the context_object_name attribute, specifying that we want to use latest_question_list instead.
