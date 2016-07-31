from django.conf.urls import patterns, url
from killerapp import views


urlpatterns = patterns('',
                       url(r'^size/$', views.get_size, name='get_size'),
                       url(r'^file/$', views.get_file, name='get_file'),
                       url(r'^update/$', views.update, name='get_file'),
                       )
