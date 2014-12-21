using System;
using System.Collections.Generic;
using System.Configuration;
using MicroServicesHackathon.Facts;
using StackExchange.Redis;

namespace MicroServicesHackathon.Data.Redis
{
    public class RedisRepositoryAdaptor : IRepository
    {
        private readonly IRedisRepository _repository;

        public RedisRepositoryAdaptor(IRedisRepository repository)
        {
            _repository = repository;
        }

        public void Save(AcceptedMovement movement)
        {
            var moves = _repository.Get<List<AcceptedMovement>>(movement.GameId);
            if (moves == null)
                moves = new List<AcceptedMovement> { movement };
            else
                moves.Add(movement);

            _repository.Set(movement.GameId, moves);
        }

        public IEnumerable<AcceptedMovement> GetGame(string gameId)
        {
            return _repository.Get<List<AcceptedMovement>>(gameId);
        }

        public static RedisRepositoryAdaptor CreateInstance()
        {
            var redisHost = ConfigurationManager.AppSettings["redishost"]; // "127.0.0.1";
            var redisPort = Convert.ToInt32(ConfigurationManager.AppSettings["redisport"]);

            var connectionMultiplexer = ConnectionMultiplexer.Connect(
                new ConfigurationOptions {
                    EndPoints = {
                        string.Format("{0}:{1}", redisHost, redisPort)
                    },
                    ConnectTimeout = 10000,
                    AbortOnConnectFail = false
                });


            return new RedisRepositoryAdaptor(new RedisRepository(connectionMultiplexer));
        }
    }
}