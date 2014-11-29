using System;
using System.Collections.Generic;
using System.Linq;
using MicroServicesHackathon.Facts;

namespace MicroServicesHackathon.Rest
{
    public interface IRestClient
    {
        string Subscribe(string topic);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="topic"></param>
        /// <param name="subscription"></param>
        /// <param name="waitSeconds">How long to wait for a next fact. Must be > 1</param>
        /// <returns></returns>
        TFact NextFact<TFact>(string topic, string subscription, int waitSeconds = 1) where TFact : Fact, new();

        /// <summary>
        /// Post a fact to the rest api
        /// </summary>
        /// <param name="topic"></param>
        /// <param name="fact"></param>
        /// <returns>False if request failed and true if it worked</returns>
        bool PostFact(string topic, Fact fact);
    }
}
