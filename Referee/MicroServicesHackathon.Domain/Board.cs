using System;
using System.Collections.Generic;

namespace MicroServicesHackathon.Domain
{
    using System.Collections.ObjectModel;
    using System.Linq;

    public class Board
    {
        public Board() : this(new Collection<Movement>())
        {
        }

        public Board(IList<Movement> movements)
        {
            if (movements == null)
                throw new ArgumentNullException("movements", "movements is null.");

            Size = 3;
            Movements = movements;
        }

        public string GameId { get; set; }

        public ICollection<Movement> Movements { get; private set; }

        public int Size { get; private set; }

        public bool IsValid(Movement movement)
        {
            if (movement.X > Size || movement.Y > Size)
                return false;

            return !Movements.Any(m => m.X == movement.X && m.Y == movement.Y);
        }
    }
}