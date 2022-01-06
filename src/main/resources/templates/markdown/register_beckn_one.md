# Making use of the beckn one network
Beckn One is a reference network that consists of a registry, gateway, a mock bap and bpp for various domains(retail,logistics,healthcare...) . Using these tools, beckn applications can be tested. In order to make use of the beckn one network, you need to : 

1. Register yourself as a beckn application developer with your <a href="/oid/login?SELECTED_OPEN_ID=GOOGLE" target="_blank" >google id</a>.
1. Then, register 
    * Your company's fully qualified domain name (fqdn) as a Network Participant by clicking the <a class="fas fa-plus"></a> button
        <a href="/network_participants" target="_blank">here.</a>
    * Generate a Ed25518  key pair for signing and X25518 key pair for encryption purposes. 
        Keep the private keys with you safely and register the public keys on **Participant Key** tab against your company's entry 
        as a **Network Participant**. If you want to simply play around, you can generate the key pairs on beckn one by clicking on the 
        <a class="fa fa-key" href="#" ></a> icon  on the **Network Participant Information** tab. To know  you private keys you can see <a href="/crypto_keys" target="_blank" >here</a>
    * Register the domain and roles you wish to participate on the network by clicking the <a class="fas fa-plus"></a> button on the **Network Role Tab**. 
        *Each Role you wish to play and the domain you wish to participate in need to have unique url end points.*  
    * If you wish to test your /subscribe api , keep the status as initiated or else you can mark yourself in "SUBSCRIBED"  status.

# Testing your implementation on beckn one. 
1. Beckn Volunteers have created a list of apis that are to be implemented by various participants under Menu ( Beckn -> Beckn Api )
1. Sample named usecases for each  of these apis have been put up for your reference. You can see these usecases on clicking 
    <a class="fas fa-eye"></a> icon against any of the apis displayed under **Beckn Api** list
1. When you view <a class="fas fa-eye"></a> details of a usecase, you will see a sample json with variables like "${name}" etc. 
    * This is the structure of the beckn json api's "message" attribute . While testing you will be able to pass values for these variables. "context" attribute in the beckn api payload is created by beckn one testing tool.. 
1. In order to test a usecase against your application platform, you need to create an test (**ApiTest**) against the usecase 
    * If you want to call an api on your endpoint, Specify one of your role as the subscriber on whom the api must be called. 
    * If you want to simulate how you should be calling on another subscriber, select one of your role as the subscriber you want beckn one to proxy for. (*Note For this you need to have your private keys on beck-one under <a href="/crypto_keys">Crypto Keys</a>* )
1. Once you create Test for your use case, you can click on the <a class="fas fa-bolt"></a> icon on the ApiTest record and wait for your responses. If all goes well , you would see the payload and headers that the api was fired with, the ACK response from the endpoint and the Callback response and the corresponding headers (*possibly after a short while since beckn apis are async in nature.*).  

[Prev - Getting Started](getting_started.md)

[Home](/)

