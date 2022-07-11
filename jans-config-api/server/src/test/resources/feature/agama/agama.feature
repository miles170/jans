
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

