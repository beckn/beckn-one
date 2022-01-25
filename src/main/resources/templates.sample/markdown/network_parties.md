# Network parties.
In a Beckn Network, these are some of the typical parties who may collaborate together to complete a commercial/non-commercial transaction using the *beckn protocol*. 
1. Beckn Application Platform. (BAP)
1. Beckn Provider Platform (BPP)
1. Beckn Gateway (BG)
1. Beckn Registry. (REG)


This are the typical list. there may be more parties in a network who provide horizontal services such as payment reconcilation between parties. analytics of buyer preferences, logistics optimization, etc.. 


While there may be many parties in a network, the top 4 parties are critical for completion of any transaction in the network. 

## BAP 
A BAP is a platform that primarily focuses on providing value to a Buyer. It is a buyer facing application. It is a application through which Buyer's find the Sellers selling the product/service they require. 

## BPP 
A BPP is a platform that primariy focuses  on the needs of Sellers. It is a seller facing application. It is an application that sellers would use to be notified and fulfill orders placed on them by buyers on the network.

## BG 
A BG is like an asyncronous queue to which a BAP queues as search request on behalf of a buyer on their platform. The BG would broadcast the request to multiple relevant BPPs. 

## REG
A REG is a key component of any beckn network. 

BPP, BAP and BG register with the registrar of a network, their end points, public_keys etc that are needed for successful completion of a beckn [transaction](transactions.md).


<a href="#"  class="btn btn-secondary" onclick="history.back();return false">back</a>






