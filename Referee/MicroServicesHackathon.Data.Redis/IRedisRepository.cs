namespace MicroServicesHackathon.Data.Redis
{
    public interface IRedisRepository
    {
        void Set<T>(string key, T message);

        T Get<T>(string key) where T : class;
    }
}