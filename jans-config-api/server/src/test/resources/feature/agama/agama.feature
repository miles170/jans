
Feature: Agama flow

Background:
* def mainUrl = agama_url

Scenario: Fetch all agama without bearer token 
	Given url mainUrl 
	When method GET 
	Then status 401 


Scenario: Fetch all agama flows 
	Given url mainUrl 
	And print 'accessToken = '+accessToken
	And print 'issuer = '+issuer
	And header Authorization = 'Bearer ' + accessToken
	#And header issuer = issuer  
	When method GET 
	Then status 200 
	And print response
	And assert response.length != null 
	And assert response.length >= 10 

@CreateDelete 
Scenario: Create new agama flow 
	Given url mainUrl
	And header Authorization = 'Bearer ' + accessToken 
	And request read('agama.json') 
	When method POST 
	Then status 201 
	Then def result = response 
	Then def flowName = result.qname 
	Given url mainUrl + '/' +flowName
	And header Authorization = 'Bearer ' + accessToken 
	When method DELETE 
	Then status 204 
