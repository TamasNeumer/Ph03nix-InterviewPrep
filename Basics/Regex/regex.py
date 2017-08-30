import re

p = re.compile('[a-z]+')
m = p.match('ha ha')

print(m.group())
print(m.start())
print(m.end())
