# Beckn One (Api Playground/ Sandbox)
As part of beckn one v0, we created an api playground for developers to build/test their beckn api implementations. 

Currently hosted <a href="https://beckn-one.succinct.in">Here (https://beckn-one.succinct.in</a>, beckn-one comes with its one internal registry. Subscribers can either use the /subscribe api or use the beckn-one UI to manually add their subscription to the network. Once subscribe one can browse available beckn apis and execute them on your specific subscriber. We will explore how to do this in this document.

# 1. Login
Login to beckn-one with your gmail id.. 

# 2 Creating subscriptions
## 2.1 Manually
Once you have logged in you can go to the menu Admin->Subscriber

* You will see all subscribers registed on the network. 
* You can add new record for your platform giving your own.
    1. Subscriber Id, 
    2. Base_URL relative to which beckn apis are fired, 
    3. Domain, 
    4. Public Keys for signing (Ed25519) and encryption RSA-2048) in the pem format,  Don't worry if you dont know to generate, Beckn-one can generate for you. 
    5. And type (bap/bpp).
    6. City and  country can be left blank if the bpp provides service for the  entire country in the  registry it belongs.


## 2.2 Via Registry Api 

## /subscribe##
Sample:

curl  -H 'Content-Type: application/json' 'https://beckn-registry.succinct.in/subscribers/subscribe' -d '{"country":"IN","signing_public_key":"MCowBQYDK2VwAyEAIOCUiUuLOiUR6EOBgJcjL1zgF5XnKArJLfWKncSKcXY=","subscriber_id":"mandi.succinct.in","valid_until":"2031-04-16T11:33:23.000+05:30","subscriber_url":"https:\/\/mandi.succinct.in\/bpp","city":"080","domain":"local-retail","valid_from":"2021-04-15T23:33:23.000+05:30","type":"bpp","encr_public_key":"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmTuLyNf1odybZJBHVdShs7JZ3Wy8JgO\/Wz\/5cKK647wl+ljR5\/6qJGHk0cvl9n9hvqRqsu6G\/ZWQossHRn2TWcXkKybK+lCujX5wnokpcHLBan5S0SKXfjFi7rSirMqUuLEf866106OMfpO\/9es8DQ9Oa9+jI2pqL1nkGF85BFMO8Ep4obhz9i6sRcLl9NBS5xBvTyhwp2weugkqpS\/BqvemePpRl41tonHFQX6iFmNOxVE8jsbdcr7Oe1Wssd1zrjPITI4jk513IZlwtu5SRyzqAzPoJ3szx\/x4\/T0\/75uWPDXVdVeFgNQlmQKF2gMdyiQbISMgWcQzBa6DJxLnwQIDAQAB"}'



## /on_subscribe##
### Request###
Sample:

curl  -H 'Content-type: application/json'  -H 'Signature: Ml7UwArf/ACLeVpxn51n9FPqMdB4OmnzViZlq4fYWDcT3nH0z64h10oAMGlkVjkWexzJBtfWrVpyHNEcQ6k3Dg==' 'https://mandi.succinct.in/bpp/on_subscribe' -d '{"subscriber_id":"mandi.succinct.in","challenge":"Cd45F5vqaHqUS4VUkWKkTPVwiwi5SfivGKiyWBICzrs0hdo9mJduoNPQQX3THbyhvxKMGT78b9pzzEp+JuZmfqP2\/3scn3l\/8Gy0GjnCX9u923EmO3K91QYkVAq1z\/A1RwF1jw2xGPPk4qbKnnkBl7y4ZsjS42mTJfbp5y2oONZBl3+yaakDMl5FEwqDLT0SirVaPz0S5W5HjTgdCNzJ34L4kTK8TdGjmcjJ6oEbamOTbLQUaQp+Y9Rf+++Uq7OTgUwCCHtcRJdStyhJP4BoTLwbfKIAc40V4mx1qvc4r2RWq+rs2ltce5a8SyHqKxk8gTtRr6nRdr53TLvjThj8dw=="}'

###Response###
Sample

{"answer":"285160"}

# 3. Testing Beckn Apis.

## 3.1 Beckn Api
Under Menu Beckn->Beckn Api, you would see a list of the published beckn apis for each platform-type(bap/bpp) 
for. e.g 

1. bpp, search
2. bpp, on_search 

## 3.2 Use Cases
Each api in beckn can be called on a platform in many possible ways by altering the what subset of parameters are passed, what values are passed for those parameters etc. 

Each callable input pattern is called a **usecase**. Some of these usecases are more common and have meaning in every day usage. These meaningful usecases are cataloged under each beckn api with a template input json having placeholder variables. Thse variables may be supplied while testing the usecase against your platform,  


## 3.3. Api Test. 
When you want to test a usecase against your platform, 

1. create an api test record under the usecase by specifying a test name, your platform id  and template variable values you want to use for the test. 
2. Then click the execute button to execute the api-usecase against your platform. 
3. Beckn-One will act as the participant firing the request on your platform and sign the requests with its public key. Depending on your platform role (bap/bpp), beckn-one will automatically take on the other role. 
4. All Request/Response and call back Payloads are logged against the same message_id that gets generated for each test. This information is persisted as api_calls under api_tests.
5. If your call fails and you want to rerun the same test, you can click on excecute against the persisted api_call information also. 


# 4. Knowing public keys of participants.
## 4.2 Via registry apis.

## /lookup##
Sample.

curl  -H 'Content-Type: application/json' 'https://beckn-registry.succinct.in/subscribers/lookup' -d '{"subscriber_id":"mandi.succinct.in", "type":"bpp" , "domain":"local-retail" , "country":"IN" ,"city":"080"}'

### Response ###
Sample

[{"country":{"name":"India","id":"12","iso_code":"IN"},"city":{"code":"080","name":"Bangalore","id":"19900","state":{"country":{"name":"India","id":"12","iso_code":"IN"},"code":"KA","name":"Karnataka","id":"397"}},"created":"2021-06-20 23:17:14","valid_from":"2021-04-15 23:33:23","type":"bpp","signing_public_key":"MCowBQYDK2VwAyEAIOCUiUuLOiUR6EOBgJcjL1zgF5XnKArJLfWKncSKcXY=","subscriber_id":"mandi.succinct.in","valid_until":"2031-04-16 11:33:23","subscriber_url":"https:\/\/mandi.succinct.in\/bpp","domain":"local-retail","encr_public_key":"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmTuLyNf1odybZJBHVdShs7JZ3Wy8JgO\/Wz\/5cKK647wl+ljR5\/6qJGHk0cvl9n9hvqRqsu6G\/ZWQossHRn2TWcXkKybK+lCujX5wnokpcHLBan5S0SKXfjFi7rSirMqUuLEf866106OMfpO\/9es8DQ9Oa9+jI2pqL1nkGF85BFMO8Ep4obhz9i6sRcLl9NBS5xBvTyhwp2weugkqpS\/BqvemePpRl41tonHFQX6iFmNOxVE8jsbdcr7Oe1Wssd1zrjPITI4jk513IZlwtu5SRyzqAzPoJ3szx\/x4\/T0\/75uWPDXVdVeFgNQlmQKF2gMdyiQbISMgWcQzBa6DJxLnwQIDAQAB","updated":"2021-06-21 00:00:49","status":"SUBSCRIBED"}] 

# 5.Feedback 
Please feel free to give feedback to 
<a href="mailto:ravi@beckn.org">Ravi</a> or <a href="mailto:venky@humbhionline.in">Venky</a>
