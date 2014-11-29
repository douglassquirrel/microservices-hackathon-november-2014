using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using MicroServicesHackathon.Data;
using MicroServicesHackathon.Domain;
using MicroServicesHackathon.Facts;
using MicroServicesHackathon.Rest;

namespace MicroServicesHackathon
{

    public class Referee
    {
        private readonly IRestClient _restClient;
        private readonly string _acceptedMovementSubscribeId;
        private readonly IRepository _acceptedMovementRepository;

        public Referee(IRestClient restClient, IRepository acceptedMovementRepository)
        {
            _restClient = restClient;
            _acceptedMovementRepository = acceptedMovementRepository;
            _acceptedMovementSubscribeId = _restClient.Subscribe(ProposedMovement.Topic);
        }

        public bool IsGameOver { get; set; }

        public Task Start()
        {
            IsGameOver = false;

            return Task.Factory.StartNew(() =>
            {
                while (!IsGameOver) {
                    ProcessMovement();
                }
            });
        }

        public void ProcessMovement()
        {
            ProposedMovement fact = GetMovement();
            Movement movement = Convert(fact);

            IList<Movement> previousMovements =
                _acceptedMovementRepository.GetGame(fact.GameId)
                    .Select(Convert)
                    .ToList();
            Board board = new Board(previousMovements);

            if (board.IsValid(movement)) {
                AcceptedMovement acceptedMovement = Accept(fact);
                _restClient.PostFact(AcceptedMovement.Topic, acceptedMovement);
                _acceptedMovementRepository.Save(acceptedMovement);
            } else {
                InvalidMovement invalidMovement = Reject(fact);
                _restClient.PostFact(InvalidMovement.Topic, invalidMovement);
            }
        }

        private ProposedMovement GetMovement()
        {
            ProposedMovement fact = null;

            int attempted = 10;
            while (attempted > 0) {
                fact = _restClient.NextFact<ProposedMovement>(
                    ProposedMovement.Topic,
                    _acceptedMovementSubscribeId,
                    30);
                if (fact == null) {
                    attempted--;
                } else {
                    break;
                }
            }

            if (fact == null)
                throw new TimeoutException("Unable to get a proposed movement within the expected attempts");
            return fact;
        }

        private static Movement Convert(MovementFact fact)
        {
            if (fact == null)
                throw new ArgumentNullException("fact");
            if (fact.Position == null)
                throw new ArgumentException("The movement fact doesn't have a position");

            return new Movement(fact.Position.X, fact.Position.Y, fact.PlayerId);
        }

        private static AcceptedMovement Accept(ProposedMovement fact)
        {
            return new AcceptedMovement {
                GameId = fact.GameId,
                PlayerId = fact.PlayerId,
                Position = fact.Position
            };
        }

        private static InvalidMovement Reject(ProposedMovement fact)
        {
            return new InvalidMovement {
                GameId = fact.GameId,
                PlayerId = fact.PlayerId,
                Position = fact.Position
            };
        }
    }
}
