# Spacy

- Define so called `spider` classes that extend the `scrapy.Sider`.
- Requests are **scheduled and processed asynchronously**. This means that Scrapy doesn’t need to wait for a request to be finished and processed, it can send another request or do other things in the meantime.
-

#### Spiders
- Sub-class the `spacy.Spider` class
- `name` - identifies the Spider. It must be unique within a project, that is, you can’t set the same name for different Spiders.
- `start_requests()` - must return an iterable of Requests which the Spider will begin to crawl from
- `parse()` - a method that will be called to handle the response downloaded for each of the requests made

```python
class QuotesSpider(scrapy.Spider):
    name = "quotes"

    def start_requests(self):
        urls = [
            'http://quotes.toscrape.com/page/1/',
            'http://quotes.toscrape.com/page/2/',
        ]
        for url in urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        page = response.url.split("/")[-2]
        filename = 'quotes-%s.html' % page
        with open(filename, 'wb') as f:
            f.write(response.body)
        self.log('Saved file %s' % filename)
```

- Scrapy schedules the scrapy.Request objects returned by the start_requests method of the Spider. Upon receiving a response for each one, it instantiates Response objects and calls the callback method associated with the request (in this case, the parse method) passing the response as argument.
- Optionally you can simply pull out the `urls` list from the `start_requests()` method, and the result will be the same. The above described code is rather for demonstration.

#### Extracting information
- `response.css('title')`
  - <Selector xpath='descendant-or-self::title' data='<title>Quotes to Scrape</title>'>]
- `response.css('title::text').extract()` (content of the text tag, hence the ::text)
  - ['Quotes to Scrape']
  - However, using .extract_first() avoids an IndexError and returns None when it doesn’t find any element matching the selection.
- Besides the extract() and extract_first() methods, you can also use the `re()` method to extract using regular expressions.

- Scrapy also supports XPath expressions. (In fact, CSS selectors are converted to XPath under-the-hood.)
- More on it [here](https://docs.scrapy.org/en/latest/topics/selectors.html#topics-selectors)

- A Scrapy spider typically generates many dictionaries containing the data extracted from the page. To do that, we use the yield Python keyword in the callback, as you can see below:

```python
import scrapy


class QuotesSpider(scrapy.Spider):
    name = "quotes"
    start_urls = [
        'http://quotes.toscrape.com/page/1/',
        'http://quotes.toscrape.com/page/2/',
    ]

    def parse(self, response):
        for quote in response.css('div.quote'):
            yield {
                'text': quote.css('span.text::text').extract_first(),
                'author': quote.css('small.author::text').extract_first(),
                'tags': quote.css('div.tags a.tag::text').extract(),
            }
```

#### Following links
```html
<ul class="pager">
    <li class="next">
        <a href="/page/2/">Next <span aria-hidden="true">&rarr;</span></a>
    </li>
</ul>
```
- `response.css('li.next a').extract_first()`
  - <a href="/page/2/">Next <span aria-hidden="true">→</span></a>
- `response.css('li.next a::attr(href)').extract_first()`
  - '/page/2/'

Then you can add the following code to the parse function:
```python
next_page = response.css('li.next a::attr(href)').extract_first()
        if next_page is not None:
            next_page = response.urljoin(next_page)
            yield scrapy.Request(next_page, callback=self.parse)

```
- What you see here is Scrapy’s mechanism of following links: when you yield a Request in a callback method, Scrapy will schedule that request to be sent and register a callback method to be executed when that request finishes. Or simply:
  - `yield response.follow(next_page, callback=self.parse)`
