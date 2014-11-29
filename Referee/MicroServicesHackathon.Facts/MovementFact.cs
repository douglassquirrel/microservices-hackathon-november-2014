
namespace MicroServicesHackathon.Facts
{
    public abstract class MovementFact : Fact
    {
        public string PlayerId { get; set; }
        public string GameId { get; set; }
        public Position Position { get; set; }
    }
}
