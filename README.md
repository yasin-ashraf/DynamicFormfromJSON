# Assignment for Hubbler
Assignment done by me as part of recruitment process at hubbler.mobi

Recruitment process managed and overseen by Renjith Chaz, CTO Hubbler

# Problem

There will be two screens in this assignment. 

The 1st screen will look like the following:





Total reports will be zero initially as you haven't added any reports.


When you click on the Add button, the second screen is shown. 

The second screen is completely dynamic. You have to keep a JSON Array to create this screen. 


For example consider the following JSON:

[

{'field-name':'name', 'type':'text', 'required':true},

{'field-name':'age', 'type':'number', 'min':18, 'max':65},

{'field-name':'address', 'type':'multiline'}

]


Screen should look like the following:





If input JSON is as below:

[

{'field-name':'name', 'type':'text'},

{'field-name':'age', 'type':'number'},

{'field-name':'gender', 'type':dropdown', 'options':['male', 'female', 'other']},

{'field-name':'address', 'type':'multiline'}

]


The screen should look like:





Note: The drop-down field can be displayed as a spinner.


Suppose for the above screen, you entered the name as 'Hari', age: 29, gender: 'male' and Address: 'Oxford Engg College, Bangalore, Karnataka'

After entering these data, if you hit 'Done' button, you should prepare a JSON in the following format:


{'Name':'Hari', 'Age':29, 'Gender':'male', 'Address':'Oxford Engg College, Bangalore, Karnataka' }


You may print above data as a log statement


Once you fill in data and click the Done button, control goes back to the 1st page. and it will look like shown below: 





The listing page should display 1st two fields data. If you have only 1 field, display just that. 


About Validations:

required, min, max etc. work as expected; that is if a field has 'required': true, and if you leave it unfilled,  upon tapping the done button, an error msg/toast shall be displayed. For min, max the value entered should be within the range. Else as before an error toast/msg will be displayed upon done button tap.


Notes:


1. Once completed, send APK as well as codebase.

2. The app UI should look neat & professional

3. If I change the input JSON structure, the 2nd screen should change  & behave accordingly.

5. Don't create orm classes as we don't have to save reports to db considering the dynamic nature of the input JSON.  

6. Coding & UI Design both are important factors of the code review.


# Solution

To be updated soon.
