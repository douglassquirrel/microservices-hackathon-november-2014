import requests
from sys import argv
import json

base_url = argv[1]

headers = {'content-type': 'application/json'}
requests.post(base_url + "topics/player.joined/facts", data=json.dumps({ "player": { "uuid": "some_uuid", "name": "FooBar"} }))