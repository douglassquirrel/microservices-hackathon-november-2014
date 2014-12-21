using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace MicroServicesHackathon.Domain.Tests
{
    [TestClass]
    public class BoardTests
    {
        [TestMethod]
        public void When_movement_is_already_in_board_returns_false()
        {
            Board board = new Board();
            board.Movements.Add(new Movement(2, 1, Guid.NewGuid().ToString()));
            board.Movements.Add(new Movement(2, 2, Guid.NewGuid().ToString()));

            bool isValid = board.IsValid(new Movement(2, 1, Guid.NewGuid().ToString()));
            Assert.IsFalse(isValid);
        }

        [TestMethod]
        public void When_movement_is_not_in_board_returns_true()
        {
            Board board = new Board();
            board.Movements.Add(new Movement(2, 1, Guid.NewGuid().ToString()));
            board.Movements.Add(new Movement(2, 2, Guid.NewGuid().ToString()));

            bool isValid = board.IsValid(new Movement(0, 0, Guid.NewGuid().ToString()));
            Assert.IsTrue(isValid);
        }

        [TestMethod]
        public void When_movement_is_not_within_board_limits_return_false()
        {
            Board board = new Board();
            board.Movements.Add(new Movement(2, 1, Guid.NewGuid().ToString()));
            board.Movements.Add(new Movement(2, 2, Guid.NewGuid().ToString()));

            bool isValid = board.IsValid(new Movement(4, 4, Guid.NewGuid().ToString()));
            Assert.IsFalse(isValid);
        }
    }
}
