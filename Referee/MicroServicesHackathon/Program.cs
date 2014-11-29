using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MicroServicesHackathon.Data;
using MicroServicesHackathon.Data.Redis;
using MicroServicesHackathon.Rest;

namespace MicroServicesHackathon
{
    class Program
    {
        private const bool USE_REDIS = false;

        static void Main(string[] args)
        {
            IRepository repository;
            if (USE_REDIS)
                repository = RedisRepositoryAdaptor.CreateInstance();
            else
                repository = new InMemoryRepository();

            IRestClient restClient = new RestClient();
            Referee referee = new Referee(restClient, repository);
            Task task = referee.Start();
            task.Wait(Timeout.InfiniteTimeSpan);
        }
    }
}