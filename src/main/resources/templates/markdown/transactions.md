# Beckn Transactions
A Transaction on beckn can be summarized as fullfillment of a request.

## Roughly the following steps are involved in fulfilling any request in the beckn way!
1. A person has a problem to be solved. 
1. Searches for solutions. (/search api)
1. Find multiple Solution providers. (/on_search callback api)
1. Narrows on the provider and solutions and selects a provider's solution and communicates the intention to the provider. (/select api)
1. Provider submits a quote for the solution (/on_select api)
1. the person accepts the quote and asks for payment terms ( /init api ) 
1. Provider submits the payment terms the provider is ok with ( before confirming/before fulfillment /after fulfillment/ cod etc.. ) (/on_init api)
1. the person selects one of the allowed payment terms and either pays ( or promises to pay ) later. ( /confirm ) 
1. Provider accepts the payment ( or promise to may payment ) and works  on fulfilling the request. (/on_confirm)

## Domain specfic interpretation 
In every domain, the definition of the problem changes and the fulfullment looks different. for e.g. 

1. In Local Retail, the problem is, I need a product, which a seller has and would sell to me for a price. 
1. In a delivery network, the problem is, I need some thing to delivered, which a courier picks it up and delivers as needed. 
1. In a mobility network, the problem is, I need a cab to get somewhere from here, and a cab driver would pickup and drop me as needed.
1. In a health care network, the problem could be , I need a test to be done, which a lab agrees to do it for a price.


*The possibilities as you can see is enormous as many problems can be abstracted and solved in the same mannner. For getting insight on the philosophy of beckn, read [the beckn white paper](https://becknprotocol.io/imagining-with-beckn/)*

<a href="#"  class="btn btn-secondary" onclick="history.back();return false">back</a>

