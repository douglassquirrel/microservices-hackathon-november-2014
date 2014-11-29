using System;
using System.Collections.Generic;
using System.Linq;
using MicroServicesHackathon.Data;
using MicroServicesHackathon.Facts;

namespace MicroServicesHackathon
{
    public class InMemoryRepository : IRepository
    {
        private readonly IList<AcceptedMovement> _movements;

        public InMemoryRepository()
        {
            _movements = new List<AcceptedMovement>();
        }

        public void Save(AcceptedMovement movement)
        {
            _movements.Add(movement);
        }

        public IEnumerable<AcceptedMovement> GetGame(string gameId)
        {
            return _movements
                .Where(m => string.Equals(m.GameId, gameId, StringComparison.OrdinalIgnoreCase))
                .ToList();
        }
    }
}
