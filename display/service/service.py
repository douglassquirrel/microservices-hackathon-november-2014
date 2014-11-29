import requests
from sys import argv
from os.path import join
import json

base_url = argv[1]
data_folder = argv[2]

def make_subscription(topic):
	r = requests.post("%stopics/%s/subscriptions"%(base_url, topic))
	return r.json()["subscription_id"]

def load_items(fname, default):
	full_path = join(data_folder, fname)
	try:
		with open(full_path) as f:
			return json.load(f)
	except Exception, e:
		print "no existing %s: %s"%(fname, e)
		return default

def dump_items(fname, obj):
	full_path = join(data_folder, fname)
	with open(full_path, "w") as f:
		json.dump(obj, f, sort_keys = True, indent = 4)

subscriptions = load_items("subscriptions.json", {})
player_ids = load_items("players.json", [])
players = {}
to_remove = []
for player_id in player_ids:
	player = load_items("player-%s.json"%player_id, None)
	if player != None:
		players[player_id] = player
	else:
		to_remove.append(player_id)
for remove in to_remove:
	player_ids.remove(remove)

def on_player_joined(obj):
	global player_ids
	global players
	if not "player" in obj:
		raise Exception, "Needed a player key"
	player = obj["player"]
	if not "uuid" in player:
		raise Exception, "Player did not contain uuid"
	if not "name" in player:
		raise Exception, "Player did not contain name"
	uuid = player["uuid"]
	if uuid not in player_ids:
		player_ids.append(uuid)
		dump_items("players.json", player_ids)
		players[uuid] = {"id": uuid, "connected": True, "score": 0, "playing": False}
	else:
		players[uuid]["connected"] = True
	dump_items("player-%s.json"%uuid, players[uuid])

fact_handlers = {"player.joined": on_player_joined}

changed_subs = False
for sub in fact_handlers.keys():
	if sub not in subscriptions:
		sub_id = make_subscription(sub)
		subscriptions[sub] = {"id": sub_id, "last_fact": 0}
		changed_subs = True

if changed_subs:
	dump_items("subscriptions.json", subscriptions)

for sub in subscriptions.keys():
	r = requests.get("%stopics/%s/facts?after_id=%d" %(base_url, sub, subscriptions[sub]["last_fact"]))
	facts = r.json()
	for fact in facts:
		print "new fact for %s: %s"%(sub, fact)
		fact_handlers[sub](fact)
	if facts!=[]:
		max_fact = max([x["combo_id"] for x in facts])
		subscriptions[sub]["last_fact"] = max_fact

dump_items("subscriptions.json", subscriptions)
