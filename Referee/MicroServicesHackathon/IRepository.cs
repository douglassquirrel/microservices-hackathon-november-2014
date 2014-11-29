namespace MicroServicesHackathon
{
    public interface IRepository
    {
        void Set<T>(string key, T message);
        T Get<T>(string key)
            where T : class;
    }
}