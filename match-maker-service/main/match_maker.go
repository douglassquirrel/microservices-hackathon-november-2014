package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
)

type MatchMaker struct {
	Queue              []string
	PlayerInQueueError error
	gameCounter        int
}

type Match struct {
	NaughtsPlayerID string
	CrossesPlayerID string
	GameID          int
}

func NewMatchMaker() MatchMaker {
	return MatchMaker{
		Queue:              []string{},
		PlayerInQueueError: errors.New("Could not add player to queue as they are in the queue already"),
		gameCounter:        1,
	}
}

func main() {
	baseURL := "http://combo-squirrel.herokuapp.com/topics"
	subscribeURL := baseURL + "/player.ready/subscriptions"
	// subscribe to topic
	resp, err := http.Post(subscribeURL, "application/json", bytes.NewBuffer([]byte{}))

	if err != nil {
		println("couldn't get a subscription")
		os.Exit(2)
	}

	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)

	type Subscription struct {
		SubscriptionURL string `json:"retrieval_url"`
		SubscriptionID  string `json:"subscription_id"`
	}

	var subscriptionData Subscription
	err = json.Unmarshal(body, &subscriptionData)
	if err != nil {
		fmt.Println("error getting subscription:", err)
		os.Exit(1)
	}

	//get the next fact
	nextFactURL := baseURL + "/player.ready/subscriptions/" + subscriptionData.SubscriptionID + "/next"

	resp, _ = http.Get(nextFactURL)
	defer resp.Body.Close()
	body, _ = ioutil.ReadAll(resp.Body)

	// jsonMessage := []byte(`{"who":"Alex", "says":"hello everyone!"}`)
	// PostFactToTopic(baseURL, "chat", jsonMessage)
	//resp, err := http.Get(baseURL)

}

func (matchMaker *MatchMaker) playerInQueue(playerID string) bool {
	for _, queuedPlayerId := range matchMaker.Queue {
		if queuedPlayerId == playerID {
			return true
		}
	}
	return false
}

func (matchMaker *MatchMaker) AddPlayerToQueue(playerID string) error {
	if matchMaker.playerInQueue(playerID) {
		return errors.New("Could not add player to queue as they are in the queue already")
	} else {
		matchMaker.Queue = append(matchMaker.Queue, playerID)
		return nil
	}
}

func (matchMaker *MatchMaker) MakeMatch() *Match {
	if len(matchMaker.Queue) == 0 || len(matchMaker.Queue)%2 == 1 {
		return nil
	} else {
		naughts_player := matchMaker.Queue[0]
		crosses_player := matchMaker.Queue[1]
		match := &Match{
			NaughtsPlayerID: naughts_player,
			CrossesPlayerID: crosses_player,
			GameID:          matchMaker.gameCounter,
		}
		matchMaker.Queue = matchMaker.Queue[2:len(matchMaker.Queue)]
		matchMaker.gameCounter += 1
		return match
	}
}

func PostFactToTopic(baseURL string, topic string, jsonMessage []byte) bool {
	resp, err := http.Post(baseURL+"/"+topic+"/facts", "application/json", bytes.NewBuffer(jsonMessage))
	if err != nil {
		println("opps something went wrong publishing fact")
		return false
	}
	return resp.StatusCode == http.StatusOK
}

func subscribeToTopic() {

}
