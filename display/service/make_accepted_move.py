import requests
from sys import argv
import json
import random

base_url = argv[1]

headers = {'content-type': 'application/json'}
requests.post(base_url + "topics/game.moveaccepted/facts", data=json.dumps({ "player": "foo", "game": random.randint(1, 200000000), "position": { "x": 1, "y": 1 } }))