# Getting Started

### Understanding the Beckn Network
A Beckn network is a collaboration framework that connects various [parties](network_parties.md) intending to conduct business using a common language, (the beckn protocol).

### The protocol 
The _Protocol( or beckn protocol)_ is a collection of published standardized apis by various domains. These apis enable participants to speak a common language to conduct business [transactions](transactions) electronically. 

Architecturally, the beckn protocol has be split into multiple layers. (for e.g) 
1. A core api specification. 
	1. This is a generic specification for apis. It is domain and network agnostic, meaning, is same for all domains and all networks.
2. A Domain extension of the core api.
	1. By each domain, there could be domain specific extensions that would be implemented on top of the core api specification. An example would be order cancellation reasons could be different by domain. 
2. Network extension of the core api. 
	1. In each network, participants may agree to use specific  enum constants for cancellation  reasons or payment terms. 
	2. Can choose to use their own KYB (Know your business) processes that is meaningful that that domain and network 

When a beckn network goes live for a domain, a network would decide the tuple (core-version,domain-extension-version, network-policy-version) together on which the network participants would agreed upon. 

[Next - Beckn one network](register_beckn_one.md)

[Home](/)

