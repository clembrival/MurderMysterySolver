import os

from killerapp_server import settings

from django.http import HttpResponse
from django.core.servers.basehttp import FileWrapper


filename = os.path.join(settings.PROJECT_ROOT, "dataset.arff")


def get_size(request):
    size = os.path.getsize(filename)
    return HttpResponse(size)


def get_file(request):
    wrapper = FileWrapper(file(filename))
    return HttpResponse(wrapper, content_type='text/plain')


def update(request):

    if request.method != 'POST':
        return HttpResponse("ERROR")

    data = ""

    data += str(request.POST.get('title') + ",")
    data += str(request.POST.get('year') + ",")
    data += str(request.POST.get('detective') + ",")
    data += str(request.POST.get('location') + ",")
    data += str(request.POST.get('point_of_view') + ",")
    data += str(request.POST.get('murder_weapon') + ",")
    data += str(request.POST.get('victim_gender') + ",")
    data += str(request.POST.get('murderer_gender') + ",")
    data += str(request.POST.get('average_ratings') + "\n")

    with open(filename, "a") as dataset:
        dataset.write(data)

    return HttpResponse("OK")
