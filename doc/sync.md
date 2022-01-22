# Support for synchronous apis 
## The problem :
Beckn Apis are designed for scale and are primarily async in nature. 
Often times a Sync api is better suited for better usability. This paper is an exploration into the possibility of building a syncronous bridge.. 


## Registry
For every subscription endpoint, after successful resolution of challenge, the subscriber would return meta information about apis supported  at the end  point as part of the on_subscribe.


	{ "meta":{ 
	      "apis" : [
	          { "name" : "string name of the api callable  at the endpoint serving this meta",
	            "synchronous" : "boolean default false for bpp and true for bap on_search is sync and search is async" }
	      ]
	  } 
	}

### BAP 
A Bap endpoint would return meta information about apis that can be called on their endpoint e.g on_search, on_select ... 

* If an api is missing in the list of apis supported, it would mean that the bap is expecting a synchronous reponse for the corresponding request call made on a bpp/bg  (e.g /search /select...) 
* The value of the attribute "sync" is always true for a BAP. Since on_[api] is a call back api to begin with and ACK response is the only response possible from that api. 


### BPP 
A BPP endpoint would return meta information about apis that can be called on their endpoint e.g search, select, init ... 

* If an api ,is missing, it means it is not implemented by the bpp. For e.g a bpp may not implement /track api. Then track api name will be missing in the meta apis list.

* If an api is specified as sync : "true" , it would mean that the BPP can respond with a json that a bap may expect as part of on_[api] callback. 

* If an api is specified  as "sync" : "false" , That would be the default behaviour of the api, it would return an ACK and respond with a call back to on_[api] of the BAP in the context. 




