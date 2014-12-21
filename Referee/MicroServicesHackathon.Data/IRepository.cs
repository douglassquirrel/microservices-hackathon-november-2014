using System.Collections.Generic;
using MicroServicesHackathon.Facts;

namespace MicroServicesHackathon.Data
{
    public interface IRepository
    {
        void Save(AcceptedMovement movement);
        IEnumerable<AcceptedMovement> GetGame(string gameId);
    }
}