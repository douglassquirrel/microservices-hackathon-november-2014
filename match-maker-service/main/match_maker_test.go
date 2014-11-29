package main_test

import (
	. "github.com/douglassquirrel/microservices-hackathon-november-2014/match-maker-service/main"
	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"
)

var _ = Describe("Match Maker", func() {

	Describe(".AddToQueue", func() {

		var matchMaker MatchMaker

		BeforeEach(func() {
			matchMaker = NewMatchMaker()
		})

		It("when given a playerid add that playerid to the queue", func() {
			matchMaker.AddPlayerToQueue("player-a")
			Ω(len(matchMaker.Queue)).To(Equal(1))
			Ω(matchMaker.Queue).To(ContainElement("player-a"))
		})

		Context("when the player is already in the queue", func() {
			It("Does not add the same player twice", func() {
				matchMaker.AddPlayerToQueue("player-a")
				matchMaker.AddPlayerToQueue("player-a")
				Ω(len(matchMaker.Queue)).To(Equal(1))
			})
			It("raises an error", func() {
				err := matchMaker.AddPlayerToQueue("player-a")
				Ω(err).ToNot(HaveOccurred())
				err = matchMaker.AddPlayerToQueue("player-a")
				Ω(err).To(MatchError(matchMaker.PlayerInQueueError))
			})
		})
	})

	Describe(".MakeMatch", func() {

		var matchMaker MatchMaker

		BeforeEach(func() {
			matchMaker = NewMatchMaker()
		})

		Context("when there are no players in the queue", func() {
			It("should not give a match", func() {
				match := matchMaker.MakeMatch()
				Ω(match).To(BeNil())
			})
		})

		Context("when there is only one player in the queue", func() {
			It("should not give a match", func() {
				match := matchMaker.MakeMatch()
				Ω(match).To(BeNil())
			})
		})

		Context("when there are two players in the queue", func() {
			var match *Match

			BeforeEach(func() {
				matchMaker.AddPlayerToQueue("player-a")
				matchMaker.AddPlayerToQueue("player-b")
				match = matchMaker.MakeMatch()

			})
			It("should make a match", func() {
				Ω(match.NaughtsPlayerID).To(Equal("player-a"))
				Ω(match.CrossesPlayerID).To(Equal("player-b"))
				Ω(match.GameID).To(Equal(1))
			})

			It("should remove the players from the queue", func() {
				Ω(len(matchMaker.Queue)).To(Equal(0))
			})
		})

		Context("when there are three players in the queue", func() {
			BeforeEach(func() {
				matchMaker.AddPlayerToQueue("player-a")
				matchMaker.AddPlayerToQueue("player-b")
				matchMaker.AddPlayerToQueue("player-c")
			})
			It("should not give a match", func() {
				match := matchMaker.MakeMatch()
				Ω(match).To(BeNil())
			})
		})

		Context("when there are four players in the queue", func() {

			BeforeEach(func() {
				matchMaker.AddPlayerToQueue("player-a")
				matchMaker.AddPlayerToQueue("player-b")
				matchMaker.AddPlayerToQueue("player-c")
				matchMaker.AddPlayerToQueue("player-d")
			})

			It("makes a first match", func() {
				match := matchMaker.MakeMatch()
				Ω(match.NaughtsPlayerID).To(Equal("player-a"))
				Ω(match.CrossesPlayerID).To(Equal("player-b"))
				Ω(match.GameID).To(Equal(1))
			})

			It("makes a second match", func() {
				matchMaker.MakeMatch()
				match := matchMaker.MakeMatch()
				Ω(match.NaughtsPlayerID).To(Equal("player-c"))
				Ω(match.CrossesPlayerID).To(Equal("player-d"))
				Ω(match.GameID).To(Equal(2))
			})

		})
	})

})
