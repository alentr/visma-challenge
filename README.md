# visma-challenge

To resolve the problem given in task 5, I decided to use the [token bucket algorithm](https://en.wikipedia.org/wiki/Token_bucket), using the library [Bucket4j](https://bucket4j.com/).
The implementation uses an in-memory bucket, that uses a new request param (customerId), with this param, I'm creating a bucket for each customer.

Taking into consideration that in a real scenario,
I would not use a header from a public API, since the client could change this value between requests.

The param to use as a key to create the bucket should be taken from a JWT for example,
or in cases when the API is protected by an API-GW, that can inject some customer information on the request.
