using System;
using System.Collections.Generic;
using System.Linq;
using MicroServicesHackathon.Facts;
using RestSharp;
using System.Net;

namespace MicroServicesHackathon.Rest
{
    public class RestClient : IRestClient
    {
        private readonly RestSharp.RestClient _client;

        public RestClient()
        {
            _client = new RestSharp.RestClient("http://combo-squirrel.herokuapp.com");
        }

        public string Subscribe(string topic)
        {
            RestRequest request = new RestRequest("/topics/{topic_name}/subscriptions", Method.POST);
            request.AddUrlSegment("topic_name", topic);

            IRestResponse<Subscription> response = _client.Post<Subscription>(request);
            if (IsPostSuccessful(response))
                return response.Data.subscription_id;
            else
                return null;
        }

        public TFact NextFact<TFact>(string topic, string subscription, int waitSeconds = 1) where TFact : Fact, new()
        {
            if (waitSeconds <= 0)
                throw new ArgumentOutOfRangeException("waitSeconds", "waitSeconds has to > 0");

            RestRequest request = new RestRequest("/topics/{topic_name}/subscriptions/{subscription_id}/next", Method.GET);
            request.AddUrlSegment("topic_name", topic);
            request.AddUrlSegment("subscription_id", subscription);
            request.AddHeader("Patience", waitSeconds.ToString());

            IRestResponse<TFact> response = _client.Get<TFact>(request);
            if (response.ResponseStatus == ResponseStatus.Completed)
                return (TFact) response.Data;
            else
                return null;
        }


        public bool PostFact(string topic, Fact fact)
        {
            RestRequest request = new RestRequest("/topics/{topic_name}/facts", Method.POST);
            request.AddUrlSegment("topic_name", topic);

            request.AddJsonBody(fact);

            var response = _client.Post(request);

            return IsPostSuccessful(response);
        }

        #region Utils

        private static bool IsPostSuccessful(IRestResponse response)
        {
            return response.ResponseStatus == ResponseStatus.Completed && 
                (response.StatusCode == HttpStatusCode.Accepted || response.StatusCode == HttpStatusCode.OK);
        }

        #endregion
    }
}
