using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroServicesHackathon.Domain
{
    public class Movement
    {
        public Movement(int x, int y, string playerId)
        {
            if (x < 0)
                throw new ArgumentOutOfRangeException();
            if (y < 0)
                throw new ArgumentOutOfRangeException();
            if (playerId == null)
                throw new ArgumentNullException("playerId");

            X = x;
            Y = y;
            PlayerId = playerId;
        }

        public string PlayerId { get; private set; }
        public int X { get; private set; }
        public int Y { get; private set; }
    }
}
