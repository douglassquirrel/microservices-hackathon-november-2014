import requests
from sys import argv
import json
import random

base_url = argv[1]

headers = {'content-type': 'application/json'}
requests.post(base_url + "topics/game.start/facts", data=json.dumps({ "naughts_player_id": "foo", "crosses_player_id": "bar", "game_id": random.randint(1, 200000000) }
))