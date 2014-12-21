# Matcher Service
A simple service to match pairs of players looking for a game.

Created during the Microservices London Hackathon November 2014.

Written by [@avade](https://twitter.com/AlexEvade) & [@sleepyfox](https://twitter.com/sleepyfox)
## Overview

The aim of this service is to match pairs of players looking for a game and to generate a start game event back to the combo server using the API spec listed [here](http://docs.combo.apiary.io/).

### Responsibilities

This service is responsible for starting games. This includes generating a sequenced `game_id` that is used by other services to identify a game.

#### Listens for Facts
This service listens on `player.ready` topic expecting the message in JSON format:

	{ player: { uuid: "32 char string", name: "xxx"} }

#### Produces Facts
Events will be published to the `game.start` topic in the JSON format:

	{ naughts_player_id: string,
	crosses_player_id: "string, game_id: int }

##TODO

Currently under implementation.

* To react to events published on `player.ready` topic.
* To publish events to the `game.start` topic when a pair are matched.
* Test drive the integration with the topic server (combo server).
* Look at genericising response for so this can be used with other two player games.

## Installation

### Requirements
* [Go](https://golang.org/doc/install) 1.3 (only tested with this version)
* [Godeps](https://github.com/tools/godep)

### Building

From the root directory of the project:

	godeps restore
	go build main/match_maker.go
	
### Running

	./match_maker



