using System;
using System.Collections.Generic;
using MicroServicesHackathon.Facts;
using MicroServicesHackathon.Rest;
using MicroServicesHackathon.Data;
using MicroServicesHackathon;
using Moq;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace MicroServicesHackathon.Tests
{

    [TestClass]
    public class RefereeTests
    {
        [TestMethod]
        public void When_proposed_movement_is_valid_pub_accepted_movement_and_store_it()
        {
            Mock<IRestClient> mockRestClient = new Mock<IRestClient>();
            Mock<IRepository> mockRepository = new Mock<IRepository>();

            string subscriptionId = Guid.NewGuid().ToString();
            string gameId = Guid.NewGuid().ToString();
            string firstPlayerId = Guid.NewGuid().ToString();
            string secondPlayerId = Guid.NewGuid().ToString();

            List<AcceptedMovement> previousMovements = new List<AcceptedMovement> {
                new AcceptedMovement { GameId = gameId, PlayerId = firstPlayerId, Position = new Position { X = 0, Y = 0 }},
                new AcceptedMovement { GameId = gameId, PlayerId = secondPlayerId, Position = new Position { X = 2, Y = 2 }},
            };

            mockRestClient.Setup(m => m.Subscribe(ProposedMovement.Topic)).Returns(subscriptionId);
            mockRestClient
                .Setup(m => m.NextFact<ProposedMovement>(ProposedMovement.Topic, subscriptionId, 30))
                .Returns(new ProposedMovement {
                    GameId = gameId,
                    PlayerId = firstPlayerId,
                    Position = new Position { X = 0, Y = 1 }
                });
            mockRestClient
                .Setup(m => m.PostFact(AcceptedMovement.Topic, It.IsAny<AcceptedMovement>()))
                .Callback((string topic, Fact fact) => {
                    Assert.IsInstanceOfType(fact, typeof(AcceptedMovement));

                    AcceptedMovement mov = (AcceptedMovement)fact;
                    Assert.AreEqual(0, mov.Position.X);
                    Assert.AreEqual(1, mov.Position.Y);
                    Assert.AreEqual(gameId, mov.GameId);
                    Assert.AreEqual(firstPlayerId, mov.PlayerId);
                });

            mockRepository.Setup(m => m.GetGame(gameId)).Returns(previousMovements);
            mockRepository.Setup(m => m.Save(It.IsAny<AcceptedMovement>())).Callback((AcceptedMovement mov) => {
                Assert.AreEqual(0, mov.Position.X);
                Assert.AreEqual(1, mov.Position.Y);
                Assert.AreEqual(gameId, mov.GameId);
                Assert.AreEqual(firstPlayerId, mov.PlayerId);
            });

            Referee referee = new Referee(mockRestClient.Object, mockRepository.Object);

            referee.ProcessMovement();
        }
    }
}
